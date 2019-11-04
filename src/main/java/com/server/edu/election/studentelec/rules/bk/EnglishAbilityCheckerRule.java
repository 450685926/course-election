package com.server.edu.election.studentelec.rules.bk;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.server.edu.election.dao.ElcStuCouLevelDao;
import com.server.edu.election.dao.ElectionConstantsDao;
import com.server.edu.election.entity.ElcStuCouLevel;
import com.server.edu.election.rpc.CultureSerivceInvoker;
import com.server.edu.election.studentelec.cache.StudentInfoCache;
import com.server.edu.election.studentelec.cache.TeachingClassCache;
import com.server.edu.election.studentelec.context.ElecRespose;
import com.server.edu.election.studentelec.context.bk.ElecContextBk;
import com.server.edu.election.studentelec.rules.AbstractElecRuleExceutorBk;
import com.server.edu.util.CollectionUtil;

import tk.mybatis.mapper.entity.Example;

/**
 * 英语课程能力等级检察器<br>
 * 如果课程有英语等级要求，那么学生必须满足要求才能选<br>
 */
@Component("EnglishAbilityCheckerRule")
public class EnglishAbilityCheckerRule extends AbstractElecRuleExceutorBk
{
    
    @Autowired
    private ElcStuCouLevelDao couLevelDao;
    
    @Autowired
    private ElectionConstantsDao constantsDao;
    
    /**
     * 执行选课操作时
     */
    @Override
    public boolean checkRule(ElecContextBk context,
        TeachingClassCache courseClass)
    {
        
        StudentInfoCache studentInfo = context.getStudentInfo();
        String courseCode = courseClass.getCourseCode();
        String englishCourses = constantsDao.findEnglishCourses();
        // 查询不到英语课-通过
        if (StringUtils.isBlank(englishCourses))
        {
            return true;
        }
        // 如果不是英语课-通过
        String[] split = englishCourses.split(",");
        List<String> asList = Arrays.asList(split);
        if (!asList.contains(courseCode))
        {
            return true;
        }
        
        Example example = new Example(ElcStuCouLevel.class);
        example.createCriteria()
            .andEqualTo("studentId", studentInfo.getStudentId());
        List<ElcStuCouLevel> list = couLevelDao.selectByExample(example);
        // 没有配置英语能力-通过
        if (CollectionUtil.isEmpty(list))
        {
            return true;
        }
        Long courseCategoryId = list.get(0).getCourseCategoryId();
        List<String> courseCodeList =
            CultureSerivceInvoker.getCoursesLevelCourse(courseCategoryId);
        if (CollectionUtil.isNotEmpty(courseCodeList)
            && !courseCodeList.contains(courseCode))
        {
            ElecRespose respose = context.getRespose();
            respose.getFailedReasons()
                .put(courseClass.getCourseCodeAndClassCode(), "没有达到课程英语能力要求");
            return false;
        }
        return true;
        
    }
    
}