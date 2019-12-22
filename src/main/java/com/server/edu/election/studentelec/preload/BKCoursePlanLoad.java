package com.server.edu.election.studentelec.preload;

import java.util.*;
import java.util.stream.Collectors;

import com.server.edu.common.entity.BkPublicCourse;
import com.server.edu.common.entity.BkPublicCourseVo;
import com.server.edu.common.entity.PublicCourse;
import com.server.edu.common.locale.I18nUtil;
import com.server.edu.common.vo.ScoreStudentResultVo;
import com.server.edu.dictionary.service.DictionaryService;
import com.server.edu.election.constants.Constants;
import com.server.edu.election.dao.ElcStuCouLevelDao;
import com.server.edu.election.entity.ElcStuCouLevel;
import com.server.edu.election.entity.ElectionRounds;
import com.server.edu.election.studentelec.context.ElecRequest;
import com.server.edu.election.studentelec.context.ElecRespose;
import com.server.edu.election.studentelec.context.bk.TsCourse;
import com.server.edu.election.studentelec.service.cache.TeachClassCacheService;
import com.server.edu.election.studentelec.service.impl.RoundDataProvider;
import com.server.edu.election.studentelec.utils.ElecContextUtil;
import com.server.edu.exception.ParameterValidateException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.server.edu.common.dto.CultureRuleDto;
import com.server.edu.common.dto.PlanCourseDto;
import com.server.edu.common.dto.PlanCourseTypeDto;
import com.server.edu.election.dto.StudentScoreDto;
import com.server.edu.election.rpc.CultureSerivceInvoker;
import com.server.edu.election.service.BkStudentScoreService;
import com.server.edu.election.studentelec.cache.StudentInfoCache;
import com.server.edu.election.studentelec.cache.TeachingClassCache;
import com.server.edu.election.studentelec.context.CourseGroup;
import com.server.edu.election.studentelec.context.ElecCourse;
import com.server.edu.election.studentelec.context.bk.ElecContextBk;
import com.server.edu.election.studentelec.context.bk.PlanCourse;
import com.server.edu.election.util.CourseCalendarNameUtil;
import com.server.edu.election.vo.ElectionRuleVo;
import com.server.edu.util.CollectionUtil;
import tk.mybatis.mapper.entity.Example;

/**
 * 本科生培养计划课程查询
 * 
 */
@Component
public class BKCoursePlanLoad extends DataProLoad<ElecContextBk>
{
    Logger log = LoggerFactory.getLogger(getClass());
    

    @Autowired
    private DictionaryService dictionaryService;

    @Autowired
    private ElcStuCouLevelDao couLevelDao;

    @Autowired
    private RoundDataProvider dataProvider;

    @Autowired
    private TeachClassCacheService teachClassCacheService;
    
    @Autowired
    private BkStudentScoreService bkStudentScoreService;

    @Override
    public int getOrder()
    {
        return 2;
    }
    
    @Override
    public String getProjectIds()
    {
        return "1";
    }

