package com.server.edu.election.service.impl;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.*;
import java.util.stream.Collectors;

import com.server.edu.election.entity.TeachingClassTeacher;
import com.server.edu.election.studentelec.cache.TeachingClassCache;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.server.edu.common.PageCondition;
import com.server.edu.common.locale.I18nUtil;
import com.server.edu.common.rest.PageResult;
import com.server.edu.common.rest.RestResult;
import com.server.edu.common.vo.SchoolCalendarVo;
import com.server.edu.dictionary.service.DictionaryService;
import com.server.edu.dictionary.utils.ClassroomCacheUtil;
import com.server.edu.dictionary.utils.TeacherCacheUtil;
import com.server.edu.election.constants.Constants;
import com.server.edu.election.dao.ElcCourseTakeDao;
import com.server.edu.election.dao.StudentDao;
import com.server.edu.election.dao.TeachingClassDao;
import com.server.edu.election.dao.TeachingClassTeacherDao;
import com.server.edu.election.dto.ClassTeacherDto;
import com.server.edu.election.dto.ExportPreCondition;
import com.server.edu.election.dto.PreViewRollDto;
import com.server.edu.election.dto.PreviewRollBookList;
import com.server.edu.election.dto.ReportManagementCondition;
import com.server.edu.election.dto.RollBookConditionDto;
import com.server.edu.election.dto.StudentSchoolTimetab;
import com.server.edu.election.dto.StudentSelectCourseList;
import com.server.edu.election.dto.StudnetTimeTable;
import com.server.edu.election.dto.TimeTableMessage;
import com.server.edu.election.entity.Student;
import com.server.edu.election.rpc.BaseresServiceInvoker;
import com.server.edu.election.service.ReportManagementService;
import com.server.edu.election.util.WeekUtil;
import com.server.edu.election.vo.RollBookList;
import com.server.edu.election.vo.StudentSchoolTimetabVo;
import com.server.edu.election.vo.StudentVo;
import com.server.edu.election.vo.TimeTable;
import com.server.edu.session.util.SessionUtils;
import com.server.edu.session.util.entity.Session;
import com.server.edu.util.CalUtil;
import com.server.edu.util.CollectionUtil;
import com.server.edu.util.FileUtil;
import com.server.edu.util.excel.GeneralExcelDesigner;
import com.server.edu.util.excel.export.ExcelExecuter;
import com.server.edu.util.excel.export.ExcelResult;
import com.server.edu.util.excel.export.ExportExcelUtils;

import freemarker.template.Template;

/**
 * @description: 报表管理实现类
 * @author: bear
 * @create: 2019-02-14 14:52
 */

@Service
@Primary
public class ReportManagementServiceImpl implements ReportManagementService
{
    private static Logger LOG = LoggerFactory.getLogger(ReportManagementServiceImpl.class);

    @Autowired
    private ElcCourseTakeDao courseTakeDao;

    @Autowired
    private StudentDao studentDao;

    @Autowired
    private TeachingClassTeacherDao teachingClassTeacherDao;

    @Autowired
    private TeachingClassDao teachingClassDao;

    @Value("${task.cache.directory}")
    private String cacheDirectory;

    @Autowired
    private DictionaryService dictionaryService;

    @Autowired
    private FreeMarkerConfigurer freeMarkerConfigurer;

    @Autowired
    private TeacherLessonTableServiceServiceImpl teacherLessonTableServiceServiceImpl;

    private static final String[] setTimeListTitle =
        {"序号", "课程序号", "课程名称", "重修", "必/选修", "考试/查", "学分", "教师", "教学安排", "备注", "校区"};

    /**
    *@Description: 预览点名册
    *@Param:
    *@return:
    *@Author: bear
    *@date: 2019/2/15 10:26
    */
    @Override
    public PreviewRollBookList findPreviewRollBookList(RollBookList bookList)
    {
        PreviewRollBookList previewRollBookList = new PreviewRollBookList();
        List<StudentVo> student = courseTakeDao
            .findStudentByTeachingClassId(bookList.getTeachingClassId());
        previewRollBookList.setList(student);//上课冲突待做todo
        previewRollBookList.setTeacherName(bookList.getTeacherName());
        previewRollBookList.setClassCode(bookList.getClassCode());
        previewRollBookList.setCourseName(bookList.getCourseName());
        SchoolCalendarVo schoolCalendarVo = BaseresServiceInvoker
            .getSchoolCalendarById(bookList.getCalendarId());
        previewRollBookList.setCalendarName(schoolCalendarVo.getFullName());
        String str = findClassroomAndTime(bookList.getTeachingClassId());
        previewRollBookList.setTime(str);
        return previewRollBookList;
    }

