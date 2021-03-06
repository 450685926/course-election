package com.server.edu.election.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.server.edu.common.PageCondition;
import com.server.edu.common.locale.I18nUtil;
import com.server.edu.election.constants.Constants;
import com.server.edu.election.dao.CourseOpenDao;
import com.server.edu.election.dao.ElcAffinityCoursesDao;
import com.server.edu.election.dao.ElcAffinityCoursesStdsDao;
import com.server.edu.election.dao.ElecRoundStuDao;
import com.server.edu.election.dao.StudentDao;
import com.server.edu.election.dto.ElcAffinityCoursesDto;
import com.server.edu.election.dto.StudentDto;
import com.server.edu.election.entity.CourseOpen;
import com.server.edu.election.entity.ElcAffinityCourses;
import com.server.edu.election.entity.ElcAffinityCoursesStds;
import com.server.edu.election.entity.Student;
import com.server.edu.election.service.ElcAffinityCoursesService;
import com.server.edu.election.vo.CourseOpenVo;
import com.server.edu.election.vo.ElcAffinityCoursesVo;
import com.server.edu.exception.ParameterValidateException;
import com.server.edu.session.util.SessionUtils;
import com.server.edu.session.util.entity.Session;
import com.server.edu.util.CollectionUtil;
import com.server.edu.util.async.AsyncExecuter;
import com.server.edu.util.async.AsyncProcessUtil;
import com.server.edu.util.async.AsyncResult;

import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

@Service
public class ElcAffinityCoursesServiceImpl implements ElcAffinityCoursesService
{
    @Autowired
    private ElcAffinityCoursesDao elcAffinityCoursesDao;
    
    @Autowired
    private ElcAffinityCoursesStdsDao elcAffinityCoursesStdsDao;
    
    @Autowired
    private CourseOpenDao courseOpenDao;
    
    @Autowired
    private StudentDao studentDao;
    
    @Autowired
    private ElecRoundStuDao elecRoundStuDao;

    @Autowired
    private SqlSessionFactory factory;
    
    @Autowired
    private RedisTemplate<String, AsyncResult> redisTemplate;
    
    @Override
    public PageInfo<ElcAffinityCoursesVo> list(
        PageCondition<ElcAffinityCoursesDto> condition)
    {
        PageHelper.startPage(condition.getPageNum_(), condition.getPageSize_());
        List<ElcAffinityCoursesVo> list = elcAffinityCoursesDao
            .selectElcAffinityCourses(condition.getCondition());
        PageInfo<ElcAffinityCoursesVo> pageInfo = new PageInfo<>(list);
        return pageInfo;
    }
    
