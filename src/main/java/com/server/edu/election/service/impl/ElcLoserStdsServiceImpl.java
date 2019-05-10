package com.server.edu.election.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.server.edu.common.PageCondition;
import com.server.edu.common.locale.I18nUtil;
import com.server.edu.common.rest.PageResult;
import com.server.edu.common.vo.SchoolCalendarVo;
import com.server.edu.dictionary.service.DictionaryService;
import com.server.edu.election.dao.ElcCourseTakeDao;
import com.server.edu.election.dao.ElcLoserStdsDao;
import com.server.edu.election.dto.LoserStuElcCourse;
import com.server.edu.election.entity.ElcCourseTake;
import com.server.edu.election.rpc.BaseresServiceInvoker;
import com.server.edu.election.service.ElcCourseTakeService;
import com.server.edu.election.service.ElcLoserStdsService;
import com.server.edu.election.vo.ElcLoserStdsVo;
import com.server.edu.session.util.SessionUtils;
import com.server.edu.util.CollectionUtil;
import com.server.edu.util.excel.GeneralExcelDesigner;
import com.server.edu.util.excel.export.ExcelExecuter;
import com.server.edu.util.excel.export.ExcelResult;
import com.server.edu.util.excel.export.ExportExcelUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * @description: 预警学生
 * @author: bear
 * @create: 2019-05-09 16:44
 */
@Service
@Primary
public class ElcLoserStdsServiceImpl implements ElcLoserStdsService {

    @Autowired
    private ElcLoserStdsDao stdsDao;

    @Autowired
    private ElcCourseTakeDao courseTakeDao;

    @Autowired
    private ElcCourseTakeService courseTakeService;

    @Value("${task.cache.directory}")
    private String cacheDirectory;


    @Autowired
    private DictionaryService dictionaryService;

    @Override
    public PageResult<ElcLoserStdsVo> findElcLoserStds(PageCondition<ElcLoserStdsVo> condition) {
        PageHelper.startPage(condition.getPageNum_(),condition.getPageSize_());
        Page<ElcLoserStdsVo> page=stdsDao.findElcLoserStds(condition.getCondition());
        if(page!=null){
            List<ElcLoserStdsVo> result = page.getResult();
            if(CollectionUtil.isNotEmpty(result)){
                SchoolCalendarVo calendarVo = BaseresServiceInvoker.getSchoolCalendarById(condition.getCondition().getCalendarId());
                for (ElcLoserStdsVo elcLoserStdsVo : result) {
                    elcLoserStdsVo.setCalendarName(calendarVo.getFullName());
                }

            }
        }
        return new PageResult<>(page);
    }

    /**
    *@Description: 移除预警学生
    *@Param:
    *@return: 
    *@Author: bear
    *@date: 2019/5/10 10:50
    */
    @Override
    @Transactional
    public String deleteLoserStudent(List<Long> ids) {
        stdsDao.deleteByIds(ids);
        return "common.deleteSuccess";
    }

    /**
    *@Description: 预警学生已经选的课程
    *@Param:
    *@return: 
    *@Author: bear
    *@date: 2019/5/10 11:36
    */
    @Override
    public List<LoserStuElcCourse> findStudentElcCourse(Long calendarId, String studentId) {
        List<LoserStuElcCourse> list =courseTakeDao.findStudentElcCourse(calendarId,studentId);
        return list;
    }

    /**
    *@Description: 退课
    *@Param: 
    *@return: 
    *@Author: bear
    *@date: 2019/5/10 15:20
    */
    @Override
    public void withdrawCourse(List<LoserStuElcCourse> list) {
        //进入预警选课回收站todo
        //调用退课服务
        List<ElcCourseTake> takes=new ArrayList<>();
        for (LoserStuElcCourse loserStuElcCourse : list) {
            ElcCourseTake take=new ElcCourseTake();
            take.setStudentId(loserStuElcCourse.getStudentId());
            take.setCalendarId(loserStuElcCourse.getCalendarId());
            take.setTeachingClassId(loserStuElcCourse.getTeachingClassId());
            takes.add(take);
        }
        courseTakeService.withdraw(takes);
    }

    /**
    *@Description: 导出
    *@Param:
    *@return: 
    *@Author: bear
    *@date: 2019/5/10 17:30
    */
    @Override
    public ExcelResult exportLoserStu(ElcLoserStdsVo condition) {
        ExcelResult excelResult = ExportExcelUtils.submitTask("elcLoserStds", new ExcelExecuter() {
            @Override
            public GeneralExcelDesigner getExcelDesigner() {
                ExcelResult result = this.getResult();
                PageCondition<ElcLoserStdsVo> pageCondition = new PageCondition<ElcLoserStdsVo>();
                pageCondition.setCondition(condition);
                pageCondition.setPageSize_(100);
                int pageNum = 0;
                pageCondition.setPageNum_(pageNum);
                List<ElcLoserStdsVo> list = new ArrayList<>();
                while (true)
                {
                    pageNum++;
                    pageCondition.setPageNum_(pageNum);
                    PageResult<ElcLoserStdsVo> electCourseList = findElcLoserStds(pageCondition);
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
        design.addCell(I18nUtil.getMsg("exemptionApply.calendarName"), "calendarName");
        design.addCell(I18nUtil.getMsg("exemptionApply.studentCode"), "studentId");
        design.addCell(I18nUtil.getMsg("exemptionApply.studentName"), "studentName");
        design.addCell(I18nUtil.getMsg("rebuildCourse.grade"), "grade");
        design.addCell(I18nUtil.getMsg("rebuildCourse.trainingLevel"), "trainingLevel").setValueHandler(
                (value, rawData, cell) -> {
                    return dictionaryService.query("X_PYCC", value, SessionUtils.getLang());
                });
        design.addCell(I18nUtil.getMsg("rebuildCourse.studentCategory"), "studentCategory");
        design.addCell(I18nUtil.getMsg("exemptionApply.faculty"), "faculty").setValueHandler(
                (value, rawData, cell) -> {
                    return dictionaryService.query("X_YX", value, SessionUtils.getLang());
                });

        design.addCell(I18nUtil.getMsg("exemptionApply.major"), "profession").setValueHandler(
                (value, rawData, cell) -> {
                    return dictionaryService.query("G_ZY", value, SessionUtils.getLang());
                });
        design.addCell(I18nUtil.getMsg("rollBookManage.direction"), "researchDirection");
        design.addCell(I18nUtil.getMsg("rollBookManage.unpassedCredits"), "unpassedCredits");

        return design;
    }
}
