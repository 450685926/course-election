package com.server.edu.election.studentelec.context;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.server.edu.dictionary.utils.SpringUtils;
import com.server.edu.election.studentelec.cache.StudentInfoCache;
import com.server.edu.election.studentelec.utils.Keys;

/**
 * 执行“学生选课请求”时的上下文环境，组装成本对象，供各种约束调用
 *
 */
public class ElecContext
{
    
    private StringRedisTemplate redisTemplate;
    
    private String studentId;
    
    /** 个人信息 */
    private StudentInfoCache studentInfo;
    
    /** 已完成课程 */
    private List<CompletedCourse> completedCourses;
    
    /** 已选择课程 */
    private List<SelectedCourse> selectedCourses;
    
    /** 免修申请课程 */
    private List<ElecCourse> applyForDropCourses;
    
    /** 个人计划内课程 */
    private List<ElecCourse> planCourses;
    
    private ElecRequest request;
    
    private ElecRespose respose;
    
    public ElecContext(String studentId)
    {
        this.studentId = studentId;
        this.redisTemplate = SpringUtils.getBean(StringRedisTemplate.class);
        String value = this.getByKey("stdInfo");
        if (StringUtils.isNoneBlank(value))
        {
            studentInfo = JSONObject.parseObject(value, StudentInfoCache.class);
        }
        String text = this.getByKey("CompletedCourses");
        if (StringUtils.isNoneBlank(text))
        {
            completedCourses =
                JSONObject.parseArray(text, CompletedCourse.class);
        }
        text = this.getByKey("SelectedCourses");
        if (StringUtils.isNoneBlank(text))
        {
            selectedCourses = JSONObject.parseArray(text, SelectedCourse.class);
        }
        text = this.getByKey("ApplyForDropCourses");
        if (StringUtils.isNoneBlank(text))
        {
            applyForDropCourses = JSONObject.parseArray(text, ElecCourse.class);
        }
        text = this.getByKey("PlanCourses");
        if (StringUtils.isNoneBlank(text))
        {
            planCourses = JSONObject.parseArray(text, ElecCourse.class);
        }
    }
    
    /**
     * 保存到redis中
     * 
     */
    public void save()
    {
        save("stdInfo", this.studentInfo);
        save("CompletedCourses", this.completedCourses);
        save("SelectedCourses", this.selectedCourses);
        save("ApplyForDropCourses", this.applyForDropCourses);
        save("PlanCourses", this.planCourses);
    }
    
    private String getByKey(String key)
    {
        ValueOperations<String, String> opsForValue =
            this.redisTemplate.opsForValue();
        String value = opsForValue.get(Keys.STD + key + "-" + studentId);
        return value;
    }
    
    private void save(String key, Object value)
    {
        ValueOperations<String, String> opsForValue =
            this.redisTemplate.opsForValue();
        opsForValue.set(Keys.STD + key + "-" + studentId,
            JSON.toJSONString(value));
    }
    
    public String getStudentId() {
        return this.studentId;
    }
    public StudentInfoCache getStudentInfo()
    {
        return studentInfo;
    }
    
    public List<CompletedCourse> getCompletedCourses()
    {
        return completedCourses;
    }
    
    public List<SelectedCourse> getSelectedCourses()
    {
        return selectedCourses;
    }
    
    public List<ElecCourse> getApplyForDropCourses()
    {
        return applyForDropCourses;
    }
    
    public List<ElecCourse> getPlanCourses()
    {
        return planCourses;
    }
    
    public ElecRequest getRequest()
    {
        return request;
    }
    
    public void setRequest(ElecRequest request)
    {
        this.request = request;
    }
    
    public ElecRespose getRespose()
    {
        return respose;
    }
    
    public void setRespose(ElecRespose respose)
    {
        this.respose = respose;
    }
    
}
