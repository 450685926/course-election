package com.server.edu.election.service.impl;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.server.edu.common.PageCondition;
import com.server.edu.common.entity.AbnormalTypeElection;
import com.server.edu.common.locale.I18nUtil;
import com.server.edu.common.rest.PageResult;
import com.server.edu.common.rest.RestResult;
import com.server.edu.dictionary.service.DictionaryService;
import com.server.edu.dictionary.utils.SpringUtils;
import com.server.edu.election.constants.Constants;
import com.server.edu.election.dao.ElcCourseTakeDao;
import com.server.edu.election.dao.ElcNoSelectReasonDao;
import com.server.edu.election.dto.ElcResultDto;
import com.server.edu.election.dto.NoSelectCourseStdsDto;
import com.server.edu.election.entity.ElcNoSelectReason;
import com.server.edu.election.rpc.StudentServiceInvoker;
import com.server.edu.election.service.NoSelectStudentService;
import com.server.edu.election.util.ExcelStoreConfig;
import com.server.edu.election.vo.ElcNoSelectReasonVo;
import com.server.edu.session.util.SessionUtils;
import com.server.edu.session.util.entity.Session;
import com.server.edu.util.CollectionUtil;
import com.server.edu.util.FileUtil;
import com.server.edu.util.excel.ExcelWriterUtil;
import com.server.edu.util.excel.GeneralExcelDesigner;
import com.server.edu.util.excel.GeneralExcelUtil;
import com.server.edu.util.excel.export.ExcelExecuter;
import com.server.edu.util.excel.export.ExcelResult;
import com.server.edu.util.excel.export.ExportExcelUtils;
import com.server.edu.welcomeservice.util.ExcelEntityExport;

/**
 * 
 * 未选课学生
 * 
 */
@Service
public class NoSelectStudentServiceImpl implements NoSelectStudentService
{
	private static Logger LOG =
	        LoggerFactory.getLogger(NoSelectStudentServiceImpl.class);
	
    @Autowired
    private ElcNoSelectReasonDao reasonDao;
    
    @Autowired
    private ElcCourseTakeDao courseTakeDao;
    
    @Value("${task.cache.directory}")
    private String cacheDirectory;
    
    @Autowired
    private DictionaryService dictionaryService;
    
    @Autowired
    private ExcelStoreConfig excelStoreConfig;
    
