package com.server.edu.election.service.impl;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.server.edu.common.PageCondition;
import com.server.edu.common.entity.PayResult;
import com.server.edu.common.jackson.JacksonUtil;
import com.server.edu.common.locale.I18nUtil;
import com.server.edu.common.rest.PageResult;
import com.server.edu.common.vo.SchoolCalendarVo;
import com.server.edu.dictionary.DictTypeEnum;
import com.server.edu.dictionary.service.DictionaryService;
import com.server.edu.dictionary.utils.SchoolCalendarCacheUtil;
import com.server.edu.election.config.DoubleHandler;
import com.server.edu.election.constants.Constants;
import com.server.edu.election.dao.*;
import com.server.edu.election.dto.PayResultDto;
import com.server.edu.election.dto.RebuildCourseDto;
import com.server.edu.election.dto.StudentRePaymentDto;
import com.server.edu.election.entity.*;
import com.server.edu.election.rpc.BaseresServiceInvoker;
import com.server.edu.election.service.ElcCourseTakeService;
import com.server.edu.election.service.RebuildCourseChargeService;
import com.server.edu.election.util.CommonConstant;
import com.server.edu.election.util.TableIndexUtil;
import com.server.edu.election.vo.ElcLogVo;
import com.server.edu.election.vo.RebuildCourseNoChargeList;
import com.server.edu.election.vo.RebuildCourseNoChargeTypeVo;
import com.server.edu.election.vo.StudentVo;
import com.server.edu.exception.ParameterValidateException;
import com.server.edu.session.util.SessionUtils;
import com.server.edu.session.util.entity.Session;
import com.server.edu.util.CollectionUtil;
import com.server.edu.util.excel.ExcelWriterUtil;
import com.server.edu.util.excel.GeneralExcelDesigner;
import com.server.edu.util.excel.GeneralExcelUtil;
import com.server.edu.util.excel.export.ExcelExecuter;
import com.server.edu.util.excel.export.ExcelResult;
import com.server.edu.util.excel.export.ExportExcelUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.Date;
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
    private Logger LOG = LoggerFactory.getLogger(RebuildCourseChargeServiceImpl.class);

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

    @Autowired
    private ElcBillDao elcBillDao;

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
        //查询校历时间
        SchoolCalendarVo calendar = SchoolCalendarCacheUtil.getCalendar(condition.getCondition().getCalendarId());
        condition.getCondition().setBeginTime(calendar.getBeginDay());
        condition.getCondition().setEndTime(calendar.getEndDay());
        condition.getCondition().setIndex(TableIndexUtil.getIndex(condition.getCondition().getCalendarId()));
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
            take.setTurn(courseNoChargeList.getTurn());
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
    public List<RebuildCourseNoChargeList> moveRecycleCourseToNoChargeList(List<RebuildCourseNoChargeList> list) {
        if (CollectionUtil.isEmpty(list)) {
            throw new ParameterValidateException("common.parameterError");
        }
        //有冲突的数据集合
        List<RebuildCourseNoChargeList> conflictList = new ArrayList<>();
        for (RebuildCourseNoChargeList noChargeList : list) {
            //判断加课有没有成功
            Boolean aBoolean = recoverClass(noChargeList);
            if (!aBoolean){
                conflictList.add(noChargeList);
            }
        }
        /**从回收站删除*/
        list.removeAll(conflictList);
        if (CollectionUtil.isNotEmpty(list)){
            courseChargeDao.recoveryDataFromRecycleCourse(list);
        }
        return conflictList;
    }

    @Transactional
    private Boolean recoverClass(RebuildCourseNoChargeList noChargeList) {
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
        record.setCalendarId(calendarId);
        int selectCount = courseTakeDao.selectCount(record);
        if (selectCount < 1) {
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
            LOG.info("currentSession Uid ============="+currentSession.getUid());
            log.setCreateBy(currentSession.getUid());
            log.setCreatedAt(new Date());
            log.setCreateIp(currentSession.getIp());
            LOG.info("currentSession ip ============="+currentSession.getIp());
            log.setMode(chooseObj != 1 ? ElcLogVo.MODE_2 : ElcLogVo.MODE_1);
            log.setStudentId(studentCode);
            log.setTeachingClassCode(teachingClassCode);
            log.setTurn(turn);
            log.setType(ElcLogVo.TYPE_1);
            elcLogDao.insertSelective(log);
            return true;
        }else {
            return false;
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
                GeneralExcelDesigner design = null;
                if (condition.getType().intValue() == Constants.ONE){
                    design = getDesignThere();
                }else{
                    design = getDesignElec();
                }

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
        design.addCell("课程序号", "teachingClassCode");
        design.addCell(I18nUtil.getMsg("exemptionApply.courseName"), "courseName");
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
        design.addCell("学分", "credits").setValueHandler(new DoubleHandler());
        design.addCell("是否缴费", "paid").setValueHandler(
                (value, rawData, cell) -> {
                    if("1".equals(value)) {
                        value ="已缴费";
                    }else {
                        value ="未缴费";
                    }
                    return value;
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
        design.addCell("学分", "credits").setValueHandler(new DoubleHandler());
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
        design.addCell(I18nUtil.getMsg("exemptionApply.courseName"), "courseName");
        design.addCell("课程性质", "nature").setValueHandler(
                (value, rawData, cell) -> {
                    if ("1".equals(value)) {
                        value = "公开课";
                    } else {
                        value = "专业课";
                    }
                    return value;
                });
        design.addCell("课程安排", "courseArr");
        design.addCell(I18nUtil.getMsg("rebuildCourse.credits"), "credits").setValueHandler(new DoubleHandler());
        design.addCell("是否缴费", "paid").setValueHandler(
                (value, rawData, cell) -> {
                    if (Constants.PAID.toString().equals(value)) {
                        value = "已缴费";
                    } else if (Constants.UN_PAID.toString().equals(value)){
                        value = "未缴费";
                    }else {
                        value = StringUtils.EMPTY;
                    }
                    return value;
                });
        return design;
    }
    private GeneralExcelDesigner getDesignElec() {
        GeneralExcelDesigner design = new GeneralExcelDesigner();
        design.setNullCellValue("");
        design.addCell(I18nUtil.getMsg("exemptionApply.studentCode"), "studentCode");
        design.addCell(I18nUtil.getMsg("rebuildCourse.grade"), "grade");
        design.addCell(I18nUtil.getMsg("exemptionApply.faculty"), "faculty").setValueHandler(
                (value, rawData, cell) -> {
                    String dict = dictionaryService
                            .query(DictTypeEnum.X_YX.getType(), value);
                    return dict;
                });
        design.addCell(I18nUtil.getMsg("exemptionApply.major"), "profession").setValueHandler(
                (value, rawData, cell) -> {
                    String dict = dictionaryService
                            .query(DictTypeEnum.G_ZY.getType(), value);
                    return dict;
                });
        design.addCell(I18nUtil.getMsg("rebuildCourse.courseIndex"), "teachingClassCode");
        design.addCell(I18nUtil.getMsg("exemptionApply.courseName"), "courseName");
        design.addCell(I18nUtil.getMsg("rollBookManage.courseOpenFaculty"), "courseFaculty").setValueHandler(
                (value, rawData, cell) -> {
                    String dict = dictionaryService
                            .query(DictTypeEnum.X_YX.getType(), value);
                    return dict;
                });
        design.addCell(I18nUtil.getMsg("修读类别"), "courseTakeType").setValueHandler(
                (value, rawData, cell) -> {
                    String dict = dictionaryService
                            .query(DictTypeEnum.X_XDLX.getType(), value);
                    return dict;
                });
        design.addCell(I18nUtil.getMsg("rebuildCourse.turn"), "turn");
        design.addCell("筛选标签", "labelName");

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
    public PageResult<StudentRePaymentDto> findStuRePayment(PageCondition<StudentRePaymentDto> pageCondition) {
        PageResult<StudentRePaymentDto> paymentDtoList=new PageResult<>();
        //String studentCode = studentRePaymentDto.getStudentCode();
        /**是否在不缴费学生类型中*/
        // todo 因为毕业证书类型现在取不到，暂时无法判断是否需要收费
        /*boolean retake = isNoNeedPayForRetake(studentCode);
        if(retake){
            return null;
        }*/
        //去收费标准查询，是否需要缴费
        List<RebuildCourseCharge> rebuildCourseChargeList =  courseChargeDao.selectByStuId(pageCondition.getCondition().getStudentCode());
        if (CollectionUtil.isNotEmpty(rebuildCourseChargeList) && rebuildCourseChargeList.get(0).getIsCharge().equals(1)){
            //查询该学期缴费的课程及缴费状态
            PageHelper.startPage(pageCondition.getPageNum_(), pageCondition.getPageSize_());
            Page<StudentRePaymentDto> page = (Page<StudentRePaymentDto>)courseTakeDao.findByStuIdAndCId(pageCondition.getCondition());
            //设置单价
            if (CollectionUtil.isNotEmpty(page.getResult())){
                page.getResult().forEach(p -> p.setUnitPrice(rebuildCourseChargeList.get(0).getUnitPrice()));
            }
            paymentDtoList = new PageResult<>(page);
        }
        return paymentDtoList;

//        //查询收费单价
//        Student record = new Student();
//        record.setStudentCode(studentCode);
//        Student student = studentDao.selectOne(record);
//        String trainingLevel = student.getTrainingLevel();
//        String formLearning = student.getFormLearning();
//        RebuildCourseCharge prices = courseChargeDao.findPrice(trainingLevel,formLearning);
//        if(prices == null || prices.getIsCharge()==0){
//            return null;
//        }
//
//        for (ElcCourseTakeVo courseTake : courseTakes) {
//            double credits=courseTake.getCredits();
//            int unitPrice= prices.getUnitPrice();
//            double payable =  (unitPrice*credits);
//            StudentRePaymentDto paymentDto=new StudentRePaymentDto();
//            paymentDto.setCourseCode(courseTake.getCourseCode());
//            paymentDto.setCourseName(courseTake.getCourseName());
//            paymentDto.setStudentCode(studentRePaymentDto.getStudentCode());
//            paymentDto.setCredits(credits);
//            paymentDto.setUnitPrice(unitPrice);
//            paymentDto.setCalendarId(studentRePaymentDto.getCalendarId());
//            paymentDto.setPayable(payable);
//            paymentDto.setBillId(courseTake.getBillId());
//            paymentDto.setPaid(courseTake.getPaid());
//            paymentDtoList.add(paymentDto);
//        }
    }

    /**
     * @Description: 根据学号查询重修详情
     * @author kan yuanfeng
     * @date 2019/10/22 11:26
     */
    @Override
    public PageResult<RebuildCourseNoChargeList> findNoChargeListByStuId(PageCondition<RebuildCourseDto> condition) {
        PageHelper.startPage(condition.getPageNum_(), condition.getPageSize_());
               // int mode = TableIndexUtil.getMode(c.getCalendarId());
        condition.getCondition().setIndex(TableIndexUtil.getIndex(condition.getCondition().getCalendarId()));
        Page<RebuildCourseNoChargeList> courseNoChargeList = courseTakeDao.findNoChargeListByStuId(condition.getCondition());
        return new PageResult<>(courseNoChargeList);
    }

    /**
     * @Description: 导出重修详情根据学号
     * @author kan yuanfeng
     * @date 2019/10/22 11:26
     */
    @Override
    public ExcelWriterUtil exportByStuId(RebuildCourseDto rebuildCourseDto) throws Exception {
        rebuildCourseDto.setIndex(TableIndexUtil.getIndex(rebuildCourseDto.getCalendarId()));
        Page<RebuildCourseNoChargeList> list = courseTakeDao.findNoChargeListByStuId(rebuildCourseDto);
        GeneralExcelDesigner design = getDesignByStuId();
        List<JSONObject> convertList = JacksonUtil.convertList(list);
        design.setDatas(convertList);
        ExcelWriterUtil excelUtil = GeneralExcelUtil.generalExcelHandle(design);
        return excelUtil;
    }

    /**
     * @Description: 财务对账(通过账单号)
     * @author kan yuanfeng
     * @date 2019/10/22 11:26
     */
    @Override
    public void payResult(List<RebuildCourseNoChargeList> rebuildCourseNoChargeLists) {
        //财务对接
        if (CollectionUtil.isNotEmpty(rebuildCourseNoChargeLists)){
            //分表规则
            int index = TableIndexUtil.getIndex(rebuildCourseNoChargeLists.get(0).getCalendarId());
            List<Long> billIds = rebuildCourseNoChargeLists.stream().filter(r -> null != r.getBillId()).map(r -> r.getBillId()).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(billIds)){
                //去数据库查询订单号
                Example example = new Example(ElcBill.class);
                Example.Criteria criteria = example.createCriteria();
                criteria.andIn("id",billIds);
                List<ElcBill> elcBills = elcBillDao.selectByExample(example);
                if (CollectionUtil.isNotEmpty(elcBills)){
                    List<String> list = elcBills.stream().map(e -> e.getBillNum()).collect(Collectors.toList());
                    List<PayResult> payResult = BaseresServiceInvoker.getPayResult(list);
                    //解析对账结果
                    List<PayResult> results = payResult.stream().filter(r -> r.getPayFlag() && StringUtils.isNotBlank(r.getPaystate())).collect(Collectors.toList());
                    List<PayResultDto> payResultDtoList = new ArrayList<>();
                    List<ElcBill> elcBillList = new ArrayList<>();
                    results.forEach(r ->{
                        PayResultDto payResultDto = new PayResultDto();
                        BeanUtils.copyProperties(r,payResultDto);
                        payResultDto.setIndex(index);
                        payResultDto.setPaid(("4".equals(payResultDto.getPaystate())?1:0));
                        payResultDtoList.add(payResultDto);
                        //账单金额已缴金额处理
                        ElcBill elcBill = new ElcBill();
                        elcBill.setBillNum(payResultDto.getBillno());
                        elcBill.setFlag(("4".equals(payResultDto.getPaystate())?true:false));
                        elcBillList.add(elcBill);
                    });
                    courseTakeDao.setPayStatusBatch(payResultDtoList);
                    //账单金额已缴金额处理
                    elcBillDao.updatePayBatch(elcBillList);
                }
            }
        }
    }

    /**
     * @Description: 重修缴费回调接口
     * @author kan yuanfeng
     * @date 2019/11/7 9:22
     */
    @Override
    @Transactional
    public void payCallback(JSONObject jsonObject) {
        //参数校验
        /*json.put(CommonConstant.PAY_CODE_BILLNO, queryPay.get(CommonConstant.PAY_CODE_BILLNO));   // 订单号
        json.put(CommonConstant.PAY_CODE_RETURNMSG, queryPay.get(CommonConstant.PAY_CODE_RETURNMSG));  //返回信息
        json.put(CommonConstant.PAY_CODE_PAYSTATE, queryPay.get(CommonConstant.PAY_CODE_PAYSTATE));  //订单状态（1：缴费失败 4：缴费成功）
        json.put(CommonConstant.PAY_CODE_STUID, queryPay.get(CommonConstant.PAY_CODE_STUID));  //学生ID
        json.put(CommonConstant.PAY_CODE_FEEITEMID, queryPay.get(CommonConstant.PAY_CODE_FEEITEMID));  //缴费编码
        json.put(CommonConstant.PAY_CODE_SIGN, queryPay.get(CommonConstant.PAY_CODE_SIGN));  // 签名
        json.put(CommonConstant.PAY_CALLBACKDATA, queryPay.get(CommonConstant.PAY_CALLBACKDATA));  // 回调数据*/
        String billNo = jsonObject.getString(CommonConstant.PAY_CODE_BILLNO);
        String reTurnmsg = jsonObject.getString(CommonConstant.PAY_CODE_RETURNMSG);
        String payState = jsonObject.getString(CommonConstant.PAY_CODE_PAYSTATE);
        String studentId = jsonObject.getString(CommonConstant.PAY_CODE_STUID);
        String callBackData = jsonObject.getString(CommonConstant.PAY_CALLBACKDATA);
        if (StringUtils.isBlank(billNo) || StringUtils.isBlank(payState)
            ||StringUtils.isBlank(studentId) || StringUtils.isBlank(callBackData)){
            throw new ParameterValidateException(I18nUtil.getMsg("common.parameterError"));
        }
        //解析callBackData
        /**
         * callBackData内容
         * put("id":"1,2")
         * put("price":"700")
         */
        Map callDate = JSON.parseObject(callBackData, Map.class);
        String id = (String)callDate.get("id");
        String[] split = StringUtils.split(id, ",");
        ElcCourseTake elcCourseTake = courseTakeDao.selectByPrimaryKey(Long.valueOf(split[0]));
        //保存订单
        ElcBill elcBill = new ElcBill();
        elcBill.setStudentId(studentId);
        elcBill.setCalendarId(elcCourseTake.getCalendarId());
        elcBill.setBillNum(billNo);
        String price = (String) callDate.get("price");
        elcBill.setAmount(Double.valueOf(price));
        elcBill.setRemark(reTurnmsg);
        elcBill.setPay(("4".equals(payState)?Double.valueOf(price):0));
        elcBillDao.insertSelective(elcBill);
        //更新缴费信息
        List<Long> ids = new ArrayList<>();
        for (int i = 0; i <split.length ; i++) {
            ids.add(Long.valueOf(split[i]));
        }
        ElcCourseTake courseTake = new ElcCourseTake();
        courseTake.setPaid(("4".equals(payState)?1:0));
        courseTake.setBillId(elcBill.getId());
        Example example = new Example(ElcCourseTake.class);
        example.createCriteria().andIn("id",ids);
        courseTakeDao.updateByExampleSelective(courseTake,example);
    }

    /**
     * @Description: 缴费订单查看
     * @author kan yuanfeng
     * @date 2019/11/7 16:34
     */
    @Override
    public PageResult<StudentRePaymentDto> payDetail(PageCondition<StudentRePaymentDto> condition) {
        PageHelper.startPage(condition.getPageNum_(), condition.getPageSize_());
        condition.getCondition().setIndex(TableIndexUtil.getIndex(condition.getCondition().getCalendarId()));
        Page<StudentRePaymentDto> page =  (Page<StudentRePaymentDto>)elcBillDao.payDetail(condition.getCondition());
        return new PageResult<>(page);
    }

    /**
     * @Description: 缴费订单查看(订单id)
     * @author kan yuanfeng
     * @date 2019/11/7 16:34
     */
    @Override
    public List<StudentRePaymentDto> payDetailById(Long id) {
        ElcBill elcBill = elcBillDao.selectByPrimaryKey(id);
        StudentRePaymentDto dto = new StudentRePaymentDto();
        dto.setBillId(id);
        dto.setIndex(TableIndexUtil.getIndex(elcBill.getCalendarId()));
        List<StudentRePaymentDto> studentRePaymentDtos = elcBillDao.payDetailById(dto);
        return studentRePaymentDtos;
    }

    private GeneralExcelDesigner getDesignByStuId() {
        GeneralExcelDesigner design = new GeneralExcelDesigner();
        design.setNullCellValue("");
        design.addCell("学号", "studentCode");
        design.addCell("姓名", "studentName");
        design.addCell("培养层次", "trainingLevelI18n");
        design.addCell("课程序号", "teachingClassCode");
        design.addCell("课程名称", "courseName");
        design.addCell("课程性质", "nature").setValueHandler(
                (value, rawData, cell) -> {
                    if ("1".equals(value)) {
                        value = "公开课";
                    } else {
                        value = "专业课";
                    }
                    return value;
                });
        design.addCell("课程安排", "courseArr");
        design.addCell("学分", "credits").setValueHandler(new DoubleHandler());
        design.addCell("是否缴费", "paid").setValueHandler(
                (value, rawData, cell) -> {
                    if (Constants.PAID.toString().equals(value)) {
                        value = "已缴费";
                    } else if (Constants.UN_PAID.toString().equals(value)){
                        value = "未缴费";
                    }else {
                        value = StringUtils.EMPTY;
                    }
                    return value;
                });
        design.addCell("是否选课", "chooseObj").setValueHandler(
                (value, rawData, cell) -> {
                    if (Constants.PAID.toString().equals(value)) {
                        value = "已选课";
                    } else {
                        value = "未选课";
                    }
                    return value;
                });
        return design;
    }
}
