package com.server.edu.election.query;

import com.server.edu.election.vo.PublicCourseVo;

import java.util.List;

public class PublicCourseTag {
    private String tag;

    private List<PublicCourseVo> list;

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public List<PublicCourseVo> getList() {
        return list;
    }

    public void setList(List<PublicCourseVo> list) {
        this.list = list;
    }
}
