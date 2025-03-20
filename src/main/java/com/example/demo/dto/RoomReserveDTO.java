package com.example.demo.dto;




public class RoomReserveDTO {
	
	private Integer reserveno;

	private String reserveTitle;

	private String reserveType;
	
	private String roomName;

	private String empno;

	private String eventDay;

	private String startTime;

	private String endTime;
	
	//資料庫預設為0、當等於1代表審核通過
	private String status;
	
	//資料庫預設為0、當等於1的時候就代表已經被使用者刪除，但資料庫繼續留存
	private String fakeDelete;
	
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getFakeDelete() {
		return fakeDelete;
	}

	public void setFakeDelete(String fakeDelete) {
		this.fakeDelete = fakeDelete;
	}

	public RoomReserveDTO() {

	}

	public Integer getReserveno() {
		return reserveno;
	}

	public String getReserveTitle() {
		return reserveTitle;
	}

	public String getReserveType() {
		return reserveType;
	}

	public String getRoomName() {
		return roomName;
	}

	public String getEmpno() {
		return empno;
	}

	public String getEventDay() {
		return eventDay;
	}

	public String getStartTime() {
		return startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setReserveno(Integer reserveno) {
		this.reserveno = reserveno;
	}

	public void setReserveTitle(String reserveTitle) {
		this.reserveTitle = reserveTitle;
	}

	public void setReserveType(String reserveType) {
		this.reserveType = reserveType;
	}

	public void setRoomName(String roomName) {
		this.roomName = roomName;
	}

	public void setEmpno(String empno) {
		this.empno = empno;
	}

	public void setEventDay(String eventDay) {
		this.eventDay = eventDay;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}


	
	

	
}
