package com.server.edu.election.vo;

import java.util.List;

/**
  *    操作redis的对象
 * @author xlluoc
 *
 */
public class RedisVo {
	/**
	 * redis中的key键
	 */
	private String key;
	
	private List<String> list;
	
	/**
	 *  模糊匹配值
	 */
	private String pattern;

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public List<String> getList() {
		return list;
	}

	public void setList(List<String> list) {
		this.list = list;
	}

	public String getPattern() {
		return pattern;
	}

	public void setPattern(String pattern) {
		this.pattern = pattern;
	}
}
