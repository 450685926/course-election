package com.server.edu.exam.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.server.edu.common.PageCondition;
import com.server.edu.common.locale.I18nUtil;
import com.server.edu.common.rest.PageResult;
import com.server.edu.exam.constants.DeleteStatus;
import com.server.edu.exam.dao.GraduateExamMonitorTeacherDao;
import com.server.edu.exam.entity.GraduateExamMonitorTeacher;
import com.server.edu.exam.service.GraduateExamMonitorTeacherService;
import com.server.edu.exception.ParameterValidateException;
import com.server.edu.session.util.SessionUtils;
import com.server.edu.util.CollectionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;

/**
 * @description:
 * @author: bear
 * @create: 2019-08-27 10:43
 */
@Service
@Primary
public class GraduateExamMonitorTeacherServiceImpl implements GraduateExamMonitorTeacherService {

    @Autowired
    private GraduateExamMonitorTeacherDao teacherDao;


    @Override
    @Transactional
    public void addGraduateMonitorTeacher(GraduateExamMonitorTeacher monitorTeacher) {
        if(monitorTeacher.getStudentMinNumber() == null || monitorTeacher.getStudentMaxNumber() == null
                || monitorTeacher.getTeacherNumber() == null){
            throw new ParameterValidateException(I18nUtil.getMsg("baseresservice.parameterError"));
        }
        //校验人数区间冲突
        String dptId = SessionUtils.getCurrentSession().getCurrentManageDptId();
        monitorTeacher.setProjId(dptId);
        this.checkNumConflict(monitorTeacher);
        monitorTeacher.setCreateAt(new Date());
        monitorTeacher.setUpdateAt(new Date());
        monitorTeacher.setDeleteStatus(DeleteStatus.NOT_DELETE);
        teacherDao.insertSelective(monitorTeacher);
    }

    @Override
    @Transactional
    public void editGraduateMonitorTeacher(GraduateExamMonitorTeacher monitorTeacher) {
        if(monitorTeacher.getId() == null || monitorTeacher.getStudentMinNumber() == null
                || monitorTeacher.getStudentMaxNumber() == null
                || monitorTeacher.getTeacherNumber() == null){
            throw new ParameterValidateException(I18nUtil.getMsg("baseresservice.parameterError"));
        }
        Example example = new Example(GraduateExamMonitorTeacher.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("id",monitorTeacher.getId());
        criteria.andEqualTo("deleteStatus", DeleteStatus.NOT_DELETE);
        int i = teacherDao.selectCountByExample(example);
        if(i == 0){
            throw new ParameterValidateException("该条数据不存在，请刷新页面");
        }
        this.checkNumConflict(monitorTeacher);
        monitorTeacher.setUpdateAt(new Date());
        teacherDao.updateByPrimaryKeySelective(monitorTeacher);
    }

    @Override
    @Transactional
    public void deleteGraduateMonitorTeacher(List<Long> ids) {
        if(CollectionUtil.isEmpty(ids)){
            throw new ParameterValidateException(I18nUtil.getMsg("baseresservice.parameterError"));
        }
        Example example = new Example(GraduateExamMonitorTeacher.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("id",ids);
        GraduateExamMonitorTeacher monitorTeacher = new GraduateExamMonitorTeacher();
        monitorTeacher.setUpdateAt(new Date());
        monitorTeacher.setDeleteStatus(DeleteStatus.HAS_DELETE);
        teacherDao.updateByExampleSelective(monitorTeacher,example);
    }

    @Override
    public PageResult<GraduateExamMonitorTeacher> listGraduateMonitorTeacher(PageCondition<GraduateExamMonitorTeacher> monitorTeacher) {
        String dptId = SessionUtils.getCurrentSession().getCurrentManageDptId();
        Example example = new Example(GraduateExamMonitorTeacher.class);
        Example.Criteria criteria = example.createCriteria();
        example.setOrderByClause("STUDENT_MIN_NUMBER_ ASC");
        criteria.andEqualTo("projId","2");
        criteria.andEqualTo("deleteStatus",DeleteStatus.NOT_DELETE);
        PageHelper.startPage(monitorTeacher.getPageNum_(),monitorTeacher.getPageSize_());
        Page<GraduateExamMonitorTeacher> page = (Page<GraduateExamMonitorTeacher>) teacherDao.selectByExample(example);
        return new PageResult<>(page);
    }

    private void checkNumConflict(GraduateExamMonitorTeacher monitorTeacher){
       List<GraduateExamMonitorTeacher> teachers = teacherDao.checkNum(monitorTeacher);
       if(CollectionUtil.isNotEmpty(teachers)){
           throw new ParameterValidateException("人数区间存在交叉，请检查");
       }
    }
}
