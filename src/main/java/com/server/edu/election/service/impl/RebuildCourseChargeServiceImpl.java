package com.server.edu.election.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.server.edu.common.PageCondition;
import com.server.edu.common.locale.I18nUtil;
import com.server.edu.common.rest.PageResult;
import com.server.edu.common.vo.SchoolCalendarVo;
import com.server.edu.dictionary.service.DictionaryService;
import com.server.edu.election.constants.Constants;
import com.server.edu.election.dao.ElcCourseTakeDao;
import com.server.edu.election.dao.RebuildCourseChargeDao;
import com.server.edu.election.dao.RebuildCourseNoChargeTypeDao;
import com.server.edu.election.dto.RebuildCoursePaymentCondition;
import com.server.edu.election.entity.RebuildCourseCharge;
import com.server.edu.election.vo.RebuildCourseNoChargeList;
import com.server.edu.election.entity.RebuildCourseNoChargeType;
import com.server.edu.election.rpc.BaseresServiceInvoker;
import com.server.edu.election.service.RebuildCourseChargeService;
import com.server.edu.election.vo.StudentVo;
import com.server.edu.session.util.SessionUtils;
import com.server.edu.util.CollectionUtil;
import com.server.edu.util.FileUtil;
import com.server.edu.util.excel.ExcelWriterUtil;
import com.server.edu.util.excel.GeneralExcelDesigner;
import com.server.edu.util.excel.GeneralExcelUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.FileOutputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @description: 重修收费管理
 * @author: bear
 * @create: 2019-01-31 19:39
 */
@Service
@Primary
public class RebuildCourseChargeServiceImpl implements RebuildCourseChargeService {

    @Autowired
    private RebuildCourseChargeDao courseChargeDao;

    @Autowired
    private RebuildCourseNoChargeTypeDao noChargeTypeDao;

    @Autowired
    private ElcCourseTakeDao courseTakeDao;

    @Autowired
    private DictionaryService dictionaryService;

    @Value("${cache.directory}")
    private String cacheDirectory;

    /**
    *@Description: 查询收费管理
    *@Param:
    *@return:
    *@Author: bear
    *@date: 2019/2/1 8:58
    */
    @Override
    public PageResult<RebuildCourseCharge> findCourseCharge(PageCondition<RebuildCourseCharge> condition) {
        PageHelper.startPage(condition.getPageNum_(), condition.getPageSize_());
        Page<RebuildCourseCharge> courseCharge = courseChargeDao.findCourseCharge(condition.getCondition());
        return new PageResult<>(courseCharge);
    }


    /**
    *@Description: 删除重修收费信息
    *@Param:
    *@return:
    *@Author: bear
    *@date: 2019/2/1 9:14
    */
    @Override
    @Transactional
    public String deleteCourseCharge(List<Long> ids) {
        if(CollectionUtil.isEmpty(ids)){
            return "common.parameterError";
        }
        courseChargeDao.deleteCourseCharge(ids);
         return  "common.deleteSuccess";
    }

    
    /**
    *@Description: 编辑收费信息
    *@Param: 
    *@return: 
    *@Author: bear
    *@date: 2019/2/1 9:42
    */
    @Override
    @Transactional
    public String editCourseCharge(RebuildCourseCharge courseCharge) {
        RebuildCourseCharge rebuildCourseCharge=new RebuildCourseCharge();
        Page<RebuildCourseCharge> courseCharges = courseChargeDao.findCourseCharge(rebuildCourseCharge);
        if(courseCharges!=null&&courseCharges.getResult().size()>0){
            List<RebuildCourseCharge> result = courseCharges.getResult();
            List<RebuildCourseCharge> collect = result.stream().filter((RebuildCourseCharge vo) -> vo.getId().longValue() != courseCharge.getId().longValue()).collect(Collectors.toList());
            if(CollectionUtil.isNotEmpty(collect)){
                if(collect.contains(courseCharge)){
                    return "common.exist";
                }
            }
        }
        courseChargeDao.updateByPrimaryKeySelective(courseCharge);
        return "common.editSuccess";
    }

    /**
    *@Description: 新增收费信息
    *@Param:
    *@return: 
    *@Author: bear
    *@date: 2019/2/1 9:50
    */
    @Override
    @Transactional
    public String addCourseCharge(RebuildCourseCharge courseCharge) {
        RebuildCourseCharge rebuildCourseCharge=new RebuildCourseCharge();
        Page<RebuildCourseCharge> courseCharges = courseChargeDao.findCourseCharge(rebuildCourseCharge);
        if(courseCharges!=null){
            List<RebuildCourseCharge> result = courseCharges.getResult();
            if(CollectionUtil.isNotEmpty(result)){
                if(result.contains(courseCharge)){
                    return "common.exist";
                }
            }
        }
        courseChargeDao.insertSelective(courseCharge);
        return "common.addsuccess";
    }

