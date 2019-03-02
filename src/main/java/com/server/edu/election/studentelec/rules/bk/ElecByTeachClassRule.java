package com.server.edu.election.studentelec.rules.bk;

import org.springframework.stereotype.Component;

import com.server.edu.election.studentelec.context.ElecContext;
import com.server.edu.election.studentelec.cache.TeachingClassCache;
import com.server.edu.election.studentelec.rules.AbstractRuleExceutor;
import com.server.edu.election.studentelec.rules.RulePriority;

/**
 * 按教学班选课
 */
@Component("ElecByTeachClassRule")
public class ElecByTeachClassRule extends AbstractRuleExceutor {

	// 是否按教学班选课
	private static final String IF_ELECT_BY_TEACHCLASS = "TEACHCLASS";

	// 学生专业限制项
	private static final String RULE_PARAM_MAJOR = "MAJOR";

	// 学生行政班限制项
	private static final String RULE_PARAM_ADMINCLASS = "ADMINCLASS";

	// 学生性别限制项
	private static final String RULE_PARAM_GENDER = "GENDER";

	// 学生年级限制项
	private static final String RULE_PARAM_GRADE = "GRADE";

	// 学生类别限制项
	private static final String RULE_PARAM_STDTYPE = "STDTYPE";

	// 学生学生方向限制项
	private static final String RULE_PARAM_DIRECTION = "DIRECTION";

	// 学历层次
	private static final String RULE_PARAM_EDUCATION = "EDUCATION";

	// 培养计划限制项（对教务管理上没有“行政班”这一概念的学校有用）
	private static final String RULE_PARAM_PROGRAM = "PROGRAM";

	// 学生专业所在院系限制项
	private static final String RULE_PARAM_DEPARTMENT = "DEPARTMENT";

	// 是否新生选课
//	private static final String RULE_PARAM_IFNEW = "IFNEW";

	@Override
	public int getOrder() {
		return RulePriority.FIFTH.ordinal();
	}

	public ElecByTeachClassRule() {
		super();
	}

