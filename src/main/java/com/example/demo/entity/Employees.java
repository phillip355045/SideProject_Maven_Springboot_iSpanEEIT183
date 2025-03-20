package com.example.demo.entity;

import java.util.Date;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
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
@Table(name = "employees")
@Component
public class Employees {

	@Id
	@Column(nullable = false)
	private String empno;

	@Column(nullable = false)
	private String job;

	private String name;

	private String mgr;

	private String deptno;

	@Column(nullable = true)
	private String deptTransferID;

	private String password;

	private String identityID;

	// 日期格式
	@JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
	@DateTimeFormat(pattern = "yyyy-MM-dd") // 若要在thymeleaf強制使用Messages設置的格式加兩層{{}}
	@Temporal(TemporalType.DATE)
	private Date birthDate;

	private String gender;
	private String citizenship;
	private String phone;
	private String telPhone;
	private String mail;

	@Lob
	@Column(nullable = true)
	private byte[] photo;

	private String emergencyContact;
	private String city;
	private String address;

	// 日期格式
	@Column(nullable = true)
	@JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
	@DateTimeFormat(pattern = "yyyy-MM-dd") // 若要在thymeleaf強制使用Messages設置的格式加兩層{{}}
	@Temporal(TemporalType.DATE)
	private Date hiredate;

	private String salGrade;
	@Column(nullable = false)
	private String empnoStatus;

	// 日期格式
	@Column(nullable = true)
	@JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
	@DateTimeFormat(pattern = "yyyy-MM-dd") // 若要在thymeleaf強制使用Messages設置的格式加兩層{{}}
	@Temporal(TemporalType.DATE)
	private Date departureDate;

	// 日期格式
	@JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") // 若要在thymeleaf強制使用Messages設置的格式加兩層{{}}
	// 讓資料庫知道要什麼時間類型
	@Temporal(TemporalType.TIMESTAMP)
	@Column(columnDefinition = "datetime2(0)")
	private Date lastTimeLogin;
	
	
	
    // 通知和員工的多對多關係
    @ManyToMany(mappedBy = "employees")
    private List<Notification> notifications;
	
    //
    @JsonIgnore
    @OneToMany(mappedBy = "employee")
    private List<Survey> surveys;
}
