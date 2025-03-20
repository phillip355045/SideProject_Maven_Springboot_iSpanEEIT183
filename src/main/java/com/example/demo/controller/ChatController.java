package com.example.demo.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.demo.entity.ChatMessage;
import com.example.demo.entity.Employees;
import com.example.demo.repository.ChatMessageRepository;
import com.example.demo.service.ChatMessageService;
import com.example.demo.service.EmployeesService;

import jakarta.servlet.http.HttpSession;


@Controller
//@RequestMapping("/PSNEXUS")
public class ChatController {

	@Autowired
	private ChatMessageRepository chatMessageRepo;

	@Autowired
	private ChatMessageService chatMessageService;

	@Autowired
	private SimpMessagingTemplate messagingTemplate;
	
	@Autowired
	private EmployeesService employeesService;

    // 當客戶端發送到 "/app/sendMessage" 的消息時，此方法會被調用
    @MessageMapping("/sendMessage")
    // 將處理過的消息發送到 "/topic/messages" 主題，讓所有訂閱此主題的客戶端收到消息
    @SendTo("/topic/messages")
    public ChatMessage sendMessage(ChatMessage message) {
    	// 這裡可以進行一些邏輯處理，比如添加時間戳、格式化消息等
    	// 設定消息的時間戳
        message.setChattime(new Date());
        // 將消息保存到資料庫
        chatMessageRepo.save(message);
        
        // 發送消息到接收者
        messagingTemplate.convertAndSendToUser(message.getReceiver(), "/queue/messages", message);
        
        return message; // 返回處理後的消息，將發送給訂閱者
    }
    
    @GetMapping("/getChatHistory")
    public ResponseEntity<Map<String, List<ChatMessage>>> getChatHistory(@RequestParam String sender, @RequestParam String receiver) {
        List<ChatMessage> chatHistory = chatMessageRepo.findChatHistory(sender, receiver);
        
        Map<String, List<ChatMessage>> response = new HashMap<>();
        response.put("chatHistory", chatHistory);
        
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    
    @GetMapping("/getCurrentUser")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getCurrentUser(HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        
        String empno = (String) session.getAttribute("account");
        String name = (String) session.getAttribute("name");
        
        if (empno != null && name != null) {
            response.put("empno", empno);
            response.put("name", name);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }
    
    @GetMapping("/somePage")
    public String somePage(Model model, HttpSession session) {
        List<Employees> employees = employeesService.getAllEmployees();
        model.addAttribute("employees", employees);
        return "somePage";
    }


}
