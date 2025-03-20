package com.example.demo.dto;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

public class MeetingRoomInfoDTO {
    private Integer reserveno;
    private String sponsor;
    private String sponsorName; 
    private String empno;
    private String roomName;
    private String reserveTitle;
    private Date eventDay;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
    private Date startTime;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
    private Date endTime;
    
    private Boolean hasFile;
    
    private String webexLink;
    


	public MeetingRoomInfoDTO() {
	}

	public MeetingRoomInfoDTO(Integer reserveno, String sponsor, String empno, String roomName, String reserveTitle, Date eventDay,
			Date startTime, Date endTime,String webexLink) {
		this.reserveno = reserveno;
		this.sponsor = sponsor;
		this.empno = empno;
		this.roomName = roomName;
		this.reserveTitle = reserveTitle;
		this.eventDay = eventDay;
		this.startTime = startTime;
		this.endTime = endTime;
		this.webexLink = webexLink;
	}

	
	
	public String getSponsorName() {
		return sponsorName;
	}

	public void setSponsorName(String sponsorName) {
		this.sponsorName = sponsorName;
	}

	public Integer getReserveno() {
		return reserveno;
	}

	public void setReserveno(Integer reserveno) {
		this.reserveno = reserveno;
	}
	
	
	public String getSponsor() {
		return sponsor;
	}

	public void setSponsor(String sponsor) {
		this.sponsor = sponsor;
	}

	public String getEmpno() {
		return empno;
	}

	public void setEmpno(String empno) {
		this.empno = empno;
	}

	public String getRoomName() {
		return roomName;
	}

	public void setRoomName(String roomName) {
		this.roomName = roomName;
	}

	public String getReserveTitle() {
		return reserveTitle;
	}

	public void setReserveTitle(String reserveTitle) {
		this.reserveTitle = reserveTitle;
	}

	public Date getEventDay() {
		return eventDay;
	}

	public void setEventDay(Date eventDay) {
		this.eventDay = eventDay;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}
	public Boolean getHasFile() {
		return hasFile;
	}

	public void setHasFile(Boolean hasFile) {
		this.hasFile = hasFile;
	}

	public String getWebexLink() {
		return webexLink;
	}

	public void setWebexLink(String webexLink) {
		this.webexLink = webexLink;
	}
	
	
	
    
    
}
