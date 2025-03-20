package com.example.demo.entity;

import java.util.LinkedHashSet;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
@Entity @Table(name = "leaveType")
public class LeaveType {
	
	//LeaveType屬於一的那一方
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long  requestTypeID;
	private String requestTypeName;
		
	//設置關聯
//	@OneToMany(fetch = FetchType.LAZY,cascade = CascadeType.ALL,mappedBy = "leaveType")
//	private Set<LeaveRequest> leaveRequests = new LinkedHashSet<LeaveRequest>();

}
