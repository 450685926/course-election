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
    private List<StudentVo> list;
    /**
     * 班级人数
     * */
    private Integer size;

    private Integer lineNumber;

    private Integer rowNumber;

    private List<String> stringlist;

    private String calendarName;

    public String getCalendarName() {
        return calendarName;
    }

    public void setCalendarName(String calendarName) {
        this.calendarName = calendarName;
    }

    public List<StudentVo> getList() {
        return list;
    }

    public void setList(List<StudentVo> list) {
        this.list = list;
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

    public List<String> getStringlist() {
        return stringlist;
    }

    public void setStringlist(List<String> stringlist) {
        this.stringlist = stringlist;
    }
}
