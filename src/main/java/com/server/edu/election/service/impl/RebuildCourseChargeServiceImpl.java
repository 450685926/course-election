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
import com.server.edu.election.constants.ChooseObj;
import com.server.edu.election.constants.Constants;
import com.server.edu.election.dao.*;
import com.server.edu.election.dto.*;
import com.server.edu.election.entity.*;
import com.server.edu.election.rpc.BaseresServiceInvoker;
import com.server.edu.election.service.ElcCourseTakeService;
import com.server.edu.election.service.ElcRebuildFeeStatisticsService;
import com.server.edu.election.service.RebuildCourseChargeService;
import com.server.edu.election.studentelec.event.ElectLoadEvent;
import com.server.edu.election.studentelec.service.cache.TeachClassCacheService;
import com.server.edu.election.util.CommonConstant;
import com.server.edu.election.util.TableIndexUtil;
import com.server.edu.election.vo.*;
import com.server.edu.exam.rpc.BaseresServiceExamInvoker;
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
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.*;
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
    private TeachClassCacheService teachClassCacheService;

    @Autowired
    private ApplicationContext applicationContext;

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

    @Autowired
    private RebuildCourseRecycleDao courseRecycleDao;

    @Autowired
    private ElcRebuildFeeStatisticsService feeStatisticsService;

    @Autowired
    private ElecRoundsDao elecRoundsDao;

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

    /**@Description: 删除选课日志
     *
     * 优先根据ids删除，如果ids为空，在根据calendarId，electionObj，部门id，turn删除，
     * 如果ids为空，那么calendarId，electionObj，部门id，turn不能为空
     * @param ids 选课日志id
     * @param calendarId 学期id
     * @param turn 选课伦次
     */
    @Override
    @Transactional
    public void deleteRecycleCourse(List<Long> ids,Long calendarId,Long turn,String electionObj) {
        if (CollectionUtil.isNotEmpty(ids)) {
            courseChargeDao.deleteRecycleCourse(ids);
            return;
        }
        Session currentSession = SessionUtils.getCurrentSession();
        String dptId = currentSession.getCurrentManageDptId();
        //calendarId和turn必须要有值
        if(null==calendarId||null==turn){
            throw new ParameterValidateException(I18nUtil.getMsg("common.parameterError"));
        }
        int pageNum = 0;
        List<RebuildCourseNoChargeList> list = new ArrayList<>();
        PageCondition<RebuildCourseDto> pageCondition = new PageCondition<>();
        RebuildCourseDto rebuildCourseDto = new RebuildCourseDto();
        rebuildCourseDto.setCalendarId(calendarId);
        rebuildCourseDto.setTurn(Integer.valueOf(String.valueOf(turn)));
        rebuildCourseDto.setElectionObj(StringUtils.isEmpty(electionObj)?null:electionObj);
        rebuildCourseDto.setDeptId(dptId);
        pageCondition.setCondition(rebuildCourseDto);
        while (true) {
            pageNum++;
            pageCondition.setPageNum_(pageNum);
            PageResult<RebuildCourseNoChargeList> courseNoChargeList = findRecycleCourse(pageCondition);
            list.addAll(courseNoChargeList.getList());
            if (courseNoChargeList.getTotal_() <= list.size()) {
                break;
            }
        }
        List<Long> idList = list.stream().filter(Objects::nonNull).map(RebuildCourseNoChargeList::getId).collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(idList)) {
            courseChargeDao.deleteRecycleCourse(idList);
        }

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

        List<RebuildCourseNoChargeType> item= noChargeTypeDao.findTypeByCondition(noChargeType);
        if (CollectionUtil.isNotEmpty(item) && item.contains(noChargeType)) {
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
        List<RebuildCourseNoChargeType> list= noChargeTypeDao.findTypeByCondition(courseNoCharge);
        if(CollectionUtil.isNotEmpty(list)){
            Map<Boolean, List<RebuildCourseNoChargeType>> map = list.stream().collect(Collectors.partitioningBy(vo -> vo.getId().equals(courseNoCharge.getId())));
            List<RebuildCourseNoChargeType> otherId = map.get(false);
            if(CollectionUtil.isNotEmpty(otherId) && otherId.contains(courseNoCharge)){

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

        RebuildCourseDto dto = condition.getCondition();
        //查询校历时间
        SchoolCalendarVo calendar = SchoolCalendarCacheUtil.getCalendar(dto.getCalendarId());
        //获取上学期
        SchoolCalendarVo preTerm = BaseresServiceExamInvoker.getPreOrNextTerm(dto.getCalendarId(), false);
        dto.setIndex(TableIndexUtil.getIndex(dto.getCalendarId()));
        List<RebuildCourseNoChargeType> noStuPay = new ArrayList<>();
        //结业和留学的都得收费
        if(Constants.THREE_MODE.equals(dto.getMode()) || Constants.FOUR_MODE.equals(dto.getMode())){

        }else{
            noStuPay = noChargeTypeDao.selectAll();
        }
        dto.setNoStuPay(noStuPay);
        dto.setDeptId(dptId);
        dto.setAbnormalEndTime(calendar.getEndDay());
        dto.setAbnormalStartTime(preTerm.getBeginDay());
        PageHelper.startPage(condition.getPageNum_(), condition.getPageSize_());
        Page<RebuildCourseNoChargeList> courseNoChargeList = courseTakeDao.findCourseNoChargeList(dto);

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
        RebuildCourseDto rebuildCourseDto = condition.getCondition();
        rebuildCourseDto.setDeptId(dptId);
        if(rebuildCourseDto.getCalendarId() != null){
            SchoolCalendarVo calendar = SchoolCalendarCacheUtil.getCalendar(rebuildCourseDto.getCalendarId());
            Integer year = calendar.getYear();
            Integer term = calendar.getTerm();
            if(term.intValue() == 2){
                year = year + 1 ;
                rebuildCourseDto.setYear(year);
            }
            if(term.intValue() == 1){
                rebuildCourseDto.setYear(year);
                rebuildCourseDto.setSemester(term);
            }
        }
        PageHelper.startPage(condition.getPageNum_(), condition.getPageSize_());
        /*Page<StudentVo> courseNoChargeStudentList = courseTakeDao
                .findCourseNoChargeStudentList(condition.getCondition());*/
        Page<StudentVo> courseNoChargeStudentList = courseTakeDao.ListRebuildCourseNumber(rebuildCourseDto);
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
        RebuildCourseDto condition1 = condition.getCondition();
        if (StringUtils.isEmpty(condition1.getDeptId())){
            Session currentSession = SessionUtils.getCurrentSession();
            String dptId = currentSession.getCurrentManageDptId();
            condition1.setDeptId(dptId);
        }
        PageHelper.startPage(condition.getPageNum_(), condition.getPageSize_());
        Page<RebuildCourseNoChargeList> recycleCourse =
                courseChargeDao.findRecycleCourse(condition1);
        return new PageResult<>(recycleCourse);
    }

    /**
     * 查询轮次
     */
    @Override
    public List<RebuildCourseDto> selectTurn(RebuildCourseDto rebuildCourseDto) {
        String dptId = SessionUtils.getCurrentSession().getCurrentManageDptId();
        rebuildCourseDto.setDeptId(dptId);
        return courseChargeDao.selectTurn(rebuildCourseDto);
    }

    /**
     * 查询筛选标签
     */
    @Override
    public List<String> selectLabelName(Long calendarId) {
        return courseChargeDao.selectLabelName(calendarId);
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
            // 更新缓存中教学班人数
            teachClassCacheService.updateTeachingClassNumber(teachingClassId);
            //ElecContextUtil.updateSelectedCourse(calendarId, studentId);
            applicationContext
                    .publishEvent(new ElectLoadEvent(calendarId, studentCode));
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
                List<Long> ids = condition.getIds();
                if(CollectionUtil.isEmpty(ids)){
                    while (true) {
                        pageNum++;
                        pageCondition.setPageNum_(pageNum);
                        PageResult<RebuildCourseNoChargeList> recycleCourse = findRecycleCourse(pageCondition);
                        list.addAll(recycleCourse.getList());
                        result.setTotal((int) recycleCourse.getTotal_());
                        Double count = list.size() / 1.5;
                        this.updateResult(result);
                        result.setDoneCount(count.intValue());

                        if (recycleCourse.getTotal_() <= list.size()) {
                            break;
                        }

                    }
                }else{
                    for(Long id :ids){
                        pageCondition.getCondition().setId(id);
                        PageResult<RebuildCourseNoChargeList> recycleCourse = findRecycleCourse(pageCondition);
                        list.addAll(recycleCourse.getList());
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
        design.addCell("缴费状态", "paid").setValueHandler(
                (value, rawData, cell) -> {
                    if("1".equals(value)) {
                        value ="已缴费";
                    }else if("0".equals(value)){
                        value ="未缴费";
                    }else{
                        value ="无需缴费";
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
        design.addCell("缴费状态", "paid").setValueHandler(
                (value, rawData, cell) -> {
                    if (Constants.PAID.toString().equals(value)) {
                        value = "已缴费";
                    } else if (Constants.UN_PAID.toString().equals(value)){
                        value = "未缴费";
                    }else {
                        value = "无需缴费";
                    }
                    return value;
                });
        return design;
    }
    private GeneralExcelDesigner getDesignElec() {
        GeneralExcelDesigner design = new GeneralExcelDesigner();
        design.setNullCellValue("");
        design.addCell(I18nUtil.getMsg("exemptionApply.studentCode"), "studentCode");
        design.addCell(I18nUtil.getMsg("exemptionApply.studentName"), "studentName");
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
    public boolean isNoNeedPayForRetake(String studentId,Long calendarId) {
        Student record = new Student();
        record.setStudentCode(studentId);
        Student student = studentDao.selectOne(record);
        if(student == null){
            throw new ParameterValidateException("没有查到该学生，请用正确学生登陆");
        }
        List<RebuildCourseNoChargeType> list = noChargeTypeDao.selectAll();
        if(CollectionUtil.isNotEmpty(list)){
            List<String> collect = list.stream().filter(vo ->StringUtils.isNotBlank(vo.getRegistrationStatus())).map(RebuildCourseNoChargeType::getRegistrationStatus).collect(Collectors.toList());
            List<StudentRebuildFeeVo> abnormalStu = new ArrayList<>();
            if(CollectionUtil.isNotEmpty(collect)){

                SchoolCalendarVo calendar = SchoolCalendarCacheUtil.getCalendar(calendarId);
                SchoolCalendarVo preTerm = BaseresServiceExamInvoker.getPreOrNextTerm(calendarId, false);

                //查找当前学期 以及上学期内异动学生
                Long oneYearTime = calendar.getEndDay();
                Long oneYearAgo = preTerm.getBeginDay();
                //不收费类型 中编级 不能有转专业的，此处先查询，下面判断
                if(collect.contains("300015")){
                    collect.add("300006");
                }
                abnormalStu = noChargeTypeDao.getAbnormalStudentByOne(collect,oneYearAgo,oneYearTime,studentId);
            }
            for (RebuildCourseNoChargeType t : list) {
                String trainingLevel = t.getTrainingLevel();
                String trainingCategory = t.getTrainingCategory();
                String enrolMethods = t.getEnrolMethods();
                String spcialPlan = t.getSpcialPlan();
                String isOverseas = t.getIsOverseas();
                String registrationStatus = t.getRegistrationStatus();
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
                    if(CollectionUtil.isEmpty(abnormalStu)){
                        continue;
                    }else{
                        List<String> stringList = abnormalStu.stream().map(StudentRebuildFeeVo::getRegistrationStatus).collect(Collectors.toList());

                        if(!stringList.contains(registrationStatus)){
                            continue;
                        }else{
                            //编级（300015 300006 需要收费）
                            if(registrationStatus.equals("300015") && stringList.contains("300006")){
                                continue;
                            }
                        }
                    }

                }
                return true;

            }
            return false;
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
        StudentRePaymentDto paymentDto = pageCondition.getCondition();
        String studentCode = paymentDto.getStudentCode();
        /**是否在不缴费学生类型中*/
        //判断是否是结业 （结业 和留学结业 都要收费）
        Boolean graduation = isGraduation(studentCode);
        if(!graduation){
            boolean retake = isNoNeedPayForRetake(studentCode,paymentDto.getCalendarId());
            if(retake){
                return null;
            }
        }
        paymentDto.setGraduation(graduation);

        //去收费标准查询，是否需要缴费
        List<RebuildCourseCharge> rebuildCourseChargeList =  courseChargeDao.selectByStuId(paymentDto.getStudentCode());
        if (CollectionUtil.isNotEmpty(rebuildCourseChargeList) && rebuildCourseChargeList.get(0).getIsCharge().equals(1)){
            //查询该学期缴费的课程及缴费状态
            PageHelper.startPage(pageCondition.getPageNum_(), pageCondition.getPageSize_());
            Page<StudentRePaymentDto> page = (Page<StudentRePaymentDto>)courseTakeDao.findByStuIdAndCId(paymentDto);
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

    //判断是否是结业生
    private Boolean isGraduation(String studentCode) {
        StudentDto studentType = elecRoundsDao.findStudentRoundType(studentCode);
        if(studentType != null){
            if(StringUtils.isNotBlank(studentType.getGraduateStudent()) ||
                    StringUtils.isNotBlank(studentType.getInternationalGraduates())){
                return true;
            }
        }else{
            throw new ParameterValidateException("该学生不存在");
        }
        return false;
    }

    /**
     * @Description: 根据学号查询重修详情
     * @author kan yuanfeng
     * @date 2019/10/22 11:26
     */
    @Override
    public PageResult<RebuildCourseNoChargeList> findNoChargeListByStuId(PageCondition<RebuildCourseDto> condition) {
        RebuildCourseDto rebuildCourseDto = condition.getCondition();
        boolean retake = isNoNeedPayForRetake(rebuildCourseDto.getStudentId(),rebuildCourseDto.getCalendarId());
        PageHelper.startPage(condition.getPageNum_(), condition.getPageSize_());
               // int mode = TableIndexUtil.getMode(c.getCalendarId());
        rebuildCourseDto.setIndex(TableIndexUtil.getIndex(rebuildCourseDto.getCalendarId()));
        Page<RebuildCourseNoChargeList> courseNoChargeList = courseTakeDao.findNoChargeListByStuId(rebuildCourseDto);
        if(CollectionUtil.isNotEmpty(courseNoChargeList)){
            for (RebuildCourseNoChargeList rebuildCourseNoChargeList : courseNoChargeList) {
                if(retake){
                    //无需缴费类型
                    rebuildCourseNoChargeList.setPaid(2);
                }
            }
        }
        return new PageResult<>(courseNoChargeList);
    }

    /**
     * @Description: 导出重修详情根据学号
     * @author kan yuanfeng
     * @date 2019/10/22 11:26
     */
    @Override
    public ExcelWriterUtil exportByStuId(RebuildCourseDto rebuildCourseDto) throws Exception {
        boolean retake = isNoNeedPayForRetake(rebuildCourseDto.getStudentId(),rebuildCourseDto.getCalendarId());
        rebuildCourseDto.setIndex(TableIndexUtil.getIndex(rebuildCourseDto.getCalendarId()));
        Page<RebuildCourseNoChargeList> list = courseTakeDao.findNoChargeListByStuId(rebuildCourseDto);
        for (RebuildCourseNoChargeList rebuildCourseNoChargeList : list) {
            if(retake){
                //无需缴费类型
                rebuildCourseNoChargeList.setPaid(2);
            }
        }
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
            List<Long> ids = rebuildCourseNoChargeLists.stream().map(r -> r.getId()).collect(Collectors.toList());
            //根据id学期与数据id去base库查询缴费状态
            List<PayOrderDto> orderDtos = elcBillDao.findToBaseById(ids);
            //分表规则
            int index = TableIndexUtil.getIndex(rebuildCourseNoChargeLists.get(0).getCalendarId());
            //全部的订单号集合
            List<String> allList = new ArrayList<>();
            //从页面传下来的订单id
            List<String> list = new ArrayList<>();
            List<Long> billIds = rebuildCourseNoChargeLists.stream().filter(r -> null != r.getBillId()).map(r -> r.getBillId()).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(billIds)) {
                //去数据库查询订单号
                Example example = new Example(ElcBill.class);
                Example.Criteria criteria = example.createCriteria();
                criteria.andIn("id", billIds);
                List<ElcBill> elcBills = elcBillDao.selectByExample(example);
                if (CollectionUtil.isNotEmpty(elcBills)) {
                    list = elcBills.stream().map(e -> e.getBillNum()).collect(Collectors.toList());
                    if (CollectionUtil.isNotEmpty(orderDtos)) {
                        //过滤掉从页面查询过来的订单
                        List<String> finalList = list;
                        orderDtos = orderDtos.stream().filter(o -> !finalList.contains(o.getOrderNo())).collect(Collectors.toList());
                    }
                }
            }
            if (CollectionUtil.isNotEmpty(orderDtos)){
                //创建订单，以及更新选课数据
                this.setOrder(orderDtos);
                //把从页面传过来的和base获取的订单号全部去财务对账
                orderDtos.forEach(d ->{
                    allList.add(d.getOrderNo());
                });
            }
            allList.addAll(list);
            List<PayResult> payResult = BaseresServiceInvoker.getPayResult(allList);
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

            if(CollectionUtil.isNotEmpty(payResultDtoList)){
                courseTakeDao.setPayStatusBatch(payResultDtoList);
            }
            //账单金额已缴金额处理
            if(CollectionUtil.isNotEmpty(elcBillList)){
                elcBillDao.updatePayBatch(elcBillList);
            }
        }
    }

    private void setOrder(List<PayOrderDto> dtos) {
        dtos.forEach(d ->{
            ElcCourseTake elcCourseTake = courseTakeDao.selectByPrimaryKey(d.getBusIds().split(",")[0]);
            //保存订单
            ElcBill elcBill = new ElcBill();
            elcBill.setStudentId(d.getStudentId());
            elcBill.setCalendarId(elcCourseTake.getCalendarId());
            elcBill.setBillNum(d.getOrderNo());
            elcBill.setAmount(Double.valueOf(d.getAmount()));
            elcBill.setPay(("4".equals(d.getPayStatus())?Double.valueOf(d.getAmount()):0));
            elcBillDao.save(elcBill);
            //elcBillDao.insertSelective(elcBill);
            System.out.println("返回的id======"+elcBill.getId());
            //更新缴费信息
            ElcCourseTake courseTake = new ElcCourseTake();
            courseTake.setPaid(("4".equals(d.getPayStatus())?1:0));
            courseTake.setBillId(elcBill.getId());
            Example example = new Example(ElcCourseTake.class);
            example.createCriteria().andIn("id",Arrays.asList(d.getBusIds().split(",")));
            courseTakeDao.updateByExampleSelective(courseTake,example);
        });
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
        design.addCell("缴费状态", "paid").setValueHandler(
                (value, rawData, cell) -> {
                    if (Constants.PAID.toString().equals(value)) {
                        value = "已缴费";
                    } else if (Constants.UN_PAID.toString().equals(value)){
                        value = "未缴费";
                    }else {
                        value = "无需缴费";
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

    @Override
    public boolean hasRetakeCourseNoPay(Long calendarId, String studentId) {
        Example ex = new Example(RebuildCourseRecycle.class);
        ex.createCriteria().andEqualTo("calendarId",calendarId);
        ex.createCriteria().andEqualTo("studentCode",studentId);
        List<RebuildCourseRecycle> rebuildCourseRecycles = courseRecycleDao.selectByExample(ex);
        for (RebuildCourseRecycle rebuildCourseRecycle :rebuildCourseRecycles){
            if (rebuildCourseRecycle.getPaid().intValue() == Constants.ZERO && rebuildCourseRecycle.getType().intValue() == Constants.ONE){
                return true;
            }
        }
        return false;
    }
}
