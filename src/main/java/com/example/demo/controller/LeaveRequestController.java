package com.example.demo.controller;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.example.demo.dto.LeaveRequestDTO;
import com.example.demo.entity.Approval;
import com.example.demo.entity.Employees;
import com.example.demo.entity.LeaveRequest;
import com.example.demo.entity.LeaveType;
import com.example.demo.entity.Notification;
import com.example.demo.service.EmployeesService;
import com.example.demo.service.LeaveRequestService;
import com.example.demo.service.LeaveTypeService;
import com.example.demo.service.NotificationService;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Controller
public class LeaveRequestController {
	
	@Autowired
	private EmployeesService employeesService;
	
	@Autowired
	private LeaveRequestService leaveRequestService;
	
	@Autowired
	private LeaveTypeService leaveTypeService;
	
	@Autowired
	private NotificationService notificationService;
	
	//進入請假首頁
//	@GetMapping("/leaveRequestHomePage")
//	public String leaveRequestHomePage() {
//		return "leaveRequest/leaveRequestHomePage";
//	}
	
	//查看所有資料s
//	@GetMapping("/GetAllLeaveRequest")
//	public String getAllLeaveRequest(Model m) {
//		List<LeaveRequest> allLeaveRequest = leaveRequestService.getAllLeaveRequest();
//		m.addAttribute("allLeaveRequest",allLeaveRequest);ㄋ
//		return "leaveRequest/GetAllLeaveRequest";
//	}
	
	//利用分頁來查看資料
//	@GetMapping("/GetAllLeaveRequest")
//	public String getAllLeaveRequest(Model model, HttpSession session,
//	                              @RequestParam(value = "page", defaultValue = "1") int page,
//	                              @RequestParam(value = "size", defaultValue = "10") int size) {
//	    Page<LeaveRequest> leaveRequestPage = leaveRequestService.getLeaveRequestPage(page, size);
//	    model.addAttribute("leaveRequestPage", leaveRequestPage);
//	    model.addAttribute("currentPage", page);
//	    model.addAttribute("pageSize", size);
//	    model.addAttribute("totalPages", leaveRequestPage.getTotalPages());
//
//	    if (session.getAttribute("message") != null) {
//	        model.addAttribute("message", session.getAttribute("message"));
//	        model.addAttribute("messageType", session.getAttribute("messageType"));
//	        session.removeAttribute("message");
//	        session.removeAttribute("messageType");
//	    }
//
//	    return "leaveRequest/GetAllLeaveRequest";
//	}
	
//	//呈現新增頁面
//	@GetMapping("/InsertLeaveRequest")
//	public String insertLeaveRequest() {
//		return "leaveRequest/GetInsertLveReq";
//	}

	//查詢特定資料
//	@GetMapping("/GetSpecificLeaveRequest")
//	public String getSpecificLeaveRequest(
//	      @RequestParam("searchField") String searchField,
//	      @RequestParam("searchValue") String searchValue,
//	     Model m) {
//			
//	        List<LeaveRequest> allLeaveRequest = leaveRequestService.getSpecificLeaveRequest(searchField, searchValue);
//	        m.addAttribute("allLeaveRequest", allLeaveRequest);
//
//	        return "leaveRequest/GetAllLeaveRequest";
//	    }
	
