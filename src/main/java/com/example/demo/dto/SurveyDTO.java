package com.example.demo.dto;

import java.util.Date;
import java.util.List;
import com.example.demo.entity.*;
import com.fasterxml.jackson.annotation.JsonFormat;

public class SurveyDTO {

	private String sponsor;
	private String sponsorName;
	private Integer reserveno;
	private String reserveTitle;
	private Date eventDay;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
    private Date startTime;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
    private Date endTime;
    private boolean completed;
    
    private String onlineMeeting;
    
    private Boolean hasFile;
    
	public SurveyDTO() {
		hasFile = false;
	}
	


	public SurveyDTO(String sponsor, Integer reserveno, String reserveTitle, Date eventDay, Date startTime,
			Date endTime, boolean completed) {
		super();
		this.sponsor = sponsor;
		this.reserveno = reserveno;
		this.reserveTitle = reserveTitle;
		this.eventDay = eventDay;
		this.startTime = startTime;
		this.endTime = endTime;
		this.completed = completed;
	}



	public String getSponsorName() {
		return sponsorName;
	}



	public void setSponsorName(String sponsorName) {
		this.sponsorName = sponsorName;
	}



	public String getOnlineMeeting() {
		return onlineMeeting;
	}



	public void setOnlineMeeting(String onlineMeeting) {
		this.onlineMeeting = onlineMeeting;
	}







	public Boolean getHasFile() {
		return hasFile;
	}



	public void setHasFile(Boolean hasFile) {
		this.hasFile = hasFile;
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
	public boolean isCompleted() {
		return completed;
	}
	public void setCompleted(boolean completed) {
		this.completed = completed;
	}
    
    
}
