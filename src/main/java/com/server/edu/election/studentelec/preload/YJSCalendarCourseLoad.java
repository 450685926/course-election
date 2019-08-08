package com.server.edu.election.studentelec.preload;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.server.edu.election.constants.Constants;
import com.server.edu.election.dao.ElecRoundCourseDao;
import com.server.edu.election.dto.CourseOpenDto;
import com.server.edu.election.studentelec.context.ElecContext;
import com.server.edu.election.studentelec.utils.Keys;

/**
 * 本科生培养计划课程查询
 * 
 */
@Component
public class YJSCalendarCourseLoad extends DataProLoad<ElecContext>
{
    Logger log = LoggerFactory.getLogger(getClass());
    

    @Autowired
    private ElecRoundCourseDao roundCourseDao;
    
    @Autowired
    private StringRedisTemplate strTemplate;
    
    @Override
    public int getOrder()
    {
        return 3;
    }
    
    @Override
    public String getProjectIds()
    {
        return "2,4";
    }

    @Override
    public void load(ElecContext context)
    {
    	if (context.getRequest().getChooseObj() == null || context.getRequest().getChooseObj() != Constants.THREE) {
			return;
		}
    	
        // 加载学年学期所有教学班与课程数据到缓存中
    	Long calendarId = context.getRequest().getCalendarId();
    	List<CourseOpenDto> lessons = roundCourseDao.selectCorseGraduteByCalendarId(calendarId);

		Map<String, Set<Long>> courseClassMap = new HashMap<>();
        for (CourseOpenDto teachClasss : lessons)
        {
            String courseCode = teachClasss.getCourseCode();
            Long teachingClassId = teachClasss.getTeachingClassId();
            if (courseClassMap.containsKey(courseCode))
            {
                courseClassMap.get(courseCode).add(teachingClassId);
            }
            else
            {
                Set<Long> ids = new HashSet<>();
                ids.add(teachingClassId);
                courseClassMap.put(courseCode, ids);
            }
        }
        HashOperations<String, String, String> ops = strTemplate.opsForHash();
        String key = Keys.getCalendarCourseKey(calendarId);
        Set<String> existKeys = ops.keys(key);
        for (Entry<String, Set<Long>> entry : courseClassMap.entrySet())
        {
            String courseCode = entry.getKey();
            Set<Long> teachClassIds = entry.getValue();
            
            ops.put(key, courseCode, JSON.toJSONString(teachClassIds));
            
            // 移除存在的
            existKeys.remove(courseCode);
        }
        if (null != existKeys && !existKeys.isEmpty())
        {
            // 删除掉没有关联的课程
            ops.delete(key, existKeys.toArray());
        }
    }
}
