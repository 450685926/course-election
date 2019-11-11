package com.server.edu.exam.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.server.edu.common.PageCondition;
import com.server.edu.common.locale.I18nUtil;
import com.server.edu.common.rest.PageResult;
import com.server.edu.exam.constants.DeleteStatus;
import com.server.edu.exam.dao.GraduateExamAuthDao;
import com.server.edu.exam.entity.GraduateExamAuth;
import com.server.edu.exam.service.GraduateExamAuthService;
import com.server.edu.exam.vo.GraduateExamAuthVo;
import com.server.edu.exception.ParameterValidateException;
import com.server.edu.session.util.SessionUtils;
import com.server.edu.util.CollectionUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @description:
 * @author: bear
 * @create: 2019-08-26 15:18
 */

@Service
@Primary
public class GraduateExamAuthServiceImpl implements GraduateExamAuthService {

    @Autowired
    private GraduateExamAuthDao authDao;

    @Override
    @Transactional
    public void addGraduateAuth(GraduateExamAuthVo authVo) {
        //判断条件是否满足
        if(authVo.getCalendarId() == null || authVo.getBeginTime()==null || authVo.getEndTime() == null
                || CollectionUtil.isEmpty(authVo.getTrainingLevels()) || CollectionUtil.isEmpty(authVo.getCourseNatures())
                || CollectionUtil.isEmpty(authVo.getOpenColleges()) || authVo.getExamType() == null){
            throw new ParameterValidateException(I18nUtil.getMsg("baseresservice.parameterError"));
        }
        //直接删除该数据
        String dptId = SessionUtils.getCurrentSession().getCurrentManageDptId();
        Example example = new Example(GraduateExamAuth.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("deleteStatus", DeleteStatus.NOT_DELETE);
        criteria.andEqualTo("projId",dptId);
        authDao.deleteByExample(example);
        //插入
        insert(authVo,dptId);

    }

    @Override
    @Transactional
    public void editGraduateAuth(GraduateExamAuthVo examAuthVo) {
        if(examAuthVo.getId() == null){
            throw new ParameterValidateException(I18nUtil.getMsg("baseresservice.parameterError"));
        }
        //判断数据是否存在
        Example example = new Example(GraduateExamAuth.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("id",examAuthVo.getId());
        criteria.andEqualTo("deleteStatus", DeleteStatus.NOT_DELETE);
        int i = authDao.selectCountByExample(example);
        if (i == 0){
            throw new ParameterValidateException(I18nUtil.getMsg("common.notExist",I18nUtil.getMsg("graduateExam.auth")));
        }
        //直接删除该数据
        authDao.deleteByPrimaryKey(examAuthVo.getId());
        String dptId = SessionUtils.getCurrentSession().getCurrentManageDptId();
        insert(examAuthVo,dptId);

    }

    @Override
    @Transactional
    public void deleteGraduateAuth(List<Long> ids) {
        if(CollectionUtil.isEmpty(ids)){
            throw new ParameterValidateException(I18nUtil.getMsg("baseresservice.parameterError"));
        }
        Example example = new Example(GraduateExamAuth.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("id",ids);
        GraduateExamAuth auth = new GraduateExamAuth();
        auth.setDeleteStatus(DeleteStatus.HAS_DELETE);
        auth.setUpdateAt(new Date());
        authDao.updateByExampleSelective(auth,example);
    }

    @Override
    public PageResult<GraduateExamAuthVo> listGraduateExamAuth(PageCondition<GraduateExamAuthVo> examAuthVo) {
        GraduateExamAuthVo authVo = examAuthVo.getCondition();
        String dptId = SessionUtils.getCurrentSession().getCurrentManageDptId();
        authVo.setProjId(dptId);
        PageHelper.startPage(examAuthVo.getPageNum_(),examAuthVo.getPageSize_());
        Page<GraduateExamAuthVo> page = authDao.listGraduateExamAuth(authVo);
        if(CollectionUtil.isNotEmpty(page)){
            for (GraduateExamAuthVo graduateExamAuthVo : page.getResult()) {
                String trainingLevel = graduateExamAuthVo.getTrainingLevel();
                String courseNature = graduateExamAuthVo.getCourseNature();
                String openCollege = graduateExamAuthVo.getOpenCollege();
                String trainingCategory = graduateExamAuthVo.getTrainingCategory();
                String degreeType = graduateExamAuthVo.getDegreeType();
                String formLearning = graduateExamAuthVo.getFormLearning();
                graduateExamAuthVo.setTrainingLevels(Arrays.asList(trainingLevel.split(",")));
                graduateExamAuthVo.setCourseNatures(Arrays.asList(courseNature.split(",")));
                graduateExamAuthVo.setOpenColleges(Arrays.asList(openCollege.split(",")));
                if(StringUtils.isNotBlank(trainingCategory)){
                    graduateExamAuthVo.setTrainingCategorys(Arrays.asList(trainingCategory.split(",")));
                }else{
                    graduateExamAuthVo.setTrainingCategorys(new ArrayList<String>());
                }
                if(StringUtils.isNotBlank(degreeType)){
                    graduateExamAuthVo.setDegreeTypes(Arrays.asList(degreeType.split(",")));
                }else{
                    graduateExamAuthVo.setDegreeTypes(new ArrayList<String>());
                }
                if(StringUtils.isNotBlank(formLearning)){
                    graduateExamAuthVo.setFormLearnings(Arrays.asList(formLearning.split(",")));
                }else{
                    graduateExamAuthVo.setFormLearnings(new ArrayList<String>());
                }
            }
        }
        return new PageResult<>(page);
    }

    public void insert(GraduateExamAuthVo authVo,String deptid){
        //插入
        List<String> trainingLevels = authVo.getTrainingLevels();
        List<String> courseNatures = authVo.getCourseNatures();
        List<String> openColleges = authVo.getOpenColleges();
        List<String> trainingCategorys = authVo.getTrainingCategorys();
        List<String> degreeTypes = authVo.getDegreeTypes();
        List<String> formLearnings = authVo.getFormLearnings();
        String trainingLevel = StringUtils.join(trainingLevels, ",");
        String courseNature = StringUtils.join(courseNatures, ",");
        String openCollege = StringUtils.join(openColleges, ",");
        String trainingCategory = StringUtils.join(trainingCategorys, ",");
        String degreeType = StringUtils.join(degreeTypes, ",");
        String formLearning = StringUtils.join(formLearnings, ",");
        authVo.setCreateAt(new Date());
        authVo.setUpdateAt(new Date());
        GraduateExamAuth auth = new GraduateExamAuth();
        BeanUtils.copyProperties(authVo,auth);
        auth.setTrainingLevel(trainingLevel);
        auth.setCourseNature(courseNature);
        auth.setOpenCollege(openCollege);
        auth.setDegreeType(degreeType);
        auth.setTrainingCategory(trainingCategory);
        auth.setFormLearning(formLearning);
        auth.setDeleteStatus(DeleteStatus.NOT_DELETE);
        auth.setProjId(deptid);
        authDao.insertSelective(auth);
    }

}
