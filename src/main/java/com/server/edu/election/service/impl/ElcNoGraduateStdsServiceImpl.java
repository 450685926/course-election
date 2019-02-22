package com.server.edu.election.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.server.edu.common.PageCondition;
import com.server.edu.common.rest.PageResult;
import com.server.edu.election.dao.ElcNoGraduateStdsDao;
import com.server.edu.election.dao.StudentDao;
import com.server.edu.election.entity.ElcNoGraduateStds;
import com.server.edu.election.entity.Student;
import com.server.edu.election.service.ElcNoGraduateStdsService;
import com.server.edu.election.vo.ElcNoGraduateStdsVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;


import java.util.ArrayList;
import java.util.List;

/**
 * @description: 留学生
 * @author: bear
 * @create: 2019-02-22 10:29
 */

@Service
@Primary
public class ElcNoGraduateStdsServiceImpl implements ElcNoGraduateStdsService {

    @Autowired
    private ElcNoGraduateStdsDao noGraduateStdsDao;

    @Autowired
    private StudentDao studentDao;
    
    /**
    *@Description: 查询留学生结业名单
    *@Param:
    *@return: 
    *@Author: bear
    *@date: 2019/2/22 10:30
    */
    @Override
    public PageResult<ElcNoGraduateStdsVo> findOverseasOrGraduate(PageCondition<ElcNoGraduateStdsVo> condition) {
        PageHelper.startPage(condition.getPageNum_(),condition.getPageSize_());
        Page<ElcNoGraduateStdsVo> overseasOrGraduate = noGraduateStdsDao.findOverseasOrGraduate(condition.getCondition());
        return new PageResult<>(overseasOrGraduate);
    }


    /**
    *@Description: 新增结业名单
    *@Param:
    *@return: 
    *@Author: bear
    *@date: 2019/2/22 10:55
    */
    @Override
    public String addOverseasOrGraduate( List<String> studentCodes,Integer mode) {
        StringBuilder sb = new StringBuilder();
        List<String> list=new ArrayList<>();
        for (String studentCode : studentCodes) {
            Student studentByCode = studentDao.findStudentByCode(studentCode);
            if(studentByCode==null){
                sb.append("学号"+studentCode+"不存在");
            }else{
                if(mode==3&&"0".equals(studentByCode.getIsOverseas())){//结业生
                    ElcNoGraduateStds student = noGraduateStdsDao.findStudentByCode(studentCode);
                    if(student==null){
                        noGraduateStdsDao.addOverseasOrGraduate(studentCode);
                    }else{
                        sb.append("学号"+studentCode+"已经添加");
                    }
                }else if(mode==4&&"1".equals(studentByCode.getIsOverseas())){//留学结业生
                    ElcNoGraduateStds student = noGraduateStdsDao.findStudentByCode(studentCode);
                    if(student==null){
                        noGraduateStdsDao.addOverseasOrGraduate(studentCode);
                    }else{
                        sb.append("学号"+studentCode+"已经添加");
                    }
                }else{
                    sb.append("学号"+studentCode+"与是否留学不匹配");
                }
            }
        }

        if (sb.length() > 0)
        {
            return sb.substring(0, sb.length() - 1);
        }
        return StringUtils.EMPTY;
    }

    /**
    *@Description: 删除结业生
    *@Param:
    *@return: 
    *@Author: bear
    *@date: 2019/2/22 12:35
    */
    @Override
    public String deleteOverseasOrGraduate(List<Long> ids) {
        noGraduateStdsDao.deleteOverseasOrGraduate(ids);
        return "common.deleteSuccess";
    }

}
