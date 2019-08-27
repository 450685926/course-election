package com.server.edu.election.studentelec.rules.yjs;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.server.edu.common.locale.I18nUtil;
import com.server.edu.election.constants.Constants;
import com.server.edu.election.dao.ElectionParameterDao;
import com.server.edu.election.dao.ElectionRuleDao;
import com.server.edu.election.dao.StudentDao;
import com.server.edu.election.dao.TeachingClassElectiveRestrictAttrDao;
import com.server.edu.election.dto.SuggestProfessionDto;
import com.server.edu.election.entity.ElectionParameter;
import com.server.edu.election.entity.ElectionRule;
import com.server.edu.election.entity.Student;
import com.server.edu.election.entity.TeachingClassElectiveRestrictAttr;
import com.server.edu.election.studentelec.cache.StudentInfoCache;
import com.server.edu.election.studentelec.cache.TeachingClassCache;
import com.server.edu.election.studentelec.context.ElecContext;
import com.server.edu.election.studentelec.context.ElecRespose;
import com.server.edu.election.studentelec.rules.AbstractElecRuleExceutor;
import com.server.edu.election.studentelec.rules.RulePriority;

import tk.mybatis.mapper.entity.Example;

/**
 * 按教学班选课
 * <p>
 * <p>
 * electableLessonByTeachClassFilter
 */
@Component("yjsElecByTeachClassRule")
public class ElecByTeachClassRule extends AbstractElecRuleExceutor {
    @Override
    public int getOrder() {
        return RulePriority.FIFTH.ordinal();
    }

    public ElecByTeachClassRule() {
        super();
    }

    @Autowired
    private TeachingClassElectiveRestrictAttrDao restrictAttrDao;
    
    @Autowired
    private ElectionRuleDao ectionRuleDao;
    
    @Autowired
    private ElectionParameterDao electionParameterDao;

    @Autowired
    private StudentDao studentDao;
    
//    private static final String NOT_DISTINGYISH_SEX = "0";//不区分性别
    private static final String MALE = "1";//男性
    private static final String FEMALE = "2";//女性

