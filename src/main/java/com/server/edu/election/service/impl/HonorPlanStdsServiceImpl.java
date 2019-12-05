package com.server.edu.election.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.server.edu.common.PageCondition;
import com.server.edu.common.locale.I18nUtil;
import com.server.edu.common.rest.PageResult;
import com.server.edu.common.rest.RestResult;
import com.server.edu.dictionary.service.DictionaryService;
import com.server.edu.election.dao.HonorPlanStdsDao;
import com.server.edu.election.entity.HonorPlanStds;
import com.server.edu.election.query.HonorPlanStdsQuery;
import com.server.edu.election.service.HonorPlanStdsService;
import com.server.edu.election.vo.HonorPlanStdsVo;
import com.server.edu.session.util.SessionUtils;
import com.server.edu.util.CollectionUtil;
import com.server.edu.util.excel.GeneralExcelDesigner;
import com.server.edu.util.excel.export.ExcelExecuter;
import com.server.edu.util.excel.export.ExcelResult;
import com.server.edu.util.excel.export.ExportExcelUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.List;


@Service
public class HonorPlanStdsServiceImpl implements HonorPlanStdsService
{

    @Autowired
    private HonorPlanStdsDao honorPlanStdsDao;

    @Autowired
    private DictionaryService dictionaryService;
    @Override
    public PageResult<HonorPlanStdsVo> listPage(PageCondition<HonorPlanStdsQuery> page) {
        HonorPlanStdsQuery condition = page.getCondition();

        PageHelper.startPage(page.getPageNum_(), page.getPageSize_());
        Page<HonorPlanStdsVo> list = honorPlanStdsDao.pageList(condition);
        return new PageResult<HonorPlanStdsVo>(list);
    }

    @Override
    public RestResult<?> add(HonorPlanStdsVo honorPlanStdsVo) {
        if (honorPlanStdsVo.getCalendarId() == null ){
            return RestResult.fail("学年学期不能为空");
        }
        if (StringUtils.isEmpty(honorPlanStdsVo.getStudentId())){
            return RestResult.fail("学号不能为空");
        }
        if (StringUtils.isEmpty(honorPlanStdsVo.getHonorPlanName())){
            return RestResult.fail("荣耀计划名称不能为空");
        }
        Example example = new Example(HonorPlanStds.class);
        example.createCriteria()
                .andEqualTo("studentId",honorPlanStdsVo.getStudentId());
        example.createCriteria()
                .andEqualTo("calendarId",honorPlanStdsVo.getCalendarId());
        List<HonorPlanStds> honorPlanStds = honorPlanStdsDao.selectByExample(example);
        if (CollectionUtil.isNotEmpty(honorPlanStds))
        {
            return RestResult.fail("该学生已存在");
        }
        HonorPlanStds honor = new HonorPlanStds();
        honor.setCalendarId(honorPlanStdsVo.getCalendarId());
        honor.setDirectionName(honorPlanStdsVo.getDirectionName());
        honor.setHonorPlanName(honorPlanStdsVo.getHonorPlanName());
        honor.setStudentId(honorPlanStdsVo.getStudentId());
        honorPlanStdsDao.insert(honor);
        return RestResult.success("添加成功");
    }

    @Override
    public RestResult<?> delete(HonorPlanStdsQuery honorPlanStds) {
        if (honorPlanStds.getCalendarId() == null ){
            return RestResult.fail("学年学期不能为空");
        }
        Example example = new Example(HonorPlanStds.class);
        example.createCriteria()
                .andEqualTo("calendarId",honorPlanStds.getCalendarId());

        if (CollectionUtil.isNotEmpty(honorPlanStds.getIds())){
            example.createCriteria()
                    .andIn("id",honorPlanStds.getIds());
            honorPlanStdsDao.deleteByExample(example);
        }else{
            honorPlanStdsDao.deleteByExample(example);
        }
        return RestResult.success("删除成功");
    }

    @Override
    public ExcelResult export(HonorPlanStdsQuery condition) {
        ExcelResult excelResult = ExportExcelUtils.submitTask("HonorPlanStdsExport", new ExcelExecuter() {
            @Override
            public GeneralExcelDesigner getExcelDesigner() {
                ExcelResult result = this.getResult();
                PageCondition<HonorPlanStdsQuery> pageCondition = new PageCondition<HonorPlanStdsQuery>();
                pageCondition.setCondition(condition);
                pageCondition.setPageSize_(100);
                int pageNum = 0;
                pageCondition.setPageNum_(pageNum);
                List<HonorPlanStdsVo> list = new ArrayList<>();
                while (true)
                {
                    pageNum++;
                    pageCondition.setPageNum_(pageNum);
                    PageResult<HonorPlanStdsVo> electCourseList = listPage(pageCondition);
                    list.addAll(electCourseList.getList());

                    result.setTotal((int)electCourseList.getTotal_());
                    Double count = list.size() / 1.5;
                    result.setDoneCount(count.intValue());
                    this.updateResult(result);

                    if (electCourseList.getTotal_() <= list.size())
                    {
                        break;
                    }
                }
                //组装excel
                GeneralExcelDesigner design = getDesign();

                //将数据放入excel对象中
                design.setDatas(list);
                result.setDoneCount(list.size());
                return design;
            }
        });
        return excelResult;
    }
    private GeneralExcelDesigner getDesign() {
        GeneralExcelDesigner design = new GeneralExcelDesigner();
        design.setNullCellValue("");
        design.addCell("学号", "studentId");
        design.addCell("姓名", "studentName");
        design.addCell("培养层次", "trainingLevel").setValueHandler(
                (value, rawData, cell) -> {
                    return dictionaryService.query("X_PYCC", value);
                });
        design.addCell("年级", "grade");
        design.addCell("学院", "facutly").setValueHandler(
                (value, rawData, cell) -> {
                    return dictionaryService.query("X_YX", value);
                });
        design.addCell("专业", "profession").setValueHandler(
                (value, rawData, cell) -> {
                    return dictionaryService.query("G_ZY", value);
                });
        design.addCell("荣誉计划", "honorPlanName");
        design.addCell("课程方向", "directionName");
        return design;
    }
}
