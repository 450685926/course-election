package com.server.edu.election.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.server.edu.common.PageCondition;
import com.server.edu.common.rest.PageResult;
import com.server.edu.election.dao.ElcNoGraduateStdsDao;
import com.server.edu.election.dao.StudentDao;
import com.server.edu.election.dto.GraduateExcelDto;
import com.server.edu.election.entity.ElcNoGraduateStds;
import com.server.edu.election.entity.Student;
import com.server.edu.election.service.ElcNoGraduateStdsService;
import com.server.edu.election.vo.ElcNoGraduateStdsVo;

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
        List<String> list=new ArrayList<>();
        for (String studentCode : studentCodes) {
            Student studentByCode = studentDao.findStudentByCode(studentCode);
            if(studentByCode==null){
                list.add("学号"+studentCode+"不存在");
            }else{
                if(mode==3&&"0".equals(studentByCode.getIsOverseas())){//结业生
                    ElcNoGraduateStds student = noGraduateStdsDao.findStudentByCode(studentCode);
                    if(student==null){
                        noGraduateStdsDao.addOverseasOrGraduate(studentCode);
                    }else{
                        list.add("学号"+studentCode+"已经添加");
                    }
                }else if(mode==4&&"1".equals(studentByCode.getIsOverseas())){//留学结业生
                    ElcNoGraduateStds student = noGraduateStdsDao.findStudentByCode(studentCode);
                    if(student==null){
                        noGraduateStdsDao.addOverseasOrGraduate(studentCode);
                    }else{
                        list.add("学号"+studentCode+"已经添加");
                    }
                }else{
                    list.add("学号"+studentCode+"与是否留学不匹配");
                }
            }
        }

        if (list.size() > 0)
        {
            return String.join(",",list);
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
    public String deleteOverseasOrGraduate(List<String> ids) {
        noGraduateStdsDao.deleteOverseasOrGraduate(ids);//删除结业生表
        /*//删除轮次已选学生
        roundStuDao.deleteBystudentId(ids);
        //删除该学生选择课程
        takeDao.deleteStudentById(ids);*/
        return "common.deleteSuccess";
    }

    /**批量导入结业生*/
    @Override
    public String addExcel(List<GraduateExcelDto> datas, Integer mode) {
        List<String> list=new ArrayList<>();
        ElcNoGraduateStdsVo vo=new ElcNoGraduateStdsVo();
        List<String> stringList  =new ArrayList<>();
        vo.setMode(mode);
        Page<ElcNoGraduateStdsVo> overseasOrGraduate = noGraduateStdsDao.findOverseasOrGraduate(vo);//查询所有结业生
        if(overseasOrGraduate!=null){
            List<ElcNoGraduateStdsVo> result = overseasOrGraduate.getResult();
             stringList = result.stream().map(ElcNoGraduateStdsVo::getStudentId).collect(Collectors.toList());
        }
        for (GraduateExcelDto data : datas) {
            String studentId = StringUtils.trim(data.getStudentId());
            String graduateYearStr = StringUtils.trim(data.getGraduateYearStr());
            String remark = StringUtils.trim(data.getRemark());
            if(StringUtils.isNotBlank(studentId)){
                 Student studentByCode = studentDao.findStudentByCode(studentId);
                 if(studentByCode!=null){//学号存在
                    if(mode==3&&"0".equals(studentByCode.getIsOverseas())&&!stringList.contains(studentId)){
                        //插入
                        ElcNoGraduateStds stds = add(studentId, graduateYearStr, remark);
                        noGraduateStdsDao.insertSelective(stds);
                    }else if(mode==4&&"1".equals(studentByCode.getIsOverseas())&&!stringList.contains(studentId)){
                        //插入
                        ElcNoGraduateStds stds = add(studentId, graduateYearStr, remark);
                        noGraduateStdsDao.insertSelective(stds);
                    }else{//学号已经存在或与是否留学不匹配
                        list.add(studentId+"学号存在或与是否留学不匹配");
                    }
                 }else{//学号不存在
                     list.add(studentId+"学号不存在");
                 }
             }

        }

        if(list.size() > 0){
            return org.apache.commons.lang.StringUtils.join(list,",");
        }
        return StringUtils.EMPTY;
    }


    private ElcNoGraduateStds add(String studentId,String graduateYearStr,String remark ){
        ElcNoGraduateStds stds=new ElcNoGraduateStds();
        if(StringUtils.isNotBlank(graduateYearStr)){
            int year = Integer.parseInt(graduateYearStr);
            stds.setGraduateYear(year);
        }else{
            stds.setGraduateYear(null);
        }
        stds.setStudentId(studentId);
        stds.setRemark(remark);
        return stds;
    }
}