	//查看所有的資料
//	@GetMapping("/GetAllLeaveRequest.front")
//	public String getAllLeaveRequestFront(Model model, HttpSession session,
//	                                      @RequestParam(value = "page", defaultValue = "1") int page,
//	                                      @RequestParam(value = "size", defaultValue = "10") int size,
//	                                      @RequestParam(value = "keyword", required = false) String keyword) {
//	    String empno = (String) session.getAttribute("account");
//	    String job = (String) session.getAttribute("job");
//
//	    Page<LeaveRequest> leaveRequestPage;
//
//	    if (keyword != null && !keyword.isEmpty()) {
//	        leaveRequestPage = leaveRequestService.searchLeaveRequests(keyword, page, size);
//	    } else {
//	        if ("boss".equals(job)) {
//	            leaveRequestPage = leaveRequestService.getLeaveRequestPage(page, size);
//	        } else {
//	            leaveRequestPage = leaveRequestService.getLeaveRequestsByEmpno(page, size, empno);
//	        }
//	    }
//
//	    model.addAttribute("leaveRequestPage", leaveRequestPage);
//	    model.addAttribute("currentPage", page);
//	    model.addAttribute("pageSize", size);
//	    model.addAttribute("totalPages", leaveRequestPage.getTotalPages());
//	    model.addAttribute("keyword", keyword);
//	    model.addAttribute("job", job);
//	    model.addAttribute("empno", empno);
//
//	    if (session.getAttribute("message") != null) {
//	        model.addAttribute("message", session.getAttribute("message"));
//	        model.addAttribute("messageType", session.getAttribute("messageType"));
//	        session.removeAttribute("message");
//	        session.removeAttribute("messageType");
//	    }
//
//	    if ("boss".equals(job)) {
//	        return "leaveRequest/back/GetAllLeaveRequest"; // 管理者畫面
//	    } else {
//	        return "leaveRequest/GetAllLeaveRequestUser"; // 前台使用者畫面
//	    }
//	}
	
//	//模糊查詢
//		@GetMapping("/LeaveRequestSearch")
//		public void searchLeaveRequests(@RequestParam("keyword") String keyword,
//		                                @RequestParam(value = "page", defaultValue = "1") int page,
//		                                @RequestParam(value = "size", defaultValue = "10") int size,
//		                                HttpServletResponse response,
//		                                HttpSession session) throws IOException {
//		    response.setCharacterEncoding("UTF-8");
//		    Page<LeaveRequest> searchResults = leaveRequestService.searchLeaveRequests(keyword, page, size);
//
//		    //將結果傳到前端
//		    session.setAttribute("leaveRequestPage", searchResults);
//		    session.setAttribute("currentPage", page);
//		    session.setAttribute("pageSize", size);
//		    session.setAttribute("totalPages", searchResults.getTotalPages());
//		    session.setAttribute("keyword", keyword);
//
//		    //設置絕對路徑
//		    String contextPath = session.getServletContext().getContextPath();
//		    response.sendRedirect(contextPath + "/GetAllLeaveRequest.front?keyword=" + URLEncoder.encode(keyword, "UTF-8"));
//		}
	
	//chart.js資料傳送(傳送所有員工請假資料)
	@GetMapping("/LeaveRequestAnalysis")
	@ResponseBody
	public Map<String, List<LeaveRequest>> LeaveRequestAnalysis() {
		
		  List<LeaveRequest> leaveRequestByMonth = leaveRequestService.getLeaveRequestByMonth();

	        Map<String, List<LeaveRequest>> categorizedLeaves = new HashMap<>();
	        categorizedLeaves.put("病假", filterLeavesByType(leaveRequestByMonth, "病假"));
	        categorizedLeaves.put("事假", filterLeavesByType(leaveRequestByMonth, "事假"));
	        categorizedLeaves.put("年假", filterLeavesByType(leaveRequestByMonth, "年假"));
	        categorizedLeaves.put("婚假", filterLeavesByType(leaveRequestByMonth, "婚假"));
	        categorizedLeaves.put("產假", filterLeavesByType(leaveRequestByMonth, "產假"));
	        categorizedLeaves.put("喪假", filterLeavesByType(leaveRequestByMonth, "喪假"));

	        return categorizedLeaves;
	    }
	
	//區分假別的方法
    private List<LeaveRequest> filterLeavesByType(List<LeaveRequest> leaveRequestByMonth, String type) {
        return leaveRequestByMonth.stream()
        		//使用filter來過濾當type等於LeaveRequest中的假別時
                .filter(LeaveRequest -> type.equals(LeaveRequest.getLeaveType().getRequestTypeName()))
                //過濾完畢後將資料放進一個List中
                .collect(Collectors.toList());
    }
	
	
	
