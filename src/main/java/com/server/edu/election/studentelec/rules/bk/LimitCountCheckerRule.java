package com.server.edu.election.studentelec.rules.bk;

import org.springframework.stereotype.Component;

import com.server.edu.election.studentelec.context.ElecContext;
import com.server.edu.election.studentelec.cache.TeachingClassCache;
import com.server.edu.election.studentelec.rules.AbstractRuleExceutor;

/**
 * 人数上限检查
 * 
 */
@Component("LimitCountCheckerRule")
public class LimitCountCheckerRule extends AbstractRuleExceutor {

	public LimitCountCheckerRule() {
		super();
	}

	@Override
	public int getOrder() {
		return Integer.MAX_VALUE;
	}

	@Override
	public boolean checkRule(ElecContext context, TeachingClassCache courseClass) {
//        ElectionCourseContext electContext = (ElectionCourseContext)context;
//        // 获取匹配的授课对象组
//        List<CourseLimitGroup> limitGroups =
//            matchCourseLimitGroup(electContext.getCourseTake(),
//                electContext.getState());
//        if (limitGroups.isEmpty())
//        {
//            String sql =
//                "update t_lessons set std_count=std_count+1 where std_count<(limit_count-reserved_count) and id=?";
//            int update = electionDao.updateStdCount(sql,
//                electContext.getLesson().getId());
//            if (update == 0)
//            {
//                context.addMessage(new ElectMessage("人数已满",
//                    ElectRuleType.ELECTION, false, electContext.getLesson()));
//                return false;
//            }
//        }
//        else
//        {
//            CourseLimitGroup limitGroup = null;
//            for (CourseLimitGroup courseLimitGroup : limitGroups)
//            {
//                if (courseLimitGroup.getMaxCount() == 0 || courseLimitGroup
//                    .getCurCount() < courseLimitGroup.getMaxCount())
//                {
//                    limitGroup = courseLimitGroup;
//                    break;
//                }
//            }
//            if (limitGroup == null)
//            {
//                context.addMessage(new ElectMessage("人数已满",
//                    ElectRuleType.ELECTION, false, electContext.getLesson()));
//                return false;
//            }
//            String sql =
//                "update t_lessons set std_count=std_count+1 where std_count<(limit_count-reserved_count) and id=?";
//            int update = electionDao.updateStdCount(sql,
//                electContext.getLesson().getId());
//            if (update == 0)
//            {
//                context.addMessage(new ElectMessage("人数已满",
//                    ElectRuleType.ELECTION, false, electContext.getLesson()));
//                return false;
//            }
//            else
//            {
//                // 检查完Lesson人数上线后update授课对象组,如果不能update就将Lesson的人数还原
//                sql =
//                    "update t_course_limit_groups set cur_count = cur_count+1 where (cur_count<max_count or max_count=0) and id=?";
//                update = electionDao.updateStdCount(sql, limitGroup.getId());
//                if (update == 0)
//                {
//                    context.addMessage(
//                        new ElectMessage("人数已满", ElectRuleType.ELECTION, false,
//                            electContext.getLesson()));
//                    sql =
//                        "update t_lessons set std_count=std_count-1 where id=?";
//                    electionDao.updateStdCount(sql,
//                        electContext.getLesson().getId());
//                    return false;
//                }
//                else
//                {
//                    electContext.getCourseTake().setLimitGroup(limitGroup);
//                }
//            }
//        }
		return true;
	}

