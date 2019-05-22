package com.server.edu.election.studentelec.service.cache;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.server.edu.common.entity.Teacher;
import com.server.edu.dictionary.utils.TeacherCacheUtil;
import com.server.edu.election.constants.Constants;
import com.server.edu.election.dao.ElecRoundCourseDao;
import com.server.edu.election.dto.CourseOpenDto;
import com.server.edu.election.studentelec.cache.TeachingClassCache;
import com.server.edu.election.studentelec.context.ClassTimeUnit;
import com.server.edu.election.studentelec.preload.CourseGradeLoad;
import com.server.edu.election.studentelec.utils.Keys;
import com.server.edu.util.CollectionUtil;

/**
 * 缓存教学班，教学班是公共的
 * 
 * 
 * @author  OuYangGuoDong
 * @version  [版本号, 2019年5月21日]
 * @see  [相关类/方法]
 * @since  [产品/模块版本]
 */
@Service
public class TeachClassCacheService extends AbstractCacheService
{
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
    
    @Autowired
    private ElecRoundCourseDao roundCourseDao;
    
    @Autowired
    private CourseGradeLoad gradeLoad;
    
    @Autowired
    private RoundCacheService roundCacheService;
    
    @Autowired
    private StringRedisTemplate strTemplate;
    
    public static HashOperations<String, String, TeachingClassCache> opsTeachClass()
    {
        RedisTemplate<String, TeachingClassCache> redisTemplate =
            redisTemplate(TeachingClassCache.class);
        HashOperations<String, String, TeachingClassCache> ops =
            redisTemplate.opsForHash();
        return ops;
    }
    
    public static HashOperations<String, String, Integer> opsClassNum()
    {
        RedisTemplate<String, Integer> redisTemplate =
            redisTemplate(Integer.class);
        
        HashOperations<String, String, Integer> ops =
            redisTemplate.opsForHash();
        return ops;
    }
    
    public void cacheAllTeachClass(Long calendarId)
    {
        PageInfo<CourseOpenDto> page = new PageInfo<>();
        page.setNextPage(1);
        page.setHasNextPage(true);
        while (page.isHasNextPage())
        {
            PageHelper.startPage(page.getNextPage(), 300);
            List<CourseOpenDto> lessons =
                roundCourseDao.selectTeachingClassByCalendarId(calendarId);
            this.cacheTeachClass(100, lessons);
            
            page = new PageInfo<>(lessons);
        }
    }
    
    static String[] split(String str)
    {
        return str.split(",");
    }
    
    /**
     * 缓存教学班
     * 
     * @param template
     * @param timeout 缓存过期时间分钟
     * @param teachClasss 教学班
     * @param classKeys redis已经存在的教学班KEY
     * @return
     * @see [类、类#方法、类#成员]
     */
    public void cacheTeachClass(long timeout, List<CourseOpenDto> teachClasss)
    {
        if (CollectionUtil.isEmpty(teachClasss))
        {
            return;
        }
        List<Long> classIds = teachClasss.stream()
            .map(temp -> temp.getTeachingClassId())
            .collect(Collectors.toList());
        //按周数拆分的选课数据集合
        Map<Long, List<ClassTimeUnit>> collect =
            gradeLoad.groupByTime(classIds);
        Map<String, Teacher> teacherMap = new HashMap<>();
        
        Map<String, TeachingClassCache> map = new HashMap<>();
        Map<String, Integer> numMap = new HashMap<>();
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
            String tName = null;
            tName = getTeacherName(times, tName);
            if (StringUtils.isNotBlank(tName))
            {
                courseClass.setTeacherName(tName);
            }
            numMap.put(teachingClassId.toString(),
                courseClass.getCurrentNumber());
            map.put(teachingClassId.toString(), courseClass);
        }
        // 缓存选课人数
        opsClassNum().putAll(Keys.getClassElecNumberKey(), numMap);
        
        strTemplate
            .expire(Keys.getClassElecNumberKey(), timeout, TimeUnit.MINUTES);
        
