package com.server.edu.election.studentelec.rules.bk;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.server.edu.election.dao.ElectionConstantsDao;
import com.server.edu.election.entity.ElectionConstants;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.server.edu.common.locale.I18nUtil;
import com.server.edu.election.constants.Constants;
import com.server.edu.election.dao.ElectionParameterDao;
import com.server.edu.election.entity.ElectionParameter;
import com.server.edu.election.studentelec.cache.TeachingClassCache;
import com.server.edu.election.studentelec.context.ElecRespose;
import com.server.edu.election.studentelec.context.bk.ElecContextBk;
import com.server.edu.election.studentelec.context.bk.SelectedCourse;
import com.server.edu.election.studentelec.rules.AbstractElecRuleExceutorBk;
import com.server.edu.util.CollectionUtil;
import tk.mybatis.mapper.entity.Example;

/**
 * 是选修课门数上限规则检查
 * XuanXiuMaxCountChecker
 */
@Component("XuanXiuMaxCountCheckerRule")
public class XuanXiuMaxCountCheckerRule extends AbstractElecRuleExceutorBk
{
    
    @Autowired
    private ElectionConstantsDao electionConstantsDao;

    final String MAXELECTIVECOURSECOUNT = "MAXELECTIVECOURSECOUNT";
    
    /**
     * 执行选课操作时
     */
    @Override
    public boolean checkRule(ElecContextBk context,
        TeachingClassCache courseClass)
    {
        String courseCode = courseClass.getCourseCode();
        boolean publicElec = courseClass.isPublicElec();
        if (StringUtils.isNotBlank(courseCode)
            && courseClass.getTeachClassId() != null)
        {
            if (!publicElec)
            {//是否选修课
                return true;
            }
            Set<SelectedCourse> selectedCourses = context.getSelectedCourses();
            if (CollectionUtil.isNotEmpty(selectedCourses))
            {
                List<SelectedCourse> list = selectedCourses.stream()
                    .filter(c -> c.getCourse().isPublicElec() == true)
                    .collect(Collectors.toList());
                int stsNum = list.size();
                // 选课门数上限
//                ElectionParameter electionParameter =
//                    electionParameterDao.selectByPrimaryKey(101L);

                Example example = new Example(ElectionConstants.class);
                Example.Criteria criteria = example.createCriteria();
                criteria.andEqualTo("managerDeptId", context.getRequest().getProjectId());
                criteria.andEqualTo("key",MAXELECTIVECOURSECOUNT);
                ElectionConstants electionConstant = electionConstantsDao.selectOneByExample(example);
                int max = 0;
                try
                {
                    max = Integer.parseInt(electionConstant.getValue());
                }
                catch (NumberFormatException e)
                {
                    ElecRespose respose = context.getRespose();
                    e.printStackTrace();
                    respose.getFailedReasons()
                        .put(courseClass.getCourseCodeAndClassCode(),
                            I18nUtil.getMsg("ruleCheck.psrseError"));
                    return false;
                }
                
                if (max >= stsNum + Constants.ONE)
                {
                    return true;
                }
                ElecRespose respose = context.getRespose();
                respose.getFailedReasons()
                    .put(courseClass.getCourseCodeAndClassCode(),
                        I18nUtil.getMsg("ruleCheck.xuanXiuMaxCountChecker"));
                return false;
                
            }
        }
        return true;
    }
    
}
