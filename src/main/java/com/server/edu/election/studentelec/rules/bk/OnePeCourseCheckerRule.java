package com.server.edu.election.studentelec.rules.bk;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.server.edu.common.locale.I18nUtil;
import com.server.edu.election.dao.ElectionConstantsDao;
import com.server.edu.election.entity.ElectionConstants;
import com.server.edu.election.studentelec.cache.TeachingClassCache;
import com.server.edu.election.studentelec.context.ElecContext;
import com.server.edu.election.studentelec.context.ElecRespose;
import com.server.edu.election.studentelec.context.SelectedCourse;
import com.server.edu.election.studentelec.rules.AbstractElecRuleExceutor;
import com.server.edu.util.CollectionUtil;

/**
 * 只能选一门体育课的规则
 */
@Component("OnePeCourseCheckerRule")
public class OnePeCourseCheckerRule extends AbstractElecRuleExceutor
{
    @Autowired
    private ElectionConstantsDao electionConstantsDao;
    
    /**
     * 执行选课操作时
     */
    @Override
    public boolean checkRule(ElecContext context,
        TeachingClassCache courseClass)
    {
        // 体育课程代码
        ElectionConstants electionConstants =
            electionConstantsDao.selectByPrimaryKey(2L);
        Set<SelectedCourse> selectedCourses = context.getSelectedCourses();
        if (courseClass.getTeachClassId() != null && electionConstants != null)
        {
            String courseCodes = electionConstants.getValue();//体育课程代码
            
            if (!courseCodes.contains(courseClass.getCourseCode()))
            {
                return true;
            }
            
            if (CollectionUtil.isNotEmpty(selectedCourses))
            {
                for (SelectedCourse selectedCours : selectedCourses)
                {
                    if (courseCodes.contains(selectedCours.getCourseCode()))
                    {
                        ElecRespose respose = context.getRespose();
                        respose.getFailedReasons()
                            .put(courseClass.getCourseCodeAndClassCode(),
                                I18nUtil
                                    .getMsg("ruleCheck.onePeCourseChecker"));
                        return false;
                    }
                }
            }
        }
        
        return true;
    }
    
}