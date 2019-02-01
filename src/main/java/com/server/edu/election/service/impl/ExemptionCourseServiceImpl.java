package com.server.edu.election.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.server.edu.common.PageCondition;
import com.server.edu.common.rest.PageResult;
import com.server.edu.common.vo.SchoolCalendarVo;
import com.server.edu.election.dao.ExemptionCourseDao;
import com.server.edu.election.dao.ExemptionCourseMaterialDao;
import com.server.edu.election.dao.ExemptionCourseRuleDao;
import com.server.edu.election.dao.ExemptionCourseScoreDao;
import com.server.edu.election.dto.ExemptionCourseScoreDto;
import com.server.edu.election.entity.ExemptionCourse;
import com.server.edu.election.entity.ExemptionCourseRule;
import com.server.edu.election.rpc.BaseresServiceInvoker;
import com.server.edu.election.service.ExemptionCourseService;
import com.server.edu.election.vo.ExemptionCourseRuleVo;
import com.server.edu.election.vo.ExemptionCourseScoreVo;
import com.server.edu.election.vo.ExemptionCourseVo;
import com.server.edu.util.CollectionUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @description: 免修免考课程实现类
 * @author: bear
 * @create: 2019-01-31 09:56
 */

@Service
@Primary
public class ExemptionCourseServiceImpl implements ExemptionCourseService{

    @Autowired
    private ExemptionCourseDao exemptionCourseDao;

    @Autowired
    private ExemptionCourseScoreDao scoreDao;

    @Autowired
    private ExemptionCourseRuleDao ruleDao;

    @Autowired
    private ExemptionCourseMaterialDao materialDao;

    /**
    *@Description: 分页查询免修免考课程
    *@Param:
    *@return:
    *@Author: bear
    *@date: 2019/1/31 10:05
    */
    @Override
    public PageResult<ExemptionCourseVo> findExemptionCourse(PageCondition<ExemptionCourse> condition) {
        PageHelper.startPage(condition.getPageNum_(), condition.getPageSize_());
        Page<ExemptionCourseVo> exemptionCourse = exemptionCourseDao.findExemptionCourse(condition.getCondition());
        if(exemptionCourse!=null){
            List<ExemptionCourseVo> result = exemptionCourse.getResult();
            List<SchoolCalendarVo> schoolCalendarList = BaseresServiceInvoker.getSchoolCalendarList();
            Map<Long, String> schoolCalendarMap = new HashMap<>();
            for(SchoolCalendarVo schoolCalendarVo : schoolCalendarList) {
                schoolCalendarMap.put(schoolCalendarVo.getId(), schoolCalendarVo.getFullName());
            }
            if(schoolCalendarMap.size()!=0){
                for (ExemptionCourseVo exemptionCourseVo : result) {
                    String schoolCalendarName = schoolCalendarMap.get(exemptionCourseVo.getCalendarId());
                    if(StringUtils.isNotEmpty(schoolCalendarName)) {
                        exemptionCourseVo.setCalendarName(schoolCalendarName);
                    }
                }
            }
        }
        return new PageResult<>(exemptionCourse);
    }


    /**
    *@Description: 删除免修免考课程
    *@Param:
    *@return:
    *@Author: bear
    *@date: 2019/1/31 10:20
    */

    @Override
    @Transactional
    public String deleteExemptionCourse(List<Long> ids) {
        if(CollectionUtil.isEmpty(ids)){
             return "common.parameterError";
        }
        exemptionCourseDao.deleteExemptionCourseByIds(ids);
        return  "common.deleteSuccess";

    }

    /**
    *@Description: 新增免修免考课程
    *@Param: 
    *@return: 
    *@Author: bear
    *@date: 2019/1/31 12:51
    */
    @Override
    @Transactional
    public String addExemptionCourse(ExemptionCourse exemptionCourse) {
        ExemptionCourse ex=new ExemptionCourse();
        ex.setCalendarId(exemptionCourse.getCalendarId());
        ex.setCourseCode(exemptionCourse.getCourseCode());
        Page<ExemptionCourseVo> exCourse = exemptionCourseDao.findExemptionCourse(ex);
        if(exCourse!=null && exCourse.getResult().size()>0){
            return "common.exist";
        }
        exemptionCourse.setCreatedAt(new Date());
        exemptionCourseDao.insertSelective(exemptionCourse);
        return "common.addsuccess";
    }

