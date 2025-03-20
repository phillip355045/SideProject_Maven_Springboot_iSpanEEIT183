package com.example.demo.controller;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

import com.example.demo.dto.BusinessTripDTO;
import com.example.demo.dto.LeaveRequestDTO;
import com.example.demo.entity.Approval;
import com.example.demo.entity.BusinessTrip;
import com.example.demo.entity.Employees;
import com.example.demo.entity.LeaveRequest;
import com.example.demo.entity.LeaveType;
import com.example.demo.service.BusinessTripService;
import com.example.demo.service.EmployeesService;
import com.example.demo.service.NotificationService;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Controller
public class BusinessTripController {
	
	@Autowired
	private BusinessTripService businessTripService;
	
	@Autowired
	private EmployeesService employeesService;
	
	@Autowired
	private NotificationService notificationService;
	
//	//利用分頁來查看所有資料
//	@GetMapping("/GetAllBusinessTrip.front")
//	public String getAllBusinessTrip(Model model, HttpSession session,
//		                              @RequestParam(value = "page", defaultValue = "1") int page,
//		                              @RequestParam(value = "size", defaultValue = "10") int size,
//		                              @RequestParam(value = "keyword", required = false) String keyword) {
//		
//		String empno = (String) session.getAttribute("account");
//	    String job = (String) session.getAttribute("job");
//		
//		Page<BusinessTrip> businessTripPage;
//		
//		if (keyword != null && !keyword.isEmpty()) {
//			businessTripPage = businessTripService.searchBusinessTrips(keyword, page, size);
//	    } else {
//	        if ("boss".equals(job)) {
//	        	businessTripPage = businessTripService.getBusinessTrip(page, size);
//	        } else {
//	        	businessTripPage = businessTripService.getBusinessTripByEmpno(page, size, empno);
//	        }
//	    }
//		
//		for (BusinessTrip bt : businessTripPage) {
//	        if (bt.getEmployee() == null) {
//	            // 打印或記錄日誌以便進行調試
//	            System.out.println("BusinessTrip " + bt.getTripID() + " does not have an associated employee.");
//	        }
//	    }
//		    
//		    model.addAttribute("businessTripPage", businessTripPage);
//		    model.addAttribute("currentPage", page);
//		    model.addAttribute("pageSize", size);
//		    model.addAttribute("totalPages", businessTripPage.getTotalPages());
//		    model.addAttribute("keyword", keyword);
//		    model.addAttribute("job", job);
//		    model.addAttribute("empno", empno);
//
//		    if (session.getAttribute("message") != null) {
//		        model.addAttribute("message", session.getAttribute("message"));
//		        model.addAttribute("messageType", session.getAttribute("messageType"));
//		        session.removeAttribute("message");
//		        session.removeAttribute("messageType");
//		    }
//
//		    if ("boss".equals(job)) {
//		        return "businessTrip/back/GetAllBusinessTrip"; // 管理者畫面
//		    } else {
//		        return "businessTrip/GetAllBusinessTripUser"; // 前台使用者畫面
//		    }
//	}
	
	//使用data table查詢所有的出差資料(不能使用page物件)
	@GetMapping("/GetAllBusinessTrip.front")
	public String getAllBusinessTrip(Model model, HttpSession session) {
		
		//從session抓值進來
		String empno = (String) session.getAttribute("account");
		String job = (String) session.getAttribute("job");
		
		List<BusinessTrip> businessTrip;
		
		if ("boss".equals(job)) {
			businessTrip = businessTripService.getBusinessTrip();
        } else {
        	businessTrip = businessTripService.getBusinessTripByEmpno(empno);
        }
		
		model.addAttribute("businessTrip", businessTrip);
		model.addAttribute("job", job);
		model.addAttribute("empno", empno);
		
	    if (session.getAttribute("message") != null) {
        model.addAttribute("message", session.getAttribute("message"));
        model.addAttribute("messageType", session.getAttribute("messageType"));
        session.removeAttribute("message");
        session.removeAttribute("messageType");
	    }
	    
	    if ("boss".equals(job)) {
	        return "businessTrip/back/GetAllBusinessTrip"; // 管理者畫面
	    } else {
	        return "businessTrip/GetAllBusinessTripUser"; // 前台使用者畫面
	    }

	    
	}
	

	
	//查看單筆請假資料
	@GetMapping("/GetBusinessTrip")
	public String getBusinessTrip(@RequestParam("tripID") String tripID,Model m) {
			BusinessTrip businessTrip = businessTripService.getBusinessTrip(tripID);
			
			if(businessTrip != null) {
				m.addAttribute("businessTrip",businessTrip);
			}
			return "businessTrip/GetBusinessTrip";
	}
	
