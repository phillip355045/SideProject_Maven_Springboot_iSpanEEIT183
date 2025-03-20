package com.example.demo.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.example.demo.entity.LeaveRequest;
import com.example.demo.service.LeaveRequestService;


import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Controller
public class ReviewLeaveRequestController {
	
	@Autowired
	private LeaveRequestService leaveRequestService;

	//檔案儲存路徑
	private static final String FILE_DIRECTORY = "C:/upload/";
	
	
	//查看所有審核總表請假資料
//	@GetMapping("/GetAllLeaveRequestToReview.front")
//	public String getAllLeaveRequest(Model model, HttpSession session,
//	                                 @RequestParam(value = "page", defaultValue = "1") int page,
//	                                 @RequestParam(value = "size", defaultValue = "10") int size,
//	                                 @RequestParam(value = "keyword", required = false) String keyword) {
//
//	    String empno = (String) session.getAttribute("account");
//	    String job = (String) session.getAttribute("job");
//	    //String deptno = employeesService.findUsersByEmpno(empno).getDeptno(); // 獲取目前員工的部門
//
//	    Page<LeaveRequest> leaveRequestToReviewPage;
//
//	    if (keyword != null && !keyword.isEmpty()) {
//	        leaveRequestToReviewPage = leaveRequestService.searchLeaveRequests(keyword, page, size);
//	    } else {
//	        switch (job) {
//	            case "boss":
//	                leaveRequestToReviewPage = leaveRequestService.getLeaveRequestPage(page, size);
//	                break;
//	            
//	            default:
//	                leaveRequestToReviewPage = leaveRequestService.getLeaveRequestsForApproval(page, size, empno);
//	                break;
//	        }
//	    }
//
//	    model.addAttribute("leaveRequestToReviewPage", leaveRequestToReviewPage);
//	    model.addAttribute("currentPage", page);
//	    model.addAttribute("pageSize", size);
//	    model.addAttribute("totalPages", leaveRequestToReviewPage.getTotalPages());
//	    model.addAttribute("keyword", keyword);
//	    model.addAttribute("job", job);
//	    model.addAttribute("empno", empno);
//
//	    if ("boss".equals(job)) {
//	        return "leaveRequest/back/GetAllLeaveRequestToReview"; // 管理者畫面
//	    } else if ("manager".equals(job) || "leader".equals(job)) {
//	        return "leaveRequest/GetAllLeaveRequestToReviewUser"; // 前台使用者畫面
//	    } else {
//	        return "leaveRequest/accessDenied"; // assistant沒有審核畫面
//	    }
//	}
	
	//使用data table查詢所有審核資料(不能使用page物件)
	@GetMapping("/GetAllLeaveRequestToReview.front")
	public String getAllLeaveRequest(Model model, HttpSession session) {
		
		//抓session進來
		String empno = (String) session.getAttribute("account");
	    String job = (String) session.getAttribute("job");
	    
	    List<LeaveRequest> leaveRequestToReview;
	    
	    if ("boss".equals(job)) {
	    	leaveRequestToReview = leaveRequestService.getLeaveRequestNotFinalStatus();
        } else {
        	leaveRequestToReview = leaveRequestService.getLeaveRequestsForApproval(empno);
        }
	    
	    model.addAttribute("leaveRequestToReview", leaveRequestToReview);
	    model.addAttribute("job", job);
	    model.addAttribute("empno", empno);
	    
	    if ("boss".equals(job)) {
	        return "leaveRequest/back/GetAllLeaveRequestToReview"; // 管理者畫面
	    } else if ("manager".equals(job) || "leader".equals(job)) {
	        return "leaveRequest/GetAllLeaveRequestToReviewUser"; // 前台使用者畫面
	    } else {
	        return "leaveRequest/accessDenied"; // assistant沒有審核畫面
	    }
 }
	
	//抓取請假表單作為審核
	@GetMapping("/GetLeaveRequestToReview")
	public String getLeaveRequest(@RequestParam("requestID") String requestID,Model m) {
		LeaveRequest leaveRequest = leaveRequestService.getLeaveRequest(requestID);
		m.addAttribute("leaveRequest",leaveRequest);
		return "leaveRequest/GetLeaveRequestToReview";
	}
	
	//進行審核
	@PutMapping("/GetLeaveRequestToReview")
	public String toReviewLeaveRequest(
		        @RequestParam("requestID") String requestID,
		        @RequestParam("isApproved") boolean isApproved,
		        HttpServletResponse response, HttpSession session,RedirectAttributes redirectAttributes) throws IOException {

		    try {
		        // 根據請假單ID獲取請假單
		        LeaveRequest leaveRequest = leaveRequestService.getLeaveRequest(requestID);
		        //System.out.println("請假單為 : " + leaveRequest);

		        if (leaveRequest != null) {
		            // 根據當前的審核狀態來決定審核級別
		            int determineApprovalLevel = determineApprovalLevel(leaveRequest);
		            //System.out.println("目前審核級別 : " + determineApprovalLevel);

		            // 更新審核狀態和時間
		            leaveRequestService.approveLeaveRequest(requestID, determineApprovalLevel, isApproved);
		            
		            redirectAttributes.addFlashAttribute("message", "審核成功");
		            redirectAttributes.addFlashAttribute("messageType", "success");
		        } else {
		        	redirectAttributes.addFlashAttribute("message", "找不到對應的請假申請");
		        	redirectAttributes.addFlashAttribute("messageType", "error");
		        }
		    } catch (Exception e) {
		    	redirectAttributes.addFlashAttribute("message", "審核失敗: " + e.getMessage());
		    	redirectAttributes.addFlashAttribute("messageType", "error");
		   }
		return "redirect:/GetAllLeaveRequestToReview.front";
	}
	
