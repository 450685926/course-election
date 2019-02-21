package com.server.edu.election.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.validator.constraints.NotBlank;

import com.server.edu.common.validator.DelGroup;
import com.server.edu.dictionary.DictTypeEnum;
import com.server.edu.dictionary.annotation.Code2Text;

@Table(name = "elc_course_takes_t")
public class ElcCourseTake implements Serializable
{
    /**
     * 主键（自增）
     */
    @Id
    @Column(name = "ID_")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * 学号
     */
    @NotBlank(groups= {DelGroup.class})
    @Column(name = "STUDENT_ID_")
    private String studentId;
    
    /**
     * 校历ID（学年学期）
     */
    @Column(name = "CALENDAR_ID_")
    private Long calendarId;
    
    /**
     * 课程code
     */
    @Column(name = "COURSE_CODE_")
    private String courseCode;
    
    /**
     * 教学班ID
     */
    @NotBlank(groups= {DelGroup.class})
    @Column(name = "TEACHING_CLASS_ID_")
    private Long teachingClassId;
    
    /**
     * 修读类别(1正常修读,2重修,3免修不免考,4免修)
     */
    @Code2Text(DictTypeEnum.X_XDLX)
    @Column(name = "COURSE_TAKE_TYPE_")
    private Integer courseTakeType;
    
    /**
     * 第几轮
     */
    @Column(name = "TURN_")
    private Integer turn;

    /**
     * 选课模式
     * */
    @Column(name = "MODE_")
    private Integer mode;
    
    /**
     * 选课对象(1学生，2教务员，3管理员)
     */
    @Column(name = "CHOOSE_OBJ_")
    private Integer chooseObj;
    
    /**
     * 重修费是否支付(0否，1是)
     */
    @Column(name = "PAID_")
    private Integer paid;
    
    /**
     * 账单ID
     */
    @Column(name = "BILL_ID_")
    private Long billId;
    
    /**
     * 创建时间
     */
    @Column(name = "CREATED_AT_")
    private Date createdAt;
    
    private static final long serialVersionUID = 1L;
    
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
     * 获取学号
     *
     * @return STUDENT_ID_ - 学号
     */
    public String getStudentId()
    {
        return studentId;
    }
    
    /**
     * 设置学号
     *
     * @param studentId 学号
     */
    public void setStudentId(String studentId)
    {
        this.studentId = studentId == null ? null : studentId.trim();
    }
    
    /**
     * 获取校历ID（学年学期）
     *
     * @return CALENDAR_ID_ - 校历ID（学年学期）
     */
    public Long getCalendarId()
    {
        return calendarId;
    }
    
    /**
     * 设置校历ID（学年学期）
     *
     * @param calendarId 校历ID（学年学期）
     */
    public void setCalendarId(Long calendarId)
    {
        this.calendarId = calendarId;
    }
    
    /**
     * 获取课程Code
     *
     * @return 课程Code
     */
    public String getCourseCode()
    {
        return courseCode;
    }
    
    /**
     * 设置课程Code
     *
     * @param 课程Code
     */
    public void setCourseId(String courseCode)
    {
        this.courseCode = courseCode == null ? null : courseCode.trim();
    }
    
    /**
     * 获取教学班ID
     *
     * @return TEACHING_CLASS_ID_ - 教学班ID
     */
    public Long getTeachingClassId()
    {
        return teachingClassId;
    }
    
    /**
     * 设置教学班ID
     *
     * @param teachingClassId 教学班ID
     */
    public void setTeachingClassId(Long teachingClassId)
    {
        this.teachingClassId = teachingClassId;
    }
    
    /**
     * 获取修读类别(1正常修读,2重修,3免修不免考,4免修)
     *
     * @return COURSE_TAKE_TYPE_ - 修读类别(1正常修读,2重修,3免修不免考,4免修)
     */
    public Integer getCourseTakeType()
    {
        return courseTakeType;
    }
    
    /**
     * 设置修读类别(1正常修读,2重修,3免修不免考,4免修)
     *
     * @param courseTakeType 修读类别(1正常修读,2重修,3免修不免考,4免修)
     */
    public void setCourseTakeType(Integer courseTakeType)
    {
        this.courseTakeType = courseTakeType;
    }
    
    /**
     * 获取第几轮
     *
     * @return TURN_ - 第几轮
     */
    public Integer getTurn()
    {
        return turn;
    }
    
    /**
     * 设置第几轮
     *
     * @param turn 第几轮
     */
    public void setTurn(Integer turn)
    {
        this.turn = turn;
    }
    
    /**
     * 获取选课对象(1学生，2教务员，3管理员)
     *
     * @return CHOOSE_OBJ_ - 选课对象(1学生，2教务员，3管理员)
     */
    public Integer getChooseObj()
    {
        return chooseObj;
    }
    
    /**
     * 设置选课对象(1学生，2教务员，3管理员)
     *
     * @param chooseObj 选课对象(1学生，2教务员，3管理员)
     */
    public void setChooseObj(Integer chooseObj)
    {
        this.chooseObj = chooseObj;
    }
    
    /**
     * 获取重修费是否支付(0否，1是)
     *
     * @return PAID_ - 重修费是否支付(0否，1是)
     */
    public Integer getPaid()
    {
        return paid;
    }
    
    /**
     * 设置重修费是否支付(0否，1是)
     *
     * @param paid 重修费是否支付(0否，1是)
     */
    public void setPaid(Integer paid)
    {
        this.paid = paid;
    }
    
    /**
     * 获取账单ID
     *
     * @return BILL_ID_ - 账单ID
     */
    public Long getBillId()
    {
        return billId;
    }
    
    /**
     * 设置账单ID
     *
     * @param billId 账单ID
     */
    public void setBillId(Long billId)
    {
        this.billId = billId;
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

    public Integer getMode() {
        return mode;
    }

    public void setMode(Integer mode) {
        this.mode = mode;
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", studentId=").append(studentId);
        sb.append(", calendarId=").append(calendarId);
        sb.append(", courseCode=").append(courseCode);
        sb.append(", teachingClassId=").append(teachingClassId);
        sb.append(", courseTakeType=").append(courseTakeType);
        sb.append(", turn=").append(turn);
        sb.append(", chooseObj=").append(chooseObj);
        sb.append(", paid=").append(paid);
        sb.append(", billId=").append(billId);
        sb.append(", createdAt=").append(createdAt);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}