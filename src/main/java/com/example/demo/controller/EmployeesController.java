package com.example.demo.controller;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.entity.EmpFamilyMember;
import com.example.demo.entity.Employees;
import com.example.demo.service.CheckInService;
import com.example.demo.service.DepartmentService;
import com.example.demo.service.EmpFamilyMemberService;
import com.example.demo.service.EmployeesService;
import com.example.demo.service.NotificationEmployeesService;
import com.example.demo.service.NotificationService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
public class EmployeesController {
	@Autowired
	private EmployeesService employeesService;

	@Autowired
	private EmpFamilyMemberService familyMemberService;

	@GetMapping("/HomePage")
	public String processMainAction1() {
		return "employees/logInHomePage";
	}

	// 登入
	@PostMapping("/checklogin")
	public String processAction(@RequestParam("empno") String empno, @RequestParam("password") String password,
			Model model, HttpSession session) {

		Employees employee = employeesService.checkLogin(empno, password);

		if (employee != null) {
			String name = employeesService.findUsersByEmpno(empno).getName();
			String job = employeesService.findUsersByEmpno(empno).getJob();
			String mgr = employeesService.findUsersByEmpno(empno).getMgr();
			String empnoStatus = employeesService.findUsersByEmpno(empno).getEmpnoStatus();
			if ("Resigned".equals(empnoStatus)) {
				model.addAttribute("errorMsg", "您已離職，無法登入!!");
				return "index.html";
			}
			session.setAttribute("account", empno);
			session.setAttribute("name", name);
			session.setAttribute("mgr", mgr);
			session.setAttribute("job", job);

			if ("A0001".equals(empno)) {
				return "redirect:/BackHomePage";
			}
			return "redirect:/logInHomePage";

		} else {
			model.addAttribute("errorMsg", "帳號/密碼有誤，請重新輸入");
			return "index.html";
		}
	}

	// 獲得員工全部資料
	@GetMapping("/getAllEmployees")
	public String getAllEmployees(Model model) {
		List<Employees> employeesList = employeesService.getAllEmployees();
		model.addAttribute("employees", employeesList);
		return "employees/back/allEmployees";
	}

	// 獲得員工全部資料by分頁
	@GetMapping("/getAllEmployees2")
	public String getAllEmployees2(Model model, @RequestParam(value = "page", defaultValue = "1") int page,
			@RequestParam(value = "size", defaultValue = "7") int size) {
		Page<Employees> employeesPage = employeesService.getEmployeesPage(page, size);
		model.addAttribute("employeesPage", employeesPage);
		model.addAttribute("size", size);
		return "employees/back/allEmployees2";
	}

	// 獲取單一員工資料
	@GetMapping("/getOneEmployees")
	public String getOneEmployees(@RequestParam("empno") String empno, Model model) {
		Employees employees = employeesService.findUsersByEmpno(empno);
		String mgrempno = employees.getMgr();
		String mgrName = null;
		if (!mgrempno.isEmpty()) {
			mgrName = employeesService.findUsersByEmpno(mgrempno).getName();
		}
		String deptno = employees.getDeptno();
		String deptname = departmentService.findDeptname(deptno);

		List<EmpFamilyMember> familyMember = familyMemberService.findByempno(empno);
		model.addAttribute("familyMember", familyMember);
		model.addAttribute("emp", employees);
		model.addAttribute("mgrName", mgrName);
		model.addAttribute("deptname", deptname);
		return "employees/back/getOneEmployees";
	}

	@Autowired
	private DepartmentService departmentService;

	// 獲取單一員工資料
	@GetMapping("/getEmpByAccount")
	public String getEmpByAccount(Model model, HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		String empno = (String) session.getAttribute("account");
		Employees employees = employeesService.findUsersByEmpno(empno);
		String mgrempno = employees.getMgr();
		String mgrName = null;
		if (!mgrempno.isEmpty()) {
			mgrName = employeesService.findUsersByEmpno(mgrempno).getName();
		}
		String deptno = employees.getDeptno();
		String deptname = departmentService.findDeptname(deptno);

		List<EmpFamilyMember> familyMember = familyMemberService.findByempno(empno);
		model.addAttribute("familyMember", familyMember);
		model.addAttribute("emp", employees);
		model.addAttribute("mgrName", mgrName);
		model.addAttribute("deptname", deptname);
		return "employees/getAccountEmp";
	}

