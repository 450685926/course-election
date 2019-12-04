package com.server.edu.exam.service;

import com.server.edu.common.PageCondition;
import com.server.edu.common.rest.PageResult;
import com.server.edu.exam.vo.MyGraduateExam;

import java.util.List;

public interface MyGraduateExamService {
    /**
    * 我的考试
    * @param myExam
    * @return
    * @author bear
    * @date 2019/9/5 17:14
    */
    PageResult<MyGraduateExam> listMyExam(PageCondition<MyGraduateExam> myExam);

    /**
    * 取消申请缓考
    * @param myExam
    * @return
    * @author bear
    * @date 2019/9/6 10:44
    */
    void cancelApply(List<MyGraduateExam> myExam,Integer examType);

    /**
    * 申请缓考
    * @param
    * @return
    * @author bear
    * @date 2019/10/14 15:45
    */
    void addGraduateApplyList(List<MyGraduateExam> myExam,Integer examType,String applyReason);

    PageResult<MyGraduateExam> listMyExamTime(PageCondition<MyGraduateExam> myExam);

    Boolean checkMakeUp(String studentCode,String courseCode);

    /**列表展示我的考试（支持没有排考也展示）*/
    PageResult<MyGraduateExam> listMyExamTimeAndCourse(PageCondition<MyGraduateExam> myExam);
}
