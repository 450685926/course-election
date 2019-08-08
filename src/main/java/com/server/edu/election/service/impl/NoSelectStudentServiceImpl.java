package com.server.edu.election.service.impl;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.server.edu.common.PageCondition;
import com.server.edu.common.locale.I18nUtil;
import com.server.edu.common.rest.PageResult;
import com.server.edu.dictionary.service.DictionaryService;
import com.server.edu.election.constants.Constants;
import com.server.edu.election.dao.ElcCourseTakeDao;
import com.server.edu.election.dao.ElcNoSelectReasonDao;
import com.server.edu.election.dto.NoSelectCourseStdsDto;
import com.server.edu.election.entity.ElcNoSelectReason;
import com.server.edu.election.service.NoSelectStudentService;
import com.server.edu.election.vo.ElcNoSelectReasonVo;
import com.server.edu.session.util.SessionUtils;
import com.server.edu.util.FileUtil;
import com.server.edu.util.excel.ExcelWriterUtil;
import com.server.edu.util.excel.GeneralExcelDesigner;
import com.server.edu.util.excel.GeneralExcelUtil;
import com.server.edu.util.excel.export.ExcelExecuter;
import com.server.edu.util.excel.export.ExcelResult;
import com.server.edu.util.excel.export.ExportExcelUtils;

/**
 * 
 * 未选课学生
 * 
 */
@Service
public class NoSelectStudentServiceImpl implements NoSelectStudentService
{
    @Autowired
    private ElcNoSelectReasonDao reasonDao;
    
    @Autowired
    private ElcCourseTakeDao courseTakeDao;
    
    @Value("${task.cache.directory}")
    private String cacheDirectory;
    
    @Autowired
    private DictionaryService dictionaryService;
    
    /**
     *@Description: 查询学生未选课名单
     *@Param:
     *@return:
     *@Author: bear
     *@date: 2019/2/19 15:42
     */
     @Override
     public PageResult<NoSelectCourseStdsDto> findElectCourseList(PageCondition<NoSelectCourseStdsDto> condition) {
         String deptId = SessionUtils.getCurrentSession().getCurrentManageDptId();
         condition.getCondition().setDeptId(deptId);
         PageHelper.startPage(condition.getPageNum_(),condition.getPageSize_());

         Page<NoSelectCourseStdsDto> electCourseList;
         if (org.apache.commons.lang.StringUtils.isNotEmpty(deptId) && Constants.PROJ_UNGRADUATE.equals(deptId)) {
             electCourseList = courseTakeDao.findNoSelectCourseStds(condition.getCondition());
         }else {
             electCourseList = courseTakeDao.findNoSelectCourseGraduteStds(condition.getCondition());
         }
         return new PageResult<>(electCourseList);
     }

     /**
      * 增加未选课原因
      * */
     @Override
     public String addNoSelectReason(ElcNoSelectReasonVo noSelectReason) {
         reasonDao.deleteNoSelectReason(noSelectReason.getCalendarId(),noSelectReason.getStudentIds());
         reasonDao.insertReason(noSelectReason.getCalendarId(),noSelectReason.getStudentIds(),noSelectReason.getReason());
         return "common.editSuccess";
     }

     /**查找未选课原因*/
     @Override
     public ElcNoSelectReason findNoSelectReason(Long calendarId, String studentCode) {
         ElcNoSelectReason noSelectReason = reasonDao.findNoSelectReason(calendarId, studentCode);
         return noSelectReason;
     }
     
      /*
       * 研究生导出未选课学生名单
       */
      @Override
      public String exportStudentNoCourseListGradute(NoSelectCourseStdsDto condition) throws Exception {
          PageCondition<NoSelectCourseStdsDto> pageCondition = new PageCondition<NoSelectCourseStdsDto>();
          pageCondition.setCondition(condition);
          pageCondition.setPageSize_(Constants.ZERO);
          pageCondition.setPageNum_(Constants.ZERO);
          PageResult<NoSelectCourseStdsDto> electCourseList = findElectCourseList(pageCondition);
          if(electCourseList!=null){
              List<NoSelectCourseStdsDto> list = electCourseList.getList();
              if (list == null) {
                  list = new ArrayList<>();
              }
              GeneralExcelDesigner design = getDesignGraduteStudent();
              design.setDatas(list);
              ExcelWriterUtil generalExcelHandle;
              generalExcelHandle = GeneralExcelUtil.generalExcelHandle(design);
              FileUtil.mkdirs(cacheDirectory);
              String fileName = "studentNoSelectCourseListGradute.xls";
              String path = cacheDirectory + fileName;
              generalExcelHandle.writeExcel(new FileOutputStream(path));
              return fileName;
          }
          return "";
      }
      
