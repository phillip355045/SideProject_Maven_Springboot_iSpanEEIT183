package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.Department;
import com.example.demo.entity.Employees;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, String> {

	
	@Query("SELECT deptno FROM Department d WHERE deptname = :deptname")
    String findDeptnoByDeptname(String deptname);
	//dolly增加
	Department findByDeptno(String deptno);
}
