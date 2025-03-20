package com.example.demo.entity;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonFormat;

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
@Table(name = "departmentTransfer")
@Component
public class DepartmentTransfer {

	@Id
	private String deptTransferID;

	private String empno;

	@JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@Temporal(TemporalType.DATE)
	private Date transDate;

	private String lastDeptno;

	private String newDeptno;

	private String newJob;
	

	// 	T-D04-D02-A0002-20240302
    public void addDeptTransferID() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String formattedDate = sdf.format(this.transDate);
        String deptTransferId = "T" + "-" + this.lastDeptno + "-" + this.newDeptno + "-" + this.empno + "-" + formattedDate;
        this.deptTransferID = deptTransferId;
    }

	public DepartmentTransfer(String empno, Date transDate, String lastDeptno, String newDeptno) {
		this.empno = empno;
		this.transDate = transDate;
		this.lastDeptno = lastDeptno;
		this.newDeptno = newDeptno;
	}
	
	public DepartmentTransfer(String empno, String lastDeptno, String newDeptno) {
		this.empno = empno;
		this.lastDeptno = lastDeptno;
		this.newDeptno = newDeptno;
	}
}
