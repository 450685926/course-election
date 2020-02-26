package com.server.edu.mutual.util;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.server.edu.election.constants.Constants;
import com.server.edu.mutual.dao.ScoreRecordModeDao;
import com.server.edu.mutual.vo.Scoresetting;
import com.server.edu.mutual.vo.ScoresettingDetail;
import com.server.edu.mutual.vo.StudentScoreInfoVo;
import com.server.edu.session.util.SessionUtils;
import com.server.edu.session.util.entity.Session;

@Component
public class GradePointUtil {

    @Autowired
    private ScoreRecordModeDao scoreRecordModeDao;
    private static ScoreRecordModeDao scoreRecordModeDao1;

    @PostConstruct
    public void init1() {
        scoreRecordModeDao1 = this.scoreRecordModeDao;
    }


    public static List<StudentScoreInfoVo> getGradePoint(List<StudentScoreInfoVo> list) {

        Session session = SessionUtils.getCurrentSession();
        String pid = session.getCurrentManageDptId();
        List<Scoresetting> scoresettings = scoreRecordModeDao1.queryRecordMode();
        List<ScoresettingDetail> setDetailList = new ArrayList<>();
        for (StudentScoreInfoVo vo : list) {
            if (StringUtils.isEmpty(pid)) {
                if (null != vo && StringUtils.isNotEmpty(vo.getCourseCode())) {
                    String courseCode = vo.getCourseCode();
                    String cal = String.valueOf(vo.getCalendarId());
                    pid = scoreRecordModeDao1.findPidByCourseCode(courseCode, cal);
                }
            }
            if (StringUtils.isEmpty(pid)) {
                pid = "2";
            }
            if (StringUtils.isEmpty(vo.getRecoredType())) {
                vo.setRecoredType("1");
            }
            for (Scoresetting scoresetting : scoresettings) {
                if (vo.getRecoredType().equals(String.valueOf(scoresetting.getCoretypeCode()))
                        && pid.equals(scoresetting.getManageDptId())) {
                    setDetailList = scoresetting.getScoresettingDetailList();
                    break;
                }
            }
            if (Constants.TEACHING_CLASS_SCORE_XDLB_MX.equals(vo.getLearnType())
                    || Constants.EXAMTYPE_MX.equals(vo.getExamType())) {
                vo.setTotalMarkScore("60");
            }
            if (StringUtils.isNotEmpty(vo.getTotalMarkScore())) {
                //给非百分制成绩设置默认成绩
                if (!Constants.TEACHING_CLASS_SCORE_CJJLFS_BFZ.equals(vo.getRecoredType())
                        || !Constants.TEACHING_CLASS_SCORE_CJJLFS_SFZ.equals(vo.getRecoredType())
                        || !Constants.TEACHING_CLASS_SCORE_CJJLFS_ESFZ.equals(vo.getRecoredType())) {
                    for (ScoresettingDetail sd : setDetailList) {
                        if (vo.getTotalMarkScore().equals(sd.getScoreName())) {
                            vo.setTotalMarkScore(String.valueOf(sd.getDefaultScore()));
                            break;
                        }
                    }
                }
                //设置成绩的绩点
                for (ScoresettingDetail sd : setDetailList) {
                    if (Double.valueOf(vo.getTotalMarkScore()) >= sd.getMinScore() && Double.valueOf(vo.getTotalMarkScore()) <= sd.getMaxScore()) {
                        vo.setGreadePoint(sd.getScorePoint());
                        break;
                    }
                }
            } else {
                vo.setTotalMarkScore("0");
                vo.setGreadePoint(0d);
            }
            if (null == vo.getGreadePoint()) {
                vo.setGreadePoint(0d);
            }
        }
        return list;
    }
    

}
