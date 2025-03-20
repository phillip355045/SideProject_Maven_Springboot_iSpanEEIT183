package com.example.demo.controller;

import java.util.ArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.demo.dto.WorklogItemDTO;
import com.example.demo.dto.WorklogsDTO;
import com.example.demo.entity.Employees;
import com.example.demo.entity.Department;

import com.example.demo.entity.WorklogItem;
import com.example.demo.entity.Worklogs;
import com.example.demo.service.NotificationService;
import com.example.demo.service.WorklogsService;

import jakarta.servlet.http.HttpSession;

@Controller
public class WorklogsController {

	@Autowired
	private WorklogsService worklogsService;

	// User -> Director -> Manager -> Historical
	
	// 只讓現在登入的人看到自己的東西
	@GetMapping("/GetEmployeesByEmpno")
	@ResponseBody
	public List<Employees> getEmployeesByEmpno(HttpSession session, @RequestParam String query) {
		String empno = (String) session.getAttribute("account");

		if (empno == null) {
			// 處理未登入的情況
			return Collections.emptyList();
		}

		System.out.println(empno);
		List<Employees> result = worklogsService.getEmployeesByEmpnoAndName(query);

		// 移除當前帳號的選項
		result.removeIf(employee -> empno.equals(employee.getEmpno()));

		return result;
	}

	@GetMapping("/GetCurrentEmployeeInfo")
	@ResponseBody
	public Employees getCurrentEmployeeInfo(HttpSession session) {
		String empno = (String) session.getAttribute("account");

		if (empno == null) {
			// 處理未登入的情況
			return null; 
		}

		Employees employee = worklogsService.getCurrentEmployee(empno);
		return employee;
	}

	// 前台
	// 使用者 全部日誌
	@GetMapping("/UserGetAllWorklogs")
	public String UserGetAllWorklogs(HttpSession session, Model model,
			@RequestParam(value = "page", defaultValue = "1") int page,
			@RequestParam(value = "size", defaultValue = "5") int size) {
		String empno = (String) session.getAttribute("account");

		Page<Worklogs> worklogsPage = worklogsService.getAllWorklogsByEmpno(empno, page, size);
		model.addAttribute("worklogsPage", worklogsPage);
		model.addAttribute("size", size);
		return "worklogs/UserGetAllWorklogs";
	}

	// 使用者 模糊查詢
	@GetMapping("/WorklogsSearch1")
	public String searchWorklogs1(HttpSession session, @RequestParam("keyword") String keyword,
			@RequestParam(value = "page", defaultValue = "1") int page,
			@RequestParam(value = "size", defaultValue = "5") int size, Model model) {
		String empno = (String) session.getAttribute("account");

		Page<Worklogs> worklogsPage = worklogsService.UsersearchWorklogs(empno, keyword, page, size);

		model.addAttribute("worklogsPage", worklogsPage);
		model.addAttribute("currentPage", page);
		model.addAttribute("pageSize", size);
		model.addAttribute("totalPages", worklogsPage.getTotalPages());
		model.addAttribute("keyword", keyword);
		return "worklogs/UserGetAllWorklogs";
	}

	// 使用者查詢單筆工作日誌 根據工作日誌編號查詢工作日誌詳細資訊
	@GetMapping("/UserGetWorklog")
	public String getWorklogById1(@RequestParam("worklogid") Long worklogid, Model model) {

		Worklogs worklogs = worklogsService.getWorklogWithItems(worklogid);
		model.addAttribute("worklog", worklogs);
		return "worklogs/UserGetOneWorklogPage";
	}

	// 使用者新增
	@GetMapping("/UserInsertWorklog")
	public String insertPage1(HttpSession session, Model model) {
		String empno = (String) session.getAttribute("account");
		Employees employee = worklogsService.getCurrentEmployee(empno);
		Department department = worklogsService.getCurrentEmployeeByDeptno(employee.getDeptno());
		Worklogs worklogs = new Worklogs();
		worklogs.setEmployees(employee);
		worklogs.setDepartment(department);

		model.addAttribute("worklogs", worklogs);
		return "worklogs/UserInsertWorklog";
	}

