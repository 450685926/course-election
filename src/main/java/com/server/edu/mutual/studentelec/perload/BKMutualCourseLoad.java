package com.server.edu.mutual.studentelec.perload;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.server.edu.common.entity.Teacher;
import com.server.edu.dictionary.utils.ClassroomCacheUtil;
import com.server.edu.dictionary.utils.SchoolCalendarCacheUtil;
import com.server.edu.dictionary.utils.TeacherCacheUtil;
import com.server.edu.election.constants.Constants;
import com.server.edu.election.dao.TeachingClassDao;
import com.server.edu.election.dao.TeachingClassTeacherDao;
import com.server.edu.election.dto.TeacherClassTimeRoom;
import com.server.edu.election.entity.ElectionRounds;
import com.server.edu.election.studentelec.cache.TeachingClassCache;
import com.server.edu.election.studentelec.context.ClassTimeUnit;
import com.server.edu.election.studentelec.context.ElecRequest;
import com.server.edu.election.studentelec.context.bk.SelectedCourse;
import com.server.edu.mutual.Enum.MutualApplyAuditStatus;
import com.server.edu.mutual.dao.ElecMutualRoundsDao;
import com.server.edu.mutual.dto.ElcMutualApplyDto;
import com.server.edu.mutual.service.ElcMutualAuditService;
import com.server.edu.mutual.studentelec.context.ElecContextMutualBk;
import com.server.edu.mutual.util.ProjectUtil;
import com.server.edu.mutual.vo.ElcMutualApplyVo;
import com.server.edu.util.CalUtil;
import com.server.edu.util.CollectionUtil;

/**
 * 加载学生本研互选审批通过的课程信息
 * 
 * @author  luoxiaoli
 * @version  [版本号, 2019年12月03日]
 * @see  [相关类/方法]
 * @since  [产品/模块版本]
 */
@Component
public class BKMutualCourseLoad extends MutualDataProLoad<ElecContextMutualBk>{

	@Override
	public int getOrder() {
		return 3;
	}

	@Override
	public String getProjectIds() {
		return "1";
	}
	
	private Logger LOG = LoggerFactory.getLogger(BKMutualCourseLoad.class);
	
	
    @Autowired
    private ElcMutualAuditService elcMutualAuditService;
    
    @Autowired
    private ElecMutualRoundsDao elecMutualRoundsDao;
    
    @Autowired
    private TeachingClassDao classDao;
    
    @Autowired
    private TeachingClassTeacherDao teacherDao;
	
	@Override
	public void load(ElecContextMutualBk context) {		
		ElecRequest request = context.getRequest();
		Long calendarId = context.getCalendarId();
		
		String projectId = request.getProjectId();
		String studentId = request.getStudentId();
		Long roundId = request.getRoundId();
		ElectionRounds round = elecMutualRoundsDao.selectByPrimaryKey(roundId);
		
		/** 封装本研互选的数据  */
		ElcMutualApplyDto dto = new ElcMutualApplyDto();
		List<String> projectIds = ProjectUtil.getProjectIds(projectId);
		dto.setProjectIds(projectIds);
		dto.setMode(Constants.FIRST);
		dto.setCalendarId(calendarId);
		dto.setStatus(MutualApplyAuditStatus.AUDITED_APPROVED.status());
		dto.setStudentId(studentId);
		
		List<ElcMutualApplyVo> list = elcMutualAuditService.getOpenCollegeAuditList(dto);
		
		Set<SelectedCourse> mutualCourses = context.getSelectedMutualCourses();
		this.loadSelectedCourses(studentId, mutualCourses, calendarId, list, round);
		
		context.setSelectedMutualCourses(mutualCourses);
	}
	
	/**
	 * 获取选课对象
	 * @return
	 */
	private Integer getChooseObj(String electionObj) {
		// 选课对象(STU学生，DEPART_ADMIN教务员，MANAGER_ADMIN管理员)
		Integer chooseObj = 0;
		
		switch (electionObj) {
			case "STU":
				chooseObj = 1;
				break;
			case "DEPART_ADMIN":
				chooseObj = 2;
				break;
			case "MANAGER_ADMIN":
				chooseObj = 3;
				break;
			default:
				break;
		}
		
		return chooseObj;
	}
	
