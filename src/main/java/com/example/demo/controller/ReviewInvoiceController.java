package com.example.demo.controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
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

import com.example.demo.entity.Employees;
import com.example.demo.entity.Invoice;
import com.example.demo.entity.LeaveRequest;
import com.example.demo.service.EmployeesService;
import com.example.demo.service.InvoiceService;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Controller
public class ReviewInvoiceController {
	
	@Autowired
	private InvoiceService invoiceService;
	
	@Autowired
	private EmployeesService employeesService;
	
	//檔案儲存路徑
	private static final String FILE_DIRECTORY = "C:/upload/";
	
	//使用data table查詢所有審核資料(不能使用page物件)
	@GetMapping("/GetAllInvoiceToReview.front")
	public String getAllInvoice(Model model, HttpSession session){
		
		String empno = (String) session.getAttribute("account");
	    String job = (String) session.getAttribute("job");
	    
	    List<Invoice> invoiceToReview ;
		 
		if ("boss".equals(job)) {
		    	invoiceToReview = invoiceService.getInvoiceNotFinalStatus();
	        } else {
	        	invoiceToReview = invoiceService.getInvoiceForApproval(empno);
	        }		 
				
		model.addAttribute("invoiceToReview", invoiceToReview);
	    model.addAttribute("job", job);
	    model.addAttribute("empno", empno);
		
	    if ("boss".equals(job)) {
	        return "invoice/back/GetAllInvoiceToReview"; // 管理者畫面
	    } else if ("manager".equals(job) || "leader".equals(job)) {
	        return "invoice/GetAllInvoiceToReviewUser"; // 前台使用者畫面
	    } else {
	        return "invoice/accessDenied"; // assistant沒有審核畫面
	    }
	}
		
	//抓取請假表單作為審核
	@GetMapping("/GetInvoiceToReview")
	public String Invoice(@RequestParam("invoiceID") String invoiceID,Model m) {
		Invoice invoice = invoiceService.getInvoice(invoiceID);
		m.addAttribute("invoice",invoice);
		return "invoice/GetInvoiceToReview";
	}
			
	//進行審核
	@PutMapping("/GetInvoiceToReview")
	public String toReviewInvoice(
			        @RequestParam("invoiceID") String invoiceID,
			        @RequestParam("isApproved") boolean isApproved,
			        HttpServletResponse response, HttpSession session,RedirectAttributes redirectAttributes) throws IOException {

		try {
			Invoice invoice = invoiceService.getInvoice(invoiceID);
			
			if(invoice != null) {
				int determineApprovalLevel = determineApprovalLevel(invoice);
				invoiceService.approveInvoice(invoiceID,determineApprovalLevel,isApproved);
				
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
		return "redirect:/GetAllInvoiceToReview.front";
	}
	
	//管理者進行一鍵審核
	@ResponseBody
	@PostMapping("/approveAllInvoices")
    public String approveAllInvoices(@RequestBody Map<String, String> payload, HttpSession session,RedirectAttributes redirectAttributes) {
		String invoiceID = payload.get("invoiceID");
        try {
        	
        	invoiceService.approveAllPendingInvoicesByAdmin(invoiceID);
            
            redirectAttributes.addFlashAttribute("message", "一鍵審核成功");
            redirectAttributes.addFlashAttribute("messageType", "success");
        } catch (Exception e) {
        	redirectAttributes.addFlashAttribute("message", "一鍵審核失敗: " + e.getMessage());
        	redirectAttributes.addFlashAttribute("messageType", "error");
        }
        return "redirect:/GetAllInvoiceToReview.front";
    }
		
	//查看所有審核歷史資料
	@GetMapping("/GetAllInvoiceToReviewHistrory.front")
	public String getAllInvoiceHistrory(Model model, HttpSession session) {

		//抓session
	    String empno = (String) session.getAttribute("account");
	    String job = (String) session.getAttribute("job");
	    

	    List<Invoice> invoiceToReview;
	    
	    
	    if ("boss".equals(job)) {
	    	invoiceToReview = invoiceService.getInvoicesByFinalStatus();
        } else {
        	invoiceToReview = invoiceService.getInvoicesByFinalStatusWithEmpno(empno);
        }

	    model.addAttribute("invoiceToReview", invoiceToReview);
	    model.addAttribute("job", job);
	    model.addAttribute("empno", empno);

	    if ("boss".equals(job)) {
	        return "invoice/back/GetAllInvoiceToReviewHistrory"; // 管理者畫面
	    } else if ("manager".equals(job) || "leader".equals(job)) {
	        return "invoice/GetAllInvoiceToReviewHistroryUser"; // 前台使用者畫面
	    } else {
	        return "invoice/accessDenied"; // assistant沒有審核畫面
	    }
	}
	
    // 確定審核級別的方法
	private int determineApprovalLevel(Invoice invoice) {
//	    System.out.println("ApprovalNo1Status: " + invoice.getApprovalNo1Status());
//	    System.out.println("ApprovalNo2Status: " + invoice.getApprovalNo2Status());
//	    System.out.println("ApprovalNo3Status: " + invoice.getApprovalNo3Status());

	    if ("待審查".equals(invoice.getApprovalNo1Status())) {
//	        System.out.println("審核級別: 1");
	        return 1;
	    } else if ("通過".equals(invoice.getApprovalNo1Status()) && "待審查".equals(invoice.getApprovalNo2Status())) {
//	        System.out.println("審核級別: 2");
	        return 2;
	    } else if ("通過".equals(invoice.getApprovalNo2Status()) && "待審查".equals(invoice.getApprovalNo3Status())) {
	        //System.out.println("審核級別: 3");
	        return 3;
	    } else {
	        //System.out.println("無法確定審核級別");
	        throw new IllegalArgumentException("無法確定審核級別");
	    }
	}
	
			
	//模糊查詢
	@GetMapping("/InvoiceToReviewSearch")
	public void searchInvoicesToReview(@RequestParam("keyword") String keyword,
			                                          @RequestParam(value = "page", defaultValue = "1") int page,
			                                          @RequestParam(value = "size", defaultValue = "10") int size,
			                                          HttpServletResponse response,
			                                          HttpSession session) throws UnsupportedEncodingException, IOException {
		
		response.setCharacterEncoding("UTF-8");
		
		Page<Invoice> invoiceToReviewPage = invoiceService.searchInvoices(keyword, page, size);
				
			    
		invoiceToReviewPage.forEach(lr -> System.out.println("Invoices: " + lr.toString()));
			    
		session.setAttribute("invoiceToReviewPage", invoiceToReviewPage);
		session.setAttribute("currentPage", page);
		session.setAttribute("pageSize", size);
		session.setAttribute("totalPages", invoiceToReviewPage.getTotalPages());
		session.setAttribute("keyword", keyword);
		
		
		String contextPath = session.getServletContext().getContextPath();
	    response.sendRedirect(contextPath + "/GetAllInvoiceToReview.front?keyword=" + URLEncoder.encode(keyword, "UTF-8"));
			}

}
