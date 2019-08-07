package com.server.edu.election.service.impl;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
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
import com.server.edu.dictionary.utils.SpringUtils;
import com.server.edu.election.constants.Constants;
import com.server.edu.election.dao.ElcCourseTakeDao;
import com.server.edu.election.dao.ElcNoSelectReasonDao;
import com.server.edu.election.dao.StudentDao;
import com.server.edu.election.dao.TeachingClassTeacherDao;
import com.server.edu.election.dto.ClassCodeToTeacher;
import com.server.edu.election.dto.ClassTeacherDto;
import com.server.edu.election.dto.ExportPreCondition;
import com.server.edu.election.dto.NoSelectCourseStdsDto;
import com.server.edu.election.dto.PreViewRollDto;
import com.server.edu.election.dto.PreviewRollBookList;
import com.server.edu.election.dto.ReportManagementCondition;
import com.server.edu.election.dto.RollBookConditionDto;
import com.server.edu.election.dto.StudentSchoolTimetab;
import com.server.edu.election.dto.StudentSelectCourseList;
import com.server.edu.election.dto.StudnetTimeTable;
import com.server.edu.election.dto.TeacherTimeTable;
import com.server.edu.election.dto.TimeTableMessage;
import com.server.edu.election.entity.ElcNoSelectReason;
import com.server.edu.election.entity.Student;
import com.server.edu.election.rpc.BaseresServiceInvoker;
import com.server.edu.election.rpc.CultureSerivceInvoker;
import com.server.edu.election.service.ReportManagementService;
import com.server.edu.election.util.ExcelStoreConfig;
import com.server.edu.election.util.PageConditionUtil;
import com.server.edu.election.util.WeekUtil;
import com.server.edu.election.vo.ElcNoSelectReasonVo;
import com.server.edu.election.vo.RollBookList;
import com.server.edu.election.vo.StudentSchoolTimetabVo;
import com.server.edu.election.vo.StudentVo;
import com.server.edu.election.vo.TimeTable;
import com.server.edu.session.util.SessionUtils;
import com.server.edu.util.CalUtil;
import com.server.edu.util.CollectionUtil;
import com.server.edu.util.FileUtil;
import com.server.edu.util.excel.ExcelWriterUtil;
import com.server.edu.util.excel.GeneralExcelDesigner;
import com.server.edu.util.excel.GeneralExcelUtil;
import com.server.edu.util.excel.export.ExcelExecuter;
import com.server.edu.util.excel.export.ExcelResult;
import com.server.edu.util.excel.export.ExportExcelUtils;
import com.server.edu.welcomeservice.util.ExcelEntityExport;

import freemarker.template.Template;

/**
 * @description: 报表管理实现类
 * @author: bear
 * @create: 2019-02-14 14:52
 */

@Service
@Primary
public class ReportManagementServiceImpl implements ReportManagementService {
	private static Logger LOG =
	        LoggerFactory.getLogger("com.server.edu.election.service.impl.ReportManagementServiceImpl");
	
    @Autowired
    private ElcCourseTakeDao courseTakeDao;

    @Autowired
    private StudentDao studentDao;

    @Autowired
    private ElcNoSelectReasonDao reasonDao;

    @Autowired
    private TeachingClassTeacherDao teachingClassTeacherDao;

    @Value("${task.cache.directory}")
    private String cacheDirectory;


    @Autowired
    private DictionaryService dictionaryService;

    @Autowired
    private FreeMarkerConfigurer freeMarkerConfigurer;

    @Autowired
    private ExcelStoreConfig excelStoreConfig;
    
