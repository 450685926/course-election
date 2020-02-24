package com.server.edu.election.service.impl;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.server.edu.common.PageCondition;
import com.server.edu.common.locale.I18nUtil;
import com.server.edu.common.rest.PageResult;
import com.server.edu.election.constants.Constants;
import com.server.edu.election.constants.ElectRuleType;
import com.server.edu.election.dao.CourseOpenDao;
import com.server.edu.election.dao.ElcCourseTakeDao;
import com.server.edu.election.dao.ElecRoundsDao;
import com.server.edu.election.dao.ElectionApplyCoursesDao;
import com.server.edu.election.dao.ElectionApplyDao;
import com.server.edu.election.dto.CourseDto;
import com.server.edu.election.dto.ElectionApplyDto;
import com.server.edu.election.dto.ElectionApplyRejectDto;
import com.server.edu.election.entity.Course;
import com.server.edu.election.entity.CourseOpen;
import com.server.edu.election.entity.ElectionApply;
import com.server.edu.election.entity.ElectionApplyCourses;
import com.server.edu.election.entity.ElectionRounds;
import com.server.edu.election.service.ElectionApplyService;
import com.server.edu.election.studentelec.utils.ElecContextUtil;
import com.server.edu.election.util.TableIndexUtil;
import com.server.edu.election.vo.ElcCourseTakeVo;
import com.server.edu.election.vo.ElectionApplyVo;
import com.server.edu.exception.ParameterValidateException;
import com.server.edu.session.util.SessionUtils;
import com.server.edu.session.util.entity.Session;
import com.server.edu.util.CollectionUtil;

import tk.mybatis.mapper.entity.Example;

/**
 * 选课申请
 * 
 * 
 * @version  [版本号, 2019年6月14日]
 * @see  [相关类/方法]
 * @since  [产品/模块版本]
 */
@Service
public class ElectionApplyServiceImpl implements ElectionApplyService
{
    @Autowired
    private ElectionApplyDao electionApplyDao;
    
    @Autowired
    private ElecRoundsDao elecRoundsDao;
    
    @Autowired
    private ElectionApplyCoursesDao electionApplyCoursesDao;
    
    @Autowired
    private ApplicationContext applicationContext;
    
    @Autowired
    private ElcCourseTakeDao courseTakeDao;
    
    @Autowired
    private CourseOpenDao courseOpenDao;
    
    @Override
    public PageInfo<ElectionApplyVo> applyList(
        PageCondition<ElectionApplyDto> condition)
    {
        ElectionApplyDto dto = condition.getCondition();
        List<ElectionApplyVo> applylist = electionApplyDao.selectApplys(dto);
        Session session = SessionUtils.getCurrentSession();
        if (CollectionUtil.isNotEmpty(applylist))
        {
            Example roundExample = new Example(ElectionRounds.class);
            roundExample.setOrderByClause("BEGIN_TIME_ ASC");
            Example.Criteria roundCriteria = roundExample.createCriteria();
            roundCriteria.andEqualTo("calendarId", dto.getCalendarId());
            roundCriteria.andEqualTo("projectId", dto.getProjectId());
            roundCriteria.andEqualTo("electionObj", Constants.DEPART_ADMIN);
            Date date = new Date();
            roundCriteria.andGreaterThanOrEqualTo("beginTime", date);
            roundCriteria.andLessThanOrEqualTo("endTime", date);
            roundCriteria.andEqualTo("openFlag", Constants.ONE);
            ElectionRounds electionRounds =
                elecRoundsDao.selectOneByExample(roundExample);
            for (ElectionApplyVo electionApplyVo : applylist)
            {
                electionApplyVo.setStatus(Constants.ZERO);
                if (session.isAcdemicDean() || electionRounds != null)
                {
                    electionApplyVo.setStatus(Constants.ONE);
                }
            }
        }
        PageInfo<ElectionApplyVo> pageInfo = new PageInfo<>(applylist);
        return pageInfo;
    }
    

