package com.server.edu.election.studentelec.rules.bk;

import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.server.edu.common.locale.I18nUtil;
import com.server.edu.election.constants.CourseTakeType;
import com.server.edu.election.dao.ElcStudentLimitDao;
import com.server.edu.election.dao.ElectionConstantsDao;
import com.server.edu.election.entity.ElcStudentLimit;
import com.server.edu.election.studentelec.cache.StudentInfoCache;
import com.server.edu.election.studentelec.cache.TeachingClassCache;
import com.server.edu.election.studentelec.context.ElecRespose;
import com.server.edu.election.studentelec.context.bk.ElecContextBk;
import com.server.edu.election.studentelec.context.bk.SelectedCourse;
import com.server.edu.election.studentelec.rules.AbstractElecRuleExceutorBk;
import com.server.edu.election.studentelec.utils.RetakeCourseUtil;

import tk.mybatis.mapper.entity.Example;

/**
 * 选课限制检查器<br>
 * 限制学生的新选学分上限和重修门数上限
 * NewElectionConstraintChecker
 */
@Component("NewElecConstraintCheckerRule")
public class NewElecConstraintCheckerRule extends AbstractElecRuleExceutorBk
{
    
    @Autowired
    private ElectionConstantsDao constantsDao;
    @Autowired
    private ElcStudentLimitDao elcStudentLimitDao;
    
    @Override
    public boolean checkRule(ElecContextBk context,
        TeachingClassCache courseClass)
    {
        boolean count = RetakeCourseUtil.isRetakeCourseBk(context,
            courseClass.getCourseCode());
        StudentInfoCache studentInfo = context.getStudentInfo();
    	String studentId = studentInfo.getStudentId();
    	Long calendarId = context.getCalendarId();
        ElcStudentLimit elcStudentLimit = getLimitNum(studentId, calendarId);
        if (count)
        {//重修
        	int totalNumber = 0;
        	if(elcStudentLimit.getRebuildLimitNumber() != null) {
        		totalNumber = elcStudentLimit.getRebuildLimitNumber();
        	} else {
        		String number = constantsDao.findRebuildCourseNumber();
        		if (StringUtils.isBlank(number))
        		{
        			return true;
        		}
        		totalNumber = Integer.valueOf(number);
			}
			Set<SelectedCourse> selectedCourses = context.getSelectedCourses();
			Set<SelectedCourse> collect = selectedCourses.stream()
			    .filter(selectedCourse -> CourseTakeType.RETAKE
			        .eq(selectedCourse.getCourseTakeType()))
			    .collect(Collectors.toSet());
			int size = collect.size();//已选重修门数
			if (size + 1 <= totalNumber)
			{
			    return true;
			}
			else
			{
			    ElecRespose respose = context.getRespose();
			    respose.getFailedReasons()
			        .put(courseClass.getCourseCodeAndClassCode(),
			            I18nUtil
			                .getMsg("ruleCheck.rebuildElecNumberLimit"));
			    return false;
			}
        }
        else
        {//新选
        	double num = 0.0;
        	if(elcStudentLimit.getNewLimitCredits() != null) {
        		num = elcStudentLimit.getNewLimitCredits();
        	}else {
        		 String credits = constantsDao.findNewCreditsLimit();
                 if (StringUtils.isBlank(credits))
                 {
                     return true;
                 }
                 num = Double.parseDouble(credits);
			}
            Set<SelectedCourse> selectedCourses =
                context.getSelectedCourses();
            Set<SelectedCourse> collect = selectedCourses.stream()
                .filter(selectedCourse -> CourseTakeType.NORMAL
                    .eq(selectedCourse.getCourseTakeType()))
                .collect(Collectors.toSet());
            double size = collect.stream()
                .collect(
                    Collectors.summingDouble(c -> {return c.getCourse().getCredits();}));//已经新选学分
            Double curCredits = courseClass.getCredits();//当前课程学分
            if (curCredits + size <= num)
            {
                return true;
            }
            else
            {
                ElecRespose respose = context.getRespose();
                respose.getFailedReasons()
                    .put(courseClass.getCourseCodeAndClassCode(),
                        I18nUtil.getMsg("ruleCheck.creditsLimit"));
                return false;
            }
        }
        
    }

	private ElcStudentLimit getLimitNum(String studentId, Long calendarId) {
		Example example  = new Example(ElcStudentLimit.class);
		Example.Criteria criteria = example.createCriteria();
		criteria.andEqualTo("studentId", studentId);
		criteria.andEqualTo("calendarId", calendarId);
		ElcStudentLimit elcStudentLimit = elcStudentLimitDao.selectOneByExample(example);
		if(elcStudentLimit==null) {
			elcStudentLimit = new ElcStudentLimit();
		}
		return elcStudentLimit;
	}
    
}
