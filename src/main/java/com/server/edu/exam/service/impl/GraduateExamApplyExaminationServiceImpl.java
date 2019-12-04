package com.server.edu.exam.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.server.edu.common.PageCondition;
import com.server.edu.common.entity.ExamMakeUp;
import com.server.edu.common.enums.GroupDataEnum;
import com.server.edu.common.locale.I18nUtil;
import com.server.edu.common.rest.PageResult;
import com.server.edu.common.vo.SchoolCalendarVo;
import com.server.edu.dictionary.DictTypeEnum;
import com.server.edu.election.dao.StudentDao;
import com.server.edu.election.entity.Student;
import com.server.edu.election.rpc.BaseresServiceInvoker;
import com.server.edu.exam.constants.ApplyStatus;
import com.server.edu.exam.dao.*;
import com.server.edu.exam.dto.ExamInfoRoomDto;
import com.server.edu.exam.dto.GraduateExamScore;
import com.server.edu.exam.dto.SelectDto;
import com.server.edu.exam.entity.GraduateExamApplyExamination;
import com.server.edu.exam.entity.GraduateExamLog;
import com.server.edu.exam.entity.GraduateExamMakeUpAuth;
import com.server.edu.exam.entity.GraduateExamStudent;
import com.server.edu.exam.rpc.BaseresServiceExamInvoker;
import com.server.edu.exam.service.GraduateExamApplyExaminationService;
import com.server.edu.exam.service.GraduateExamInfoService;
import com.server.edu.exam.vo.GraduateExamApplyExaminationVo;
import com.server.edu.exam.vo.MyGraduateExam;
import com.server.edu.exception.ParameterValidateException;
import com.server.edu.session.util.SessionUtils;
import com.server.edu.session.util.entity.Session;
import com.server.edu.util.CollectionUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @description:
 * @author: bear
 * @create: 2019-08-29 09:27
 */

@Service
@Primary
public class GraduateExamApplyExaminationServiceImpl implements GraduateExamApplyExaminationService {
    
    @Autowired
    private GraduateExamApplyExaminationDao applyExaminationDao;

    @Autowired
    private GraduateExamMakeUpAuthDao makeUpAuthDao;

    @Autowired
    private GraduateExamInfoDao examInfoDao;

    @Autowired
    private GraduateExamStudentDao examStudentDao;

    @Autowired
    private GraduateExamRoomDao roomDao;

    @Autowired
    private GraduateExamInfoService infoService;

    @Autowired
    private GraduateExamLogDao logDao;

    @Autowired
    private StudentDao studentDao;

    /**
    *@Description: 新增补缓考申请
    *@Param:
    *@return: 
    *@Author: bear
    *@date: 2019/8/29 9:49
    */
    @Override
    @Transactional
    public void addGraduateApply(GraduateExamApplyExamination applyExamination) {
        //参数校验
        if(applyExamination.getApplyType() == null || applyExamination.getCalendarId() == null ||
                StringUtils.isBlank(applyExamination.getCourseCode()) || StringUtils.isBlank(applyExamination.getStudentCode()) ){
            throw new ParameterValidateException(I18nUtil.getMsg("baseresservice.parameterError"));
        }
        Session currentSession = SessionUtils.getCurrentSession();
        if(currentSession != null){
            String dptId = currentSession.getCurrentManageDptId();
            applyExamination.setProjId(dptId);
            //检验是否有权限申请
            this.checkTime(applyExamination);
            applyExamination.setApplySource(ApplyStatus.APPLY_SOURCE_OTHER);
            if(ApplyStatus.CONSTANTS_STRING.equals(currentSession.getCurrentRole()) && !currentSession.isAdmin() && currentSession.isAcdemicDean()){
                applyExamination.setApplyStatus(ApplyStatus.COLLEGE_EXAMINE_PASS);
            }else{
                applyExamination.setApplyStatus(ApplyStatus.SCHOOL_EXAMINE_PASS);
            }
            //代申请检验是否可以申请该类型()
            this.checkApplyType(applyExamination,currentSession);
            this.checkApplyEffective(applyExamination,currentSession);
        }

    }


