package com.server.edu.exam.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.server.edu.common.PageCondition;
import com.server.edu.common.entity.WorkTime;
import com.server.edu.common.enums.GroupDataEnum;
import com.server.edu.common.rest.PageResult;
import com.server.edu.common.vo.SchCalendarTimeVo;
import com.server.edu.dictionary.service.DictionaryService;
import com.server.edu.dictionary.utils.SpringUtils;
import com.server.edu.exam.constants.ApplyStatus;
import com.server.edu.exam.constants.DeleteStatus;
import com.server.edu.exam.dao.*;
import com.server.edu.exam.dto.*;
import com.server.edu.exam.entity.*;
import com.server.edu.exam.query.GraduateExamAutoStudent;
import com.server.edu.exam.query.GraduateExamRoomsQuery;
import com.server.edu.exam.query.StudentQuery;
import com.server.edu.exam.rpc.BaseresServiceExamInvoker;
import com.server.edu.exam.service.GraduateExamInfoService;
import com.server.edu.exam.util.GraduateExamTransTime;
import com.server.edu.exam.util.OccupyUtils;
import com.server.edu.exam.vo.ExamStudent;
import com.server.edu.exam.vo.GraduateExamInfoVo;
import com.server.edu.exam.vo.GraduateExamRoomVo;
import com.server.edu.exam.vo.GraduateExamStudentVo;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;


@Service
@Primary
public class GraduateExamInfoServiceImpl implements GraduateExamInfoService {

    @Autowired
    private GraduateExamInfoDao examInfoDao;

    @Autowired
    private GraduateExamAuthDao authDao;

    @Autowired
    private GraduateExamRoomDao roomDao;

    @Autowired
    private GraduateExamTeacherDao teacherDao;

    @Autowired
    private GraduateExamStudentDao studentDao;

    @Autowired
    private GraduateExamInfoRoomDao infoRoomDao;

    @Autowired
    private GraduateExamLogDao logDao;

    @Autowired
    private GraduateExamMonitorTeacherDao monitorTeacherDao;

    @Autowired
    private DictionaryService dictionaryService;


    @Override
    public PageResult<GraduateExamInfoVo> listGraduateExamInfo(PageCondition<GraduateExamInfoVo> condition) {
        GraduateExamInfoVo examInfoVo = condition.getCondition();
        //参数校验
        if (examInfoVo.getCalendarId() == null || examInfoVo.getExamType() == null) {
            throw new ParameterValidateException("入参有误");
        }
        Session session = SessionUtils.getCurrentSession();
        String dptId = session.getCurrentManageDptId();
        examInfoVo.setProjId(dptId);
        List<String> facultys = session.getGroupData().get(GroupDataEnum.department.getValue());
        examInfoVo.setFacultys(facultys);
        int mo = (int) (examInfoVo.getCalendarId() % 6);
        examInfoVo.setMode(mo);
        //校验当前时间当前角色是否有权限进行排考
        this.checkTimeAndFaculty(examInfoVo);
        //通过查询条件和权限过滤数据,查询补缓考数据 1 期末考试 2 补缓考
        Page<GraduateExamInfoVo>  page = new Page<>();
        PageHelper.startPage(condition.getPageNum_(),condition.getPageSize_());
        if (examInfoVo.getExamType() == ApplyStatus.CONSTANTS_INT) {
             page = examInfoDao.listGraduateExamInfoMakeUp(examInfoVo);
        } else {//查询期末考试
             page = examInfoDao.listGraduateExamInfoFinal(examInfoVo);
        }
        if(CollectionUtil.isNotEmpty(page)){
            for (GraduateExamInfoVo vo : page.getResult()) {
                if(StringUtils.isNotBlank(vo.getTeachingClassName())){
                    vo.setTeachingClassName(vo.getTeachingClassName().replaceAll(",","/"));
                }else{
                    vo.setTeachingClassName("");
                }
            }
        }
        return new PageResult<>(page);
    }

