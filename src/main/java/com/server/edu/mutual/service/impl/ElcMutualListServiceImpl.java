package com.server.edu.mutual.service.impl;


import java.util.ArrayList;
import java.util.List;

import com.server.edu.common.enums.GroupDataEnum;
import com.server.edu.mutual.service.ElcMutualCommonService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.server.edu.common.PageCondition;
import com.server.edu.common.locale.I18nUtil;
import com.server.edu.common.rest.RestResult;
import com.server.edu.dictionary.utils.SpringUtils;
import com.server.edu.election.constants.Constants;
import com.server.edu.election.dao.ElcCourseTakeDao;
import com.server.edu.election.dto.TimeTableMessage;
import com.server.edu.election.entity.ElcCourseTake;
import com.server.edu.election.studentelec.context.TimeAndRoom;
import com.server.edu.election.util.ExcelStoreConfig;
import com.server.edu.election.util.TableIndexUtil;
import com.server.edu.election.vo.ElcCourseTakeVo;
import com.server.edu.mutual.dao.ElcMutualListDao;
import com.server.edu.mutual.dto.ElcMutualCrossStuDto;
import com.server.edu.mutual.dto.ElcMutualListDto;
import com.server.edu.mutual.service.ElcMutualListService;
import com.server.edu.mutual.vo.ElcMutualListVo;
import com.server.edu.session.util.SessionUtils;
import com.server.edu.session.util.entity.Session;
import com.server.edu.welcomeservice.util.ExcelEntityExport;

import tk.mybatis.mapper.entity.Example;

import org.springframework.stereotype.Service;

@Service
public class ElcMutualListServiceImpl implements ElcMutualListService {
    private static Logger LOG =
            LoggerFactory.getLogger(ElcMutualListServiceImpl.class);
    @Autowired
    private ElcMutualListDao elcMutualListDao;

    @Autowired
    private ExcelStoreConfig excelStoreConfig;
    
    @Autowired
    private ElcCourseTakeDao courseTakeDao;

    @Autowired
    private ElcMutualCommonService elcMutualCommonService;
    
    /**
     * 文件存储路径
     */
    @Value("${task.cache.directory}")
    private String cacheDirectory;
    
    @Override
    public PageInfo<ElcMutualListVo> getMutualStuList(PageCondition<ElcMutualListDto> condition) {
        PageHelper.startPage(condition.getPageNum_(), condition.getPageSize_());

        Session session = SessionUtils.getCurrentSession();

//        String faculty = session.getFaculty();

        String projectId = session.getCurrentManageDptId();

        ElcMutualListDto dto = condition.getCondition();

        if (Constants.BK_CROSS.equals(dto.getMode())) {
            dto.setInType(Constants.FIRST);
        } else {
            dto.setByType(Constants.FIRST);
        }

        // 教务员查看本学院申请了本研互选选课的学生和申请了本学院开设课程的学生
        boolean isAcdemicDean = StringUtils.equals(session.getCurrentRole(), String.valueOf(Constants.ONE)) && !session.isAdmin() && session.isAcdemicDean();
        if (isAcdemicDean) {
            List<String> deptIds = SessionUtils.getCurrentSession().
                    getGroupData().get(GroupDataEnum.department.getValue());

            if (dto.getProjectIds().contains(projectId)) {
                dto.setColleges(deptIds);  // 学生行政学院
            } else {
                dto.setOpenColleges(deptIds);  // 开课学院
            }
        }
        LOG.info("=======修读类型的courseTakeType==========" + dto.getCourseTakeType());
        List<ElcMutualListVo> list = elcMutualListDao.getMutualStuList(dto);
        //获取教学安排
        for(ElcMutualListVo vo:list) {
        	if(org.apache.commons.lang3.StringUtils.isNotEmpty(String.valueOf(dto.getCalendarId()))
        			&& org.apache.commons.lang3.StringUtils.isNotEmpty(vo.getStudentId())
        			&& org.apache.commons.lang3.StringUtils.isNotEmpty(vo.getCourseCode())) {
        		ElcCourseTakeVo elcCourseTake = getElcCourseTake(String.valueOf(dto.getCalendarId()),vo.getStudentId(),vo.getCourseCode());
            	List<TimeAndRoom> timeTableList = new ArrayList<>();
            	if(null != elcCourseTake) {
            		TimeTableMessage timeTableMessage = getTimeTableMessage(elcCourseTake.getTeachingClassId());
            		if(null != timeTableMessage) {
            			TimeAndRoom timeAndRoom = new TimeAndRoom();
            			timeAndRoom.setRoomId(timeTableMessage.getRoomId());
            			timeAndRoom.setTimeId(timeTableMessage.getTimeId());
            			timeAndRoom.setTimeAndRoom(timeTableMessage.getTimeAndRoom());
            			timeTableList.add(timeAndRoom);
            		}
            	}
            	vo.setTimeTableList(timeTableList);
        	}
        	
        }
        PageInfo<ElcMutualListVo> pageInfo = new PageInfo<ElcMutualListVo>(list);
        return pageInfo;
    }