    /**
    *@Description: 查询重修不收费类型
    *@Param: 
    *@return: 
    *@Author: bear
    *@date: 2019/2/1 14:02
    */
    @Override
    public PageResult<RebuildCourseNoChargeType> findCourseNoChargeType(PageCondition<RebuildCourseNoChargeType> condition) {
        PageHelper.startPage(condition.getPageNum_(), condition.getPageSize_());
        Page<RebuildCourseNoChargeType> courseNoChargeType = noChargeTypeDao.findCourseNoChargeType(condition.getCondition());
        return new PageResult<>(courseNoChargeType);
    }

    /**
    *@Description: 新增重修不收费类型
    *@Param: 
    *@return: 
    *@Author: bear
    *@date: 2019/2/1 14:19
    */
    @Override
    public String addCourseNoChargeType(RebuildCourseNoChargeType noChargeType) {
        RebuildCourseNoChargeType courseNoChargeType=new RebuildCourseNoChargeType();
        Page<RebuildCourseNoChargeType> chargeType = noChargeTypeDao.findCourseNoChargeType(courseNoChargeType);
        if(chargeType!=null){
            List<RebuildCourseNoChargeType> result = chargeType.getResult();
            if(CollectionUtil.isNotEmpty(result)){
                if(result.contains(noChargeType)){
                    return "common.exist";
                }
            }
        }
        noChargeTypeDao.insertSelective(noChargeType);
        return "common.addsuccess";
    }

    /**
    *@Description: 删除重修不收费类型
    *@Param:
    *@return:
    *@Author: bear
    *@date: 2019/2/1 14:29
    */
    @Override
    public String deleteCourseNoChargeType(List<Long> ids) {
        if(CollectionUtil.isEmpty(ids)){
            return "common.parameterError";
        }

        noChargeTypeDao.deleteRebuildCourseNoChargeType(ids);
        return  "common.deleteSuccess";
    }

    /**
    *@Description: 编辑重修不收费学生类型
    *@Param:
    *@return:
    *@Author: bear
    *@date: 2019/2/1 14:37
    */
    @Override
    public String editCourseNoChargeType(RebuildCourseNoChargeType courseNoCharge) {
        RebuildCourseNoChargeType courseNoChargeType=new RebuildCourseNoChargeType();
        Page<RebuildCourseNoChargeType> chargeType = noChargeTypeDao.findCourseNoChargeType(courseNoChargeType);
        if(chargeType!=null){
            List<RebuildCourseNoChargeType> result = chargeType.getResult();
            if(CollectionUtil.isNotEmpty(result)){
                List<RebuildCourseNoChargeType> collect = result.stream().filter((RebuildCourseNoChargeType vo) -> vo.getId().longValue() != courseNoCharge.getId().longValue()).collect(Collectors.toList());
                if(CollectionUtil.isNotEmpty(collect)){
                    if(collect.contains(courseNoCharge)){
                        return "common.exist";
                    }
                }
            }

        }
        noChargeTypeDao.updateByPrimaryKeySelective(courseNoCharge);
        return "common.editSuccess";
    }

    /**
    *@Description: 查询未缴费课程名单
    *@Param:
    *@return:
    *@Author: bear
    *@date: 2019/2/13 15:17
    */
    @Override
    public PageResult<RebuildCourseNoChargeList> findCourseNoChargeList(PageCondition<RebuildCoursePaymentCondition > condition) {
        PageHelper.startPage(condition.getPageNum_(), condition.getPageSize_());
        Page<RebuildCourseNoChargeList> courseNoChargeList = courseTakeDao.findCourseNoChargeList(condition.getCondition());
        return new PageResult<>(courseNoChargeList);
    }

    
    /**
    *@Description: 重新汇总名单
    *@Param:
    *@return: 
    *@Author: bear
    *@date: 2019/2/13 16:19
    */
    @Override
    public PageResult<StudentVo> findCourseNoChargeStudentList(PageCondition<RebuildCoursePaymentCondition > condition) {
        PageHelper.startPage(condition.getPageNum_(), condition.getPageSize_());
        Page<StudentVo> courseNoChargeStudentList = courseTakeDao.findCourseNoChargeStudentList(condition.getCondition());
        return new PageResult<>(courseNoChargeStudentList);
    }


