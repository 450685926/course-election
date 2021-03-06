package com.server.edu.mutual.controller;

import java.util.ArrayList;
import java.util.List;

import com.server.edu.common.locale.I18nUtil;
import com.server.edu.exception.ParameterValidateException;
import org.apache.commons.lang3.StringUtils;
import org.apache.servicecomb.provider.rest.common.RestSchema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import com.github.pagehelper.PageInfo;
import com.server.edu.common.PageCondition;
import com.server.edu.common.rest.RestResult;
import com.server.edu.mutual.dto.AgentApplyDto;
import com.server.edu.mutual.dto.ElcMutualApplyDto;
import com.server.edu.mutual.service.ElcMutualAuditService;
import com.server.edu.mutual.vo.ElcMutualApplyAuditLogsVo;
import com.server.edu.mutual.vo.ElcMutualApplyVo;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Info;
import io.swagger.annotations.SwaggerDefinition;

@SwaggerDefinition(info = @Info(title = "互选审核", version = ""))
@RestSchema(schemaId = "elcMutualAuditController")
@RequestMapping("elcMutualAudit")
public class ElcMutualAuditController {
	private static Logger LOG =
	        LoggerFactory.getLogger(ElcMutualApplyController.class);
	@Autowired
	private ElcMutualAuditService elcMutualAuditService;
	
    /**
     * 行政学院审核课程视图
     * 
     * @param dto
     * @return
     * @see [类、类#方法、类#成员]
     */
    @ApiOperation(value = "行政学院审核课程视图")
    @PostMapping("/collegeApplyCourseList")
    public RestResult<PageInfo<ElcMutualApplyVo>> collegeApplyCourseList(
    		@RequestBody PageCondition<ElcMutualApplyDto> dto)
        throws Exception
    {
        LOG.info("collegeApplyCourseList.start");
        PageInfo<ElcMutualApplyVo> list =elcMutualAuditService.collegeApplyCourseList(dto);
        return RestResult.successData(list);
    }
    
    /**
     * 行政学院审核学生视图
     * 
     * @param dto
     * @return
     * @see [类、类#方法、类#成员]
     */
    @ApiOperation(value = "行政学院审核学生视图")
    @PostMapping("/collegeApplyStuList")
    public RestResult<PageInfo<ElcMutualApplyVo>> collegeApplyStuList(
    		@RequestBody PageCondition<ElcMutualApplyDto> dto)
        throws Exception
    {
        LOG.info("getElcMutualApplyList.start");
        PageInfo<ElcMutualApplyVo> list =elcMutualAuditService.collegeApplyStuList(dto);
        return RestResult.successData(list);
    }
    
    /**
     * 开课学院审核课程视图
     * 
     * @param dto
     * @return
     * @see [类、类#方法、类#成员]
     */
    @ApiOperation(value = "开课学院审核课程视图")
    @PostMapping("/openCollegeApplyCourseList")
    public RestResult<PageInfo<ElcMutualApplyVo>> openCollegeApplyCourseList(
    		@RequestBody PageCondition<ElcMutualApplyDto> dto)
        throws Exception
    {
        LOG.info("openCollegeApplyCourseList.start");
        PageInfo<ElcMutualApplyVo> list =elcMutualAuditService.openCollegeApplyCourseList(dto);
        return RestResult.successData(list);
    }
    
    /**
     * 开课学院审核学生视图
     * 
     * @param dto
     * @return
     * @see [类、类#方法、类#成员]
     */
    @ApiOperation(value = "开课学院审核学生视图")
    @PostMapping("/openCollegeApplyStuList")
    public RestResult<PageInfo<ElcMutualApplyVo>> openCollegeApplyStuList(
    		@RequestBody PageCondition<ElcMutualApplyDto> dto)
    				throws Exception
    {
    	LOG.info("openCollegeApplyStuList.start");
    	PageInfo<ElcMutualApplyVo> list =elcMutualAuditService.openCollegeApplyStuList(dto);
    	return RestResult.successData(list);
    }
    
    
    /**
     *  审核
     * @param dto
     * @return
     * @see [类、类#方法、类#成员]
     */
    @ApiOperation(value = "审核")
    @PostMapping("/aduit")
    public RestResult<Integer> aduit(@RequestBody ElcMutualApplyDto dto)
        throws Exception
    {
        LOG.info("aduit.start");
        int result =elcMutualAuditService.aduit(dto);
        return RestResult.successData(result);
    }

