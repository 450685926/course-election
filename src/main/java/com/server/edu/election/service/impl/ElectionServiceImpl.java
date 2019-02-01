package com.server.edu.election.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import com.server.edu.election.constants.ElectRuleType;
import com.server.edu.election.dao.ElectionRoundsDao;
import com.server.edu.election.dao.ElectionRuleDao;
import com.server.edu.election.dto.ElcCourseTakeDto;
import com.server.edu.election.dto.LessonDto;
import com.server.edu.election.entity.ElectionRounds;
import com.server.edu.election.entity.ElectionRule;
import com.server.edu.election.service.ElectionService;
import com.server.edu.election.service.rule.context.AbstractElectRuleExecutor;
import com.server.edu.election.service.rule.context.ElectMessage;
import com.server.edu.election.service.rule.context.ElectState;
import com.server.edu.election.service.rule.defaults.BKCoursePlanPrepare;
import com.server.edu.election.service.rule.defaults.CourseGradePrepare;
import com.server.edu.util.CollectionUtil;

public class ElectionServiceImpl implements ElectionService
{
    @Autowired
    private ElectionRoundsDao roundsDao;
    
    @Autowired
    private ElectionRuleDao ruleDao;
    
    @Autowired
    private ApplicationContext applicationContext;
    
    protected final Map<String, ElectionRounds> profiles = new HashMap<>();
    
    protected List<AbstractElectRuleExecutor> buildInPrepares =
        new ArrayList<>();
    
    /** 轮次-> 规则 */
    protected final Map<String, List<AbstractElectRuleExecutor>> rules =
        new HashMap<>();
    
    @Override
    public List<ElectionRounds> getProfiles(Long stdId)
    {
        List<ElectionRounds> selectByStdId = roundsDao.selectByStdId(stdId);
        return selectByStdId;
    }
    
    @Override
    public List<ElectMessage> generalCheck(ElectionRounds profile,
        ElectState context)
    {
        rebuildExecutors(profile);
        return null;
    }
    
    private String profileKey(Long profileId)
    {
        return ElectionRounds.class.getName() + profileId.toString();
    }
    
    /**
     * 根据轮次加载需要的处理器
     * 
     * @param profile
     */
    private synchronized void rebuildExecutors(ElectionRounds profile)
    {
        String profileKey = profileKey(profile.getId());
        ElectionRounds exists = profiles.get(profileKey);
        if (null != exists
            && !exists.getUpdatedAt().before(profile.getUpdatedAt()))
        {
            return;
        }
        profiles.put(profileKey, profile);
        
        List<AbstractElectRuleExecutor> electionExecutors = new ArrayList<>();
        List<ElectionRule> ruleList = ruleDao.selectByRoundId(profile.getId());
        for (ElectionRule config : ruleList)
        {
            AbstractElectRuleExecutor ruleExecutor =
                applicationContext.getBean(config.getServiceName(),
                    AbstractElectRuleExecutor.class);
            ruleExecutor.setType(ElectRuleType.valueOf(config.getType()));
            
            electionExecutors.add(ruleExecutor);
        }
        
        Collections.sort(electionExecutors);
        
        rules.put(profileKey, electionExecutors);
    }
    
    @Override
    public void prepare(ElectionRounds rounds, ElectState state,
        List<ElcCourseTakeDto> courseTake, Integer lessonType)
    {
        for (ElcCourseTakeDto take : courseTake)
        {
            if (take.getLessonType().equals(lessonType))
            {
                LessonDto lesson = new LessonDto();
                lesson.setCourseId(take.getCourseId());
                lesson.setTeachingClassId(take.getTeachingClassId());
                lesson.setLessonType(take.getLessonType());
                lesson.setCampus(take.getCampus());
                lesson.setElectedFlag(true);
                //添加到已选课表
                state.getElectedCourses().add(lesson);
            }
        }
        
        if (CollectionUtil.isEmpty(buildInPrepares))
        {
            buildInPrepares
                .add(applicationContext.getBean(CourseGradePrepare.class));
            buildInPrepares
                .add(applicationContext.getBean(BKCoursePlanPrepare.class));
        }
        for (AbstractElectRuleExecutor executor : buildInPrepares)
        {
            executor.prepare(state);
        }
        
        List<AbstractElectRuleExecutor> executors =
            rules.get(profileKey(rounds.getId()));
        for (AbstractElectRuleExecutor executor : executors)
        {
            executor.prepare(state);
        }
    }
    
}
