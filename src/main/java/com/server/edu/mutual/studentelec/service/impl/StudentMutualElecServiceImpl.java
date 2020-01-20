package com.server.edu.mutual.studentelec.service.impl;


import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageInfo;
import com.server.edu.common.PageCondition;
import com.server.edu.common.enums.GroupDataEnum;
import com.server.edu.common.locale.I18nUtil;
import com.server.edu.common.rest.PageResult;
import com.server.edu.common.rest.RestResult;
import com.server.edu.common.validator.Assert;
import com.server.edu.election.constants.Constants;
import com.server.edu.election.dao.StudentDao;
import com.server.edu.election.dto.CourseOpenDto;
import com.server.edu.election.entity.ElectionRounds;
import com.server.edu.election.entity.Student;
import com.server.edu.election.query.ElecRoundCourseQuery;
import com.server.edu.election.studentelec.context.ElecRequest;
import com.server.edu.election.studentelec.context.ElecRespose;
import com.server.edu.election.studentelec.service.cache.AbstractCacheService;
import com.server.edu.election.studentelec.service.impl.RoundDataProvider;
import com.server.edu.election.studentelec.utils.ElecContextUtil;
import com.server.edu.election.studentelec.utils.ElecStatus;
import com.server.edu.mutual.Enum.MutualApplyAuditStatus;
import com.server.edu.mutual.dao.ElecMutualRoundsDao;
import com.server.edu.mutual.dto.ElcMutualApplyDto;
import com.server.edu.mutual.service.ElcMutualApplyService;
import com.server.edu.mutual.service.ElcMutualRoundCourseService;
import com.server.edu.mutual.studentelec.context.ElecContextMutualBk;
import com.server.edu.mutual.studentelec.service.ElecMutualQueueService;
import com.server.edu.mutual.studentelec.service.StudentMutualElecService;
import com.server.edu.mutual.studentelec.utils.MutualQueueGroups;
import com.server.edu.mutual.vo.ElcMutualApplyVo;
import com.server.edu.mutual.vo.SelectedCourse;
import com.server.edu.session.util.SessionUtils;
import com.server.edu.session.util.entity.Session;
import com.server.edu.util.CollectionUtil;

/**
 * @author xlluoc
 *
 */