    @Override
    public PageInfo<ElcMutualListVo> getMutualCourseList(PageCondition<ElcMutualListDto> condition) {
        PageHelper.startPage(condition.getPageNum_(), condition.getPageSize_());
        ElcMutualListDto dto = condition.getCondition();
        Session session = SessionUtils.getCurrentSession();
        String faculty = session.getFaculty();

        if (Constants.BK_CROSS.equals(dto.getMode())) {
            dto.setInType(Constants.FIRST);
        } else {
            dto.setByType(Constants.FIRST);
        }
        
        //课程维度 mode是1 是查询研究生选本科生的课程
        //mode是2 是查询本科生选研究生的课程,此处的mode需要调整
        if (Constants.BK_MUTUAL.equals(dto.getMode())) {
            dto.setMode(Constants.GRADUATE_MUTUAL);
        } else if (Constants.GRADUATE_MUTUAL.equals(dto.getMode())) {
            dto.setMode(Constants.BK_MUTUAL);
        }

        LOG.info("=======getMutualCourseList dto.getMode==========" + dto.getMode());
        boolean isAcdemicDean = StringUtils.equals(session.getCurrentRole(), String.valueOf(Constants.ONE)) && !session.isAdmin() && session.isAcdemicDean();
        if (isAcdemicDean) {
//            dto.setOpenCollege(faculty);
            dto.setOpenColleges(elcMutualCommonService.getCollegeList(session));
        }

        List<ElcMutualListVo> list = elcMutualListDao.getMutualCourseList(dto);
        PageInfo<ElcMutualListVo> pageInfo = new PageInfo<ElcMutualListVo>(list);
        return pageInfo;
    }
    
    
    /**
     * @导出本研互选名单列表
     * @param dto
     * @return
     */
    @Override
    public RestResult<String> exportelcMutualStuList(PageCondition<ElcMutualListDto> condition)
    {
        RestResult<String> restResult = RestResult.error(I18nUtil.getMsg("export.exception"));
        Session session = SessionUtils.getCurrentSession();
        String faculty = session.getFaculty();
        String projectId = session.getCurrentManageDptId();

        ElcMutualListDto dto = condition.getCondition();

        if (Constants.BK_CROSS.equals(dto.getMode())) {
            dto.setInType(Constants.FIRST);
        } else {
            dto.setByType(Constants.FIRST);
        }

        // 教务员查看本学院申请了本研互选选课的学生和申请了本学院开设课程的学生
        boolean isAcdemicDean = StringUtils.equals(session.getCurrentRole(), String.valueOf(Constants.ONE)) && !session.isAdmin() && session.isAcdemicDean();
        if (isAcdemicDean) {
            if (dto.getProjectIds().contains(projectId)) {
                dto.setCollege(faculty);  // 学生行政学院
            } else {
                dto.setOpenCollege(faculty);  // 开课学院
            }
        }

        List<ElcMutualListVo> selectElcMutualListVoList = elcMutualListDao.getMutualStuList(dto);
        try
        {
            List<ElcMutualListVo> exportElcMutualCrossList = SpringUtils.convert(selectElcMutualListVoList);
            String path ;
            // 本科生名单
            if(dto.getProjectIds().contains(Constants.PROJ_UNGRADUATE))
            {
                ExcelEntityExport<ElcMutualListVo> excelExport = new ExcelEntityExport(exportElcMutualCrossList,
                        excelStoreConfig.getExportelcMutualUNGRADUATEStuListExcelKey(),
                        excelStoreConfig.getExportelcMutualUNGRADUATEStuListExcelTitle(),
                        cacheDirectory);
                path = excelExport.exportExcelToCacheDirectory("本研互选名单列表(本科生)");
            }
            else
            {
                ExcelEntityExport<ElcMutualListVo> excelExport = new ExcelEntityExport(exportElcMutualCrossList,
                        excelStoreConfig.getExportelcMutualStuListExcelKey(),
                        excelStoreConfig.getExportelcMutualStuListExcelTitle(),
                        cacheDirectory);
                path = excelExport.exportExcelToCacheDirectory("本研互选名单列表(研究生)");
            }
            restResult = RestResult.successData(I18nUtil.getMsg("export.success"), path);
            return restResult;
        }
        catch (Exception e)
        {
            return RestResult.fail();
        }
    }

    /**
     * 查询互选课程选课结果
     * 
     * @return
     */
    public ElcCourseTakeVo getElcCourseTake(String calendarId, String studentId, String courseCode){
    	Example example = new Example(ElcCourseTake.class);
    	example.createCriteria()
        .andEqualTo("calendarId", calendarId)
        .andEqualTo("studentId", studentId)
        .andEqualTo("courseCode", courseCode);
    	int index = TableIndexUtil.getIndex(Long.valueOf(calendarId));
    	ElcCourseTakeVo elcCourseTake =courseTakeDao.findElcCourseTake(studentId,Long.valueOf(calendarId),index,courseCode);
    	return elcCourseTake;
    }
    
    /**
     * 查询教学安排
     * 
     * @return
     */
    
    public TimeTableMessage getTimeTableMessage(Long teachingClassId) {
    	List<Long> ids = new ArrayList<>();
    	ids.add(teachingClassId);
    	List<TimeTableMessage> tableMessages = courseTakeDao.findClassTime(ids);
    	if(CollectionUtils.isNotEmpty(tableMessages)) {
    		return tableMessages.get(0);
    	}
    	return null;
    }
    

}
