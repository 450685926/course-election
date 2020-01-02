package com.server.edu.mutual.studentelec.service;

import com.server.edu.common.rest.RestResult;
import com.server.edu.election.studentelec.context.ElecRequest;
import com.server.edu.election.studentelec.context.ElecRespose;

/**
 * 本研互选选课请求的主入口
 *
 */
public interface StudentMutualElecService {
    /**
     * 加载学生数据
     * @return 当前状态
     */
    RestResult<ElecRespose> loading(ElecRequest elecRequest);

	/**
	 * 选课操作
	 * @param elecRequest
	 * @return
	 */
	RestResult<ElecRespose> elect(ElecRequest elecRequest);

	/**
	 * 获取学生选课结果
	 * @param elecRequest
	 * @return
	 */
	ElecRespose getElectResult(ElecRequest elecRequest);
}
