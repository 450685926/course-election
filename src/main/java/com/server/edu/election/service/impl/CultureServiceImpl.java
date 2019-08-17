package com.server.edu.election.service.impl;

import com.server.edu.election.dao.CultureDao;
import com.server.edu.election.service.CultureService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Service
public class CultureServiceImpl implements CultureService {

    @Autowired
    private CultureDao cultureDao;

    @Override
    public Map<String, String> findStudentFirstLanguageCode(List<String> studentIds) {
        Map<String,String> map=new HashMap<>();
        for(String studentId :studentIds) {
            List<String> courseCodeList=cultureDao.getStudentScoreByStudentId(studentId);
            if(courseCodeList.size()>0&&courseCodeList!=null){
                String courseCode=courseCodeList.get(0);
                map.put(studentId,courseCode);
            }else{
                map.put(studentId,null);
            }
        }
        System.out.println(map);
        return map;
    }
}