	@Override
	public PageResult<ElectionApplyVo> applyUnList(PageCondition<ElectionApplyDto> condition) {
		ElectionApplyDto dto = condition.getCondition();
        Session session = SessionUtils.getCurrentSession();
		PageHelper.startPage(condition.getPageNum_(), condition.getPageSize_());
		Page<ElectionApplyVo> applylist = electionApplyDao.applyUnList(dto);
        if (CollectionUtil.isNotEmpty(applylist))
        {
            Example roundExample = new Example(ElectionRounds.class);
            roundExample.setOrderByClause("BEGIN_TIME_ ASC");
            Example.Criteria roundCriteria = roundExample.createCriteria();
            roundCriteria.andEqualTo("calendarId", dto.getCalendarId());
            roundCriteria.andEqualTo("projectId", dto.getProjectId());
            roundCriteria.andEqualTo("electionObj", Constants.DEPART_ADMIN);
            Date date = new Date();
            roundCriteria.andGreaterThanOrEqualTo("beginTime", date);
            roundCriteria.andLessThanOrEqualTo("endTime", date);
            roundCriteria.andEqualTo("openFlag", Constants.ONE);
            ElectionRounds electionRounds =
                elecRoundsDao.selectOneByExample(roundExample);
            for (ElectionApplyVo electionApplyVo : applylist)
            {
                electionApplyVo.setStatus(Constants.ZERO);
                if (session.isAcdemicDean() || electionRounds != null)
                {
                    electionApplyVo.setStatus(Constants.ONE);
                }
            }
        }
        
		return new PageResult<ElectionApplyVo>(applylist);
	}


	@Override
	public PageResult<ElectionApplyVo> alreadyApplyList(PageCondition<ElectionApplyDto> condition) {
		ElectionApplyDto dto = condition.getCondition();
		PageHelper.startPage(condition.getPageNum_(), condition.getPageSize_());
		Page<ElectionApplyVo> applylist = electionApplyDao.alreadyApplyList(dto);
		Session session = SessionUtils.getCurrentSession();
        if (CollectionUtil.isNotEmpty(applylist))
        {
            Example roundExample = new Example(ElectionRounds.class);
            roundExample.setOrderByClause("BEGIN_TIME_ ASC");
            Example.Criteria roundCriteria = roundExample.createCriteria();
            roundCriteria.andEqualTo("calendarId", dto.getCalendarId());
            roundCriteria.andEqualTo("projectId", dto.getProjectId());
            roundCriteria.andEqualTo("electionObj", Constants.DEPART_ADMIN);
            Date date = new Date();
            roundCriteria.andGreaterThanOrEqualTo("beginTime", date);
            roundCriteria.andLessThanOrEqualTo("endTime", date);
            roundCriteria.andEqualTo("openFlag", Constants.ONE);
            ElectionRounds electionRounds =
                elecRoundsDao.selectOneByExample(roundExample);
            for (ElectionApplyVo electionApplyVo : applylist)
            {
                electionApplyVo.setStatus(Constants.ZERO);
                if (session.isAcdemicDean() || electionRounds != null)
                {
                    electionApplyVo.setStatus(Constants.ONE);
                }
            }
        }
        
		return new PageResult<ElectionApplyVo>(applylist);
	}
    
    @Override
	public PageInfo<ElectionApplyVo> stuApplyCourseList(
            PageCondition<ElectionApplyDto> condition)
        {
            ElectionApplyDto dto = condition.getCondition();
            Session session = SessionUtils.getCurrentSession();
            dto.setStudentId(session.realUid());
            List<ElectionApplyVo> applylist = electionApplyDao.selectApplys(dto);
            PageInfo<ElectionApplyVo> pageInfo = new PageInfo<>(applylist);
            return pageInfo;
        }
    
    @Override
    @Transactional
    public int reply(ElectionApply electionApply)
    {
    	electionApply.setApply(Constants.TOW);
        int result =
            electionApplyDao.updateByPrimaryKeySelective(electionApply);
        
        if (result <= 0)
        {
            throw new ParameterValidateException(
                I18nUtil.getMsg("common.saveError",
                    I18nUtil.getMsg("electionApply.reply")));
        }
        this.updateCache(electionApply.getStudentId(), electionApply.getCalendarId());
        return result;
    }
    
    @Override
    @Transactional
    public int reject(ElectionApplyRejectDto dto)
    {
    	ElectionRounds electionRounds = elecRoundsDao.selectByPrimaryKey(dto.getRoundId());
    	if(electionRounds ==null) {
    		throw new ParameterValidateException("轮次数据错误");
    	}
    	Example example = new Example(ElectionApply.class);
    	example.createCriteria().andEqualTo("studentId", dto.getStudentId()).andEqualTo("calendarId", electionRounds.getCalendarId()).andEqualTo("courseCode",dto.getCourseCode());
    	List<ElectionApply> electionApplys = electionApplyDao.selectByExample(example);
    	if(CollectionUtil.isEmpty(electionApplys)) {
    		throw new ParameterValidateException("学生选课申请数据异常");
    	}
    	ElectionApply electionApply = electionApplys.get(0);
    	electionApply.setApply(Constants.TOW);
    	electionApply.setRemark(dto.getRemark());
        int result =
            electionApplyDao.updateByPrimaryKeySelective(electionApply);
        if (result <= 0)
        {
            throw new ParameterValidateException(
                I18nUtil.getMsg("common.saveError",
                    I18nUtil.getMsg("electionApply.reject")));
        }
        this.updateCache(electionApply.getStudentId(), electionApply.getCalendarId());
        return result;
    }
    
