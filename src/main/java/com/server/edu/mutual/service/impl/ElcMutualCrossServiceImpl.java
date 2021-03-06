package com.server.edu.mutual.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.server.edu.common.entity.Department;
import com.server.edu.common.rest.RestResult;
import com.server.edu.mutual.entity.ElcCrossStdVo;
import com.server.edu.mutual.entity.ElcMutualStdVo;
import com.server.edu.mutual.service.ElcMutualCommonService;
import com.server.edu.mutual.util.ProjectUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.Transient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.server.edu.common.PageCondition;
import com.server.edu.common.locale.I18nUtil;
import com.server.edu.common.vo.SchoolCalendarVo;
import com.server.edu.dictionary.utils.SchoolCalendarCacheUtil;
import com.server.edu.election.constants.Constants;
import com.server.edu.election.dao.StudentDao;
import com.server.edu.election.entity.Student;
import com.server.edu.exception.ParameterValidateException;
import com.server.edu.mutual.dao.ElcCrossStdsDao;
import com.server.edu.mutual.dao.ElcMutualStdsDao;
import com.server.edu.mutual.dto.ElcMutualCrossStu;
import com.server.edu.mutual.dto.ElcMutualCrossStuDto;
import com.server.edu.mutual.entity.ElcCrossStds;
import com.server.edu.mutual.entity.ElcMutualStds;
import com.server.edu.mutual.service.ElcMutualCrossService;
import com.server.edu.mutual.vo.ElcMutualCrossStuVo;
import com.server.edu.session.util.SessionUtils;
import com.server.edu.session.util.entity.Session;
import com.server.edu.util.CollectionUtil;

import tk.mybatis.mapper.entity.Example;
/**
 * @author xlluoc
 *
 */
@Service
public class ElcMutualCrossServiceImpl implements ElcMutualCrossService {
	private static Logger LOG =
			LoggerFactory.getLogger(ElcMutualCrossServiceImpl.class);
	@Autowired
	private ElcMutualStdsDao elcMutualStdsDao;
	@Autowired
	private ElcCrossStdsDao elcCrossStdsDao;
	@Autowired
	private StudentDao studentDao;
	@Autowired
	private ElcMutualCommonService elcMutualCommonService;
	
	@Override
	public PageInfo<ElcMutualCrossStuVo> getElcMutualCrossList(PageCondition<ElcMutualCrossStuDto> condition) {
		Session session = SessionUtils.getCurrentSession();
		boolean isAcdemicDean = StringUtils.equals(session.getCurrentRole(), String.valueOf(Constants.ONE))
				&& !session.isAdmin() && session.isAcdemicDean();

		//判断是否是教务员，如果是进行下列操作  
		if(isAcdemicDean){
			//判断是否选择学院(有的前端接口会上送学院字段为筛选条件，有的前端接口不会上送)
			String facultyCondition = condition.getCondition().getFaculty();
			if(StringUtils.isEmpty(facultyCondition)){
				//获取当前用户的所属学院
				String faculty = session.getFaculty();
				//获取当前用户的管理学院和
				String manageFaculty = session.getManageFaculty();
				//有管理学院以管理学院为准，没有管理学院以所属学院为准
				if(StringUtils.isNotEmpty(manageFaculty)){
					faculty = manageFaculty;
				}
				condition.getCondition().setFaculty(faculty);
			}
		}

		PageHelper.startPage(condition.getPageNum_(), condition.getPageSize_());
		ElcMutualCrossStuDto dto = condition.getCondition();
		dto.setLeaveSchool(Constants.INSCHOOL);
		
		List<ElcMutualCrossStuVo> list = new ArrayList<ElcMutualCrossStuVo>();
		if(Constants.BK_CROSS.equals(dto.getMode())) {
			list = elcCrossStdsDao.getCrossStds(dto);
		}else {
			list = elcMutualStdsDao.getMutualStds(dto);
		}
		PageInfo<ElcMutualCrossStuVo> pageInfo = new PageInfo<ElcMutualCrossStuVo>(list);
		return pageInfo;
	}
	
