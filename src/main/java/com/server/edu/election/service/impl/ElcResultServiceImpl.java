package com.server.edu.election.service.impl;

import static java.util.stream.Collectors.toSet;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.server.edu.common.PageCondition;
import com.server.edu.common.rest.PageResult;
import com.server.edu.election.constants.Constants;
import com.server.edu.election.dao.ElcAffinityCoursesStdsDao;
import com.server.edu.election.dao.ElcCourseSuggestSwitchDao;
import com.server.edu.election.dao.ElcCourseTakeDao;
import com.server.edu.election.dao.ElcInvincibleStdsDao;
import com.server.edu.election.dao.StudentDao;
import com.server.edu.election.dao.TeachingClassDao;
import com.server.edu.election.dao.TeachingClassElectiveRestrictAttrDao;
import com.server.edu.election.dao.TeachingClassTeacherDao;
import com.server.edu.election.dto.AutoRemoveDto;
import com.server.edu.election.dto.SuggestProfessionDto;
import com.server.edu.election.entity.ElcAffinityCoursesStds;
import com.server.edu.election.entity.ElcCourseSuggestSwitch;
import com.server.edu.election.entity.ElcCourseTake;
import com.server.edu.election.entity.Student;
import com.server.edu.election.entity.TeachingClass;
import com.server.edu.election.entity.TeachingClassTeacher;
import com.server.edu.election.query.ElcResultQuery;
import com.server.edu.election.service.ElcCourseTakeService;
import com.server.edu.election.service.ElcResultService;
import com.server.edu.election.service.impl.resultFilter.ClassElcConditionFilter;
import com.server.edu.election.service.impl.resultFilter.GradAndPreFilter;
import com.server.edu.election.vo.TeachingClassVo;
import com.server.edu.util.CollectionUtil;

import tk.mybatis.mapper.entity.Example;

@Service
public class ElcResultServiceImpl implements ElcResultService
{
    Logger logger = LoggerFactory.getLogger(getClass());
    
    @Autowired
    private TeachingClassDao classDao;
    
    @Autowired
    private ElcCourseTakeDao courseTakeDao;
    
    @Autowired
    private ElcInvincibleStdsDao invincibleStdsDao;
    
    @Autowired
    private ElcAffinityCoursesStdsDao affinityCoursesStdsDao;
    
    @Autowired
    private TeachingClassElectiveRestrictAttrDao classElectiveRestrictAttrDao;
    
    @Autowired
    private StudentDao studentDao;
    
    @Autowired
    private ElcCourseTakeService courseTakeService;
    
    @Autowired
    private TeachingClassTeacherDao teacherDao;
    
    @Autowired
    private ElcCourseSuggestSwitchDao elcCourseSuggestSwitchDao;
    
    @Override
    public PageResult<TeachingClassVo> listPage(
        PageCondition<ElcResultQuery> page)
    {
        PageHelper.startPage(page.getPageNum_(), page.getPageSize_());
        ElcResultQuery condition = page.getCondition();
        Page<TeachingClassVo> listPage = new Page<TeachingClassVo>();
        if (StringUtils.equals(condition.getProjectId(), "1")) {
        	listPage = classDao.listPage(page.getCondition());
		}else {
			listPage = classDao.grduateListPage(page.getCondition());
		}
        
        List<TeachingClassVo> list = listPage.getResult();
        if(CollectionUtil.isNotEmpty(list)) {
        	List<Long>  classIds = list.stream().map(TeachingClassVo::getId).collect(Collectors.toList());
    	    // 查找任课教师信息
            Example teacherExample = new Example(TeachingClassTeacher.class);
            teacherExample.createCriteria().andIn("teachingClassId", classIds).
                    andEqualTo("type",Constants.TEACHER_DEFAULT);
            List<TeachingClassTeacher> teacherList = teacherDao.selectByExample(teacherExample);
            for(TeachingClassVo vo: list) {
            	if(CollectionUtil.isNotEmpty(teacherList)) {
                	List<TeachingClassTeacher> teachers = teacherList.stream().filter(c->vo.getId().equals(c.getTeachingClassId())).collect(Collectors.toList());
                	StringBuilder stringBuilder = new StringBuilder();
                	if(CollectionUtil.isNotEmpty(teachers)) {
                		for(TeachingClassTeacher teacher:teachers) {
                			stringBuilder.append(teacher.getTeacherName());
                			stringBuilder.append("(");
                			stringBuilder.append(teacher.getTeacherCode());
                			stringBuilder.append(")");
                			stringBuilder.append(",");
                		}
                		vo.setTeacherName(stringBuilder.deleteCharAt(stringBuilder.length()-1).toString());
                	}
            	}
            }
        }
        return new PageResult<>(listPage);
    }
    
