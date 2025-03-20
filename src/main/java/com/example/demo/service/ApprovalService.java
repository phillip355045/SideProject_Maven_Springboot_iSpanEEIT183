package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entity.Approval;
import com.example.demo.repository.ApprovalRepository;

@Service
public class ApprovalService {
	
	@Autowired
	private ApprovalRepository approvalRepository;
	
	//利用員工編號抓取審核單
	public Approval getApprovalByEmpno(String empno){
		return approvalRepository.findByEmployee_Empno(empno);
	}

}
