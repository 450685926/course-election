package com.server.edu.election.studentelec.rules.bk;

import org.springframework.stereotype.Component;

import com.server.edu.election.studentelec.context.ElecContext;
import com.server.edu.election.studentelec.context.ElecCourseClass;
import com.server.edu.election.studentelec.rules.AbstractRuleExceutor;

/**
 * 第二轮选课时，不能选第一轮选课已经选满的课程<br>
 * 具体描述：
 * A任务人数上限90人，第一轮选满了90人，那么第二轮选课时<br>
 * 学生可能能够看到A任务（依据HideWhenFullRule是否勾上来确定），但是不能选A任务。
 * 如果在第二轮选课时，第一轮选上的某人退了A任务，那么学生就能选该任务了。
 */
@Component("SecondRdCantElect1stRdFullRule")
public class SecondRdCantElect1stRdFullRule extends AbstractRuleExceutor
{
    private static final String PARAM = "HIDE_WHEN_FIRST_ELECT_FULL";
    
    public SecondRdCantElect1stRdFullRule()
    {
        super();
    }
    
    @Override
    public int getOrder()
    {
        return Integer.MAX_VALUE;
    }
    
    public void prepare()
    {
        //context.getState().getParams().put(PARAM, true);
    }
    
    @Override
    public boolean checkRule(ElecContext context, ElecCourseClass courseClass)
    {
        //		ElectionCourseContext electContext = (ElectionCourseContext) context;
        //		// 如果不是选课那就算了
        //		ElectRuleType eltype = electContext.getOp();
        //		if(!ElectRuleType.ELECTION.equals(eltype) && !ElectRuleType.EXCHANGE.equals(eltype)) {
        //			return true;
        //		}
        //		Lesson lesson = electContext.getCourseTake().getLesson();
        //		// 第一轮选课人数
        //		StringBuilder queryStr = new StringBuilder();
        ////		queryStr
        ////			.append("select l.limit_count, l.reserved_count, (select count(*) from t_course_takes t where t.lesson_id=l.id and t.remark='1')")
        ////			.append(" from t_lessons l where l.id=").append(lesson.getId());
        //		
        //		queryStr.append(
        //				"select count(take.id) as allCount ,sum((case std.gender_id when 1 then 1 end)) as male, sum((case std.gender_id when 2 then 1 end)) as female")
        //				.append(" from t_course_takes take join c_students std on std.id = take.std_id join t_lessons lesson on lesson.id = take.lesson_id")
        //				.append(" where take.remark='1' and lesson.id=").append(lesson.getId());
        //		List<Object[]> counts = (List)entityDao.search(new SqlQuery(queryStr.toString()));
        //		if(CollectionUtils.isEmpty(counts)) {
        //			return true;
        //		}
        //		Object[] count = counts.get(0);
        //		int limitCount = lesson.getTeachClass().getLimitCount();
        //		int reservedCount = lesson.getTeachClass().getReservedCount();
        //		int firstRdCount = 0 ;
        //		if(count[0] != null) {
        //			firstRdCount = ((Number)count[0]).intValue();
        //		}
        //		if(limitCount - reservedCount - firstRdCount <= 0) {
        //			context.addMessage(new ElectMessage("第一轮已选满，不能再选", ElectRuleType.ELECTION, false, lesson));
        //			return false;
        //		}
        //		
        //		//男女生判断
        //		Integer male = lesson.getTeachClass().getMale();
        //		Integer female = lesson.getTeachClass().getFemale();
        //		if (male == null || female == null) {
        //			return true;
        //		}
        //		if (male == 0 || female == 0) {
        //			return true;
        //		}
        //		int maleCount = 0;
        //		int femaleCount = 0;
        //		if(null != count[1]){
        //			maleCount = ((Number)count[1]).intValue();
        //		}
        //		if(null != count[2]){
        //			femaleCount = ((Number)count[2]).intValue();
        //		}
        //		
        //		Long genderId = electContext.getState().getStd().getGenderId();
        //		if(genderId == 1l){//男生上限判断
        //			if (maleCount >= male) {
        //				context.addMessage(new ElectMessage("男生上限已满，不能再选",
        //						ElectRuleType.ELECTION, false, lesson));
        //				return false;
        //			}
        //		}else if (genderId == 2l){//女生上限判断
        //			if (femaleCount >= female) {
        //				context.addMessage(new ElectMessage("女生上限已满，不能再选",
        //						ElectRuleType.ELECTION, false, lesson));
        //				return false;
        //			}
        //		}
        
        return true;
    }
    
}
