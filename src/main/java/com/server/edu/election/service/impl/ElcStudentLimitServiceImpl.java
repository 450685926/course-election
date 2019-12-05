package com.server.edu.election.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.server.edu.common.PageCondition;
import com.server.edu.common.enums.GroupDataEnum;
import com.server.edu.common.jackson.JacksonUtil;
import com.server.edu.common.locale.I18nUtil;
import com.server.edu.election.constants.Constants;
import com.server.edu.election.dao.ElcStudentLimitDao;
import com.server.edu.election.dao.StudentDao;
import com.server.edu.election.dto.ElcStudentLimitDto;
import com.server.edu.election.dto.StudentDto;
import com.server.edu.election.entity.ElcStudentLimit;
import com.server.edu.election.entity.Student;
import com.server.edu.election.service.ElcStudentLimitService;
import com.server.edu.election.util.TableIndexUtil;
import com.server.edu.election.vo.ElcStudentLimitVo;
import com.server.edu.exception.ParameterValidateException;
import com.server.edu.session.util.SessionUtils;
import com.server.edu.session.util.entity.Session;
import com.server.edu.util.CollectionUtil;
import com.server.edu.util.excel.GeneralExcelDesigner;
import com.server.edu.util.excel.export.ExcelExecuter;
import com.server.edu.util.excel.export.ExcelResult;
import com.server.edu.util.excel.export.ExportExcelUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
@Service
public class ElcStudentLimitServiceImpl implements ElcStudentLimitService {

	@Autowired
	private StudentDao studentDao;
	@Autowired
	private ElcStudentLimitDao elcStudentLimitDao;
	@Override
	public PageInfo<Student> getUnLimitStudents(PageCondition<StudentDto> condition) {
		StudentDto studentDto = condition.getCondition();
		Session session = SessionUtils.getCurrentSession();
		if (StringUtils.equals(session.getCurrentRole(), "1") && !session.isAdmin() && session.isAcdemicDean()) {
			List<String> deptIds = SessionUtils.getCurrentSession().getGroupData().get(GroupDataEnum.department.getValue());
			studentDto.setFaculties(deptIds);
		}
		PageHelper.startPage(condition.getPageNum_(), condition.getPageSize_());
		List<Student> list = studentDao.getUnLimitStudents(studentDto);
		PageInfo<Student> pageInfo =new PageInfo<>(list);
		return pageInfo;
	}

	@Transactional
	@Override
	public int add(ElcStudentLimitDto elcStudentLimitDto) {
		List<String> studentCodes =elcStudentLimitDto.getStudentCodes();
		String projectId = SessionUtils.getCurrentSession().getCurrentManageDptId();
		List<ElcStudentLimit> list = new ArrayList<>();
		for(String studentId : studentCodes) {
			ElcStudentLimit elcStudentLimit = new ElcStudentLimit();
			elcStudentLimit.setStudentId(studentId);
			elcStudentLimit.setCalendarId(elcStudentLimitDto.getCalendarId());
			elcStudentLimit.setProjectId(projectId);
			elcStudentLimit.setNewLimitCredits(Constants.NEW_LIMIT_CREDITS);
			elcStudentLimit.setTotalLimitCredits(Constants.TOTAL_LIMIT_CREDITS);
			elcStudentLimit.setRebuildLimitNumber(Constants.REBUILD_LIMIT_NUMBER);
			elcStudentLimit.setCreatedAt(new Date());
			list.add(elcStudentLimit);
		}
		int result = elcStudentLimitDao.insertList(list);
		if(result<=Constants.ZERO) {
			throw new ParameterValidateException(I18nUtil.getMsg("common.saveSuccess",I18nUtil.getMsg("election.elcStudentLimit")));
		}
		return result;
	}

	@Override
	public PageInfo<ElcStudentLimitVo> getLimitStudents(PageCondition<ElcStudentLimitDto> condition) {
		ElcStudentLimitDto dto  = condition.getCondition();
		Session session = SessionUtils.getCurrentSession();
		if (StringUtils.equals(session.getCurrentRole(), "1") && !session.isAdmin() && session.isAcdemicDean()) {
			List<String> deptIds = SessionUtils.getCurrentSession().getGroupData().get(GroupDataEnum.department.getValue());
			dto.setFaculties(deptIds);
		}
		dto.setIndex(TableIndexUtil.getIndex(dto.getCalendarId()));
		PageHelper.startPage(condition.getPageNum_(), condition.getPageSize_());
		List<ElcStudentLimitVo> list = elcStudentLimitDao.getLimitStudents(dto);
		PageInfo<ElcStudentLimitVo> pageInfo =new PageInfo<>(list);
		return pageInfo;
	}
	