      /**
       *@Description: 导出未选课名单
       *@Param:
       *@return:
       *@Author: bear
       *@date: 2019/5/8 16:13
       */
       @Override
       public ExcelResult export(NoSelectCourseStdsDto condition) {
           ExcelResult excelResult = ExportExcelUtils.submitTask("noSelectCourseList", new ExcelExecuter() {
               @Override
               public GeneralExcelDesigner getExcelDesigner() {
                   ExcelResult result = this.getResult();
                   PageCondition<NoSelectCourseStdsDto> pageCondition = new PageCondition<NoSelectCourseStdsDto>();
                   pageCondition.setCondition(condition);
                   pageCondition.setPageSize_(100);
                   int pageNum = 0;
                   pageCondition.setPageNum_(pageNum);
                   List<NoSelectCourseStdsDto> list = new ArrayList<>();
                   while (true)
                   {
                       pageNum++;
                       pageCondition.setPageNum_(pageNum);
                       PageResult<NoSelectCourseStdsDto> electCourseList = findElectCourseList(pageCondition);
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
           design.addCell(I18nUtil.getMsg("exemptionApply.studentCode"), "studentCode");
           design.addCell(I18nUtil.getMsg("exemptionApply.studentName"), "studentName");
           design.addCell(I18nUtil.getMsg("rebuildCourse.grade"), "grade");
           design.addCell(I18nUtil.getMsg("exemptionApply.faculty"), "faculty").setValueHandler(
                   (value, rawData, cell) -> {
                       return dictionaryService.query("X_YX", value, SessionUtils.getLang());
                   });

           design.addCell(I18nUtil.getMsg("exemptionApply.major"), "major").setValueHandler(
                   (value, rawData, cell) -> {
                       return dictionaryService.query("G_ZY", value, SessionUtils.getLang());
                   });
           design.addCell(I18nUtil.getMsg("rebuildCourse.studentStatus"), "stdStatusChanges");
           design.addCell(I18nUtil.getMsg("rebuildCourse.noSelectCourseReason"), "noSelectReason");
           return design;
       }

       private GeneralExcelDesigner getDesignGraduteStudent() {
           GeneralExcelDesigner design = new GeneralExcelDesigner();
           design.setNullCellValue("");
           design.addCell(I18nUtil.getMsg("exemptionApply.studentCode"), "studentCode");
           design.addCell(I18nUtil.getMsg("exemptionApply.studentName"), "studentName");
           design.addCell(I18nUtil.getMsg("rebuildCourse.grade"), "grade");
           design.addCell(I18nUtil.getMsg("exemptionApply.faculty"), "faculty").setValueHandler(
                   (value, rawData, cell) -> {
                       return dictionaryService.query("X_YX", value, SessionUtils.getLang());
                   });
           design.addCell(I18nUtil.getMsg("exemptionApply.major"), "major").setValueHandler(
                   (value, rawData, cell) -> {
                       return dictionaryService.query("G_ZY", value, SessionUtils.getLang());
                   });
           design.addCell(I18nUtil.getMsg("rebuildCourse.trainingLevel"), "trainingLevel").setValueHandler(
                   (value, rawData, cell) -> {
                       return dictionaryService.query("X_PYCC", value, SessionUtils.getLang());
                   });
           design.addCell(I18nUtil.getMsg("noElection.trainingCategory"), "trainingCategory").setValueHandler(
                   (value, rawData, cell) -> {
                       return dictionaryService.query("X_PYLB", value, SessionUtils.getLang());
                   });
           design.addCell(I18nUtil.getMsg("noElection.degreeType"), "degreeType").setValueHandler(
                   (value, rawData, cell) -> {
                       return dictionaryService.query("X_XWLX", value, SessionUtils.getLang());
                   });
           design.addCell(I18nUtil.getMsg("noElection.formLearning"), "formLearning").setValueHandler(
                   (value, rawData, cell) -> {
                       return dictionaryService.query("X_XXXS", value, SessionUtils.getLang());
                   });
           design.addCell(I18nUtil.getMsg("rebuildCourse.studentStatus"), "stdStatusChanges");
           return design;
       }
}