package com.example.demo.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
@Entity
@Table(name="annualSchedule")
public class AnnualSchedule {
	
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false, updatable = false)
	private String annualScheduleID;
	
	private int annual;
	
	private int totalHours;
	
	private int usedHours;
	
	//設置關聯
	@ManyToOne(fetch = FetchType.EAGER,cascade = {CascadeType.PERSIST, CascadeType.MERGE})
	@JoinColumn(name = "requestTypeID")
	private LeaveType leaveType;
		
	// 新增Employee的多對一關聯
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "employeeID")
	private Employees employee;

}
