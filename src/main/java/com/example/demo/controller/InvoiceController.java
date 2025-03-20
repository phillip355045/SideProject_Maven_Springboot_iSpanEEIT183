package com.example.demo.controller;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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

import com.example.demo.dto.InvoiceDTO;
import com.example.demo.entity.Approval;
import com.example.demo.entity.Employees;
import com.example.demo.entity.Invoice;
import com.example.demo.entity.LeaveRequest;
import com.example.demo.entity.LeaveType;
import com.example.demo.service.EmployeesService;
import com.example.demo.service.InvoiceService;
import com.example.demo.service.NotificationService;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Controller
public class InvoiceController {
	
	@Autowired
	private InvoiceService invoiceService;
	
	@Autowired
	private EmployeesService employeesService;
	
	@Autowired
	private NotificationService notificationService;
	
//	//利用分頁來查看資料
//	@GetMapping("/GetAllInvoice.front")
//	public String getAllInvoice(Model model, HttpSession session,
//		                              @RequestParam(value = "page", defaultValue = "1") int page,
//		                              @RequestParam(value = "size", defaultValue = "10") int size,
//		                              @RequestParam(value = "keyword", required = false) String keyword) {
//		String empno = (String) session.getAttribute("account");
//	    String job = (String) session.getAttribute("job");
//	    
//	    Page<Invoice> invoicePage;
//		
//	    if (keyword != null && !keyword.isEmpty()) {
//	    	invoicePage = invoiceService.searchInvoices(keyword, page, size);
//	    } else {
//	        if ("boss".equals(job)) {
//	        	invoicePage = invoiceService.getInvoicePage(page, size);
//	        } else {
//	        	invoicePage = invoiceService.getInvoiceByEmpno(page, size, empno);
//	        }
//	    }
//			
//		    model.addAttribute("invoicePage", invoicePage);
//		    model.addAttribute("currentPage", page);
//		    model.addAttribute("pageSize", size);
//		    model.addAttribute("totalPages", invoicePage.getTotalPages());
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
//		        return "invoice/back/GetAllInvoice"; // 管理者畫面
//		    } else {
//		        return "invoice/GetAllInvoiceUser"; // 前台使用者畫面
//		    }
//	}
	
	//使用data table查詢所有的請款單資料(不能使用page物件)
	@GetMapping("/GetAllInvoice.front")
	public String getAllInvoice(Model model, HttpSession session) {
		
		//從session抓值進來
		String empno = (String) session.getAttribute("account");
	    String job = (String) session.getAttribute("job");
	    
	    List<Invoice> invoice;
	    
	    if ("boss".equals(job)) {
	    	invoice = invoiceService.getInvoice();
        } else {
        	invoice = invoiceService.getInvoiceByEmpno(empno);
        }
	    
	    model.addAttribute("invoice", invoice);
	    model.addAttribute("job", job);
	    model.addAttribute("empno", empno);
	    
	    if (session.getAttribute("message") != null) {
	        model.addAttribute("message", session.getAttribute("message"));
	        model.addAttribute("messageType", session.getAttribute("messageType"));
	        session.removeAttribute("message");
	        session.removeAttribute("messageType");
	    }
	    
	    if ("boss".equals(job)) {
	        return "invoice/back/GetAllInvoice"; // 管理者畫面
	    } else {
	        return "invoice/GetAllInvoiceUser"; // 前台使用者畫面
	    }
	    
	}
	
	
	//查看單筆請假資料
	@GetMapping("/GetInvoice")
	public String getInvoice(@RequestParam("invoiceID") String invoiceID,Model m) {
			
			Invoice invoice = invoiceService.getInvoice(invoiceID);
			if(invoice != null) {
				m.addAttribute("invoice",invoice);
				System.out.println("invoice : " + invoice);
			}else {
				System.out.println("沒有抓到值");
			}
			
			return "invoice/GetInvoice";
		}
		
