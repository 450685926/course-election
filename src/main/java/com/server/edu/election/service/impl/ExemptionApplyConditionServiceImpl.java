package com.server.edu.election.service.impl;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.server.edu.common.PageCondition;
import com.server.edu.common.locale.I18nUtil;
import com.server.edu.common.rest.PageResult;
import com.server.edu.election.constants.Constants;
import com.server.edu.election.controller.ExemptionApplyConditionController;
import com.server.edu.election.dao.CourseOpenDao;
import com.server.edu.election.dao.ExemptionApplyConditionDao;
import com.server.edu.election.dao.ExemptionApplyGraduteConditionDto;
import com.server.edu.election.dao.StudentDao;
import com.server.edu.election.entity.CourseOpen;
import com.server.edu.election.entity.ExemptionApplyGraduteCondition;
import com.server.edu.election.entity.Student;
import com.server.edu.election.service.ExemptionApplyConditionService;
import com.server.edu.exception.ParameterValidateException;
import com.server.edu.session.util.SessionUtils;
import tk.mybatis.mapper.entity.Example;

@Service
public class ExemptionApplyConditionServiceImpl implements ExemptionApplyConditionService {
	static Logger logger = LoggerFactory.getLogger(ExemptionApplyConditionController.class);
	
	@Autowired
	ExemptionApplyConditionDao exemptionApplyConditionDao;
	
	@Autowired
	CourseOpenDao courseOpenDao;
	
    @Autowired
    private StudentDao studentDao;
	
	@Override
	public void addExemptionApplyCondition(ExemptionApplyGraduteCondition applyCondition) {
		Example example = new Example(ExemptionApplyGraduteCondition.class);
    	example.createCriteria()
    		.andEqualTo("courseCode", applyCondition.getCourseCode())
    		.andEqualTo("courseName", applyCondition.getCourseName())
    		.andEqualTo("trainingLevels", applyCondition.getTrainingLevels())
    		.andEqualTo("trainingCategorys", applyCondition.getTrainingCategorys())
    		.andEqualTo("formLearnings", applyCondition.getFormLearnings())
    	    .andEqualTo("degreeTypes", applyCondition.getDegreeTypes())
    		.andEqualTo("conditions", applyCondition.getConditions())
    		.andEqualTo("projId", applyCondition.getProjId())
    		.andEqualTo("deleteStatus", String.valueOf(Constants.DELETE_FALSE));
    	
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
	    applyCondition.setDeleteStatus(Constants.DELETE_FALSE);
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
		Example example = new Example(ExemptionApplyGraduteCondition.class);
    	example.createCriteria()
    		.andEqualTo("courseCode", applyCondition.getCourseCode())
    		.andEqualTo("courseName", applyCondition.getCourseName())
    		.andEqualTo("trainingLevels", applyCondition.getTrainingLevels())
    		.andEqualTo("trainingCategorys", applyCondition.getTrainingCategorys())
    		.andEqualTo("formLearnings", applyCondition.getFormLearnings())
    		.andEqualTo("degreeTypes", applyCondition.getDegreeTypes())
    		.andEqualTo("conditions", applyCondition.getConditions())
    		.andEqualTo("projId", applyCondition.getProjId())
    		.andEqualTo("deleteStatus", String.valueOf(Constants.DELETE_FALSE))
    		.andNotEqualTo("id", applyCondition.getId());
    		
	    int count = exemptionApplyConditionDao.selectCountByExample(example);
	    if (count > 0)
	    {
	        throw new ParameterValidateException(
	            I18nUtil.getMsg("exemptionApply.condition.exist"));
	    }
		
		Date date = new Date();
        applyCondition.setUpdatedAt(date);
        
        exemptionApplyConditionDao.updateByPrimaryKeySelective(applyCondition);
	}

	@Override
	public void deleteExemptionApplyCondition(List<Long> ids) {
		for (Long id : ids) {
			ExemptionApplyGraduteCondition condition = new ExemptionApplyGraduteCondition();
			condition.setId(id);
			condition.setDeleteStatus(Constants.DELETE_TRUE);
			exemptionApplyConditionDao.updateByPrimaryKeySelective(condition);
		}
	}

	@Override
	public CourseOpen queryNameAndTrainingLevelByCode(String courseCode) {
		return courseOpenDao.queryNameAndTrainingLevelByCode(courseCode);
	}

	@Override
	public List<ExemptionApplyGraduteCondition> queryApplyConditionByCourseCodeAndStudentId(String courseCode, String studentId) {
		String studentCode = "";
		Student student = new Student();
		if (StringUtils.isNotBlank(studentId)) {
			studentCode = studentId;
			student = studentDao.findStudentByCode(studentCode);
		}
		
		ExemptionApplyGraduteCondition condition = new ExemptionApplyGraduteCondition();
    	condition.setCourseCode(courseCode);
    	
    	if (StringUtils.isNotBlank(studentId) || student != null) {
	    	String trainingLevel = "";
	    	if (StringUtils.isNotBlank(student.getTrainingLevel())) {
	    		trainingLevel = String.valueOf(Integer.parseInt(student.getTrainingLevel()));
	    		condition.setTrainingLevels(trainingLevel==null?"":trainingLevel);
			}
	    	String trainingCategory = "";
	    	if (StringUtils.isNotBlank(student.getTrainingCategory())) {
	    		trainingCategory = String.valueOf(Integer.parseInt(student.getTrainingCategory()));
	    		condition.setTrainingCategorys(trainingCategory==null?"":trainingCategory);
			}
	    	String degreeType = "";
	    	if (StringUtils.isNotBlank(student.getDegreeType())) {
	    		degreeType = String.valueOf(Integer.parseInt(student.getDegreeType()));
	    		condition.setDegreeTypes(degreeType==null?"":degreeType);
			}
	    	String formLearning = "";
	    	if (StringUtils.isNotBlank(student.getFormLearning())) {
	    		formLearning = String.valueOf(Integer.parseInt(student.getFormLearning()));
	    		condition.setFormLearnings(formLearning==null?"":formLearning);
			}
    	}
		return exemptionApplyConditionDao.queryApplyConditionByCourseCodeAndStudentId(condition);
	}


}