	@Transactional
	@Override
	public int init(Long calendarId) {
		int result = Constants.ZERO;
		SchoolCalendarVo schoolCalendarVo =SchoolCalendarCacheUtil.getCalendar(calendarId);
		if(schoolCalendarVo==null) {
			throw new ParameterValidateException(I18nUtil.getMsg("election.calendarRedisError")); 
		}
		List<String> studentIds = new ArrayList<>();
		if(Constants.FIRST.equals(schoolCalendarVo.getTerm())) {
			// 秋季学期：毕业班专业排名前10%的学生可以申请，使用符合条件的本科生名单初始化学生信息
			
		}else {
			// 春季学期：保研的学生可以申请，使用保研学生名单初始化学生信息
			
		}
		return result;
	}

	@Transactional
	@Override
	public int add(Long calendarId, String studentIds, Integer mode) {
		String studentIdsStr = "";
		if (StringUtils.isNotBlank(studentIds)) {
			studentIdsStr = studentIds.replaceAll("，", ",");
		}
		Set<String> studentIdSet = new HashSet<String>(Arrays.asList(studentIdsStr.split(",")));
		List<String> studentIdList =new ArrayList<>(studentIdSet);
		checkStuIsLeaveSchool(studentIdList);
		int result = saveElcMutualCross(calendarId, mode, studentIdList);
		return result;
	}

	private int saveElcMutualCross(Long calendarId, Integer mode, List<String> studentIdList) {
		int result = Constants.ZERO;
		Session session = SessionUtils.getCurrentSession();

		Example stuExample = new Example(Student.class);
		Example.Criteria stuCriteria = stuExample.createCriteria();
		stuExample.selectProperties("studentCode");
		stuCriteria.andIn("studentCode", studentIdList);
		stuCriteria.andEqualTo("managerDeptId", session.getCurrentManageDptId());
		stuCriteria.andEqualTo("leaveSchool", Constants.INSCHOOL);
		if (isDepartAdmin()) {
			//修改说明：当前教务员除了当前所属学院还管理其他学院
//			stuCriteria.andEqualTo("faculty", session.getFaculty());
			//封装学院数据
			stuCriteria.andIn("faculty",elcMutualCommonService.getCollegeList(session));
		}
		List<Student> students = studentDao.selectByExample(stuExample);
		if(CollectionUtil.isEmpty(students) || students.size()<studentIdList.size()) {
			throw new ParameterValidateException(I18nUtil.getMsg("elcMutualStu.addStuProjId")); 
		}
		
		if(Constants.BK_CROSS.equals(mode)) { // 跨学科互选学生名单管理
			Example example = new Example(ElcCrossStds.class);
			Example.Criteria criteria =example.createCriteria();
			example.selectProperties("studentId");
			criteria.andEqualTo("calendarId", calendarId);
			criteria.andIn("studentId", studentIdList);
			List<ElcCrossStds> list = elcCrossStdsDao.selectByExample(example);
			if(CollectionUtil.isNotEmpty(list)) {
				//如果已添加学生，则求二者差集
				Set<String> studentIds = list.stream().map(e -> e.getStudentId()).collect(Collectors.toSet());
				studentIdList = studentIdList.stream().filter(e -> !studentIds.contains(e)).collect(Collectors.toList());
//				throw new ParameterValidateException(I18nUtil.getMsg("common.exist","学生"));
			}
			List<ElcCrossStds> crossList = new ArrayList<>();
			for(String studentId:studentIdList) {
				ElcCrossStds elcCrossStds = new ElcCrossStds();
				elcCrossStds.setCalendarId(calendarId);
				elcCrossStds.setStudentId(studentId);
				crossList.add(elcCrossStds);
				if(crossList.size() > 500) {
					result = elcCrossStdsDao.insertList(crossList);
					crossList = new ArrayList<>();
				}
			}
			if (CollectionUtil.isNotEmpty(crossList)) {
				result =  elcCrossStdsDao.insertList(crossList);
			}
		}else { // 本研互选学生名单管理
			Example example = new Example(ElcMutualStds.class);
			Example.Criteria criteria =example.createCriteria();
			example.selectProperties("studentId");
			criteria.andEqualTo("calendarId", calendarId);
			//criteria.andIn("studentId", studentIdList);
			List<ElcMutualStds> list = elcMutualStdsDao.selectByExample(example);
			if(CollectionUtil.isNotEmpty(list)) {
				//如果已添加学生，则求二者差集
				Set<String> studentIds = list.stream().map(e -> e.getStudentId()).collect(Collectors.toSet());
				studentIdList = studentIdList.stream().filter(e -> !studentIds.contains(e)).collect(Collectors.toList());
//				throw new ParameterValidateException(I18nUtil.getMsg("common.exist","学生"));
			}
			
			List<ElcMutualStds> mutualList = new ArrayList<>();
			for(String studentId:studentIdList) {
				ElcMutualStds elcMutualStds = new ElcMutualStds();
				elcMutualStds.setCalendarId(calendarId);
				elcMutualStds.setStudentId(studentId);
				mutualList.add(elcMutualStds);
				if(mutualList.size() > 500) {
					result = elcMutualStdsDao.insertList(mutualList);
					mutualList = new ArrayList<>();
				}
			}
			if (CollectionUtil.isNotEmpty(mutualList)) {
				result = elcMutualStdsDao.insertList(mutualList);
			}
		}
		return result;
	}

