package com.server.edu.mutual.testvo;

import java.util.Set;

/**
 * @author: kan yuanfeng
 * @Date: 2018/10/24 16:58
 * @Description:
 */
public class CoursesCategoryVo  {
    private Boolean page = true;

    public Boolean getPage() {
        return null==page?true:page;
    }

    public void setPage(Boolean page) {
        this.page = page;
    }

    private Integer pageNum_;

    private Integer pageSize_;

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