	//呈現新增頁面
	@GetMapping("/InsertInvoice.front")
	public String insertInvoice(HttpSession session) {
		String job = (String)session.getAttribute("job");
		
		if ("boss".equals(job)) {
			return "invoice/back/GetInsertInvoice";
		} else {
			return "invoice/GetInsertInvoiceUser";
		}
		
	}
		
		
	//新增請假資料
	@PostMapping("/InsertInvoice.front")
	public String insertInvoice(@ModelAttribute InvoiceDTO invoiceDTO, HttpSession session, 
			RedirectAttributes redirectAttributes, @RequestParam String category) {
		
		    String job = (String) session.getAttribute("job");
		    String empnoFromSession = (String)session.getAttribute("account");
		    
		    
		    try {
		    	
		    	Invoice invoice = new Invoice();
		    	
		    	String empno = "boss".equals(job) ? invoiceDTO.getEmpno() : empnoFromSession;
		        //System.out.println("確認empno是否有抓到值 : " + empno);
		    	
		    	notificationService.createNotificationBasisOnEmpno(category, empno);
		    	
		        // 透過員工編號抓取審核人員
		        Approval approval = invoiceService.getApprovalByEmpno(empno);
		        if (approval == null) {
		            redirectAttributes.addFlashAttribute("message", "找不到對應的審查人員");
		            redirectAttributes.addFlashAttribute("messageType", "error");
		            return "redirect:/GetAllInvoice.front";
		        }
		        
		        // 設置審核人員
		        invoice.setApprovalNo1ID(approval.getApprovalNo1ID());
		        invoice.setApprovalNo2ID(approval.getApprovalNo2ID());
		        invoice.setApprovalNo3ID(approval.getApprovalNo3ID());
		        
		        notificationService.createNotificationBasisOnEmpno("請款審核通知1",approval.getApprovalNo1ID());
		        
		        // 將員工關聯進來
		        Employees employee = invoiceService.getEmployeesByEmpno(empno);
		        if (employee == null) {
		        	redirectAttributes.addFlashAttribute("success", false);
		        	redirectAttributes.addFlashAttribute("message", "找不到對應的員工編號");
		        	return "redirect:/GetAllInvoice.front";
		        }
		        invoice.setEmployee(employee);

		        // 確保員工編號不為空值
		        if (empno == null || empno.isEmpty()) {
		        	redirectAttributes.addFlashAttribute("success", false);
		        	redirectAttributes.addFlashAttribute("message", "員工編號不能為空");
		        	return "redirect:/GetAllInvoice.front";
		        }
		        
		    	
		    	invoice.setDescription(invoiceDTO.getDescription());
		    	invoice.setAmount(invoiceDTO.getAmount());
		    	invoice.setPaymentDate(java.sql.Timestamp.valueOf(invoiceDTO.getPaymentDate()));

		       		        
		        if (invoiceDTO.getFile() != null && !invoiceDTO.getFile().isEmpty()) {
		            String fileName = invoiceDTO.getFile().getOriginalFilename();
		            try {
		                File saveFile = new File("C:/upload/" + fileName);
		                invoiceDTO.getFile().transferTo(saveFile);
		                invoice.setDocuments(fileName);
		            } catch (IOException e) {
		                e.printStackTrace();
		                redirectAttributes.addFlashAttribute("success", false);
		                redirectAttributes.addFlashAttribute("message", "檔案上傳失敗");
		                return "redirect:/GetAllInvoice.front";
		            }
		        }
		        
		        Invoice insertData = invoiceService.insertInvoice(invoice);

		        if (insertData != null) {
		        	redirectAttributes.addFlashAttribute("message", "申請成功");
		        	redirectAttributes.addFlashAttribute("messageType", "success");
		        } else {
		        	redirectAttributes.addFlashAttribute("message", "申請失敗");
		        	redirectAttributes.addFlashAttribute("messageType", "error");
		        }


		    } catch (Exception e) {
		        e.printStackTrace();
		        session.setAttribute("message", "內部錯誤");
		        session.setAttribute("messageType", "error");
		       
		    }
		    return "redirect:/GetAllInvoice.front";
		}

