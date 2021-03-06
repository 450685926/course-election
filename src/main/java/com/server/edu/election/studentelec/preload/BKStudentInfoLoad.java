package com.server.edu.election.studentelec.preload;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.server.edu.election.constants.Constants;
import com.server.edu.election.dao.ElcLoserDownStdsDao;
import com.server.edu.election.dao.ElcNoGraduateStdsDao;
import com.server.edu.election.dao.ElecRoundsDao;
import com.server.edu.election.dao.StudentDao;
import com.server.edu.election.dto.StudentDto;
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
    
    @Autowired
    private ElecRoundsDao elecRoundsDao;
    
    @Override
    public void load(ElecContextBk context)
    {
        StudentInfoCache studentInfo = context.getStudentInfo();

        ElecRequest request = context.getRequest();

        Student stu = studentDao.findStudentByCode(studentInfo.getStudentId());
        if (null == stu)
        {
            String msg = String.format("student not find studentId=%s",
                studentInfo.getStudentId());
            throw new RuntimeException(msg);
        }

        //查询学生专业校区维护是否有记录
        String campus = studentDao.getStudentCampus(request.getCalendarId(),stu.getGrade(),stu.getProfession());
        if(StringUtils.isEmpty(campus)){
            campus = stu.getCampus();
        }
        String major = studentDao.getStudentMajor(stu.getGrade(),stu.getProfession());
        studentInfo.setGrade(stu.getGrade());
        studentInfo.setMajor(stu.getProfession());
        studentInfo.setSex(stu.getSex());
        studentInfo.setStudentName(stu.getName());
        studentInfo.setCampus(campus);
        studentInfo.setManagerDeptId(stu.getManagerDeptId());
        studentInfo.setBkMajor(major);
        //专项计划
        studentInfo.setSpcialPlan(stu.getSpcialPlan());
        // 是否留学生
        studentInfo
            .setAboard(Constants.IS_OVERSEAS.equals(stu.getIsOverseas()));
        //是否结业生
//        ElcNoGraduateStds elcNoGraduateStds =
//            elcNoGraduateStdsDao.findStudentByCode(studentInfo.getStudentId());
//        studentInfo.setGraduate(elcNoGraduateStds == null ? false : true);
        StudentDto studentDto = elecRoundsDao.findStudentRoundType(studentInfo.getStudentId());
        boolean isGraduate = false;
        boolean isAboardGraduate = false;
        if(StringUtils.isNotEmpty(studentDto.getGraduateStudent())) {
        	isGraduate =true;
        }
        if(StringUtils.isNotEmpty(studentDto.getInternationalGraduates())) {
        	isAboardGraduate =true;
        }
        studentInfo.setGraduate(isGraduate);
        studentInfo.setAboardGraduate(isAboardGraduate);
        // 否为留降级学生
        ElcLoserDownStds loserDownStds = elcLoserDownStdsDao
            .findLoserDownStds(request.getRoundId(), stu.getStudentCode());
        studentInfo.setRepeater(loserDownStds == null ? false : true);
        //是否欠费
        studentInfo.setIsArrears(stu.getIsArrears());
       
        
    }
    
}
