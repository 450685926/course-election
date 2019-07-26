package com.server.edu.election.studentelec.service.cache;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
import com.server.edu.election.constants.Constants;
import com.server.edu.election.dao.ElcCourseTakeDao;
import com.server.edu.election.dao.ElecRoundCourseDao;
import com.server.edu.election.dto.ClassTeacherDto;
import com.server.edu.election.dto.CourseOpenDto;
import com.server.edu.election.studentelec.cache.TeachingClassCache;
import com.server.edu.election.studentelec.context.ClassTimeUnit;
import com.server.edu.election.studentelec.context.TimeAndRoom;
import com.server.edu.election.studentelec.preload.CourseGradeLoad;
import com.server.edu.election.studentelec.utils.Keys;
import com.server.edu.util.CalUtil;
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
    private ElcCourseTakeDao courseTakeDao;
    
    @Autowired
    private CourseGradeLoad gradeLoad;
    
    @Autowired
    private RoundCacheService roundCacheService;
    
    @Autowired
    private StringRedisTemplate strTemplate;
    
    public HashOperations<String, String, TeachingClassCache> opsTeachClass()
    {
        RedisTemplate<String, TeachingClassCache> redisTemplate =
            redisTemplate(TeachingClassCache.class);
        HashOperations<String, String, TeachingClassCache> ops =
            redisTemplate.opsForHash();
        return ops;
    }
    
    public HashOperations<String, String, Integer> opsClassNum()
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
            List<CourseOpenDto> lessons = roundCourseDao.selectTeachingClassByCalendarId(calendarId);
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
        Map<String, TeachingClassCache> map = new HashMap<>();
        Map<String, Integer> numMap = new HashMap<>();
        for (CourseOpenDto lesson : teachClasss)
        {
            Long teachingClassId = lesson.getTeachingClassId();
            TeachingClassCache tc = new TeachingClassCache();
            tc.setFaculty(lesson.getFaculty());
            tc.setNature(lesson.getNature());
            tc.setCourseCode(lesson.getCourseCode());
            tc.setCourseName(lesson.getCourseName());
            tc.setCredits(lesson.getCredits());
            tc.setNameEn(lesson.getCourseNameEn());
            tc.setTeachClassId(teachingClassId);
            tc.setTeachClassCode(lesson.getTeachingClassCode());
            tc.setCampus(lesson.getCampus());
            tc.setTeachClassType(lesson.getTeachClassType());
            tc.setMaxNumber(lesson.getMaxNumber());
            tc.setCurrentNumber(lesson.getCurrentNumber());
            tc.setRemark(lesson.getRemark());
            tc.setTimeTableList(getTimeById(teachingClassId));
            tc.setPublicElec(
                lesson.getIsElective() == Constants.ONE ? true : false);
            tc.setFaculty(lesson.getFaculty());
            List<ClassTimeUnit> times =
                gradeLoad.concatTime(collect, tc);
            tc.setTimes(times);
            numMap.put(teachingClassId.toString(),
                tc.getCurrentNumber());
            map.put(teachingClassId.toString(), tc);
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
    private List<TimeAndRoom>  getTimeById(Long teachingClassId){
        List<TimeAndRoom> list=new ArrayList<>();
        List<ClassTeacherDto> classTimeAndRoom = courseTakeDao.findClassTimeAndRoomStr(teachingClassId);
        if(CollectionUtil.isNotEmpty(classTimeAndRoom)){
            for (ClassTeacherDto classTeacherDto : classTimeAndRoom) {
            	TimeAndRoom time=new TimeAndRoom();
                Integer dayOfWeek = classTeacherDto.getDayOfWeek();
                Integer timeStart = classTeacherDto.getTimeStart();
                Integer timeEnd = classTeacherDto.getTimeEnd();
                String roomID = classTeacherDto.getRoomID();
                String weekNumber = classTeacherDto.getWeekNumberStr();
                Long timeId = classTeacherDto.getTimeId();
                String[] str = weekNumber.split(",");
                
                List<Integer> weeks = Arrays.asList(str).stream().map(Integer::parseInt).collect(Collectors.toList());
                List<String> weekNums = CalUtil.getWeekNums(weeks.toArray(new Integer[] {}));
                String weekNumStr = weekNums.toString();//周次
                String weekstr = findWeek(dayOfWeek);//星期
                String timeStr=weekstr+" "+timeStart+"-"+timeEnd+" "+weekNumStr+" ";
                time.setTimeId(timeId);
                time.setTimeAndRoom(timeStr);
                time.setRoomId(roomID);
                list.add(time);
            }
        }
        return list;
    }
    /**
     *@Description: 星期
     *@Param:
     *@return:
     *@Author: bear
     *@date: 2019/2/15 13:59
     */
  	private String findWeek(Integer number){
         String week="";
         switch(number){
             case 1:
                 week="星期一";
                 break;
             case 2:
                 week="星期二";
                 break;
             case 3:
                 week="星期三";
                 break;
             case 4:
                 week="星期四";
                 break;
             case 5:
                 week="星期五";
                 break;
             case 6:
                 week="星期六";
                 break;
             case 7:
                 week="星期日";
                 break;
         }
         return week;
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
            // 过滤null
            lessons = lessons.stream().filter(Objects::nonNull).collect(Collectors.toList());
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
    
    public int decrElecNumber(Long teachClassId)
    {
        if (teachClassId == null)
        {
            return 0;
        }
        HashOperations<String, String, Integer> opsClassNum = opsClassNum();
        return opsClassNum
            .increment(Keys.getClassElecNumberKey(), teachClassId.toString(), -1)
            .intValue();
    }
}
