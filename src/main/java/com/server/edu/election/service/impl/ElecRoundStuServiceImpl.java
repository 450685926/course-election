package com.server.edu.election.service.impl;

import java.io.File;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.server.edu.election.studentelec.service.cache.RoundCacheService;
import com.server.edu.election.studentelec.service.impl.RoundDataProvider;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.server.edu.common.PageCondition;
import com.server.edu.common.rest.PageResult;
import com.server.edu.election.constants.Constants;
import com.server.edu.election.constants.RoundMode;
import com.server.edu.election.dao.ElcNoGraduateStdsDao;
import com.server.edu.election.dao.ElecRoundStuDao;
import com.server.edu.election.dao.ElecRoundsDao;
import com.server.edu.election.dao.StudentDao;
import com.server.edu.election.dto.ElecRoundStuDto;
import com.server.edu.election.dto.Student4Elc;
import com.server.edu.election.entity.ElcNoGraduateStds;
import com.server.edu.election.entity.ElectionRounds;
import com.server.edu.election.entity.ElectionRoundsStu;
import com.server.edu.election.entity.Student;
import com.server.edu.election.query.ElecRoundStuQuery;
import com.server.edu.election.service.ElecRoundStuService;
import com.server.edu.exception.ParameterValidateException;
import com.server.edu.session.util.SessionUtils;
import com.server.edu.session.util.entity.Session;
import com.server.edu.util.CollectionUtil;
import com.server.edu.util.async.AsyncExecuter;
import com.server.edu.util.async.AsyncProcessUtil;
import com.server.edu.util.async.AsyncResult;

import tk.mybatis.mapper.entity.Example;
@Service
public class ElecRoundStuServiceImpl implements ElecRoundStuService
{
    @Autowired
    private ElecRoundStuDao elecRoundStuDao;
    
    @Autowired
    private ElecRoundsDao elecRoundsDao;

    @Autowired
    private StudentDao studentDao;

    @Autowired
    private ElcNoGraduateStdsDao noGraduateStdsDao;
    
    @Autowired
    private RedisTemplate<String, AsyncResult> redisTemplate;

    @Autowired
    private RoundDataProvider dataProvider;

    
    @Override
    public PageResult<Student4Elc> listPage(
        PageCondition<ElecRoundStuQuery> condition)
    {
        PageHelper.startPage(condition.getPageNum_(), condition.getPageSize_());
        
        ElecRoundStuQuery stu = condition.getCondition();
        Session session = SessionUtils.getCurrentSession();
        stu.setProjectId(session.getCurrentManageDptId());
        Page<Student4Elc> listPage =
            elecRoundStuDao.listPage(stu, stu.getRoundId());
        
        PageResult<Student4Elc> result = new PageResult<>(listPage);
        return result;
    }
    
    @Transactional
    @Override
    public String add(Long roundId, List<String> studentCodes,Integer mode)//判断mode,3,4都要校验学生来源(结业生表)
    {
        ElectionRounds rounds = elecRoundsDao.selectByPrimaryKey(roundId);
        if (rounds == null)
        {
            throw new ParameterValidateException("选课轮次不存在");
        }
        Session session = SessionUtils.getCurrentSession();
       
        List<String> listExistStu = elecRoundStuDao.listExistStu(studentCodes, session.getCurrentManageDptId());
        //mode 为 1，2  查找在校的学生 3，4 只要在库就行(特殊情况不在校学校要求可以选课，所以用上面的代码)
        //List<String> listExistStu = elecRoundStuDao.listExistStuByMode(studentCodes, session.getCurrentManageDptId(),mode);
        List<String> listAddedStu =
            elecRoundStuDao.listAddedStu(roundId, studentCodes);
        Set<String> updateCache = new HashSet<>();
        List<String> notExistStu = new ArrayList<>();
        for (String code : studentCodes)
        {
            if (listExistStu.contains(code) && !listAddedStu.contains(code))
            {
                if(RoundMode.JieYe.eq(mode) ){//结业生
                    int i = studentDao.findJieYeStudent(code);
                    if(i > 0){
                        elecRoundStuDao.add(roundId, code);
                        updateCache.add(code);
                    }else{
                        notExistStu.add(code);
                    }
                }else if(RoundMode.LiuXueJieYe.eq(mode) ){//留学结业生
                    int i = studentDao.findLiuXueJieYeStudent(code);
                    if(i > 0){
                        elecRoundStuDao.add(roundId, code);
                        updateCache.add(code);
                    }else{
                        notExistStu.add(code);
                    }
                }else if (RoundMode.NORMAL.eq(mode) || RoundMode.ShiJian.eq(mode)){
                    elecRoundStuDao.add(roundId, code);
                    updateCache.add(code);
                }else {
                    notExistStu.add(code);
                }
            }
            else
            {
                notExistStu.add(code);
            }
        }
        //更新轮此缓存学生信息
        if(CollectionUtil.isNotEmpty(updateCache)){
            dataProvider.updateRoundCacheStuOrCourse(roundId,Constants.STUDENT);

        }

        return StringUtils.join(notExistStu, ",");
    }
    