	@Transactional
	@Override
	public int update(ElcStudentLimitDto elcStudentLimitDto) {
		ElcStudentLimit elcStudentLimit = new ElcStudentLimit();
		if(elcStudentLimitDto.getId()==null||elcStudentLimitDto.getTotalLimitCredits()<elcStudentLimitDto.getNewLimitCredits() 
		   ||elcStudentLimitDto.getTotalLimitCredits()<elcStudentLimitDto.getSelectedCredits()
		   ||elcStudentLimitDto.getNewLimitCredits()<elcStudentLimitDto.getSelectedCredits()
		   ||elcStudentLimitDto.getRebuildLimitNumber()<elcStudentLimitDto.getSelectedRebuild()) {
			throw new ParameterValidateException(I18nUtil.getMsg("elcStudentLimit.parameterError")); 
		}
		BeanUtils.copyProperties(elcStudentLimitDto, elcStudentLimit);
		int result = elcStudentLimitDao.updateByPrimaryKeySelective(elcStudentLimit);
		if(result<Constants.ZERO) {
			throw new ParameterValidateException(I18nUtil.getMsg("common.editError",I18nUtil.getMsg("election.elcStudentLimit")));
		}
		return result;
	}
	
	@Transactional
	@Override
	public int delete(List<Long> ids) {
		Example example = new Example(ElcStudentLimit.class);
		Example.Criteria criteria = example.createCriteria();
		criteria.andIn("id", ids);
		int result = elcStudentLimitDao.deleteByExample(example);
		if(result<=Constants.ZERO) {
			throw new ParameterValidateException(I18nUtil.getMsg("common.failSuccess",I18nUtil.getMsg("election.elcStudentLimit")));
		}
		return result;
	}
	
	@Transactional
	@Override
	public int deleteAll(ElcStudentLimitDto elcStudentLimitDto) {
		int result = 0;
		elcStudentLimitDto.setIndex(TableIndexUtil.getIndex(elcStudentLimitDto.getCalendarId()));
		Session session = SessionUtils.getCurrentSession();
		if (StringUtils.equals(session.getCurrentRole(), "1") && !session.isAdmin() && session.isAcdemicDean()) {
			List<String> deptIds = SessionUtils.getCurrentSession().getGroupData().get(GroupDataEnum.department.getValue());
			elcStudentLimitDto.setFaculties(deptIds);
		}
		List<ElcStudentLimitVo> list = elcStudentLimitDao.getLimitStudents(elcStudentLimitDto);
		if(CollectionUtil.isNotEmpty(list)) {
			List<Long> ids = list.stream().map(ElcStudentLimitVo::getId).collect(Collectors.toList());
			result =delete(ids);
		}
		return result;
	}
	
	@Override
	public ExcelResult export(ElcStudentLimitDto elcStudentLimitDto) throws Exception{
		// 使用使用ExportExcelUtils默认缓存地址
		ExcelResult excelResult = ExportExcelUtils.submitTask("elcStudentLimit", new ExcelExecuter() {
			@Override
			public GeneralExcelDesigner getExcelDesigner() {
				ExcelResult result = this.getResult();
				// 需要生产excel的数据
				PageCondition<ElcStudentLimitDto> pageCondition = new PageCondition<ElcStudentLimitDto>();
				pageCondition.setCondition(elcStudentLimitDto);
				pageCondition.setPageSize_(100);
				int pageNum = Constants.ZERO;
				List<ElcStudentLimitVo> resultList = new ArrayList<>();
				List<String> studentIdList = elcStudentLimitDto.getStudentIdList();
				if(null==studentIdList||studentIdList.size()==0){
					while(true) {
						pageNum++;
						pageCondition.setPageNum_(pageNum);
						PageInfo<ElcStudentLimitVo> pageResult = getLimitStudents(pageCondition);
						resultList.addAll(pageResult.getList());
						result.setTotal((int)pageResult.getTotal());
						Double count = resultList.size() / 1.5;
						result.setDoneCount(count.intValue());
						this.updateResult(result);
						if(pageResult.getTotal() <= resultList.size()) {
							break;
						}
					}
				}else{
					for(String string:studentIdList){
						pageCondition.getCondition().setStudentId(string);
						PageInfo<ElcStudentLimitVo> pageResult = getLimitStudents(pageCondition);
						resultList.addAll(pageResult.getList());
					}
				}

				GeneralExcelDesigner design = getDesign();
				List<JSONObject> convertList = JacksonUtil.convertList(resultList);
				design.setDatas(convertList);
				return design;
			}
		});
		return excelResult;
	}
	
