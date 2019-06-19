package com.server.edu.election.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.server.edu.common.BaseEntity;
import com.server.edu.common.jackson.LongJsonSerializer;
import com.server.edu.dictionary.DictTypeEnum;
import com.server.edu.dictionary.annotation.Code2Text;
import com.server.edu.dictionary.annotation.CodeI18n;

import io.swagger.annotations.ApiModelProperty;

/**
 * 教学班表
 * 
 * 
 * @author  OuYangGuoDong
 * @version  [版本号, 2018年11月28日]
 * @see  [相关类/方法]
 * @since  [产品/模块版本]
 */
@CodeI18n
@Table(name = "teaching_class_t")
public class TeachingClass extends BaseEntity
{
    /**
     * 主键（自增）
     */
    @Id
    @Column(name = "ID_")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonSerialize(using = LongJsonSerializer.class)
    private Long id;
    
    /**
     * 教学任务ID
     */
    @Column(name = "TASK_ID_")
    private Long taskId;
    
    /**
     * 教学班编号（课程代码+2位）
     */
    @NotEmpty
    @Column(name = "CODE_")
    private String code;
    
    /**
     * 教学班名称
     */
    @Column(name = "NAME_")
    private String name;
    
    /**
     * 是否多媒体课程(1:是，0：否)
     */
    @Column(name = "IS_MEDIA_")
    private Integer isMedia;
    
    /**
     * 教室类型X_JSLX
     */
    @Code2Text(DictTypeEnum.X_JSLX)
    @Column(name = "ROOM_TYPE_")
    private String roomType;
    
    /**
     * 授课语言X_SKYY
     */
    @Code2Text(DictTypeEnum.X_SKYY)
    @Column(name = "TEACHING_LANGUAGE_")
    private String teachingLanguage;
    
    /**
     * 课程类别ID
     */
    @Code2Text(DictTypeEnum.X_KCFL)
    @Column(name = "COURSE_LABEL_ID_")
    private Long courseLabelId;
    
    /**
     * 考核方式X_KSLX（0考试/1考查）
     */
    @Code2Text(DictTypeEnum.X_KSLX)
    @Column(name = "ASSESSMENT_MODE_")
    private String assessmentMode;
    
    /**
     * 学时
     */
    @Column(name = "PERIOD_")
    private Double period;
    
    /**
     * 单双周（0单双周/1单周/2双周）
     */
    @Column(name = "ARRANGE_MODE_")
    private String arrangeMode;
    
    /**
     * 周课时
     */
    @Column(name = "WEEK_HOUR_")
    private Double weekHour;
    
    /**
     * 排课周学时
     */
    @Column(name = "CLASS_HOUR_")
    private Double classHour;
    
    /**
     * 多媒体周学时
     */
    @Column(name = "MEDIA_HOUR_")
    private Double mediaHour;
    
    /**
     * 校区（取字典X_XQ）
     */
    @Code2Text(DictTypeEnum.X_XQ)
    @Column(name = "CAMPUS_")
    private String campus;
    
    /**
     * 班级类型
     */
    @Column(name = "CLASS_TYPE_")
    private String classType;
    
    /**
     * 开班人数
     */
    @NotNull
    @Column(name = "NUMBER_")
    private Integer number;
    
    /**
     * 选课人数
     */
    @Column(name = "ELC_NUMBER_")
    private Integer elcNumber;
    
    /**
     * 预排课标识(0未排,1已排)
     */
    @Column(name = "PRE_ARRANGE_FLAG_")
    private Integer preArrangeFlag;
    
    /**
     * 手工排课标识(0未排,1已排)
     */
    @Column(name = "MAN_ARRANGE_FLAG_")
    private Integer manArrangeFlag;
    
    /**
     * 发布标识(0未发布,1已发布)
     */
    @Column(name = "DEPLOY_FLAG_")
    private Integer deployFlag;
    
    /**
     * 备注
     */
    @Column(name = "REMARK_")
    private String remark;
    
    /**
     * 创建时间
     */
    @Column(name = "CREATED_AT_")
    private Date createdAt;
    
    /**
     * 修改时间
     */
    @Column(name = "UPDATED_AT_")
    private Date updatedAt;
    
    /**
     * 总周时
     */
    @ApiModelProperty(value = "总周时", example = "1")
    @Column(name = "TOTAL_WEEK_")
    private Integer totalWeek;
    
    @ApiModelProperty(value = "开始排课周", example = "")
    @Column(name = "START_WEEK_")
    private Integer startWeek;
    
    @ApiModelProperty(value = "结束排课周", example = "")
    @Column(name = "END_WEEK_")
    private Integer endWeek;
    
    /**
     * 获取主键（自增）
     *
     * @return ID_ - 主键（自增）
     */
    public Long getId()
    {
        return id;
    }
    
    /**
     * 设置主键（自增）
     *
     * @param id 主键（自增）
     */
    public void setId(Long id)
    {
        this.id = id;
    }
    
