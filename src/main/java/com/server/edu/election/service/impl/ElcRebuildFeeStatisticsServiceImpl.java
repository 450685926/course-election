package com.server.edu.election.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.server.edu.common.PageCondition;
import com.server.edu.common.jackson.JacksonUtil;
import com.server.edu.common.rest.PageResult;
import com.server.edu.common.vo.SchoolCalendarVo;
import com.server.edu.dictionary.service.DictionaryService;
import com.server.edu.dictionary.utils.SchoolCalendarCacheUtil;
import com.server.edu.election.config.DoubleHandler;
import com.server.edu.election.constants.Constants;
import com.server.edu.election.dao.ElcCourseTakeDao;
import com.server.edu.election.dao.RebuildCourseNoChargeTypeDao;
import com.server.edu.election.dto.StudentRebuildFeeDto;
import com.server.edu.election.entity.RebuildCourseNoChargeType;
import com.server.edu.election.entity.Student;
import com.server.edu.election.service.ElcRebuildFeeStatisticsService;
import com.server.edu.election.service.RebuildCourseChargeService;
import com.server.edu.election.vo.RebuildCourseNoChargeTypeVo;
import com.server.edu.election.vo.StudentRebuildFeeVo;
import com.server.edu.election.vo.StudentVo;
import com.server.edu.exam.rpc.BaseresServiceExamInvoker;
import com.server.edu.session.util.SessionUtils;
import com.server.edu.util.CollectionUtil;
import com.server.edu.util.excel.ExcelWriterUtil;
import com.server.edu.util.excel.GeneralExcelDesigner;
import com.server.edu.util.excel.GeneralExcelUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class ElcRebuildFeeStatisticsServiceImpl implements ElcRebuildFeeStatisticsService{

	@Autowired
	private ElcCourseTakeDao elcCourseTakeDao;
	
    @Autowired
    private DictionaryService dictionaryService;


	@Autowired
	private RebuildCourseChargeService noChargeTypeservice;

	@Autowired
	private RebuildCourseNoChargeTypeDao noChargeTypeDao;

	@Override
	public PageResult<StudentRebuildFeeVo> getStudentRebuildFeeList(PageCondition<StudentRebuildFeeDto> condition) {
		// TODO Auto-generated method stub
		String dptId = SessionUtils.getCurrentSession().getCurrentManageDptId();

		StudentRebuildFeeDto dto = condition.getCondition();
		//查询校历时间
		SchoolCalendarVo calendar = SchoolCalendarCacheUtil.getCalendar(dto.getCalendarId());
		//获取上学期
		SchoolCalendarVo preTerm = BaseresServiceExamInvoker.getPreOrNextTerm(dto.getCalendarId(), false);

		dto.setManageDptId(dptId);
        List<RebuildCourseNoChargeType> noStuPay = noChargeTypeDao.selectAll();
		dto.setNoPayStudentType(noStuPay);
		dto.setAbnormalStatuEndTime(calendar.getEndDay());
        dto.setAbnormalStatuStartTime(preTerm.getBeginDay());
		PageHelper.startPage(condition.getPageNum_(), condition.getPageSize_());
		Page<StudentRebuildFeeVo> list = elcCourseTakeDao.getStudentRebuildFeeList(condition.getCondition());
		return new PageResult<>(list);
	}

	@Override
	public List<String> transNoChargeTypeStudent(){
		Set<String> noTypeStudent = new HashSet<>();
		PageCondition<RebuildCourseNoChargeTypeVo> condition = new PageCondition<>();
		condition.setCondition(new RebuildCourseNoChargeTypeVo());
		condition.setPageNum_(0);
		condition.setPageSize_(0);
		PageResult<RebuildCourseNoChargeType> noChargeType = noChargeTypeservice.findCourseNoChargeType(condition);
		if(noChargeType != null && CollectionUtil.isNotEmpty(noChargeType.getList())){
			List<RebuildCourseNoChargeType> list = noChargeType.getList();
            List<StudentRebuildFeeVo> abnormalStu = new ArrayList<>();
			//判断是否需要去学籍异动里面查找一年内异动学生
            List<String> collect = list.stream().filter(vo ->StringUtils.isNotBlank(vo.getRegistrationStatus())).map(RebuildCourseNoChargeType::getRegistrationStatus).collect(Collectors.toList());
            if(CollectionUtil.isNotEmpty(collect)){
                //查找一年内异动学生
                Long oneYearTime =System.currentTimeMillis();
               abnormalStu = noChargeTypeDao.getAbnormalStudent(collect,oneYearTime);
            }
            //分组获取异动类型的学生集合
            Map<String, List<StudentRebuildFeeVo>> listMap = new HashMap<>();
            if(CollectionUtil.isNotEmpty(abnormalStu)){
                 listMap = abnormalStu.stream().collect(Collectors.groupingBy(StudentRebuildFeeVo::getRegistrationStatus));
            }
            //todo 获取所有重修的学生
            List<Student> stuList = new ArrayList<>();
            for (Student student : stuList) {
                for (RebuildCourseNoChargeType noChargeStudent : list) {
                    String trainingLevel = noChargeStudent.getTrainingLevel();
                    String trainingCategory = noChargeStudent.getTrainingCategory();
                    String enrolMethods = noChargeStudent.getEnrolMethods();
                    String spcialPlan = noChargeStudent.getSpcialPlan();
                    String isOverseas = noChargeStudent.getIsOverseas();
                    String registrationStatus = noChargeStudent.getRegistrationStatus();

                    if(StringUtils.isNotBlank(trainingLevel) && !trainingLevel.equals(student.getTrainingLevel())){
                         continue;
                    }

                    if(StringUtils.isNotBlank(trainingCategory) && !trainingCategory.equals(student.getTrainingCategory())){
                        continue;
                    }

                    if(StringUtils.isNotBlank(enrolMethods) && !enrolMethods.equals(student.getEnrolMethods())){
                        continue;
                    }

                    if(StringUtils.isNotBlank(spcialPlan) && !spcialPlan.equals(student.getSpcialPlan())){
                        continue;
                    }

                    if(StringUtils.isNotBlank(isOverseas) && !isOverseas.equals(student.getIsOverseas())){
                        continue;
                    }

                    if(StringUtils.isNotBlank(registrationStatus)){
                        if(listMap.size() == 0){
                            continue;
                        }
                        if(listMap.size() > 0){
                            List<StudentRebuildFeeVo> feeVoList = listMap.get(registrationStatus);
                            if(CollectionUtil.isEmpty(feeVoList)){
                                continue;
                            }else{
                                List<String> stringList = feeVoList.stream().map(StudentRebuildFeeVo::getStudentId).collect(Collectors.toList());
                                if(!stringList.contains(student.getStudentCode())){
                                    continue;
                                }
                            }
                        }
                    }

                    noTypeStudent.add(student.getStudentCode());
                    break;
                }
            }
		}

		return new ArrayList<>(noTypeStudent);
	}

	@Override
	public ExcelWriterUtil export(StudentRebuildFeeDto studentRebuildFeeDto) throws Exception {
		// TODO Auto-generated method stub
		PageCondition<StudentRebuildFeeDto> pageCondition = new PageCondition<>();
        pageCondition.setCondition(studentRebuildFeeDto);
        pageCondition.setPageSize_(0);
        pageCondition.setPageNum_(0);
		//List<StudentRebuildFeeVo> list = elcCourseTakeDao.getStudentRebuildFeeList(studentRebuildFeeDto);
        PageResult<StudentRebuildFeeVo> studentRebuildFeeList = getStudentRebuildFeeList(pageCondition);
        List<StudentRebuildFeeVo> list = studentRebuildFeeList.getList();
        GeneralExcelDesigner design = getDesign();
		List<JSONObject> convertList = JacksonUtil.convertList(list);
	    design.setDatas(convertList);
	    ExcelWriterUtil excelUtil = GeneralExcelUtil.generalExcelHandle(design);
		return excelUtil;
	}
	
	private GeneralExcelDesigner getDesign() {
		 DoubleHandler doubleHandler = new DoubleHandler();
		 GeneralExcelDesigner design = new GeneralExcelDesigner();
		 design.setNullCellValue("");
		 design.addCell("学号","studentId");
		 design.addCell("姓名","studentName");
		 design.addCell("学院", "facultyI18n");
         design.addCell("专业", "professionI18n");
		 design.addCell("课程序号","teachingClassCode");
		 design.addCell("课程名称","courseName");
         design.addCell("课程性质", "nature").setValueHandler(
				 (value, rawData, cell) -> {
					 if("1".equals(value)) {
						 value ="公开课";
					 }else {
						 value ="专业课";
					 }
					 return value;
				 });
        design.addCell("学分", "credits");
	 	design.addCell("应缴金额","amount").setValueHandler(doubleHandler);
	 	design.addCell("已缴金额","pay").setValueHandler(doubleHandler);
	 	design.addCell("是否已缴费","paid").setValueHandler(
				 (value, rawData, cell) -> {
					    if(Constants.PAID.toString().equals(value)) {
					    	value ="是";
					    }else {
					    	value ="否";
						}
	                    return value;
	                });
		 design.addCell("课程学年学期","couCalendarIdI18n");
		 design.addCell("缴费时间","payTime");
		 return design;
	}

}
