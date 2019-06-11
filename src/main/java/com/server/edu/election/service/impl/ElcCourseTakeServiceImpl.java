package com.server.edu.election.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.server.edu.common.PageCondition;
import com.server.edu.common.locale.I18nUtil;
import com.server.edu.common.rest.PageResult;
import com.server.edu.common.vo.StudentScoreVo;
import com.server.edu.election.constants.ChooseObj;
import com.server.edu.election.constants.CourseTakeType;
import com.server.edu.election.dao.ElcCourseTakeDao;
import com.server.edu.election.dao.ElcLogDao;
import com.server.edu.election.dao.ElectionConstantsDao;
import com.server.edu.election.dao.TeachingClassDao;
import com.server.edu.election.dto.ElcCourseTakeAddDto;
import com.server.edu.election.dto.ElcCourseTakeDto;
import com.server.edu.election.entity.ElcCourseTake;
import com.server.edu.election.entity.ElcLog;
import com.server.edu.election.entity.Student;
import com.server.edu.election.query.ElcCourseTakeQuery;
import com.server.edu.election.rpc.ScoreServiceInvoker;
import com.server.edu.election.service.ElcCourseTakeService;
import com.server.edu.election.studentelec.event.ElectLoadEvent;
import com.server.edu.election.vo.ElcCourseTakeVo;
import com.server.edu.election.vo.ElcLogVo;
import com.server.edu.exception.ParameterValidateException;
import com.server.edu.session.util.SessionUtils;
import com.server.edu.session.util.entity.Session;
import com.server.edu.util.CollectionUtil;

import tk.mybatis.mapper.entity.Example;

@Service
public class ElcCourseTakeServiceImpl implements ElcCourseTakeService
{
    Logger logger = LoggerFactory.getLogger(getClass());
    
    @Autowired
    private ElcCourseTakeDao courseTakeDao;
    
    @Autowired
    private TeachingClassDao classDao;
    
    @Autowired
    private ElcLogDao elcLogDao;
    
    @Autowired
    private ElectionConstantsDao constantsDao;
    
    @Autowired
    private ApplicationContext applicationContext;
    
    @Override
    public PageResult<ElcCourseTakeVo> listPage(
        PageCondition<ElcCourseTakeQuery> page)
    {
        ElcCourseTakeQuery cond = page.getCondition();
        List<String> includeCodes = new ArrayList<>();
        // 1体育课
        if (Objects.equals(cond.getCourseType(), 1))
        {
            String findPECourses = constantsDao.findPECourses();
            if (StringUtils.isNotBlank(findPECourses))
            {
                includeCodes.addAll(Arrays.asList(findPECourses.split(",")));
            }
        }
        else if (Objects.equals(cond.getCourseType(), 2))
        {// 2英语课
            String findEnglishCourses = constantsDao.findEnglishCourses();
            if (StringUtils.isNotBlank(findEnglishCourses))
            {
                includeCodes
                    .addAll(Arrays.asList(findEnglishCourses.split(",")));
            }
        }
        cond.setIncludeCourseCodes(includeCodes);
        PageHelper.startPage(page.getPageNum_(), page.getPageSize_());
        Page<ElcCourseTakeVo> listPage = courseTakeDao.listPage(cond);
        
        PageResult<ElcCourseTakeVo> result = new PageResult<>(listPage);
        return result;
    }
    
    @Transactional
    @Override
    public String add(ElcCourseTakeAddDto add)
    {
        StringBuilder sb = new StringBuilder();
        Date date = new Date();
        Long calendarId = add.getCalendarId();
        List<String> studentIds = add.getStudentIds();
        List<Long> teachingClassIds = add.getTeachingClassIds();
        Integer mode = add.getMode();
        for (String studentId : studentIds)
        {
            for (int i = 0; i < teachingClassIds.size(); i++)
            {
                Long teachingClassId = teachingClassIds.get(i);
                ElcCourseTakeVo vo = courseTakeDao
                    .getTeachingClassInfo(calendarId, teachingClassId, null);
                if (null != vo && vo.getCourseCode() != null)
                {
                    addTake(date, calendarId, studentId, vo, mode);
                }
                else
                {
                    String code = teachingClassId.toString();
                    if (vo != null)
                    {
                        code = vo.getTeachingClassCode();
                    }
                    sb.append("教学班[" + code + "]对应的课程不存在,");
                }
            }
        }
        
        if (sb.length() > 0)
        {
            return sb.substring(0, sb.length() - 1);
        }
        return StringUtils.EMPTY;
    }
    
