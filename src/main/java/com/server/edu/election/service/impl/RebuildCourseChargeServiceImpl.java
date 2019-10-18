package com.server.edu.election.service.impl;


import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.server.edu.common.PageCondition;
import com.server.edu.common.jackson.JacksonUtil;
import com.server.edu.common.locale.I18nUtil;
import com.server.edu.common.rest.PageResult;
import com.server.edu.dictionary.service.DictionaryService;
import com.server.edu.election.dao.*;
import com.server.edu.election.dto.RebuildCourseDto;
import com.server.edu.election.dto.StudentRePaymentDto;
import com.server.edu.election.entity.*;
import com.server.edu.election.service.ElcCourseTakeService;
import com.server.edu.election.service.RebuildCourseChargeService;
import com.server.edu.election.vo.*;
import com.server.edu.exception.ParameterValidateException;
import com.server.edu.session.util.SessionUtils;
import com.server.edu.session.util.entity.Session;
import com.server.edu.util.CollectionUtil;
import com.server.edu.util.excel.GeneralExcelDesigner;
import com.server.edu.util.excel.export.ExcelExecuter;
import com.server.edu.util.excel.export.ExcelResult;
import com.server.edu.util.excel.export.ExportExcelUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
    private TeachingClassDao classDao;

    @Autowired
    private ElcCourseTakeService courseTakeService;

    @Autowired
    private ElcLogDao elcLogDao;

    @Autowired
    private DictionaryService dictionaryService;

    @Autowired
    private StudentDao studentDao;

    @Value("${cache.directory}")
    private String cacheDirectory;

    /**
     * @Description: 查询收费管理
     * @Param:
     * @return:
     * @Author: bear
     * @date: 2019/2/1 8:58
     */
    @Override
    public PageResult<RebuildCourseCharge> findCourseCharge(
            PageCondition<RebuildCourseCharge> condition) {
        PageHelper.startPage(condition.getPageNum_(), condition.getPageSize_());
        Page<RebuildCourseCharge> courseCharge =
                courseChargeDao.findCourseCharge(condition.getCondition());
        return new PageResult<>(courseCharge);
    }

    /**
     * @Description: 删除重修收费信息
     * @Param:
     * @return:
     * @Author: bear
     * @date: 2019/2/1 9:14
     */
    @Override
    @Transactional
    public void deleteCourseCharge(List<Long> ids) {
        if (CollectionUtil.isEmpty(ids)) {
            throw new ParameterValidateException(I18nUtil.getMsg("common.parameterError"));
        }
        courseChargeDao.deleteCourseCharge(ids);
    }

    /**
     * @Description: 编辑收费信息
     * @Param:
     * @return:
     * @Author: bear
     * @date: 2019/2/1 9:42
     */
    @Override
    @Transactional
    public void editCourseCharge(RebuildCourseCharge courseCharge) {
        courseChargeDao.updateByPrimaryKeySelective(courseCharge);
    }

    /**
     * @Description: 新增收费信息
     * @Param:
     * @return:
     * @Author: bear
     * @date: 2019/2/1 9:50
     */
    @Override
    @Transactional
    public void addCourseCharge(RebuildCourseCharge courseCharge) {
        RebuildCourseCharge item = courseChargeDao.findPrice(courseCharge.getTrainingLevel(),courseCharge.getFormLearning());
        if (item != null) {
            throw new ParameterValidateException(I18nUtil.getMsg("common.exist",I18nUtil.getMsg("rebuildCourse.charge")));
        }
        courseChargeDao.insertSelective(courseCharge);
    }

    /**
     * @Description: 查询重修不收费类型
     * @Param:
     * @return:
     * @Author: bear
     * @date: 2019/2/1 14:02
     */
    @Override
    public PageResult<RebuildCourseNoChargeType> findCourseNoChargeType(
            PageCondition<RebuildCourseNoChargeTypeVo> condition) {
        String manageDptId = SessionUtils.getCurrentSession().getCurrentManageDptId();
        condition.getCondition().setDeptId(manageDptId);
        PageHelper.startPage(condition.getPageNum_(), condition.getPageSize_());
        Page<RebuildCourseNoChargeType> courseNoChargeType =
                noChargeTypeDao.findCourseNoChargeType(condition.getCondition());
        return new PageResult<>(courseNoChargeType);
    }

    /**
     * @Description: 新增重修不收费类型
     * @Param:
     * @return:
     * @Author: bear
     * @date: 2019/2/1 14:19
     */
    @Override
    public void addCourseNoChargeType(RebuildCourseNoChargeType noChargeType) {

        RebuildCourseNoChargeType item= noChargeTypeDao.findTypeByCondition(noChargeType);
        if (item != null) {
            throw new ParameterValidateException(I18nUtil.getMsg("common.exist",I18nUtil.getMsg("rebuildCourse.noCharge")));
        }
        noChargeTypeDao.insertSelective(noChargeType);
    }

    /**
     * @Description: 删除重修不收费类型
     * @Param:
     * @return:
     * @Author: bear
     * @date: 2019/2/1 14:29
     */
    @Override
    public void deleteCourseNoChargeType(List<Long> ids) {
        if (CollectionUtil.isEmpty(ids)) {
            throw new ParameterValidateException(I18nUtil.getMsg("common.parameterError"));
        }
        noChargeTypeDao.deleteRebuildCourseNoChargeType(ids);
    }

    /**
     * @Description: 编辑重修不收费学生类型
     * @Param:
     * @return:
     * @Author: bear
     * @date: 2019/2/1 14:37
     */
    @Override
    public void editCourseNoChargeType(
            RebuildCourseNoChargeType courseNoCharge) {
        RebuildCourseNoChargeType item= noChargeTypeDao.findTypeByCondition(courseNoCharge);
        if(item!=null){
            if(item.getId().intValue()!=courseNoCharge.getId().intValue()){
                throw new ParameterValidateException(I18nUtil.getMsg("common.exist",I18nUtil.getMsg("rebuildCourse.noCharge")));
            }
        }
        noChargeTypeDao.updateByPrimaryKeySelective(courseNoCharge);
    }

    /**
     * @Description: 查询未缴费课程名单
     * @Param:
     * @return:
     * @Author: bear
     * @date: 2019/2/13 15:17
     */
    @Override
    public PageResult<RebuildCourseNoChargeList> findCourseNoChargeList(PageCondition<RebuildCourseDto> condition) {
        String dptId = SessionUtils.getCurrentSession().getCurrentManageDptId();
        condition.getCondition().setDeptId(dptId);
        PageHelper.startPage(condition.getPageNum_(), condition.getPageSize_());
        Page<RebuildCourseNoChargeList> courseNoChargeList = courseTakeDao.findCourseNoChargeList(condition.getCondition());
       /* if (courseNoChargeList != null) {
            List<RebuildCourseNoChargeList> list = courseNoChargeList.getResult();
            for (RebuildCourseNoChargeList rebuildList : list) {
                String courseArr = "";
                DecimalFormat decimalFormat = new DecimalFormat("###################.###########");
                String format = decimalFormat.format(rebuildList.getPeriod());
                courseArr = rebuildList.getStartWeek() + "-" + rebuildList.getEndWeek() + "周" + format + "学时";
                rebuildList.setCourseArr(courseArr);
            }

        }*/
        return new PageResult<>(courseNoChargeList);
    }

    /**
     * @Description: 重新汇总名单
     * @Param:
     * @return:
     * @Author: bear
     * @date: 2019/2/13 16:19
     */
    @Override

    public PageResult<StudentVo> findCourseNoChargeStudentList(PageCondition<RebuildCourseDto> condition) {
        String dptId = SessionUtils.getCurrentSession().getCurrentManageDptId();
        condition.getCondition().setDeptId(dptId);
        PageHelper.startPage(condition.getPageNum_(), condition.getPageSize_());
        Page<StudentVo> courseNoChargeStudentList = courseTakeDao
                .findCourseNoChargeStudentList(condition.getCondition());
        return new PageResult<>(courseNoChargeStudentList);
    }

    /**
     * @Description: 移动到回收站
     * @Param:
     * @return:
     * @Author: bear
     * @date: 2019/2/14 10:56
     */
    @Override
    @Transactional
    public void moveToRecycle(List<RebuildCourseNoChargeList> list) {
        if (CollectionUtil.isEmpty(list)) {
            throw new ParameterValidateException(I18nUtil.getMsg("common.parameterError"));
        }
        //调用退课接口todo
        List<ElcCourseTake> takes = new ArrayList<>();
        for (RebuildCourseNoChargeList courseNoChargeList : list) {
            ElcCourseTake take = new ElcCourseTake();
            take.setStudentId(courseNoChargeList.getStudentCode());
            take.setCalendarId(courseNoChargeList.getCalendarId());
            take.setTeachingClassId(courseNoChargeList.getTeachingClassId());
            takes.add(take);
        }
        courseTakeService.withdraw(takes);
        /**增加到回收站*/
        courseChargeDao.addCourseStudentToRecycle(list);
    }

    /**
     * @Description: 查询回收站
     * @Param:
     * @return:
     * @Author: bear
     * @date: 2019/2/14 11:32
     */
    @Override
    public PageResult<RebuildCourseNoChargeList> findRecycleCourse(PageCondition<RebuildCourseDto> condition) {
        String dptId = SessionUtils.getCurrentSession().getCurrentManageDptId();
        condition.getCondition().setDeptId(dptId);
        PageHelper.startPage(condition.getPageNum_(), condition.getPageSize_());
        Page<RebuildCourseNoChargeList> recycleCourse =
                courseChargeDao.findRecycleCourse(condition.getCondition());
        return new PageResult<>(recycleCourse);
    }

    /**
     * @Description: 从回收站回复数据
     * @Param:
     * @return:
     * @Author: bear
     * @date: 2019/2/14 14:03
     */
    @Override
    @Transactional
    public void moveRecycleCourseToNoChargeList(
            List<RebuildCourseNoChargeList> list) {
        if (CollectionUtil.isEmpty(list)) {
            throw new ParameterValidateException("common.parameterError");
        }
        for (RebuildCourseNoChargeList noChargeList : list) {
            recoverClass(noChargeList);
        }
        /**从回收站删除*/
        courseChargeDao.recoveryDataFromRecycleCourse(list);
    }

    @Transactional
    private void recoverClass(RebuildCourseNoChargeList noChargeList) {
        String studentCode = noChargeList.getStudentCode();
        String courseCode = noChargeList.getCourseCode();
        Long calendarId = noChargeList.getCalendarId();
        Integer chooseObj = noChargeList.getChooseObj();
        Integer courseTakeType = noChargeList.getCourseTakeType();
        Long teachingClassId = noChargeList.getTeachingClassId();
        String teachingClassCode = noChargeList.getTeachingClassCode();
        Integer mode = noChargeList.getMode();
        Integer turn = noChargeList.getTurn();
        String courseName = noChargeList.getCourseName();
        ElcCourseTake record = new ElcCourseTake();
        record.setStudentId(studentCode);
        record.setCourseCode(courseCode);
        int selectCount = courseTakeDao.selectCount(record);
        if (selectCount == 0) {
            ElcCourseTake take = new ElcCourseTake();
            take.setCalendarId(calendarId);
            take.setChooseObj(chooseObj);
            take.setCourseCode(courseCode);
            take.setCourseTakeType(courseTakeType);
            take.setCreatedAt(new Date());
            take.setStudentId(studentCode);
            take.setTeachingClassId(teachingClassId);
            take.setMode(mode);
            take.setTurn(turn);
            courseTakeDao.insertSelective(take);
            //增加选课人数
            classDao.increElcNumber(teachingClassId);
            ElcLog log = new ElcLog();
            log.setCalendarId(calendarId);
            log.setCourseCode(courseCode);
            log.setCourseName(courseName);
            Session currentSession = SessionUtils.getCurrentSession();
            log.setCreateBy(currentSession.getUid());
            log.setCreatedAt(new Date());
            log.setCreateIp(currentSession.getIp());
            log.setMode(chooseObj != 1 ? ElcLogVo.MODE_2 : ElcLogVo.MODE_1);
            log.setStudentId(studentCode);
            log.setTeachingClassCode(teachingClassCode);
            log.setTurn(turn);
            log.setType(ElcLogVo.TYPE_1);
            elcLogDao.insertSelective(log);
        }
    }


    /**
     * @Description: 导出重修缴费名单
     * @Param:
     * @return:
     * @Author: bear
     * @date: 2019/5/20 9:48
     */
    @Override
    public ExcelResult export(RebuildCourseDto condition) {
        ExcelResult excelResult = ExportExcelUtils.submitTask("rebuildNoCharge", new ExcelExecuter() {
            @Override
            public GeneralExcelDesigner getExcelDesigner() {
                ExcelResult result = this.getResult();
                PageCondition<RebuildCourseDto> pageCondition = new PageCondition<>();
                pageCondition.setCondition(condition);
                pageCondition.setPageSize_(100);
                int pageNum = 0;
                List<RebuildCourseNoChargeList> list = new ArrayList<>();
                while (true) {
                    pageNum++;
                    pageCondition.setPageNum_(pageNum);
                    PageResult<RebuildCourseNoChargeList> courseNoChargeList = findCourseNoChargeList(pageCondition);
                    list.addAll(courseNoChargeList.getList());
                    result.setTotal((int) courseNoChargeList.getTotal_());
                    Double count = list.size() / 1.5;
                    result.setDoneCount(count.intValue());
                    this.updateResult(result);
                    if (courseNoChargeList.getTotal_() <= list.size()) {
                        break;
                    }
                }
                //组装excel
                GeneralExcelDesigner design = getDesign();
                //将数据放入excel对象中
                design.setDatas(list);
                result.setDoneCount(list.size());
                return design;
            }
        });
        return excelResult;
    }

    /**
     * @Description: 导出汇总名单
     * @Param:
     * @return:
     * @Author: bear
     * @date: 2019/5/24 10:14
     */
    @Override
    public ExcelResult exportStuNumber(RebuildCourseDto condition) {
        ExcelResult excelResult = ExportExcelUtils.submitTask("rebuildNoChargeStuNumber", new ExcelExecuter() {
            @Override
            public GeneralExcelDesigner getExcelDesigner() {
                ExcelResult result = this.getResult();
                PageCondition<RebuildCourseDto> pageCondition = new PageCondition<>();
                pageCondition.setCondition(condition);
                pageCondition.setPageSize_(100);
                int pageNum = 0;
                List<StudentVo> list = new ArrayList<>();
                while (true) {
                    pageNum++;
                    pageCondition.setPageNum_(pageNum);
                    PageResult<StudentVo> studentList = findCourseNoChargeStudentList(pageCondition);
                    list.addAll(studentList.getList());
                    result.setTotal((int) studentList.getTotal_());
                    Double count = list.size() / 1.5;
                    result.setDoneCount(count.intValue());
                    this.updateResult(result);

                    if (studentList.getTotal_() <= list.size()) {
                        break;
                    }

                }
                //组装excel
                GeneralExcelDesigner design = getDesignTWo();
                //将数据放入excel对象中
                List<JSONObject> convertList = JacksonUtil.convertList(list);
                design.setDatas(convertList);
                result.setDoneCount(list.size());
                return design;
            }
        });
        return excelResult;
    }

    /**
     * @Description: 导出回收站
     * @Param:
     * @return:
     * @Author: bear
     * @date: 2019/5/24 11:12
     */
    @Override
    public ExcelResult exportRecycle(RebuildCourseDto condition) {
        ExcelResult excelResult = ExportExcelUtils.submitTask("rebuildRecycle", new ExcelExecuter() {
            @Override
            public GeneralExcelDesigner getExcelDesigner() {
                ExcelResult result = this.getResult();
                PageCondition<RebuildCourseDto> pageCondition = new PageCondition<>();
                pageCondition.setCondition(condition);
                pageCondition.setPageSize_(100);
                int pageNum = 0;
                List<RebuildCourseNoChargeList> list = new ArrayList<>();
                while (true) {
                    pageNum++;
                    pageCondition.setPageNum_(pageNum);
                    PageResult<RebuildCourseNoChargeList> recycleCourse = findRecycleCourse(pageCondition);
                    list.addAll(recycleCourse.getList());
                    result.setTotal((int) recycleCourse.getTotal_());
                    Double count = list.size() / 1.5;
                    result.setDoneCount(count.intValue());
                    this.updateResult(result);

                    if (recycleCourse.getTotal_() <= list.size()) {
                        break;
                    }

                }
                //组装excel
                GeneralExcelDesigner design = getDesignThere();
                //将数据放入excel对象中
                design.setDatas(list);
                result.setDoneCount(list.size());
                return design;
            }
        });
        return excelResult;
    }


    private GeneralExcelDesigner getDesign() {
        GeneralExcelDesigner design = new GeneralExcelDesigner();
        design.setNullCellValue("");
        design.addCell(I18nUtil.getMsg("exemptionApply.studentCode"), "studentCode");
        design.addCell(I18nUtil.getMsg("exemptionApply.studentName"), "studentName");
        design.addCell(I18nUtil.getMsg("exemptionApply.courseCode"), "courseCode");
        design.addCell(I18nUtil.getMsg("exemptionApply.courseName"), "courseName");
        design.addCell(I18nUtil.getMsg("rebuildCourse.label"), "label");
        design.addCell(I18nUtil.getMsg("rebuildCourse.courseArr"), "courseArr");
        design.addCell(I18nUtil.getMsg("rebuildCourse.credits"), "credits");
        design.addCell(I18nUtil.getMsg("rebuildCourse.isCharge"), "paid")
                .setValueHandler((value, rawData, cell) -> {
                    return "0".equals(value) ? "未缴费" : "已缴费";
                });
        return design;
    }

    private GeneralExcelDesigner getDesignTWo() {
        GeneralExcelDesigner design = new GeneralExcelDesigner();
        design.setNullCellValue("");
        design.addCell(I18nUtil.getMsg("exemptionApply.studentCode"), "studentCode");
        design.addCell(I18nUtil.getMsg("exemptionApply.studentName"), "studentName");
        design.addCell("培养层次", "trainingLevelI18n");
        design.addCell("课程序号", "teachingClassCode");
        design.addCell("课程名称", "courseName");
        design.addCell("课程性质", "nature").setValueHandler(
                (value, rawData, cell) -> {
                    if("1".equals(value)) {
                        value ="公开课";
                    }else {
                        value ="专业课";
                    }
                    return value;
                });
        design.addCell("课程安排", "courseArr");
        design.addCell("学分", "credits");
        design.addCell("是否缴费", "paid").setValueHandler(
                (value, rawData, cell) -> {
                    if("1".equals(value)) {
                        value ="已缴费";
                    }else {
                        value ="未缴费";
                    }
                    return value;
                });
        design.addCell("是否选课", "credits").setValueHandler((value, rawData, cell) -> {
                    return "已选课";
                });
        return design;
}

    private GeneralExcelDesigner getDesignThere() {
        GeneralExcelDesigner design = new GeneralExcelDesigner();
        design.setNullCellValue("");
        design.addCell(I18nUtil.getMsg("exemptionApply.studentCode"), "studentCode");
        design.addCell(I18nUtil.getMsg("exemptionApply.studentName"), "studentName");
        design.addCell(I18nUtil.getMsg("rebuildCourse.courseIndex"), "teachingClassCode");
        design.addCell(I18nUtil.getMsg("exemptionApply.courseCode"), "courseCode");
        design.addCell(I18nUtil.getMsg("exemptionApply.courseName"), "courseName");
        design.addCell(I18nUtil.getMsg("rebuildCourse.label"), "label");
        design.addCell(I18nUtil.getMsg("rebuildCourse.credits"), "credits");
        design.addCell(I18nUtil.getMsg("rebuildCourse.revisionategory"), "courseTakeType").setValueHandler(
                (value, rawData, cell) -> {
                    return dictionaryService.query("X_XDLX", value, SessionUtils.getLang());
                });
        return design;
    }

    @Override
    public boolean isNoNeedPayForRetake(String studentId) {
        Student record = new Student();
        record.setStudentCode(studentId);
        Student student = studentDao.selectOne(record);
        List<RebuildCourseNoChargeType> list = noChargeTypeDao.selectAll();
        for (RebuildCourseNoChargeType t : list) {
            if (t.getFormLearning().equals(student.getFormLearning())
                    && t.getRegistrationStatus().equals(student.getRegistrationStatus())
                    && t.getSpcialPlan().equals(student.getSpcialPlan())
                    && t.getTrainingLevel().equals(student.getTrainingLevel())) {
                return true;
            }
        }
        return false;
    }

    /**
    *@Description: 查询学生应缴费用
    *@Param:
    *@return: 
    *@Author: bear
    *@date: 2019/5/27 11:15
    */
    @Override
    public List<StudentRePaymentDto> findStuRePayment(StudentRePaymentDto studentRePaymentDto) {
        List<StudentRePaymentDto> paymentDtoList=new ArrayList<>();
        String studentCode = studentRePaymentDto.getStudentCode();
        /**是否在不缴费学生类型中*/
        boolean retake = isNoNeedPayForRetake(studentCode);
        if(retake){
            return null;
        }
        //不再不缴费学生类型中，查询重修课程并且判断是否需要缴费
        List<ElcCourseTakeVo> courseTakes=courseTakeDao.findStuRebuildCourse(studentRePaymentDto);
        //没有重修课程
        if(CollectionUtil.isEmpty(courseTakes)){
            return null;
        }
        //查询收费单价
        Student record = new Student();
        record.setStudentCode(studentCode);
        Student student = studentDao.selectOne(record);
        String trainingLevel = student.getTrainingLevel();
        String formLearning = student.getFormLearning();
        RebuildCourseCharge prices = courseChargeDao.findPrice(trainingLevel,formLearning);
        if(prices == null || prices.getIsCharge()==0){
            return null;
        }

        for (ElcCourseTakeVo courseTake : courseTakes) {
            double credits=courseTake.getCredits();
            int unitPrice= prices.getUnitPrice();
            double payable =  (unitPrice*credits);
            StudentRePaymentDto paymentDto=new StudentRePaymentDto();
            paymentDto.setCourseCode(courseTake.getCourseCode());
            paymentDto.setCourseName(courseTake.getCourseName());
            paymentDto.setStudentCode(studentRePaymentDto.getStudentCode());
            paymentDto.setCredits(credits);
            paymentDto.setUnitPrice(unitPrice);
            paymentDto.setCalendarId(studentRePaymentDto.getCalendarId());
            paymentDto.setPayable(payable);
            paymentDto.setBillId(courseTake.getBillId());
            paymentDto.setPaid(courseTake.getPaid());
            paymentDtoList.add(paymentDto);
        }

        return paymentDtoList;
    }

}
