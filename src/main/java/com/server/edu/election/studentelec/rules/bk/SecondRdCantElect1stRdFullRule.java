package com.server.edu.election.studentelec.rules.bk;

import com.server.edu.election.dao.TeachingClassDao;
import com.server.edu.election.util.TableIndexUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.server.edu.common.locale.I18nUtil;
import com.server.edu.election.entity.ElectionRounds;
import com.server.edu.election.studentelec.cache.TeachingClassCache;
import com.server.edu.election.studentelec.context.ElecRespose;
import com.server.edu.election.studentelec.context.bk.ElecContextBk;
import com.server.edu.election.studentelec.rules.AbstractElecRuleExceutorBk;
import com.server.edu.election.studentelec.service.impl.RoundDataProvider;

/**
 * 第二轮选课时，不能选第一轮选课已经选满的课程<br>
 * 具体描述： A任务人数上限90人，第一轮选满了90人，那么第二轮选课时<br>
 * 学生可能能够看到A任务（依据HideWhenFullRule是否勾上来确定），但是不能选A任务。
 * 如果在第二轮选课时，第一轮选上的某人退了A任务，那么学生就能选该任务了。
 * SecondRdCantElect1stRdFullRule
 */
@Component("SecondRdCantElect1stRdFullRule")
public class SecondRdCantElect1stRdFullRule extends AbstractElecRuleExceutorBk
{
    @Autowired
    private RoundDataProvider dataProvider;

    @Autowired
    private TeachingClassDao classDao;
    
    @Override
    public int getOrder()
    {
        return Integer.MAX_VALUE;
    }
    
    @Override
    public boolean checkRule(ElecContextBk context,
        TeachingClassCache courseClass)
    {
        ElectionRounds round =
            dataProvider.getRound(context.getRequest().getRoundId());
        
        if (round == null)
        {
            String msg = String.format("electionRounds not find roundId=%s",
                context.getRequest().getRoundId());
            throw new RuntimeException(msg);
        }
        if (round.getTurn().intValue() == 2)
        {//第二轮
            Integer maxNumber = courseClass.getMaxNumber();
            //获取第一轮选课人数
            Integer FirstTurnNumber = classDao.getFirstTurnNum(round.getCalendarId(), TableIndexUtil.getIndex(round.getCalendarId()),courseClass.getTeachClassId());

            if (maxNumber != null && FirstTurnNumber != null
                && FirstTurnNumber.intValue() + 1 > maxNumber.intValue())
            {
                ElecRespose respose = context.getRespose();
                respose.getFailedReasons()
                    .put(courseClass.getTeachClassCode() + courseClass.getCourseName(),
                        I18nUtil.getMsg("ruleCheck.classNumberLimit"));
                return false;
            }
        }
        
        return true;
    }
    
}
