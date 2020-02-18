package com.server.edu.mutual.service.impl;


import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.server.edu.common.PageCondition;
import com.server.edu.common.locale.I18nUtil;
import com.server.edu.common.rest.RestResult;
import com.server.edu.election.constants.Constants;
import com.server.edu.election.util.ExcelStoreConfig;
import com.server.edu.mutual.dao.ElcMutualListDao;
import com.server.edu.mutual.dto.ElcMutualCrossStuDto;
import com.server.edu.mutual.dto.ElcMutualListDto;
import com.server.edu.mutual.service.ElcMutualListService;
import com.server.edu.mutual.vo.ElcMutualListVo;
import com.server.edu.session.util.SessionUtils;
import com.server.edu.session.util.entity.Session;
import com.server.edu.welcomeservice.util.ExcelEntityExport;

import org.springframework.stereotype.Service;

@Service
public class ElcMutualListServiceImpl implements ElcMutualListService {
    @Autowired
    private ElcMutualListDao elcMutualListDao;

    @Autowired
    private ExcelStoreConfig excelStoreConfig;
    
    /**
     * 文件存储路径
     */
    @Value("${task.cache.directory}")
    private String cacheDirectory;
    
    @Override
    public PageInfo<ElcMutualListVo> getMutualStuList(PageCondition<ElcMutualListDto> condition) {
        PageHelper.startPage(condition.getPageNum_(), condition.getPageSize_());

        Session session = SessionUtils.getCurrentSession();
        String faculty = session.getFaculty();
        String projectId = session.getCurrentManageDptId();

        ElcMutualListDto dto = condition.getCondition();

        if (Constants.BK_CROSS.equals(dto.getMode())) {
            dto.setInType(Constants.FIRST);
        } else {
            dto.setByType(Constants.FIRST);
        }

        // 教务员查看本学院申请了本研互选选课的学生和申请了本学院开设课程的学生
        boolean isAcdemicDean = StringUtils.equals(session.getCurrentRole(), String.valueOf(Constants.ONE)) && !session.isAdmin() && session.isAcdemicDean();
        if (isAcdemicDean) {
            if (dto.getProjectIds().contains(projectId)) {
                dto.setCollege(faculty);  // 学生行政学院
            } else {
                dto.setOpenCollege(faculty);  // 开课学院
            }
        }

        List<ElcMutualListVo> list = elcMutualListDao.getMutualStuList(dto);
        PageInfo<ElcMutualListVo> pageInfo = new PageInfo<ElcMutualListVo>(list);
        return pageInfo;
    }

    @Override
    public PageInfo<ElcMutualListVo> getMutualCourseList(PageCondition<ElcMutualListDto> condition) {
        PageHelper.startPage(condition.getPageNum_(), condition.getPageSize_());
        ElcMutualListDto dto = condition.getCondition();
        Session session = SessionUtils.getCurrentSession();
        String faculty = session.getFaculty();

        if (Constants.BK_CROSS.equals(dto.getMode())) {
            dto.setInType(Constants.FIRST);
        } else {
            dto.setByType(Constants.FIRST);
        }

        boolean isAcdemicDean = StringUtils.equals(session.getCurrentRole(), String.valueOf(Constants.ONE)) && !session.isAdmin() && session.isAcdemicDean();
        if (isAcdemicDean) {
            dto.setOpenCollege(faculty);
        }

        List<ElcMutualListVo> list = elcMutualListDao.getMutualCourseList(dto);
        PageInfo<ElcMutualListVo> pageInfo = new PageInfo<ElcMutualListVo>(list);
        return pageInfo;
    }
    
    
    /**
     * @导出本研互选名单列表
     * @param dto
     * @return
     */
    @Override
    public RestResult<String> exportelcMutualStuList(PageCondition<ElcMutualListDto> condition)
    {
        RestResult<String> restResult = RestResult.error(I18nUtil.getMsg("export.exception"));
        Session session = SessionUtils.getCurrentSession();
        String faculty = session.getFaculty();
        String projectId = session.getCurrentManageDptId();

        ElcMutualListDto dto = condition.getCondition();

        if (Constants.BK_CROSS.equals(dto.getMode())) {
            dto.setInType(Constants.FIRST);
        } else {
            dto.setByType(Constants.FIRST);
        }

        // 教务员查看本学院申请了本研互选选课的学生和申请了本学院开设课程的学生
        boolean isAcdemicDean = StringUtils.equals(session.getCurrentRole(), String.valueOf(Constants.ONE)) && !session.isAdmin() && session.isAcdemicDean();
        if (isAcdemicDean) {
            if (dto.getProjectIds().contains(projectId)) {
                dto.setCollege(faculty);  // 学生行政学院
            } else {
                dto.setOpenCollege(faculty);  // 开课学院
            }
        }

        List<ElcMutualListVo> exportElcMutualCrossList = elcMutualListDao.getMutualStuList(dto);

        try
        {
            ExcelEntityExport<ElcMutualListVo> excelExport = new ExcelEntityExport(exportElcMutualCrossList,
                excelStoreConfig.getExportelcMutualStuListExcelKey(),
                excelStoreConfig.getExportelcMutualStuListExcelTitle(),
                cacheDirectory);
            String path = excelExport.exportExcelToCacheDirectory("本研互选名单列表");
            restResult = RestResult.successData(I18nUtil.getMsg("export.success"), path);
            return restResult;
        }
        catch (Exception e)
        {
    //        LOG.error("exportElcMutualCrossList: [{}]", e.getMessage());
            return RestResult.fail();
        }
    }


}