    /**
    *@Description: 移动到回收站
    *@Param:
    *@return:
    *@Author: bear
    *@date: 2019/2/14 10:56
    */
    @Override
    public String moveToRecycle(List<RebuildCourseNoChargeList> list) {
        if(CollectionUtil.isEmpty(list)){
            return "common.parameterError";
        }
        //调用退课接口todo
        /**增加到回收站*/
        courseChargeDao.addCourseStudentToRecycle(list);
        return "common.deleteSuccess";
    }


    /**
    *@Description: 查询回收站
    *@Param:
    *@return:
    *@Author: bear
    *@date: 2019/2/14 11:32
    */
    @Override
    public PageResult<RebuildCourseNoChargeList> findRecycleCourse(PageCondition<RebuildCoursePaymentCondition> condition) {
        PageHelper.startPage(condition.getPageNum_(), condition.getPageSize_());
        Page<RebuildCourseNoChargeList> recycleCourse = courseChargeDao.findRecycleCourse(condition.getCondition());
        return new PageResult<>(recycleCourse);
    }

    /**
    *@Description: 从回收站回复数据
    *@Param:
    *@return:
    *@Author: bear
    *@date: 2019/2/14 14:03
    */
    @Override
    public String moveRecycleCourseToNoChargeList(List<RebuildCourseNoChargeList> list) {
        if(CollectionUtil.isEmpty(list)){
            return "common.parameterError";
        }
        //添加到选课表
        courseTakeDao.addCourseTakeFromRecycle(list);
        /**从回收站删除*/
        courseChargeDao.recoveryDataFromRecycleCourse(list);
        return "common.deleteSuccess";
    }

   /**
   *@Description: 导出课程汇总名单
   *@Param: 
   *@return: 
   *@Author: bear
   *@date: 2019/2/20 12:41
   */
    @Override
    public String exportStudentNoChargeCourse(RebuildCoursePaymentCondition condition) throws Exception{
        PageCondition<RebuildCoursePaymentCondition> pageCondition = new PageCondition<RebuildCoursePaymentCondition>();
        pageCondition.setCondition(condition);
        pageCondition.setPageSize_(Constants.ZERO);
        pageCondition.setPageNum_(Constants.ZERO);
        PageResult<StudentVo> result = findCourseNoChargeStudentList(pageCondition);
        if(result!=null){
            List<StudentVo> list = result.getList();
            List<SchoolCalendarVo> schoolCalendarList = BaseresServiceInvoker.getSchoolCalendarList();
            Map<Long, String> schoolCalendarMap = new HashMap<>();
            for (SchoolCalendarVo schoolCalendarVo : schoolCalendarList) {
                schoolCalendarMap.put(schoolCalendarVo.getId(), schoolCalendarVo.getFullName());
            }
            for (StudentVo studentVo : list) {
                if(0!=schoolCalendarMap.size()){
                    String str = schoolCalendarMap.get(studentVo.getCalendarId());
                    if (StringUtils.isNotEmpty(str)) {
                        studentVo.setCalendarName(str);
                    }
                }
            }
            if (list == null) {
                list = new ArrayList<>();
            }
            GeneralExcelDesigner design = getDesignTWo();
            design.setDatas(list);
            ExcelWriterUtil generalExcelHandle;
            generalExcelHandle = GeneralExcelUtil.generalExcelHandle(design);
            FileUtil.mkdirs(cacheDirectory);
            String fileName = "studentNoChargeCourse.xls";
            String path = cacheDirectory + fileName;
            generalExcelHandle.writeExcel(new FileOutputStream(path));
            return fileName;
        }
        return "";
    }