	private void loadSelectedCourses(String studentId,
	        Set<SelectedCourse> mutualCourses, Long calendarId, List<ElcMutualApplyVo> list, ElectionRounds round) {
		String name = SchoolCalendarCacheUtil.getName(calendarId);
		
		if (CollectionUtil.isNotEmpty(list))
        {
            List<Long> teachClassIds = list.stream().map(temp -> Long.parseLong(temp.getTeachingClassId())).collect(Collectors.toList());
            LOG.info("--------------teachClassIds----------------:" + teachClassIds);
            Map<Long, List<ClassTimeUnit>> collect = groupByTime(teachClassIds);
            for (ElcMutualApplyVo vo : list)
            {
            	String teacherNameAndCode = "";
            	
                SelectedCourse course = new SelectedCourse();
                TeachingClassCache lesson = new TeachingClassCache();
                lesson.setCalendarName(name);
                lesson.setNature(vo.getNature());
//                lesson.setApply(vo.getApply());
                lesson.setCampus(vo.getCampus());
                lesson.setCourseCode(vo.getCourseCode());
                lesson.setCourseName(vo.getCourseName());
                lesson.setCredits(vo.getCredits());
                lesson.setCalendarId(vo.getCalendarId());
//                lesson.setPublicElec(
//                		vo.getIsPublicCourse() == Constants.ZERO ? false : true);
                lesson.setTeachClassId(Long.parseLong(vo.getTeachingClassId()));
                lesson.setTeachClassCode(vo.getTeachingClassCode());
                lesson.setFaculty(vo.getFaculty());
                lesson.setTerm(vo.getTerm());
                lesson.setCompulsory("");
                List<ClassTimeUnit> times = this.concatTime(collect, lesson);
                lesson.setTimes(times);
                for (ClassTimeUnit time : times) {
                	teacherNameAndCode = teacherNameAndCode + getTeacherInfo(time.getTeacherCode()) + ",";
                }
                LOG.info("--------------teacherNameAndCode----------------:" + teacherNameAndCode);
                lesson.setTeacherNameAndCode(teacherNameAndCode.substring(0, teacherNameAndCode.length()-1));

                course.setCourse(lesson);
//                course.setLabel(vo.getLabel());
                course.setChooseObj(getChooseObj(round.getElectionObj()));
//                course.setCourseTakeType(vo.getCourseTakeType());
                course.setAssessmentMode(vo.getAssessmentMode());
                course.setTurn(round.getTurn());
                mutualCourses.add(course);
            }
        }
		
		
	}
	
    /**
     * 查询教学班排课时间，并按教学班分组
     * 
     * @param teachClassIds
     * @return
     * @see [类、类#方法、类#成员]
     */
    public Map<Long, List<ClassTimeUnit>> groupByTime(List<Long> teachClassIds)
    {
        Map<Long, List<ClassTimeUnit>> map = new HashMap<>();
        List<TeacherClassTimeRoom> list = new ArrayList<>();
        //按周数拆分的选课数据集合
        if(CollectionUtil.isNotEmpty(teachClassIds)) {
        	list = classDao.getClassTimes(teachClassIds);
        }
        if (CollectionUtil.isEmpty(list))
        {
            return map;
        }
        //一个教学班分组
        Map<Long, List<TeacherClassTimeRoom>> classTimeMap = list.stream()
            .collect(
                Collectors.groupingBy(TeacherClassTimeRoom::getTeachClassId));
        
        for (Entry<Long, List<TeacherClassTimeRoom>> entry : classTimeMap
            .entrySet())
        {
            List<TeacherClassTimeRoom> ls = entry.getValue();
            if (CollectionUtil.isEmpty(ls))
            {
                continue;
            }
            List<ClassTimeUnit> times = new ArrayList<>();
            // time->room
            Map<Long, List<TeacherClassTimeRoom>> timeRoomMap = ls.stream()
                .collect(Collectors
                    .groupingBy(TeacherClassTimeRoom::getArrangeTimeId));
            for (Entry<Long, List<TeacherClassTimeRoom>> entry2 : timeRoomMap
                .entrySet())
            {
                List<TeacherClassTimeRoom> rooms = entry2.getValue();
                if (CollectionUtil.isEmpty(rooms))
                {
                    continue;
                }
                ClassTimeUnit un = new ClassTimeUnit();
                TeacherClassTimeRoom room = rooms.get(0);
                un.setArrangeTimeId(room.getArrangeTimeId());
                un.setDayOfWeek(room.getDayOfWeek());
                un.setTeachClassId(room.getTeachClassId());
                un.setTimeEnd(room.getTimeEnd());
                un.setTimeStart(room.getTimeStart());
                un.setTeacherCode(room.getTeacherCode());
                un.setRoomId(room.getRoomId());
                // 所有周
                List<Integer> weeks = rooms.stream()
                    .map(TeacherClassTimeRoom::getWeekNumber)
                    .collect(Collectors.toList());
                // 相同教室相同老师的周次
                Map<String, List<TeacherClassTimeRoom>> roomTeacherMap =
                    rooms.stream().collect(Collectors.groupingBy(r -> {
                        return r.getRoomId() + "_" + r.getTeacherCode();
                    }));
                
                StringBuilder sb = new StringBuilder();
                for (Entry<String, List<TeacherClassTimeRoom>> e : roomTeacherMap
                    .entrySet())
                {
                    List<TeacherClassTimeRoom> roomTeachers = e.getValue();
                    TeacherClassTimeRoom r = roomTeachers.get(0);
                    List<Integer> roomWeeks = roomTeachers.stream()
                        .map(TeacherClassTimeRoom::getWeekNumber)
                        .collect(Collectors.toList());
                    
                    String weekStr = CalUtil.getWeeks(roomWeeks);
                    
                    String teacherNames = getTeacherInfo(r.getTeacherCode());
                    
                    String roomName =
                        ClassroomCacheUtil.getRoomName(r.getRoomId());
                    // 老师名称(老师编号)[周] 教室
                    sb.append(String
                        .format("%s[%s] %s", teacherNames, weekStr, roomName))
                        .append(" ");
                }
                Collections.sort(weeks);
                un.setValue(sb.toString());
                un.setWeeks(weeks);
                times.add(un);
            }
            
            map.put(entry.getKey(), times);
        }
        
        return map;
    }
    
