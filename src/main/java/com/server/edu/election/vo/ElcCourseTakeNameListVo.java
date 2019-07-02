package com.server.edu.election.vo;

import com.server.edu.dictionary.annotation.CodeI18n;
import com.server.edu.election.entity.Student;

@CodeI18n
public class ElcCourseTakeNameListVo
{
    private static final long serialVersionUID = 1L;
    
    private Student StudentInfo;
    
    private ElcCourseTakeVo elcCourseTakeVo;

	public Student getStudentInfo() {
		return StudentInfo;
	}

	public void setStudentInfo(Student studentInfo) {
		StudentInfo = studentInfo;
	}

	public ElcCourseTakeVo getElcCourseTakeVo() {
		return elcCourseTakeVo;
	}

	public void setElcCourseTakeVo(ElcCourseTakeVo elcCourseTakeVo) {
		this.elcCourseTakeVo = elcCourseTakeVo;
	}
    
    
    
}
