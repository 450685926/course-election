package com.server.edu.election.service.impl;

import static java.util.stream.Collectors.toSet;

import java.util.*;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.ibm.icu.math.BigDecimal;
import com.server.edu.common.PageCondition;
import com.server.edu.common.entity.ClassroomN;
import com.server.edu.common.entity.Teacher;
import com.server.edu.common.enums.GroupDataEnum;
import com.server.edu.common.locale.I18nUtil;
import com.server.edu.common.rest.PageResult;
import com.server.edu.common.rest.RestResult;
import com.server.edu.common.validator.Assert;
import com.server.edu.dictionary.service.DictionaryService;
import com.server.edu.dictionary.utils.ClassroomCacheUtil;
import com.server.edu.dictionary.utils.SpringUtils;
import com.server.edu.dictionary.utils.TeacherCacheUtil;
import com.server.edu.election.constants.Constants;
import com.server.edu.election.constants.RoundMode;
import com.server.edu.election.dao.ElcAffinityCoursesStdsDao;
import com.server.edu.election.dao.ElcClassEditAuthorityDao;
import com.server.edu.election.dao.ElcCourseSuggestSwitchDao;
import com.server.edu.election.dao.ElcCourseTakeDao;
import com.server.edu.election.dao.ElcInvincibleStdsDao;
import com.server.edu.election.dao.ElcResultCountDao;
import com.server.edu.election.dao.ElcScreeningLabelDao;
import com.server.edu.election.dao.ElcTeachingClassBindDao;
import com.server.edu.election.dao.ElecRoundsDao;
import com.server.edu.election.dao.ElectionConstantsDao;
import com.server.edu.election.dao.RebuildCourseRecycleDao;
import com.server.edu.election.dao.StudentDao;
import com.server.edu.election.dao.TeachingClassDao;
import com.server.edu.election.dao.TeachingClassElectiveRestrictAttrDao;
import com.server.edu.election.dao.TeachingClassElectiveRestrictProfessionDao;
import com.server.edu.election.dao.TeachingClassSuggestStudentDao;
import com.server.edu.election.dao.TeachingClassTeacherDao;
import com.server.edu.election.dto.AutoRemoveDto;
import com.server.edu.election.dto.BatchAutoRemoveDto;
import com.server.edu.election.dto.ClassTeacherDto;
import com.server.edu.election.dto.ElcCourseTakeAddDto;
import com.server.edu.election.dto.ElcResultDto;
import com.server.edu.election.dto.ReserveDto;
import com.server.edu.election.dto.Student4Elc;
import com.server.edu.election.dto.SuggestProfessionDto;
import com.server.edu.election.entity.ElcClassEditAuthority;
import com.server.edu.election.entity.ElcCourseSuggestSwitch;
import com.server.edu.election.entity.ElcCourseTake;
import com.server.edu.election.entity.ElcScreeningLabel;
import com.server.edu.election.entity.ElcTeachingClassBind;
import com.server.edu.election.entity.ElectionRounds;
import com.server.edu.election.entity.RebuildCourseRecycle;
import com.server.edu.election.entity.Student;
import com.server.edu.election.entity.TeachingClass;
import com.server.edu.election.entity.TeachingClassChange;
import com.server.edu.election.entity.TeachingClassElectiveRestrictAttr;
import com.server.edu.election.entity.TeachingClassElectiveRestrictProfession;
import com.server.edu.election.entity.TeachingClassTeacher;
import com.server.edu.election.query.ElcResultQuery;
import com.server.edu.election.service.ElcCourseTakeService;
import com.server.edu.election.service.ElcResultService;
import com.server.edu.election.service.impl.resultFilter.ClassElcConditionFilter;
import com.server.edu.election.service.impl.resultFilter.GradAndPreFilter;
import com.server.edu.election.service.impl.resultFilter.NewClassElcConditionFilter;
import com.server.edu.election.service.impl.resultFilter.NewGradAndPreFilter;
import com.server.edu.election.studentelec.cache.TeachingClassCache;
import com.server.edu.election.studentelec.context.TimeAndRoom;
import com.server.edu.election.studentelec.service.cache.RoundCacheService;
import com.server.edu.election.studentelec.service.cache.TeachClassCacheService;
import com.server.edu.election.util.ExcelStoreConfig;
import com.server.edu.election.util.TableIndexUtil;
import com.server.edu.election.vo.ElcAffinityCoursesStdsVo;
import com.server.edu.election.vo.ElcResultCountVo;
import com.server.edu.election.vo.RestrictStudent;
import com.server.edu.election.vo.SuggestCourseVo;
import com.server.edu.election.vo.TeachingClassLimitVo;
import com.server.edu.election.vo.TeachingClassVo;
import com.server.edu.exception.ParameterValidateException;
import com.server.edu.session.util.SessionUtils;
import com.server.edu.session.util.entity.Session;
import com.server.edu.util.CalUtil;
import com.server.edu.util.CollectionUtil;
import com.server.edu.util.async.AsyncExecuter;
import com.server.edu.util.async.AsyncProcessUtil;
import com.server.edu.util.async.AsyncResult;
import com.server.edu.util.excel.GeneralExcelDesigner;
import com.server.edu.util.excel.export.ExcelExecuter;
import com.server.edu.util.excel.export.ExcelResult;
import com.server.edu.util.excel.export.ExportExcelUtils;
import com.server.edu.welcomeservice.util.ExcelEntityExport;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import tk.mybatis.mapper.entity.Example;

@Service
public class ElcResultServiceImpl implements ElcResultService
{
    Logger logger = LoggerFactory.getLogger(ElcResultServiceImpl.class);
    
    @Autowired
    private TeachingClassDao classDao;
    
    @Autowired
    private ElcCourseTakeDao courseTakeDao;
    
    @Autowired
    private ElcInvincibleStdsDao invincibleStdsDao;
    
    @Autowired
    private ElcAffinityCoursesStdsDao affinityCoursesStdsDao;

    @Autowired
    private TeachingClassSuggestStudentDao suggestStudentDao;

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
    
    @Autowired
    private ElcClassEditAuthorityDao elcClassEditAuthorityDao;
    
    @Autowired
    private ExcelStoreConfig excelStoreConfig;
    
    @Autowired
    private ElcCourseTakeService elcCourseTakeService;
    
    @Autowired
    private ElcTeachingClassBindDao elcTeachingClassBindDao;
    
    @Autowired
    private ElecRoundsDao electionRoundsDao;
    
    @Autowired
    // 文件缓存目录
    @Value("${task.cache.directory}")
    private String cacheDirectory;
    
    @Autowired
    private TeachingClassElectiveRestrictAttrDao attrDao;

    @Autowired
    private TeachingClassElectiveRestrictProfessionDao professionDao;
    
    @Autowired
    private ElcScreeningLabelDao elcScreeningLabelDao;
    
    @Autowired
    private ElcTeachingClassBindDao bindDao;
    
    @Autowired
    private TeachingClassDao teachingClassDao;
    
    @Autowired
    private ElectionConstantsDao constantsDao;

    @Autowired
    private TeachClassCacheService teachClassCacheService;
    
    @Autowired
    private DictionaryService dictionaryService;
    
    @Autowired
    private RebuildCourseRecycleDao rebuildCourseRecycleDao;
    
    @Autowired
    private RoundCacheService roundCacheService;
    
    @Autowired
    private ElecRoundsDao elecRoundsDao;
    
    @Autowired
    private TeachingClassElectiveRestrictAttrDao restrictAttrDao;
    
    @Override
    public PageResult<TeachingClassVo> listPage(
        PageCondition<ElcResultQuery> page)
    {
    	ElcResultQuery condition = page.getCondition();
    	
    	Session session = SessionUtils.getCurrentSession();
    	//通过session信息获取访问接口人员角色
    	if (StringUtils.equals(session.getCurrentRole(), "1") && !session.isAdmin() && session.isAcdemicDean()) {
            if (StringUtils.isBlank(condition.getFaculty())) {
                List<String> deptIds = SessionUtils.getCurrentSession().getGroupData().get(GroupDataEnum.department.getValue());
                condition.setFaculties(deptIds);
            }
		}
        Page<TeachingClassVo> listPage = null;
        listPage = getListPage(condition, listPage, page);
        List<TeachingClassVo> list = listPage.getResult();
        if(CollectionUtil.isNotEmpty(list)) {
    		// 添加教室容量
    		Set<String> roomIds = list.stream().filter(teachingClassVo->StringUtils.isNotBlank(teachingClassVo.getRoomId())).map(TeachingClassVo::getRoomId).collect(toSet());
    		List<ClassroomN> classroomList = ClassroomCacheUtil.getList(roomIds);
            for(TeachingClassVo vo: list) {
        	    //拼装教师
                getTeacgerName(vo);
                //获得男女比例
            	getProportion(condition, vo);
            	vo.setClassNumberStr("不限");
            	if(CollectionUtil.isNotEmpty(classroomList) && StringUtils.isNotBlank(vo.getRoomId())) {
            		ClassroomN classroom = classroomList.stream().filter(c->c!=null).filter(c->c.getId()!=null).filter(c->vo.getRoomId().equals(c.getId().toString())).findFirst().orElse(null);
    				if(classroom!=null && classroom.getClassCapacity()!=null) {
    					vo.setClassNumberStr(String.valueOf(classroom.getClassCapacity()));
    				}
    			}
            	// 处理教学安排（上课时间地点）信息
				List<TimeAndRoom> tableMessages = getTimeById(vo.getId());
				vo.setTimeTableList(tableMessages);
				String timeAndRoom = "";
				for (TimeAndRoom tAndR : tableMessages) {
                    timeAndRoom = timeAndRoom + tAndR.getTimeAndRoom();
					if (StringUtils.isNotEmpty(tAndR.getRoomId())) {
						ClassroomN classroom = ClassroomCacheUtil.getClassroom(tAndR.getRoomId());
						if (classroom != null) {
							timeAndRoom = "/" + classroom.getName()+" ";
						}
					}else {
                        timeAndRoom = timeAndRoom + " ";
                    }
				}
				vo.setTimeAndRoom(timeAndRoom);
            }
        }
        return new PageResult<>(listPage);
    }

