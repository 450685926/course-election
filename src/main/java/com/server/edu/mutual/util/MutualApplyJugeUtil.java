package com.server.edu.mutual.util;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.server.edu.common.PageCondition;
import com.server.edu.common.entity.ScoreCountVo;
import com.server.edu.common.entity.StudentScoreChangeCondition;
import com.server.edu.common.locale.I18nUtil;
import com.server.edu.election.constants.Constants;
import com.server.edu.election.rpc.ScoreServiceInvoker;
import com.server.edu.exception.ParameterValidateException;
import com.server.edu.mutual.dao.ElcMutualApplyDao;
import com.server.edu.mutual.dao.ElcMutualApplySwitchDao;
import com.server.edu.mutual.dto.ElcMutualApplyDto;
import com.server.edu.mutual.entity.ElcMutualApplyTurns;
import com.server.edu.mutual.vo.ElcMutualApplyVo;
import com.server.edu.util.CollectionUtil;

import tk.mybatis.mapper.entity.Example;

public class MutualApplyJugeUtil {
	@Autowired
	private ElcMutualApplySwitchDao elcMutualApplySwitchDao;
	
	@Autowired
	private ElcMutualApplyDao elcMutualApplyDao;
	
	
	public MutualApplyJugeUtil() {
		super();
	}

	public MutualApplyJugeUtil(ElcMutualApplySwitchDao elcMutualApplySwitchDao, ElcMutualApplyDao elcMutualApplyDao) {
		super();
		this.elcMutualApplySwitchDao = elcMutualApplySwitchDao;
		this.elcMutualApplyDao = elcMutualApplyDao;
	}


	/**
	 * 校验学生是否可以申请互选课程
	 * @return
	 */
	public Boolean jugeApplyMutualCourses(ElcMutualApplyDto dto, String projectId, String studentId) {
		Boolean flag = false;

		Example example = new Example(ElcMutualApplyTurns.class);
		Example.Criteria criteria = example.createCriteria();
						 criteria.andEqualTo("calendarId", dto.getCalendarId());
						 criteria.andEqualTo("projectId", projectId);
						 criteria.andEqualTo("category", dto.getCategory());
						 criteria.andEqualTo("open", Constants.DELETE_TRUE);
		ElcMutualApplyTurns elcMutualApplyTurns  = elcMutualApplySwitchDao.selectOneByExample(example);
		if (elcMutualApplyTurns == null) {
			String appendStr = "";
			if(Constants.BK_CROSS.equals(dto.getMode())) {//跨学科选课
				throw new ParameterValidateException(I18nUtil.getMsg("elcMutualApply.applyStatusError2"));
			}else {	//本研互选
				throw new ParameterValidateException(I18nUtil.getMsg("elcMutualApply.applyStatusError"));
			}
//			throw new ParameterValidateException(I18nUtil.getMsg("elcMutualApply.applyStatusError"));
		}
		
		Date date = new Date();
		if (date.after(elcMutualApplyTurns.getBeginAt()) && date.before(elcMutualApplyTurns.getEndAt())) {
			// 是否开启成绩检测
			if (elcMutualApplyTurns.getFail().intValue() == 1) {
			    // ScoreServiceInvoker.findStuFailedCourseCodes(studentId);
			    // 接口调用失败,暂时查主表,后期分表建立完善后,再做修改
				List<String> failedCourseCodes = elcMutualApplyDao.findStuFailedCourseCodes(studentId);
				if (CollectionUtil.isNotEmpty(failedCourseCodes)) {
					throw new ParameterValidateException(I18nUtil.getMsg("elcMutualApply.hasFailCourses"));
				}
			}
			
			// 检测绩点是否合格
            // 检测绩点的前提是首先校验成绩检测是否开启，只有在成绩检测开启状态下才继续校验绩点
			if (elcMutualApplyTurns.getFail().intValue() == 1 && elcMutualApplyTurns.getGpa() != null
					&& elcMutualApplyTurns.getGpa().floatValue() > 0.0f) {
				//本科生校验绩点
				if(projectId.equalsIgnoreCase("1")){
					//获取设置表中的绩点 eg:4
					double gpa=Double.valueOf(elcMutualApplyTurns.getGpa());
					//校验学生平均绩点
					Map<String,Object> objectMap=elcMutualApplyDao.selectAvgPonitByStudentId(studentId);
					//判断平均绩点,float
					if(objectMap!=null){
						if(objectMap.get("avgPoint")!=null){
							Double avgPoint= (Double) objectMap.get("avgPoint");//eg:4.222
							//如果设置的绩点数值比计算的平均值大.则说明平均绩点不合格
							if(gpa>avgPoint){
								throw new ParameterValidateException(I18nUtil.getMsg("elcMutualApply.avgGradePoint",elcMutualApplyTurns.getGpa().floatValue()));
							}
						}
					}
				}else {
					PageCondition<StudentScoreChangeCondition> pageCondition = new PageCondition<StudentScoreChangeCondition>();
					StudentScoreChangeCondition studentScore = new StudentScoreChangeCondition();
					studentScore.setCalendarId(dto.getCalendarId());
					studentScore.setStudentId(studentId);
					pageCondition.setCondition(studentScore);

					// 备注：本科生查询学生平均绩点功能未做，研究生可用
					List<ScoreCountVo> list = ScoreServiceInvoker.listAvgScorePage2(pageCondition);
					if (CollectionUtil.isEmpty(list)) {
						throw new ParameterValidateException(I18nUtil.getMsg("elcMutualApply.avgGradePoint", elcMutualApplyTurns.getGpa().floatValue()));
					} else {
						Double avgGradePoint = list.get(0).getAvgGradePoint();
						if (avgGradePoint.doubleValue() < elcMutualApplyTurns.getGpa().floatValue()) {
							throw new ParameterValidateException(I18nUtil.getMsg("elcMutualApply.avgGradePoint", elcMutualApplyTurns.getGpa().floatValue()));
						}
					}
				}
			}
						
			// 校验是否超出申请门数上限
			if (elcMutualApplyTurns.getAppLimit() != null) {
				dto.setStudentId(studentId);
				if(Constants.BK_CROSS.equals(dto.getMode())) {
					dto.setInType(Constants.FIRST);
				}else {
					dto.setByType(Constants.FIRST);
				}
				
				List<String> projectIds = ProjectUtil.getProjectIds(projectId);
				dto.setProjectIds(projectIds);
				
				List<ElcMutualApplyVo> list = elcMutualApplyDao.getElcMutualApplyList(dto);
				
				List<Long> mutualCourseIds = dto.getMutualCourseIds();
				if(CollectionUtil.isEmpty(mutualCourseIds)) {
					throw new ParameterValidateException(I18nUtil.getMsg("elcMutualCourses.courseNullError")); 
				}
				if (!Constants.BK_CROSS.equals(dto.getMode()) && CollectionUtil.isNotEmpty(list) && elcMutualApplyTurns.getAppLimit() < list.size() + mutualCourseIds.size()) {
					throw new ParameterValidateException(I18nUtil.getMsg("elcMutualApply.outOfLimit"));
				}
			}
			flag = true;
		}else {
			throw new ParameterValidateException(I18nUtil.getMsg("elcMutualApply.applyStatusError"));
		}
		return flag;
	}
}
