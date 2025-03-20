package com.example.demo.entity;

import java.time.LocalDateTime;
import java.util.Date;
import org.springframework.format.annotation.DateTimeFormat;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
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
@Table(name="leaveRequest")
public class LeaveRequest {
	
	//LeaveRequest屬於多的那一方
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false, updatable = false)
	private String requestID;
	
//	private String empno;
	
	private String reason;
	
	@JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss") //確保 JSON 資料在傳輸過程中格式正確
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@Temporal(TemporalType.TIMESTAMP)
	private Date startTime;
	@JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss") //確保 JSON 資料在傳輸過程中格式正確
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@Temporal(TemporalType.TIMESTAMP)
	private Date endTime;
	
	private String delegateID;
	private String documents;
	private int totalHours;
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false, updatable = false)
    private Date createdDate;
	
	
	@Column(nullable = true)
	private String approvalNo1ID;
	@Column(nullable = false)
	private String approvalNo1Status = "待審查";
	
	@JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss") //確保 JSON 資料在傳輸過程中格式正確
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@Temporal(TemporalType.TIMESTAMP)
	@Column(nullable = true)
	private Date approvalNo1Date;
	
	@Column(nullable = true)
	private String approvalNo2ID;
	@Column(nullable = false)
	private String approvalNo2Status = "待審查";
	
	@JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss") //確保 JSON 資料在傳輸過程中格式正確
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@Temporal(TemporalType.TIMESTAMP)
	@Column(nullable = true)
	private Date approvalNo2Date;
	
	@Column(nullable = true)
	private String approvalNo3ID;
	@Column(nullable = false)
	private String approvalNo3Status = "待審查";
	
	@JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss") //確保 JSON 資料在傳輸過程中格式正確
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@Temporal(TemporalType.TIMESTAMP)
	@Column(nullable = true)
	private Date approvalNo3Date;
	
	@Column(nullable = false)
	private String finalStatus = "待審查";
	
	//設置關聯
	@ManyToOne(fetch = FetchType.EAGER,cascade = {CascadeType.PERSIST, CascadeType.MERGE})
	@JoinColumn(name = "requestTypeID")
	private LeaveType leaveType;
	
	// 新增Employee的多對一關聯
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "employeeID")
    private Employees employee;
	
	
  //在實體第一次持久化之前設置創建時間
  	@PrePersist
    protected void onCreate() {
          this.createdDate = new Date();
      }

}
