package com.server.edu.mutual.voutil;

import com.server.edu.common.entity.ApproveAttachment;

import java.util.List;

public class ApproveListVo {
    private Long id;
    private String no;
    private String applyNo;
    private String approvorName;
    private String approvorId;
    private String userType;
    private String applicantId;
    private String applicantName;
    private Long time;
    private String submitterId;
    private String submitterName;
    private String content;
    private String contentId;
    private String result;
    private String departmentId;
    private int approveType;
    private Long workflowId;
    private Long wfType;
    private String wfTypeName;
    private boolean isNeedOaNum;
    private List<ApproveHistoryVo> listApproveHistory;
    private List<ApproveAttachment> listApproveAttachment;

    public ApproveListVo() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
    }

    public String getApplyNo() {
        return applyNo;
    }

    public void setApplyNo(String applyNo) {
        this.applyNo = applyNo;
    }

    public String getApprovorName() {
        return approvorName;
    }

    public void setApprovorName(String approvorName) {
        this.approvorName = approvorName;
    }

    public String getApprovorId() {
        return approvorId;
    }

    public void setApprovorId(String approvorId) {
        this.approvorId = approvorId;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getApplicantId() {
        return applicantId;
    }

    public void setApplicantId(String applicantId) {
        this.applicantId = applicantId;
    }

    public String getApplicantName() {
        return applicantName;
    }

    public void setApplicantName(String applicantName) {
        this.applicantName = applicantName;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public String getSubmitterId() {
        return submitterId;
    }

    public void setSubmitterId(String submitterId) {
        this.submitterId = submitterId;
    }

    public String getSubmitterName() {
        return submitterName;
    }

    public void setSubmitterName(String submitterName) {
        this.submitterName = submitterName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContentId() {
        return contentId;
    }

    public void setContentId(String contentId) {
        this.contentId = contentId;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(String departmentId) {
        this.departmentId = departmentId;
    }

    public int getApproveType() {
        return approveType;
    }

    public void setApproveType(int approveType) {
        this.approveType = approveType;
    }

    public Long getWorkflowId() {
        return workflowId;
    }

    public void setWorkflowId(Long workflowId) {
        this.workflowId = workflowId;
    }

    public Long getWfType() {
        return wfType;
    }

    public void setWfType(Long wfType) {
        this.wfType = wfType;
    }

    public String getWfTypeName() {
        return wfTypeName;
    }

    public void setWfTypeName(String wfTypeName) {
        this.wfTypeName = wfTypeName;
    }

    public boolean getIsNeedOaNum() {
        return this.isNeedOaNum;
    }

    public void setisNeedOaNum(boolean isNeedOaNum) {
        this.isNeedOaNum = isNeedOaNum;
    }

    public List<ApproveHistoryVo> getListApproveHistory() {
        return listApproveHistory;
    }

    public void setListApproveHistory(List<ApproveHistoryVo> listApproveHistory) {
        this.listApproveHistory = listApproveHistory;
    }

    public List<ApproveAttachment> getListApproveAttachment() {
        return listApproveAttachment;
    }

    public void setListApproveAttachment(List<ApproveAttachment> listApproveAttachment) {
        this.listApproveAttachment = listApproveAttachment;
    }
}
