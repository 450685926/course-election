package com.server.edu.mutual.testvo;

import com.server.edu.common.entity.CourseLabelRelation;
import com.server.edu.dictionary.annotation.Code2Text;
import com.server.edu.dictionary.annotation.CodeI18n;

import java.util.Set;

@CodeI18n
public class CourseLabelRelationVo extends CourseLabelRelation
{
    // 课程Code
    private String code;

    private String coursesName;

    private String coursesNameEn;
    
    private Integer isElective;
    
    private String label;
    
    private String labelEn;
    
    private Integer level;
    
    private String parentLabel;
    
    private String parentLabelEn;
    
    private Long parentLabelId;
    
    private Integer parentLevel;
    
    private Integer year;
    
    private Long templateId; //以前的模板Id
    
    private Integer parentIsElective;

    //==========分组，分模块=========
    private Integer chooseNum;

    private Integer totalNum;

    private Integer parentChooseNum;

    private Integer parentTotalNum;

    private Long minNum;

    private Long maxNum;

    private Double minCredits;

    private Integer expression;

    private String groupType;

    public Long getMinNum() {
        return minNum;
    }

    public void setMinNum(Long minNum) {
        this.minNum = minNum;
    }

    public Long getMaxNum() {
        return maxNum;
    }

    public void setMaxNum(Long maxNum) {
        this.maxNum = maxNum;
    }

    public Double getMinCredits() {
        return minCredits;
    }

    public void setMinCredits(Double minCredits) {
        this.minCredits = minCredits;
    }

    public Integer getExpression() {
        return expression;
    }

    public void setExpression(Integer expression) {
        this.expression = expression;
    }

    public String getGroupType() {
        return groupType;
    }

    public void setGroupType(String groupType) {
        this.groupType = groupType;
    }

    public Integer getChooseNum() {
        return chooseNum;
    }

    public void setChooseNum(Integer chooseNum) {
        this.chooseNum = chooseNum;
    }

    public Integer getTotalNum() {
        return totalNum;
    }

    public void setTotalNum(Integer totalNum) {
        this.totalNum = totalNum;
    }

    public Integer getParentChooseNum() {
        return parentChooseNum;
    }

    public void setParentChooseNum(Integer parentChooseNum) {
        this.parentChooseNum = parentChooseNum;
    }

    public Integer getParentTotalNum() {
        return parentTotalNum;
    }

    public void setParentTotalNum(Integer parentTotalNum) {
        this.parentTotalNum = parentTotalNum;
    }

    //========================

    //===========================
    /**
     * @Description: ids(批量保存模板(方案)课程用)
     * @author kan yuanfeng
     * @date 2018/11/16 15:01
     */
    private Set<String> courseCodes;
    
    // 开课学期
    @Code2Text(transformer="X_KKXQ")
    private String term;
    
    // 开课学院
    @Code2Text(transformer="X_YX")
    private String college;
    
    // 周课时
    private Double weekHour;
    
    // 课时
    private Double period;
    
    public String getTerm()
    {
        return term;
    }

    public void setTerm(String term)
    {
        this.term = term;
    }

    public String getCollege()
    {
        return college;
    }

    public void setCollege(String college)
    {
        this.college = college;
    }

    public Double getWeekHour()
    {
        return weekHour;
    }

    public void setWeekHour(Double weekHour)
    {
        this.weekHour = weekHour;
    }

    public Double getPeriod()
    {
        return period;
    }

    public void setPeriod(Double period)
    {
        this.period = period;
    }
    
    //==============================

    public Set<String> getCourseCodes() {
		return courseCodes;
	}

	public void setCourseCodes(Set<String> courseCodes) {
		this.courseCodes = courseCodes;
	}

	public Integer getLevel()
    {
        return level;
    }

    public void setLevel(Integer level)
    {
        this.level = level;
    }

    public String getParentLabel()
    {
        return parentLabel;
    }

    public void setParentLabel(String parentLabel)
    {
        this.parentLabel = parentLabel;
    }

    public String getParentLabelEn()
    {
        return parentLabelEn;
    }

    public void setParentLabelEn(String parentLabelEn)
    {
        this.parentLabelEn = parentLabelEn;
    }

    public Integer getParentIsElective()
    {
        return parentIsElective;
    }

    public void setParentIsElective(Integer parentIsElective)
    {
        this.parentIsElective = parentIsElective;
    }

    public Integer getParentLevel()
    {
        return parentLevel;
    }

    public void setParentLevel(Integer parentLevel)
    {
        this.parentLevel = parentLevel;
    }
    
    public Integer getYear()
    {
        return year;
    }

    public void setYear(Integer year)
    {
        this.year = year;
    }

    
    public Long getTemplateId()
    {
        return templateId;
    }

    public void setTemplateId(Long templateId)
    {
        this.templateId = templateId;
    }

    public String getCode()
    {
        return code;
    }

    public void setCode(String code)
    {
        this.code = code;
    }

    public String getCoursesName()
    {
        return coursesName;
    }

    public void setCoursesName(String coursesName)
    {
        this.coursesName = coursesName;
    }

    public String getCoursesNameEn()
    {
        return coursesNameEn;
    }

    public void setCoursesNameEn(String coursesNameEn)
    {
        this.coursesNameEn = coursesNameEn;
    }

    public Integer getIsElective()
    {
        return isElective;
    }

    public void setIsElective(Integer isElective)
    {
        this.isElective = isElective;
    }

    public String getLabel()
    {
        return label;
    }

    public void setLabel(String label)
    {
        this.label = label;
    }

    public String getLabelEn()
    {
        return labelEn;
    }

    public void setLabelEn(String labelEn)
    {
        this.labelEn = labelEn;
    }

    public Long getParentLabelId()
    {
        return parentLabelId;
    }

    public void setParentLabelId(Long parentLabelId)
    {
        this.parentLabelId = parentLabelId;
    }
    
    

}

