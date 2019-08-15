package com.server.edu.election.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.server.edu.common.PageCondition;
import com.server.edu.common.enums.UserTypeEnum;
import com.server.edu.election.dao.ElcCourseTakeDao;
import com.server.edu.election.entity.ElcMedWithdraw;
import com.server.edu.election.service.ElcMedWithdrawService;
import com.server.edu.election.vo.ElcCourseTakeVo;
import com.server.edu.session.util.SessionUtils;
import com.server.edu.session.util.entity.Session;
@Service
public class ElcMedWithdrawServiceImpl implements ElcMedWithdrawService {
    @Autowired
    private ElcCourseTakeDao elcCourseTakeDao;
	@Override
	public PageInfo<ElcCourseTakeVo> page(PageCondition<ElcMedWithdraw> condition) {
		// TODO Auto-generated method stub
        List<ElcCourseTakeVo> elcCourseTakes = new ArrayList<>();
        ElcMedWithdraw dto = condition.getCondition();
        Session session = SessionUtils.getCurrentSession();
        String uid = session.realUid();
        int userType = session.realType();
        if (UserTypeEnum.STUDENT.is(userType))
        {
            PageHelper.startPage(condition.getPageNum_(),
                condition.getPageSize_());
            elcCourseTakes =
                elcCourseTakeDao.getElcMedWithdraw(uid, dto.getCalendarId());
        }
        PageInfo<ElcCourseTakeVo> pageInfo = new PageInfo<>(elcCourseTakes);
        return pageInfo;
	}

	@Override
	public int medWithdraw(Long id, String projectId) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int cancelMedWithdraw(Long id, String projectId) {
		// TODO Auto-generated method stub
		return 0;
	}

}
