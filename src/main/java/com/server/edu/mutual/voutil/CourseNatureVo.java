package com.server.edu.mutual.voutil;

import java.io.Serializable;

public class CourseNatureVo implements Serializable{

    /*中文名称*/
    private String nameCN;
    /*英文名称*/
    private String nameEN;
    /*编码*/
    private String code;
    /*类型*/
    private String type;

    public CourseNatureVo() {
    }

    public CourseNatureVo(String nameCN, String nameEN, String code, String type) {
        this.nameCN = nameCN;
        this.nameEN = nameEN;
        this.code = code;
        this.type = type;
    }

    public String getNameCN() {
        return nameCN;
    }

    public void setNameCN(String nameCN) {
        this.nameCN = nameCN;
    }

    public String getNameEN() {
        return nameEN;
    }

    public void setNameEN(String nameEN) {
        this.nameEN = nameEN;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
