package com.server.edu.election.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.server.edu.common.vo.SchoolCalendarVo;
import com.server.edu.election.rpc.BaseresServiceInvoker;
import com.server.edu.util.DateUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.server.edu.common.PageCondition;
import com.server.edu.common.enums.UserTypeEnum;
import com.server.edu.common.locale.I18nUtil;
import com.server.edu.election.constants.Constants;
import com.server.edu.election.dao.ElcCourseTakeDao;
import com.server.edu.election.dao.ElcLogDao;
import com.server.edu.election.dao.ElcMedWithdrawDao;
import com.server.edu.election.dao.ElcMedWithdrawRuleRefCourDao;
import com.server.edu.election.dao.ElcMedWithdrawRulesDao;
import com.server.edu.election.dao.ElecRoundCourseDao;
import com.server.edu.election.dao.ElecRoundsDao;
import com.server.edu.election.dao.ElectionConstantsDao;
import com.server.edu.election.dao.TeachingClassDao;
import com.server.edu.election.dto.CourseOpenDto;
import com.server.edu.election.dto.ElcMedWithdrawDto;
import com.server.edu.election.dto.ElcMedWithdrawRuleRefCourDto;
import com.server.edu.election.entity.ElcCourseTake;
import com.server.edu.election.entity.ElcLog;
import com.server.edu.election.entity.ElcMedWithdraw;
import com.server.edu.election.entity.ElcMedWithdrawRules;
import com.server.edu.election.entity.ElectionConstants;
import com.server.edu.election.entity.ElectionRounds;
import com.server.edu.election.query.ElcResultQuery;
import com.server.edu.election.service.ElcMedWithdrawService;
import com.server.edu.election.studentelec.event.ElectLoadEvent;
import com.server.edu.election.util.TableIndexUtil;
import com.server.edu.election.vo.ElcCourseTakeVo;
import com.server.edu.election.vo.ElcMedWithdrawRuleRefCourVo;
import com.server.edu.election.vo.TeachingClassVo;
import com.server.edu.exception.ParameterValidateException;
import com.server.edu.session.util.SessionUtils;
import com.server.edu.session.util.entity.Session;
import com.server.edu.util.CollectionUtil;

import tk.mybatis.mapper.entity.Example;
@Service
public class ElcMedWithdrawServiceImpl implements ElcMedWithdrawService {
    @Autowired
    private ElcCourseTakeDao elcCourseTakeDao;
    @Autowired
    private ElcMedWithdrawRuleRefCourDao elcMedWithdrawRuleRefCourDao;
    @Autowired
    private ElcMedWithdrawRulesDao elcMedWithdrawRulesDao;
    
    @Autowired
    private ElectionConstantsDao electionConstantsDao;
    
    @Autowired
    private ElecRoundsDao elecRoundsDao;
    
    @Autowired
    private ElecRoundCourseDao elcRoundsCourDao;
    
    @Autowired
    private TeachingClassDao teachingClassDao;
    
    @Autowired
    private ElcLogDao elcLogDao;
    
    @Autowired
    private ElcMedWithdrawDao elcMedWithdrawDao;
    
    @Autowired
    private ApplicationContext applicationContext;
	@Override
	public PageInfo<ElcCourseTakeVo> page(PageCondition<ElcMedWithdrawDto> condition) {
		// TODO Auto-generated method stub
        List<ElcCourseTakeVo> elcCourseTakes = new ArrayList<>();
        ElcMedWithdrawDto dto = condition.getCondition();
        Session session = SessionUtils.getCurrentSession();
        String uid = session.realUid();
        int userType = session.realType();
        if (UserTypeEnum.STUDENT.is(userType))
        {
            PageHelper.startPage(condition.getPageNum_(),
                condition.getPageSize_());
    		dto.setIndex(TableIndexUtil.getIndex(dto.getCalendarId()));
    		dto.setStudentId(uid);
            elcCourseTakes =
                elcCourseTakeDao.getElcMedWithdraw(dto);
        }
        PageInfo<ElcCourseTakeVo> pageInfo = new PageInfo<>(elcCourseTakes);
        return pageInfo;
	}

