package com.example.demo.controller;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.example.demo.dto.PostDto;
import com.example.demo.dto.PostWallDto;
import com.example.demo.dto.SalTotalDTO;
import com.example.demo.entity.BusinessTrip;
import com.example.demo.entity.ChatMessage;
import com.example.demo.entity.CheckIn;
import com.example.demo.entity.Employees;
import com.example.demo.entity.Invoice;
import com.example.demo.entity.LeaveRequest;
import com.example.demo.entity.Worklogs;
import com.example.demo.entity.Post;
import com.example.demo.entity.SalRecordFinal;
import com.example.demo.service.BusinessTripService;
import com.example.demo.service.ChatMessageService;
import com.example.demo.service.CheckInService;
import com.example.demo.service.DepartmentService;

import com.example.demo.service.EmployeesService;
import com.example.demo.service.InvoiceService;
import com.example.demo.service.LeaveRequestService;
import com.example.demo.service.NotificationService;
import com.example.demo.service.WorklogsService;

import com.example.demo.service.PostService;
import com.example.demo.service.SalRecordFinalService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
public class PageController {

	@Autowired
	private CheckInService checkInService;
	@Autowired
	private EmployeesService employeesService;
	@Autowired
	private SalRecordFinalService salRecordFinalService;
	@Autowired
	private LeaveRequestService leaveRequestService;
	
	@Autowired
	private BusinessTripService businessTripService;
	
	@Autowired
	private InvoiceService invoiceService;
	
	@Autowired
	private WorklogsService WorklogsService;

	@Autowired
	private PostService postService;

	@Autowired
	private ChatMessageService chatMessageService;

	@Autowired
	private NotificationService notificationService;

	@GetMapping("/backHomePage")
	public String backHomePage() {
		return "employees/back/backHomePage";
	}

	@GetMapping("/")
	public String home() {
		return "index.html";
	}

	// 登入後需要的功能都可以寫在這裡面!!!只是要寫的話要先跟其他人說!!不然會衝突!!
	@GetMapping("/logInHomePage")
	public String logInHomePage(Model model, HttpServletRequest request) throws ParseException {

		// 首頁打卡時間顯示功能!!
		HttpSession session = request.getSession(false);
		if (session == null) {
			// 閒置過久則登出
			return "redirect:/";
		}
		String empno = (String) session.getAttribute("account");

		CheckIn checkIn = checkInService.getAccountCheckIn(empno);
		if (checkIn != null) {
			Date workon = checkIn.getWorkon();
			Date workoff = checkIn.getWorkoff();
			if (workon == null) {
				notificationService.createNotificationBasisOnEmpno("提醒上班打卡", empno);
			}
			model.addAttribute("workon", workon);
			model.addAttribute("workoff", workoff);
			// 假設沒有該筆員工資料，則回傳null
		} else {
			System.out.println("這裡有執行嗎");
			notificationService.createNotificationBasisOnEmpno("提醒上班打卡", empno);
			model.addAttribute("workon", null);
			model.addAttribute("workoff", null);
		}

		// 首頁前一次登入時間顯示
		Employees employee = employeesService.findUsersByEmpno(empno);
		Date lastTimeLogin = employee.getLastTimeLogin();
		Date now = new Date();
		System.out.println(lastTimeLogin);
		model.addAttribute("lastTimeLogin", lastTimeLogin);
		model.addAttribute("now", now);

		// 首頁薪資細項顯示功能
		List<SalRecordFinal> emps = salRecordFinalService.getAllSals().stream()
                .filter(emp -> empno.equals(emp.getEmpno()) && "已發送".equals(emp.getStatus()))
                .collect(Collectors.toList());
		
		List<SalTotalDTO> empDtos = new ArrayList<>();

		for (SalRecordFinal emp : emps) {
			// 計算薪資相關金額
			double sal = emp.getSal().doubleValue();
			double foodAllowance = emp.getFoodAllowance().doubleValue();
			double trafficAllowance = emp.getTrafficAllowance().doubleValue();
			double mgrAllowance = emp.getMgrAllowance().doubleValue();
			double holidayAllowance = emp.getHolidayAllowance().doubleValue();
			double overtimePay = emp.getOvertimePay().doubleValue();
			double attendanceBonus = emp.getAttendanceBonus().doubleValue();

			double laborInsurance = emp.getLaborInsurance().doubleValue();
			double healthInsurance = emp.getHealthInsurance().doubleValue();

			double totalDeduction = laborInsurance + healthInsurance;
			double totalAmount = sal + foodAllowance + trafficAllowance + mgrAllowance + holidayAllowance + overtimePay
					+ attendanceBonus;
			double netPay = totalAmount - totalDeduction;

			// 將計算結果取整數
			int roundedTotalDeduction = (int) Math.round(totalDeduction);
			int roundedTotalAmount = (int) Math.round(totalAmount);
			int roundedNetPay = (int) Math.round(netPay);

			// 創建 DTO 並設置屬性
			SalTotalDTO dto = new SalTotalDTO();

			dto.setSalno(emp.getSalno());
			dto.setEmpno(emp.getEmpno());
			dto.setYear(emp.getYear());
			dto.setMonth(emp.getMonth());
			dto.setSal(emp.getSal());
			dto.setFoodAllowance(emp.getFoodAllowance());
			dto.setTrafficAllowance(emp.getTrafficAllowance());
			dto.setMgrAllowance(emp.getMgrAllowance());
			dto.setHolidayAllowance(emp.getHolidayAllowance());
			dto.setOvertimePay(emp.getOvertimePay());
			dto.setAttendanceBonus(emp.getAttendanceBonus());
			dto.setLaborInsurance(emp.getLaborInsurance());
			dto.setHealthInsurance(emp.getHealthInsurance());
			dto.setTotalAmount(roundedTotalAmount);
			dto.setTotalDeduction(roundedTotalDeduction);
			dto.setNetPay(roundedNetPay);

			// 將 DTO 添加到列表中
			empDtos.add(dto);
		}

		model.addAttribute("emps", empDtos);

		// 查詢特定員工請假資料
		List<LeaveRequest> leaveRequests = leaveRequestService.getLeaveRequestsByEmpno(empno);
		model.addAttribute("leaveRequests", leaveRequests);
		
		//查詢特定員工出差資料
		List<BusinessTrip> businessTrips = businessTripService.getBusinessTripByEmpno(empno);
		model.addAttribute("businessTrips", businessTrips);
		
		//查詢特定員工請款資料
		List<Invoice> invoices =  invoiceService.getInvoiceByEmpno(empno);
		model.addAttribute("invoices", invoices);

		// 查詢特定員工工作日誌(員工編號)
		List<Worklogs> Worklogs = WorklogsService.getWorklogsByEmpno(empno);
		model.addAttribute("Worklogs", Worklogs);

		// 查詢貼文資料(限定'活動','考勤','薪資','福利')
		List<PostDto> posts = postService.findAllPostsOnHomePage();
		model.addAttribute("posts", posts);
//		System.out.println(posts.get(0).getPostDate());
		
		// 聊天
		List<ChatMessage> chatMessages = chatMessageService.getAllMessages(empno);
		model.addAttribute("chatMessages", chatMessages);
		model.addAttribute("empno", session.getAttribute("account"));
		model.addAttribute("name", session.getAttribute("name"));
		model.addAttribute("employees", employeesService.getAllEmployees());

		return "employees/HomePage";
	}
}