	//呈現新增頁面
	@GetMapping("/InsertBusinessTrip.front")
	public String insertBusinessTripFront(HttpSession session) {
		String job = (String)session.getAttribute("job");
		if ("boss".equals(job)) {
			return "businessTrip/back/GetInsertBusinessTrip";
		} else {
			return "businessTrip/GetInsertBusinessTripUser";
		}
	}
	
	
	//新增請假資料
	@PostMapping("/InsertBusinessTrip.front")
	public String insertBusinessTrip(@ModelAttribute BusinessTripDTO businessTripDTO,
			HttpSession session, RedirectAttributes redirectAttributes,@RequestParam String category) {
		   
		   String job = (String) session.getAttribute("job");
		   String empnoFromSession = (String)session.getAttribute("account");
		   
		   try {
			   BusinessTrip businessTrip = new BusinessTrip();

			   String empno = "boss".equals(job) ? businessTripDTO.getEmpno() : empnoFromSession;
			   
			   notificationService.createNotificationBasisOnEmpno(category, empno);
			   
			   // 透過員工編號抓取審核人員
		       Approval approval = businessTripService.getApprovalByEmpno(empno);
		       if (approval == null) {
		            redirectAttributes.addFlashAttribute("message", "找不到對應的審查人員");
		            redirectAttributes.addFlashAttribute("messageType", "error");
		            return "redirect:/GetAllBusinessTrip.front";
		        }
		       
		       // 設置審核人員
		       businessTrip.setApprovalNo1ID(approval.getApprovalNo1ID());
		       businessTrip.setApprovalNo2ID(approval.getApprovalNo2ID());
		       businessTrip.setApprovalNo3ID(approval.getApprovalNo3ID());
		       
		       notificationService.createNotificationBasisOnEmpno("出差審核通知1",approval.getApprovalNo1ID());
			   
			   // 將員工關聯進來
			   Employees employee = businessTripService.getEmployeesByEmpno(empno);
			   if (employee == null) {
				   redirectAttributes.addFlashAttribute("success", false);
				   redirectAttributes.addFlashAttribute("message", "找不到對應的員工編號");
				   return "redirect:/GetAllBusinessTrip.front";
		        }businessTrip.setEmployee(employee);
		        
		        // 確保員工編號不為空值
		        if (empno == null || empno.isEmpty()) {
		        	redirectAttributes.addFlashAttribute("success", false);
		        	redirectAttributes.addFlashAttribute("message", "員工編號不能為空");
		        	return "redirect:/GetAllBusinessTrip.front";
		        }
			   
			   
			   businessTrip.setReason(businessTripDTO.getReason());
			   businessTrip.setStartTime(java.sql.Timestamp.valueOf(businessTripDTO.getStartTime()));
			   businessTrip.setEndTime(java.sql.Timestamp.valueOf(businessTripDTO.getEndTime())); 
			   businessTrip.setDelegateID(businessTripDTO.getDelegateID());
			   businessTrip.setLocation(businessTripDTO.getLocation());
			   businessTrip.setTotalHours(businessTripDTO.getTotalHours());

		       if (businessTripDTO.getFile() != null && !businessTripDTO.getFile().isEmpty()) {
		            String fileName = businessTripDTO.getFile().getOriginalFilename();
		            try {
		                File saveFile = new File("C:/upload/" + fileName);
		                businessTripDTO.getFile().transferTo(saveFile);
		                businessTrip.setDocuments(fileName);
		                
		            } catch (IOException e) {
		                e.printStackTrace();
		                redirectAttributes.addFlashAttribute("success", false);
		                redirectAttributes.addFlashAttribute("message", "檔案上傳失敗");
		                return "redirect:/GetAllBusinessTrip.front";
		            }
		        }

		       BusinessTrip insertData = businessTripService.insertBusinessTrip(businessTrip);

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
		   return "redirect:/GetAllBusinessTrip.front";
		}
	

	//刪除請假資料
	@DeleteMapping("/DeleteBusinessTrip")
	public void deleteBusinessTrip(@RequestParam("tripID") String tripID,HttpServletResponse response, HttpSession session) throws IOException {
		businessTripService.deleteBusinessTrip(tripID);
			
//		BusinessTrip isExist = businessTripService.getBusinessTrip(tripID);
//		boolean success = isExist == null;
			
		String contextPath = session.getServletContext().getContextPath();
		response.sendRedirect(contextPath + "/GetAllBusinessTrip.front");		
	}
	
	
	//獲取更新請假資料
	@GetMapping("/GetUpdateBusinessTrip")
	public String getUpdateBusinessTrip(@RequestParam("tripID") String tripID,Model m, HttpSession session) {
		String job = (String) session.getAttribute("job");
		
		BusinessTrip businessTrip = businessTripService.getBusinessTrip(tripID);
		m.addAttribute("businessTrip",businessTrip);
		
		if ("boss".equals(job)) {
			return "businessTrip/back/GetUpdateBusinessTrip";
		} else {
			return "businessTrip/GetUpdateBusinessTripUser";
		}
	}
		
	@PutMapping("/GetUpdateBusinessTrip")
	public void updateBusinessTrip(
			@RequestParam("tripID") String tripID,
		    @RequestParam("empno") String empno,
		    @RequestParam("reason") String reason,
		    @RequestParam("startTime") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") Date startTime,
		    @RequestParam("endTime") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") Date endTime,
		    @RequestParam("delegateID") String delegateID,
		    @RequestParam("location") String location,
		    @RequestParam("totalHours") int totalHours,
		    @RequestParam("approvalNo1ID") String approvalNo1ID,
		        @RequestParam("approvalNo2ID") String approvalNo2ID,
		        @RequestParam("approvalNo3ID") String approvalNo3ID,
		        @RequestParam(value = "approvalNo1Status", required = false, defaultValue = "待審查") String approvalNo1Status,
		        @RequestParam(value = "approvalNo2Status", required = false, defaultValue = "待審查") String approvalNo2Status,
		        @RequestParam(value = "approvalNo3Status", required = false, defaultValue = "待審查") String approvalNo3Status,
		        @RequestParam(value = "finalStatus", required = false, defaultValue = "待審查") String finalStatus,
		        @RequestPart("file") MultipartFile file,
		        HttpServletResponse response,HttpSession session,
		        RedirectAttributes redirectAttributes)throws IOException {

			BusinessTrip businessTrip = businessTripService.getBusinessTrip(tripID);

		    if (businessTrip != null) {
		    	Employees usersByEmpno = employeesService.findUsersByEmpno(empno);
		    	businessTrip.setEmployee(usersByEmpno);
		    	
		    	businessTrip.setReason(reason);
		    	businessTrip.setStartTime(startTime);
		    	businessTrip.setEndTime(endTime);
		    	businessTrip.setDelegateID(delegateID);
		    	businessTrip.setTotalHours(totalHours);
		    	businessTrip.setLocation(location);
		    	businessTrip.setApprovalNo1ID(approvalNo1ID);
		    	businessTrip.setApprovalNo2ID(approvalNo2ID);
		    	businessTrip.setApprovalNo3ID(approvalNo3ID);
		    	businessTrip.setApprovalNo1Status(approvalNo1Status);
		    	businessTrip.setApprovalNo2Status(approvalNo2Status);
		    	businessTrip.setApprovalNo3Status(approvalNo3Status);
		    	businessTrip.setFinalStatus(finalStatus);

		        if (!file.isEmpty()) {
		            String fileName = file.getOriginalFilename();
		            try {
		                File saveFile = new File("C:/upload/" + fileName);
		                file.transferTo(saveFile);
		                businessTrip.setDocuments(fileName);
		            } catch (IOException e) {
		                e.printStackTrace();
		                response.sendRedirect(session.getServletContext().getContextPath() + "/GetAllBusinessTrip.front?success=false");        
		            }
		        }

		        BusinessTrip updateBusinessTrip = businessTripService.updateBusinessTrip(businessTrip);

		        
		        boolean success = updateBusinessTrip != null;
		        
		        if (updateBusinessTrip != null) {
		        	session.setAttribute("message", "修改成功"); // 設置成功消息
		        	session.setAttribute("messageType", "success"); // 設置消息類型
		            
		        } else {
		        	session.setAttribute("message", "修改失敗"); // 設置失敗消息
		        	session.setAttribute("messageType", "error"); // 設置消息類型
		            
		        }
		        
		        response.sendRedirect(session.getServletContext().getContextPath() + "/GetAllBusinessTrip.front?success=" + success);
		    } else {
		    	response.sendRedirect(session.getServletContext().getContextPath() + "/GetAllBusinessTrip.front?success=false");
		    }

		}
	
	//模糊查詢
	@GetMapping("/BusinessTripSearch")
	public void searchBusinessTrips(@RequestParam("keyword") String keyword,
		                                  @RequestParam(value = "page", defaultValue = "1") int page,
		                                  @RequestParam(value = "size", defaultValue = "10") int size,
		                                  HttpServletResponse response,
			                                HttpSession session) throws IOException {
		response.setCharacterEncoding("UTF-8");
		
		Page<BusinessTrip> businessTripPage = businessTripService.searchBusinessTrips(keyword, page, size);
		//將結果傳到前端
		session.setAttribute("businessTripPage", businessTripPage);
		session.setAttribute("currentPage", page);
		session.setAttribute("pageSize", size);
		session.setAttribute("totalPages", businessTripPage.getTotalPages());
		session.setAttribute("keyword", keyword);
		
		//設置絕對路徑
	    String contextPath = session.getServletContext().getContextPath();
	    response.sendRedirect(contextPath + "/GetAllBusinessTrip.front?keyword=" + URLEncoder.encode(keyword, "UTF-8"));
		}
	
	
	
}