        // 缓存教学班信息
        String key = Keys.getClassKey();
        opsTeachClass().putAll(key, map);
        
        strTemplate.expire(key, timeout, TimeUnit.MINUTES);
        
    }
    
    public String getTeacherName(List<ClassTimeUnit> times, String tName)
    {
        if (CollectionUtil.isNotEmpty(times))
        {
            List<String> teacherSet = new ArrayList<>(times.stream()
                .map(ClassTimeUnit::getTeacherCode)
                .collect(Collectors.toSet()));
            if (CollectionUtil.isNotEmpty(teacherSet))
            {
                String str = StringUtils.join(teacherSet, ",");
                List<String> nameList = new ArrayList<>();
                Collections.addAll(nameList, str.split(","));
                Set<String> tnames = new HashSet<>(nameList);
                List<Teacher> teachers = TeacherCacheUtil
                    .getTeachers(tnames.toArray(new String[] {}));
                List<String> names = teachers.stream().map(t -> {
                    if (t == null)
                        return "";
                    return String.format("%s(%s)", t.getName(), t.getCode());
                }).collect(Collectors.toList());
                
                tName = StringUtils.join(names, ",");
            }
        }
        return tName;
    }
    
    /**
     * 
     * 通过轮次与课程代码获取教学班信息
     * @param roundId
     * @param courseCode
     * @return
     */
    public List<TeachingClassCache> getTeachClasss(Long roundId,
        String courseCode)
    {
        List<TeachingClassCache> lessons = new ArrayList<>();
        
        List<Long> teachClassIds =
            this.roundCacheService.getTeachClassIds(roundId, courseCode);
        if (CollectionUtil.isEmpty(teachClassIds))
        {
            return lessons;
        }
        
        if (CollectionUtil.isNotEmpty(teachClassIds))
        {
            Collections.sort(teachClassIds);
            
            List<String> keys = teachClassIds.stream()
                .map(String::valueOf)
                .collect(Collectors.toList());
            
            HashOperations<String, String, TeachingClassCache> hash =
                opsTeachClass();
            lessons = hash.multiGet(Keys.getClassKey(), keys);
        }
        return lessons;
    }
    
    /**
     * 获取指定教学班信息
     * 
     * @param calendarId 校历
     * @param teachClassId 教学班ID
     * @return
     */
    public TeachingClassCache getTeachClass(Long roundId, String courseCode,
        Long teachClassId)
    {
        if (roundId == null || StringUtils.isBlank(courseCode)
            || teachClassId == null)
        {
            logger.warn(
                "---- roundId, courseCode and teachClassId can not be null ----");
            return null;
        }
        
        List<Long> teachClassIds =
            this.roundCacheService.getTeachClassIds(roundId, courseCode);
        
        if (CollectionUtil.isEmpty(teachClassIds)
            || !teachClassIds.contains(teachClassId))
        {
            return null;
        }
        HashOperations<String, String, TeachingClassCache> hash =
            opsTeachClass();
        
        TeachingClassCache lesson =
            hash.get(Keys.getClassKey(), teachClassId.toString());
        return lesson;
    }
    
    /**
     * 获取教学班选课人数
     * 
     * @param teachClassId 教学班ID
     * @return
     * @see [类、类#方法、类#成员]
     */
    public Integer getElecNumber(Long teachClassId)
    {
        if (teachClassId == null)
        {
            return 0;
        }
        HashOperations<String, String, Integer> opsClassNum = opsClassNum();
        Integer num = opsClassNum.get(Keys.getClassElecNumberKey(),
            teachClassId.toString());
        return num;
    }
    
    /**
     * 增加教学班人数
     * 
     * @param teachClassId 教学班ID
     * @return
     * @see [类、类#方法、类#成员]
     */
    public int incrementElecNumber(Long teachClassId)
    {
        if (teachClassId == null)
        {
            return 0;
        }
        HashOperations<String, String, Integer> opsClassNum = opsClassNum();
        return opsClassNum
            .increment(Keys.getClassElecNumberKey(), teachClassId.toString(), 1)
            .intValue();
    }
}