    @Override
    @Transactional
    public void releaseOrRebackGraduateExamInfo(List<Long> ids, String type) {
        if (CollectionUtil.isEmpty(ids) || StringUtils.isBlank(type)) {
            throw new ParameterValidateException("入参有误");
        }
        Example example = new Example(GraduateExamInfo.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("id", ids);
        GraduateExamInfo examInfo = new GraduateExamInfo();
        if (ApplyStatus.CONSTANTS_STRING.equals(type)) {
            examInfo.setExamStatus(ApplyStatus.CONSTANTS_INT);
        } else {
            examInfo.setExamStatus(ApplyStatus.PASS_INT);
        }
        examInfoDao.updateByExampleSelective(examInfo, example);
    }

    @Override
    @Transactional
    public ExamSaveTimeRebackDto insertTime(List<GraduateExamInfo> examInfo) {
        if (CollectionUtil.isEmpty(examInfo)) {
            throw new ParameterValidateException("入参有误");
        }
        GraduateExamInfo info = examInfo.get(0);
        if(info.getNotice() == null){
            throw new ParameterValidateException("入参有误");
        }
        Integer weekNumber = null;
        Integer weekDay = null;
        String classNode = "";
        Long actualCalendarId = null;

        String examTime = "";
        //时间地点
        if(info.getNotice() == ApplyStatus.NOT_EXAMINE){
            Date examDate = info.getExamDate();
            String examStartTime = info.getExamStartTime();
            String examEndTime = info.getExamEndTime();
             weekNumber = info.getWeekNumber();
             weekDay = info.getWeekDay();
             classNode = info.getClassNode();
             actualCalendarId = info.getActualCalendarId();
            if (examDate == null || StringUtils.isBlank(examStartTime)
                    || StringUtils.isBlank(examEndTime) || info.getCalendarId() == null
                    || StringUtils.isBlank(info.getCourseCode()) || StringUtils.isBlank(info.getCampus())
                    || info.getExamType() == null || actualCalendarId == null) {
                throw new ParameterValidateException("入参有误");
            }

             examTime = GraduateExamTransTime.transTime(examDate, examStartTime, examEndTime, weekNumber, weekDay);
        }
        List<Long> list = new ArrayList<>();
        for (GraduateExamInfo graduateExamInfo : examInfo) {
            graduateExamInfo.setUpdateAt(new Date());
            graduateExamInfo.setExamStatus(ApplyStatus.PASS_INT);
            graduateExamInfo.setExamTime(examTime);
            //this.transTime(graduateExamInfo);
            if(graduateExamInfo.getNotice() == ApplyStatus.FINAL_EXAM){
                graduateExamInfo.setExamDate(null);
                graduateExamInfo.setExamEndTime("");
                graduateExamInfo.setExamStartTime("");
                graduateExamInfo.setWeekDay(null);
                graduateExamInfo.setWeekNumber(null);
                graduateExamInfo.setClassNode("");
                graduateExamInfo.setActualCalendarId(null);
            }

            this.checkPublicExamTimeSame(graduateExamInfo);

            Example example = new Example(GraduateExamInfo.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo("calendarId",graduateExamInfo.getCalendarId());
            criteria.andEqualTo("courseCode",graduateExamInfo.getCourseCode());
            criteria.andEqualTo("campus",graduateExamInfo.getCampus());
            criteria.andEqualTo("examType",graduateExamInfo.getExamType());
            criteria.andEqualTo("projId",graduateExamInfo.getProjId());
            GraduateExamInfo existInfo = examInfoDao.selectOneByExample(example);

            //（第一次入库）
            if(existInfo == null){
                graduateExamInfo.setExamRooms(ApplyStatus.NOT_EXAMINE);
                graduateExamInfo.setActualNumber(ApplyStatus.NOT_EXAMINE);
                examInfoDao.insert(graduateExamInfo);
            } else {
                //变更时间不一样需要先删除考场
                GraduateExamInfo item = examInfoDao.selectByPrimaryKey(existInfo.getId());
                if(!graduateExamInfo.getExamTime().equals(item.getExamTime())){
                    if(item.getExamRooms() != null && item.getExamRooms() > 0 ){
                        throw new ParameterValidateException("变更排考时间,请先删除考场,然后再次保存时间");
                    }
                }
                graduateExamInfo.setExamRooms(item.getExamRooms());
                graduateExamInfo.setActualNumber(item.getActualNumber());
                graduateExamInfo.setId(existInfo.getId());
                examInfoDao.updateByPrimaryKey(graduateExamInfo);
            }
            list.add(graduateExamInfo.getId());

        }
        ExamSaveTimeRebackDto dto = new ExamSaveTimeRebackDto();
        dto.setExamInfoIds(list);
        dto.setNotice(info.getNotice());
        dto.setWeekNumber(weekNumber);
        dto.setWeekDay(weekDay);
        dto.setClassNode(classNode);
        dto.setActualCalendarId(actualCalendarId);
        return dto;
    }

    //检验公共课相同课程时间必须一致
    private void checkPublicExamTimeSame(GraduateExamInfo graduateExamInfo) {
        List<GraduateExamInfo> info = examInfoDao.checkPublicExamTimeSame(graduateExamInfo);
        if(CollectionUtil.isNotEmpty(info)){
            GraduateExamInfo examInfo = info.get(0);
            if(info.size() == 1){
                    if(!examInfo.getCampus().equals(graduateExamInfo.getCampus())){
                        if(!graduateExamInfo.getExamTime().equals(examInfo.getExamTime())){
                            throw new ParameterValidateException("公共课课程代码"+examInfo.getCourseCode()+"所有校区排考时间必须一样"+examInfo.getExamTime());
                        }
                    }
            }else{

                if(!graduateExamInfo.getExamTime().equals(examInfo.getExamTime())){
                    throw new ParameterValidateException("公共课课程代码"+examInfo.getCourseCode()+"所有校区排考时间必须一样"+examInfo.getExamTime());
                }
            }
        }
    }

    @Override
    @Transactional
    public void insertRoom(List<GraduateExamRoom> room, String examInfoIds) {
        if (CollectionUtil.isEmpty(room) || StringUtils.isBlank(examInfoIds)) {
            throw new ParameterValidateException("入参有误");
        }
        for (GraduateExamRoom graduateExamRoom : room) {
            graduateExamRoom.setCreateAt(new Date());
            graduateExamRoom.setUpdateAt(new Date());
            graduateExamRoom.setRoomNumber(ApplyStatus.NOT_EXAMINE);
        }

        roomDao.insertList(room);

        List<GraduateExamInfoRoom> list = new ArrayList<>();
        List<String> ids = Arrays.asList(examInfoIds.split(","));
        for (String examInfoId : ids) {
            for (GraduateExamRoom examRoom : room) {
                GraduateExamInfoRoom infoRoom = new GraduateExamInfoRoom();
                infoRoom.setExamInfoId(Long.parseLong(examInfoId));
                infoRoom.setExamRoomId(examRoom.getId());
                list.add(infoRoom);
            }
        }
        infoRoomDao.insertList(list);


        for (String examInfoId : ids) {
            //更新考场数
            GraduateExamInfo examInfo = examInfoDao.selectByPrimaryKey(Long.parseLong(examInfoId));
            examInfo.setExamRooms(examInfo.getExamRooms() + room.size());
            examInfo.setUpdateAt(new Date());
            examInfoDao.updateByPrimaryKeySelective(examInfo);
        }
        //添加资源占用
        GraduateExamInfo examInfo = examInfoDao.selectByPrimaryKey(Long.valueOf(ids.get(0)));

        List<Long> collect = ids.stream().map(a -> Long.parseLong(a)).collect(Collectors.toList());
        List<SelectDto> course = examInfoDao.findCourse(collect);
        StringBuilder builder = new StringBuilder();
        if(CollectionUtil.isNotEmpty(course)){
            for (SelectDto selectDto : course) {
                builder.append(selectDto.getCourseName()).append("(").append(selectDto.getCourseCode()).append(")").append(",");
            }
        }
        String remark = "";
        if(builder.length()>0){
             remark = builder.substring(0, builder.length() - 1)+"排考";
        }

        OccupyUtils.addOccupy(room, examInfo,remark);

    }

    @Override
    public PageResult<GraduateExamRoomVo> listExamRoomByExamInfoId(PageCondition<GraduateExamRoomsQuery> condition) {
        GraduateExamRoomsQuery examRoom = condition.getCondition();
        if (CollectionUtil.isEmpty(examRoom.getExamInfoIds())) {
            return null;
        }
        PageHelper.startPage(condition.getPageNum_(), condition.getPageSize_());
        Page<GraduateExamRoomVo> page = roomDao.listExamRoomByExamInfoId(examRoom);
        return new PageResult<>(page);
    }

    @Override
    @Transactional
    public void insertTeacher(List<GraduateExamTeacher> teachers, Long examRoomId,String examInfoIds) {
        if (CollectionUtil.isEmpty(teachers) || examRoomId == null) {
            throw new ParameterValidateException("入参有误");
        }

        String dptId = SessionUtils.getCurrentSession().getCurrentManageDptId();

        GraduateExamRoom examRoom = roomDao.selectByPrimaryKey(examRoomId);
        Integer roomCapacity = examRoom.getRoomCapacity();
        List<GraduateExamMonitorTeacher> graduateExamMonitorTeacherList = monitorTeacherDao.checkTeacherNumber(roomCapacity, dptId);
        if(CollectionUtil.isEmpty(graduateExamMonitorTeacherList)){
            throw new ParameterValidateException("请联系管理员在监考老师设置页面设置监考老师人数");
        }
        GraduateExamMonitorTeacher monitorTeacher = graduateExamMonitorTeacherList.get(0);
        Integer teacherNumber = monitorTeacher.getTeacherNumber();

        GraduateExamTeacher te = new GraduateExamTeacher();
        te.setExamRoomId(examRoomId);
        List<GraduateExamTeacher> teacherList = teacherDao.select(te);
        if(CollectionUtil.isNotEmpty(teacherList)){
            if(teacherList.size() + teachers.size() > teacherNumber ){
                throw new ParameterValidateException("本考场已经设置"+teacherList.size()+"个监考老师,不能再次添加监考老师");
            }
        }

        if(teachers.size() != teacherNumber){
            throw new ParameterValidateException("本考场应该设置"+teacherNumber+"个监考老师");
        }


        for (GraduateExamTeacher teacher : teachers) {
            teacher.setCreateAt(new Date());
            teacher.setUpdateAt(new Date());
            teacher.setExamRoomId(examRoomId);
        }

        teacherDao.insertList(teachers);


        //添加资源占用
        GraduateExamInfo examInfo = examInfoDao.selectByPrimaryKey(Long.valueOf(examInfoIds.split(",")[0]));

        OccupyUtils.addTeaOccupy(teachers, examInfo);
    }

    @Override
    @Transactional
    public Restrict insertTeachingClass(GraduateExamRoomsQuery condition) {
        List<Long> examInfoIds = condition.getExamInfoIds();
        Long examRoomId = condition.getExamRoomId();
        List<Long> teachingClassIds = condition.getTeachingClassIds();
        if (CollectionUtil.isEmpty(teachingClassIds) || examRoomId == null
                || CollectionUtil.isEmpty(examInfoIds)) {
            throw new ParameterValidateException("入参有误");
        }
        //查找该教室容量
        GraduateExamRoom graduateExamRoom = roomDao.selectByPrimaryKey(examRoomId);
        if (graduateExamRoom == null || (graduateExamRoom.getRoomCapacity() - graduateExamRoom.getRoomNumber()) <= 0) {
            throw new ParameterValidateException("教室已满，请选择其他教室");
        }
        Long id = examInfoIds.get(0);
        GraduateExamInfo examInfo = examInfoDao.selectByPrimaryKey(id);
        Long calendarId = examInfo.getCalendarId();
        Integer examType = examInfo.getExamType();
        int size = graduateExamRoom.getRoomCapacity() - graduateExamRoom.getRoomNumber();
        //查找教学班下面已经剩余要排考的学生
        //获取该课程下的所有考场
        Page<GraduateExamRoomVo> graduateExamRoomVos = roomDao.listExamRoomByExamInfoId(condition);
        List<Long> examRoomIds = graduateExamRoomVos.getResult().stream().map(GraduateExamRoomVo::getId).collect(Collectors.toList());
        List<GraduateExamStudent> listStudent = studentDao.listStudentByClass(teachingClassIds, examRoomIds, calendarId,examType);
        if (CollectionUtil.isEmpty(listStudent)) {
            throw new ParameterValidateException("没有满足条件的可排考学生");
        } else {
            if (listStudent.size() <= size) {
                //删除教室下面的教学班，重新添加
                //学生入库，教室排考人数更新
                for (GraduateExamStudent graduateExamStudent : listStudent) {
                    graduateExamStudent.setExamRoomId(examRoomId);
                    graduateExamStudent.setExamSituation(ApplyStatus.PASS_INT);
                    graduateExamStudent.setCreateAt(new Date());
                    graduateExamStudent.setUpdateAt(new Date());
                }
                //排考学生校验
                Restrict restrict = this.checkExamConflict(listStudent);
                if(restrict != null){
                    Set<String> studentIds = restrict.getStudentIds();
                    if(CollectionUtil.isNotEmpty(studentIds)){
                        listStudent = listStudent.stream().filter(vo ->!studentIds.contains(vo.getStudentCode())).collect(Collectors.toList());
                    }
                }
                this.saveStudentAndUpdateNumber(listStudent);
                return restrict;
            } else {
                throw new ParameterValidateException("选择的班级排考已经超过了教室剩余容量，不能排考");
            }
        }

    }

    @Override
    @Transactional
    public Restrict insertStudent(List<NoExamStudent> examStudents, Long examRoomId) {
        if (CollectionUtil.isEmpty(examStudents) || examRoomId == null) {
            throw new ParameterValidateException("入参有误");
        }
        GraduateExamRoom graduateExamRoom = roomDao.selectByPrimaryKey(examRoomId);
        if (graduateExamRoom == null || (graduateExamRoom.getRoomCapacity() - graduateExamRoom.getRoomNumber()) <= 0) {
            throw new ParameterValidateException("教室已满，请选择其他教室");
        }
        int size = graduateExamRoom.getRoomCapacity() - graduateExamRoom.getRoomNumber();
        if (examStudents.size() > size) {
            throw new ParameterValidateException("排考学生超过教室容量，不能排考");
        }

        List<GraduateExamStudent> list = new ArrayList<>();
        for (NoExamStudent noExamStudent : examStudents) {
            GraduateExamStudent examStudent = new GraduateExamStudent();
            examStudent.setExamSituation(ApplyStatus.PASS_INT);
            examStudent.setCreateAt(new Date());
            examStudent.setUpdateAt(new Date());
            examStudent.setExamRoomId(examRoomId);
            examStudent.setExamInfoId(noExamStudent.getExamInfoId());
            examStudent.setTeachingClassId(noExamStudent.getTeachingClassId());
            examStudent.setTeachingClassCode(noExamStudent.getTeachingClassCode());
            examStudent.setTeachingClassName(noExamStudent.getTeachingClassName());
            examStudent.setStudentCode(noExamStudent.getStudentCode());
            list.add(examStudent);
        }
        //排考学生校验
        Restrict restrict = this.checkExamConflict(list);
        if(restrict != null){
            Set<String> studentIds = restrict.getStudentIds();
            if(CollectionUtil.isNotEmpty(studentIds)){
                list = list.stream().filter(vo ->!studentIds.contains(vo.getStudentCode())).collect(Collectors.toList());
            }
        }
        this.saveStudentAndUpdateNumber(list);
        return restrict;

    }

    @Override
    @Transactional
    public void cleanStudentByRoomId(List<Long> ids) {
        if (CollectionUtil.isEmpty(ids)) {
            throw new ParameterValidateException("入参有误");
        }
        //删除考场下的学生
        Example example = new Example(GraduateExamStudent.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("examRoomId", ids);
        criteria.andEqualTo("examSituation", ApplyStatus.PASS_INT);
        List<GraduateExamStudent> list = studentDao.selectByExample(example);
        // 排考日志 退考
        //更新对应课程总数
        this.updateActualNumber(list, ApplyStatus.EXAM_REDUCE);
        List<GraduateExamLog> logList = studentDao.selectStudentByExamRoomIds(ids);
        Session currentSession = SessionUtils.getCurrentSession();
        for (GraduateExamLog examLog : logList) {
            examLog.setCreateAt(new Date());
            examLog.setExamType(ApplyStatus.EXAM_LOG_NO);
            examLog.setOperatorCode(currentSession.realUid());
            examLog.setOperatorName(currentSession.realName());
            examLog.setIp(currentSession.getIp());
        }
        if (CollectionUtil.isNotEmpty(logList)) {
            logDao.insertList(logList);
        }
        //this.insertExamLog(list,ApplyStatus.EXAM_LOG_NO);

        studentDao.deleteByExample(example);
        //更新考场排考人数
        Example exampleRoom = new Example(GraduateExamRoom.class);
        Example.Criteria criteriaRoom = exampleRoom.createCriteria();
        criteriaRoom.andIn("id", ids);
        GraduateExamRoom examRoom = new GraduateExamRoom();
        examRoom.setRoomNumber(ApplyStatus.NOT_EXAMINE);
        roomDao.updateByExampleSelective(examRoom, exampleRoom);

    }

    @Override
    @Transactional
    public void deleteRoom(List<Long> ids, String examInfoIds) {
        if (CollectionUtil.isEmpty(ids)) {
            throw new ParameterValidateException("入参有误");
        }
        //获取删除考场排考信息
        //String infoId = OccupyUtils.sortBussniessId(examInfoIds);
        GraduateExamInfo examInfo = examInfoDao.selectByPrimaryKey(Long.valueOf(examInfoIds.split(",")[0]));

        //删除考场下的学生
        Example example = new Example(GraduateExamStudent.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("examRoomId", ids);
        criteria.andEqualTo("examSituation", ApplyStatus.PASS_INT);

        List<GraduateExamStudent> examStudents = studentDao.selectByExample(example);

        //更新对应课程总数
        this.updateActualNumber(examStudents, ApplyStatus.EXAM_REDUCE);
        // 排考日志 退考
        List<GraduateExamLog> logList = studentDao.selectStudentByExamRoomIds(ids);
        Session currentSession = SessionUtils.getCurrentSession();
        for (GraduateExamLog examLog : logList) {
            examLog.setCreateAt(new Date());
            examLog.setExamType(ApplyStatus.EXAM_LOG_NO);
            examLog.setOperatorCode(currentSession.realUid());
            examLog.setOperatorName(currentSession.realName());
            examLog.setIp(currentSession.getIp());
        }
        if (CollectionUtil.isNotEmpty(logList)) {
            logDao.insertList(logList);
        }
        //this.insertExamLog(examStudents,ApplyStatus.EXAM_LOG_NO);
        studentDao.deleteByExample(example);
        //删除监考老师,
        //todo 释放教师占用资源
        Example exampleTeacher = new Example(GraduateExamTeacher.class);
        Example.Criteria criteriaTeacher = exampleTeacher.createCriteria();
        criteriaTeacher.andIn("examRoomId", ids);
        teacherDao.deleteByExample(exampleTeacher);
        //删除考场
        //todo 释放教室占用资源
        Example exampleRoom = new Example(GraduateExamRoom.class);
        Example.Criteria criteriaRoom = exampleRoom.createCriteria();
        criteriaRoom.andIn("id", ids);
        roomDao.deleteByExample(exampleRoom);

        //更新考场数
        List<ExamInfoRoomDto> list = examInfoDao.listExamInfos(ids);
        if (CollectionUtil.isNotEmpty(list)) {
            examInfoDao.updateExamInfoExamRooms(list);
        }

        //删除exam_Info_room中间表
        Example infoRoom = new Example(GraduateExamInfoRoom.class);
        Example.Criteria infoRoomCriteria = infoRoom.createCriteria();
        infoRoomCriteria.andIn("examRoomId", ids);
        infoRoomDao.deleteByExample(infoRoom);

        // 删除教室占用
        Long bussniessId = ids.get(0);
        OccupyUtils.delOccupy(bussniessId, examInfo);

    }

    @Override
    public List<ExamStudent> listExamStudentById(Long id) {
        if (id == null) {
            throw new ParameterValidateException("入参有误");
        }
        List<ExamStudent> list = studentDao.listExamStudentById(id);
        return list;
    }

    @Override
    public PageResult<TeachingClassDto> listTeachingClass(PageCondition<GraduateExamRoomsQuery> teachingClassQuery) {
        GraduateExamRoomsQuery classQuery = teachingClassQuery.getCondition();
        Long examRoomId = classQuery.getExamRoomId();
        List<Long> examInfoIds = classQuery.getExamInfoIds();
        if (CollectionUtil.isEmpty(examInfoIds) || examRoomId == null) {
            throw new ParameterValidateException("入参有误");
        }
        Long id = examInfoIds.get(0);
        Long calendarId = examInfoDao.selectByPrimaryKey(id).getCalendarId();
        classQuery.setMode((int) (calendarId % 6));
        PageHelper.startPage(teachingClassQuery.getPageNum_(), teachingClassQuery.getPageSize_());
        Page<TeachingClassDto> page = studentDao.listTeachingClass(classQuery);
        if (CollectionUtil.isNotEmpty(page)) {
            List<TeachingClassDto> teachingClassDtos = page.getResult();

            Example example = new Example(GraduateExamStudent.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andIn("examInfoId", examInfoIds);
            criteria.andEqualTo("examSituation", ApplyStatus.EXAM_SITUATION_NORMAL);
            List<GraduateExamStudent> list = studentDao.selectByExample(example);
            if (CollectionUtil.isNotEmpty(list)) {
                Map<Long, List<GraduateExamStudent>> listMap = list.stream().filter(vo -> vo.getTeachingClassId() != null).collect(Collectors.groupingBy(GraduateExamStudent::getTeachingClassId));
                for (TeachingClassDto teachingClassDto : teachingClassDtos) {
                    Long teachingClassId = teachingClassDto.getTeachingClassId();
                    List<GraduateExamStudent> studentList = listMap.get(teachingClassId);
                    int examRoomNumber = 0;
                    int examNumber = 0;
                    if (CollectionUtil.isNotEmpty(studentList)) {
                        examNumber = studentList.size();
                        examRoomNumber = studentList.stream().filter(vo -> vo.getExamRoomId().equals(examRoomId)).collect(Collectors.toList()).size();
                    }
                    teachingClassDto.setNoExamNumber(teachingClassDto.getTotalNumber() - examNumber);
                    teachingClassDto.setExamNumber(examNumber);
                    teachingClassDto.setExamRoomNumber(examRoomNumber);
                }
            }else{
                for (TeachingClassDto teachingClassDto : teachingClassDtos) {
                    teachingClassDto.setNoExamNumber(teachingClassDto.getTotalNumber());
                    teachingClassDto.setExamNumber(0);
                    teachingClassDto.setExamRoomNumber(0);
                }
            }
        }
        return new PageResult<>(page);
    }

    @Override
    public PageResult<NoExamStudent> listStudent(PageCondition<StudentQuery> condition) {
        StudentQuery studentQuery = condition.getCondition();
        if (studentQuery.getExamType() == null || CollectionUtil.isEmpty(studentQuery.getExamInfoIds())
                || studentQuery.getCalendarId() == null) {
            throw new ParameterValidateException("入参有误");
        }
        studentQuery.setMode((int) (studentQuery.getCalendarId() % 6));
        GraduateExamRoomsQuery query = new GraduateExamRoomsQuery();
        query.setExamInfoIds(studentQuery.getExamInfoIds());
        Page<GraduateExamRoomVo> graduateExamRoomVos = roomDao.listExamRoomByExamInfoId(query);
        List<Long> examRoomIds = graduateExamRoomVos.getResult().stream().map(GraduateExamRoomVo::getId).collect(Collectors.toList());
        studentQuery.setExamRoomIds(examRoomIds);
        PageHelper.startPage(condition.getPageNum_(), condition.getPageSize_());
        if (studentQuery.getExamType().equals(ApplyStatus.FINAL_EXAM)) {
            Page<NoExamStudent> page = studentDao.listStudent(studentQuery);
            return new PageResult<>(page);
        }
        Page<NoExamStudent> page = studentDao.listMakeUpStudent(studentQuery);
        return new PageResult<>(page);

    }

    @Override
    public ExcelResult export(GraduateExamInfoVo condition) {
        ExcelResult excelResult = ExportExcelUtils.submitTask("listExamInfo", new ExcelExecuter() {
            @Override
            public GeneralExcelDesigner getExcelDesigner() {
                PageCondition<GraduateExamInfoVo> pageCondition = new PageCondition<>();
                pageCondition.setCondition(condition);
                pageCondition.setPageNum_(0);
                pageCondition.setPageSize_(0);
                PageResult<GraduateExamInfoVo> pageResult = listGraduateExamInfo(pageCondition);
                List<GraduateExamInfoVo> list = pageResult.getList();
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
                design.addCell("校区", "campus");
                design.addCell("开课学院", "faculty");
                design.addCell("课程性质", "nature");
                design.addCell("课程层次", "trainingLevel");
                design.addCell("学分", "credits");
                design.addCell("考场数", "examRooms");
                design.addCell("应考人数", "totalNumber");
                design.addCell("实考人数", "actualNumber");
                design.addCell("考试时间", "examTime");
//                design.addCell("排考状态", "examStatus").setValueHandler(new CellValueHandler() {
//                    @Override
//                    public String handler(String s, Object o, GeneralExcelCell generalExcelCell) {
//                        if ("1".equals(s)) {
//                            return "已排考";
//                        } else if ("2".equals(s)) {
//                            return "已发布";
//                        } else {
//                            return "未排考";
//                        }
//                    }
//                });
                return design;
            }
        });
        return excelResult;
    }

    private void saveStudentAndUpdateNumber(List<GraduateExamStudent> listStudent) {
        if (CollectionUtil.isNotEmpty(listStudent)) {
            //学生入库
            studentDao.insertList(listStudent);
            //更新人数
            Long examRoomId = listStudent.get(0).getExamRoomId();
            int size = listStudent.size();
            GraduateExamRoom graduateExamRoom = roomDao.selectByPrimaryKey(examRoomId);
            graduateExamRoom.setRoomNumber(graduateExamRoom.getRoomNumber() + size);
            roomDao.updateByPrimaryKeySelective(graduateExamRoom);

            //排考日志 排考
            List<GraduateExamLog> list = new ArrayList<>();
            Session currentSession = SessionUtils.getCurrentSession();
            for (GraduateExamStudent examStudent : listStudent) {
                GraduateExamLog examLog = new GraduateExamLog();
                examLog.setCreateAt(new Date());
                examLog.setStudentCode(examStudent.getStudentCode());
                examLog.setExamInfoId(examStudent.getExamInfoId());
                examLog.setRoomId(graduateExamRoom.getRoomId());
                examLog.setRoomName(graduateExamRoom.getRoomName());
                examLog.setExamType(ApplyStatus.EXAM_LOG_YES);
                examLog.setOperatorCode(currentSession.realUid());
                examLog.setOperatorName(currentSession.realName());
                examLog.setIp(currentSession.getIp());
                list.add(examLog);
            }
            logDao.insertList(list);
            //this.insertExamLog(listStudent,ApplyStatus.EXAM_LOG_YES);
            //更新对应课程已排考人数（由于有合考，不能直接统计课程对应下的考场人数）
            this.updateActualNumber(listStudent, ApplyStatus.EXAM_ADD);


        }
    }

    @Override
    @Transactional
    public void updateActualNumber(List<GraduateExamStudent> listStudent, Integer symbol) {
        Map<Long, List<GraduateExamStudent>> map = listStudent.stream().collect(Collectors.groupingBy(GraduateExamStudent::getExamInfoId));
        List<ExamInfoRoomDto> list = new ArrayList<>();
        for (Long examInfoId : map.keySet()) {
            ExamInfoRoomDto roomDto = new ExamInfoRoomDto();
            roomDto.setExamInfoId(examInfoId);
            roomDto.setActualNumber(map.get(examInfoId).size());
            list.add(roomDto);
        }
        if (CollectionUtil.isNotEmpty(list)) {
            examInfoDao.updateActualNumber(list, symbol);
        }
    }

    @Override
    @Transactional
    public String autoAllocationExamRoom(GraduateExamAutoStudent roomsQuery) {
        Integer examType = roomsQuery.getExamType();
        List<Long> examInfoIds = roomsQuery.getExamInfoIds();
        List<Long> examRoomIds = roomsQuery.getExamRoomIds();
        if(CollectionUtil.isEmpty(examInfoIds) || examType == null){
            return "入参有误";
        }
        if (CollectionUtil.isEmpty(examRoomIds)) {
            examRoomIds = roomDao.listRoomsByExamInfoIds(examInfoIds);
        }

        Long id = examInfoIds.get(0);
        String campus = examInfoDao.selectByPrimaryKey(id).getCampus();

        //通过ExamRoomId查询未满的考场
        if (CollectionUtil.isEmpty(examRoomIds)) {
            return "当前课程没有按排考场,请先按排考场";
        }
        List<GraduateExamRoom> autoExamRoom = roomDao.findSurplusRoom(examRoomIds);
        //通过examInfoIds查询未排考的学生
        StudentQuery query = new StudentQuery();
        query.setExamInfoIds(examInfoIds);
        query.setCampus(campus);
        Page<NoExamStudent> studentList = new Page<>();
        if (examType.equals(ApplyStatus.FINAL_EXAM)) {
            query.setMode((int) (roomsQuery.getCalendarId() % 6));
            studentList = studentDao.listStudent(query);
        } else {
            studentList = studentDao.listMakeUpStudent(query);
        }
        if (CollectionUtil.isEmpty(studentList)) {
            return "当前课程没有可排考的考生";
        }
        //自动分配
        List<NoExamStudent> noExamStudents = studentList.getResult();
        //已经分配的学生
        List<GraduateExamStudentVo> matchList = new ArrayList<>();
        //排考校验
        List<GraduateExamStudent> checkList = new ArrayList<>();
        //未分配学生
        List<String> noMatchList = new ArrayList<>();
        Session currentSession = SessionUtils.getCurrentSession();

        //校验冲突学生
        Restrict restrict = new Restrict();
        noExamStudents.forEach(item ->{
            GraduateExamStudent examStudent = new GraduateExamStudent();
            examStudent.setStudentCode(item.getStudentCode());
            examStudent.setExamInfoId(item.getExamInfoId());
            checkList.add(examStudent);
        });

        //排考校验
        restrict = this.checkExamStudentsConflict(checkList);
        if(restrict != null){
            Set<String> studentIds = restrict.getStudentIds();
            if(CollectionUtil.isNotEmpty(studentIds)){
                noMatchList.addAll(studentIds);
                noExamStudents = noExamStudents.stream().filter(vo ->!studentIds.contains(vo.getStudentCode())).collect(Collectors.toList());
            }
        }
        noExamStudents.forEach(noExamStudent -> {
            int j = 0;
            for (int i = 0; i < autoExamRoom.size(); i++) {
                int num = roomDao.checkNum(autoExamRoom.get(i).getId());
                if (num > 0) {
                    GraduateExamStudentVo examStudentVo = new GraduateExamStudentVo();
                    examStudentVo.setRoomId(autoExamRoom.get(i).getRoomId());
                    examStudentVo.setRoomName(autoExamRoom.get(i).getRoomName());
                    examStudentVo.setExamRoomId(autoExamRoom.get(i).getId());
                    examStudentVo.setStudentCode(noExamStudent.getStudentCode());
                    examStudentVo.setExamSituation(ApplyStatus.EXAM_SITUATION_NORMAL);
                    examStudentVo.setTeachingClassId(noExamStudent.getTeachingClassId());
                    examStudentVo.setTeachingClassCode(noExamStudent.getTeachingClassCode());
                    examStudentVo.setTeachingClassName(noExamStudent.getTeachingClassName());
                    examStudentVo.setCreateAt(new Date());
                    examStudentVo.setExamInfoId(noExamStudent.getExamInfoId());
                    examStudentVo.setIp(currentSession.getIp());
                    examStudentVo.setExamType(ApplyStatus.EXAM_LOG_YES);
                    examStudentVo.setOperatorCode(currentSession.realUid());
                    examStudentVo.setOperatorName(currentSession.realName());
                    matchList.add(examStudentVo);
                    j++;
                    break;
                }
            }

            if (j < 1) {
                noMatchList.add(noExamStudent.getStudentCode());
            }

        });

        if(CollectionUtil.isNotEmpty(matchList)){
            //入库
            studentDao.insertBatchs(matchList);
            //排考日志
            logDao.insertBatchs(matchList);
            //更新总人数
            Map<Long, List<GraduateExamStudentVo>> map = matchList.stream().collect(Collectors.groupingBy(GraduateExamStudentVo::getExamInfoId));
            List<ExamInfoRoomDto> list = new ArrayList<>();
            for (Long examInfoId : map.keySet()) {
                ExamInfoRoomDto dto = new ExamInfoRoomDto();
                dto.setExamInfoId(examInfoId);
                dto.setActualNumber(map.get(examInfoId).size());
                list.add(dto);
            }
            examInfoDao.updateActualNumber(list, ApplyStatus.EXAM_ADD);
        }


        String msg = "";
        if(StringUtils.isNotBlank(restrict.getDescript())){
            msg = restrict.getDescript();
        }
        return "成功分配" + matchList.size() + "学生" + "还有" + noMatchList.size() + "学生未分配。" + msg;
    }

    @Override
    public GraduateExamStudentNumber getExamStudentNumber(GraduateExamStudentNumber studentNumber) {
        if (CollectionUtil.isEmpty(studentNumber.getExamInfoIds())) {
            throw new ParameterValidateException("入参有误");
        }
        GraduateExamStudentNumber examStudentNumber = examInfoDao.getExamInfoNumber(studentNumber.getExamInfoIds());
        if (studentNumber.getExamRoomId() != null) {
            GraduateExamRoom examRoom = roomDao.getExamRoomNumber(studentNumber.getExamRoomId());
            examStudentNumber.setRoomCapacity(examRoom.getRoomCapacity());
            examStudentNumber.setRoomNumber(examRoom.getRoomNumber());
        }
        return examStudentNumber;
    }

    @Override
    public Restrict checkExamStudentsConflict(List<GraduateExamStudent> list) {
        Restrict restrict = new Restrict();
        if(CollectionUtil.isNotEmpty(list)){
            GraduateExamStudent graduateExamStudent = list.get(0);
            Long examInfoId = graduateExamStudent.getExamInfoId();
            GraduateExamInfo examInfo = examInfoDao.selectByPrimaryKey(examInfoId);
            if(examInfo != null) {
                List<ExamStudentInfoAndDate> studentList = studentDao.listStudentInfo(list, examInfo.getCalendarId());
                if (CollectionUtil.isNotEmpty(studentList)) {
                    Map<Long, List<ExamStudentInfoAndDate>> listMap = studentList.stream().collect(Collectors.groupingBy(ExamStudentInfoAndDate::getExamInfoId));
                    StringBuilder descriptBuilder = new StringBuilder();
                    Set<String> repeatStudent = new HashSet<>();
                    for (Long infoId : listMap.keySet()) {
                        List<ExamStudentInfoAndDate> infoAndDates = listMap.get(infoId);
                        ExamStudentInfoAndDate date = infoAndDates.get(0);
                        Long id = this.checkTime(examInfo, date);
                        if (id != null) {
                            String courseCode = date.getCourseCode();
                            String courseName = date.getCourseName();
                            String campus = dictionaryService.query("X_XQ", date.getCampus(), SessionUtils.getLang());
                            List<String> studentIds = infoAndDates.stream().map(ExamStudentInfoAndDate::getStudentCode).collect(Collectors.toList());
                            repeatStudent.addAll(studentIds);
                            descriptBuilder.append(String.format("排考学生: %s,冲突课程: %s(%s)(%s),冲突时间: %s;",
                                    studentIds.toString(), courseCode, courseName,campus,date.getExamTime()));
                        }
                    }
                    if (descriptBuilder.length() > 0) {
                        restrict.setStudentIds(repeatStudent);
                        restrict.setDescript(descriptBuilder.toString());
                    }
                }
            }
        }
        return restrict;
    }

    //返回冲突的Id
    private Long checkTime(GraduateExamInfo examInfo, ExamStudentInfoAndDate date) {
        Date examDate = examInfo.getExamDate();
        Date examDateYes = date.getExamDate();

        SimpleDateFormat formatParse = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String examDateStr = formatParse.format(examDate);
        String examDateYesStr = formatParse.format(examDateYes);

        //同一天,比较冲突
        if(examDateStr.equals(examDateYesStr)){
            String examStartTime = examInfo.getExamStartTime();
            String examEndTime = examInfo.getExamEndTime();

            String examStartTimeYes = date.getExamStartTime();
            String examndTimeYes = date.getExamEndTime();

            String dateStr = examDateStr.substring(0, 10);
            String examDateStart = dateStr + " " + examStartTime + ":00";
            String examDateEnd = dateStr + " " + examEndTime + ":00";

            String examDateStartYes = dateStr + " " + examStartTimeYes + ":00";
            String examDateEndYes = dateStr + " " + examndTimeYes + ":00";

            try {
                long startTime = formatParse.parse(examDateStart).getTime();
                long endTime = formatParse.parse(examDateEnd).getTime();
                long startTimeYes = formatParse.parse(examDateStartYes).getTime();
                long endTimeYes = formatParse.parse(examDateEndYes).getTime();

                if( (startTime <= startTimeYes && endTime >= startTimeYes ) || ( startTimeYes <= startTime && startTime <= endTimeYes ) ){
                   return date.getExamInfoId();
                }else{
                    if(!examInfo.getCampus().equals(date.getCampus())){
                        if(endTime < startTimeYes && ((startTimeYes - endTime)/1000/60/60 < 2) ){
                            return date.getExamInfoId();
                        }else if( startTime > endTimeYes && ((startTime - endTimeYes )/1000/60/60 < 2) ){
                            return date.getExamInfoId();
                        }

                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    private void checkTimeAndFaculty(GraduateExamInfoVo vo) {
        Date date = new Date(System.currentTimeMillis());
        Example example = new Example(GraduateExamAuth.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("calendarId", vo.getCalendarId());
        criteria.andEqualTo("examType", vo.getExamType());
        criteria.andLessThanOrEqualTo("beginTime", date);
        criteria.andGreaterThanOrEqualTo("endTime", date);
        criteria.andEqualTo("projId", vo.getProjId());
        criteria.andEqualTo("deleteStatus", DeleteStatus.NOT_DELETE);
        GraduateExamAuth auth = authDao.selectOneByExample(example);
        if (auth == null) {
            throw new ParameterValidateException("当前时间段，您还没有权限进行排考");
        } else {
            //有权限的学院(交集)
            String facultyAuths = auth.getOpenCollege();
            List<String> existList = Arrays.asList(facultyAuths.split(","));
            if (CollectionUtil.isEmpty(vo.getFacultys())) {
                vo.setFacultys(existList);
            } else {
                vo.getFacultys().retainAll(existList);
            }
            if (CollectionUtil.isEmpty(vo.getFacultys())) {
                throw new ParameterValidateException("当前时间段，您还没有权限进行排考");
            }
            String[] courseNature = auth.getCourseNature().split(",");
            List<String> courseNatures = Arrays.asList(courseNature);
            String[] trainingLevel = auth.getTrainingLevel().split(",");
            List<String> trainingLevels = Arrays.asList(trainingLevel);
            vo.setCourseNatures(courseNatures);
            vo.setTrainingLevels(trainingLevels);
        }
    }

    private void transTime(GraduateExamInfo examInfo) {
        Date examDate = examInfo.getExamDate();
        String examStartTime = examInfo.getExamStartTime();
        String examEndTime = examInfo.getExamEndTime();
        SimpleDateFormat formatParse = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String dateStr = formatParse.format(examDate);
        String substring = dateStr.substring(0, 10);
        String dateStart = substring + " " + examStartTime + ":00";
        String dateEnd = substring + " " + examEndTime + ":00";
        Long startTime = 0L;
        Long endTime = 0L;
        try {
            startTime = formatParse.parse(dateStart).getTime();
            endTime = formatParse.parse(dateEnd).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        SchCalendarTimeVo schCalendarTimeVo = BaseresServiceExamInvoker.getTime(startTime, endTime);
        if (schCalendarTimeVo != null) {
            Integer weekNum = schCalendarTimeVo.getRealWeek();
            Integer weekDay = schCalendarTimeVo.getRealWeekDay();
            List<WorkTime> workTimeList = schCalendarTimeVo.getWorkTimeList();
            if (CollectionUtil.isNotEmpty(workTimeList)) {
                List<String> classNodes = workTimeList.stream().map(WorkTime::getClassNode).collect(Collectors.toList());
                String nodesStr = StringUtils.join(classNodes, ",");
                examInfo.setWeekNumber(weekNum);
                examInfo.setWeekDay(weekDay);
                examInfo.setClassNode(nodesStr);
            }
        }
    }

    @Override
    public void insertExamLog(List<GraduateExamStudent> listStudent, Integer examType) {
        List<GraduateExamLog> list = new ArrayList<>();
        for (GraduateExamStudent examStudent : listStudent) {
            GraduateExamLog examLog = new GraduateExamLog();
            examLog.setCreateAt(new Date());
            examLog.setStudentCode(examStudent.getStudentCode());
            examLog.setExamInfoId(examStudent.getExamInfoId());
            examLog.setExamType(examType);
            list.add(examLog);
        }

        if (CollectionUtil.isNotEmpty(list)) {
            logDao.insertList(list);
        }
    }


    private Restrict checkExamConflict(List<GraduateExamStudent> list){
        //排考学生校验
        Restrict restrict = this.checkExamStudentsConflict(list);
        return restrict;
    }
}
