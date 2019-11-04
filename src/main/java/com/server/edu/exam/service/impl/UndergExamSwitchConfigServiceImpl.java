package com.server.edu.exam.service.impl;

import com.github.pagehelper.PageHelper;
import com.server.edu.common.rest.RestResult;
import com.server.edu.exam.dao.UndergExamSwitchConfigDao;
import com.server.edu.exam.dao.UndergExamSwitchDepartRelationDao;
import com.server.edu.exam.entity.UndergExamSwitchConfig;
import com.server.edu.exam.entity.UndergExamSwitchDepartRelation;
import com.server.edu.exam.enums.UndergExamCommonEnum;
import com.server.edu.exam.service.UndergExamSwitchConfigService;
import com.server.edu.exam.util.UndergExamUtil;
import com.server.edu.exam.vo.UndergExamSwitchConfigVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 功能描述：本科生排考开关 service 实现类
 *
 * @ClassName UndergExamSwitchConfigServiceImpl
 * @Author zhaoerhu
 * @Date 2019/8/19 16:06
 */
@Service
public class UndergExamSwitchConfigServiceImpl implements UndergExamSwitchConfigService {
    @Autowired
    private UndergExamSwitchConfigDao undergExamSwitchConfigDao;
    @Autowired
    private UndergExamSwitchDepartRelationDao undergExamSwitchDepartRelationDao;


    /**
     * 功能描述: 增加本科生排考开关设置
     *
     * @params: [undergExamSwitchConfigVo]
     * @return: com.server.edu.common.rest.RestResult
     * @author: zhaoerhu
     * @date: 2019/8/19 16:07
     */
    @Transactional
    @Override
    public RestResult addOrUpdateUndergExamSwitchConfig(UndergExamSwitchConfigVo undergExamSwitchConfigVo) throws Exception {
        if (null == undergExamSwitchConfigVo
                || null == undergExamSwitchConfigVo.getBeginTime()
                || null == undergExamSwitchConfigVo.getEndTime()
                || null == undergExamSwitchConfigVo.getTermId()
                || null == undergExamSwitchConfigVo.getExamType()
                || null == undergExamSwitchConfigVo.getRelations()
                || undergExamSwitchConfigVo.getRelations().size() == 0) {
            return RestResult.fail("common.parameterError");
        }

        //校验type
        if (!UndergExamCommonEnum.ConfigTypeEnum.checkCodeExist(undergExamSwitchConfigVo.getType())) {
            return RestResult.fail("type 取值错误，请确认");
        }

        //校验examType
        if (!UndergExamCommonEnum.ExamTypeEnum.checkCodeExist(undergExamSwitchConfigVo.getExamType())) {
            return RestResult.fail("examType 取值错误，请确认");
        }

        Long configId = undergExamSwitchConfigVo.getId();
        List<UndergExamSwitchDepartRelation> relations = undergExamSwitchConfigVo.getRelations();
        if (null == configId) {
            //新增
            packageConfigParam(undergExamSwitchConfigVo, Boolean.TRUE);
            undergExamSwitchConfigDao.insertSelective(undergExamSwitchConfigVo);
            if (relations.size() > 0) {
                List<UndergExamSwitchDepartRelation> undergExamSwitchDepartRelations = new ArrayList<>();
                relations.forEach((e) -> {
                    UndergExamSwitchDepartRelation relation = new UndergExamSwitchDepartRelation();
                    packageRelationParam(relation, undergExamSwitchConfigVo, Boolean.TRUE, Boolean.FALSE);
                    undergExamSwitchDepartRelations.add(relation);
                });
                undergExamSwitchDepartRelationDao.insertList(undergExamSwitchDepartRelations);
            }
        } else {
            //更新
            packageConfigParam(undergExamSwitchConfigVo, Boolean.FALSE);
            undergExamSwitchConfigDao.updateByPrimaryKeySelective(undergExamSwitchConfigVo);

            if (relations.size() > 0) {
                //查询当前开关关联的学院列表
                Example example = new Example(UndergExamSwitchDepartRelation.class);
                Example.Criteria criteria = example.createCriteria();
                criteria.andEqualTo("configId", configId);
                criteria.andEqualTo("deleteStatus", UndergExamCommonEnum.DeletedEnum.NOT_DELETED.getCode());
                criteria.andEqualTo("type", undergExamSwitchConfigVo.getType());
                List<UndergExamSwitchDepartRelation> undergExamSwitchDepartRelations = undergExamSwitchDepartRelationDao.selectByExample(example);
                //收集应该删除的关系id
                List<UndergExamSwitchDepartRelation> delIds = undergExamSwitchDepartRelations.stream().filter(e -> !relations.contains(e)).collect(Collectors.toList());
                //收集应该修改的关系id
                List<UndergExamSwitchDepartRelation> updIds = relations.stream().filter(e -> e.getId() != null).collect(Collectors.toList());
                //收集应该新增的关系id
                List<UndergExamSwitchDepartRelation> insIds = relations.stream().filter(e -> e.getId() == null).collect(Collectors.toList());

                //删除关系id列表
                if (null != delIds && delIds.size() > 0) {
                    delIds.forEach(e -> {
                        packageRelationParam(e, undergExamSwitchConfigVo, Boolean.FALSE, Boolean.TRUE);
                        undergExamSwitchDepartRelationDao.updateByPrimaryKeySelective(e);
                    });
                }

                //修改关系id列表
                if (null != updIds && updIds.size() > 0) {
                    updIds.forEach(e -> {
                        packageRelationParam(e, undergExamSwitchConfigVo, Boolean.FALSE, Boolean.FALSE);
                        undergExamSwitchDepartRelationDao.updateByPrimaryKeySelective(e);
                    });
                }

                //新增关系id列表
                if (null != insIds && insIds.size() > 0) {
                    insIds.forEach(e -> {
                        packageRelationParam(e, undergExamSwitchConfigVo, Boolean.FALSE, Boolean.FALSE);
                    });
                    undergExamSwitchDepartRelationDao.insertList(insIds);
                }
            }
        }

        return RestResult.successData(undergExamSwitchConfigVo);
    }

