package com.server.edu.election.dto;

import com.server.edu.election.entity.ElcMedWithdraw;

public class ElcMedWithdrawDto extends ElcMedWithdraw {
	private static final long serialVersionUID = 1L;
	/**分库分表标识*/
	private Integer index;

	public Integer getIndex() {
		return index;
	}

	public void setIndex(Integer index) {
		this.index = index;
	}
	
}