    @Override
    public void load(ElecContextBk context)
    {
        StudentInfoCache stu = context.getStudentInfo();
        List<PlanCourseDto> courseType = CultureSerivceInvoker.findUnGraduateCourse(stu.getStudentId());
        List<PlanCourseDto> planCourseType = CultureSerivceInvoker.findBKPlanCourseCourse(stu.getStudentId());
//        Map<String, String> map = new HashMap<>(60);
        StudentScoreDto dto = new StudentScoreDto();
        dto.setStudentId(context.getStudentInfo().getStudentId());
        List<ScoreStudentResultVo> stuScore = bkStudentScoreService.getStudentScoreList(dto);
        List<String> selectedCourse = new ArrayList<>();
        if(CollectionUtil.isNotEmpty(stuScore)) {
            selectedCourse = stuScore.stream().map(ScoreStudentResultVo::getCourseCode).collect(Collectors.toList());
        }
        ElecRequest request = context.getRequest();
        Long roundId = request.getRoundId();
        Long calendarId = request.getCalendarId();
        if(CollectionUtil.isNotEmpty(courseType)){
            log.info("plan course size:{}", courseType.size());
            Set<PlanCourse> planCourses = context.getPlanCourses();//培养课程
            Set<CourseGroup> courseGroups = context.getCourseGroups();//课程组学分限制
            for (PlanCourseDto planCourse : courseType) {
                List<PlanCourseTypeDto> list = planCourse.getList();
                CultureRuleDto rule = planCourse.getRule();
                Long labelId = planCourse.getLabel();
                String labelName = planCourse.getLabelName();
                if(CollectionUtil.isNotEmpty(list)){
                    for (PlanCourseTypeDto pct : list) {//培养课程
                        String courseCode = pct.getCourseCode();
                        if(StringUtils.isBlank(courseCode) ||(CollectionUtil.isNotEmpty(selectedCourse) && selectedCourse.contains(courseCode)) ) {
                            log.warn("courseCode is Blank skip this record: {}", JSON.toJSONString(pct));
                            continue;
                        }
                        boolean isEnglishFlag = checkCultureEnglish(stu.getStudentId(), courseCode);
                        if(!isEnglishFlag){
                            log.warn("The course({}) is not a course that he can learn at his level", JSON.toJSONString(pct));
                            continue;
                        }
                        PlanCourse pl=new PlanCourse();
//                        Example example = new Example(Course.class);
//                        example.createCriteria().andEqualTo("code", courseCode);
//                        Course course = courseDao.selectOneByExample(example);
//                        List<TeachingClassCache> teachingClassCaches =teachClassCacheService.getTeachClasss(roundId, courseCode);
                        List<TeachingClassCache> teachingClassCaches = teachClassCacheService.getTeachClasssBycalendarId(calendarId, courseCode);
                        if (CollectionUtil.isNotEmpty(teachingClassCaches)) {
                        	TeachingClassCache teachingClassCache = teachingClassCaches.get(0);
                        	ElecCourse course2 = new ElecCourse();
                            course2.setCourseCode(courseCode);
                            course2.setCourseName(pct.getName());
                            course2.setNameEn(pct.getNameEn());
                            course2.setNature(teachingClassCache.getNature());
                            course2.setCredits(pct.getCredits());
                            String calendarName = CourseCalendarNameUtil.getCalendarName(stu.getGrade(), pct.getSemester());
                            course2.setCalendarName(calendarName);
                            course2.setCompulsory(pct.getCompulsory());
                            course2.setLabelId(labelId);
                            course2.setLabelName(labelName);
                            course2.setChosen(pct.getChosen());
                            course2.setIsQhClass(pct.getIsQhClass());
                            pl.setCourse(course2);
                            pl.setSemester(pct.getSemester());
                            pl.setWeekType(pct.getWeekType());
                            pl.setSubCourseCode(pct.getSubCourseCode());
                            pl.setLabel(labelId);
                            pl.setLabelName(labelName);
//                            if(Constants.FIRST.equals(pct.getChosen())) {
//                            	onlyCourses.add(pl);
//                            }
                            planCourses.add(pl);
//                            map.put(courseCode, pct.getCompulsory());
						}
                    }
                }
                if("1".equals(rule.getLimitType())&&rule.getExpression().intValue()==2){
                    CourseGroup courseGroup=new CourseGroup();
                    courseGroup.setLabel(labelId);
                    courseGroup.setCrrdits(rule.getMinCredits());
                    if("1".equals(rule.getLabelType())){
                        courseGroup.setLimitType("1");
                    }else{
                        courseGroup.setLimitType("0");
                    }
                    courseGroups.add(courseGroup);
                }
            }
        }
        if(CollectionUtil.isNotEmpty(planCourseType)){
            log.info("plan course size:{}", planCourseType.size());
            Set<PlanCourse> onlyCourses = context.getOnlyCourses();//培养课程
            Set<CourseGroup> courseGroups = context.getCourseGroups();//课程组学分限制
            for (PlanCourseDto planCourse : planCourseType) {
                List<PlanCourseTypeDto> list = planCourse.getList();
                CultureRuleDto rule = planCourse.getRule();
                Long labelId = planCourse.getLabel();
                String labelName = planCourse.getLabelName();
                if(CollectionUtil.isNotEmpty(list)){
                    for (PlanCourseTypeDto pct : list) {//培养课程
                        String courseCode = pct.getCourseCode();
                        if(StringUtils.isBlank(courseCode) ||(CollectionUtil.isNotEmpty(selectedCourse) && selectedCourse.contains(courseCode)) ) {
                            log.warn("courseCode is Blank skip this record: {}", JSON.toJSONString(pct));
                            continue;
                        }
                        boolean isEnglishFlag = checkCultureEnglish(stu.getStudentId(), courseCode);
                        if(!isEnglishFlag){
                            log.warn("The course({}) is not a course that he can learn at his level", JSON.toJSONString(pct));
                            continue;
                        }
                        PlanCourse pl=new PlanCourse();
//                        Example example = new Example(Course.class);
//                        example.createCriteria().andEqualTo("code", courseCode);
//                        Course course = courseDao.selectOneByExample(example);
//                        List<TeachingClassCache> teachingClassCaches =teachClassCacheService.getTeachClasss(roundId, courseCode);
                        List<TeachingClassCache> teachingClassCaches = teachClassCacheService.getTeachClasssBycalendarId(calendarId, courseCode);
                        if (CollectionUtil.isNotEmpty(teachingClassCaches)) {
                        	TeachingClassCache teachingClassCache = teachingClassCaches.get(0);
                        	ElecCourse course2 = new ElecCourse();
                            course2.setCourseCode(courseCode);
                            course2.setCourseName(pct.getName());
                            course2.setNameEn(pct.getNameEn());
                            course2.setNature(teachingClassCache.getNature());
                            course2.setCredits(pct.getCredits());
                            String calendarName = CourseCalendarNameUtil.getCalendarName(stu.getGrade(), pct.getSemester());
                            course2.setCalendarName(calendarName);
                            course2.setCompulsory(pct.getCompulsory());
                            course2.setLabelId(labelId);
                            course2.setLabelName(labelName);
                            course2.setChosen(pct.getChosen());
                            course2.setIsQhClass(pct.getIsQhClass());
                            pl.setCourse(course2);
                            pl.setSemester(pct.getSemester());
                            pl.setWeekType(pct.getWeekType());
                            pl.setSubCourseCode(pct.getSubCourseCode());
                            pl.setLabel(labelId);
                            pl.setLabelName(labelName);
                            onlyCourses.add(pl);
//
//                            planCourses.add(pl);
//                            map.put(courseCode, pct.getCompulsory());
						}
                    }
                }
                if("1".equals(rule.getLimitType())&&rule.getExpression().intValue()==2){
                    CourseGroup courseGroup=new CourseGroup();
                    courseGroup.setLabel(labelId);
                    courseGroup.setCrrdits(rule.getMinCredits());
                    if("1".equals(rule.getLabelType())){
                        courseGroup.setLimitType("1");
                    }else{
                        courseGroup.setLimitType("0");
                    }
                    courseGroups.add(courseGroup);
                }
            }
        }
        ElectionRounds electionRounds = dataProvider.getRound(roundId);
        if(electionRounds==null) {
            throw new ParameterValidateException(I18nUtil.getMsg("common.notExist",I18nUtil.getMsg("election.round")));
        }

        Set<TsCourse> publicCourses = context.getPublicCourses();//通识选修课
        //通识选修课
        List<BkPublicCourseVo> bkPublicCourse = ElecContextUtil.getBKPublicCourse();
        Integer grade = stu.getGrade();
        if (CollectionUtil.isNotEmpty(bkPublicCourse)) {
            for (BkPublicCourseVo bkPublicCourseVo : bkPublicCourse) {
                String grades = bkPublicCourseVo.getGrades();
                if (compare(grade, grades)) {
                    List<BkPublicCourse> list = bkPublicCourseVo.getList();
                    if (CollectionUtil.isNotEmpty(list)) {
                        for (int i = 0; i < list.size(); i++) {
                            BkPublicCourse publicCourse = list.get(i);
                            String tag = publicCourse.getTag();
                            List<PublicCourse> publicCourseList = publicCourse.getList();
                            if (CollectionUtil.isEmpty(publicCourseList)) {
                                TsCourse tsCourse = new TsCourse();
                                tsCourse.setTag(tag);
                                tsCourse.setIndex(i);
                                publicCourses.add(tsCourse);
                            } else {
                                for (PublicCourse pc : publicCourseList) {
                                    String courseCode = pc.getCourseCode();
                                    if (!selectedCourse.contains(courseCode)) {
                                        List<TeachingClassCache> teachingClassCaches = teachClassCacheService.getTeachClasssBycalendarId(calendarId, courseCode);
                                        // 判断这门课程在本轮次是否有对应的教学班
                                        if (CollectionUtil.isNotEmpty(teachingClassCaches)) {
                                            Set<String> set = new HashSet(5);
                                            Set<String> collect = teachingClassCaches.stream().map(TeachingClassCache::getCampus).collect(Collectors.toSet());
                                            for (String s : collect) {
                                                if (StringUtils.isNotBlank(s)) {
                                                    String campus = dictionaryService.query("X_XQ", s);
                                                    if (StringUtils.isNotBlank(campus)) {
                                                        set.add(campus);
                                                    }
                                                }
                                            }
                                            ElecCourse elecCourse = new ElecCourse();
                                            elecCourse.setCampus(String.join(",", set));
                                            elecCourse.setCourseCode(courseCode);
//                                            elecCourse.setCompulsory(map.get(courseCode));
                                            elecCourse.setCourseName(pc.getCourseName());
                                            elecCourse.setCredits(pc.getCreidits());
                                            elecCourse.setJp(pc.getJp());
                                            elecCourse.setCx(pc.isCx());
                                            elecCourse.setYs(pc.isYs());
                                            TsCourse tsCourse = new TsCourse();
                                            tsCourse.setTag(tag);
                                            tsCourse.setIndex(i);
                                            tsCourse.setCourse(elecCourse);
                                            publicCourses.add(tsCourse);
                                        }
                                    }
                                }
                            }
                        }
                    }
                    break;
                }
            }
        }
    }

