package com.server.edu.election.studentelec.rules.bk;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.server.edu.election.dao.StudentNumDao;
import com.server.edu.election.entity.StudentNum;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.server.edu.common.locale.I18nUtil;
import com.server.edu.common.vo.SchoolCalendarVo;
import com.server.edu.dictionary.utils.SchoolCalendarCacheUtil;
import com.server.edu.election.constants.Constants;
import com.server.edu.election.dao.ElectionConstantsDao;
import com.server.edu.election.dao.StudentDao;
import com.server.edu.election.dao.StudentUndergraduateScoreInfoDao;
import com.server.edu.election.entity.ElectionConstants;
import com.server.edu.election.entity.Student;
import com.server.edu.election.entity.StudentUndergraduateScoreInfo;
import com.server.edu.election.studentelec.cache.TeachingClassCache;
import com.server.edu.election.studentelec.context.ElecRequest;
import com.server.edu.election.studentelec.context.ElecRespose;
import com.server.edu.election.studentelec.context.bk.ElecContextBk;
import com.server.edu.election.studentelec.context.bk.ElecContextLogin;
import com.server.edu.election.studentelec.rules.AbstractElecRuleExceutorBk;
import com.server.edu.election.studentelec.rules.AbstractLoginRuleExceutorBk;
import com.server.edu.util.CollectionUtil;

import tk.mybatis.mapper.entity.Example;

/**
 * 预警学生不能选课
 * LoserGoodbyeRule
 */
@Component("LoserNotElcRule")
public class LoserNotElcRule extends AbstractLoginRuleExceutorBk
{
    @Autowired
    private StudentNumDao studentNumDao;
    @Autowired
    private ElectionConstantsDao electionConstantsDao;
    
    @Override
    public boolean checkRule(ElecContextLogin context,
        TeachingClassCache courseClass)
    {
        String studentId = context.getRequest().getStudentId();
        Example example = new Example(StudentNum.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("studentNum", studentId);
        criteria.andEqualTo("isPass", Constants.UN_PASS);
//        Student stu = studentDao.isLoserStu(request.getRoundId(), studentId);
//        if (stu == null)
//        {
//            return true;
//        }
        List<StudentNum> stuList = studentNumDao.selectByExample(example);
        if (CollectionUtil.isNotEmpty(stuList))
        {
        	Double creditTotal = stuList.stream().mapToDouble(StudentNum::getCredit).sum();
        	Example conExample = new Example(ElectionConstants.class);
        	conExample.createCriteria().andEqualTo("key", "MAXFAILCREDITS").andEqualTo("managerDeptId", 1);
        	ElectionConstants electionConstants = electionConstantsDao.selectOneByExample(conExample);
        	if(electionConstants!=null) {
            	if(StringUtils.isNotBlank(electionConstants.getValue())) {
                	Double value =Double.parseDouble(electionConstants.getValue());
                	if(creditTotal>=value) {
                		ElecRespose respose = context.getRespose();
                        Map<String, String> failedReasons = new HashMap<>();
                        failedReasons
                            .put("01",
                                I18nUtil.getMsg("ruleCheck.isLoserStu"));
                        respose.setFailedReasons(failedReasons);
                		return false;
                	}
            	}
        	}
        }
        return true;
        
    }
}
