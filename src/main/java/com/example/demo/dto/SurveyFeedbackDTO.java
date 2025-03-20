package com.example.demo.dto;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

public class SurveyFeedbackDTO {
	
	private Integer reserveno;
	private String reserveTitle;
	private Date eventDay;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
    private Date startTime;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
    private Date endTime;
    
    private Integer total;
    
    private Integer feedbackNumber;
    
    private Double averagescore;
    
//    private List<String> opinion;
    private String opinion;

	public SurveyFeedbackDTO() {	

	}

	public SurveyFeedbackDTO(Integer reserveno, String reserveTitle, Date eventDay, Date startTime, Date endTime,
			Integer total, Integer feedbackNumber, Double averagescore, String opinion) {
		this.reserveno = reserveno;
		this.reserveTitle = reserveTitle;
		this.eventDay = eventDay;
		this.startTime = startTime;
		this.endTime = endTime;
		this.total = total;
		this.feedbackNumber = feedbackNumber;
		this.averagescore = averagescore;
		this.opinion = opinion;
	}

	public Integer getReserveno() {
		return reserveno;
	}

	public void setReserveno(Integer reserveno) {
		this.reserveno = reserveno;
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

	public Integer getTotal() {
		return total;
	}

	public void setTotal(Integer total) {
		this.total = total;
	}

	public Integer getFeedbackNumber() {
		return feedbackNumber;
	}

	public void setFeedbackNumber(Integer feedbackNumber) {
		this.feedbackNumber = feedbackNumber;
	}

	public Double getAveragescore() {
		return averagescore;
	}

	public void setAveragescore(Double averagescore) {
		this.averagescore = averagescore;
	}

	public String getOpinion() {
		return opinion;
	}

	public void setOpinion(String opinion) {
		this.opinion = opinion;
	}
	
	
	

}