    /**
     *@Description: 查询学生未选课名单
     *@Param:
     *@return:
     *@Author: bear
     *@date: 2019/2/19 15:42
     */
     @Override
     public PageResult<NoSelectCourseStdsDto> findElectCourseList(PageCondition<NoSelectCourseStdsDto> condition) {
    	 Session session = SessionUtils.getCurrentSession();
    	 String deptId = session.getCurrentManageDptId();
//    	 String deptId = "1";
         condition.getCondition().setDeptId(deptId);
         PageHelper.startPage(condition.getPageNum_(),condition.getPageSize_());

         Page<NoSelectCourseStdsDto> electCourseList;
         if (org.apache.commons.lang.StringUtils.isNotEmpty(deptId) && Constants.PROJ_UNGRADUATE.equals(deptId)) {
             electCourseList = courseTakeDao.findNoSelectCourseStds(condition.getCondition());
             List<NoSelectCourseStdsDto> result = electCourseList.getResult();
             if (CollectionUtil.isNotEmpty(result)) {
	             List<String> studentCodes = result.stream().map(NoSelectCourseStdsDto::getStudentCode).collect(Collectors.toList());
	             List<AbnormalTypeElection> list = StudentServiceInvoker.getAbnormalTypeByStudentCodeBK(studentCodes);

	             Iterator<NoSelectCourseStdsDto> iterator = result.iterator();
	             while (iterator.hasNext()) {
	            	 NoSelectCourseStdsDto stdsDto = iterator.next();
	            	 for (AbnormalTypeElection abnormalTypeElection : list) {
						if (StringUtils.equals(stdsDto.getStudentCode(), abnormalTypeElection.getStudentCode())) {
							stdsDto.setStdStatusChanges(abnormalTypeElection.getAbnormalClassCode());
						}
					 }
				}
            }
         }else {
        	 if (StringUtils.equals(session.getCurrentRole(), String.valueOf(Constants.ONE))
        	            && !session.isAdmin() && session.isAcdemicDean()) { // 教务员
        		 condition.getCondition().setFaculty(session.getFaculty());
			 }
        	 
             electCourseList = courseTakeDao.findNoSelectCourseGraduteStds(condition.getCondition());
             List<NoSelectCourseStdsDto> result = electCourseList.getResult();
             if (CollectionUtil.isNotEmpty(result)) {
	             List<String> studentCodes = result.stream().map(NoSelectCourseStdsDto::getStudentCode).collect(Collectors.toList());
	             List<AbnormalTypeElection> list = StudentServiceInvoker.getAbnormalTypeByStudentCode(studentCodes);
	             
	             Iterator<NoSelectCourseStdsDto> iterator = result.iterator();
	             while (iterator.hasNext()) {
	            	 NoSelectCourseStdsDto stdsDto = iterator.next();
	            	 for (AbnormalTypeElection abnormalTypeElection : list) {
						if (StringUtils.equals(stdsDto.getStudentCode(), abnormalTypeElection.getStudentCode())) {
							stdsDto.setStdStatusChanges(abnormalTypeElection.getTypeName());
						}
					 }
				}
            }
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
    
    /*
     * 研究生导出未选课学生名单
     */
  	@Override
  	public RestResult<String> exportStudentNoCourseListGradute2(NoSelectCourseStdsDto condition) {
  		String path="";
        try {
        	 PageCondition<NoSelectCourseStdsDto> pageCondition = new PageCondition<NoSelectCourseStdsDto>();
             pageCondition.setCondition(condition);
             pageCondition.setPageSize_(1000);
             int pageNum = 0;
             pageCondition.setPageNum_(pageNum);
             List<NoSelectCourseStdsDto> list = new ArrayList<NoSelectCourseStdsDto>();
             while (true)
             {
                 pageNum++;
                 pageCondition.setPageNum_(pageNum);
                 PageResult<NoSelectCourseStdsDto> electCourseList = findElectCourseList(pageCondition);
                 list.addAll(electCourseList.getList());

                 if (electCourseList.getList().size() <= list.size())
                 {
                     break;
                 }
             }
            list = SpringUtils.convert(list);
            
        	@SuppressWarnings("unchecked")
			ExcelEntityExport<ElcResultDto> excelExport = new ExcelEntityExport(list,
        			excelStoreConfig.getGraduateNoSelectStudenExportKey(),
        			excelStoreConfig.getGraduateNoSelectStudenExportTitle(),
        			cacheDirectory);
        	path = excelExport.exportExcelToCacheDirectory("未选课学生名单");
			
        }catch (Exception e){
            return RestResult.failData("minor.export.fail");
        }
        return RestResult.successData("minor.export.success",path);
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
           design.addCell(I18nUtil.getMsg("noElection.trainingCategory"), "trainingCategory").setValueHandler(
        		   (value, rawData, cell) -> {
        			   return dictionaryService.query("X_PYLB", value, SessionUtils.getLang());
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

	@Override
	public List<NoSelectCourseStdsDto> findElectCourseListByIds(List<String> ids) {
		List<NoSelectCourseStdsDto> electCourseList = courseTakeDao.findElectCourseListByIds(ids);
		
        List<AbnormalTypeElection> list = StudentServiceInvoker.getAbnormalTypeByStudentCode(ids);
        
        for (NoSelectCourseStdsDto stdsDto : electCourseList) {
       	 	for (AbnormalTypeElection abnormalTypeElection : list) {
				if (StringUtils.equals(stdsDto.getStudentCode(), abnormalTypeElection.getStudentCode())) {
					stdsDto.setStdStatusChanges(abnormalTypeElection.getTypeName());
				}
			}
		}
		return electCourseList;
	}
}
