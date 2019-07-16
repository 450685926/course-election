package com.server.edu.election.service.impl;

import static java.util.stream.Collectors.toSet;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.ibm.icu.math.BigDecimal;
import com.server.edu.common.PageCondition;
import com.server.edu.common.locale.I18nUtil;
import com.server.edu.common.rest.PageResult;
import com.server.edu.dictionary.DictTypeEnum;
import com.server.edu.dictionary.service.DictionaryService;
import com.server.edu.election.constants.Constants;
import com.server.edu.election.dao.ElcAffinityCoursesStdsDao;
import com.server.edu.election.dao.ElcClassEditAuthorityDao;
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
import com.server.edu.election.dto.ReserveDto;
import com.server.edu.election.dto.Student4Elc;
import com.server.edu.election.dto.SuggestProfessionDto;
import com.server.edu.election.entity.ElcAffinityCoursesStds;
import com.server.edu.election.entity.ElcClassEditAuthority;
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
import com.server.edu.exception.ParameterValidateException;
import com.server.edu.session.util.SessionUtils;
import com.server.edu.session.util.entity.Session;
import com.server.edu.util.CalUtil;
import com.server.edu.util.CollectionUtil;
import com.server.edu.util.FileUtil;
import com.server.edu.util.excel.ExcelWriterUtil;
import com.server.edu.util.excel.GeneralExcelCell;
import com.server.edu.util.excel.GeneralExcelDesigner;
import com.server.edu.util.excel.GeneralExcelUtil;
import com.server.edu.util.excel.export.ExcelExecuter;
import com.server.edu.util.excel.export.ExcelResult;
import com.server.edu.util.excel.export.ExportExcelUtils;

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
    
    @Autowired
    private ElcClassEditAuthorityDao elcClassEditAuthorityDao;
    
    @Autowired
    private DictionaryService dictionaryService;
    // 文件缓存目录
    @Value("${task.cache.directory}")
    private String cacheDirectory;
    
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
    public void adjustClassNumber(TeachingClassVo teachingClassVo)
    {
    	Session session = SessionUtils.getCurrentSession();
    	Example example = new Example(ElcClassEditAuthority.class);
    	Example.Criteria criteria = example.createCriteria();
    	criteria.andEqualTo("calendarId", teachingClassVo.getCalendarId());
    	criteria.andEqualTo("status", Constants.ZERO);
    	ElcClassEditAuthority editAuthority =elcClassEditAuthorityDao.selectOneByExample(example);
    	if(session.isAcdemicDean()&&editAuthority!=null) {
    		throw new ParameterValidateException(I18nUtil.getMsg("election.noClassEditAuthority")); 
    	}
        TeachingClass record = new TeachingClass();
        record.setId(teachingClassVo.getId());
        record.setNumber(teachingClassVo.getNumber());
        classDao.updateByPrimaryKeySelective(record);
    }
    
    @Override
    public void setReserveNum(TeachingClass teachingClass)
    {
        TeachingClass record = new TeachingClass();
        record.setId(teachingClass.getId());
        record.setReserveNumber(teachingClass.getReserveNumber());
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
        	}
        	classDao.updateReserveProportion(teachingClasses);
    	}
    }
    @Override
	@Transactional
    public void batchSetReserveNum(ReserveDto reserveDto) {
    	TeachingClass teachingClass = new TeachingClass();
    	teachingClass.setReserveNumber(reserveDto.getReserveNumber());
    	Example example = new Example(TeachingClass.class);
    	Example.Criteria criteria = example.createCriteria();
    	criteria.andIn("id", reserveDto.getIds());
    	classDao.updateByExampleSelective(teachingClass, example);
    }
    @Override
	@Transactional
    public void release(ReserveDto reserveDto) {
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
			PageResult<ElcResultDto> elceResultByStudent = new PageResult<>(elcResultList);
			elcResultCountVo.setPageNum_(elceResultByStudent.getPageNum_());
			elcResultCountVo.setPageSize_(elceResultByStudent.getPageSize_());
			elcResultCountVo.setTotal_(elceResultByStudent.getTotal_());
			elcResultCountVo.setList(elceResultByStudent.getList());
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
			elcResultCountVo.setElcNumberByFaculty(elcNumberByFaculty);
			PageResult<ElcResultDto> elceResultByFaculty = new PageResult<>(eleResultByFacultyList);
			elcResultCountVo.setPageNum_(elceResultByFaculty.getPageNum_());
			elcResultCountVo.setPageSize_(elceResultByFaculty.getPageSize_());
			elcResultCountVo.setTotal_(elceResultByFaculty.getTotal_());
			elcResultCountVo.setList(elceResultByFaculty.getList());
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
		page.getCondition().setManagerDeptId(Constants.ONE+"");
		Page<Student4Elc> result = studentDao.getAllNonSelectedCourseStudent(page.getCondition());
		return new PageResult<>(result);
	}

	/**
	 * 导出研究生未选课名单
	 */
	@Override
	public ExcelResult export(ElcResultQuery condition) {
        logger.info("缓存目录："+cacheDirectory);
        FileUtil.mkdirs(cacheDirectory);
        //删除超过30天的文件
        FileUtil.deleteFile(cacheDirectory, 30);
		condition.setManagerDeptId(Constants.ONE+"");
		ExcelResult excelResult = ExportExcelUtils.submitTask("YanJiuShengWeiXuanKeMingDan", new ExcelExecuter() {
            @Override
            public GeneralExcelDesigner getExcelDesigner() {
                ExcelResult result = this.getResult();
                PageCondition<ElcResultQuery> pageCondition = new PageCondition<ElcResultQuery>();
                pageCondition.setCondition(condition);
                pageCondition.setPageSize_(100);
                int pageNum = 0;
                pageCondition.setPageNum_(pageNum);
                List<Student4Elc> list = new ArrayList<>();
                while (true)
                {
                    pageNum++;
                    pageCondition.setPageNum_(pageNum);
                    PageResult<Student4Elc> rollBookList = getStudentPage(pageCondition);
                    list.addAll(rollBookList.getList());

                    result.setTotal((int)rollBookList.getTotal_());
                    Double count = list.size() / 1.5;
                    result.setDoneCount(count.intValue());
                    this.updateResult(result);

                    if (rollBookList.getTotal_() <= list.size())
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

    @SuppressWarnings("all")
    private GeneralExcelDesigner getDesign() {
        GeneralExcelDesigner design = new GeneralExcelDesigner();
        design.setNullCellValue("");
        design.addCell("学号", "studentId");
        design.addCell("姓名", "name");
        design.addCell("培养层次", "trainingLevel").setValueHandler(
                (String value, Object rawData, GeneralExcelCell cell) -> {
                    String dict = dictionaryService
                        .query(DictTypeEnum.X_PYCC.getType(), value);
                    return dict;
                });
        design.addCell("培养类别", "degreeCategory").setValueHandler(
                (String value, Object rawData, GeneralExcelCell cell) -> {
                    String dict = dictionaryService
                        .query(DictTypeEnum.X_PYLB.getType(), value);
                    return dict;
                });
        design.addCell("学位类型", "degreeType").setValueHandler(
                (String value, Object rawData, GeneralExcelCell cell) -> {
                    String dict = dictionaryService
                        .query(DictTypeEnum.X_XWLX.getType(), value);
                    return dict;
                });
        design.addCell("学习形式", "formLearning").setValueHandler(
                (String value, Object rawData, GeneralExcelCell cell) -> {
                    String dict = dictionaryService
                        .query(DictTypeEnum.X_XXXS.getType(), value);
                    return dict;
                });
        design.addCell("学院", "faculty").setValueHandler(
                (String value, Object rawData, GeneralExcelCell cell) -> {
                    String dict = dictionaryService
                        .query(DictTypeEnum.X_YX.getType(), value);
                    return dict;
                });
        design.addCell("专业", "profession").setValueHandler(
                (String value, Object rawData, GeneralExcelCell cell) -> {
                    String dict = dictionaryService
                        .query(DictTypeEnum.G_ZY.getType(), value);
                    return dict;
                });
        design.addCell("入学季节", "enrolSeason").setValueHandler(
                (String value, Object rawData, GeneralExcelCell cell) -> {
                    String dict = dictionaryService
                        .query(DictTypeEnum.X_RXJJ.getType(), value);
                    return dict;
                });
        return design;
    }

	@Override
	public ExcelResult elcResultCountByStudentExport(ElcResultQuery condition) {
        logger.info("缓存目录："+cacheDirectory);
        FileUtil.mkdirs(cacheDirectory);
        //删除超过30天的文件
        FileUtil.deleteFile(cacheDirectory, 30);
		ExcelResult excelResult = ExportExcelUtils.submitTask("YanJiuShengXuanKeJieGuoTongJi", new ExcelExecuter() {
            @Override
            public GeneralExcelDesigner getExcelDesigner() {
                ExcelResult result = this.getResult();
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
                    ElcResultCountVo elcResultCountVo = elcResultCountByStudent(pageCondition);
                    list.addAll(elcResultCountVo.getList());

                    result.setTotal((int)elcResultCountVo.getTotal_());
                    Double count = list.size() / 1.5;
                    result.setDoneCount(count.intValue());
                    this.updateResult(result);

                    if (elcResultCountVo.getTotal_() <= list.size())
                    {
                        break;
                    }
                }
                //组装excel
                GeneralExcelDesigner design = getDesign4ResultCount(condition.getDimension());
                //将数据放入excel对象中
                design.setDatas(list);
                result.setDoneCount(list.size());
                return design;
            }
        });
        return excelResult;
	}
	
	@SuppressWarnings("all")
    private GeneralExcelDesigner getDesign4ResultCount(Integer dimension) {
        GeneralExcelDesigner design = new GeneralExcelDesigner();
        design.setNullCellValue("");
        if(dimension.intValue() == Constants.ONE){
        	design.addCell("年级", "grade");
            design.addCell("培养层次", "trainingLevel").setValueHandler(
                    (String value, Object rawData, GeneralExcelCell cell) -> {
                        String dict = dictionaryService
                            .query(DictTypeEnum.X_PYCC.getType(), value);
                        return dict;
                    });
            design.addCell("培养类别", "trainingCategory").setValueHandler(
                    (String value, Object rawData, GeneralExcelCell cell) -> {
                        String dict = dictionaryService
                            .query(DictTypeEnum.X_PYLB.getType(), value);
                        return dict;
                    });
            design.addCell("学位类型", "degreeType").setValueHandler(
                    (String value, Object rawData, GeneralExcelCell cell) -> {
                        String dict = dictionaryService
                            .query(DictTypeEnum.X_XWLX.getType(), value);
                        return dict;
                    });
            design.addCell("学习形式", "formLearning").setValueHandler(
                    (String value, Object rawData, GeneralExcelCell cell) -> {
                        String dict = dictionaryService
                            .query(DictTypeEnum.X_XXXS.getType(), value);
                        return dict;
                    });
            design.addCell("人数", "studentNum");
            design.addCell("已选人数", "numberOfelectedPersons");
            design.addCell("未选人数", "numberOfNonCandidates");
            design.addCell("已选人数百分比（%）", "numberOfelectedPersonsPoint");
        }else{
	        design.addCell("年级", "grade");
	        design.addCell("学院", "faculty").setValueHandler(
	                (String value, Object rawData, GeneralExcelCell cell) -> {
	                    String dict = dictionaryService
	                        .query(DictTypeEnum.X_YX.getType(), value);
	                    return dict;
	                });
	        design.addCell("专业", "profession").setValueHandler(
	                (String value, Object rawData, GeneralExcelCell cell) -> {
	                    String dict = dictionaryService
	                        .query(DictTypeEnum.G_ZY.getType(), value);
	                    return dict;
	                });
	        design.addCell("人数", "studentNum");
	        design.addCell("已选人数", "numberOfelectedPersons");
	        design.addCell("未选人数", "numberOfNonCandidates");
	        design.addCell("已选人数百分比（%）", "numberOfelectedPersonsPoint");
        }
        return design;
    }
}
