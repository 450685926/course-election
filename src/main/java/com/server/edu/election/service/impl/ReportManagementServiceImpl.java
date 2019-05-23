package com.server.edu.election.service.impl;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.server.edu.common.rest.RestResult;
import com.server.edu.election.dto.*;
import com.server.edu.election.vo.*;
import com.server.edu.session.util.entity.Session;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.server.edu.common.PageCondition;
import com.server.edu.common.entity.Teacher;
import com.server.edu.common.locale.I18nUtil;
import com.server.edu.common.rest.PageResult;
import com.server.edu.common.vo.SchoolCalendarVo;
import com.server.edu.dictionary.service.DictionaryService;
import com.server.edu.dictionary.utils.ClassroomCacheUtil;
import com.server.edu.dictionary.utils.TeacherCacheUtil;
import com.server.edu.election.constants.Constants;
import com.server.edu.election.dao.ElcCourseTakeDao;
import com.server.edu.election.dao.ElcLogDao;
import com.server.edu.election.dao.ElcNoSelectReasonDao;
import com.server.edu.election.dao.ElecRoundsDao;
import com.server.edu.election.dao.StudentDao;
import com.server.edu.election.entity.ElcNoSelectReason;
import com.server.edu.election.entity.Student;
import com.server.edu.election.rpc.BaseresServiceInvoker;
import com.server.edu.election.rpc.CultureSerivceInvoker;
import com.server.edu.election.service.ReportManagementService;
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

import freemarker.template.Template;

/**
 * @description: 报表管理实现类
 * @author: bear
 * @create: 2019-02-14 14:52
 */

@Service
@Primary
public class ReportManagementServiceImpl implements ReportManagementService {

    @Autowired
    private ElcCourseTakeDao courseTakeDao;

    @Autowired
    private StudentDao studentDao;

    @Autowired
    private ElcLogDao elcLogDao;

    @Autowired
    private ElecRoundsDao elecRoundsDao;

    @Autowired
    private ElcNoSelectReasonDao reasonDao;

    @Autowired
    private RedisTemplate redisTemplate;

    @Value("${task.cache.directory}")
    private String cacheDirectory;


    @Autowired
    private DictionaryService dictionaryService;

