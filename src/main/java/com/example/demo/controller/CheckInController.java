package com.example.demo.controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.entity.CheckIn;
import com.example.demo.entity.CheckInId;
import com.example.demo.service.CheckInService;
import com.example.demo.service.NotificationService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
public class CheckInController {

	@Autowired
	private CheckInService checkInService;
	
	@Autowired
	private NotificationService notificationService;
	
	@GetMapping("/addCheckInTableData")
	public String addCheckInTableData() {
		return "checkin/back/addCheckInTableData";
	}

	@GetMapping("/empCheckIn")
	public String empCheckIn(Model model, HttpServletRequest request) throws ParseException {
		HttpSession session = request.getSession();
		String empno = (String) session.getAttribute("account");
		String note = checkInService.findByCheckInId(empno);
		model.addAttribute("note", note);
		return "checkin/empCheckIn";
	}

	@PostMapping("/checkin/generate")
	public String generateCheckIns(@RequestParam int year, @RequestParam int month) {
		checkInService.generateCheckInsForAllEmployees(year, month);
		return "redirect:/addCheckInTableData?success=true";
	}

	@PostMapping("/CheckIn.controller")
	public String empCheckIn(HttpServletRequest request, @RequestParam("checkinTime") String checkinTime)
			throws ParseException {
		HttpSession session = request.getSession();
		String empno = (String) session.getAttribute("account");

		if (empno != null && !empno.isEmpty()) {
			checkInService.handleCheckIn(empno, checkinTime);
			return "redirect:/empCheckIn?success=true";
		} else {
			return "redirect:/";
		}
	}

	// 獲得員工全部資料
	@GetMapping("/allCheckIns")
	public String getAllCheckIns(Model model) {
		List<CheckIn> checkIns = checkInService.getAllCheckIns();
		model.addAttribute("checkIn", checkIns);
		return "checkin/back/allCheckIns";
	}
	
	// 獲得員工全部資料
	@GetMapping("/checkInchart")
	public String checkInchart(Model model) {
		List<CheckIn> checkIns = checkInService.getAllCheckIns();
		model.addAttribute("checkIn", checkIns);
		return "checkin/back/checkInchart";
	}

//	// 獲得員工全部資料by分頁
//    @GetMapping("/allCheckIns")
//    public String getAllCheckIns(Model model,
//                                  @RequestParam(value = "page", defaultValue = "1") int page,
//                                  @RequestParam(value = "size", defaultValue = "5") int size) {
//    	Page<CheckIn> checkIns = checkInService.getCheckInPage(page, size);
//        model.addAttribute("checkIn", checkIns);
//        model.addAttribute("size", size);
//        return "checkin/back/allCheckIns";
//    }

	// 抓取員工打卡資料
	@GetMapping("/findUpdateCheckInData")
	public String findUpdateCheckInData(@RequestParam String empno,
			@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date date, Model model) {
		CheckInId checkInId = new CheckInId(empno, date);
		CheckIn checkIn = checkInService.findCheckinById(checkInId);
		model.addAttribute("checkIn", checkIn);
		return "checkin/back/editCheckInData";
	}

	// 更新後存檔
	@PostMapping("/updateCheckIn")
	public String updateCheckIn(@ModelAttribute("checkIn") CheckIn checkIn) {
		checkInService.saveCheckIn(checkIn);
		return "redirect:/allCheckIns";
	}

	@DeleteMapping("/deleteCheckInById")
	public String deleteCheckInById(@RequestParam("empno") String empno,
			@RequestParam("date") @DateTimeFormat(pattern = "yyyy-MM-dd") Date date) {
		CheckInId checkInId = new CheckInId(empno, date);
		checkInService.deleteById(checkInId);
		return "redirect:/allCheckIns";
	}

	// 查詢畫面
	@GetMapping("/checkInSearch")
	public String checkInSearch() {
		return "checkin/back/checkInSearch";
	}

	// 依照欄位查詢
	@PostMapping("/getCheckInByCol")
	public String getCheckInByCol(@RequestParam String col, @RequestParam String colvalue, Model model)
			throws ParseException {
		List<CheckIn> checkIns = checkInService.getCheckInByCol(col, colvalue);
		model.addAttribute("checkIn", checkIns);
		return "checkin/back/findcheckInByCol";
	}

