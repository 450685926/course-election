package com.server.edu.exam.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.server.edu.common.PageCondition;
import com.server.edu.common.enums.GroupDataEnum;
import com.server.edu.common.rest.PageResult;
import com.server.edu.dictionary.utils.SpringUtils;
import com.server.edu.exam.dao.GraduateExamApplyExaminationDao;
import com.server.edu.exam.service.GraduateExamMakeUpQueryService;
import com.server.edu.exam.vo.GraduateExamApplyExaminationVo;
import com.server.edu.exception.ParameterValidateException;
import com.server.edu.session.util.SessionUtils;
import com.server.edu.session.util.entity.Session;
import com.server.edu.util.excel.CellValueHandler;
import com.server.edu.util.excel.GeneralExcelCell;
import com.server.edu.util.excel.GeneralExcelDesigner;
import com.server.edu.util.excel.export.ExcelExecuter;
import com.server.edu.util.excel.export.ExcelResult;
import com.server.edu.util.excel.export.ExportExcelUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @description:
 * @author: bear
 * @create: 2019-09-11 16:23
 */

@Service
@Primary
public class GraduateExamMakeUpQueryServiceImpl implements GraduateExamMakeUpQueryService {

    @Autowired
    private GraduateExamApplyExaminationDao applyExaminationDao;

    @Override
    public PageResult<GraduateExamApplyExaminationVo> listExamMakeUpQuery(PageCondition<GraduateExamApplyExaminationVo> condition) {
        GraduateExamApplyExaminationVo examinationVo = condition.getCondition();
        if(examinationVo.getCalendarId() == null){
            throw new ParameterValidateException("入参有误");
        }
        Session session = SessionUtils.getCurrentSession();
        String dptId = session.getCurrentManageDptId();
        List<String> facultys = session.getGroupData().get(GroupDataEnum.department.getValue());
        examinationVo.setFacultys(facultys);
        examinationVo.setProjId(dptId);
        PageHelper.startPage(condition.getPageNum_(),condition.getPageSize_());
        Page<GraduateExamApplyExaminationVo> page = applyExaminationDao.listGraduateMakeUp(examinationVo);
        return new PageResult<>(page);
    }

    @Override
    public ExcelResult export(GraduateExamApplyExaminationVo condition) {
        ExcelResult excelResult = ExportExcelUtils.submitTask("examMakeUp", new ExcelExecuter() {
            @Override
            public GeneralExcelDesigner getExcelDesigner() {
                PageCondition<GraduateExamApplyExaminationVo> pageCondition = new PageCondition<>();
                pageCondition.setCondition(condition);
                pageCondition.setPageNum_(0);
                pageCondition.setPageSize_(0);
                PageResult<GraduateExamApplyExaminationVo> pageResult = listExamMakeUpQuery(pageCondition);
                List<GraduateExamApplyExaminationVo> list = pageResult.getList();
                try {
                    list = SpringUtils.convert(list);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //组装excel
                GeneralExcelDesigner design = new GeneralExcelDesigner();
                design.setDatas(list);
                design.setNullCellValue("");
                design.addCell("学号", "studentCode");
                design.addCell("姓名", "studentName");
                design.addCell("校区", "campus");
                design.addCell("学院", "collenge");
                design.addCell("课程代码", "courseCode");
                design.addCell("课程名称", "courseName");
                design.addCell("申请来源", "applySource").setValueHandler(new CellValueHandler() {
                    @Override
                    public String handler(String s, Object o, GeneralExcelCell generalExcelCell) {
                        if("1".equals(s)){
                            return  "学生申请";
                        }else if("2".equals(s)){
                            return  "代理申请";
                        }
                        return s;
                    }
                });
                design.addCell("申请类型", "applyType").setValueHandler(new CellValueHandler() {
                    @Override
                    public String handler(String s, Object o, GeneralExcelCell generalExcelCell) {
                        if("2".equals(s)){
                            return  "缓考";
                        }else if("3".equals(s)){
                            return  "补考";
                        }
                        return s;
                    }
                });
                design.addCell("审核状态", "applyStatus").setValueHandler(new CellValueHandler() {
                    @Override
                    public String handler(String s, Object o, GeneralExcelCell generalExcelCell) {
                        if("0".equals(s)){
                            return "待审核";
                        }else if("1".equals(s)){
                            return "学院审核未通过";
                        }else if("2".equals(s)){
                            return "学院审核通过";
                        }else if("3".equals(s)){
                            return "学校审核未通过";
                        }else if("4".equals(s)){
                            return "学校审核通过";
                        }
                        return s;
                    }
                });
                design.addCell("申请理由", "applyReason");
                return design;
            }
        });
         return excelResult;
    }
}
