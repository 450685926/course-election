package com.server.edu.election.service.impl;

import static java.util.stream.Collectors.toSet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.server.edu.common.PageCondition;
import com.server.edu.common.rest.PageResult;
import com.server.edu.election.constants.Constants;
import com.server.edu.election.dao.ElcAffinityCoursesStdsDao;
import com.server.edu.election.dao.ElcCourseSuggestSwitchDao;
import com.server.edu.election.dao.ElcCourseTakeDao;
import com.server.edu.election.dao.ElcInvincibleStdsDao;
import com.server.edu.election.dao.ElcResultCountDao;
import com.server.edu.election.dao.StudentDao;
import com.server.edu.election.dao.TeachingClassDao;
import com.server.edu.election.dao.TeachingClassElectiveRestrictAttrDao;
import com.server.edu.election.dao.TeachingClassTeacherDao;
import com.server.edu.election.dto.AutoRemoveDto;
import com.server.edu.election.dto.ClassTeacherDto;
import com.server.edu.election.dto.ElcResultDto;
import com.server.edu.election.dto.Student4Elc;
import com.server.edu.election.dto.SuggestProfessionDto;
import com.server.edu.election.entity.ElcAffinityCoursesStds;
import com.server.edu.election.entity.ElcCourseSuggestSwitch;
import com.server.edu.election.entity.ElcCourseTake;
import com.server.edu.election.entity.Student;
import com.server.edu.election.entity.TeachingClass;
import com.server.edu.election.entity.TeachingClassTeacher;
import com.server.edu.election.query.ElcResultQuery;
import com.server.edu.election.service.ElcCourseTakeService;
import com.server.edu.election.service.ElcResultService;
import com.server.edu.election.service.impl.resultFilter.ClassElcConditionFilter;
import com.server.edu.election.service.impl.resultFilter.GradAndPreFilter;
import com.server.edu.election.studentelec.context.TimeAndRoom;
import com.server.edu.election.vo.ElcResultCountVo;
import com.server.edu.election.vo.TeachingClassVo;
import com.server.edu.util.CalUtil;
import com.server.edu.util.CollectionUtil;

import tk.mybatis.mapper.entity.Example;

@Service
public class ElcResultServiceImpl implements ElcResultService
{
    Logger logger = LoggerFactory.getLogger(getClass());
    
    @Autowired
    private TeachingClassDao classDao;
    
    @Autowired
    private ElcCourseTakeDao courseTakeDao;
    
    @Autowired
    private ElcInvincibleStdsDao invincibleStdsDao;
    
    @Autowired
    private ElcAffinityCoursesStdsDao affinityCoursesStdsDao;
    
    @Autowired
    private TeachingClassElectiveRestrictAttrDao classElectiveRestrictAttrDao;
    
    @Autowired
    private StudentDao studentDao;
    
    @Autowired
    private ElcCourseTakeService courseTakeService;
    
    @Autowired
    private TeachingClassTeacherDao teacherDao;
    
    @Autowired
    private ElcCourseSuggestSwitchDao elcCourseSuggestSwitchDao;
    
    @Autowired
    private ElcResultCountDao elcResultCountDao;
    
    @Override
    public PageResult<TeachingClassVo> listPage(
        PageCondition<ElcResultQuery> page)
    {
        PageHelper.startPage(page.getPageNum_(), page.getPageSize_());
        ElcResultQuery condition = page.getCondition();
        Page<TeachingClassVo> listPage = new Page<TeachingClassVo>();
        if (StringUtils.equals(condition.getProjectId(), "1")) {
        	listPage = classDao.listPage(page.getCondition());
		}else {
			listPage = classDao.grduateListPage(page.getCondition());
		}
        
        List<TeachingClassVo> list = listPage.getResult();
        if(CollectionUtil.isNotEmpty(list)) {
        	List<Long>  classIds = list.stream().map(TeachingClassVo::getId).collect(Collectors.toList());
    	    // 查找任课教师信息
            Example teacherExample = new Example(TeachingClassTeacher.class);
            teacherExample.createCriteria().andIn("teachingClassId", classIds).
                    andEqualTo("type",Constants.TEACHER_DEFAULT);
            List<TeachingClassTeacher> teacherList = teacherDao.selectByExample(teacherExample);
            for(TeachingClassVo vo: list) {
            	if(CollectionUtil.isNotEmpty(teacherList)) {
                	List<TeachingClassTeacher> teachers = teacherList.stream().filter(c->vo.getId().equals(c.getTeachingClassId())).collect(Collectors.toList());
                	StringBuilder stringBuilder = new StringBuilder();
                	if(CollectionUtil.isNotEmpty(teachers)) {
                		for(TeachingClassTeacher teacher:teachers) {
                			stringBuilder.append(teacher.getTeacherName());
                			stringBuilder.append("(");
                			stringBuilder.append(teacher.getTeacherCode());
                			stringBuilder.append(")");
                			stringBuilder.append(",");
                		}
                		vo.setTeacherName(stringBuilder.deleteCharAt(stringBuilder.length()-1).toString());
                	}
            	}
            }
            // 处理教学安排（上课时间地点）信息
            getTimeList(list);
        }
        return new PageResult<>(listPage);
    }
    