	@Transactional
	@Override
	public int medWithdraw(Long id, String projectId) {
		//1.查询选课信息
		ElcCourseTake elcCourseTake = elcCourseTakeDao.selectByPrimaryKey(id);
		if(elcCourseTake==null) {
            throw new ParameterValidateException(
                    I18nUtil.getMsg("common.notExist",I18nUtil.getMsg("elcMedWithdraw.elcinfo")));
		}
        ElcResultQuery elcResultQuery = new ElcResultQuery();
        elcResultQuery
            .setTeachingClassId(elcCourseTake.getTeachingClassId());
        //查询教学班相关信息
        List<TeachingClassVo> teachingClassList =
            teachingClassDao.findTeachingClass(elcResultQuery);
        if (CollectionUtil.isEmpty(teachingClassList))
        {
            throw new ParameterValidateException(
                    I18nUtil.getMsg("elcMedWithdraw.teachingClassError"));
        }
        TeachingClassVo teachingClassVo = teachingClassList.get(0);
        //校验期中退课规则
		medWithdrawCheck(projectId,elcCourseTake,teachingClassVo);
		//保存期中退课日志
        ElcLog elcLog = new ElcLog();
        BeanUtils.copyProperties(elcCourseTake, elcLog);
        elcLog.setId(null);
        elcLog.setCourseCode(teachingClassVo.getCourseCode());
        elcLog.setCourseCode(teachingClassVo.getCourseName());
        elcLog.setTeachingClassCode(teachingClassVo.getCode());
        elcLog.setType(Constants.THREE);
        String ip  = SessionUtils.getCurrentSession().getIp();
        elcLog.setCreateIp(ip);
        int result = elcLogDao.insertSelective(elcLog);
        if(result<Constants.ZERO) {
            throw new ParameterValidateException(
                    I18nUtil.getMsg("common.saveError",I18nUtil.getMsg("elcMedWithdraw.elcLog")));
        }
        ElcMedWithdraw elcMedWithdraw = new ElcMedWithdraw();
        BeanUtils.copyProperties(elcCourseTake, elcMedWithdraw);
        result = elcMedWithdrawDao.insertSelective(elcMedWithdraw);
        if(result<Constants.ZERO) {
            throw new ParameterValidateException(
                    I18nUtil.getMsg("common.saveError",I18nUtil.getMsg("elcMedWithdraw.info")));
        }
        result = elcCourseTakeDao.deleteByPrimaryKey(id);
        applicationContext.publishEvent(new ElectLoadEvent(
        		elcCourseTake.getCalendarId(), elcCourseTake.getStudentId()));
		return result;
	}

	@Transactional
	@Override
	public int cancelMedWithdraw(Long medWithdrawId,String projectId) {
		ElcMedWithdraw elcMedWithdraw = elcMedWithdrawDao.selectByPrimaryKey(medWithdrawId);
		if(elcMedWithdraw==null) {
			 throw new ParameterValidateException(
	                    I18nUtil.getMsg("common.notExist",I18nUtil.getMsg("elcMedWithdraw.info")));
		}
		Date date = new Date();
		ElcMedWithdrawRules elcMedWithdrawRules = getMedWithdrawRules(projectId, elcMedWithdraw.getCalendarId());
        if(elcMedWithdrawRules==null) {
			 throw new ParameterValidateException(
	                    I18nUtil.getMsg("common.notExist",I18nUtil.getMsg("elcMedWithdraw.rule")));
        }
        if(elcMedWithdrawRules.getBeginTime().getTime()>=date.getTime()||date.getTime()>=elcMedWithdrawRules.getEndTime().getTime()) {
        	 throw new ParameterValidateException(
	                    I18nUtil.getMsg("elcMedWithdraw.outRuleTime"));
        }
		ElcCourseTake elcCourseTake = new ElcCourseTake();
		BeanUtils.copyProperties(elcMedWithdraw, elcCourseTake);
		int result = elcCourseTakeDao.insertSelective(elcCourseTake);
		if(result<Constants.ZERO) {
            throw new ParameterValidateException(
                    I18nUtil.getMsg("common.saveError",I18nUtil.getMsg("elcMedWithdraw.elcinfo")));
		}
		result = elcMedWithdrawDao.deleteByPrimaryKey(medWithdrawId);
        applicationContext.publishEvent(new ElectLoadEvent(
        		elcCourseTake.getCalendarId(), elcCourseTake.getStudentId()));
		return result;
	}
	
