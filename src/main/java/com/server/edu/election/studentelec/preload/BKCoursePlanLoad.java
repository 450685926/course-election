package com.server.edu.election.studentelec.preload;

import java.util.List;

import org.springframework.stereotype.Component;

import com.server.edu.election.rpc.CultureSerivceInvoker;
import com.server.edu.election.studentelec.context.ElecContext;

/**
 * 本科生培养计划课程查询
 * 
 * 
 * @author  OuYangGuoDong
 * @version  [版本号, 2019年2月25日]
 * @see  [相关类/方法]
 * @since  [产品/模块版本]
 */
@Component
public class BKCoursePlanLoad implements DataProLoad
{
    @Override
    public void load(ElecContext context)
    {
        String studentId = context.getStudentId();
        List<String> courseCodes = CultureSerivceInvoker.getCourseCodes(studentId);
        // TODO Auto-generated method stub
        
    }
    
}