	private List<TeachingClassVo>  getTimeList(List<TeachingClassVo> list){
		if(CollectionUtil.isNotEmpty(list)){
			for (TeachingClassVo teachingClassVo : list) {
				List<TimeAndRoom> tableMessages = getTimeById(teachingClassVo.getId());
				teachingClassVo.setTimeTableList(tableMessages);
			}
		}
		return list;
	}
	
	private List<TimeAndRoom>  getTimeById(Long teachingClassId){
        List<TimeAndRoom> list=new ArrayList<>();
        List<ClassTeacherDto> classTimeAndRoom = courseTakeDao.findClassTimeAndRoomStr(teachingClassId);
        if(CollectionUtil.isNotEmpty(classTimeAndRoom)){
            for (ClassTeacherDto classTeacherDto : classTimeAndRoom) {
            	TimeAndRoom time=new TimeAndRoom();
                Integer dayOfWeek = classTeacherDto.getDayOfWeek();
                Integer timeStart = classTeacherDto.getTimeStart();
                Integer timeEnd = classTeacherDto.getTimeEnd();
                String roomID = classTeacherDto.getRoomID();
                String weekNumber = classTeacherDto.getWeekNumberStr();
                Long timeId = classTeacherDto.getTimeId();
                String[] str = weekNumber.split(",");
                
                List<Integer> weeks = Arrays.asList(str).stream().map(Integer::parseInt).collect(Collectors.toList());
                List<String> weekNums = CalUtil.getWeekNums(weeks.toArray(new Integer[] {}));
                String weekNumStr = weekNums.toString();//周次
                String weekstr = findWeek(dayOfWeek);//星期
                String timeStr=weekstr+" "+timeStart+"-"+timeEnd+" "+weekNumStr+" ";
                time.setTimeId(timeId);
                time.setTimeAndRoom(timeStr);
                time.setRoomId(roomID);
                list.add(time);
            }
        }
        return list;
    }
	