    /**
     *@Description: 查询学生课表
     *@Param:
     *@return:
     *@Author: bear
     *@date: 2019/2/15 15:23
     */
    @Override
    public StudentSchoolTimetabVo findSchoolTimetab(Long calendarId,
        String studentCode)
    {
        Student student = studentDao.findStudentByCode(studentCode);
        Double totalCredits = 0.0;
        StudentSchoolTimetabVo timetabVo = new StudentSchoolTimetabVo();
        List<TimeTable> list = new ArrayList<>();
        timetabVo.setStudentCode(student.getStudentCode());
        timetabVo.setName(student.getName());
        timetabVo.setFaculty(student.getFaculty());
        timetabVo.setTrainingLevel(student.getTrainingLevel());
        List<StudentSchoolTimetab> schoolTimetab =
            courseTakeDao.findSchoolTimetab(calendarId, studentCode);
        if (CollectionUtil.isNotEmpty(schoolTimetab))
        {
            for (StudentSchoolTimetab studentSchoolTimetab : schoolTimetab)
            {
                if (studentSchoolTimetab.getCredits() != null)
                {
                    totalCredits += studentSchoolTimetab.getCredits();
                }
                List<ClassTeacherDto> studentAndTeacherTime =
                    teacherLessonTableServiceServiceImpl.findStudentAndTeacherTime(
                        studentSchoolTimetab.getTeachingClassId());
                if (CollectionUtil.isNotEmpty(studentAndTeacherTime))
                {
                    for (ClassTeacherDto classTeacherDto : studentAndTeacherTime)
                    {
                        TimeTable timeTable = new TimeTable();
                        String value = classTeacherDto.getTeacherName() + " "
                            + studentSchoolTimetab.getCourseName() + "("
                            + studentSchoolTimetab.getCourseCode() + ")" + "("
                            + classTeacherDto.getWeekNumberStr()
                            + classTeacherDto.getRoom() + ")";
                        timeTable.setValue(value);
                        timeTable.setDayOfWeek(classTeacherDto.getDayOfWeek());
                        timeTable.setTimeStart(classTeacherDto.getTimeStart());
                        timeTable.setTimeEnd(classTeacherDto.getTimeEnd());
                        list.add(timeTable);
                    }
                }
                List<String> names = findTeacherByTeachingClassId(
                    studentSchoolTimetab.getTeachingClassId());
                if (CollectionUtil.isNotEmpty(names))
                {
                    studentSchoolTimetab
                        .setTeacherName(String.join(",", names));
                }
                String s = findClassroomAndTime(
                    studentSchoolTimetab.getTeachingClassId());
                String[] strings = s.split("/");
                List<String> timelist = new ArrayList<>();
                Set<String> roomList = new HashSet<>();
                for (String string : strings)
                {
                    int i = string.indexOf("]");
                    String time = string.substring(0, i + 1);
                    timelist.add(time);
                    String[] rooms =
                        string.substring(i + 1).replaceAll(" ", "").split(",");
                    List<String> stringList = Arrays.asList(rooms);
                    roomList.addAll(stringList);
                }
                String time = String.join(",", timelist);
                String room = String.join(",", roomList);
                studentSchoolTimetab.setTime(time);
                studentSchoolTimetab.setRoom(room);
            }

        }
        timetabVo.setList(schoolTimetab);
        timetabVo.setTotalCredits(totalCredits);
        timetabVo.setTimeTables(list);
        return timetabVo;
    }

    /**
    *@Description: 查询学生课表
    *@Param:
    *@return:
    *@Author: bear
    *@date: 2019/2/15 15:23
    */
    @Override
    public StudentSchoolTimetabVo findSchoolTimetab2(Long calendarId, String studentCode) {
        Student student = studentDao.findStudentByCode(studentCode);
        Double totalCredits=0.0;
        StudentSchoolTimetabVo timetabVo=new StudentSchoolTimetabVo();
        timetabVo.setStudentCode(student.getStudentCode());
        timetabVo.setName(student.getName());
        timetabVo.setFaculty(student.getFaculty());
        timetabVo.setTrainingLevel(student.getTrainingLevel());
        timetabVo.setProfession(student.getProfession());
        timetabVo.setGrade(student.getGrade());
        List<StudentSchoolTimetab> schoolTimetab = courseTakeDao.findSchoolTimetab(calendarId, studentCode);
        if(CollectionUtil.isNotEmpty(schoolTimetab)){
            List<Long> ids = schoolTimetab.stream().map(StudentSchoolTimetab::getTeachingClassId).collect(Collectors.toList());
            List<TimeTableMessage> tableMessages = courseTakeDao.findClassTime(ids);
            int size = tableMessages.size();
            MultiValueMap<Long, String> arrangeMap = new LinkedMultiValueMap<>(size);
            Map<Long, String> names = new HashMap<>(size);
            List<TeachingClassCache> teacherClass = teachingClassTeacherDao.findTeacherClass(ids);
            Map<Long, List<TeachingClassCache>> collect = teacherClass.stream().collect(Collectors.groupingBy(TeachingClassCache::getTeachClassId));
            List<TimeTable> list=new ArrayList<>(size);
            String lang = SessionUtils.getLang();
            for (TimeTableMessage tableMessage : tableMessages) {
                Integer dayOfWeek = tableMessage.getDayOfWeek();
                Integer timeStart = tableMessage.getTimeStart();
                Integer timeEnd = tableMessage.getTimeEnd();
                String roomID = tableMessage.getRoomId();
                String campus = tableMessage.getCampus();
                String courseName = tableMessage.getCourseName();
                String[] str = tableMessage.getWeekNum().split(",");
                Long teachingClassId = tableMessage.getTeachingClassId();
                List<Integer> weeks = Arrays.asList(str).stream().map(Integer::parseInt).collect(Collectors.toList());
                List<String> weekNums = CalUtil.getWeekNums(weeks.toArray(new Integer[] {}));
                String weekNumStr = weekNums.toString();//周次
                String weekstr = WeekUtil.findWeek(dayOfWeek);//星期
                String timeStr=weekstr+" "+timeStart+"-"+timeEnd+"节"+weekNumStr+ClassroomCacheUtil.getRoomName(roomID);
                arrangeMap.add(teachingClassId, timeStr);
                List<TeachingClassCache> teachingClassCaches = collect.get(teachingClassId);
                Set<String> set = teachingClassCaches.stream().map(TeachingClassCache::getTeacherName).collect(Collectors.toSet());
                String teacherName = String.join(",",set);
                names.put(teachingClassId, teacherName);
                TimeTable time = new TimeTable();
                time.setDayOfWeek(dayOfWeek);
                time.setTimeStart(timeStart);
                time.setTimeEnd(timeEnd);
                String value = teacherName + " " + courseName
                        + "(" + weekNumStr + ClassroomCacheUtil.getRoomName(roomID) + ")"
                        + " " + dictionaryService.query("X_XQ", campus, lang);
                time.setValue(value);
                list.add(time);
            }
            List<TimeTable> timtable = getTimtable(list);
            timetabVo.setTimeTables(timtable);
//            timetabVo.setTimeTables(list);
            for (StudentSchoolTimetab studentSchoolTimetab : schoolTimetab) {
                if(studentSchoolTimetab.getCredits()!=null){
                    totalCredits+=studentSchoolTimetab.getCredits();
                }
                Long teachingClassId = studentSchoolTimetab.getTeachingClassId();
                studentSchoolTimetab.setTeacherName(names.get(teachingClassId));
                List<String> times = arrangeMap.get(teachingClassId);
                if (CollectionUtil.isNotEmpty(times)) {
                    studentSchoolTimetab.setTime(String.join(",", times));
                }
            }
        }
        timetabVo.setList(schoolTimetab);
        timetabVo.setTotalCredits(totalCredits);
        return timetabVo;
    }