@Service
public class StudentMutualElecServiceImpl extends AbstractCacheService 
	implements StudentMutualElecService 
{
	
	Logger LOG = LoggerFactory.getLogger(getClass());
	
    @Autowired
    private RoundDataProvider dataProvider;
	
    @Autowired
//    private ElecMutualQueueService<ElecRequest> queueMutualService;
    private ElecMutualQueueService<ElecRequest> mutualRedisQueue;
    
    @Autowired
    private ElcMutualRoundCourseService elcMutualRoundCourseServiceImpl;
    
    @Autowired
    private StudentDao stuDao;
    
	@Autowired
	private ElecMutualRoundsDao elecMutualRoundsDao;
	
	@Autowired
	private ElcMutualApplyService elcMutualApplyService;
	
	@Override
	public RestResult<ElecRespose> loading(ElecRequest elecRequest) {
        Long roundId = elecRequest.getRoundId();
        String studentId = elecRequest.getStudentId();
        
        ElectionRounds round = dataProvider.getRound(roundId);
        Assert.notNull(round, "elec.roundCourseExistTip");
        Long calendarId = round.getCalendarId();
        elecRequest.setCalendarId(calendarId);
        
        ElecStatus currentStatus =
            ElecContextUtil.getElecStatus(calendarId, studentId);
        // 如果当前是Init状态，进行初始化
        if (ElecStatus.Init.equals(currentStatus))
        {
            ElecContextUtil.saveElecResponse(studentId, new ElecRespose());
            // 加入队列
            currentStatus = ElecStatus.Loading;
            ElecContextUtil.setElecStatus(calendarId, studentId, currentStatus);
            if (!mutualRedisQueue.add(MutualQueueGroups.MUTUAL_STUDENT_LOADING, elecRequest))
            {
                currentStatus = ElecStatus.Init;
                ElecContextUtil
                    .setElecStatus(calendarId, studentId, currentStatus);
                return RestResult.fail("请稍后再试");
            }
        }
        
        return RestResult.successData(new ElecRespose(currentStatus));
	}

	@Override
	public RestResult<ElecRespose> elect(ElecRequest elecRequest) {
        if (CollectionUtil.isEmpty(elecRequest.getElecClassList())
                && CollectionUtil.isEmpty(elecRequest.getWithdrawClassList())){
            return RestResult.fail("你没选择任何课程");
        }
        Long roundId = elecRequest.getRoundId();
        String studentId = elecRequest.getStudentId();
        
        ElectionRounds round = dataProvider.getRound(roundId);
        Assert.notNull(round, "elec.roundCourseExistTip");
        Long calendarId = round.getCalendarId();
        elecRequest.setCalendarId(calendarId);
        
        ElecStatus currentStatus =
            ElecContextUtil.getElecStatus(calendarId, studentId);
        
        if (ElecStatus.Ready.equals(currentStatus)
            && ElecContextUtil.tryLock(calendarId, studentId))
        {
            try
            {
                ElecContextUtil.saveElecResponse(studentId, new ElecRespose());
                // 加入选课队列
                if (mutualRedisQueue.add(MutualQueueGroups.MUTUAL_STUDENT_ELEC, elecRequest))
                {
                    ElecContextUtil.setElecStatus(calendarId,
                        studentId,
                        ElecStatus.Processing);
                    currentStatus = ElecStatus.Processing;
                }
                else
                {
                    return RestResult.fail("请稍后再试");
                }
            }
            finally
            {
                ElecContextUtil.unlock(calendarId, studentId);
            }
        }
        return RestResult.successData(new ElecRespose(currentStatus));
	}

	@Override
	public ElecRespose getElectResult(ElecRequest elecRequest) {
        Long roundId = elecRequest.getRoundId();
        String studentId = elecRequest.getStudentId();
        Long calendarId = elecRequest.getCalendarId();
        
        // 本科生/研究生教务员
        if (roundId != null) {
            ElectionRounds round = dataProvider.getRound(roundId);
            Assert.notNull(round, "elec.roundCourseExistTip");
            calendarId = round.getCalendarId();
        }
        
        ElecRespose response =
            ElecContextUtil.getElecRespose(studentId);
        ElecStatus status =
            ElecContextUtil.getElecStatus(calendarId, studentId);
        if (response == null)
        {
            response = new ElecRespose(status);
        }
        else
        {
            response.setStatus(status);
        }
        return response;
	}

	@Override
	public ElecContextMutualBk getData(ElecContextMutualBk c, ElectionRounds round, Long calendarId) {
		List<SelectedCourse> optionalCourses = c.getOptionalCourses();
		
		Set<SelectedCourse> selectedMutualCourses = c.getSelectedMutualCourses();     // 本学期已选的互选课程
		Set<SelectedCourse> unSelectedMutualCourses = c.getUnSelectedMutualCourses(); // 未选的互选课程
		
		// 获取轮次可选的互选课程
		PageCondition<ElecRoundCourseQuery> dto = new PageCondition<ElecRoundCourseQuery>();
		ElecRoundCourseQuery query = new ElecRoundCourseQuery();
		query.setCalendarId(calendarId);
		query.setMode(Constants.ONE);
		query.setRoundId(round.getId());
		query.setProjectId(round.getProjectId());
		dto.setCondition(query);
		dto.setPageSize_(99999);
		PageResult<CourseOpenDto> pageResult = elcMutualRoundCourseServiceImpl.listPage(dto);
		List<CourseOpenDto> list = pageResult.getList();
		
		if (CollectionUtil.isNotEmpty(list)) {
			Set<String> courseCodes = list.stream().map(CourseOpenDto::getCourseCode).collect(Collectors.toSet());
			for (SelectedCourse unSelectedCourse : unSelectedMutualCourses) {
				if (courseCodes.contains(unSelectedCourse.getCourseCode())) {
					optionalCourses.add(unSelectedCourse);
				}
			}
		}
		
		optionalCourses.addAll(selectedMutualCourses);
		c.setOptionalCourses(optionalCourses);
		
		return c;
	}

	@Override
	public RestResult<Student> findStuRound(Long roundId, String studentId) throws Exception {
		Session session = SessionUtils.getCurrentSession();
        String projectId = session.getCurrentManageDptId();
		
        RestResult<Student> result = new RestResult<Student>();
		Student stu = stuDao.findStudentByCode(studentId);
        ElectionRounds round = elecMutualRoundsDao.selectByPrimaryKey(roundId);
        
        if (stu != null) {
//        	String major = stuDao.getStudentMajor(stu.getGrade(),stu.getProfession());
//            stu.setProfession(major);
            result.setData(stu);
            
            if (StringUtils.equals(session.getCurrentRole(), "1") && !session.isAdmin() && session.isAcdemicDean()) {
            	List<String> deptIds = SessionUtils.getCurrentSession().getGroupData().get(GroupDataEnum.department.getValue());
            	if (stu.getFaculty() == null || !deptIds.contains(stu.getFaculty())) {
            		result.setMsg(I18nUtil.getMsg("mutualAgent.studentFautyJuge"));
            		return result;
            	}
            }
            
            if (!dataProvider.containsMutualStu(studentId, round.getCalendarId(), projectId)) {
            	result.setMsg(I18nUtil.getMsg("mutualAgent.mutualStudentList"));
            	return result;
            }
            
            if (!dataProvider.containsMutualStuCondition(roundId, studentId, projectId)) {
            	result.setMsg(I18nUtil.getMsg("mutualAgent.studentJuge"));
            	return result;
            }
            
            if (!getMutualCourseForStu(studentId, projectId, round)) {
            	result.setMsg(I18nUtil.getMsg("mutualAgent.mutualCourse"));
            	return result;
			}
        }
        return result;
	}     
	
	/**
	 * 判断学生是否有审核通过的互选课程
	 * @param projectId
	 * @param round
	 * @return
	 */
	private boolean getMutualCourseForStu(String studentId, String projectId, ElectionRounds round) {
		PageCondition<ElcMutualApplyDto> page = new PageCondition<ElcMutualApplyDto>();
		ElcMutualApplyDto dto = new ElcMutualApplyDto();
		
		if (StringUtils.equals(projectId, Constants.PROJ_UNGRADUATE)) {
			dto.setMode(Constants.FIRST);
		}else if (StringUtils.equals(projectId, Constants.PROJ_GRADUATE) 
				|| StringUtils.equals(projectId, Constants.PROJ_LINE_GRADUATE)) {
			dto.setMode(Constants.SECOND);
		}
		dto.setCalendarId(round.getCalendarId());
		dto.setProjectId(projectId);
		dto.setStudentId(studentId);
		dto.setStatus(MutualApplyAuditStatus.AUDITED_APPROVED.status());
		page.setPageSize_(Constants.PAGE_SIZE);
		page.setCondition(dto);
		
		PageInfo<ElcMutualApplyVo> pageInfo = elcMutualApplyService.getElcMutualApplyList(page);
		return CollectionUtil.isNotEmpty(pageInfo.getList());
	}

}
