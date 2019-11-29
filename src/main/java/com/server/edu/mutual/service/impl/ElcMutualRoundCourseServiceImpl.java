package com.server.edu.mutual.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.server.edu.common.PageCondition;
import com.server.edu.common.entity.Teacher;
import com.server.edu.common.locale.I18nUtil;
import com.server.edu.common.rest.PageResult;
import com.server.edu.dictionary.utils.TeacherCacheUtil;
import com.server.edu.election.constants.Constants;
import com.server.edu.election.dao.ElecRoundsDao;
import com.server.edu.election.dto.CourseOpenDto;
import com.server.edu.election.entity.ElectionRounds;
import com.server.edu.election.entity.ElectionRoundsCour;
import com.server.edu.election.query.ElecRoundCourseQuery;
import com.server.edu.election.rpc.CultureSerivceInvoker;
import com.server.edu.exception.ParameterValidateException;
import com.server.edu.mutual.dao.ElcMutualRoundCourseDao;
import com.server.edu.mutual.service.ElcMutualRoundCourseService;
import com.server.edu.util.CollectionUtil;

import tk.mybatis.mapper.entity.Example;

@Service
public class ElcMutualRoundCourseServiceImpl implements ElcMutualRoundCourseService{
	@Autowired
	private ElcMutualRoundCourseDao elcMutualRoundCourseDao;
	
	@Autowired
	private ElecRoundsDao elecRoundsDao;
	
	@Override
	public PageResult<CourseOpenDto> listUnAddPage(PageCondition<ElecRoundCourseQuery> condition) {
        PageHelper.startPage(condition.getPageNum_(), condition.getPageSize_());
        ElecRoundCourseQuery query = condition.getCondition();
        Page<CourseOpenDto> listPage;
        if (Constants.PROJ_UNGRADUATE.equals(query.getProjectId())) {
        	listPage = elcMutualRoundCourseDao.listUnAddPage(query);
		}else {
			listPage = elcMutualRoundCourseDao.listUnAddPageGraduate(query);
		}
        if (CollectionUtil.isNotEmpty(listPage)){
        	for (CourseOpenDto courseOpenDto: listPage) {
        		getTeacgerName(courseOpenDto);
        	}
        }
        PageResult<CourseOpenDto> result = new PageResult<>(listPage);
        
        return result;
	}
	
	@Override
	public PageResult<CourseOpenDto> listPage(PageCondition<ElecRoundCourseQuery> condition) {
        PageHelper.startPage(condition.getPageNum_(), condition.getPageSize_());
        ElecRoundCourseQuery query = condition.getCondition();
        Page<CourseOpenDto> listPage;
        if (Constants.PROJ_UNGRADUATE.equals(query.getProjectId())) {
        	listPage = elcMutualRoundCourseDao.listPage(query);
            if (CollectionUtil.isNotEmpty(listPage)){
                for (CourseOpenDto courseOpenDto: listPage) {
                    getTeacgerName(courseOpenDto);
                }
            }
        }else {
        	listPage = elcMutualRoundCourseDao.listPageGraduate(query);
		}
        
        PageResult<CourseOpenDto> result = new PageResult<>(listPage);
        
        return result;
	}
	
    private void getTeacgerName(CourseOpenDto vo) {
        if(StringUtils.isNotBlank(vo.getTeacherCode())) {
            String[] codes = vo.getTeacherCode().split(",");
            List<Teacher> teachers = new ArrayList<Teacher>();
            for (String code : codes) {
                teachers.addAll(TeacherCacheUtil.getTeachers(code));
            }
            if(CollectionUtil.isNotEmpty(teachers)) {
                StringBuilder stringBuilder = new StringBuilder();
                for(Teacher teacher:teachers) {
                    if(teacher!=null) {
                        stringBuilder.append(teacher.getName());
                        stringBuilder.append("(");
                        stringBuilder.append(teacher.getCode());
                        stringBuilder.append(")");
                        stringBuilder.append(",");
                    }
                }
                if(stringBuilder.length()>Constants.ZERO) {
                    vo.setTeacherCode(stringBuilder.deleteCharAt(stringBuilder.length()-1).toString());
                }
            }
        }
    }

	@Override
	public void add(Long roundId, List<Long> teachingClassIds) {
        ElectionRounds rounds = elecRoundsDao.selectByPrimaryKey(roundId);
        if (rounds == null)
        {
            throw new ParameterValidateException(I18nUtil.getMsg("elec.roundCourseExistTip"));
        }
        //过滤已经添加的课程
        List<Long> listAddedCourse =
        		elcMutualRoundCourseDao.listAddedCourse(roundId, teachingClassIds);
        List<ElectionRoundsCour> list = new ArrayList<>();
        for(Long teachingClassId:teachingClassIds) {
        	ElectionRoundsCour electionRoundsCour = new ElectionRoundsCour();
        	if(!listAddedCourse.contains(teachingClassId)) {
		    	electionRoundsCour.setRoundsId(roundId);
		    	electionRoundsCour.setTeachingClassId(teachingClassId);
		    	list.add(electionRoundsCour);
        	}
        }
        elcMutualRoundCourseDao.batchInsert(list);
	}

	@Override
	public void addAll(ElecRoundCourseQuery condition) {
        Page<CourseOpenDto> listPage = null;
        if (StringUtils.equals(condition.getProjectId(), "1")) {
        	listPage = elcMutualRoundCourseDao.listUnAddPage(condition);
		}else {
			listPage = elcMutualRoundCourseDao.listUnAddPageGraduate(condition);
		}
        if(CollectionUtil.isNotEmpty(listPage)) {
            List<ElectionRoundsCour> list = new ArrayList<>();
            for (CourseOpenDto courseOpenDto : listPage)
            {
                ElectionRoundsCour electionRoundsCour = new ElectionRoundsCour();
                electionRoundsCour.setRoundsId(condition.getRoundId());
                electionRoundsCour.setTeachingClassId(courseOpenDto.getTeachingClassId());
                list.add(electionRoundsCour);
            }
            elcMutualRoundCourseDao.batchInsert(list);
        }
		
	}

	@Override
	public void delete(Long roundId, List<Long> teachingClassIds) {
        if (CollectionUtil.isNotEmpty(teachingClassIds))
        {
        	Example example = new Example(ElectionRoundsCour.class);
        	Example.Criteria criteria =example.createCriteria();
        	criteria.andEqualTo("roundsId", roundId);
        	criteria.andIn("teachingClassId", teachingClassIds);
        	elcMutualRoundCourseDao.deleteByExample(example);
        }
	}

	@Override
	public void deleteAll(Long roundId) {
		elcMutualRoundCourseDao.deleteByRoundId(roundId);
	}


    
    
}