    @Override
    public void adjustClassNumber(TeachingClass teachingClass)
    {
        TeachingClass record = new TeachingClass();
        record.setId(teachingClass.getId());
        record.setNumber(teachingClass.getNumber());
        classDao.updateByPrimaryKeySelective(record);
    }
    
    String key(SuggestProfessionDto dto)
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
            String course = takes.get(0).getCourseCode();
        	if(Boolean.TRUE.equals(dto.getSuggestSwitchCourse())) {
        		List<ElcCourseSuggestSwitch> suggestSwitchs = elcCourseSuggestSwitchDao.selectAll();
        		if(CollectionUtil.isNotEmpty(suggestSwitchs)) {
        			List<String> suggestCoures = suggestSwitchs.stream().map(ElcCourseSuggestSwitch::getCourseCode).collect(Collectors.toList());
        			if(!suggestCoures.contains(course)) {
        				return;
        			}
        		}
        	}
            // 特殊学生
            List<String> invincibleStdIds =
                invincibleStdsDao.selectAllStudentId();
            // 优先学生
            List<ElcAffinityCoursesStds> affinityCoursesStds =
                affinityCoursesStdsDao.selectStuAndCourse();
            Set<String> affinityCoursesStdSet = affinityCoursesStds.stream()
                .map(aff -> aff.getCourseCode() + "-" + aff.getStudentId())
                .collect(toSet());
            
            List<Student> normalStus = new ArrayList<>();
            List<Student> invincibleStus = new ArrayList<>();
            List<Student> affinityStus = new ArrayList<>();
            //把学生分类(普通学生、优先学生、特殊学生)
            for (ElcCourseTake take : takes)
            {
                String courseCode = take.getCourseCode();
                String studentId = take.getStudentId();
                Student stu = studentDao.findStudentByCode(studentId);
                if (invincibleStdIds.contains(studentId))
                {
                    invincibleStus.add(stu);
                }
                else if (affinityCoursesStdSet
                    .contains(courseCode + "-" + studentId))
                {
                    affinityStus.add(stu);
                }
                else
                {
                    normalStus.add(stu);
                }
            }
            
            if (!Boolean.TRUE.equals(dto.getInvincibleStu()))
            {
                invincibleStus.clear();
            }
            if (!Boolean.TRUE.equals(dto.getAffinityStu()))
            {
                affinityStus.clear();
            }
            
            List<String> removeStus = new ArrayList<>();
            if (invincibleStus.size() + affinityStus.size()
                + normalStus.size() > teachingClass.getNumber())
            {
                GradAndPreFilter gradAndPreFilter =
                    new GradAndPreFilter(dto, classDao);
                gradAndPreFilter.init();
                ClassElcConditionFilter elcConditionFilter =
                    new ClassElcConditionFilter(dto,
                        classElectiveRestrictAttrDao);
                elcConditionFilter.init();
                
                // 这里做三次的原因是因为有三种学生类型
                for (int i = 0; i < 3; i++)
                {
                    List<Student> stuList = normalStus.size() > 0 ? normalStus
                        : (affinityStus.size() > 0 ? affinityStus
                            : invincibleStus);
                    
                    if (CollectionUtil.isEmpty(stuList))
                    {
                        continue;
                    }
                    gradAndPreFilter.execute(stuList, removeStus);
                    elcConditionFilter.execute(stuList, removeStus);
                    
                    if (CollectionUtil.isEmpty(stuList))
                    {
                        //执行完后人数还是超过上限则进行随机删除
                        int overSize = (invincibleStus.size()
                            + affinityStus.size() + normalStus.size())
                            - teachingClass.getNumber();
                        if (overSize > 0)
                        {
                            // 1.普通人数小于超出人数则说明优先人数的人也超了这时需要清空普通人数
                            // 2.普通人数大于超出人数则说明只有普通人数超了，普通人数需要等于超出人数
                            int limitNumber = overSize;
                            if (stuList.size() < overSize)
                            {
                                limitNumber = 0;
                            }
                            GradAndPreFilter
                                .randomRemove(removeStus, limitNumber, stuList);
                        }
                    }
                }
                
                removeAndRecordLog(dto,
                    teachingClassId,
                    teachingClass,
                    removeStus);
            }
        }
    }
    
    public void removeAndRecordLog(AutoRemoveDto dto, Long teachingClassId,
        TeachingClass teachingClass, List<String> removeStus)
    {
        if (CollectionUtil.isNotEmpty(removeStus))
        {
            Long calendarId = dto.getCalendarId();
            List<ElcCourseTake> values = new ArrayList<>();
            for (String studentId : removeStus)
            {
                ElcCourseTake t = new ElcCourseTake();
                t.setCalendarId(calendarId);
                t.setStudentId(studentId);
                t.setTeachingClassId(teachingClassId);
                values.add(t);
            }
            courseTakeService.withdraw(values);
        }
    }
    
}
