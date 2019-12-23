package com.server.edu.election.entity;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.server.edu.common.BaseEntity;
import com.server.edu.dictionary.annotation.Code2Text;
import com.server.edu.dictionary.annotation.CodeI18n;
import com.server.edu.dictionary.translator.ClassRoomTranslator;

@CodeI18n
@Table(name = "teaching_class_arrange_room_t")
public class ArrangeRoom extends BaseEntity {
    /**
     * 主键（自增）
     */
    @Id
    @Column(name = "ID_")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 教学班时间ID
     */
    @Column(name = "ARRANGE_TIME_ID_")
    private Long arrangeTimeId;

    /**
     *
     */
    @Column(name = "TIME_NUMBER_")
    private Integer timeNumber;

    /**
     * 起始周
     */
    @Column(name = "WEEK_NUMBER_")
    private Integer weekNumber;

//    /**
//     * 结束周
//     */
//    @Column(name = "WEEK_END_")
//    private Integer weekEnd;

    /**
     * 教室代码
     */
    @Code2Text(translator = ClassRoomTranslator.class)
    @Column(name = "ROOM_ID_")
    private String roomId;
    /**
     * 老师code
     */
    @Column(name = "TEACHER_CODE_")
    private String teacherCode;

    /**
     * 获取主键（自增）
     *
     * @return ID_ - 主键（自增）
     */
    public Long getId() {
        return id;
    }

    /**
     * 设置主键（自增）
     *
     * @param id 主键（自增）
     */
    public void setId(Long id) {
        this.id = id;
    }

    public Long getArrangeTimeId()
    {
        return arrangeTimeId;
    }

    public void setArrangeTimeId(Long arrangeTimeId)
    {
        this.arrangeTimeId = arrangeTimeId;
    }

    /**
     *
     *
     * @return TIME_NUMBER_
     */
    public Integer getTimeNumber() {
        return timeNumber;
    }

    /**
     *
     *
     */
    public void setTimeNumber(Integer timeNumber) {
        this.timeNumber = timeNumber;
    }

    /**
     * 获取起始周
     *
     * @return WEEK_NUMBER - 起始周
     */
    public Integer getWeekNumber() {
        return weekNumber;
    }

    /**
     * 设置起始周
     *
     * @param weekNumber 起始周
     */
    public void setWeekNumber(Integer weekNumber) {
        this.weekNumber = weekNumber;
    }

//    /**
//     * 获取结束周
//     *
//     * @return WEEK_END_ - 结束周
//     */
//    public Integer getWeekEnd() {
//        return weekEnd;
//    }

//    /**
//     * 设置结束周
//     *
//     * @param weekEnd 结束周
//     */
//    public void setWeekEnd(Integer weekEnd) {
//        this.weekEnd = weekEnd;
//    }

    public String getRoomId()
    {
        return roomId;
    }

    public void setRoomId(String roomId)
    {
        this.roomId = roomId;
    }
    
    public String getTeacherCode()
    {
        return teacherCode;
    }

    public void setTeacherCode(String teacherCode)
    {
        this.teacherCode = teacherCode;
    }

}
