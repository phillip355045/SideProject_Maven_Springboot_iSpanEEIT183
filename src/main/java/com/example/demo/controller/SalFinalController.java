package com.example.demo.controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
import com.example.demo.entity.CheckIn;
import com.example.demo.entity.Employees;
import com.example.demo.entity.LaborIhealthLevel;
import com.example.demo.entity.SalRecordBean;
import com.example.demo.entity.SalRecordFinal;
import com.example.demo.repository.SalRecordFinalRepository;
import com.example.demo.service.EmployeesService;
import com.example.demo.service.NotificationService;
import com.example.demo.service.SalRecordFinalService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
public class SalFinalController {
	@Autowired
	private SalRecordFinalService salRecordFinalService;
	@Autowired
	private EmployeesService employeesService;
	@Autowired
	private NotificationService notificationService;
	 @Autowired
	 private SalRecordFinalRepository salRecordFinalRepo;

	public SalFinalController(SalRecordFinalService salRecordFinalService) {
		this.salRecordFinalService = salRecordFinalService;
	}

//	網站首頁
	@GetMapping("/frontIndex")
	public String processMainActionHome() {
		return "frontIndex";
	}

	@GetMapping("/empSalfront")
	public String processMainAction10() {
		return "sal/salfront/empSalfront";
	}

//	@GetMapping("/empsal")
//	public String processMainAction1() {
//		return "sal/empSal";
//	}

	@GetMapping("/toGetEmpsal")
	public String processMainAction2() {
		return "sal/ToGetEmp";
	}

//	後台新增
	@GetMapping("/toInsertEmpSal")
	public String processMainAction3() {
		return "sal/back/insertEmpSal";
	}

	@GetMapping("/toInsertEmpSalBatch")
	public String processMainAction4() {
		return "sal/back/insertEmpSalBatch";
	}

	@GetMapping("/toGetInsurance")
	public String processMainAction5() {
		return "sal/ToGetInsurance";
	}

//	勞健保試算
	@GetMapping("/getInsurance")
	public String getInsurance(@RequestParam("roundedTotalAmount") String roundedTotalAmount, Model model) {
        // 解析月薪總額
		  BigDecimal salary = new BigDecimal(roundedTotalAmount);

        // 從資料庫中獲取對應的級距數據
        Optional<LaborIhealthLevel> optionalLevel = salRecordFinalRepo.findBySalary(salary);

        if (optionalLevel.isPresent()) {
            LaborIhealthLevel level = optionalLevel.get();

            // 計算各項費用
            BigDecimal multiplier = new BigDecimal("0.024");
            BigDecimal laborInsuranceBase = (level.getId() == 52 ? level.getMinSalGetTotal() : level.getMaxSalGetTotal());
            BigDecimal laborInsuranceSelf = laborInsuranceBase.multiply(multiplier).setScale(0, RoundingMode.HALF_UP);
            
         // 如果勞保自付超過1100，則設為1100
            BigDecimal maxLaborInsuranceSelf = new BigDecimal("1100");
            if (laborInsuranceSelf.compareTo(maxLaborInsuranceSelf) > 0) {
                laborInsuranceSelf = maxLaborInsuranceSelf;
            }
            
            
            multiplier = new BigDecimal("0.071");
            BigDecimal laborInsuranceEmployer = salary.multiply(multiplier);

            multiplier = new BigDecimal("0.0155");
            BigDecimal healthInsuranceBase = (level.getId() == 52 ? level.getMinSalGetTotal() : level.getMaxSalGetTotal());
            BigDecimal healthInsuranceSelf = healthInsuranceBase.multiply(multiplier).setScale(0, RoundingMode.HALF_UP);

            multiplier = new BigDecimal("0.041");
            BigDecimal healthInsuranceEmployer = salary.multiply(multiplier);

            multiplier = new BigDecimal("0.06");
            BigDecimal pensionEmployer = salary.multiply(multiplier);

            BigDecimal totalSelf = laborInsuranceSelf.add(healthInsuranceSelf);
            BigDecimal totalEmployer = laborInsuranceEmployer.add(healthInsuranceEmployer).add(pensionEmployer);

            // 將計算結果傳遞給視圖
            model.addAttribute("salary", salary);
            model.addAttribute("laborInsuranceSelf", laborInsuranceSelf);
            model.addAttribute("laborInsuranceEmployer", laborInsuranceEmployer);
            model.addAttribute("healthInsuranceSelf", healthInsuranceSelf);
            model.addAttribute("healthInsuranceEmployer", healthInsuranceEmployer);
            model.addAttribute("pensionEmployer", pensionEmployer);
            model.addAttribute("totalSelf", totalSelf);
            model.addAttribute("totalEmployer", totalEmployer);
            model.addAttribute("employeeNetIncome", salary.subtract(totalSelf));
            model.addAttribute("employerTotalCost", salary.add(totalEmployer));

            return "sal/insuranceResult";
        } else {
            // 處理找不到級距數據的情況
            model.addAttribute("error", "無法找到對應的勞健保級距數據");
            return "error";
        }
    }

	
	
