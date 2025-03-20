package com.example.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entity.DepartmentTransfer;
import com.example.demo.repository.DepartmentTransferRepository;
import com.example.demo.repository.EmployeesRepository;

@Service
public class DepartmentTransferService {

	@Autowired
	private DepartmentTransferRepository departmentTransferRepository;

	
	@Autowired
	private EmployeesRepository employeesRepository;

	public List<DepartmentTransfer> findAllDepartmentTransfers() {
		return departmentTransferRepository.findAll();
	}
	
	public void saveDepartmentTransfers(DepartmentTransfer departmentTransfer) {
		departmentTransfer.addDeptTransferID();
		departmentTransferRepository.save(departmentTransfer);
		String empno = departmentTransfer.getEmpno();
		String deptTransferID = departmentTransfer.getDeptTransferID();
		String deptno = departmentTransfer.getNewDeptno();
		String job = departmentTransfer.getNewJob();
		String mgr = employeesRepository.findFirstByDeptnoAndJob(deptno).getEmpno();
		employeesRepository.deptTransfer(empno, deptno, deptTransferID, job,mgr);
		
	}
	
	
}
