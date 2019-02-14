package com.server.edu.election.service.impl;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.server.edu.common.PageCondition;
import com.server.edu.common.locale.I18nUtil;
import com.server.edu.election.constants.Constants;
import com.server.edu.election.dao.ElcMedWithdrawApplyDao;
import com.server.edu.election.dao.ElcMedWithdrawApplyLogDao;
import com.server.edu.election.dto.ElcMedWithdrawApplyDto;
import com.server.edu.election.entity.ElcMedWithdrawApply;
import com.server.edu.election.entity.ElcMedWithdrawApplyLog;
import com.server.edu.election.service.ElcMedWithdrawApplyService;
import com.server.edu.election.vo.ElcMedWithdrawApplyVo;
import com.server.edu.exception.ParameterValidateException;
import com.server.edu.session.util.SessionUtils;

import tk.mybatis.mapper.entity.Example;
@Service
public class ElcMedWithdrawApplyServiceImpl implements ElcMedWithdrawApplyService {
	@Autowired
	private ElcMedWithdrawApplyDao elcMedWithdrawApplyDao;
	@Autowired
	private ElcMedWithdrawApplyLogDao elcMedWithdrawApplyLog;
	@Override
	public PageInfo<ElcMedWithdrawApplyVo> list(PageCondition<ElcMedWithdrawApplyDto> condition){
		PageHelper.startPage(condition.getPageNum_(), condition.getPageSize_());
		List<ElcMedWithdrawApplyVo> list = elcMedWithdrawApplyDao.selectMedApplyList(condition.getCondition());
		PageInfo<ElcMedWithdrawApplyVo> pageInfo =new PageInfo<>(list);
		return pageInfo;
	}
	
	@Override
	public int approval(List<Long> ids) {
		List<ElcMedWithdrawApplyLog> logs =new ArrayList<>();
		Example example = new Example(ElcMedWithdrawApply.class);
		Example.Criteria criteria = example.createCriteria();
		criteria.andIn("id", ids);
		List<ElcMedWithdrawApply> list = elcMedWithdrawApplyDao.selectByExample(example);
		list.forEach(temp->{
			ElcMedWithdrawApplyLog log = new ElcMedWithdrawApplyLog();
			temp.setWithdrawFlag(ElcMedWithdrawApplyVo.WITHDRAW_FLAG_);
			log.setStudentId(temp.getStudentId());
			log.setTeachingClassId(temp.getTeachingClassId());
			//获取的是本地的IP地址 //PC-20140317PXKX/192.168.0.121
			try {
				InetAddress address = InetAddress.getLocalHost();
				String hostAddress = address.getHostAddress();
				log.setOprationClientIp(hostAddress);
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String userId =SessionUtils.getCurrentSession().realUid();
			String userName = SessionUtils.getCurrentSession().realName();
			log.setOprationObjCode(userId);
			log.setOprationObjName(userName);
			log.setTargetObjCode(userId);
			log.setTargetObjName(userName);
			log.setOprationType(Constants.EN_AUDIT);
			log.setCreatedAt(new Date());
			logs.add(log);
		});
		//审核
		int result = elcMedWithdrawApplyDao.batchUpdate(list);
		if(result<=Constants.ZERO) {
			throw new ParameterValidateException(I18nUtil.getMsg("elcMedWithdraw.approvalError"));
		}
		result = elcMedWithdrawApplyLog.batchInsert(logs);
		return result;
	}

}
