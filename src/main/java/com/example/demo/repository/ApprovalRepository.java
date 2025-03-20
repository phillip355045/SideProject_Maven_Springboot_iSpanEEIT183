package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.Approval;

public interface ApprovalRepository extends JpaRepository<Approval,String>{
	
	Approval findByEmployee_Empno(String empno);

}
