package com.server.edu.exam.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.server.edu.common.PageCondition;
import com.server.edu.common.rest.PageResult;
import com.server.edu.dictionary.utils.ClassroomCacheUtil;
import com.server.edu.dictionary.utils.SpringUtils;
import com.server.edu.exam.constants.ApplyStatus;
import com.server.edu.exam.dao.*;
import com.server.edu.exam.dto.*;
import com.server.edu.exam.entity.*;
import com.server.edu.exam.query.GraduateExamStudentQuery;
import com.server.edu.exam.service.GraduateExamApplyExaminationService;
import com.server.edu.exam.service.GraduateExamInfoService;
import com.server.edu.exam.service.GraduateExamStudentService;
import com.server.edu.exam.util.GraduateExamTransTime;
import com.server.edu.exception.ParameterValidateException;
import com.server.edu.session.util.SessionUtils;
import com.server.edu.session.util.entity.Session;
import com.server.edu.util.CollectionUtil;
import com.server.edu.util.excel.CellValueHandler;
import com.server.edu.util.excel.GeneralExcelCell;
import com.server.edu.util.excel.GeneralExcelDesigner;
import com.server.edu.util.excel.export.ExcelExecuter;
import com.server.edu.util.excel.export.ExcelResult;
import com.server.edu.util.excel.export.ExportExcelUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @description:
 * @author: bear
 * @create: 2019-09-10 09:29
 */

@Service
@Primary
public class GraduateExamStudentServiceImpl implements GraduateExamStudentService {

    @Autowired
    private GraduateExamRoomDao roomDao;

    @Autowired
    private GraduateExamStudentDao examStudentDao;

    @Autowired
    private GraduateExamInfoDao examInfoDao;

    @Autowired
    private GraduateExamLogDao logDao;

    @Autowired
    private GraduateExamInfoService infoService;

    @Autowired
    private GraduateExamApplyExaminationDao applyExaminationDao;

    @Autowired
    private GraduateExamApplyExaminationService applyExaminationService;

        @Override
    public PageResult<SelectDto> listRoom(PageCondition<SelectDto> condition) {
        PageHelper.startPage(condition.getPageNum_(),condition.getPageSize_());
        Page<SelectDto> page = roomDao.listRoom(condition.getCondition());
        return new PageResult<>(page);
    }

    @Override
    public PageResult<SelectDto> listStudent(PageCondition<SelectDto> condition) {
        SelectDto selectDto = condition.getCondition();
        if(selectDto.getCalendarId() == null || selectDto.getExamType() == null){
            throw new ParameterValidateException("入参有误");
        }
        PageHelper.startPage(condition.getPageNum_(),condition.getPageSize_());
        Page<SelectDto> page = examStudentDao.listSelectStudent(selectDto);
        return new PageResult<>(page);
    }

    @Override
    public PageResult<SelectDto> listCourse(PageCondition<SelectDto> condition) {
        SelectDto selectDto = condition.getCondition();
        if(selectDto.getCalendarId() == null || selectDto.getExamType() == null){
            throw new ParameterValidateException("入参有误");
        }
        String dptId = SessionUtils.getCurrentSession().getCurrentManageDptId();
        selectDto.setProjId(dptId);
        PageHelper.startPage(condition.getPageNum_(),condition.getPageSize_());
        Page<SelectDto> page = examInfoDao.ListCourse(selectDto);
        return new PageResult<>(page);
    }

    @Override
    public PageResult<GraduateExamStudentDto> listExamStudent(PageCondition<GraduateExamStudentQuery> condition) {
        GraduateExamStudentQuery studentQuery = condition.getCondition();
        if(studentQuery.getCalendarId() == null || studentQuery.getExamType() == null){
            throw new ParameterValidateException("入参有误");
        }
        String dptId = SessionUtils.getCurrentSession().getCurrentManageDptId();
        studentQuery.setProjId(dptId);
        studentQuery.setExamSituation(ApplyStatus.EXAM_SITUATION_NORMAL);
        PageHelper.startPage(condition.getPageNum_(),condition.getPageSize_());
        Page<GraduateExamStudentDto> page = examInfoDao.listExamStudent(studentQuery);
        return new PageResult<>(page);
    }

