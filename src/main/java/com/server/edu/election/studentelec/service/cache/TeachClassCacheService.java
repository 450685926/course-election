package com.server.edu.election.studentelec.service.cache;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.server.edu.election.dao.TeachingClassDao;
import com.server.edu.election.dto.TimeTableMessage;
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
import com.server.edu.common.vo.SchoolCalendarVo;
import com.server.edu.election.constants.Constants;
import com.server.edu.election.dao.ElcCourseTakeDao;
import com.server.edu.election.dao.ElecRoundCourseDao;
import com.server.edu.election.dto.ClassTeacherDto;
import com.server.edu.election.dto.CourseOpenDto;
import com.server.edu.election.rpc.BaseresServiceInvoker;
import com.server.edu.election.studentelec.cache.TeachingClassCache;
import com.server.edu.election.studentelec.context.ClassTimeUnit;
import com.server.edu.election.studentelec.context.ElecCourse;
import com.server.edu.election.studentelec.context.TimeAndRoom;
import com.server.edu.election.studentelec.preload.BKCourseGradeLoad;
import com.server.edu.election.studentelec.utils.Keys;
import com.server.edu.election.util.WeekUtil;
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
    private TeachingClassDao classDao;

    @Autowired
    private BKCourseGradeLoad gradeLoad;
    
    @Autowired
    private RoundCacheService roundCacheService;
    
    @Autowired
    private StringRedisTemplate strTemplate;
    
    public HashOperations<String, String, ElecCourse> opsElecCourse()
    {
        RedisTemplate<String, ElecCourse> redisTemplate =
            redisTemplate(ElecCourse.class);
        HashOperations<String, String, ElecCourse> ops =
            redisTemplate.opsForHash();
        return ops;
    } 
    
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
    
    public void cacheAllTeachClass(Long calendarId, String year)
    {
        PageInfo<CourseOpenDto> page = new PageInfo<>();
        page.setNextPage(1);
        page.setHasNextPage(true);
        while (page.isHasNextPage())
        {
            PageHelper.startPage(page.getNextPage(), 300);
            List<CourseOpenDto> lessons =
                roundCourseDao.selectTeachingClassByCalendarId(calendarId);
            this.cacheTeachClass(100, lessons, year);
            page = new PageInfo<>(lessons);
        }
    }

    public void cacheYjsAllTeachClass(Long calendarId, String year)
    {
        PageInfo<CourseOpenDto> page = new PageInfo<>();

        page.setNextPage(1);
        page.setHasNextPage(true);
        while (page.isHasNextPage())
        {
            PageHelper.startPage(page.getNextPage(), 300);
            List<CourseOpenDto> lessons =
                    roundCourseDao.selectYjsTeachingClassByCalendarId(calendarId);
            this.cacheYjsTeachClass(100, lessons, year);
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
    public void cacheTeachClass(long timeout, List<CourseOpenDto> teachClasss,
                                String year)
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
        Map<String, ElecCourse> publicCourseMap = new HashMap<>();
        Map<String, Integer> numMap = new HashMap<>();
        Map<String, Integer> drawMap = new HashMap<>();
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
            tc.setTeachClassName(lesson.getTeachingClassName());
            tc.setCampus(lesson.getCampus());
            tc.setTeachClassType(lesson.getTeachClassType());
            tc.setMaxNumber(lesson.getMaxNumber());
            tc.setCurrentNumber(lesson.getCurrentNumber());
            tc.setRemark(lesson.getTeachingClassRemark());
            tc.setManArrangeFlag(lesson.getManArrangeFlag());
            tc.setTimeTableList(getTimeById(teachingClassId));
            tc.setTeachingLanguage(lesson.getTeachingLanguage());
            tc.setPublicElec(
                    lesson.getIsElective() == Constants.ONE ? true : false);
            tc.setFaculty(lesson.getFaculty());
            tc.setCalendarId(lesson.getCalendarId());
            tc.setThirdWithdrawNumber(lesson.getThirdWithdrawNumber());
            List<ClassTimeUnit> times = gradeLoad.concatTime(collect, tc);
            tc.setTimes(times);
            //设置研究生学年跟开课学期，勿删
            tc.setTerm(lesson.getTerm());
            tc.setCalendarName(year);
            tc.setReserveNumber(lesson.getReserveNumber());

            numMap.put(teachingClassId.toString(), tc.getCurrentNumber());
            drawMap.put(teachingClassId.toString(), tc.getThirdWithdrawNumber());
            map.put(teachingClassId.toString(), tc);
            // 公共选修课
            if (tc.isPublicElec() && !publicCourseMap.containsKey(tc.getCourseCode()))
            {
                publicCourseMap.put(tc.getCourseCode(), tc);
            }
        }

        // 缓存选课人数
        opsClassNum().putAll(Keys.getClassElecNumberKey(), numMap);
        strTemplate
                .expire(Keys.getClassElecNumberKey(), timeout, TimeUnit.MINUTES);
        // 缓存第三、四轮退课人数
        opsClassNum().putAll(Keys.getWitdthDrawNumberKey(), drawMap);
        strTemplate
                .expire(Keys.getWitdthDrawNumberKey(), timeout, TimeUnit.MINUTES);
        // 缓存教学班信息
        String key = Keys.getClassKey();
        opsTeachClass().putAll(key, map);
        strTemplate.expire(key, timeout, TimeUnit.MINUTES);

        // 缓存公共选修课信息
        String publicCourseKey = Keys.getPublicCourseKey();
        opsElecCourse().putAll(publicCourseKey, publicCourseMap);
        strTemplate.expire(publicCourseKey, timeout, TimeUnit.MINUTES);
    }

    public void cacheYjsTeachClass(long timeout, List<CourseOpenDto> teachClasss,
                                   String year)
    {
        if (CollectionUtil.isEmpty(teachClasss))
        {
            return;
        }
        List<Long> classIds = teachClasss.stream()
                .map(temp -> temp.getTeachingClassId())
                .collect(Collectors.toList());
        Map<Long, List<TimeTableMessage>> classTimeMap = new HashMap<>(classIds.size());
        if(CollectionUtil.isEmpty(classIds)) {
            List<TimeTableMessage> list = classDao.getYjsClassTimes(classIds);
            classTimeMap = list.stream()
                    .collect(Collectors.groupingBy(TimeTableMessage::getTeachingClassId));
        }
        Map<String, TeachingClassCache> map = new HashMap<>();
        Map<String, ElecCourse> publicCourseMap = new HashMap<>();
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
            tc.setTeachClassName(lesson.getTeachingClassName());
            tc.setCampus(lesson.getCampus());
            tc.setTeachClassType(lesson.getTeachClassType());
            tc.setMaxNumber(lesson.getMaxNumber());
            tc.setCurrentNumber(lesson.getCurrentNumber());
            tc.setRemark(lesson.getTeachingClassRemark());
            tc.setManArrangeFlag(lesson.getManArrangeFlag());
            List<TimeTableMessage> timeTableMessages = classTimeMap.get(teachingClassId);
            tc.setTimeTableList(getYjsTimeById(timeTableMessages));
            tc.setTeachingLanguage(lesson.getTeachingLanguage());
            tc.setPublicElec(
                    lesson.getIsElective() == Constants.ONE ? true : false);
            tc.setFaculty(lesson.getFaculty());
            tc.setCalendarId(lesson.getCalendarId());
            List<ClassTimeUnit> times = gradeLoad.concatYjsTime(timeTableMessages, tc);
            tc.setTimes(times);
            //设置研究生学年跟开课学期，勿删
            tc.setTerm(lesson.getTerm());
            tc.setCalendarName(year);
            tc.setReserveNumber(lesson.getReserveNumber());

            numMap.put(teachingClassId.toString(), tc.getCurrentNumber());
            map.put(teachingClassId.toString(), tc);
            // 公共选修课
            if (tc.isPublicElec() && !publicCourseMap.containsKey(tc.getCourseCode()))
            {
                publicCourseMap.put(tc.getCourseCode(), tc);
            }
        }

        // 缓存选课人数
        opsClassNum().putAll(Keys.getClassElecNumberKey(), numMap);
        strTemplate
                .expire(Keys.getClassElecNumberKey(), timeout, TimeUnit.MINUTES);
        strTemplate
                .expire(Keys.getWitdthDrawNumberKey(), timeout, TimeUnit.MINUTES);
        // 缓存教学班信息
        String key = Keys.getClassKey();
        opsTeachClass().putAll(key, map);
        strTemplate.expire(key, timeout, TimeUnit.MINUTES);

        // 缓存公共选修课信息
        String publicCourseKey = Keys.getPublicCourseKey();
        opsElecCourse().putAll(publicCourseKey, publicCourseMap);
        strTemplate.expire(publicCourseKey, timeout, TimeUnit.MINUTES);
    }
    
    private List<TimeAndRoom> getTimeById(Long teachingClassId)
    {
        List<TimeAndRoom> list = new ArrayList<>();
        List<ClassTeacherDto> classTimeAndRoom =
            courseTakeDao.findClassTimeAndRoomStr(teachingClassId);
        if (CollectionUtil.isNotEmpty(classTimeAndRoom))
        {
            for (ClassTeacherDto classTeacherDto : classTimeAndRoom)
            {
                TimeAndRoom time = new TimeAndRoom();
                Integer dayOfWeek = classTeacherDto.getDayOfWeek();
                Integer timeStart = classTeacherDto.getTimeStart();
                Integer timeEnd = classTeacherDto.getTimeEnd();
                String roomID = classTeacherDto.getRoomID();
                String weekNumber = classTeacherDto.getWeekNumberStr();
                Long timeId = classTeacherDto.getTimeId();
                String[] str = weekNumber.split(",");
                
                List<Integer> weeks = Arrays.asList(str)
                    .stream()
                    .map(Integer::parseInt)
                    .collect(Collectors.toList());
                String oneCycle = "[1, 3, 5, 7, 9, 11, 13, 15, 17]";
                String biweekly = "[2, 4, 6, 8, 10, 12, 14, 16]";
                String allweekly = "[1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17]";
                List<String> weekNums =
                    CalUtil.getWeekNums(weeks.toArray(new Integer[] {}));
                String weekNumStr = weekNums.toString();//周次
                if (StringUtils.equalsIgnoreCase(oneCycle, weekNumStr)) {
                	weekNumStr = "单周";
				}else if (StringUtils.equalsIgnoreCase(biweekly, weekNumStr)) {
					weekNumStr = "双周";
				}else if (StringUtils.equalsIgnoreCase(allweekly, weekNumStr)) {
					weekNumStr = "单双周[1-17]";
				}
                String weekstr = WeekUtil.findWeek(dayOfWeek);//星期
                String timeStr = weekstr + " " + timeStart + "-" + timeEnd + " "
                    + weekNumStr + " ";
                time.setTimeId(timeId);
                time.setTimeAndRoom(timeStr);
                time.setRoomId(roomID);
                list.add(time);
            }
        }
        return list;
    }

    private List<TimeAndRoom> getYjsTimeById(List<TimeTableMessage> timeTableMessages)
    {
        List<TimeAndRoom> list = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(timeTableMessages))
        {
            for (TimeTableMessage timeTableMessage : timeTableMessages)
            {
                TimeAndRoom time = new TimeAndRoom();
                Integer dayOfWeek = timeTableMessage.getDayOfWeek();
                Integer timeStart = timeTableMessage.getTimeStart();
                Integer timeEnd = timeTableMessage.getTimeEnd();
                String roomID = timeTableMessage.getRoomId();
                String weekNumber = timeTableMessage.getWeekNum();
                Long timeId = timeTableMessage.getTimeId();
                String[] str = weekNumber.split(",");

                List<Integer> weeks = Arrays.asList(str)
                        .stream()
                        .map(Integer::parseInt)
                        .collect(Collectors.toList());
                timeTableMessage.setWeeks(weeks);
                String oneCycle = "[1, 3, 5, 7, 9, 11, 13, 15, 17]";
                String biweekly = "[2, 4, 6, 8, 10, 12, 14, 16]";
                String allweekly = "[1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17]";
                List<String> weekNums =
                        CalUtil.getWeekNums(weeks.toArray(new Integer[] {}));
                String weekNumStr = weekNums.toString();//周次
                if (StringUtils.equalsIgnoreCase(oneCycle, weekNumStr)) {
                    weekNumStr = "单周";
                }else if (StringUtils.equalsIgnoreCase(biweekly, weekNumStr)) {
                    weekNumStr = "双周";
                }else if (StringUtils.equalsIgnoreCase(allweekly, weekNumStr)) {
                    weekNumStr = "单双周[1-17]";
                }
                String weekstr = WeekUtil.findWeek(dayOfWeek);//星期
                String timeStr = weekstr + " " + timeStart + "-" + timeEnd + " "
                        + weekNumStr + " ";
                time.setTimeId(timeId);
                time.setTimeAndRoom(timeStr);
                time.setRoomId(roomID);
                list.add(time);
            }
        }
        return list;
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
            lessons = lessons.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        }
        return lessons;
    }
    
    /**
     * 
     * 通过学年学期与课程代码获取教学班信息
     * @param calendarId
     * @param courseCode
     * @return
     */
    public List<TeachingClassCache> getTeachClasssBycalendarId(Long calendarId,
        String courseCode)
    {
        List<TeachingClassCache> lessons = new ArrayList<>();
        
        List<Long> teachClassIds = this.roundCacheService
            .getTeachClassIdsByCalendarId(calendarId, courseCode);
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
            lessons = lessons.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        }
        return lessons;
    }
    
    /**
     * 获取指定教学班信息
     * 
     * @param roundId 轮次ID
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
     * 获取指定教学班信息
     * 
     * @param calendarId 校历
     * @param teachClassId 教学班ID
     * @return
     */
    public TeachingClassCache getTeachClassByCalendarId(Long calendarId,
        String courseCode, Long teachClassId)
    {
        if (calendarId == null || StringUtils.isBlank(courseCode)
            || teachClassId == null)
        {
            logger.warn(
                "---- calendarId, courseCode and teachClassId can not be null ----");
            return null;
        }
        
        List<Long> teachClassIds = this.roundCacheService
            .getTeachClassIdsByCalendarId(calendarId, courseCode);
        
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
     * 获取缓存中指定教学班
     * @param calendarId
     * @param courseCode
     * @param teachClassId
     * @return
     */
    public TeachingClassCache getTeachClassByTeachClassId(Long teachClassId)
    {
        HashOperations<String, String, TeachingClassCache> hash =
            opsTeachClass();
        
        TeachingClassCache lesson =
            hash.get(Keys.getClassKey(), teachClassId.toString());
        return lesson;
    }
    
    /**
     * 保存教学班缓存
     * 
     * @param teachClassId
     * @param cache
     * @see [类、类#方法、类#成员]
     */
    public void saveTeachClassCache(Long teachClassId, TeachingClassCache cache)
    {
        HashOperations<String, String, TeachingClassCache> hash =
            opsTeachClass();
        hash.put(Keys.getClassKey(), teachClassId.toString(), cache);
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
     * 获取第三、四轮教学班退课人数
     * 
     * @param teachClassId 教学班ID
     * @return
     * @see [类、类#方法、类#成员]
     */
    public Integer getWitdthDrawNumber(Long teachClassId)
    {
        if (teachClassId == null)
        {
            return 0;
        }
        HashOperations<String, String, Integer> opsClassNum = opsClassNum();
        Integer num = opsClassNum.get(Keys.getWitdthDrawNumberKey(),
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
    
    /**
     * 增加第三、四轮教学班退课人数
     * 
     * @param teachClassId 教学班ID
     * @return
     * @see [类、类#方法、类#成员]
     */
    public int incrementDrawNumber(Long teachClassId)
    {
        if (teachClassId == null)
        {
            return 0;
        }
        HashOperations<String, String, Integer> opsClassNum = opsClassNum();
        return opsClassNum
            .increment(Keys.getWitdthDrawNumberKey(), teachClassId.toString(), 1)
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
            .increment(Keys.getClassElecNumberKey(),
                teachClassId.toString(),
                -1)
            .intValue();
    }
    
    public int decrWitdhDrawNumber(Long teachClassId)
    {
        if (teachClassId == null)
        {
            return 0;
        }
        HashOperations<String, String, Integer> opsClassNum = opsClassNum();
        return opsClassNum
            .increment(Keys.getWitdthDrawNumberKey(),
                teachClassId.toString(),
                -1)
            .intValue();
    }
    
    /**
     * 清空第三、四轮教学班退课人数
     * @param teachClassId
     */
    public void clearWitdhDrawNumber(Map<String, Integer> drawMap)
    {
        if (drawMap != null)
        {
            HashOperations<String, String, Integer> opsClassNum = opsClassNum();
            opsClassNum.putAll(Keys.getWitdthDrawNumberKey(),drawMap);
        }
    }
    
    /**
     * 获取当前学期的公共选修课
     * @param calendarId
     * @return
     */
    public Set<ElecCourse> getPublicCourses(Long calendarId) {
    	Set<ElecCourse> publicCourses = new HashSet<>();
    	List<ElecCourse> list = opsElecCourse().values(Keys.getPublicCourseKey());
    	if(CollectionUtil.isNotEmpty(list)) {
        	publicCourses = list.stream().filter(c->c.getCalendarId().equals(calendarId)).collect(Collectors.toSet());
    	}
    	return publicCourses;
	}
    
    /**
     * 更新教学班人数
     * @param teachClassId
     * @return
     */
    public void updateTeachingClassNumber(Long teachClassId) {
        TeachingClassCache teachingClassCache = getTeachClassByTeachClassId(teachClassId);
        if (teachingClassCache != null) {
        	// 实时获取选课人数
        	Integer elecNumber = getElecNumber(teachClassId);
        	if(elecNumber!=null) {
        		teachingClassCache.setCurrentNumber(elecNumber);
        	}
        	Integer thirdWithdrawNumber = getWitdthDrawNumber(teachClassId);
        	if(thirdWithdrawNumber!=null) {
            	teachingClassCache.setThirdWithdrawNumber(thirdWithdrawNumber);
        	}
        	saveTeachClassCache(teachClassId, teachingClassCache);
		}
    }
    
    /**
     * 释放第三、四轮退课人数
     * @param teachClassId
     * @return
     */
    public void updateDrawNumber(Long teachClassId) {
        TeachingClassCache teachingClassCache = getTeachClassByTeachClassId(teachClassId);
        if (teachingClassCache != null) {
        	teachingClassCache.setThirdWithdrawNumber(Constants.ZERO);
        	saveTeachClassCache(teachClassId, teachingClassCache);
		}
    }

}
