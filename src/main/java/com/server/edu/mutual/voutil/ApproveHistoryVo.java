package com.server.edu.mutual.voutil;

public class ApproveHistoryVo {
    private Long id;
    private Long approveId;
    private String approvorId;
    private String approvorName;
    private String departmentName;
    private Integer setpId;
    private Long time;
    private String groupStr;
    private String result;
    private String content;
    private String contentId;
    private String fileInfo;
    private String oaNo;

    public ApproveHistoryVo() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getApproveId() {
        return approveId;
    }

    public void setApproveId(Long approveId) {
        this.approveId = approveId;
    }

    public String getApprovorId() {
        return approvorId;
    }

    public void setApprovorId(String approvorId) {
        this.approvorId = approvorId;
    }

    public String getApprovorName() {
        return approvorName;
    }

    public void setApprovorName(String approvorName) {
        this.approvorName = approvorName;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public Integer getSetpId() {
        return setpId;
    }

    public void setSetpId(Integer setpId) {
        this.setpId = setpId;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public String getGroupStr() {
        return groupStr;
    }

    public void setGroupStr(String groupStr) {
        this.groupStr = groupStr;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
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

    public String getFileInfo() {
        return fileInfo;
    }

    public void setFileInfo(String fileInfo) {
        this.fileInfo = fileInfo;
    }

    public String getOaNo() {
        return oaNo;
    }

    public void setOaNo(String oaNo) {
        this.oaNo = oaNo;
    }
}
