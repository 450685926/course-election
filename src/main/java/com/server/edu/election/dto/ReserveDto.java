package com.server.edu.election.dto;

import java.util.List;

import org.hibernate.validator.constraints.NotEmpty;

public class ReserveDto {
	@NotEmpty
	private List<Long> ids;
	private int reserveNumber;
	private int reserveProportion;
	public List<Long> getIds() {
		return ids;
	}
	public void setIds(List<Long> ids) {
		this.ids = ids;
	}
	public int getReserveNumber() {
		return reserveNumber;
	}
	public void setReserveNumber(int reserveNumber) {
		this.reserveNumber = reserveNumber;
	}
	public int getReserveProportion() {
		return reserveProportion;
	}
	public void setReserveProportion(int reserveProportion) {
		this.reserveProportion = reserveProportion;
	}
	
}