	// 课程导出字段封装
	private GeneralExcelDesigner getDesign() {
			GeneralExcelDesigner design = new GeneralExcelDesigner();
			design.setNullCellValue("");
			design.addCell(I18nUtil.getMsg("elcStudentLimit.studentId"), "studentId");
			design.addCell(I18nUtil.getMsg("elcStudentLimit.name"), "name");
			design.addCell(I18nUtil.getMsg("elcStudentLimit.grade"), "grade");
			design.addCell(I18nUtil.getMsg("elcStudentLimit.faculty"), "facultyI18n");
			design.addCell(I18nUtil.getMsg("elcStudentLimit.profession"), "professionI18n");
			design.addCell(I18nUtil.getMsg("elcStudentLimit.newLimitCredits"), "newLimitCredits");
			design.addCell(I18nUtil.getMsg("elcStudentLimit.selectedCredits"), "selectedCredits");
			design.addCell(I18nUtil.getMsg("elcStudentLimit.totalLimitCredits"), "totalLimitCredits");
			design.addCell(I18nUtil.getMsg("elcStudentLimit.rebuildLimitNumber"), "rebuildLimitNumber");
			design.addCell(I18nUtil.getMsg("elcStudentLimit.selectedRebuild"), "selectedRebuild");
			return design;
		}
	@Override
	public ExcelResult exportUnLimit(StudentDto studentDto) throws Exception{
		// 使用使用ExportExcelUtils默认缓存地址
		ExcelResult excelResult = ExportExcelUtils.submitTask("elcUnStudentLimit", new ExcelExecuter() {
			@Override
			public GeneralExcelDesigner getExcelDesigner() {
				ExcelResult result = this.getResult();
				// 需要生产excel的数据
				PageCondition<StudentDto> pageCondition = new PageCondition<StudentDto>();
				pageCondition.setCondition(studentDto);
				pageCondition.setPageSize_(100);
				int pageNum = Constants.ZERO;
				List<Student> resultList = new ArrayList<>();
				List<String> studentIds = studentDto.getStudentIds();
                if(null==studentIds||studentIds.size()==0){
                    while(true) {
                        pageNum++;
                        pageCondition.setPageNum_(pageNum);
                        PageInfo<Student> pageResult = getUnLimitStudents(pageCondition);
                        resultList.addAll(pageResult.getList());
                        result.setTotal((int)pageResult.getTotal());
                        Double count = resultList.size() / 1.5;
                        result.setDoneCount(count.intValue());
                        this.updateResult(result);
                        if(pageResult.getTotal() <= resultList.size()) {
                            break;
                        }
                    }
                }else{
                    for(String string:studentIds){
                        pageCondition.getCondition().setStudentId(string);
                        PageInfo<Student> pageResult = getUnLimitStudents(pageCondition);
                        resultList.addAll(pageResult.getList());
                    }
                }

				GeneralExcelDesigner design = new GeneralExcelDesigner();
				design.setNullCellValue("");
				design.addCell(I18nUtil.getMsg("elcStudentLimit.studentId"), "studentCode");
				design.addCell(I18nUtil.getMsg("elcStudentLimit.name"), "name");
				design.addCell(I18nUtil.getMsg("elcStudentLimit.grade"), "grade");
				design.addCell(I18nUtil.getMsg("elcStudentLimit.faculty"), "facultyI18n");
				design.addCell(I18nUtil.getMsg("elcStudentLimit.profession"), "professionI18n");
				List<JSONObject> convertList = JacksonUtil.convertList(resultList);
				design.setDatas(convertList);
				return design;
			}
		});
		return excelResult;
	}
}
