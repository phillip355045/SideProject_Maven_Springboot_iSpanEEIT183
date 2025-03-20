package com.example.demo.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.entity.BusinessTrip;
import com.example.demo.entity.Employees;
import com.example.demo.entity.LeaveRequest;
import com.example.demo.service.BusinessTripService;
import com.example.demo.service.EmployeesService;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;



@Controller
public class ReviewBusinessTripController {
	
	@Autowired
	private BusinessTripService businessTripService;
	

//	//查看所有審核請假資料
//	@GetMapping("/GetAllBusinessTripToReview.front")
//	public String getAllBusinessTrip(Model model,HttpSession session,
//				 @RequestParam(value = "page", defaultValue = "1") int page,
//	             @RequestParam(value = "size", defaultValue = "10") int size,
//	             @RequestParam(value = "keyword", required = false) String keyword) {
//		
//		String empno = (String) session.getAttribute("account");
//	    String job = (String) session.getAttribute("job");
//	    Page<BusinessTrip> businessTripToReviewPage;
//	    
//	    if (keyword != null && !keyword.isEmpty()) {
//	    	businessTripToReviewPage = businessTripService.searchBusinessTrips(keyword, page, size);
//	    } else {
//	        switch (job) {
//	            case "boss":
//	            	businessTripToReviewPage = businessTripService.getBusinessTrip(page, size);
//	                break;
//	            case "manager":
//	            	businessTripToReviewPage = businessTripService.getBusinessTripsForManager(page, size, empno);
//	                break;
//	            case "leader":
//	            	businessTripToReviewPage = businessTripService.getBusinessTripsForLeader(page, size, empno);
//	                break;
//	            default:
//	            	businessTripToReviewPage = Page.empty(); // assistant沒有審核畫面
//	                break;
//	        }
//	    }
//	    
//		
//			
//			model.addAttribute("businessTripToReviewPage", businessTripToReviewPage);
//	        model.addAttribute("currentPage", page);
//	        model.addAttribute("pageSize", size);
//	        model.addAttribute("totalPages", businessTripToReviewPage.getTotalPages());
//	        model.addAttribute("keyword", keyword);
//		    model.addAttribute("job", job);
//		    model.addAttribute("empno", empno);
//
//		    if ("boss".equals(job)) {
//		        return "businessTrip/back/GetAllBusinessTripToReview"; // 管理者畫面
//		    } else if ("manager".equals(job) || "leader".equals(job)) {
//		        return "businessTrip/GetAllBusinessTripToReviewUser"; // 前台使用者畫面
//		    } else {
//		        return "businessTrip/accessDenied"; // assistant沒有審核畫面
//		    }
//	}
	
	
	//使用data table查詢所有審核資料(不能使用page物件)
	@GetMapping("/GetAllBusinessTripToReview.front")
	public String getAllBusinessTrip(Model model,HttpSession session) {
		
		//抓session進來
		String empno = (String) session.getAttribute("account");
	    String job = (String) session.getAttribute("job");
	    
	    List<BusinessTrip> businessTripToReview;
	    
	    if ("boss".equals(job)) {
	    	businessTripToReview = businessTripService.getBusinessTripNotFinalStatus();
        } else {
        	businessTripToReview = businessTripService.getBusinessTripForApproval(empno);
        }
	    
	    model.addAttribute("businessTripToReview", businessTripToReview);
	    model.addAttribute("job", job);
	    model.addAttribute("empno", empno);
	    
	    if ("boss".equals(job)) {
	        return "businessTrip/back/GetAllBusinessTripToReview"; // 管理者畫面
	    } else if ("manager".equals(job) || "leader".equals(job)) {
	        return "businessTrip/GetAllBusinessTripToReviewUser"; // 前台使用者畫面
	    } else {
	        return "businessTrip/accessDenied"; // assistant沒有審核畫面
	    }
	}

	
	
	//抓取請假表單作為審核
	@GetMapping("/GetBusinessTripToReview")
	public String getLeaveRequest(@RequestParam("tripID") String tripID,Model m) {
			BusinessTrip businessTrip = businessTripService.getBusinessTrip(tripID);
			m.addAttribute("businessTrip",businessTrip);
			return "businessTrip/GetBusinessTripToReview";
	}
		
