package com.example.demo.controller;

import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.SalTotalDTO;
import com.example.demo.entity.Employees;
import com.example.demo.entity.SalRecordBean;
import com.example.demo.entity.SalRecordFinal;

import com.example.demo.service.EmployeesService;
import com.example.demo.service.SalRecordFinalService;
import com.example.demo.service.SalaryService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@RestController
public class SalaryController {
	@Autowired
	private SalaryService salaryService;

	
	
	@GetMapping("/fetchSalaryData")
	public Map<String, Object> fetchSalaryData(@RequestParam String empno,  @RequestParam String year, @RequestParam String month) {
		
		return salaryService.getSalaryData(empno, year, month);
	}


}
