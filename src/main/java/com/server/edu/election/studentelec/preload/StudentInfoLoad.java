package com.server.edu.election.studentelec.preload;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.server.edu.election.dao.ElcLoserDownStdsDao;
import com.server.edu.election.dao.ElcNoGraduateStdsDao;
import com.server.edu.election.dao.StudentDao;
import com.server.edu.election.entity.ElcLoserDownStds;
import com.server.edu.election.entity.ElcNoGraduateStds;
import com.server.edu.election.entity.Student;
import com.server.edu.election.studentelec.cache.StudentInfoCache;
import com.server.edu.election.studentelec.context.ElecContext;

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
public class StudentInfoLoad extends DataProLoad
{
    @Override
    public int getOrder()
    {
        return 0;
    }
    
    @Autowired
    private StudentDao studentDao;
    
    @Autowired
    private ElcNoGraduateStdsDao elcNoGraduateStdsDao;

    @Autowired
    private ElcLoserDownStdsDao elcLoserDownStdsDao;
    
    @Override
    public void load(ElecContext context)
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
        //专项计划
        studentInfo.setSpcialPlan(stu.getSpcialPlan());
        // 是否留学生
        studentInfo.setAboard("1".equals(stu.getIsOverseas()));
        //是否结业生
        studentInfo.setGraduate(false);
        ElcNoGraduateStds elcNoGraduateStds = elcNoGraduateStdsDao.findStudentByCode(studentInfo.getStudentId());
        if(elcNoGraduateStds!=null) {
        	studentInfo.setGraduate(true);
        }
        // 1. 查询学生是否为留降级学生
        ElcLoserDownStds loserDownStds = elcLoserDownStdsDao.findLoserDownStds(context.getRoundId(), stu.getStudentCode());
        if(loserDownStds==null){
            studentInfo.setRepeater(false);
        }else{
            studentInfo.setRepeater(true);
        }

        // 3. 是否缴费//todo
        
    }
    
}
