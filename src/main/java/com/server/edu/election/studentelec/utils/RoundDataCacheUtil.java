package com.server.edu.election.studentelec.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.server.edu.common.entity.TeacherInfo;
import com.server.edu.common.vo.SchoolCalendarVo;
import com.server.edu.election.constants.Constants;
import com.server.edu.election.dao.ElecRoundStuDao;
import com.server.edu.election.dao.ElectionParameterDao;
import com.server.edu.election.dao.ElectionRoundsConditionDao;
import com.server.edu.election.dao.ElectionRuleDao;
import com.server.edu.election.dto.CourseOpenDto;
import com.server.edu.election.entity.ElectionParameter;
import com.server.edu.election.entity.ElectionRounds;
import com.server.edu.election.entity.ElectionRoundsCondition;
import com.server.edu.election.rpc.BaseresServiceInvoker;
import com.server.edu.election.studentelec.cache.CourseCache;
import com.server.edu.election.studentelec.cache.TeachingClassCache;
import com.server.edu.election.studentelec.context.ClassTimeUnit;
import com.server.edu.election.studentelec.preload.CourseGradeLoad;
import com.server.edu.election.vo.ElectionRuleVo;

/**
 * 轮次数据缓存类
 * 
 * 
 * @author  OuYangGuoDong
 * @version  [版本号, 2019年4月26日]
 * @see  [相关类/方法]
 * @since  [产品/模块版本]
 */
@Component
@Lazy(false)
public class RoundDataCacheUtil
{
    @Autowired
    private RedisTemplate<String, Integer> elecNumRedis;
    
    @Autowired
    private ElectionRuleDao ruleDao;
    
    @Autowired
    private ElectionParameterDao parameterDao;
    
    @Autowired
    private ElectionRoundsConditionDao electionRoundsConditionDao;
    
    /**
     * 缓存轮次选课规则
     * 
     * @param ops
     * @param roundId 轮次ID
     * @param timeout 缓存失效时间分钟
     * @param ruleKeys 已存在的选课规则
     * @see [类、类#方法、类#成员]
     */
    public void cacheRoundRule(ValueOperations<String, String> ops,
        Long roundId, long timeout, Set<String> ruleKeys)
    {
        List<ElectionRuleVo> rules = ruleDao.selectByRoundId(roundId);
        List<ElectionParameter> params = parameterDao.selectAll();
        for (ElectionRuleVo rule : rules)
        {
            rule.setList(new ArrayList<>());
            for (ElectionParameter param : params)
            {
                if (param.getRuleId().equals(rule.getId()))
                {
                    rule.getList().add(param);
                }
            }
            String key = Keys.getRoundRuleKey(roundId, rule.getServiceName());
            if (ruleKeys.contains(key))
            {
                ruleKeys.remove(key);
            }
            ops.set(key, JSON.toJSONString(rule), timeout, TimeUnit.MINUTES);
        }
    }
    
    @Autowired
    private CourseGradeLoad gradeLoad;
    
    /**
     * 缓存教学班
     * 
     * @param ops
     * @param timeout 缓存过期时间分钟
     * @param calendarId 学期ID
     * @param teachClasss 教学班
     * @param classKeys redis已经存在的教学班KEY
     * @return
     * @see [类、类#方法、类#成员]
     */
    public Set<Long> cacheTeachClass(ValueOperations<String, String> ops,
        long timeout, Long calendarId, List<CourseOpenDto> teachClasss,
        Set<String> classKeys)
    {
        Set<Long> teachClassIds = new HashSet<>();
        
        List<Long> classIds = teachClasss.stream()
            .map(temp -> temp.getTeachingClassId())
            .collect(Collectors.toList());
        //按周数拆分的选课数据集合
        Map<Long, List<ClassTimeUnit>> collect =
            gradeLoad.groupByTime(classIds);
        Map<String, TeacherInfo> teacherMap = new HashMap<>();
        
        for (CourseOpenDto lesson : teachClasss)
        {
            Long teachingClassId = lesson.getTeachingClassId();
            TeachingClassCache courseClass = new TeachingClassCache();
            
            courseClass.setCourseCode(lesson.getCourseCode());
            courseClass.setCourseName(lesson.getCourseName());
            courseClass.setCredits(lesson.getCredits());
            courseClass.setNameEn(lesson.getCourseNameEn());
            courseClass.setTeachClassId(teachingClassId);
            courseClass.setTeachClassCode(lesson.getTeachingClassCode());
            courseClass.setCampus(lesson.getCampus());
            courseClass.setTeachClassType(lesson.getTeachClassType());
            courseClass.setMaxNumber(lesson.getMaxNumber());
            courseClass.setCurrentNumber(lesson.getCurrentNumber());
            courseClass.setPublicElec(
                lesson.getIsElective() == Constants.ONE ? true : false);
            
            List<ClassTimeUnit> times =
                gradeLoad.concatTime(collect, teacherMap, courseClass);
            courseClass.setTimes(times);
            
            String classText = JSON.toJSONString(courseClass);
            String classKey = Keys.getClassKey(calendarId, teachingClassId);
            if (classKeys.contains(classKey))
            {
                classKeys.remove(classKey);
            }
            setElecNumberToRedis(timeout,
                teachingClassId,
                courseClass.getCurrentNumber());
            // 保存教学班信息
            ops.set(classKey, classText, timeout, TimeUnit.MINUTES);
        }
        teachClassIds.addAll(classIds);
        return teachClassIds;
    }
    