    @Transactional
    @Override
    public void addByCondition(ElecRoundStuQuery stu)
    {
    	Session session = SessionUtils.getCurrentSession();
        stu.setProjectId(session.getCurrentManageDptId());
        Set<String> updateCache = new HashSet<>();
        ExecutorService executor = Executors.newFixedThreadPool(32);
        List<ElectionRoundsStu> listCount = new ArrayList<>();
        // 1普通选课 2实践选课
        if(RoundMode.NORMAL.eq(stu.getMode()) || RoundMode.ShiJian.eq(stu.getMode())){ //来源学生

            List<Student4Elc> listStudent =
                    elecRoundStuDao.listNotExistStudent(stu);

            if (CollectionUtil.isNotEmpty(listStudent))
            {

                for (Student4Elc info : listStudent)
                {
                    ElectionRoundsStu roundsStu = new ElectionRoundsStu();
                    roundsStu.setRoundsId(stu.getRoundId());
                    roundsStu.setStudentId(info.getStudentId());
                    listCount.add(roundsStu);
                    updateCache.add(info.getStudentId());
                }
                //elecRoundStuDao.add(stu.getRoundId(), info.getStudentId());
            } else {
                throw new ParameterValidateException("没有匹配的学生");
            }
        }else{
            //选课学生来源与结业表
            String tableName = "tj_ungraduate";
            if(RoundMode.LiuXueJieYe.eq(stu.getMode())){
                tableName = "tj_ungraduate_foreign";
            }
            stu.setTableName(tableName);
            List<String> stringList = elecRoundStuDao.notExistStudent(stu);
            if(CollectionUtil.isNotEmpty(stringList)){
                for (String s : stringList) {
                    ElectionRoundsStu roundsStu = new ElectionRoundsStu();
                    roundsStu.setRoundsId(stu.getRoundId());
                    roundsStu.setStudentId(s);
                    listCount.add(roundsStu);
                    updateCache.add(s);
                }
            } else {
                throw new ParameterValidateException("没有匹配的学生");
            }
        }

        List<List<ElectionRoundsStu>> lists = splitList(listCount, 500);
        CountDownLatch down = new CountDownLatch(lists.size());
        try {
            for (List<ElectionRoundsStu> list : lists) {
                executor.execute(() ->{
                    elecRoundStuDao.batchInsert(list);
                    down.countDown();
                });
            }
            down.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if(CollectionUtil.isNotEmpty(updateCache)){
            executor.execute(() ->{
                dataProvider.updateRoundCacheStuOrCourse(stu.getRoundId(),Constants.STUDENT);
            });
        }
    }
    

    @Transactional
	@Override
	public AsyncResult addByConditionBK(ElecRoundStuQuery stu) {
    	AsyncResult result = AsyncProcessUtil.submitTask("addStudentByConditionBK", new AsyncExecuter() {
			
			@Override
			public void execute() {
				AsyncResult result = this.getResult();
				Session session = SessionUtils.getCurrentSession();
		        stu.setProjectId(session.getCurrentManageDptId());
//		        stu.setProjectId("1");
		        // 1普通选课 2实践选课
		        if(RoundMode.NORMAL.eq(stu.getMode()) || RoundMode.ShiJian.eq(stu.getMode())){ //来源学生

		            List<Student4Elc> listStudent =
		                    elecRoundStuDao.listNotExistStudent(stu);
		            result.setTotal(listStudent.size());
		            if (CollectionUtil.isNotEmpty(listStudent))
		            {
		            	int i = 0;
		                for (Student4Elc info : listStudent)
		                {
		                	i = i + 1;
		                    elecRoundStuDao.add(stu.getRoundId(), info.getStudentId());
		                    result.setDoneCount(i);
		                    redisTemplate.opsForValue().getAndSet("commonAsyncProcessKey-"+result.getKey(), result);
		                }
		                result.setMsg("添加成功");
		            } else {
		            	result.setMsg("没有匹配的学生");
		            }
		        }else{//选课学生来源与结业表
                    //选课学生来源与结业表
                    String tableName = "tj_ungraduate";
                    if(RoundMode.LiuXueJieYe.eq(stu.getMode())){
                        tableName = "tj_ungraduate_foreign";
                    }
                    stu.setTableName(tableName);
		            List<String> stringList = elecRoundStuDao.notExistStudent(stu);
		            if(CollectionUtil.isNotEmpty(stringList)){
		            	int i = 0;
		                for (String s : stringList) {
		                	i = i++;
		                    elecRoundStuDao.add(stu.getRoundId(), s);
		                    result.setDoneCount(i);
		                }
		                result.setMsg("添加成功");
		            } else {
		            	result.setMsg("没有匹配的学生");
		            }
		        }
				
			}
		});
    	return result;
	}
    
    @Transactional
    @Override
    public void delete(Long roundId, List<String> studentCodes)
    {
        if (CollectionUtil.isNotEmpty(studentCodes))
        {
            elecRoundStuDao.delete(roundId, studentCodes);
        }else{
            List<String> stu = elecRoundStuDao.findStuByRoundId(roundId);
            if (CollectionUtil.isEmpty(stu)) {
                throw new ParameterValidateException("无可移除名单");
            }
            elecRoundStuDao.deleteAll(roundId);
        }
        dataProvider.updateRoundCacheStuOrCourse(roundId,Constants.STUDENT);
    }
    
    @Transactional
    @Override
    public void deleteByCondition(ElecRoundStuQuery stu)
    {
    	Session session = SessionUtils.getCurrentSession();
        stu.setProjectId(session.getCurrentManageDptId());
        if(StringUtils.isEmpty(stu.getGrade())&&StringUtils.isEmpty(stu.getFaculty())&&
                StringUtils.isEmpty(stu.getProfession())&&StringUtils.isEmpty(stu.getResearchDirection())&&
                StringUtils.isEmpty(stu.getIsOverseas())){
            List<String> stuByRoundId = elecRoundStuDao.findStuByRoundId(stu.getRoundId());
            if (CollectionUtil.isEmpty(stuByRoundId)) {
                throw new ParameterValidateException("无可移除名单");
            }
            elecRoundStuDao.deleteAll(stu.getRoundId());
            dataProvider.updateRoundCacheStuOrCourse(stu.getRoundId(),Constants.STUDENT);
        }else{
            Page<Student4Elc> listStudent =
                    elecRoundStuDao.listPage(stu, stu.getRoundId());

            List<String> studentCodes = new ArrayList<>();
            if (CollectionUtil.isNotEmpty(listStudent))
            {
                for (Student4Elc info : listStudent)
                {
                    studentCodes.add(info.getStudentId());
                }
                elecRoundStuDao.delete(stu.getRoundId(), studentCodes);
                dataProvider.updateRoundCacheStuOrCourse(stu.getRoundId(),Constants.STUDENT);

            } else {
                throw new ParameterValidateException("无可移除名单或没有匹配的学生");
            }
        }
    }
    
    @Transactional
    @Override
    public int initData(ElecRoundStuDto dto) {
    	int result = Constants.ZERO;
    	elecRoundStuDao.deleteByRoundId(dto.getRoundId());
    	List<String> stuList = new ArrayList<>();
    	if(Constants.THREE_MODE.equals(dto.getMode())) {
    		stuList = elecRoundStuDao.getGradStus(); 
    	}else if(Constants.FOUR_MODE.equals(dto.getMode())) {
    		stuList = elecRoundStuDao.getOverseasStus();
    	}
    	if(CollectionUtil.isNotEmpty(stuList)) {
    		List<ElectionRoundsStu> electionRoundsStus = new ArrayList<>();
    		for(String studentId :stuList) {
    			ElectionRoundsStu electionRoundsStu = new ElectionRoundsStu();
    			electionRoundsStu.setRoundsId(dto.getRoundId());
    			electionRoundsStu.setStudentId(studentId);
    			electionRoundsStus.add(electionRoundsStu);
    		}
    		result = elecRoundStuDao.insertList(electionRoundsStus);
    	}
    	return result;
    }

    private List<List<ElectionRoundsStu>> splitList(List<ElectionRoundsStu>  list, int size) {
        if (null == list || list.isEmpty()) {
            return Collections.emptyList();
        }
        int listCount = (list.size() - 1) / size + 1;
        int remaider = list.size() % listCount; // (先计算出余数)
        int number = list.size() / listCount; // 然后是商
        int offset = 0;// 偏移量
        List<List<ElectionRoundsStu>> newList = new ArrayList<>(size);
        for (int i = 0; i < listCount; i++) {
            List<ElectionRoundsStu> value;
            if (remaider > 0) {
                value = list.subList(i * number + offset, (i + 1) * number + offset + 1);
                remaider--;
                offset++;
            } else {
                value = list.subList(i * number + offset, (i + 1) * number + offset);
            }
            newList.add(value);
        }
        return newList;
    }

}
