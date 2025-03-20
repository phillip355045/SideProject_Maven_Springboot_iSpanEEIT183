package com.example.demo.controller;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.entity.Department;
import com.example.demo.entity.Employees;
import com.example.demo.entity.Notification;
import com.example.demo.entity.NotificationEmployees;
import com.example.demo.service.DepartmentService;
import com.example.demo.service.EmployeesService;
import com.example.demo.service.NotificationEmployeesService;
import com.example.demo.service.NotificationService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;


@Controller
public class NotificationController {

	@Autowired
	private NotificationService notificationService;

	@Autowired
	private EmployeesService employeesService;

	@Autowired
	private DepartmentService departmentService;

	@Autowired
	private NotificationEmployeesService notificationEmployeesService;
	
//	@Autowired
//	private NotifyRetrunService notifyRetrunService;
//	
	@GetMapping("/notificationpage")
	public String createNotification(Model model) {
		List<Employees> employees = employeesService.findAll();
		List<Department> departments = departmentService.findAll();
//		List<NotifyRetrun> notifyRetruns = notifyRetrunService.findAll();
		model.addAttribute("employees", employees);
		model.addAttribute("departments", departments);
//		model.addAttribute("notifyRetruns", notifyRetruns);
		return "employees/back/notification";
	}

	@SuppressWarnings("unchecked")
	@PostMapping("/createNotification")
	public ResponseEntity<?> createNotification(@RequestBody Map<String, Object> requestData) {
		String basis = (String) requestData.get("basis");
		String category = (String) requestData.get("category");
		String message = (String) requestData.get("message");
		String returnPage = (String) requestData.get("returnPage");
		List<String> empnos = (List<String>) requestData.get("empnos");
		List<String> deptnos = (List<String>) requestData.get("deptnos");
		System.out.println(deptnos);
		Notification notification = null;
		if ("empno".equals(basis)) {
			notification = notificationService.createNotification(basis, category, message, returnPage, empnos);
		} else {
			notification = notificationService.createNotificationBydeptno(basis, category, message, returnPage,
					deptnos);
		}
		return ResponseEntity.ok(notification);
	}

	@GetMapping("/notificationsapi")
	public ResponseEntity<List<NotificationEmployees>> getNotifications(@RequestParam String account) {
//		System.out.println("測試:" + account);
		List<NotificationEmployees> unreadNotificationsByEmployee = notificationEmployeesService
				.findUnreadNotificationsByEmployee(account);
//		System.out.println(unreadNotificationsByEmployee.get(0).getNotification().getMessage());
		return ResponseEntity.ok(unreadNotificationsByEmployee);
	}

	@GetMapping("/readnotification")
	public ResponseEntity<String> readnotificateion(@RequestParam Integer id) {
		Date readTime=new Date();
		notificationEmployeesService.updateRead(id,readTime);
		return ResponseEntity.ok("讀取!");
	}

	@GetMapping("/allnotification")
	public String allnotification(Model model, HttpServletRequest request) {
		HttpSession session = request.getSession();
		String empno = (String) session.getAttribute("account");
		List<NotificationEmployees> allNotificationsByEmployee = notificationEmployeesService
				.findNotificationsByEmployee(empno);
		model.addAttribute("allNotifications", allNotificationsByEmployee);
		return "employees/empNotification";
	}

	@GetMapping("/readAllNotification")
	public String readAll(Model model, HttpServletRequest request){
		System.out.println("有傳送");
		HttpSession session = request.getSession();
		String empno = (String) session.getAttribute("account");
		Date date = new Date();
		System.out.println(empno);
		System.out.println(date);
		notificationEmployeesService.updateRead(empno, date);
		return "redirect:/allnotification";
	}
	
//	@GetMapping("/readAllNotification")
//	public ResponseEntity<String> readAll(HttpServletRequest request){
//		System.out.println("有傳送");
//		HttpSession session = request.getSession();
//		String empno = (String) session.getAttribute("account");
//		Date date = new Date();
//		System.out.println(empno);
//		System.out.println(date);
//		notificationEmployeesService.updateRead(empno, date);
//		return ResponseEntity.ok("ok!");
//	}
}
