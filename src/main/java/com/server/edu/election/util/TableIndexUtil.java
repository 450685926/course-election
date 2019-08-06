package com.server.edu.election.util;

public class TableIndexUtil {
	public static int getMode(long caladerId){
		int mode =(int) caladerId % 6;
		return mode;
	}

}
