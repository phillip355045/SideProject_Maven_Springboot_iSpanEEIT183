package com.example.demo.dto;

import java.util.List;

public class MeetingInfoRequestDTO {

	private String reserveno;
	private List<String> empnoList;
	
	public MeetingInfoRequestDTO() {
	}

	public String getReserveno() {
		return reserveno;
	}

	public void setReserveno(String reserveno) {
		this.reserveno = reserveno;
	}

	public List<String> getEmpnoList() {
		return empnoList;
	}

	public void setEmpnoList(List<String> empnoList) {
		this.empnoList = empnoList;
	}
	

}
