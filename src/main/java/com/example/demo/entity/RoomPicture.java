package com.example.demo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name="roomPicture")
public class RoomPicture {
	
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer pictureno;
	
    @Column(columnDefinition = "nvarchar(MAX)")
	private String picturePath;
		
	@ManyToOne//(fetch = FetchType.LAZY)
	@JoinColumn(name = "roomno", nullable=false)
	private MeetingRoom meetingRoom;

	public RoomPicture() {
		
	}
	

	public RoomPicture(String picturePath) {
		this.picturePath = picturePath;
	}



	public Integer getPictureno() {
		return pictureno;
	}

	public void setPictureno(Integer pictureno) {
		this.pictureno = pictureno;
	}

	public String getPicturePath() {
		return picturePath;
	}

	public void setPicturePath(String picturePath) {
		this.picturePath = picturePath;
	}

	public MeetingRoom getMeetingRoom() {
		return meetingRoom;
	}

	public void setMeetingRoom(MeetingRoom meetingRoom) {
		this.meetingRoom = meetingRoom;
	}
	
	
	
	
	
	
}
