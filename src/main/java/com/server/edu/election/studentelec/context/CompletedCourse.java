package com.server.edu.election.studentelec.context;

/**
 * 已完成课程
 */
public class CompletedCourse extends ElecCourse{
    /** 成绩 */
    private String score;

    /**等级*/
    private String rank;

    /** 是否作弊 */
    private boolean cheat;

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public boolean isCheat() {
        return cheat;
    }

    public void setCheat(boolean cheat) {
        this.cheat = cheat;
    }
}