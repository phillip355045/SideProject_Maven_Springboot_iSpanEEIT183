package com.example.demo.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class WebexService {

//    @Value("${webex.api.access-token}")  //2024/08/03使用
    private String accessToken = "OTU1YjVmMWYtZmE0OC00YmU1LWJhNDctMzUxNmM2MWFlNTFjODdhZWZmZGYtMzll_P0A1_f12782f8-b766-4b7a-91c1-233eb802c8fc";
//    @Value("${webex.api.base-url}")
    private String baseUrl = "https://webexapis.com/v1";
    
    @Autowired
    private final RestTemplate restTemplate;
    
    private final ObjectMapper objectMapper;
    
    public WebexService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        this.objectMapper = new ObjectMapper();
    }
    
    public String createMeeting(String title, Date date, Date startTime, Date endTime){
        String url = baseUrl + "/meetings";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(accessToken);
        
        //這邊因為是從資料庫取值，toInstant()會認為他是sql.date，無法使用
//        LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
//        LocalTime localStartTime = startTime.toInstant().atZone(ZoneId.systemDefault()).toLocalTime();
//        LocalTime localEndTime = endTime.toInstant().atZone(ZoneId.systemDefault()).toLocalTime();
        
//        LocalDateTime startDateTime = TimeTransfer.connectDateAndTime(date, startTime);
//        LocalDateTime endDateTime = TimeTransfer.connectDateAndTime(date, endTime);
        
        //轉換成localdateTime
        String dateString = date.toString();
        String startTimeString = startTime.toString();
        String endTimeString = endTime.toString();
        
        DateTimeFormatter dateFormate = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate localDate = LocalDate.parse(dateString, dateFormate);
        
        DateTimeFormatter timeFormate = DateTimeFormatter.ofPattern("HH:mm:ss");
        LocalTime localStartTime = LocalTime.parse(startTimeString,timeFormate);
        LocalTime localEndTime = LocalTime.parse(endTimeString,timeFormate);
        
        //進行拼接
        LocalDateTime startDateTime = LocalDateTime.of(localDate, localStartTime);
        LocalDateTime endDateTime = LocalDateTime.of(localDate, localEndTime);
        
        // 格式化为 ISO 8601 字符串
        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
        String start = startDateTime.format(formatter);
        String end = endDateTime.format(formatter);
        
        //找時間看start跟end印出來長什麼樣
        
        Map<String, Object> request = new HashMap<>();
        request.put("title", title);
        request.put("start", start);
        request.put("end", end);
        request.put("enabledJoinBeforeHost", false); // 禁止在主持人之前加入
        request.put("joinBeforeHostMinutes", 0);
        
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);
        

		try {
			ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
			if (response.getStatusCode() != HttpStatus.OK) {
				return null;
			}
			//解析response當中的webLink
			JsonNode root = objectMapper.readTree(response.getBody());
			String webLink = root.path("webLink").asText();
			System.out.println("-----------" + root);
			return webLink;
		}catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                // 處理 401 Unauthorized 錯誤
                return null;
            }
            e.printStackTrace();
            return null;		
		}catch (Exception e) {
			e.printStackTrace();
			return null;
		}
    }
}
