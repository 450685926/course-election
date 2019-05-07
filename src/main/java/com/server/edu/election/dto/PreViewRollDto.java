package com.server.edu.election.dto;

import com.server.edu.election.vo.StudentVo;

import java.io.Serializable;
import java.util.List;

/**
 * @description: 预览点名册
 * @author: bear
 * @create: 2019-04-29 10:32
 */
public class PreViewRollDto implements Serializable{
    private List<StudentVo> studentsList;
    /**
     * 班级人数
     * */
    private Integer size;

    private Integer lineNumber;

    private Integer rowNumber;

    private List<TimeTableMessage> timeTabelList;

    private String calendarName;

    public String getCalendarName() {
        return calendarName;
    }

    public void setCalendarName(String calendarName) {
        this.calendarName = calendarName;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public Integer getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(Integer lineNumber) {
        this.lineNumber = lineNumber;
    }

    public Integer getRowNumber() {
        return rowNumber;
    }

    public void setRowNumber(Integer rowNumber) {
        this.rowNumber = rowNumber;
    }

    public List<StudentVo> getStudentsList() {
        return studentsList;
    }

    public void setStudentsList(List<StudentVo> studentsList) {
        this.studentsList = studentsList;
    }

    public List<TimeTableMessage> getTimeTabelList() {
        return timeTabelList;
    }

    public void setTimeTabelList(List<TimeTableMessage> timeTabelList) {
        this.timeTabelList = timeTabelList;
    }
}
