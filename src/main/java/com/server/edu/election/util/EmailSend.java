package com.server.edu.election.util;

import com.server.edu.common.ServicePathEnum;
import com.server.edu.common.entity.EmailEntity;
import com.server.edu.election.dto.PaidMail;
import com.server.edu.election.entity.RemindTimeBean;
import com.server.edu.util.CollectionUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class EmailSend {

	private static final Logger LOG = LoggerFactory.getLogger(EmailSend.class);

	public static EmailSend emailSend;

	private String emailSendUrl = "cse://commonservice/mail/";

	@Autowired
	private RestTemplate restTemplate;

	@PostConstruct
	public void init() {
		emailSend = this;
	}

	/**
	 * <发送邮件>
	 * 
	 * @param num(1:提醒时间发邮件;2：驳回发邮件;3:申请成功发邮件)
	 * @param calendarId
	 * @param bean
	 */
	public void sendStatisticsEmail(List<String> emailList, RemindTimeBean bean, String calendarName, Integer num, String mess)
			throws Exception {

		// 生成发送邮件的内容
		String content = null;

		if (num == 1) {
			content = buildEmailContent(bean, calendarName);
		} else if (num == 2) {
			content = buildEmailContent2(bean, calendarName, mess);
		}

		// 构建发送邮件的实体
		EmailEntity emailEntity = buildEmailEntity(content, emailList);
		try {
			// 调用邮件发送服务发送邮件
			LOG.info("EmailSend.sendStatisticsEmail() send email");
			List<EmailEntity> emailEntityList = new ArrayList<>();
			emailEntityList.add(emailEntity);
			// String result = emailSend.restTemplate.postForObject(emailSendUrl, emailEntityList, String.class);
			String result = ServicePathEnum.COMMONSERVICE.postForObject("/mail/", emailEntityList, String.class);
//			LOG.info("sendStatisticsEmail() result:" + result);
		} catch (RestClientException e) {
			e.printStackTrace();
			LOG.info("sendStatisticsEmail() error mess: {}", e);
		}
	}

	/**
	 * <构建email内容>
	 * 
	 * @param statisticsMap
	 *            包含部门和相应的统计数据的map
	 * @return String 要发送的email的内容
	 */
	public String buildEmailContent(RemindTimeBean bean, String calendarName) throws Exception {
		String count = (bean.getStudentName() + "(" + bean.getStudentId() + "),您好：\r\n" + "       " + calendarName
				+ " 课程：" + bean.getCourseNameAndCode() + "\r\n已经在" + bean.getRemindTime() + " 代理选取成功");
		return count;
	}

	public String buildEmailContent2(RemindTimeBean bean, String calendarName, String mess) throws Exception {
		String count = (bean.getStudentName() + "(" + bean.getStudentId() + "),您好：\r\n" + "       " + calendarName
				+ " 课程：" + bean.getCourseNameAndCode() + "\r\n已经在" + bean.getRemindTime()
				+ " 代理退课成功。如有问题请在选课开放期间内联系学院及本科生院学务管理中心解决，逾期不予办理。");
		return count;
	}


	/**
	 * <构建邮件实体>
	 * 
	 * @param content
	 *            邮件内容
	 * @param emailList
	 *            需要接收邮件的收件人列表
	 * @return
	 * @see [类、类#方法、类#成员]
	 */
	public EmailEntity buildEmailEntity(String content, List<String> emailList) {
		EmailEntity emailEntity = new EmailEntity();
		// 邮件实体类
		emailEntity.setSubject("代理选课邮件通知");
		emailEntity.setText(content);
		emailEntity.setTos(emailList);
		return emailEntity;
	}
	
	
	public Long getDaySub(String beginDateStr, String endDateStr) {
		long day = 0;
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Date beginDate;
		Date endDate;
		try {
			beginDate = format.parse(beginDateStr);
			endDate = format.parse(endDateStr);
			day = (endDate.getTime() - beginDate.getTime()) / (24 * 60 * 60 * 1000);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return day;
	}

	/**
	 * 重修缴费提醒发邮件
	 * @param list
	 * @param calendarName
	 * @param startTime
	 * @param endTime
	 * @throws Exception
	 */
	public void sendEmail(List<PaidMail> list, String calendarName, String startTime, String endTime)
	{
		List<EmailEntity> emailEntityList = new ArrayList<>(100);

		Map<String, List<PaidMail>> collect = list.stream().
				filter(s -> StringUtils.isNotBlank(s.getMail())).
				collect(Collectors.groupingBy(
						s -> s.getStudentName() + "(" + s.getStudentId()));
		for (Map.Entry<String, List<PaidMail>> entry : collect.entrySet()) {
			String student = entry.getKey();
			List<PaidMail> value = entry.getValue();
			List<String> courses = value.stream().map(s -> s.getCourseCode() + "["
					+ s.getCourseName() + "]").collect(Collectors.toList());
			StringBuilder sb = new StringBuilder();
			sb.append(student).append("),您好：\r\n").append("       ").
					append(calendarName).append(" 重修课程：").
					append(String.join(", ", courses)).
					append(" 还未缴费，缴费时间为").append(startTime).
					append("-").append(endTime).
					append("，逾期未缴费选课数据将被自动删除，请知悉。");;
			String content = sb.toString();
			// 构建发送邮件的实体
			EmailEntity emailEntity = new EmailEntity();
			// 邮件实体类
			emailEntity.setSubject("重修缴费邮件通知");
			emailEntity.setText(content);
			List<String> emailList = new ArrayList<>(1);
			String mail = value.get(0).getMail();
			emailList.add(mail);
//			emailList.add("jysung@isoftstone.com");
//			emailList.add("nanzhangw@isoftstone.com");
			emailEntity.setTos(emailList);
			emailEntityList.add(emailEntity);
		}

		try {
			// 调用邮件发送服务发送邮件
			if (CollectionUtil.isNotEmpty(emailEntityList)) {

				String s = ServicePathEnum.
						COMMONSERVICE.postForObject("/mail/asyncSend", emailEntityList, String.class);
				LOG.info("==============" + emailEntityList.size());
			}

		} catch (RestClientException e) {
			e.printStackTrace();
			LOG.info("sendEmail() error mess: {}", e);
		}

	}

}