    @Override
    public ExcelResult export(GraduateExamStudentQuery condition) {
        ExcelResult excelResult = ExportExcelUtils.submitTask("listExamStudent", new ExcelExecuter() {
            @Override
            public GeneralExcelDesigner getExcelDesigner() {
                PageCondition<GraduateExamStudentQuery> pageCondition = new PageCondition<>();
                pageCondition.setCondition(condition);
                pageCondition.setPageNum_(0);
                pageCondition.setPageSize_(0);
                PageResult<GraduateExamStudentDto> pageResult = listExamStudent(pageCondition);
                List<GraduateExamStudentDto> list = pageResult.getList();
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
                design.addCell("课程代码", "courseCode");
                design.addCell("课程名称", "courseName");
                design.addCell("校区", "campus");
                design.addCell("考试地点", "roomName");
                design.addCell("考试时间", "examTime");
//                design.addCell("考试情况", "examSituation").setValueHandler(new CellValueHandler() {
//                    @Override
//                    public String handler(String s, Object o, GeneralExcelCell generalExcelCell) {
//                        if("1".equals(s)){
//                            return "正常";
//                        }else if("2".equals(s)){
//                            return "缓考";
//                        }else if("3".equals(s)){
//                            return "补考";
//                        }else if("4".equals(s)){
//                            return "重修";
//                        }else if("5".equals(s)){
//                            return "旷考";
//                        }
//                        return s;
//                    }
//                });
                design.addCell("课程序号", "teachingClassCode");
                design.addCell("教师", "teacherName");
                design.addCell("备注信息", "remark");
                return design;
            }
        });
        return excelResult;
    }

    @Override
    @Transactional
    public void deleteExamStudent(List<GraduateExamStudentDto> condition) {
        if(CollectionUtil.isEmpty(condition)){
            throw new ParameterValidateException("入参有误");
        }
        for (GraduateExamStudentDto graduateExamStudentDto : condition) {
            this.deleteExamStudent(graduateExamStudentDto);
        }
    }

    @Override
    @Transactional
    public void changeExamStudentRoom(List<GraduateExamStudentDto> condition, Long examRoomId) {
        if(CollectionUtil.isEmpty(condition) || examRoomId == null){
            throw new ParameterValidateException("入参有误");
        }
        int size = condition.size();
        GraduateExamRoom examRoom = roomDao.selectByPrimaryKey(examRoomId);
        if(examRoom.getRoomNumber().intValue() == examRoom.getRoomCapacity().intValue()){
            throw new ParameterValidateException("该考场已满，请选择其他考场");
        }
        if(size + examRoom.getRoomNumber() > examRoom.getRoomCapacity()){
            throw new ParameterValidateException("该教室剩余容量有限，不能同时更换这么多学生");
        }
        for (GraduateExamStudentDto graduateExamStudentDto : condition) {
            //先删除原先考场下面的学生，并更新考场人数
            this.deleteExamStudent(graduateExamStudentDto);
            this.updateExamStudentRoom(graduateExamStudentDto,examRoomId);
        }
    }

    @Override
    @Transactional
    public Restrict addExamStudent(ExamStudentAddDto condition) {
        if(condition.getCalendarId() == null || StringUtils.isBlank(condition.getCourseCode())
                || StringUtils.isBlank(condition.getStudentCode())|| condition.getExamRoomId() == null
                || condition.getExamInfoId() == null){
            throw new ParameterValidateException("入参有误");
        }
        GraduateExamInfo examInfo = examInfoDao.selectByPrimaryKey(condition.getExamInfoId());
        condition.setExamType(examInfo.getExamType());
        ExamStudentAddDto addDto = new ExamStudentAddDto();
        if(examInfo.getExamType().equals(ApplyStatus.FINAL_EXAM)){
            //查询该学生是否已经有成绩
           int i =  examStudentDao.findStudentScoreByCondition(condition);
           if(i > 0){
               throw new ParameterValidateException("该学生该课程已经有成绩，不能添加应考学生");
           }
            //查询该学生是否修读该课程
            addDto = examInfoDao.findStudentElcCourseTake(condition);
            if(addDto == null){
                throw new ParameterValidateException("该学生没有修读这个课程，不能添加应考学生");
            }
        }else{
            //查询该学生是课程补缓考审核通过
            addDto = applyExaminationDao.findStudentMakeUp(condition);
            if(addDto == null){
                throw new ParameterValidateException("补缓考审核通过列表没有对应学生课程，不能添加应考学生");
            }
        }
        //查询该学生是否已经排考了
        int i = examInfoDao.findStudentByInfoId(condition.getCalendarId(),condition.getCourseCode(),condition.getStudentCode());
        if(i > 0){
            throw new ParameterValidateException("该学生课程已经排考，不能再次添加应考学生");
        }
        GraduateExamRoom examRoom = roomDao.selectByPrimaryKey(condition.getExamRoomId());
        if(examRoom.getRoomNumber() + 1 > examRoom.getRoomCapacity()){
            throw new ParameterValidateException("该考场已经满了，请选择其他考场");
        }
        //考生入库
        GraduateExamStudentDto studentDto = new GraduateExamStudentDto();
        studentDto.setStudentCode(addDto.getStudentCode());
        studentDto.setTeachingClassId(addDto.getTeachingClassId());
        studentDto.setTeachingClassCode(addDto.getTeachingClassCode());
        studentDto.setTeachingClassName(addDto.getTeachingClassName());
        studentDto.setExamInfoId(condition.getExamInfoId());
        studentDto.setRoomId(examRoom.getRoomId());
        studentDto.setRoomName(examRoom.getRoomName());
        //排考时间冲突检验
        List<GraduateExamStudent> list = new ArrayList<>();
        GraduateExamStudent student = new GraduateExamStudent();
        student.setExamInfoId(condition.getExamInfoId());
        student.setStudentCode(addDto.getStudentCode());
        list.add(student);
        Restrict restrict = infoService.checkExamStudentsConflict(list);
        if(restrict != null){
            Set<String> studentIds = restrict.getStudentIds();
            if(CollectionUtil.isNotEmpty(studentIds)){
                return restrict;
            }
        }
        this.updateExamStudentRoom(studentDto,condition.getExamRoomId());
        return new Restrict();
    }

