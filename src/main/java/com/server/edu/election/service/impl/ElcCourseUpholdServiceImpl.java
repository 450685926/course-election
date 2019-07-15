package com.server.edu.election.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.server.edu.common.PageCondition;
import com.server.edu.common.ServicePathEnum;
import com.server.edu.common.locale.I18nUtil;
import com.server.edu.common.rest.PageResult;
import com.server.edu.common.rest.RestResult;
import com.server.edu.dictionary.service.DictionaryService;
import com.server.edu.election.dao.ElcCourseTakeDao;
import com.server.edu.election.dao.ElcLogDao;
import com.server.edu.election.dto.*;
import com.server.edu.election.entity.ElcCourseTake;
import com.server.edu.election.entity.ElcLog;
import com.server.edu.election.service.ElcCourseUpholdService;
import com.server.edu.election.vo.ElcStudentVo;
import com.server.edu.session.util.SessionUtils;
import com.server.edu.util.CalUtil;
import com.server.edu.util.CollectionUtil;
import com.server.edu.util.FileUtil;
import com.server.edu.util.excel.ExcelWriterUtil;
import com.server.edu.util.excel.GeneralExcelDesigner;
import com.server.edu.util.excel.GeneralExcelUtil;
import org.apache.servicecomb.provider.springmvc.reference.RestTemplateBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.io.FileOutputStream;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ElcCourseUpholdServiceImpl implements ElcCourseUpholdService {
    private RestTemplate restTemplate = RestTemplateBuilder.create();
    @Autowired
    private ElcCourseTakeDao courseTakeDao;

    @Autowired
    private ElcLogDao elcLogDao;

    @Value("${cache.directory}")
    private String cacheDirectory;


    @Autowired
    private DictionaryService dictionaryService;

    @Override
    public PageResult<ElcStudentVo> elcStudentInfo(PageCondition<ElcStudentDto> condition) {
        PageHelper.startPage(condition.getPageNum_(), condition.getPageSize_());
        Page<ElcStudentVo> elcStudentVos = courseTakeDao.findElcStudentInfo(condition.getCondition());
        setCourseArrange(elcStudentVos);
        return new PageResult<>(elcStudentVos);
    }

    @Override
    public PageResult<ElcStudentVo> addCourseList(PageCondition<String> condition) {
        /**
         * 调用培养：个人培养计划完成情况接口
         * coursesLabelList (课程分类列表)
         * cultureCourseLabelRelationList(课程列表)
         */
        String studentId = condition.getCondition();
        String path = ServicePathEnum.CULTURESERVICE.getPath("/culturePlan/getCourseCode?id={id}&isPass={isPass}");
        RestResult<List<String>> restResult = restTemplate.getForObject(path, RestResult.class, studentId, 0);
        List<String> allCourseCode = restResult.getData();
        List<String> selectedCourseCode = courseTakeDao.findSelectedCourseCode(studentId);
        List<String> collect = allCourseCode.stream().filter(item -> !selectedCourseCode.contains(item)).collect(Collectors.toList());
        PageHelper.startPage(condition.getPageNum_(), condition.getPageSize_());
        Page<ElcStudentVo> elcStudentVos = courseTakeDao.findAddCourseList(collect);
        setCourseArrange(elcStudentVos);
        return new PageResult<>(elcStudentVos);
    }

    @Override
    public PageResult<ElcStudentVo> removedCourseList(PageCondition<String> condition) {
        PageHelper.startPage(condition.getPageNum_(), condition.getPageSize_());
        Page<ElcStudentVo> elcStudentVos = courseTakeDao.findRemovedCourseList(condition.getCondition());
        setCourseArrange(elcStudentVos);
        return new PageResult<>(elcStudentVos);
    }

    private void setCourseArrange(Page<ElcStudentVo> elcStudentVos) {
        if (CollectionUtil.isNotEmpty(elcStudentVos)) {
            List<Long> ids = elcStudentVos.stream().map(ElcStudentVo::getTeachingClassId).collect(Collectors.toList());
            List<TimeTableMessage> tableMessages = getTimeById(ids);
            Map<Long, List<TimeTableMessage>> listMap = new HashMap<>();
            listMap = tableMessages.stream().collect(Collectors.groupingBy(TimeTableMessage::getTeachingClassId));
            for (ElcStudentVo elcStudentVo : elcStudentVos) {
                List<TimeTableMessage> timeTableMessages = listMap.get(elcStudentVo.getTeachingClassId());
                elcStudentVo.setCourseArrange(timeTableMessages);
            }
        }
    }

    @Override
    @Transactional
    public Integer addCourse(AddAndRemoveCourseDto courseDto) {
        List<Long> teachingClassId = courseDto.getTeachingClassId();
        List<ElcStudentVo> elcStudentVos = courseTakeDao.findCourseInfo(teachingClassId);
        List<ElcCourseTake> elcCourseTakes = new ArrayList<>();
        List<ElcLog> elcLogs = new ArrayList<>();
        addToList(courseDto, elcStudentVos, elcCourseTakes, elcLogs);
        Integer count = courseTakeDao.saveCourseTask(elcCourseTakes);
        if (count != 0) {
            elcLogDao.saveCourseLog(elcLogs);
        }
        return count;
    }

    @Override
    public Integer removedCourse(AddAndRemoveCourseDto courseDto) {
        List<Long> teachingClassId = courseDto.getTeachingClassId();
        List<ElcStudentVo> elcStudentVos = courseTakeDao.findCourseInfo(teachingClassId);
        Integer count = courseTakeDao.deleteCourseTask(teachingClassId, courseDto.getStudentId());
        //防止重复请求的时候重复保存
        if (count != 0) {
            List<ElcLog> elcLogs = new ArrayList<>();
            addToList(courseDto, elcStudentVos, elcLogs);
            elcLogDao.saveCourseLog(elcLogs);
        }
        return count;
    }

    private void addToList(AddAndRemoveCourseDto courseDto, List<ElcStudentVo> elcStudentVos, List<ElcLog> elcLogs) {
        String studentId = courseDto.getStudentId();
        String id = courseDto.getId();
        String name = courseDto.getName();
        for (ElcStudentVo elcStudentVo : elcStudentVos) {
            ElcLog elcLog = new ElcLog();
            elcLog.setStudentId(studentId);
            elcLog.setCourseCode(elcStudentVo.getCourseCode());
            elcLog.setCourseName(elcStudentVo.getCourseName());
            elcLog.setTeachingClassCode(elcStudentVo.getClassCode());
            elcLog.setCalendarId(elcStudentVo.getCalendarId());
            elcLog.setType(2);
            elcLog.setMode(2);
            elcLog.setCreateBy(id);
            elcLog.setCreateName(name);
            elcLog.setCreatedAt(new Date());
            elcLogs.add(elcLog);
        }
    }

    /*
     * 导出学生选课信息
     */
    @Override
    public String exportElcStudentInfo(PageCondition<ElcStudentDto> condition) throws Exception {
        PageHelper.startPage(condition.getPageNum_(), condition.getPageSize_());
        Page<ElcStudentCourseDto> studentCourses = courseTakeDao.findElcStudentCourse(condition.getCondition());
        if (studentCourses != null) {
            GeneralExcelDesigner design = getDesignElcStudent();
            design.setDatas(studentCourses);
            ExcelWriterUtil generalExcelHandle;
            generalExcelHandle = GeneralExcelUtil.generalExcelHandle(design);
            FileUtil.mkdirs(cacheDirectory);
            String fileName = "elcStudentInfo.xls";
            String path = cacheDirectory + fileName;
            generalExcelHandle.writeExcel(new FileOutputStream(path));
            return fileName;
        }
        return "";
    }

    private GeneralExcelDesigner getDesignElcStudent() {
        GeneralExcelDesigner design = new GeneralExcelDesigner();
        design.setNullCellValue("");
        design.addCell(I18nUtil.getMsg("exemptionApply.studentCode"), "studentCode");
        design.addCell(I18nUtil.getMsg("exemptionApply.studentName"), "studentName");
        design.addCell(I18nUtil.getMsg("elcCourseUphold.teachingClassName"), "teachingClassName");
        design.addCell(I18nUtil.getMsg("rebuildCourse.grade"), "grade");
        String lang = SessionUtils.getLang();
        design.addCell(I18nUtil.getMsg("rebuildCourse.trainingLevel"), "trainingLevel").setValueHandler(
                (value, rawData, cell) -> {
                    return dictionaryService.query("X_PYCC", value, lang);
                });
        design.addCell(I18nUtil.getMsg("noElection.trainingCategory"), "trainingCategory").setValueHandler(
                (value, rawData, cell) -> {
                    return dictionaryService.query("X_PYLB", value, lang);
                });
        design.addCell(I18nUtil.getMsg("noElection.degreeType"), "degreeType").setValueHandler(
                (value, rawData, cell) -> {
                    return dictionaryService.query("X_XWLX", value, lang);
                });
        design.addCell(I18nUtil.getMsg("noElection.formLearning"), "formLearning").setValueHandler(
                (value, rawData, cell) -> {
                    return dictionaryService.query("X_XXXS", value, lang);
                });
        design.addCell(I18nUtil.getMsg("exemptionApply.faculty"), "faculty").setValueHandler(
                (value, rawData, cell) -> {
                    return dictionaryService.query("X_YX", value, lang);
                });
        design.addCell(I18nUtil.getMsg("exemptionApply.major"), "profession").setValueHandler(
                (value, rawData, cell) -> {
                    return dictionaryService.query("G_ZY", value, lang);
                });
        design.addCell(I18nUtil.getMsg("rollBookManage.direction"), "researchDirection").setValueHandler(
                (value, rawData, cell) -> {
                    return dictionaryService.query("X_YJFX", value, lang);
                });
        design.addCell(I18nUtil.getMsg("elcCourseUphold.courseFaculty"), "courseFaculty").setValueHandler(
                (value, rawData, cell) -> {
                    return dictionaryService.query("X_YX", value, lang);
                });
        design.addCell(I18nUtil.getMsg("elcCourseUphold.nature"), "nature").setValueHandler(
                (value, rawData, cell) -> {
                    return dictionaryService.query("X_KCXZ", value, lang);
                });
        design.addCell(I18nUtil.getMsg("rebuildCourse.courseIndex"), "classCode");
        design.addCell(I18nUtil.getMsg("exemptionApply.courseCode"), "courseCode");
        design.addCell(I18nUtil.getMsg("exemptionApply.courseName"), "courseName");
        design.addCell(I18nUtil.getMsg("rebuildCourse.revisionategory"), "courseTakeType").setValueHandler(
                (value, rawData, cell) -> {
                    String resp = "";
                    switch (value) {
                        case "1":
                            resp = "正常修读";
                            break;
                        case "2":
                            resp = "重修";
                            break;
                        case "3":
                            resp = "免修不免考";
                            break;
                        case "4":
                            resp = "免修";
                            break;
                    }
                    return resp;
                });
        ;
        design.addCell(I18nUtil.getMsg("elcCourseUphold.chooseObj"), "chooseObj").setValueHandler(
                (value, rawData, cell) -> {
                    return "1".equals(value) ? "自选" : "代选";
                });
        return design;
    }

    private void addToList(AddAndRemoveCourseDto courseDto, List<ElcStudentVo> elcStudentVos, List<ElcCourseTake> elcCourseTakes, List<ElcLog> elcLogs) {
        String studentId = courseDto.getStudentId();
        Integer chooseObj = courseDto.getChooseObj();
        String id = courseDto.getId();
        String name = courseDto.getName();
        for (ElcStudentVo elcStudentVo : elcStudentVos) {
            ElcCourseTake elcCourseTake = new ElcCourseTake();
            elcCourseTake.setStudentId(studentId);
            elcCourseTake.setCalendarId(elcStudentVo.getCalendarId());
            elcCourseTake.setCourseCode(elcStudentVo.getCourseCode());
            elcCourseTake.setTeachingClassId(elcStudentVo.getTeachingClassId());
            elcCourseTake.setMode(2);
            elcCourseTake.setChooseObj(chooseObj);
            elcCourseTake.setCreatedAt(new Date());
            //默认为正常修读
            elcCourseTake.setCourseTakeType(1);
            elcCourseTake.setTurn(0);
            elcCourseTakes.add(elcCourseTake);

            ElcLog elcLog = new ElcLog();
            elcLog.setStudentId(studentId);
            elcLog.setCourseCode(elcStudentVo.getCourseCode());
            elcLog.setCourseName(elcStudentVo.getCourseName());
            elcLog.setTeachingClassCode(elcStudentVo.getClassCode());
            elcLog.setCalendarId(elcStudentVo.getCalendarId());
            elcLog.setType(1);
            elcLog.setMode(2);
            elcLog.setCreateBy(id);
            elcLog.setCreateName(name);
            elcLog.setCreatedAt(new Date());
            elcLogs.add(elcLog);
        }
    }

    private List<TimeTableMessage> getTimeById(List<Long> teachingClassId) {
        List<TimeTableMessage> list = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(teachingClassId)) {
            List<ClassTeacherDto> classTimeAndRoom = courseTakeDao.findClassTimeAndRoom(teachingClassId);
            if (CollectionUtil.isNotEmpty(classTimeAndRoom)) {
                for (ClassTeacherDto classTeacherDto : classTimeAndRoom) {
                    TimeTableMessage time = new TimeTableMessage();
                    time.setTeachingClassId(classTeacherDto.getTeachingClassId());
                    Integer dayOfWeek = classTeacherDto.getDayOfWeek();
                    Integer timeStart = classTeacherDto.getTimeStart();
                    Integer timeEnd = classTeacherDto.getTimeEnd();
                    String roomID = classTeacherDto.getRoomID();
                    String weekNumber = classTeacherDto.getWeekNumberStr();
                    String[] str = weekNumber.split(",");

                    List<Integer> weeks = Arrays.asList(str).stream().map(Integer::parseInt).collect(Collectors.toList());
                    List<String> weekNums = CalUtil.getWeekNums(weeks.toArray(new Integer[]{}));
                    String weekNumStr = weekNums.toString();//周次
                    String weekstr = findWeek(dayOfWeek);//星期
                    String timeStr = weekstr + " " + timeStart + "-" + timeEnd + "节 " + weekNumStr + " ";
                    time.setRoomId(roomID);
                    time.setTimeAndRoom(timeStr);
                    list.add(time);
                }
            }
        }
        return list;
    }

    public String findWeek(Integer number) {
        String week = "";
        switch (number) {
            case 1:
                week = "星期一";
                break;
            case 2:
                week = "星期二";
                break;
            case 3:
                week = "星期三";
                break;
            case 4:
                week = "星期四";
                break;
            case 5:
                week = "星期五";
                break;
            case 6:
                week = "星期六";
                break;
            case 7:
                week = "星期日";
                break;
        }
        return week;
    }

}
