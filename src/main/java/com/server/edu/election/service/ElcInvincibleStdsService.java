package com.server.edu.election.service;

import java.util.List;

import com.github.pagehelper.PageInfo;
import com.server.edu.common.PageCondition;
import com.server.edu.election.entity.Student;

public interface ElcInvincibleStdsService {
	PageInfo<Student> list(PageCondition<Student> condition);
    PageInfo<Student> getStudents(PageCondition<Student> condition);
    int delete(List<String> ids);
    int add(List<String> ids);
}
