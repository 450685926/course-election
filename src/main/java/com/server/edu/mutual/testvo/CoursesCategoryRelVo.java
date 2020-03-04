package com.server.edu.mutual.testvo;

import com.server.edu.dictionary.DictTypeEnum;
import com.server.edu.dictionary.annotation.Code2Text;
import com.server.edu.dictionary.annotation.CodeI18n;

import java.util.Set;

@CodeI18n
public class CoursesCategoryRelVo {
    private Boolean page = true;

    public Boolean getPage() {
        return null == page ? true : page;
    }

    public void setPage(Boolean page) {
        this.page = page;
    }

    private Integer pageNum_;

    private Integer pageSize_;

    private String condition;

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public Integer getPageNum_() {
        return pageNum_;
    }

    public void setPageNum_(Integer pageNum_) {
        this.pageNum_ = pageNum_;
    }

    public Integer getPageSize_() {
        return pageSize_;
    }

    public void setPageSize_(Integer pageSize_) {
        this.pageSize_ = pageSize_;
    }

    /**
     * @Description: 批量删除接受id
     * @author kan yuanfeng
     * @date 2018/10/24 16:59
     */
    private Set<Long> ids;

    public Set<Long> getIds() {
        return ids;
    }

    public void setIds(Set<Long> ids) {
        this.ids = ids;
    }

    /**
     * @Description: 开课院系
     * @author kan yuanfeng
     * @date 2019/2/12 14:48
     */
    @Code2Text(DictTypeEnum.X_YX)
    private String faculty;

    private String trainingLevel;

    /**
     * 课程code
     */
    private String code;

    /**
     * 课程名称
     */
    private String name;

    /**
     * 课程名称英文
     */
    private String nameEn;

    /**
     * 课程学分
     */
    private Double credits;

    /**
     * 周数
     */
    private Integer weeks;

    /**
     * 周课时
     */
    private Double weekHour;

    /**
     * 课时
     */
    private Double period;

    /**
     * 选课获取英语课表示
     */
    private String englishFlag;

    public String getFaculty() {
        return faculty;
    }

    public void setFaculty(String faculty) {
        this.faculty = faculty;
    }

    public String getTrainingLevel() {
        return trainingLevel;
    }

    public void setTrainingLevel(String trainingLevel) {
        this.trainingLevel = trainingLevel;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getCredits() {
        return credits;
    }

    public void setCredits(Double credits) {
        this.credits = credits;
    }

    public Integer getWeeks() {
        return weeks;
    }

    public void setWeeks(Integer weeks) {
        this.weeks = weeks;
    }

    public Double getWeekHour() {
        return weekHour;
    }

    public void setWeekHour(Double weekHour) {
        this.weekHour = weekHour;
    }

    public Double getPeriod() {
        return period;
    }

    public void setPeriod(Double period) {
        this.period = period;
    }

    public String getNameEn() {
        return nameEn;
    }

    public void setNameEn(String nameEn) {
        this.nameEn = nameEn;
    }

    public String getEnglishFlag() {
        return englishFlag;
    }

    public void setEnglishFlag(String englishFlag) {
        this.englishFlag = englishFlag;
    }
}