    /**
     * 缓存课程数据
     * @param ops
     * @param timeout缓存结束时间分钟
     * @param roundId 轮次ID
     * @param teachClassIds课程对应的教学班ID
     * @param cour课程
     * @param courseKeys已存在于redis的key
     */
    public void cacheCourse(ValueOperations<String, String> ops, long timeout,
        Long roundId, Set<Long> teachClassIds, CourseOpenDto cour,
        Set<String> courseKeys)
    {
        CourseCache course = new CourseCache();
        course.setCourseCode(cour.getCourseCode());
        course.setCourseName(cour.getCourseName());
        course.setCredits(cour.getCredits());
        course.setNameEn(cour.getCourseNameEn());
        course.setTeachClassIds(teachClassIds);
        
        String courseKey =
            Keys.getRoundCourseKey(roundId, cour.getCourseCode());
        if (courseKeys.contains(courseKey))
        {
            courseKeys.remove(courseKey);
        }
        String text = JSON.toJSONString(course);
        ops.set(courseKey, text, timeout, TimeUnit.MINUTES);
    }
    
    private void setElecNumberToRedis(long timeout, Long teachingClassId,
        Integer currentNumber)
    {
        // 保存教学班已选课人数
        currentNumber = currentNumber == null ? 0 : currentNumber;
        elecNumRedis.opsForValue()
            .set(Keys.getClassElecNumberKey(teachingClassId),
                currentNumber,
                timeout,
                TimeUnit.MINUTES);
    }
    
    @Autowired
    private ElecRoundStuDao roundStuDao;
    
    /**
          *  缓存轮次学生
     * @param ops
     * @param roundId
     * @param timeout缓存的保持时间分钟
     * @param roundStuKeys redis已存在的Key
     * 
     * */
    public void cacheRoundStu(ValueOperations<String, String> ops, Long roundId,
        long timeout, Set<String> roundStuKeys)
    {
        List<String> stuIds = roundStuDao.findStuByRoundId(roundId);
        for (String stuId : stuIds)
        {
            String roundStuKey = Keys.getRoundStuKey(roundId, stuId);
            if (roundStuKeys.contains(roundStuKey))
            {
                roundStuKeys.remove(roundStuKey);
            }
            
            ops.set(roundStuKey, stuId, timeout, TimeUnit.MINUTES);
        }
        
    }
    
	/**
	     *  缓存轮次条件
	* @param ops
	* @param roundId
	* @param timeout缓存的保持时间分钟
	* @param roundConKeys redis已存在的Key
	* 
	* */
	public void cacheRoundCondition(ValueOperations<String, String> ops, Long roundId,
	   long timeout,Set<String> roundConKeys)
	{
	   String roundConKey = Keys.getRoundConditionOne(roundId);
	   if(!roundConKey.contains(roundConKey)) {
		   ElectionRoundsCondition electionRoundsCondition = electionRoundsConditionDao.selectByPrimaryKey(roundId);
		   ops.set(roundConKey, JSONArray.toJSONString(electionRoundsCondition), timeout, TimeUnit.MINUTES);
	   }
	}

    public void cachePreSemester(ValueOperations<String, String> ops, ElectionRounds round, long timeout){
        Long calendarId = round.getCalendarId();//当前学期
        SchoolCalendarVo preSemester = BaseresServiceInvoker.getPreSemester(calendarId);
        Long id = preSemester.getId();
        String roundPreSemester =String.format(Keys.ROUND_PRESEMESTER, round.getId());
        ops.set(roundPreSemester, Long.toString(id),timeout, TimeUnit.MINUTES);
    }
}