    private List<TimeTable> getTimtable(List<TimeTable> list) {
        Map<Integer, List<TimeTable>> map = list.stream().collect(Collectors.groupingBy(TimeTable::getDayOfWeek));
        List<TimeTable> tableList = new ArrayList<>(list.size() * 2);
        MultiValueMap<Integer, String> timeMap = new LinkedMultiValueMap<>();
        for (Map.Entry<Integer, List<TimeTable>>entry : map.entrySet()) {
            List<TimeTable> tables = entry.getValue();
            Integer day = entry.getKey();
            // 上课节次集合
            Set<Integer> set = new LinkedHashSet<>(12);
            if (tables.size() > 1) {
                // 将上课节次以一节为单位拆分
                for (TimeTable table : tables) {
                    Integer timeStart = table.getTimeStart();
                    Integer timeEnd = table.getTimeEnd();
                    String value = table.getValue();
                    for (int i = timeStart; i <= timeEnd; i++) {
                        timeMap.add(i, value);
                        set.add(i);
                    }
                }
                List<TimeTable> times = new ArrayList<>(12);
                for (Integer i : set) {
                    List<String> value = timeMap.get(i);
                    String values = String.join(",", value);
                    TimeTable tab = new TimeTable();
                    tab.setTimeStart(i);
                    tab.setValue(values);
                    tab.setTimeEnd(i);
                    times.add(tab);
                }
                int size = times.size();
                loop:for (int i = 0; i < size;i++) {
                    TimeTable timeTable = times.get(i);
                    String value = timeTable.getValue();
                    timeTable.setDayOfWeek(day);
                    for (int j = i + 1; j < size; j++) {
                        TimeTable table = times.get(j);
                        Integer timeEnd = table.getTimeEnd();
                        if (value.equals(table.getValue())) {
                            timeTable.setTimeEnd(timeEnd);
                            if (j == size - 1) {
                                tableList.add(timeTable);
                                break loop;
                            }
                            continue;
                        } else {
                            tableList.add(timeTable);
                            i = j - 1;
                            continue loop;
                        }
                    }
                    tableList.add(timeTable);
                }
            } else {
                tableList.add(tables.get(0));
            }
        }
        return tableList;
    }

    /**
    *@Description: 查询所有学生课表
    *@Param:
    *@return:
    *@Author: bear
    *@date: 2019/2/15 19:08
    */
    @Override
    public PageResult<StudentVo> findAllSchoolTimetab(
        PageCondition<ReportManagementCondition> condition)
    {
        PageHelper.startPage(condition.getPageNum_(), condition.getPageSize_());
        Page<StudentVo> allSchoolTimetab =
            courseTakeDao.findAllSchoolTimetab(condition.getCondition());
        return new PageResult<>(allSchoolTimetab);
    }

    /**
     * @Description: 根据用户角色查询学生课表
     * @Param:
     * @return:
     * @Author:
     * @date: 2019/7/4
     */
    @Override
    public PageResult<StudentVo> findStudentTimeTableByRole(PageCondition<ReportManagementCondition> condition) {
        PageHelper.startPage(condition.getPageNum_(), condition.getPageSize_());
        ReportManagementCondition reportManagementCondition = condition.getCondition();
        Session currentSession = SessionUtils.getCurrentSession();
        String currentManageDptId = currentSession.getCurrentManageDptId();
        reportManagementCondition.setProjectId(currentManageDptId);
        Page<StudentVo> schoolTimetab = courseTakeDao.findSchoolTimetabByRole(reportManagementCondition);
        if (!schoolTimetab.isEmpty()) {
            List<StudentVo> result = schoolTimetab.getResult();
            SchoolCalendarVo schoolCalendar = BaseresServiceInvoker.getSchoolCalendarById(condition.getCondition().getCalendarId());
            for (StudentVo studentVo : result) {
                studentVo.setCalendarName(schoolCalendar.getFullName());
            }
        }
        return new PageResult<>(schoolTimetab);
    }

    /**代选课*/
    @Override
    public String otherSelectCourse(
        StudentSelectCourseList studentSelectCourseList)
    {
        //调用选课接口todo
        return null;
    }

    /**
    *@Description: 导出点名册
    *@Param:
    *@return:
    *@Author: bear
    *@date: 2019/2/20 15:48
    */
    @Override
    public ExcelResult exportRollBookList(RollBookConditionDto condition)
        throws Exception
    {
        ExcelResult excelResult =
            ExportExcelUtils.submitTask("rollBookList", new ExcelExecuter()
            {
                @Override
                public GeneralExcelDesigner getExcelDesigner()
                {
                    ExcelResult result = this.getResult();
                    PageCondition<RollBookConditionDto> pageCondition =
                        new PageCondition<RollBookConditionDto>();
                    pageCondition.setCondition(condition);
                    pageCondition.setPageSize_(100);
                    int pageNum = 0;
                    pageCondition.setPageNum_(pageNum);
                    List<RollBookList> list = new ArrayList<>();
                    while (true)
                    {
                        pageNum++;
                        pageCondition.setPageNum_(pageNum);
                        PageResult<RollBookList> rollBookList =
                            findRollBookList(pageCondition);
                        list.addAll(rollBookList.getList());

                        result.setTotal((int)rollBookList.getTotal_());
                        Double count = list.size() / 1.5;
                        result.setDoneCount(count.intValue());
                        this.updateResult(result);

                        if (rollBookList.getTotal_() <= list.size())
                        {
                            break;
                        }
                    }
                    //组装excel
                    GeneralExcelDesigner design = getDesignTwo();
                    //将数据放入excel对象中
                    design.setDatas(list);
                    result.setDoneCount(list.size());
                    return design;
                }
            });
        return excelResult;
    }