    private String getTeacherInfo(String teacherCode)
    {
        if (StringUtils.isEmpty(teacherCode))
        {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        String[] codes = teacherCode.split(",");
        List<String> names = TeacherCacheUtil.getNames(codes);
        if (CollectionUtil.isNotEmpty(names))
        {
            for (int i = 0; i < codes.length; i++)
            {
                String tCode = codes[i];
                String tName = names.get(i);
                // 老师名称(老师编号)
                sb.append(String.format("%s(%s) ", tName, tCode));
            }
        }
        return sb.toString();
    }
    
    /**
     * 拼接上课时间
     * 
     * @param collect
     * @param teacherMap
     * @param c
     * @return
     * @see [类、类#方法、类#成员]
     */
    public List<ClassTimeUnit> concatTime(
        Map<Long, List<ClassTimeUnit>> collect, TeachingClassCache c)
    {
        //一个教学班的排课时间信息
    	Long teachClassId = c.getTeachClassId();
        List<ClassTimeUnit> times = collect.get(teachClassId);
        String teacherName = null;
        if (CollectionUtil.isNotEmpty(times))
        {
            for (ClassTimeUnit ctu : times)
            {
                ctu.setValue(String.format("%s(%s) %s",
                    c.getCourseName(),
                    c.getCourseCode(),
                    ctu.getValue()));
            }
            
            teacherName = this.getTeacherName(times);
            
            c.setTeacherName(teacherName);
            
            return times;
        }else{
        	List<String> findNamesByTeachingClassId = teacherDao.findNamesByTeachingClassId(teachClassId);
        	Set<String> names = new HashSet<>(findNamesByTeachingClassId);
        	teacherName = StringUtils.join(names, ",");
        	c.setTeacherName(teacherName);
        }
        
        return null;
    }
    
    private String getTeacherName(List<ClassTimeUnit> times)
    {
        String tName = null;
        if (CollectionUtil.isNotEmpty(times))
        {
            List<String> teacherSet = new ArrayList<>(times.stream()
                .map(ClassTimeUnit::getTeacherCode)
                .collect(Collectors.toSet()));
            if (CollectionUtil.isNotEmpty(teacherSet))
            {
                String str = StringUtils.join(teacherSet, ",");
                List<String> nameList = new ArrayList<>();
                Collections.addAll(nameList, str.split(","));
                Set<String> tnames = new HashSet<>(nameList);
                List<Teacher> teachers = TeacherCacheUtil
                    .getTeachers(tnames.toArray(new String[] {}));
                if (CollectionUtil.isNotEmpty(teachers))
                {
                    List<String> names = teachers.stream().map(t -> {
                        if (t == null)
                            return "";
                        return String
                            .format("%s", t.getName());
                    }).collect(Collectors.toList());
                    
                    tName = StringUtils.join(names, ",");
                }
            }
        }
        return tName;
    }
}


