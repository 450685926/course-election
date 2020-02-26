package com.server.edu.mutual.vo;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Table(name = "score_setting_detail_t")
public class ScoresettingDetail {
	// 主键（自增）
	@Id
	@Column(name = "ID_")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long detailid;
	// score_setting_t表主键
	@Column(name = "SCORE_SETTING_ID_")
	private Long scoreSettingId;
	// 分数名称
	@Column(name = "SCORE_NAME_")
	private String scoreName;
	// 最小值（含）
	@Column(name = "MIN_SCORE_")
	private Double minScore;
	// 最大值（含）
	@Column(name = "MAX_SCORE_")
	private Double maxScore;
	// 默认值
	@Column(name = "DEFAULT_SCORE_")
	private Double defaultScore;
	// 绩点
	@Column(name = "SCORE_POINT_")
	private Double scorePoint;
	// 最后更新时间
	@Column(name = "UPDATE_TIME_")
	private Long updateTimeDet;
	// 删除标识（0：正常；1：删除）
	@Column(name = "DELETE_STATUS")
	private int isDeleteDet;

	public Long getDetailid() {
		return detailid;
	}

	public void setDetailid(Long detailid) {
		this.detailid = detailid;
	}

	public Long getScoreSettingId() {
		return scoreSettingId;
	}

	public void setScoreSettingId(Long scoreSettingId) {
		this.scoreSettingId = scoreSettingId;
	}

	public String getScoreName() {
		return scoreName;
	}

	public void setScoreName(String scoreName) {
		this.scoreName = scoreName;
	}

	public Double getMinScore() {
		return minScore;
	}

	public void setMinScore(Double minScore) {
		this.minScore = minScore;
	}

	public Double getMaxScore() {
		return maxScore;
	}

	public void setMaxScore(Double maxScore) {
		this.maxScore = maxScore;
	}

	public Double getDefaultScore() {
		return defaultScore;
	}

	public void setDefaultScore(Double defaultScore) {
		this.defaultScore = defaultScore;
	}

	public Double getScorePoint() {
		return scorePoint;
	}

	public void setScorePoint(Double scorePoint) {
		this.scorePoint = scorePoint;
	}

	public Long getUpdateTimeDet() {
		return updateTimeDet;
	}

	public void setUpdateTimeDet(Long updateTimeDet) {
		this.updateTimeDet = updateTimeDet;
	}

	public int getIsDeleteDet() {
		return isDeleteDet;
	}

	public void setIsDeleteDet(int isDeleteDet) {
		this.isDeleteDet = isDeleteDet;
	}

	@Override
	public String toString() {
		return "ScoresettingDetail [detailid=" + detailid + ", scoreSettingId=" + scoreSettingId + ", scoreName="
				+ scoreName + ", minScore=" + minScore + ", maxScore=" + maxScore + ", defaultScore=" + defaultScore
				+ ", scorePoint=" + scorePoint + ", updateTimeDet=" + updateTimeDet + ", isDeleteDet=" + isDeleteDet
				+ "]";
	}

}