    /**
     * 获取教学任务ID
     *
     * @return TASK_ID_ - 教学任务ID
     */
    public Long getTaskId()
    {
        return taskId;
    }
    
    /**
     * 设置教学任务ID
     *
     * @param taskId 教学任务ID
     */
    public void setTaskId(Long taskId)
    {
        this.taskId = taskId;
    }
    
    /**
     * 获取教学班编号（课程代码+2位）
     *
     * @return CODE_ - 教学班编号（课程代码+2位）
     */
    public String getCode()
    {
        return code;
    }
    
    /**
     * 设置教学班编号（课程代码+2位）
     *
     * @param code 教学班编号（课程代码+2位）
     */
    public void setCode(String code)
    {
        this.code = code == null ? null : code.trim();
    }
    
    /**
     * 获取是否多媒体课程(1:是，0：否)
     *
     * @return IS_MEDIA_ - 是否多媒体课程(1:是，0：否)
     */
    public Integer getIsMedia()
    {
        return isMedia;
    }
    
    /**
     * 设置是否多媒体课程(1:是，0：否)
     *
     * @param isMedia 是否多媒体课程(1:是，0：否)
     */
    public void setIsMedia(Integer isMedia)
    {
        this.isMedia = isMedia;
    }
    
    /**
     * 获取教室类型X_JSLX
     *
     * @return ROOM_TYPE_ - 教室类型X_JSLX
     */
    public String getRoomType()
    {
        return roomType;
    }
    
    /**
     * 设置教室类型X_JSLX
     *
     * @param roomType 教室类型X_JSLX
     */
    public void setRoomType(String roomType)
    {
        this.roomType = roomType == null ? null : roomType.trim();
    }
    
    /**
     * 获取授课语言X_SKYY
     *
     * @return TEACHING_LANGUAGE_ - 授课语言X_SKYY
     */
    public String getTeachingLanguage()
    {
        return teachingLanguage;
    }
    
    /**
     * 设置授课语言X_SKYY
     *
     * @param teachingLanguage 授课语言X_SKYY
     */
    public void setTeachingLanguage(String teachingLanguage)
    {
        this.teachingLanguage =
            teachingLanguage == null ? null : teachingLanguage.trim();
    }
    
    /**
     * 获取课程类别ID
     *
     * @return COURSE_LABEL_ID_ - 课程类别ID
     */
    public Long getCourseLabelId()
    {
        return courseLabelId;
    }
    
    /**
     * 设置课程类别ID
     *
     * @param courseLabelId 课程类别ID
     */
    public void setCourseLabelId(Long courseLabelId)
    {
        this.courseLabelId = courseLabelId;
    }
    
    /**
     * 获取考核方式X_KSLX（0考试/1考查）
     *
     * @return ASSESSMENT_MODE_ - 考核方式X_KSLX（0考试/1考查）
     */
    public String getAssessmentMode()
    {
        return assessmentMode;
    }
    
    /**
     * 设置考核方式X_KSLX（0考试/1考查）
     *
     * @param assessmentMode 考核方式X_KSLX（0考试/1考查）
     */
    public void setAssessmentMode(String assessmentMode)
    {
        this.assessmentMode =
            assessmentMode == null ? null : assessmentMode.trim();
    }
    
    /**
     * 获取学时
     *
     * @return PERIOD_ - 学时
     */
    public Double getPeriod()
    {
        return period;
    }
    
    /**
     * 设置学时
     *
     * @param period 学时
     */
    public void setPeriod(Double period)
    {
        this.period = period;
    }
    
    /**
     * 获取单双周（0单双周/1单周/2双周）
     *
     * @return ARRANGE_MODE_ - 单双周（0单双周/1单周/2双周）
     */
    public String getArrangeMode()
    {
        return arrangeMode;
    }
    
    /**
     * 设置单双周（0单双周/1单周/2双周）
     *
     * @param arrangeMode 单双周（0单双周/1单周/2双周）
     */
    public void setArrangeMode(String arrangeMode)
    {
        this.arrangeMode = arrangeMode == null ? null : arrangeMode.trim();
    }
    
    /**
     * 获取周课时
     *
     * @return WEEK_HOUR_ - 周课时
     */
    public Double getWeekHour()
    {
        return weekHour;
    }
    
    /**
     * 设置周课时
     *
     * @param weekHour 周课时
     */
    public void setWeekHour(Double weekHour)
    {
        this.weekHour = weekHour;
    }
    
    /**
     * 获取排课周学时
     *
     * @return CLASS_HOUR_ - 排课周学时
     */
    public Double getClassHour()
    {
        return classHour;
    }
    
    /**
     * 设置排课周学时
     *
     * @param classHour 排课周学时
     */
    public void setClassHour(Double classHour)
    {
        this.classHour = classHour;
    }
    
    /**
     * 获取多媒体周学时
     *
     * @return MEDIA_HOUR_ - 多媒体周学时
     */
    public Double getMediaHour()
    {
        return mediaHour;
    }
    
