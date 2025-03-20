package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.example.demo.entity.EmpFamilyMember;
import com.example.demo.service.EmpFamilyMemberService;
import com.example.demo.service.EmployeesService;

@Controller
public class EmpFamilyMemberController {

	@Autowired
	private EmpFamilyMemberService empFamilyMemberService;
	
	@Autowired
	private EmployeesService employeesService;

	@GetMapping("/familyMemberInsert")
	public String insertFamilyMember(Model model) {
		List<String> employeeIds = employeesService.findAllEmployeeIds();
        model.addAttribute("employeeIds", employeeIds);
		return "employees/back/familyMemberInsert";
	}

	@PostMapping("/insertFamilyMemberToTable")
	public String addEmpFamilyMember(@RequestBody EmpFamilyMember empFamilyMember) {
		try {
			empFamilyMemberService.createEmpFamilyMember(empFamilyMember);
			return "employees/back/familyMemberInsert";
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Error occurred: " + e.getMessage());
		}
		return null;
	}
}