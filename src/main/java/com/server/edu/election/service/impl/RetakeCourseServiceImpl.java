package com.server.edu.election.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.server.edu.common.PageCondition;
import com.server.edu.common.enums.GroupDataEnum;
import com.server.edu.common.locale.I18nUtil;
import com.server.edu.common.rest.PageResult;
import com.server.edu.common.vo.SchoolCalendarVo;
import com.server.edu.dictionary.utils.ClassroomCacheUtil;
import com.server.edu.election.constants.Constants;
import com.server.edu.election.constants.CourseTakeType;
import com.server.edu.election.constants.ElectRuleType;
import com.server.edu.election.dao.*;
import com.server.edu.election.dto.ClassTeacherDto;
import com.server.edu.election.dto.RebuildCourseDto;
import com.server.edu.election.dto.RetakeCourseCountDto;
import com.server.edu.election.dto.TimeTableMessage;
import com.server.edu.election.entity.Course;
import com.server.edu.election.entity.ElcCourseTake;
import com.server.edu.election.entity.ElcLog;
import com.server.edu.election.entity.ElectionRule;
import com.server.edu.election.entity.Student;
import com.server.edu.election.rpc.BaseresServiceInvoker;
import com.server.edu.election.rpc.ScoreServiceInvoker;
import com.server.edu.election.service.RetakeCourseService;
import com.server.edu.election.studentelec.cache.TeachingClassCache;
import com.server.edu.election.studentelec.service.impl.ElecYjsServiceImpl;
import com.server.edu.election.util.WeekUtil;
import com.server.edu.election.vo.*;
import com.server.edu.exception.ParameterValidateException;
import com.server.edu.session.util.SessionUtils;
import com.server.edu.session.util.entity.Session;
import com.server.edu.util.CalUtil;
import com.server.edu.util.CollectionUtil;
import org.apache.commons.lang3.StringUtils;
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
    private CourseDao courseDao;

    @Autowired
    private ElectionRuleDao electionRuleDao;

    @Autowired
    private StudentDao studentDao;

    @Autowired
    private ElcLogDao elcLogDao;

    @Autowired
    private TeachingClassDao teachingClassDao;

    @Autowired
    private TeachingClassTeacherDao teachingClassTeacherDao;