    /**
    *@Description: 查询点名册
    *@Param:
    *@return:
    *@Author: bear
    *@date: 2019/4/28 16:26
    */
    @Override
    public PageResult<RollBookList> findRollBookList(
        PageCondition<RollBookConditionDto> condition)
    {
        PageHelper.startPage(condition.getPageNum_(), condition.getPageSize_());
        Page<RollBookList> rollBookList =
            courseTakeDao.findClassByTeacherCode(condition.getCondition());
        if (rollBookList != null)
        {
            List<RollBookList> result = rollBookList.getResult();
            if (CollectionUtil.isNotEmpty(result))
            {
                List<Long> list = result.stream().map(RollBookList::getTeachingClassId).collect(Collectors.toList());
                Map<Long, List<RollBookList>> map = new HashMap<>();
                //批量查询教师名字
                List<RollBookList> teahers = courseTakeDao.findTeacherName(list);
                if (CollectionUtil.isNotEmpty(teahers))
                {
                    map = teahers.stream().collect(Collectors.groupingBy(RollBookList::getTeachingClassId));
                }
                if (map.size() != 0)
                {
                    for (RollBookList bookList : result)
                    {
                        List<RollBookList> rollBookLists = map.get(bookList.getTeachingClassId());
                        if (CollectionUtil.isNotEmpty(rollBookLists))
                        {
                            Set<String> collect = rollBookLists.stream().map(RollBookList::getTeacherName).collect(Collectors.toSet());
                            String teacherName = String.join(",", collect);
                            bookList.setTeacherName(teacherName);
                        }

                    }
                }
            }
        }
        return new PageResult<>(rollBookList);
    }

    /**
     *@Description: 查询研究生点名册
     *@Param:
     *@return:
     *@Author: bear
     *@date: 2019/8/13
     */
    @Override
    public PageResult<RollBookList> findGraduteRollBookList(
            PageCondition<RollBookConditionDto> condition)
    {
        PageHelper.startPage(condition.getPageNum_(), condition.getPageSize_());
        Page<RollBookList> rollBookList =
                courseTakeDao.findClassByTeacherCode(condition.getCondition());
        if (rollBookList != null)
        {
            List<RollBookList> result = rollBookList.getResult();
            setTeacherNames(result);
        }
        return new PageResult<>(rollBookList);
    }

    private void setTeacherNames(List<RollBookList> result) {
        if (CollectionUtil.isNotEmpty(result))
        {
            List<Long> list = result.stream().map(RollBookList::getTeachingClassId).collect(Collectors.toList());
            //获取老师id
            List<TeachingClassTeacher> teachers = teachingClassDao.findTeacherNames(list);
            Map<Long, String> map = teachers.stream().collect(Collectors.toMap(TeachingClassTeacher::getTeachingClassId, TeachingClassTeacher::getTeacherName));
            if (CollectionUtil.isNotEmpty(teachers)) {
                for (RollBookList bookList : result)
                {
                    String name = map.get(bookList.getTeachingClassId());
                        bookList.setTeacherName(name);
                }
            }
        }
    }

    /**
     *@Description: 查询研究生点名册
     *@Param:
     *@return:
     *@Author: bear
     *@date: 2019/8/13
     */
    @Override
    public List<RollBookList> getExportGraduteRollBookList(
            List<String> ids)
    {
        List<RollBookList> rollBookList =
                courseTakeDao.getExportGraduteRollBookList(ids);
         setTeacherNames(rollBookList);
        return rollBookList;
    }

    /**
     *@Description: 预览点名册
     *@Param:
     *@return:
     *@Author: bear
     *@date: 2019/4/28 16:26
     */
    @Override
    public PreViewRollDto findPreviewRollBookListById(Long teachingClassId,
        Long calendarId)
    {
        PreViewRollDto pre = new PreViewRollDto();
        List<StudentVo> student =
            courseTakeDao.findStudentByTeachingClassId(teachingClassId);
        if (CollectionUtil.isNotEmpty(student))
        {
            for (StudentVo vo : student)
            {
                String exportName = vo.getName();
                if (!StringUtils.equals(vo.getTrainingLevel(), "1"))
                {
                    exportName = "(#)" + vo.getName();
                }
                vo.setExportName(exportName);
            }
            pre.setStudentsList(student);
            //星期多行展示
            //pre.setSize(student.size());
            pre.setSize(Constants.ONE);
        }
        //        SchoolCalendarVo schoolCalendarVo = BaseresServiceInvoker.getSchoolCalendarById(calendarId);
        //        pre.setCalendarName(schoolCalendarVo.getFullName());
        //封装教学班信息拆解
        List<Long> ids = new ArrayList<>();
        ids.add(teachingClassId);
        List<ClassTeacherDto> classTimeAndRoom =
            courseTakeDao.findClassTimeAndRoom(ids);
        Set<String> set = new HashSet<>();
        List<Integer> number = new ArrayList<>();
        int max = 0;
        if (CollectionUtil.isNotEmpty(classTimeAndRoom))
        {
            for (ClassTeacherDto classTeacherDto : classTimeAndRoom)
            {
                List<String> num = Arrays
                    .asList(classTeacherDto.getWeekNumberStr().split(","));
                set.addAll(num);//获取最大周数
            }
            for (String s : set)
            {
                number.add(Integer.valueOf(s));
            }
            max = Collections.max(number);
        }
        List<TimeTableMessage> list = teacherLessonTableServiceServiceImpl.getTimeById(ids);
        //        int size=0;
        //        if(CollectionUtil.isNotEmpty(list)){
        //            Map<Integer, List<TimeTableMessage>> collect = list.stream().collect(Collectors.groupingBy(TimeTableMessage::getDayOfWeek));
        //            size = collect.size();
        //        }
        //        pre.setLineNumber(size);
        pre.setLineNumber(1);
        pre.setRowNumber(max);
        pre.setTimeTabelList(list);
        return pre;

    }

