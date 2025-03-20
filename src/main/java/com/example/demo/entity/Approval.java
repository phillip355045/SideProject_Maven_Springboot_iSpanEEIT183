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
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
@Entity
@Table(name="approval")
public class Approval {
	
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false, updatable = false)
	private String approvalID;
	
	@Column(nullable = false)
	private String approvalNo1ID;
	@Column(nullable = false)
	private String approvalNo2ID;
	@Column(nullable = false)
	private String approvalNo3ID;
	
	// 新增Employee的多對一關聯
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "employeeID")
    private Employees employee;

}