    private void checkApplyType(GraduateExamApplyExamination applyExamination,Session currentSession) {
        Student student = studentDao.findStudentByCode(applyExamination.getStudentCode());
        if(student == null){
            throw new ParameterValidateException("该学生不存在学籍中");
        }
        if(applyExamination.getApplyStatus().equals(ApplyStatus.COLLEGE_EXAMINE_PASS)){
            List<String> list = currentSession.getGroupData().get(GroupDataEnum.department.getValue());
            String faculty = student.getFaculty();
            if(CollectionUtil.isNotEmpty(list)){
                if(!list.contains(faculty)){
                    throw new ParameterValidateException("当前学院教务员不能替其他学院学生进行代理申请");
                }
            }
        }
        Integer applyType = applyExamination.getApplyType();
        //申请类型3 是补考,进行不及格成绩校验,否则进行期末考试的校验,是否可以申请这个类型的考试
        if(applyType.equals(ApplyStatus.EXAM_SITUATION_MAKE_UP)){
            //补考进行不及格成绩校验
            List<GraduateExamScore> examScore = examStudentDao.findStudentScore(applyExamination);
            if(CollectionUtil.isEmpty(examScore) || examScore.size() > 1){
                throw new ParameterValidateException("该生补考申请不满足条件");
            }else{
                int isPass = examScore.get(0).getIsPass();
                if(isPass != 0){
                    throw new ParameterValidateException("该生补考申请不满足条件");
                }
                Long teachingClassId = examScore.get(0).getTeachingClassId();
                applyExamination.setTeachingClassId(teachingClassId);
                applyExamination.setExamCalendarId(applyExamination.getCalendarId());
            }
        }else{
            MyGraduateExam myGraduateExam = new MyGraduateExam();
            myGraduateExam.setCalendarId(applyExamination.getCalendarId());
            myGraduateExam.setStudentCode(applyExamination.getStudentCode());
            myGraduateExam.setExamType(ApplyStatus.FINAL_EXAM);
            myGraduateExam.setCourseCode(applyExamination.getCourseCode());
            myGraduateExam.setExamSituation(ApplyStatus.EXAM_SITUATION_NORMAL);
            Page<MyGraduateExam> page = examInfoDao.listMyExam(myGraduateExam);
            if(CollectionUtil.isEmpty(page)){
                throw new ParameterValidateException("该生缓考申请不满足条件");
            }else{
                List<MyGraduateExam> result = page.getResult();
                MyGraduateExam graduateExam = result.get(0);
                applyExamination.setTeachingClassId(graduateExam.getTeachingClassId());
                SchoolCalendarVo preOrNextTerm = BaseresServiceExamInvoker.getPreOrNextTerm(applyExamination.getCalendarId(), true);
                applyExamination.setExamCalendarId(preOrNextTerm.getId());
            }
        }

    }

    /**
    *@Description: 查询研究生补缓考申请
    *@Param:
    *@return: 
    *@Author: bear
    *@date: 2019/8/29 14:38
    */
    @Override
    public PageResult<GraduateExamApplyExaminationVo> listGraduateApply(PageCondition<GraduateExamApplyExaminationVo> applyExamination) {
        GraduateExamApplyExaminationVo condition = applyExamination.getCondition();
        if(condition.getCalendarId() == null){
            throw new ParameterValidateException(I18nUtil.getMsg("baseresservice.parameterError"));
        }
        Session session = SessionUtils.getCurrentSession();
        String dptId = session.getCurrentManageDptId();
        List<String> facultys = session.getGroupData().get(GroupDataEnum.department.getValue());
        condition.setProjId(dptId);
        condition.setFacultys(facultys);
        //校管理员只展示学院审核通过数据
        if(ApplyStatus.CONSTANTS_STRING.equals(session.getCurrentRole()) && session.isAdmin()){
            condition.setIsAdmin("admin");
        }
        PageHelper.startPage(applyExamination.getPageNum_(),applyExamination.getPageSize_());
        Page<GraduateExamApplyExaminationVo> page = applyExaminationDao.listGraduateApply(condition);
        return new PageResult<>(page);
    }

