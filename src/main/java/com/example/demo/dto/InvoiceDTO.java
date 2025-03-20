package com.example.demo.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
//嘗試建立dto來與axios前端作互動
@Setter
@Getter
@NoArgsConstructor
public class InvoiceDTO {
	
	private String empno;
	
	private String description;
	
	private BigDecimal amount;
	
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime paymentDate;
	
	
	private String approvalNo1ID;
    private String approvalNo2ID;
    private String approvalNo3ID;
    private MultipartFile file;
	

}