    // 判断学生年级是否符合通识选修课年级
    private boolean compare(Integer grade, String grades) {
        if (StringUtils.isNotBlank(grades)) {
            String[] split = grades.split(",");
            if (split.length > 1) {
                Integer start = Integer.parseInt(split[0]);
                Integer end = Integer.parseInt(split[1]);
                int value = grade.intValue();
                if (start.intValue() <= value && value <= end.intValue()) {
                    return true;
                }
            } else {
                Integer start = Integer.parseInt(split[0]);
                if (start.intValue() <= grade) {
                    return true;
                }
            }
        }
        return false;
    }

    //判断该门英语课是否为他英语等级可以学习的课程
    public boolean checkCultureEnglish(String studentId,
                             String courseCode)
    {
        List<String> allCourseCodeList =
                CultureSerivceInvoker.getAllCoursesLevelCourse();
        if (CollectionUtil.isEmpty(allCourseCodeList) || (CollectionUtil.isNotEmpty(allCourseCodeList) && !allCourseCodeList.contains(courseCode))){
            return true;
        }
        Example example = new Example(ElcStuCouLevel.class);
        example.createCriteria()
                .andEqualTo("studentId", studentId);
        List<ElcStuCouLevel> list = couLevelDao.selectByExample(example);
        // 没有配置英语能力-通过
        if (CollectionUtil.isEmpty(list))
        {
            return true;
        }
        Long courseCategoryId = list.get(0).getCourseCategoryId();
        List<String> courseCodeList =
                CultureSerivceInvoker.getCoursesLevelCourse(courseCategoryId);
        if (CollectionUtil.isNotEmpty(courseCodeList)
                && !courseCodeList.contains(courseCode))
        {
            return false;
        }
        return true;
    }

}