	// 單筆資料
	@GetMapping("/getEmpSal.controller")
	public String getEmpSal(@RequestParam("salno") Integer salno, Model model) {
		SalRecordFinal salRecordFinal = salRecordFinalService.getSal(salno);
		// 獲取員工的年份和月份
		String year = salRecordFinal.getYear();
		String month = salRecordFinal.getMonth();

		// 計算下個發薪日期
		int intYear = Integer.parseInt(year);
		int intMonth = Integer.parseInt(month);

		if (intMonth == 12) {
			intYear += 1;
			intMonth = 1;
		} else {
			intMonth += 1;
		}

		LocalDate payDate = LocalDate.of(intYear, intMonth, 10);
		DayOfWeek dayOfWeek = payDate.getDayOfWeek();

		if (dayOfWeek == DayOfWeek.SATURDAY) {
			payDate = payDate.minusDays(1);
		} else if (dayOfWeek == DayOfWeek.SUNDAY) {
			payDate = payDate.minusDays(2);
		}

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		String formattedPayDate = payDate.format(formatter);
		// 計算薪資相關金額

		double sal = salRecordFinal.getSal().doubleValue();
		double foodAllowance = salRecordFinal.getFoodAllowance().doubleValue();
		double trafficAllowance = salRecordFinal.getTrafficAllowance().doubleValue();
		double mgrAllowance = salRecordFinal.getMgrAllowance().doubleValue();
		double holidayAllowance = salRecordFinal.getHolidayAllowance().doubleValue();
		double overtimePay = salRecordFinal.getOvertimePay().doubleValue();
		double attendanceBonus = salRecordFinal.getAttendanceBonus().doubleValue();

		double laborInsurance = salRecordFinal.getLaborInsurance().doubleValue();
		double healthInsurance = salRecordFinal.getHealthInsurance().doubleValue();

		double totalDeduction = laborInsurance + healthInsurance;
		double totalAmount = sal + foodAllowance + trafficAllowance + mgrAllowance + holidayAllowance + overtimePay
				+ attendanceBonus;
		double netPay = totalAmount - totalDeduction;

		// 將計算結果取整數

		int roundlaborInsurance = (int) Math.round(laborInsurance);
		int roundhealthInsurance = (int) Math.round(healthInsurance);

		int roundedTotalDeduction = (int) Math.round(totalDeduction);
		int roundedTotalAmount = (int) Math.round(totalAmount);
		int roundedNetPay = (int) Math.round(netPay);

		// 將計算結果傳遞到視圖中
		model.addAttribute("laborInsurance", roundlaborInsurance);
		model.addAttribute("healthInsurance", roundhealthInsurance);

		model.addAttribute("payDate", formattedPayDate);
		model.addAttribute("totalAmount", roundedTotalAmount);
		model.addAttribute("totalDeduction", roundedTotalDeduction);
		model.addAttribute("netPay", roundedNetPay);

		model.addAttribute("emp", salRecordFinal);

		return "sal/GetEmp";
	}

//所有資料
	@GetMapping("/getAllEmpSal.controller")
	public String getAllEmpSal(@RequestParam(value = "status", required = false) String status, Model model) {
		try {
			List<SalRecordFinal> emps;

			// 根據 status 過濾數據
			if (status != null && !status.isEmpty()) {
				emps = salRecordFinalService.findByStatus(status);
			} else {
				emps = salRecordFinalService.getAllSals();
			}

			List<SalTotalDTO> empDtos = new ArrayList<>();

			for (SalRecordFinal salRecordFinal : emps) {
				// 計算薪資相關金額
				double sal = salRecordFinal.getSal().doubleValue();
				double foodAllowance = salRecordFinal.getFoodAllowance().doubleValue();
				double trafficAllowance = salRecordFinal.getTrafficAllowance().doubleValue();
				double mgrAllowance = salRecordFinal.getMgrAllowance().doubleValue();
				double holidayAllowance = salRecordFinal.getHolidayAllowance().doubleValue();
				double overtime = salRecordFinal.getOvertimePay().doubleValue();
				double attendanceBonus = salRecordFinal.getAttendanceBonus().doubleValue();

				double laborInsurance = salRecordFinal.getLaborInsurance().doubleValue();
				double healthInsurance = salRecordFinal.getHealthInsurance().doubleValue();
				double leavePay = salRecordFinal.getLeavePay().doubleValue();

				double totalDeduction = laborInsurance + healthInsurance + leavePay;
				double totalAmount = sal + foodAllowance + trafficAllowance + mgrAllowance + holidayAllowance + overtime
						+ attendanceBonus;
				double netPay = totalAmount - totalDeduction;

				// 將計算結果取整數
				int roundedTotalDeduction = (int) Math.round(totalDeduction);
				int roundedTotalAmount = (int) Math.round(totalAmount);
				int roundedNetPay = (int) Math.round(netPay);

				// 創建 DTO 並設置屬性
				SalTotalDTO dto = new SalTotalDTO();
				dto.setSalno(salRecordFinal.getSalno());
				dto.setEmpno(salRecordFinal.getEmpno());
				dto.setName(salRecordFinal.getName());
				dto.setYear(salRecordFinal.getYear());
				dto.setMonth(salRecordFinal.getMonth());
				dto.setSal(salRecordFinal.getSal());
				dto.setStatus(salRecordFinal.getStatus());
				dto.setFoodAllowance(salRecordFinal.getFoodAllowance());
				dto.setTrafficAllowance(salRecordFinal.getTrafficAllowance());
				dto.setMgrAllowance(salRecordFinal.getMgrAllowance());
				dto.setHolidayAllowance(salRecordFinal.getHolidayAllowance());
				dto.setOvertimePay(salRecordFinal.getOvertimePay());
				dto.setAttendanceBonus(salRecordFinal.getAttendanceBonus());
				dto.setLaborInsurance(salRecordFinal.getLaborInsurance());
				dto.setHealthInsurance(salRecordFinal.getHealthInsurance());
				dto.setLeavePay(salRecordFinal.getLeavePay());

				dto.setTotalAmount(roundedTotalAmount);
				dto.setTotalDeduction(roundedTotalDeduction);
				dto.setNetPay(roundedNetPay);

				// 將 DTO 添加到列表中
				empDtos.add(dto);
			}

			model.addAttribute("emps", empDtos);
		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("error", "操作失敗");
		}
		return "sal/back/GetAllEmp";
	}

