package com.server.edu.election.entity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.server.edu.election.studentelec.cache.StudentInfoCache;
import com.server.edu.election.studentelec.context.CompletedCourse;
import com.server.edu.election.studentelec.context.CourseGroup;
import com.server.edu.election.studentelec.context.ElecCourse;
import com.server.edu.election.studentelec.context.ElecRequest;
import com.server.edu.election.studentelec.context.ElecRespose;
import com.server.edu.election.studentelec.context.PlanCourse;
import com.server.edu.election.studentelec.context.SelectedCourse;
import com.server.edu.election.studentelec.utils.ElecContextUtil;

/**
 * 执行“学生选课请求”时的上下文环境，组装成本对象，供各种约束调用
 *
 */
public class ElecContextMock
{
    /**学期*/
    private Long calendarId;
    
    /** 个人信息 */
    private StudentInfoCache studentInfo;
    
    /** 已完成通過课程 */
    private Set<CompletedCourse> completedCourses;
    
    /** 本学期已选择课程 */
    private Set<SelectedCourse> selectedCourses;
    
    /** 免修申请课程 */
    private Set<ElecCourse> applyForDropCourses;
    
    /**课程组学分限制*/
    private Set<CourseGroup> courseGroups;
    
    /** 个人计划内课程 */
    private Set<PlanCourse> planCourses;
    
    /** 通识选修课程 */
    private Set<ElecCourse> publicCourses;
    
    /**未通過課程*/
    private Set<CompletedCourse> failedCourse;
    
    /**申请课程*/
    private Set<String> applyCourse;
    
    /**选课申请课程*/
    private Set<ElectionApply> elecApplyCourses;
    
    private ElecRequest request;
    
    private ElecRespose respose;
    
    private ElecContextUtil contextUtil;
    
    public ElecContextMock(String studentId, Long calendarId,
        ElecRequest elecRequest)
    {
        this(studentId, calendarId);
        this.request = elecRequest;
    }
    
    public ElecContextMock(String studentId, Long calendarId)
    {
        this.calendarId = calendarId;
        this.contextUtil = ElecContextUtil.create(studentId, this.calendarId);
        
        this.mockData();
    }
    