   /**
   *  @Description: 星期
   */
	private String findWeek(Integer number){
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
    
    @Override
    public void adjustClassNumber(TeachingClass teachingClass)
    {
        TeachingClass record = new TeachingClass();
        record.setId(teachingClass.getId());
        record.setNumber(teachingClass.getNumber());
        classDao.updateByPrimaryKeySelective(record);
    }
    
    String key(SuggestProfessionDto dto)
    {
        return dto.getGrade() + "-" + dto.getProfession();
    }
    
    @Override
    public void autoRemove(AutoRemoveDto dto)
    {
        Long teachingClassId = dto.getTeachingClassId();
        TeachingClass teachingClass = classDao.selectOversize(teachingClassId);
        if (null != teachingClass)
        {
            ElcCourseTake param = new ElcCourseTake();
            param.setTeachingClassId(teachingClassId);
            List<ElcCourseTake> takes = courseTakeDao.select(param);
            String course = takes.get(0).getCourseCode();
        	if(Boolean.TRUE.equals(dto.getSuggestSwitchCourse())) {
        		List<ElcCourseSuggestSwitch> suggestSwitchs = elcCourseSuggestSwitchDao.selectAll();
        		if(CollectionUtil.isNotEmpty(suggestSwitchs)) {
        			List<String> suggestCoures = suggestSwitchs.stream().map(ElcCourseSuggestSwitch::getCourseCode).collect(Collectors.toList());
        			if(!suggestCoures.contains(course)) {
        				return;
        			}
        		}
        	}
            // 特殊学生
            List<String> invincibleStdIds =
                invincibleStdsDao.selectAllStudentId();
            // 优先学生
            List<ElcAffinityCoursesStds> affinityCoursesStds =
                affinityCoursesStdsDao.selectAll();
            Set<String> affinityCoursesStdSet = affinityCoursesStds.stream()
                .map(aff -> aff.getCourseId() + "-" + aff.getStudentId())
                .collect(toSet());
            
            List<Student> normalStus = new ArrayList<>();
            List<Student> invincibleStus = new ArrayList<>();
            List<Student> affinityStus = new ArrayList<>();
            //把学生分类(普通学生、优先学生、特殊学生)
            for (ElcCourseTake take : takes)
            {
                String courseCode = take.getCourseCode();
                String studentId = take.getStudentId();
                Student stu = studentDao.findStudentByCode(studentId);
                if (invincibleStdIds.contains(studentId))
                {
                    invincibleStus.add(stu);
                }
                else if (affinityCoursesStdSet
                    .contains(courseCode + "-" + studentId))
                {
                    affinityStus.add(stu);
                }
                else
                {
                    normalStus.add(stu);
                }
            }
            
            if (!Boolean.TRUE.equals(dto.getInvincibleStu()))
            {
                invincibleStus.clear();
            }
            if (!Boolean.TRUE.equals(dto.getAffinityStu()))
            {
                affinityStus.clear();
            }
            
            List<String> removeStus = new ArrayList<>();
            if (invincibleStus.size() + affinityStus.size()
                + normalStus.size() > teachingClass.getNumber())
            {
                GradAndPreFilter gradAndPreFilter =
                    new GradAndPreFilter(dto, classDao);
                gradAndPreFilter.init();
                ClassElcConditionFilter elcConditionFilter =
                    new ClassElcConditionFilter(dto,
                        classElectiveRestrictAttrDao);
                elcConditionFilter.init();
                
                // 这里做三次的原因是因为有三种学生类型
                for (int i = 0; i < 3; i++)
                {
                    List<Student> stuList = normalStus.size() > 0 ? normalStus
                        : (affinityStus.size() > 0 ? affinityStus
                            : invincibleStus);
                    
                    if (CollectionUtil.isEmpty(stuList))
                    {
                        continue;
                    }
                    gradAndPreFilter.execute(stuList, removeStus);
                    elcConditionFilter.execute(stuList, removeStus);
                    
                    if (CollectionUtil.isEmpty(stuList))
                    {
                        //执行完后人数还是超过上限则进行随机删除
                        int overSize = (invincibleStus.size()
                            + affinityStus.size() + normalStus.size())
                            - teachingClass.getNumber();
                        if (overSize > 0)
                        {
                            // 1.普通人数小于超出人数则说明优先人数的人也超了这时需要清空普通人数
                            // 2.普通人数大于超出人数则说明只有普通人数超了，普通人数需要等于超出人数
                            int limitNumber = overSize;
                            if (stuList.size() < overSize)
                            {
                                limitNumber = 0;
                            }
                            GradAndPreFilter
                                .randomRemove(removeStus, limitNumber, stuList);
                        }
                    }
                }
                
                removeAndRecordLog(dto,
                    teachingClassId,
                    teachingClass,
                    removeStus);
            }
        }
    }
    
    public void removeAndRecordLog(AutoRemoveDto dto, Long teachingClassId,
        TeachingClass teachingClass, List<String> removeStus)
    {
        if (CollectionUtil.isNotEmpty(removeStus))
        {
            Long calendarId = dto.getCalendarId();
            List<ElcCourseTake> values = new ArrayList<>();
            for (String studentId : removeStus)
            {
                ElcCourseTake t = new ElcCourseTake();
                t.setCalendarId(calendarId);
                t.setStudentId(studentId);
                t.setTeachingClassId(teachingClassId);
                values.add(t);
            }
            courseTakeService.withdraw(values);
        }
    }

    /**
     * 选课结果统计
     */
	@Override
	public ElcResultCountVo elcResultCountByStudent(PageCondition<ElcResultQuery> page) {
		//从学生维度查询
		//根据条件查出满足条件的学生分类（年级、培养层次、培养类别、学位类型、学习形式）
		PageHelper.startPage(page.getPageNum_(), page.getPageSize_());
		ElcResultQuery condition = page.getCondition();
		ElcResultCountVo elcResultCountVo = new ElcResultCountVo();
		if(condition.getDimension().intValue() == Constants.ONE){
			condition.setManagerDeptId(Constants.PROJ_UNGRADUATE);
			Page<ElcResultDto>  elcResultList = elcResultCountDao.getElcResult(condition);
			Integer elcNumber = 0;
			for (ElcResultDto elcResultDto : elcResultList) {
				
				//该年级、培养层次、培养类别、学位类型、学习形式查询条件
				ElcResultQuery query = new ElcResultQuery();
				if (StringUtils.isNotEmpty(condition.getGrade())) {
					query.setGrade(condition.getGrade());
				}else{
					elcResultDto.setGrade("全部");
				}
				query.setFaculty(condition.getFaculty());
				query.setEnrolSeason(condition.getEnrolSeason());
				query.setDegreeType(elcResultDto.getDegreeType());
				query.setFormLearning(elcResultDto.getFormLearning());
				query.setTrainingCategory(elcResultDto.getTrainingCategory());
				query.setTrainingLevel(elcResultDto.getTrainingLevel());
				query.setCalendarId(condition.getCalendarId());
				//根据条件查询查询已将选课学生人数
				Integer numberOfelectedPersons = elcResultCountDao.getNumberOfelectedPersons(query);
				elcNumber += numberOfelectedPersons;
				elcResultDto.setNumberOfelectedPersons(numberOfelectedPersons);
				elcResultDto.setNumberOfelectedPersonsPoint(Double.parseDouble((numberOfelectedPersons/elcResultDto.getStudentNum() + "")));
				elcResultDto.setNumberOfNonCandidates(elcResultDto.getStudentNum() - numberOfelectedPersons);
			}
			Integer elcGateMumber = elcResultCountDao.getElcGateMumber(condition);
			Integer elcPersonTime = elcResultCountDao.getElcPersonTime(condition);
			elcResultCountVo.setElcNumberByStudent(elcNumber);
			elcResultCountVo.setElceResultByStudent(new PageResult<>(elcResultList));
			elcResultCountVo.setElcGateMumberByStudent(elcGateMumber);
			elcResultCountVo.setElcPersonTimeByStudent(elcPersonTime);
		}else{
			condition.setManagerDeptId(Constants.PROJ_UNGRADUATE);
			//从学院维度查询
			Page<ElcResultDto> eleResultByFacultyList = elcResultCountDao.getElcResultByFacult(condition);
			Integer elcNumberByFaculty = 0;
			for (ElcResultDto elcResultDto : eleResultByFacultyList) {
				//该学院该专业查询条件
				ElcResultQuery query = new ElcResultQuery();
				if (StringUtils.isNotEmpty(condition.getGrade())) {
					query.setGrade(condition.getGrade());
				}else{
					elcResultDto.setGrade("全部");
				}
				query.setFaculty(elcResultDto.getFaculty());
				query.setEnrolSeason(condition.getEnrolSeason());
				query.setProfession(elcResultDto.getProfession());
				query.setDegreeType(condition.getDegreeType());
				query.setFormLearning(condition.getFormLearning());
				query.setTrainingCategory(condition.getTrainingCategory());
				query.setTrainingLevel(condition.getTrainingLevel());
				query.setCalendarId(condition.getCalendarId());
				
				//根据条件查询查询已将选课学生人数
				Integer numberOfelectedPersons = elcResultCountDao.getNumberOfelectedPersonsByFaculty(query);
				elcNumberByFaculty += numberOfelectedPersons;
				elcResultDto.setNumberOfelectedPersons(numberOfelectedPersons);
				elcResultDto.setNumberOfelectedPersonsPoint(Double.parseDouble((numberOfelectedPersons/elcResultDto.getStudentNum() + "")));
				elcResultDto.setNumberOfNonCandidates(elcResultDto.getStudentNum() - numberOfelectedPersons);
			}
			Integer elcGateMumberByFaculty = elcResultCountDao.getElcGateMumberByFaculty(condition);
			Integer elcPersonTimeByFaculty = elcResultCountDao.getElcPersonTimeByFaculty(condition);
			elcResultCountVo.setElceResultByFaculty(new PageResult<>(eleResultByFacultyList));
			elcResultCountVo.setElcNumberByFaculty(elcNumberByFaculty);
			elcResultCountVo.setElcGateMumberByFaculty(elcGateMumberByFaculty);
			elcResultCountVo.setElcPersonTimeByFaculty(elcPersonTimeByFaculty);
		}
		return elcResultCountVo;
	}

	/**
	 * 未选课学生名单
	 * 
	 */
	@Override
	public PageResult<Student4Elc> getStudentPage(PageCondition<ElcResultQuery> page ) {
		//查询该条件下未选课学生名单
		PageHelper.startPage(page.getPageNum_(), page.getPageSize_());
		Page<Student4Elc> result = studentDao.getAllNonSelectedCourseStudent(page.getCondition());
		return new PageResult<>(result);
	}
    
}
