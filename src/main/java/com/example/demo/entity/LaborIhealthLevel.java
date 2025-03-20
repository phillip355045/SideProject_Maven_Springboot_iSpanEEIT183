package com.example.demo.entity;

import java.math.BigDecimal;
import java.util.Date;

import org.hibernate.annotations.Formula;
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
@Table(name = "laborIhealthLevel")
public class LaborIhealthLevel {
    

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int Id;
    
    
    private BigDecimal minSalGetTotal;
    
    
    private BigDecimal maxSalGetTotal;
    
    
    @Formula("CASE WHEN Id = 52 THEN minSalGetTotal * 0.0240 ELSE ROUND(maxSalGetTotal * 0.0240, 0) END")
    private BigDecimal laborInsurance;

    @Formula("CASE WHEN Id = 52 THEN minSalGetTotal * 0.0155 ELSE ROUND(maxSalGetTotal * 0.0155, 0) END")
    private BigDecimal healthInsurance;

}