	public void prepare() {
//        if (!context.isPreparedData(PreparedDataName.CHECK_MAX_LIMIT_COUNT))
//        {
//            context.getState().setCheckMaxLimitCount(true);
//            context.addPreparedDataName(PreparedDataName.CHECK_MAX_LIMIT_COUNT);
//        }
	}

//    protected List<CourseLimitGroup> matchCourseLimitGroup(CourseTake take,
//        ElectState state)
//    {
//        List<CourseLimitGroup> limitGroups = new ArrayList<>();
//        for (CourseLimitGroup group : take.getLesson()
//            .getTeachClass()
//            .getLimitGroups())
//        {
//            boolean groupPass = true;
//            if (group.isForClass())
//            {
//                for (CourseLimitItem item : group.getItems())
//                {
//                    boolean itemPass = true;
//                    Operator op = item.getOperator();
//                    Long metaId = item.getMeta().getId();
//                    Set<String> values = CollectionUtils
//                        .newHashSet(item.getContent().split(","));
//                    String value = null;
//                    if (metaId.equals(CourseLimitMeta.ADMINCLASS))
//                    {
//                        value = state.getStd().getAdminclassId() + "";
//                    }
//                    else if (metaId.equals(CourseLimitMeta.DEPARTMENT))
//                    {
//                        value = state.getStd().getDepartId() + "";
//                    }
//                    else if (metaId.equals(CourseLimitMeta.DIRECTION))
//                    {
//                        value = state.getStd().getDirectionId() + "";
//                    }
//                    else if (metaId.equals(CourseLimitMeta.EDUCATION))
//                    {
//                        value = state.getStd().getEducationId() + "";
//                    }
//                    else if (metaId.equals(CourseLimitMeta.GENDER))
//                    {
//                        value = state.getStd().getGenderId() + "";
//                    }
//                    else if (metaId.equals(CourseLimitMeta.GRADE))
//                    {
//                        value = state.getStd().getGrade();
//                    }
//                    else if (metaId.equals(CourseLimitMeta.MAJOR))
//                    {
//                        value = state.getStd().getMajorId() + "";
//                    }
//                    else if (metaId.equals(CourseLimitMeta.STDTYPE))
//                    {
//                        value = state.getStd().getStdTypeId() + "";
//                    }
//                    else if (metaId.equals(CourseLimitMeta.PROGRAM))
//                    {
//                        value = state.getStd().getProgramId() + "";
//                    }
//                    if (op.equals(Operator.EQUAL) || op.equals(Operator.IN))
//                    {
//                        itemPass = values.isEmpty() || values.contains(value);
//                    }
//                    else
//                    {
//                        itemPass = !values.isEmpty() && !values.contains(value);
//                    }
//                    // 如果单项没通过，那么就整个组就不通过了
//                    if (!itemPass)
//                    {
//                        groupPass = false;
//                        break;
//                    }
//                }
//            }
//            else
//            {
//                for (CourseLimitItem item : group.getItems())
//                {
//                    boolean itemPass = true;
//                    Operator op = item.getOperator();
//                    Long metaId = item.getMeta().getId();
//                    if (metaId.equals(CourseLimitMeta.ADMINCLASS))
//                    {
//                        continue;
//                    }
//                    else if (metaId.equals(CourseLimitMeta.DEPARTMENT))
//                    {
//                        continue;
//                    }
//                    else if (metaId.equals(CourseLimitMeta.DIRECTION))
//                    {
//                        continue;
//                    }
//                    else if (metaId.equals(CourseLimitMeta.PROGRAM))
//                    {
//                        continue;
//                    }
//                    Set<String> values =
//                        CollectUtils.newHashSet(item.getContent().split(","));
//                    String value = null;
//                    if (metaId.equals(CourseLimitMeta.EDUCATION))
//                    {
//                        value = state.getStd().getEducationId() + "";
//                    }
//                    else if (metaId.equals(CourseLimitMeta.GENDER))
//                    {
//                        value = state.getStd().getGenderId() + "";
//                    }
//                    else if (metaId.equals(CourseLimitMeta.GRADE))
//                    {
//                        value = state.getStd().getGrade();
//                    }
//                    else if (metaId.equals(CourseLimitMeta.MAJOR))
//                    {
//                        value = state.getStd().getMajorId() + "";
//                    }
//                    else if (metaId.equals(CourseLimitMeta.STDTYPE))
//                    {
//                        value = state.getStd().getStdTypeId() + "";
//                    }
//                    if (metaId.equals(CourseLimitMeta.MAJOR))
//                    {
//                        if (op.equals(Operator.EQUAL) || op.equals(Operator.IN))
//                        {
//                            itemPass =
//                                !values.isEmpty() && values.contains(value);
//                        }
//                        else
//                        {
//                            itemPass =
//                                !values.isEmpty() && !values.contains(value);
//                        }
//                        itemPass = !itemPass;
//                    }
//                    else
//                    {
//                        if (op.equals(Operator.EQUAL) || op.equals(Operator.IN))
//                        {
//                            itemPass =
//                                values.isEmpty() || values.contains(value);
//                        }
//                        else
//                        {
//                            itemPass =
//                                !values.isEmpty() && !values.contains(value);
//                        }
//                    }
//                    if (!itemPass)
//                    {
//                        groupPass = false;
//                        break;
//                    }
//                }
//            }
//            if (groupPass)
//            {
//                limitGroups.add(group);
//            }
//        }
//        return limitGroups;
//    }
}