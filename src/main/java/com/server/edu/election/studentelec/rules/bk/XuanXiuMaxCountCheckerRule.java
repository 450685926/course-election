package com.server.edu.election.studentelec.rules.bk;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.server.edu.common.locale.I18nUtil;
import com.server.edu.election.constants.Constants;
import com.server.edu.election.dao.CourseDao;
import com.server.edu.election.dao.ElectionParameterDao;
import com.server.edu.election.entity.Course;
import com.server.edu.election.entity.ElectionParameter;
import com.server.edu.election.studentelec.cache.TeachingClassCache;
import com.server.edu.election.studentelec.context.ElecContext;
import com.server.edu.election.studentelec.context.ElecRespose;
import com.server.edu.election.studentelec.context.SelectedCourse;
import com.server.edu.election.studentelec.rules.AbstractElecRuleExceutor;
import com.server.edu.util.CollectionUtil;

import tk.mybatis.mapper.entity.Example;

/**
 * 是选修课门数上限规则检查
 */
@Component("XuanXiuMaxCountCheckerRule")
public class XuanXiuMaxCountCheckerRule extends AbstractElecRuleExceutor {


    @Autowired
    private ElectionParameterDao electionParameterDao;

    /**
     * 执行选课操作时
     */
    @Override
    public boolean checkRule(ElecContext context,
                             TeachingClassCache courseClass) {
        if (StringUtils.isNotBlank(courseClass.getCourseCode())
                && courseClass.getTeachClassId() != null) {
            if (!courseClass.isPublicElec()) {//是否选修课
                return true;
            }
            Set<SelectedCourse> selectedCourses = context.getSelectedCourses();
            if (CollectionUtil.isNotEmpty(selectedCourses)) {
                List<SelectedCourse> list = selectedCourses.stream()
                        .filter(c -> c.isPublicElec() == true)
                        .collect(Collectors.toList());
                int stsNum = list.size();
                // 选课门数上限
                ElectionParameter electionParameter =
                        electionParameterDao.selectByPrimaryKey(101L);

                int max = 0;
                try {
                    max = Integer.parseInt(electionParameter.getValue());
                } catch (NumberFormatException e) {
                    ElecRespose respose = context.getRespose();
                    e.printStackTrace();
                    respose.getFailedReasons()
                            .put(courseClass.getCourseCodeAndClassCode(),
                                    I18nUtil.getMsg(
                                            "ruleCheck.psrseError"));
                    return false;
                }

                if (max >= stsNum + Constants.ONE) {
                    return true;
                }
                ElecRespose respose = context.getRespose();
                respose.getFailedReasons()
                        .put(courseClass.getCourseCodeAndClassCode(),
                                I18nUtil.getMsg(
                                        "ruleCheck.xuanXiuMaxCountChecker"));
                return false;


            }
        }
        return true;
    }

}
