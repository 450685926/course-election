package com.server.edu.election.studentelec.service.cache;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.server.edu.election.dao.ElectionParameterDao;
import com.server.edu.election.dao.ElectionRuleDao;
import com.server.edu.election.entity.ElectionParameter;
import com.server.edu.election.studentelec.utils.Keys;
import com.server.edu.election.vo.ElectionRuleVo;
import com.server.edu.util.CollectionUtil;

/**
 * 缓存所有规则，规则是公共的
 * 
 * 
 * @author  OuYangGuoDong
 * @version  [版本号, 2019年5月21日]
 * @see  [相关类/方法]
 * @since  [产品/模块版本]
 */
@Service
public class RuleCacheService extends AbstractCacheService
{
    @Autowired
    private ElectionRuleDao ruleDao;
    
    @Autowired
    private ElectionParameterDao parameterDao;
    
    @Autowired
    private StringRedisTemplate redisTemplate;
    
    /**
     * 设置选课規則，把所有规则都放到缓存中本科生26个，研究生七八个
     */
    public void cacheAllRule()
    {
        HashOperations<String, Object, Object> ops = redisTemplate.opsForHash();
        String key = Keys.getRuleKey();
        
        List<ElectionRuleVo> selectAll = ruleDao.listAll();
        List<ElectionParameter> parameters = parameterDao.selectAll();
        for (ElectionRuleVo vo : selectAll)
        {
            List<ElectionParameter> list = parameters.stream()
                .filter(c -> vo.getId().equals(c.getRuleId()))
                .collect(Collectors.toList());
            vo.setList(list);
            ops.put(key, vo.getServiceName(), JSON.toJSONString(vo));
        }
    }
    
    /**
     * 获取选课规则
     * 
     * @param roundId 轮次
     * @param serviceName 服务名
     * @return
     */
    public ElectionRuleVo getRule(String serviceName)
    {
        HashOperations<String, String, String> ops = redisTemplate.opsForHash();
        
        String text = ops.get(Keys.getRuleKey(), serviceName);
        
        ElectionRuleVo vo = JSON.parseObject(text, ElectionRuleVo.class);
        return vo;
    }
    
    /**
     * 获取选择规则列表
     * 
     * @param roundId 轮次
     * @return
     */
    public List<ElectionRuleVo> getRules(Long roundId)
    {
        ValueOperations<String, String> opsForValue =
            redisTemplate.opsForValue();
        
        String key = Keys.getRoundRuleKey(roundId);
        List<String> list = JSON.parseArray(opsForValue.get(key), String.class);
        
        List<ElectionRuleVo> rules = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(list))
        {
            HashOperations<String, String, String> ops =
                redisTemplate.opsForHash();
            List<String> multiGet = ops.multiGet(Keys.getRuleKey(), list);
            for (String text : multiGet)
            {
                rules.add(JSON.parseObject(text, ElectionRuleVo.class));
            }
        }
        return rules;
    }
    
    /**
     * 轮次是否包含指定规则
     * 
     * @param roundId
     * @param serviceName
     * @param redisTemplate
     * @return
     * @see [类、类#方法、类#成员]
     */
    public boolean containsRule(Long roundId, String serviceName)
    {
        ValueOperations<String, String> opsForValue =
            redisTemplate.opsForValue();
        
        String key = Keys.getRoundRuleKey(roundId);
        List<String> list = JSON.parseArray(opsForValue.get(key), String.class);
        
        return CollectionUtil.isNotEmpty(list) && list.contains(serviceName);
    }
}
