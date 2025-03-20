package com.example.demo.entity;


import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name="meetingRoom")
public class MeetingRoom {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer roomno;
	
	private String roomName;
	
	private String capacity;
	
	private String location;
	
	private String buildignAndFloor;
	
	private String equip;
	
	//這定這個就能讓多方資料一起被刪除
	@JsonIgnore
	@OneToMany(mappedBy = "meetingRoom", cascade = CascadeType.ALL,fetch = FetchType.LAZY)
	private List<RoomPicture> roomPictures;

	public MeetingRoom() {

	}

	

	public MeetingRoom(String roomName, String capacity, String location, String buildignAndFloor, String equip,
			List<RoomPicture> roomPictures) {
		this.roomName = roomName;
		this.capacity = capacity;
		this.location = location;
		this.buildignAndFloor = buildignAndFloor;
		this.equip = equip;
		this.roomPictures = roomPictures;
	}



	public String getBuildignAndFloor() {
		return buildignAndFloor;
	}



	public void setBuildignAndFloor(String buildignAndFloor) {
		this.buildignAndFloor = buildignAndFloor;
	}



	public List<RoomPicture> getRoomPictures() {
		return roomPictures;
	}



	public void setRoomPictures(List<RoomPicture> roomPictures) {
		this.roomPictures = roomPictures;
	}



	public Integer getRoomno() {
		return roomno;
	}

	public void setRoomno(Integer roomno) {
		this.roomno = roomno;
	}

	public String getRoomName() {
		return roomName;
	}

	public void setRoomName(String roomName) {
		this.roomName = roomName;
	}

	public String getCapacity() {
		return capacity;
	}

	public void setCapacity(String capacity) {
		this.capacity = capacity;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getEquip() {
		return equip;
	}

	public void setEquip(String equip) {
		this.equip = equip;
	}


	
	
	
	
	
	
	
}
