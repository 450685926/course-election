package com.server.edu.election.service.impl;

import static java.util.stream.Collectors.toSet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
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
import com.server.edu.election.studentelec.cache.TeachingClassCache;
import com.server.edu.election.studentelec.context.TimeAndRoom;
import com.server.edu.election.studentelec.service.cache.RoundCacheService;
import com.server.edu.election.studentelec.service.cache.TeachClassCacheService;
import com.server.edu.election.util.ExcelStoreConfig;
import com.server.edu.election.util.TableIndexUtil;
import com.server.edu.election.vo.ElcAffinityCoursesStdsVo;
import com.server.edu.election.vo.ElcResultCountVo;
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
        		condition.setIndex(TableIndexUtil.getIndex(condition.getCalendarId()));
                PageHelper.startPage(page.getPageNum_(), page.getPageSize_());
        		listPage = classDao.listScreeningPage(condition);
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
                PageHelper.startPage(page.getPageNum_(), page.getPageSize_());
        		listPage = classDao.listPage(condition);
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
		if (StringUtils.equals(session.getCurrentRole(), String.valueOf(Constants.ONE)) 
				&& !session.isAdmin()
				&& session.isAcdemicDean()) {
			String faculty = session.getFaculty();
			condition.setFaculty(faculty);
		}
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
        record.setReserveNumberRate((teachingClassVo.getNumber() == null|| teachingClassVo.getNumber().intValue()==0 )?0.0:new BigDecimal(teachingClassVo.getReserveNumber()).divide(new BigDecimal(teachingClassVo.getNumber()),4,BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100)).doubleValue());
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
        TeachingClass record = new TeachingClass();
        record.setId(teachingClass.getId());
        record.setReserveNumber(teachingClass.getReserveNumber());
        record.setReserveNumberRate(teachingClass.getNumber().intValue()==0?0.0:new BigDecimal(teachingClass.getReserveNumber()).divide(new BigDecimal(teachingClass.getNumber()),4,BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100)).doubleValue());
        classDao.updateByPrimaryKeySelective(record);
    }
    
    @Override
	@Transactional
    public void setReserveProportion(ReserveDto reserveDto)
    {
    	BigDecimal reserveProportion = new BigDecimal(reserveDto.getReserveProportion());
    	Example example = new Example(TeachingClass.class);
    	Example.Criteria criteria = example.createCriteria();
    	criteria.andIn("id", reserveDto.getIds());
    	List<TeachingClass> teachingClasses = classDao.selectByExample(example);
    	if(CollectionUtil.isNotEmpty(teachingClasses)) {
        	BigDecimal hundred = new BigDecimal(Constants.HUNDRED);
        	for(TeachingClass temp:teachingClasses) {
        		int reserveNumber = new BigDecimal(temp.getNumber()).multiply(reserveProportion).divide(hundred, BigDecimal.ROUND_UP).intValue();
        		temp.setReserveNumber(reserveNumber);
        		temp.setReserveNumberRate(reserveProportion.doubleValue());
        	}
        	classDao.updateReserveProportion(teachingClasses);
    	}
    }
    @Override
	@Transactional
    public void batchSetReserveNum(ReserveDto reserveDto) {
    	TeachingClass teachingClass = new TeachingClass();
    	teachingClass.setReserveNumber(reserveDto.getReserveNumber());
    	if(reserveDto.getReserveNumberRate() == null){
            for (Long id : reserveDto.getIds()) {
                TeachingClass teachingClass1 = teachingClassDao.selectByPrimaryKey(id);
                teachingClass.setReserveNumberRate(teachingClass1.getNumber().intValue()==0?0.0:new BigDecimal(reserveDto.getReserveNumber()).divide(new BigDecimal(teachingClass1.getNumber()),4,BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100)).doubleValue());
            }
        }else{
            teachingClass.setReserveNumberRate(reserveDto.getReserveNumberRate());
        }

    	Example example = new Example(TeachingClass.class);
    	Example.Criteria criteria = example.createCriteria();
    	criteria.andIn("id", reserveDto.getIds());
    	classDao.updateByExampleSelective(teachingClass, example);
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
            
            if (dto.getInvincibleStu())
            {
                invincibleStus.clear();
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
                
                //1.删除普通学生
                
                
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
                    
                    if (CollectionUtil.isNotEmpty(stuList))
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
                        }else {
                        	break;
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
            	rebuildCourseRecycles.add(rebuildCourseRecycle);
            }
            rebuildCourseRecycleDao.insertList(rebuildCourseRecycles);
            courseTakeService.withdraw(values);
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
		TeachingClassElectiveRestrictAttr attr = new TeachingClassElectiveRestrictAttr();
		attr.setTeachingClassId(teachingClassVo.getId());
		int numberMale = teachingClassVo.getNumberMale();
		int numberFemale = teachingClassVo.getNumberFemale();
		//获取是否是男女班，男1 女2 不区分0
        String limitIsDivsex = teachingClassVo.getLimitIsDivsex();
        if("1".equals(limitIsDivsex)&0!=numberFemale){
            throw new ParameterValidateException(I18nUtil.getMsg("election.male.error"));
        }else if("2".equals(limitIsDivsex)&0!=numberMale){
            throw new ParameterValidateException(I18nUtil.getMsg("election.female.error"));
        }
        //获取实际人数
        int elcNumber = teachingClassVo.getElcNumber();
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
		Example example = new Example(TeachingClassElectiveRestrictAttr.class);
		Example.Criteria criteria = example.createCriteria();
		criteria.andEqualTo("teachingClassId", teachingClassVo.getId());
		TeachingClassElectiveRestrictAttr teachingClassAttr = attrDao.selectOneByExample(example);
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
		ElectionRounds round = roundCacheService.getRound(dto.getRoundId());
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
            	int num = 0;
        		for(Long teachingClassId :teachingClassIds) {
        			AutoRemoveDto autoRemoveDto = new AutoRemoveDto();
        			BeanUtils.copyProperties(dto, autoRemoveDto);
        			autoRemoveDto.setTeachingClassId(teachingClassId);
        			autoRemove(autoRemoveDto);
        			num++;
        			result.setDoneCount(num);
        			this.updateResult(result);
        		}
            }
        });
		return resul;
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
}
