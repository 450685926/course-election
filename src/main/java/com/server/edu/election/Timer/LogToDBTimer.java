package com.server.edu.election.Timer;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.obs.services.ObsClient;
import com.obs.services.model.PutObjectRequest;
import com.server.edu.common.entity.LogEntity;
import com.server.edu.common.rest.RedisConstant;
import com.server.edu.election.dao.EleLogDao;

@SuppressWarnings("all")
@Component
@PropertySource("classpath:/obs.properties")
public class LogToDBTimer {
	
	@Value("${obs.ak}")
	private String ak;

	@Value("${obs.sk}")
	private String sk;

	@Value("${obs.endPoint}")
	private String endPoint;

	@Value("${obs.bucketName}")
	private String bucketName;

	@Value("${obs.folderpath}")
	private String folderpath;

	private static Logger LOG = LoggerFactory.getLogger("com.server.edu.election.Timer.LogToDBTimer");

	@Autowired
	private RedisTemplate redisTemplate;
	
	@Autowired
	private EleLogDao eld;

	@Scheduled(cron = "0 10 0 * * ? ") // 每天0点10分
	// @Scheduled(cron = "0 0/1 * * * ?")//每分钟
	// @Scheduled(cron = "0 0 0/1 * * ?")//每小时
	public void output2FileTimer() {
		LocalDate preDate = LocalDate.now().minusDays(1);
		LocalDate now = LocalDate.now();
		LOG.info("SYSTEM:{}redis write to DB, start", now);
		Calendar startC = Calendar.getInstance();
		long startTime = startC.getTimeInMillis();

		// 将redis数据写入数据库
		output2DB(preDate);
		// 清空redis
		clearRedis(preDate);

		Calendar endC = Calendar.getInstance();
		long endTime = endC.getTimeInMillis();
		LOG.info("write to DB end,use time {} seconds", ((endTime - startTime) / 1000));
	}
	 
	 /**
	  * @Description: 过期数据老化，每周五老化一次一年之外的数据,过期标准：当前日期0点整
	  * @author: zhang yichi 
	  * @date: 2019年5月28日 上午10:12:07
	  */
	 //@Scheduled(cron = "0 30 0 ? * SAT")//每周六0：30执行
	@Scheduled(cron = "0 0 9 * * ? ")
	 public void agingLogTimer() {
		 //获取一年前的时间
		 //LocalDate minusYears = LocalDate.now().minusYears(1);
		 LocalDate minusYears = LocalDate.now().minusDays(1);
		 Calendar calendar = Calendar.getInstance();
		 //minusYears.getMonthValue()-1 才是本月
		 calendar.set(minusYears.getYear(), minusYears.getMonthValue()-1, minusYears.getDayOfMonth(), 0, 0, 0);
		 long overDueTime = calendar.getTimeInMillis();
		 List<LogEntity> list = eld.getAgingLog(overDueTime);
		 if (list != null && list.size() > 0) {
			 LOG.info("aging log (select course) start");
			 //将数据库中一年之外的数据导出到obs并删除数据库的过期数据
			 output2Obs(LocalDate.now(), list);
			 eld.deleteAgingLog(list);
			 LOG.info("aging log (select course) end");
		 } else {
			 LOG.info("no aging select course log to OBS");
		 }
	 }
	 
	 public void output2Obs(LocalDate preDate, List<LogEntity> list) {
		ObsClient obsClient = new ObsClient(ak, sk, endPoint);
		
		// 命名存入obs的log文件
		StringBuffer objectkey = new StringBuffer(preDate.toString() + ".log");
		objectkey.insert(0, folderpath + "/");
		InputStream input;
		PutObjectRequest request = new PutObjectRequest();	
		StringBuffer sb = new StringBuffer();
		
		for (LogEntity l : list) {
			sb = sb.append(l.getLog() + "\r\n");
		}
		
		try {
			input = new ByteArrayInputStream(sb.toString().getBytes("GBK"));
			request.setInput(input);
			request.setObjectKey(objectkey.toString());
			request.setBucketName(bucketName);
			obsClient.putObject(request);
			obsClient.close();
		} catch (IOException e) {
			LOG.info("create {} operation sql log error", preDate);
		}
	}

	private void output2DB(LocalDate preDate) {
		LocalDate minusDays = LocalDate.now().minusDays(1);
		for (int i = 0; i < 24; i++) {
			List<String> list = new ArrayList<>();
			if (redisTemplate.hasKey(RedisConstant.ELE_SQL_KEY + preDate + i)) {
				list = redisTemplate.opsForList().range(RedisConstant.ELE_SQL_KEY + preDate + i, 0, -1);
				if (list != null && list.size() > 0) {
					List<LogEntity> entityList = new ArrayList<>();
					for (String s : list) {
						LogEntity al = new LogEntity();
						al.setLog(s.split("\\|")[0]);
						al.setOperator(s.split("\\|")[1]);
						al.setCreateDate(Long.parseLong(s.split("\\|")[2]));
						entityList.add(al);
						//ald.addOperateLog(al);
					}
					eld.addOperateLog(entityList);
					entityList.clear();
				}
			}
		}
	}

	private void clearRedis(LocalDate preDate) {
		for (int i = 0; i < 24; i++) {
			redisTemplate.delete(RedisConstant.ELE_SQL_KEY + preDate + i);
		}
		redisTemplate.delete(RedisConstant.ELE_SQL_KEY + preDate);
	}
}