    /**
     * 批量审核
     * @param dto
     * @return
     * */
    @ApiOperation(value = "批量审核")
    @PostMapping("/batchAudit")
    public RestResult<Integer> batchAduit(@RequestBody ElcMutualApplyDto dto) throws Exception{
        LOG.info("batchAudit.start");
        Integer result = elcMutualAuditService.batchAduit(dto);
        return RestResult.successData(result);
    }

    
    /**
     *互选代理申请
     * 
     * @param dto
     * @return
     * @see [类、类#方法、类#成员]
     */
    @ApiOperation(value = "互选代理申请")
    @PostMapping("/agentApply")
    public RestResult<Integer> agentApply(
    		@RequestBody AgentApplyDto dto)
        throws Exception
    {
        LOG.info("agentApply.start");

        List<String> studentIdList = dto.getStudentIdList();
        List<Long> mutualCourseIdList = dto.getMutualCourseIdList();
        if (studentIdList == null || studentIdList.isEmpty()) {
            String studentId = dto.getStudentId();
            if (StringUtils.isBlank(studentId)) {
                return RestResult.fail(I18nUtil.getMsg("student.notNull"));
            }
            studentIdList = new ArrayList<>();
            studentIdList.add(studentId);
            dto.setStudentIdList(studentIdList);
//            return RestResult.fail(I18nUtil.getMsg("student.notNull"));
        }
        if (null == mutualCourseIdList || mutualCourseIdList.isEmpty()) {
            Long mutualCourseId = dto.getMutualCourseId();
            if (null == mutualCourseId) {
                return RestResult.fail(I18nUtil.getMsg("courseId.notNull"));
            }
            mutualCourseIdList = new ArrayList<>();
            mutualCourseIdList.add(mutualCourseId);
            dto.setMutualCourseIdList(mutualCourseIdList);
//            return RestResult.fail(I18nUtil.getMsg("courseId.notNull"));
        }
        LOG.info("studentIdList:" + dto.getStudentIdList());
        LOG.info("mutualCourseIdList:" +dto.getMutualCourseIdList());
        LOG.info("studentId:"+dto.getStudentId());
        LOG.info("mutualCourseId：" + dto.getMutualCourseId());
        int result =elcMutualAuditService.agentApply(dto);
        return RestResult.successData(result);
    }
    
    /**
     * 查询审核日志
     * @param dto
     * @return
     * @see [类、类#方法、类#成员]
     */
    @ApiOperation(value = "查询审核日志")
    @PostMapping("/queryAuditLogList")
    public RestResult<List<ElcMutualApplyAuditLogsVo>> queryAuditLogList(@RequestBody ElcMutualApplyAuditLogsVo vo )
        throws Exception
    {
        LOG.info("queryAuditLogList.start");
        String studentId = vo.getStudentId();
        String courseCode = vo.getCourseCode();
        Long calendarId = vo.getCalendarId();
        if (org.apache.commons.lang.StringUtils.isBlank(studentId) || org.apache.commons.lang.StringUtils.isBlank(courseCode) || null == calendarId) {
            throw new ParameterValidateException(I18nUtil.getMsg("common.parameterError"));
        }
        List<ElcMutualApplyAuditLogsVo> list = new ArrayList<ElcMutualApplyAuditLogsVo>();
        try {
        	list =elcMutualAuditService.queryAuditLogList(vo);
		} catch (Exception e) {
			LOG.error(e.getMessage());
		}
        return RestResult.successData(list);
    }
}
