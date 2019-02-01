package com.server.edu.election.service.rule.context;

import com.server.edu.election.constants.ElectRuleType;
import com.server.edu.election.dto.ElcCourseTakeDto;

//FIXME 所有参数需要通过参数名匹配参数
public abstract class AbstractElectRuleExecutor
    implements RuleExecutor, Comparable<AbstractElectRuleExecutor>
{
    /**
     * 执行选课规则的顺序，默认为第三个执行
     */
    public enum Priority
    {
        FIRST, SECOND, THIRD, FOURTH, FIFTH
    }
    
    public int getOrder()
    {
        return Priority.THIRD.ordinal();
    }
    
    @Override
    public int compareTo(AbstractElectRuleExecutor abstractElectRuleExecutor)
    {
        return this.getOrder() - abstractElectRuleExecutor.getOrder();
    }
    
    private ElectRuleType type;
    
    @Override
    public void setType(ElectRuleType type)
    {
        this.type = type;
    }
    
    @Override
    public ElectRuleType getType()
    {
        return this.type;
    }
    
    @Override
    public void prepare(ElectState context)
    {
    }
    
    @Override
    public boolean execute(ElectState state, ElcCourseTakeDto courseTake)
    {
        return true;
    }
    
    //	protected Set<RuleConfigParam> getParams(Collection<? extends RuleConfig> configs){
    //		String serviceName = this.getClass().getSimpleName().toLowerCase();
    //		for (RuleConfig config : configs) {
    //			if(config.getRule().getServiceName().toLowerCase().equals(serviceName)){
    //				return config.getParams();
    //			}
    //		}
    //		return Collections.emptySet();
    //	}
    //	
    //	protected Iterator<RuleConfigParam> iteratorParams(Collection<? extends RuleConfig> configs){
    //		return getParams(configs).iterator();
    //	} 
    //	
    //	protected RuleConfigParam uniqueParam(Collection<? extends RuleConfig> configs){
    //		Iterator<RuleConfigParam> it = iteratorParams(configs);
    //		return it.hasNext() ? it.next() : null;
    //	} 
}