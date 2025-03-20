package com.example.demo.util;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import org.apache.naming.java.javaURLContextFactory;

public class TimeTransfer {
	//Servlet時期要將後段資料傳到前端為了固定時間格式而寫的static方法
//	public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
//	public static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm:ss");
	
	
//	LocalDate currentDate = LocalDate.now();
//	System.out.println("1"+currentDate);//2024-07-24
//	
//	LocalDateTime now = LocalDateTime.now();
//	System.out.println("2"+now);//2024-07-24T20:40:23.617613200
//	
//	LocalDateTime reminderTime = now.plusHours(1);
//	System.out.println("3"+reminderTime);//2024-07-24T21:40:23.617613200
//	
//	Date rightnow = new Date();
//	System.out.println("4"+ rightnow);//Wed Jul 24 20:40:23 CST 2024
	
	public static LocalDate dateToLocalDate(Date date) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		String dateString = dateFormat.format(date);
        DateTimeFormatter dateFormate = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate localDate = LocalDate.parse(dateString, dateFormate);
        return localDate;
	}
	
	//webexService、跟notificationService使用
	public static LocalDateTime connectDateAndTime(Date date, Date time) {
		
        // 使用SimpleDateFormat格式化時間的字串，如果直接將util.date轉換成字串，格式會不符合
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
//        String dateString = date.toString();
//        String timeString = time.toString();
        String dateString = dateFormat.format(date);
        String timeString = timeFormat.format(time);
        
        DateTimeFormatter dateFormate = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate localDate = LocalDate.parse(dateString, dateFormate);
        
        DateTimeFormatter timeFormate = DateTimeFormatter.ofPattern("HH:mm:ss");
//        LocalTime localStartTime = LocalTime.parse(timeString,timeFormate);
        LocalTime localTime;
        if (timeString.length() == 5) {
            timeString += ":00"; // 如果没有秒，补上秒
        }
        localTime = LocalTime.parse(timeString, timeFormate);
        
        LocalDateTime endDateTime = LocalDateTime.of(localDate, localTime);//2024-07-20T13:00
        
//        
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
//        LocalDateTime DateTime = LocalDateTime.parse(endDateTime.toString(),formatter);
        
        return endDateTime;
	}
	
	
	public static Date utilDate(String eventDay) throws ParseException {
		 // 定义时间格式
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		return sdf.parse(eventDay);		
	}
	
	public static Date utilTime(String time) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		return sdf.parse(time);
		
	}
	
	
	
	/*
	 * 過往java bean的資料型態都是使用sql.date&sql.time，故需要將前端傳入的字串轉型才能存入資料庫當中
	 * springboot的內建時間格式設定解決了此問題，將時間都改成java.util.Date
	 */
	
	public static java.sql.Date sqlDate(String eventDay) {
		
		 // 定义时间格式
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		try {
			java.util.Date utilDate = sdf.parse(eventDay);
			java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());
			return sqlDate;
		} catch (ParseException e) {

			e.printStackTrace();
		}
		return null;
	}
//	public static Time sqlTime(String time) {
//		 // 定义时间格式
//		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");//html傳入的格式是HH:mm 要跟這邊相同
//        // 解析字符串到 java.util.Date
//		try {
//			java.util.Date utilDate = sdf.parse(time);
//			Date utilTime = new java.sql.Date(utilDate.getTime());
//			Time sqlTime = new Time(utilTime.getTime());
//			return sqlTime;
//		} catch (ParseException e) {
//			e.printStackTrace();
//		}		
//		return null;
//	}
}
