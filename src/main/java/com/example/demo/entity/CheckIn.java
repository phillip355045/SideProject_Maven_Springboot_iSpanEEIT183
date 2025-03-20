package com.example.demo.entity;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "checkin")
@Component
public class CheckIn {

//	@Id
//	private String empno;
//
//	@Id
//	@JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
//	@DateTimeFormat(pattern = "yyyy-MM-dd")
//	@Temporal(TemporalType.DATE)
//	private Date date;

	@Id
	@EmbeddedId
	private CheckInId checkInId;
	
	@JsonFormat(timezone = "GMT+8", pattern = "HH:mm:ss")
	@DateTimeFormat(pattern = "HH:mm:ss")
	@Temporal(TemporalType.TIME)
	@Column(columnDefinition = "TIME(0)")
	private Date workon;


	@JsonFormat(timezone = "GMT+8", pattern = "HH:mm:ss")
	@DateTimeFormat(pattern = "HH:mm:ss")
	@Temporal(TemporalType.TIME)
	@Column(columnDefinition = "TIME(0)")
	private Date workoff;

	private String note;

	private String record;

}