    @Override
    public int delete(List<Long> teachingClassIds)
    {
        Example example = new Example(ElcAffinityCourses.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("teachingClassId", teachingClassIds);
        int result = elcAffinityCoursesDao.deleteByExample(example);
        if (result <= Constants.ZERO)
        {
            throw new ParameterValidateException(
                I18nUtil.getMsg("common.failSuccess",
                    I18nUtil.getMsg("elcAffinity.courses")));
        }
        Example refExample = new Example(ElcAffinityCoursesStds.class);
        Example.Criteria refCriteria = refExample.createCriteria();
        refCriteria.andIn("courseId", teachingClassIds);
        List<ElcAffinityCoursesStds> list =
            elcAffinityCoursesStdsDao.selectByExample(refExample);
        if (CollectionUtil.isNotEmpty(list))
        {
            result = elcAffinityCoursesStdsDao.deleteByExample(refExample);
            if (result <= Constants.ZERO)
            {
                throw new ParameterValidateException(
                    I18nUtil.getMsg("common.failSuccess",
                        I18nUtil.getMsg("elcAffinity.courses")));
            }
        }
        return result;
    }
    
    @Override
    public PageInfo<CourseOpenVo> courseList(PageCondition<CourseOpen> condition)
    {
        PageHelper.startPage(condition.getPageNum_(), condition.getPageSize_());
        CourseOpen courseOpen = condition.getCondition();
        List<CourseOpenVo> list = courseOpenDao.selectCourseList(courseOpen);
        PageInfo<CourseOpenVo> pageInfo = new PageInfo<>(list);
        return pageInfo;
    }
    
    @Override
    public int addCourse(List<Long> ids, Long calendarId)
    {
        List<ElcAffinityCourses> list = new ArrayList<>();
        ids.forEach(temp -> {
            ElcAffinityCourses elcAffinityCourses = new ElcAffinityCourses();
            elcAffinityCourses.setTeachingClassId(temp);
            elcAffinityCourses.setCalendarId(calendarId);
            list.add(elcAffinityCourses);
        });
        int result = elcAffinityCoursesDao.insertList(list);
        if (result <= Constants.ZERO)
        {
            throw new ParameterValidateException(
                I18nUtil.getMsg("common.saveError",
                    I18nUtil.getMsg("elcAffinity.courses")));
        }
        return result;
    }
    
    @Override
    public PageInfo<Student> studentList(PageCondition<StudentDto> condition)
    {
        PageHelper.startPage(condition.getPageNum_(), condition.getPageSize_());
        List<Student> list =
            studentDao.selectElcStudents(condition.getCondition());
        PageInfo<Student> pageInfo = new PageInfo<>(list);
        return pageInfo;
    }
    
    @Override
    public PageInfo<Student> getStudents(PageCondition<StudentDto> condition)
    {
        PageHelper.startPage(condition.getPageNum_(), condition.getPageSize_());
        List<Student> list =
            studentDao.selectUnElcStudents(condition.getCondition());
        PageInfo<Student> pageInfo = new PageInfo<>(list);
        return pageInfo;
    }
    
    @Override
    public int addStudent(ElcAffinityCoursesVo elcAffinityCoursesVo)
    {
    	 List<ElcAffinityCoursesStds> stuList = new ArrayList<>();
    	Session session = SessionUtils.getCurrentSession();
        //新增学生不在已经添加的名单
        StudentDto student =
                new StudentDto();
        student.setTeachingClassId(elcAffinityCoursesVo.getTeachingClassId());
        student.setStudentIds(elcAffinityCoursesVo.getStudentIds());
        List<Student> selectUnElcStudents = studentDao.selectUnElcStudents(student);
        List<String> stuIds = selectUnElcStudents.stream().map(Student::getStudentCode).collect(Collectors.toList());
        if (CollectionUtil.isEmpty(stuIds)) {
       	 throw new ParameterValidateException(
                    I18nUtil.getMsg("输入学号无效或已经存在",""));
		}
        
        //查找用户添加的学生名单,拿到可以添加的学生名单
        List<String> listExistStu = elecRoundStuDao.listExistStu(stuIds, session.getCurrentManageDptId());
//      List<String> listExistStu = elecRoundStuDao.listExistStu(stuIds, "1");
        if (CollectionUtil.isEmpty(listExistStu)) {
        	 throw new ParameterValidateException(
                     I18nUtil.getMsg("common.saveError",
                         I18nUtil.getMsg("elcAffinity.courses")));
		}
        //查找学生
        listExistStu.forEach(temp -> {
            ElcAffinityCoursesStds elcAffinityCoursesStds =
                new ElcAffinityCoursesStds();
            elcAffinityCoursesStds
                .setTeachingClassId(elcAffinityCoursesVo.getTeachingClassId());
            elcAffinityCoursesStds.setStudentId(temp);
            stuList.add(elcAffinityCoursesStds);
        });
        int result = elcAffinityCoursesStdsDao.batchInsert(stuList);
        if (result <= Constants.ZERO)
        {
            throw new ParameterValidateException(
                I18nUtil.getMsg("common.saveError",
                    I18nUtil.getMsg("elcAffinity.courses")));
        }
        return result;
    }
    
    @Override
    public int batchAddStudent(StudentDto studentDto)
    {
        List<Student> list = studentDao.selectUnElcStudents(studentDto);
        int result = 0;
        if(CollectionUtil.isNotEmpty(list)) {
            List<ElcAffinityCoursesStds> stuList = new ArrayList<>();
            list.forEach(temp -> {
                ElcAffinityCoursesStds elcAffinityCoursesStds =
                    new ElcAffinityCoursesStds();
                elcAffinityCoursesStds.setTeachingClassId(studentDto.getTeachingClassId());
                elcAffinityCoursesStds.setStudentId(temp.getStudentCode());
                stuList.add(elcAffinityCoursesStds);
            });
            result = elcAffinityCoursesStdsDao.batchInsert(stuList);
            if (result <= Constants.ZERO)
            {
                throw new ParameterValidateException(
                    I18nUtil.getMsg("common.saveError",
                        I18nUtil.getMsg("elcAffinity.courses")));
            }
        }
        return result;
    }
    

    @Transactional
	@Override
	public AsyncResult asyncBatchAddStudent(StudentDto studentDto) {
		AsyncResult result = AsyncProcessUtil.submitTask("asyncBatchAddStudent", new AsyncExecuter() {
			
			@Override
			public void execute() {
				AsyncResult result = this.getResult();
				List<Student> list = studentDao.selectUnElcStudents(studentDto);
		        int resultCount = 0;
		        result.setTotal(list.size());
		        if(CollectionUtil.isNotEmpty(list)) {
		            List<ElcAffinityCoursesStds> stuList = new ArrayList<>();
		            list.forEach(temp -> {
		                ElcAffinityCoursesStds elcAffinityCoursesStds =
		                    new ElcAffinityCoursesStds();
		                elcAffinityCoursesStds.setTeachingClassId(studentDto.getTeachingClassId());
		                elcAffinityCoursesStds.setStudentId(temp.getStudentCode());
		                stuList.add(elcAffinityCoursesStds);
		            });

                    SqlSession session = factory.openSession(ExecutorType.BATCH,false);
                    ElcAffinityCoursesStdsDao mapper = session.getMapper(ElcAffinityCoursesStdsDao.class);
                    for (int i = 0; i <stuList.size() ; i++) {
                        mapper.insert(stuList.get(i));
                        if(i%40==0){//每40条提交一次防止内存溢出
                            result.setDoneCount(i+1);
                            String counts = String.format("已经添加成功的数据", i+1);
                            result.setMsg(counts);
                            redisTemplate.opsForValue().getAndSet("commonAsyncProcessKey-"+result.getKey(), result);
                            session.commit();
                            session.clearCache();
                        }
                    }
                    session.commit();
                    session.clearCache();






		            /*resultCount = elcAffinityCoursesStdsDao.batchInsert(stuList);
		            result.setDoneCount(resultCount);
		            redisTemplate.opsForValue().getAndSet("commonAsyncProcessKey-"+result.getKey(), result);
		            if (resultCount <= Constants.ZERO)
		            {
		            	result.setMsg(I18nUtil.getMsg("common.saveError",
		                        I18nUtil.getMsg("elcAffinity.courses")));
		                throw new ParameterValidateException(
		                    I18nUtil.getMsg("common.saveError",
		                        I18nUtil.getMsg("elcAffinity.courses")));

		            }*/
		        }
			}}
		);
		return result;
	}
	
    @Override
    public int deleteStudent(ElcAffinityCoursesVo vo)
    {
        Example example = new Example(ElcAffinityCoursesStds.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("teachingClassId", vo.getTeachingClassId());
        criteria.andIn("studentId", vo.getStudentIds());
        int result = elcAffinityCoursesStdsDao.deleteByExample(example);
        if (result <= Constants.ZERO)
        {
            throw new ParameterValidateException(
                I18nUtil.getMsg("common.failSuccess",
                    I18nUtil.getMsg("elcAffinity.courses")));
        }
        return result;
    }
    
    @Override
    public int batchDeleteStudent(Long teachingClassId)
    {
        Example example = new Example(ElcAffinityCoursesStds.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("teachingClassId", teachingClassId);
        int result = elcAffinityCoursesStdsDao.deleteByExample(example);
        if (result <= Constants.ZERO)
        {
            throw new ParameterValidateException(
                I18nUtil.getMsg("common.failSuccess",
                    I18nUtil.getMsg("elcAffinity.courses")));
        }
        return result;
    }
    
}