	//刪除請假資料
	@DeleteMapping("/DeleteInvoice")
	public void deleteInvoice(@RequestParam("invoiceID") String invoiceID, HttpServletResponse response, HttpSession session) throws IOException {
			invoiceService.deleteInvoice(invoiceID);
			
//			Invoice isExist = invoiceService.getInvoice(invoiceID);
//			boolean success = isExist == null;
			
			String contextPath = session.getServletContext().getContextPath();
			response.sendRedirect(contextPath + "/GetAllInvoice.front");
		}
		
		
	//獲取更新請假資料
	@GetMapping("/GetUpdateInvoice")
	public String getUpdateInvoice(@RequestParam("invoiceID") String invoiceID,Model m, HttpSession session) {
		String job = (String) session.getAttribute("job");
		
		Invoice invoice = invoiceService.getInvoice(invoiceID);
			
		m.addAttribute("invoice",invoice);
		
		if ("boss".equals(job)) {
			return "invoice/back/GetUpdateInvoice";
		} else {
			return "invoice/GetUpdateInvoiceUser";
		}
	}
		
	@PutMapping("/GetUpdateInvoice")
	public void updateInvoice(
		        @RequestParam("invoiceID") String invoiceID,
		        @RequestParam("empno") String empno,
		        @RequestParam("paymentDate") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")Date paymentDate,
		        @RequestParam("amount") BigDecimal amount,
		        @RequestParam("description") String description,
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
		
		Invoice invoice = invoiceService.getInvoice(invoiceID);
		
		

		    if (invoice != null) {
		    	Employees usersByEmpno = employeesService.findUsersByEmpno(empno);
		    	invoice.setEmployee(usersByEmpno);
		    	invoice.setPaymentDate(paymentDate);
		    	invoice.setAmount(amount);
		    	invoice.setDescription(description);
		    	invoice.setApprovalNo1ID(approvalNo1ID);
		    	invoice.setApprovalNo2ID(approvalNo2ID);
		        invoice.setApprovalNo3ID(approvalNo3ID);
		        invoice.setApprovalNo1Status(approvalNo1Status);
		        invoice.setApprovalNo2Status(approvalNo2Status);
		        invoice.setApprovalNo3Status(approvalNo3Status);
		        invoice.setFinalStatus(finalStatus);

		        if (!file.isEmpty()) {
		            String fileName = file.getOriginalFilename();
		            try {
		                File saveFile = new File("C:/upload/" + fileName);
		                file.transferTo(saveFile);
		                invoice.setDocuments(fileName);
		            } catch (IOException e) {
		                e.printStackTrace();
		                response.sendRedirect(session.getServletContext().getContextPath() + "/GetAllInvoice.front?success=false");
		            }
		        }

		        Invoice updateInvoice = invoiceService.updateInvoice(invoice);
		        boolean success = updateInvoice != null;
		        
		        if (updateInvoice != null) {
		            session.setAttribute("message", "修改成功"); // 設置成功消息
		            session.setAttribute("messageType", "success"); // 設置消息類型
		            
		        } else {
		            session.setAttribute("message", "修改失敗"); // 設置失敗消息
		            session.setAttribute("messageType", "error"); // 設置消息類型
		            
		        }
		        
		        response.sendRedirect(session.getServletContext().getContextPath() + "/GetAllInvoice.front?success=" + success);
		    } else {
		    	response.sendRedirect(session.getServletContext().getContextPath() + "/GetAllInvoice.front?success=false");
		    }
		}
		
	//模糊查詢
	@GetMapping("/InvoiceSearch")
	public void searchInvoices(@RequestParam("keyword") String keyword,
		                                  @RequestParam(value = "page", defaultValue = "1") int page,
		                                  @RequestParam(value = "size", defaultValue = "10") int size,
		                                  HttpServletResponse response,
			                                HttpSession session) throws IOException  {
		response.setCharacterEncoding("UTF-8");
		Page<Invoice> invoicePage = invoiceService.searchInvoices(keyword, page, size);
		    
		session.setAttribute("invoicePage", invoicePage);
		session.setAttribute("currentPage", page);
		session.setAttribute("pageSize", size);
		session.setAttribute("totalPages", invoicePage.getTotalPages());
		session.setAttribute("keyword", keyword);
		
		//設置絕對路徑
	    String contextPath = session.getServletContext().getContextPath();
	    response.sendRedirect(contextPath + "/GetAllInvoice.front?keyword=" + URLEncoder.encode(keyword, "UTF-8"));
		}

}