	@PostMapping("/UserInsertWorklog1")
	public String insertWorklog1(HttpSession session,
			@ModelAttribute("worklogsDto") @DateTimeFormat(pattern = "yyyy-MM-dd") Worklogs worklogs, Model model,
			@RequestParam String category, @RequestParam String notifyEmpno) {
		 String empno = (String) session.getAttribute("account");
		    if (empno == null) {
		        model.addAttribute("error", "No employee found in session.");
		        return "errorPage";
		    }
		
		Employees employee = worklogsService.getCurrentEmployee(empno);
	    Department department = worklogsService.getCurrentEmployeeByDeptno(employee.getDeptno());
	    
	    worklogs.setEmployees(employee);
	    worklogs.setDepartment(department);
	    
	    worklogs.setWorklogdate(worklogs.getWorklogdate());
		// reviewstatus如果 worklogsDto 中没有值，則待審核
		if (worklogs.getReviewstatus() == null || worklogs.getReviewstatus().isEmpty()) {
			worklogs.setReviewstatus("待審核");
		} else {
			worklogs.setReviewstatus(worklogs.getReviewstatus());
		}

		if (worklogs.getWorklogItems() != null) {
			List<WorklogItem> worklogItems = new ArrayList<>();
			for (WorklogItem item : worklogs.getWorklogItems()) {
				item.setDeptname(item.getDeptname());
				item.setJobType(item.getJobType());
				item.setJobDescription(item.getJobDescription());
				item.setRegularHours((double) item.getRegularHours());
				item.setOvertimeHours((double) item.getOvertimeHours());
				item.setWorklogs(worklogs); // 設定關聯
				worklogItems.add(item);
			}
			worklogs.setWorklogItems(worklogItems);
		}

		worklogsService.insertWorklog(worklogs);

		model.addAttribute("message", "工作日誌新增成功");
		model.addAttribute("worklogs", worklogs);
		notificationService.createNotificationBasisOnEmpno(category, notifyEmpno);
		return "worklogs/UserInsertWorklogPage";
	}

	// 一鍵生成假資料
	@PostMapping("/generateFakeData")
	public String generateFakeData(Model model) {
		worklogsService.generateFakeData();
		return "redirect:/WorklogsHomePage";
	}

	@Autowired
	private NotificationService notificationService;

//	// 使用者 修改
//	@GetMapping("/UserUpdateWorklog")
//	public String getUpdateWorklog1(@RequestParam("worklogid") Long worklogid, Model model) {
//		Worklogs worklogs = worklogsService.getWorklogWithItems(worklogid);
//		model.addAttribute("worklog", worklogs);
//		return "worklogs/UserUpdateWorklog";
//	}
//
//	// 使用者 修改 Worklog 和 WorklogItem
//	@PostMapping("/UserUpdateWorklog1")
//	public String updateWorklog1(@RequestParam("worklogid") Long worklogid,
//			@ModelAttribute("worklog") Worklogs updatedWorklog, Model model) {
//		Worklogs updated = worklogsService.updateWorklog(worklogid, updatedWorklog);
//		model.addAttribute("worklog", updated);
//		return "worklogs/UserUpdateWorklogPage";
//	}

	// 使用者 修改 修改過後發送通知給審核人員的
	@GetMapping("/UserUpdateWorklog")
	public String getUpdateWorklog1(@RequestParam("worklogid") Long worklogid, Model model) {
		Worklogs worklogs = worklogsService.getWorklogWithItems(worklogid);
		if (worklogs.getDepartment() == null) {
	        worklogs.setDepartment(new Department());
	    }
		model.addAttribute("worklog", worklogs);
		return "worklogs/UserUpdateWorklog";
	}

