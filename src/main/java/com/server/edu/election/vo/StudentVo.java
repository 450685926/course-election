package com.server.edu.election.vo;

import com.server.edu.dictionary.annotation.Code2Text;
import com.server.edu.dictionary.annotation.CodeI18n;
import com.server.edu.dictionary.translator.SchoolCalendarTranslator;
import com.server.edu.election.entity.Student;

/**
 * @description: 重修门数
 * @author: bear
 * @create: 2019-02-13 16:14
 */
@CodeI18n
public class StudentVo extends Student {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private Integer rebuildNumber;

    @Code2Text(translator = SchoolCalendarTranslator.class)
    private Long calendarId;

    private String calendarName;
    
    private String exportName;
    

    public String getExportName() {
		return exportName;
	}

	public void setExportName(String exportName) {
		this.exportName = exportName;
	}

	public String getCalendarName() {
        return calendarName;
    }

    public void setCalendarName(String calendarName) {
        this.calendarName = calendarName;
    }

    private Integer courseTakeType;

    public Integer getCourseTakeType() {
        return courseTakeType;
    }

    public void setCourseTakeType(Integer courseTakeType) {
        this.courseTakeType = courseTakeType;
    }

    public Long getCalendarId() {
        return calendarId;
    }

    public void setCalendarId(Long calendarId) {
        this.calendarId = calendarId;
    }

    public Integer getRebuildNumber() {
        return rebuildNumber;
    }

    public void setRebuildNumber(Integer rebuildNumber) {
        this.rebuildNumber = rebuildNumber;
    }
}
