package com.example.demo.dto;

import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
public class SalTotalDTO {
    private Integer salno;
    private String empno;
    private String name;
    private String year;
    private String month;
    private BigDecimal sal;
    private BigDecimal foodAllowance;
    private BigDecimal trafficAllowance;
    private BigDecimal mgrAllowance;
    private BigDecimal holidayAllowance;
    private BigDecimal overtimePay;
    private BigDecimal attendanceBonus;
    private BigDecimal leavePay;
    
    private BigDecimal laborInsurance;
    private BigDecimal healthInsurance;
    private Integer totalAmount;
    private Integer totalDeduction;
    private Integer netPay;
    
   
    private String status; 
    
    
    private String deptno;
    private String job;
    private String mgr;
  
    private BigDecimal totalOvertimeHours;
    
    private Integer laborIhealthLevelId;
    
    private BigDecimal laborInsuranceCompany;
    private BigDecimal healthInsuranceCompany;
    private Integer halfpaidHours;
    private Integer fullpaidHours;
    private Integer unpaidHours;
  
    private BigDecimal invoice;
}
