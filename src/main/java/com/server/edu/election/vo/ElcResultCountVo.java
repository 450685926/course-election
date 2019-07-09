package com.server.edu.election.vo;

import java.util.List;

import com.server.edu.common.rest.PageResult;
import com.server.edu.election.dto.ElcResultDto;

/**
 * 选课结果统计（学生统计）
 * @author qiangliz
 *
 */
public class ElcResultCountVo
{
	/**
	 * 选课人数
	 */
	private Integer elcNumberByStudent;
    
	/**
	 * 选课门数
	 */
    private Integer elcGateMumberByStudent;
    
    /**
     * 选课人次
     */
    private Integer elcPersonTimeByStudent;
    
    /**
     * 选课人数
     */
    private Integer elcNumberByFaculty;
    
    /**
     * 选课门数
     */
    private Integer elcGateMumberByFaculty;
    
    /**
     * 选课人次
     */
    private Integer elcPersonTimeByFaculty;
    
    private PageResult<ElcResultDto> elceResultByStudent;
    
    private PageResult<ElcResultDto> elceResultByFaculty;

	public Integer getElcNumberByStudent() {
		return elcNumberByStudent;
	}

	public void setElcNumberByStudent(Integer elcNumberByStudent) {
		this.elcNumberByStudent = elcNumberByStudent;
	}

	public Integer getElcGateMumberByStudent() {
		return elcGateMumberByStudent;
	}

	public void setElcGateMumberByStudent(Integer elcGateMumberByStudent) {
		this.elcGateMumberByStudent = elcGateMumberByStudent;
	}

	public Integer getElcPersonTimeByStudent() {
		return elcPersonTimeByStudent;
	}

	public void setElcPersonTimeByStudent(Integer elcPersonTimeByStudent) {
		this.elcPersonTimeByStudent = elcPersonTimeByStudent;
	}

	public Integer getElcNumberByFaculty() {
		return elcNumberByFaculty;
	}

	public void setElcNumberByFaculty(Integer elcNumberByFaculty) {
		this.elcNumberByFaculty = elcNumberByFaculty;
	}

	public Integer getElcGateMumberByFaculty() {
		return elcGateMumberByFaculty;
	}

	public void setElcGateMumberByFaculty(Integer elcGateMumberByFaculty) {
		this.elcGateMumberByFaculty = elcGateMumberByFaculty;
	}

	public Integer getElcPersonTimeByFaculty() {
		return elcPersonTimeByFaculty;
	}

	public void setElcPersonTimeByFaculty(Integer elcPersonTimeByFaculty) {
		this.elcPersonTimeByFaculty = elcPersonTimeByFaculty;
	}

	public PageResult<ElcResultDto> getElceResultByStudent() {
		return elceResultByStudent;
	}

	public void setElceResultByStudent(PageResult<ElcResultDto> elceResultByStudent) {
		this.elceResultByStudent = elceResultByStudent;
	}

	public PageResult<ElcResultDto> getElceResultByFaculty() {
		return elceResultByFaculty;
	}

	public void setElceResultByFaculty(PageResult<ElcResultDto> elceResultByFaculty) {
		this.elceResultByFaculty = elceResultByFaculty;
	}



}
