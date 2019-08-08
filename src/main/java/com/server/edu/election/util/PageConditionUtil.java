package com.server.edu.election.util;

import com.github.pagehelper.PageHelper;
import com.server.edu.common.PageCondition;

/**
 * 文件导出帮助类，去除分页
 */
public class PageConditionUtil {
    /**
     * 设置导出分页默认值
     * @param obj
     * @param <T>
     * @return
     */
    public static <T> PageCondition getPageCondition(T obj){
        PageCondition<T> condition = new PageCondition<>();
        condition.setPageNum_(1);
        condition.setPageSize_(1000);
        condition.setCondition(obj);
        return condition;
    }

    /**
     * 设置分页
     * @param condition
     * @param <T>
     */
    public static <T> void setPageHelper(PageCondition<T> condition){
        Integer pageNum_ = condition.getPageNum_();
        Integer pageSize_ = condition.getPageSize_();
        if (pageNum_ != 0 && pageSize_ != 0) {
            PageHelper.startPage(pageNum_, pageSize_);
        }
    }
}