    /**
    *@Description: 导出未缴费课程名单
    *@Param:
    *@return:
    *@Author: bear
    *@date: 2019/2/20 11:17
    */
    @Override
    public String exportNoChargeList(RebuildCoursePaymentCondition condition) throws Exception{
        PageCondition<RebuildCoursePaymentCondition> pageCondition = new PageCondition<RebuildCoursePaymentCondition>();
        pageCondition.setCondition(condition);
        pageCondition.setPageSize_(Constants.ZERO);
        pageCondition.setPageNum_(Constants.ZERO);
        PageResult<RebuildCourseNoChargeList> result = findCourseNoChargeList(pageCondition);
        if(result!=null){
            List<RebuildCourseNoChargeList> list = result.getList();
            List<SchoolCalendarVo> schoolCalendarList = BaseresServiceInvoker.getSchoolCalendarList();
            Map<Long, String> schoolCalendarMap = new HashMap<>();
            for (SchoolCalendarVo schoolCalendarVo : schoolCalendarList) {
                schoolCalendarMap.put(schoolCalendarVo.getId(), schoolCalendarVo.getFullName());
            }
            for (RebuildCourseNoChargeList rebuildCourseNoChargeList : list) {
                if (0 != schoolCalendarMap.size()) {
                    String schoolCalendarName = schoolCalendarMap.get(rebuildCourseNoChargeList.getCalendarId());
                    if (StringUtils.isNotEmpty(schoolCalendarName)) {
                        rebuildCourseNoChargeList.setCalendarName(schoolCalendarName);
                    }
                }
                String format = new DecimalFormat("###################.###########").format(rebuildCourseNoChargeList.getPeriod());
                String s=rebuildCourseNoChargeList.getStartWeek()+"-"+rebuildCourseNoChargeList.getEndWeek()+
                        "周"+format+"课时";
                rebuildCourseNoChargeList.setCourseArr(s);
                if(rebuildCourseNoChargeList.getId()!=null){
                    String str=rebuildCourseNoChargeList.getPaid()==0?"未缴费":"已缴费";
                    rebuildCourseNoChargeList.setStrPaid(str);
                }

            }
            if (list == null) {
                list = new ArrayList<>();
            }
            GeneralExcelDesigner design = getDesign();
            design.setDatas(list);
            ExcelWriterUtil generalExcelHandle;
            generalExcelHandle = GeneralExcelUtil.generalExcelHandle(design);
            FileUtil.mkdirs(cacheDirectory);
            String fileName = "rebuildCourseNoChargeList.xls";
            String path = cacheDirectory + fileName;
            generalExcelHandle.writeExcel(new FileOutputStream(path));
            return fileName;
        }
        return "";
    }

    private GeneralExcelDesigner getDesign() {
        GeneralExcelDesigner design = new GeneralExcelDesigner();
        design.setNullCellValue("");
        design.addCell(I18nUtil.getMsg("exemptionApply.calendarName"), "calendarName");
        design.addCell(I18nUtil.getMsg("exemptionApply.studentCode"), "studentCode");
        design.addCell(I18nUtil.getMsg("exemptionApply.studentName"), "studentName");
        design.addCell(I18nUtil.getMsg("exemptionApply.courseCode"), "code");
        design.addCell(I18nUtil.getMsg("exemptionApply.courseName"), "codeName");
        design.addCell(I18nUtil.getMsg("rebuildCourse.label"), "label");
        design.addCell(I18nUtil.getMsg("rebuildCourse.courseArr"), "courseArr");
        design.addCell(I18nUtil.getMsg("rebuildCourse.credits"), "credits");
        design.addCell(I18nUtil.getMsg("rebuildCourse.isCharge"), "strPaid");
        return design;
    }

    private GeneralExcelDesigner getDesignTWo() {
        GeneralExcelDesigner design = new GeneralExcelDesigner();
        design.setNullCellValue("");
        design.addCell(I18nUtil.getMsg("exemptionApply.calendarName"), "calendarName");
        design.addCell(I18nUtil.getMsg("exemptionApply.studentCode"), "studentCode");
        design.addCell(I18nUtil.getMsg("exemptionApply.studentName"), "name");
        design.addCell(I18nUtil.getMsg("rebuildCourse.sex"), "sex").setValueHandler(
                (value, rawData, cell) -> {
                    return dictionaryService.query("G_XBIE", value, SessionUtils.getLang());
                });
        design.addCell(I18nUtil.getMsg("rebuildCourse.grade"), "grade");
        design.addCell(I18nUtil.getMsg("exemptionApply.faculty"), "faculty").setValueHandler(
                (value, rawData, cell) -> {
                    return dictionaryService.query("X_YX", value, SessionUtils.getLang());
                });

        design.addCell(I18nUtil.getMsg("exemptionApply.major"), "profession").setValueHandler(
                (value, rawData, cell) -> {
                    return dictionaryService.query("G_ZY", value, SessionUtils.getLang());
                });
        design.addCell(I18nUtil.getMsg("rebuildCourse.studentCategory"), "studentCategory");
        design.addCell(I18nUtil.getMsg("rebuildCourse.rebuildNumber"), "rebuildNumber");

        return design;
    }

}