    /**
     * 功能描述:本科生排考开关分页查询
     *
     * @params: [examType]
     * @return: com.server.edu.common.rest.RestResult
     * @author: zhaoerhu
     * @date: 2019/8/20 19:44
     */
    @Override
    public RestResult queryUndergExamSwitchConfigList(UndergExamSwitchConfigVo undergExamSwitchConfigVo) throws Exception {
        if (null == undergExamSwitchConfigVo
                || undergExamSwitchConfigVo.getPageNum_() == 0
                || undergExamSwitchConfigVo.getPageSize_() == 0) {
            return RestResult.fail("common.parameterError");
        }

        //校验type
        Integer type = undergExamSwitchConfigVo.getType();
        if (!UndergExamCommonEnum.ConfigTypeEnum.checkCodeExist(type)) {
            return RestResult.fail("type 取值错误，请确认");
        }

        //校验examType
        Integer examType = undergExamSwitchConfigVo.getExamType();
        Boolean examTypeIsNull = Boolean.TRUE;
        if (null != examType) {
            examTypeIsNull = Boolean.FALSE;
            if (!UndergExamCommonEnum.ExamTypeEnum.checkCodeExist(examType)) {
                return RestResult.fail("examType 取值错误，请确认");
            }
        }

        PageHelper.startPage(undergExamSwitchConfigVo.getPageNum_(), undergExamSwitchConfigVo.getPageNum_());
        Example example = new Example(UndergExamSwitchConfig.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("deleteStatus", UndergExamCommonEnum.DeletedEnum.NOT_DELETED.getCode());
        criteria.andEqualTo("type", type);
        if (!examTypeIsNull) {
            criteria.andEqualTo("examType", examType);
        }
        example.orderBy("sortTime").desc();
        List<UndergExamSwitchConfig> undergExamSwitchConfigs = undergExamSwitchConfigDao.selectByExample(example);
        if (null != undergExamSwitchConfigs && undergExamSwitchConfigs.size() > 0) {
            List<UndergExamSwitchConfigVo> undergExamSwitchConfigVos = new ArrayList<>();

            undergExamSwitchConfigs.forEach(e -> {
                Example example1 = new Example(UndergExamSwitchDepartRelation.class);
                Example.Criteria criteria1 = example.createCriteria();
                criteria1.andEqualTo("configId", e.getId());
                criteria1.andEqualTo("deleteStatus", UndergExamCommonEnum.DeletedEnum.NOT_DELETED.getCode());
                example1.orderBy("sortTime").desc();
                List<UndergExamSwitchDepartRelation> relations = undergExamSwitchDepartRelationDao.selectByExample(example1);
                UndergExamSwitchConfigVo vo = new UndergExamSwitchConfigVo();
                BeanUtils.copyProperties(e, vo);
                vo.setRelations(relations);
                undergExamSwitchConfigVos.add(vo);
            });
            return RestResult.successData(undergExamSwitchConfigVos);
        } else {
            return RestResult.successData(Collections.emptyList());
        }
    }

    /**
     * 功能描述: 本科生排考开关删除（批量）
     *
     * @params: [undergExamSwitchConfigVo]
     * @return: com.server.edu.common.rest.RestResult
     * @author: zhaoerhu
     * @date: 2019/8/20 11:21
     */
    @Override
    public RestResult deleteSwitchConfigBatch(UndergExamSwitchConfigVo undergExamSwitchConfigVo) throws Exception {
        if (null == undergExamSwitchConfigVo || null == undergExamSwitchConfigVo.getDelIdSet() || undergExamSwitchConfigVo.getDelIdSet().size() == 0) {
            return RestResult.fail("请选择数据进行删除");
        }
        Set<Long> delIdSet = undergExamSwitchConfigVo.getDelIdSet();

        //删除主表数据
        UndergExamSwitchConfig config = new UndergExamSwitchConfig();
        config.setDeleteStatus(UndergExamCommonEnum.DeletedEnum.DELETED.getCode());
        Example exampleConfig = new Example(UndergExamSwitchConfig.class);
        Example.Criteria criteriaConfig = exampleConfig.createCriteria();
        criteriaConfig.andIn("id", delIdSet);
        undergExamSwitchConfigDao.updateByExampleSelective(config, exampleConfig);

        //删除关联表数据
        UndergExamSwitchDepartRelation relation = new UndergExamSwitchDepartRelation();
        relation.setDeleteStatus(UndergExamCommonEnum.DeletedEnum.DELETED.getCode());
        Example exampleRelation = new Example(UndergExamSwitchDepartRelation.class);
        Example.Criteria criteriaRelation = exampleRelation.createCriteria();
        Set<String> configIdSet = delIdSet.stream().map(e -> Long.toString(e)).collect(Collectors.toSet());
        criteriaRelation.andIn("configId", configIdSet);
        undergExamSwitchDepartRelationDao.updateByExampleSelective(relation, exampleRelation);
        return RestResult.success();
    }

    /**
     * 功能描述: 封装主表参数
     *
     * @params: [undergExamSwitchConfig]
     * @return: void
     * @author: zhaoerhu
     * @date: 2019/8/19 16:20
     */
    private void packageConfigParam(UndergExamSwitchConfigVo undergExamSwitchConfigVo, Boolean isAdd) {
        undergExamSwitchConfigVo.setDeleteStatus(UndergExamCommonEnum.DeletedEnum.NOT_DELETED.getCode());
        if (isAdd) {
            UndergExamUtil.builderByParam(undergExamSwitchConfigVo).setCreateParam().setOtherParam();
        } else {
            UndergExamUtil.builderByParam(undergExamSwitchConfigVo).setUpdateParam().setOtherParam();
        }
    }

    /**
     * 功能描述: 封装关联表参数
     *
     * @params: [undergExamSwitchDepartRelation, undergExamSwitchConfig, isAdd]
     * @return: void
     * @author: zhaoerhu
     * @date: 2019/8/19 16:09
     */
    private void packageRelationParam(UndergExamSwitchDepartRelation undergExamSwitchDepartRelation,
                                      UndergExamSwitchConfig undergExamSwitchConfig,
                                      Boolean isAdd,
                                      Boolean idDel) {
        undergExamSwitchDepartRelation.setConfigId(Long.toString(undergExamSwitchConfig.getId()));
        if (isAdd) {
            UndergExamUtil.builderByParam(undergExamSwitchDepartRelation).setCreateParam().setOtherParam();
        } else {
            UndergExamUtil.builderByParam(undergExamSwitchDepartRelation).setUpdateParam().setOtherParam();
        }
        if (idDel) {
            undergExamSwitchDepartRelation.setDeleteStatus(UndergExamCommonEnum.DeletedEnum.DELETED.getCode());
        } else {
            undergExamSwitchDepartRelation.setDeleteStatus(UndergExamCommonEnum.DeletedEnum.NOT_DELETED.getCode());
        }
    }
}
