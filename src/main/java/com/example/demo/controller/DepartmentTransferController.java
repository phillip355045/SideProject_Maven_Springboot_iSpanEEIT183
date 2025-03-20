package com.example.demo.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.example.demo.entity.Department;
import com.example.demo.entity.DepartmentTransfer;
import com.example.demo.service.DepartmentService;
import com.example.demo.service.DepartmentTransferService;
import com.example.demo.service.EmployeesService;
import com.example.demo.service.NotificationService;


@Controller
public class DepartmentTransferController {

	@Autowired
	private DepartmentTransferService departmentTransferService;
	
	@Autowired
	private EmployeesService employeesService;
	
	@Autowired
	private DepartmentService departmentService;

	@Autowired
	private NotificationService notificationService;
	
	@GetMapping("/alldepartmentTransfer")
	public String alldepartmentTransfer(Model model) {
		List<DepartmentTransfer> allDepartmentTransfers = departmentTransferService.findAllDepartmentTransfers();
		model.addAttribute("deptTransfers", allDepartmentTransfers);
		return "employees/back/alldepartmentTransfer";
	}

	
	@PostMapping("/addDepartmentTransfer")
	public ResponseEntity<String> addDepartmentTransfer(@RequestBody DepartmentTransfer departmentTransfer) {
		departmentTransferService.saveDepartmentTransfers(departmentTransfer);
		String empno = departmentTransfer.getEmpno();
		String deptname = departmentService.findDeptname(departmentTransfer.getNewDeptno());
		Date transDate = departmentTransfer.getTransDate();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String formattedDate = sdf.format(transDate);
		// 等等測試看看能不能用XY變數方便後續大家套用
		notificationService.createNotificationBasisOnEmpno("通知員工部門調動", empno,formattedDate,deptname);		
		return ResponseEntity.ok("ok");
	}
	
	@GetMapping("/departmentTransferInsert")
	public String departmentTransferInsert(Model model) {
		List<Department> dept = departmentService.findAll();
		List<String> employeeIds = employeesService.findAllEmployeeIds();
        model.addAttribute("employeeIds", employeeIds);
        model.addAttribute("dept", dept);
		return "employees/back/departmentTransferInsert";
	}
	
	// 找原部門
	@PostMapping("/findEmpnoDeptno")
	public ResponseEntity<Map<String, String>> findEmpnoDeptno(@RequestBody String empno) {
	    String deptno = employeesService.findUsersByEmpno(empno).getDeptno();
	    String deptname = departmentService.findDeptname(deptno);

	    Map<String, String> deptInfo = new HashMap<>();
	    deptInfo.put("deptno", deptno);
	    deptInfo.put("deptname", deptname);

	    return ResponseEntity.ok(deptInfo);
	}
}
