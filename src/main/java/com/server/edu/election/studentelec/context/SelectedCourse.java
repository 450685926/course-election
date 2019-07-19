package com.server.edu.election.studentelec.context;

import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import com.server.edu.dictionary.DictTypeEnum;
import com.server.edu.dictionary.annotation.Code2Text;
import com.server.edu.election.studentelec.cache.TeachingClassCache;

/**
 * 已选择课程
 */
public class SelectedCourse extends TeachingClassCache
{
    /** 教学班Id */
    private Long teachClassId;
    
    /**
     * 选课对象(1学生，2教务员，3管理员)
     */
    private Integer chooseObj;
    
    /**
     * 修读类别(1正常修读,2重修,3免修不免考,4免修)
     */
    @Code2Text(DictTypeEnum.X_XDLX)
    private Integer courseTakeType;
    

    @Code2Text(transformer="X_KCFL")
    private String label;
    
    /**
     * 第几轮
     */
    private Integer turn;
    
    private Integer isApply;

    /**
     * 研究生课程教学安排
     */
    private List<String> teachingArrange;

    @Override
    public Long getTeachClassId() {
        return teachClassId;
    }

    @Override
    public void setTeachClassId(Long teachClassId) {
        this.teachClassId = teachClassId;
    }

    public List<String> getTeachingArrange() {
        return teachingArrange;
    }

    public void setTeachingArrange(List<String> teachingArrange) {
        this.teachingArrange = teachingArrange;
    }

    public Integer getIsApply() {
		return isApply;
	}

	public void setIsApply(Integer isApply) {
		this.isApply = isApply;
	}

	public SelectedCourse()
    {
        
    }
    
    public SelectedCourse(ElecCourse course)
    {
        this.setCampus(course.getCampus());
        this.setCourseCode(course.getCourseCode());
        this.setCourseName(course.getCourseName());
        this.setCredits(course.getCredits());
        this.setNameEn(course.getNameEn());
    }
    
    public SelectedCourse(TeachingClassCache course)
    {
        this.setCampus(course.getCampus());
        this.setCourseCode(course.getCourseCode());
        this.setCourseName(course.getCourseName());
        this.setCredits(course.getCredits());
        this.setNameEn(course.getNameEn());
        this.setPublicElec(course.isPublicElec());
        this.setTeachClassId(course.getTeachClassId());
        this.setTeachClassCode(course.getTeachClassCode());
        this.setTeachClassType(course.getTeachClassType());
        this.setPractice(course.getPractice());
        this.setRetraining(course.getRetraining());
        this.setMaxNumber(course.getMaxNumber());
        this.setCurrentNumber(course.getCurrentNumber());
        this.setTeacherName(course.getTeacherName());
        this.setTimes(course.getTimes());
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