	@Override
	public boolean checkRule(ElecContext context, TeachingClassCache courseClass) {
//		if (CollectionUtils.isNotEmpty(state.getUnlimitCourses()) && state.getUnlimitCourses().contains(lesson.getCourse())) {
//		  return true;
//		}
//		
//		/*// 如果是重修，且重修不检查教学班，那么学生肯定能选这个课
//		if (retakeService.isRetakeCourse(state, lesson.getCourse().getId())
//		        && !retakeService.isCheckTeachClass(state.getProfile(entityDao).getElectConfigs())) {
//			return true;
//		}
//		
//		// 只有当不是重修，或者重修时也检查教学班的时候才需要检
//		if(CollectionUtils.isEmpty(lesson.getTeachClass().getLimitGroups())) {
//			return true;
//		}*/
//		
//		
//		List<CourseLimitGroup> groups = lesson.getTeachClass().getLimitGroups();
//		
//	/*	if(null == state.getParams().get(RULE_PARAM_IFNEW)){
//			Set<RuleConfigParam> params = getParams(state.getProfile(entityDao).getElectConfigs());
//		}*/
//		
//		
//		
//		//当没有设定教学任务的时候，可以任意的选择
//		if (groups.size() < 1) {
//			return true;
//		}
//	
//		if(null == state.getParams().get(RULE_PARAM_MAJOR)){
//			Set<RuleConfigParam> params = getParams(state.getProfile(entityDao).getElectConfigs());
//			for(RuleConfigParam param : params) {
//				if(RULE_PARAM_MAJOR.equals(param.getParam().getName())) {
//					boolean major = Boolean.valueOf((String)param.getValue());
//					state.getParams().put(RULE_PARAM_MAJOR, major);
//					
//				} else if (RULE_PARAM_ADMINCLASS.equals(param.getParam().getName())) {
//					boolean adminClass = Boolean.valueOf((String)param.getValue());
//					state.getParams().put(RULE_PARAM_ADMINCLASS, adminClass);
//					
//				}else if (RULE_PARAM_GENDER.equals(param.getParam().getName())) {
//					boolean gender = Boolean.valueOf((String)param.getValue());
//					state.getParams().put(RULE_PARAM_GENDER, gender);
//					
//				}else if (RULE_PARAM_GRADE.equals(param.getParam().getName())) {
//					boolean grade = Boolean.valueOf((String)param.getValue());
//					state.getParams().put(RULE_PARAM_GRADE, grade);
//					
//				}else if (RULE_PARAM_STDTYPE.equals(param.getParam().getName())) {
//					boolean stdType = Boolean.valueOf((String)param.getValue());
//					state.getParams().put(RULE_PARAM_STDTYPE, stdType);
//					
//				}else if (RULE_PARAM_DIRECTION.equals(param.getParam().getName())) {
//					boolean direction = Boolean.valueOf((String)param.getValue());
//					state.getParams().put(RULE_PARAM_DIRECTION, direction);
//					
//				}else if (RULE_PARAM_EDUCATION.equals(param.getParam().getName())) {
//					boolean education = Boolean.valueOf((String)param.getValue());
//					state.getParams().put(RULE_PARAM_EDUCATION, education);
//					
//				}else if (RULE_PARAM_PROGRAM.equals(param.getParam().getName())) {
//					boolean program = Boolean.valueOf((String)param.getValue());
//					state.getParams().put(RULE_PARAM_PROGRAM, program);
//					
//				}else if (RULE_PARAM_DEPARTMENT.equals(param.getParam().getName())) {
//					boolean department = Boolean.valueOf((String)param.getValue());
//					state.getParams().put(RULE_PARAM_DEPARTMENT, department);
//				}else if (IF_ELECT_BY_TEACHCLASS.equals(param.getParam().getName())){
//					boolean teachClass = Boolean.valueOf((String)param.getValue());
//					state.getParams().put(IF_ELECT_BY_TEACHCLASS, teachClass);
//				}
//				
//			}
//		}
//		
//		Boolean major      = (Boolean)state.getParams().get(RULE_PARAM_MAJOR);
//		Boolean adminClass = (Boolean)state.getParams().get(RULE_PARAM_ADMINCLASS);
//		Boolean gender     = (Boolean)state.getParams().get(RULE_PARAM_GENDER);
//		Boolean grade      = (Boolean)state.getParams().get(RULE_PARAM_GRADE);
//		Boolean stdType    = (Boolean)state.getParams().get(RULE_PARAM_STDTYPE);
//		Boolean direction  = (Boolean)state.getParams().get(RULE_PARAM_DIRECTION);
//		Boolean education  = (Boolean)state.getParams().get(RULE_PARAM_EDUCATION);
//		Boolean program    = (Boolean)state.getParams().get(RULE_PARAM_PROGRAM);
//		Boolean department = (Boolean)state.getParams().get(RULE_PARAM_DEPARTMENT);
//		Boolean teachClass = (Boolean)state.getParams().get(IF_ELECT_BY_TEACHCLASS);
//		
//		
//	/*	//当没有设定教学任务的时候，可以任意的选择
//		if(ifnew){							//如果是新生选课，没有limitgroup 就不出现
//			return false;
//		}else if(groups.size()<1){			//如果不是新生，没有limitgroup 就出现
//			return true;
//		}
//		*/
//		boolean atLast=false;
//		for (CourseLimitGroup group : groups) {
//			 atLast = false;
//			boolean groupPass = true;
//			if(!teachClass && group.isForClass()){
//				atLast = true;
//				continue;
//			}
//			for (CourseLimitItem item : group.getItems()) {
//				if(item == null) {
//					continue;
//				}
//				boolean itemPass = true;
//
//				Operator op = item.getOperator();
//				Long metaId = item.getMeta().getId();
//				Set<String> values = CollectUtils.newHashSet(item.getContent().split(","));
//				String value = null;
//				String value1 = null;
//				if (metaId.equals(CourseLimitMeta.ADMINCLASS)) {
//					if(adminClass){
//						value = state.getStd().getAdminclassId() + "";
//					}else{
//						continue;
//					}
//				} else if (metaId.equals(CourseLimitMeta.DEPARTMENT)) {
//					if(department){
//						value = state.getStd().getDepartId() + "";
//					}else{
//						continue;
//					}
//				} else if (metaId.equals(CourseLimitMeta.DIRECTION)) {
//					if(direction){
//						value = state.getStd().getDirectionId() + "";
//					}else{
//						continue;
//					}
//				} else if (metaId.equals(CourseLimitMeta.EDUCATION)) {
//					if(education){
//						value = state.getStd().getEducationId() + "";
//					}else{
//						continue;
//					}
//				} else if (metaId.equals(CourseLimitMeta.GENDER)) {
//					if(gender){
//						value = state.getStd().getGenderId() + "";
//					}else{
//						continue;
//					}
//				} else if (metaId.equals(CourseLimitMeta.GRADE)) {
//					if(grade){
//						value = state.getStd().getGrade();
//					}else{
//						continue;
//					}
//				} else if (metaId.equals(CourseLimitMeta.MAJOR)) {
//					if(major){
//						Major majorr = entityDao.get(Major.class, state.getStd().getMajorId());
//						if(null != majorr&& null != majorr.getMajorLei()){
//							value1 = majorr.getMajorLei().getId() + "";
//						}
//						value = state.getStd().getMajorId() + "";
//					}else{
//						continue;
//					}
//				} else if (metaId.equals(CourseLimitMeta.STDTYPE)) {
//					if(stdType){
//						value = state.getStd().getStdTypeId() + "";
//					}else{
//						continue;
//					}
//				} else if (metaId.equals(CourseLimitMeta.PROGRAM)) {
//					if(program){
//						value = state.getStd().getProgramId() + "";
//					}else{
//						continue;
//					}
//				}
//
//				if (op.equals(Operator.EQUAL) || op.equals(Operator.IN)) {
//					itemPass = values.isEmpty() || values.contains(value) || values.contains(value1);
//				} else {
//					itemPass = !values.isEmpty() && !values.contains(value);
//				}
//				// 如果单项没通过，那么就整个组就不通过了
//				if (!itemPass) {
//					groupPass = false;
//					
//					break;
//				}
//			}
//			
//			
//			if((group.isForClass()  && !atLast)){
//				atLast = groupPass;
//			}else if(!group.isForClass()){
//				atLast = groupPass;
//			}
//			if(!group.isForClass()){
//				if (!groupPass) {
//					break;
//				}
//			}
////			if(group.isForClass()){
////				if (groupPass) {
////					break;
////				}
////			}
//			
//				
//			// 只要有一个组不通过，那么就算不通过了 TODO
//			//FIX 只要一个组通过， 就算通过。
//					
//					
//		}
//		// 如果没有在循环里return，那么就说明学生不适合这个任务
//		return atLast;
//	    if (!result) {
//            context.addMessage(new ElectMessage("只开放给:" 
//                    + "通过选课限制的学生ElectableLessonByTeachClassFilter", ElectRuleType.ELECTION, false, context.getLesson()));
//        }
		return false;
	}

}