	@Transactional
	@Override
	public int batchAdd(ElcMutualCrossStu dto) {
		// 教务员只能添加本行政学院学生
//		String faculty = SessionUtils.getCurrentSession().getFaculty();
		Session session = SessionUtils.getCurrentSession();
		List<String> collegeList = elcMutualCommonService.getCollegeList(session);
//		if (isDepartAdmin() && StringUtils.equals(faculty, dto.getFaculty())) {
//			throw new ParameterValidateException(I18nUtil.getMsg("elcMutualStu.addStuFaculty"));
//		}
		if (isDepartAdmin() && (StringUtils.isNotBlank(dto.getFaculty()) && !collegeList.contains(dto.getFaculty()))) {
			throw new ParameterValidateException(I18nUtil.getMsg("elcMutualStu.addStuFaculty"));
		}
		
		List<Student> students = getStudentInfos(dto);
		int result = Constants.ZERO;
		if(CollectionUtil.isNotEmpty(students)) {
			List<String> studentIds = students.stream().map(Student::getStudentCode).collect(Collectors.toList());
			result = saveElcMutualCross(dto.getCalendarId(), dto.getMode(), studentIds);
		}
		return result;
	}

	
	public List<Student> getStudentInfos(ElcMutualCrossStu dto) {
		Example example = new Example(Student.class);
		Session session = SessionUtils.getCurrentSession();
		example.selectProperties("studentCode");
		Example.Criteria criteria = example.createCriteria();
		criteria.andEqualTo("managerDeptId",session.getCurrentManageDptId());
		if (isDepartAdmin()) {
			if(StringUtils.isBlank(dto.getFaculty())) {
				//修改说明：当前教务员除了当前所属学院还管理其他学院
				//封装学院数据
				criteria.andIn("faculty",elcMutualCommonService.getCollegeList(session));
			}
		}
		if(dto.getGrade()!=null) {
			criteria.andEqualTo("grade", dto.getGrade());
		}
		if(StringUtils.isNotBlank(dto.getTrainingLevel())) {
			criteria.andEqualTo("trainingLevel", dto.getTrainingLevel());           // 培养层次
		}
		if(StringUtils.isNotBlank(dto.getTrainingCategory())) {
			criteria.andEqualTo("trainingCategory", dto.getTrainingCategory());     // 培养类别 
		}
		if(StringUtils.isNotBlank(dto.getEnrolMethods())) {
			criteria.andEqualTo("enrolMethods", dto.getEnrolMethods());             // 入学方式
		}
		if(StringUtils.isNotBlank(dto.getSpcialPlan())) {
			criteria.andEqualTo("spcialPlan", dto.getSpcialPlan());                 // 专项计划
		}
		if(StringUtils.isNotBlank(dto.getFaculty())) {
			criteria.andEqualTo("faculty", dto.getFaculty());
		}
		if(StringUtils.isNotBlank(dto.getProfession())) {
			criteria.andEqualTo("profession", dto.getProfession());
		}
		if(StringUtils.isNotBlank(dto.getResearchDirection())) {
			criteria.andEqualTo("researchDirection", dto.getResearchDirection());   // 研究方向
		}
		if(StringUtils.isNotBlank(dto.getIsOverseas())) {
			criteria.andEqualTo("isOverseas", dto.getIsOverseas());                 // 是否留学生( 0：否  1：是)
		}
		criteria.andEqualTo("leaveSchool",Constants.ONE);
		List<Student> students = studentDao.selectByExample(example);
		return students;
	}

