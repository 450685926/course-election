package com.server.edu.exam.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.server.edu.common.PageCondition;
import com.server.edu.common.locale.I18nUtil;
import com.server.edu.common.rest.PageResult;
import com.server.edu.exam.constants.DeleteStatus;
import com.server.edu.exam.dao.GraduateExamMakeUpAuthDao;
import com.server.edu.exam.entity.GraduateExamMakeUpAuth;
import com.server.edu.exam.service.GraduateExamMakeUpAuthService;
import com.server.edu.exception.ParameterValidateException;
import com.server.edu.session.util.SessionUtils;
import com.server.edu.util.CollectionUtil;
import org.apache.commons.lang3.StringUtils;
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
 * @create: 2019-08-28 11:32
 */
@Service
@Primary
public class GraduateExamMakeUpAuthServiceImpl implements GraduateExamMakeUpAuthService {

    @Autowired
    private GraduateExamMakeUpAuthDao makeUpAuthDao;

    /**
    *@Description: 新增补缓考权限
    *@Param:
    *@return:
    *@Author: bear
    *@date: 2019/8/28 11:40
    */
    @Override
    @Transactional
    public void addMakeUpExamAuth(GraduateExamMakeUpAuth makeUpAuth) {
        //参数校验
        if(makeUpAuth.getCalendarId() == null || makeUpAuth.getApplyType() == null
                || makeUpAuth.getBeginTime() == null || makeUpAuth.getEndTime() == null){
            throw new ParameterValidateException(I18nUtil.getMsg("baseresservice.parameterError"));
        }
        String dptId = SessionUtils.getCurrentSession().getCurrentManageDptId();
        makeUpAuth.setProjId(dptId);
        //重复性检验
        int i = this.checkConflict(makeUpAuth);
        if(i > 0){
            throw new ParameterValidateException("研究生补缓考权限已经存在，不能再次添加");
        }
        //入库
        makeUpAuth.setCreateAt(new Date());
        makeUpAuth.setUpdateAt(new Date());
        makeUpAuth.setDeleteStatus(DeleteStatus.NOT_DELETE);
        makeUpAuthDao.insertSelective(makeUpAuth);

    }

    /**
    *@Description: 编辑补缓考权限
    *@Param: 
    *@return: 
    *@Author: bear
    *@date: 2019/8/28 15:21
    */
    @Override
    @Transactional
    public void editMakeUpExamAuth(GraduateExamMakeUpAuth makeUpAuth) {
        if(makeUpAuth.getId() == null){
            throw new ParameterValidateException(I18nUtil.getMsg("baseresservice.parameterError"));
        }
        //判断数据是否存在
        Example example = new Example(GraduateExamMakeUpAuth.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("id",makeUpAuth.getId());
        criteria.andEqualTo("deleteStatus", DeleteStatus.NOT_DELETE);
        GraduateExamMakeUpAuth examMakeUpAuth = makeUpAuthDao.selectOneByExample(example);
        if (examMakeUpAuth == null){
            throw new ParameterValidateException("该条数据不存在，请刷新页面");
        }else{
            //比较排考类型
            boolean flag = examMakeUpAuth.getApplyType().equals(makeUpAuth.getApplyType());
            if(flag){
                makeUpAuth.setUpdateAt(new Date());
                makeUpAuthDao.updateByPrimaryKeySelective(makeUpAuth);
            }else{
                //校验更改的排考类型数据是否存在
                String dptId = SessionUtils.getCurrentSession().getCurrentManageDptId();
                makeUpAuth.setProjId(dptId);
                int i = this.checkConflict(makeUpAuth);
                if(i > 0 ){
                    throw new ParameterValidateException("研究生补缓考权限已经存在，不能编辑");
                }
                makeUpAuth.setUpdateAt(new Date());
                makeUpAuthDao.updateByPrimaryKeySelective(makeUpAuth);
            }
        }

    }

    /**
    *@Description: 删除补缓考权限
    *@Param:
    *@return: 
    *@Author: bear
    *@date: 2019/8/28 15:22
    */
    @Override
    @Transactional
    public void deleteMakeUpExamAuth(List<Long> ids) {
        if(CollectionUtil.isEmpty(ids)){
            throw new ParameterValidateException(I18nUtil.getMsg("baseresservice.parameterError"));
        }
        Example example = new Example(GraduateExamMakeUpAuth.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("id",ids);
        GraduateExamMakeUpAuth auth = new GraduateExamMakeUpAuth();
        auth.setDeleteStatus(DeleteStatus.HAS_DELETE);
        auth.setUpdateAt(new Date());
        makeUpAuthDao.updateByExampleSelective(auth,example);
    }

    /**
    *@Description: 分页查询
    *@Param:
    *@return: 
    *@Author: bear
    *@date: 2019/8/28 15:33
    */
    @Override
    public PageResult<GraduateExamMakeUpAuth> listMakeUpExamAuth(PageCondition<GraduateExamMakeUpAuth> makeUpAuth) {
        String dptId = SessionUtils.getCurrentSession().getCurrentManageDptId();
        GraduateExamMakeUpAuth upAuth = makeUpAuth.getCondition();
        upAuth.setProjId(dptId);
        Example example = new Example(GraduateExamMakeUpAuth.class);
        Example.Criteria criteria = example.createCriteria();
        this.queryCondition(criteria,upAuth);
        PageHelper.startPage(makeUpAuth.getPageNum_(),makeUpAuth.getPageSize_());
        Page<GraduateExamMakeUpAuth> page = (Page<GraduateExamMakeUpAuth>) makeUpAuthDao.selectByExample(example);
        return new PageResult<>(page);
    }

    //查询条件
    private void queryCondition(Example.Criteria criteria, GraduateExamMakeUpAuth upAuth) {
        if(StringUtils.isNotBlank(upAuth.getProjId())){
            criteria.andEqualTo("projId",upAuth.getProjId());
        }
        if(upAuth.getCalendarId() != null){
            criteria.andEqualTo("calendarId",upAuth.getCalendarId());
        }
        if(upAuth.getApplyType() != null){
            criteria.andEqualTo("applyType",upAuth.getApplyType());
        }
        criteria.andEqualTo("deleteStatus",DeleteStatus.NOT_DELETE);
    }

    private int checkConflict(GraduateExamMakeUpAuth makeUpAuth){
        Example example = new Example(GraduateExamMakeUpAuth.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("calendarId",makeUpAuth.getCalendarId());
        criteria.andEqualTo("applyType",makeUpAuth.getApplyType());
        criteria.andEqualTo("projId",makeUpAuth.getProjId());
        criteria.andEqualTo("deleteStatus", DeleteStatus.NOT_DELETE);
        int i = makeUpAuthDao.selectCountByExample(example);
        return i;
    }


}
