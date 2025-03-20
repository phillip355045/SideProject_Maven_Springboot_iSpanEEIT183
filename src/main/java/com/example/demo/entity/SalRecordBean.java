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

@NoArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "empSalRecord")

public class SalRecordBean {

	@Id
	@Column(name = "SALNO", nullable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer salno;

	private String empno;
	private String year;
	private String month;
	private String foodAllowance; // 伙食津貼
	private String holidayAllowance; // 節日禮金
	private String overtime; // 加班費
	private String attendanceBonus; // 全勤獎金
	private String leave; // 請假扣薪
	
	
	
	
	
	
}
