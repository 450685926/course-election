package com.server.edu.election.config;

import com.server.edu.util.excel.CellValueHandler;
import com.server.edu.util.excel.GeneralExcelCell;

/**
 * @author: kan yuanfeng
 * @date: 2019/7/1 09:51
 * @description:
 */
public class DoubleHandler implements CellValueHandler {

    @Override
    public String handler(String value, Object o, GeneralExcelCell generalExcelCell) {
        if (value.endsWith(".0")){
            value =  value.substring(0,value.indexOf("."));
        }
        return value;
    }
}
