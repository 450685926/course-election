package com.server.edu.election.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.server.edu.common.PageCondition;
import com.server.edu.common.locale.I18nUtil;
import com.server.edu.election.constants.Constants;
import com.server.edu.election.dao.ExamArrangementDao;
import com.server.edu.election.dto.ExamArrangementDto;
import com.server.edu.election.entity.ExamArrangement;
import com.server.edu.election.service.ExamArrangementService;
import com.server.edu.exception.ParameterValidateException;
import com.server.edu.util.CollectionUtil;

import tk.mybatis.mapper.entity.Example;
@Service
public class ExamArrangementServiceImpl implements ExamArrangementService {
	@Autowired
	private ExamArrangementDao examArrangementDao;
	@Override
	public PageInfo<ExamArrangement> list(PageCondition<ExamArrangementDto> condition){
		PageHelper.startPage(condition.getPageNum_(), condition.getPageSize_());
		List<ExamArrangement> list = examArrangementDao.selectExamArrangements(condition.getCondition());
		PageInfo<ExamArrangement> pageInfo =new PageInfo<>(list);
		return pageInfo;
	}
	
	@Override
	public int update(ExamArrangement examArrangement) {
		int result = examArrangementDao.insert(examArrangement);
		if(result<=Constants.ZERO) {
			throw new ParameterValidateException(I18nUtil.getMsg("common.saveError",I18nUtil.getMsg("achievementLevel.examArrangement")));
		}
		return result;
	}
	
	@Override
	public int delete(List<Long> ids) {
		Example example = new Example(ExamArrangement.class);
		Example.Criteria criteria = example.createCriteria();
		criteria.andIn("id", ids);
		int result =examArrangementDao.deleteByExample(example);
		if(result<=Constants.ZERO) {
			throw new ParameterValidateException(I18nUtil.getMsg("common.failSuccess",I18nUtil.getMsg("achievementLevel.examArrangement")));
		}
		return result;
	}
	
    @Override
    public String addByExcel(List<ExamArrangement> datas)
    {
    	List<ExamArrangement> list = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        for (ExamArrangement data : datas)
        {
            String studentCode = StringUtils.trim(data.getStudentCode());
            String subject = StringUtils.trim(data.getSubject());
            if (StringUtils.isNotBlank(studentCode))
            {
            	Example example = new Example(ExamArrangement.class);
            	Example.Criteria criteria = example.createCriteria();
            	criteria.andEqualTo("studentCode",data.getStudentCode());
            	criteria.andEqualTo("subject",data.getSubject());
                List<ExamArrangement> examArrangements = this.examArrangementDao
                    .selectByExample(example);
                if(CollectionUtil.isNotEmpty(examArrangements)) {
                	sb.append("学生[" + studentCode + "]对应的["+subject+"]考试安排已经存在,");
                	}else {
                		list.add(data);
                	}
            }
        }
        int result = examArrangementDao.batchInsert(list);
        if(result<=Constants.ZERO) {
			throw new ParameterValidateException(I18nUtil.getMsg("common.saveError",I18nUtil.getMsg("achievementLevel.examArrangement")));
		}
        if (sb.length() > 0)
        {
            return sb.substring(0, sb.length() - 1);
        }
        return StringUtils.EMPTY;
    }

}