	@Transactional
	@Override
	public int addAll(ElcMutualCrossStu dto) {
		Example example = new Example(Student.class);
		Session session = SessionUtils.getCurrentSession();
		Example.Criteria criteria = example.createCriteria();
		example.selectProperties("studentCode");
		criteria.andEqualTo("leaveSchool", Constants.INSCHOOL);
		criteria.andEqualTo("managerDeptId",session.getCurrentManageDptId());
		if (isDepartAdmin()) {
			//修改说明：当前教务员除了当前所属学院还管理其他学院

			//封装学院数据
			criteria.andIn("faculty",elcMutualCommonService.getCollegeList(session));
		}
		List<Student> students  = studentDao.selectByExample(example);
		int result = Constants.ZERO;
		if(CollectionUtil.isNotEmpty(students)) {
			List<String> studentIdList = students.stream().map(Student::getStudentCode).collect(Collectors.toList());
			result = saveElcMutualCross(dto.getCalendarId(), dto.getMode(), studentIdList);
		}
		return result;
	}

	@Transactional
	@Override
	public int delete(ElcMutualCrossStuDto dto) {
		int result = Constants.ZERO;
		if(Constants.BK_CROSS.equals(dto.getMode())) {
			Example example = new Example(ElcCrossStds.class);
			Example.Criteria criteria =example.createCriteria();
			criteria.andIn("id", dto.getIds());
			List<ElcCrossStds> list =  elcCrossStdsDao.selectByExample(example);
			if(CollectionUtil.isEmpty(list)) {
				throw new ParameterValidateException(I18nUtil.getMsg("common.dataError","跨学科互选")); 
			}
			result = elcCrossStdsDao.deleteByExample(example);
		}else {
			Example example = new Example(ElcMutualStds.class);
			Example.Criteria criteria =example.createCriteria();
			criteria.andIn("id", dto.getIds());
			List<ElcMutualStds> list =  elcMutualStdsDao.selectByExample(example);
//			if(CollectionUtil.isEmpty(list)) {
//				throw new ParameterValidateException(I18nUtil.getMsg("common.dataError","本研互选"));
//			}
			result = elcMutualStdsDao.deleteByExample(example);
		}
		return result;
	}

