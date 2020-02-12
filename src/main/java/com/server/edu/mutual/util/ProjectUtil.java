package com.server.edu.mutual.util;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
}
