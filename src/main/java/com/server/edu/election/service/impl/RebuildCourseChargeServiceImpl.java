package com.server.edu.election.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.server.edu.common.PageCondition;
import com.server.edu.common.rest.PageResult;
import com.server.edu.election.dao.RebuildCourseChargeDao;
import com.server.edu.election.entity.RebuildCourseCharge;
import com.server.edu.election.service.RebuildCourseChargeService;
import com.server.edu.util.CollectionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @description: 重修收费管理
 * @author: bear
 * @create: 2019-01-31 19:39
 */
@Service
@Primary
public class RebuildCourseChargeServiceImpl implements RebuildCourseChargeService {

    @Autowired
    private RebuildCourseChargeDao courseChargeDao;

    /**
    *@Description: 查询收费管理
    *@Param:
    *@return:
    *@Author: bear
    *@date: 2019/2/1 8:58
    */
    @Override
    public PageResult<RebuildCourseCharge> findCourseCharge(PageCondition<RebuildCourseCharge> condition) {
        PageHelper.startPage(condition.getPageNum_(), condition.getPageSize_());
        Page<RebuildCourseCharge> courseCharge = courseChargeDao.findCourseCharge(condition.getCondition());
        return new PageResult<>(courseCharge);
    }


    /**
    *@Description: 删除重修收费信息
    *@Param:
    *@return:
    *@Author: bear
    *@date: 2019/2/1 9:14
    */
    @Override
    @Transactional
    public String deleteCourseCharge(List<Long> ids) {
        if(CollectionUtil.isEmpty(ids)){
            return "common.parameterError";
        }
        courseChargeDao.deleteCourseCharge(ids);
         return  "common.deleteSuccess";
    }

    
    /**
    *@Description: 编辑收费信息
    *@Param: 
    *@return: 
    *@Author: bear
    *@date: 2019/2/1 9:42
    */
    @Override
    @Transactional
    public String editCourseCharge(RebuildCourseCharge courseCharge) {
        RebuildCourseCharge rebuildCourseCharge=new RebuildCourseCharge();
        Page<RebuildCourseCharge> courseCharges = courseChargeDao.findCourseCharge(rebuildCourseCharge);
        if(courseCharges!=null&&courseCharges.getResult().size()>0){
            List<RebuildCourseCharge> result = courseCharges.getResult();
            List<RebuildCourseCharge> collect = result.stream().filter((RebuildCourseCharge vo) -> vo.getId().longValue() != courseCharge.getId().longValue()).collect(Collectors.toList());
            if(CollectionUtil.isNotEmpty(collect)){
                if(collect.contains(courseCharge)){
                    return "common.exist";
                }
            }
        }
        courseChargeDao.updateByPrimaryKeySelective(courseCharge);
        return "common.editSuccess";
    }

    /**
    *@Description: 新增收费信息
    *@Param:
    *@return: 
    *@Author: bear
    *@date: 2019/2/1 9:50
    */
    @Override
    @Transactional
    public String addCourseCharge(RebuildCourseCharge courseCharge) {
        RebuildCourseCharge rebuildCourseCharge=new RebuildCourseCharge();
        Page<RebuildCourseCharge> courseCharges = courseChargeDao.findCourseCharge(rebuildCourseCharge);
        if(courseCharges!=null){
            List<RebuildCourseCharge> result = courseCharges.getResult();
            if(CollectionUtil.isNotEmpty(result)){
                if(result.contains(courseCharge)){
                    return "common.exist";
                }
            }
        }
        courseChargeDao.insertSelective(courseCharge);
        return "common.addsuccess";
    }

}
