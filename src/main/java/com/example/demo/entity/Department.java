package com.example.demo.entity;

import java.util.List;

import org.springframework.stereotype.Component;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "department")
@Component
public class Department {

	@Id
	@Column(nullable = false)
	private String deptno;

	@Column(nullable = false)
	private String deptname;

	@Transient
	private int headcount;

	@Transient
	private String mgr;

    public int calculateHeadcount(List<Employees> employeesList) {
        return (int) employeesList.stream().filter(employee -> employee.getDeptno().equals(this.deptno)).count();
    }

}