    @Transactional
    private void addTake(Date date, Long calendarId, String studentId,
        ElcCourseTakeVo vo, Integer mode)
    {
        String courseCode = vo.getCourseCode();
        Long teachingClassId = vo.getTeachingClassId();
        String courseName = vo.getCourseName();
        String teachingClassCode = vo.getTeachingClassCode();
        
        ElcCourseTake record = new ElcCourseTake();
        record.setStudentId(studentId);
        record.setCourseCode(courseCode);
        int selectCount = courseTakeDao.selectCount(record);
        if (selectCount == 0)
        {
            ElcCourseTake take = new ElcCourseTake();
            take.setCalendarId(calendarId);
            take.setChooseObj(ChooseObj.ADMIN.type());
            take.setCourseCode(courseCode);
            take.setCourseTakeType(CourseTakeType.NORMAL.type());
            take.setCreatedAt(date);
            take.setStudentId(studentId);
            take.setTeachingClassId(teachingClassId);
            take.setMode(mode);
            take.setTurn(0);
            courseTakeDao.insertSelective(take);
            // 增加选课人数
            classDao.increElcNumber(teachingClassId);
            // 添加选课日志
            ElcLog log = new ElcLog();
            log.setCalendarId(calendarId);
            log.setCourseCode(courseCode);
            log.setCourseName(courseName);
            Session currentSession = SessionUtils.getCurrentSession();
            log.setCreateBy(currentSession.getUid());
            log.setCreatedAt(date);
            log.setCreateIp(currentSession.getIp());
            log.setMode(ElcLogVo.MODE_2);
            log.setStudentId(studentId);
            log.setTeachingClassCode(teachingClassCode);
            log.setTurn(0);
            log.setType(ElcLogVo.TYPE_1);
            this.elcLogDao.insertSelective(log);
            
            applicationContext
                .publishEvent(new ElectLoadEvent(calendarId, studentId));
        }
    }
    
    @Transactional
    @Override
    public String addByExcel(Long calendarId, List<ElcCourseTakeAddDto> datas,
        Integer mode)
    {
        StringBuilder sb = new StringBuilder();
        Date date = new Date();
        for (ElcCourseTakeAddDto data : datas)
        {
            String studentId = StringUtils.trim(data.getStudentId());
            String teachingClassCode =
                StringUtils.trim(data.getTeachingClassCode());
            
            if (StringUtils.isNotBlank(studentId)
                && StringUtils.isNotBlank(teachingClassCode))
            {
                ElcCourseTakeVo vo = this.courseTakeDao
                    .getTeachingClassInfo(calendarId, null, teachingClassCode);
                
                if (null != vo && vo.getCourseCode() != null)
                {
                    addTake(date, calendarId, studentId, vo, mode);
                }
                else
                {
                    sb.append("教学班[" + teachingClassCode + "]对应的课程不存在,");
                }
                
            }
        }
        if (sb.length() > 0)
        {
            return sb.substring(0, sb.length() - 1);
        }
        return StringUtils.EMPTY;
    }
    
    @Transactional
    @Override
    public void withdraw(List<ElcCourseTake> value)
    {
        Map<String, ElcCourseTakeVo> classInfoMap = new HashMap<>();
        
        List<ElcLog> logList = new ArrayList<>();
        Map<String, ElcCourseTake> withdrawMap = new HashMap<>();
        for (ElcCourseTake take : value)
        {
            Long calendarId = take.getCalendarId();
            String studentId = take.getStudentId();
            Long teachingClassId = take.getTeachingClassId();
            //删除选课记录
            Example example = new Example(ElcCourseTake.class);
            example.createCriteria()
                .andEqualTo("calendarId", calendarId)
                .andEqualTo("studentId", studentId)
                .andEqualTo("teachingClassId", teachingClassId);
            courseTakeDao.deleteByExample(example);
            //减少选课人数
            classDao.decrElcNumber(teachingClassId);
            
            ElcCourseTakeVo vo = null;
            String key = calendarId + "-" + teachingClassId;
            if (!classInfoMap.containsKey(key))
            {
                vo = this.courseTakeDao
                    .getTeachingClassInfo(calendarId, teachingClassId, null);
                classInfoMap.put(key, vo);
            }
            else
            {
                vo = classInfoMap.get(key);
            }
            
            // 记录退课日志
            if (null != vo)
            {
                String teachingClassCode = vo.getTeachingClassCode();
                ElcLog log = new ElcLog();
                log.setCalendarId(calendarId);
                log.setCourseCode(vo.getCourseCode());
                log.setCourseName(vo.getCourseName());
                Session currentSession = SessionUtils.getCurrentSession();
                log.setCreateBy(currentSession.getUid());
                log.setCreatedAt(new Date());
                log.setCreateIp(currentSession.getIp());
                log.setMode(ElcLogVo.MODE_2);
                log.setStudentId(studentId);
                log.setTeachingClassCode(teachingClassCode);
                log.setTurn(0);
                log.setType(ElcLogVo.TYPE_2);
                logList.add(log);
                
                vo.setCalendarId(calendarId);
                vo.setStudentId(studentId);
                withdrawMap.put(
                    String
                        .format("%s-%s", vo.getCalendarId(), vo.getStudentId()),
                    vo);
            }
            else
            {
                logger.warn(
                    "not find teachingClassInfo calendarId={},teachingClassId={}",
                    calendarId,
                    teachingClassId);
            }
        }
        if (CollectionUtil.isNotEmpty(logList))
        {
            this.elcLogDao.insertList(logList);
            for (Entry<String, ElcCourseTake> entry : withdrawMap.entrySet())
            {
                ElcCourseTake take = entry.getValue();
                applicationContext.publishEvent(new ElectLoadEvent(
                    take.getCalendarId(), take.getStudentId()));
            }
        }
    }
    