	// 使用者 修改 Worklog 和 WorklogItem
	@PostMapping("/UserUpdateWorklog1")
	public String updateWorklog1(@RequestParam("worklogid") Long worklogid,
			@ModelAttribute("worklog") Worklogs updatedWorklog, Model model, @RequestParam String category,
			@RequestParam String notifyEmpno) {

		Worklogs updated = worklogsService.updateWorklog(worklogid, updatedWorklog);
		model.addAttribute("worklog", updated);

		notificationService.createNotificationBasisOnEmpno(category, notifyEmpno);
		return "worklogs/UserUpdateWorklogPage";
	}

	// =======================後台=======================
	// =======================主管 審核=======================

	// 主管 全部未審核使用分頁
	@GetMapping("/ReviewAllWorklogs")
	public String getReviewAllWorklogs(Model model, @RequestParam(name = "page", defaultValue = "1") int page,
			@RequestParam(name = "size", defaultValue = "5") int size) {
		Page<Worklogs> worklogsPage = worklogsService.ReviewAllWorklogsPage(page, size);
		model.addAttribute("worklogsPage", worklogsPage);
		return "worklogs/back/ReviewAllWorklogs";
	}

	// 主管審核 模糊查詢
	@GetMapping("/WorklogsSearch3")
	public String searchWorklogs3(@RequestParam("keyword") String keyword,
			@RequestParam(value = "page", defaultValue = "1") int page,
			@RequestParam(value = "size", defaultValue = "5") int size, Model model) {

		Page<Worklogs> worklogsPage = worklogsService.ReviewsearchWorklogs(keyword, page, size);

		model.addAttribute("worklogsPage", worklogsPage);
		model.addAttribute("currentPage", page);
		model.addAttribute("pageSize", size);
		model.addAttribute("totalPages", worklogsPage.getTotalPages());
		model.addAttribute("keyword", keyword);
		return "worklogs/back/ReviewAllWorklogs";
	}

	// 主管審核功能 更改審核狀態
	@GetMapping("/ReviewUpdateWorklog")
	public String getUpdateWorklog2(@RequestParam("worklogid") Long worklogid, Model model) {
		Worklogs worklogs = worklogsService.getWorklogWithItems1(worklogid);
		model.addAttribute("worklog", worklogs);
		return "worklogs/back/ReviewUpdateWorklog";
	}

	// 審核 主管可以修改 Worklog 和 WorklogItem
	@PostMapping("/ReviewUpdateWorklog1")
	public String updateReviewStatus(@RequestParam("worklogid") Long worklogid,
			@ModelAttribute("worklog") Worklogs worklogs, @RequestParam("action") String action, Model model) {

		String reviewStatusToUpdate = "";
		if ("approve".equals(action)) {
			reviewStatusToUpdate = "已核准";
		} else if ("reject".equals(action)) {
			reviewStatusToUpdate = "填寫中";
		}

		worklogsService.updateReviewStatus(worklogid, worklogs, reviewStatusToUpdate);

		// 根據 reviewstatus 的值進行重定向
		return "redirect:/ReviewAllWorklogs";
	}

	// 刪除
	@GetMapping("/delete2")
	public String deleteWorklog2(@RequestParam("worklogid") Long worklogid,
			@RequestParam(value = "page", defaultValue = "1") int page,
			@RequestParam(value = "size", defaultValue = "5") int size, Model model) {
		
	    worklogsService.softDeleteWorklog(worklogid);
	    
		Page<Worklogs> worklogsPage = worklogsService.getAllWorklogsPage(page, size);
		if (worklogsPage.getContent().isEmpty() && page > 1) {
			page = page - 1;
			worklogsPage = worklogsService.getAllWorklogsPage(page, size);
		}
		model.addAttribute("worklogsPage", worklogsPage);
		model.addAttribute("currentPage", page);
		model.addAttribute("pageSize", size);
		return "redirect:/ReviewAllWorklogs";
	}

	
	
	// =======================管理者=======================

	// 管理者 查詢全部使用分頁
	@GetMapping("/GetAllWorklogs")
	public String getAllWorklogs(Model model, @RequestParam(value = "page", defaultValue = "1") int page,
			@RequestParam(value = "size", defaultValue = "5") int size) {
		Page<Worklogs> worklogsPage = worklogsService.getAllWorklogsPage(page, size);
		model.addAttribute("worklogsPage", worklogsPage);
		model.addAttribute("size", size);
		return "worklogs/back/GetAllWorklogs";
	}

