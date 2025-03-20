package com.example.demo.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entity.NotificationEmployees;
import com.example.demo.repository.NotificationEmployeesRepository;

@Service
public class NotificationEmployeesService {

    @Autowired
    private NotificationEmployeesRepository notificationEmployeesRepository;

    // 創建新的通知與員工關聯表
    public NotificationEmployees createNotificationEmployees(NotificationEmployees notificationEmployees) {
        return notificationEmployeesRepository.save(notificationEmployees);
    }

    // 查詢員工的未讀通知
    public List<NotificationEmployees> findUnreadNotificationsByEmployee(String empno) {
        return notificationEmployeesRepository.findByEmployeesEmpnoAndIsReadFalse(empno);
    }
    
    // 查詢所有通知與員工關聯表
    public List<NotificationEmployees> findAll() {
        return notificationEmployeesRepository.findAll();
    }
    
    
    public void updateRead(Integer id,Date date) {
    	notificationEmployeesRepository.updateRead(id,date);
    }
    
    // 查詢員工的所有通知
    public List<NotificationEmployees> findNotificationsByEmployee(String empno) {
    	return notificationEmployeesRepository.findByEmployeesEmpno(empno);
    }

    public void deletebyempno(String empno) {
    	notificationEmployeesRepository.deleteByempno(empno);
    }
    
    public void updateRead(String empno,Date date) {
    	notificationEmployeesRepository.updateRead(empno,date);
    }
}