	@Autowired
	private NotificationEmployeesService notificationEmployeesService;

	@Autowired
	private CheckInService checkInService;

	@Autowired
	private EmpFamilyMemberService empFamilyMemberService;

	// 刪除
	@DeleteMapping("/deleteEmployee")
	public String deleteEmp(@RequestParam String empno) {
		notificationEmployeesService.deletebyempno(empno);
		employeesService.deleteMsgById(empno);
		empFamilyMemberService.deleteByEmpno(empno);
		Date date = new Date();
		checkInService.deleteByempno(empno, date);
		return "redirect:/getAllEmployees";
	}

	// 抓取員工資料
	@GetMapping("/findUpdateEmpData")
	public String findUpdateEmpData(@RequestParam String empno, Model model) {
		Employees employees = employeesService.findUsersByEmpno(empno);
		String mgrempno = employees.getMgr();
		String mgrName = null;
		if (!mgrempno.isEmpty()) {
			mgrName = employeesService.findUsersByEmpno(mgrempno).getName();
		}
		String deptno = employees.getDeptno();
		String deptname = departmentService.findDeptname(deptno);

		List<EmpFamilyMember> familyMember = familyMemberService.findByempno(empno);
		model.addAttribute("familyMember", familyMember);
		model.addAttribute("emp", employees);
		model.addAttribute("mgrName", mgrName);
		model.addAttribute("deptname", deptname);
		System.out.println(employees.getJob());
		return "employees/back/editEmpData1";
	}

	// 新增
	@PutMapping("/insertEmpData")
	public String insertEmpData(@ModelAttribute Employees emp, @RequestParam("file") MultipartFile file) {
		String deptno = emp.getDeptno();
		Employees mgr = employeesService.findManagerByDeptno(deptno);

		String mgrempno = mgr.getEmpno();
		emp.setMgr(mgrempno);
		try {
			if (!file.isEmpty()) {
				byte[] photoBytes = file.getBytes();
				emp.setPhoto(photoBytes);
			}
			employeesService.saveEmployees(emp);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "redirect:/getAllEmployees";
	}

	// 更新員工資料，看要不要更新到新的畫面
	@PutMapping("/updateEmpData")
	public String editEmpData(@ModelAttribute Employees emp, @RequestParam("file") MultipartFile file,
			@RequestParam String category, @RequestParam String notifyEmpno) {
		System.out.println(emp.getPhoto());
		try {
			if (!file.isEmpty()) {
				byte[] photoBytes = file.getBytes();
				emp.setPhoto(photoBytes);
			} else {
				Employees existingEmp = employeesService.findUsersByEmpno(emp.getEmpno());
				emp.setPhoto(existingEmp.getPhoto());
			}
			employeesService.saveEmployees(emp);
			notificationService.createNotificationBasisOnEmpno(category, notifyEmpno);

		} catch (IOException e) {
			e.printStackTrace();
		}
		return "redirect:/getAllEmployees";
	}

	// 新增
	@GetMapping("/insertEmp")
	public String insertEmp(Model model) {
		String lastEmpId = employeesService.getLastEmployeeId();
		String prefix = lastEmpId.substring(0, 1); // 取得字母
		String numberStr = lastEmpId.substring(1); // 取得數字
		int number = Integer.parseInt(numberStr) + 1;
		String nextNumberStr = String.format("%04d", number); // 補0
		String nextEmpno = prefix + nextNumberStr;

		LocalDate currentDate = LocalDate.now();
		String formattedDate = currentDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		model.addAttribute("today", formattedDate);
		model.addAttribute("nextEmpno", nextEmpno);
		return "employees/back/insertEmp";
	}

	@GetMapping("/emphomepage")
	public String emphomepage() {
		return "employees/emphomepage";
	}

	@GetMapping("/getEmpsByCol")
	public String getEmpsByCol() {
		return "employees/back/getEmpByCol";
	}

	// 依照欄位查詢
	@PostMapping("/getEmployeesByCol")
	public String getEmployeesByCol(@RequestParam String col, @RequestParam String colvalue, Model model) {
		List<Employees> employeesList = employeesService.getEmployeesByCol(col, colvalue);
		model.addAttribute("employees", employeesList);
		return "employees/back/findEmpByCol";
	}

	@GetMapping("/logout")
	public String logout(HttpServletRequest request) {
		HttpSession session = request.getSession(false);

		if (session != null) {
			String empno = (String) session.getAttribute("account");

			if (empno != null) {
				Date lastTimeLogin = new Date();
				employeesService.logoutAndUpdate(empno, lastTimeLogin);
				session.invalidate();
				return "redirect:/";
			}
		}
		return "redirect:/";
	}

	@GetMapping("/photos/download")
	public ResponseEntity<byte[]> downloadPhotos(@RequestParam String empno) {
		byte[] photoFile = employeesService.findUsersByEmpno(empno).getPhoto();

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.IMAGE_JPEG);

		return new ResponseEntity<byte[]>(photoFile, headers, HttpStatus.OK);
	}

//	// 員工修改個人資料，原本的
//	@PutMapping("/updateAccountEmpData")
//	public String updateAccountEmpData(@ModelAttribute Employees emp, @RequestParam("file") MultipartFile file) {
//	    try {
//	        if (!file.isEmpty()) {
//	            byte[] photoBytes = file.getBytes();
//	            emp.setPhoto(photoBytes);
//	        } else {
//	            Employees existingEmp = employeesService.findUsersByEmpno(emp.getEmpno());
//	            emp.setPhoto(existingEmp.getPhoto());
//	        }
//	        employeesService.saveEmployees(emp);
//	    } catch (IOException e) {
//	        e.printStackTrace();
//	    }
//	    return "redirect:/getEmpByAccount";
//	}	

