package com.example.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entity.ChatMessage;
import com.example.demo.entity.Employees;
import com.example.demo.repository.ChatMessageRepository;
import com.example.demo.repository.EmployeesRepository;

@Service
public class ChatMessageService {

	@Autowired
	private ChatMessageRepository chatMessageRepo;
	
	@Autowired
	private EmployeesRepository employeesRepo;

	public List<ChatMessage> getAllMessages(String empno) {
		return chatMessageRepo.findAll();
	}

	// 透過姓名帳號取得員工
	public List<Employees> getEmployeesByEmpnoAndName(String query) {
		return employeesRepo.findByEmpnoContainingOrNameContaining(query, query);
	}

	// 抓取員工編號
	public Employees getCurrentEmployee(String empno) {
		return employeesRepo.findByEmpno(empno);
	}
	
	// 新增
		public ChatMessage insertMessage (ChatMessage chatMessage) {
			return chatMessageRepo.save(chatMessage);
		}
}
