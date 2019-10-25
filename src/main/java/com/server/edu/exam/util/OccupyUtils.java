package com.server.edu.exam.util;

import com.server.edu.arrangeoccupation.*;
import com.server.edu.common.rest.RestResult;
import com.server.edu.exam.entity.GraduateExamInfo;
import com.server.edu.exam.entity.GraduateExamRoom;
import com.server.edu.exam.entity.GraduateExamTeacher;
import com.server.edu.exam.rpc.BaseresServiceExamInvoker;
import com.server.edu.exception.ParameterValidateException;
import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author daichang
 * @description 资源占用工具类
 * @date 2019/10/10
 */
public class OccupyUtils {
    public static void addOccupy(List<GraduateExamRoom> room, GraduateExamInfo examInfo,String remark) {
        ArrayList<OccupationParam> occupationParams = new ArrayList<>();

        for (GraduateExamRoom examRoom : room) {

            //Long bussniessId = Long.valueOf(infoId + examRoom.getRoomId());
            OccupationParamsBuilder builder = new OccupationParamsBuilder(Usage.ExamArrange, examRoom.getId(), examInfo.getActualCalendarId(), IgnorePolicy.None);

            List<String> classNodes = Arrays.asList(examInfo.getClassNode().split(","));
            int startTime = Integer.parseInt(classNodes.get(0));
            int endTime = Integer.parseInt(classNodes.get(classNodes.size() - 1));
            builder.occupy(examRoom.getRoomId(), ResourceType.Classroom, remark, examInfo.getCampus())
                    .addTime(WeekDay.valueOf(examInfo.getWeekDay()), startTime, endTime, Arrays.asList(examInfo.getWeekNumber()));
            List<OccupationParam> params = builder.build(); // 这个参数就可以传递给对应的接口了
            occupationParams.add(params.get(0));
        }

        RestResult<List<ConflictMessage>> result = BaseresServiceExamInvoker.addOccupy(occupationParams);
        if (result.getCode() != 200) {
            throwException(result.getData());
        }

    }

    public static void addTeaOccupy(List<GraduateExamTeacher> teachers, GraduateExamInfo examInfo) {
        ArrayList<OccupationParam> occupationParams = new ArrayList<>();

        for (GraduateExamTeacher teacher : teachers) {

            OccupationParamsBuilder builder = new OccupationParamsBuilder(Usage.ExamArrange, teacher.getExamRoomId(), examInfo.getActualCalendarId(), IgnorePolicy.None);

            List<String> classNodes = Arrays.asList(examInfo.getClassNode().split(","));
            int startTime = Integer.parseInt(classNodes.get(0));
            int endTime = Integer.parseInt(classNodes.get(classNodes.size() - 1));

            builder.occupy(teacher.getTeacherCode(), ResourceType.Teacher, "添加研究生排考占用", examInfo.getCampus())
                    .addTime(WeekDay.valueOf(examInfo.getWeekDay()), startTime, endTime, Arrays.asList(examInfo.getWeekNumber()));
            List<OccupationParam> params = builder.build(); // 这个参数就可以传递给对应的接口了
            occupationParams.add(params.get(0));
        }

        RestResult<List<ConflictMessage>> result = BaseresServiceExamInvoker.addOccupy(occupationParams);
        if (result.getCode() != 200) {

            throwException(result.getData());
        }

    }

    public static void delOccupy(Long bussniessId, GraduateExamInfo examInfo) {

        OccupationParamsBuilder builder = new OccupationParamsBuilder(Usage.ExamArrange, examInfo.getActualCalendarId());
        List<OccupationParam> params = builder.buildDeleteParams(bussniessId);

        RestResult<List<ConflictMessage>> result = BaseresServiceExamInvoker.delOccupy(params);
        if (result.getCode() != 200) {


            throw new ParameterValidateException("有冲突");
        } else {
            System.out.println("删除占用成功");
        }

    }

    public static String sortBussniessId(String examInfoIds) {
        List<String> list = Arrays.asList(examInfoIds.split(","));
        List<Long> collect = list.stream().map(a -> Long.parseLong(a)).collect(Collectors.toList());
        Collections.sort(collect);
        return StringUtils.join(collect, "");
    }

    public static void throwException(List<ConflictMessage> data){
        StringBuilder builder = new StringBuilder();
        for (ConflictMessage datum : data) {
            builder.append(datum.getName()).append(",时间冲突为 :").append(datum.getTime()).append(";");
        }
        builder.substring(0,builder.length()-1);
        throw new ParameterValidateException(builder.toString());
    }

}
