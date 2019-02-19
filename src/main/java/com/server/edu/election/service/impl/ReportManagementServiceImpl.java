package com.server.edu.election.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.server.edu.common.PageCondition;
import com.server.edu.common.rest.PageResult;
import com.server.edu.common.vo.SchoolCalendarVo;
import com.server.edu.dictionary.utils.SpringUtils;
import com.server.edu.election.dao.*;
import com.server.edu.election.dto.*;
import com.server.edu.election.entity.ElcLog;
import com.server.edu.election.entity.ElcNoSelectReason;
import com.server.edu.election.entity.RollBookList;
import com.server.edu.election.entity.Student;
import com.server.edu.election.rpc.BaseresServiceInvoker;
import com.server.edu.election.service.ReportManagementService;
import com.server.edu.election.vo.ElcLogVo;
import com.server.edu.election.vo.StudentSchoolTimetabVo;
import com.server.edu.election.vo.StudentVo;
import com.server.edu.util.CollectionUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
    /**
    *@Description: 查询点名册
    *@Param:
    *@return:
    *@Author: bear
    *@date: 2019/2/14 15:52
    */
    @Override
    public PageResult<RollBookList> findRollBookList(PageCondition<ReportManagementCondition> condition) {
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
        previewRollBookList.setClassCode(bookList.getCalssCode());
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
                    Map<String, List<ClassTeacherDto>> byRoomList = teacherDtos.stream().collect(Collectors.groupingBy(ClassTeacherDto::getRoomID));
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
    public List<ClassTeacherDto> findTeacherTimetable(Long calendarId, String teacherCode) {
        //查询所有教学班
        List<ClassTeacherDto> list=new ArrayList<>();
        List<ClassTeacherDto> classId = courseTakeDao.findAllTeachingClassId(calendarId);
        if(CollectionUtil.isNotEmpty(classId)){
            for (ClassTeacherDto classTeacherDto : classId) {
                List<ClassTeacherDto> teacherTime = findStudentAndTeacherTime(classTeacherDto.getTeachingClassId());
                if(CollectionUtil.isNotEmpty(teacherTime)){
                    List<ClassTeacherDto> collect = teacherTime.stream().filter((ClassTeacherDto timrTab) -> timrTab.getTeacherCode().equals(teacherCode)).collect(Collectors.toList());
                    if(CollectionUtil.isNotEmpty(collect)){
                        for (ClassTeacherDto teacherDto : collect) {
                            teacherDto.setCourseCode(classTeacherDto.getCourseCode());
                            teacherDto.setCourseName(classTeacherDto.getCourseName());
                            teacherDto.setLabel(classTeacherDto.getLabel());
                            teacherDto.setWeekHour(classTeacherDto.getWeekHour());
                            teacherDto.setCredits(classTeacherDto.getCredits());
                        }
                    }
                    list.addAll(collect);
                }
            }
        }

        return list;
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
    *@Description: 查询学生课表
    *@Param:
    *@return: 
    *@Author: bear
    *@date: 2019/2/19 15:42
    */
    @Override
    public PageResult<StudentSelectCourseList> findElectCourseList(PageCondition<ReportManagementCondition> condition) {
        PageHelper.startPage(condition.getPageNum_(),condition.getPageSize_());
        Page<StudentSelectCourseList> electCourseList = elecRoundsDao.findElectCourseList(condition.getCondition());
        if(electCourseList!=null){
            List<StudentSelectCourseList> result = electCourseList.getResult();
            List<SchoolCalendarVo> schoolCalendarList = BaseresServiceInvoker.getSchoolCalendarList();
            Map<Long, String> schoolCalendarMap = new HashMap<>();
            for(SchoolCalendarVo schoolCalendarVo : schoolCalendarList) {
                schoolCalendarMap.put(schoolCalendarVo.getId(), schoolCalendarVo.getFullName());
            }
            if(schoolCalendarMap.size()!=0){
                for (StudentSelectCourseList managementCondition : result) {
                    String s = schoolCalendarMap.get(managementCondition.getCalendarId());
                    if(StringUtils.isNotEmpty(s)){
                        managementCondition.setCalendarName(s);
                    }
                }

            }
        }
        return new PageResult<>(electCourseList);
    }

    /**
     * 增加未选课原因
     * */
    @Override
    public String addNoSelectReason(ElcNoSelectReason noSelectReason) {
        reasonDao.deleteNoSelectReason(noSelectReason.getCalendarId(),noSelectReason.getStudentId());
        reasonDao.insertSelective(noSelectReason);
        return "common.editSuccess";
    }

    /**查找未选课原因*/
    @Override
    public ElcNoSelectReason findNoSelectReason(Long calendarId, String studentCode) {
        ElcNoSelectReason noSelectReason = reasonDao.findNoSelectReason(calendarId, studentCode);
        return noSelectReason;
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
       List<ClassTeacherDto> classTimeAndRoom = courseTakeDao.findClassTimeAndRoom(id);

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
