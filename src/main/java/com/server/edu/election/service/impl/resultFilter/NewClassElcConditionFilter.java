package com.server.edu.election.service.impl.resultFilter;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.server.edu.election.constants.Constants;
import com.server.edu.election.dao.TeachingClassElectiveRestrictAttrDao;
import com.server.edu.election.dto.AutoRemoveDto;
import com.server.edu.election.dto.SuggestProfessionDto;
import com.server.edu.election.entity.Student;
import com.server.edu.election.entity.TeachingClassElectiveRestrictAttr;
import com.server.edu.util.CollectionUtil;

public class NewClassElcConditionFilter {

    private TeachingClassElectiveRestrictAttrDao restrictAttrDao;
    
    private AutoRemoveDto dto;
    
    /**选课限制条件*/
    private TeachingClassElectiveRestrictAttr restrictAttr;
    
    /**选课限制专业人数配比*/
    private Map<String, Integer> profNumMap;
    
    /**具体学生*/
    private List<String> restrictStus;
    
    public NewClassElcConditionFilter(AutoRemoveDto dto,
        TeachingClassElectiveRestrictAttrDao classElectiveRestrictAttrDao)
    {
        this.dto = dto;
        this.restrictAttrDao = classElectiveRestrictAttrDao;
    }
    
    public void init()
    {
    	List<SuggestProfessionDto> restrictProfessionList =dto.getRestrictProfessionList();
        //选课年级专业人数
        profNumMap = GradAndPreFilter.sumPerson(restrictProfessionList);
        this.restrictStus =dto.getRestrictStus();
        this.restrictAttr = dto.getClassAttrList();
    }
    
    public void execute(List<Student> stuList, List<String> removeStus)
    {
        // 按教学班选课条件
        if (dto.getClassElcCondition())
        {
            //0. 删除指定学生外的人
            if (CollectionUtil.isNotEmpty(restrictStus))
            {
                Iterator<Student> iterator = stuList.iterator();
                while (iterator.hasNext())
                {
                    Student stu = iterator.next();
                    if (!restrictStus.contains(stu.getStudentCode()))
                    {
                        removeStus.add(stu.getStudentCode());
                        iterator.remove();
                    }
                }
            }
            //1. 按教学班选课限制的专业与人数
            NewGradAndPreFilter.gradAndPre(stuList, profNumMap, removeStus);
            NewGradAndPreFilter.gradAndPrePeople(stuList, profNumMap, removeStus,dto.getFirstStudent(),dto.getTurn());
            if (null == restrictAttr)
            {
                return;
            }
            //2. 将男女分开类，1男性 2女性
            Map<Integer, List<Student>> collect = stuList.stream()
                .collect(Collectors.groupingBy(Student::getSex));
            List<Student> firstStudent = dto.getFirstStudent();
            Map<Integer, List<Student>> firstCollect = firstStudent.stream()
                    .collect(Collectors.groupingBy(Student::getSex));
            String isDivsex = restrictAttr.getIsDivsex();
            isDivsex = StringUtils.isEmpty(isDivsex) ? "0" : isDivsex;
            List<Student> newStudents = new ArrayList<>();
            //3. 删除超过人数的男生或女生
            //是否男女生班 0：不区分  1：男生班 2：女生班
            Integer numberMale = restrictAttr.getNumberMale();
            List<Student> maleStus = collect.get(1);
            Integer numberFemale = restrictAttr.getNumberFemale();
            List<Student> femaleStus = collect.get(2);
            List<Student> firstStus = new ArrayList<>();
            if(CollectionUtil.isNotEmptyMap(firstCollect)) {
            	firstStus =  firstCollect.get(1);
            }
        	if(Constants.SECOND.equals(dto.getTurn())) {
        		numberMale = numberMale- firstStus.size();
        	}
            if (CollectionUtil.isNotEmpty(maleStus))
            {
                if (StringUtils.containsAny(isDivsex, '0')
                    && null != numberMale && 0 != numberMale && maleStus.size() > numberMale)
                {
                    NewGradAndPreFilter
                        .randomRemove(removeStus, numberMale, maleStus);// 删除超过的男生
                    newStudents.addAll(maleStus);
                }
                if (StringUtils.containsAny(isDivsex, '2')
                         )
                {
                	if(Constants.SECOND.equals(dto.getTurn())) {
                		numberMale = numberMale- firstStus.size();
                	}
                	removeStus.addAll(maleStus.stream().map(Student ::getStudentCode).collect(Collectors.toList()));
//                	NewGradAndPreFilter
//                            .randomRemove(removeStus, numberMale, maleStus);// 删除超过的男生
                	newStudents.addAll(femaleStus);
                }
            }
            if (CollectionUtil.isNotEmpty(femaleStus))
            {
                if (StringUtils.containsAny(isDivsex, '0')
                    && null != numberFemale  && 0 != numberFemale && femaleStus.size() > numberFemale)
                {
                	NewGradAndPreFilter
                        .randomRemove(removeStus, numberMale, femaleStus);// 删除超过的女生
                	newStudents.addAll(femaleStus);
                }
                if (StringUtils.containsAny(isDivsex, '1')
                    && null != numberFemale  && 0 != numberFemale)
                {
//                	NewGradAndPreFilter
//                        .randomRemove(removeStus, numberMale, femaleStus);// 删除超过的女生
                	removeStus.addAll(femaleStus.stream().map(Student ::getStudentCode).collect(Collectors.toList()));
                	newStudents.addAll(maleStus);
                }
            }
            if(CollectionUtil.isNotEmpty(newStudents)) {
            	stuList.clear();
            	stuList.addAll(newStudents);
            }
            //4. 删除其它不满足条件的学生
            if (CollectionUtil.isNotEmpty(stuList))
            {
                Iterator<Student> iterator = stuList.iterator();
                while (iterator.hasNext())
                {
                    Student stu = iterator.next();
                    if ((isNotBlank(restrictAttr.getTrainingLevel())
                        && !restrictAttr.getTrainingLevel()
                            .equals(stu.getTrainingLevel()))// 培养层次
                        || (isNotBlank(restrictAttr.getSpcialPlan())
                            && !restrictAttr.getSpcialPlan()
                                .equals(stu.getSpcialPlan()))// 专项计划
                        || (isNotBlank(restrictAttr.getIsOverseas())
                            && !restrictAttr.getIsOverseas()
                                .equals(stu.getIsOverseas())))// 是否留学生
                    {
                        removeStus.add(stu.getStudentCode());
                        iterator.remove();
                    }
                }
            }
        }
    }
    
}