	// 個人所有薪資資料
	@GetMapping("/getOneEmpSal.controller")
	public String getOneEmpSal(Model model, HttpServletRequest request) {
		try {
			HttpSession session = request.getSession(false);
			String empno = (String) session.getAttribute("account");


	        // 獲取與指定empno相關且status欄位內容為"已發送"的薪資資料
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
				double totalAmount = sal + foodAllowance + trafficAllowance + mgrAllowance + holidayAllowance
						+ overtimePay + attendanceBonus;
				double netPay = totalAmount - totalDeduction;

				// 將計算結果取整數
				int roundedTotalDeduction = (int) Math.round(totalDeduction);
				int roundedTotalAmount = (int) Math.round(totalAmount);
				int roundedNetPay = (int) Math.round(netPay);

				// 創建 DTO 並設置屬性
				SalTotalDTO dto = new SalTotalDTO();
				dto.setSalno(emp.getSalno());
				dto.setEmpno(emp.getEmpno());
				dto.setName(emp.getName());
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
		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("error", "操作失敗");
		}
		return "sal/GetEmpSal";
	}

	// 後台依欄位模糊查詢薪資資料

	@GetMapping("/backGetSelectSal")
	public String backGetSelectSal(@RequestParam("col") String col, @RequestParam("colvalue") String colvalue,
			Model model) {
		try {
			List<SalRecordFinal> emps = salRecordFinalService.getSelectSal(col, colvalue);
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
				double totalAmount = sal + foodAllowance + trafficAllowance + mgrAllowance + holidayAllowance
						+ overtimePay + attendanceBonus;
				double netPay = totalAmount - totalDeduction;

				// 將計算結果取整數
				int roundedTotalDeduction = (int) Math.round(totalDeduction);
				int roundedTotalAmount = (int) Math.round(totalAmount);
				int roundedNetPay = (int) Math.round(netPay);

				// 創建 DTO 並設置屬性
				SalTotalDTO dto = new SalTotalDTO();

				dto.setSalno(emp.getSalno());
				dto.setEmpno(emp.getEmpno());
				dto.setName(emp.getName());
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
				dto.setStatus(emp.getStatus());

				// 將 DTO 添加到列表中
				empDtos.add(dto);
			}

			model.addAttribute("emps", empDtos);
		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("error", "操作失敗");
		}
		return "sal/back/GetAllEmp";
	}
	// 前台依欄位模糊查詢薪資資料

	@GetMapping("/getSelectSal")
	public String getSelectSal(@RequestParam("col") String col, @RequestParam("colvalue") String colvalue, Model model,
			HttpServletRequest request) {
		try {
			HttpSession session = request.getSession(false);
			String empno = (String) session.getAttribute("account");
			// 獲取與指定 empno 相關的薪資資料
			List<SalRecordFinal> emps = salRecordFinalService.getSelectSal(col, colvalue).stream()
					.filter(emp -> empno.equals(emp.getEmpno())).collect(Collectors.toList());

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
				double totalAmount = sal + foodAllowance + trafficAllowance + mgrAllowance + holidayAllowance
						+ overtimePay + attendanceBonus;
				double netPay = totalAmount - totalDeduction;

				// 將計算結果取整數
				int roundedTotalDeduction = (int) Math.round(totalDeduction);
				int roundedTotalAmount = (int) Math.round(totalAmount);
				int roundedNetPay = (int) Math.round(netPay);

				// 創建 DTO 並設置屬性
				SalTotalDTO dto = new SalTotalDTO();

				dto.setSalno(emp.getSalno());
				dto.setEmpno(emp.getEmpno());
				dto.setName(emp.getName());
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
		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("error", "操作失敗");
		}
		return "sal/GetEmpSal";
	}

	// 刪除資料
	@DeleteMapping("/deleteEmpSal")
	public String deleteEmp(@RequestParam Integer salno) {

		salRecordFinalService.deleteSalById(salno);
		return "redirect:/getAllEmpSal.controller";
	}