    @Override
    public PageResult<TeachingClassVo> listPageTj(
            PageCondition<ElcResultQuery> page)
    {
        ElcResultQuery condition = page.getCondition();
        Session session = SessionUtils.getCurrentSession();
        //通过session信息获取访问接口人员角色
        if (StringUtils.equals(session.getCurrentRole(), "1") && !session.isAdmin() && session.isAcdemicDean()) {
            if (StringUtils.isBlank(condition.getFaculty())) {
                List<String> deptIds = SessionUtils.getCurrentSession().getGroupData().get(GroupDataEnum.department.getValue());
                condition.setFaculties(deptIds);
            }
        }
        Page<TeachingClassVo> listPage = null;
        if (StringUtils.equals(condition.getProjectId(), Constants.PROJ_UNGRADUATE)) {
            PageHelper.startPage(page.getPageNum_(), page.getPageSize_());
            listPage = classDao.listPage(condition);
        }
        List<TeachingClassVo> list = listPage.getResult();
        if(CollectionUtil.isNotEmpty(list)) {
            List<Long> collect = list.stream().map(TeachingClassVo::getId).collect(Collectors.toList());
            List<ClassTeacherDto> classTimeAndRoom = courseTakeDao.findClassTimeAndRoom(collect);
            MultiValueMap<Long, TimeAndRoom> multiValueMap = new LinkedMultiValueMap<>(50);
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
                    multiValueMap.add(classTeacherDto.getTeachingClassId(), time);
                }
            }
            // 添加教室容量
            Set<String> roomIds = list.stream().filter(teachingClassVo->StringUtils.isNotBlank(teachingClassVo.getRoomId())).map(TeachingClassVo::getRoomId).collect(toSet());
            List<ClassroomN> classroomList = ClassroomCacheUtil.getList(roomIds);
            for(TeachingClassVo vo: list) {
                Long id = vo.getId();
                TeachingClassVo teachingClassVo = classDao.bindClass(id);
                if (teachingClassVo != null) {
                    String tId;
                    Long voId = teachingClassVo.getId();
                    if (voId.longValue() == id.longValue()) {
                        tId = teachingClassVo.getBindClassId();
                    } else {
                        tId = voId + "";
                    }
                    vo.setBindClassId(tId);
                    String s = classDao.classCode(tId);
                    vo.setBindClassCode(s);
                }
                //拼装教师
                getTeacgerName(vo);
                //获得男女比例
                getProportion(condition, vo);
                vo.setClassNumberStr("不限");
                if(CollectionUtil.isNotEmpty(classroomList) && StringUtils.isNotBlank(vo.getRoomId())) {
                    ClassroomN classroom = classroomList.stream().filter(c->c!=null).filter(c->c.getId()!=null).filter(c->vo.getRoomId().equals(c.getId().toString())).findFirst().orElse(null);
                    if(classroom!=null && classroom.getClassCapacity()!=null) {
                        vo.setClassNumberStr(String.valueOf(classroom.getClassCapacity()));
                    }
                }
                // 处理教学安排（上课时间地点）信息
                List<TimeAndRoom> tableMessages = multiValueMap.get(vo.getId());
                if (CollectionUtil.isNotEmpty(tableMessages)) {
                    vo.setTimeTableList(tableMessages);
                    Set<String> set = new HashSet<>(3);
                    for (TimeAndRoom tAndR : tableMessages) {
                        if (StringUtils.isNotEmpty(tAndR.getRoomId())) {
                            ClassroomN classroom = ClassroomCacheUtil.getClassroom(tAndR.getRoomId());
                            if (classroom != null) {
                                String name = classroom.getName();
                                if (StringUtils.isNotBlank(name)) {
                                    set.add(name);
                                }
                            }
                        }
                    }
                    vo.setTimeAndRoom(String.join(",",set));
                }
            }
        }
        return new PageResult<>(listPage);
    }

	private void getProportion(ElcResultQuery condition, TeachingClassVo vo) {
		if(condition.getIsHaveLimit() != null && Constants.ONE== condition.getIsHaveLimit().intValue()) {
            //没有设置男女比例时，页面显示 无/无
		    if(vo.getNumberMale()==null||vo.getNumberFemale()==null) {
                vo.setProportion("无/无");
                return;
            }
            //有设置男女比例时
			int numberMale =vo.getNumberMale();
            int numberFemale = vo.getNumberFemale();

            if(numberMale==0){
                numberFemale = 1;
            }
            else if(numberFemale==0){
                numberMale = 1;
            }
            else if(numberMale % numberFemale == 0){
                numberMale = numberMale / numberFemale ;
                numberFemale =1;
            }
            //如果女比例可以被男比例除尽
            else if(numberFemale % numberMale == 0){
                numberFemale = numberFemale / numberMale ;
                numberMale = 1;
            }
            vo.setNumberFemale(numberFemale);
            vo.setNumberMale(numberMale);
			vo.setProportion(String.valueOf(numberMale) +"/" +String.valueOf(numberFemale));
		}

	}

	private void getTeacgerName(TeachingClassVo vo) {
		if(StringUtils.isNotBlank(vo.getTeacherCodes())) {
			String[] codes = vo.getTeacherCodes().split(",");
			List<Teacher> teachers = new ArrayList<Teacher>();
			for (String code : codes) {
				teachers.addAll(TeacherCacheUtil.getTeachers(code));
			}
		     	if(CollectionUtil.isNotEmpty(teachers)) {
		     		StringBuilder stringBuilder = new StringBuilder();
		    		for(Teacher teacher:teachers) {
		    			if(teacher!=null) {
			    			stringBuilder.append(teacher.getName());
			    			stringBuilder.append("(");
			    			stringBuilder.append(teacher.getCode());
			    			stringBuilder.append(")");
			    			stringBuilder.append(",");
		    			}
		    		}
		    		if(stringBuilder.length()>Constants.ZERO) {
			    		vo.setTeacherName(stringBuilder.deleteCharAt(stringBuilder.length()-1).toString());
		    		}
				}
		    }
	}

	private Page<TeachingClassVo> getListPage(ElcResultQuery condition, Page<TeachingClassVo> listPage, PageCondition<ElcResultQuery> page) {
		if (StringUtils.equals(condition.getProjectId(), Constants.PROJ_UNGRADUATE)) {
        	if(Constants.IS.equals(condition.getIsScreening())) {
                PageHelper.startPage(page.getPageNum_(), page.getPageSize_());
        		listPage = classDao.listScreeningPage(condition);
        		if (CollectionUtil.isNotEmpty(listPage)) {
                    List<Long> ids = listPage.stream().map(TeachingClassVo::getId).collect(Collectors.toList());
                    int index = TableIndexUtil.getIndex(condition.getCalendarId());
                    List<TeachingClassVo> selCount = courseTakeDao.findSelCount(index, ids);
                    Map<Long, TeachingClassVo> map = selCount.stream().collect(Collectors.toMap(s -> s.getId(), s -> s));
                    for (TeachingClassVo teachingClassVo : listPage) {
                        TeachingClassVo vo = map.get(teachingClassVo.getId());
                        if (vo != null) {
                            teachingClassVo.setFirstTurnNum(vo.getFirstTurnNum());
                            teachingClassVo.setSecondTurnNum(vo.getSecondTurnNum());
                        }
                    }
                }
            }else {
                List<String> includeCodes = new ArrayList<>();
                // 1体育课
                if (Objects.equals(condition.getCourseType(), 1))
                {
                    String findPECourses = constantsDao.findPECourses();
                    if (StringUtils.isNotBlank(findPECourses))
                    {
                        includeCodes.addAll(Arrays.asList(findPECourses.split(",")));
                    }
                }
                else if (Objects.equals(condition.getCourseType(), 2))
                {// 2英语课
                    String findEnglishCourses = constantsDao.findEnglishCourses();
                    if (StringUtils.isNotBlank(findEnglishCourses))
                    {
                        includeCodes
                            .addAll(Arrays.asList(findEnglishCourses.split(",")));
                    }
                }
                condition.setIncludeCodes(includeCodes);
//                PageHelper.startPage(page.getPageNum_(), page.getPageSize_());
                if(condition.getIsHaveLimit() != null && condition.getIsHaveLimit().intValue() == Constants.ONE){
                    PageHelper.startPage(page.getPageNum_(), page.getPageSize_());
                    listPage = classDao.listPage4limit(condition);
                }else{
                    PageHelper.startPage(page.getPageNum_(), page.getPageSize_());
                    listPage = classDao.listPage(condition);
                }
			}
		}
		return listPage;
	}
    
    
    @Override
    public PageResult<TeachingClassVo> graduatePage(
        PageCondition<ElcResultQuery> page)
    {
        PageHelper.startPage(page.getPageNum_(), page.getPageSize_());
        ElcResultQuery condition = page.getCondition();
        Session session = SessionUtils.getCurrentSession();
        if (StringUtils.equals(session.getCurrentRole(), String.valueOf(Constants.ONE)) && !session.isAdmin() && session.isAcdemicDean()) {
            String faculty = condition.getFaculty();
            //如果筛选条件学院为空,则获取session中的学院;否则设置条件学院
            if(StringUtils.isEmpty(faculty)) {
                condition.setFaculties(SessionUtils.getCurrentSession().getGroupData().get(GroupDataEnum.department.getValue()));
            }else {
                condition.setFaculty(faculty);
            }
        }
        logger.info("--------alex-------the qurey parames:{}",condition.toString());
		Page<TeachingClassVo> listPage = classDao.grduateListPage(condition);
		// 添加教室容量
		List<String> roomIds = listPage.stream().filter(teachingClassVo->teachingClassVo.getRoomId()!= null).map(TeachingClassVo::getRoomId).collect(Collectors.toList());
		
		Set<String> set = new HashSet<String>(roomIds);
		roomIds.clear();
		roomIds.addAll(set);
		
        List<ClassroomN> classroomList = null;
        classroomList = CollectionUtil.isEmpty(roomIds)?ClassroomCacheUtil.getAll():ClassroomCacheUtil.getList(roomIds);
        for (TeachingClassVo teachingClassVo : listPage) {
        	teachingClassVo.setClassNumberStr("不限");
        	if(CollectionUtil.isNotEmpty(classroomList) 
        			&& StringUtils.isNotBlank(teachingClassVo.getRoomId()) 
        			&& !StringUtils.equals(teachingClassVo.getRoomId(),String.valueOf(Constants.ZERO))) {
        		ClassroomN classroom = classroomList.stream().filter(c->c!=null).filter(c->c.getId()!=null).filter(c->teachingClassVo.getRoomId().equals(c.getId().toString())).findFirst().orElse(null);
        		if(classroom!=null && classroom.getClassCapacity()!=null) {
        			teachingClassVo.setClassNumberStr(String.valueOf(classroom.getClassCapacity()));
        		}
        	}
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
    public void adjustClassNumber(TeachingClassVo teachingClassVo)
    {
    	int elcNumber = teachingClassVo.getElcNumber().intValue(); // 选课人数
    	int number = teachingClassVo.getNumber().intValue();       // 人数上限
    	
    	if (StringUtils.equals(teachingClassVo.getClassNumberStr(), "不限")) {
    		if (number < elcNumber) {
    			throw new ParameterValidateException(I18nUtil.getMsg("election.classNumber.error"));
    		}
		}else {
			int classNumber = Integer.parseInt(teachingClassVo.getClassNumberStr());  // 教室容量
			boolean flag = elcNumber <= number && number <= classNumber;
			if (!flag) {
				throw new ParameterValidateException(I18nUtil.getMsg("election.classNumber.error")); 
			}
		}
    	
    	Session session = SessionUtils.getCurrentSession();
    	if(StringUtils.equals(session.getCurrentManageDptId(), Constants.PROJ_UNGRADUATE)) {
    		Assert.notNull(teachingClassVo.getCalendarId(), "学期不能为空");
    		Example example = new Example(ElcClassEditAuthority.class);
    		Example.Criteria criteria = example.createCriteria();
    		criteria.andEqualTo("calendarId", teachingClassVo.getCalendarId());
    		criteria.andEqualTo("status", Constants.ZERO);
    		ElcClassEditAuthority editAuthority =elcClassEditAuthorityDao.selectOneByExample(example);
    		
    		if (StringUtils.equals(session.getCurrentRole(), String.valueOf(Constants.ONE)) 
    				&& !session.isAdmin() 
    				&& session.isAcdemicDean()
    				&& editAuthority!=null) {
    			throw new ParameterValidateException(I18nUtil.getMsg("election.noClassEditAuthority")); 
    		}
    	}
    	
        TeachingClass record = new TeachingClass();
        record.setId(teachingClassVo.getId());
        record.setNumber(teachingClassVo.getNumber());
        record.setReserveNumberRate((teachingClassVo.getNumber() == null|| teachingClassVo.getNumber().intValue()==0 || teachingClassVo.getReserveNumber() == null  )?0.0:new BigDecimal(teachingClassVo.getReserveNumber()).divide(new BigDecimal(teachingClassVo.getNumber()),4,BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100)).doubleValue());
        classDao.updateByPrimaryKeySelective(record);
        
        // 更新缓存中教学班人数上限
        TeachingClassCache teachingClassCache = teachClassCacheService.getTeachClassByTeachClassId(teachingClassVo.getId());
        if (teachingClassCache != null) {
        	teachingClassCache.setMaxNumber(teachingClassVo.getNumber());
        	// 实时获取选课人数
        	Integer elecNumber = teachClassCacheService.getElecNumber(teachingClassVo.getId());
        	teachingClassCache.setCurrentNumber(elecNumber);
        	teachClassCacheService.saveTeachClassCache(teachingClassVo.getId(), teachingClassCache);
		}
    }
    
    @Override
    public void setReserveNum(TeachingClass teachingClass)
    {
        TeachingClass teachingClass1 = classDao.selectByPrimaryKey(teachingClass.getId());
        Integer number = (teachingClass1.getNumber() == null ? 0 : teachingClass1.getNumber());
        Integer elcNumber = (teachingClass1.getElcNumber() == null ? 0 : teachingClass1.getElcNumber());
        if (teachingClass.getReserveNumber().intValue() + elcNumber.intValue() > number.intValue()){
            throw new ParameterValidateException(I18nUtil.getMsg("election.ReserveNum.error",""));
        }
        TeachingClass record = new TeachingClass();
        record.setId(teachingClass.getId());
        record.setReserveNumber(teachingClass.getReserveNumber());
        record.setReserveNumberRate(teachingClass.getNumber().intValue()==0?0.0:new BigDecimal(teachingClass.getReserveNumber()).divide(new BigDecimal(teachingClass.getNumber()),4,BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100)).doubleValue());
        classDao.updateByPrimaryKeySelective(record);
    }
    
    @Override
	@Transactional
    public List<TeachingClass> setReserveProportion(ReserveDto reserveDto)
    {
        BigDecimal reserveProportion = new BigDecimal(reserveDto.getReserveProportion());
        Example example = new Example(TeachingClass.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("id", reserveDto.getIds());
        List<TeachingClass> teachingClasses = classDao.selectByExample(example);
        List<TeachingClass> teachingClasses1 = new ArrayList<>();
        List<TeachingClass> teachingClasses2 = new ArrayList<>();
        if(CollectionUtil.isNotEmpty(teachingClasses)) {
            BigDecimal hundred = new BigDecimal(Constants.HUNDRED);
            for(TeachingClass temp:teachingClasses) {
                Integer number = (temp.getNumber() == null ? 0 : temp.getNumber());
                Integer elcNumber = (temp.getElcNumber() == null ? 0 : temp.getElcNumber());
                int reserveNumber = new BigDecimal(temp.getNumber()).multiply(reserveProportion).divide(hundred, BigDecimal.ROUND_UP).intValue();
                if(reserveProportion.doubleValue() >= 100.0){
                    reserveNumber = number.intValue();
                    reserveProportion = new BigDecimal(100);
                }
                temp.setReserveNumber(reserveNumber);
                temp.setReserveNumberRate(reserveProportion.doubleValue());
                if (reserveNumber + elcNumber.intValue() > number.intValue()){
                    teachingClasses1.add(temp);
                }else{
                    teachingClasses2.add(temp);
                }
            }
            if(CollectionUtil.isNotEmpty(teachingClasses2)){
                classDao.updateReserveProportion(teachingClasses2);
            }

        }
        return teachingClasses1;
    }
    @Override
	@Transactional
    public List<TeachingClass> batchSetReserveNum(ReserveDto reserveDto) {
    	TeachingClass teachingClass = new TeachingClass();
    	teachingClass.setReserveNumber(reserveDto.getReserveNumber());
        List<TeachingClass> teachingClasss = new ArrayList<>();
    	if(reserveDto.getReserveNumberRate() == null){
    	    Example example = new Example(TeachingClass.class);
    	    example.createCriteria().andIn("id",reserveDto.getIds());
            List<TeachingClass> teachingClasses = teachingClassDao.selectByExample(example);
            Map<Long, TeachingClass> collect = teachingClasses.stream().collect(Collectors.toMap(s -> s.getId(), s -> s));
            for (Long id : reserveDto.getIds()) {
                TeachingClass teachingClass1 = collect.get(id);
                Integer number = (teachingClass1.getNumber() == null ? 0 : teachingClass1.getNumber());
                Integer elcNumber = (teachingClass1.getElcNumber() == null ? 0 : teachingClass1.getElcNumber());
                if (reserveDto.getReserveNumber() + elcNumber.intValue() > number.intValue()){
                    teachingClasss.add(teachingClass1);
                }else{
                    teachingClass.setReserveNumberRate(teachingClass1.getNumber().intValue()==0?0.0:new BigDecimal(reserveDto.getReserveNumber()).divide(new BigDecimal(teachingClass1.getNumber()),4,BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100)).doubleValue());
                }
            }
        }else{
            teachingClass.setReserveNumberRate(reserveDto.getReserveNumberRate());
        }
    	Example example = new Example(TeachingClass.class);
    	Example.Criteria criteria = example.createCriteria();
    	criteria.andIn("id", reserveDto.getIds());
    	classDao.updateByExampleSelective(teachingClass, example);
        return teachingClasss;
    }
    @Override
	@Transactional
    public void release(ReserveDto reserveDto) {
        reserveDto.setReserveNumberRate(Constants.RESERVENUMBERRATE);
    	batchSetReserveNum(reserveDto);
    }
    
    @Override
	public void releaseAll(ElcResultQuery condition) {
    	Page<TeachingClassVo> listPage = classDao.listPage(condition);
    	List<TeachingClassVo> list = listPage.getResult();
    	if(CollectionUtil.isNotEmpty(list)) {
    		List<Long> ids = list.stream().map(TeachingClassVo::getId).collect(Collectors.toList());
    		ReserveDto reserveDto = new ReserveDto();
    		reserveDto.setIds(ids);
            reserveDto.setReserveNumberRate(Constants.RESERVENUMBERRATE);
    		release(reserveDto);
    	}
    }
    
    String key(SuggestProfessionDto dto)
    {
        return dto.getGrade() + "-" + dto.getProfession();
    }
    
    @Override
    public void autoRemove(AutoRemoveDto dto)
    {
        Long teachingClassId = dto.getTeachingClassId();
    	TeachingClass teachingClass = classDao.selectByPrimaryKey(teachingClassId);
        if (null != teachingClass)
        {
            ElcCourseTake param = new ElcCourseTake();
            param.setTeachingClassId(teachingClassId);
            List<ElcCourseTake> takes = courseTakeDao.select(param);
            if(CollectionUtil.isNotEmpty(takes)&&teachingClass.getMaxFirstRoundNum()==0) {
            	List<ElcCourseTake> firstTakes = takes.stream().filter(c->Constants.FIRST.equals(c.getTurn())).collect(Collectors.toList());
            	teachingClass.setMaxFirstRoundNum(firstTakes.size());
            }
            List<ElcCourseTake> secondTakes = takes.stream().filter(c->Constants.SECOND.equals(c.getTurn())).collect(Collectors.toList());
            if(CollectionUtil.isNotEmpty(secondTakes)) {
            	if(teachingClass.getMaxSecondRoundNum() ==0) {
            		teachingClass.setMaxSecondRoundNum(secondTakes.size());
            	}
            	takes = secondTakes;
            }
            classDao.updateByPrimaryKeySelective(teachingClass);
            String course = takes.get(0).getCourseCode();
//        	if(Boolean.TRUE.equals(dto.getSuggestSwitchCourse())) {
//        		List<ElcCourseSuggestSwitch> suggestSwitchs = elcCourseSuggestSwitchDao.selectAll();
//        		if(CollectionUtil.isNotEmpty(suggestSwitchs)) {
//        			List<String> suggestCoures = suggestSwitchs.stream().map(ElcCourseSuggestSwitch::getCourseCode).collect(Collectors.toList());
//        			if(!suggestCoures.contains(course)) {
//        				return;
//        			}
//        		}
//        	}
            // 特殊学生
            List<String> invincibleStdIds =
                invincibleStdsDao.selectAllStudentId();
            // 优先学生
            List<ElcAffinityCoursesStdsVo> affinityCoursesStds =
                affinityCoursesStdsDao.getStudentByCourseId(course);
            Set<String> affinityCoursesStdSet = affinityCoursesStds.stream()
                .map(aff -> aff.getCourseCode() + "-" + aff.getStudentId())
                .collect(toSet());
            //普通学生集合
            List<Student> normalStus = new ArrayList<>();
            //特殊学生集合
            List<Student> invincibleStus = new ArrayList<>();
            //优先学生集合
            List<Student> affinityStus = new ArrayList<>();
            //把学生分类(普通学生、特殊学生、优先学生)
            for (ElcCourseTake take : takes)
            {
                String courseCode = take.getCourseCode();
                String studentId = take.getStudentId();
                Student stu = studentDao.findStudentByCode(studentId);
                if (invincibleStdIds.contains(studentId))
                {
                    invincibleStus.add(stu);
                }
                else if (dto.getAffinityStu() && affinityCoursesStdSet
                    .contains(courseCode + "-" + studentId))
                {
                    affinityStus.add(stu);
                }
                else
                {
                    normalStus.add(stu);
                }
            }
            //班级配对 查找绑定班级
            Example example = new Example(ElcTeachingClassBind.class);
            example.createCriteria().andEqualTo("teachingClassId", teachingClass.getId());
            ElcTeachingClassBind elcTeachingClassBind = elcTeachingClassBindDao.selectOneByExample(example);
            Example bindExample = new Example(ElcTeachingClassBind.class);
            bindExample.createCriteria().andEqualTo("bindClassId", teachingClass.getId());
            ElcTeachingClassBind bindElcTeachingClassBind = elcTeachingClassBindDao.selectOneByExample(bindExample);
            List<ElcCourseTake> bindTakes = new ArrayList<ElcCourseTake>();
            List<String> bindStudentIds = new ArrayList<>();
            List<Student> bindStudents = new ArrayList<>();
            Long bindTeachingClassId = null;
            if(elcTeachingClassBind !=null) {
            	 bindTeachingClassId =elcTeachingClassBind.getBindClassId();
            	 ElcCourseTake bindParam = new ElcCourseTake();
            	 bindParam.setTeachingClassId(elcTeachingClassBind.getBindClassId());
                 bindTakes = courseTakeDao.select(bindParam);
                 if(CollectionUtil.isNotEmpty(bindTakes)) {
                	 bindStudentIds = bindTakes.stream().map(ElcCourseTake ::getStudentId).collect(Collectors.toList());
                	 bindStudents = studentDao.findStudentByIds(bindStudentIds);
                 }
            }
            if(bindElcTeachingClassBind !=null) {
            	 bindTeachingClassId = bindElcTeachingClassBind.getTeachingClassId();
           	     ElcCourseTake bindParam = new ElcCourseTake();
           	     bindParam.setTeachingClassId(bindElcTeachingClassBind.getTeachingClassId());
                 bindTakes = courseTakeDao.select(bindParam);
                 if(CollectionUtil.isNotEmpty(bindTakes)) {
                	 bindStudentIds = bindTakes.stream().map(ElcCourseTake ::getStudentId).collect(Collectors.toList());
                	 bindStudents = studentDao.findStudentByIds(bindStudentIds);
                 }
            }
            Integer limitnumber  = teachingClass.getNumber() - teachingClass.getReserveNumber();
            List<String> removeStus = new ArrayList<>();
            List<String> bindremoveStus = new ArrayList<>();
            GradAndPreFilter gradAndPreFilter =
                new GradAndPreFilter(dto, classDao);
            gradAndPreFilter.init();
            ClassElcConditionFilter elcConditionFilter =
                new ClassElcConditionFilter(dto,
                    classElectiveRestrictAttrDao);
            elcConditionFilter.init();
            //1.删除普通学生
            // 这里做三次的原因是因为有三种学生类型
            for (int i = 0; i < 3; i++)
            {
            	List<Student> stuList = normalStus;
//                List<Student> stuList = normalStus.size() > 0 ? normalStus
//                    : (affinityStus.size() > 0 ? affinityStus
//                        : invincibleStus);
                if(i==1) {
                	stuList = affinityStus;
                }else if(i ==2) {
                    if (dto.getInvincibleStu())
                    {
                    	if(limitnumber >0) {
                    		invincibleStus.clear();
                    	}
                    }
                	stuList = invincibleStus;
                }
                
                if (CollectionUtil.isEmpty(stuList))
                {
                    continue;
                }
                gradAndPreFilter.execute(stuList, removeStus);
                if (invincibleStus.size() + affinityStus.size()
                + normalStus.size() > limitnumber) {
                    int overSize = (invincibleStus.size()
                            + affinityStus.size() + normalStus.size())
                            - limitnumber;
                    if(i!=0 && overSize<=0) {
                    	break;
                    }
                    //执行班级匹配
                    if(bindTeachingClassId !=null) {
                    	//班级匹配交集
                    	List<String> onlyList = stuList.stream().map(Student ::getStudentCode).collect(Collectors.toList());
                    	//本班级多余学生
                    	List<String> onlyRemoveList = stuList.stream().map(Student ::getStudentCode).collect(Collectors.toList());
                    	//绑定班级多余学生
                    	List<String> bindList = bindStudents.stream().map(Student ::getStudentCode).collect(Collectors.toList());
                    	//交集
                    	onlyList.retainAll(bindList);
                    	onlyRemoveList.removeAll(onlyList);
                    	bindList.removeAll(onlyList);
                    	removeStus.addAll(onlyRemoveList);
                    	bindremoveStus.addAll(bindList);
                    	List<Student> saveList = new ArrayList<Student>(stuList);
                    	stuList.clear();
                    	List<Student> saveStudents = saveList.stream().filter(c->onlyList.contains(c.getStudentCode())).collect(Collectors.toList());
                    	stuList.addAll(saveStudents);
                    }
                    //执行完后人数还是超过上限则进行随机删除
                    if(overSize > 0) {
                    	elcConditionFilter.execute(stuList, removeStus);
                    }
                    if (CollectionUtil.isNotEmpty(stuList))
                    {
                        //执行完后人数还是超过上限则进行随机删除
                        overSize = (invincibleStus.size()
                            + affinityStus.size() + normalStus.size())
                            - limitnumber;
                        if (overSize > 0)
                        {
                            // 1.普通人数小于超出人数则说明优先人数的人也超了这时需要清空普通人数
                            // 2.普通人数大于超出人数则说明只有普通人数超了，普通人数需要等于超出人数
                            int limitNumber = limitnumber - invincibleStus.size();
                            if(limitnumber == 0) {
                            	limitNumber = 0;
                            }
                            if (stuList.size() < overSize)
                            {
                            	limitNumber = -1;
                            }
                            if(limitNumber>-1) {
                                GradAndPreFilter
                                .randomRemove(removeStus, limitNumber, stuList);
                            }
                        }else {
                        	break;
                        }
                    }
                }
                //移除本班级不符合规则学生
                removeAndRecordLog(dto,
                    teachingClassId,
                    teachingClass,
                    removeStus,dto.getLabel());
                //移除绑定班级不符合学生
                if(bindTeachingClassId !=null) {
                	TeachingClass bindClass = classDao.selectOversize(teachingClassId);
                    removeAndRecordLog(dto,
                    		bindTeachingClassId,
                    		bindClass,
                    		bindremoveStus,dto.getLabel());
                }
            }
        
        }
    
    }
    
    
    @Override
    public AsyncResult asyncAutoRemove(AutoRemoveDto dto)
    {
    	AsyncResult resul = AsyncProcessUtil.submitTask("asyncAutoRemove", new AsyncExecuter() {
            @Override
            public void execute() {
            	autoRemove(dto);
            }
        });
		return resul;
    }
    
    public void removeAndRecordLog(AutoRemoveDto dto, Long teachingClassId,
        TeachingClass teachingClass, List<String> removeStus,String label)
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
            Example example = new Example(ElcCourseTake.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo("calendarId", calendarId);
            criteria.andEqualTo("teachingClassId", teachingClassId);
            criteria.andIn("studentId", removeStus);
            List<ElcCourseTake> elcCourseTakes = courseTakeDao.selectByExample(example);
            if(CollectionUtil.isEmpty(elcCourseTakes)) {
            	throw new ParameterValidateException(I18nUtil.getMsg("common.dataError",
                        I18nUtil.getMsg("学生选课")));
            }
            List<RebuildCourseRecycle> rebuildCourseRecycles = new ArrayList<>();
            for(ElcCourseTake elcCourseTake:elcCourseTakes) {
            	RebuildCourseRecycle rebuildCourseRecycle = new RebuildCourseRecycle();
            	BeanUtils.copyProperties(elcCourseTake, rebuildCourseRecycle);
            	rebuildCourseRecycle.setStudentCode(elcCourseTake.getStudentId());
            	rebuildCourseRecycle.setId(null);
            	rebuildCourseRecycle.setType(Constants.AUTOTYPE);
            	rebuildCourseRecycle.setScreenLabel(label);
            	rebuildCourseRecycles.add(rebuildCourseRecycle);
            }
            rebuildCourseRecycleDao.insertList(rebuildCourseRecycles);
            courseTakeService.withdraw(values);
        }
    }
    
    public void newRemoveAndRecordLog(AutoRemoveDto dto,
            TeachingClass teachingClass, List<String> removeStus,String label,List<RebuildCourseRecycle> rebuildCourseRecycles,List<ElcCourseTake> withdrawTakes)
        {
            if (CollectionUtil.isNotEmpty(removeStus))
            {
            	List<ElcCourseTake> classTakes = dto.getTakes();
            	List<ElcCourseTake> values= classTakes.stream().filter(c->removeStus.contains(c.getStudentId())).collect(Collectors.toList());
            	withdrawTakes.addAll(values);
                for(ElcCourseTake elcCourseTake:withdrawTakes) {
                	RebuildCourseRecycle rebuildCourseRecycle = new RebuildCourseRecycle();
                	BeanUtils.copyProperties(elcCourseTake, rebuildCourseRecycle);
                	rebuildCourseRecycle.setStudentCode(elcCourseTake.getStudentId());
                	rebuildCourseRecycle.setId(null);
                	rebuildCourseRecycle.setType(Constants.AUTOTYPE);
                	rebuildCourseRecycle.setScreenLabel(label);
                	rebuildCourseRecycles.add(rebuildCourseRecycle);
                }
            }
        }
    

    /**
     * 选课结果统计
     */
	@Override
	public ElcResultCountVo elcResultCount(PageCondition<ElcResultQuery> page) {
		//从学生维度查询
		//根据条件查出满足条件的学生分类（年级、培养层次、培养类别、学位类型、学习形式）
		ElcResultQuery condition = page.getCondition();
		ElcResultCountVo elcResultCountVo = new ElcResultCountVo();
		PageHelper.startPage(page.getPageNum_(), page.getPageSize_());
		if(condition.getDimension().intValue() == Constants.ONE){
			/*Page<ElcResultDto>  elcResultList = elcResultCountDao.getElcResult(condition);
			condition.setIndex(TableIndexUtil.getIndex(condition.getCalendarId()));
			Integer elcNumber = elcResultCountDao.getElcNumber(condition);
			for (ElcResultDto elcResultDto : elcResultList) {
				
				//该年级、培养层次、培养类别、学位类型、学习形式查询条件
				ElcResultQuery query = new ElcResultQuery();
				if (StringUtils.isNotEmpty(condition.getGrade())) {
					query.setGrade(condition.getGrade());
				}else{
					elcResultDto.setGrade("全部");
				}
				query.setFaculty(condition.getFaculty() == null ? "" : condition.getFaculty());
				query.setEnrolSeason(condition.getEnrolSeason()  == null ? "" : condition.getEnrolSeason());
				query.setCalendarId(condition.getCalendarId());
				query.setIndex(TableIndexUtil.getIndex(condition.getCalendarId()));
				query.setManagerDeptId(condition.getManagerDeptId());
				query.setDegreeType(elcResultDto.getDegreeType() == null ? "" : elcResultDto.getDegreeType());
				query.setFormLearning(elcResultDto.getFormLearning() == null ? "" : elcResultDto.getFormLearning());
				query.setTrainingCategory(elcResultDto.getTrainingCategory() == null ? "" : elcResultDto.getTrainingCategory());
				query.setTrainingLevel(elcResultDto.getTrainingLevel() == null ? "" : elcResultDto.getTrainingLevel());
				//根据条件查询查询已将选课学生人数
				Integer numberOfelectedPersons = elcResultCountDao.getNumberOfelectedPersons(query);
				elcResultDto.setNumberOfelectedPersons(numberOfelectedPersons);
				elcResultDto.setNumberOfelectedPersonsPoint(elcResultDto.getStudentNum().intValue()==0?new BigDecimal(0).doubleValue():new BigDecimal(numberOfelectedPersons).divide(new BigDecimal(elcResultDto.getStudentNum()),4,BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100)).doubleValue());
				elcResultDto.setNumberOfNonCandidates(elcResultDto.getStudentNum() - numberOfelectedPersons);
			}
			Integer elcGateMumber = elcResultCountDao.getElcGateMumber(condition);
			Integer elcPersonTime = elcResultCountDao.getElcPersonTime(condition);
			PageResult<ElcResultDto> elceResultByStudent = new PageResult<>(elcResultList);
			elcResultCountVo.setPageNum_(elceResultByStudent.getPageNum_());
			elcResultCountVo.setPageSize_(elceResultByStudent.getPageSize_());
			elcResultCountVo.setTotal_(elceResultByStudent.getTotal_());
			elcResultCountVo.setList(elceResultByStudent.getList());
			elcResultCountVo.setElcNumberByStudent(elcNumber);
			elcResultCountVo.setElcGateMumberByStudent(elcGateMumber);
			elcResultCountVo.setElcPersonTimeByStudent(elcPersonTime);*/
            condition.setIndex(TableIndexUtil.getIndex(condition.getCalendarId()));
            Page<ElcResultDto>  elcResultList = elcResultCountDao.getElcResultUpdate(condition);
            Integer elcNumber = elcResultCountDao.getElcNumber(condition);
            for (ElcResultDto elcResultDto : elcResultList) {
                if (StringUtils.isNotEmpty(condition.getGrade())) {
                }else{
                    elcResultDto.setGrade("全部");
                }
                elcResultDto.setNumberOfelectedPersonsPoint(elcResultDto.getStudentNum().intValue()==0?new BigDecimal(0).doubleValue():new BigDecimal(elcResultDto.getNumberOfelectedPersons()).divide(new BigDecimal(elcResultDto.getStudentNum()),4,BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100)).doubleValue());
                elcResultDto.setNumberOfNonCandidates(elcResultDto.getStudentNum() - elcResultDto.getNumberOfelectedPersons());
            }
            Integer elcGateMumber = elcResultCountDao.getElcGateMumber(condition);
            Integer elcPersonTime = elcResultCountDao.getElcPersonTime(condition);
            PageResult<ElcResultDto> elceResultByStudent = new PageResult<>(elcResultList);
            elcResultCountVo.setPageNum_(elceResultByStudent.getPageNum_());
            elcResultCountVo.setPageSize_(elceResultByStudent.getPageSize_());
            elcResultCountVo.setTotal_(elceResultByStudent.getTotal_());
            elcResultCountVo.setList(elceResultByStudent.getList());
            elcResultCountVo.setElcNumberByStudent(elcNumber);
            elcResultCountVo.setElcGateMumberByStudent(elcGateMumber);
            elcResultCountVo.setElcPersonTimeByStudent(elcPersonTime);
		}else{
			//从学院维度查询
			/*Page<ElcResultDto> eleResultByFacultyList = elcResultCountDao.getElcResultByFacult(condition);
			condition.setIndex(TableIndexUtil.getIndex(condition.getCalendarId()));
			Integer elcNumberByFaculty = elcResultCountDao.getElcNumberByFaculty(condition);
			for (ElcResultDto elcResultDto : eleResultByFacultyList) {
				//该学院该专业查询条件
				ElcResultQuery query = new ElcResultQuery();
				if (StringUtils.isNotEmpty(condition.getGrade())) {
					query.setGrade(condition.getGrade());
				}else{
					elcResultDto.setGrade("全部");
				}
				query.setFaculty(elcResultDto.getFaculty() == null ? "" : elcResultDto.getFaculty());
				query.setProfession(elcResultDto.getProfession() == null ? "" : elcResultDto.getProfession());
				query.setEnrolSeason(condition.getEnrolSeason() == null ? "" : condition.getEnrolSeason());
				query.setDegreeType(condition.getDegreeType() == null ? "" : condition.getDegreeType());
				query.setFormLearning(condition.getFormLearning() == null ? "" : condition.getFormLearning());
				query.setTrainingCategory(condition.getTrainingCategory() == null ? "" : condition.getTrainingCategory());
				query.setTrainingLevel(condition.getTrainingLevel() == null ? "" : condition.getTrainingLevel());
				query.setCalendarId(condition.getCalendarId());
				query.setIndex(TableIndexUtil.getIndex(condition.getCalendarId()));
				query.setManagerDeptId(condition.getManagerDeptId());
				
				//根据条件查询查询已将选课学生人数
				Integer numberOfelectedPersons = elcResultCountDao.getNumberOfelectedPersonsByFaculty(query);
				elcResultDto.setNumberOfelectedPersons(numberOfelectedPersons);
				elcResultDto.setNumberOfelectedPersonsPoint(elcResultDto.getStudentNum().intValue()==0?new BigDecimal(0).doubleValue():new BigDecimal(numberOfelectedPersons).divide(new BigDecimal(elcResultDto.getStudentNum()),4,BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100)).doubleValue());
				elcResultDto.setNumberOfNonCandidates(elcResultDto.getStudentNum() - numberOfelectedPersons);
			}
			Integer elcGateMumberByFaculty = elcResultCountDao.getElcGateMumberByFaculty(condition);
			Integer elcPersonTimeByFaculty = elcResultCountDao.getElcPersonTimeByFaculty(condition);
			PageResult<ElcResultDto> elceResultByFaculty = new PageResult<>(eleResultByFacultyList);
			elcResultCountVo.setPageNum_(elceResultByFaculty.getPageNum_());
			elcResultCountVo.setPageSize_(elceResultByFaculty.getPageSize_());
			elcResultCountVo.setTotal_(elceResultByFaculty.getTotal_());
			elcResultCountVo.setList(elceResultByFaculty.getList());
			elcResultCountVo.setElcNumberByFaculty(elcNumberByFaculty);
			elcResultCountVo.setElcGateMumberByFaculty(elcGateMumberByFaculty);
			elcResultCountVo.setElcPersonTimeByFaculty(elcPersonTimeByFaculty);*/
            condition.setIndex(TableIndexUtil.getIndex(condition.getCalendarId()));
            Page<ElcResultDto>  eleResultByFacultyList = elcResultCountDao.getElcResultByFacultyUpdate(condition);
            Integer elcNumberByFaculty = elcResultCountDao.getElcNumberByFaculty(condition);
            for (ElcResultDto elcResultDto : eleResultByFacultyList) {
                if (StringUtils.isNotEmpty(condition.getGrade())) {
                }else{
                    elcResultDto.setGrade("全部");
                }
                elcResultDto.setNumberOfelectedPersonsPoint(elcResultDto.getStudentNum().intValue()==0?new BigDecimal(0).doubleValue():new BigDecimal(elcResultDto.getNumberOfelectedPersons()).divide(new BigDecimal(elcResultDto.getStudentNum()),4,BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100)).doubleValue());
                elcResultDto.setNumberOfNonCandidates(elcResultDto.getStudentNum() - elcResultDto.getNumberOfelectedPersons());
            }
            Integer elcGateMumberByFaculty = elcResultCountDao.getElcGateMumberByFaculty(condition);
            Integer elcPersonTimeByFaculty = elcResultCountDao.getElcPersonTimeByFaculty(condition);
            PageResult<ElcResultDto> elceResultByFaculty = new PageResult<>(eleResultByFacultyList);
            elcResultCountVo.setPageNum_(elceResultByFaculty.getPageNum_());
            elcResultCountVo.setPageSize_(elceResultByFaculty.getPageSize_());
            elcResultCountVo.setTotal_(elceResultByFaculty.getTotal_());
            elcResultCountVo.setList(elceResultByFaculty.getList());
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
	@SuppressWarnings("resource")
	@Override
	public PageResult<Student4Elc> getStudentPage(PageCondition<ElcResultQuery> page ) {
		PageHelper.startPage(page.getPageNum_(), page.getPageSize_());
		//查询该条件下未选课学生名单
		Page<Student4Elc> result = new Page<Student4Elc>();
		if (StringUtils.equalsIgnoreCase(page.getCondition().getTrainingCategory(),"-1")){
            page.getCondition().setTrainingCategory(null);
        }
		if(page.getCondition().getDimension().intValue() == Constants.ONE){
			result = studentDao.getAllNonSelectedCourseStudent(page.getCondition());
		}else{
			result = studentDao.getAllNonSelectedCourseStudent1(page.getCondition());
		}
		return new PageResult<>(result);
	}

	


	@Override
	public RestResult<String> elcResultCountsExport(ElcResultQuery condition) {
		String path="";
        try {
        	 PageCondition<ElcResultQuery> pageCondition = new PageCondition<ElcResultQuery>();
             pageCondition.setCondition(condition);
             pageCondition.setPageSize_(100);
             int pageNum = 0;
             pageCondition.setPageNum_(pageNum);
             List<ElcResultDto> list = new ArrayList<>();
             while (true)
             {
                 pageNum++;
                 pageCondition.setPageNum_(pageNum);
                 ElcResultCountVo elcResultCountVo = elcResultCount(pageCondition);
                 
                 list.addAll(elcResultCountVo.getList());

                 if (elcResultCountVo.getTotal_() <= list.size())
                 {
                     break;
                 }
             }
             list = SpringUtils.convert(list);
            if (condition.getDimension().intValue() == Constants.ONE) {
            	@SuppressWarnings("unchecked")
				ExcelEntityExport<ElcResultDto> excelExport = new ExcelEntityExport(list,
            			excelStoreConfig.getElcResultCountExportByStudentKey(),
            			excelStoreConfig.getElcResultCountExportByStudentTitle(),
            			cacheDirectory);
            	path = excelExport.exportExcelToCacheDirectory("研究生选课统计");
			}else{
				@SuppressWarnings("unchecked")
				ExcelEntityExport<ElcResultDto> excelExport = new ExcelEntityExport(list,
						excelStoreConfig.getElcResultCountExportByFacultyKey(),
						excelStoreConfig.getElcResultCountExportByFacultyTitle(),
						cacheDirectory);
				path = excelExport.exportExcelToCacheDirectory("研究生选课统计");
			}
        }catch (Exception e){
            return RestResult.failData("minor.export.fail");
        }
        return RestResult.successData("minor.export.success",path);
	}

	@Override
	public RestResult<String> exportOfNonSelectedCourse(ElcResultQuery condition) {
		String path="";
        try {
        	condition.setGrade(StringUtils.equalsIgnoreCase("全部", condition.getGrade()) ? "" : condition.getGrade());
        	condition.setFaculty(StringUtils.equalsIgnoreCase("null", condition.getFaculty()) ? "" : condition.getFaculty());
        	condition.setTrainingLevel(StringUtils.equalsIgnoreCase("null", condition.getTrainingLevel()) ? "" : condition.getTrainingLevel());
        	condition.setTrainingCategory(StringUtils.equalsIgnoreCase("null", condition.getTrainingCategory()) ? "" : condition.getTrainingCategory());
        	condition.setDegreeType(StringUtils.equalsIgnoreCase("null", condition.getDegreeType()) ? "" : condition.getDegreeType());
        	condition.setFormLearning(StringUtils.equalsIgnoreCase("null", condition.getFormLearning()) ? "" : condition.getFormLearning());
        	condition.setEnrolSeason(StringUtils.equalsIgnoreCase("null", condition.getEnrolSeason()) ? "" : condition.getEnrolSeason());
        	condition.setProfession(StringUtils.equalsIgnoreCase("null", condition.getProfession()) ? "" : condition.getProfession());
        	PageCondition<ElcResultQuery> pageCondition = new PageCondition<ElcResultQuery>();
            pageCondition.setCondition(condition);
            pageCondition.setPageSize_(100);
            int pageNum = 0;
            pageCondition.setPageNum_(pageNum);
            List<Student4Elc> list = new ArrayList<Student4Elc>();
            while (true)
            {
                pageNum++;
                pageCondition.setPageNum_(pageNum);
                PageResult<Student4Elc> studentList = getStudentPage(pageCondition);
                
                
                list.addAll(studentList.getList());

                if (studentList.getTotal_() <= list.size())
                {
                    break;
                }
            }
            logger.info(list.size() + "convert   dictionary   start");
            list = SpringUtils.convert(list);
            logger.info("convert   dictionary   end");
        	ExcelEntityExport<ElcResultDto> excelExport = new ExcelEntityExport(list,
        			excelStoreConfig.getAllNonSelectedCourseStudentKey(),
        			excelStoreConfig.getAllNonSelectedCourseStudentTitle(),
        			cacheDirectory);
        	path = excelExport.exportExcelToCacheDirectory("研究生未选课学生名单");
        }catch (Exception e){
            return RestResult.failData("minor.export.fail");
        }
        return RestResult.successData("minor.export.success",path);
	}
	
	@Override
	@Transactional
	public void saveElcLimit(TeachingClassVo teachingClassVo) {
		TeachingClassElectiveRestrictAttr attr = new TeachingClassElectiveRestrictAttr();
		attr.setTeachingClassId(teachingClassVo.getId());
		attr.setTrainingLevel(teachingClassVo.getLimitTrainingLevel());
		attr.setTrainingCategory(teachingClassVo.getLimitTrainingCategory());
		attr.setFaculty(teachingClassVo.getLimitFaculty());
		attr.setIsDivsex(teachingClassVo.getLimitIsDivsex());
		String limitIsDivsex = teachingClassVo.getLimitIsDivsex();
		//all
		if("1".equals(limitIsDivsex)){
            attr.setNumberMale(1);
            attr.setNumberFemale(1);
        }//boy
        else if("2".equals(limitIsDivsex)){
            attr.setNumberMale(1);
            attr.setNumberFemale(0);
        }//girl
        else if("3".equals(limitIsDivsex)){
            attr.setNumberMale(0);
            attr.setNumberFemale(1);
        }
		Example example = new Example(TeachingClassElectiveRestrictAttr.class);
		Example.Criteria criteria = example.createCriteria();
		criteria.andEqualTo("teachingClassId", teachingClassVo.getId());
		TeachingClassElectiveRestrictAttr teachingClassAttr = attrDao.selectOneByExample(example);
		if(teachingClassAttr!=null) {
			attr.setCreatedAt(teachingClassAttr.getCreatedAt());
			attr.setUpdatedAt(new Date());
			attrDao.updateByExampleSelective(attr, example);
		}else {
			attr.setCreatedAt(new Date());
			attrDao.insertSelective(attr);
		}
		TeachingClassElectiveRestrictProfession profession = new TeachingClassElectiveRestrictProfession();
		profession.setTeachingClassId(teachingClassVo.getId());
		profession.setGrade(teachingClassVo.getLimitGrade());
		profession.setProfession(teachingClassVo.getLimitProfession());
		profession.setDirectionCode(teachingClassVo.getLimitDirectionCode());
		Example pExample = new Example(TeachingClassElectiveRestrictProfession.class);
		Example.Criteria pEriteria = pExample.createCriteria();
		pEriteria.andEqualTo("teachingClassId", teachingClassVo.getId());
		TeachingClassElectiveRestrictProfession elcProfession = professionDao.selectOneByExample(pExample);
		if(elcProfession!=null) {
			profession.setUpdatedAt(new Date());
			professionDao.updateByExampleSelective(profession, pExample);
		}else {
			profession.setCreatedAt(new Date());
			professionDao.insertSelective(profession);
		}
	}
	
	@Override
	@Transactional
	public void saveProportion(TeachingClassVo teachingClassVo) {
        Example example = new Example(TeachingClassElectiveRestrictAttr.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("teachingClassId", teachingClassVo.getId());
        TeachingClassElectiveRestrictAttr teachingClassAttr = attrDao.selectOneByExample(example);
        //获取是否是男女班，男1 女2 不区分0
        String limitIsDivsex = teachingClassVo.getLimitIsDivsex();
        if(teachingClassAttr != null){
            limitIsDivsex = teachingClassAttr.getIsDivsex();
        }
		TeachingClassElectiveRestrictAttr attr = new TeachingClassElectiveRestrictAttr();
		attr.setTeachingClassId(teachingClassVo.getId());
		int numberMale = teachingClassVo.getNumberMale();
		int numberFemale = teachingClassVo.getNumberFemale();

        if("1".equals(limitIsDivsex)&&0!=numberFemale){
            throw new ParameterValidateException(I18nUtil.getMsg("election.male.error"));
        }else if("2".equals(limitIsDivsex)&&0!=numberMale){
            throw new ParameterValidateException(I18nUtil.getMsg("election.female.error"));
        }
        //获取实际人数
        int elcNumber = teachingClassVo.getNumber();
        if(numberMale==0){
            numberFemale = elcNumber;
        }else if(numberFemale==0){
            numberMale = elcNumber;
        }else{
            numberMale = (int)((((double)numberMale/(numberMale+numberFemale)))*elcNumber);
            numberFemale = elcNumber-numberMale;
        }

		attr.setNumberMale(numberMale);
		attr.setNumberFemale(numberFemale);

		if(teachingClassAttr!=null) {
			attr.setUpdatedAt(new Date());
			attrDao.updateByExampleSelective(attr, example);
		}else {
			attr.setCreatedAt(new Date());
			attrDao.insertSelective(attr);
		}
	}
	
	@Override
	@Transactional
	public void saveScreeningLabel(ElcScreeningLabel elcScreeningLabel) {
		Example example = new Example(ElcScreeningLabel.class);
		Example.Criteria criteria = example.createCriteria();
		criteria.andEqualTo("calendarId", elcScreeningLabel.getCalendarId());
		elcScreeningLabelDao.deleteByExample(example);
		elcScreeningLabelDao.insertSelective(elcScreeningLabel);
	}
	
	@Override
	@Transactional
	public void updateScreeningLabel(ElcScreeningLabel elcScreeningLabel) {
		if(elcScreeningLabel.getId() ==null) {
			 throw new ParameterValidateException(I18nUtil.getMsg("baseresservice.parameterError"));
		}
		elcScreeningLabelDao.updateByPrimaryKeySelective(elcScreeningLabel);
	}
	@Override
	@Transactional
	public void saveClassBind(ElcTeachingClassBind elcTeachingClassBind) {
        Example example = new Example(ElcTeachingClassBind.class);
        example.createCriteria().andEqualTo("teachingClassId",elcTeachingClassBind.getTeachingClassId());
        List<ElcTeachingClassBind> elcTeachingClassBinds = bindDao.selectByExample(example);
        if(CollectionUtil.isNotEmpty(elcTeachingClassBinds)) {
            throw new ParameterValidateException(I18nUtil.getMsg("common.notExist",
                    I18nUtil.getMsg("election.elcTeachingClassBind")));
		}
		bindDao.insertSelective(elcTeachingClassBind);
	}
	@Override
	@Transactional
	public void updateClassBind(ElcTeachingClassBind elcTeachingClassBind) {
		ElcTeachingClassBind bind =bindDao.selectOne(elcTeachingClassBind);
		if(bind==null) {
            throw new ParameterValidateException(I18nUtil.getMsg("common.exist",
                    I18nUtil.getMsg("election.elcTeachingClassBind")));
		}
		bindDao.updateByPrimaryKey(elcTeachingClassBind);
	}
	
	@Override
	@Transactional
	public void updateClassRemark(Long id, String remark) {
		TeachingClass teachingClass =new TeachingClass();
		teachingClass.setId(id);
		teachingClass.setRemark(remark);
		teachingClassDao.updateByPrimaryKeySelective(teachingClass);
	}

	@Override
	public ExcelResult teachClassPageExport(ElcResultQuery condition) {
		ExcelResult excelResult = ExportExcelUtils.submitTask("teachClassPageExportList", new ExcelExecuter() {
            @Override
            public GeneralExcelDesigner getExcelDesigner() {
                ExcelResult result = this.getResult();
                PageCondition<ElcResultQuery> pageCondition = new PageCondition<ElcResultQuery>();
                pageCondition.setCondition(condition);
                pageCondition.setPageSize_(100);
                int pageNum = 0;
                pageCondition.setPageNum_(pageNum);
                List<TeachingClassVo> list = new ArrayList<>();
                while (true)
                {
                    pageNum++;
                    pageCondition.setPageNum_(pageNum);
                    PageResult<TeachingClassVo> electCourseList = listPage(pageCondition);
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
                GeneralExcelDesigner design = null;
                if(Constants.IS.equals(condition.getIsScreening())) {
                    design = getDesignWhenIsScreening();
                }else {
                    design = getDesign();
                }
                //将数据放入excel对象中
                design.setDatas(list);
                result.setDoneCount(list.size());
                return design;
            }
        });
        return excelResult;
	}

	private GeneralExcelDesigner getDesign() {
        GeneralExcelDesigner design = new GeneralExcelDesigner();
        design.setNullCellValue("");
        design.addCell(I18nUtil.getMsg("teachClassPageExport.code"), "code");
        design.addCell(I18nUtil.getMsg("teachClassPageExport.courseName"), "courseName");
        design.addCell(I18nUtil.getMsg("teachClassPageExport.teacherName"), "teacherName");
        design.addCell(I18nUtil.getMsg("teachClassPageExport.campus"), "campus").setValueHandler(
                (value, rawData, cell) -> {
                    return dictionaryService.query("X_XQ", value, SessionUtils.getLang());
                });
        design.addCell(I18nUtil.getMsg("teachClassPageExport.elcNumber"), "elcNumber");
        design.addCell(I18nUtil.getMsg("teachClassPageExport.reserveNumber"), "reserveNumber");
        design.addCell(I18nUtil.getMsg("teachClassPageExport.timeAndRoom"), "timeAndRoom");
        design.addCell(I18nUtil.getMsg("teachClassPageExport.classNumberStr"), "classNumberStr");
        design.addCell(I18nUtil.getMsg("teachClassPageExport.remark"), "remark");
        design.addCell(I18nUtil.getMsg("teachClassPageExport.bindClassCode"), "bindClassCode");
        return design;
	}

    private GeneralExcelDesigner getDesignWhenIsScreening() {
        GeneralExcelDesigner design = new GeneralExcelDesigner();
        design.setNullCellValue("");
        design.addCell(I18nUtil.getMsg("teachClassPageExport.code"), "code");
        design.addCell(I18nUtil.getMsg("teachClassPageExport.courseName"), "courseName");
        design.addCell(I18nUtil.getMsg("teachClassPageExport.isElective"), "isElective").setValueHandler(
                (value, rawData, cell) -> {
                    return dictionaryService.query("K_BKKCXZ", value, SessionUtils.getLang());
                });
        design.addCell(I18nUtil.getMsg("teachClassPageExport.faculty"), "faculty").setValueHandler(
                (value, rawData, cell) -> {
                    return dictionaryService.query("X_YX", value, SessionUtils.getLang());
                });
        design.addCell(I18nUtil.getMsg("teachClassPageExport.credits"), "credits");
        design.addCell(I18nUtil.getMsg("teachClassPageExport.teacherName"), "teacherName");
        design.addCell(I18nUtil.getMsg("teachClassPageExport.elcNumber"), "elcNumber");
        design.addCell(I18nUtil.getMsg("teachClassPageExport.number"), "number");
        design.addCell(I18nUtil.getMsg("teachClassPageExport.firstTurnNum"), "firstTurnNum").setValueHandler(
                (value, rawData, cell) -> {
                    if (value == null){
                        return 0+"";
                    }else {
                        return value;
                    }

                });
        design.addCell(I18nUtil.getMsg("teachClassPageExport.secondTurnNum"), "secondTurnNum").setValueHandler(
                (value, rawData, cell) -> {
                    if (value == null){
                        return 0+"";
                    }else {
                        return value;
                    }
                });
        return design;
    }
	@Override
	@Transactional
	public void changeStudentClass(TeachingClassChange condition) {
		Example example = new Example(ElcCourseTake.class); 
        example.createCriteria()
        .andEqualTo("calendarId", condition.getCalendarId())
        .andIn("studentId", condition.getStudentIds())
        .andEqualTo("teachingClassId", condition.getOldTeachingClassId());
        List<ElcCourseTake> elcCourseTakes = courseTakeDao.selectByExample(example);
        if(CollectionUtil.isEmpty(elcCourseTakes)) {
        	throw new ParameterValidateException(I18nUtil.getMsg("common.dataError",
                    I18nUtil.getMsg("学生选课")));
        }
        List<Long> teachingClassIds = new ArrayList<Long>();
        teachingClassIds.add(condition.getNewTeachingClassId());
        elcCourseTakeService.withdraw(elcCourseTakes);
        ElcCourseTakeAddDto dto = new ElcCourseTakeAddDto();
        dto.setStudentIds(condition.getStudentIds());
        dto.setTeachingClassIds(teachingClassIds);
        dto.setCalendarId(condition.getCalendarId());
        dto.setMode(RoundMode.NORMAL.mode());
        elcCourseTakeService.add(dto);                                                             
	}
	
	@Override
	@Transactional
	public AsyncResult autoBatchRemove(BatchAutoRemoveDto dto) {
		//判断轮次是否存在
		ElectionRounds round = elecRoundsDao.selectByPrimaryKey(dto.getRoundId());
		if(round==null) {
			throw new ParameterValidateException(I18nUtil.getMsg("common.dataError",
                    I18nUtil.getMsg("轮次")));
		}
		Example example = new Example(ElcCourseTake.class);
		Example.Criteria criteria = example.createCriteria();
		criteria.andEqualTo("calendarId", dto.getCalendarId());
		criteria.andEqualTo("turn", round.getTurn());
		criteria.andEqualTo("chooseObj", getChooseObj(round.getElectionObj()));
		criteria.andEqualTo("mode", round.getMode());
		Integer index =TableIndexUtil.getIndex(dto.getCalendarId());
		List<Long> teachingClassIds =courseTakeDao.selectClassByRoundId(round.getId(),dto.getCalendarId(),index);
		if(CollectionUtil.isEmpty(teachingClassIds)) {
			throw new ParameterValidateException("该轮次没有选课数据");
		}
		AsyncResult resul = AsyncProcessUtil.submitTask("autoBatchRemove", new AsyncExecuter() {
            @Override
            public void execute() {
                AsyncResult result = this.getResult();
            	result.setTotal(teachingClassIds.size());
            	Example tExample = new Example(TeachingClass.class);
        		tExample.createCriteria().andIn("id", teachingClassIds);
        		List<TeachingClass> allTeachingClass = classDao.selectByExample(tExample);
            	//不在建议课表非特殊学生的名单
            	List<ElcCourseTake> unSuggestCourses = courseTakeDao.getUnSuggestStuents(round.getCalendarId(),index);
            	List<Long> unSuggestCoursesIds = unSuggestCourses.stream().filter(c->c!=null).map(ElcCourseTake ::getId).collect(Collectors.toList());
            	List<ElcCourseTake> alltakes = courseTakeDao.selectAllTakes(round.getCalendarId(),index);
            	List<ElcCourseTake> takes = alltakes.stream().filter(c->!unSuggestCoursesIds.contains(c.getTeachingClassId())).collect(Collectors.toList());
            	 // 特殊学生
                List<String> invincibleStdIds =
                    invincibleStdsDao.selectAllStudentId();
                // 优先学生
                List<ElcAffinityCoursesStdsVo> affinityCoursesStds =
                    affinityCoursesStdsDao.getAllElcAffinityStudents();
                //配课年级专业
                List<SuggestProfessionDto> allSuggestProfessionList =
                    classDao.selectAllSuggestProfession(teachingClassIds);
                //查询多个教学班的限制年级专业
                List<SuggestProfessionDto> allRestrictProfessionList =
                        restrictAttrDao.selectAllRestrictProfession(teachingClassIds);
                //查询多个教学班的限制学生
                List<RestrictStudent> allRestrictStus =
                        restrictAttrDao.selectAllRestrictStudent(teachingClassIds);
                //查询所有在校学生
                List<Student> allStudents = studentDao.findAllStudents();
                //查询多个教学班的限制
                Example attrExample = new Example(TeachingClassElectiveRestrictAttr.class);
                attrExample.createCriteria().andIn("teachingClassId", teachingClassIds);
                List<TeachingClassElectiveRestrictAttr> attrList = restrictAttrDao.selectByExample(attrExample);
                List<TeachingClass> classList = new ArrayList<TeachingClass>();
    			List<RebuildCourseRecycle> rebuildCourseRecycles = new ArrayList<RebuildCourseRecycle>();
    			List<ElcCourseTake> withdrawTakes = new ArrayList<ElcCourseTake>();
    			withdrawTakes.addAll(unSuggestCourses);
    			int num = 0;
        		for(TeachingClass teachingClass :allTeachingClass) {
        			Long teachingClassId = teachingClass.getId();
        			AutoRemoveDto autoRemoveDto = new AutoRemoveDto();
        			BeanUtils.copyProperties(dto, autoRemoveDto);
        			autoRemoveDto.setTeachingClassId(teachingClass.getId());
        			List<ElcCourseTake> classTakes = takes.stream().filter(c->teachingClassId.equals(c.getTeachingClassId())).collect(Collectors.toList());
        			String course = classTakes.get(0).getCourseCode();
        			autoRemoveDto.setTakes(classTakes);
        			autoRemoveDto.setTeachingClass(teachingClass);
        			autoRemoveDto.setInvincibleStdIds(invincibleStdIds);
        			List<ElcAffinityCoursesStdsVo> classAffinitys = affinityCoursesStds.stream().filter(c->course.equals(c.getCourseCode())).collect(Collectors.toList());
        	        Set<String> affinityCoursesStdSet = classAffinitys.stream()
        	                .map(aff -> aff.getCourseCode() + "-" + aff.getStudentId())
        	                .collect(toSet());
        			autoRemoveDto.setAffinityCoursesStdSet(affinityCoursesStdSet);
        			List<SuggestProfessionDto> suggestProfessionList = allSuggestProfessionList.stream().filter(c->teachingClassId.equals(c.getTeachingClassId())).collect(Collectors.toList());
        			autoRemoveDto.setSuggestProfessionList(suggestProfessionList);
        			List<SuggestProfessionDto> restrictProfessionList = allRestrictProfessionList.stream().filter(c->teachingClassId.equals(c.getTeachingClassId())).collect(Collectors.toList());
        			autoRemoveDto.setRestrictProfessionList(restrictProfessionList);
        			List<String> restrictStus = allRestrictStus.stream().filter(c->teachingClassId.equals(c.getTeachingClassId())).map(RestrictStudent ::getStudentId).collect(Collectors.toList());
        			autoRemoveDto.setRestrictStus(restrictStus);
        			TeachingClassElectiveRestrictAttr classAttrList =attrList.stream().filter(c->teachingClassId.equals(c.getTeachingClassId())).findFirst().orElse(null);
        			autoRemoveDto.setClassAttrList(classAttrList);
        			newAutoRemove(autoRemoveDto,rebuildCourseRecycles,withdrawTakes,classList,allStudents);
        			num++;
        			result.setDoneCount(num);
        			this.updateResult(result);
        		}
        		classDao.updateClassRoundNum(classList);
                rebuildCourseRecycleDao.insertList(rebuildCourseRecycles);
                courseTakeService.newWithdraw(withdrawTakes);
            }
        });
		return resul;
	}
	
    private void newAutoRemove(AutoRemoveDto dto,List<RebuildCourseRecycle> rebuildCourseRecycles,List<ElcCourseTake> withdrawTakes,List<TeachingClass> classList,List<Student> allStudents)
    {
    	TeachingClass updateTeachingClass = new TeachingClass();
    	TeachingClass teachingClass = dto.getTeachingClass();
        List<ElcCourseTake> takes = dto.getTakes();
        updateTeachingClass.setId(teachingClass.getId());
        if(CollectionUtil.isNotEmpty(takes)&&teachingClass.getMaxFirstRoundNum()==0) {
        	List<ElcCourseTake> firstTakes = takes.stream().filter(c->Constants.FIRST.equals(c.getTurn())).collect(Collectors.toList());
        	updateTeachingClass.setMaxFirstRoundNum(firstTakes.size());
        }
        List<ElcCourseTake> secondTakes = takes.stream().filter(c->Constants.SECOND.equals(c.getTurn())).collect(Collectors.toList());
        if(CollectionUtil.isNotEmpty(secondTakes)) {
        	if(teachingClass.getMaxSecondRoundNum() ==0) {
        		updateTeachingClass.setMaxSecondRoundNum(secondTakes.size());
        	}
        	takes = secondTakes;
        }
        if(updateTeachingClass.getMaxFirstRoundNum() !=null || updateTeachingClass.getMaxSecondRoundNum() !=null) {
        	classList.add(updateTeachingClass);
        }
        // 特殊学生
        List<String> invincibleStdIds = dto.getInvincibleStdIds();
        // 优先学生
        Set<String> affinityCoursesStdSet = dto.getAffinityCoursesStdSet();
        //普通学生集合
        List<Student> normalStus = new ArrayList<>();
        //特殊学生集合
        List<Student> invincibleStus = new ArrayList<>();
        //优先学生集合
        List<Student> affinityStus = new ArrayList<>();
        //把学生分类(普通学生、特殊学生、优先学生)
        for (ElcCourseTake take : takes)
        {
            String courseCode = take.getCourseCode();
            String studentId = take.getStudentId();
            Student stu = allStudents.stream().filter(c->studentId.equals(c.getStudentCode())).findFirst().orElse(null);
            if(stu ==null) {
            	continue;
            }
            if (invincibleStdIds.contains(studentId))
            {
                invincibleStus.add(stu);
            }
            else if (dto.getAffinityStu() && affinityCoursesStdSet
                .contains(courseCode + "-" + studentId))
            {
                affinityStus.add(stu);
            }
            else
            {
                normalStus.add(stu);
            }
        }
        //班级配对 查找绑定班级
        Example example = new Example(ElcTeachingClassBind.class);
        example.createCriteria().andEqualTo("teachingClassId", teachingClass.getId());
        ElcTeachingClassBind elcTeachingClassBind = null;
        List<ElcTeachingClassBind> elcTeachingClassBinds = elcTeachingClassBindDao.selectByExample(example);
        if(CollectionUtil.isNotEmpty(elcTeachingClassBinds)) {
        	elcTeachingClassBind = elcTeachingClassBinds.get(0);
        }
        ElcTeachingClassBind bindElcTeachingClassBind = null;
        Example bindExample = new Example(ElcTeachingClassBind.class);
        bindExample.createCriteria().andEqualTo("bindClassId", teachingClass.getId());
        List<ElcTeachingClassBind> bindElcTeachingClassBinds = elcTeachingClassBindDao.selectByExample(bindExample);
        if(CollectionUtil.isNotEmpty(bindElcTeachingClassBinds)) {
        	bindElcTeachingClassBind = bindElcTeachingClassBinds.get(0);
        }
        List<ElcCourseTake> bindTakes = new ArrayList<ElcCourseTake>();
        List<String> bindStudentIds = new ArrayList<>();
        List<Student> bindStudents = new ArrayList<>();
        Long bindTeachingClassId = null;
        if(elcTeachingClassBind !=null) {
        	 bindTeachingClassId =elcTeachingClassBind.getBindClassId();
        	 ElcCourseTake bindParam = new ElcCourseTake();
        	 bindParam.setTeachingClassId(elcTeachingClassBind.getBindClassId());
             bindTakes = courseTakeDao.select(bindParam);
             if(CollectionUtil.isNotEmpty(bindTakes)) {
            	 bindStudentIds = bindTakes.stream().map(ElcCourseTake ::getStudentId).collect(Collectors.toList());
            	 bindStudents = studentDao.findStudentByIds(bindStudentIds);
             }
        }
        if(bindElcTeachingClassBind !=null) {
        	 bindTeachingClassId = bindElcTeachingClassBind.getTeachingClassId();
       	     ElcCourseTake bindParam = new ElcCourseTake();
       	     bindParam.setTeachingClassId(bindElcTeachingClassBind.getTeachingClassId());
             bindTakes = courseTakeDao.select(bindParam);
             if(CollectionUtil.isNotEmpty(bindTakes)) {
            	 bindStudentIds = bindTakes.stream().map(ElcCourseTake ::getStudentId).collect(Collectors.toList());
            	 bindStudents = studentDao.findStudentByIds(bindStudentIds);
             }
        }
        Integer limitnumber  = teachingClass.getNumber() - teachingClass.getReserveNumber();
        List<String> removeStus = new ArrayList<>();
        List<String> bindremoveStus = new ArrayList<>();
        NewGradAndPreFilter gradAndPreFilter =
            new NewGradAndPreFilter(dto, classDao);
        gradAndPreFilter.init();
        NewClassElcConditionFilter elcConditionFilter =
            new NewClassElcConditionFilter(dto,
                classElectiveRestrictAttrDao);
        elcConditionFilter.init();
        //1.删除普通学生
        // 这里做三次的原因是因为有三种学生类型
        for (int i = 0; i < 3; i++)
        {
        	List<Student> stuList = normalStus;
//            List<Student> stuList = normalStus.size() > 0 ? normalStus
//                : (affinityStus.size() > 0 ? affinityStus
//                    : invincibleStus);
            if(i==1) {
            	stuList = affinityStus;
            }else if(i ==2) {
                if (dto.getInvincibleStu())
                {
                	if(limitnumber >0) {
                		invincibleStus.clear();
                	}
                }
            	stuList = invincibleStus;
            }
            
            if (CollectionUtil.isEmpty(stuList))
            {
                continue;
            }
            gradAndPreFilter.execute(stuList, removeStus);
            if (invincibleStus.size() + affinityStus.size()
            + normalStus.size() > limitnumber) {
                int overSize = (invincibleStus.size()
                        + affinityStus.size() + normalStus.size())
                        - limitnumber;
                if(i!=0 && overSize<=0) {
                	break;
                }
                //执行班级匹配
                if(bindTeachingClassId !=null) {
                	//班级匹配交集
                	List<String> onlyList = stuList.stream().map(Student ::getStudentCode).collect(Collectors.toList());
                	//本班级多余学生
                	List<String> onlyRemoveList = stuList.stream().map(Student ::getStudentCode).collect(Collectors.toList());
                	//绑定班级多余学生
                	List<String> bindList = bindStudents.stream().map(Student ::getStudentCode).collect(Collectors.toList());
                	//交集
                	onlyList.retainAll(bindList);
                	onlyRemoveList.removeAll(onlyList);
                	bindList.removeAll(onlyList);
                	removeStus.addAll(onlyRemoveList);
                	bindremoveStus.addAll(bindList);
                	List<Student> saveList = new ArrayList<Student>(stuList);
                	stuList.clear();
                	List<Student> saveStudents = saveList.stream().filter(c->onlyList.contains(c.getStudentCode())).collect(Collectors.toList());
                	stuList.addAll(saveStudents);
                }
                //执行完后人数还是超过上限则进行随机删除
                if(overSize > 0) {
                	elcConditionFilter.execute(stuList, removeStus);
                }
                if (CollectionUtil.isNotEmpty(stuList))
                {
                    //执行完后人数还是超过上限则进行随机删除
                    overSize = (invincibleStus.size()
                        + affinityStus.size() + normalStus.size())
                        - limitnumber;
                    if (overSize > 0)
                    {
                        // 1.普通人数小于超出人数则说明优先人数的人也超了这时需要清空普通人数
                        // 2.普通人数大于超出人数则说明只有普通人数超了，普通人数需要等于超出人数
                        int limitNumber = limitnumber - invincibleStus.size();
                        if(limitnumber == 0) {
                        	limitNumber = 0;
                        }
                        if (stuList.size() < overSize)
                        {
                        	limitNumber = -1;
                        }
                        if(limitNumber>-1) {
                            GradAndPreFilter
                            .randomRemove(removeStus, limitNumber, stuList);
                        }
                    }else {
                    	break;
                    }
                }
            }
            //移除本班级不符合规则学生
            newRemoveAndRecordLog(dto,
                teachingClass,
                removeStus,dto.getLabel(),rebuildCourseRecycles,withdrawTakes);
            //移除绑定班级不符合学生
            if(bindTeachingClassId !=null) {
            	TeachingClass bindClass = classDao.selectByPrimaryKey(bindTeachingClassId);
            	newRemoveAndRecordLog(dto,
                		bindClass,
                		bindremoveStus,dto.getLabel(),rebuildCourseRecycles,withdrawTakes);
            }
        }
    
    
    
    }
	
	private int getChooseObj(String electionObj) {
		Integer chooseObj = null;
		if("ADMIN".equals(electionObj)) {
			chooseObj = 3;
		}else if ("DEPART_ADMIN".equals(electionObj)) {
			chooseObj = 2;
		}else {
			chooseObj = 1;
		}
		return chooseObj;
	}
	
    @Override
    @Transactional
    public void updateClassLimit(Long teachingClassId, TeachingClassLimitVo classVo) {
        // 更新配课建议学生
        suggestStudentDao.deleteByClassId(teachingClassId);
        if (CollectionUtil.isNotEmpty(classVo.getLstSuggestStud()))
        {
            classVo.getLstSuggestStud().forEach(student -> {
                student.setTeachingClassId(teachingClassId);
                suggestStudentDao.insertSelective(student);
            });
        }
        // 更新选课限制专业
        professionDao.deleteByClassId(teachingClassId);
        if (CollectionUtil.isNotEmpty(classVo.getLstElectiveProf()))
        {
            classVo.getLstElectiveProf().forEach(restrict -> {
                restrict.setTeachingClassId(teachingClassId);
                professionDao.insertSelective(restrict);
            });
        }
        // 选课限制
        if (classVo.getElectiveRestrictAttr() != null && classVo.getElectiveRestrictAttr().getId() != null && classVo.getElectiveRestrictAttr().getId() > 0)
        {
            classElectiveRestrictAttrDao.updateByPrimaryKey(classVo.getElectiveRestrictAttr());
        } else
        {
            // 先删除，保证不会出现重复的记录
            classElectiveRestrictAttrDao.deleteByClassId(teachingClassId);
            if (classVo.getElectiveRestrictAttr() == null)
            {
                classVo.setElectiveRestrictAttr(new TeachingClassElectiveRestrictAttr());
            }
            classVo.getElectiveRestrictAttr().setTeachingClassId(teachingClassId);
            classVo.getElectiveRestrictAttr().setCreatedAt(new Date());
            classVo.getElectiveRestrictAttr().setUpdatedAt(new Date());
            classElectiveRestrictAttrDao.insertSelective(classVo.getElectiveRestrictAttr());
        }
    }

    @Override
    public TeachingClassVo getMaleToFemaleRatio(ElcResultQuery elcResultQuery) {

        TeachingClassVo teachingClassVo = classDao.getMaleToFemaleRatio(elcResultQuery);
        return teachingClassVo;
    }

    @Override
    public List<TeachingClassVo> getTeachingClass(Long calendarId, String classCode) {
        PageHelper.startPage(1, 50);
        List<TeachingClassVo> list = teachingClassDao.getTeachingClass(calendarId, classCode);
        for (TeachingClassVo teachingClassVo : list) {
            teachingClassVo.setCourseName(teachingClassVo.getCourseName() + teachingClassVo.getCode());
        }
        return list;
    }

    @Override
    @Transactional
    public void bindClass(Long id, Long bindClassId) {
        List<Long> ids = new ArrayList<>(2);
        ids.add(id);
        ids.add(bindClassId);
        List<TeachingClass> teachingClasses = teachingClassDao.findTeachingClasses(ids);
        if (teachingClasses.size() < 2) {
            throw new ParameterValidateException(I18nUtil.getMsg("baseresservice.parameterError"));
        }
        int count = teachingClassDao.findCount(id, bindClassId);
        if (count == 0) {
            TeachingClassVo c1 = teachingClassDao.findBindClass(id);
            TeachingClassVo c2 = teachingClassDao.findBindClass(bindClassId);
            if (c1 != null && c2 != null) {
                throw new ParameterValidateException("教学班绑定冲突");
            } else if (c1 == null && c2 == null) {
                teachingClassDao.insertBindClass(id, bindClassId);
            } else {
                if (c1 != null) {
                    teachingClassDao.deleteBindClass(id);
                } else {
                    teachingClassDao.deleteBindClass(bindClassId);
                }
                teachingClassDao.insertBindClass(id, bindClassId);
            }
        }

    }
}
