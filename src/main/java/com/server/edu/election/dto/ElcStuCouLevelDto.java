package com.server.edu.election.dto;

import com.server.edu.dictionary.DictTypeEnum;
import com.server.edu.dictionary.annotation.Code2Text;
import com.server.edu.dictionary.annotation.CodeI18n;
import com.server.edu.election.entity.ElcStuCouLevel;

/**
 * 学生能力
 * 
 * 
 * @author  OuYangGuoDong
 * @version  [版本号, 2019年8月22日]
 * @see  [相关类/方法]
 * @since  [产品/模块版本]
 */
@CodeI18n
public class ElcStuCouLevelDto extends ElcStuCouLevel
{
    private static final long serialVersionUID = 1L;
    
    /** 姓名 */
    private String studentName;
    
    /**培养层次*/
    @Code2Text(DictTypeEnum.X_PYCC)
    private String trainingLevel;
    
    /** 学院 */
    @Code2Text(DictTypeEnum.X_YX)
    private String faculty;
    
    /** 专业 */
    @Code2Text(DictTypeEnum.G_ZY)
    private String major;
    
    private String courseCategoryName;
    
    public String getStudentName()
    {
        return studentName;
    }
    
    public void setStudentName(String studentName)
    {
        this.studentName = studentName;
    }
    
    public String getTrainingLevel()
    {
        return trainingLevel;
    }
    
    public void setTrainingLevel(String trainingLevel)
    {
        this.trainingLevel = trainingLevel;
    }
    
    public String getFaculty()
    {
        return faculty;
    }
    
    public void setFaculty(String faculty)
    {
        this.faculty = faculty;
    }
    
    public String getMajor()
    {
        return major;
    }
    
    public void setMajor(String major)
    {
        this.major = major;
    }
    
    public String getCourseCategoryName()
    {
        return courseCategoryName;
    }
    
    public void setCourseCategoryName(String courseCategoryName)
    {
        this.courseCategoryName = courseCategoryName;
    }
    
}
