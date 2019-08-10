package com.server.edu.election.studentelec.context;

import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import com.server.edu.dictionary.DictTypeEnum;
import com.server.edu.dictionary.annotation.Code2Text;
import com.server.edu.dictionary.annotation.CodeI18n;
import com.server.edu.election.studentelec.cache.TeachingClassCache;

/**
 * 已选择课程
 */
@CodeI18n
public class SelectedCourse extends TeachingClassCache
{
    /** 教学班Id */
    private Long teachClassId;
    
    /** 教学班 */
    private Long teachClassMsg;
    
    /**
     * 选课对象(1学生，2教务员，3管理员)
     */
    private Integer chooseObj;
    
    /**
     * 修读类别(1正常修读,2重修,3免修不免考,4免修)
     */
    @Code2Text(DictTypeEnum.X_XDLX)
    private Integer courseTakeType;
    

    @Code2Text(DictTypeEnum.X_KCFL)
    private String label;
    
    private String labelName;

    @Code2Text(transformer=" X_KSLX")
    private String assessmentMode;
    
    /**
     * 第几轮
     */
    private Integer turn;
    
    private Integer isApply;

    public String getAssessmentMode() {
        return assessmentMode;
    }

    public void setAssessmentMode(String assessmentMode) {
        this.assessmentMode = assessmentMode;
    }

    @Override
    public Long getTeachClassId() {
        return teachClassId;
    }

    @Override
    public void setTeachClassId(Long teachClassId) {
        this.teachClassId = teachClassId;
    }

    public Integer getIsApply() {
		return isApply;
	}

	public void setIsApply(Integer isApply) {
		this.isApply = isApply;
	}

	
	public Long getTeachClassMsg() {
		return teachClassMsg;
	}

	public void setTeachClassMsg(Long teachClassMsg) {
		this.teachClassMsg = teachClassMsg;
	}

	public SelectedCourse()
    {
        
    }
    
    public SelectedCourse(ElecCourse course)
    {
        this.setCourseCode(course.getCourseCode());
        this.setCourseName(course.getCourseName());
        this.setCredits(course.getCredits());
        this.setNameEn(course.getNameEn());
    }
    
    public SelectedCourse(TeachingClassCache course)
    {
    	this.setNature(course.getNature());
        this.setCampus(course.getCampus());
        this.setCourseCode(course.getCourseCode());
        this.setCourseName(course.getCourseName());
        this.setCredits(course.getCredits());
        this.setNameEn(course.getNameEn());
        this.setPublicElec(course.isPublicElec());
        this.setTeachClassId(course.getTeachClassId());
        this.setTeachClassMsg(course.getTeachClassId());
        this.setTeachClassCode(course.getTeachClassCode());
        this.setTeachClassType(course.getTeachClassType());
        this.setPractice(course.getPractice());
        this.setRetraining(course.getRetraining());
        this.setMaxNumber(course.getMaxNumber());
        this.setCurrentNumber(course.getCurrentNumber());
        this.setTeacherName(course.getTeacherName());
        this.setTimes(course.getTimes());
        this.setTerm(course.getTerm());
    }
    
    public Integer getChooseObj()
    {
        return chooseObj;
    }
    
    public void setChooseObj(Integer chooseObj)
    {
        this.chooseObj = chooseObj;
    }
    
    public Integer getCourseTakeType()
    {
        return courseTakeType;
    }
    
    public void setCourseTakeType(Integer courseTakeType)
    {
        this.courseTakeType = courseTakeType;
    }
    
    public Integer getTurn()
    {
        return turn;
    }
    
    public void setTurn(Integer turn)
    {
        this.turn = turn;
    }
    
	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getLabelName() {
		return labelName;
	}

	public void setLabelName(String labelName) {
		this.labelName = labelName;
	}

	@Override
    public int hashCode()
    {
        return Objects.hash(this.teachClassId, this.getCourseCode());
    }
    
    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        
        if (obj instanceof SelectedCourse)
        {
            SelectedCourse o = (SelectedCourse)obj;
            return Objects.equals(this.teachClassId, o.teachClassId)
                && StringUtils.equals(this.getCourseCode(), o.getCourseCode());
        }
        return false;
    }
}