package com.server.edu.election.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.server.edu.common.PageCondition;
import com.server.edu.common.ServicePathEnum;
import com.server.edu.common.rest.PageResult;
import com.server.edu.common.rest.RestResult;
import com.server.edu.election.dao.ElcCourseTakeDao;
import com.server.edu.election.dao.ElcLogDao;
import com.server.edu.election.dto.AddAndRemoveCourseDto;
import com.server.edu.election.dto.ClassTeacherDto;
import com.server.edu.election.dto.ElcStudentDto;
import com.server.edu.election.dto.TimeTableMessage;
import com.server.edu.election.entity.ElcCourseTake;
import com.server.edu.election.entity.ElcLog;
import com.server.edu.election.service.ElcCourseUpholdService;
import com.server.edu.election.vo.ElcStudentVo;
import com.server.edu.util.CalUtil;
import com.server.edu.util.CollectionUtil;
import org.apache.servicecomb.provider.springmvc.reference.RestTemplateBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ElcCourseUpholdServiceImpl implements ElcCourseUpholdService {
    private RestTemplate restTemplate = RestTemplateBuilder.create();
    @Autowired
    private ElcCourseTakeDao courseTakeDao;

    @Autowired
    private ElcLogDao elcLogDao;

    @Override
    public PageResult<ElcStudentVo> elcStudentInfo(PageCondition<ElcStudentDto> condition) {
        PageHelper.startPage(condition.getPageNum_(), condition.getPageSize_());
        Page<ElcStudentVo> elcStudentVos = courseTakeDao.findElcStudentInfo(condition.getCondition());
        setCourseArrange(elcStudentVos);
        return new PageResult<>(elcStudentVos);
    }

    @Override
    public PageResult<ElcStudentVo> addCourseList(String studentId) {
        /**
         * 调用培养：个人培养计划完成情况接口
         * coursesLabelList (课程分类列表)
         * cultureCourseLabelRelationList(课程列表)
         */
        String path = ServicePathEnum.CULTURESERVICE.getPath("/culturePlan/getCulturePlanByStudentIdForElection?id={id}&&isPass={isPass}");
        RestResult<Map<String, Object>> restResult = restTemplate.getForObject(path, RestResult.class, studentId, 0);
        System.out.println(restResult);
        List<Long> list = new ArrayList<>();


        Page<ElcStudentVo> elcStudentVos = courseTakeDao.findAddCourseList(list, studentId);
        setCourseArrange(elcStudentVos);
        return new PageResult<>(elcStudentVos);
    }

    @Override
    public PageResult<ElcStudentVo> removedCourseList(String studentId) {
        List<Long> list = new ArrayList<>();
        Page<ElcStudentVo> elcStudentVos = courseTakeDao.findRemovedCourseList(studentId);
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
    public Integer addCourse(AddAndRemoveCourseDto courseDto) {
        List<Long> teachingClassId = courseDto.getTeachingClassId();
        List<ElcStudentVo> elcStudentVos = courseTakeDao.findCourseInfo(teachingClassId);
        List<ElcCourseTake> elcCourseTakes = new ArrayList<>();
        List<ElcLog> elcLogs = new ArrayList<>();
        addToList(courseDto, elcStudentVos, elcCourseTakes, elcLogs);
        Integer count = courseTakeDao.saveCourseTask(elcCourseTakes);
        elcLogDao.saveCourseLog(elcLogs);
        return count;
    }

    @Override
    public Integer removedCourse(AddAndRemoveCourseDto courseDto) {
        List<Long> teachingClassId = courseDto.getTeachingClassId();
        List<ElcStudentVo> elcStudentVos = courseTakeDao.findCourseInfo(teachingClassId);
        Integer count = courseTakeDao.deleteCourseTask(teachingClassId, courseDto.getStudentId());
        List<ElcLog> elcLogs = new ArrayList<>();
        addToList(courseDto, elcStudentVos,elcLogs);
        elcLogDao.saveCourseLog(elcLogs);
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
