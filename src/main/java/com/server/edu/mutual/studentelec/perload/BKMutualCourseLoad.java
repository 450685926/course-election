package com.server.edu.mutual.studentelec.perload;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.server.edu.election.constants.Constants;
import com.server.edu.election.studentelec.cache.TeachingClassCache;
import com.server.edu.election.studentelec.context.bk.SelectedCourse;
import com.server.edu.mutual.Enum.MutualApplyAuditStatus;
import com.server.edu.mutual.dto.ElcMutualApplyDto;
import com.server.edu.mutual.service.ElcMutualAuditService;
import com.server.edu.mutual.studentelec.context.ElecContextMutualBk;
import com.server.edu.mutual.util.ProjectUtil;
import com.server.edu.mutual.vo.ElcMutualApplyVo;
import com.server.edu.session.util.SessionUtils;
import com.server.edu.session.util.entity.Session;
import com.server.edu.util.BeanUtil;
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
	
	@Override
	public void load(ElecContextMutualBk context) {
		Session session = SessionUtils.getCurrentSession();
		
		boolean isAdmin = StringUtils.equals(session.getCurrentRole(), String.valueOf(Constants.ONE)) && session.isAdmin(); // 管理员
		boolean isAcdemicDean = StringUtils.equals(session.getCurrentRole(), String.valueOf(Constants.ONE)) && !session.isAdmin() && session.isAcdemicDean(); // 教务员
		boolean isStudent = session.isStudent();
		
		String projectId = session.getCurrentManageDptId();
		LOG.info("---------BKMutualCourseLoad-------projectId:" + projectId);
		LOG.info("---------BKMutualCourseLoad-------calendarId:" + context.getCalendarId());
		LOG.info("---------BKMutualCourseLoad-------studentId:" + context.getStudentInfo().getStudentId());
		
		/** 封装本研互选的数据  */
		ElcMutualApplyDto dto = new ElcMutualApplyDto();
		List<String> projectIds = ProjectUtil.getProjectIds(projectId);
		dto.setProjectIds(projectIds);
		dto.setMode(Constants.FIRST);
		dto.setCalendarId(context.getCalendarId());
		dto.setStatus(MutualApplyAuditStatus.AUDITED_APPROVED.status());
		dto.setStudentId(context.getStudentInfo().getStudentId());
		
		List<ElcMutualApplyVo> list = elcMutualAuditService.getOpenCollegeAuditList(dto);
		
		Set<SelectedCourse> set = new HashSet<SelectedCourse>();
		try {
			if (CollectionUtil.isNotEmpty(list)) {
				for (ElcMutualApplyVo vo : list) {
					SelectedCourse course = new SelectedCourse();
					TeachingClassCache cache = new TeachingClassCache();
					BeanUtil.copyProperties(cache, vo);
					cache.setTeachClassCode(vo.getTeachingClassCode());
					cache.setTeachClassId(Long.parseLong(vo.getTeachingClassId()));
					
					course.setCourse(cache);
					// 选课对象(1学生，2教务员，3管理员)
					course.setChooseObj(getChooseObj(isAdmin,isAcdemicDean,isStudent));
					
					set.add(course);
				}
			}
		} catch (Exception e) {
			LOG.error(this.getClass()+ "---" + e.getMessage());
		}
		context.setSelectedMutualCourses(set);
	}
	
	/**
	 * 获取选课对象
	 * @return
	 */
	private Integer getChooseObj(boolean isAdmin, boolean isAcdemicDean, boolean isStudent) {
		Integer chooseObj = 0;
		if (isAdmin) {
			chooseObj = 3;
		}else if (isAcdemicDean) {
			chooseObj = 2;
		}else if (isStudent){
			chooseObj = 1;
		}
		return chooseObj;
	}

}