	@Autowired
	private NotificationService notificationService;

	// 員工修改個人資料
	@PutMapping("/updateAccountEmpData")
	public String updateAccountEmpData(@ModelAttribute Employees emp, @RequestParam("file") MultipartFile file,
			@RequestParam String category, @RequestParam String notifyEmpno, @RequestParam String category1) {
		try {
			if (!file.isEmpty()) {
				byte[] photoBytes = file.getBytes();
				emp.setPhoto(photoBytes);
			} else {
				Employees existingEmp = employeesService.findUsersByEmpno(emp.getEmpno());
				emp.setPhoto(existingEmp.getPhoto());
			}
			employeesService.saveEmployees(emp);

			notificationService.createNotificationBasisOnEmpno(category, notifyEmpno);
			notificationService.createNotificationBasisOnEmpno(category1, "A0001", notifyEmpno);

		} catch (IOException e) {
			e.printStackTrace();
		}
		return "redirect:/getEmpByAccount";
	}

	// 忘記密碼驗證
	@PostMapping("/forgotPassword")
	public ResponseEntity<Map<String, String>> forgotPassword(@RequestParam String forgetempno,
			@RequestParam String phone, @RequestParam String identityID, @RequestParam String newpassword) {

		Employees usersByEmpno = employeesService.forgotpassword(forgetempno, phone, identityID);

		Map<String, String> info = new HashMap<>();

		if (usersByEmpno == null) {
			info.put("errormsg", "驗證錯誤!!");
			return ResponseEntity.ok(info);
		}
		employeesService.updatepassword(forgetempno, newpassword);
		info.put("correctmsg", "密碼重置成功!!");
		return ResponseEntity.ok(info);
	}

	// 資料
	@PostMapping("/findEmpnoinfo")
	public ResponseEntity<Map<String, String>> findEmpnoinfo(@RequestBody String empno) {
		String phone = employeesService.findUsersByEmpno(empno).getPhone();
		String email = employeesService.findUsersByEmpno(empno).getMail();
		String name = employeesService.findUsersByEmpno(empno).getName();

		Map<String, String> empinfo = new HashMap<>();
		empinfo.put("phone", phone);
		empinfo.put("email", email);
		empinfo.put("name", name);

		return ResponseEntity.ok(empinfo);
	}

	@PostMapping("/findEmpnoAndPassword")
	public ResponseEntity<Map<String, String>> findEmpnoAndPassword(@RequestBody String empno) {
	    String password = employeesService.findUsersByEmpno(empno).getPassword();

	    Map<String, String> empInfo = new HashMap<>();
	    empInfo.put("password", password);

	    return ResponseEntity.ok(empInfo);
	}
	
	
}
