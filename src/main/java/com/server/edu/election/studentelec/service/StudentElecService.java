package com.server.edu.election.studentelec.service;


import com.server.edu.common.rest.RestResult;
import com.server.edu.election.studentelec.context.ElecRequest;
import com.server.edu.election.studentelec.context.ElecRespose;

/**
 * 选课请求的主入口
 */
public interface StudentElecService {
    /**
     * 加载学生数据
     * @return 当前状态
     */
    RestResult<ElecRespose> loading(Integer roundId, String studentId);

    /**
     * 选课
     * @param elecRequest
     * @return
     */
    RestResult<ElecRespose> elect(ElecRequest elecRequest);
    /**
     * 获取学生选课结果
     * 
     * @param roundId
     * @param studentId
     * @return
     * @see [类、类#方法、类#成员]
     */
    ElecRespose getElectResult(Integer roundId, String studentId);
}
