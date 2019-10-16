package com.server.edu.election.controller;

import com.server.edu.common.PageCondition;
import com.server.edu.common.rest.PageResult;
import com.server.edu.common.rest.RestResult;
import com.server.edu.election.dto.StudentRebuildFeeDto;
import com.server.edu.election.service.ElcRebuildFeeStatisticsService;
import com.server.edu.election.vo.StudentRebuildFeeVo;
import com.server.edu.util.ExportUtil;
import com.server.edu.util.FileUtil;
import com.server.edu.util.excel.ExcelWriterUtil;
import io.swagger.annotations.*;
import org.apache.servicecomb.provider.rest.common.RestSchema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.File;

@SwaggerDefinition(info = @Info(title = "重修缴费统计", version = ""))
@RestSchema(schemaId = "ElcRebuildFeeStatisticsController")
@RequestMapping("elcRebuildFeeStatistics")
public class ElcRebuildFeeStatisticsController {
	private static Logger LOG =
	        LoggerFactory.getLogger(ElcRebuildFeeStatisticsController.class);
	
	@Autowired
	private ElcRebuildFeeStatisticsService elcRebuildFeeStatisticsService;
    @Value("${cache.directory}")
    private String cacheDirectory;
    /**
     *学生重修缴费信息列表
     * 
     * @param condition
     * @return
     * @see [类、类#方法、类#成员]
     */
    @ApiOperation(value = "学生重修缴费信息列表")
    @PostMapping("/page")
    public RestResult<PageResult<StudentRebuildFeeVo>> page(@RequestBody PageCondition<StudentRebuildFeeDto> condition) {
        PageResult<StudentRebuildFeeVo> page =elcRebuildFeeStatisticsService.getStudentRebuildFeeList(condition);
        return RestResult.successData(page);
    }
    
    /**
               *   学生重修缴费信息列表导出
     *
     * @param dto
     * @return
     * @see [类、类#方法、类#成员]
     */
    @ApiResponses({
            @ApiResponse(code = 200, response = File.class, message = "学生重修缴费信息列表导出")})
    @PostMapping(value = "/export")
    public ResponseEntity<Resource> export(
            @RequestBody StudentRebuildFeeDto dto)
            throws Exception
    {
        FileUtil.mkdirs(cacheDirectory);
        //删除超过30天的文件
        FileUtil.deleteFile(cacheDirectory, 2);
        ExcelWriterUtil result = elcRebuildFeeStatisticsService.export(dto);
        return ExportUtil.exportExcel(result, cacheDirectory,  "studentRebuildFee.xls");
    }
	

}
