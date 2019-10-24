package com.server.edu.exam.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.ibm.icu.impl.CalendarCache;
import com.server.edu.common.PageCondition;
import com.server.edu.common.enums.GroupDataEnum;
import com.server.edu.common.locale.I18nUtil;
import com.server.edu.common.rest.PageResult;
import com.server.edu.common.vo.SchoolCalendarVo;
import com.server.edu.dictionary.service.DictionaryService;
import com.server.edu.dictionary.utils.SpringUtils;
import com.server.edu.election.dto.ExportPreCondition;
import com.server.edu.election.dto.PreViewRollDto;
import com.server.edu.election.dto.TimeTableMessage;
import com.server.edu.election.rpc.BaseresServiceInvoker;
import com.server.edu.election.service.impl.ReportManagementServiceImpl;
import com.server.edu.election.vo.RollBookList;
import com.server.edu.exam.dao.GraduateExamInfoDao;
import com.server.edu.exam.dao.GraduateExamRoomDao;
import com.server.edu.exam.dao.GraduateExamStudentDao;
import com.server.edu.exam.dto.ExportExamInfoDto;
import com.server.edu.exam.dto.ExportStuDto;
import com.server.edu.exam.dto.GraduateTeachingClassDto;
import com.server.edu.exam.dto.PropertySheetDto;
import com.server.edu.exam.entity.GraduateExamRoom;
import com.server.edu.exam.query.GraduateExamMessageQuery;
import com.server.edu.exam.service.GraduateExamMessageService;
import com.server.edu.exam.vo.ExamStudent;
import com.server.edu.exam.vo.GraduateExamMessage;
import com.server.edu.exception.ParameterValidateException;
import com.server.edu.session.util.SessionUtils;
import com.server.edu.session.util.entity.Session;
import com.server.edu.util.CollectionUtil;
import com.server.edu.util.FileUtil;
import com.server.edu.util.ZipUtil;
import com.server.edu.util.excel.ExcelWriterUtil;
import com.server.edu.util.excel.GeneralExcelDesigner;
import com.server.edu.util.excel.GeneralExcelUtil;
import com.server.edu.util.excel.export.ExcelExecuter;
import com.server.edu.util.excel.export.ExcelResult;
import com.server.edu.util.excel.export.ExportExcelUtils;
import com.server.edu.util.excel.export.FileExecuter;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.apache.poi.hssf.model.InternalWorkbook;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.util.HSSFColor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;
import org.stringtemplate.v4.ST;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @description:
 * @author: bear
 * @create: 2019-09-06 16:06
 */

@Service
@Primary
public class GraduateExamMessageServiceImpl implements GraduateExamMessageService {

    private static Logger LOG = LoggerFactory.getLogger(ReportManagementServiceImpl.class);

    @Autowired
    private GraduateExamInfoDao examInfoDao;

    @Autowired
    private GraduateExamRoomDao roomDao;

    @Autowired
    private DictionaryService dictionaryService;

    @Autowired
    private FreeMarkerConfigurer freeMarkerConfigurer;

    @Autowired
    private RedisTemplate<String, ExcelResult> redisTemplate;

    @Autowired
    private GraduateExamStudentDao examStudentDao;

    @Value("${task.cache.directory}")
    private String cacheDirectory;

    @Override
    public PageResult<GraduateExamMessage> listGraduateExamMessage(PageCondition<GraduateExamMessageQuery> condition) {
        GraduateExamMessageQuery examMessage = condition.getCondition();
        if (examMessage.getCalendarId() == null || examMessage.getExamType() == null) {
            throw new ParameterValidateException(I18nUtil.getMsg("baseresservice.parameterError"));
        }
        Session session = SessionUtils.getCurrentSession();
        String dptId = session.getCurrentManageDptId();
        List<String> facultys = session.getGroupData().get(GroupDataEnum.department.getValue());
        examMessage.setFacultys(facultys);
        examMessage.setProjId(dptId);
        PageHelper.startPage(condition.getPageNum_(), condition.getPageSize_());
        Page<GraduateExamMessage> page = examInfoDao.listGraduateExamMessage(examMessage);
        if(CollectionUtil.isNotEmpty(page)){
            List<GraduateExamMessage> result = page.getResult();
            for (GraduateExamMessage graduateExamMessage : result) {
                Long examInfoId = graduateExamMessage.getExamInfoId();
                Long examRoomId = graduateExamMessage.getExamRoomId();
                String teacherStr = this.getTeacherStr(examInfoId,examRoomId);
                graduateExamMessage.setTeacherStr(teacherStr);
            }
        }
        return new PageResult<>(page);
    }

