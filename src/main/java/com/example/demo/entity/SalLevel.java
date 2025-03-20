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
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "salLevel")
public class SalLevel {

	@Id
	@JoinColumn(name = "salGrade", nullable = false)
	private String salGrade;
	

	@Column(nullable = false)
	private BigDecimal sal;

	// Getters and Setters
}
