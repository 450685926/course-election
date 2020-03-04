package com.server.edu.mutual.testvo;


import java.util.Set;

/**
 * @Auther: kan yuanfeng
 * @Date: 2018/10/24 16:58
 * @Description:
 */
public class CoursesCategoryLabelVo {
    private Boolean page = true;

    public Boolean getPage() {
        return null == page ? true : page;
    }

    public void setPage(Boolean page) {
        this.page = page;
    }

    private Integer pageNum_;

    private Integer pageSize_;

    /**
     * 课程分类名称
     */
    private String labelName;

    /**
     * 课程分类名称英文
     */
    private String labelNameEn;

    public String getLabelName() {
        return labelName;
    }

    public void setLabelName(String labelName) {
        this.labelName = labelName;
    }

    public String getLabelNameEn() {
        return labelNameEn;
    }

    public void setLabelNameEn(String labelNameEn) {
        this.labelNameEn = labelNameEn;
    }

    public Integer getPageNum_() {
        return pageNum_;
    }

    public void setPageNum_(Integer pageNum_) {
        this.pageNum_ = pageNum_;
    }

    public Integer getPageSize_() {
        return pageSize_;
    }

    public void setPageSize_(Integer pageSize_) {
        this.pageSize_ = pageSize_;
    }

    /**
     * @Description: 批量删除接受id
     * @author kan yuanfeng
     * @date 2018/10/24 16:59
     */
    private Set<Long> ids;

    public Set<Long> getIds() {
        return ids;
    }

    public void setIds(Set<Long> ids) {
        this.ids = ids;
    }
}