	@Transactional
	@Override
	public int batchDelete(ElcMutualCrossStu dto) {
		int result = Constants.ZERO;
		List<Student> students = getStudentInfos(dto);
		if(CollectionUtil.isNotEmpty(students)) {
			List<String> studentIdList = new ArrayList<>();
			for(Student student : students) {
				studentIdList.add(student.getStudentCode());
				if(studentIdList.size() > 500) {
					if(Constants.BK_CROSS.equals(dto.getMode())) {
						Example example = new Example(ElcCrossStds.class);
						Example.Criteria criteria =example.createCriteria();
						criteria.andEqualTo("calendarId", dto.getCalendarId());
						criteria.andIn("studentId", studentIdList);
						result = elcCrossStdsDao.deleteByExample(example);
					} else {
						Example example = new Example(ElcMutualStds.class);
						Example.Criteria criteria =example.createCriteria();
						criteria.andEqualTo("calendarId", dto.getCalendarId());
						criteria.andIn("studentId", studentIdList);
						result = elcMutualStdsDao.deleteByExample(example);
					}
					studentIdList = new ArrayList<>();
				}
			}
			if (CollectionUtil.isNotEmpty(studentIdList)) {
				if(Constants.BK_CROSS.equals(dto.getMode())) {
					Example example = new Example(ElcCrossStds.class);
					Example.Criteria criteria =example.createCriteria();
					criteria.andEqualTo("calendarId", dto.getCalendarId());
					criteria.andIn("studentId", studentIdList);
					result = elcCrossStdsDao.deleteByExample(example);
				} else {
					Example example = new Example(ElcMutualStds.class);
					Example.Criteria criteria =example.createCriteria();
					criteria.andEqualTo("calendarId", dto.getCalendarId());
					criteria.andIn("studentId", studentIdList);
					result = elcMutualStdsDao.deleteByExample(example);
				}
			}
		}
		return result;
	}

	@Override
	@Transactional
	public int deleteAll(Long calendarId, Integer mode) {
		int result = Constants.ZERO;
		Boolean isBkCross = Constants.BK_CROSS.equals(mode);
		//获取管理学院和所属学院
		Session session = SessionUtils.getCurrentSession();
		boolean isAcdemicDean = StringUtils.equals(session.getCurrentRole(),String.valueOf(Constants.ONE))
				&& !session.isAdmin() && session.isAcdemicDean();
		//定义全局学院,作为条件删除
		String facultyCondition = "";
		//判断是否是教务员，如果是进行下列操作
		//boolean isAcdemicDean=true;
		if(isAcdemicDean){
			//获取当前用户的所属学院
			String faculty = session.getFaculty();
			//String faculty="000163";
			//获取当前用户的管理学院
			String manageFaculty = session.getManageFaculty();
			//String manageFaculty="000215,000287";
			//学院数据分权
			//  1 如果mock所属学院为空,则取所属管理学院;
			//  2 如果所管理学院为空,取mock学院;
			//  3 如果都不为空,取管理学院
			if(StringUtils.isNotEmpty(manageFaculty)){
				facultyCondition = manageFaculty;
			}else{
				facultyCondition = faculty;
			}
            //通过学院查询学生id
            List<String> studentList=elcCrossStdsDao.queryStudentIdByFacuty(facultyCondition);
            //修改为以对象形式接收前端参数，因为前端使用json格式上传
//		Long calendarId = dto.getCalendarId();
//		Integer mode = dto.getMode();
            LOG.info("----------the parapes of delete is calendarId:{},facultyCondition:{}",calendarId,facultyCondition);
            if(isBkCross) {
//                Example example = new Example(ElcCrossStds.class);
//                Example.Criteria criteria = example.createCriteria();
//                criteria.andEqualTo("calendarId", calendarId);
                //result = elcCrossStdsDao.deleteByExample(example);
			/*ElcCrossStdVo elcCrossStdVo=new ElcCrossStdVo();
			elcCrossStdVo.setCalendarId(calendarId);
			elcCrossStdVo.setFaculty(facultyCondition);*/
                if (studentList!=null && studentList.size()>0){
                    List<String> strList=new ArrayList<>();
                    for(String str:studentList) {
                        strList.add(str);
                        if(strList.size()>500){
                            result = elcCrossStdsDao.deleteCrossByParames(calendarId, strList);
                            strList = new ArrayList<>();
                        }
                    }
                    //整除500条移除后的余条数
                    if(strList.size()>0){
                        result = elcCrossStdsDao.deleteCrossByParames(calendarId, strList);
                    }
                }
            }else {
//                Example example = new Example(ElcMutualStds.class);
//                Example.Criteria criteria =example.createCriteria();
//                criteria.andEqualTo("calendarId", calendarId);
                //result = elcMutualStdsDao.deleteByExample(example);
			/*ElcMutualStdVo elcMutualStdVo=new ElcMutualStdVo();
			elcMutualStdVo.setCalendarId(calendarId);
			elcMutualStdVo.setFaculty(facultyCondition);*/
			/*if (studentList!=null && studentList.size()>0){
				result=elcMutualStdsDao.deleteMutualByParames(calendarId,studentList);
			}*/
                if (studentList!=null && studentList.size()>0){
                    List<String> currentList=new ArrayList<>();
                    for(String str:studentList) {
                        currentList.add(str);
                        if(currentList.size()>500){
                            result=elcMutualStdsDao.deleteMutualByParames(calendarId,currentList);
                            currentList = new ArrayList<>();
                        }
                    }
                    //整除500条移除后的余条数
                    if(currentList.size()>0){
                        result=elcMutualStdsDao.deleteMutualByParames(calendarId,currentList);
                    }
                }
            }
        } else {
            //管理员权限直接删除：该接口只开放给教务员和管理员
            if (isBkCross) {
                Example example = new Example(ElcCrossStds.class);
                Example.Criteria criteria = example.createCriteria();
                criteria.andEqualTo("calendarId", calendarId);
                result = elcCrossStdsDao.deleteByExample(example);
            } else {
                Example example = new Example(ElcMutualStds.class);
                Example.Criteria criteria = example.createCriteria();
                criteria.andEqualTo("calendarId", calendarId);
                result = elcMutualStdsDao.deleteByExample(example);
            }
        }


		return result;
	}