    // 导出学生课表
    private static final String[] setSchoolTimeTitle = {"节次/周次", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六", "星期日"};
    private static final String[] setSchoolTimeCol = {"第一节课", "第二节课", "第三节课", "第四节课", "第五节课", "第六节课", "第七节课", "第八节课", "第九节课", "第十节课", "第十一节课", "第十二节课"};
    private static final String[] setTimeListTitle = {"序号", "课程序号", "课程名称", "重修", "必/选修", "考试/查", "学分", "教师", "教学安排","备注"};
    private static final String[] setTimeListTeacherTitle = {"序号", "课程序号", "课程名称", "课程性质", "周学时", "学分", "授课语言", "教学安排","上课人数", "备注"};
    /**
    *@Description: 查询点名册
    *@Param:
    *@return:
    *@Author: bear
    *@date: 2019/2/14 15:52
    */
    @Override
    public PageResult<RollBookList> findRollBookList2(PageCondition<ReportManagementCondition> condition) {
        PageHelper.startPage(condition.getPageNum_(),condition.getPageSize_());
        Page<RollBookList> rollBookList = courseTakeDao.findRollBookList(condition.getCondition());
        if(rollBookList!=null){
            List<RollBookList> result = rollBookList.getResult();
            if(CollectionUtil.isNotEmpty(result)){
                //通过classCode 查询老师封装rollBookList
                    for (RollBookList bookList : result) {
                        List<String> names = findTeacherByTeachingClassId(bookList.getTeachingClassId());
                        if(CollectionUtil.isNotEmpty(names)){
                            bookList.setTeacherName(String.join(",",names));
                        }
                    }
            }
        }
        return new PageResult<>(rollBookList);
    }

    /**
    *@Description: 预览点名册
    *@Param:
    *@return:
    *@Author: bear
    *@date: 2019/2/15 10:26
    */
    @Override
    public PreviewRollBookList findPreviewRollBookList(RollBookList bookList) {
        PreviewRollBookList previewRollBookList=new PreviewRollBookList();
        List<StudentVo> student = courseTakeDao.findStudentByTeachingClassId(bookList.getTeachingClassId());
        previewRollBookList.setList(student);//上课冲突待做todo
        previewRollBookList.setTeacherName(bookList.getTeacherName());
        previewRollBookList.setClassCode(bookList.getClassCode());
        previewRollBookList.setCourseName(bookList.getCourseName());
        SchoolCalendarVo schoolCalendarVo = BaseresServiceInvoker.getSchoolCalendarById(bookList.getCalendarId());
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
    public StudentSchoolTimetabVo findSchoolTimetab(Long calendarId, String studentCode) {
        Student student = studentDao.findStudentByCode(studentCode);
        Double totalCredits=0.0;
        StudentSchoolTimetabVo timetabVo=new StudentSchoolTimetabVo();
        List<TimeTable> list=new ArrayList<>();
        timetabVo.setStudentCode(student.getStudentCode());
        timetabVo.setName(student.getName());
        timetabVo.setFaculty(student.getFaculty());
        timetabVo.setTrainingLevel(student.getTrainingLevel());
        List<StudentSchoolTimetab> schoolTimetab = courseTakeDao.findSchoolTimetab(calendarId, studentCode);
        if(CollectionUtil.isNotEmpty(schoolTimetab)){
            for (StudentSchoolTimetab studentSchoolTimetab : schoolTimetab) {
                if(studentSchoolTimetab.getCredits()!=null){
                    totalCredits+=studentSchoolTimetab.getCredits();
                }
                List<ClassTeacherDto> studentAndTeacherTime = findStudentAndTeacherTime(studentSchoolTimetab.getTeachingClassId());
                if(CollectionUtil.isNotEmpty(studentAndTeacherTime)){
                    for (ClassTeacherDto classTeacherDto : studentAndTeacherTime) {
                        TimeTable timeTable=new TimeTable();
                        String value=classTeacherDto.getTeacherName()+" "+studentSchoolTimetab.getCourseName()+"("+
                                studentSchoolTimetab.getCourseCode()+")"+"("+classTeacherDto.getWeekNumberStr()+classTeacherDto.getRoom()+")";
                        timeTable.setValue(value);
                        timeTable.setDayOfWeek(classTeacherDto.getDayOfWeek());
                        timeTable.setTimeStart(classTeacherDto.getTimeStart());
                        timeTable.setTimeEnd(classTeacherDto.getTimeEnd());
                        list.add(timeTable);
                    }
                }
                List<String> names = findTeacherByTeachingClassId(studentSchoolTimetab.getTeachingClassId());
                if(CollectionUtil.isNotEmpty(names)){
                    studentSchoolTimetab.setTeacherName(String.join(",",names));
                }
                String s = findClassroomAndTime(studentSchoolTimetab.getTeachingClassId());
                String[] strings = s.split("/");
                List<String> timelist=new ArrayList<>();
                Set<String> roomList=new HashSet<>();
                for (String string : strings) {
                    int i = string.indexOf("]");
                    String time = string.substring(0,i+1);
                    timelist.add(time);
                    String[] rooms= string.substring(i + 1).replaceAll(" ","").split(",");
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
            List<TimeTable> list=new ArrayList<>(size);
            MultiValueMap<Long, String> nameMap = new LinkedMultiValueMap<>(size);
            String lang = SessionUtils.getLang();
            for (TimeTableMessage tableMessage : tableMessages) {
                Integer dayOfWeek = tableMessage.getDayOfWeek();
                Integer timeStart = tableMessage.getTimeStart();
                Integer timeEnd = tableMessage.getTimeEnd();
                String roomID = tableMessage.getRoomId();
                String campus = tableMessage.getCampus();
                String courseName = tableMessage.getCourseName();
                String teacherCode = tableMessage.getTeacherCode();
                String[] str = tableMessage.getWeekNum().split(",");
                Long teachingClassId = tableMessage.getTeachingClassId();
                List<Integer> weeks = Arrays.asList(str).stream().map(Integer::parseInt).collect(Collectors.toList());
                List<String> weekNums = CalUtil.getWeekNums(weeks.toArray(new Integer[] {}));
                String weekNumStr = weekNums.toString();//周次
                String weekstr = WeekUtil.findWeek(dayOfWeek);//星期
                String timeStr=weekstr+" "+timeStart+"-"+timeEnd+"节"+weekNumStr+ClassroomCacheUtil.getRoomName(roomID);
                arrangeMap.add(teachingClassId, timeStr);
                String teacherName = "";
                if (teacherCode != null) {
                    teacherName = teachingClassTeacherDao.findTeacherName(teacherCode);
                    nameMap.add(teachingClassId,teacherName);
                }
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
            for (StudentSchoolTimetab studentSchoolTimetab : schoolTimetab) {
                if(studentSchoolTimetab.getCredits()!=null){
                    totalCredits+=studentSchoolTimetab.getCredits();
                }
                Long teachingClassId = studentSchoolTimetab.getTeachingClassId();
                List<String> times = arrangeMap.get(teachingClassId);
                if (CollectionUtil.isNotEmpty(times)) {
                    studentSchoolTimetab.setTime(String.join(",", times));
                }
                List<String> names = nameMap.get(teachingClassId);
                if (CollectionUtil.isNotEmpty(names)) {
                    Set<String> set = new HashSet<>(names.size());
                    set.addAll(names);
                    studentSchoolTimetab.setTeacherName(String.join(",", set));
                }
            }
            timetabVo.setTimeTables(list);
        }
        timetabVo.setList(schoolTimetab);
        timetabVo.setTotalCredits(totalCredits);
        return timetabVo;
    }

    /**
    *@Description: 查询所有学生课表
    *@Param:
    *@return:
    *@Author: bear
    *@date: 2019/2/15 19:08
    */
    @Override
    public PageResult<StudentVo> findAllSchoolTimetab(PageCondition<ReportManagementCondition> condition) {
        PageHelper.startPage(condition.getPageNum_(),condition.getPageSize_());
        Page<StudentVo> allSchoolTimetab = courseTakeDao.findAllSchoolTimetab(condition.getCondition());
        if(allSchoolTimetab!=null){
            List<StudentVo> result = allSchoolTimetab.getResult();
            SchoolCalendarVo schoolCalendar= BaseresServiceInvoker.getSchoolCalendarById(condition.getCondition().getCalendarId());
            for (StudentVo studentVo : result) {
                studentVo.setCalendarName(schoolCalendar.getFullName());
            }

        }
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
        Page<StudentVo> schoolTimetab = courseTakeDao.findSchoolTimetabByRole(condition.getCondition());
        if (!schoolTimetab.isEmpty()) {
            List<StudentVo> result = schoolTimetab.getResult();
            SchoolCalendarVo schoolCalendar = BaseresServiceInvoker.getSchoolCalendarById(condition.getCondition().getCalendarId());
            for (StudentVo studentVo : result) {
                studentVo.setCalendarName(schoolCalendar.getFullName());
            }

        }
        return new PageResult<>(schoolTimetab);
    }

    /**
    *@Description: 教师上课时间地点详情
    *@Param:
    *@return:
    *@Author: bear
    *@date: 2019/2/16 10:18
    */
    @Override
    public List<ClassTeacherDto> findStudentAndTeacherTime(Long teachingClassId) {
        List<ClassTeacherDto> list=new ArrayList<>();
        if (teachingClassId == null) {
            return list;
        }
        List<ClassTeacherDto> classTeacherDtos = courseTakeDao.findStudentAndTeacherTime(teachingClassId);
        if(CollectionUtil.isNotEmpty(classTeacherDtos)){
            Map<String, List<ClassTeacherDto>> listMap = classTeacherDtos.stream().filter((ClassTeacherDto dto)->dto.getTeacherCode()!=null).collect(Collectors.groupingBy(ClassTeacherDto::getTeacherCode));
            for (List<ClassTeacherDto> teacherDtoList : listMap.values()) {
                Map<Long, List<ClassTeacherDto>> roomList = teacherDtoList.stream().collect(Collectors.groupingBy(ClassTeacherDto::getTimeId));
                for (List<ClassTeacherDto> teacherDtos : roomList.values()) {
                    Map<Integer, List<ClassTeacherDto>> collect = teacherDtos.stream().collect(Collectors.groupingBy(ClassTeacherDto::getTimeStart));
                    for (List<ClassTeacherDto> dtoList : collect.values()) {
                        Map<String, List<ClassTeacherDto>> byRoomList = dtoList.stream().collect(Collectors.groupingBy(ClassTeacherDto::getRoomID));
                        for (List<ClassTeacherDto> dtos : byRoomList.values()) {
                            ClassTeacherDto timetab=new ClassTeacherDto();
                            ClassTeacherDto classTeacherDto = dtos.get(0);
                            String teacherCode = classTeacherDto.getTeacherCode();
                            String[] teacherCodes = teacherCode.split(",");

                            Integer dayOfWeek = classTeacherDto.getDayOfWeek();
                            String week = WeekUtil.findWeek(dayOfWeek);
                            Integer timeStart = classTeacherDto.getTimeStart();
                            Integer timeEnd = classTeacherDto.getTimeEnd();
                            String roomID = classTeacherDto.getRoomID();
                            List<Integer> integerList = dtos.stream().map(ClassTeacherDto::getWeekNumber).collect(Collectors.toList());
                            //Integer weekNumber1 = dtos.stream().max(Comparator.comparingInt(ClassTeacherDto::getWeekNumber)).get().getWeekNumber();
                            //Integer weekNumber2 = dtos.stream().min(Comparator.comparingInt(ClassTeacherDto::getWeekNumber)).get().getWeekNumber();
                            Integer maxWeek = Collections.max(integerList);
                            Integer minWeek = Collections.min(integerList);
                            String strWeek="[";
                            String strTime=timeStart+"-"+timeEnd;
                            int size = dtos.size();//判断是否连续
                            if(minWeek+size-1==maxWeek){//连续拼接周次
                                strWeek+=minWeek+"-"+maxWeek+"]";
                            }else{
                                for(int i=0;i<dtos.size();i++){
                                    Integer weekNumber = dtos.get(i).getWeekNumber();
                                    if(i!=dtos.size()-1){
                                        strWeek+=weekNumber+",";
                                    }else{
                                        strWeek+=weekNumber+"]";
                                    }
                                }
                            }
                            String time=week+" "+strTime+" "+strWeek;
                            List<String> names = courseTakeDao.findTeacherNameByTeacherCode(teacherCodes);
                            timetab.setCampus(classTeacherDto.getCampus());
                            timetab.setTeacherName(String.join(",",names));
                            timetab.setTime(time);
                            timetab.setWeekNumberStr(strWeek);
                            timetab.setDayOfWeek(dayOfWeek);
                            timetab.setTimeStart(timeStart);
                            timetab.setTimeEnd(timeEnd);
                            timetab.setRoomID(roomID);
                            timetab.setRoom(roomID);
                            timetab.setTeacherCode(teacherCode);
                            timetab.setRemark(classTeacherDto.getRemark());
                            timetab.setTeachingLanguage(classTeacherDto.getTeachingLanguage());
                            timetab.setClassCode(classTeacherDto.getClassCode());
                            timetab.setTeachingClassId(classTeacherDto.getTeachingClassId());
                            list.add(timetab);
                        }
                    }
                }
            }
        }

        return list;
    }

    /**
    *@Description:教师对应教学班信息
    *@Param:
    *@return:
    *@Author: bear
    *@date: 2019/2/16 17:45
    */
    @Override
    public PageResult<ClassCodeToTeacher> findAllClassTeacher(PageCondition<ClassCodeToTeacher> condition) {
        PageHelper.startPage(condition.getPageNum_(),condition.getPageSize_());
        Page<ClassCodeToTeacher> allClassTeacher = courseTakeDao.findAllClassTeacher(condition.getCondition());
        if(allClassTeacher!=null){
            List<ClassCodeToTeacher> result = allClassTeacher.getResult();
            List<SchoolCalendarVo> schoolCalendarList = BaseresServiceInvoker.getSchoolCalendarList();
            Map<Long, String> schoolCalendarMap = new HashMap<>();
            for(SchoolCalendarVo schoolCalendarVo : schoolCalendarList) {
                schoolCalendarMap.put(schoolCalendarVo.getId(), schoolCalendarVo.getFullName());
            }
            if(schoolCalendarMap.size()!=0){
                for (ClassCodeToTeacher toTeacher : result) {
                    String s = schoolCalendarMap.get(toTeacher.getCalendarId());
                    if(StringUtils.isNotEmpty(s)) {
                        toTeacher.setCalendarName(s);
                    }
                }
            }
        }
        return new PageResult<>(allClassTeacher);
    }



    /**
    *@Description: 研究生查询教师课表
    *@Param:
    *@return:
    *@Author: bear
    *@date: 2019/2/18 10:56
    */
    @Override
    public StudentSchoolTimetabVo findTeacherTimetable2(Long calendarId, String teacherCode) {
        //查询所有教学班
        StudentSchoolTimetabVo vo = new StudentSchoolTimetabVo();
        String name = teachingClassTeacherDao.findTeacherName(teacherCode);
        List<ClassTeacherDto> classTeachers = courseTakeDao.findTeachingClassIds(calendarId, teacherCode);
        if (CollectionUtil.isEmpty(classTeachers)) {
            return vo;
        }
        Map<Long, String> campus = classTeachers.stream()
                .collect(Collectors.toMap(ClassTeacherDto::getTeachingClassId,ClassTeacherDto::getCampus));
        Map<Long, String> courseNames = classTeachers.stream()
                .collect(Collectors.toMap(ClassTeacherDto::getTeachingClassId,ClassTeacherDto::getCourseName));
        Set<Long> ids = campus.keySet();
        List<TimeTableMessage> courseArrange = courseTakeDao.findTeachingArrange(ids, teacherCode);
        String lang = SessionUtils.getLang();
        List<TimeTable> timeTables=new ArrayList<>(courseArrange.size());
        if(CollectionUtil.isNotEmpty(courseArrange)){
            for (TimeTableMessage timeTableMessage : courseArrange) {
                Long teachingClassId = timeTableMessage.getTeachingClassId();
                Integer dayOfWeek = timeTableMessage.getDayOfWeek();
                Integer timeStart = timeTableMessage.getTimeStart();
                Integer timeEnd = timeTableMessage.getTimeEnd();
                String roomID = timeTableMessage.getRoomId();
                String weekNumber = timeTableMessage.getWeekNum();
                String[] str = weekNumber.split(",");
                List<Integer> weeks = Stream.of(str).map(Integer::parseInt).collect(Collectors.toList());
                List<String> weekNums = CalUtil.getWeekNums(weeks.toArray(new Integer[] {}));
                String weekNumStr = weekNums.toString();//周次
                String weekstr = WeekUtil.findWeek(dayOfWeek);//星期
                String timeStr=weekstr+" "+timeStart+"-"+timeEnd+"节"+weekNumStr+ClassroomCacheUtil.getRoomName(roomID);
                String value=name+" "+ courseNames.get(teachingClassId)
                        +"("+ weekNumStr + ClassroomCacheUtil.getRoomName(roomID) + ")"
                        + " " + dictionaryService.query("X_XQ",campus.get(teachingClassId), lang);
                timeTableMessage.setTimeAndRoom(timeStr);
                TimeTable timeTable = new TimeTable();
                timeTable.setDayOfWeek(dayOfWeek);
                timeTable.setTimeStart(timeStart);
                timeTable.setTimeEnd(timeEnd);
                timeTable.setValue(value);
                timeTables.add(timeTable);
            }
            Map<Long, List<TimeTableMessage>> collect = courseArrange.stream().collect(Collectors.groupingBy(TimeTableMessage::getTeachingClassId));
            for (ClassTeacherDto classTeacher : classTeachers) {
                List<TimeTableMessage> timeTableMessages = collect.get(classTeacher.getTeachingClassId());
                if (CollectionUtil.isNotEmpty(timeTableMessages)) {
                    List<String> times = timeTableMessages.stream().map(TimeTableMessage::getTimeAndRoom).collect(Collectors.toList());
                    classTeacher.setTime(String.join(",",times));
                }
            }
        }
        vo.setTimeTables(timeTables);
        vo.setTeacherDtos(classTeachers);
        return vo;
    }

    /**
    *@Description: 查询学生未选课名单
    *@Param:
    *@return:
    *@Author: bear
    *@date: 2019/2/19 15:42
    */
    @Override
    public PageResult<NoSelectCourseStdsDto> findElectCourseList(PageCondition<NoSelectCourseStdsDto> condition) {
        String deptId = SessionUtils.getCurrentSession().getCurrentManageDptId();
    	condition.getCondition().setDeptId(deptId);
        PageHelper.startPage(condition.getPageNum_(),condition.getPageSize_());

        Page<NoSelectCourseStdsDto> electCourseList;
        if (org.apache.commons.lang.StringUtils.isNotEmpty(deptId) && Constants.PROJ_UNGRADUATE.equals(deptId)) {
        	electCourseList = courseTakeDao.findNoSelectCourseStds(condition.getCondition());
		}else {
			electCourseList = courseTakeDao.findNoSelectCourseGraduteStds(condition.getCondition());
		}
        return new PageResult<>(electCourseList);
    }

    /**
     * 增加未选课原因
     * */
    @Override
    public String addNoSelectReason(ElcNoSelectReasonVo noSelectReason) {
        reasonDao.deleteNoSelectReason(noSelectReason.getCalendarId(),noSelectReason.getStudentIds());
        reasonDao.insertReason(noSelectReason.getCalendarId(),noSelectReason.getStudentIds(),noSelectReason.getReason());
        return "common.editSuccess";
    }

    /**查找未选课原因*/
    @Override
    public ElcNoSelectReason findNoSelectReason(Long calendarId, String studentCode) {
        ElcNoSelectReason noSelectReason = reasonDao.findNoSelectReason(calendarId, studentCode);
        return noSelectReason;
    }

    /**代选课*/
    @Override
    public String otherSelectCourse(StudentSelectCourseList studentSelectCourseList) {
        //调用选课接口todo
        return null;
    }

    /**
    *@Description: 本科生导出未选课学生名单
    *@Param:
    *@return:
    *@Author: bear
    *@date: 2019/2/20 15:08
    */
    @Override
    public String exportStudentNoCourseList(NoSelectCourseStdsDto condition) throws Exception{
        PageCondition<NoSelectCourseStdsDto> pageCondition = new PageCondition<NoSelectCourseStdsDto>();
        pageCondition.setCondition(condition);
        pageCondition.setPageSize_(Constants.ZERO);
        pageCondition.setPageNum_(Constants.ZERO);
        PageResult<NoSelectCourseStdsDto> electCourseList = findElectCourseList(pageCondition);
        if(electCourseList!=null){
            List<NoSelectCourseStdsDto> list = electCourseList.getList();
            if (list == null) {
                list = new ArrayList<>();
            }
            GeneralExcelDesigner design = getDesign();
            design.setDatas(list);
            ExcelWriterUtil generalExcelHandle;
            generalExcelHandle = GeneralExcelUtil.generalExcelHandle(design);
            FileUtil.mkdirs(cacheDirectory);
            String fileName = "studentNoSelectCourseList.xls";
            String path = cacheDirectory + fileName;
            generalExcelHandle.writeExcel(new FileOutputStream(path));
            return fileName;
        }
        return "";
    }

    /*
     * 研究生导出未选课学生名单
     */
    @Override
    public String exportStudentNoCourseListGradute(NoSelectCourseStdsDto condition) throws Exception {
    	PageCondition<NoSelectCourseStdsDto> pageCondition = new PageCondition<NoSelectCourseStdsDto>();
        pageCondition.setCondition(condition);
        pageCondition.setPageSize_(Constants.ZERO);
        pageCondition.setPageNum_(Constants.ZERO);
        PageResult<NoSelectCourseStdsDto> electCourseList = findElectCourseList(pageCondition);
        if(electCourseList!=null){
            List<NoSelectCourseStdsDto> list = electCourseList.getList();
            if (list == null) {
                list = new ArrayList<>();
            }
            GeneralExcelDesigner design = getDesignGraduteStudent();
            design.setDatas(list);
            ExcelWriterUtil generalExcelHandle;
            generalExcelHandle = GeneralExcelUtil.generalExcelHandle(design);
            FileUtil.mkdirs(cacheDirectory);
            String fileName = "studentNoSelectCourseListGradute.xls";
            String path = cacheDirectory + fileName;
            generalExcelHandle.writeExcel(new FileOutputStream(path));
            return fileName;
        }
        return "";
    }

    /**
    *@Description: 导出点名册
    *@Param:
    *@return:
    *@Author: bear
    *@date: 2019/2/20 15:48
    */
    @Override
    public ExcelResult exportRollBookList(RollBookConditionDto condition) throws Exception{
        ExcelResult excelResult = ExportExcelUtils.submitTask("rollBookList", new ExcelExecuter() {
            @Override
            public GeneralExcelDesigner getExcelDesigner() {
                ExcelResult result = this.getResult();
                PageCondition<RollBookConditionDto> pageCondition = new PageCondition<RollBookConditionDto>();
                pageCondition.setCondition(condition);
                pageCondition.setPageSize_(100);
                int pageNum = 0;
                pageCondition.setPageNum_(pageNum);
                List<RollBookList> list = new ArrayList<>();
                while (true)
                {
                    pageNum++;
                    pageCondition.setPageNum_(pageNum);
                    PageResult<RollBookList> rollBookList = findRollBookList(pageCondition);
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
     *@Description: 导出研究生点名册
     *@Param:
     *@return:
     *@Author:
     *@date: 2019/7/4
     * @param rollBookConditionDto
     */
    @Override
    public RestResult<String> exportGraduteRollBookList(RollBookConditionDto rollBookConditionDto) throws Exception{
        FileUtil.mkdirs(cacheDirectory);
        //删除超过30天的文件
        FileUtil.deleteFile(cacheDirectory, 30);
        PageCondition condition = PageConditionUtil.getPageCondition(rollBookConditionDto);
        PageResult<RollBookList> rollBookList = findRollBookList(condition);
        String path="";
        if (rollBookList != null) {
            List<RollBookList> list = rollBookList.getList();
            if (CollectionUtil.isNotEmpty(list)) {
                list = SpringUtils.convert(list);
                @SuppressWarnings("unchecked")
                ExcelEntityExport<RollBookList> excelExport = new ExcelEntityExport(list,
                        excelStoreConfig.getGraduteRollBookListKey(),
                        excelStoreConfig.getGraduteRollBookListTitle(),
                        cacheDirectory);
                path = excelExport.exportExcelToCacheDirectory("研究生点名册");
            }
        }
        return RestResult.successData("minor.export.success",path);
    }


    /**
    *@Description: 查询点名册
    *@Param:
    *@return:
    *@Author: bear
    *@date: 2019/4/28 16:26
    */
    @Override
    public PageResult<RollBookList> findRollBookList(PageCondition<RollBookConditionDto> condition) {
        PageConditionUtil.setPageHelper(condition);
        Page<RollBookList> rollBookList = courseTakeDao.findClassByTeacherCode(condition.getCondition());
            if (rollBookList != null) {
                List<RollBookList> result = rollBookList.getResult();
                if (CollectionUtil.isNotEmpty(result)) {
                    List<Long> list = result.stream().map(RollBookList::getTeachingClassId).collect(Collectors.toList());
                    Map<Long, List<RollBookList>> map = new HashMap<>();
                    //批量查询教师名字
                    List<RollBookList> teahers = courseTakeDao.findTeacherName(list);
                    if (CollectionUtil.isNotEmpty(teahers)) {
                        map = teahers.stream().collect(Collectors.groupingBy(RollBookList::getTeachingClassId));
                    }
                    if (map.size() != 0) {
                                for (RollBookList bookList : result) {
                                    List<RollBookList> rollBookLists = map.get(bookList.getTeachingClassId());
                                    if (CollectionUtil.isNotEmpty(rollBookLists)) {
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
     *@Description: 预览点名册
     *@Param:
     *@return:
     *@Author: bear
     *@date: 2019/4/28 16:26
     */
    @Override
    public PreViewRollDto findPreviewRollBookListById(Long teachingClassId,Long calendarId) {
        PreViewRollDto pre=new PreViewRollDto();
        List<StudentVo> student = courseTakeDao.findStudentByTeachingClassId(teachingClassId);
        if(CollectionUtil.isNotEmpty(student)) {
        	for(StudentVo vo:student) {
        		String exportName = vo.getName();
        		if(!StringUtils.equals(vo.getTrainingLevel(), "1")) {
        			exportName ="(#)"+vo.getName();
        		}
        		vo.setExportName(exportName);
        	}
            pre.setStudentsList(student);
            pre.setSize(student.size());
        }
//        SchoolCalendarVo schoolCalendarVo = BaseresServiceInvoker.getSchoolCalendarById(calendarId);
//        pre.setCalendarName(schoolCalendarVo.getFullName());
        //封装教学班信息拆解
        List<Long> ids=new ArrayList<>();
        ids.add(teachingClassId);
        List<ClassTeacherDto> classTimeAndRoom = courseTakeDao.findClassTimeAndRoom(ids);
        Set<String> set=new HashSet<>();
        List<Integer> number=new ArrayList<>();
        int max=0;
        int size=0;
        if(CollectionUtil.isNotEmpty(classTimeAndRoom)){
            for (ClassTeacherDto classTeacherDto : classTimeAndRoom) {
                List<String> num = Arrays.asList(classTeacherDto.getWeekNumberStr().split(","));
                set.addAll(num);//获取最大周数
            }
            for (String s : set) {
                number.add(Integer.valueOf(s));
            }
            max=Collections.max(number);
        }
        List<TimeTableMessage> list = getTimeById(ids);
        if(CollectionUtil.isNotEmpty(list)){
            Map<Integer, List<TimeTableMessage>> collect = list.stream().collect(Collectors.groupingBy(TimeTableMessage::getDayOfWeek));
            size = collect.size();
        }
        pre.setLineNumber(size);
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
        List<StudentVo> student = courseTakeDao.findStudentByTeachingClassId(teachingClassId);
        if(CollectionUtil.isNotEmpty(student)) {
            for(StudentVo vo:student) {
                String exportName = vo.getName();
                Integer courseTakeType = vo.getCourseTakeType();
                if (courseTakeType != null && courseTakeType.intValue() == 2) {
                    exportName ="(重)"+vo.getName();
                } else if (!StringUtils.equals(vo.getTrainingLevel(), "1")) {
                    exportName ="(#)"+vo.getName();
                }
                vo.setExportName(exportName);
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
        Integer max = Collections.max(number);
        pre.setRowNumber(max);
        pre.setLineNumber(days.size());
        return pre;
    }


    /**
    *@Description: 查询学生个人课表
    *@Param:
    *@return:
    *@Author: bear
    *@date: 2019/4/30 11:58
    */
    @Override
    public List<StudnetTimeTable> findStudentTimetab(Long calendarId, String studentCode) {
        List<StudnetTimeTable> studentTable = courseTakeDao.findStudentTable(calendarId, studentCode);//查询所有教学班
        if(CollectionUtil.isNotEmpty(studentTable)){
            List<Long> ids = studentTable.stream().map(StudnetTimeTable::getTeachingClassId).collect(Collectors.toList());
            for (StudnetTimeTable studnetTimeTable : studentTable) {
                List<TimeTableMessage> tableMessages = getTimeById(ids);
                if(CollectionUtil.isNotEmpty(tableMessages)){
                    Map<Long, List<TimeTableMessage>> map = tableMessages.stream().collect(Collectors.groupingBy(TimeTableMessage::getTeachingClassId));
                    List<TimeTableMessage> timeTableMessages = map.get(studnetTimeTable.getTeachingClassId());
                    studnetTimeTable.setTimeTableList(timeTableMessages);
                    if(CollectionUtil.isNotEmpty(timeTableMessages)){
                        Set<String> teacher=new HashSet<>();
                        Set<String> timeTabel=new HashSet<>();
                        Set<String> room=new HashSet<>();
                        for (TimeTableMessage tableMessage : timeTableMessages) {
                            String teacherName = tableMessage.getTeacherName();
                            String timeTab = tableMessage.getTimeTab();
                            String roomId = tableMessage.getRoomId();
                            List<String> list = Arrays.asList(teacherName.split(","));
                            if(CollectionUtil.isNotEmpty(list)){
                                teacher.addAll(list);
                            }
                            if(StringUtils.isNotEmpty(timeTab)){
                                timeTabel.add(timeTab);
                            }
                            if(StringUtils.isNotEmpty(roomId)){
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
    *@Description: 查询所有教师课表
    *@Param:
    *@return:
    *@Author: bear
    *@date: 2019/4/30 17:39
    */
    @Override
    public PageResult<ClassCodeToTeacher> findAllTeacherTimeTable(PageCondition<ClassCodeToTeacher> condition) {
        PageHelper.startPage(condition.getPageNum_(),condition.getPageSize_());
        Page<ClassCodeToTeacher> teacherTimeTable = courseTakeDao.findAllTeacherTimeTable(condition.getCondition());
        if(teacherTimeTable!=null){
            List<ClassCodeToTeacher> result = teacherTimeTable.getResult();
            if(CollectionUtil.isNotEmpty(result)){
                SchoolCalendarVo schoolCalendar= BaseresServiceInvoker.getSchoolCalendarById(condition.getCondition().getCalendarId());
                for (ClassCodeToTeacher toTeacher : result) {
                    toTeacher.setCalendarName(schoolCalendar.getFullName());
                }
            }
        }
        return new PageResult<>(teacherTimeTable);
    }

    /**
     * 查询所有教师课表
     * @param condition
     * @return
     */
    @Override
    public PageResult<ClassCodeToTeacher> findTeacherTimeTableByRole(PageCondition<ClassCodeToTeacher> condition) {
        PageHelper.startPage(condition.getPageNum_(), condition.getPageSize_());
        Page<ClassCodeToTeacher> teacherTimeTable = courseTakeDao.findTeacherTimeTableByRole(condition.getCondition());
        if (CollectionUtil.isNotEmpty(teacherTimeTable)) {
            List<ClassCodeToTeacher> result = teacherTimeTable.getResult();
            if (CollectionUtil.isNotEmpty(result)) {
                SchoolCalendarVo schoolCalendar = BaseresServiceInvoker.getSchoolCalendarById(condition.getCondition().getCalendarId());
                for (ClassCodeToTeacher toTeacher : result) {
                    toTeacher.setCalendarName(schoolCalendar.getFullName());
                }
            }
        }
        return new PageResult<>(teacherTimeTable);
    }

    /**
    *@Description: 学生课表其他服务调用
    *@Param:
    *@return:
    *@Author: bear
    *@date: 2019/5/5 9:34
    */
    @Cacheable(value="getStudentTimetab", key="#p1")
    @Override
    public List<TimeTable> getStudentTimetab(Long calendarId, String studentCode, Integer week) {
        List<StudnetTimeTable> studentTable = courseTakeDao.findStudentTable(calendarId, studentCode);//查询所有教学班
        List<TimeTable> list =new ArrayList<>();
        if(CollectionUtil.isNotEmpty(studentTable)){
            List<Long> ids = studentTable.stream().map(StudnetTimeTable::getTeachingClassId).collect(Collectors.toList());
            List<TimeTableMessage> timeById = getTimeById(ids);
            for (TimeTableMessage tm : timeById) {
                if(null != tm.getWeeks() && null != week && !tm.getWeeks().contains(week)) {
                    continue;
                }
                TimeTable tt=new TimeTable();
                String name="";
                if(StringUtils.isNotBlank(tm.getRoomId())){
                    name = ClassroomCacheUtil.getRoomName(tm.getRoomId());
                }
                String value= String.format("%s %s(%s,%s)", tm.getTeacherName(), tm.getCourseName(), tm.getWeekNum(), name);
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
    *@Description: 查询教师课表
    *@Param:
    *@return:
    *@Author: bear
    *@date: 2019/5/5 14:13
    */
    @Override
    public List<TeacherTimeTable> findTeacherTimetable(Long calendarId, String teacherCode) {
        List<TeacherTimeTable > classTimeAndRoom=courseTakeDao.findTeacherTimetable(calendarId,teacherCode);
        if(CollectionUtil.isNotEmpty(classTimeAndRoom)){
            List<Long> ids = classTimeAndRoom.stream().map(TeacherTimeTable::getTeachingClassId).collect(Collectors.toList());
            List<TimeTableMessage> tableMessages = getTimeById(ids);
            Map<Long, List<TimeTableMessage>> listMap=new HashMap<>();
            if(CollectionUtil.isNotEmpty(tableMessages)){
                List<TimeTableMessage> timeTableMessages = tableMessages.stream().filter(vo -> Arrays.asList(vo.getTeacherCode().split(",")).contains(teacherCode)).collect(Collectors.toList());
                if(CollectionUtil.isNotEmpty(timeTableMessages)){
                    listMap = timeTableMessages.stream().collect(Collectors.groupingBy(TimeTableMessage::getTeachingClassId));
                }
            }
            for (TeacherTimeTable teacherTimeTable : classTimeAndRoom) {
                List<TimeTableMessage> timeTableMessages = listMap.get(teacherTimeTable.getTeachingClassId());
                String labelName = CultureSerivceInvoker.getCourseLabelNameById(teacherTimeTable.getCourseLabel());
                teacherTimeTable.setCourseLabelName(labelName);
                teacherTimeTable.setTimeTableList(timeTableMessages);
                if(CollectionUtil.isNotEmpty(timeTableMessages)){
                    Set<String> timeTabel=new HashSet<>();
                    Set<String> room=new HashSet<>();
                    for (TimeTableMessage tableMessage : timeTableMessages) {
                            String timeTab = tableMessage.getTimeTab();
                            String roomId = tableMessage.getRoomId();
                            if(StringUtils.isNotEmpty(timeTab)){
                                timeTabel.add(timeTab);
                            }
                            if(StringUtils.isNotEmpty(roomId)){
                                room.add(roomId);
                            }
                    }
                    String classTime = String.join(",", timeTabel);
                    String classRoom = String.join(",", room);
                    teacherTimeTable.setClassTime(classTime);
                    teacherTimeTable.setClassRoom(classRoom);
                }

            }

        }
        return classTimeAndRoom;
    }

    /**
    *@Description: 登陆获取教师课表
    *@Param:
    *@return:
    *@Author: bear
    *@date: 2019/5/5 16:33
    */
    @Override
    public List<TimeTable> getTeacherTimetable(Long calendarId, String teacherCode, Integer week) {
        List<TimeTable> list=new ArrayList<>();
        List<TeacherTimeTable > classList=courseTakeDao.findTeacherTimetable(calendarId,teacherCode);
        if(CollectionUtil.isNotEmpty(classList)){
            List<Long> ids = classList.stream().map(TeacherTimeTable::getTeachingClassId).collect(Collectors.toList());
            List<TimeTableMessage> tableMessages = getTimeById(ids);
            List<TimeTableMessage> teacherList=new ArrayList<>();
            if(CollectionUtil.isNotEmpty(tableMessages)){
                teacherList = tableMessages.stream().filter(vo -> {
                  return Arrays.asList(vo.getTeacherCode().split(",")).contains(teacherCode) && vo.getWeeks().contains(week);
                }).collect(Collectors.toList());

                if(CollectionUtil.isNotEmpty(teacherList)) {
                    for (TimeTableMessage tm : teacherList) {
                        TimeTable tt=new TimeTable();
                        String name="";
                        if(StringUtils.isNotBlank(tm.getRoomId())){
                            name = ClassroomCacheUtil.getRoomName(tm.getRoomId());
                        }
                        String value= String.format("%s %s (%s,%s)", tm.getClassCode(), tm.getCourseName(), tm.getWeekNum(), name);
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
            }

        }
        return list;
    }

    /**
    *@Description: 导出未选课名单
    *@Param:
    *@return:
    *@Author: bear
    *@date: 2019/5/8 16:13
    */
    @Override
    public ExcelResult export(NoSelectCourseStdsDto condition) {
        ExcelResult excelResult = ExportExcelUtils.submitTask("noSelectCourseList", new ExcelExecuter() {
            @Override
            public GeneralExcelDesigner getExcelDesigner() {
                ExcelResult result = this.getResult();
                PageCondition<NoSelectCourseStdsDto> pageCondition = new PageCondition<NoSelectCourseStdsDto>();
                pageCondition.setCondition(condition);
                pageCondition.setPageSize_(100);
                int pageNum = 0;
                pageCondition.setPageNum_(pageNum);
                List<NoSelectCourseStdsDto> list = new ArrayList<>();
                while (true)
                {
                    pageNum++;
                    pageCondition.setPageNum_(pageNum);
                    PageResult<NoSelectCourseStdsDto> electCourseList = findElectCourseList(pageCondition);
                    list.addAll(electCourseList.getList());

                    result.setTotal((int)electCourseList.getTotal_());
                    Double count = list.size() / 1.5;
                    result.setDoneCount(count.intValue());
                    this.updateResult(result);

                    if (electCourseList.getTotal_() <= list.size())
                    {
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
    *@Description: 导出所有教师课表
    *@Param:
    *@return:
    *@Author: bear
    *@date: 2019/5/8 16:13
    */
    @Override
    public ExcelResult exportTeacher(ClassCodeToTeacher condition) {
        ExcelResult excelResult = ExportExcelUtils.submitTask("allTeacherList", new ExcelExecuter() {
            @Override
            public GeneralExcelDesigner getExcelDesigner() {
                ExcelResult result = this.getResult();
                PageCondition<ClassCodeToTeacher> pageCondition = new PageCondition<ClassCodeToTeacher>();
                pageCondition.setCondition(condition);
                pageCondition.setPageSize_(100);
                int pageNum = 0;
                pageCondition.setPageNum_(pageNum);
                List<ClassCodeToTeacher> list = new ArrayList<>();
                while (true)
                {
                    pageNum++;
                    pageCondition.setPageNum_(pageNum);
                    PageResult<ClassCodeToTeacher> allTeacherTimeTable = findAllTeacherTimeTable(pageCondition);
                    list.addAll(allTeacherTimeTable.getList());

                    result.setTotal((int)allTeacherTimeTable.getTotal_());
                    Double count = list.size() / 1.5;
                    result.setDoneCount(count.intValue());
                    this.updateResult(result);

                    if (allTeacherTimeTable.getTotal_() <= list.size())
                    {
                        break;
                    }
                }
                //组装excel
                GeneralExcelDesigner design = getDesignTeacher();
                //将数据放入excel对象中
                design.setDatas(list);
                result.setDoneCount(list.size());
                return design;
            }
        });
        return excelResult;
    }

    /**
    *@Description: 导出预览点名册
    *@Param:
    *@return:
    *@Author: bear
    *@date: 2019/5/8 16:14
    */
    @Override
    public String exportPreRollBookList(ExportPreCondition condition) throws Exception{
        PreViewRollDto preViewRollDto = previewGraduteRollBook(condition.getTeachingClassId());
        List<StudentVo> studentsList = preViewRollDto.getStudentsList();
        SchoolCalendarVo schoolCalendarVo = BaseresServiceInvoker.getSchoolCalendarById(condition.getCalendarId());
        String calendarName ="同济大学"+ schoolCalendarVo.getFullName() + "学生点名册";
        Integer lineNumber = preViewRollDto.getLineNumber();
        Integer rowNumber = preViewRollDto.getRowNumber();
        List<Integer> lineList = new ArrayList<>();
        if(lineNumber>1) {
            for(int i=0;i<lineNumber-1;i++) {
            	lineList.add(i);
            }
        }
        FileUtil.mkdirs(cacheDirectory);
        FileUtil.deleteFile(cacheDirectory, 2);
        String fileName = "preRollBookList-" + System.currentTimeMillis() + ".xls";
        String path = cacheDirectory + fileName;
        Map<String, Object> map = new HashMap<>();
        map.put("list", studentsList);
        map.put("calendar",calendarName);
        map.put("lineNumber",lineNumber-1);
        map.put("rowNumber",rowNumber);
        map.put("lineList",lineList);
        map.put("item",condition);
        Template tpl = freeMarkerConfigurer.getConfiguration().getTemplate("preRollBookList1.ftl");
        // 将模板和数据模型合并生成文件
        Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path), "UTF-8"));
        tpl.process(map, out);
        // 关闭流
        out.flush();
        out.close();
        return path;
    }


    private GeneralExcelDesigner getDesignTwo() {
        GeneralExcelDesigner design = new GeneralExcelDesigner();
        design.setNullCellValue("");
        design.addCell(I18nUtil.getMsg("rollBookManage.teachingClass"), "classCode");
        design.addCell(I18nUtil.getMsg("exemptionApply.courseCode"), "courseCode");
        design.addCell(I18nUtil.getMsg("exemptionApply.courseName"), "courseName");
        design.addCell(I18nUtil.getMsg("rollBookManage.teachingClassName"), "className");
        design.addCell(I18nUtil.getMsg("rebuildCourse.label"), "courseLabel");
        design.addCell(I18nUtil.getMsg("rollBookManage.actualNumber"), "selectCourseNumber");
        design.addCell(I18nUtil.getMsg("rollBookManage.upperLimit"), "numberLimit");

        design.addCell(I18nUtil.getMsg("rollBookManage.courseOpenFaculty"), "faculty").setValueHandler(
                (value, rawData, cell) -> {
                    return dictionaryService.query("X_YX", value, SessionUtils.getLang());
                });

        design.addCell(I18nUtil.getMsg("rollBookManage.teacher"), "teacherName");
        return design;
    }

    private GeneralExcelDesigner getDesign() {
        GeneralExcelDesigner design = new GeneralExcelDesigner();
        design.setNullCellValue("");
        design.addCell(I18nUtil.getMsg("exemptionApply.studentCode"), "studentCode");
        design.addCell(I18nUtil.getMsg("exemptionApply.studentName"), "studentName");
        design.addCell(I18nUtil.getMsg("rebuildCourse.grade"), "grade");
        design.addCell(I18nUtil.getMsg("exemptionApply.faculty"), "faculty").setValueHandler(
                (value, rawData, cell) -> {
                    return dictionaryService.query("X_YX", value, SessionUtils.getLang());
                });

        design.addCell(I18nUtil.getMsg("exemptionApply.major"), "major").setValueHandler(
                (value, rawData, cell) -> {
                    return dictionaryService.query("G_ZY", value, SessionUtils.getLang());
                });
        design.addCell(I18nUtil.getMsg("rebuildCourse.studentStatus"), "stdStatusChanges");
        design.addCell(I18nUtil.getMsg("rebuildCourse.noSelectCourseReason"), "noSelectReason");
        return design;
    }

    private GeneralExcelDesigner getDesignGraduteStudent() {
    	GeneralExcelDesigner design = new GeneralExcelDesigner();
    	design.setNullCellValue("");
    	design.addCell(I18nUtil.getMsg("exemptionApply.studentCode"), "studentCode");
    	design.addCell(I18nUtil.getMsg("exemptionApply.studentName"), "studentName");
    	design.addCell(I18nUtil.getMsg("rebuildCourse.grade"), "grade");
    	design.addCell(I18nUtil.getMsg("exemptionApply.faculty"), "faculty").setValueHandler(
    			(value, rawData, cell) -> {
    				return dictionaryService.query("X_YX", value, SessionUtils.getLang());
    			});
    	design.addCell(I18nUtil.getMsg("exemptionApply.major"), "major").setValueHandler(
    			(value, rawData, cell) -> {
    				return dictionaryService.query("G_ZY", value, SessionUtils.getLang());
    			});
    	design.addCell(I18nUtil.getMsg("rebuildCourse.trainingLevel"), "trainingLevel").setValueHandler(
    			(value, rawData, cell) -> {
    				return dictionaryService.query("X_PYCC", value, SessionUtils.getLang());
    			});
    	design.addCell(I18nUtil.getMsg("noElection.trainingCategory"), "trainingCategory").setValueHandler(
    			(value, rawData, cell) -> {
    				return dictionaryService.query("X_PYLB", value, SessionUtils.getLang());
    			});
    	design.addCell(I18nUtil.getMsg("noElection.degreeType"), "degreeType").setValueHandler(
    			(value, rawData, cell) -> {
    				return dictionaryService.query("X_XWLX", value, SessionUtils.getLang());
    			});
    	design.addCell(I18nUtil.getMsg("noElection.formLearning"), "formLearning").setValueHandler(
    			(value, rawData, cell) -> {
    				return dictionaryService.query("X_XXXS", value, SessionUtils.getLang());
    			});
    	design.addCell(I18nUtil.getMsg("rebuildCourse.studentStatus"), "stdStatusChanges");
    	return design;
    }

    private GeneralExcelDesigner getDesignTeacher() {
        GeneralExcelDesigner design = new GeneralExcelDesigner();
        design.setNullCellValue("");
        design.addCell(I18nUtil.getMsg("exemptionApply.calendarName"), "calendarName");
        design.addCell(I18nUtil.getMsg("rollBookManage.teacherCode"), "teacherCode");
        design.addCell(I18nUtil.getMsg("exemptionApply.studentName"), "teacherName");
        design.addCell(I18nUtil.getMsg("rollBookManage.teachingClassName"), "classCode");
        design.addCell(I18nUtil.getMsg("exemptionApply.courseCode"), "courseCode");
        design.addCell(I18nUtil.getMsg("exemptionApply.courseName"), "courseName");
        design.addCell(I18nUtil.getMsg("exemptionApply.faculty"), "faculty").setValueHandler(
                (value, rawData, cell) -> {
                    return dictionaryService.query("X_YX", value, SessionUtils.getLang());
                });

        design.addCell(I18nUtil.getMsg("rebuildCourse.sex"), "sex").setValueHandler(
                (value, rawData, cell) -> {
                    return dictionaryService.query("G_XBIE", value, SessionUtils.getLang());
                });
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
   private List<String> findTeacherByTeachingClassId(Long id){
       List<String> names=new ArrayList<>();
       List<ClassTeacherDto> teacher = courseTakeDao.findTeacherByClassCode(id);
       if(CollectionUtil.isNotEmpty(teacher)) {
           //Map<Long, List<ClassTeacherDto>> collect = teacher.stream().collect(Collectors.groupingBy(ClassTeacherDto::getTeachingClassId));
           //List<ClassTeacherDto> classTeacherDtos = collect.get(id);
           //List<ClassTeacherDto> classTeacherDtoStream = teacher.stream().filter((ClassTeacherDto dto) -> dto.getTeachingClassId().longValue() == id.longValue()).collect(Collectors.toList());
           //if (CollectionUtil.isNotEmpty(classTeacherDtoStream)) {
               names = teacher.stream().map(ClassTeacherDto::getTeacherName).filter(StringUtils::isNotBlank).collect(Collectors.toList());
           //}
       }
       return names;
   }

   /**
   *@Description: 通过classId查询教室信息
   *@Param:
   *@return:
   *@Author: bear
   *@date: 2019/2/15 17:19
   */

   private String findClassroomAndTime(Long id){
       //查询教学班信息TeachingClassId查询
       StringBuilder str=new StringBuilder();
       List<Long> ids=new ArrayList<>();
       ids.add(id);
       List<ClassTeacherDto> classTimeAndRoom = courseTakeDao.findClassTimeAndRoom(ids);
       if(CollectionUtil.isNotEmpty(classTimeAndRoom)){
           Map<Long, List<ClassTeacherDto>> collect = classTimeAndRoom.stream().collect(Collectors.groupingBy(ClassTeacherDto::getTimeId));
           if(collect.size()!=0){
               for (List<ClassTeacherDto> list : collect.values()) {
                   if(CollectionUtil.isNotEmpty(list)){
                       ClassTeacherDto item=list.get(0);
                       Integer dayOfWeek = item.getDayOfWeek();
                       Integer timeStart = item.getTimeStart();
                       Integer timeEnd = item.getTimeEnd();
                       //List<Integer> integerList = list.stream().map(ClassTeacherDto::getWeekNumber).collect(Collectors.toList());
                       List<String> asList = Arrays.asList(item.getWeekNumberStr().split(","));
                       List<Integer> intList = new ArrayList<Integer>();
                       for (String string : asList) {
						   intList.add(Integer.parseInt(string));
					   }
                       Integer maxWeek = Collections.max(intList);
                       Integer minWeek = Collections.min(intList);
                       Set<String> rooms = list.stream().map(ClassTeacherDto::getRoomID).filter(StringUtils::isNotBlank).collect(Collectors.toSet());
                       String roomId = String.join(",", rooms);
                       String time=WeekUtil.findWeek(dayOfWeek)+" "+timeStart+"-"+timeEnd+"["+minWeek+"-"+maxWeek+"] "+roomId;
                       str.append(time);
                       str.append("/");
                   }
               }

           }
       }
       return str.toString();
   }

	@Override
	public RestResult<String> exportStudentTimetabPdf(Long calendarId, String calendarName, String studentCode,
			String studentName) throws Exception {
		//检查目录是否存在
		//cacheDirectory = "C://temp//pdf//cacheWord";
        LOG.info("缓存目录："+cacheDirectory);
        FileUtil.mkdirs(cacheDirectory);
        //删除超过30天的文件
        FileUtil.deleteFile(cacheDirectory, 30);
		
        /************************ PDF初始化操作 ******************************/
        //所有使用中文处理
        BaseFont bfChinese = BaseFont.createFont("STSongStd-Light","UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
        //粗体文字 title使用
        Font titleChinese = new Font(bfChinese, 20, Font.BOLD);
        //副标题
        Font subtitleChinese = new Font(bfChinese, 13, Font.BOLD);
        //正常文字
        //Font name1 = new Font(bfChinese, 12, Font.BOLD, BaseColor.GRAY);
        Font name2 = new Font(bfChinese, 12, Font.NORMAL);
          
        String fileName = new StringBuffer("")
                .append(cacheDirectory)
                .append("//")
                .append(System.currentTimeMillis())
                .append("_")
                .append(studentName)
                .append(".pdf")
                .toString();
        Document document = new Document(PageSize.A4,24,24,16,16);
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
        Paragraph subtitle = new Paragraph(calendarName, subtitleChinese);
        subtitle.setAlignment(Element.ALIGN_CENTER);
        //设置行间距
        subtitle.setLeading(10);
        //内容距离左边8个单位
        subtitle.setFirstLineIndent(7);
        subtitle.setIndentationRight(20);
        //副标题与标题之间的距离
        subtitle.setSpacingBefore(15);
        document.add(subtitle);
		
        //----3 学生基本信息----
        StudentSchoolTimetabVo studentTimetab = findSchoolTimetab2(calendarId,studentCode);
        
        PdfPTable table1 = new PdfPTable(4);
        //前间距
        table1.setSpacingBefore(5);
        
        PdfPCell cell1 = createNoBorderCell("学号："+studentCode,name2,20f);
        table1.addCell(cell1);
       
        PdfPCell cell2 = createNoBorderCell("学生姓名："+studentName, name2,20f);
        table1.addCell(cell2);
        
        PdfPCell cell3 = createNoBorderCell("所属班级："+studentName, name2,20f);
        table1.addCell(cell3);
        
        PdfPCell cell4 = createNoBorderCell("总学分："+studentTimetab.getTotalCredits(), name2,20f);
        table1.addCell(cell4);
        document.add(table1);
        
        // ----3 学生选课课表展示---- 
        PdfPTable table2 = createStudentTable(studentTimetab.getTimeTables(),subtitleChinese,name2);
        document.add(table2);
        
        // ----4 学生选课列表 -------
        PdfPTable table3 = createStudentTimeList(studentTimetab.getList(),subtitleChinese,name2);
        document.add(table3);
        
        document.close();
        return RestResult.successData("导出成功。",fileName);
	}
	
	
	/**
	 * @param 创建选课课表table
	 */
	public PdfPTable createStudentTable(List<TimeTable> tables,Font subtitleChinese,Font name2) throws DocumentException, IOException {
		PdfPTable table = new PdfPTable(setSchoolTimeTitle.length);
		table.setWidthPercentage(100);
		table.setSpacingBefore(10);
		
		// 添加课表表头
		for (int i = 0; i < setSchoolTimeTitle.length; i++) {
			PdfPCell cell = createCell(setSchoolTimeTitle[i],1,1,subtitleChinese,30f);
			table.addCell(cell);
		}
		
		// 添加课表内容
		//setTimeListCol
		for (int i = 0; i < setSchoolTimeCol.length; i++) {        // 12行  节次
			for (int j = 0; j < setSchoolTimeTitle.length; j++) {  // 8列    星期
				PdfPCell cell = new PdfPCell(new Paragraph("",name2));
				if (j == 0) {
					cell = createCell(setSchoolTimeCol[i],1,1,name2,30f);
					table.addCell(cell);
				}else {
					TimeTable time = hasCourseArrangment(j,i+1,tables);
					if (time != null) {
						if (time.getTimeStart() == i+1) {
							cell = createCell(time.getValue(),1,time.getTimeEnd()-i,name2,30f);
							table.addCell(cell);
						}
					}else {
						cell = createCell("",1,1,name2,30f);
						table.addCell(cell);
					}
				}
			}
		}
		return table;
	}
	
	/**
	   *   查询：对应的星期对应的节次是否有课程安排
	 * @param dayOfWeek  星期几
	 * @param timeNo     第几节课
	 * @param tables     课程安排list
	 * @return
	 */
	public TimeTable hasCourseArrangment(int dayOfWeek,int timeNo,List<TimeTable> tables) {
		TimeTable time = null;
		for (TimeTable timeTable : tables) {
			if (timeTable.getDayOfWeek() == dayOfWeek && timeTable.getTimeStart() <= timeNo && timeNo <= timeTable.getTimeEnd()) {
				time = timeTable;
				break;
			}
		}
		return time;
	}
	
	public TimeTable getTimeTable(int dayOfWeek,int timeStart,List<TimeTable> tables) {
		TimeTable time = new TimeTable();
		for (TimeTable timeTable : tables) {
			if (timeTable.getDayOfWeek() == dayOfWeek && timeTable.getTimeStart() == timeStart+1) {
				time = timeTable;
			}
		}
		return time;
	}
	/**
	   * 创建学生选课列表table
	 * @throws IOException 
	 */
	public PdfPTable createStudentTimeList(List<StudentSchoolTimetab> list,Font subtitleChinese,Font name2) throws DocumentException, IOException {
		PdfPTable table = new PdfPTable(setTimeListTitle.length);
		table.setWidthPercentage(100);
		table.setSpacingBefore(10);
		
		// 添加表头
		for (int i = 0; i < setTimeListTitle.length; i++) {
			PdfPCell cell = createCell(setTimeListTitle[i],1,1,subtitleChinese,null);
			table.addCell(cell);
		}

		// 添加表内容
		for (int j = 0; j < list.size(); j++) {
			List<String> timeTableList = getTimeTableList(list.get(j));
			for (int i = 0; i < setTimeListTitle.length - 1; i++) {
				PdfPCell cell = new PdfPCell();
				if (i == 0) {
					cell = createCell(String.valueOf(j+1),1,1,name2,null);
				}else {
					cell = createCell(timeTableList.get(i-1),1,1,name2,null);
				}
				table.addCell(cell);
			}
		}
		return table;
	}
	
	public List<String> getTimeTableList(StudentSchoolTimetab timeTable){
		List<String> list = new ArrayList<String>();
		list.add(timeTable.getCourseCode());
		list.add(timeTable.getCourseName());
		list.add("2".equals(timeTable.getCourseType())?"是":"");
        Integer isElective = timeTable.getIsElective();
        if (isElective != null) {
            list.add( isElective == 1?"选修":"必修");
        }
		list.add(timeTable.getAssessmentMode());
		list.add(String.valueOf(timeTable.getCredits()));
		list.add(timeTable.getTeacherName());
		list.add(timeTable.getTime()+timeTable.getRoom());
		list.add(timeTable.getRemark());
		return list;
	}
	
    /**
            *     生成一个有边框有跨行的小单元格
     * @param content 内容
     * @param rowSpan 跨行
     * @param colSpan 跨列
     * @param font 字体 传null视为默认黑色字体
     * @param height 单元格高度  传null默认为自动填充
     * @throws IOException
     * @throws DocumentException
     */
    private PdfPCell createCell(String content,int rowSpan,int colSpan,Font font,Float height) throws IOException, DocumentException {
        if (font == null) {
            BaseFont bfChinese = BaseFont.createFont("STSongStd-Light","UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
            font = new Font(bfChinese,8,Font.NORMAL );
        }
        // 构建每个单元格
        PdfPCell cell = new PdfPCell(new Paragraph(content,font));
        // 设置内容水平居中显示
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        // 设置垂直居中
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        // 设置边框的颜色
        cell.setBorderColor(new BaseColor(15, 110, 176));
        // 设置单元格的行高
        if (height != null) {
        	cell.setFixedHeight(height);
		}
        cell.setColspan(rowSpan);
        cell.setRowspan(colSpan);
        return cell;
    }
    
    /**
             *   生成一个无边框无跨行的单元格
     * @param content  内容
     * @param font     字体 传null视为默认黑色字体
     * @param width    单元格宽度
     * @return
     */
    private PdfPCell createNoBorderCell(String content,Font font,Float width) {
        PdfPCell cell = new PdfPCell(new Paragraph(content, font));
        //无边框
        cell.setBorder(Rectangle.NO_BORDER);
        // 设置内容水平居左显示
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        // 设置内容垂直居左显示
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setBorderWidth(width);
        return cell;
    }

	@Override
	public RestResult<String> exportTeacherTimetabPdf(Long calendarId, String calendarName, String teacherCode,
			String teacherName) throws DocumentException, IOException {
		//检查目录是否存在
		cacheDirectory = "C://temp//pdf//cacheWord";
        LOG.info("缓存目录："+cacheDirectory);
        FileUtil.mkdirs(cacheDirectory);
        //删除超过30天的文件
        FileUtil.deleteFile(cacheDirectory, 30);
		
        /************************ PDF初始化操作 ******************************/
        //所有使用中文处理
        BaseFont bfChinese = BaseFont.createFont("STSongStd-Light","UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
        //粗体文字 title使用
        Font titleChinese = new Font(bfChinese, 20, Font.BOLD);
        //副标题
        Font subtitleChinese = new Font(bfChinese, 13, Font.BOLD);
        //正常文字
        //Font name1 = new Font(bfChinese, 12, Font.BOLD, BaseColor.GRAY);
        Font name2 = new Font(bfChinese, 12, Font.NORMAL);
          
        String fileName = new StringBuffer("")
                .append(cacheDirectory)
                .append("//")
                .append(System.currentTimeMillis())
                .append("_")
                .append(teacherName)
                .append(".pdf")
                .toString();
        Document document = new Document(PageSize.A4,24,24,16,16);
        PdfWriter.getInstance(document, new FileOutputStream(fileName));
		
        /************************ PDF填充内容  ******************************/
        document.open();
        //---1 添加标题---
        Paragraph title = new Paragraph("教师课程表", titleChinese);
        //居中设置
        title.setAlignment(Element.ALIGN_CENTER);
        //设置行间距
        title.setLeading(20);
        title.setSpacingBefore(30);
        document.add(title);
        
        //---2 副标题---
        Paragraph subtitle = new Paragraph(calendarName, subtitleChinese);
        subtitle.setAlignment(Element.ALIGN_CENTER);
        //设置行间距
        subtitle.setLeading(10);
        //内容距离左边8个单位
        subtitle.setFirstLineIndent(7);
        subtitle.setIndentationRight(20);
        //副标题与标题之间的距离
        subtitle.setSpacingBefore(15);
        document.add(subtitle);
		
        //----3 教师基本信息----
        StudentSchoolTimetabVo teacherTimetab = findTeacherTimetable2(calendarId,teacherCode);
        
        PdfPTable table1 = new PdfPTable(4);
        //前间距
        table1.setSpacingBefore(5);
        
        PdfPCell cell1 = createNoBorderCell("工号："+teacherCode,name2,20f);
        table1.addCell(cell1);
       
        PdfPCell cell2 = createNoBorderCell("姓名："+teacherName, name2,20f);
        table1.addCell(cell2);
        
        PdfPCell cell3 = createNoBorderCell("学院："+teacherTimetab.getFaculty(), name2,20f);
        table1.addCell(cell3);
        
        PdfPCell cell4 = createNoBorderCell("", name2,20f);
        table1.addCell(cell4);
        document.add(table1);
        
        // ----3 教师选课课表展示---- 
        PdfPTable table2 = createStudentTable(teacherTimetab.getTimeTables(),subtitleChinese,name2);
        document.add(table2);
        
        // ----4 学生选课列表 -------
        PdfPTable table3 = createTeacherTimeList(teacherTimetab.getTeacherDtos(),subtitleChinese,name2);
        document.add(table3);
        
        document.close();
        return RestResult.successData("导出成功。",fileName);
	}
	
	public PdfPTable createTeacherTimeList(List<ClassTeacherDto> list,Font subtitleChinese,Font name2) throws IOException, DocumentException {
		PdfPTable table = new PdfPTable(setTimeListTeacherTitle.length);
		table.setWidthPercentage(100);
		table.setSpacingBefore(10);
		
		// 添加表头
		for (int i = 0; i < setTimeListTeacherTitle.length; i++) {
			PdfPCell cell = createCell(setTimeListTeacherTitle[i],1,1,subtitleChinese,null);
			table.addCell(cell);
		}
		
		// 添加表内容
		for (int j = 0; j < list.size(); j++) {
			List<String> timeTableList = getTimeTableTeacherList(list.get(j));
			for (int i = 0; i < setTimeListTeacherTitle.length; i++) {
				PdfPCell cell = new PdfPCell();
				if (i == 0) {
					cell = createCell(String.valueOf(j+1),1,1,name2,null);
				}else {
					cell = createCell(timeTableList.get(i-1),1,1,name2,null);
				}
				table.addCell(cell);
			}
		}
		return table;
	}

	private List<String> getTimeTableTeacherList(ClassTeacherDto classTeacherDto) {
		List<String> list = new ArrayList<String>();
		list.add(classTeacherDto.getClassCode());
		list.add(classTeacherDto.getCourseName());
		list.add(classTeacherDto.getNature());
		list.add(String.valueOf(classTeacherDto.getWeekHour()));
		list.add(String.valueOf(classTeacherDto.getCredits()));
		list.add(classTeacherDto.getTeachingLanguage());
		list.add(classTeacherDto.getTime());
		list.add(String.valueOf(classTeacherDto.getSelectCourseNumber()));
		list.add(classTeacherDto.getRemark());
		return list;
	}

    private List<TimeTableMessage> getTimeById(List<Long> teachingClassId) {
        List<TimeTableMessage> list = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(teachingClassId)) {
            List<ClassTeacherDto> classTimeAndRoom = courseTakeDao.findClassTimeAndRoom(teachingClassId);
            if (CollectionUtil.isNotEmpty(classTimeAndRoom)) {
                for (ClassTeacherDto classTeacherDto : classTimeAndRoom) {
                    Integer dayOfWeek = classTeacherDto.getDayOfWeek();
                    Integer timeStart = classTeacherDto.getTimeStart();
                    Integer timeEnd = classTeacherDto.getTimeEnd();
                    String weekNumber = classTeacherDto.getWeekNumberStr();
                    String[] str = weekNumber.split(",");

                    List<Integer> weeks = Arrays.asList(str).stream().map(Integer::parseInt).collect(Collectors.toList());
                    List<String> weekNums = CalUtil.getWeekNums(weeks.toArray(new Integer[]{}));
                    String weekNumStr = weekNums.toString();//周次
                    String weekstr = WeekUtil.findWeek(dayOfWeek);//星期
                    String timeStr = weekstr + " " + timeStart + "-" + timeEnd + "节" + weekNumStr + dictionaryService.query("X_XQ",classTeacherDto.getCampus());
                    TimeTableMessage time = new TimeTableMessage();
                    time.setTeachingClassId(classTeacherDto.getTeachingClassId());
                    time.setTimeAndRoom(timeStr);
                    list.add(time);
                }
            }
        }
        return list;
    }
}
