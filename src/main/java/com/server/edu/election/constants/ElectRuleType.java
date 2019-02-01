package com.server.edu.election.constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum ElectRuleType
{
    /**选课*/
    ELECTION,
    /**退课*/
    WITHDRAW,
    /**登录*/
    GENERAL,
    /** 换课，换同课程ID的其他任务 */
    EXCHANGE;
    
    public String getCnName()
    {
        return valueMap().get(this.toString());
    }
    
    public static Map<String, String> valueMap()
    {
        Map<String, String> result = new HashMap<String, String>();
        result.put(ELECTION.toString(), "选课");
        result.put(WITHDRAW.toString(), "退课");
        result.put(GENERAL.toString(), "登录");
        result.put(EXCHANGE.toString(), "换课");
        return result;
    }
    
    public static List<String> strValues()
    {
        ElectRuleType[] values = values();
        List<String> result = new ArrayList<>();
        for (ElectRuleType electRuleType : values)
        {
            result.add(electRuleType.toString());
        }
        return result;
    }
    
    public static Map<String, String> getElectTypes()
    {
        Map<String, String> result = valueMap();
        result.remove(GENERAL.toString());
        return result;
    }
}