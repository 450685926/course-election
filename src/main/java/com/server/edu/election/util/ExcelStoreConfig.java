package com.server.edu.election.util;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * excel导入导出 key store定义
 * @author tengzhou
 * @since 2018年6月21日12:01:28
 */
@Configuration
@ConfigurationProperties(prefix="excel")
@PropertySource(value="classpath:excelstore.properties")
public class ExcelStoreConfig
{
    private List<String> elcResultCountExportByStudentTitle;

    private List<String> elcResultCountExportByStudentKey;
    
    private List<String> elcResultCountExportByFacultyTitle;
    
    private List<String> elcResultCountExportByFacultyKey;
    
    private List<String> allNonSelectedCourseStudentTitle;
    
    private List<String> allNonSelectedCourseStudentKey;
    
    private List<String> exemptionCountExportTitle;
    
    private List<String> exemptionCountExportKey;
    
    private List<String> graduateExemptionApplyExportTitle;
    
    private List<String> graduateExemptionApplyExportKey;
    
    private List<String> graduateNoSelectStudenExportTitle; // 研究生未选课学生名单
    
    private List<String> graduateNoSelectStudenExportKey;   // 研究生未选课学生名单
    
    /**
     * 本研互选名单
     */
    private List<String> exportelcMutualStuListExcelKey;

    private List<String> exportelcMutualStuListExcelTitle;
    

	public List<String> getElcResultCountExportByStudentTitle() {
		return elcResultCountExportByStudentTitle;
	}

	public void setElcResultCountExportByStudentTitle(List<String> elcResultCountExportByStudentTitle) {
		this.elcResultCountExportByStudentTitle = elcResultCountExportByStudentTitle;
	}

	public List<String> getElcResultCountExportByStudentKey() {
		return elcResultCountExportByStudentKey;
	}

	public void setElcResultCountExportByStudentKey(List<String> elcResultCountExportByStudentKey) {
		this.elcResultCountExportByStudentKey = elcResultCountExportByStudentKey;
	}

	public List<String> getElcResultCountExportByFacultyTitle() {
		return elcResultCountExportByFacultyTitle;
	}

	public void setElcResultCountExportByFacultyTitle(List<String> elcResultCountExportByFacultyTitle) {
		this.elcResultCountExportByFacultyTitle = elcResultCountExportByFacultyTitle;
	}

	public List<String> getElcResultCountExportByFacultyKey() {
		return elcResultCountExportByFacultyKey;
	}

	public void setElcResultCountExportByFacultyKey(List<String> elcResultCountExportByFacultyKey) {
		this.elcResultCountExportByFacultyKey = elcResultCountExportByFacultyKey;
	}

	public List<String> getAllNonSelectedCourseStudentTitle() {
		return allNonSelectedCourseStudentTitle;
	}

	public void setAllNonSelectedCourseStudentTitle(List<String> allNonSelectedCourseStudentTitle) {
		this.allNonSelectedCourseStudentTitle = allNonSelectedCourseStudentTitle;
	}

	public List<String> getAllNonSelectedCourseStudentKey() {
		return allNonSelectedCourseStudentKey;
	}

	public void setAllNonSelectedCourseStudentKey(List<String> allNonSelectedCourseStudentKey) {
		this.allNonSelectedCourseStudentKey = allNonSelectedCourseStudentKey;
	}

	public List<String> getExemptionCountExportTitle() {
		return exemptionCountExportTitle;
	}

	public void setExemptionCountExportTitle(List<String> exemptionCountExportTitle) {
		this.exemptionCountExportTitle = exemptionCountExportTitle;
	}

	public List<String> getExemptionCountExportKey() {
		return exemptionCountExportKey;
	}

	public void setExemptionCountExportKey(List<String> exemptionCountExportKey) {
		this.exemptionCountExportKey = exemptionCountExportKey;
	}

	public List<String> getGraduateExemptionApplyExportTitle() {
		return graduateExemptionApplyExportTitle;
	}

	public void setGraduateExemptionApplyExportTitle(List<String> graduateExemptionApplyExportTitle) {
		this.graduateExemptionApplyExportTitle = graduateExemptionApplyExportTitle;
	}

	public List<String> getGraduateExemptionApplyExportKey() {
		return graduateExemptionApplyExportKey;
	}

	public void setGraduateExemptionApplyExportKey(List<String> graduateExemptionApplyExportKey) {
		this.graduateExemptionApplyExportKey = graduateExemptionApplyExportKey;
	}

	public List<String> getGraduateNoSelectStudenExportTitle() {
		return graduateNoSelectStudenExportTitle;
	}

	public void setGraduateNoSelectStudenExportTitle(List<String> graduateNoSelectStudenExportTitle) {
		this.graduateNoSelectStudenExportTitle = graduateNoSelectStudenExportTitle;
	}

	public List<String> getGraduateNoSelectStudenExportKey() {
		return graduateNoSelectStudenExportKey;
	}

	public void setGraduateNoSelectStudenExportKey(List<String> graduateNoSelectStudenExportKey) {
		this.graduateNoSelectStudenExportKey = graduateNoSelectStudenExportKey;
	}

    public List<String> getExportelcMutualStuListExcelKey()
    {
        return exportelcMutualStuListExcelKey;
    }

    public void setExportelcMutualStuListExcelKey(List<String> exportelcMutualStuListExcelKey)
    {
        this.exportelcMutualStuListExcelKey = exportelcMutualStuListExcelKey;
    }

    public List<String> getExportelcMutualStuListExcelTitle()
    {
        return exportelcMutualStuListExcelTitle;
    }

    public void setExportelcMutualStuListExcelTitle(List<String> exportelcMutualStuListExcelTitle)
    {
        this.exportelcMutualStuListExcelTitle = exportelcMutualStuListExcelTitle;
    }

	
}