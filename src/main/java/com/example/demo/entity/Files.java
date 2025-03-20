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
@Table(name="files")
public class Files {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer fileno;
	
    
	private String name;
	private String contentType;
	private String filePath;
	
	@Column(name = "reserveno", insertable = false, updatable = false)// insertable = false	, 
	private Integer reserveno;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "reserveno", nullable=false)//nullable
	private RoomReserve roomReserve;

	public Files() {

	}

	public Files(String name, String contentType, String filePath) {
		this.name = name;
		this.contentType = contentType;
		this.filePath = filePath;
	}

	public Integer getFileno() {
		return fileno;
	}

	public void setFileno(Integer fileno) {
		this.fileno = fileno;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public Integer getReserveno() {
		return reserveno;
	}

	public void setReserveno(Integer reserveno) {
		this.reserveno = reserveno;
	}

	public RoomReserve getRoomReserve() {
		return roomReserve;
	}

	public void setRoomReserve(RoomReserve roomReserve) {
		this.roomReserve = roomReserve;
	}
	
	
	

	
}