//    @Autowired
//    private ElecYjsServiceImpl elecYjsServiceImpl;

    @Override
    @Transactional
    public void setRetakeRules(ElcRetakeSetVo elcRetakeSetVo) {
        Session currentSession = SessionUtils.getCurrentSession();
        Long retakeSetId = retakeCourseSetDao.findRetakeSetId(elcRetakeSetVo.getCalendarId(), currentSession.getCurrentManageDptId());
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
    public PageResult<RetakeCourseCountDto> findRetakeCourseCountList(PageCondition<RetakeCourseCountVo> condition) {
        RetakeCourseCountDto retakeCourseCountDto = getRetakeCourseCountDto(condition.getCondition());
        PageHelper.startPage(condition.getPageNum_(), condition.getPageSize_());
        Page<RetakeCourseCountDto> retakeCourseCountVos = retakeCourseCountDao.findRetakeCourseCountList(retakeCourseCountDto);
        return new PageResult<>(retakeCourseCountVos);
    }

    @Override
    public void updateRetakeCourseCount(RetakeCourseCountVo retakeCourseCountVo) {
        Long id = retakeCourseCountVo.getId();
        RetakeCourseCountDto retakeCourseCountDto = getRetakeCourseCountDto(retakeCourseCountVo);
        // 判断这条数据是否与数据库现有数据重复重复
        Session currentSession = SessionUtils.getCurrentSession();
        String manageDptId = currentSession.getCurrentManageDptId();
        retakeCourseCountDto.setProjectId(manageDptId);
        RetakeCourseCountDto retakeCourseCount = retakeCourseCountDao.findRetakeCourseCount(retakeCourseCountDto);
        if (id == null) {
            if (retakeCourseCount != null) {
                throw new ParameterValidateException(I18nUtil.getMsg("elcCourseUphold.dataError",retakeCourseCount.getProjectName()));
            }
            String uid = currentSession.getUid();
            retakeCourseCountDto.setProjectId(manageDptId);
            retakeCourseCountDto.setCreateBy(uid);
            retakeCourseCountDto.setCreateAt(new Date());
            retakeCourseCountDao.saveRetakeCourseCount(retakeCourseCountDto);
        } else {
            // 判断修改后的数据是否与数据库已有数据重复
            if (retakeCourseCount != null && id.intValue() != retakeCourseCount.getId().intValue()) {
                throw new ParameterValidateException(I18nUtil.getMsg("elcCourseUphold.dataError",retakeCourseCount.getProjectName()));
            }
            retakeCourseCountDto.setId(id);
            retakeCourseCountDto.setUpdatedAt(new Date());
            retakeCourseCountDao.updateRetakeCourseCount(retakeCourseCountDto);
        }
    }

    /**
     * 重修门数上限参数转换
     * @param retakeCourseCountVo
     * @return
     */
    private RetakeCourseCountDto getRetakeCourseCountDto(RetakeCourseCountVo retakeCourseCountVo) {
        List<String> trainingLevel = getList(retakeCourseCountVo.getTrainingLevel());
        String trainingLevels = String.join(",",trainingLevel);
        List<String> trainingCategory = getList(retakeCourseCountVo.getTrainingCategory());
        String trainingCategories = String.join(",",trainingCategory);
        List<String> degreeType = getList(retakeCourseCountVo.getDegreeType());
        String degreeTypes = String.join(",",degreeType);
        List<String> formLearning = getList(retakeCourseCountVo.getFormLearning());
        String formLearnings = String.join(",",formLearning);
        RetakeCourseCountDto retakeCourseCountDto = new RetakeCourseCountDto();
        retakeCourseCountDto.setTrainingLevel(trainingLevels);
        retakeCourseCountDto.setTrainingCategory(trainingCategories);
        retakeCourseCountDto.setDegreeType(degreeTypes);
        retakeCourseCountDto.setFormLearning(formLearnings);
        retakeCourseCountDto.setStatus(Constants.DELETE_FALSE);
        retakeCourseCountDto.setProjectName(retakeCourseCountVo.getProjectName());
        retakeCourseCountDto.setRetakeCount(retakeCourseCountVo.getRetakeCount());
        Session session = SessionUtils.getCurrentSession();
        retakeCourseCountDto.setProjectId(session.getCurrentManageDptId());
        return retakeCourseCountDto;
    }

    /**
     * 去除集合中的空字符串
     * @param list
     * @return
     */
    private List<String> getList(List<String> list) {
        List<String> newList = new ArrayList<>(list.size());
        for (String s : list) {
            if ("".equals(s)) {
                continue;
            }
            newList.add(s);
        }
        return newList;
    }

    @Override
    public void deleteRetakeCourseCount(List<Long> retakeCourseCountIds) {
        retakeCourseCountDao.deleteRetakeCourseCount(retakeCourseCountIds);
    }

    @Override
    public ElcRetakeSetVo getRetakeSet(Long calendarId, String projectId) {
        return retakeCourseSetDao.findRetakeSet(calendarId, projectId);
    }

    @Override
    public Boolean getRetakeRule(Long calendarId, String projectId) {
        ElcRetakeSetVo retakeSet = retakeCourseSetDao.findRetakeSet(calendarId, projectId);
        if (retakeSet != null && retakeSet.getOpenFlag().intValue() == 1) {
            Date start = retakeSet.getStart();
            Date end = retakeSet.getEnd();
            if (start != null && end != null) {
                long startTime = start.getTime();
                long endTime = end.getTime();
                long nowTime = System.currentTimeMillis();
                if (startTime < nowTime && nowTime < endTime ) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public List<FailedCourseVo> failedCourseList(Long calendarId) {
        Session currentSession = SessionUtils.getCurrentSession();
        String uid = currentSession.realUid();
        String currentManageDptId = currentSession.getCurrentManageDptId();
        return failedCourse(calendarId, uid, currentManageDptId);
    }

    @Override
    public List<FailedCourseVo> failedCourses(Long calendarId, String studentId) {
        Session currentSession = SessionUtils.getCurrentSession();
        String currentManageDptId = currentSession.getCurrentManageDptId();
        return failedCourse(calendarId, studentId, currentManageDptId);
    }

    private List<FailedCourseVo> failedCourse(Long calendarId,
                                              String studentId, String currentManageDptId)
    {
        List<String> failedCourseCodes = ScoreServiceInvoker.findStuFailedCourseCodes(studentId);
        List<FailedCourseVo> failedCourseInfo = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(failedCourseCodes)) {
            failedCourseInfo = courseOpenDao.findFailedCourseInfo(failedCourseCodes, calendarId, currentManageDptId);
            for (FailedCourseVo failedCourseVo : failedCourseInfo) {
                SchoolCalendarVo schoolCalendar = BaseresServiceInvoker.getSchoolCalendarById(calendarId);
                failedCourseVo.setCalendarName(schoolCalendar.getFullName());
                // 借用 判断申请免修免考课程是否已经选课 判断学生是否选课
                int count = courseTakeDao.findIsEletionCourse(studentId, calendarId, failedCourseVo.getCourseCode());
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
    @Transactional
    public void updateRebuildCourse(RebuildCourseVo rebuildCourseVo) {
        Session currentSession = SessionUtils.getCurrentSession();
        String studentId = currentSession.realUid();
        String ip = currentSession.getIp();
        String courseCode = rebuildCourseVo.getCourseCode();
        Long teachingClassId = rebuildCourseVo.getTeachingClassId();
        String courseName = rebuildCourseVo.getCourseName();
        String teachingClassCode = rebuildCourseVo.getClassCode();
        Long calendarId = rebuildCourseVo.getCalendarId();
        Date date = new Date();
        ElcLog log = new ElcLog();
        log.setStudentId(studentId);
        log.setCalendarId(calendarId);
        log.setCourseCode(courseCode);
        log.setCourseName(courseName);
        log.setTeachingClassCode(teachingClassCode);
        log.setMode(ElcLogVo.MODE_1);
        log.setTurn(0);
        log.setCreateBy(studentId);
        log.setCreatedAt(date);
        log.setCreateIp(ip);
        if (rebuildCourseVo.getStatus() == 0) {
            Student student = studentDao.findStudentByCode(studentId);
            Integer maxCount = retakeCourseCountDao.findRetakeCount(student);
            if (maxCount == null) {
                throw new ParameterValidateException(I18nUtil.getMsg("rebuildCourse.countLimitError",I18nUtil.getMsg("election.elcNoGradCouSubs")));
            }
            Set<String> set =courseTakeDao.findRetakeCount(studentId);
            if (set.size() >= maxCount.intValue()) {
                throw new ParameterValidateException(I18nUtil.getMsg("rebuildCourse.countLimit",I18nUtil.getMsg("election.elcNoGradCouSubs")));
            }
            // 判断学生是否已经选过该门课程
            int count = courseTakeDao.findIsEletionCourse(studentId, calendarId, courseCode);
            if (count != 0) {
                throw new ParameterValidateException(I18nUtil.getMsg("rebuildCourse.repeatedError"));
            }
            ElcCourseTake take = new ElcCourseTake();
            take.setStudentId(studentId);
            take.setCalendarId(calendarId);
            take.setTeachingClassId(teachingClassId);
            take.setCourseCode(courseCode);
            take.setCourseTakeType(CourseTakeType.RETAKE.type());
            take.setChooseObj(1);
            take.setCreatedAt(date);
            take.setTurn(0);
            take.setMode(1);
            courseTakeDao.insertSelective(take);
            teachingClassDao.increElcNumber(teachingClassId);
            log.setType(ElcLogVo.TYPE_1);
            // 添加选课日志
            elcLogDao.insertSelective(log);
//            try {
//                elecYjsServiceImpl.updateSelectCourse(studentId,courseCode, ElectRuleType.ELECTION);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
        } else if (rebuildCourseVo.getStatus() == 1) {
            Long id = rebuildCourseVo.getTeachingClassId();
            List<Long> list = new ArrayList<>(1);
            list.add(id);
            List<String> courseCodes = new ArrayList<>(1);
            courseCodes.add(courseCode);
            List<String> courses = ScoreServiceInvoker.findCourseHaveScore(studentId, calendarId, courseCodes);
            if (CollectionUtil.isNotEmpty(courses)) {
                throw new ParameterValidateException(I18nUtil.getMsg(I18nUtil.getMsg("elcCourseUphold.removeCourseError",courses.get(0))));
            }
            courseTakeDao.deleteCourseTask(list, studentId);
            teachingClassDao.decrElcNumber(teachingClassId);
            // 添加选课日志
            log.setType(ElcLogVo.TYPE_2);
            elcLogDao.insertSelective(log);
//            try {
//                elecYjsServiceImpl.updateSelectCourse(studentId,courseCode, ElectRuleType.WITHDRAW);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
        } else {
            throw new ParameterValidateException(I18nUtil.getMsg("rebuildCourse.statusError",I18nUtil.getMsg("election.elcNoGradCouSubs")));
        }
    }

    @Override
    public RebuildStuVo findRebuildStu(Long calendarId, String studentId) {
        Session session = SessionUtils.getCurrentSession();
        Student student = studentDao.selectByPrimaryKey(studentId);
        String currentManageDptId = session.getCurrentManageDptId();
        // 判断是不是教务员
        if (StringUtils.equals(session.getCurrentRole(), "1") && !session.isAdmin() && session.isAcdemicDean()) {
            List<String> deptIds = SessionUtils.getCurrentSession().getGroupData().get(GroupDataEnum.department.getValue());
            String faculty = student.getFaculty();
            // 教务员只能对其管理学院的学生进行代选
            if (!deptIds.contains(faculty)) {
                throw new ParameterValidateException("学生" + student.getName() + "不在您的管理范围之内");
            }
            // 判断重修选课开关是否开启
            Boolean retakeRule = getRetakeRule(calendarId, currentManageDptId);
            if (!retakeRule) {
                throw new ParameterValidateException("重修选课未开放，您不能进行代选课！");
            }
        }
        Integer maxCount = retakeCourseCountDao.findRetakeCount(student);
        List<String> failedCourseCodes = ScoreServiceInvoker.findStuFailedCourseCodes(studentId);
        Example example = new Example(Course.class);
        example.createCriteria()
                .andIn("code", failedCourseCodes);
        List<Course> courses = courseDao.selectByExample(example);
        List<String> collect = courses.stream().map(s -> s.getCode() + " " + s.getName()).collect(Collectors.toList());
        RebuildStuVo rebuildStuVo = new RebuildStuVo();
        rebuildStuVo.setStudentCode(studentId);
        rebuildStuVo.setName(student.getName());
        rebuildStuVo.setGrade(student.getGrade());
        rebuildStuVo.setFaculty(student.getFaculty());
        rebuildStuVo.setProfession(student.getProfession());
        rebuildStuVo.setTrainingCategory(student.getTrainingCategory());
        rebuildStuVo.setTrainingLevel(student.getTrainingLevel());
        rebuildStuVo.setDegreeType(student.getDegreeType());
        rebuildStuVo.setFormLearning(student.getFormLearning());
        rebuildStuVo.setCount(maxCount);
        rebuildStuVo.setCourses(collect);
        return rebuildStuVo;
    }


    @Override
    public PageResult<RebuildCourseVo> findRebuildCourseList(PageCondition<RebuildCourseDto> condition) {
        Session session = SessionUtils.getCurrentSession();
        String studentId = session.realUid();
        condition.getCondition().setStudentId(studentId);
        String currentManageDptId = session.getCurrentManageDptId();
        Page<RebuildCourseVo> page = findRebuildCourse(currentManageDptId, condition);
        return new PageResult<>(page);
    }

    @Override
    public PageResult<RebuildCourseVo> findRebuildCourses(PageCondition<RebuildCourseDto> condition) {
        // 区分管理员教务员
        Session session = SessionUtils.getCurrentSession();
        String currentManageDptId = session.getCurrentManageDptId();
        Page<RebuildCourseVo> page = new Page<RebuildCourseVo>();
        if (StringUtils.equals(session.getCurrentRole(), "1") && !session.isAdmin() && session.isAcdemicDean()) {
           // 如果是教务员则和学生一样受选课规则限制
            page = findRebuildCourse(currentManageDptId, condition);
        } else {
            String studentId = condition.getCondition().getStudentId();
            // 管理员不做校验，所有课程都可以选
            List<String> failedCourseCodes = ScoreServiceInvoker.findStuFailedCourseCodes(studentId);
            if (CollectionUtil.isNotEmpty(failedCourseCodes)) {
                // 通过重修的课程代码获取当前学期可重修的课程
                RebuildCourseDto rebuildCourseDto = condition.getCondition();
                Long calendarId = rebuildCourseDto.getCalendarId();
                PageHelper.startPage(condition.getPageNum_(), condition.getPageSize_());
                page = courseOpenDao.findRebuildCourses(failedCourseCodes, calendarId, rebuildCourseDto.getKeyWord(), currentManageDptId);
                if (CollectionUtil.isNotEmpty(page)) {
                    // 获取重修课程教学安排
                    List<TimeTableMessage> timeTableMessages = getTimeById(page);
                    Map<Long, List<TimeTableMessage>> map = timeTableMessages.stream().
                            collect(Collectors.groupingBy(TimeTableMessage::getTeachingClassId));
                    for (RebuildCourseVo rebuildCourseVo : page) {
                        setCourseArrange(map, rebuildCourseVo);
                        // 判断这门课程是可以进行选课操作还是退课操作
                        int count = courseTakeDao.findCount(studentId, calendarId, rebuildCourseVo.getTeachingClassId());
                        if (count == 0) {
                            rebuildCourseVo.setStatus(0);
                        } else {
                            rebuildCourseVo.setStatus(1);
                        }
                    }
                }
            }
        }
        return new PageResult<>(page);
    }

    private Page<RebuildCourseVo> findRebuildCourse(
           String currentManageDptId, PageCondition<RebuildCourseDto> condition)
    {
        RebuildCourseDto rebuildCourseDto = condition.getCondition();
        String studentId = rebuildCourseDto.getStudentId();
        List<String> failedCourseCodes = ScoreServiceInvoker.findStuFailedCourseCodes(studentId);
        if (CollectionUtil.isNotEmpty(failedCourseCodes)) {
            // 通过重修的课程代码获取当前学期可重修的课程
            Long calendarId = rebuildCourseDto.getCalendarId();
            PageHelper.startPage(condition.getPageNum_(), condition.getPageSize_());
            Page<RebuildCourseVo> page = courseOpenDao.findRebuildCourses(failedCourseCodes, calendarId, rebuildCourseDto.getKeyWord(), currentManageDptId);
            if (CollectionUtil.isEmpty(page)) {
                return page;
            }
            // 获取学生已选课程上课安排
            List<Long> ids = courseTakeDao.findTeachingClassIdByStudentId(studentId, calendarId);
            List<TimeTableMessage> selectTimeTables = new ArrayList<>();
            if (CollectionUtil.isNotEmpty(ids)) {
                selectTimeTables = courseTakeDao.findCourseArrange(ids);
            }
            // 获取重修课程教学安排
            List<TimeTableMessage> timeTableMessages = getTimeById(page);
            Map<Long, List<TimeTableMessage>> map = timeTableMessages.stream().collect(Collectors.groupingBy(TimeTableMessage::getTeachingClassId));
            // 获取重修规则
            List<Integer> courseRole = getCourseRole(calendarId, currentManageDptId);
            // 重修规则为空说明所有重修课程都可以选择
            if (courseRole.isEmpty()) {
                for (RebuildCourseVo rebuildCourseVo : page) {
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
                    for (RebuildCourseVo rebuildCourseVo : page) {
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
                    for (RebuildCourseVo rebuildCourseVo : page) {
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
                    for (RebuildCourseVo rebuildCourseVo : page) {
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
                if (courseRole.contains(1) && courseRole.contains(2)) {
                    String campus = studentDao.findCampus(studentId);
                    for (RebuildCourseVo rebuildCourseVo : page) {
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
                } else if (courseRole.contains(1) && courseRole.contains(3)) {
                    String campus = studentDao.findCampus(studentId);
                    for (RebuildCourseVo rebuildCourseVo : page) {
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
                } else if (courseRole.contains(2) && courseRole.contains(3)) {
                    for (RebuildCourseVo rebuildCourseVo : page) {
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
                for (RebuildCourseVo rebuildCourseVo : page) {
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
            return  page;
        }
        return new Page<RebuildCourseVo>();
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
        List<Integer> addWeeks = addTimeTables.get(0).getWeeks();
        if (CollectionUtil.isEmpty(addWeeks)) {
            //说明上课时间未安排，暂时不冲突，可以添加
            return true;
        }
        // 循环已选课程上课周，判断上课周是否冲突
        for (TimeTableMessage timeTableMessage : selectTimeTables) {
            String[] split = timeTableMessage.getWeekNum().split(",");
            if (split.length == 0) {
                return true;
            }
            // 避免重复周次
            Set<Integer> selectWeeks = Arrays.asList(split).stream().map(Integer::parseInt).collect(Collectors.toSet());
            Set<Integer> set = new HashSet<>();
            set.addAll(addWeeks);
            set.addAll(selectWeeks);
            // 数组长度之和大于set集合长度，说明上课周重复
            if (addWeeks.size() + selectWeeks.size() > set.size()) {
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
    public List<Integer> getCourseRole(Long calendarId, String manageDptId) {
        List<Long> ruleIds = retakeCourseSetDao.findRuleIds(calendarId, manageDptId);
        if (ruleIds.isEmpty()) {
            return new ArrayList<>();
        }
        Example example = new Example(ElectionRule.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("id", ruleIds);
        List<ElectionRule> electionRules = electionRuleDao.selectByExample(example);
        List<Integer> list = new ArrayList<>(3);
        Set<Integer> set = new HashSet<>(electionRules.size());
        for (ElectionRule electionRule : electionRules) {
            String name = electionRule.getName();
            if ("不能跨校区选课".equals(name)) {
                set.add(1);
            } else if ("不能超出人数上限".equals(name)) {
                set.add(2);
            }else if ("不允许时间冲突".equals(name)) {
                set.add(3);
            }
        }
        list.addAll(set);
        return list;
    }

    /**
     * 通过教学班id获取课程安排
     *
     * @param page
     * @return
     */
    private List<TimeTableMessage> getTimeById(Page<RebuildCourseVo> page) {
        List<Long> teachingClassIds = page.stream().map(RebuildCourseVo::getTeachingClassId).collect(Collectors.toList());
        List<TeachingClassCache> teacherClass = teachingClassTeacherDao.findTeacherClass(teachingClassIds);
        Map<Long, List<TeachingClassCache>> map = teacherClass.stream().collect(Collectors.groupingBy(TeachingClassCache::getTeachClassId));
        //添加教师名
        for (RebuildCourseVo rebuildCourseVo : page) {
            Long teachingClassId = rebuildCourseVo.getTeachingClassId();
            List<TeachingClassCache> teachingClassCaches = map.get(teachingClassId);
            if (CollectionUtil.isNotEmpty(teachingClassCaches)) {
                Set<String> set = teachingClassCaches.stream().map(TeachingClassCache::getTeacherName).collect(Collectors.toSet());
                rebuildCourseVo.setTeacherName(String.join(",",set));
            }
        }
        List<TimeTableMessage> courseArrange = courseTakeDao.findCourseArrange(teachingClassIds);
            if (CollectionUtil.isNotEmpty(courseArrange)) {
                for (TimeTableMessage timeTableMessage : courseArrange) {
                    Integer dayOfWeek = timeTableMessage.getDayOfWeek();
                    Integer timeStart = timeTableMessage.getTimeStart();
                    Integer timeEnd = timeTableMessage.getTimeEnd();
                    String weekNumber = timeTableMessage.getWeekNum();
                    String[] str = weekNumber.split(",");
                    Set<String> weeksSet = new HashSet<>(Arrays.asList(str));
                    List<Integer> weeks = weeksSet.stream().map(Integer::parseInt).collect(Collectors.toList());
                    List<String> weekNums = CalUtil.getWeekNums(weeks.toArray(new Integer[]{}));
                    String weekNumStr = weekNums.toString();//周次
                    String weekstr = WeekUtil.findWeek(dayOfWeek);//星期
                    String timeStr = weekstr + timeStart + "-" + timeEnd + "节"
                            + weekNumStr + ClassroomCacheUtil.getRoomName(timeTableMessage.getRoomId());
                    timeTableMessage.setTimeAndRoom(timeStr);
                    timeTableMessage.setWeeks(weeks);
                }
            }
        return courseArrange;
    }

}