	//使用data table查詢所有的請假單資料(不能使用page物件)
	@GetMapping("/GetAllLeaveRequest.front")
	public String getAllLeaveRequestFront(Model model, HttpSession session,RedirectAttributes redirectAttributes) {
		
		//從session抓值進來
		String empno = (String) session.getAttribute("account");
	    String job = (String) session.getAttribute("job");
	    
	    List<LeaveRequest> leaveRequests ;
	    
	    if ("boss".equals(job)) {
	    	leaveRequests = leaveRequestService.getAllLeaveRequest();
        } else {
        	leaveRequests = leaveRequestService.getLeaveRequestsByEmpno(empno);
        }
	    
	    model.addAttribute("leaveRequests", leaveRequests);
	    model.addAttribute("job", job);
	    model.addAttribute("empno", empno);
	    
	    
	    if (session.getAttribute("message") != null) {
	        model.addAttribute("message", session.getAttribute("message"));
	        model.addAttribute("messageType", session.getAttribute("messageType"));
	        session.removeAttribute("message");
	        session.removeAttribute("messageType");
	    }

	    if ("boss".equals(job)) {
	        return "leaveRequest/back/GetAllLeaveRequest"; // 管理者畫面
	    } else {
	        return "leaveRequest/GetAllLeaveRequestUser"; // 前台使用者畫面
	    }
	    
	}
	
	//查看單筆請假資料
	@GetMapping("/GetLeaveRequest")
	public String getLeaveRequest(@RequestParam("requestID") String requestID,HttpServletResponse response , Model m , HttpSession session) {
		LeaveRequest leaveRequest = leaveRequestService.getLeaveRequest(requestID);
		
		if(leaveRequest != null) {
			m.addAttribute("leaveRequest",leaveRequest);
		}
		return "leaveRequest/GetLeaveRequest";
	}
	

	//呈現不同權限的新增頁面
	@GetMapping("/InsertLeaveRequest.front")
	public String insertLeaveRequestFront(HttpSession session) {
			String job = (String)session.getAttribute("job");
			
			if ("boss".equals(job)) {
				return "leaveRequest/back/GetInsertLveReq";
			} else {
				return "leaveRequest/GetInsertLveReqUser";
			}
	}
		

