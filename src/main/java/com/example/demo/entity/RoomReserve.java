package com.example.demo.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

@Entity
@Table(name = "roomReserve")//這會等於資料庫的表格名稱
public class RoomReserve {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer reserveno;
	
	private String reserveTitle;
	
	private String roomName;
	
	private String empno;
		
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@Temporal(TemporalType.DATE)
	private Date eventDay;
	
	@DateTimeFormat(pattern = "HH:mm:ss")
	@Temporal(TemporalType.TIME)
	private Date startTime;

	@DateTimeFormat(pattern = "HH:mm:ss")
	@Temporal(TemporalType.TIME)
	private Date endTime;
	
	@JsonIgnore //不會造成無限迴圈
	@OneToMany(mappedBy = "roomReserve", cascade = CascadeType.ALL,fetch = FetchType.LAZY)
	private List<Files> reservationFiles = new ArrayList<>();
	
//	//設定這個就能讓多方資料一起被刪除
//	@JsonIgnore
//	@OneToMany(mappedBy = "roomReserve", cascade = CascadeType.ALL,fetch = FetchType.LAZY)
//	private List<MeetingInfo> meetingInfos;
	
	@JsonIgnore
	@OneToMany(mappedBy = "roomReserve", cascade = CascadeType.ALL,fetch = FetchType.LAZY)
	private List<Survey> surveys;
	
	//資料庫預設為0、當等於1代表審核通過
	private String status;
	
	//資料庫預設為0、當等於1的時候就代表已經被使用者刪除，但資料庫繼續留存
	private String fakeDelete;
	
	//為了送到thmyleaf判斷有沒有檔案
	private Boolean hasFile;
	
	private String onlineMeeting;
	
	
	public RoomReserve() {

	}
	
	

	public RoomReserve(Integer reserveno,String reserveTitle, String roomName, String empno, Date eventDay, Date startTime, Date endTime,String status,String fakeDelete) {
		this.reserveno = reserveno;
		this.reserveTitle = reserveTitle;
		this.roomName = roomName;
		this.empno = empno;
		this.eventDay = eventDay;
		this.startTime = startTime;
		this.endTime = endTime;
		this.status = status;
		this.fakeDelete = fakeDelete;
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



	public String getRoomName() {
		return roomName;
	}

	public void setRoomName(String roomName) {
		this.roomName = roomName;
	}

	public String getEmpno() {
		return empno;
	}

	public void setEmpno(String empno) {
		this.empno = empno;
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

	public List<Files> getReservationFiles() {
		return reservationFiles;
	}

	public void setReservationFiles(List<Files> reservationFiles) {
		this.reservationFiles = reservationFiles;
	}
	
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



	public Boolean getHasFile() {
		return hasFile;
	}



	public void setHasFile(Boolean hasFile) {
		this.hasFile = hasFile;
	}



	public String getOnlineMeeting() {
		return onlineMeeting;
	}


	public void setOnlineMeeting(String onlineMeeting) {
		this.onlineMeeting = onlineMeeting;
	}



	public List<Survey> getSurveys() {
		return surveys;
	}



	public void setSurveys(List<Survey> surveys) {
		this.surveys = surveys;
	}



	@Override
	public String toString() {
		return "RoomReserve [eventDay=" + eventDay + ", startTime=" + startTime + ", endTime=" + endTime + "]";
	}
	
	

	
	
}