    public void mockData()
    {
        String text ="{\"studentInfo\":{\"studentId\":\"1659350\",\"studentName\":\"梅锐梓\",\"sex\":1,\"campus\":\"1\",\"faculty\":\"000192\",\"major\":\"01025\",\"grade\":2016,\"trainingLevel\":\"1\",\"englishLevel\":null,\"spcialPlan\":\"\",\"paid\":false,\"graduate\":false,\"repeater\":false,\"aboard\":false,\"sexI18n\":\"男\",\"campusI18n\":\"四平路校区\",\"facultyI18n\":\"经济与管理学院\",\"majorI18n\":\"工商管理(国际班)\",\"trainingLevelI18n\":\"本科\",\"spcialPlanI18n\":\"\"},\"completedCourses\":[],\"selectedCourses\":[{\"courseCode\":\"10001921094\",\"courseName\":\"质量管理\",\"credits\":2.0,\"nameEn\":\"Quality Management\",\"campus\":\"1\",\"publicElec\":false,\"calendarName\":null,\"electionApplyId\":null,\"apply\":null,\"teachClassId\":111111112458015,\"teachClassCode\":\"01037801\",\"teachClassType\":\"1\",\"maxNumber\":35,\"currentNumber\":1,\"times\":[{\"teachClassId\":111111112458015,\"arrangeTimeId\":2950648,\"timeStart\":5,\"timeEnd\":6,\"dayOfWeek\":1,\"weeks\":[1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17],\"value\":\"质量管理(10001921094) null\",\"teacherCode\":\"04066\"}],\"teacherCode\":null,\"teacherName\":null,\"chooseObj\":3,\"courseTakeType\":1,\"turn\":1,\"isApply\":null,\"practice\":false,\"retraining\":false}],\"applyForDropCourses\":[],\"courseGroups\":[],\"planCourses\":[{\"courseCode\":\"10001921094\",\"courseName\":\"质量管理\",\"credits\":2.0,\"nameEn\":\"Quality Management\",\"campus\":null,\"publicElec\":false,\"calendarName\":\"2018-2019学年第2学期\",\"electionApplyId\":null,\"apply\":null,\"label\":101,\"weekType\":0,\"semester\":\"6\",\"subCourseCode\":null},{\"courseCode\":\"10001921531\",\"courseName\":\"中国市场研究\",\"credits\":2.0,\"nameEn\":\"Chinese Market Research\",\"campus\":null,\"publicElec\":false,\"calendarName\":\"2018-2019学年第2学期\",\"electionApplyId\":null,\"apply\":null,\"label\":101,\"weekType\":0,\"semester\":\"6\",\"subCourseCode\":null},{\"courseCode\":\"10001921377\",\"courseName\":\"公司治理\",\"credits\":2.0,\"nameEn\":\"Corporate Governance\",\"campus\":null,\"publicElec\":false,\"calendarName\":\"2018-2019学年第2学期\",\"electionApplyId\":null,\"apply\":null,\"label\":101,\"weekType\":0,\"semester\":\"6\",\"subCourseCode\":null},{\"courseCode\":\"10001921530\",\"courseName\":\"中国房地产开发\",\"credits\":2.0,\"nameEn\":\"Real Estate Development in China\",\"campus\":null,\"publicElec\":false,\"calendarName\":\"2018-2019学年第2学期\",\"electionApplyId\":null,\"apply\":null,\"label\":101,\"weekType\":0,\"semester\":\"6\",\"subCourseCode\":null},{\"courseCode\":\"10001921376\",\"courseName\":\"货币金融学\",\"credits\":2.0,\"nameEn\":\"The Economics of Money, Banking and Financial Markets\",\"campus\":null,\"publicElec\":false,\"calendarName\":\"2018-2019学年第1学期\",\"electionApplyId\":null,\"apply\":null,\"label\":101,\"weekType\":0,\"semester\":\"5\",\"subCourseCode\":null},{\"courseCode\":\"10001921533\",\"courseName\":\"中国城市与房地产开发\",\"credits\":2.0,\"nameEn\":\"Urban and Real Estate Development in China\",\"campus\":null,\"publicElec\":false,\"calendarName\":\"2018-2019学年第2学期\",\"electionApplyId\":null,\"apply\":null,\"label\":101,\"weekType\":0,\"semester\":\"6\",\"subCourseCode\":null},{\"courseCode\":\"10001921532\",\"courseName\":\"国际商务及中国企业国际化\",\"credits\":2.0,\"nameEn\":\"International Business & Internationalization of Chinese Firms\",\"campus\":null,\"publicElec\":false,\"calendarName\":\"2018-2019学年第2学期\",\"electionApplyId\":null,\"apply\":null,\"label\":101,\"weekType\":0,\"semester\":\"6\",\"subCourseCode\":null},{\"courseCode\":\"10001921516\",\"courseName\":\"市场营销学（英）\",\"credits\":2.0,\"nameEn\":\"Marketing（English）\",\"campus\":null,\"publicElec\":false,\"calendarName\":\"2018-2019学年第1学期\",\"electionApplyId\":null,\"apply\":null,\"label\":191,\"weekType\":0,\"semester\":\"5\",\"subCourseCode\":null},{\"courseCode\":\"10001920649\",\"courseName\":\"管理学概论\",\"credits\":2.0,\"nameEn\":\"Introduction to Management\",\"campus\":null,\"publicElec\":false,\"calendarName\":\"2018-2019学年第1学期\",\"electionApplyId\":null,\"apply\":null,\"label\":100,\"weekType\":0,\"semester\":\"5\",\"subCourseCode\":null},{\"courseCode\":\"10001921513\",\"courseName\":\"管理信息系统A（英）\",\"credits\":2.0,\"nameEn\":\"Management Information System A(English)\",\"campus\":null,\"publicElec\":false,\"calendarName\":\"2018-2019学年第2学期\",\"electionApplyId\":null,\"apply\":null,\"label\":191,\"weekType\":0,\"semester\":\"6\",\"subCourseCode\":null},{\"courseCode\":\"10001920666\",\"courseName\":\"电子商务\",\"credits\":2.0,\"nameEn\":\"Electronic Business\",\"campus\":null,\"publicElec\":false,\"calendarName\":\"2018-2019学年第2学期\",\"electionApplyId\":null,\"apply\":null,\"label\":101,\"weekType\":0,\"semester\":\"6\",\"subCourseCode\":null},{\"courseCode\":\"10001921535\",\"courseName\":\"毕业论文（国际班）\",\"credits\":16.0,\"nameEn\":\"Graduation Thesis\",\"campus\":null,\"publicElec\":false,\"calendarName\":\"2019-2020学年第2学期\",\"electionApplyId\":null,\"apply\":null,\"label\":25,\"weekType\":0,\"semester\":\"8\",\"subCourseCode\":null},{\"courseCode\":\"10001921512\",\"courseName\":\"商务谈判（英）\",\"credits\":2.0,\"nameEn\":\"Business Negotiation（English）\",\"campus\":null,\"publicElec\":false,\"calendarName\":\"2018-2019学年第2学期\",\"electionApplyId\":null,\"apply\":null,\"label\":191,\"weekType\":0,\"semester\":\"6\",\"subCourseCode\":null},{\"courseCode\":\"10001921534\",\"courseName\":\"信息技术与管理\",\"credits\":2.0,\"nameEn\":\"Information Technology and Management\",\"campus\":null,\"publicElec\":false,\"calendarName\":\"2019-2020学年第1学期\",\"electionApplyId\":null,\"apply\":null,\"label\":101,\"weekType\":0,\"semester\":\"7\",\"subCourseCode\":null},{\"courseCode\":\"10001921515\",\"courseName\":\"项目管理 （英）\",\"credits\":2.0,\"nameEn\":\"Project Management(English)\",\"campus\":null,\"publicElec\":false,\"calendarName\":\"2018-2019学年第2学期\",\"electionApplyId\":null,\"apply\":null,\"label\":191,\"weekType\":0,\"semester\":\"6\",\"subCourseCode\":null},{\"courseCode\":\"10001921537\",\"courseName\":\"毕业实习(国际班)\",\"credits\":6.0,\"nameEn\":\"Internship\",\"campus\":null,\"publicElec\":false,\"calendarName\":\"2019-2020学年第2学期\",\"electionApplyId\":null,\"apply\":null,\"label\":25,\"weekType\":0,\"semester\":\"8\",\"subCourseCode\":null},{\"courseCode\":\"10001921514\",\"courseName\":\"人力资源管理（英）\",\"credits\":2.0,\"nameEn\":\"Human Resource Management（English）\",\"campus\":null,\"publicElec\":false,\"calendarName\":\"2018-2019学年第1学期\",\"electionApplyId\":null,\"apply\":null,\"label\":191,\"weekType\":0,\"semester\":\"5\",\"subCourseCode\":null},{\"courseCode\":\"10000390105\",\"courseName\":\"基础中文\",\"credits\":6.0,\"nameEn\":\"Chinese Basic\",\"campus\":null,\"publicElec\":false,\"calendarName\":\"2018-2019学年第1学期\",\"electionApplyId\":null,\"apply\":null,\"label\":100,\"weekType\":0,\"semester\":\"5\",\"subCourseCode\":null},{\"courseCode\":\"10000390106\",\"courseName\":\"初级中文\",\"credits\":6.0,\"nameEn\":\"Chinese Elementary\",\"campus\":null,\"publicElec\":false,\"calendarName\":\"2018-2019学年第2学期\",\"electionApplyId\":null,\"apply\":null,\"label\":100,\"weekType\":0,\"semester\":\"6\",\"subCourseCode\":null},{\"courseCode\":\"10000390107\",\"courseName\":\"中级中文\",\"credits\":6.0,\"nameEn\":\"Chinese Intermediate\",\"campus\":null,\"publicElec\":false,\"calendarName\":\"2019-2020学年第1学期\",\"electionApplyId\":null,\"apply\":null,\"label\":100,\"weekType\":0,\"semester\":\"7\",\"subCourseCode\":null},{\"courseCode\":\"10001921388\",\"courseName\":\"物流与供应链管理\",\"credits\":2.0,\"nameEn\":\"Logistics and Supply Chain Management\",\"campus\":null,\"publicElec\":false,\"calendarName\":\"2018-2019学年第2学期\",\"electionApplyId\":null,\"apply\":null,\"label\":101,\"weekType\":0,\"semester\":\"6\",\"subCourseCode\":null},{\"courseCode\":\"10001921368\",\"courseName\":\"创业学\",\"credits\":2.0,\"nameEn\":\"Entrepreneurship\",\"campus\":null,\"publicElec\":false,\"calendarName\":\"2018-2019学年第1学期\",\"electionApplyId\":null,\"apply\":null,\"label\":101,\"weekType\":0,\"semester\":\"5\",\"subCourseCode\":null},{\"courseCode\":\"10001921522\",\"courseName\":\"物流与供应链管理Ⅱ\",\"credits\":2.0,\"nameEn\":\"Logistics & Supply Chain Management in China (Ⅱ)\",\"campus\":null,\"publicElec\":false,\"calendarName\":\"2019-2020学年第1学期\",\"electionApplyId\":null,\"apply\":null,\"label\":101,\"weekType\":0,\"semester\":\"7\",\"subCourseCode\":null},{\"courseCode\":\"10001921521\",\"courseName\":\"学术讲座\",\"credits\":2.0,\"nameEn\":\"Academic Lectures and Seminars\",\"campus\":null,\"publicElec\":false,\"calendarName\":\"2018-2019学年第2学期\",\"electionApplyId\":null,\"apply\":null,\"label\":100,\"weekType\":0,\"semester\":\"6\",\"subCourseCode\":null},{\"courseCode\":\"10001921045\",\"courseName\":\"运营管理\",\"credits\":2.0,\"nameEn\":\"Operation Management\",\"campus\":null,\"publicElec\":false,\"calendarName\":\"2018-2019学年第2学期\",\"electionApplyId\":null,\"apply\":null,\"label\":203,\"weekType\":0,\"semester\":\"6\",\"subCourseCode\":null},{\"courseCode\":\"10001921143\",\"courseName\":\"公司金融\",\"credits\":2.0,\"nameEn\":\"Corporate Finance\",\"campus\":null,\"publicElec\":false,\"calendarName\":\"2018-2019学年第1学期\",\"electionApplyId\":null,\"apply\":null,\"label\":101,\"weekType\":0,\"semester\":\"5\",\"subCourseCode\":null},{\"courseCode\":\"10000390096\",\"courseName\":\"中国概论（英）\",\"credits\":2.0,\"nameEn\":\"Chinese Affairs(English)\",\"campus\":null,\"publicElec\":false,\"calendarName\":\"2018-2019学年第1学期\",\"electionApplyId\":null,\"apply\":null,\"label\":100,\"weekType\":0,\"semester\":\"5\",\"subCourseCode\":null},{\"courseCode\":\"10001921528\",\"courseName\":\"资本市场分析\",\"credits\":2.0,\"nameEn\":\"Capital Market Analysis\",\"campus\":null,\"publicElec\":false,\"calendarName\":\"2018-2019学年第2学期\",\"electionApplyId\":null,\"apply\":null,\"label\":101,\"weekType\":0,\"semester\":\"6\",\"subCourseCode\":null},{\"courseCode\":\"10001921527\",\"courseName\":\"跨文化沟通\",\"credits\":2.0,\"nameEn\":\"Multicultural Communication\",\"campus\":null,\"publicElec\":false,\"calendarName\":\"2018-2019学年第1学期\",\"electionApplyId\":null,\"apply\":null,\"label\":101,\"weekType\":0,\"semester\":\"5\",\"subCourseCode\":null},{\"courseCode\":\"10001921529\",\"courseName\":\"私募股权与风险资本\",\"credits\":2.0,\"nameEn\":\"Private Equity and Venture Capital in China\",\"campus\":null,\"publicElec\":false,\"calendarName\":\"2018-2019学年第2学期\",\"electionApplyId\":null,\"apply\":null,\"label\":101,\"weekType\":0,\"semester\":\"6\",\"subCourseCode\":null},{\"courseCode\":\"10001921524\",\"courseName\":\"财务与管理会计\",\"credits\":2.0,\"nameEn\":\"Financial & Management Accounting\",\"campus\":null,\"publicElec\":false,\"calendarName\":\"2018-2019学年第2学期\",\"electionApplyId\":null,\"apply\":null,\"label\":203,\"weekType\":0,\"semester\":\"6\",\"subCourseCode\":null},{\"courseCode\":\"10001921523\",\"courseName\":\"可持续发展经济学\",\"credits\":2.0,\"nameEn\":\"Economics of Sustainable Development in China\",\"campus\":null,\"publicElec\":false,\"calendarName\":\"2019-2020学年第1学期\",\"electionApplyId\":null,\"apply\":null,\"label\":101,\"weekType\":0,\"semester\":\"7\",\"subCourseCode\":null},{\"courseCode\":\"10001921526\",\"courseName\":\"中国金融市场\",\"credits\":2.0,\"nameEn\":\"Financial Market in China\",\"campus\":null,\"publicElec\":false,\"calendarName\":\"2018-2019学年第1学期\",\"electionApplyId\":null,\"apply\":null,\"label\":101,\"weekType\":0,\"semester\":\"5\",\"subCourseCode\":null},{\"courseCode\":\"10001921525\",\"courseName\":\"中国经济：改革与发展\",\"credits\":2.0,\"nameEn\":\"China Economy: Reform and Development\",\"campus\":null,\"publicElec\":false,\"calendarName\":\"2018-2019学年第1学期\",\"electionApplyId\":null,\"apply\":null,\"label\":101,\"weekType\":0,\"semester\":\"5\",\"subCourseCode\":null},{\"courseCode\":\"10001920216\",\"courseName\":\"投资学\",\"credits\":2.0,\"nameEn\":\"Investment\",\"campus\":null,\"publicElec\":false,\"calendarName\":\"2019-2020学年第1学期\",\"electionApplyId\":null,\"apply\":null,\"label\":101,\"weekType\":0,\"semester\":\"7\",\"subCourseCode\":null},{\"courseCode\":\"10001920816\",\"courseName\":\"战略管理\",\"credits\":2.0,\"nameEn\":\"Strategic Management\",\"campus\":null,\"publicElec\":false,\"calendarName\":\"2018-2019学年第1学期\",\"electionApplyId\":null,\"apply\":null,\"label\":203,\"weekType\":0,\"semester\":\"5\",\"subCourseCode\":null}],\"publicCourses\":[],\"failedCourse\":[],\"applyCourse\":[\"10001921531\"],\"elecApplyCourses\":[{\"id\":4,\"studentId\":\"1659350\",\"calendarId\":107,\"courseCode\":\"10001921531\",\"apply\":0,\"applyBy\":null,\"remark\":null,\"mode\":1,\"createdAt\":\"2019-06-13 16:47:49\"}],\"request\":null,\"respose\":{\"status\":null,\"successCourses\":[],\"failedReasons\":{},\"data\":null}}";
        JSONObject json = JSON.parseObject(text);
        
        studentInfo = getObject(json, "studentInfo", StudentInfoCache.class);
        respose = getObject(json, "respose", ElecRespose.class);
        completedCourses =
            getSet(json, "completedCourses", CompletedCourse.class);
        selectedCourses = getSet(json, "selectedCourses", SelectedCourse.class);
        applyForDropCourses =
            getSet(json, "applyForDropCourses", ElecCourse.class);
        planCourses = getSet(json, "planCourses", PlanCourse.class);
        publicCourses = getSet(json, "publicCourses", ElecCourse.class);
        courseGroups = getSet(json, "courseGroups", CourseGroup.class);
        failedCourse = getSet(json, "failedCourse", CompletedCourse.class);
    }
    
