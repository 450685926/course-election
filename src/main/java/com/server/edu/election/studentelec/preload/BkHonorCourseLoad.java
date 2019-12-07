package com.server.edu.election.studentelec.preload;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.server.edu.common.entity.BclHonorModule;
import com.server.edu.election.rpc.CultureSerivceInvoker;
import com.server.edu.election.studentelec.cache.StudentInfoCache;
import com.server.edu.election.studentelec.context.bk.ElecContextBk;
import com.server.edu.util.CollectionUtil;

public class BkHonorCourseLoad extends DataProLoad<ElecContextBk>{
    Logger log = LoggerFactory.getLogger(getClass());
    
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
        StudentInfoCache stu = context.getStudentInfo();
        List<BclHonorModule> list = CultureSerivceInvoker.findHonorCourseList(stu.getStudentId());
        if(CollectionUtil.isNotEmpty(list)){
            log.info("plan course size:{}", list.size());
            @SuppressWarnings("unused")
			Set<BclHonorModule> honorCourses = context.getHonorCourses();//荣誉课程
            honorCourses = new HashSet<>(list);
        }
    }

}
