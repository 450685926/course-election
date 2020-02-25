package com.server.edu.election.studentelec.preload;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.server.edu.common.vo.ScoreStudentResultVo;
import com.server.edu.election.dao.HonorPlanStdsDao;
import com.server.edu.election.dto.StudentScoreDto;
import com.server.edu.election.entity.HonorPlanStds;
import com.server.edu.election.service.BkStudentScoreService;
import com.server.edu.election.studentelec.cache.TeachingClassCache;
import com.server.edu.election.studentelec.context.BclHonorCourse;
import com.server.edu.election.studentelec.context.ElecRequest;
import com.server.edu.election.studentelec.context.bk.HonorCourseBK;
import com.server.edu.election.studentelec.service.cache.TeachClassCacheService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.server.edu.common.entity.BclHonorModule;
import com.server.edu.election.rpc.CultureSerivceInvoker;
import com.server.edu.election.studentelec.cache.StudentInfoCache;
import com.server.edu.election.studentelec.context.bk.ElecContextBk;
import com.server.edu.util.CollectionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

@Component
public class BkHonorCourseLoad extends DataProLoad<ElecContextBk>{
    Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private HonorPlanStdsDao honorPlanStdsDao;

    @Autowired
    private TeachClassCacheService teachClassCacheService;

    @Autowired
    private BkStudentScoreService bkStudentScoreService;

    @Override
    public int getOrder()
    {
        return 3;
    }
    
    @Override
    public String getProjectIds()
    {
        return "1";
    }
    
    @Override
    public void load(ElecContextBk context)
    {

        log.info("----------------11111111111111------------------");
        ElecRequest request = context.getRequest();
        StudentInfoCache stu = context.getStudentInfo();
        Set<HonorCourseBK> honorCourses = context.getHonorCourses();//荣誉课程

        //查询学生荣誉计划名单信息
        Example example = new Example(HonorPlanStds.class);
        example.createCriteria().andEqualTo("studentId",stu.getStudentId()).andEqualTo("calendarId",request.getCalendarId());
        HonorPlanStds honorPlanStds = honorPlanStdsDao.selectOneByExample(example);
        log.info("----------------2222222222222222------------------"+honorPlanStds);
        if (honorPlanStds!=null){
            List<BclHonorModule> list = CultureSerivceInvoker.findHonorCourseList(stu.getStudentId());
            Long roundId = request.getRoundId();
            if(CollectionUtil.isNotEmpty(list)){
                log.info("honor course size:{}", list.size());
                StudentScoreDto dto = new StudentScoreDto();
                dto.setStudentId(context.getStudentInfo().getStudentId());
                List<ScoreStudentResultVo> stuScore = bkStudentScoreService.getAllStudentScoreList(dto);
                List<String> selectedCourse = stuScore.stream().map(ScoreStudentResultVo::getCourseCode).collect(Collectors.toList());
                //过滤属于这个学生的荣誉课程
                list.forEach(c->{
                    if (StringUtils.isEmpty(honorPlanStds.getDirectionName())){
                        if (StringUtils.equalsIgnoreCase(c.getHonorModuleName(),honorPlanStds.getHonorPlanName())
                                && flag(roundId, c.getHonorCourseCode())
                                && compare(selectedCourse, c.getHonorCourseCode())){
                            BclHonorCourse bclHonorCourse = new BclHonorCourse();
                            bclHonorCourse.setCourseCode(c.getHonorCourseCode());
                            bclHonorCourse.setCourseName(c.getHonorCourseName());
                            bclHonorCourse.setCollege(c.getCollege());
                            bclHonorCourse.setDirectionName(c.getDirectionName());
                            bclHonorCourse.setGrade(c.getGrade());
                            bclHonorCourse.setTrainingLevel(c.getTrainingLevel());
                            bclHonorCourse.setCourseCodeRep(c.getCourseCodeRep());
                            bclHonorCourse.setCourseNameRep(c.getCourseNameRep());
                            bclHonorCourse.setCredits(c.getCreidits());
                            bclHonorCourse.setDirectionId(c.getDirectionId());
                            bclHonorCourse.setEndYear(c.getEndYear());
                            bclHonorCourse.setStartYear(c.getStartYear());
                            bclHonorCourse.setHonorModuleId(c.getHonorModuleId());
                            bclHonorCourse.setHonorModuleName(c.getHonorModuleName());
                            bclHonorCourse.setPeroid(c.getPeroid());
                            bclHonorCourse.setHonorCourseId(c.getHonorCourseId());

                            HonorCourseBK honorCourseBK = new HonorCourseBK();
                            honorCourseBK.setCourse(bclHonorCourse);
                            honorCourses.add(honorCourseBK);
                        }
                    }else{
                        if (StringUtils.equalsIgnoreCase(c.getHonorModuleName(),honorPlanStds.getHonorPlanName())
                                && StringUtils.equalsIgnoreCase(c.getDirectionName(),honorPlanStds.getDirectionName())
                                && flag(roundId, c.getHonorCourseCode())
                                && compare(selectedCourse, c.getHonorCourseCode())){
                            BclHonorCourse bclHonorCourse = new BclHonorCourse();
                            bclHonorCourse.setCourseCode(c.getHonorCourseCode());
                            bclHonorCourse.setCourseName(c.getHonorCourseName());
                            bclHonorCourse.setCollege(c.getCollege());
                            bclHonorCourse.setDirectionName(c.getDirectionName());
                            bclHonorCourse.setGrade(c.getGrade());
                            bclHonorCourse.setTrainingLevel(c.getTrainingLevel());
                            bclHonorCourse.setCourseCodeRep(c.getCourseCodeRep());
                            bclHonorCourse.setCourseNameRep(c.getCourseNameRep());
                            bclHonorCourse.setCredits(c.getCreidits());
                            bclHonorCourse.setDirectionId(c.getDirectionId());
                            bclHonorCourse.setEndYear(c.getEndYear());
                            bclHonorCourse.setStartYear(c.getStartYear());
                            bclHonorCourse.setHonorModuleId(c.getHonorModuleId());
                            bclHonorCourse.setHonorModuleName(c.getHonorModuleName());
                            bclHonorCourse.setPeroid(c.getPeroid());
                            bclHonorCourse.setHonorCourseId(c.getHonorCourseId());
                            bclHonorCourse.setCourseId(c.getHonorCourseId());

                            HonorCourseBK honorCourseBK = new HonorCourseBK();
                            honorCourseBK.setCourse(bclHonorCourse);
                            honorCourses.add(honorCourseBK);
                        }
                    }

                });
            }
        }



    }

    // 判断轮次里面某门课程是否有教学班，没有返回false。不展示
    private boolean flag(Long roundId, String courseCode) {
        List<TeachingClassCache> teachingClassCaches = teachClassCacheService.getTeachClasss(roundId, courseCode);
        if (CollectionUtil.isNotEmpty(teachingClassCaches)) {
            return true;
        } else {
            return false;
        }
    }

    private boolean compare(List<String> list, String courseCode) {
        if (CollectionUtil.isNotEmpty(list)) {
            if (list.contains(courseCode)) {
                return false;
            }
        }
        return true;
    }

}
