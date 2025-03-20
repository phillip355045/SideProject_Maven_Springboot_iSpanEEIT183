package com.example.demo.dto;

public class ReserveCountDTO {
	
	private String roomName;
	
	private Integer count;

	public ReserveCountDTO() {
		
	}

	public ReserveCountDTO(String roomName, Integer count) {
		this.roomName = roomName;
		this.count = count;
	}

	public String getRoomName() {
		return roomName;
	}

	public void setRoomName(String roomName) {
		this.roomName = roomName;
	}

	public Integer getCount() {
		return count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}
	
	
	
	

}
