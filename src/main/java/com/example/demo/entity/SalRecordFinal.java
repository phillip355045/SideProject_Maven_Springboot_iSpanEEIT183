package com.example.demo.entity;

import java.math.BigDecimal;
import java.util.Date;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.persistence.NamedStoredProcedureQuery;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.StoredProcedureParameter;

@NoArgsConstructor
@Setter
@Getter
@Entity
@NamedStoredProcedureQuery(
	    name = "insertSalRecordFinal",
	    procedureName = "insertSalRecordFinal",
	    parameters = {
	        @StoredProcedureParameter(mode = ParameterMode.IN, name = "empno", type = String.class),
	        @StoredProcedureParameter(mode = ParameterMode.IN, name = "year", type = String.class),
	        @StoredProcedureParameter(mode = ParameterMode.IN, name = "month", type = String.class)
	    }
	)


@Table(name = "SalRecordFinal")

public class SalRecordFinal {

	@Id
	@Column(name = "SALNO", nullable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer salno;

	private String empno;
	private String year;
	private String month;
	private BigDecimal sal;
	private String deptno;
	private String job;
	private String mgr;
	private String name;
	
	private String status; 
	
	
	private BigDecimal foodAllowance;
	private BigDecimal trafficAllowance;
	private BigDecimal mgrAllowance;
	private BigDecimal holidayAllowance;
	private BigDecimal totalOvertimeHours;
	private BigDecimal overtimePay;
	private BigDecimal attendanceBonus;
	private Integer laborIhealthLevelId;
	private BigDecimal laborInsurance;
	private BigDecimal healthInsurance;
	private BigDecimal laborInsuranceCompany;
	private BigDecimal healthInsuranceCompany;
	private Integer halfpaidHours;
	private Integer fullpaidHours;
	private Integer unpaidHours;
	private BigDecimal leavePay;
	private BigDecimal invoice;

}