    static <T> T getObject(JSONObject json, String key, Class<T> clazz)
    {
        return JSON.parseObject(json.getString(key), clazz);
    }
    
    static <T> Set<T> getSet(JSONObject json, String key, Class<T> clazz)
    {
        String text = json.getString(key);
        List<T> list = JSON.parseArray(text, clazz);
        if (null == list)
        {
            list = new ArrayList<>();
        }
        return new HashSet<>(list);
    }
    
    /**
     * 保存到redis中
     * 
     */
    public void saveToCache()
    {
        this.contextUtil.updateMem(StudentInfoCache.class.getSimpleName(),
            this.studentInfo);
        this.respose.setStatus(null);
        this.contextUtil.updateMem(ElecRespose.class.getSimpleName(),
            this.respose);
        this.contextUtil.updateMem("CompletedCourses", this.completedCourses);
        this.contextUtil.updateMem("SelectedCourses", this.selectedCourses);
        this.contextUtil.updateMem("ApplyForDropCourses",
            this.applyForDropCourses);
        this.contextUtil.updateMem("PlanCourses", this.planCourses);
        this.contextUtil.updateMem("courseGroups", this.courseGroups);
        this.contextUtil.updateMem("publicCourses", this.publicCourses);
        this.contextUtil.updateMem("failedCourse", this.failedCourse);
        this.contextUtil.updateMem("elecApplyCourses", this.elecApplyCourses);
        // 保存所有到redis
        this.contextUtil.saveAll();
    }
    