    private String getTeacherStr(Long examInfoId, Long examRoomId) {
        List<GraduateTeachingClassDto> teacherList = examStudentDao.findTeacherStudentNumber(examInfoId,examRoomId);
        String teacherStr = "";
        if(CollectionUtil.isNotEmpty(teacherList)){
            Map<Long, List<GraduateTeachingClassDto>> listMap = teacherList.stream().filter(vo -> vo.getTeachingClassId() != null).collect(Collectors.groupingBy(GraduateTeachingClassDto::getTeachingClassId));
            StringBuilder teacher = new StringBuilder();
            for (Long aLong : listMap.keySet()) {
                List<GraduateTeachingClassDto> classDtos = listMap.get(aLong);
                Integer number = classDtos.get(0).getStudentNumber();
                StringBuilder str = new StringBuilder();
                for (GraduateTeachingClassDto classDto : classDtos) {
                    String teacherCode = classDto.getTeacherCode();
                    String teacherName = classDto.getTeacherName();
                    String s = teacherCode +" "+teacherName;
                    str.append(s).append("、");
                }
                if(str.length()>0){
                    String s = str.substring(0, str.length() - 1);
                    String te = s+"("+number+"人)";
                    teacher.append(te).append(",");
                }
            }
            if(teacher.length()>0){
                teacherStr = teacher.substring(0, teacher.length() - 1);
            }
        }
        return teacherStr;
    }

