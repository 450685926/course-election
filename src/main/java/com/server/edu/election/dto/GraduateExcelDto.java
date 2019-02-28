package com.server.edu.election.dto;

import com.server.edu.election.entity.ElcNoGraduateStds;

/**
 * @description: 导入结业生
 * @author: bear
 * @create: 2019-02-27 22:15
 */
public class GraduateExcelDto extends ElcNoGraduateStds {
    private String graduateYearStr;

    public String getGraduateYearStr() {
        return graduateYearStr;
    }

    public void setGraduateYearStr(String graduateYearStr) {
        this.graduateYearStr = graduateYearStr;
    }
}