    /**
    *@Description: 修改课程
    *@Param:
    *@return:
    *@Author: bear
    *@date: 2019/1/31 16:17
    */
    @Override
    @Transactional
    public String updateExemptionCourse(ExemptionCourse exemptionCourse) {
        ExemptionCourse ex=new ExemptionCourse();
        Page<ExemptionCourseVo> exCourse = exemptionCourseDao.findExemptionCourse(ex);
        if(exCourse!=null&&exCourse.getResult().size()>0){
            List<ExemptionCourseVo> result = exCourse.getResult();
            List<ExemptionCourseVo> collect = result.stream().filter((ExemptionCourseVo vo) -> vo.getId().longValue() != exemptionCourse.getId().longValue()).collect(Collectors.toList());
            if(CollectionUtil.isNotEmpty(collect)){
                for (ExemptionCourseVo exemptionCourseVo : collect) {
                    if(exemptionCourseVo.getCourseCode().equals(exemptionCourse.getCourseCode())
                            &&exemptionCourseVo.getCalendarId().longValue()==exemptionCourse.getCalendarId().longValue()){
                        return "common.exist";
                    }
                }
            }
        }


        exemptionCourse.setCreatedAt(new Date());
        exemptionCourseDao.updateByPrimaryKeySelective(exemptionCourse);
        return "common.editSuccess";
    }

    /**
    *@Description: 查询免修免考入学成绩
    *@Param: 
    *@return:
    *@Author: bear
    *@date: 2019/1/31 18:45
    */
    @Override
    public PageResult<ExemptionCourseScoreVo> findExemptionScore(PageCondition<ExemptionCourseScoreDto>  courseScoreDto) {
        PageHelper.startPage(courseScoreDto.getPageNum_(), courseScoreDto.getPageSize_());
        Page<ExemptionCourseScoreVo> exemptionScore = scoreDao.findExemptionScore(courseScoreDto.getCondition());
        return new PageResult<>(exemptionScore);
    }


    /**
    *@Description: 查询免修免考规则
    *@Param:
    *@return:
    *@Author: bear
    *@date: 2019/2/1 16:22
    */
    @Override
    public PageResult<ExemptionCourseRuleVo> findExemptionRule(PageCondition<ExemptionCourseRule> rulePageCondition) {
        PageHelper.startPage(rulePageCondition.getPageNum_(), rulePageCondition.getPageSize_());
        Page<ExemptionCourseRuleVo> exemptionCourseRule = ruleDao.findExemptionCourseRule(rulePageCondition.getCondition());
        if(exemptionCourseRule!=null){
            List<SchoolCalendarVo> schoolCalendarList = BaseresServiceInvoker.getSchoolCalendarList();
            Map<Long, String> schoolCalendarMap = new HashMap<>();
            for(SchoolCalendarVo schoolCalendarVo : schoolCalendarList) {
                schoolCalendarMap.put(schoolCalendarVo.getId(), schoolCalendarVo.getFullName());
            }
            List<ExemptionCourseRuleVo> result = exemptionCourseRule.getResult();
            if(CollectionUtil.isNotEmpty(result)){
                for (ExemptionCourseRuleVo exemptionCourseRuleVo : result) {
                    String calendarName=schoolCalendarMap.get(exemptionCourseRuleVo.getCalendarId());
                    if(StringUtils.isNotEmpty(calendarName)) {
                        exemptionCourseRuleVo.setCalendarName(calendarName);
                    }
                }
            }
        }
        return new PageResult<>(exemptionCourseRule);
    }

    /**
    *@Description: 删除申请规则
    *@Param:
    *@return:
    *@Author: bear
    *@date: 2019/2/1 17:47
    */
    @Override
    public String deleteExemptionCourseRule(List<Long> ids, Integer applyType) {
        if(CollectionUtil.isEmpty(ids) || applyType==null){
            return "common.parameterError";
        }
        ruleDao.deleteExemptionCourseRule(ids);
        if(applyType==1){//材料申请
            materialDao.deleteExemptionCourseMaterial(ids);
        }
        return "common.deleteSuccess";
    }

}