	/**
	 * 获取当前所有启动的部门列表（包括虚拟部门）
	 * @param virtualDept 0：不包含  1:包含虚拟部门
	 * @param type 0：非学院，其他，全部 1：学院
	 * @param manageDept 是否区分本研 0或null：不区分本研 1 区分本研
	 * */
	@Override
	public RestResult<List<Department>> findDept(String virtualDept, Integer type, Integer manageDept) {
		if (type ==null){
			type = 1;
		}

		String managDeptId = String.valueOf(manageDept);

		LOG.info("managDeptId : {}, === virtualDept:{} ===type:{} =======projectId：{} ", managDeptId, virtualDept, type);
		List<Department> deptList = elcCrossStdsDao.findFaculty(virtualDept, managDeptId, type);
		return RestResult.successData(deptList);
	}


	/**
	 * 判断当前登录人是否是教务员
	 * @return
	 */
	private boolean isDepartAdmin(){
		Session session = SessionUtils.getCurrentSession();
        boolean isDepartAdmin = StringUtils.equals(session.getCurrentRole(), String.valueOf(Constants.ONE))
				                && !session.isAdmin() && session.isAcdemicDean();
        return isDepartAdmin;
	}

	/**
	 * 功能描述: 校验添加学生时，是否存在离校学生
	 *
	 * 备注：抽取该方法，避免后续单个添加、批量添加、全部添加时共用。已和需求确认，目前学生是否在校只校验单个添加。
	 *
	 * @params: [studentIdList]
	 * @return: void
	 * @author: zhaoerhu
	 * @date: 2020/2/17 16:02
	 */
	private void checkStuIsLeaveSchool(List<String> studentIdList) {
		Example stuExample = new Example(Student.class);
		Example.Criteria stuCriteria = stuExample.createCriteria();
		stuExample.selectProperties("studentCode");
		stuCriteria.andIn("studentCode", studentIdList);
		stuCriteria.andNotEqualTo("leaveSchool", Constants.INSCHOOL);
		List<Student> students = studentDao.selectByExample(stuExample);
		//上送学号中若存在离校学生则直抛异常
		if (students != null && students.size() > 0) {
			throw new ParameterValidateException(I18nUtil.getMsg("elcMutualStu.addStuLeave"));
		}
	}
}
