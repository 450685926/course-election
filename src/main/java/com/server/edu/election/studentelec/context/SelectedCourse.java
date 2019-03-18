package com.server.edu.election.studentelec.context;

import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import com.server.edu.dictionary.DictTypeEnum;
import com.server.edu.dictionary.annotation.Code2Text;

/**
 * 已选择课程
 */
public class SelectedCourse extends ElecCourse
{
    /**是否重修课*/
    private boolean isRebuildElec;
    
    /** 上课时间按教学周拆分集合 */
    private List<ClassTimeUnit> times;
    
    /** 上课时间按节次拆分集合 */
    private List<ClassTimes> classTimes;
    
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
    
    /**
     * 第几轮
     */
    private Integer turn;
    
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
    
    public List<ClassTimeUnit> getTimes()
    {
        return times;
    }
    
    public void setTimes(List<ClassTimeUnit> times)
    {
        this.times = times;
    }
    
    public Integer getChooseObj()
    {
        return chooseObj;
    }
    
    public void setChooseObj(Integer chooseObj)
    {
        this.chooseObj = chooseObj;
    }
    
    public Long getTeachClassId()
    {
        return teachClassId;
    }
    
    public void setTeachClassId(Long teachClassId)
    {
        this.teachClassId = teachClassId;
    }
    
    public boolean isRebuildElec()
    {
        return isRebuildElec;
    }
    
    public void setRebuildElec(boolean rebuildElec)
    {
        isRebuildElec = rebuildElec;
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
    
    
    public List<ClassTimes> getClassTimes() {
		return classTimes;
	}

	public void setClassTimes(List<ClassTimes> classTimes) {
		this.classTimes = classTimes;
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