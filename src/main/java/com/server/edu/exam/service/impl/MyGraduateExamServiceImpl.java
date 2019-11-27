package com.server.edu.exam.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.server.edu.common.PageCondition;
import com.server.edu.common.locale.I18nUtil;
import com.server.edu.common.rest.PageResult;
import com.server.edu.dictionary.utils.ClassroomCacheUtil;
import com.server.edu.election.dao.StudentDao;
import com.server.edu.election.entity.Student;
import com.server.edu.exam.constants.ApplyStatus;
import com.server.edu.exam.dao.GraduateExamApplyExaminationDao;
import com.server.edu.exam.dao.GraduateExamInfoDao;
import com.server.edu.exam.dao.GraduateExamStudentDao;
import com.server.edu.exam.entity.GraduateExamApplyExamination;
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
                this.cancelApplyByOne(myGraduateExam,applyType,list);
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
        List<GraduateExamApplyExamination> list = new ArrayList<>();
        for (MyGraduateExam myGraduateExam : myExam) {
            GraduateExamApplyExamination applyExamination = new GraduateExamApplyExamination();
            applyExamination.setCalendarId(myGraduateExam.getCalendarId());
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

    private void cancelApplyByOne(MyGraduateExam myExam,Integer applyType,List<GraduateExamApplyExamination> list){
        Example example = new Example(GraduateExamApplyExamination.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("calendarId",myExam.getCalendarId());
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
