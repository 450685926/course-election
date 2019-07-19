package com.server.edu.election.service.impl;

import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.server.edu.common.PageCondition;
import com.server.edu.common.locale.I18nUtil;
import com.server.edu.common.rest.PageResult;
import com.server.edu.election.dao.CourseOpenDao;
import com.server.edu.election.dao.ExemptionApplyConditionDao;
import com.server.edu.election.dao.ExemptionApplyGraduteConditionDto;
import com.server.edu.election.entity.CourseOpen;
import com.server.edu.election.entity.ExemptionApplyGraduteCondition;
import com.server.edu.election.service.ExemptionApplyConditionService;
import com.server.edu.exception.ParameterValidateException;
import com.server.edu.session.util.SessionUtils;
import tk.mybatis.mapper.entity.Example;

@Service
public class ExemptionApplyConditionServiceImpl implements ExemptionApplyConditionService {
	@Autowired
	ExemptionApplyConditionDao exemptionApplyConditionDao;
	
	@Autowired
	CourseOpenDao courseOpenDao;
	
	@Override
	public void addExemptionApplyCondition(ExemptionApplyGraduteCondition applyCondition) {
		Example example = new Example(ExemptionApplyGraduteCondition.class);
    	example.createCriteria()
    		.andEqualTo("courseCode", applyCondition.getCourseCode())
    		.andEqualTo("courseName", applyCondition.getCourseName())
    		.andEqualTo("trainingLevels", applyCondition.getTrainingLevels())
    		.andEqualTo("trainingCategorys", applyCondition.getTrainingCategorys())
    		.andEqualTo("formLearnings", applyCondition.getFormLearnings())
    	    .andEqualTo("degreeCategorys", applyCondition.getDegreeCategorys())
    		.andEqualTo("conditions", applyCondition.getConditions())
    		.andEqualTo("projId", applyCondition.getProjId());
    	
	    int count = exemptionApplyConditionDao.selectCountByExample(example);
	    if (count > 0)
	    {
	        throw new ParameterValidateException(
	            I18nUtil.getMsg("exemptionApply.condition.exist"));
	    }
		
		String uid = SessionUtils.getCurrentSession().getUid();
	    Date date = new Date();
	    applyCondition.setCreateBy(uid);
	    applyCondition.setCreatedAt(date);
	    exemptionApplyConditionDao.insertSelective(applyCondition);
	}

	@Override
	public PageResult<ExemptionApplyGraduteCondition> queryExemptionApplyCondition(
			PageCondition<ExemptionApplyGraduteConditionDto> applyCondition) {
		PageHelper.startPage(applyCondition.getPageNum_(), applyCondition.getPageSize_());
		
		ExemptionApplyGraduteCondition applyAuditSwitch = applyCondition.getCondition();
		Page<ExemptionApplyGraduteCondition> page = exemptionApplyConditionDao.listPage(applyAuditSwitch);
		PageResult<ExemptionApplyGraduteCondition> result = new PageResult<>(page);
		return result;
	}

	@Override
	public ExemptionApplyGraduteCondition findExemptionAuditConditionById(Long id) {
		return exemptionApplyConditionDao.selectByPrimaryKey(id);
	}

	@Override
	public void updateExemptionApplyCondition(ExemptionApplyGraduteCondition applyCondition) {
        Date date = new Date();
        applyCondition.setUpdatedAt(date);
        
        exemptionApplyConditionDao.updateByPrimaryKeySelective(applyCondition);
	}

	@Override
	public void deleteExemptionApplyCondition(List<Long> ids) {
		for (Long id : ids) {
			exemptionApplyConditionDao.deleteByPrimaryKey(id);
		}
	}

	@Override
	public CourseOpen queryNameAndTrainingLevelByCode(String courseCode) {
		return courseOpenDao.queryNameAndTrainingLevelByCode(courseCode);
	}

}
