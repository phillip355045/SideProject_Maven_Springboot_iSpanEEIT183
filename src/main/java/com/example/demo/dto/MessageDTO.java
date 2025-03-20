package com.example.demo.dto;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
public class MessageDTO {
	private String empno;// 發送者
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")

	private long chattime;// 時間
	private String content;// 訊息內容
	private String receiver;// 接收者
	private String rname;
    private String ename;
}
