package com.server.edu.election.studentelec.rules.bk;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.github.pagehelper.PageInfo;
import com.server.edu.common.PageCondition;
import com.server.edu.common.locale.I18nUtil;
import com.server.edu.election.constants.Constants;
import com.server.edu.election.dao.ElectionConstantsDao;
import com.server.edu.election.dto.ElcStudentLimitDto;
import com.server.edu.election.entity.ElectionConstants;
import com.server.edu.election.service.ElcCourseTakeService;
import com.server.edu.election.service.ElcStudentLimitService;
import com.server.edu.election.studentelec.cache.StudentInfoCache;
import com.server.edu.election.studentelec.cache.TeachingClassCache;
import com.server.edu.election.studentelec.context.ElecRequest;
import com.server.edu.election.studentelec.context.ElecRespose;
import com.server.edu.election.studentelec.context.bk.ElecContextBk;
import com.server.edu.election.studentelec.context.bk.SelectedCourse;
import com.server.edu.election.studentelec.rules.AbstractElecRuleExceutorBk;
import com.server.edu.election.studentelec.rules.RulePriority;
import com.server.edu.election.studentelec.utils.RetakeCourseUtil;
import com.server.edu.election.vo.ElcStudentLimitVo;
import com.server.edu.util.CollectionUtil;

import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.entity.Example.Criteria;

/**
 * filter，过滤掉学生不能选的任务（校区不对），使得学生在界面上看不见
 *CampusFilter
 */
@Component("CreditLiMitFor2018AndBeyondRule")
public class CreditLiMitFor2018AndBeyondRule extends AbstractElecRuleExceutorBk
{
	@Autowired
	private ElcStudentLimitService elcStudentLimitService;
	
	@Autowired
	private ElcCourseTakeService elcCourseTakeService;
	
    @Autowired
    private ElectionConstantsDao electionConstantsDao;
	
    final String MAXRETAKECOURSECOUNT = "MAXRETAKECOURSECOUNT";
    
    final String MAXNEWCREDITS = "MAXNEWCREDITS";
    
    @Override
    public int getOrder()
    {
        return RulePriority.FIRST.ordinal();
    }
    
    @Override
    public boolean checkRule(ElecContextBk context,
        TeachingClassCache courseClass)
    {
    	//学生信息
        StudentInfoCache studentInfo = context.getStudentInfo();
        
        //请求参数
        ElecRequest request = context.getRequest();
        
        //已选课程
        Set<SelectedCourse> selectedCourses = context.getSelectedCourses();
     
        //学生本学期已经重修的门数
        Integer retakeNumber = elcCourseTakeService.getRetakeNumber(studentInfo.getStudentId(),request.getCalendarId());
        
        boolean isReTakeCourse = RetakeCourseUtil.isRetakeCourseBk(context,
        		courseClass.getCourseCode());
        //选择该课程之后的重修门数
        if (isReTakeCourse) {
        	retakeNumber = retakeNumber + 1;
        }
        //学生新选学分
        Double sumCredits = 0d;
        for (SelectedCourse selectedCourse : selectedCourses) {
        	Double credits = selectedCourse.getCourse().getCredits();
        	sumCredits = sumCredits + credits;
		}
        
        //选择该课程之后的新选学分
        sumCredits = sumCredits + courseClass.getCredits();
        
        //学生年级
        Integer grade = studentInfo.getGrade();
        if (grade.intValue() >= Constants.GRADE) {
			//查询个选限制(根据学生Id查询个选限制)
        	PageCondition<ElcStudentLimitDto> condition = new PageCondition<>();
        	ElcStudentLimitDto elcStudentLimitDto = new ElcStudentLimitDto();
        	elcStudentLimitDto.setCalendarId(request.getCalendarId());
        	elcStudentLimitDto.setStudentId(studentInfo.getStudentId());
        	condition.setCondition(elcStudentLimitDto);
        	PageInfo<ElcStudentLimitVo> limitStudents = elcStudentLimitService.getLimitStudents(condition);
        	if (CollectionUtil.isNotEmpty(limitStudents.getList())) {
				//判断学生个选限制是否满足要求
        		ElcStudentLimitVo elcStudentLimitVo = limitStudents.getList().get(0);
        		
        		if (sumCredits.doubleValue() > elcStudentLimitVo.getNewLimitCredits()) {
        			 ElecRespose respose = context.getRespose();
        	            respose.getFailedReasons()
        	                .put(courseClass.getCourseCodeAndClassCode(),
        	                    I18nUtil.getMsg("ruleCheck.creditLiMit.newLimitCredits"));
        			return false;
				} else if ( retakeNumber  > elcStudentLimitVo.getRebuildLimitNumber()) {
					ElecRespose respose = context.getRespose();
					respose.getFailedReasons()
						.put(courseClass.getCourseCodeAndClassCode(),
							I18nUtil.getMsg("ruleCheck.creditLiMit.rebuildLimitNumber"));
					return false;
				}else{
					return true;
				}
			}else{
				//如果没有个选限制，查询选课常量
				 Example example = new Example(ElectionConstants.class);
				 Criteria createCriteria = example.createCriteria();
				 createCriteria.andEqualTo("key",MAXRETAKECOURSECOUNT);
				 createCriteria.andEqualTo("managerDeptId",request.getProjectId());
				 ElectionConstants selectOneByExample = electionConstantsDao.selectOneByExample(example);
				 
				 Example example1 = new Example(ElectionConstants.class);
				 Criteria createCriteria1 = example.createCriteria();
				 createCriteria1.andEqualTo("key",MAXNEWCREDITS);
				 createCriteria1.andEqualTo("managerDeptId",request.getProjectId());
				 ElectionConstants selectOneByExample1 = electionConstantsDao.selectOneByExample(example1);
				 
				 
				 if (sumCredits.doubleValue() > Double.parseDouble(selectOneByExample.getValue())) {
        			 ElecRespose respose = context.getRespose();
        	            respose.getFailedReasons()
        	                .put(courseClass.getCourseCodeAndClassCode(),
        	                    I18nUtil.getMsg("ruleCheck.creditLiMit.newLimitCredits"));
        			return false;
				} else if ( retakeNumber  > Integer.parseInt(selectOneByExample1.getValue())) {
					ElecRespose respose = context.getRespose();
					respose.getFailedReasons()
						.put(courseClass.getCourseCodeAndClassCode(),
							I18nUtil.getMsg("ruleCheck.creditLiMit.rebuildLimitNumber"));
					return false;
				}else{
					return true;
				}
			}
		}
        return true;
    }
    
}