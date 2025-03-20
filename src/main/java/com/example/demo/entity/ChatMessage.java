package com.example.demo.entity;


import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "chatmessage")
@Component
public class ChatMessage {
	 	@Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long id;
	    
	    private String empno;
	    private String ename;
	    private String content;
	    private String receiver;
	    private String rname;
	    
	    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
		@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
		@Temporal(TemporalType.TIMESTAMP)
	    private Date chattime;
	    

	    public ChatMessage(String empno, String content, Date chattime, String receiver) {
	        this.empno = empno;
	        this.content = content;
	        this.chattime = chattime;
	        this.receiver=receiver;
	    }
}
