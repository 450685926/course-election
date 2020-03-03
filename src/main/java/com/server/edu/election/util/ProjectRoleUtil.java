package com.server.edu.election.util;

import org.apache.commons.lang3.StringUtils;
import com.server.edu.election.constants.Constants;
import com.server.edu.session.util.entity.Session;

public class ProjectRoleUtil {

	/**
	 * 判断管理员
	 * @param session
	 */
	public static Boolean isAdmin(Session session){
		return StringUtils.equals(session.getCurrentRole(), String.valueOf(Constants.ONE)) && session.isAdmin();
	}
	
	/**
	 * 判断教务员
	 * @param session
	 */
	public static Boolean isAcdemicDean(Session session){
		return StringUtils.equals(session.getCurrentRole(), String.valueOf(Constants.ONE)) && !session.isAdmin() && session.isAcdemicDean();
	}

}