    /**
    *@Description: 审核通过或驳回
    *@Param:
    *@return: //审核状态 1 通过 0 未通过
    *@Author: bear
    *@date: 2019/8/29 15:54
    */
    @Override
    @Transactional
    public void examineGraduateApply(List<Long> ids,Integer status,String aduitOpinions) {
        if(CollectionUtil.isEmpty(ids) || status == null){
            throw new ParameterValidateException(I18nUtil.getMsg("baseresservice.parameterError"));
        }
        int applyStatus = 0;
        Session session = SessionUtils.getCurrentSession();
        if(session != null){
            //教务员
            if(ApplyStatus.CONSTANTS_STRING.equals(session.getCurrentRole()) && !session.isAdmin() && session.isAcdemicDean()){
                if(status == ApplyStatus.PASS_INT){
                    applyStatus = ApplyStatus.COLLEGE_EXAMINE_PASS;
                }else {
                    applyStatus = ApplyStatus.COLLEGE_EXAMINE_NOT_PASS;
                }
            }else{//校管理员
                if(status == ApplyStatus.PASS_INT){
                    applyStatus = ApplyStatus.SCHOOL_EXAMINE_PASS;
                }else {
                    applyStatus = ApplyStatus.SCHOOL_EXAMINE_NOT_PASS;
                }
            }
        }
        applyExaminationDao.updateByList(ids,applyStatus,aduitOpinions);

        Example example = new Example(GraduateExamApplyExamination.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("id",ids);
        List<GraduateExamApplyExamination> applyExamination = applyExaminationDao.selectByExample(example);
        //根据状态不同，回写相应状态，审核的是缓考,回写到期末考试，否则回写到成绩
        this.changeStatusToInfo(applyExamination,applyStatus,aduitOpinions,session);

    }

    @Override
    @Transactional
    public void checkRepeat(GraduateExamApplyExamination applyExamination) {
        Example example = new Example(GraduateExamApplyExamination.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("calendarId",applyExamination.getCalendarId());
        criteria.andEqualTo("studentCode",applyExamination.getStudentCode());
        criteria.andEqualTo("courseCode",applyExamination.getCourseCode());
        criteria.andEqualTo("projId",applyExamination.getProjId());
        criteria.andEqualTo("applyType",applyExamination.getApplyType());
        GraduateExamApplyExamination examination = applyExaminationDao.selectOneByExample(example);
        if(examination == null){
            applyExaminationDao.insertSelective(applyExamination);

        }else{
            //审核不通过，可以再次申请
            if(examination.getApplyStatus() == ApplyStatus.COLLEGE_EXAMINE_NOT_PASS || examination.getApplyStatus() == ApplyStatus.SCHOOL_EXAMINE_NOT_PASS){
                applyExamination.setId(examination.getId());
                applyExamination.setAduitOpinions("");
                applyExamination.setUpdateAt(new Date());
                applyExaminationDao.updateByPrimaryKeySelective(applyExamination);
            }else{
                throw new ParameterValidateException("该课程已经提交申请，请勿反复提交");
            }

        }
    }

    @Transactional
    @Override
    public void changeStatusToInfo(List<GraduateExamApplyExamination> applyExamination,Integer applyStatus,String aduitOpinions,Session session){
        //拆分申请状态，变更到相应的表
        Map<Integer, List<GraduateExamApplyExamination>> collect = applyExamination.stream().collect(Collectors.groupingBy(GraduateExamApplyExamination::getApplyType));
        //缓考
        List<GraduateExamApplyExamination> examInfos = collect.get(ApplyStatus.EXAM_SITUATION_SLOW);
        //补考
        List<GraduateExamApplyExamination> scores = collect.get(ApplyStatus.EXAM_SITUATION_MAKE_UP);

        if(CollectionUtil.isNotEmpty(examInfos)){

            //学校审核通过，缓考变更期末考试状态,学校审核不通过或学院审核不通过只变更remark信息
            if(applyStatus.equals(ApplyStatus.SCHOOL_EXAMINE_PASS)){
                String str = "缓考申请审核通过";
                if(StringUtils.isBlank(aduitOpinions)){
                    aduitOpinions = str;
                }else{
                    aduitOpinions = str + "(" + aduitOpinions + ")";
                }
                //变更考试状态,删减考场人数
                GraduateExamApplyExamination examinationVo = examInfos.get(0);
                Long calendarId = examinationVo.getCalendarId();
                Integer applyType = examinationVo.getApplyType();
                List<GraduateExamStudent> list =  examStudentDao.findGraduateStudent(examInfos,calendarId);
                if(CollectionUtil.isNotEmpty(list)){
                    examStudentDao.updateSituationByIds(list,aduitOpinions,applyType);
                    Map<Long, List<GraduateExamStudent>> roomIds = list.stream().collect(Collectors.groupingBy(GraduateExamStudent::getExamRoomId));
                    List<ExamInfoRoomDto> dtoList = new ArrayList<>();
                    for (Long examRoomId : roomIds.keySet()) {
                        ExamInfoRoomDto dto = new ExamInfoRoomDto();
                        dto.setExamInfoId(examRoomId);
                        dto.setExamRooms(roomIds.get(examRoomId).size());
                        dtoList.add(dto);
                    }

                    //批量更新对应考生考场人数
                    roomDao.updateRoomNumberByList(dtoList);
                    infoService.updateActualNumber(list,ApplyStatus.EXAM_REDUCE);
                    //退考
                    List<Long> studentIds = list.stream().map(GraduateExamStudent::getId).collect(Collectors.toList());
                    List<GraduateExamLog> logList = examStudentDao.findRoomIdByExamStudentId(studentIds);
                    for (GraduateExamLog examLog : logList) {
                        examLog.setCreateAt(new Date());
                        examLog.setExamType(ApplyStatus.EXAM_LOG_NO);
                        examLog.setOperatorCode(session.realUid());
                        examLog.setOperatorName(session.realName());
                        examLog.setIp(session.getIp());
                    }
                    logDao.insertList(logList);
                    //infoService.insertExamLog(list,ApplyStatus.EXAM_LOG_NO);
                }

            }else if(applyStatus.equals(ApplyStatus.SCHOOL_EXAMINE_NOT_PASS) || applyStatus.equals(ApplyStatus.COLLEGE_EXAMINE_NOT_PASS)){
                String str = "缓考申请审核不通过";
                if(StringUtils.isNotBlank(aduitOpinions)){
                    aduitOpinions = str + "(" + aduitOpinions + ")";
                    examStudentDao.updateExamStudentRemark(examInfos,aduitOpinions);
                }

            }
        }

        if(CollectionUtil.isNotEmpty(scores)){
            //回写补考信息
            String str = "";
            if(applyStatus.equals(ApplyStatus.SCHOOL_EXAMINE_PASS)){
                str = "补考申请审核通过";
            }else if(applyStatus.equals(ApplyStatus.SCHOOL_EXAMINE_NOT_PASS) || applyStatus.equals(ApplyStatus.COLLEGE_EXAMINE_NOT_PASS)){
                 str = "补考申请审核不通过";
            }
            if(StringUtils.isBlank(aduitOpinions)){
                aduitOpinions = str;
            }else{
                aduitOpinions = str + "(" + aduitOpinions + ")";
            }
            if(StringUtils.isNotBlank(aduitOpinions)){
                examStudentDao.updateStudentScoreMessage(scores,aduitOpinions);
            }
        }

    }

    @Override
    public PageResult<SelectDto> applyStudentList(PageCondition<SelectDto> condition) {
        SelectDto selectDto = condition.getCondition();
        Integer examType = selectDto.getExamType();
        Long calendarId = selectDto.getCalendarId();
        if(examType == null || calendarId == null){
            throw new ParameterValidateException(I18nUtil.getMsg("baseresservice.parameterError"));
        }
        Session currentSession = SessionUtils.getCurrentSession();
        String dptId = currentSession.getCurrentManageDptId();
        List<String> facultys = currentSession.getGroupData().get(GroupDataEnum.department.getValue());
        //缓考
        selectDto.setProjId(dptId);
        selectDto.setFacultys(facultys);
        Page<SelectDto> page = new Page<>();
        PageHelper.startPage(condition.getPageNum_(),condition.getPageSize_());
        if(examType.equals(ApplyStatus.EXAM_SITUATION_SLOW)){
           //page =  examStudentDao.listApplyListSlow(selectDto);
           page =  examStudentDao.listApplyListSlowAndCourseTake(selectDto);
        }else{
            page =  examStudentDao.listApplyListMakeUp(selectDto);
        }
        return new PageResult<>(page);
    }

    @Override
    public PageResult<SelectDto> applyCourseList(PageCondition<SelectDto> condition) {
        SelectDto selectDto = condition.getCondition();
        Integer examType = selectDto.getExamType();
        Long calendarId = selectDto.getCalendarId();
        if(examType == null || calendarId == null){
            throw new ParameterValidateException(I18nUtil.getMsg("baseresservice.parameterError"));
        }
        Session currentSession = SessionUtils.getCurrentSession();
        String dptId = currentSession.getCurrentManageDptId();
        selectDto.setProjId(dptId);
        Page<SelectDto> page = new Page<>();
        PageHelper.startPage(condition.getPageNum_(),condition.getPageSize_());
        if(examType.equals(ApplyStatus.EXAM_SITUATION_SLOW)){
            //page =  examStudentDao.listApplyCourseListSlow(selectDto);
            page =  examStudentDao.listApplyCourseListSlowAndCourseTake(selectDto);
        }else{
            page =  examStudentDao.listApplyCourseListMakeUp(selectDto);
        }
        return new PageResult<>(page);
    }

    @Override
    public PageResult<ExamMakeUp> makeUpCourseList(PageCondition<ExamMakeUp> condition) {
        PageHelper.startPage(condition.getPageNum_(),condition.getPageSize_());
        Page<ExamMakeUp> page = applyExaminationDao.makeUpCourseList(condition.getCondition());
        return new PageResult<>(page);
    }


    //校验是否重复申请或重新申请的条件
    private void checkApplyEffective(GraduateExamApplyExamination applyExamination,Session currentSession) {

        this.checkRepeat(applyExamination);
        //如果是学校代申请,那么需要缓考要变更相应状态,补考回写审核通过原因
        if(applyExamination.getApplyStatus().equals(ApplyStatus.SCHOOL_EXAMINE_PASS)){
            List<GraduateExamApplyExamination> list = new ArrayList<>();
            list.add(applyExamination);
            this.changeStatusToInfo(list,ApplyStatus.SCHOOL_EXAMINE_PASS,"",currentSession);
        }

        //学院代理申请,回写相应数据remark 为审核中
        if(applyExamination.getApplyStatus().equals(ApplyStatus.COLLEGE_EXAMINE_PASS)){
            //补考
            List<GraduateExamApplyExamination> list = new ArrayList<>();
            list.add(applyExamination);
            if(applyExamination.getApplyType().equals(ApplyStatus.EXAM_SITUATION_MAKE_UP)){
                String aduitOpinions = "补考申请审核中";
                examStudentDao.updateStudentScoreMessage(list,aduitOpinions);
            }else{
                String aduitOpinions = "缓考申请审核中";
                examStudentDao.updateExamStudentRemark(list,aduitOpinions);
            }
        }


    }


    //检验当前时间是否有权限申请
    @Override
    public void checkTime(GraduateExamApplyExamination applyExamination){
        Date date = new Date(System.currentTimeMillis());
        Example example = new Example(GraduateExamMakeUpAuth.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("calendarId",applyExamination.getCalendarId());
        criteria.andEqualTo("applyType",applyExamination.getApplyType());
        criteria.andEqualTo("projId",applyExamination.getProjId());
        criteria.andLessThanOrEqualTo("beginTime",date);
        criteria.andGreaterThanOrEqualTo("endTime",date);
        List<GraduateExamMakeUpAuth> makeUpAuths = makeUpAuthDao.selectByExample(example);
        if(CollectionUtil.isEmpty(makeUpAuths)){
            throw new ParameterValidateException("目前还没有开放该申请权限");
        }
    }
}