    public void saveResponse()
    {
        this.respose.setStatus(null);
        this.contextUtil.saveOne(ElecRespose.class.getSimpleName(),
            this.respose);
    }
    
    /**
     * 清空CompletedCourses,SelectedCourses,ApplyForDropCourses,PlanCourses,courseGroups,publicCourses,failedCourse
     * 
     */
    public void clear()
    {
        this.getCompletedCourses().clear();
        this.getSelectedCourses().clear();
        this.getApplyForDropCourses().clear();
        this.getPlanCourses().clear();
        this.getCourseGroups().clear();
        this.getPublicCourses().clear();
        this.getFailedCourse().clear();
        this.getRespose().getFailedReasons().clear();
        this.getRespose().getSuccessCourses().clear();
        this.getApplyCourse().clear();
        this.getElecApplyCourses().clear();
    }
    
    public StudentInfoCache getStudentInfo()
    {
        return studentInfo;
    }
    
    public Set<CompletedCourse> getCompletedCourses()
    {
        return completedCourses;
    }
    
    public Set<SelectedCourse> getSelectedCourses()
    {
        return selectedCourses;
    }
    
    public Set<ElecCourse> getApplyForDropCourses()
    {
        return applyForDropCourses;
    }
    
    public Set<PlanCourse> getPlanCourses()
    {
        return planCourses;
    }
    
    public Set<ElecCourse> getPublicCourses()
    {
        return publicCourses;
    }
    
    public Set<CourseGroup> getCourseGroups()
    {
        return courseGroups;
    }
    
    public Set<CompletedCourse> getFailedCourse()
    {
        return failedCourse;
    }
    
    public ElecRequest getRequest()
    {
        return request;
    }
    
    public void setRequest(ElecRequest request)
    {
        this.request = request;
    }
    
    public ElecRespose getRespose()
    {
        return respose;
    }
    
    public void setRespose(ElecRespose respose)
    {
        this.respose = respose;
    }
    
    public Set<String> getApplyCourse()
    {
        
        if (applyCourse == null)
        {
            applyCourse =
                new HashSet<>(ElecContextUtil.getApplyCourse(calendarId));
        }
        return applyCourse;
    }
    
    public Set<ElectionApply> getElecApplyCourses()
    {
        return elecApplyCourses;
    }
    
}
