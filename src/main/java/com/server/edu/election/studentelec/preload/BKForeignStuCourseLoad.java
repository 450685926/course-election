package com.server.edu.election.studentelec.preload;

import java.util.*;
import java.util.stream.Collectors;

import com.server.edu.common.entity.BkPublicCourse;
import com.server.edu.common.entity.BkPublicCourseVo;
import com.server.edu.common.entity.PublicCourse;
import com.server.edu.common.locale.I18nUtil;
import com.server.edu.common.vo.ScoreStudentResultVo;
import com.server.edu.dictionary.service.DictionaryService;
import com.server.edu.election.constants.Constants;
import com.server.edu.election.dao.CourseDao;
import com.server.edu.election.dao.ElcStuCouLevelDao;
import com.server.edu.election.dao.ElectionConstantsDao;
import com.server.edu.election.entity.Course;
import com.server.edu.election.entity.ElcStuCouLevel;
import com.server.edu.election.entity.ElectionConstants;
import com.server.edu.election.entity.ElectionRounds;
import com.server.edu.election.studentelec.context.ElecRequest;
import com.server.edu.election.studentelec.context.ElecRespose;
import com.server.edu.election.studentelec.context.bk.TsCourse;
import com.server.edu.election.studentelec.service.cache.TeachClassCacheService;
import com.server.edu.election.studentelec.service.impl.RoundDataProvider;
import com.server.edu.election.studentelec.utils.ElecContextUtil;
import com.server.edu.exception.ParameterValidateException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.server.edu.common.dto.CultureRuleDto;
import com.server.edu.common.dto.PlanCourseDto;
import com.server.edu.common.dto.PlanCourseTypeDto;
import com.server.edu.election.dto.StudentScoreDto;
import com.server.edu.election.rpc.CultureSerivceInvoker;
import com.server.edu.election.service.BkStudentScoreService;
import com.server.edu.election.studentelec.cache.StudentInfoCache;
import com.server.edu.election.studentelec.cache.TeachingClassCache;
import com.server.edu.election.studentelec.context.CourseGroup;
import com.server.edu.election.studentelec.context.ElecCourse;
import com.server.edu.election.studentelec.context.bk.ElecContextBk;
import com.server.edu.election.studentelec.context.bk.ForeignCourse;
import com.server.edu.election.studentelec.context.bk.PlanCourse;
import com.server.edu.election.util.CourseCalendarNameUtil;
import com.server.edu.election.vo.ElectionRuleVo;
import com.server.edu.util.CollectionUtil;
import tk.mybatis.mapper.entity.Example;

/**
 * 本科生培养计划课程查询
 * 
 */
@Component
public class BKForeignStuCourseLoad extends DataProLoad<ElecContextBk>
{
    Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private CourseDao courseDao;

    @Autowired
    private RoundDataProvider dataProvider;

    @Autowired
    private ElectionConstantsDao electionConstantsDao;
    
    @Autowired
    private BkStudentScoreService bkStudentScoreService;
    
    @Autowired
    private TeachClassCacheService teachClassCacheService;

    @Override
    public int getOrder()
    {
        return 3;
    }
    
    @Override
    public String getProjectIds()
    {
        return "1";
    }

    @Override
    public void load(ElecContextBk context)
    {
        //查询留学生课程
    	Example example = new Example(ElectionConstants.class);
    	example.createCriteria().andEqualTo("managerDeptId", Constants.FIRST).andEqualTo("key", "FOR_OVERSEAS_STUDENT_COURSES");
    	ElectionConstants electionConstants = electionConstantsDao.selectOneByExample(example);
    	String value = electionConstants.getValue();
    	if(StringUtils.isNoneBlank(value)) {
    	    ElecRequest request = context.getRequest();
    	    Long roundId = request.getRoundId();
    		List<String> courseCodes = Arrays.asList(value.split(","));
    		Example cexample = new Example(Course.class);
    		cexample.createCriteria().andEqualTo("managerDeptId", 1).andIn("code", courseCodes);
    		List<Course> courses = courseDao.selectByExample(cexample);
            //List<PlanCourseDto> courseType = CultureSerivceInvoker.findUnGraduateCourse(stu.getStudentId());
//            Map<String, String> map = new HashMap<>(60);
            StudentScoreDto dto = new StudentScoreDto();
            dto.setStudentId(context.getStudentInfo().getStudentId());
            List<ScoreStudentResultVo> stuScore = bkStudentScoreService.getAllStudentScoreList(dto);
            List<String> stuCompletedCourse = new ArrayList<>();
            if(CollectionUtil.isNotEmpty(stuScore)) {
            	stuCompletedCourse = stuScore.stream().map(ScoreStudentResultVo::getCourseCode).collect(Collectors.toList());
            }
            Set<ForeignCourse> foreignStuCourses = context.getForeignStuCourses();//留学生课程
            for(Course course:courses) {
            	String courseCode = course.getCode();
                if(StringUtils.isBlank(courseCode) ||(CollectionUtil.isNotEmpty(stuCompletedCourse) && stuCompletedCourse.contains(courseCode)) ) {
                    continue;
                }
                List<TeachingClassCache> teachingClassCaches =teachClassCacheService.getTeachClasss(roundId, courseCode);
                if (CollectionUtil.isNotEmpty(teachingClassCaches)) {
                    ElecCourse elecCourse = new ElecCourse();
                    elecCourse.setCourseCode(courseCode);
                    elecCourse.setCourseName(course.getName());
                    elecCourse.setCredits(course.getCredits());
                    elecCourse.setNameEn(course.getNameEn());
                    ForeignCourse foreignCourse = new ForeignCourse();
                    foreignCourse.setCourse(elecCourse);
                    foreignStuCourses.add(foreignCourse);
                }
            }
    		
    	}

    }


}
