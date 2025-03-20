package com.example.demo.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.example.demo.entity.Connection;
import com.example.demo.service.ConnectionService;
import com.example.demo.service.NotificationService;

@Controller
public class ConnectionController {

	@Autowired
	private ConnectionService connectionService;

	@Autowired
	private NotificationService notificationService;
	
	@PostMapping("/addconnection")
	public ResponseEntity<String> addconnection(@RequestBody Connection connection) {
		System.out.println(connection.getEmpno());
		System.out.println(connection.getMail());
		System.out.println(connection.getMessage());
		System.out.println(connection.getName());
		if(connection.getEmpno() == null) {
			connection.setEmpno("訪客");
		}
		String empno = connection.getEmpno();
		Date date=new Date();
		connection.setConnectTime(date);
		connectionService.saveConnection(connection);
		SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String format = simpleDateFormat.format(date);
		notificationService.createNotificationBasisOnEmpno("意見箱有新留言", "A0001",format , empno);
		return ResponseEntity.ok("ok");

	}
	
	@GetMapping("/allconnection")
	public String allconnection(Model model) {
		List<Connection> allconnection = connectionService.allconnection();
		model.addAttribute("connections", allconnection);
		return "employees/back/allconnection";

	}	
	

}