    //期中退课规则校验
    private void medWithdrawCheck(String projectId, ElcCourseTake elcCourseTake,TeachingClassVo teachingClassVo)
    {
        //查询选择学期的退课规则和课程
        ElcMedWithdrawRuleRefCourDto ruleDto =
            new ElcMedWithdrawRuleRefCourDto();
        ruleDto.setCalendarId(elcCourseTake.getCalendarId());
        List<ElcMedWithdrawRuleRefCourVo> list =
            elcMedWithdrawRuleRefCourDao.selectMedRuleRefCours(ruleDto);
        List<ElcMedWithdrawRuleRefCourVo> refList = list.stream()
            .filter(c -> elcCourseTake.getTeachingClassId()
                .equals(c.getTeachingClassId()))
            .collect(Collectors.toList());
        if (CollectionUtil.isEmpty(refList))
        {
            throw new ParameterValidateException(
                    I18nUtil.getMsg("elcMedWithdraw.courseExist"));
        }
        //获取期中退课规则
        ElcMedWithdrawRules elcMedWithdrawRules = getMedWithdrawRules(projectId, elcCourseTake.getCalendarId());
        if(elcMedWithdrawRules==null) {
			 throw new ParameterValidateException(
	                    I18nUtil.getMsg("common.notExist",I18nUtil.getMsg("elcMedWithdraw.rule")));
        }
        //规则启用
        if (elcMedWithdrawRules.getOpenFlag())
        {
            //查询教学班信息得到周数
            ElcResultQuery elcResultQuery = new ElcResultQuery();
            elcResultQuery
                .setTeachingClassId(elcCourseTake.getTeachingClassId());
            List<TeachingClassVo> teachingClassList =
                teachingClassDao.findTeachingClass(elcResultQuery);
            if (CollectionUtil.isEmpty(teachingClassList))
            {
                throw new ParameterValidateException(
                        I18nUtil.getMsg("elcMedWithdraw.teachingClassError"));
            }
            //课程结束周小于等于不得退课
            SchoolCalendarVo schoolCalendarVo = BaseresServiceInvoker.getSchoolCalendarById(elcCourseTake.getCalendarId());
            //如果选课规则没有校历时间,抛出异常
            if(null==schoolCalendarVo){
                throw new ParameterValidateException( I18nUtil.getMsg("elcMedWithdraw.courseEndWeek"));
            }
            //将此规则的开始校历时间包装成Date对象
            Date date = new Date(schoolCalendarVo.getBeginDay()) ;
            //获取课程的开始周数
           int startWeek =  teachingClassVo.getStartWeek();
           //小于等于不得退课获取退课规则的的周数
           int week = elcMedWithdrawRules.getCourseEndWeek();
            //获取最早退课周数时间点1
           long minTime =  DateUtil.addDay(date,(startWeek+week)*7 ).getTime();
           //获获取当前时间
            long nowTime = new Date(). getTime();
           //如果当前时间小于规则时间,不可以退课
            if(nowTime<minTime){
                throw new ParameterValidateException(
                        I18nUtil.getMsg("elcMedWithdraw.courseEndWeek"));
            }
            //如果退课时间不在退课规则范围内,不可以退课
            long beginTime =  elcMedWithdrawRules.getBeginTime().getTime();//规则开始时间
            long endTime =  elcMedWithdrawRules.getBeginTime().getTime();//规则结束时间
            if(nowTime>endTime||nowTime<beginTime){
                throw new ParameterValidateException(
                        I18nUtil.getMsg("elcMedWithdraw.outRuleTime"));
            }

           /* int week = new Becimal(teachingClassVo.getPeriod())
                   .divide(new BigDecimal(teachingClassVo.getWeekHour()),
                    BigDecimal.ROUND_HALF_UP)
                .intValue();
            if (elcMedWithdrawRules.getCourseEndWeek() > week)
            {
                throw new ParameterValidateException(
                        I18nUtil.getMsg("elcMedWithdraw.courseEndWeek"));
            }*/
            //外语强化班不得退课
            if (elcMedWithdrawRules.getEnglishCourse())
            {
                Example constant = new Example(ElectionConstants.class);
                Example.Criteria constantCriteria = constant.createCriteria();
                List<String> keys = new ArrayList<>();
                keys.add("LANGUAGECOURSEFORGERMANY");//德强
                keys.add("LANGUAGECOURSEFORFRENCE");//法强
                constantCriteria.andEqualTo("managerDeptId",
                    projectId);
                constantCriteria.andIn("key", keys);
                List<ElectionConstants> electionConstants =
                    electionConstantsDao.selectByExample(constant);
                StringBuilder stringBuilder = new StringBuilder();
                for (ElectionConstants elc : electionConstants)
                {
                    stringBuilder.append(elc.getValue());
                    stringBuilder.append(",");
                }
                String courses =
                    stringBuilder.deleteCharAt(stringBuilder.length() - 1)
                        .toString();
                if (courses.contains(elcCourseTake.getCourseCode()))
                {
                    throw new ParameterValidateException(
                            I18nUtil.getMsg("elcMedWithdraw.englishCourse"));
                }
                
            }
            //体育课不得退课
            if (elcMedWithdrawRules.getPeCourse())
            {
                Example constant = new Example(ElectionConstants.class);
                Example.Criteria constantCriteria = constant.createCriteria();
                constantCriteria.andEqualTo("managerDeptId",
                    projectId);
                constantCriteria.andEqualTo("key", "PE_COURSE_CODES");
                ElectionConstants electionConstants =
                    electionConstantsDao.selectOneByExample(constant);
                String courses = electionConstants.getValue();
                if (courses.contains(elcCourseTake.getCourseCode()))
                {
                    throw new ParameterValidateException(
                            I18nUtil.getMsg("elcMedWithdraw.pcCourse"));
                }
            }
            //实践课不得退课
            if (elcMedWithdrawRules.getPracticeCourse())
            {
                Example rounds = new Example(ElectionRounds.class);
                Example.Criteria roundsCriteria = rounds.createCriteria();
                roundsCriteria.andEqualTo("projectId", projectId);
                roundsCriteria.andEqualTo("calendarId",
                    elcCourseTake.getCalendarId());
                roundsCriteria.andEqualTo("mode", Constants.PRACTICE_COURSE_);
                roundsCriteria.andEqualTo("openFlag", Constants.IS_OPEN);
                List<ElectionRounds> roundsList =
                    elecRoundsDao.selectByExample(rounds);
                if (CollectionUtil.isNotEmpty(roundsList))
                {
                    List<Long> roundIds = roundsList.stream()
                        .map(ElectionRounds::getId)
                        .collect(Collectors.toList());
                     List<CourseOpenDto> elcRoundsCours= elcRoundsCourDao.getAddedCourseByRoundIds(roundIds);
                    if (CollectionUtil.isNotEmpty(elcRoundsCours))
                    {
                        List<String> courseCodes = elcRoundsCours.stream()
                            .map(CourseOpenDto::getCourseCode)
                            .collect(Collectors.toList());
                        if (courseCodes.contains(elcCourseTake.getCourseCode()))
                        {
                            throw new ParameterValidateException(I18nUtil
                                    .getMsg("elcMedWithdraw.practiceCourse"));
                        }
                    }
                    
                }
                
            }
            //重修课程不得退课
            if (elcMedWithdrawRules.getRetakeCourse())
            {
                if (Constants.REBUILD_CALSS
                    .equals(elcCourseTake.getCourseTakeType().toString()))
                {
                    throw new ParameterValidateException(
                            I18nUtil.getMsg("elcMedWithdraw.retakeCourse"));
                }
            }
        }
    }

	private ElcMedWithdrawRules getMedWithdrawRules(String projectId,Long calendarId) {
		Example elcExample = new Example(ElcMedWithdrawRules.class);
        Example.Criteria elcCriteria = elcExample.createCriteria();
        elcCriteria.andEqualTo("projectId", projectId);
        elcCriteria.andEqualTo("calendarId", calendarId);
        ElcMedWithdrawRules elcMedWithdrawRules =
            elcMedWithdrawRulesDao.selectOneByExample(elcExample);
		return elcMedWithdrawRules;
	}

}
