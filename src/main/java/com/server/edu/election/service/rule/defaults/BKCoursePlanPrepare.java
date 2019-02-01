package com.server.edu.election.service.rule.defaults;

import com.server.edu.election.service.rule.context.AbstractElectRuleExecutor;
import com.server.edu.election.service.rule.context.ElectState;

/**
 * 同济本科计划准备
 * <ul>
 * <li>添加所有学期的课程到计划课程组中
 * <li>对体育课有特别处理，体育课的选课是完全按照计划来的，不能提前也不能滞后
 * </ul>
 * 
 */
public class BKCoursePlanPrepare extends AbstractElectRuleExecutor {

//	protected EntityDao entityDao;
//
//	protected PublicCourseDataProvider publicCourseDataProvider;

	@Override
    public void prepare(ElectState context) {
//		if (context.isPreparedData(PreparedDataName.COURSE_PLAN)) return;
//		ElectState state = context.getState();
//		// 提取体育课程代码
//		String constant = publicCourseDataProvider.getConstants(state.getStd().getProjectId()).get(
//		        ElectionConstantKey.PE_COURSE_CODES);
//		Set<String> PECourseCodes = null;
//		if (null != constant) {
//			PECourseCodes = CollectUtils.newHashSet(StringUtils.split(constant, ","));
//		} else {
//			PECourseCodes = Collections.emptySet();
//		}
//		state.getParams().put("PECourseCodes", PECourseCodes);
//		// 检查学生是否 选体育课时不受一门限制
//		List<PeFreeStd> peFreeStds = entityDao.get(PeFreeStd.class, "std.id", state.getStd().getId());
//		if (CollectionUtils.isNotEmpty(peFreeStds)) {
//			state.getParams().put("PEFree", true);
//		}
//		/*super.prepare(context);
//		state.getParams().remove("PECourseCodes");*/
//		
//		if (context.isPreparedData(PreparedDataName.COURSE_PLAN)) return;
//		currentTermPrepare.prepare(context);
//
//		CoursePlan plan = context.getPlan();
//		// 选课计划
//		ElectCoursePlan electPlan = state.getCoursePlan();
//		if (null == electPlan) {
//			electPlan = new ElectCoursePlan();
//			context.getState().setCoursePlan(electPlan);
//		}
//		
//		//找出长学制学生id
//		Long stdId = context.getStudent().getId();
//		Long semesterId = context.getProfile().getSemester().getId();
//		OqlBuilder<Long> query = OqlBuilder.from(NoGraduateStd.class.getName(),"noGraduateStd");
//		query.select("noGraduateStd.std.id");
//		query.where( "noGraduateStd.invalid = true" );
//		List<Long> stdIds = entityDao.search(query);
//		
//		OqlBuilder<Long> query2 = OqlBuilder.from(AbroadStd.class.getName(),"abroadStd");
//		query2.select("abroadStd.std.id");
//		query2.where("abroadStd.invalid = true");
//		stdIds.addAll(entityDao.search(query2));
//				
//		
//		
//		// 根据培养计划设定courseIds(course.id->courseType.id的map)
//		if (null != plan) {
//			List<CourseGroup> groups = getTopCourseGroups(plan);
//			for (CourseGroup group : groups) {
//				addGroup(group, electPlan, null, state,stdIds);
//			}
//		}
//		
//		
//		if(stdIds.contains(stdId)){
//			Student student = entityDao.get(Student.class, stdId);
//			//长学制替换课程
//			OqlBuilder<NoGradCouSub> query1 = OqlBuilder.from(NoGradCouSub.class,"noGradCouSub");
//			query1.where("noGradCouSub.project = :project",student.getProject());
//			query1.where("noGradCouSub.semester.id = :semesterId",semesterId);
//			List<NoGradCouSub> noGradCouSubs = entityDao.search(query1);
//			
//			for (NoGradCouSub noGradCouSub : noGradCouSubs) {
//				Long courseTypeId = null;
//				ElectCourseSubstitution electCourseSubstitution = new ElectCourseSubstitution();
//				for (Course course : noGradCouSub.getOrigins()) {
//					electCourseSubstitution.getOrigins().add(course.getId());
//					if (null != courseTypeId) {
//						continue;
//					} else {
//						courseTypeId = electPlan.courseIds.get(course.getId());
//					}
//				}
//				if (null != courseTypeId) {
//					for (Course course : noGradCouSub.getSubstitutes()) {
//						electCourseSubstitution.getSubstitutes().add(course.getId());
//						electPlan.courseIds.put(course.getId(), courseTypeId);
//					}
//				} else {
//					for (Course course : noGradCouSub.getSubstitutes()) {
//						electCourseSubstitution.getSubstitutes().add(course.getId());
//					}
//				}
//				state.addCourseSubsititution(electCourseSubstitution);
//			}
//			context.addPreparedDataName(PreparedDataName.COURSE_PLAN);
//			state.getParams().remove("PECourseCodes");
//		}else{
//			
//			// 添加替代课程
//			List<CourseSubstitution> substitutions = courseSubstitutionService.getCourseSubstitutions(context.getStudent());
//			for (CourseSubstitution substitution : substitutions) {
//				Long courseTypeId = null;
//				ElectCourseSubstitution electCourseSubstitution = new ElectCourseSubstitution();
//				for (Course course : substitution.getOrigins()) {
//					electCourseSubstitution.getOrigins().add(course.getId());
//					if (null != courseTypeId) {
//						continue;
//					} else {
//						courseTypeId = electPlan.courseIds.get(course.getId());
//					}
//				}
//				if (null != courseTypeId) {
//					for (Course course : substitution.getSubstitutes()) {
//						electCourseSubstitution.getSubstitutes().add(course.getId());
//						electPlan.courseIds.put(course.getId(), courseTypeId);
//					}
//				} else {
//					for (Course course : substitution.getSubstitutes()) {
//						electCourseSubstitution.getSubstitutes().add(course.getId());
//					}
//				}
//				state.addCourseSubsititution(electCourseSubstitution);
//			}
//			context.addPreparedDataName(PreparedDataName.COURSE_PLAN);
//			state.getParams().remove("PECourseCodes");
//		}

	}

//	protected void addGroup(CourseGroup group, ElectCoursePlan electPlan, ElectCourseGroup parent,
//	        ElectState state,List<Long> stdIds) {
//		Boolean isShowByTerm = true;
//		RuleConfigParam ifTrueParam = uniqueParam(state.getProfile(entityDao).getElectConfigs());
//		if(null !=ifTrueParam){
//			isShowByTerm = Boolean.valueOf((String)ifTrueParam.getValue());
//		}
//		Long id = group.getCourseType().getId();
//		
//		TJElectCourseGroup electGroup = (TJElectCourseGroup) electPlan.groups.get(group.getCourseType().getId());
//		if(null == electGroup){
//			electGroup = new TJElectCourseGroup(group.getCourseType());
//			electGroup.setParent(parent);
//			electPlan.addGroup(electGroup);
//		}
//		for (CourseGroup childGroup : group.getChildren()) {
//			addGroup(childGroup, electPlan, electGroup, state,stdIds);
//		}
//		@SuppressWarnings("unchecked")
//        Set<String> PECourseCodes = (Set<String>) state.getParams().get("PECourseCodes");
//		Integer term = (Integer) state.getParams().get("CURRENT_TERM");
//		for (PlanCourse planCourse : group.getPlanCourses()) {
//			Long courseId = planCourse.getCourse().getId();
//			String courseCode = planCourse.getCourse().getCode();
//			electPlan.courseIds.put(courseId, group.getCourseType().getId());
//			// 如果是体育课，而学生不是任意选体育课的学生，那么就要判断下体育课的开课学期
//			if (!Boolean.TRUE.equals(state.getParams().get("PEFree")) && PECourseCodes.contains(courseCode)) {
//				// 如果这个体育课是重修的，那么不必判断他的学期
//				if (state.isRetakeCourse(courseId)) {
//					electGroup.addPlanCourse(planCourse);
//				} else {
//					if(isShowByTerm){
//						if (TermCalculator.inTerm(planCourse.getTerms(), term)) {//TODO 获取页面参数来决定该判断是否执行
//							electGroup.addPlanCourse(planCourse);
//						}else if(stdIds.contains(state.getStd().getId())){
//							electGroup.addPlanCourse(planCourse);
//						}
//					}else{
//						electGroup.addPlanCourse(planCourse);
//					}
//				}
//			} else {
//				electGroup.addPlanCourse(planCourse);
//			}
//		}
//	}
//	
//	protected final List<CourseGroup> getTopCourseGroups2(CoursePlan plan) {
//		if (plan.getGroups() == null) { return new ArrayList<CourseGroup>(); }
//		List<CourseGroup> res = new ArrayList<CourseGroup>();
//		for (CourseGroup group : plan.getGroups()) {
//			if (group != null && group.getParent() == null) {
//				res.add((CourseGroup) group);
//			}
//		}
//		return res;
//	}
//	
//	protected Set<RuleConfigParam> getParams(Collection<? extends RuleConfig> configs){
//		String serviceName = "onepecoursechecker";
//		for (RuleConfig config : configs) {
//			//System.out.println(config.getRule().getServiceName().toLowerCase()+"和"+serviceName);
//			if(config.getRule().getServiceName().toLowerCase().equals(serviceName)){
//				return config.getParams();
//			}
//		}
//		return Collections.emptySet();
//	}
//	
//
//	protected Iterator<RuleConfigParam> iteratorParams(Collection<? extends RuleConfig> configs){
//		return getParams(configs).iterator();
//	} 
//	
//	protected RuleConfigParam uniqueParam(Collection<? extends RuleConfig> configs){
//		Iterator<RuleConfigParam> it = iteratorParams(configs);
//		return it.hasNext() ? it.next() : null;
//	} 

}