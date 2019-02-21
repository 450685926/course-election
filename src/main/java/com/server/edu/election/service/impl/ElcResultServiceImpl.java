package com.server.edu.election.service.impl;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.server.edu.common.PageCondition;
import com.server.edu.common.rest.PageResult;
import com.server.edu.election.dao.ElcAffinityCoursesStdsDao;
import com.server.edu.election.dao.ElcCourseTakeDao;
import com.server.edu.election.dao.ElcInvincibleStdsDao;
import com.server.edu.election.dao.StudentDao;
import com.server.edu.election.dao.TeachingClassDao;
import com.server.edu.election.dto.AutoRemoveDto;
import com.server.edu.election.dto.SuggestProfessionDto;
import com.server.edu.election.entity.ElcCourseTake;
import com.server.edu.election.entity.ElcInvincibleStds;
import com.server.edu.election.entity.Student;
import com.server.edu.election.entity.TeachingClass;
import com.server.edu.election.query.ElcResultQuery;
import com.server.edu.election.service.ElcResultService;
import com.server.edu.election.vo.ElcAffinityCoursesStdsVo;
import com.server.edu.election.vo.TeachingClassVo;

@Service
public class ElcResultServiceImpl implements ElcResultService
{
    @Autowired
    private TeachingClassDao classDao;
    
    @Autowired
    private ElcCourseTakeDao courseTakeDao;
    
    @Autowired
    private ElcInvincibleStdsDao invincibleStdsDao;
    
    @Autowired
    private ElcAffinityCoursesStdsDao affinityCoursesStdsDao;
    
    @Autowired
    private StudentDao studentDao;
    
    @Override
    public PageResult<TeachingClassVo> listPage(
        PageCondition<ElcResultQuery> page)
    {
        PageHelper.startPage(page.getPageNum_(), page.getPageSize_());
        Page<TeachingClassVo> listPage = classDao.listPage(page.getCondition());
        
        return new PageResult<>(listPage);
    }
    
    static String key(SuggestProfessionDto dto)
    {
        return dto.getGrade() + "-" + dto.getProfession();
    }
    
    @Override
    public void autoRemove(AutoRemoveDto dto)
    {
        Long teachingClassId = dto.getTeachingClassId();
        TeachingClass teachingClass = classDao.selectOversize(teachingClassId);
        if (null != teachingClass)
        {
            ElcCourseTake param = new ElcCourseTake();
            param.setTeachingClassId(teachingClassId);
            List<ElcCourseTake> takes = courseTakeDao.select(param);
            // 特殊学生
            List<ElcInvincibleStds> invincibleStds =
                invincibleStdsDao.selectAll();
            List<String> invincibleStdIds = invincibleStds.stream()
                .map(mapper -> mapper.getStudentId())
                .collect(toList());
            // 优先学生
            List<ElcAffinityCoursesStdsVo> affinityCoursesStds =
                affinityCoursesStdsDao.selectStuAndCourse();
            Set<String> affinityCoursesStdSet = affinityCoursesStds.stream()
                .map(mapper -> mapper.getCourseId() + "-"
                    + mapper.getStudentId())
                .collect(toSet());
            List<SuggestProfessionDto> suggestProfessionList =
                classDao.selectSuggestProfession(teachingClassId);
            
            Map<String, Integer> suggestProfMap = new HashMap<>();
            suggestProfessionList.stream().forEach(action -> {
                suggestProfMap.put(key(action), action.getNumber());
            });
            
            List<String> suggestStudentList =
                classDao.selectSuggestStudent(teachingClassId);
            
            List<Long> removeIds = new ArrayList<>();
            List<Student> students = new ArrayList<>();
            for (ElcCourseTake take : takes)
            {
                String courseCode = take.getCourseCode();
                String studentId = take.getStudentId();
                if (!dto.getInvincibleStu()
                    && invincibleStdIds.contains(studentId))
                {
                    break;
                }
                if (!dto.getAffinityStu() && affinityCoursesStdSet
                    .contains(courseCode + "-" + studentId))
                {
                    break;
                }
                Student stu = studentDao.findStudentByCode(studentId);
                students.add(stu);
                // 配课年级专业
                if (dto.getGradAndPre())
                {
                   String key = stu.getGrade() + "-" + stu.getProfession();
                    if (suggestProfMap.containsKey(key))
                    {
                        break;
                    }
                }
                if (dto.getGradAndPrePeople())
                {
                    
                }
                removeIds.add(take.getId());
            }
        }
    }
    
}
