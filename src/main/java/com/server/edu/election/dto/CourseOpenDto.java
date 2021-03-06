package com.server.edu.election.dto;

import java.util.List;

import com.server.edu.dictionary.DictTypeEnum;
import com.server.edu.dictionary.annotation.Code2Text;
import com.server.edu.dictionary.annotation.CodeI18n;
import com.server.edu.dictionary.translator.TeacherTranslator;
import com.server.edu.election.entity.CourseOpen;
import com.server.edu.election.studentelec.context.TimeAndRoom;

@CodeI18n
public class CourseOpenDto extends CourseOpen
{
    private static final long serialVersionUID = 1L;

    private Long roundId;
    
    private Long teachingClassId;
    
    /**
     *  教学班编号
     */
    private String teachingClassCode;

    /**
     *  校区
     */
    @Code2Text(transformer="X_XQ")
    private String campus;
    
    /**
     * 教学班名称
     */
    private String teachingClassName;

    private String teachClassType;

    private Integer maxNumber;

    private Integer currentNumber;
    
    private Integer suggestStatus;
    
    private String keyWord;
    
    private List<String> courses;
    
    private String projectId;
    
    private String teachingClassRemark;
    
    private Long courseLabelId;
    @Code2Text(translator=TeacherTranslator.class,dataType = Code2Text.DataType.SPLIT)
    private String teacherCode;
    //教学班预留人数
    private Integer reserveNumber;
    
    /**
    * 手工排课标识(0未排,1已排,2教室待定,3学院通知(暂不排课))
     */
    private Integer manArrangeFlag;
    
    private String courseLabelName;
    /**
     * 选课第三、四轮退课人数
     */
    private Integer thirdWithdrawNumber;

    // 上课时间地点
    private String timeAndRoom;
    
    /**
     * 授课语言X_SKYY
     */
    @Code2Text(DictTypeEnum.X_SKYY)
    private String teachingLanguage;
    

    public String getTeachingLanguage() {
		return teachingLanguage;
	}

	public void setTeachingLanguage(String teachingLanguage) {
		this.teachingLanguage = teachingLanguage;
	}

	public String getTimeAndRoom() {
        return timeAndRoom;
    }

    public void setTimeAndRoom(String timeAndRoom) {
        this.timeAndRoom = timeAndRoom;
    }

    public Integer getThirdWithdrawNumber() {
		return thirdWithdrawNumber;
	}

	public void setThirdWithdrawNumber(Integer thirdWithdrawNumber) {
		this.thirdWithdrawNumber = thirdWithdrawNumber;
	}

	public Integer getReserveNumber() {
		return reserveNumber;
	}

	public void setReserveNumber(Integer reserveNumber) {
		this.reserveNumber = reserveNumber;
	}

	public Long getCourseLabelId() {
		return courseLabelId;
	}

	public void setCourseLabelId(Long courseLabelId) {
		this.courseLabelId = courseLabelId;
	}

	public String getTeacherCode() {
		return teacherCode;
	}

	public void setTeacherCode(String teacherCode) {
		this.teacherCode = teacherCode;
	}

