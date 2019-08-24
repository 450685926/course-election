package com.server.edu.election.dto;

import com.server.edu.dictionary.annotation.Code2Text;
import com.server.edu.dictionary.annotation.CodeI18n;

/**
 * 课程分级DTO
 * 
 * 
 * @author  OuYangGuoDong
 * @version  [版本号, 2019年8月24日]
 * @see  [相关类/方法]
 * @since  [产品/模块版本]
 */
@CodeI18n
public class CourseLevelDto
{
    private Long id;
    
    @Code2Text(transformer = "X_KCKM")
    private String name;
    
    /**
     * 分级（type为2有该字段，1为1级，2为2级...）
     */
    @Code2Text(transformer = "X_CJDJ")
    private String level;
    
    private Integer type;
    
    /**
     * 等级名称
     */
    private String levelName;
    
    public Long getId()
    {
        return id;
    }
    
    public void setId(Long id)
    {
        this.id = id;
    }
    
    public String getName()
    {
        return name;
    }
    
    public void setName(String name)
    {
        this.name = name;
    }
    
    public String getLevel()
    {
        return level;
    }
    
    public void setLevel(String level)
    {
        this.level = level;
    }
    
    public Integer getType()
    {
        return type;
    }
    
    public void setType(Integer type)
    {
        this.type = type;
    }
    
    public String getLevelName()
    {
        return levelName;
    }
    
    public void setLevelName(String levelName)
    {
        this.levelName = levelName;
    }
    
}
