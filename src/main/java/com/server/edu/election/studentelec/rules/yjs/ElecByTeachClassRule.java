package com.server.edu.election.studentelec.rules.yjs;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.server.edu.common.locale.I18nUtil;
import com.server.edu.election.constants.Constants;
import com.server.edu.election.dao.ElcCourseTakeDao;
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
import com.server.edu.util.CollectionUtil;

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
    private ElcCourseTakeDao takeDao;
    
    @Autowired
    private StudentDao studentDao;

    private static final String IS_OVERSEAS_ = "1";
    private static final String IS_NOT_OVERSEAS_ = "0";
//    private static final String NOT_DISTINGYISH_SEX = "0";//不区分性别
    private static final String MALE = "1";//男性
    private static final String FEMALE = "2";//女性

    @Override
    public boolean checkRule(ElecContext context,
                             TeachingClassCache courseClass) {
    	
    	
    	//教学班规则
    	Example electionRuleExample = new Example(ElectionRule.class);
    	electionRuleExample.createCriteria().andEqualTo("serviceName", "yjsElecByTeachClassRule");
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
        		if (CollectionUtil.isNotEmpty(suggestProfessionDtos)) {
        			String major = studentInfo.getMajor();
        			for (SuggestProfessionDto suggestProfessionDto : suggestProfessionDtos) {
        				if ( suggestProfessionDto.getProfession().equals(major)) {
        					resultFlag = true;
        				}else{
        					resultFlag = false;
        					break;
        				}
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
	                	formLearningFlag = degreeType.equals(student.getDegreeType());
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
	                Integer grade = restrictAttr.getGrade();
	                Integer stugrade = studentInfo.getGrade();
	                //学生类别校验
	                if (grade.intValue() != 0 && grade.intValue() == stugrade
    						.intValue()) {
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
	                	resultFlag = faculty.equals(studentInfo.getFaculty());
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
        //教学班可选名单内校验
        /*if (CollectionUtil.isNotEmpty(stringList)) {
            if (stringList.contains(studentInfo.getStudentId())) {
                return true;
            }
        }*/
        
        //专业限制
/*        if (CollectionUtil.isNotEmpty(suggestProfessionDtos))
        {
            Integer grade = studentInfo.getGrade();
            String major = studentInfo.getMajor();
            for (SuggestProfessionDto suggestProfessionDto : suggestProfessionDtos)
            {
                if (suggestProfessionDto.getGrade().intValue() == grade
                    .intValue()
                    && suggestProfessionDto.getProfession().equals(major))
                {
                    return true;
                }
            }
        }*/

        /*if (restrictAttr != null) {
        	//是否留学限制
            String isOverseas = restrictAttr.getIsOverseas();
            //培养层次
            String trainingLevel = restrictAttr.getTrainingLevel();
            //专项计划
            String spcialPlan = restrictAttr.getSpcialPlan();
            //男女班
            String isDivsex = restrictAttr.getIsDivsex();
            //男生人数 1
            Integer numberMale = restrictAttr.getNumberMale();
            //女生人数 2
            Integer numberFemale = restrictAttr.getNumberFemale();
            boolean flag = false;
            if (isOverseas != null) {
                String s = studentInfo.isAboard() ? IS_OVERSEAS_ : IS_NOT_OVERSEAS_;
                flag = isOverseas.equals(s);
            }
            //培养层次校验
            if (trainingLevel != null) {
                flag = trainingLevel.equals(studentInfo.getTrainingLevel());
            }

            if (spcialPlan != null) {
                flag = spcialPlan.equals(studentInfo.getSpcialPlan());
            }

            if (isDivsex != null) {
                ElcCourseLimitDto sexNumber = takeDao.findSexNumber(teachClassId);
                String sex = String.valueOf(studentInfo.getSex());//当前学生性别
                int currentNum = 0;
                if (sexNumber==null) {//当前还没有选课人数
                    currentNum = 0;
                } else {
                    if(sex.equals(MALE)&&sexNumber.getMaleNum()!=null){
                        currentNum=sexNumber.getMaleNum();
                    }
                    if(sex.equals(FEMALE)&&sexNumber.getFeMaleNum()!=null){
                        currentNum=sexNumber.getFeMaleNum();
                    }
                }

                int limitNumber = 0;
                if (sex.equals(MALE)) {//男
                    limitNumber = numberMale;
                } else {
                    limitNumber = numberFemale;
                }
                if (currentNum + Constants.ONE <= limitNumber) {
                    flag = true;
                } else {
                    flag = false;
                }
            }


            if (flag == true) {
                return flag;
            }
        }*/

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