package com.server.edu.election.studentelec.rules.bk;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.server.edu.common.locale.I18nUtil;
import com.server.edu.election.dao.ElcPeFreeStdsDao;
import com.server.edu.election.dao.ElectionConstantsDao;
import com.server.edu.election.studentelec.cache.TeachingClassCache;
import com.server.edu.election.studentelec.context.ElecRespose;
import com.server.edu.election.studentelec.context.bk.ElecContextBk;
import com.server.edu.election.studentelec.context.bk.SelectedCourse;
import com.server.edu.election.studentelec.rules.AbstractElecRuleExceutorBk;
import com.server.edu.util.CollectionUtil;

/**
 * 只能选一门体育课的规则
 * OnePeCourseChecker
 */
@Component("OnePeCourseCheckerRule")
public class OnePeCourseCheckerRule extends AbstractElecRuleExceutorBk
{
    @Autowired
    private ElectionConstantsDao electionConstantsDao;
    
    @Autowired
    private ElcPeFreeStdsDao elcPeFreeStdsDao;
    
    /**
     * 执行选课操作时
     */
    @Override
    public boolean checkRule(ElecContextBk context,
        TeachingClassCache courseClass)
    {
        // 体育课程代码
        String PECourses = electionConstantsDao.findPECourses();
        if (courseClass.getTeachClassId() != null
            && StringUtils.isNotBlank(PECourses))
        {
            String[] courseCodes = PECourses.split(",");//体育课程代码
            List<String> list = Arrays.asList(courseCodes);
            if (CollectionUtil.isEmpty(list))
            {//没有体育课
                return true;
            }
            if (!list.contains(courseClass.getCourseCode()))//不是体育课
            {
                return true;
            }
            
            int i = elcPeFreeStdsDao
                .findStudentByStuId(context.getStudentInfo().getStudentId());
            if (i != 0)
            {
                //在体育课不限选名单中
                return true;
            }
            
            Set<SelectedCourse> selectedCourses = context.getSelectedCourses();
            if (CollectionUtil.isNotEmpty(selectedCourses))
            {
                for (SelectedCourse selectedCours : selectedCourses)
                {
                    if (list
                        .contains(selectedCours.getCourse().getCourseCode()))
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