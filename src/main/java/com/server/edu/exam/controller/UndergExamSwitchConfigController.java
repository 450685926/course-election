package com.server.edu.exam.controller;

import com.server.edu.common.rest.RestResult;
import com.server.edu.exam.service.UndergExamSwitchConfigService;
import com.server.edu.exam.vo.UndergExamSwitchConfigVo;
import io.swagger.annotations.Info;
import io.swagger.annotations.SwaggerDefinition;
import org.apache.servicecomb.provider.rest.common.RestSchema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 功能描述：本科生排考开关控制器
 *
 * @ClassName UndergExamSwitchConfigController
 * @Author zhaoerhu
 * @Date 2019/8/19 15:59
 */
@SwaggerDefinition(info = @Info(title = "本科生排考开关", version = ""))
@RestSchema(schemaId = "UndergExamSwitchConfigController")
@RequestMapping("/undergExamSwitchConfig")
public class UndergExamSwitchConfigController {

    private static Logger LOG = LoggerFactory.getLogger(UndergExamSwitchConfigController.class);
    @Autowired
    private UndergExamSwitchConfigService undergExamSwitchConfigService;

    /**
     * 功能描述: 本科生排考开关信息新增
     *
     * @params: [undergExamSwitchConfigVo]
     * @return: com.server.edu.common.rest.RestResult<com.server.edu.election.vo.UndergExamSwitchConfigVo>
     * @author: zhaoerhu
     * @date: 2019/8/20 18:09
     */
    @PutMapping("addSwitchConfig")
    public RestResult<UndergExamSwitchConfigVo> addUndergExamSwitchConfig(@RequestBody UndergExamSwitchConfigVo undergExamSwitchConfigVo) {
        LOG.info("begin addUndergExamSwitchConfig...");
        try {
            return undergExamSwitchConfigService.addOrUpdateUndergExamSwitchConfig(undergExamSwitchConfigVo);
        } catch (Exception e) {
            LOG.error("e:" + e.toString());
            return RestResult.fail("开关操作失败");
        }
    }

    /**
     * 功能描述:本科生排考开关列表查询
     *
     * @params: [undergExamSwitchConfigVo]
     * @return: com.server.edu.common.rest.RestResult<com.server.edu.election.vo.UndergExamSwitchConfigVo>
     * @author: zhaoerhu
     * @date: 2019/8/20 19:08
     */
    @PostMapping("querySwitchConfigList")
    public RestResult<UndergExamSwitchConfigVo> queryUndergExamSwitchConfigList(@RequestBody UndergExamSwitchConfigVo undergExamSwitchConfigVo) {
        LOG.info("begin queryUndergExamSwitchConfigList...");
        try {
            return undergExamSwitchConfigService.queryUndergExamSwitchConfigList(undergExamSwitchConfigVo);
        } catch (Exception e) {
            LOG.error("e:" + e.toString());
            return RestResult.fail("开关查询失败");
        }
    }

    @DeleteMapping("deleteSwitchConfigBatch")
    public RestResult deleteSwitchConfigBatch(@RequestBody UndergExamSwitchConfigVo undergExamSwitchConfigVo) {
        LOG.info("begin deleteSwitchConfigBatch...");
        try {
            return undergExamSwitchConfigService.deleteSwitchConfigBatch(undergExamSwitchConfigVo);
        } catch (Exception e) {
            LOG.error("e:" + e.toString());
            return RestResult.fail("开关查询失败");
        }
    }
}
