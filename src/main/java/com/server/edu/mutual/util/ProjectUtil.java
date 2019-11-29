package com.server.edu.mutual.util;

import java.util.ArrayList;
import java.util.List;

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
}