    /**
     *@Description: 研究生点名册详情
     *@Param:
     *@return:
     *@Author:
     *@date: 2019/8/5
     */
    @Override
    public PreViewRollDto previewGraduteRollBook(Long teachingClassId) {
        PreViewRollDto pre=new PreViewRollDto();
        String trainingLevel = teachingClassDao.findTrainingLevel(teachingClassId);
        List<StudentVo> student = courseTakeDao.findStudentByTeachingClassId(teachingClassId);
        if(CollectionUtil.isNotEmpty(student)) {
            for(StudentVo vo:student) {
                Integer courseTakeType = vo.getCourseTakeType();
                List<String> list = new ArrayList<>();
                if (courseTakeType != null && courseTakeType.intValue() == 2) {
                    list.add("重");
                }
                if (!trainingLevel.equals(vo.getTrainingLevel())) {
                    list.add("#");
                }
                boolean conflict = getConflict(vo.getCalendarId(), vo.getStudentCode(), teachingClassId);
                if (conflict) {
                    list.add("*");
                }
                String exportName = "";
                if (CollectionUtil.isNotEmpty(list)) {
                    exportName = "(" + String.join(",",list) + ")";
                }
                exportName = exportName + vo.getName();
                vo.setExportName(exportName);
                vo.setPrefix(list);
            }
        }
        pre.setStudentsList(student);
        pre.setSize(student.size());
        //封装教学班信息拆解
        List<TimeTableMessage> timeTableMessages = courseTakeDao.findClassTimeAndRoomById(teachingClassId);
        // 最大周集合
        List<Integer> number=new ArrayList<>();
        Set<Integer> days =new HashSet<>();
        if (CollectionUtil.isNotEmpty(timeTableMessages)) {
            for (TimeTableMessage timeTableMessage : timeTableMessages) {
                Integer dayOfWeek = timeTableMessage.getDayOfWeek();
                if (dayOfWeek == null) {
                    continue;
                }
                days.add(dayOfWeek);
                Integer timeStart = timeTableMessage.getTimeStart();
                Integer timeEnd = timeTableMessage.getTimeEnd();
                String weekNumber = timeTableMessage.getWeekNum();
                String[] str = weekNumber.split(",");
                if (str == null || str.length == 0) {
                    continue;
                }
                LinkedHashSet<String> set = new LinkedHashSet<>(Arrays.asList(str));
                List<Integer> weeks = set.stream().map(Integer::parseInt).collect(Collectors.toList());
                number.addAll(weeks);
                List<String> weekNums = CalUtil.getWeekNums(weeks.toArray(new Integer[]{}));
                String weekNumStr = weekNums.toString();//周次
                String weekstr = WeekUtil.findWeek(dayOfWeek);//星期
                String timeStr = weekstr + " " + timeStart + "-" + timeEnd + "节" + weekNumStr + ClassroomCacheUtil.getRoomName(timeTableMessage.getRoomId());
                timeTableMessage.setTimeAndRoom(timeStr);
            }
            pre.setTimeTabelList(timeTableMessages);
        }
        Integer max = 0;
        if (CollectionUtil.isNotEmpty(number)) {
            max = Collections.max(number);
        }
        pre.setRowNumber(max);
        int size = days.size();
        if (size < 1) {
            size = 1;
        }
        pre.setLineNumber(size);
        return pre;
    }