    @Autowired
    private FreeMarkerConfigurer freeMarkerConfigurer;
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
            List<SchoolCalendarVo> schoolCalendarList = BaseresServiceInvoker.getSchoolCalendarList();
            Map<Long, String> schoolCalendarMap = new HashMap<>();
            for(SchoolCalendarVo schoolCalendarVo : schoolCalendarList) {
                schoolCalendarMap.put(schoolCalendarVo.getId(), schoolCalendarVo.getFullName());
            }
            if(schoolCalendarMap.size()!=0){
                for (StudentVo studentVo : result) {
                    String s = schoolCalendarMap.get(studentVo.getCalendarId());
                    if(StringUtils.isNotEmpty(s)) {
                        studentVo.setCalendarName(s);
                    }
                }
            }
        }
        return new PageResult<>(allSchoolTimetab);
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
        List<ClassTeacherDto> classTeacherDtos = courseTakeDao.findStudentAndTeacherTime(teachingClassId);
        List<ClassTeacherDto> list=new ArrayList<>();
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
                            Integer dayOfWeek = classTeacherDto.getDayOfWeek();
                            String week = findWeek(dayOfWeek);
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
                            String name = courseTakeDao.findClassTeacherByTeacherCode(teacherCode);
                            timetab.setTime(time);
                            timetab.setWeekNumberStr(strWeek);
                            timetab.setDayOfWeek(dayOfWeek);
                            timetab.setTimeStart(timeStart);
                            timetab.setTimeEnd(timeEnd);
                            timetab.setRoom(roomID);
                            timetab.setTeacherCode(teacherCode);
                            timetab.setTeacherName(name);
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
    *@Description: 查询教师课表
    *@Param:
    *@return:
    *@Author: bear
    *@date: 2019/2/18 10:56
    */
    @Override
    public StudentSchoolTimetabVo findTeacherTimetable2(Long calendarId, String teacherCode) {
        //查询所有教学班
        StudentSchoolTimetabVo vo=new StudentSchoolTimetabVo();
        List<ClassTeacherDto> list=new ArrayList<>();
        List<TimeTable> timeTables=new ArrayList<>();
        List<ClassTeacherDto> classId = courseTakeDao.findAllTeachingClassId(calendarId);
        if(CollectionUtil.isNotEmpty(classId)){
            for (ClassTeacherDto classTeacherDto : classId) {
                List<ClassTeacherDto> teacherTime = findStudentAndTeacherTime(classTeacherDto.getTeachingClassId());
                if(CollectionUtil.isNotEmpty(teacherTime)){
                    List<ClassTeacherDto> collect = teacherTime.stream().filter((ClassTeacherDto timrTab) -> timrTab.getTeacherCode().equals(teacherCode)).collect(Collectors.toList());
                    if(CollectionUtil.isNotEmpty(collect)){
                        for (ClassTeacherDto teacherDto : collect) {
                            TimeTable time=new TimeTable();
                            teacherDto.setCourseCode(classTeacherDto.getCourseCode());
                            teacherDto.setCourseName(classTeacherDto.getCourseName());
                            teacherDto.setLabel(classTeacherDto.getLabel());
                            teacherDto.setWeekHour(classTeacherDto.getWeekHour());
                            teacherDto.setCredits(classTeacherDto.getCredits());
                            teacherDto.setSelectCourseNumber(classTeacherDto.getSelectCourseNumber());
                            time.setDayOfWeek(teacherDto.getDayOfWeek());
                            time.setTimeStart(teacherDto.getTimeStart());
                            time.setTimeEnd(teacherDto.getTimeEnd());
                            String value=teacherDto.getClassCode()+classTeacherDto.getCourseName()
                                    +"("+teacherDto.getWeekNumberStr()+teacherDto.getRoom()+")";
                            time.setValue(value);
                            timeTables.add(time);
                        }
                    }
                    list.addAll(collect);
                }
            }
        }
        vo.setTeacherDtos(list);
        vo.setTimeTables(timeTables);
        return vo;
    }

    /**
    *@Description: 选退课日志
    *@Param:
    *@return:
    *@Author: bear
    *@date: 2019/2/18 16:30
    */
    @Override
    public PageResult<ElcLogVo> findCourseLog(PageCondition<ElcLogVo> condition) {
        PageHelper.startPage(condition.getPageNum_(),condition.getPageSize_());
        Page<ElcLogVo> courseLog = elcLogDao.findCourseLog(condition.getCondition());
        if(courseLog !=null){
            List<ElcLogVo> result = courseLog.getResult();
            if(CollectionUtil.isNotEmpty(result)){
                List<SchoolCalendarVo> schoolCalendarList = BaseresServiceInvoker.getSchoolCalendarList();
                Map<Long, String> schoolCalendarMap = new HashMap<>();
                for(SchoolCalendarVo schoolCalendarVo : schoolCalendarList) {
                    schoolCalendarMap.put(schoolCalendarVo.getId(), schoolCalendarVo.getFullName());
                }
                if(schoolCalendarMap.size()!=0){
                    for (ElcLogVo elcLogVo : result) {
                        String s = schoolCalendarMap.get(elcLogVo.getCalendarId());
                        if(StringUtils.isNotEmpty(s)) {
                            elcLogVo.setCalendarName(s);
                        }
                    }
                }
            }

        }
        return new PageResult<>(courseLog);
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
        Page<NoSelectCourseStdsDto> electCourseList = courseTakeDao.findNoSelectCourseStds(condition.getCondition());
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
    *@Description: 导出未选课学生名单
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

    /**
    *@Description: 导出点名册
    *@Param:
    *@return:
    *@Author: bear
    *@date: 2019/2/20 15:48
    */
    @Override
    public String exportRollBookList(ReportManagementCondition condition) throws Exception{
        PageCondition<ReportManagementCondition> pageCondition = new PageCondition<ReportManagementCondition>();
        pageCondition.setCondition(condition);
        pageCondition.setPageSize_(Constants.ZERO);
        pageCondition.setPageNum_(Constants.ZERO);
        PageResult<RollBookList> rollBookList = findRollBookList2(pageCondition);
        if(rollBookList!=null){
            List<RollBookList> list = rollBookList.getList();
            List<SchoolCalendarVo> schoolCalendarList = BaseresServiceInvoker.getSchoolCalendarList();
            Map<Long, String> schoolCalendarMap = new HashMap<>();
            for(SchoolCalendarVo schoolCalendarVo : schoolCalendarList) {
                schoolCalendarMap.put(schoolCalendarVo.getId(), schoolCalendarVo.getFullName());
            }
            for (RollBookList bookList : list) {
                if(0!=schoolCalendarMap.size()){
                    String s = schoolCalendarMap.get(bookList.getCalendarId());
                    if(StringUtils.isNotEmpty(s)){
                        bookList.setCalendarName(s);

                    }
                }
                bookList.setClassCodeAndcourseName(bookList.getCourseName()+bookList.getClassCode());
            }
            if (list == null) {
                list = new ArrayList<>();
            }
            GeneralExcelDesigner design = getDesignTwo();
            design.setDatas(list);
            ExcelWriterUtil generalExcelHandle;
            generalExcelHandle = GeneralExcelUtil.generalExcelHandle(design);
            FileUtil.mkdirs(cacheDirectory);
            String fileName = "rollBookList.xls";
            String path = cacheDirectory + fileName;
            generalExcelHandle.writeExcel(new FileOutputStream(path));
            return fileName;

        }
        return "";
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

            PageHelper.startPage(condition.getPageNum_(), condition.getPageSize_());
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
        pre.setStudentsList(student);
        pre.setSize(student.size());
        SchoolCalendarVo schoolCalendarVo = BaseresServiceInvoker.getSchoolCalendarById(calendarId);
        pre.setCalendarName(schoolCalendarVo.getFullName());
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
    *@Description: 学生课表其他服务调用
    *@Param:
    *@return: 
    *@Author: bear
    *@date: 2019/5/5 9:34
    */
    @Override
    public List<TimeTable> getStudentTimetab(Long calendarId, String studentCode, Integer week) {
        List<StudnetTimeTable> studentTable = courseTakeDao.findStudentTable(calendarId, studentCode);//查询所有教学班
        List<TimeTable> list =new ArrayList<>();
        if(CollectionUtil.isNotEmpty(studentTable)){
            List<Long> ids = studentTable.stream().map(StudnetTimeTable::getTeachingClassId).collect(Collectors.toList());
            List<TimeTableMessage> timeById = getTimeById(ids);
            for (TimeTableMessage tm : timeById) {
                if(!tm.getWeeks().contains(week)) {
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
        PreViewRollDto preViewRollDto = findPreviewRollBookListById(condition.getTeachingClassId(), condition.getCalendarId());
        List<StudentVo> studentsList = preViewRollDto.getStudentsList();
        String calendarName ="同济大学"+ condition.getCalendarName()+"学生点名册";
        Integer lineNumber = preViewRollDto.getLineNumber();
        Integer rowNumber = preViewRollDto.getRowNumber();
        FileUtil.mkdirs(cacheDirectory);
        String fileName = "preRollBookList-" + System.currentTimeMillis() + ".xls";
        String path = cacheDirectory + fileName;
        Map<String, Object> map = new HashMap<>();
        map.put("list", studentsList);
        map.put("calendar",calendarName);
        map.put("lineNumber",lineNumber);
        map.put("rowNumber",rowNumber);
        map.put("item",condition);
        Template tpl = freeMarkerConfigurer.getConfiguration().getTemplate("preRollBookList.ftl");
        // 将模板和数据模型合并生成文件
        Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path), "UTF-8"));
        tpl.process(map, out);
        // 关闭流
        out.flush();
        out.close();
        return path;
    }


    private List<TimeTableMessage>  getTimeById(List<Long> teachingClassId){
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
                
                String timeStr=weekstr+" "+timeStart+"-"+timeEnd+" "+weekNumStr+" "+roomID;
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

    private GeneralExcelDesigner getDesignTwo() {
        GeneralExcelDesigner design = new GeneralExcelDesigner();
        design.setNullCellValue("");
        design.addCell(I18nUtil.getMsg("exemptionApply.calendarName"), "calendarName");
        design.addCell(I18nUtil.getMsg("rollBookManage.teachingClass"), "calssCode");
        design.addCell(I18nUtil.getMsg("exemptionApply.courseCode"), "courseCode");
        design.addCell(I18nUtil.getMsg("exemptionApply.courseName"), "courseName");
        design.addCell(I18nUtil.getMsg("rollBookManage.teachingClassName"), "classCodeAndcourseName");
        design.addCell(I18nUtil.getMsg("rebuildCourse.label"), "label");
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
   *@Description: 星期
   *@Param:
   *@return:
   *@Author: bear
   *@date: 2019/2/15 13:59
   */

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
                       List<Integer> integerList = list.stream().map(ClassTeacherDto::getWeekNumber).collect(Collectors.toList());
                       Integer maxWeek = Collections.max(integerList);
                       Integer minWeek = Collections.min(integerList);
                       Set<String> rooms = list.stream().map(ClassTeacherDto::getRoomID).filter(StringUtils::isNotBlank).collect(Collectors.toSet());
                       String roomId = String.join(",", rooms);
                       String time=findWeek(dayOfWeek)+" "+timeStart+"-"+timeEnd+"["+minWeek+"-"+maxWeek+"] "+roomId;
                       str.append(time);
                       str.append("/");
                   }
               }

           }
       }
       return str.toString();
   }
}
