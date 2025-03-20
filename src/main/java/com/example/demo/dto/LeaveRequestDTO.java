package com.example.demo.dto;

import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

//嘗試建立dto來與axios前端作互動
@Setter
@Getter
@NoArgsConstructor
public class LeaveRequestDTO {
	
	private String empno;
    private String requestTypeID;
    private String reason;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime  startTime;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime  endTime;
    private int totalHours;
    private String delegateID;
    private String approvalNo1ID;
    private String approvalNo2ID;
    private String approvalNo3ID;
    private MultipartFile file;

}
