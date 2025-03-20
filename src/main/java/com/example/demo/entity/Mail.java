package com.example.demo.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Mail {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer mailno;
	
	
	private String mailRecipient;
	private String mailTitle;
	private String mailText;
	
	public Mail() {

	}

	public Mail(String mailRecipient, String mailTitle, String mailText) {
		this.mailRecipient = mailRecipient;
		this.mailTitle = mailTitle;
		this.mailText = mailText;
	}

	public Integer getMailno() {
		return mailno;
	}

	public void setMailno(Integer mailno) {
		this.mailno = mailno;
	}

	public String getMailRecipient() {
		return mailRecipient;
	}

	public void setMailRecipient(String mailRecipient) {
		this.mailRecipient = mailRecipient;
	}

	public String getMailTitle() {
		return mailTitle;
	}

	public void setMailTitle(String mailTitle) {
		this.mailTitle = mailTitle;
	}

	public String getMailText() {
		return mailText;
	}

	public void setMailText(String mailText) {
		this.mailText = mailText;
	}
	

	
	

	
}
