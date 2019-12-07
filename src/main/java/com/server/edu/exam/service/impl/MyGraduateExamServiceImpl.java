package com.server.edu.exam.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.server.edu.common.PageCondition;
import com.server.edu.common.locale.I18nUtil;
import com.server.edu.common.rest.PageResult;
import com.server.edu.common.vo.SchoolCalendarVo;
import com.server.edu.dictionary.utils.ClassroomCacheUtil;
import com.server.edu.dictionary.utils.SchoolCalendarCacheUtil;
import com.server.edu.election.dao.StudentDao;
import com.server.edu.election.entity.Student;
import com.server.edu.exam.constants.ApplyStatus;
import com.server.edu.exam.dao.GraduateExamApplyExaminationDao;
import com.server.edu.exam.dao.GraduateExamInfoDao;
import com.server.edu.exam.dao.GraduateExamMakeUpAuthDao;
import com.server.edu.exam.dao.GraduateExamStudentDao;
import com.server.edu.exam.entity.GraduateExamApplyExamination;
import com.server.edu.exam.entity.GraduateExamMakeUpAuth;
import com.server.edu.exam.rpc.BaseresServiceExamInvoker;
import com.server.edu.exam.service.GraduateExamApplyExaminationService;
import com.server.edu.exam.service.MyGraduateExamService;
import com.server.edu.exam.util.GraduateExamTransTime;
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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @description:
 * @author: bear
 * @create: 2019-09-05 17:15
 */
@Service
@Primary
public class MyGraduateExamServiceImpl implements MyGraduateExamService {

    @Autowired
    private GraduateExamInfoDao examInfoDao;

    @Autowired
    private StudentDao studentDao;

    @Autowired
    private GraduateExamApplyExaminationDao applyExaminationDao;

    @Autowired
    private GraduateExamApplyExaminationService applyExaminationService;

    @Autowired
    private GraduateExamStudentDao examStudentDao;

    @Autowired
    private GraduateExamMakeUpAuthDao makeUpAuthDao;

    @Override
    public PageResult<MyGraduateExam> listMyExam(PageCondition<MyGraduateExam> myExam) {
        MyGraduateExam condition = myExam.getCondition();
        if(condition.getCalendarId() == null || condition.getExamType() == null){
            throw new ParameterValidateException(I18nUtil.getMsg("baseresservice.parameterError"));
        }
        Session session = SessionUtils.getCurrentSession();
        String studentCode = session.realUid();
        Student student = studentDao.findStudentByCode(studentCode);
        if(student == null){
            throw new ParameterValidateException("该学生不存在学籍中");
        }
        condition.setStudentCode(studentCode);
        PageHelper.startPage(myExam.getPageNum_(),myExam.getPageSize_());
        Page<MyGraduateExam> page = examInfoDao.listMyExam(condition);
        return new PageResult<>(page);
    }

    @Override
    @Transactional
    public void cancelApply(List<MyGraduateExam> myExam,Integer applyType) {
        if(CollectionUtil.isNotEmpty(myExam) || applyType == null ){
            Session session = SessionUtils.getCurrentSession();
            String studentCode = session.realUid();
            Student student = studentDao.findStudentByCode(studentCode);
            if(student == null){
                throw new ParameterValidateException("该学生不存在学籍中");
            }
            List<GraduateExamApplyExamination> list = new ArrayList<>();
            for (MyGraduateExam myGraduateExam : myExam) {
                myGraduateExam.setStudentCode(studentCode);
                this.cancelApplyByOne(myGraduateExam,applyType,list,session);
            }

            if(CollectionUtil.isNotEmpty(list)){
                String aduitOpinions = "";
                if(applyType.equals(ApplyStatus.EXAM_SITUATION_MAKE_UP)){
                    examStudentDao.updateStudentScoreMessage(list,aduitOpinions);
                }else{
                    examStudentDao.updateExamStudentRemark(list,aduitOpinions);
                }
            }
        }

    }

