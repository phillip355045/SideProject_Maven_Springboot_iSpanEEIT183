package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;


@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendSimpleEmail(String to,
    		String subject,String text,
    		MultipartFile[] files) throws MessagingException {
    	
    	String subject2 = "PSNEXUS:";
    	String totalSubject = subject2 + subject;
    	
//        SimpleMailMessage message = new SimpleMailMessage();
//        message.setTo(to);
//        message.setSubject(totalSubject);
//        message.setText(text);
//        message.setFrom("polarstar@gmail.com");

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(message, true);
        mimeMessageHelper.setTo(to);
        mimeMessageHelper.setSubject(totalSubject);
        mimeMessageHelper.setText(text);
        
        if(files != null) {       	
        	for(MultipartFile file : files) {
        		mimeMessageHelper.addAttachment(file.getOriginalFilename(), file);
        	}
        }
        
        mailSender.send(message);
    }
}
