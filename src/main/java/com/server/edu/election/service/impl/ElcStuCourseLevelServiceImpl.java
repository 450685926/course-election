package com.server.edu.election.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.server.edu.common.PageCondition;
import com.server.edu.common.locale.I18nUtil;
import com.server.edu.common.rest.PageResult;
import com.server.edu.election.dao.ElcStuCouLevelDao;
import com.server.edu.election.dto.ElcStuCouLevelDto;
import com.server.edu.election.entity.ElcStuCouLevel;
import com.server.edu.election.query.StuCourseLevelQuery;
import com.server.edu.election.service.ElcStuCourseLevelService;
import com.server.edu.exception.ParameterValidateException;
import com.server.edu.util.CollectionUtil;

import tk.mybatis.mapper.entity.Example;

/**
 * 课程能力
 * 
 * 
 * @author  OuYangGuoDong
 * @version  [版本号, 2019年8月22日]
 * @see  [相关类/方法]
 * @since  [产品/模块版本]
 */
@Service
public class ElcStuCourseLevelServiceImpl implements ElcStuCourseLevelService
{
    @Autowired
    private ElcStuCouLevelDao couLevelDao;
    
    @Override
    public PageResult<ElcStuCouLevelDto> listPage(
        PageCondition<StuCourseLevelQuery> page)
    {
        PageHelper.startPage(page.getPageNum_(), page.getPageSize_());
        Page<ElcStuCouLevelDto> listPage = couLevelDao.listPage(page);
        
        return new PageResult<>(listPage);
    }
    
    @Override
    public void add(ElcStuCouLevel couLevel)
    {
        Example example = new Example(ElcStuCouLevel.class);
        example.createCriteria()
            .andEqualTo("studentId", couLevel.getStudentId());
        int count = this.couLevelDao.selectCountByExample(example);
        if (count > 0)
        {
            throw new ParameterValidateException(
                I18nUtil.getMsg("common.exist", couLevel.getStudentId()));
        }
        couLevel.setCreatedAt(new Date());
        couLevel.setUpdatedAt(couLevel.getCreatedAt());
        this.couLevelDao.insert(couLevel);
    }
    
    @Override
    public void update(ElcStuCouLevel couLevel)
    {
        couLevel.setUpdatedAt(new Date());
        this.couLevelDao.updateByPrimaryKeySelective(couLevel);
    }
    
    @Override
    public void delete(List<Long> ids)
    {
        if (CollectionUtil.isNotEmpty(ids))
        {
            Example example = new Example(ElcStuCouLevel.class);
            example.createCriteria().andIn("id", ids);
            this.couLevelDao.deleteByExample(example);
        }
    }
    
    @Override
    public String batchAdd(List<ElcStuCouLevel> datas)
    {
        StringBuilder sb = new StringBuilder();
        int successCount = 0;
        for (ElcStuCouLevel couLevel : datas)
        {
            Example example = new Example(ElcStuCouLevel.class);
            example.createCriteria()
                .andEqualTo("studentId", couLevel.getStudentId());
            int count = this.couLevelDao.selectCountByExample(example);
            if (count < 1)
            {
                successCount++;
                couLevel.setCreatedAt(new Date());
                couLevel.setUpdatedAt(couLevel.getCreatedAt());
                this.couLevelDao.insert(couLevel);
            }
        }
        
        sb.append("导入")
            .append(successCount)
            .append("条;")
            .append("忽略")
            .append(datas.size() - successCount)
            .append("条");
        
        return sb.toString();
        
    }
    
}
