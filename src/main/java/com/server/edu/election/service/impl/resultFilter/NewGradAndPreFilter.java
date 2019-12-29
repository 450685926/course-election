package com.server.edu.election.service.impl.resultFilter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang.math.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.server.edu.election.dao.TeachingClassDao;
import com.server.edu.election.dto.AutoRemoveDto;
import com.server.edu.election.dto.SuggestProfessionDto;
import com.server.edu.election.entity.Student;
import com.server.edu.util.CollectionUtil;

public class NewGradAndPreFilter {

    private TeachingClassDao classDao;
    
    private AutoRemoveDto dto;
    
    /**配课年级专业人数配比*/
    private Map<String, Integer> profNumMap;
    
    Logger logger = LoggerFactory.getLogger(getClass());
    
    //private List<String> suggestStus;
    
    public NewGradAndPreFilter(AutoRemoveDto dto, TeachingClassDao classDao)
    {
        this.dto = dto;
        this.classDao = classDao;
    }
    
    public static String key(SuggestProfessionDto dto)
    {
        return dto.getGrade() + "-" + dto.getProfession();
    }
    
    public void init()
    {
        //配课年级专业
    	List<SuggestProfessionDto> suggestProfessionList = dto.getSuggestProfessionList();
        //配课年级专业人数
        profNumMap = sumPerson(suggestProfessionList);
        //suggestStus = classDao.selectSuggestStudent(dto.getTeachingClassId());
    }
    
    public void execute(List<Student> stuList, List<String> removeStus)
    {
        // 删除非配课年级专业学生
    	 // 按配课年级专业人数比例(A专业20，B专业50，如果B专业70人A专业将无法选课)
        if (dto.getSuggestSwitchCourse())
        {
//            //0. 删除指定学生外的人
//            if (CollectionUtil.isNotEmpty(restrictStus))
//            {
//                Iterator<Student> iterator = stuList.iterator();
//                while (iterator.hasNext())
//                {
//                    Student stu = iterator.next();
//                    if (!restrictStus.contains(stu.getStudentCode()))
//                    {
//                        removeStus.add(stu.getStudentCode());
//                        iterator.remove();
//                    }
//                }
//            }
            //gradAndPre(stuList, profNumMap, removeStus);
            gradAndPrePeople(stuList, profNumMap, removeStus);
        }
    }
    
    public static Map<String, Integer> sumPerson(
        List<SuggestProfessionDto> suggestProfessionList)
    {
        Map<String, Integer> profNumMap = new HashMap<>();
        for (SuggestProfessionDto dto : suggestProfessionList)
        {
            String key = key(dto);
            profNumMap.put(key, dto.getNumber());
        }
        return profNumMap;
    }
    /**
     * 年级专业过滤
     * 
     * @param stuList
     * @param profNumMap
     * @param removeStus
     * @see [类、类#方法、类#成员]
     */
    public static void gradAndPre(List<Student> stuList,
        Map<String, Integer> profNumMap, List<String> removeStus)
    {
        if (CollectionUtil.isNotEmpty(stuList)
            && CollectionUtil.isNotEmptyMap(profNumMap))
        {
            Map<Boolean, List<Student>> collect = stuList.stream()
                .collect(Collectors.partitioningBy(stu -> profNumMap
                    .containsKey(stu.getGrade() + "-" + stu.getProfession())));
            List<Student> trueList = collect.get(Boolean.TRUE);
            stuList.clear();
            if (CollectionUtil.isNotEmpty(trueList))
            {
                stuList.addAll(trueList);
            }
            // 不满足条件的学生
            List<Student> unContainlist = collect.get(Boolean.FALSE);
            if (CollectionUtil.isNotEmpty(unContainlist))
            {
                unContainlist.stream()
                    .map(Student::getStudentCode)
                    .forEach(removeStus::add);
            }
        }
    }
    
    /**
     * 专业人数配比过滤
     * 
     * @param stuList
     * @param profNumMap
     * @param removeStus
     * @see [类、类#方法、类#成员]
     */
    public static void gradAndPrePeople(List<Student> stuList,
        Map<String, Integer> profNumMap, List<String> removeStus)
    {
        if (CollectionUtil.isNotEmpty(stuList)
            && CollectionUtil.isNotEmptyMap(profNumMap))
        {
        	List<Student> students= new ArrayList<>();
            //年级专业对应的学生
        	
            Map<String, List<Student>> gradeStuMap = stuList.stream().filter(c->c!=null)
                .collect(Collectors.groupingBy(
                    stu -> stu.getGrade() + "-" + stu.getProfession()));
            for (String key : gradeStuMap.keySet())
            {
                Integer number = profNumMap.get(key);//年级专业分配的人数
                List<Student> stus = gradeStuMap.get(key);//年级专业选课人数
                if (number != null && stus.size() > number)
                {
                    randomRemove(removeStus, number, stus);
                    students.addAll(stus);
                }
            }
            if(CollectionUtil.isNotEmpty(students)) {
            	stuList.clear();
            	stuList.addAll(students);
            }
        }
    }
    
    /**随机删除看过容量的学生*/
    public static void randomRemove(List<String> removeStus,
        Integer limitNumber, List<Student> stuList)
    {
        //随机删除看过容量的学生
        while (stuList.size() > limitNumber)
        {
            int i = RandomUtils.nextInt(stuList.size());
            Student stu = stuList.get(i);
            removeStus.add(stu.getStudentCode());
            stuList.remove(i);
        }
    }
    
    /**随机晒除学生人数*/
    public static void randomRemoveStu(List<String> removeStus,
        Integer limitNumber, List<Student> stuList,Integer invincibleStusNum,Integer affinityStusNum,Integer normalStusNum,Integer teachingClassNum)
    {
        //随机删除看过容量的学生
        while (stuList.size() > limitNumber)
        {
            int i = RandomUtils.nextInt(stuList.size());
            Student stu = stuList.get(i);
            removeStus.add(stu.getStudentCode());
            Integer overSize = (invincibleStusNum
                    + affinityStusNum + normalStusNum)
                    - teachingClassNum;
            if(overSize>0) {
            	stuList.remove(i);
            }
        }
    }
    

}