	public String getProjectId() {
		return projectId;
	}

	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}

	public List<String> getCourses() {
		return courses;
	}

	public void setCourses(List<String> courses) {
		this.courses = courses;
	}

	public String getKeyWord() {
		return keyWord;
	}

	public void setKeyWord(String keyWord) {
		this.keyWord = keyWord;
	}

	public Integer getSuggestStatus() {
		return suggestStatus;
	}

	public void setSuggestStatus(Integer suggestStatus) {
		this.suggestStatus = suggestStatus;
	}

	public String getCampus() {
        return campus;
    }

    public void setCampus(String campus) {
        this.campus = campus;
    }

    public String getTeachClassType() {
        return teachClassType;
    }

    public void setTeachClassType(String teachClassType) {
        this.teachClassType = teachClassType;
    }

    public Integer getMaxNumber() {
        return maxNumber;
    }

    public void setMaxNumber(Integer maxNumber) {
        this.maxNumber = maxNumber;
    }

    public Integer getCurrentNumber() {
        return currentNumber;
    }

    public void setCurrentNumber(Integer currentNumber) {
        this.currentNumber = currentNumber;
    }

    public Long getRoundId()
    {
        return roundId;
    }
    
    public void setRoundId(Long roundId)
    {
        this.roundId = roundId;
    }
    
    public Long getTeachingClassId()
    {
        return teachingClassId;
    }
    
    public void setTeachingClassId(Long teachingClassId)
    {
        this.teachingClassId = teachingClassId;
    }
    
    public String getTeachingClassCode()
    {
        return teachingClassCode;
    }
    
    public void setTeachingClassCode(String teachingClassCode)
    {
        this.teachingClassCode = teachingClassCode;
    }

	public String getTeachingClassName() {
		return teachingClassName;
	}

	public void setTeachingClassName(String teachingClassName) {
		this.teachingClassName = teachingClassName;
	}

	public String getTeachingClassRemark() {
		return teachingClassRemark;
	}

	public void setTeachingClassRemark(String teachingClassRemark) {
		this.teachingClassRemark = teachingClassRemark;
	}

	public Integer getManArrangeFlag() {
		return manArrangeFlag;
	}

	public void setManArrangeFlag(Integer manArrangeFlag) {
		this.manArrangeFlag = manArrangeFlag;
	}

    public String getCourseLabelName()
    {
        return courseLabelName;
    }

    public void setCourseLabelName(String courseLabelName)
    {
        this.courseLabelName = courseLabelName;
    }

    public CourseOpenDto() {
    }

    public CourseOpenDto(Long roundId, Long teachingClassId, String teachingClassCode, String campus, String teachingClassName, String teachClassType, Integer maxNumber, Integer currentNumber, Integer suggestStatus, String keyWord, List<String> courses, String projectId, String teachingClassRemark, Long courseLabelId, String teacherCode, Integer reserveNumber, Integer manArrangeFlag, String courseLabelName, Integer thirdWithdrawNumber, String timeAndRoom, String teachingLanguage) {
        this.roundId = roundId;
        this.teachingClassId = teachingClassId;
        this.teachingClassCode = teachingClassCode;
        this.campus = campus;
        this.teachingClassName = teachingClassName;
        this.teachClassType = teachClassType;
        this.maxNumber = maxNumber;
        this.currentNumber = currentNumber;
        this.suggestStatus = suggestStatus;
        this.keyWord = keyWord;
        this.courses = courses;
        this.projectId = projectId;
        this.teachingClassRemark = teachingClassRemark;
        this.courseLabelId = courseLabelId;
        this.teacherCode = teacherCode;
        this.reserveNumber = reserveNumber;
        this.manArrangeFlag = manArrangeFlag;
        this.courseLabelName = courseLabelName;
        this.thirdWithdrawNumber = thirdWithdrawNumber;
        this.timeAndRoom = timeAndRoom;
        this.teachingLanguage = teachingLanguage;
    }

    @Override
    public String toString() {
        return "CourseOpenDto{" +
                "roundId=" + roundId +
                ", teachingClassId=" + teachingClassId +
                ", teachingClassCode='" + teachingClassCode + '\'' +
                ", campus='" + campus + '\'' +
                ", teachingClassName='" + teachingClassName + '\'' +
                ", teachClassType='" + teachClassType + '\'' +
                ", maxNumber=" + maxNumber +
                ", currentNumber=" + currentNumber +
                ", suggestStatus=" + suggestStatus +
                ", keyWord='" + keyWord + '\'' +
                ", courses=" + courses +
                ", projectId='" + projectId + '\'' +
                ", teachingClassRemark='" + teachingClassRemark + '\'' +
                ", courseLabelId=" + courseLabelId +
                ", teacherCode='" + teacherCode + '\'' +
                ", reserveNumber=" + reserveNumber +
                ", manArrangeFlag=" + manArrangeFlag +
                ", courseLabelName='" + courseLabelName + '\'' +
                ", thirdWithdrawNumber=" + thirdWithdrawNumber +
                ", timeAndRoom='" + timeAndRoom + '\'' +
                ", teachingLanguage='" + teachingLanguage + '\'' +
                '}';
    }
}