    /**
    *@Description: 查找加课学生
    *@Param:
    *@return: 
    *@Author: bear
    *@date: 2019/2/23 14:17
    */
    @Override
    public PageResult<Student> findStudentList(
        PageCondition<ElcCourseTakeQuery> condition)
    {
        PageHelper.startPage(condition.getPageNum_(), condition.getPageSize_());
        Page<Student> page =
            courseTakeDao.findStudentList(condition.getCondition());
        return new PageResult<>(page);
    }

    /**
    *@Description: 查询学籍异动学生选课信息
    *@Param:
    *@return: 
    *@Author: bear
    *@date: 2019/6/5 16:29
    */
    @Override
    public List<ElcCourseTakeVo> page2StuAbnormal(ElcCourseTakeQuery query) {
        String studentId = query.getStudentId();
        Long calendarId = query.getCalendarId();
        //查询学生已选课程
        List<ElcCourseTakeVo> elcList =new ArrayList<>();
        PageCondition<ElcCourseTakeQuery> page =new PageCondition<>();
        page.setPageNum_(1);
        page.setPageSize_(1000);
        page.setCondition(query);
        PageResult<ElcCourseTakeVo> listPage = listPage(page);
        if(listPage!=null){
            elcList = listPage.getList();
        }
        if(CollectionUtil.isEmpty(elcList)){
            return null;
        }
        List<StudentScoreVo> stuScore = ScoreServiceInvoker.findStuScoreByCalendarIdAndStudentCode(calendarId, studentId);
        if(CollectionUtil.isEmpty(stuScore)){
            return elcList;
        }
        List<String> codes = stuScore.stream().filter(vo ->StringUtils.isNotBlank(vo.getCourseCode())).map(StudentScoreVo::getCourseCode).collect(Collectors.toList());
        List<ElcCourseTakeVo> collect = elcList.stream().filter(vo -> !codes.contains(vo.getCourseCode())).collect(Collectors.toList());
        return collect;
    }

    /**
     *@Description: 学籍异动学生退课
     *@Param:
     *@return:
     *@Author: bear
     *@date: 2019/6/5 16:29
     */
    @Override
    public void withdraw2StuAbnormal(ElcCourseTakeQuery query) {
        List<ElcCourseTakeVo> elcCourseTakeVos = page2StuAbnormal(query);
        List<ElcCourseTake> list =new ArrayList<>();
        if(CollectionUtil.isNotEmpty(elcCourseTakeVos)){
            for (ElcCourseTakeVo elcCourseTakeVo : elcCourseTakeVos) {
                ElcCourseTake courseTake=new ElcCourseTake();
                courseTake.setCalendarId(elcCourseTakeVo.getCalendarId());
                courseTake.setStudentId(elcCourseTakeVo.getStudentId());
                courseTake.setTeachingClassId(elcCourseTakeVo.getTeachingClassId());
                list.add(courseTake);
            }
            withdraw(list);
        }
    }


    
    @Override
    @Transactional
	public int editStudyType(ElcCourseTakeDto elcCourseTakeDto) {
    	if(elcCourseTakeDto.getCourseTakeType()==null||CollectionUtil.isEmpty(elcCourseTakeDto.getIds())) {
			throw new ParameterValidateException(I18nUtil.getMsg("baseresservice.parameterError"));
    	}
    	int result = courseTakeDao.editStudyType(elcCourseTakeDto.getCourseTakeType(), elcCourseTakeDto.getIds());
    	return result;
    }

}