    @Override
    public List<ExamRoomDto> getExamRoomByExamInfoId(String examInfoIds) {
        if(StringUtils.isBlank(examInfoIds)){
            throw new ParameterValidateException("入参有误");
        }
        List<String> ids = Arrays.asList(examInfoIds.split(","));
        List<ExamRoomDto> list = examInfoDao.getExamRoomByExamInfoId(ids);
        return list;
    }

    @Override
    @Transactional
    public void setExamStudentSituatiion(List<GraduateExamStudentDto> condition, Integer examSituation) {
        if(CollectionUtil.isEmpty(condition) || examSituation == null){
            throw new ParameterValidateException("入参有误");
        }

        //过滤掉和考试情况一样的数据，这种数据不需要操作
        //List<GraduateExamStudentDto> studentDtos = condition.stream().filter(vo -> !examSituation.equals(vo.getExamSituation())).collect(Collectors.toList());
        Session currentSession = SessionUtils.getCurrentSession();
        if(CollectionUtil.isNotEmpty(condition)){
            for (GraduateExamStudentDto dto : condition) {
                this.setExamSituationByOne(dto,examSituation,currentSession);
            }

        }

    }

    private void setExamSituationByOne(GraduateExamStudentDto dto, Integer examSituation,Session currentSession) {

        //正常状态变为缓考
        if(examSituation.equals(ApplyStatus.EXAM_SITUATION_SLOW)){
            //更改状态
            GraduateExamStudent examStudent = examStudentDao.selectByPrimaryKey(dto.getExamStudentId());
            examStudent.setExamSituation(examSituation);
            examStudentDao.updateByPrimaryKey(examStudent);
            //更新考场人数
            GraduateExamRoom examRoom = roomDao.selectByPrimaryKey(dto.getExamRoomId());
            examRoom.setRoomNumber(examRoom.getRoomNumber() - 1);
            roomDao.updateByPrimaryKey(examRoom);
            //更新课程人数
            this.updateActualNumber(dto,ApplyStatus.EXAM_REDUCE);
            //排考日志
            this.insertExamLog(dto,ApplyStatus.EXAM_LOG_NO);
            //申请直接进入补缓考审核列表
            this.insertAppLyExam(dto,currentSession,examSituation);

        }

        //正常状态变为重修
        if(examSituation.equals(ApplyStatus.EXAM_SITUATION_REBUILD)){
            //更改状态
            GraduateExamStudent examStudent = examStudentDao.selectByPrimaryKey(dto.getExamStudentId());
            examStudent.setExamSituation(examSituation);
            examStudentDao.updateByPrimaryKey(examStudent);
            //更新考场人数
            GraduateExamRoom examRoom = roomDao.selectByPrimaryKey(dto.getExamRoomId());
            examRoom.setRoomNumber(examRoom.getRoomNumber() - 1);
            roomDao.updateByPrimaryKey(examRoom);
            //更新课程人数
            this.updateActualNumber(dto,ApplyStatus.EXAM_REDUCE);
            //排考日志
            this.insertExamLog(dto,ApplyStatus.EXAM_LOG_NO);
            //todo 进入重修列表增量

        }
    }