//	模糊新增抓資料
	@GetMapping("/GetEmployeesByEmpnoAndNameFuzzySearchForSal")
	@ResponseBody
	public List<Employees> getEmployeesByEmpnoAndNameFuzzySearch(@RequestParam String query) {
		return salRecordFinalService.getEmployeesByEmpnoAndNameFuzzySearch(query);
	}

//		預存程序新增
	@PostMapping("/insertEmpSalProcedure")
	public String insertSalRecordFinalProcedure(@RequestParam String empno, @RequestParam String year,
			@RequestParam String month) {
		// 拆分 year 和 month
//		String[] parts = month.split("-");
//		String year = parts[0];
//		String monthPart = parts[1];

		// 調用服務層方法插入數據
		salRecordFinalService.insertSalRecordFinal(empno, year, month);

		return "redirect:/getAllEmpSal.controller";
	}

//	根據Ajax帶入資料新增
	@PostMapping("/insertEmpSal")
	public String insertSalRecordFinal(@ModelAttribute SalRecordFinal emp,
			@RequestParam(value = "action", required = false) String action,
	        @RequestParam String category, @RequestParam String notifyEmpno ,@RequestParam String empno1) {
		
		 // 打印接收到的數據
//	    System.out.println("Received Sal: " + emp.getSal());
//	    System.out.println("Received MgrAllowance: " + emp.getMgrAllowance());
		System.out.println("Category: " + category);
	    System.out.println("Notify Empno: " + notifyEmpno);
	    // 設置 empno 欄位為 empno1 的值
	    emp.setEmpno(empno1);

	    
	    //通知
	    notificationService.createNotificationBasisOnEmpno(category, notifyEmpno);
	    // 儲存 SalRecordFinal 實體
	    salRecordFinalService.insertEmpSal(emp);
		
		return "redirect:/getAllEmpSal.controller";
	}

