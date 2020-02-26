package com.server.edu.mutual.vo;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;


@Table(name = "score_setting_t")
public class Scoresetting {
	// 主键（自增）
	@Id
	@Column(name = "ID_")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	// 字典表t_dictionary_base,code_字段对应
	@Column(name = "CORETYPE_CODE_")
	private Integer coretypeCode;
	// 中文名称
	@Column(name = "CHINESE_NAME_")
	private String chineseName;
	// 英文名称
	@Column(name = "ENGLISH_NAME_")
	private String englishName;
	// 及格线
	@Column(name = "PASSLINE_")
	private Double passline;
	// 最后更新时间
	@Column(name = "UPDATE_TIME_")
	private Long updateTime;
	// 删除标识（0：正常；1：删除）
	@Column(name = "DELETE_STATUS")
	private int isDelete;
	// 成绩记录方式集合
	private List<ScoresettingDetail> scoresettingDetailList;
    //检索文本
	@Transient
    private String searchText;
	//管理部门
	@Column(name = "PROJECT_ID_")
	private String manageDptId;
    
	
	public String getManageDptId() {
		return manageDptId;
	}

	public void setManageDptId(String manageDptId) {
		this.manageDptId = manageDptId;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getChineseName() {
		return chineseName;
	}

	public void setChineseName(String chineseName) {
		this.chineseName = chineseName;
	}

	public String getEnglishName() {
		return englishName;
	}

	public void setEnglishName(String englishName) {
		this.englishName = englishName;
	}

	public Double getPassline() {
		return passline;
	}

	public void setPassline(Double passline) {
		this.passline = passline;
	}

	public Long getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Long updateTime) {
		this.updateTime = updateTime;
	}

	public int getIsDelete() {
		return isDelete;
	}

	public void setIsDelete(int isDelete) {
		this.isDelete = isDelete;
	}

	public Integer getCoretypeCode() {
		return coretypeCode;
	}

	public void setCoretypeCode(Integer coretypeCode) {
		this.coretypeCode = coretypeCode;
	}

	public List<ScoresettingDetail> getScoresettingDetailList() {
		return scoresettingDetailList;
	}

	public void setScoresettingDetailList(List<ScoresettingDetail> scoresettingDetailList) {
		this.scoresettingDetailList = scoresettingDetailList;
	}
    
	public String getSearchText() {
		return searchText;
	}

	public void setSearchText(String searchText) {
		this.searchText = searchText;
	}

	@Override
	public String toString() {
		return "Scoresetting{" +
				"id=" + id +
				", coretypeCode='" + coretypeCode + '\'' +
				", chineseName='" + chineseName + '\'' +
				", englishName='" + englishName + '\'' +
				", passline='" + passline + '\'' +
				", updateTime=" + updateTime +
				", isDelete=" + isDelete +
				", scoresettingDetailList=" + scoresettingDetailList +
				'}';
	}
}
