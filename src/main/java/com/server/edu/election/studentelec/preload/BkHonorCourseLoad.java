package com.server.edu.election.studentelec.preload;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.server.edu.election.dao.HonorPlanStdsDao;
import com.server.edu.election.entity.HonorPlanStds;
import com.server.edu.election.studentelec.context.ElecRequest;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.server.edu.common.entity.BclHonorModule;
import com.server.edu.election.rpc.CultureSerivceInvoker;
import com.server.edu.election.studentelec.cache.StudentInfoCache;
import com.server.edu.election.studentelec.context.bk.ElecContextBk;
import com.server.edu.util.CollectionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.entity.Example;

public class BkHonorCourseLoad extends DataProLoad<ElecContextBk>{
    Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private HonorPlanStdsDao honorPlanStdsDao;

    @Override
    public int getOrder()
    {
        return 3;
    }
    
    @Override
    public String getProjectIds()
    {
        return "1";
    }
    
    @Override
    public void load(ElecContextBk context)
    {
        ElecRequest request = context.getRequest();
        StudentInfoCache stu = context.getStudentInfo();
        //查询学生荣誉计划名单信息
        Example example = new Example(HonorPlanStds.class);
        example.createCriteria().andEqualTo("studentId",stu.getStudentId()).andEqualTo("calendarId",request.getCalendarId());
        HonorPlanStds honorPlanStds = honorPlanStdsDao.selectOneByExample(example);

        List<BclHonorModule> stuHonorCourseList = new ArrayList<>();
        if (honorPlanStds!=null){
            List<BclHonorModule> list = CultureSerivceInvoker.findHonorCourseList(stu.getStudentId());
            if(CollectionUtil.isNotEmpty(list)){
                log.info("honor course size:{}", list.size());

                //过滤属于这个学生的荣誉课程
                list.forEach(c->{
                    if (StringUtils.isEmpty(honorPlanStds.getDirectionName())){
                        if (StringUtils.equalsIgnoreCase(c.getHonorModuleName(),honorPlanStds.getHonorPlanName())){
                            stuHonorCourseList.add(c);
                        }
                    }else{
                        if (StringUtils.equalsIgnoreCase(c.getHonorModuleName(),honorPlanStds.getHonorPlanName())
                                && StringUtils.equalsIgnoreCase(c.getDirectionName(),honorPlanStds.getDirectionName())){
                            stuHonorCourseList.add(c);
                        }
                    }

                });
            }
        }

        Set<BclHonorModule> honorCourses = context.getHonorCourses();//荣誉课程
        honorCourses = new HashSet<>(stuHonorCourseList);


    }

}