	// 管理者 模糊查詢
	@GetMapping("/WorklogsSearch")
	public String searchWorklogs(@RequestParam("keyword") String keyword,
			@RequestParam(value = "page", defaultValue = "1") int page,
			@RequestParam(value = "size", defaultValue = "5") int size, Model model) {

		Page<Worklogs> worklogsPage = worklogsService.searchWorklogs(keyword, page, size);

		model.addAttribute("worklogsPage", worklogsPage);
		model.addAttribute("currentPage", page);
		model.addAttribute("pageSize", size);
		model.addAttribute("totalPages", worklogsPage.getTotalPages());
		model.addAttribute("keyword", keyword);
		return "worklogs/back/GetAllWorklogs";
	}

	// 查詢單筆工作日誌
	// 根據工作日誌編號查詢工作日誌詳細資訊
	@GetMapping("/GetWorklog")
	public String getWorklogById(@RequestParam("worklogid") Long worklogid, Model model) {
		Worklogs worklogs = worklogsService.getWorklogWithItems(worklogid);
		model.addAttribute("worklog", worklogs);
		return "worklogs/back/GetOneWorklogPage";
	}

	// 新增
	@GetMapping("/InsertWorklog")
	public String insertPage(HttpSession session, Model model) {
		String empno = (String) session.getAttribute("account");
		Employees employee = worklogsService.getCurrentEmployee(empno);
		Department department = worklogsService.getCurrentEmployeeByDeptno(employee.getDeptno());
		Worklogs worklogs = new Worklogs();
		worklogs.setEmployees(employee);
		worklogs.setDepartment(department);
		
		model.addAttribute("worklogs", worklogs);
		return "worklogs/back/InsertWorklog";
	}

	@PostMapping("/InsertWorklog1")
	public String insertWorklog(HttpSession session,
			@ModelAttribute("worklogsDto") @DateTimeFormat(pattern = "yyyy-MM-dd") Worklogs worklogs,
			Model model) {
		String empno = (String) session.getAttribute("account");
	    if (empno == null) {
	        model.addAttribute("error", "No employee found in session.");
	        return "errorPage";
	    }
	
	Employees employee = worklogsService.getCurrentEmployee(empno);
    Department department = worklogsService.getCurrentEmployeeByDeptno(employee.getDeptno());
    
    worklogs.setEmployees(employee);
    worklogs.setDepartment(department);
    
    worklogs.setWorklogdate(worklogs.getWorklogdate());

		// reviewstatus如果 worklogsDto 中没有值，則待審核
    if (worklogs.getReviewstatus() == null || worklogs.getReviewstatus().isEmpty()) {
		worklogs.setReviewstatus("待審核");
	} else {
		worklogs.setReviewstatus(worklogs.getReviewstatus());
	}

	if (worklogs.getWorklogItems() != null) {
		List<WorklogItem> worklogItems = new ArrayList<>();
		for (WorklogItem item : worklogs.getWorklogItems()) {
			item.setDeptname(item.getDeptname());
			item.setJobType(item.getJobType());
			item.setJobDescription(item.getJobDescription());
			item.setRegularHours((double) item.getRegularHours());
			item.setOvertimeHours((double) item.getOvertimeHours());
			item.setWorklogs(worklogs); // 設定關聯
			worklogItems.add(item);
		}
		worklogs.setWorklogItems(worklogItems);
		}

		worklogsService.insertWorklog(worklogs);

		model.addAttribute("message", "工作日誌新增成功");
		model.addAttribute("worklogs", worklogs);
		return "worklogs/back/InsertWorklogPage";
	}

	// 修改
	@GetMapping("/UpdateWorklog")
	public String getUpdateWorklog(@RequestParam("worklogid") Long worklogid, Model model) {
		Worklogs worklogs = worklogsService.getWorklogWithItems(worklogid);
		model.addAttribute("worklog", worklogs);
		return "worklogs/back/UpdateWorklog";
	}