    private void insertAppLyExam(GraduateExamStudentDto dto, Session currentSession,Integer examSituation) {
        GraduateExamApplyExamination apply = new GraduateExamApplyExamination();
        apply.setCalendarId(dto.getCalendarId());
        apply.setStudentCode(dto.getStudentCode());
        apply.setApplySource(ApplyStatus.APPLY_SOURCE_OTHER);
        apply.setCourseCode(dto.getCourseCode());
        apply.setApplyType(examSituation);
        apply.setApplyStatus(ApplyStatus.SCHOOL_EXAMINE_PASS);
        apply.setTeachingClassId(dto.getTeachingClassId());
        apply.setProjId(currentSession.getCurrentManageDptId());
        applyExaminationService.checkRepeat(apply);

    }

    private void deleteExamStudent(GraduateExamStudentDto graduateExamStudentDto){
        Long examStudentId = graduateExamStudentDto.getExamStudentId();
        Long examRoomId = graduateExamStudentDto.getExamRoomId();
        //删除学生
        examStudentDao.deleteByPrimaryKey(examStudentId);
        //更新对应课程实际人数
        this.updateActualNumber(graduateExamStudentDto,ApplyStatus.EXAM_REDUCE);
        // 退考
        this.insertExamLog(graduateExamStudentDto,ApplyStatus.EXAM_LOG_NO);
        //更新考场人数
        GraduateExamRoom examRoom = roomDao.selectByPrimaryKey(examRoomId);
        examRoom.setRoomNumber(examRoom.getRoomNumber() - 1);
        roomDao.updateByPrimaryKey(examRoom);
    }

    private void updateActualNumber(GraduateExamStudentDto graduateExamStudentDto, Integer symbol) {
        Long examInfoId = graduateExamStudentDto.getExamInfoId();
        examInfoDao.updateActualNumberById(examInfoId,symbol);
    }

    private void updateExamStudentRoom(GraduateExamStudentDto graduateExamStudentDto,Long examRoomId){
        GraduateExamStudent examStudent = new GraduateExamStudent();
        Date date = new Date();
        examStudent.setStudentCode(graduateExamStudentDto.getStudentCode());
        examStudent.setExamRoomId(examRoomId);
        examStudent.setTeachingClassId(graduateExamStudentDto.getTeachingClassId());
        examStudent.setExamSituation(ApplyStatus.EXAM_SITUATION_NORMAL);
        examStudent.setTeachingClassCode(graduateExamStudentDto.getTeachingClassCode());
        examStudent.setTeachingClassName(graduateExamStudentDto.getTeachingClassName());
        examStudent.setCreateAt(date);
        examStudent.setUpdateAt(date);
        examStudent.setExamInfoId(graduateExamStudentDto.getExamInfoId());
        examStudentDao.insert(examStudent);
        // 排考
        graduateExamStudentDto.setExamRoomId(examRoomId);
        this.updateActualNumber(graduateExamStudentDto,ApplyStatus.EXAM_ADD);
        this.insertExamLog(graduateExamStudentDto,ApplyStatus.EXAM_LOG_YES);
        //更新新教室的排考人数
        GraduateExamRoom examRoom = roomDao.selectByPrimaryKey(examRoomId);
        examRoom.setRoomNumber(examRoom.getRoomNumber() + 1);
        roomDao.updateByPrimaryKey(examRoom);

    }

    private void insertExamLog(GraduateExamStudentDto examStudentDto,Integer examType){
        Session currentSession = SessionUtils.getCurrentSession();
        GraduateExamLog examLog = new GraduateExamLog();
        examLog.setStudentCode(examStudentDto.getStudentCode());
        examLog.setExamInfoId(examStudentDto.getExamInfoId());
        examLog.setExamType(examType);
        examLog.setRoomId(examStudentDto.getRoomId());
        examLog.setRoomName(examStudentDto.getRoomName());
        examLog.setCreateAt(new Date());
        examLog.setIp(currentSession.getIp());
        examLog.setOperatorCode(currentSession.realUid());
        examLog.setOperatorName(currentSession.realName());
        logDao.insert(examLog);
    }
}
