package com.server.edu.election.studentelec.rules.bk;

import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.server.edu.common.locale.I18nUtil;
import com.server.edu.election.constants.Constants;
import com.server.edu.election.constants.CourseTakeType;
import com.server.edu.election.dao.ElectionConstantsDao;
import com.server.edu.election.studentelec.cache.TeachingClassCache;
import com.server.edu.election.studentelec.context.ElecContext;
import com.server.edu.election.studentelec.context.ElecRespose;
import com.server.edu.election.studentelec.context.SelectedCourse;
import com.server.edu.election.studentelec.rules.AbstractElecRuleExceutor;

/**
 * 选课限制检查器<br>
 * 限制学生的新选学分上限和重修门数上限
 */
@Component("NewElecConstraintCheckerRule")
public class NewElecConstraintCheckerRule extends AbstractElecRuleExceutor
{
    
    @Autowired
    private ElectionConstantsDao constantsDao;
    
    @Override
    public boolean checkRule(ElecContext context,
        TeachingClassCache courseClass)
    {
        if (Constants.REBUILD_CALSS.equals(courseClass.getTeachClassType()))
        {//重修
            String number = constantsDao.findRebuildCourseNumber();
            if (StringUtils.isBlank(number))
            {
                ElecRespose respose = context.getRespose();
                respose.getFailedReasons()
                    .put(courseClass.getCourseCodeAndClassCode(),
                        I18nUtil.getMsg("ruleCheck.rebuildElcNumNotExist"));
                return false;
            }
            int totalNumber = 0;
            try
            {
                totalNumber = Integer.parseInt(number);
                Set<SelectedCourse> selectedCourses =
                    context.getSelectedCourses();
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
            catch (NumberFormatException e)
            {
                e.printStackTrace();
                ElecRespose respose = context.getRespose();
                respose.getFailedReasons()
                    .put(courseClass.getCourseCodeAndClassCode(),
                        I18nUtil.getMsg("ruleCheck.psrseError"));
                return false;
                
            }
        }
        else
        {//新选
            String credits = constantsDao.findNewCreditsLimit();
            if (StringUtils.isBlank(credits))
            {
                ElecRespose respose = context.getRespose();
                respose.getFailedReasons()
                    .put(courseClass.getCourseCodeAndClassCode(),
                        I18nUtil.getMsg("ruleCheck.rebuildElcNumNotExist"));
                return false;
            }
            double num = 0.0;
            try
            {
                num = Double.parseDouble(credits);
                Set<SelectedCourse> selectedCourses =
                    context.getSelectedCourses();
                Set<SelectedCourse> collect = selectedCourses.stream()
                    .filter(selectedCourse -> CourseTakeType.NORMAL
                        .eq(selectedCourse.getCourseTakeType()))
                    .collect(Collectors.toSet());
                double size = collect.stream()
                    .collect(
                        Collectors.summingDouble(SelectedCourse::getCredits));//已经新选学分
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
            catch (NumberFormatException e)
            {
                e.printStackTrace();
                ElecRespose respose = context.getRespose();
                respose.getFailedReasons()
                    .put(courseClass.getCourseCodeAndClassCode(),
                        I18nUtil.getMsg("ruleCheck.psrseError"));
                return false;
                
            }
        }
        
    }
    
}