	// 修改 Worklog 和 WorklogItem
	@PostMapping("/UpdateWorklog1")
	public String updateWorklog(@RequestParam("worklogid") Long worklogid,
			@ModelAttribute("worklog") Worklogs updatedWorklog, Model model) {
		Worklogs updated = worklogsService.updateWorklog(worklogid, updatedWorklog);
		model.addAttribute("worklog", updated);
		return "worklogs/back/UpdateWorklogPage";
	}

	// 刪除 管理者
	@GetMapping("/delete")
	public String deleteWorklog(@RequestParam("worklogid") Long worklogid,
			@RequestParam(value = "page", defaultValue = "1") int page,
			@RequestParam(value = "size", defaultValue = "5") int size, Model model) {
		worklogsService.softDeleteWorklog(worklogid);
		Page<Worklogs> worklogsPage = worklogsService.getAllWorklogsPage(page, size);
		if (worklogsPage.getContent().isEmpty() && page > 1) {
			page = page - 1;
			worklogsPage = worklogsService.getAllWorklogsPage(page, size);
		}
		model.addAttribute("worklogsPage", worklogsPage);
		model.addAttribute("currentPage", page);
		model.addAttribute("pageSize", size);
		return "redirect:/GetAllWorklogs";
	}

	// =======================歷史紀錄=======================

	// 後台歷史紀錄!!!包含已刪除的資料
	@GetMapping("/HistoricalAllWorklogs")
	public String AllWorklogs(Model model, @RequestParam(value = "page", defaultValue = "1") int page,
			@RequestParam(value = "size", defaultValue = "5") int size) {
		Page<Worklogs> worklogsPage = worklogsService.getAllWorklogsPage1(page, size);
		model.addAttribute("worklogsPage", worklogsPage);
		model.addAttribute("size", size);
		return "worklogs/back/HistoricalAllWorklogs";
	}

	// 歷史資料模糊查詢
	@GetMapping("/WorklogsSearch2")
	public String searchWorklogs2(@RequestParam("keyword") String keyword,
			@RequestParam(value = "page", defaultValue = "1") int page,
			@RequestParam(value = "size", defaultValue = "5") int size, Model model) {

		Page<Worklogs> worklogsPage = worklogsService.searchAllWorklogs(keyword, page, size);

		model.addAttribute("worklogsPage", worklogsPage);
		model.addAttribute("currentPage", page);
		model.addAttribute("pageSize", size);
		model.addAttribute("totalPages", worklogsPage.getTotalPages());
		model.addAttribute("keyword", keyword);
		return "worklogs/back/HistoricalAllWorklogs";
	}

	// 歷史紀錄查詢單筆工作日誌
	@GetMapping("/HistoricalGetWorklog")
	public String getWorklogById2(@RequestParam("worklogid") Long worklogid, Model model) {
		Worklogs worklogs = worklogsService.getWorklogWithItems(worklogid);
		model.addAttribute("worklog", worklogs);
		return "worklogs/back/HistoricalGetWorklog";
	}

	// 歷史紀錄修改 先查詢到日誌 再到修改畫面 Post值進去
	@GetMapping("/HistoricalUpdateWorklog")
	public String getUpdateWorklog3(@RequestParam("worklogid") Long worklogid, Model model) {
		Worklogs worklogs = worklogsService.getWorklogWithItems(worklogid);
		model.addAttribute("worklog", worklogs);
		return "worklogs/back/HistoricalUpdateWorklog";
	}

	// 修改 Worklog 和 WorklogItem
	@PostMapping("/HistoricalUpdateWorklog1")
	public String updateWorklog1(@RequestParam("worklogid") Long worklogid,
			@ModelAttribute("worklog") Worklogs updatedWorklog, Model model) {
		Worklogs updated = worklogsService.updateWorklog(worklogid, updatedWorklog);
		model.addAttribute("worklog", updated);
		return "worklogs/back/HistoricalUpdateWorklogPage";
	}

}
