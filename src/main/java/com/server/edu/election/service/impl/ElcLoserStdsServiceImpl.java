package com.server.edu.election.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.server.edu.common.PageCondition;
import com.server.edu.common.entity.StudentScore;
import com.server.edu.common.locale.I18nUtil;
import com.server.edu.common.rest.PageResult;
import com.server.edu.common.rest.RestResult;
import com.server.edu.common.vo.SchoolCalendarVo;
import com.server.edu.dictionary.service.DictionaryService;
import com.server.edu.election.dao.ElcCourseTakeDao;
import com.server.edu.election.dao.ElcLoserStdsDao;
import com.server.edu.election.dao.ElectionConstantsDao;
import com.server.edu.election.dto.LoserStuElcCourse;
import com.server.edu.election.entity.ElcCourseTake;
import com.server.edu.election.entity.ElcLoserStds;
import com.server.edu.election.rpc.BaseresServiceInvoker;
import com.server.edu.election.rpc.ScoreServiceInvoker;
import com.server.edu.election.service.ElcCourseTakeService;
import com.server.edu.election.service.ElcLoserStdsService;
import com.server.edu.election.vo.ElcLoserStdsVo;
import com.server.edu.session.util.SessionUtils;
import com.server.edu.util.CollectionUtil;
import com.server.edu.util.async.AsyncExecuter;
import com.server.edu.util.async.AsyncProcessUtil;
import com.server.edu.util.async.AsyncResult;
import com.server.edu.util.excel.GeneralExcelDesigner;
import com.server.edu.util.excel.export.ExcelExecuter;
import com.server.edu.util.excel.export.ExcelResult;
import com.server.edu.util.excel.export.ExportExcelUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;

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
    private ElectionConstantsDao constantsDao;

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

    /**
    *@Description: 刷新预警名单
    *@Param: 
    *@return:
    *@Author: bear
    *@date: 2019/5/13 9:53
    */
    @Override
    public AsyncResult reLoadLoserStu(Long calendarId,String deptId) {
        AsyncResult resul= AsyncProcessUtil.submitTask("reloadLoserStu",new AsyncExecuter() {
            @Override
            public void execute() {
                queryReloadLoserStu(calendarId,deptId);
            }
        });
        return resul;
    }

    @Override
    @Transactional
    public void queryReloadLoserStu(Long calendarId,String deptId) {
        //首先删除当前学期的所有预警学生
        stdsDao.deleteByCalendarId(calendarId);
        //预警学生不及格学分上限
        String maxFailCredits = constantsDao.findMaxFailCredits();
        double maxCredits = Double.parseDouble(maxFailCredits);
        //查询所有不及格学生成绩
        PageCondition<String> condition=new PageCondition<>();
        condition.setCondition(deptId);
        condition.setPageNum_(1);
        condition.setPageSize_(5000);
        PageResult<StudentScore> scores = ScoreServiceInvoker.findUnPassStuScore(condition);
        List<StudentScore> unPassScoresList=new ArrayList<>();
        if(CollectionUtil.isNotEmpty(scores.getList())){
            long total_ = scores.getTotal_();
            long pageTotal = total_ % 5000 == 0 ? total_ / 5000 : total_ /5000 + 1;
            List<StudentScore> scoreList = scores.getList();
            unPassScoresList.addAll(scoreList);
            int pageNum=2;
            while(pageNum<=pageTotal){
                condition.setCondition(deptId);
                condition.setPageNum_(pageNum);
                condition.setPageSize_(5000);
                PageResult<StudentScore> unPassStu = ScoreServiceInvoker.findUnPassStuScore(condition);
                unPassScoresList.addAll(unPassStu.getList());
                pageNum++;
            }
        }
        //不及格学生中还要去除部分学生中该不及格课程中有替代课程，且替代课程及格todo

        List<StudentScore> unPassScore = findUnPassScore(unPassScoresList);
        //计算学生不及格的总学分
        List<ElcLoserStds> loserStu=new ArrayList<>();
        if(CollectionUtil.isNotEmpty(unPassScore)){
            Map<String, List<StudentScore>> map = unPassScore.stream().collect(Collectors.groupingBy(StudentScore::getStudentId));
            for (String s : map.keySet()) {
                double unpassCredits=0L;
                List<StudentScore> scoreList = map.get(s);
                unpassCredits = scoreList.stream().filter(vo -> vo.getCredit() != null).mapToDouble(StudentScore::getCredit).sum();
                if(unpassCredits>=maxCredits){//预警学生
                    ElcLoserStds stu=new ElcLoserStds();
                    stu.setStudentId(s);
                    stu.setCalendarId(calendarId);
                    stu.setUnpassedCredits(unpassCredits);
                    loserStu.add(stu);
                }
            }
        }

        if(CollectionUtil.isNotEmpty(loserStu)){
            stdsDao.insertLoserStu(loserStu);
        }

    }

    public List<StudentScore> findUnPassScore(List<StudentScore> list){
        //不及格中过滤掉已经补考及格的课程
        List<StudentScore> unPassList=new ArrayList<>();//去除重复不及格的课程成绩
        List<StudentScore> unPass=new ArrayList<>();
        if(CollectionUtil.isNotEmpty(list)){
            Map<String, List<StudentScore>> collect = list.stream().collect(Collectors.groupingBy(ElcLoserStdsServiceImpl::groupByKey));
            // stuId_courseCode
            for (String s : collect.keySet()) {
                StudentScore studentScore = collect.get(s).get(0);//不及格课程
                unPassList.add(studentScore);
            }
        }

        if(CollectionUtil.isNotEmpty(unPassList)){
            Map<String, List<StudentScore>> unPassCollect = unPassList.stream().collect(Collectors.groupingBy(ElcLoserStdsServiceImpl::groupByKey));
            //查询不及格中是否补考及格的成绩
            RestResult<List<StudentScore>> passStuScore = ScoreServiceInvoker.findPassStuScore(unPassList);
            List<StudentScore> passList = passStuScore.getData();
            if(CollectionUtil.isNotEmpty(passList)){
                Map<String, List<StudentScore>> collect = passList.stream().collect(Collectors.groupingBy(ElcLoserStdsServiceImpl::groupByKey));
                for (String s : unPassCollect.keySet()) {
                    if(!collect.keySet().contains(s)){
                        unPass.addAll(unPassCollect.get(s));
                    }
                }
                return unPass;
            }

        }
        return unPassList;
    }

    static String groupByKey(StudentScore sc){
        return sc.getStudentId()+"_"+sc.getCourseCode();
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
