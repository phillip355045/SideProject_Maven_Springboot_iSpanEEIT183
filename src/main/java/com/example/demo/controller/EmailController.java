package com.example.demo.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.entity.Mail;
import com.example.demo.repository.MailRepository;
import com.example.demo.service.EmailService;
import com.example.demo.service.MailService;

import jakarta.mail.MessagingException;
import jakarta.persistence.Entity;

@RestController
public class EmailController {

    @Autowired
    private EmailService emailService;
    
    @Autowired
    private MailService mailService;


    @PostMapping("/sendEmail")
    //public String sendEmail(@RequestBody Mail mail) {
    public String sendEmail(@RequestParam String mailRecipient,
            @RequestParam String mailTitle,
            @RequestParam String mailText,
            @RequestParam(name = "files", required = false) MultipartFile[] files) {	
    	
    	System.out.println("有傳送"+mailRecipient);
    	Mail mail = new Mail(mailRecipient,mailTitle,mailText);
    	
    	//儲存寄件資料到資料庫當中
    	mailService.save(mail);
    	
    	//透過springmail將信件寄出
//        emailService.sendSimpleEmail(mail.getMailRecipient(), mail.getMailTitle(), mail.getMailText());
        try {
			emailService.sendSimpleEmail(mailRecipient,mailTitle,mailText,files);
			return "Email sent successfully";
		} catch (MessagingException e) {
			e.printStackTrace();
			return "Error while sending mail: " + e.getMessage();
		}
    }
    
    @ResponseBody
    @PostMapping("/ReplyMessage")
    public String replyMessage(@RequestBody Map<String, String> requestData) {    	
        String mailRecipient = requestData.get("mail");
        String mailText = requestData.get("message");
    	String mailTitle = "回復意見";
    	Mail mail = new Mail(mailRecipient,mailTitle,mailText); 	
    	mailService.save(mail);
    	
        try {
			emailService.sendSimpleEmail(mailRecipient,mailTitle,mailText,null);
			return "Email sent successfully";
		} catch (MessagingException e) {
			e.printStackTrace();
			return "Error while sending mail: " + e.getMessage();
		}
    }
}
