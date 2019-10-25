package com.server.edu.election.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.server.edu.common.vo.SchoolCalendarVo;
import com.server.edu.common.vo.ScoreStudentResultVo;
import com.server.edu.dictionary.utils.SchoolCalendarCacheUtil;
import com.server.edu.election.dao.StudentUndergraduateScoreInfoDao;
import com.server.edu.election.dto.StudentScoreDto;
import com.server.edu.election.service.BkStudentScoreService;

public class BkStudentScoreServiceImpl implements BkStudentScoreService {
	@Autowired
	private StudentUndergraduateScoreInfoDao infoDao; 

	@Override
	public List<ScoreStudentResultVo> getStudentScoreList(StudentScoreDto dto) {
		// TODO Auto-generated method stub
		SchoolCalendarVo schoolCalendarVo =SchoolCalendarCacheUtil.getCalendar(dto.getCalendarId());
		if(schoolCalendarVo!=null) {
			dto.setAcademicYear(Long.parseLong(schoolCalendarVo.getYear().toString()));
			dto.setSemester(Long.parseLong(schoolCalendarVo.getTerm().toString()));
		}
		List<ScoreStudentResultVo> list = infoDao.getStudentScoreList(dto);
		return list;
	}

}