	//管理者進行一鍵審核
	@ResponseBody
	@PostMapping("/approveAllRequests")
    public String approveAllRequests(@RequestBody Map<String, String> payload, HttpSession session,RedirectAttributes redirectAttributes) {
		String requestID = payload.get("requestID");
        try {
        	//System.out.println("測試跑直");
            leaveRequestService.approveAllPendingRequestsByAdmin(requestID);
            
            redirectAttributes.addFlashAttribute("message", "一鍵審核成功");
            redirectAttributes.addFlashAttribute("messageType", "success");
        } catch (Exception e) {
        	redirectAttributes.addFlashAttribute("message", "一鍵審核失敗: " + e.getMessage());
        	redirectAttributes.addFlashAttribute("messageType", "error");
        }
        return "redirect:/GetAllLeaveRequestToReview.front";
    }
	
	
	
	//查看所有審核歷史資料
	@GetMapping("/GetAllLeaveRequestToReviewHistrory.front")
	public String getAllLeaveRequestHistrory(Model model, HttpSession session) {

		//抓session
	    String empno = (String) session.getAttribute("account");
	    String job = (String) session.getAttribute("job");
	    

	    List<LeaveRequest> leaveRequestToReview;
	    
	    
	    if ("boss".equals(job)) {
	    	leaveRequestToReview = leaveRequestService.getLeaveRequestsByFinalStatus();
        } else {
        	leaveRequestToReview = leaveRequestService.getLeaveRequestsByFinalStatusWithEmpno(empno);
        }

	    model.addAttribute("leaveRequestToReview", leaveRequestToReview);
	    model.addAttribute("job", job);
	    model.addAttribute("empno", empno);

	    if ("boss".equals(job)) {
	        return "leaveRequest/back/GetAllLeaveRequestToReviewHistrory"; // 管理者畫面
	    } else if ("manager".equals(job) || "leader".equals(job)) {
	        return "leaveRequest/GetAllLeaveRequestToReviewHistroryUser"; // 前台使用者畫面
	    } else {
	        return "leaveRequest/accessDenied"; // assistant沒有審核畫面
	    }
	}



	//----------小功能-------------

    // 確定審核級別的方法
	private int determineApprovalLevel(LeaveRequest leaveRequest) {
//	    System.out.println("ApprovalNo1Status: " + leaveRequest.getApprovalNo1Status());
//	    System.out.println("ApprovalNo2Status: " + leaveRequest.getApprovalNo2Status());
//	    System.out.println("ApprovalNo3Status: " + leaveRequest.getApprovalNo3Status());

	    if ("待審查".equals(leaveRequest.getApprovalNo1Status())) {
//	        System.out.println("審核級別: 1");
	        return 1;
	    } else if ("通過".equals(leaveRequest.getApprovalNo1Status()) && "待審查".equals(leaveRequest.getApprovalNo2Status())) {
//	        System.out.println("審核級別: 2");
	        return 2;
	    } else if ("通過".equals(leaveRequest.getApprovalNo2Status()) && "待審查".equals(leaveRequest.getApprovalNo3Status())) {
//	        System.out.println("審核級別: 3");
	        return 3;
	    } else {
//	        System.out.println("無法確定審核級別");
	        throw new IllegalArgumentException("無法確定審核級別");
	    }
	}


	
	//檔案下載
	@GetMapping("/downloadFile")
	public void downloadFile(@RequestParam("fileName") String fileName, HttpServletResponse response) throws IOException {
			//檔案位置以及檔名這整條路徑
	        File file = new File(FILE_DIRECTORY + File.separator + fileName);
	        
	        if (file.exists()) {
	        	//設定回傳內容
	            response.setContentType("application/octet-stream");
	            response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
	            response.setContentLength((int) file.length());
	            
	            
	            try (FileInputStream inputStream = new FileInputStream(file);
	                 OutputStream outputStream = response.getOutputStream()) {

	                byte[] buffer = new byte[4096];
	                int bytesRead = -1;
	                while ((bytesRead = inputStream.read(buffer)) != -1) {
	                    outputStream.write(buffer, 0, bytesRead);
	                }
	            }
	        } else {
	            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
	        }
	    }
	
	
//	//模糊查詢(用不到了)
//	@GetMapping("/LeaveRequestToReviewSearch")
//	public void searchLeaveRequestsToReview(@RequestParam("keyword") String keyword,
//	                                          @RequestParam(value = "page", defaultValue = "1") int page,
//	                                          @RequestParam(value = "size", defaultValue = "10") int size,
//	                                          HttpServletResponse response,
//	                                          HttpSession session) throws UnsupportedEncodingException, IOException {
//		response.setCharacterEncoding("UTF-8");
//	    Page<LeaveRequest> leaveRequestToReviewPage = leaveRequestService.searchLeaveRequests(keyword, page, size);
//	    
//	    leaveRequestToReviewPage.forEach(lr -> System.out.println("LeaveRequest: " + lr.toString()));
//	    
//	    session.setAttribute("leaveRequestToReviewPage", leaveRequestToReviewPage);
//	    session.setAttribute("currentPage", page);
//	    session.setAttribute("pageSize", size);
//	    session.setAttribute("totalPages", leaveRequestToReviewPage.getTotalPages());
//	    session.setAttribute("keyword", keyword);
//	    
//	    String contextPath = session.getServletContext().getContextPath();
//	    response.sendRedirect(contextPath + "/GetAllLeaveRequestToReview.front?keyword=" + URLEncoder.encode(keyword, "UTF-8"));
//	}



}