    /**
     * 设置多媒体周学时
     *
     * @param mediaHour 多媒体周学时
     */
    public void setMediaHour(Double mediaHour)
    {
        this.mediaHour = mediaHour;
    }
    
    /**
     * 获取校区（取字典X_XQ）
     *
     * @return CAMPUS_ - 校区（取字典X_XQ）
     */
    public String getCampus()
    {
        return campus;
    }
    
    /**
     * 设置校区（取字典X_XQ）
     *
     * @param campus 校区（取字典X_XQ）
     */
    public void setCampus(String campus)
    {
        this.campus = campus == null ? null : campus.trim();
    }
    
    /**
     * 获取班级类型
     *
     * @return CLASS_TYPE_ - 班级类型
     */
    public String getClassType()
    {
        return classType;
    }
    
    /**
     * 设置班级类型
     *
     * @param classType 班级类型
     */
    public void setClassType(String classType)
    {
        this.classType = classType == null ? null : classType.trim();
    }
    
    /**
     * 获取开班人数
     *
     * @return NUMBER_ - 开班人数
     */
    public Integer getNumber()
    {
        return number;
    }
    
    /**
     * 设置开班人数
     *
     * @param number 开班人数
     */
    public void setNumber(Integer number)
    {
        this.number = number;
    }
    
    public Integer getElcNumber()
    {
        return elcNumber;
    }
    
    public void setElcNumber(Integer elcNumber)
    {
        this.elcNumber = elcNumber;
    }
    
    public Integer getPreArrangeFlag()
    {
        return preArrangeFlag;
    }
    
    public void setPreArrangeFlag(Integer preArrangeFlag)
    {
        this.preArrangeFlag = preArrangeFlag;
    }
    
    public Integer getManArrangeFlag()
    {
        return manArrangeFlag;
    }
    
    public void setManArrangeFlag(Integer manArrangeFlag)
    {
        this.manArrangeFlag = manArrangeFlag;
    }
    
    public Integer getDeployFlag()
    {
        return deployFlag;
    }
    
    public void setDeployFlag(Integer deployFlag)
    {
        this.deployFlag = deployFlag;
    }
    
    /**
     * 获取备注
     *
     * @return REMARK_ - 备注
     */
    public String getRemark()
    {
        return remark;
    }
    
    /**
     * 设置备注
     *
     * @param remark 备注
     */
    public void setRemark(String remark)
    {
        this.remark = remark == null ? null : remark.trim();
    }
    
    /**
     * 获取创建时间
     *
     * @return CREATED_AT_ - 创建时间
     */
    public Date getCreatedAt()
    {
        return createdAt;
    }
    
    /**
     * 设置创建时间
     *
     * @param createdAt 创建时间
     */
    public void setCreatedAt(Date createdAt)
    {
        this.createdAt = createdAt;
    }
    
    /**
     * 获取修改时间
     *
     * @return UPDATED_AT_ - 修改时间
     */
    public Date getUpdatedAt()
    {
        return updatedAt;
    }
    
    /**
     * 设置修改时间
     *
     * @param updatedAt 修改时间
     */
    public void setUpdatedAt(Date updatedAt)
    {
        this.updatedAt = updatedAt;
    }
    
    /**
     * 拷贝教学班属性
     * @param teachingClass
     */
    public void copy(TeachingClass teachingClass)
    {
        this.taskId = teachingClass.getTaskId();
        this.code = teachingClass.getCode();
        this.campus = teachingClass.getCampus();
        this.arrangeMode = teachingClass.getArrangeMode();
        this.assessmentMode = teachingClass.getAssessmentMode();
        this.teachingLanguage = teachingClass.getTeachingLanguage();
        this.isMedia = teachingClass.getIsMedia();
        this.classHour = teachingClass.getClassHour();
        this.weekHour = teachingClass.getWeekHour();
        this.mediaHour = teachingClass.getMediaHour();
        this.classType = teachingClass.getClassType();
        this.courseLabelId = teachingClass.getCourseLabelId();
        this.period = teachingClass.getPeriod();
        this.startWeek = teachingClass.getStartWeek();
        this.endWeek = teachingClass.getEndWeek();
        this.totalWeek = teachingClass.getTotalWeek();
        this.roomType = teachingClass.getRoomType();
    }
    
    public Integer getTotalWeek()
    {
        return totalWeek;
    }
    
    public void setTotalWeek(Integer totalWeek)
    {
        this.totalWeek = totalWeek;
    }
    
    public Integer getStartWeek()
    {
        return startWeek;
    }
    
    public void setStartWeek(Integer startWeek)
    {
        this.startWeek = startWeek;
    }
    
    public Integer getEndWeek()
    {
        return endWeek;
    }
    
    public void setEndWeek(Integer endWeek)
    {
        this.endWeek = endWeek;
    }
    
    public String getName()
    {
        return name;
    }
    
    public void setName(String name)
    {
        this.name = name;
    }
    
}