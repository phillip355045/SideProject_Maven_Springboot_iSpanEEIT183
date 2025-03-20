package com.example.demo.entity;

import org.springframework.stereotype.Component;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "empFamilyMember")
@Component
public class EmpFamilyMember {

	@Id
	private String id;
	
	private String empno;

	private String memberName;

	private String relation;

	private int insurance;

}