    @Override
    public int delete(Long calendarId)
    {
        Example example = new Example(ElectionApply.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("calendarId", calendarId);
        criteria.andEqualTo("apply", Constants.ONE);
        int result = electionApplyDao.deleteByExample(example);
        if (result <= Constants.ZERO)
        {
            throw new ParameterValidateException(
                I18nUtil.getMsg("common.failSuccess",
                    I18nUtil.getMsg("electionApply.application")));
        }
        return result;
    }
    
    @Override
    @Transactional
    public int agree(Long id)
    {
        Session session = SessionUtils.getCurrentSession();
        ElectionApply electionApply = new ElectionApply();
        electionApply.setId(id);
        electionApply.setApply(Constants.ONE);
        electionApply.setApplyBy(session.realUid());
        int result =
            electionApplyDao.updateByPrimaryKeySelective(electionApply);
        if (result <= 0)
        {
            throw new ParameterValidateException(
                I18nUtil.getMsg("common.saveError",
                    I18nUtil.getMsg("electionApply.agree")));
        }
        return result;
    }
    
    @Override
    @Transactional
    public int apply(String studentId, Long roundId, String courseCode)
    {
        ElectionRounds elecRounds = elecRoundsDao.selectByPrimaryKey(roundId);
        if (elecRounds == null)
        {
            throw new ParameterValidateException(
                I18nUtil.getMsg("baseresservice.parameterError"));
        }
        Example example = new Example(ElectionApply.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("studentId", studentId);
        Long calendarId = elecRounds.getCalendarId();
        criteria.andEqualTo("calendarId", calendarId);
        criteria.andEqualTo("courseCode", courseCode);
        criteria.andEqualTo("apply", Constants.APPLY);
        ElectionApply apply = electionApplyDao.selectOneByExample(example);
        if (apply != null)
        {
            throw new ParameterValidateException(I18nUtil.getMsg("common.exist",
                I18nUtil.getMsg("election.electionApply")));
        }
        Example wExample = new Example(ElectionApply.class);
        Example.Criteria wCriteria = wExample.createCriteria();
        wCriteria.andEqualTo("studentId", studentId);
        wCriteria.andEqualTo("calendarId", calendarId);
        wCriteria.andEqualTo("courseCode", courseCode);
        int result = electionApplyDao.deleteByExample(wExample);
        Example cExample = new Example(ElectionApplyCourses.class);
        Example.Criteria cCriteria = cExample.createCriteria();
        cCriteria.andEqualTo("calendarId", calendarId);
        cCriteria.andEqualTo("courseCode", courseCode);
        ElectionApplyCourses electionApplyCourses =
            electionApplyCoursesDao.selectOneByExample(cExample);
        if(electionApplyCourses==null) {
        	throw new ParameterValidateException("选课申请课程不存在");
        }
        //查询学生的已选课程
        Integer index = TableIndexUtil.getIndex(calendarId);
        List<ElcCourseTakeVo> list = courseTakeDao.findBkSelectedCourses(studentId, calendarId, index);
        //查询所有英语课、体育课
        List<String> facultyList = Arrays.asList("000268","000293");
        Example courseExample  = new Example(CourseOpen.class);
        courseExample.createCriteria().andEqualTo("calendarId", calendarId).andIn("faculty", facultyList);
		List<CourseOpen> engLishPECourses= courseOpenDao.selectByExample(courseExample);
		if(CollectionUtil.isNotEmpty(engLishPECourses)) {
			List<String> PECourses = engLishPECourses.stream().filter(c->"000293".equals(c.getFaculty())).map(c->c.getCourseCode()).collect(Collectors.toList());
			List<String> engLishCourseCodes = engLishPECourses.stream().filter(c->"000268".equals(c.getFaculty())).map(c->c.getCourseCode()).collect(Collectors.toList());
			if(CollectionUtil.isNotEmpty(engLishCourseCodes) && engLishCourseCodes.contains(courseCode)) {
				List<String> engLishTakeCourse = list.stream().filter(c->c!=null).filter(c->engLishCourseCodes.contains(c.getCourseCode())).map(c->c.getCourseCode()).collect(Collectors.toList());
				if(CollectionUtil.isNotEmpty(engLishTakeCourse)) {
					throw new ParameterValidateException("已选英语课不能申请!");
				}
			}
			if(CollectionUtil.isNotEmpty(PECourses) && PECourses.contains(courseCode)) {
				List<String> peTakeCourse = list.stream().filter(c->c!=null).filter(c->PECourses.contains(c.getCourseCode())).map(c->c.getCourseCode()).collect(Collectors.toList());
				if(CollectionUtil.isNotEmpty(peTakeCourse)) {
					throw new ParameterValidateException("已选体育课不能申请!");
				}
			}
		}
        ElectionApply electionApply = new ElectionApply();
        electionApply.setStudentId(studentId);
        electionApply.setCalendarId(calendarId);
        electionApply.setCourseCode(courseCode);
        electionApply.setMode(electionApplyCourses.getMode());
        electionApply.setApply(Constants.ZERO);
        electionApply.setCreatedAt(new Date());
        result = electionApplyDao.insertSelective(electionApply);
        if (result <= 0)
        {
            throw new ParameterValidateException(
                I18nUtil.getMsg("electionApply.applyError"));
        }
        this.updateCache(studentId, calendarId);
        return result;
    }
    
    @Override
    @Transactional
    public void update(String studentId, Long calendarId, String courseCode,ElectRuleType type)
    {
        Example example = new Example(ElectionApply.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("studentId", studentId);
        criteria.andEqualTo("calendarId", calendarId);
        criteria.andEqualTo("courseCode", courseCode);
        Integer oldApplyStatus = Constants.APPLY;
        if(ElectRuleType.WITHDRAW.equals(type)) {
        	oldApplyStatus = Constants.AGENTELC;
        }
        criteria.andEqualTo("apply", oldApplyStatus);
        List<ElectionApply> applyList = electionApplyDao.selectByExample(example);
        if (CollectionUtil.isNotEmpty(applyList))
        {
        	ElectionApply apply = applyList.get(0);
        	ElectionApply newApply = new ElectionApply();
            Integer applyStatus = Constants.AGENTELC;
            if(ElectRuleType.WITHDRAW.equals(type)) {
            	applyStatus = Constants.APPLY_WITHDRAW;
            }
        	newApply.setApply(applyStatus);
            Example newExample = new Example(ElectionApply.class);
            Example.Criteria newCriteria = newExample.createCriteria();
            newCriteria.andEqualTo("id", apply.getId());
            int count = electionApplyDao.updateByExampleSelective(newApply, newExample);
            if(count >0) {
            	this.updateCache(studentId, calendarId);
            }
        }
    }
    
    @Override
    @Transactional
    public int cancelApply(String studentId, Long roundId, String courseCode)
    {
        ElectionRounds elecRounds = elecRoundsDao.selectByPrimaryKey(roundId);
        if (elecRounds == null)
        {
            throw new ParameterValidateException(
                I18nUtil.getMsg("baseresservice.parameterError"));
        }
        Example example = new Example(ElectionApply.class);
        
        Long calendarId = elecRounds.getCalendarId();
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("studentId", studentId);
        criteria.andEqualTo("calendarId", calendarId);
        criteria.andEqualTo("courseCode", courseCode);
        criteria.andEqualTo("apply", Constants.APPLY);
        ElectionApply apply = electionApplyDao.selectOneByExample(example);
        if (apply == null)
        {
            throw new ParameterValidateException(
                I18nUtil.getMsg("baseresservice.parameterError"));
        }
        Example cExample = new Example(ElectionApplyCourses.class);
        Example.Criteria cCriteria = cExample.createCriteria();
        cCriteria.andEqualTo("calendarId", calendarId);
        cCriteria.andEqualTo("courseCode", courseCode);
        cCriteria.andEqualTo("mode", apply.getMode());
        ElectionApplyCourses electionApplyCourses =
            electionApplyCoursesDao.selectOneByExample(cExample);
        if(electionApplyCourses==null) {
        	throw new ParameterValidateException("选课申请课程不存在");
        }
        int result = electionApplyDao.delete(apply);
        if (result <= 0)
        {
            throw new ParameterValidateException(
                I18nUtil.getMsg("common.failSuccess",
                    I18nUtil.getMsg("election.electionApply")));
        }
        this.updateCache(studentId, calendarId);
        return result;
    }
    
    /**更新学生选课缓存数据*/
    private void updateCache(String studentId, Long calendarId)
    {
        Example aExample = new Example(ElectionApply.class);
        Example.Criteria aCriteria = aExample.createCriteria();
        aCriteria.andEqualTo("studentId", studentId);
        aCriteria.andEqualTo("calendarId", calendarId);
        List<ElectionApply> electionApplys =
            electionApplyDao.selectByExample(aExample);
        ElecContextUtil
            .setElecApplyCourse(studentId, electionApplys);
    }

}