//		批次新增資料
	@PostMapping("/insertEmpSalBatch")
	public String insertEmpSalBatch(@RequestParam("month") String monthValue) {
		// 分解 monthValue 為 year 和 month
		String[] monthParts = monthValue.split("-");
		String year = monthParts[0];
		String month = monthParts[1];

		// Save all SalRecordBean objects
		salRecordFinalService.insertEmpSalBatch(year, month);

		return "redirect:/getAllEmpSal.controller";
	}

//		抓取欲修改資料
	@GetMapping("/toUpdateEmpSal")
	public String toUpdateEmpSal(@RequestParam("salno") Integer salno, Model model) {
		SalRecordFinal salRecordFinal = salRecordFinalService.getUpdateData(salno);
		model.addAttribute("emp", salRecordFinal);
		return "sal/back/editEmpSal";
	}

//		修改資料
	@PostMapping("/updateEmpSal")
	public String updateEmpSal(@ModelAttribute SalRecordFinal emp) {
		salRecordFinalService.updateData(emp);
		return "redirect:/getAllEmpSal.controller";
	}
	
	
//	修改發送狀態
	 @PostMapping("/updateStatus")
	 public ResponseEntity<String> updateStatus(@RequestBody Map<String, String> payload) {
	        String salno = payload.get("salno");
	        String status = payload.get("status");
	        salRecordFinalService.updateStatus(Integer.parseInt(salno), status);
	        return ResponseEntity.ok("Status updated successfully");
	    }
	 
	 
//	 用checkbox批次修改發送狀態

	 @PostMapping("/batchPublish")
	    public ResponseEntity<?> batchPublish(@RequestBody Map<String, List<Integer>> request) {
	        try {
	            List<Integer> salnos = request.get("salnos");
	            if (salnos == null || salnos.isEmpty()) {
	                return ResponseEntity.badRequest().body("請提供要發送的資料編號");
	            }
	            salRecordFinalService.updateStatusToPublished(salnos);
	            return ResponseEntity.ok("批次發送成功");
	        } catch (Exception e) {
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("批次發送失敗");
	        }
	    }
	 
//	 下載所有薪資資料
	 @GetMapping("/downloadAllSalData")
		public ResponseEntity<InputStreamResource> downloadAllSalData() throws IOException, ParseException {
			List<SalRecordFinal> salRecordData  = salRecordFinalService.getAllSals();

			StringBuilder csvData = new StringBuilder();
			  csvData.append("salno,empno,year,month,sal,foodAllowance,trafficAllowance,mgrAllowance,holidayAllowance,totalOvertimeHours,overtimePay,attendanceBonus,laborInsurance,healthInsurance,halfpaidHours,fullpaidHours,unpaidHours\n");
			    for (SalRecordFinal record : salRecordData) {
			        csvData.append(record.getSalno()).append(",");
			        csvData.append(record.getEmpno()).append(",");
			        csvData.append(record.getYear()).append(",");
			        csvData.append(record.getMonth()).append(",");
			        csvData.append(record.getSal()).append(",");
			        csvData.append(record.getFoodAllowance()).append(",");
			        csvData.append(record.getTrafficAllowance()).append(",");
			        csvData.append(record.getMgrAllowance()).append(",");
			        csvData.append(record.getHolidayAllowance()).append(",");
			        csvData.append(record.getTotalOvertimeHours()).append(",");
			        csvData.append(record.getOvertimePay()).append(",");
			        csvData.append(record.getAttendanceBonus()).append(",");
			        csvData.append(record.getLaborInsurance()).append(",");
			        csvData.append(record.getHealthInsurance()).append(",");
			        csvData.append(record.getHalfpaidHours()).append(",");
			        csvData.append(record.getFullpaidHours()).append(",");
			        csvData.append(record.getUnpaidHours()).append("\n");
			    }

			ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(
					csvData.toString().getBytes(StandardCharsets.UTF_8));

			HttpHeaders headers = new HttpHeaders();
			headers.add("Content-Disposition", "attachment; filename=all_sal_data.csv");

			return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_OCTET_STREAM)
					.body(new InputStreamResource(byteArrayInputStream));
		}
	 
	 
	 
}