    private boolean getConflict(Long calendarId, String studentCode, Long teachingClassId) {
        List<Long> ids = courseTakeDao.findTeachingClassIdByStudentId(studentCode, calendarId);
        List<TimeTableMessage> selectCourseArrange = courseTakeDao.findCourseArrange(ids);
        Map<Long, List<TimeTableMessage>> map = selectCourseArrange.stream().collect(Collectors.groupingBy(TimeTableMessage::getTeachingClassId));
        //获取当前教学班的课程安排
        List<TimeTableMessage> timeTableMessages = map.get(teachingClassId);
        if (CollectionUtil.isEmpty(timeTableMessages)) {
            return false;
        }
        for (TimeTableMessage timeTableMessage : timeTableMessages) {
            Integer dayOfWeek = timeTableMessage.getDayOfWeek();
            Integer timeStart = timeTableMessage.getTimeStart();
            Integer timeEnd = timeTableMessage.getTimeEnd();
            String[] split = timeTableMessage.getWeekNum().split(",");
            //当前教学班周次
            Set<String> nowWeeks = new HashSet<>(Arrays.asList(split));
            int nowSize = nowWeeks.size();
            for (TimeTableMessage tableMessage : selectCourseArrange) {
                if (tableMessage.getTeachingClassId().intValue() == teachingClassId.intValue()) {
                    continue;
                }
                String[] week = tableMessage.getWeekNum().split(",");
                Set<String> otherWeeks = new HashSet<>(Arrays.asList(week));
                int otherSize = otherWeeks.size();
                Set<String> weeks = new HashSet<>(nowSize + otherSize);
                weeks.addAll(nowWeeks);
                weeks.addAll(otherWeeks);
                // 判断上课周是否冲突
                if (nowSize + otherSize > weeks.size()) {
                    //上课周冲突，判断上课天
                    if (dayOfWeek == tableMessage.getDayOfWeek().intValue()) {
                        // 上课天相同，比价上课节次
                        int start = tableMessage.getTimeStart().intValue();
                        int end = tableMessage.getTimeEnd().intValue();
                        // 判断要添加课程上课开始、结束节次是否与已选课上课节次冲突
                        if ( (timeStart <= start && start <= timeEnd) || (timeStart <= end && end <= timeEnd)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
    /**
    *@Description: 查询学生个人课表
    *@Param:
    *@return:
    *@Author: bear
    *@date: 2019/4/30 11:58
    */
    @Override
    public List<StudnetTimeTable> findStudentTimetab(Long calendarId,
        String studentCode)
    {
        List<StudnetTimeTable> studentTable =
            courseTakeDao.findStudentTable(calendarId, studentCode);//查询所有教学班
        if (CollectionUtil.isNotEmpty(studentTable))
        {
            List<Long> ids = studentTable.stream()
                .map(StudnetTimeTable::getTeachingClassId)
                .collect(Collectors.toList());
            for (StudnetTimeTable studnetTimeTable : studentTable)
            {
                List<TimeTableMessage> tableMessages = teacherLessonTableServiceServiceImpl.getTimeById(ids);
                if (CollectionUtil.isNotEmpty(tableMessages))
                {
                    Map<Long, List<TimeTableMessage>> map =
                        tableMessages.stream()
                            .collect(Collectors.groupingBy(
                                TimeTableMessage::getTeachingClassId));
                    List<TimeTableMessage> timeTableMessages =
                        map.get(studnetTimeTable.getTeachingClassId());
                    studnetTimeTable.setTimeTableList(timeTableMessages);
                    if (CollectionUtil.isNotEmpty(timeTableMessages))
                    {
                        Set<String> teacher = new HashSet<>();
                        Set<String> timeTabel = new HashSet<>();
                        Set<String> room = new HashSet<>();
                        for (TimeTableMessage tableMessage : timeTableMessages)
                        {
                            String teacherCode = tableMessage.getTeacherCode();
                            String timeTab = tableMessage.getTimeTab();
                            String roomId = tableMessage.getRoomId();
                            //查询老师名称
                            List<String> names = TeacherCacheUtil
                                .getNames(teacherCode.split(","));
                            if (CollectionUtil.isNotEmpty(names))
                            {
                                teacher.addAll(names);
                            }
                            if (StringUtils.isNotEmpty(timeTab))
                            {
                                timeTabel.add(timeTab);
                            }
                            if (StringUtils.isNotEmpty(roomId))
                            {
                                room.add(roomId);
                            }
                        }
                        String teacherName = String.join(",", teacher);
                        String classTime = String.join(",", timeTabel);
                        String classRoom = String.join(",", room);
                        studnetTimeTable.setTeacherName(teacherName);
                        studnetTimeTable.setClassTime(classTime);
                        studnetTimeTable.setClassRoom(classRoom);
                    }
                }
            }
        }
        return studentTable;
    }

    /**
    *@Description: 学生课表其他服务调用
    *@Param:
    *@return:
    *@Author: bear
    *@date: 2019/5/5 9:34
    */
    @Cacheable(value = "getStudentTimetab", key = "#p1")
    @Override
    public List<TimeTable> getStudentTimetab(Long calendarId,
        String studentCode, Integer week)
    {
        List<StudnetTimeTable> studentTable =
            courseTakeDao.findStudentTable(calendarId, studentCode);//查询所有教学班
        List<TimeTable> list = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(studentTable))
        {
            List<Long> ids = studentTable.stream()
                .map(StudnetTimeTable::getTeachingClassId)
                .collect(Collectors.toList());
            List<TimeTableMessage> timeById = teacherLessonTableServiceServiceImpl.getTimeById(ids);
            for (TimeTableMessage tm : timeById)
            {
                if (null != tm.getWeeks() && null != week
                    && !tm.getWeeks().contains(week))
                {
                    continue;
                }
                TimeTable tt = new TimeTable();
                String name = "";
                if (StringUtils.isNotBlank(tm.getRoomId()))
                {
                    name = ClassroomCacheUtil.getRoomName(tm.getRoomId());
                }
                String value = String.format("%s %s(%s,%s)",
                    tm.getTeacherName(),
                    tm.getCourseName(),
                    tm.getWeekNum(),
                    name);
                tt.setDayOfWeek(tm.getDayOfWeek());
                tt.setTimeStart(tm.getTimeStart());
                tt.setTimeEnd(tm.getTimeEnd());
                tt.setCampus(tm.getCampus());
                tt.setValue(value);
                tt.setCourseCode(tm.getCourseCode());
                tt.setCourseName(tm.getCourseName());
                tt.setTeacherName(tm.getTeacherName());
                tt.setRoomName(name);
                list.add(tt);
            }
        }
        return list;
    }

    /**
     *@Description: 导出预览点名册
     *@Param:
     *@return:
     *@Author: bear
     *@date: 2019/5/8 16:14
     */
    @Override
    public String exportPreRollBookList(ExportPreCondition condition)
            throws Exception
    {
        PreViewRollDto preViewRollDto = findPreviewRollBookListById(condition.getTeachingClassId(), condition.getCalendarId());
        List<StudentVo> studentsList = preViewRollDto.getStudentsList();
        setSexAndFaculty(studentsList);
        SchoolCalendarVo schoolCalendarVo = BaseresServiceInvoker
                .getSchoolCalendarById(condition.getCalendarId());
        String calendarName = "同济大学" + schoolCalendarVo.getFullName() + "学生点名册";
        Integer lineNumber = preViewRollDto.getLineNumber();
        Integer rowNumber = preViewRollDto.getRowNumber();
        List<Integer> lineList = new ArrayList<>();
        if (lineNumber > 1)
        {
            for (int i = 0; i < lineNumber - 1; i++)
            {
                lineList.add(i);
            }
        }
        FileUtil.mkdirs(cacheDirectory);
        FileUtil.deleteFile(cacheDirectory, 2);
        String fileName =
                "preRollBookList-" + System.currentTimeMillis() + ".xls";
        String path = cacheDirectory + fileName;
        Map<String, Object> map = new HashMap<>();
        map.put("list", studentsList);
        map.put("calendar", calendarName);
        map.put("lineNumber", lineNumber - 1);
        map.put("rowNumber", rowNumber);
        map.put("lineList", lineList);
        map.put("item", condition);
        Template tpl = freeMarkerConfigurer.getConfiguration()
                .getTemplate("preRollBookList1.ftl");
        // 将模板和数据模型合并生成文件
        Writer out = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(path), "UTF-8"));
        try
        {
            tpl.process(map, out);
            out.flush();
        }
        finally
        {
            // 关闭流
            if(null != out) {
                out.close();
            }
        }
        return path;
    }

    /**
    *@Description: 导出研究生点名册详情
    *@Param:
    *@return:
    *@Author: bear
    *@date: 2019/8/10
    */
    @Override
    public String exportGraduteRollBook(ExportPreCondition condition)
        throws Exception
    {
        PreViewRollDto preViewRollDto =
            previewGraduteRollBook(condition.getTeachingClassId());
        List<StudentVo> studentsList = preViewRollDto.getStudentsList();
        setSexAndFaculty(studentsList);
        SchoolCalendarVo schoolCalendarVo = BaseresServiceInvoker
            .getSchoolCalendarById(condition.getCalendarId());
        String calendarName = "同济大学" + schoolCalendarVo.getFullName() + "学生点名册";
        Integer lineNumber = preViewRollDto.getLineNumber();
        Integer rowNumber = preViewRollDto.getRowNumber();
        List<Integer> lineList = new ArrayList<>();
        if (lineNumber > 1)
        {
            for (int i = 0; i < lineNumber - 1; i++)
            {
                lineList.add(i);
            }
        }
        FileUtil.mkdirs(cacheDirectory);
        FileUtil.deleteFile(cacheDirectory, 2);
        String fileName =
            "GraduteRollBook-" + System.currentTimeMillis() + ".xls";
        String path = cacheDirectory + fileName;
        Map<String, Object> map = new HashMap<>();
        map.put("list", studentsList);
        map.put("calendar", calendarName);
        map.put("lineNumber", lineNumber - 1);
        map.put("rowNumber", rowNumber);
        map.put("lineList", lineList);
        map.put("item", condition);
        Template tpl = freeMarkerConfigurer.getConfiguration()
            .getTemplate("graduteRollBookList.ftl");
        // 将模板和数据模型合并生成文件
        Writer out = new BufferedWriter(
            new OutputStreamWriter(new FileOutputStream(path), "UTF-8"));
        tpl.process(map, out);
        // 关闭流
        out.flush();
        out.close();
        return path;
    }

    private void setSexAndFaculty(List<StudentVo> studentsList) {
        for (StudentVo studentVo : studentsList) {
            Integer studentSex = studentVo.getSex();
            if (studentSex != null) {
                String sex = studentSex == 1 ? "男":"女";
                studentVo.setSexStr(sex);
            }
            String faculty = dictionaryService.query("X_YX", studentVo.getFaculty());
            studentVo.setFaculty(faculty);
        }
    }

    private GeneralExcelDesigner getDesignTwo()
    {
        GeneralExcelDesigner design = new GeneralExcelDesigner();
        design.setNullCellValue("");
        design.addCell(I18nUtil.getMsg("rollBookManage.teachingClass"),
            "classCode");
        design.addCell(I18nUtil.getMsg("exemptionApply.courseCode"),
            "courseCode");
        design.addCell(I18nUtil.getMsg("exemptionApply.courseName"),
            "courseName");
        design.addCell(I18nUtil.getMsg("rollBookManage.teachingClassName"),
            "className");
        design.addCell(I18nUtil.getMsg("rebuildCourse.label"), "courseLabel");
        design.addCell(I18nUtil.getMsg("rollBookManage.actualNumber"),
            "selectCourseNumber");
        design.addCell(I18nUtil.getMsg("rollBookManage.upperLimit"),
            "numberLimit");

        design
            .addCell(I18nUtil.getMsg("rollBookManage.courseOpenFaculty"),
                "faculty")
            .setValueHandler((value, rawData, cell) -> {
                return dictionaryService
                    .query("X_YX", value, SessionUtils.getLang());
            });

        design.addCell(I18nUtil.getMsg("rollBookManage.teacher"),
            "teacherName");
        return design;
    }

    //导出待做todo

    /**
    *@Description: 通过teachingClassId查询教师
    *@Param:
    *@return:
    *@Author: bear
    *@date: 2019/2/15 16:57
    */
    private List<String> findTeacherByTeachingClassId(Long id)
    {
        List<String> names = new ArrayList<>();
        List<ClassTeacherDto> teacher =
            courseTakeDao.findTeacherByClassCode(id);
        if (CollectionUtil.isNotEmpty(teacher))
        {
            //Map<Long, List<ClassTeacherDto>> collect = teacher.stream().collect(Collectors.groupingBy(ClassTeacherDto::getTeachingClassId));
            //List<ClassTeacherDto> classTeacherDtos = collect.get(id);
            //List<ClassTeacherDto> classTeacherDtoStream = teacher.stream().filter((ClassTeacherDto dto) -> dto.getTeachingClassId().longValue() == id.longValue()).collect(Collectors.toList());
            //if (CollectionUtil.isNotEmpty(classTeacherDtoStream)) {
            names = teacher.stream()
                .map(ClassTeacherDto::getTeacherName)
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.toList());
            //}
        }
        return names;
    }

    /**
    * 通过classId查询教室信息
    */
    private String findClassroomAndTime(Long id)
    {
        //查询教学班信息TeachingClassId查询
        StringBuilder str = new StringBuilder();
        List<Long> ids = new ArrayList<>();
        ids.add(id);
        List<ClassTeacherDto> classTimeAndRoom =
            courseTakeDao.findClassTimeAndRoom(ids);
        if (CollectionUtil.isNotEmpty(classTimeAndRoom))
        {
            Map<Long, List<ClassTeacherDto>> collect = classTimeAndRoom.stream()
                .collect(Collectors.groupingBy(ClassTeacherDto::getTimeId));
            if (collect.size() != 0)
            {
                for (List<ClassTeacherDto> list : collect.values())
                {
                    if (CollectionUtil.isNotEmpty(list))
                    {
                        ClassTeacherDto item = list.get(0);
                        Integer dayOfWeek = item.getDayOfWeek();
                        Integer timeStart = item.getTimeStart();
                        Integer timeEnd = item.getTimeEnd();
                        //List<Integer> integerList = list.stream().map(ClassTeacherDto::getWeekNumber).collect(Collectors.toList());
                        List<String> asList =
                            Arrays.asList(item.getWeekNumberStr().split(","));
                        List<Integer> intList = new ArrayList<Integer>();
                        for (String string : asList)
                        {
                            intList.add(Integer.parseInt(string));
                        }
                        Integer maxWeek = Collections.max(intList);
                        Integer minWeek = Collections.min(intList);
                        Set<String> rooms = list.stream()
                            .map(ClassTeacherDto::getRoomID)
                            .filter(StringUtils::isNotBlank)
                            .collect(Collectors.toSet());
                        String roomId = String.join(",", rooms);
                        String time = WeekUtil.findWeek(dayOfWeek) + " "
                            + timeStart + "-" + timeEnd + "[" + minWeek + "-"
                            + maxWeek + "] " + roomId;
                        str.append(time);
                        str.append("/");
                    }
                }

            }
        }
        return str.toString();
    }

    @Override
    public RestResult<String> exportStudentTimetabPdf(Long calendarId,
        String studentCode)
        throws Exception
    {
        //检查目录是否存在
        //cacheDirectory = "C://temp//pdf//cacheWord";
        LOG.info("缓存目录：" + cacheDirectory);
        FileUtil.mkdirs(cacheDirectory);
        //删除超过30天的文件
        FileUtil.deleteFile(cacheDirectory, 30);

        /************************ PDF初始化操作 ******************************/
        //所有使用中文处理
        BaseFont bfChinese = BaseFont.createFont("STSongStd-Light",
            "UniGB-UCS2-H",
            BaseFont.NOT_EMBEDDED);
        //粗体文字 title使用
        Font titleChinese = new Font(bfChinese, 20, Font.BOLD);
        //副标题
        Font subtitleChinese = new Font(bfChinese, 13, Font.BOLD);
        //正常文字
        //Font name1 = new Font(bfChinese, 12, Font.BOLD, BaseColor.GRAY);
        Font name2 = new Font(bfChinese, 12, Font.NORMAL);

        //----3 学生基本信息----
        StudentSchoolTimetabVo studentTimetab =
                findSchoolTimetab2(calendarId, studentCode);
        String fileName = new StringBuffer("").append(cacheDirectory)
            .append("//")
            .append(System.currentTimeMillis())
            .append("_")
            .append(studentCode)
            .append(".pdf")
            .toString();
        Document document = new Document(PageSize.A4, 24, 24, 16, 16);
        PdfWriter.getInstance(document, new FileOutputStream(fileName));

        /************************ PDF填充内容  ******************************/
        document.open();
        //---1 添加标题---
        Paragraph title = new Paragraph("个人课程表", titleChinese);
        //居中设置
        title.setAlignment(Element.ALIGN_CENTER);
        //设置行间距
        title.setLeading(20);
        title.setSpacingBefore(30);
        document.add(title);
        
        //---2 副标题---
        SchoolCalendarVo schoolCalendarVo = BaseresServiceInvoker
                .getSchoolCalendarById(calendarId);
        Paragraph subtitle = new Paragraph(schoolCalendarVo.getFullName(), subtitleChinese);
        subtitle.setAlignment(Element.ALIGN_CENTER);
        //设置行间距
        subtitle.setLeading(10);
        //内容距离左边8个单位
        subtitle.setFirstLineIndent(7);
        subtitle.setIndentationRight(20);
        //副标题与标题之间的距离
        subtitle.setSpacingBefore(15);
        document.add(subtitle);

        PdfPTable table1 = new PdfPTable(4);
        //前间距
        table1.setSpacingBefore(5);
        
        PdfPCell cell1 = TeacherLessonTableServiceServiceImpl.createNoBorderCell("学号：" + studentCode, name2, 20f);
        table1.addCell(cell1);

        PdfPCell cell2 = TeacherLessonTableServiceServiceImpl.createNoBorderCell("学生姓名：" + studentTimetab.getName(), name2, 20f);
        table1.addCell(cell2);
        
        PdfPCell cell4 =
            TeacherLessonTableServiceServiceImpl.createNoBorderCell("总学分：" + studentTimetab.getTotalCredits(),
                name2,
                20f);
        table1.addCell(cell4);
        document.add(table1);
        
        // ----3 学生选课课表展示---- 
        PdfPTable table2 = teacherLessonTableServiceServiceImpl.createStudentTable(studentTimetab.getTimeTables(),
            subtitleChinese,
            name2);
        document.add(table2);
        
        // ----4 学生选课列表 -------
        PdfPTable table3 = createStudentTimeList(studentTimetab.getList(),
            subtitleChinese,
            name2);
        document.add(table3);
        
        document.close();
        return RestResult.successData("导出成功。", fileName);
    }


    /**
       * 创建学生选课列表table
     * @throws IOException
     */
    private PdfPTable createStudentTimeList(List<StudentSchoolTimetab> list,
        Font subtitleChinese, Font name2)
        throws DocumentException, IOException
    {
        PdfPTable table = new PdfPTable(setTimeListTitle.length);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10);

        // 添加表头
        for (int i = 0; i < setTimeListTitle.length; i++)
        {
            PdfPCell cell =
                TeacherLessonTableServiceServiceImpl.createCell(setTimeListTitle[i], 1, 1, subtitleChinese, null);
            table.addCell(cell);
        }

        if (CollectionUtil.isNotEmpty(list)) {
            // 添加表内容
            for (int j = 0; j < list.size(); j++)
            {
                List<String> timeTableList = getTimeTableList(list.get(j));
                for (int i = 0; i < setTimeListTitle.length ; i++)
                {
                    PdfPCell cell = new PdfPCell();
                    if (i == 0)
                    {
                        cell = TeacherLessonTableServiceServiceImpl.createCell(String.valueOf(j + 1), 1, 1, name2, null);
                    }
                    else
                    {
                        cell =
                                TeacherLessonTableServiceServiceImpl.createCell(timeTableList.get(i - 1), 1, 1, name2, null);
                    }
                    table.addCell(cell);
                }
            }
        }
        return table;
    }

    private List<String> getTimeTableList(StudentSchoolTimetab timeTable)
    {
        List<String> list = new ArrayList<String>(11);
        list.add(timeTable.getCourseCode());
        list.add(timeTable.getCourseName());
        list.add("2".equals(timeTable.getCourseType()) ? "是" : "否");
        Integer isElective = timeTable.getIsElective();
        if (isElective != null)
        {
            list.add(isElective == 1 ? "选修" : "必修");
        } else {
            list.add("");
        }
        String assessmentMode = timeTable.getAssessmentMode();
        if (assessmentMode != null) {
            assessmentMode = dictionaryService.query("X_KSLX", assessmentMode);
        } else {
            assessmentMode = "";
        }
        list.add(assessmentMode);
        Double credits = timeTable.getCredits();
        String credit;
        if (credits == null) {
            credit = "";
        } else {
            credit = String.valueOf(credits);
        }
        list.add(credit);
        list.add(timeTable.getTeacherName());
        list.add(timeTable.getTime());
        list.add(timeTable.getRemark());
        String campus = timeTable.getCampus();
        if (campus != null) {
            campus = dictionaryService.query("X_XQ", campus);
        } else {
            campus = "";
        }
        list.add(campus);
        return list;
    }

}