    @Override
    public boolean checkRule(ElecContext context,
                             TeachingClassCache courseClass) {
    	
    	
    	//教学班规则
    	Example electionRuleExample = new Example(ElectionRule.class);
    	String projectId = context.getRequest().getProjectId();
    	electionRuleExample.createCriteria().andEqualTo("serviceName", "yjsElecByTeachClassRule");
    	electionRuleExample.createCriteria().andEqualTo("managerDeptId", projectId);
        ElectionRule rule =
        		ectionRuleDao.selectOneByExample(electionRuleExample);
    	
    	
        //拿到教学班选课需要校验的小规则
    	 Example example = new Example(ElectionParameter.class);
         Example.Criteria criteria = example.createCriteria();
         criteria.andEqualTo("ruleId", rule.getId());
         List<ElectionParameter> list =
             electionParameterDao.selectByExample(example);
    	
        Long teachClassId = courseClass.getTeachClassId();
        StudentInfoCache studentInfo = context.getStudentInfo();
        Student student = studentDao.selectByPrimaryKey(studentInfo.getStudentId());

        //教学班限制的可选课学生集合
        List<String> stringList =
                restrictAttrDao.selectRestrictStudent(teachClassId);
        //教学班限制的专业
        List<SuggestProfessionDto> suggestProfessionDtos =
                restrictAttrDao.selectRestrictProfession(teachClassId);
        
        TeachingClassElectiveRestrictAttr record =
                new TeachingClassElectiveRestrictAttr();
        record.setTeachingClassId(teachClassId);
        
        //其他限制条件
        TeachingClassElectiveRestrictAttr restrictAttr =
                restrictAttrDao.selectOne(record);
        
        Boolean resultFlag = true;
        for (ElectionParameter electionParameter : list) {
        	
        	
        	//培养层次
        	if (Boolean.parseBoolean(electionParameter.getValue()) && StringUtils.equals("EDUCATION", electionParameter.getName()) ) {
        		if (restrictAttr != null) {
        			//培养层次
                    String trainingLevel = restrictAttr.getTrainingLevel();
                  //培养层次校验
                    if (trainingLevel != null && trainingLevel != "" ) {
                    	resultFlag = trainingLevel.equals(studentInfo.getTrainingLevel());
                    }
        		 }
        		if (!resultFlag) {
        			 ElecRespose respose = context.getRespose();
        	            respose.getFailedReasons()
        	                    .put(courseClass.getCourseCodeAndClassCode(),
        	                            I18nUtil.getMsg("ruleCheck.classLimit.trainingLevel"));
    	            return false;
        		}
			}
        	
        	//是否按教学班选课
        	if (Boolean.parseBoolean(electionParameter.getValue()) && StringUtils.equals("TEACHCLASS", electionParameter.getName()) ) {
        	}
        	
        	
        	//培养计划限制项（对教务管理上没有“行政班”这一概念的学校有用）
        	if (Boolean.parseBoolean(electionParameter.getValue()) && StringUtils.equals("PROGRAM", electionParameter.getName()) ) {
        	}
        	
        	
        	//如果专业校验为true，校验专业
        	if (Boolean.parseBoolean(electionParameter.getValue()) && StringUtils.equals("MAJOR", electionParameter.getName()) ) {
        		
        		if (restrictAttr != null) {
        			//培养层次
                    String profession = restrictAttr.getProfession();
                    String major = studentInfo.getMajor();
                  //培养层次校验
                    if (profession != null && profession != "" ) {
                    	resultFlag = (profession.contains(major));
                    }
        		 }
        		if (!resultFlag) {
        			ElecRespose respose = context.getRespose();
    	            respose.getFailedReasons()
    	                    .put(courseClass.getCourseCodeAndClassCode(),
    	                            I18nUtil.getMsg("ruleCheck.classLimit.major"));
    	            return false;
				}
        	}
        	//学生类别限制项
        	if (Boolean.parseBoolean(electionParameter.getValue()) && StringUtils.equals("STDTYPE", electionParameter.getName()) ) {
        		Boolean trainingCategoryFlag = true,degreeTypeFlag = true,formLearningFlag = true;
        		if (restrictAttr != null) {
	        		//培养类别
	                String trainingCategory = restrictAttr.getTrainingCategory();
	                //学位类型
	                String degreeType = restrictAttr.getDegreeType();
	                //学习形式
	                String formLearning = restrictAttr.getFormLearning();
	                
	                //学生类别校验
	                if (trainingCategory != null  && trainingCategory != "" ) {
	                	trainingCategoryFlag = trainingCategory.equals(student.getTrainingCategory());
	                }
	                if (degreeType != null && degreeType != "" ) {
	                	degreeTypeFlag = degreeType.equals(student.getDegreeType());
	                }
	                if (formLearning != null && formLearning != "" ) {
	                	formLearningFlag = formLearning.equals(student.getFormLearning());
	                }
        		}
        		resultFlag = trainingCategoryFlag && degreeTypeFlag && formLearningFlag;
                if (!resultFlag) {
                	ElecRespose respose = context.getRespose();
    	            respose.getFailedReasons()
    	                    .put(courseClass.getCourseCodeAndClassCode(),
    	                            I18nUtil.getMsg("ruleCheck.classLimit.stdtype"));
    	            return false;
				}
        	}
        	//如果年级校验开放，校验学生年级
        	if (Boolean.parseBoolean(electionParameter.getValue()) && StringUtils.equals("GRADE", electionParameter.getName()) ) {
        		
        		if (restrictAttr != null) {
	        		//年级
	                String grade = restrictAttr.getGrade();
	                Integer stugrade = studentInfo.getGrade();
	                //学生类别校验
	                //if ((grade != null && grade.contains(String.valueOf(stugrade))) || grade == null) {
	                if ((StringUtils.isNotBlank(grade) && grade.contains(String.valueOf(stugrade))) || StringUtils.isBlank(grade)) {
	                	resultFlag = true;
    				}else{
    					resultFlag = false;
    				}
        		}
        		
        		if (!resultFlag) {
        			ElecRespose respose = context.getRespose();
    	            respose.getFailedReasons()
    	                    .put(courseClass.getCourseCodeAndClassCode(),
    	                            I18nUtil.getMsg("ruleCheck.classLimit.grade"));
    	            return false;
				}
        	}
        	//教学班性别要求校验
        	if (Boolean.parseBoolean(electionParameter.getValue()) && StringUtils.equals("GENDER", electionParameter.getName()) ) {
        		 if (restrictAttr != null) {
        			//男女班
        	        String isDivsex = restrictAttr.getIsDivsex();
        	        String sex = String.valueOf(studentInfo.getSex());
        	        if (isDivsex != null && isDivsex != "") {
						if (StringUtils.equalsIgnoreCase(isDivsex, Constants.ZERO+"")) {
							resultFlag = true;
						}else{
							resultFlag = StringUtils.equalsIgnoreCase(isDivsex, sex);
						}
					}
        		 }
        		 if (!resultFlag) {
        			 ElecRespose respose = context.getRespose();
     	            respose.getFailedReasons()
     	                    .put(courseClass.getCourseCodeAndClassCode(),
     	                            I18nUtil.getMsg("ruleCheck.classLimit.isDivsex"));
     	            return false;
 				}
        	}
        	//学生学生方向限制项
        	if (Boolean.parseBoolean(electionParameter.getValue()) && StringUtils.equals("DIRECTION", electionParameter.getName()) ) {
        		
        	}
        	//学生专业所在院系限制项
        	if (Boolean.parseBoolean(electionParameter.getValue()) && StringUtils.equals("DEPARTMENT", electionParameter.getName()) ) {
        		if (restrictAttr != null) {
	        		//学院
	                String faculty = restrictAttr.getFaculty();
	                
	                //学生类别校验
	                if (faculty != null && faculty != "") {
	                	resultFlag = faculty.contains(studentInfo.getFaculty());
	                }
        		}
                if (!resultFlag) {
                	ElecRespose respose = context.getRespose();
    	            respose.getFailedReasons()
    	                    .put(courseClass.getCourseCodeAndClassCode(),
    	                            I18nUtil.getMsg("ruleCheck.classLimit.faculty"));
    	            return false;
				}
        	}
        	//学生行政班限制项
        	if (Boolean.parseBoolean(electionParameter.getValue()) && StringUtils.equals("ADMINCLASS", electionParameter.getName()) ) {
        		
        	}
		}

        if (resultFlag) {
            return true;
        } else {
            ElecRespose respose = context.getRespose();
            respose.getFailedReasons()
                    .put(courseClass.getCourseCodeAndClassCode(),
                            I18nUtil.getMsg("ruleCheck.classLimit"));
            return false;
        }

    }

}