package com.server.edu.election.studentelec.rules.bk;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.server.edu.common.locale.I18nUtil;
import com.server.edu.election.dao.ElectionConstantsDao;
import com.server.edu.election.entity.ElectionConstants;
import com.server.edu.election.studentelec.cache.TeachingClassCache;
import com.server.edu.election.studentelec.context.ElecContext;
import com.server.edu.election.studentelec.context.ElecRespose;
import com.server.edu.election.studentelec.context.SelectedCourse;
import com.server.edu.election.studentelec.rules.AbstractRuleExceutor;
import com.server.edu.util.CollectionUtil;

/**
 * 只能选一门体育课的规则
 *
 */
@Component("OnePeCourseCheckerRule")
public class OnePeCourseCheckerRule extends AbstractRuleExceutor
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
        if (StringUtils.isNotBlank(courseClass.getCourseCode()))
        {
            // 体育课程代码
            ElectionConstants electionConstants =
                electionConstantsDao.selectByPrimaryKey(2L);
            Set<SelectedCourse> selectedCourses = context.getSelectedCourses();
            if (courseClass.getTeachClassId() != null
                && electionConstants != null)
            {
                String courseCodes = electionConstants.getValue();
                if (CollectionUtil.isNotEmpty(selectedCourses))
                {
                    List<SelectedCourse> list = selectedCourses.stream()
                        .filter(temp -> courseClass.getCourseCode()
                            .equals(temp.getCourseCode()))
                        .collect(Collectors.toList());
                    if (CollectionUtil.isNotEmpty(list))
                    {
                        if (!courseCodes.contains(courseClass.getCourseCode()))
                        {
                            return true;
                        }
                        else
                        {
                            ElecRespose respose = context.getRespose();
                            respose.getFailedReasons()
                                .put(courseClass.getTeachClassId().toString(),
                                    I18nUtil.getMsg(
                                        "ruleCheck.onePeCourseChecker"));
                        }
                    }
                }
                
            }
        }
        
        return false;
    }
    
}