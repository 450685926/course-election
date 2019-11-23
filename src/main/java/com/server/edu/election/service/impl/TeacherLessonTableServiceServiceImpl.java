package com.server.edu.election.service.impl;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.server.edu.dictionary.DictTypeEnum;
import com.server.edu.election.dto.*;
import com.server.edu.election.vo.TeachingClassTeacherVo;
import com.server.edu.exception.ParameterValidateException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

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
import com.server.edu.common.entity.Teacher;
import com.server.edu.common.locale.I18nUtil;
import com.server.edu.common.rest.PageResult;
import com.server.edu.common.rest.RestResult;
import com.server.edu.common.vo.SchoolCalendarVo;
import com.server.edu.dictionary.service.DictionaryService;
import com.server.edu.dictionary.utils.ClassroomCacheUtil;
import com.server.edu.dictionary.utils.TeacherCacheUtil;
import com.server.edu.election.dao.ElcCourseTakeDao;
import com.server.edu.election.dao.TeachingClassTeacherDao;
import com.server.edu.election.rpc.BaseresServiceInvoker;
import com.server.edu.election.rpc.CultureSerivceInvoker;
import com.server.edu.election.service.TeacherLessonTableService;
import com.server.edu.election.util.WeekUtil;
import com.server.edu.election.vo.StudentSchoolTimetabVo;
import com.server.edu.election.vo.TimeTable;
import com.server.edu.session.util.SessionUtils;
import com.server.edu.util.CalUtil;
import com.server.edu.util.CollectionUtil;
import com.server.edu.util.FileUtil;
import com.server.edu.util.excel.GeneralExcelDesigner;
import com.server.edu.util.excel.export.ExcelExecuter;
import com.server.edu.util.excel.export.ExcelResult;
import com.server.edu.util.excel.export.ExportExcelUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@Service
public class TeacherLessonTableServiceServiceImpl
    implements TeacherLessonTableService
{
    private static Logger LOG =
        LoggerFactory.getLogger(TeacherLessonTableServiceServiceImpl.class);
    
    @Autowired
    private ElcCourseTakeDao courseTakeDao;
    
    @Autowired
    private TeachingClassTeacherDao teachingClassTeacherDao;
    
    @Value("${task.cache.directory}")
    private String cacheDirectory;
    
    @Autowired
    private DictionaryService dictionaryService;
    
    // 导出学生课表
    private static final String[] setSchoolTimeTitle =
        {"节次/周次", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六", "星期日"};
    
    private static final String[] setSchoolTimeCol =
        {"第一节课", "第二节课", "第三节课", "第四节课", "第五节课", "第六节课", "第七节课", "第八节课", "第九节课",
            "第十节课", "第十一节课", "第十二节课"};
    
    private static final String[] setTimeListTeacherTitle = {"序号", "课程序号",
        "课程名称", "课程性质", "周学时", "学分", "授课语言", "教学安排", "上课人数", "备注"};

    private static final String[] setTimeListTitle =
            {"序号", "课程序号", "课程名称", "课程类别", "周课时",  "学分", "授课语言", "上课时间", "上课地点","上课人数","备注"};
    
    /**
     *@Description: 查询所有教师课表
     *@Param:
     *@return:
     *@Author: bear
     *@date: 2019/4/30 17:39
     */
    @Override
    public PageResult<ClassCodeToTeacher> findAllTeacherTimeTable(
        PageCondition<ClassCodeToTeacher> condition)
    {
        PageHelper.startPage(condition.getPageNum_(), condition.getPageSize_());
        Page<ClassCodeToTeacher> teacherTimeTable =
            courseTakeDao.findAllTeacherTimeTable(condition.getCondition());
        if (teacherTimeTable != null)
        {
            List<ClassCodeToTeacher> result = teacherTimeTable.getResult();
            if (CollectionUtil.isNotEmpty(result))
            {
                SchoolCalendarVo schoolCalendar =
                    BaseresServiceInvoker.getSchoolCalendarById(
                        condition.getCondition().getCalendarId());
                for (ClassCodeToTeacher toTeacher : result)
                {
                    toTeacher.setCalendarName(schoolCalendar.getFullName());
                }
            }
        }
        return new PageResult<>(teacherTimeTable);
    }
    
    /**
     * 研究生查询所有教师课表
     * @param condition
     * @return
     */
    @Override
    public PageResult<ClassCodeToTeacher> findTeacherTimeTableByRole(PageCondition<ClassCodeToTeacher> condition) {
        ClassCodeToTeacher classCodeToTeacher = condition.getCondition();
        PageHelper.startPage(condition.getPageNum_(), condition.getPageSize_());
        Page<ClassCodeToTeacher> teacherTimeTable = courseTakeDao.findTeacherTimeTableByRole(classCodeToTeacher);
        if (CollectionUtil.isEmpty(teacherTimeTable)) {
            return new PageResult<ClassCodeToTeacher>(teacherTimeTable);
        }
        Set<String> set = teacherTimeTable.stream().map(ClassCodeToTeacher::getTeacherCode).collect(Collectors.toSet());
        List<TeachingClassTeacherVo> teachers = teachingClassTeacherDao.findTeachers(set);
        Map<String, TeachingClassTeacherVo> map = teachers.stream().collect(Collectors.toMap(TeachingClassTeacherVo::getTeacherCode, s -> s));
        for (ClassCodeToTeacher codeToTeacher : teacherTimeTable) {
            TeachingClassTeacherVo teachingClassTeacherVo = map.get(codeToTeacher.getTeacherCode());
            codeToTeacher.setSex(teachingClassTeacherVo.getSex());
//            codeToTeacher.setFaculty(teachingClassTeacherVo.getFaculty());
        }
        return new PageResult<ClassCodeToTeacher>(teacherTimeTable);
    }

    /**
     *@Description: 研究生查询教师课表
     *@Param:
     *@return:
     *@Author: bear
     *@date: 2019/2/18 10:56
     */
    @Override
    public StudentSchoolTimetabVo findTeacherTimetable2(Long calendarId, String teacherCode, String projectId) {
        //查询所有教学班
        StudentSchoolTimetabVo vo = new StudentSchoolTimetabVo();
        TeachingClassTeacherVo teacher = teachingClassTeacherDao.findTeacher(teacherCode);
        String teacherName = teacher.getTeacherName();
        vo.setTeacherName(teacherName);
        vo.setFaculty(teacher.getFaculty());
        List<ClassTeacherDto> classTeachers = courseTakeDao.findTeachingClassIds(calendarId, teacherCode, projectId);
        if (CollectionUtil.isEmpty(classTeachers)) {
            return vo;
        }
        Map<Long, ClassTeacherDto> map = classTeachers.stream()
                .collect(Collectors.toMap(ClassTeacherDto::getTeachingClassId,s->s));
        Set<Long> ids = map.keySet();
        List<TimeTableMessage> courseArrange = courseTakeDao.findTeachingArrange(ids, teacherCode);
        String lang = SessionUtils.getLang();
        List<TimeTable> timeTables=new ArrayList<>(courseArrange.size());
        if(CollectionUtil.isNotEmpty(courseArrange)){
            for (TimeTableMessage timeTableMessage : courseArrange) {
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
                Long teachingClassId = timeTableMessage.getTeachingClassId();
                ClassTeacherDto classTeacherDto = map.get(teachingClassId);
                String value=teacherName+" "+ classTeacherDto.getCourseName()
                        +"("+ weekNumStr + ClassroomCacheUtil.getRoomName(roomID) + ")"
                        + " " + dictionaryService.query("X_XQ",classTeacherDto.getCampus(), lang);
                timeTableMessage.setTimeAndRoom(timeStr);
                TimeTable timeTable = new TimeTable();
                timeTable.setId(teachingClassId + "");
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
     *@Description: 查询教师课表
     *@Param:
     *@return:
     *@Author: bear
     *@date: 2019/5/5 14:13
     */
    @Override
    public List<TeacherTimeTable> findTeacherTimetable(Long calendarId,
        String teacherCode)
    {
        List<TeacherTimeTable > classTimeAndRoom=courseTakeDao.findTeacherTimetable(calendarId,teacherCode);
        if(CollectionUtil.isNotEmpty(classTimeAndRoom)){
            List<Long> ids = classTimeAndRoom.stream().map(TeacherTimeTable::getTeachingClassId).collect(Collectors.toList());
            List<TimeTableMessage> tableMessages = getTimeById(ids);
            Map<Long, List<TimeTableMessage>> listMap=new HashMap<>();
            if(CollectionUtil.isNotEmpty(tableMessages)){
                List<TimeTableMessage> timeTableMessages = tableMessages.stream()
                    .filter(vo -> Arrays.asList(vo.getTeacherCode().split(","))
                    .contains(teacherCode))
                    .collect(Collectors.toList());
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
    public List<TimeTable> getTeacherTimetable(Long calendarId,
        String teacherCode, Integer week)
    {
        List<TimeTable> list = new ArrayList<>();
        List<TeacherTimeTable> classList =
            courseTakeDao.findTeacherTimetable(calendarId, teacherCode);
        if (CollectionUtil.isNotEmpty(classList))
        {
            List<Long> ids = classList.stream()
                .map(TeacherTimeTable::getTeachingClassId)
                .collect(Collectors.toList());
            List<TimeTableMessage> tableMessages = getTimeById(ids);
            List<TimeTableMessage> teacherList = new ArrayList<>();
            if (CollectionUtil.isNotEmpty(tableMessages))
            {
                teacherList = tableMessages.stream().filter(vo -> {
                    return Arrays.asList(vo.getTeacherCode().split(","))
                        .contains(teacherCode) && vo.getWeeks().contains(week);
                }).collect(Collectors.toList());
                
                if (CollectionUtil.isNotEmpty(teacherList))
                {
                    for (TimeTableMessage tm : teacherList)
                    {
                        TimeTable tt = new TimeTable();
                        String name = "";
                        if (StringUtils.isNotBlank(tm.getRoomId()))
                        {
                            name =
                                ClassroomCacheUtil.getRoomName(tm.getRoomId());
                        }
                        String value = String.format("%s %s (%s,%s)",
                            tm.getClassCode(),
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
            }
            
        }
        return list;
    }
    
    /**
     *@Description: 教师上课时间地点详情
     *@Param:
     *@return:
     *@Author: bear
     *@date: 2019/2/16 10:18
     */
     @Override
     public List<ClassTeacherDto> findStudentAndTeacherTime(Long teachingClassId)
     {
         List<ClassTeacherDto> list = new ArrayList<>();
         if (teachingClassId == null)
         {
             return list;
         }
         List<ClassTeacherDto> classTeacherDtos =
             courseTakeDao.findStudentAndTeacherTime(teachingClassId);
         if (CollectionUtil.isNotEmpty(classTeacherDtos))
         {
             Map<String, List<ClassTeacherDto>> listMap =
                 classTeacherDtos.stream()
                     .filter(
                         (ClassTeacherDto dto) -> dto.getTeacherCode() != null)
                     .collect(
                         Collectors.groupingBy(ClassTeacherDto::getTeacherCode));
             for (List<ClassTeacherDto> teacherDtoList : listMap.values())
             {
                 Map<Long, List<ClassTeacherDto>> roomList = teacherDtoList
                     .stream()
                     .collect(Collectors.groupingBy(ClassTeacherDto::getTimeId));
                 for (List<ClassTeacherDto> teacherDtos : roomList.values())
                 {
                     Map<Integer, List<ClassTeacherDto>> collect =
                         teacherDtos.stream()
                             .collect(Collectors
                                 .groupingBy(ClassTeacherDto::getTimeStart));
                     for (List<ClassTeacherDto> dtoList : collect.values())
                     {
                         Map<String, List<ClassTeacherDto>> byRoomList =
                             dtoList.stream()
                                 .collect(Collectors
                                     .groupingBy(ClassTeacherDto::getRoomID));
                         for (List<ClassTeacherDto> dtos : byRoomList.values())
                         {
                             ClassTeacherDto timetab = new ClassTeacherDto();
                             ClassTeacherDto classTeacherDto = dtos.get(0);
                             String teacherCode =
                                 classTeacherDto.getTeacherCode();
                             String[] teacherCodes = teacherCode.split(",");
                             
                             Integer dayOfWeek = classTeacherDto.getDayOfWeek();
                             String week = WeekUtil.findWeek(dayOfWeek);
                             Integer timeStart = classTeacherDto.getTimeStart();
                             Integer timeEnd = classTeacherDto.getTimeEnd();
                             String roomID = classTeacherDto.getRoomID();
                             List<Integer> integerList = dtos.stream()
                                 .map(ClassTeacherDto::getWeekNumber)
                                 .collect(Collectors.toList());
                             //Integer weekNumber1 = dtos.stream().max(Comparator.comparingInt(ClassTeacherDto::getWeekNumber)).get().getWeekNumber();
                             //Integer weekNumber2 = dtos.stream().min(Comparator.comparingInt(ClassTeacherDto::getWeekNumber)).get().getWeekNumber();
                             Integer maxWeek = Collections.max(integerList);
                             Integer minWeek = Collections.min(integerList);
                             String strWeek = "[";
                             String strTime = timeStart + "-" + timeEnd;
                             int size = dtos.size();//判断是否连续
                             if (minWeek + size - 1 == maxWeek)
                             {//连续拼接周次
                                 strWeek += minWeek + "-" + maxWeek + "]";
                             }
                             else
                             {
                                 for (int i = 0; i < dtos.size(); i++)
                                 {
                                     Integer weekNumber =
                                         dtos.get(i).getWeekNumber();
                                     if (i != dtos.size() - 1)
                                     {
                                         strWeek += weekNumber + ",";
                                     }
                                     else
                                     {
                                         strWeek += weekNumber + "]";
                                     }
                                 }
                             }
                             String time = week + " " + strTime + " " + strWeek;
                             List<String> names = courseTakeDao
                                 .findTeacherNameByTeacherCode(teacherCodes);
                             timetab.setCampus(classTeacherDto.getCampus());
                             timetab.setTeacherName(String.join(",", names));
                             timetab.setTime(time);
                             timetab.setWeekNumberStr(strWeek);
                             timetab.setDayOfWeek(dayOfWeek);
                             timetab.setTimeStart(timeStart);
                             timetab.setTimeEnd(timeEnd);
                             timetab.setRoomID(roomID);
                             timetab.setRoom(roomID);
                             timetab.setTeacherCode(teacherCode);
                             timetab.setRemark(classTeacherDto.getRemark());
                             timetab.setTeachingLanguage(
                                 classTeacherDto.getTeachingLanguage());
                             timetab
                                 .setClassCode(classTeacherDto.getClassCode());
                             timetab.setTeachingClassId(
                                 classTeacherDto.getTeachingClassId());
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
     public PageResult<ClassCodeToTeacher> findAllClassTeacher(
         PageCondition<ClassCodeToTeacher> condition)
     {
         PageHelper.startPage(condition.getPageNum_(), condition.getPageSize_());
         Page<ClassCodeToTeacher> allClassTeacher =
             courseTakeDao.findAllClassTeacher(condition.getCondition());
         if (allClassTeacher != null)
         {
             List<ClassCodeToTeacher> result = allClassTeacher.getResult();
             List<SchoolCalendarVo> schoolCalendarList =
                 BaseresServiceInvoker.getSchoolCalendarList();
             Map<Long, String> schoolCalendarMap = new HashMap<>();
             for (SchoolCalendarVo schoolCalendarVo : schoolCalendarList)
             {
                 schoolCalendarMap.put(schoolCalendarVo.getId(),
                     schoolCalendarVo.getFullName());
             }
             if (schoolCalendarMap.size() != 0)
             {
                 for (ClassCodeToTeacher toTeacher : result)
                 {
                     String s = schoolCalendarMap.get(toTeacher.getCalendarId());
                     if (StringUtils.isNotEmpty(s))
                     {
                         toTeacher.setCalendarName(s);
                     }
                 }
             }
         }
         return new PageResult<>(allClassTeacher);
     }
    
    /**
     *@Description: 导出所有教师课表
     *@Param:
     *@return:
     *@Author: bear
     *@date: 2019/5/8 16:13
     */
    @Override
    public ExcelResult exportTeacher(ClassCodeToTeacher condition)
    {
        ExcelResult excelResult =
            ExportExcelUtils.submitTask("allTeacherList", new ExcelExecuter()
            {
                @Override
                public GeneralExcelDesigner getExcelDesigner()
                {
                    ExcelResult result = this.getResult();
                    PageCondition<ClassCodeToTeacher> pageCondition =
                        new PageCondition<ClassCodeToTeacher>();
                    pageCondition.setCondition(condition);
                    pageCondition.setPageSize_(100);
                    int pageNum = 0;
                    pageCondition.setPageNum_(pageNum);
                    List<ClassCodeToTeacher> list = new ArrayList<>();
                    while (true)
                    {
                        pageNum++;
                        pageCondition.setPageNum_(pageNum);
                        PageResult<ClassCodeToTeacher> allTeacherTimeTable =
                            findAllTeacherTimeTable(pageCondition);
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
    
    private GeneralExcelDesigner getDesignTeacher()
    {
        GeneralExcelDesigner design = new GeneralExcelDesigner();
        design.setNullCellValue("");
        design.addCell(I18nUtil.getMsg("exemptionApply.calendarName"),
            "calendarName");
        design.addCell(I18nUtil.getMsg("rollBookManage.teacherCode"),
            "teacherCode");
        design.addCell(I18nUtil.getMsg("exemptionApply.studentName"),
            "teacherName");
        design.addCell(I18nUtil.getMsg("rollBookManage.teachingClassName"),
            "classCode");
        design.addCell(I18nUtil.getMsg("exemptionApply.courseCode"),
            "courseCode");
        design.addCell(I18nUtil.getMsg("exemptionApply.courseName"),
            "courseName");
        design.addCell(I18nUtil.getMsg("exemptionApply.faculty"), "faculty")
            .setValueHandler((value, rawData, cell) -> {
                return dictionaryService
                    .query("X_YX", value, SessionUtils.getLang());
            });
        
        design.addCell(I18nUtil.getMsg("rebuildCourse.sex"), "sex")
            .setValueHandler((value, rawData, cell) -> {
                return dictionaryService
                    .query("G_XBIE", value, SessionUtils.getLang());
            });
        return design;
    }
    
    @Override
    public RestResult<String> exportTeacherTimetabPdf(Long calendarId, String teacherCode, String projectId)
        throws DocumentException, IOException
    {
        //检查目录是否存在
        LOG.info("缓存目录：" + cacheDirectory);
        FileUtil.mkdirs(cacheDirectory);
        //删除超过30天的文件
        FileUtil.deleteFile(cacheDirectory, 30);

        //----3 教师基本信息----
        StudentSchoolTimetabVo teacherTimetab =
                findTeacherTimetable2(calendarId, teacherCode,projectId);
        
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

        String teacherName = teacherTimetab.getTeacherName();
        String fileName = new StringBuffer("").append(cacheDirectory)
            .append("//")
            .append(System.currentTimeMillis())
            .append("_")
            .append(teacherName)
            .append(".pdf")
            .toString();
        Document document = new Document(PageSize.A4, 24, 24, 16, 16);
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
        SchoolCalendarVo schoolCalendarVo = BaseresServiceInvoker.getSchoolCalendarById(calendarId);
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
        
        //---3 教师基本信息---
        PdfPTable table1 = new PdfPTable(4);
        //前间距
        table1.setSpacingBefore(5);
        
        PdfPCell cell1 = createNoBorderCell("工号：" + teacherCode, name2, 20f);
        table1.addCell(cell1);
        
        PdfPCell cell2 = createNoBorderCell("姓名：" + teacherName, name2, 20f);
        table1.addCell(cell2);
        String facultyStr = dictionaryService.query(DictTypeEnum.X_YX.getType(), teacherTimetab.getFaculty());
        
        PdfPCell cell3 = new PdfPCell(new Paragraph("学院：" + facultyStr, name2));
        //无边框
        cell3.setBorder(Rectangle.NO_BORDER);
        // 设置内容水平居左显示
        cell3.setHorizontalAlignment(Element.ALIGN_LEFT);
        // 设置内容垂直居左显示
        cell3.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell3.setColspan(2);
        table1.addCell(cell3);
        
        PdfPCell cell4 = createNoBorderCell("", name2, 0f);
        table1.addCell(cell4);
        document.add(table1);
        
        // ----4 教师选课课表展示----
        List<TimeTable> timeTables = teacherTimetab.getTimeTables();
        // 教师课表上课时间冲突合并
        List<TimeTable> list = getTimtable(timeTables);
        PdfPTable table2 = createStudentTable(list,
            subtitleChinese,
            name2);
        document.add(table2);
        
        // ----5 教师课程安排列表 -------
        PdfPTable table3 =
            createTeacherTimeList(teacherTimetab.getTeacherDtos(),
                subtitleChinese,
                name2);
        document.add(table3);
        
        document.close();
        return RestResult.successData("导出成功。", fileName);
    }

    @Override
    public RestResult<String> exportTeacherTimetabPdfBk(Long calendarId, String teacherCode)
            throws DocumentException, IOException
    {
        //检查目录是否存在
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
        TeachingClassTeacherVo teacher = teachingClassTeacherDao.findTeacher(teacherCode);
        if (teacher == null) {
            throw new ParameterValidateException("找不到与" + teacherCode + "对应的教师");
        }
        String teacherName = teacher.getTeacherName();
        String fileName = new StringBuffer("").append(cacheDirectory)
                .append("//")
                .append(System.currentTimeMillis())
                .append("_")
                .append(teacherName)
                .append(".pdf")
                .toString();
        Document document = new Document(PageSize.A4, 24, 24, 16, 16);
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
        SchoolCalendarVo schoolCalendarVo = BaseresServiceInvoker.getSchoolCalendarById(calendarId);
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

        //---3 教师基本信息---
        PdfPTable table1 = new PdfPTable(4);
        //前间距
        table1.setSpacingBefore(5);

        PdfPCell cell1 = createNoBorderCell("工号：" + teacherCode, name2, 20f);
        table1.addCell(cell1);

        PdfPCell cell2 = createNoBorderCell("姓名：" + teacherName, name2, 20f);
        table1.addCell(cell2);
        String facultyStr = dictionaryService.query(DictTypeEnum.X_YX.getType(), teacher.getFaculty());

        PdfPCell cell3 = new PdfPCell(new Paragraph("学院：" + facultyStr, name2));
        //无边框
        cell3.setBorder(Rectangle.NO_BORDER);
        // 设置内容水平居左显示
        cell3.setHorizontalAlignment(Element.ALIGN_LEFT);
        // 设置内容垂直居左显示
        cell3.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell3.setColspan(2);
        table1.addCell(cell3);

        PdfPCell cell4 = createNoBorderCell("", name2, 0f);
        table1.addCell(cell4);
        document.add(table1);


        List<TeacherTimeTable> teacherTimetable = findTeacherTimetable(calendarId, teacherCode);
        List<TimeTable> tables = new ArrayList<>(20);
        // ----4 教师选课课表展示----
        for (TeacherTimeTable teacherTimeTable : teacherTimetable) {
            List<TimeTableMessage> timeTableList = teacherTimeTable.getTimeTableList();
            if (timeTableList != null) {
                for (TimeTableMessage timeTableMessage : timeTableList) {
                    TimeTable timeTable = new TimeTable();
                    timeTable.setDayOfWeek(timeTableMessage.getDayOfWeek());
                    timeTable.setTimeStart(timeTableMessage.getTimeStart());
                    timeTable.setTimeEnd(timeTableMessage.getTimeEnd());
                    StringBuffer sb = new StringBuffer();
                    sb.append(teacherName).
                            append(timeTableMessage.getCourseName()).
                            append(timeTableMessage.getWeekNum()).
                            append(ClassroomCacheUtil.getRoomName(timeTableMessage.getRoomId())).
                            append(dictionaryService.query("X_XQ", timeTableMessage.getCampus()));
                    timeTable.setValue(sb.toString());
                    tables.add(timeTable);
                }
            }
        }
        // 教师课表上课时间冲突合并
        List<TimeTable> list = getTimtable(tables);
        PdfPTable table2 = createStudentTable(list,
                subtitleChinese,
                name2);
        document.add(table2);

        // ----5 教师课程安排列表 -------
        PdfPTable table3 =
                createTeacherTimeListBk(teacherTimetable,
                        subtitleChinese,
                        name2);
        document.add(table3);

        document.close();
        return RestResult.successData("导出成功。", fileName);
    }

    /**
     * 创建教师选课列表table
     * @throws IOException
     */
    private PdfPTable createTeacherTimeListBk(List<TeacherTimeTable> list,
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
                List<String> timeTableList = getTimeTableTeacherListBk(list.get(j));
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


    private List<String> getTimeTableTeacherListBk(
            TeacherTimeTable teacherTimeTable)
    {
        List<String> list = new ArrayList<String>();
        list.add(teacherTimeTable.getClassCode());
        list.add(teacherTimeTable.getCourseName());
        list.add(teacherTimeTable.getCourseLabelName());
        list.add(String.valueOf(teacherTimeTable.getWeekHour()));
        list.add(String.valueOf(teacherTimeTable.getCredits()));
        String teachingLanguage = teacherTimeTable.getTeachingLanguage();
        if (teachingLanguage != null) {
            teachingLanguage = dictionaryService.query("X_SKYY", teachingLanguage);
        } else {
            teachingLanguage = "";
        }
        list.add(teachingLanguage);
        list.add(teacherTimeTable.getClassTime());
        String room = teacherTimeTable.getClassRoom();
        if (room != null) {
            room = ClassroomCacheUtil.getRoomName(room);
        } else {
            room = "";
        }
        list.add(room);
        Integer elcNumber = teacherTimeTable.getElcNumber();
        if (elcNumber == null) {
            list.add("");
        } else {
            list.add(elcNumber + "");
        }
        list.add(teacherTimeTable.getRemark());
        return list;
    }

    /**
     *上课时间冲突合并
     * @param list
     * @return
     */
    private List<TimeTable> getTimtable(List<TimeTable> list) {
        if (CollectionUtil.isEmpty(list)) {
            return new ArrayList<>();
        }
        Map<Integer, List<TimeTable>> map = list.stream().collect(Collectors.groupingBy(TimeTable::getDayOfWeek));
        List<TimeTable> tableList = new ArrayList<>(list.size() * 2);
        Set<Integer> days = map.keySet();
        for (Integer day : days) {
            List<TimeTable> tables = map.get(day);
            // 上课节次集合
            MultiValueMap<Integer, TimeTable> timeMap = new LinkedMultiValueMap<>();
            Set<Integer> set = new LinkedHashSet<>(12);
            if (tables.size() > 1) {
                // 将上课节次以一节为单位拆分
                for (TimeTable table : tables) {
                    Integer timeStart = table.getTimeStart();
                    Integer timeEnd = table.getTimeEnd();
                    for (int i = timeStart; i <= timeEnd; i++) {
                        timeMap.add(i, table);
                        set.add(i);
                    }
                }
                List<TimeTable> times = new ArrayList<>(12);
                for (Integer i : set) {
                    List<TimeTable> timeTables = timeMap.get(i);
                    List<String> value = timeTables.stream().map(TimeTable::getValue).collect(Collectors.toList());
                    String values = String.join(",  ", value);
                    List<String> ids = timeTables.stream().map(TimeTable::getId).collect(Collectors.toList());
                    String id = String.join(",  ", ids);
                    TimeTable tab = new TimeTable();
                    tab.setTimeStart(i);
                    tab.setValue(values);
                    tab.setTimeEnd(i);
                    tab.setId(id);
                    times.add(tab);
                }
                int size = times.size();
                loop:for (int i = 0; i < size;i++) {
                    TimeTable timeTable = times.get(i);
                    String value = timeTable.getValue();
                    String id = timeTable.getId();
                    timeTable.setDayOfWeek(day);
                    for (int j = i + 1; j < size; j++) {
                        TimeTable table = times.get(j);
                        Integer timeEnd = table.getTimeEnd();
                        if (id.equals(table.getId()) && value.equals(table.getValue())) {
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
     *   生成一个无边框无跨行的单元格
     * @param content  内容
     * @param font     字体 传null视为默认黑色字体
     * @param width    单元格宽度
     * @return
     */
    public static PdfPCell createNoBorderCell(String content, Font font, Float width)
    {
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
    
    /**
     * @param 创建选课课表table
     */
    public PdfPTable createStudentTable(List<TimeTable> tables,
        Font subtitleChinese, Font name2)
        throws DocumentException, IOException
    {
        PdfPTable table = new PdfPTable(setSchoolTimeTitle.length);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10);
        
        // 添加课表表头
        for (int i = 0; i < setSchoolTimeTitle.length; i++)
        {
            PdfPCell cell =
                createCell(setSchoolTimeTitle[i], 1, 1, subtitleChinese, 30f);
            table.addCell(cell);
        }
        
        // 添加课表内容
        //setTimeListCol
        for (int i = 0; i < setSchoolTimeCol.length; i++)
        { // 12行  节次
            for (int j = 0; j < setSchoolTimeTitle.length; j++)
            { // 8列    星期
                PdfPCell cell = new PdfPCell(new Paragraph("", name2));
                if (j == 0)
                {
                    cell = createCell(setSchoolTimeCol[i], 1, 1, name2, null);
                    table.addCell(cell);
                }
                else
                {
                    TimeTable time = hasCourseArrangment(j, i + 1, tables);
                    if (time != null)
                    {
                        if (time.getTimeStart() == i + 1)
                        {
                            cell = createCell(time.getValue(),
                                1,
                                time.getTimeEnd() - i,
                                name2,
                                null);
                            table.addCell(cell);
                        }
                    }
                    else
                    {
                        cell = createCell("", 1, 1, name2, 30f);
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
    public TimeTable hasCourseArrangment(int dayOfWeek, int timeNo,
        List<TimeTable> tables)
    {
        TimeTable time = null;
        if (CollectionUtil.isNotEmpty(tables)) {
            for (TimeTable timeTable : tables)
            {
                if (timeTable.getDayOfWeek() == dayOfWeek
                        && timeTable.getTimeStart() <= timeNo
                        && timeNo <= timeTable.getTimeEnd())
                {
                    time = timeTable;
                    break;
                }
            }
        }
        return time;
    }
    
    public PdfPTable createTeacherTimeList(List<ClassTeacherDto> list,
        Font subtitleChinese, Font name2)
        throws IOException, DocumentException
    {
        PdfPTable table = new PdfPTable(setTimeListTeacherTitle.length);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10);
        
        // 添加表头
        for (int i = 0; i < setTimeListTeacherTitle.length; i++)
        {
            PdfPCell cell = createCell(setTimeListTeacherTitle[i],
                1,
                1,
                subtitleChinese,
                null);
            table.addCell(cell);
        }
        if (CollectionUtil.isNotEmpty(list)) {
            // 添加表内容
            for (int j = 0; j < list.size(); j++)
            {
                List<String> timeTableList = getTimeTableTeacherList(list.get(j));
                for (int i = 0; i < setTimeListTeacherTitle.length; i++)
                {
                    PdfPCell cell = new PdfPCell();
                    if (i == 0)
                    {
                        cell = createCell(String.valueOf(j + 1), 1, 1, name2, null);
                    }
                    else
                    {
                        cell =
                                createCell(timeTableList.get(i - 1), 1, 1, name2, null);
                    }
                    table.addCell(cell);
                }
            }
        }
        return table;
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
    public static PdfPCell createCell(String content, int rowSpan, int colSpan,
        Font font, Float height)
        throws IOException, DocumentException
    {
        if (font == null)
        {
            BaseFont bfChinese = BaseFont.createFont("STSongStd-Light",
                "UniGB-UCS2-H",
                BaseFont.NOT_EMBEDDED);
            font = new Font(bfChinese, 8, Font.NORMAL);
        }
        // 构建每个单元格
        PdfPCell cell = new PdfPCell(new Paragraph(content, font));
        // 设置内容水平居中显示
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        // 设置垂直居中
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        // 设置边框的颜色
        cell.setBorderColor(new BaseColor(15, 110, 176));
        // 设置单元格的行高
        if (height != null)
        {
            cell.setFixedHeight(height);
        }
        cell.setColspan(rowSpan);
        cell.setRowspan(colSpan);
        return cell;
    }
    
    private List<String> getTimeTableTeacherList(
        ClassTeacherDto classTeacherDto)
    {
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
    
    public List<TimeTableMessage> getTimeById(List<Long> teachingClassId)
    {
        List<TimeTableMessage> list=new ArrayList<>();
        List<ClassTeacherDto> classTimeAndRoom = courseTakeDao.findClassTimeAndRoom(teachingClassId);
        if(CollectionUtil.isNotEmpty(classTimeAndRoom)){
            for (ClassTeacherDto classTeacherDto : classTimeAndRoom) {
                TimeTableMessage time=new TimeTableMessage();
                Integer dayOfWeek = classTeacherDto.getDayOfWeek();
                Integer timeStart = classTeacherDto.getTimeStart();
                Integer timeEnd = classTeacherDto.getTimeEnd();
                String roomID = classTeacherDto.getRoomID();
                String teacherCode = classTeacherDto.getTeacherCode();
                String weekNumber = classTeacherDto.getWeekNumberStr();
                String[] str = weekNumber.split(",");
                
                List<Integer> weeks = Arrays.asList(str).stream().map(Integer::parseInt).collect(Collectors.toList());
                List<String> weekNums = CalUtil.getWeekNums(weeks.toArray(new Integer[] {}));
                String weekNumStr = weekNums.toString();//周次
                String weekstr = findWeek(dayOfWeek);//星期
                
                String[] tcodes = teacherCode.split(",");
                List<Teacher> teachers = TeacherCacheUtil.getTeachers(tcodes);
                String teacherName="";
                if(teachers != null) {
                    teacherName = teachers.stream().map(Teacher::getName).collect(Collectors.joining(","));
                }
                
                String timeStr=weekstr+" "+timeStart+"-"+timeEnd+"节"+weekNumStr+ClassroomCacheUtil.getRoomName(roomID);
                String roomStr=weekstr+" "+timeStart+"-"+timeEnd+" "+weekNumStr;
                time.setDayOfWeek(dayOfWeek);
                time.setTimeStart(timeStart);
                time.setTimeEnd(timeEnd);
                time.setRoomId(roomID);
                time.setTeacherCode(teacherCode);
                time.setTeacherName(teacherName);
                time.setWeekNum(weekNumStr);
                time.setWeekstr(weekstr);
                time.setWeeks(weeks);
                time.setTimeAndRoom(timeStr);
                time.setTimeTab(roomStr);
                time.setCampus(classTeacherDto.getCampus());
                time.setClassCode(classTeacherDto.getClassCode());
                time.setCourseCode(classTeacherDto.getCourseCode());
                time.setCourseName(classTeacherDto.getCourseName());
                time.setClassName(classTeacherDto.getClassName());
                time.setTeachingClassId(classTeacherDto.getTeachingClassId());
                list.add(time);
            }
        }
        return list;
    }
    
    public String findWeek(Integer number){
        String week="";
        switch(number){
            case 1:
                week="星期一";
                break;
            case 2:
                week="星期二";
                break;
            case 3:
                week="星期三";
                break;
            case 4:
                week="星期四";
                break;
            case 5:
                week="星期五";
                break;
            case 6:
                week="星期六";
                break;
            case 7:
                week="星期日";
                break;
        }
        return week;
    }
}