	//進行審核
	@PutMapping("/GetBusinessTripToReview")
	public String toReviewBusinessTrip(
			@RequestParam("tripID") String tripID,
	        @RequestParam("isApproved") boolean isApproved,
	        HttpServletResponse response, HttpSession session,RedirectAttributes redirectAttributes) throws IOException {

			try {
				// 根據出差單ID獲取出差單
				BusinessTrip businessTrip = businessTripService.getBusinessTrip(tripID);
				
				if (businessTrip != null) {
		            // 根據當前的審核狀態來決定審核級別
		            int determineApprovalLevel = determineApprovalLevel(businessTrip);
		            //System.out.println("目前審核級別 : " + determineApprovalLevel);

		            // 更新審核狀態和時間
		            businessTripService.approveBusinessTrip(tripID, determineApprovalLevel, isApproved);
		            
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
		return "redirect:/GetAllBusinessTripToReview.front";
	}
	
	//管理者進行一鍵審核
	@ResponseBody
	@PostMapping("/approveAllBusinessTrip")
    public String approveAllBusinessTrip(@RequestBody Map<String, String> payload, HttpSession session,RedirectAttributes redirectAttributes) {
		String tripID = payload.get("tripID");
        try {
        	//System.out.println("測試跑直");
        	businessTripService.approveAllPendingBusinessTripByAdmin(tripID);
            
            redirectAttributes.addFlashAttribute("message", "一鍵審核成功");
            redirectAttributes.addFlashAttribute("messageType", "success");
        } catch (Exception e) {
        	redirectAttributes.addFlashAttribute("message", "一鍵審核失敗: " + e.getMessage());
        	redirectAttributes.addFlashAttribute("messageType", "error");
        }
        return "redirect:/GetAllBusinessTripToReview.front";
    }
		
	
	
	//查看所有審核歷史資料
	@GetMapping("/GetAllBusinessTripToReviewHistrory.front")
	public String getAllBusinessTripHistrory(Model model, HttpSession session) {

		//抓session
	    String empno = (String) session.getAttribute("account");
	    String job = (String) session.getAttribute("job");
	    

	    List<BusinessTrip> businessTripToReview;
	    
	    
	    if ("boss".equals(job)) {
	    	businessTripToReview = businessTripService.getBusinessTripByFinalStatus();
        } else {
        	businessTripToReview = businessTripService.getBusinessTripByFinalStatusWithEmpno(empno);
        }

	    model.addAttribute("businessTripToReview", businessTripToReview);
	    model.addAttribute("job", job);
	    model.addAttribute("empno", empno);

	    if ("boss".equals(job)) {
	        return "businessTrip/back/GetAllBusinessTripToReviewHistrory"; // 管理者畫面
	    } else if ("manager".equals(job) || "leader".equals(job)) {
	        return "businessTrip/GetAllBusinessTripToReviewHistroryUser"; // 前台使用者畫面
	    } else {
	        return "businessTrip/accessDenied"; // assistant沒有審核畫面
	    }
	}
		
	//----------小功能-------------
	
    // 確定審核級別的方法
	private int determineApprovalLevel(BusinessTrip businessTrip) {
//	    System.out.println("ApprovalNo1Status: " + businessTrip.getApprovalNo1Status());
//	    System.out.println("ApprovalNo2Status: " + businessTrip.getApprovalNo2Status());
//	    System.out.println("ApprovalNo3Status: " + businessTrip.getApprovalNo3Status());

	    if ("待審查".equals(businessTrip.getApprovalNo1Status())) {
	        //System.out.println("審核級別: 1");
	        return 1;
	    } else if ("通過".equals(businessTrip.getApprovalNo1Status()) && "待審查".equals(businessTrip.getApprovalNo2Status())) {
	        //System.out.println("審核級別: 2");
	        return 2;
	    } else if ("通過".equals(businessTrip.getApprovalNo2Status()) && "待審查".equals(businessTrip.getApprovalNo3Status())) {
	        //System.out.println("審核級別: 3");
	        return 3;
	    } else {
	        //System.out.println("無法確定審核級別");
	        throw new IllegalArgumentException("無法確定審核級別");
	    }
	}
		
		//模糊查詢
		@GetMapping("/BusinessTripToReviewSearch")
		public void searchBusinessTripsToReview(@RequestParam("keyword") String keyword,
		                                          @RequestParam(value = "page", defaultValue = "1") int page,
		                                          @RequestParam(value = "size", defaultValue = "10") int size,
		                                          HttpServletResponse response,
		                                          HttpSession session) throws UnsupportedEncodingException, IOException {
			response.setCharacterEncoding("UTF-8");
			Page<BusinessTrip> businessTripToReviewPage = businessTripService.searchBusinessTrips(keyword, page, size);
		    
			businessTripToReviewPage.forEach(lr -> System.out.println("BusinessTrip: " + lr.toString()));
		    
			session.setAttribute("businessTripToReviewPage", businessTripToReviewPage);
			session.setAttribute("currentPage", page);
			session.setAttribute("pageSize", size);
			session.setAttribute("totalPages", businessTripToReviewPage.getTotalPages());
			session.setAttribute("keyword", keyword);
			
			String contextPath = session.getServletContext().getContextPath();
		    response.sendRedirect(contextPath + "/GetAllBusinessTripToReview.front?keyword=" + URLEncoder.encode(keyword, "UTF-8"));
		}

}
