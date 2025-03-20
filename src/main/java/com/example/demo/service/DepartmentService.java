package com.example.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entity.Department;
import com.example.demo.entity.Employees;
import com.example.demo.repository.DepartmentRepository;

@Service
public class DepartmentService {

    @Autowired
    private EmployeesService employeesService;

    @Autowired
    private DepartmentRepository departmentRepository;

    public String findManagerByDeptno(String deptno) {
        Employees manager = employeesService.findManagerByDeptno(deptno);
        return (manager != null) ? manager.getEmpno() : null;
    }
    
    
    public List<Department> findAll(){
    	return departmentRepository.findAll();
    }
    
    public String findDeptname(String deptno) {
    	return departmentRepository.findById(deptno).get().getDeptname();
    }
    
}
