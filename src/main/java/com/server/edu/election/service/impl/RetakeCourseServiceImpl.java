package com.server.edu.election.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.server.edu.common.PageCondition;
import com.server.edu.common.rest.PageResult;
import com.server.edu.common.vo.SchoolCalendarVo;
import com.server.edu.dictionary.service.DictionaryService;
import com.server.edu.dictionary.utils.ClassroomCacheUtil;
import com.server.edu.election.constants.ChooseObj;
import com.server.edu.election.constants.CourseTakeType;
import com.server.edu.election.dao.*;
import com.server.edu.election.dto.ClassTeacherDto;
import com.server.edu.election.dto.TimeTableMessage;
import com.server.edu.election.entity.ElcCourseTake;
import com.server.edu.election.entity.ElcLog;
import com.server.edu.election.entity.ElectionRule;
import com.server.edu.election.rpc.BaseresServiceInvoker;
import com.server.edu.election.rpc.ScoreServiceInvoker;
import com.server.edu.election.service.RetakeCourseService;
import com.server.edu.election.studentelec.event.ElectLoadEvent;
import com.server.edu.election.vo.*;
import com.server.edu.session.util.SessionUtils;
import com.server.edu.session.util.entity.Session;
import com.server.edu.util.CalUtil;
import com.server.edu.util.CollectionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class RetakeCourseServiceImpl implements RetakeCourseService {
    @Autowired
    private RetakeCourseSetDao retakeCourseSetDao;

    @Autowired
    private RetakeSetRefRuleDao retakeSetRefRuleDao;

    @Autowired
    private RetakeCourseCountDao retakeCourseCountDao;

    @Autowired
    private ElcCourseTakeDao courseTakeDao;

    @Autowired
    private CourseOpenDao courseOpenDao;

    @Autowired
    private ElectionRuleDao electionRuleDao;

    @Autowired
    private StudentDao studentDao;

    @Override
    @Transactional
    public void setRetakeRules(ElcRetakeSetVo elcRetakeSetVo) {
        Long retakeSetId = retakeCourseSetDao.findRetakeSetId(elcRetakeSetVo.getCalendarId(), elcRetakeSetVo.getProjectId());
        if (retakeSetId == null) {
            elcRetakeSetVo.setCreateAt(new Date());
            retakeCourseSetDao.insertRetakeCourseSet(elcRetakeSetVo);
        } else {
            retakeCourseSetDao.updateRetakeCourseSet(elcRetakeSetVo);
            retakeSetRefRuleDao.deleteByRetakeSetId(retakeSetId);
            elcRetakeSetVo.setRetakeSetId(retakeSetId);
        }
        List<Long> ruleIds = elcRetakeSetVo.getRuleIds();
        if (CollectionUtil.isNotEmpty(ruleIds)) {
            retakeSetRefRuleDao.saveRetakeSetRefRule(elcRetakeSetVo);
        }
    }

    @Override
    public PageResult<RetakeCourseCountVo> findRetakeCourseCountList(PageCondition<RetakeCourseCountVo> condition) {
        PageHelper.startPage(condition.getPageNum_(), condition.getPageSize_());
        Page<RetakeCourseCountVo> retakeCourseCountVos = retakeCourseCountDao.findRetakeCourseCountList(condition.getCondition());
        return new PageResult<>(retakeCourseCountVos);
    }

    @Override
    public void updateRetakeCourseCount(RetakeCourseCountVo retakeCourseCountVo) {
        Long id = retakeCourseCountVo.getId();
        if (id == null) {
            retakeCourseCountDao.saveRetakeCourseCount(retakeCourseCountVo);
        } else {
            retakeCourseCountDao.updateRetakeCourseCount(retakeCourseCountVo);
        }
    }

    @Override
    public void deleteRetakeCourseCount(Long retakeCourseCountId) {
        retakeCourseCountDao.deleteRetakeCourseCount(retakeCourseCountId);
    }

    @Override
    public ElcRetakeSetVo getRetakeRul(Long calendarId, String projectId) {
        return retakeCourseSetDao.findRetakeSet(calendarId, projectId);
    }

    @Override
    public List<FailedCourseVo> failedCourseList(String uid, Long calendarId) {
        List<String> failedCourseCodes = ScoreServiceInvoker.findStuFailedCourseCodes(uid);
        List<FailedCourseVo> failedCourseInfo = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(failedCourseCodes)) {
            failedCourseInfo = courseOpenDao.findFailedCourseInfo(failedCourseCodes, calendarId);
            for (FailedCourseVo failedCourseVo : failedCourseInfo) {
                SchoolCalendarVo schoolCalendar = BaseresServiceInvoker.getSchoolCalendarById(calendarId);
                failedCourseVo.setCalendarName(schoolCalendar.getFullName());
                int count = courseTakeDao.findIsEletionCourse(uid, calendarId, failedCourseVo.getCourseCode());
                if (count == 0) {
                    failedCourseVo.setSelected(false);
                } else {
                    failedCourseVo.setSelected(true);
                }
            }
        }
        return failedCourseInfo;
    }

    @Override
    public void updateRebuildCourse(String studentId, RebuildCourseVo rebuildCourseVo) {

        if (rebuildCourseVo.getStatus() == 0) {
            String courseCode = rebuildCourseVo.getCourseCode();
            Long teachingClassId = rebuildCourseVo.getTeachingClassId();
            String courseName = rebuildCourseVo.getCourseName();
            String teachingClassCode = rebuildCourseVo.getClassCode();
            Long calendarId = rebuildCourseVo.getCalendarId();
            Date date = new Date();
            ElcCourseTake take = new ElcCourseTake();
            take.setCalendarId(calendarId);
            take.setChooseObj(1);
            take.setCourseCode(courseCode);
            take.setCourseTakeType(CourseTakeType.RETAKE.type());
            take.setCreatedAt(date);
            take.setStudentId(studentId);
            take.setTeachingClassId(teachingClassId);
            take.setMode(ElcLogVo.MODE_1);
            take.setTurn(0);
            courseTakeDao.insertSelective(take);
            // 添加选课日志
            ElcLog log = new ElcLog();
            log.setCalendarId(calendarId);
            log.setCourseCode(courseCode);
            log.setCourseName(courseName);
            Session currentSession = SessionUtils.getCurrentSession();
            log.setCreateBy(currentSession.getUid());
            log.setCreatedAt(date);
            log.setCreateIp(currentSession.getIp());
            log.setMode(ElcLogVo.MODE_1);
            log.setStudentId(studentId);
            log.setTeachingClassCode(teachingClassCode);
            log.setTurn(0);
            log.setType(ElcLogVo.TYPE_1);
        }
    }


    @Override
    public List<RebuildCourseVo> findRebuildCourseList(
            Session session, Long calendarId, String keyWord) {
        String studentId = session.getUid();
        String currentManageDptId = session.getCurrentManageDptId();
        List<String> failedCourseCodes = ScoreServiceInvoker.findStuFailedCourseCodes(studentId);
        if (CollectionUtil.isNotEmpty(failedCourseCodes)) {
            // 通过重修的课程代码获取当前学期可重修的课程
            List<RebuildCourseVo> list = courseOpenDao.findRebuildCourses(failedCourseCodes);
            if (list.isEmpty()) {
                return new ArrayList<>();
            }
            // 获取学生已选课程上课安排
            List<Long> ids = courseTakeDao.findTeachingClassIdByStudentId(studentId, calendarId);
            List<TimeTableMessage> selectTimeTables = courseTakeDao.findCourseArrange(ids);
            // 获取重修课程教学安排
            List<Long> teachingClassIds = list.stream().map(RebuildCourseVo::getTeachingClassId).collect(Collectors.toList());
            List<TimeTableMessage> timeTableMessages = getTimeById(teachingClassIds);
            Map<Long, List<TimeTableMessage>> map = timeTableMessages.stream().collect(Collectors.groupingBy(TimeTableMessage::getTeachingClassId));
            // 获取重修规则
            List<Integer> courseRole = getCourseRole(calendarId, currentManageDptId);
            // 重修规则为空说明所有重修课程都可以选择
            if (courseRole.isEmpty()) {
                for (RebuildCourseVo rebuildCourseVo : list) {
                    setCourseArrange(map, rebuildCourseVo);
                    // 判断这门课程是可以进行选课操作还是退课操作
                    int count = courseTakeDao.findCount(studentId, calendarId, rebuildCourseVo.getTeachingClassId());
                    if (count == 0) {
                        rebuildCourseVo.setStatus(0);
                    } else {
                        rebuildCourseVo.setStatus(1);
                    }
                }
            } else if (courseRole.size() == 1) {
                Integer i = courseRole.get(0);
                if (i == 1) {
                    String campus = studentDao.findCampus(studentId);
                    for (RebuildCourseVo rebuildCourseVo : list) {
                        setCourseArrange(map, rebuildCourseVo);
                        int count = courseTakeDao.findCount(studentId, calendarId, rebuildCourseVo.getTeachingClassId());
                        // 为0说明学生要进行选课操作，需判断规则
                        if (count == 0) {
                            // 校区相同才可进行选课
                            if (campus.equals(rebuildCourseVo.getCampus())) {
                                rebuildCourseVo.setStatus(0);
                            }
                        } else {
                            // 退课无需判断规则，直接退
                            rebuildCourseVo.setStatus(1);
                        }
                    }
                } else if (i == 2) {
                    for (RebuildCourseVo rebuildCourseVo : list) {
                        setCourseArrange(map, rebuildCourseVo);
                        int count = courseTakeDao.findCount(studentId, calendarId, rebuildCourseVo.getTeachingClassId());
                        if (count == 0) {
                            // 课程人数未选满方可选课
                            if (rebuildCourseVo.getSelectNumber() < rebuildCourseVo.getNumber()) {
                                rebuildCourseVo.setStatus(0);
                            }
                        } else {
                            rebuildCourseVo.setStatus(1);
                        }
                    }
                } else if (i == 3) {
                    for (RebuildCourseVo rebuildCourseVo : list) {
                        setCourseArrange(map, rebuildCourseVo);
                        Long teachingClassId = rebuildCourseVo.getTeachingClassId();
                        int count = courseTakeDao.findCount(studentId, calendarId, teachingClassId);
                        if (count == 0) {
                            if (getCourseConflict(selectTimeTables, map.get(teachingClassId))) {
                                rebuildCourseVo.setStatus(0);

                            }
                        } else {
                            rebuildCourseVo.setStatus(1);
                        }
                    }
                }
            } else if (courseRole.size() == 2) {
                if (list.contains(1) && list.contains(2)) {
                    String campus = studentDao.findCampus(studentId);
                    for (RebuildCourseVo rebuildCourseVo : list) {
                        setCourseArrange(map, rebuildCourseVo);
                        int count = courseTakeDao.findCount(studentId, calendarId, rebuildCourseVo.getTeachingClassId());
                        if (count == 0) {
                            // 校区相同再进行下一步判断
                            if (campus.equals(rebuildCourseVo.getCampus())) {
                                // 判断选课人数
                                if (rebuildCourseVo.getSelectNumber() < rebuildCourseVo.getNumber()) {
                                    rebuildCourseVo.setStatus(0);
                                }
                            }
                        } else {
                            rebuildCourseVo.setStatus(1);
                        }
                    }
                } else if (list.contains(1) && list.contains(3)) {
                    String campus = studentDao.findCampus(studentId);
                    for (RebuildCourseVo rebuildCourseVo : list) {
                        setCourseArrange(map, rebuildCourseVo);
                        Long teachingClassId = rebuildCourseVo.getTeachingClassId();
                        int count = courseTakeDao.findCount(studentId, calendarId, teachingClassId);
                        if (count == 0) {
                            // 校区相同再进行下一步判断
                            if (campus.equals(rebuildCourseVo.getCampus())) {
                                // 判断课程安排
                                if (getCourseConflict(selectTimeTables, map.get(teachingClassId))) {
                                    rebuildCourseVo.setStatus(0);
                                }
                            }
                        } else {
                            rebuildCourseVo.setStatus(1);
                        }
                    }
                } else if (list.contains(2) && list.contains(3)) {
                    for (RebuildCourseVo rebuildCourseVo : list) {
                        setCourseArrange(map, rebuildCourseVo);
                        Long teachingClassId = rebuildCourseVo.getTeachingClassId();
                        int count = courseTakeDao.findCount(studentId, calendarId, teachingClassId);
                        if (count == 0) {
                            // 判断选课人数
                            if (rebuildCourseVo.getSelectNumber() < rebuildCourseVo.getNumber()) {
                                // 判断课程安排
                                if (getCourseConflict(selectTimeTables, map.get(teachingClassId))) {
                                    rebuildCourseVo.setStatus(0);
                                }
                            }
                        } else {
                            rebuildCourseVo.setStatus(1);
                        }
                    }
                }
            } else {
                String campus = studentDao.findCampus(studentId);
                for (RebuildCourseVo rebuildCourseVo : list) {
                    setCourseArrange(map, rebuildCourseVo);
                    Long teachingClassId = rebuildCourseVo.getTeachingClassId();
                    int count = courseTakeDao.findCount(studentId, calendarId, teachingClassId);
                    if (count == 0) {
                        // 校区相同才可进行选课
                        if (campus.equals(rebuildCourseVo.getCampus())) {
                            // 判断选课人数
                            if (rebuildCourseVo.getSelectNumber() < rebuildCourseVo.getNumber()) {
                                // 判断课程安排
                                if (getCourseConflict(selectTimeTables, map.get(teachingClassId))) {
                                    rebuildCourseVo.setStatus(0);
                                }
                            }
                        }
                    } else {
                        rebuildCourseVo.setStatus(1);
                    }
                }
            }
            return list;
        }
        return new ArrayList<>();
    }


    /**
     * 设置教学安排
     *
     * @param map
     * @param rebuildCourseVo
     */
    private void setCourseArrange(Map<Long, List<TimeTableMessage>> map, RebuildCourseVo rebuildCourseVo) {
        List<TimeTableMessage> timeTables = map.get(rebuildCourseVo.getTeachingClassId());
        if (CollectionUtil.isNotEmpty(timeTables)) {
            List<String> courseArrange = timeTables.stream().map(TimeTableMessage::getTimeAndRoom).collect(Collectors.toList());
            rebuildCourseVo.setCourseArrange(courseArrange);
        }

    }

    /**
     * 判断要添加课程是否与学生已选课程冲突，true不冲突，false冲突
     *
     * @param selectTimeTables
     * @param addTimeTables
     * @return
     */
    private boolean getCourseConflict(List<TimeTableMessage> selectTimeTables, List<TimeTableMessage> addTimeTables) {
        if (CollectionUtil.isEmpty(addTimeTables)) {
            //说明上课时间未安排，暂时不冲突，可以添加
            return true;
        }
        //获取要添加课程的上课周
        String[] week = addTimeTables.get(0).getWeekNum().split(",");
        if (week.length == 0) {
            //说明上课时间未安排，暂时不冲突，可以添加
            return true;
        }
        // 循环已选课程上课周，判断上课周是否冲突
        for (TimeTableMessage timeTableMessage : selectTimeTables) {
            String[] split = timeTableMessage.getWeekNum().split(",");
            if (split.length == 0) {
                return true;
            }
            Set<String> set = new HashSet<>();
            set.addAll(Arrays.asList(week));
            set.addAll(Arrays.asList(split));
            // 数组长度之和大于set集合长度，说明上课周重复
            if (week.length + split.length > set.size()) {
                int dayOfWeek = timeTableMessage.getDayOfWeek().intValue();
                int timeStart = timeTableMessage.getTimeStart().intValue();
                int timeEnd = timeTableMessage.getTimeEnd().intValue();
                for (TimeTableMessage tableMessage : addTimeTables) {
                    // 比较上课天是否相同
                    if (dayOfWeek == tableMessage.getDayOfWeek().intValue()) {
                        // 上课天相同，比价上课节次
                        int start = timeTableMessage.getTimeStart().intValue();
                        int end = tableMessage.getTimeEnd().intValue();
                        // 判断要添加课程上课开始、结束节次是否与已选课上课节次冲突
                        if ((timeStart <= start && start <= timeEnd) || (timeStart <= end && end <= timeEnd)) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    /**
     * 返回选课规则
     *
     * @param calendarId
     * @param manageDptId
     * @return
     */
    private List<Integer> getCourseRole(Long calendarId, String manageDptId) {
        List<Long> ruleIds = retakeCourseSetDao.findRuleIds(calendarId, manageDptId);
        Example example = new Example(ElectionRule.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("id", ruleIds);
        List<ElectionRule> electionRules = electionRuleDao.selectByExample(example);
        List<Integer> list = new ArrayList<>();
        for (ElectionRule electionRule : electionRules) {
            String name = electionRule.getName();
            if ("不能跨校区选课".equals(name)) {
                list.add(1);
            }
            if ("不能超出人数上限".equals(name)) {
                list.add(2);
                continue;
            }
            if ("不允许时间冲突".equals(name)) {
                list.add(3);
                continue;
            }
        }
        return list;
    }

    /**
     * 通过教学班id获取课程安排
     *
     * @param teachingClassId
     * @return
     */
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
                    String weekstr = findWeek(dayOfWeek);//星期
                    String timeStr = weekstr + timeStart + "-" + timeEnd + "节"
                            + weekNumStr + ClassroomCacheUtil.getRoomName(classTeacherDto.getRoomID());
                    TimeTableMessage time = new TimeTableMessage();
                    time.setDayOfWeek(dayOfWeek);
                    time.setTimeStart(timeStart);
                    time.setTimeEnd(timeEnd);
                    time.setTeachingClassId(classTeacherDto.getTeachingClassId());
                    time.setTimeAndRoom(timeStr);
                    time.setWeeks(weeks);
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
