package com.server.edu.exam.service;

import com.server.edu.common.rest.RestResult;
import com.server.edu.exam.vo.UndergExamSwitchConfigVo;

/**
 * 功能描述：本科生排考开关 service
 *
 * @ClassName UndergExamSwitchConfigService
 * @Author zhaoerhu
 * @Date 2019/8/19 16:06
 */
public interface UndergExamSwitchConfigService {
    RestResult addOrUpdateUndergExamSwitchConfig(UndergExamSwitchConfigVo undergExamSwitchConfigVo) throws Exception;

    RestResult queryUndergExamSwitchConfigList(UndergExamSwitchConfigVo undergExamSwitchConfigVo) throws Exception;

    RestResult deleteSwitchConfigBatch(UndergExamSwitchConfigVo undergExamSwitchConfigVo) throws Exception;
}