	@GetMapping("/getEmpCheckIn")
	public String getEmpCheckIn(HttpServletRequest request, Model model) throws ParseException {
		HttpSession session = request.getSession();
		String empno = (String) session.getAttribute("account");

		if (empno != null && !empno.isEmpty()) {
			List<CheckIn> checkIns = checkInService.getEmpCheckIn(empno);
			model.addAttribute("checkIn", checkIns);
			return "checkin/empCheckInsList";
		} else {
			return "redirect:/";
		}
	}

	@GetMapping("/deleteCheckInByDateOrEmpno")
	public String deleteCheckInByDateOrEmpno() {
		return "checkin/back/deleteCheckInByDateOrEmpno";
	}

	// 依照欄位刪除
	@PostMapping("/deleteCheckInByCol")
	public String deleteCheckInByCol(@RequestParam String col, @RequestParam String colvalue,
			@RequestParam String operator) throws ParseException {
		checkInService.deleteByCol(col, colvalue, operator);
		return "redirect:/deleteCheckInByDateOrEmpno?success=true";
	}

	@GetMapping("/downloadAccountCheckInData")
	public ResponseEntity<InputStreamResource> downloadAccountCheckInData(HttpServletRequest request)
			throws IOException, ParseException {
		HttpSession session = request.getSession();
		String empno = (String) session.getAttribute("account");
		String name = (String) session.getAttribute("name");
		
		List<CheckIn> checkInData = checkInService.getEmpCheckIn(empno);

		StringBuilder csvData = new StringBuilder();
		csvData.append("date,workon,workoff\n");
		for (CheckIn checkin : checkInData) {
			csvData.append(checkin.getCheckInId().getDate()).append(",");
			csvData.append(checkin.getWorkon()).append(",");
			csvData.append(checkin.getWorkoff()).append("\n");
		}

		ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(
				csvData.toString().getBytes(StandardCharsets.UTF_8));

		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Disposition", "attachment; filename="+empno+"checkin_data.csv");
		
		notificationService.createNotificationBasisOnEmpno("打卡資料下載", empno);
		notificationService.createNotificationBasisOnEmpno("員工下載打卡資料", "A0001", empno, name);

		return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_OCTET_STREAM)
				.body(new InputStreamResource(byteArrayInputStream));
	}

	@GetMapping("/downloadAllCheckInData")
	public ResponseEntity<InputStreamResource> downloadAllCheckInData() throws IOException, ParseException {
		List<CheckIn> checkInData = checkInService.getAllCheckIns();

		StringBuilder csvData = new StringBuilder();
		csvData.append("empno,date,workon,workoff\n");
		for (CheckIn checkin : checkInData) {
			csvData.append(checkin.getCheckInId().getEmpno()).append(",");
			csvData.append(checkin.getCheckInId().getDate()).append(",");
			csvData.append(checkin.getWorkon()).append(",");
			csvData.append(checkin.getWorkoff()).append("\n");
		}

		ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(
				csvData.toString().getBytes(StandardCharsets.UTF_8));

		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Disposition", "attachment; filename=all_checkin_data.csv");

		return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_OCTET_STREAM)
				.body(new InputStreamResource(byteArrayInputStream));
	}

	
	
	
	@GetMapping("/empCheckInsByMonth")
	public String empCheckInsByMonth(Model model) {

        List<String[]> months = new ArrayList<>();
        for (int i = 1; i <= 12; i++) {
            String month = String.format("2024-%02d", i);
            String nextMonth=null;
            if(i==12) {
            	nextMonth = String.format("2025-%02d-01", (i % 12) + 1);
            }else {
            	nextMonth = String.format("2024-%02d-01", (i % 12) + 1);
            }
            months.add(new String[]{month, nextMonth});
        }
        model.addAttribute("months", months);

		return "checkin/empCheckInsByMonth";

	}

	
	@GetMapping("/empCheckInSearchByMonth")
	public String empCheckInSearchByMonth(HttpServletRequest request,Model model,@RequestParam String startDate,@RequestParam String endDate) throws ParseException{
		HttpSession session = request.getSession();
		String empno = (String) session.getAttribute("account");
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date sDate=dateFormat.parse(startDate);
		Date eDate=dateFormat.parse(endDate);
		
		List<CheckIn> checkInList = checkInService.empCheckInSearchByMonth(empno, sDate, eDate);
		
		model.addAttribute("checkIn", checkInList);
		return "checkin/empCheckInsList";
		
		
		
	}
	
	
	
}
