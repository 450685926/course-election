package com.server.edu.mutual.util;

import java.io.*;
import java.util.*;

import org.apache.commons.lang3.StringUtils;

import com.server.edu.election.constants.Constants;

public class ProjectUtil {
	/**
	 * 本研互选封装projectId
	 * @param projectId
	 * @return projectIds
	 */
	public static List<String> getProjectIds(String projectId){
		List<String> projectIds = new ArrayList<String>();
		if (StringUtils.equals(projectId, Constants.PROJ_UNGRADUATE)) {               // 本科生可以选普研和在职研究生维护的互选课程
			projectIds.add(Constants.PROJ_GRADUATE);
			projectIds.add(Constants.PROJ_LINE_GRADUATE);
		}else if (StringUtils.equals(projectId, Constants.PROJ_GRADUATE)) {           // 普通研究生可以选本科生维护的互选课程
//			projectIds = Arrays.asList(Constants.PROJ_UNGRADUATE+","+Constants.PROJ_LINE_GRADUATE);
			projectIds.add(Constants.PROJ_UNGRADUATE);
		}else if (StringUtils.equals(projectId, Constants.PROJ_LINE_GRADUATE)) {      // 在职研究生可以选本科生维护的互选课程
//			projectIds = Arrays.asList(Constants.PROJ_UNGRADUATE+","+Constants.PROJ_GRADUATE);
			projectIds.add(Constants.PROJ_UNGRADUATE);
		}
		return projectIds;
	}
	/**
	 * 两个字符串的合并
	 * @param strOne
	 * @return strTwo
	 */
	public static String stringGoHeavy(String strOne,String strTwo){
		String[] strArr = strOne.split(",");
		String[] strArrNew = strTwo.split(",");
		Set<String> temp = new HashSet<String>();


		if(strArr.length>0 && StringUtils.isNotEmpty(strTwo)){
			for (String object: strArrNew) {
				temp.add(object);
			}
		}

		if(strArrNew.length>0 && StringUtils.isNotEmpty(strOne)){
			for (String objectTwo : strArr) {
				temp.add(objectTwo);
			}
		}

		return String.join(",",new ArrayList(temp));
	}

	public static String join(final double[] array, final char separator, final int startIndex, final int endIndex) {
		if (array == null) {
			return null;
		}

		final int noOfItems = endIndex - startIndex;
		if (noOfItems <= 0) {
			return "";
		}
		final StringBuilder buf = new StringBuilder(noOfItems * 16);
		for (int i = startIndex; i < endIndex; i++) {
			if (i > startIndex) {
				buf.append(separator);
			}
			buf.append(array[i]);
		}
		return buf.toString();

	}

	/**
	 * 将字符串存储为一个文件，当文件不存在时候，自动创建该文件，当文件已存在时候，重写文件的内容，特定情况下，还与操作系统的权限有关。
	 * 	*
	 * 	* @param text 字符串
	 * @param distFile 存储的目标文件
	 * @return 当存储正确无误时返回true，否则返回false
	 */
	public static boolean string2File(String text, File distFile) {
		if (!distFile.getParentFile().exists()) distFile.getParentFile().mkdirs();
		BufferedReader br = null;
		BufferedWriter bw = null;
		boolean flag = true;
		try {
			br = new BufferedReader(new StringReader(text));
			bw = new BufferedWriter(new FileWriter(distFile));
			char buf[] = new char[1024 * 64]; //字符缓冲区
			int len;
			while ((len = br.read(buf)) != -1) {
				bw.write(buf, 0, len);
			}
			bw.flush();
			br.close();
			bw.close();
		} catch (IOException e) {
			flag = false;
		}
		return flag;
	}

	/**
	 * 将两个数组合并，并且输出字符串
	 * @param a 数组A
	 * @param b 数组b
	 * @param index 初始化数组的索引
	 * */
	public static String mergeString(int[] a,int[] b,int index) {
		Map<Integer, Integer> map = new TreeMap<>();
		for (int i = 0; i < a.length; i++) {
			map.put(a[i], a[i]);
		}
		for (int i = 0; i < b.length; i++) {
			map.put(b[i], b[i]);
		}
		Collection<Integer> values = map.values();
		Iterator<Integer> iterator = values.iterator();
		int c[] = new int[values.size()];
		while (iterator.hasNext()) {
			c[index++] = iterator.next();
		}
		return c.toString();
	}

	/**
	 * 功能描述: 本研互选mode切换
	 *
	 * @params: [mode]
	 * @return: java.lang.Integer
	 * @author: zhaoerhu
	 * @date: 2020/3/4 17:52
	 */
	public static Integer convertMode(Integer mode) {
		if (Constants.BK_MUTUAL.equals(mode)) {
			return Constants.GRADUATE_MUTUAL;
		}
		if (Constants.GRADUATE_MUTUAL.equals(mode)) {
			return Constants.BK_MUTUAL;
		}
		return mode;
	}
}
