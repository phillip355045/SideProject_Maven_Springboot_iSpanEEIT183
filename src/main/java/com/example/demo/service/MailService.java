package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entity.Mail;
import com.example.demo.repository.MailRepository;

@Service
public class MailService {
	
	@Autowired
	private MailRepository mailRepos;
	
	public void save(Mail mail) {
		mailRepos.save(mail);
	}
}