    @Override
    @Transactional
    public void addGraduateApplyList(List<MyGraduateExam> myExam,Integer applyType,String applyReason) {
        if(CollectionUtil.isEmpty(myExam) || applyType == null){
            throw new ParameterValidateException(I18nUtil.getMsg("baseresservice.parameterError"));
        }
        Session currentSession = SessionUtils.getCurrentSession();
        String dptId = currentSession.getCurrentManageDptId();
        //申请缓考，排考学期是申请学期的下一学期（补考根据权限开关获取开放的学期）
        Long examCalendarId = 0L;
        if(ApplyStatus.EXAM_SITUATION_SLOW.equals(applyType)){
            Long calendarId = myExam.get(0).getCalendarId();
            //远程调用获取下学期
            SchoolCalendarVo preOrNextTerm = BaseresServiceExamInvoker.getPreOrNextTerm(calendarId, true);
            examCalendarId = preOrNextTerm.getId();
        }else{
            //获取权限的学期
            Example example = new Example(GraduateExamMakeUpAuth.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo("applyType",applyType);
            criteria.andEqualTo("projId",dptId);
            GraduateExamMakeUpAuth graduateExamMakeUpAuth = makeUpAuthDao.selectOneByExample(example);
            if(graduateExamMakeUpAuth == null){
                throw new ParameterValidateException("目前还没有开放该申请权限");
            }
            examCalendarId = graduateExamMakeUpAuth.getCalendarId();
        }
        List<GraduateExamApplyExamination> list = new ArrayList<>();
        for (MyGraduateExam myGraduateExam : myExam) {
            GraduateExamApplyExamination applyExamination = new GraduateExamApplyExamination();
            if(ApplyStatus.EXAM_SITUATION_SLOW.equals(applyType)){
                applyExamination.setCalendarId(myGraduateExam.getCalendarId());
            }else{
                applyExamination.setCalendarId(examCalendarId);
                //校验学生课程是否有资格申请补考
               Boolean flag = this.checkMakeUp(currentSession.realUid(),myGraduateExam.getCourseCode());
               if(!flag){
                   throw new ParameterValidateException("课程:"+myGraduateExam.getCourseCode()+",不满足申请补考的资格");
               }
            }
            applyExamination.setExamCalendarId(examCalendarId);
            applyExamination.setApplySource(ApplyStatus.APPLY_SOURCE_MYSELF);
            applyExamination.setApplyStatus(ApplyStatus.NOT_EXAMINE);
            applyExamination.setProjId(dptId);
            applyExamination.setStudentCode(currentSession.realUid());
            applyExamination.setApplyType(applyType);
            applyExamination.setCourseCode(myGraduateExam.getCourseCode());
            applyExamination.setApplyReason(applyReason);
            applyExamination.setTeachingClassId(myGraduateExam.getTeachingClassId());
            applyExamination.setCreateAt(new Date());
            if(myGraduateExam.getExamSituation() != null){
                boolean flag = myGraduateExam.getExamSituation().equals(ApplyStatus.EXAM_SITUATION_NORMAL);
                if(!flag){
                    throw new ParameterValidateException("已经是缓考状态，不能再次申请缓考");
                }
            }
            applyExaminationService.checkTime(applyExamination);
            applyExaminationService.checkRepeat(applyExamination);
            list.add(applyExamination);
        }

        if(CollectionUtil.isNotEmpty(list)){
            if(applyType.equals(ApplyStatus.EXAM_SITUATION_MAKE_UP)){
                String aduitOpinions = "补考申请审核中";
                examStudentDao.updateStudentScoreMessage(list,aduitOpinions);
            }else{
                String aduitOpinions = "缓考申请审核中";
                examStudentDao.updateExamStudentRemark(list,aduitOpinions);
            }
        }
    }

    @Override
    public PageResult<MyGraduateExam> listMyExamTime(PageCondition<MyGraduateExam> myExam) {
        MyGraduateExam condition = myExam.getCondition();
        if(condition.getCalendarId() == null || condition.getExamType() == null){
            throw new ParameterValidateException(I18nUtil.getMsg("baseresservice.parameterError"));
        }
        Session session = SessionUtils.getCurrentSession();
        String dptId = session.getCurrentManageDptId();
        String studentCode = session.realUid();
        Student student = studentDao.findStudentByCode(studentCode);
        if(student == null){
            throw new ParameterValidateException("该学生不存在学籍中");
        }
        int mode = (int)(condition.getCalendarId() % 6 );
        condition.setStudentCode(studentCode);
        condition.setProjId(dptId);
        condition.setMode(mode);
        condition.setNotice(ApplyStatus.PASS_INT);
        //查询已排考的课程
        List<String> examCourseCode =  examStudentDao.findExamStuCourseCode(condition);
        condition.setCourseCodes(examCourseCode);
        Page<MyGraduateExam> page = new Page<>();
        PageHelper.startPage(myExam.getPageNum_(),myExam.getPageSize_());
        if(condition.getExamType().equals(ApplyStatus.FINAL_EXAM)){
             page = examInfoDao.listMyExamTimeFinal(condition);
        }else{
            page = examInfoDao.listMyExamTimeMakeUp(condition);
        }
        return new PageResult<>(page);
    }

    @Override
    public Boolean checkMakeUp(String studentCode, String courseCode) {
       int i =  examStudentDao.checkMakeUp(studentCode,courseCode);
       if(i > 1){
           return false;
       }
        return true;
    }

    @Override
    public PageResult<MyGraduateExam> listMyExamTimeAndCourse(PageCondition<MyGraduateExam> myExam) {
        MyGraduateExam condition = myExam.getCondition();
        if(condition.getCalendarId() == null || condition.getExamType() == null){
            throw new ParameterValidateException(I18nUtil.getMsg("baseresservice.parameterError"));
        }
        Session session = SessionUtils.getCurrentSession();
        String dptId = session.getCurrentManageDptId();
        String studentCode = session.realUid();
        Student student = studentDao.findStudentByCode(studentCode);
        if(student == null){
            throw new ParameterValidateException("请用学生登陆");
        }
        int mode = (int)(condition.getCalendarId() % 6 );
        condition.setStudentCode(studentCode);
        condition.setProjId(dptId);
        condition.setMode(mode);
        condition.setNotice(ApplyStatus.PASS_INT);
        Page<MyGraduateExam> page = new Page<>();
        //查询已排考的课程
        List<String> examCourseCode =  examStudentDao.findExamStuCourseCode(condition);
        condition.setCourseCodes(examCourseCode);
        PageHelper.startPage(myExam.getPageNum_(),myExam.getPageSize_());
        if(condition.getExamType().equals(ApplyStatus.FINAL_EXAM)){
            page = examStudentDao.listCourseTake(condition);
            if(CollectionUtil.isNotEmpty(page)){
                //查询已经排考的课程
                List<MyGraduateExam> examList = examStudentDao.findExamStudentAndCourse(condition);
                List<MyGraduateExam> result = page.getResult();
                if(CollectionUtil.isNotEmpty(examList)){
                    List<String> examCourses = examList.stream().map(vo -> vo.getCourseCode()).collect(Collectors.toList());
                    for (MyGraduateExam myGraduateExam : examList) {
                        for (MyGraduateExam graduateExam : result) {
                            if(myGraduateExam.getCourseCode().equals(graduateExam.getCourseCode())){
                                graduateExam.setExamSituation(myGraduateExam.getExamSituation());
                                graduateExam.setRoomId(myGraduateExam.getRoomId());
                                graduateExam.setRoomName(myGraduateExam.getRoomName());
                                graduateExam.setExamTime(myGraduateExam.getExamTime());
                                graduateExam.setRemark(myGraduateExam.getRemark());
                                break;
                            }
                        }
                    }
                    List<MyGraduateExam> noExamCourse = result.stream().filter(vo -> !examCourses.contains(vo.getCourseCode())).collect(Collectors.toList());
                    if(CollectionUtil.isNotEmpty(noExamCourse)){
                        List<MyGraduateExam> noList =  examStudentDao.findExamNotice(noExamCourse,condition.getExamType(),condition.getCalendarId());
                        if(CollectionUtil.isNotEmpty(noList)){
                            for (MyGraduateExam myGraduateExam : noList) {
                                for (MyGraduateExam graduateExam : result) {
                                    if(myGraduateExam.getCourseCode().equals(graduateExam.getCourseCode())){
                                        graduateExam.setExamTime(myGraduateExam.getExamTime());
                                        break;
                                    }
                                }
                            }
                        }
                    }

                }

            }
        }else{
            page = examInfoDao.listMyExamTimeMakeUp(condition);
        }
        return new PageResult<>(page);
    }

    private void cancelApplyByOne(MyGraduateExam myExam,Integer applyType,List<GraduateExamApplyExamination> list,Session session){
        Example example = new Example(GraduateExamApplyExamination.class);
        Example.Criteria criteria = example.createCriteria();
        if(ApplyStatus.EXAM_SITUATION_MAKE_UP.equals(applyType)){
            //获取权限的学期
            Example exampleAuth = new Example(GraduateExamMakeUpAuth.class);
            Example.Criteria criteriaAuth = exampleAuth.createCriteria();
            criteriaAuth.andEqualTo("applyType",applyType);
            criteriaAuth.andEqualTo("projId",session.getCurrentManageDptId());
            GraduateExamMakeUpAuth graduateExamMakeUpAuth = makeUpAuthDao.selectOneByExample(exampleAuth);
            if(graduateExamMakeUpAuth != null){
                criteria.andEqualTo("calendarId",graduateExamMakeUpAuth.getCalendarId());
            }
        }else{
            criteria.andEqualTo("calendarId",myExam.getCalendarId());
        }
        criteria.andEqualTo("studentCode",myExam.getStudentCode());
        criteria.andEqualTo("courseCode",myExam.getCourseCode());
        criteria.andEqualTo("applyType", applyType);
        GraduateExamApplyExamination examApplyExamination = applyExaminationDao.selectOneByExample(example);
        if(examApplyExamination == null){
            if(ApplyStatus.EXAM_SITUATION_MAKE_UP.equals(applyType)){
                throw new ParameterValidateException(myExam.getCourseCode()+"该课程并没有申请补考，不需要取消");
            }else{
                throw new ParameterValidateException(myExam.getCourseCode()+"该课程并没有申请缓考，不需要取消");
            }
        }else{
            Integer applyStatus = examApplyExamination.getApplyStatus();
            //待审核可以取消(删除该条数据)
            if(applyStatus == 0){
                list.add(examApplyExamination);
                applyExaminationDao.deleteByExample(example);
            }else if(applyStatus == 2 ){
                throw new ParameterValidateException(myExam.getCourseCode()+"该课程申请正在审批中，不需要取消");
            }else if(applyStatus == 4){
                throw new ParameterValidateException(myExam.getCourseCode()+"该课程申请已经通过，无法取消");
            }else{
                throw new ParameterValidateException(myExam.getCourseCode()+"该课程申请不通过，不需要取消");
            }
        }
    }

}