	// 新增請假資料
	@PostMapping("/InsertLeaveRequest.front")
	public String insertLeaveRequest(@ModelAttribute LeaveRequestDTO leaveRequestDTO, HttpSession session, 
			RedirectAttributes redirectAttributes, @RequestParam String category) {
	    String job = (String) session.getAttribute("job");
	    String empnoFromSession = (String) session.getAttribute("account");
	    
	    //System.out.println("通知總類 : " + category);
	    

	    try {
	        LeaveRequest leaveRequest = new LeaveRequest();
	        String empno = "boss".equals(job) ? leaveRequestDTO.getEmpno() : empnoFromSession;
	        Long requestTypeID = Long.parseLong(leaveRequestDTO.getRequestTypeID());
	        LeaveType leaveType = leaveRequestService.getLeaveTypeById(requestTypeID);
	        
	        notificationService.createNotificationBasisOnEmpno(category, empno);
	        
	        
	        //System.out.println("存在嗎?" + notificationBasisOnEmpno);

	        // 透過員工編號抓取審核人員
	        Approval approval = leaveRequestService.getApprovalByEmpno(empno);
	        if (approval == null) {
	            redirectAttributes.addFlashAttribute("message", "找不到對應的審查人員");
	            redirectAttributes.addFlashAttribute("messageType", "error");
	            return "redirect:/GetAllLeaveRequest.front";
	        }

	        // 設置審核人員
	        leaveRequest.setApprovalNo1ID(approval.getApprovalNo1ID());
	        leaveRequest.setApprovalNo2ID(approval.getApprovalNo2ID());
	        leaveRequest.setApprovalNo3ID(approval.getApprovalNo3ID());
	        
	        notificationService.createNotificationBasisOnEmpno("請假審核通知1",approval.getApprovalNo1ID());

	        // 將員工關聯進來
	        Employees employee = leaveRequestService.getEmployeesByEmpno(empno);
	        if (employee == null) {
	            redirectAttributes.addFlashAttribute("message", "找不到對應的員工編號");
	            redirectAttributes.addFlashAttribute("messageType", "error");
	            return "redirect:/GetAllLeaveRequest.front";
	        }
	        leaveRequest.setEmployee(employee);

	        // 確保員工編號不為空值
	        if (empno == null || empno.isEmpty()) {
	            redirectAttributes.addFlashAttribute("message", "員工編號不能為空");
	            redirectAttributes.addFlashAttribute("messageType", "error");
	            return "redirect:/GetAllLeaveRequest.front";
	        }
	        leaveRequest.setLeaveType(leaveType);
	        leaveRequest.setReason(leaveRequestDTO.getReason());
	        leaveRequest.setStartTime(java.sql.Timestamp.valueOf(leaveRequestDTO.getStartTime()));
	        leaveRequest.setEndTime(java.sql.Timestamp.valueOf(leaveRequestDTO.getEndTime()));
	        leaveRequest.setTotalHours(leaveRequestDTO.getTotalHours());
	        leaveRequest.setDelegateID(leaveRequestDTO.getDelegateID());

	        if (leaveRequestDTO.getFile() != null && !leaveRequestDTO.getFile().isEmpty()) {
	            String fileName = leaveRequestDTO.getFile().getOriginalFilename();
	            try {
	                File saveFile = new File("C:/upload/" + fileName);
	                leaveRequestDTO.getFile().transferTo(saveFile);
	                leaveRequest.setDocuments(fileName);
	            } catch (IOException e) {
	                e.printStackTrace();
	                redirectAttributes.addFlashAttribute("message", "文件上傳失敗");
	                redirectAttributes.addFlashAttribute("messageType", "error");
	                return "redirect:/GetAllLeaveRequest.front";
	            }
	        }

	        LeaveRequest insertData = leaveRequestService.insertLeaveRequest(leaveRequest);
	        if (insertData != null) {
	            redirectAttributes.addFlashAttribute("message", "申請成功");
	            redirectAttributes.addFlashAttribute("messageType", "success");
	        } else {
	            redirectAttributes.addFlashAttribute("message", "申請失敗");
	            redirectAttributes.addFlashAttribute("messageType", "error");
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	        redirectAttributes.addFlashAttribute("message", "內部錯誤");
	        redirectAttributes.addFlashAttribute("messageType", "error");
	    }
	    
	    return "redirect:/GetAllLeaveRequest.front";
	}


	
	
	//刪除請假資料
	@DeleteMapping("/DeleteLeaveRequest")
	public void deleteLeaveRequest(@RequestParam("requestID") String requestID, HttpServletResponse response, HttpSession session) throws IOException {
	    leaveRequestService.deleteLeaveRequest(requestID);
	    //LeaveRequest isExist = leaveRequestService.getLeaveRequest(requestID);
	    //boolean success = isExist == null;
	    String contextPath = session.getServletContext().getContextPath();
	    response.sendRedirect(contextPath + "/GetAllLeaveRequest.front");
	}

	
	
	//獲取更新請假資料
	@GetMapping("/GetUpdateLeaveRequest")
	public String getUpdateLeaveRequest(@RequestParam("requestID") String requestID,Model m, HttpSession session) {
		
		String job = (String) session.getAttribute("job");
		
		LeaveRequest leaveRequest = leaveRequestService.getLeaveRequest(requestID);
		m.addAttribute("leaveRequest",leaveRequest);
		if ("boss".equals(job)) {
			return "leaveRequest/back/GetUpdateLeaveRequest";
		} else {
			return "leaveRequest/GetUpdateLeaveRequestUser";
		}
		
	}
	
	@PutMapping("/GetUpdateLeaveRequest")
	public void  updateLeaveRequest(
	        @RequestParam("requestID") String requestID,
	        @RequestParam("empno") String empno,
	        @RequestParam("requestTypeID") Long requestTypeID,
	        @RequestParam("reason") String reason,
	        @RequestParam("startTime") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") Date startTime,
	        @RequestParam("endTime") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") Date endTime,
	        @RequestParam("totalHours") int totalHours,
	        @RequestParam("delegateID") String delegateID,
	        @RequestParam("approvalNo1ID") String approvalNo1ID,
	        @RequestParam("approvalNo2ID") String approvalNo2ID,
	        @RequestParam("approvalNo3ID") String approvalNo3ID,
	        @RequestParam(value = "approvalNo1Status", required = false, defaultValue = "待審查") String approvalNo1Status,
	        @RequestParam(value = "approvalNo2Status", required = false, defaultValue = "待審查") String approvalNo2Status,
	        @RequestParam(value = "approvalNo3Status", required = false, defaultValue = "待審查") String approvalNo3Status,
	        @RequestParam(value = "finalStatus", required = false, defaultValue = "待審查") String finalStatus,
	        @RequestPart("file") MultipartFile file,
	        HttpServletResponse response,HttpSession session,
	        RedirectAttributes redirectAttributes) throws IOException {

	    LeaveRequest leaveRequest = leaveRequestService.getLeaveRequest(requestID);
	    
	    if (leaveRequest != null) {
	    	Employees usersByEmpno = employeesService.findUsersByEmpno(empno);
	        leaveRequest.setEmployee(usersByEmpno);
	        leaveRequest.setReason(reason);
	        leaveRequest.setStartTime(startTime);
	        leaveRequest.setEndTime(endTime);
	        leaveRequest.setTotalHours(totalHours);
	        leaveRequest.setDelegateID(delegateID);
	        leaveRequest.setApprovalNo1ID(approvalNo1ID);
	        leaveRequest.setApprovalNo2ID(approvalNo2ID);
	        leaveRequest.setApprovalNo3ID(approvalNo3ID);
	        leaveRequest.setApprovalNo1Status(approvalNo1Status);
	        leaveRequest.setApprovalNo2Status(approvalNo2Status);
	        leaveRequest.setApprovalNo3Status(approvalNo3Status);
	        leaveRequest.setFinalStatus(finalStatus);

	        if (!file.isEmpty()) {
	            String fileName = file.getOriginalFilename();
	            try {
	                File saveFile = new File("C:/upload/" + fileName);
	                file.transferTo(saveFile);
	                leaveRequest.setDocuments(fileName);
	            } catch (IOException e) {
	                e.printStackTrace();
	                response.sendRedirect(session.getServletContext().getContextPath() + "/GetAllLeaveRequest.front?success=false");        
	            }
	        }

	        LeaveType leaveType = leaveTypeService.getLeaveType(requestTypeID);
	        leaveRequest.setLeaveType(leaveType);

	        LeaveRequest updatedLeaveRequest = leaveRequestService.updateLeaveRequest(leaveRequest);
	        boolean success = updatedLeaveRequest != null;
	        
	        if (updatedLeaveRequest != null) {
	            session.setAttribute("message", "修改成功"); // 設置成功消息
	            session.setAttribute("messageType", "success"); // 設置消息類型
	            
	        } else {
	            session.setAttribute("message", "修改失敗"); // 設置失敗消息
	            session.setAttribute("messageType", "error"); // 設置消息類型
	            
	        }
	        response.sendRedirect(session.getServletContext().getContextPath() + "/GetAllLeaveRequest.front?success=" + success);
	    } else {
	    	response.sendRedirect(session.getServletContext().getContextPath() + "/GetAllLeaveRequest.front?success=false");
	    }
	}


	
	

	




	

	
}
