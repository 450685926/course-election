package com.server.edu.election.studentelec.preload;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.server.edu.election.constants.Constants;
import com.server.edu.election.dao.ElcLoserDownStdsDao;
import com.server.edu.election.dao.ElcNoGraduateStdsDao;
import com.server.edu.election.dao.StudentDao;
import com.server.edu.election.entity.ElcLoserDownStds;
import com.server.edu.election.entity.ElcNoGraduateStds;
import com.server.edu.election.entity.Student;
import com.server.edu.election.studentelec.cache.StudentInfoCache;
import com.server.edu.election.studentelec.context.ElecRequest;
import com.server.edu.election.studentelec.context.bk.ElecContextBk;

/**
 * 加载学生信息
 * 
 * 
 * @author  OuYangGuoDong
 * @version  [版本号, 2019年2月26日]
 * @see  [相关类/方法]
 * @since  [产品/模块版本]
 */
@Component
public class BKStudentInfoLoad extends DataProLoad<ElecContextBk>
{
    @Override
    public int getOrder()
    {
        return 0;
    }
    
    @Override
    public String getProjectIds()
    {
        return "1";
    }
    
    @Autowired
    private StudentDao studentDao;
    
    @Autowired
    private ElcNoGraduateStdsDao elcNoGraduateStdsDao;
    
    @Autowired
    private ElcLoserDownStdsDao elcLoserDownStdsDao;
    
    @Override
    public void load(ElecContextBk context)
    {
        StudentInfoCache studentInfo = context.getStudentInfo();
        
        Student stu = studentDao.findStudentByCode(studentInfo.getStudentId());
        if (null == stu)
        {
            String msg = String.format("student not find studentId=%s",
                studentInfo.getStudentId());
            throw new RuntimeException(msg);
        }
        studentInfo.setGrade(stu.getGrade());
        studentInfo.setMajor(stu.getProfession());
        studentInfo.setSex(stu.getSex());
        studentInfo.setStudentName(stu.getName());
        studentInfo.setCampus(stu.getCampus());
        studentInfo.setManagerDeptId(stu.getManagerDeptId());
        //专项计划
        studentInfo.setSpcialPlan(stu.getSpcialPlan());
        // 是否留学生
        studentInfo
            .setAboard(Constants.IS_OVERSEAS.equals(stu.getIsOverseas()));
        //是否结业生
        ElcNoGraduateStds elcNoGraduateStds =
            elcNoGraduateStdsDao.findStudentByCode(studentInfo.getStudentId());
        studentInfo.setGraduate(elcNoGraduateStds == null ? false : true);
        
        // 否为留降级学生
        ElecRequest request = context.getRequest();
        ElcLoserDownStds loserDownStds = elcLoserDownStdsDao
            .findLoserDownStds(request.getRoundId(), stu.getStudentCode());
        studentInfo.setRepeater(loserDownStds == null ? false : true);
        
    }
    
}