    @Override
    public ExcelResult export(GraduateExamMessageQuery condition) {
        ExcelResult excelResult = ExportExcelUtils.submitTask("examMessage", new ExcelExecuter() {
            @Override
            public GeneralExcelDesigner getExcelDesigner() {
                PageCondition<GraduateExamMessageQuery> pageCondition = new PageCondition<>();
                pageCondition.setCondition(condition);
                pageCondition.setPageNum_(0);
                pageCondition.setPageSize_(0);
                PageResult<GraduateExamMessage> pageResult = listGraduateExamMessage(pageCondition);
                List<GraduateExamMessage> list = pageResult.getList();
                try {
                    list = SpringUtils.convert(list);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //组装excel
                GeneralExcelDesigner design = new GeneralExcelDesigner();
                design.setDatas(list);
                design.setNullCellValue("");
                design.addCell("课程代码", "courseCode");
                design.addCell("课程名称", "courseName");
                design.addCell("课程性质", "nature");
                design.addCell("开课学院", "faculty");
                design.addCell("校区", "campus");
                design.addCell("课程层次", "trainingLevel");
                design.addCell("学分", "credits");
                design.addCell("教师", "teacherStr");
                design.addCell("考试时间", "examTime");
                design.addCell("考场", "roomName");
                design.addCell("考试人数", "roomNumber");
                design.addCell("监考老师", "teacherName");
                return design;
            }
        });
        return excelResult;
    }

    @Override
    public ExcelResult exportStuList(ExportExamInfoDto exportStu) {

        ExcelResult excelResult = ExportExcelUtils.submitTask("examStuList", new ExcelExecuter() {
            @Override
            public GeneralExcelDesigner getExcelDesigner() {
                return exportStu(exportStu,"excle");
            }
        },"diy");
        return excelResult;
    }



    @Override
    public ExcelResult exportCheckTable(Long calendarId,Integer examType,String calendarName) {
        List<Long> examRoomIds = examInfoDao.getExamRoomIds(calendarId, examType);
        String key = "exportCheckTableZip";
        int total = examRoomIds.size();
        ExcelResult rs = new ExcelResult();
        rs.setStatus(false);
        rs.setCreateTime(System.currentTimeMillis());
        String newKey = key + rs.getCreateTime();
        rs.setKey(newKey);

        rs.setTotal(total);

        redisTemplate.opsForValue().set(newKey, rs);
        redisTemplate.expire(newKey, 5, TimeUnit.MINUTES);

        ExportExcelUtils.submitFileTask(newKey, new FileExecuter() {
            @Override
            public File getFile() {
                try {
                    List<File> fileList = new ArrayList<>();
                    for (Long  examRoomId: examRoomIds) {
                        ExportExamInfoDto exportExamInfoDto = new ExportExamInfoDto();
                        exportExamInfoDto.setCalendarId(calendarId);
                        exportExamInfoDto.setExamRoomId(examRoomId);
                        exportExamInfoDto.setCalendarName(calendarName);
                        GeneralExcelDesigner designer = exportStu(exportExamInfoDto,"zip");
                       // designer.getOverviews()
                        GraduateExamRoom examRoom = roomDao.getExamRoomNumber(examRoomId);

                        FileUtil.mkdirs(cacheDirectory);
                        Date date = new Date();
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
                        String dateNowStr = sdf.format(date);
                        String fileName =  "考场（"+examRoom.getRoomName()+"）签到表("+dateNowStr+").xls";
                        String path = cacheDirectory + fileName;

                        ExcelWriterUtil generalExcelHandle;
                        try {
                            generalExcelHandle = GeneralExcelUtil.generalExcelHandle(designer,"diy");
                            generalExcelHandle.writeExcel(new FileOutputStream(path));
                        } catch (Exception e) {
                            rs.setStatus(true);
                            redisTemplate.opsForValue().getAndSet(newKey, rs);
                            LOG.info(e.getMessage(), e);
                        }

                        fileList.add(new File(path));

                        int count = fileList.size();
                        ExcelResult rs = new ExcelResult();
                        rs.setStatus(false);
                        rs.setCreateTime(System.currentTimeMillis());
                        rs.setKey(newKey);
                        rs.setTotal(total);
                        rs.setDoneCount(count);
                        redisTemplate.opsForValue().getAndSet(newKey, rs);
                    }
                    ZipUtil.createZip(fileList, cacheDirectory+"checkTable.zip");
                    return new File(cacheDirectory+"checkTable.zip");
                } catch (FileNotFoundException e) {
                    rs.setStatus(true);
                    redisTemplate.opsForValue().getAndSet(newKey, rs);
                    LOG.info(e.getMessage(), e);
                    return null;
                } catch (IOException e) {
                    rs.setStatus(true);
                    redisTemplate.opsForValue().getAndSet(newKey, rs);
                    LOG.info(e.getMessage(), e);
                    return null;
                } catch (Exception e) {
                    rs.setStatus(true);
                    redisTemplate.opsForValue().getAndSet(newKey, rs);
                    LOG.info(e.getMessage(), e);
                    return null;
                }
            }
        },".zip");
        return  rs;

    }

    private GeneralExcelDesigner exportStu(ExportExamInfoDto exportExamInfoDto, String type) {
        ArrayList<ExportStuDto> dataList = new ArrayList<>();
        List<ExamStudent> list = examInfoDao.listExamStus(exportExamInfoDto.getCalendarId(), exportExamInfoDto.getExamRoomId());
        StringBuilder examTime = new StringBuilder();
        String examRoom ="";
        if(list.size()>0){
            Date date = list.get(0).getExamDate();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String dateStr = sdf.format(date);
            examTime.append(dateStr).append(" ").append(list.get(0).getExamStartTime()).append("-").append(list.get(0).getExamEndTime());
            examRoom = list.get(0).getRoomName();
        }
        if (list != null) {
            int divNum = list.size() / 2;
            int moreNum = list.size() % 2;
            if (moreNum == 0) {
                for (int i = 0; i < divNum; i++) {
                    ExportStuDto exportStuDto = new ExportStuDto();

                    exportStuDto.setOrder(list.get(i).getOrderStu());
                    exportStuDto.setTeachingClassCode(list.get(i).getTeachingClassCode());
                    exportStuDto.setStudentCode(list.get(i).getStudentCode());
                    exportStuDto.setStudentName(list.get(i).getStudentName());
                    exportStuDto.setFaculty(list.get(i).getFaculty());
                    exportStuDto.setCourseCode(list.get(i).getCourseCode());
                    exportStuDto.setCourseName(list.get(i).getCourseName());

                    exportStuDto.setOrder_R(list.get((divNum + i)).getOrderStu());
                    exportStuDto.setTeachingClassCode_R(list.get((divNum + i)).getTeachingClassCode());
                    exportStuDto.setStudentCode_R(list.get((divNum + i)).getStudentCode());
                    exportStuDto.setStudentName_R(list.get((divNum + i)).getStudentName());
                    exportStuDto.setFaculty_R(list.get((divNum + i)).getFaculty());
                    exportStuDto.setCourseCode_R(list.get((divNum + i)).getCourseCode());
                    exportStuDto.setCourseName_R(list.get((divNum + i)).getCourseName());

                    dataList.add(exportStuDto);
                }
            } else {
                for (int i = 0; i < divNum; i++) {
                    ExportStuDto exportStuDto = new ExportStuDto();

                    exportStuDto.setOrder(list.get(i).getOrderStu());
                    exportStuDto.setTeachingClassCode(list.get(i).getTeachingClassCode());
                    exportStuDto.setStudentCode(list.get(i).getStudentCode());
                    exportStuDto.setStudentName(list.get(i).getStudentName());
                    exportStuDto.setFaculty(list.get(i).getFaculty());
                    exportStuDto.setCourseCode(list.get(i).getCourseCode());
                    exportStuDto.setCourseName(list.get(i).getCourseName());

                    exportStuDto.setOrder_R(list.get((divNum + 1 + i)).getOrderStu());
                    exportStuDto.setTeachingClassCode_R(list.get((divNum + 1 + i)).getTeachingClassCode());
                    exportStuDto.setStudentCode_R(list.get((divNum + 1 + i)).getStudentCode());
                    exportStuDto.setStudentName_R(list.get((divNum + 1 + i)).getStudentName());
                    exportStuDto.setFaculty_R(list.get((divNum + 1 + i)).getFaculty());
                    exportStuDto.setCourseCode_R(list.get((divNum + 1 + i)).getCourseCode());
                    exportStuDto.setCourseName_R(list.get((divNum + 1 + i)).getCourseName());

                    dataList.add(exportStuDto);
                }
                ExportStuDto exportStuDto = new ExportStuDto();

                exportStuDto.setOrder(list.get(divNum).getOrderStu());
                exportStuDto.setTeachingClassCode(list.get(divNum).getTeachingClassCode());
                exportStuDto.setStudentCode(list.get(divNum).getStudentCode());
                exportStuDto.setStudentName(list.get(divNum).getStudentName());
                exportStuDto.setFaculty(list.get(divNum).getFaculty());
                dataList.add(exportStuDto);
            }

        }

        GeneralExcelDesigner design = new GeneralExcelDesigner();
        design.setVerticalMerges();
        design.setDatas(dataList);
        design.setNullCellValue("");
        design.setTitle("同济大学" + exportExamInfoDto.getCalendarName() + "研究生课程考试名单");
        String[] strings = {"考试时间：","", "考试地点：" , ""};
        if(dataList.size()>0){
            strings[1] = examTime.toString();
            //strings[1] = examTime.toString().split(" ")[0];
            //strings[2] = examTime.toString().split(" ")[1];
            strings[3] = examRoom;
        }

        design.setOverviews(strings);
        design.addCell("序号", "order");
        design.addCell("教学班号", "teachingClassCode");
        design.addCell("学号", "studentCode");
        design.addCell("姓名", "studentName");
        if("zip".equals(type)){
            design.addCell("课程代码", "courseCode");
            design.addCell("课程名称", "courseName");
            design.addCell("签到", "sign");
        }else {
            design.addCell("学院", "faculty").setValueHandler((value, rawData, cell) -> {
                return dictionaryService.query("X_YX", value, SessionUtils.getLang());
            });
            design.addCell("课程代码", "courseCode");
            design.addCell("课程名称", "courseName");
        }


        design.addCell("序号", "order_R");
        design.addCell("教学班号", "teachingClassCode_R");
        design.addCell("学号", "studentCode_R");
        design.addCell("姓名", "studentName_R");
        if("zip".equals(type)){
            design.addCell("课程代码", "courseCode_R");
            design.addCell("课程名称", "courseName_R");
            design.addCell("签到", "sign");
        }else {
            design.addCell("学院", "faculty_R").setValueHandler((value, rawData, cell) -> {
                return dictionaryService.query("X_YX", value, SessionUtils.getLang());
            });
            design.addCell("课程代码", "courseCode_R");
            design.addCell("课程名称", "courseName_R");
        }

        return design;
    }

    @Override
    public ExcelResult exportPropertySheet(GraduateExamMessageQuery condition)  {
         Long calendarId = condition.getCalendarId();
        if(calendarId == null || condition.getExamType() == null){
            throw new ParameterValidateException(I18nUtil.getMsg("baseresservice.parameterError"));
        }
        String key = "exportPropertySheet";
        ExcelResult rs = new ExcelResult();
        rs.setStatus(false);
        rs.setCreateTime(System.currentTimeMillis());
        String newKey = key + rs.getCreateTime();
        rs.setKey(newKey);
        redisTemplate.opsForValue().set(newKey, rs);
        redisTemplate.expire(newKey, 5, TimeUnit.MINUTES);
        String dptId = SessionUtils.getCurrentSession().getCurrentManageDptId();
        condition.setProjId(dptId);
        List<PropertySheetDto> SheetDto = roomDao.listExamRoomAndExamInfo(condition);
        ExportExcelUtils.submitFileTask(newKey, new FileExecuter() {
            @Override
            public File getFile() {
                try {
                for (PropertySheetDto propertySheetDto : SheetDto) {
                    String s = dictionaryService.queryByCodeList("X_YX", Arrays.asList(propertySheetDto.getFaculty().split(",")), SessionUtils.getLang());
                    String campus = dictionaryService.query("X_XQ", propertySheetDto.getCampus(), SessionUtils.getLang());
                    propertySheetDto.setFaculty(s);
                    propertySheetDto.setCampus(campus);
                }
                    SchoolCalendarVo calendarVo = BaseresServiceInvoker.getSchoolCalendarById(calendarId);
                    String fullName = calendarVo.getFullName();
                    Map<String, List<PropertySheetDto>> sheetMap = SheetDto.stream().collect(Collectors.groupingBy(PropertySheetDto::getCampus));
                Map<String,Object> map = new HashMap<>();
                String title = fullName+"研究生考试安排表";
                map.put("sheetMap",sheetMap);
                map.put("title",title);
                FileUtil.mkdirs(cacheDirectory);
                FileUtil.deleteFile(cacheDirectory, 2);
                String fileName =
                        "exportPropertySheet-" + System.currentTimeMillis() + ".xls";
                 String path = cacheDirectory + fileName;
                 Template tpl = freeMarkerConfigurer.getConfiguration().getTemplate("propertySheet.ftl");
                    // 将模板和数据模型合并生成文件
                    Writer  out = new BufferedWriter(
                            new OutputStreamWriter(new FileOutputStream(path), "UTF-8"));
                    tpl.process(map, out);
                    out.flush();
                    return new File(path);
                } catch (Exception e) {
                    e.printStackTrace();
                    rs.setStatus(true);
                    redisTemplate.opsForValue().getAndSet(newKey, rs);
                    return null;
                }

            }}, ".xls");
        return rs;

    }

    @Override
    public ExcelResult exportInspectionSheet(GraduateExamMessageQuery condition) {
        Long calendarId = condition.getCalendarId();
        if(calendarId == null || condition.getExamType() == null){
            throw new ParameterValidateException(I18nUtil.getMsg("baseresservice.parameterError"));
        }
        String key = "exportInspectionSheet";
        ExcelResult rs = new ExcelResult();
        rs.setStatus(false);
        rs.setCreateTime(System.currentTimeMillis());
        String newKey = key + rs.getCreateTime();
        rs.setKey(newKey);
        redisTemplate.opsForValue().set(newKey, rs);
        redisTemplate.expire(newKey, 5, TimeUnit.MINUTES);
        String dptId = SessionUtils.getCurrentSession().getCurrentManageDptId();
        condition.setProjId(dptId);
        List<PropertySheetDto> SheetDto = roomDao.listExamRoomAndExamInfo(condition);
        ExportExcelUtils.submitFileTask(newKey, new FileExecuter() {
            @Override
            public File getFile() {

                for (PropertySheetDto propertySheetDto : SheetDto) {
                    Date examDate = propertySheetDto.getExamDate();
                    String day = getDay(examDate);
                    String s = dictionaryService.queryByCodeList("X_YX", Arrays.asList(propertySheetDto.getFaculty().split(",")), SessionUtils.getLang());
                    String campus = dictionaryService.query("X_XQ", propertySheetDto.getCampus(), SessionUtils.getLang());
                    propertySheetDto.setFaculty(s);
                    propertySheetDto.setCampus(campus);
                    propertySheetDto.setDay(day);
                }
                SchoolCalendarVo calendarVo = BaseresServiceInvoker.getSchoolCalendarById(calendarId);
                String fullName = calendarVo.getFullName();
                String title = fullName+"研究生考试巡考工作安排表";
                Map<String, List<PropertySheetDto>> dayMap = SheetDto.stream().collect(Collectors.groupingBy(PropertySheetDto::getDay));
                List<Map<String,Object>> listSheet = new ArrayList<>();
                for (String day : dayMap.keySet()) {
                    List<PropertySheetDto> propertySheetDtos = dayMap.get(day);
                    Map<String, List<PropertySheetDto>> campusMap = propertySheetDtos.stream().collect(Collectors.groupingBy(PropertySheetDto::getCampus));
                    for (String campus : campusMap.keySet()) {
                        Map<String,Object> map = new HashMap<>();
                        List<PropertySheetDto> list = campusMap.get(campus);
                        map.put("day",day);
                        map.put("campus",campus);
                        map.put("dto",list.get(0));
                        map.put("list",list.subList(1,list.size()));
                        map.put("rowNumber",list.size() - 1);
                        listSheet.add(map);
                    }
                }
                Map<String,Object> myMap = new HashMap<>();
                myMap.put("title",title);
                myMap.put("listSheet",listSheet);
                FileUtil.mkdirs(cacheDirectory);
                FileUtil.deleteFile(cacheDirectory, 2);
                String fileName =
                        "exportInspectionSheet-" + System.currentTimeMillis() + ".xls";
                String path = cacheDirectory + fileName;
                try {
                    Template tpl = freeMarkerConfigurer.getConfiguration().getTemplate("inspectionSheet.ftl");
                    Writer  out = new BufferedWriter(
                            new OutputStreamWriter(new FileOutputStream(path), "UTF-8"));
                    tpl.process(myMap, out);
                    out.flush();
                    return new File(path);
                } catch (Exception e) {
                    e.printStackTrace();
                    rs.setStatus(true);
                    redisTemplate.opsForValue().getAndSet(newKey, rs);
                    return null;
                }
            }
        },".xls");
        return rs;
    }

    private String getDay(Date examDate){
        Calendar cal = Calendar.getInstance();
        cal.setTime(examDate);
        String day = String.valueOf(cal.get(Calendar.DAY_OF_MONTH));
        return day;
    }

}
