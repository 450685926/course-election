package com.server.edu.election.util;

public class TableIndexUtil {
	public static int getIndex(long caladerId){
		int index =(int) caladerId % 6;
		return index;
	}

}
