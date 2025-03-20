package com.example.demo.service;


import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.demo.entity.Approval;
import com.example.demo.entity.BusinessTrip;
import com.example.demo.entity.Employees;
import com.example.demo.entity.Invoice;
import com.example.demo.entity.LeaveRequest;
import com.example.demo.repository.ApprovalRepository;
import com.example.demo.repository.EmployeesRepository;
import com.example.demo.repository.InvoiceRepository;


@Service
public class InvoiceService {
	
	@Autowired
	private InvoiceRepository invoiceRepository;
	
	@Autowired
	private EmployeesRepository employeesRepository;
	
	@Autowired
	private ApprovalRepository approvalRepository;
	
	@Autowired
	private NotificationService notificationService;
	
	//管理者查詢所有請款單
	public List<Invoice> getInvoice() {
        
		List<Invoice> invoice = invoiceRepository.findAll();//不用在leaveRequestRepository宣告該方法
        
        //更新每個請求的最終狀態
		invoice.forEach(this::updateFinalStatus);
        
        return invoice; 
    }
	
	//管理者查看所有不是最終狀態的資料
	public List<Invoice> getInvoiceNotFinalStatus() {
        
        List<Invoice> invoice = invoiceRepository.findALLInvoiceByFinalStatus();
        
        //更新每個請求的最終狀態
        invoice.forEach(this::updateFinalStatus);
        
        return invoice; 
    }
	
	//一般層級查看不是最終狀態的資料
	public List<Invoice> getInvoiceForApproval(String empno) {
        
        List<Invoice> invoice = invoiceRepository.findInvoiceForApproval(empno);
        
        invoice.forEach(this::updateFinalStatus);
        // 調試輸出查詢結果
        //System.out.println("查詢結果: " + leaveRequests.getContent());
        return invoice;
    }
	
	
	//設定最終狀態
	private void updateFinalStatus(Invoice invoice) {
		invoice.setFinalStatus(getFinalStatus(invoice));
	    }
	private String getFinalStatus(Invoice invoice) {
	    if ("拒絕".equals(invoice.getApprovalNo1Status())) {
	        return "拒絕";
	    }
	    if ("管理者已完成審核的更改".equals(invoice.getApprovalNo1Status()) ||
	            "管理者已完成審核的更改".equals(invoice.getApprovalNo2Status()) ||
	            "管理者已完成審核的更改".equals(invoice.getApprovalNo3Status())) {
	            return "管理者已完成審核的更改";
	       }
	    if ("通過".equals(invoice.getApprovalNo1Status()) && "待審查".equals(invoice.getApprovalNo2Status())) {
	        return "審查一完成";
	    }
	    if ("拒絕".equals(invoice.getApprovalNo2Status())) {
	        return "拒絕";
	    }
	    if ("通過".equals(invoice.getApprovalNo2Status()) && "待審查".equals(invoice.getApprovalNo3Status())) {
	        return "審查二完成";
	    }
	    if ("拒絕".equals(invoice.getApprovalNo3Status())) {
	        return "拒絕";
	    }
	    if ("通過".equals(invoice.getApprovalNo3Status())) {
	        return "最終通過";
	    }
	    return "待審查";
	}
	
	//查看單一資料
	public Invoice getInvoice(String invoiceID) {
		Optional<Invoice> invoiceOptional = invoiceRepository.findById(invoiceID);
			
			if (invoiceOptional.isPresent()) {
				Invoice invoice = invoiceOptional.get();
		        // 更新最終狀態
		        updateFinalStatus(invoice);
				return invoice;
			}
			return null;
	}
	
	//利用員工編號抓取審核人員
	public Approval getApprovalByEmpno(String empno){
		return approvalRepository.findByEmployee_Empno(empno);
	}
	
	
	//利用員工編號查找出差表單
	public List<Invoice> getBusinessTripByEmpno(String empno){
		 return invoiceRepository.findByEmployeeEmpno(empno);
	}
	
	//新增請假資料
	public Invoice insertInvoice(Invoice invoice) {
		return invoiceRepository.save(invoice);
	}
	
	//利用員工編號查找出差表單
	public List<Invoice> getInvoiceByEmpno(String empno) {
		List<Invoice> invoice = invoiceRepository.findByEmployeeEmpno(empno);

		//更新每個請求的最終狀態
		invoice.forEach(this::updateFinalStatus);
		return invoice;
		        		
	}
	
	//利用員工編號查找Employees
	public Employees getEmployeesByEmpno(String empno){
		 System.out.println("Fetching employee with empno: " + empno);
		    
		    Employees employee = employeesRepository.findByEmpno(empno);
		    if (employee != null) {
	            System.out.println("Employee found: " + employee);
	        } else {
	            System.out.println("Employee not found with empno: " + empno);
	        }
	        return employee;
	 }
	
		
	//修改請假資料
	public Invoice updateInvoice(Invoice invoice) {
		
		String invoiceID = invoice.getInvoiceID();
			
		if(invoiceID != null) {
				return invoiceRepository.save(invoice);
			} return null;
		}
		
	//刪除請假資料
	public void deleteInvoice(String invoiceID) {
		invoiceRepository.deleteById(invoiceID);
	}
	
	//抓取歷史資料
	public List<Invoice> getInvoicesByFinalStatus() {
        return invoiceRepository.findInvoicesByFinalStatus();
    }
	
	//抓取各層級的歷史資料
	public List<Invoice> getInvoicesByFinalStatusWithEmpno(String empno){
		return invoiceRepository.findInvoicesByFinalStatusWithEmpno(empno);
	}

	//模糊查詢
	public Page<Invoice> searchInvoices(String keyword, int page, int size) {
		Pageable pageable = PageRequest.of(page - 1, size);
		Page<Invoice> searchAllFields = invoiceRepository.searchAllFields(keyword, pageable);
		searchAllFields.forEach(this::updateFinalStatus);
		return searchAllFields;
	}
	
	//審核請款單
		public Page<Invoice> getInvoicesForManager(int page, int size, String empno) {
		        Pageable pageable = PageRequest.of(page - 1, size);
		        List<String> jobs = Arrays.asList("leader", "assistant");
		        return invoiceRepository.findByJobAndEmpnoIn(jobs, empno, pageable);
		    }

		public Page<Invoice> getInvoicesForLeader(int page, int size, String empno) {
		        Pageable pageable = PageRequest.of(page - 1, size);
		        return invoiceRepository.findByJobAndEmpno("assistant", empno, pageable);
		    }
		
		//審查流程
		public Invoice approveInvoice(String invoiceID, int approvalLevel, boolean isApproved) {
		    Optional<Invoice> invoiceOptional = invoiceRepository.findById(invoiceID);

		    if (invoiceOptional.isPresent()) {
		    	Invoice invoice = invoiceOptional.get();
		        //System.out.println("有抓到請假表單 : " + leaveRequest);

		        switch (approvalLevel) {
		            case 1:
		            	invoice.setApprovalNo1Status(isApproved ? "通過" : "拒絕");
		            	invoice.setApprovalNo1Date(new Date());
		                if (isApproved) {
		                	invoice.setApprovalNo2Status("待審查");
		                	// 發送通知給審核二
		                    notificationService.createNotificationBasisOnEmpno("請款審核通知2", invoice.getApprovalNo2ID());
		                } else {
		                	invoice.setFinalStatus("拒絕");
		                	 notificationService.createNotificationBasisOnEmpno("請款最終審核結果通知", invoice.getEmployee().getEmpno());
		                }
		                break;

		            case 2:
		                if ("待審查".equals(invoice.getApprovalNo2Status())) {
		                	invoice.setApprovalNo2Status(isApproved ? "通過" : "拒絕");
		                	invoice.setApprovalNo2Date(new Date());
		                    if (isApproved) {
		                    	invoice.setApprovalNo3Status("待審查");
		                    	// 發送通知給審核三
			                    notificationService.createNotificationBasisOnEmpno("請款審核通知3", invoice.getApprovalNo3ID());
		                    } else {
		                    	invoice.setFinalStatus("拒絕");
		                    	 notificationService.createNotificationBasisOnEmpno("請款最終審核結果通知", invoice.getEmployee().getEmpno());
		                    }
		                }
		                break;

		            case 3:
		                if ("待審查".equals(invoice.getApprovalNo3Status())) {
		                	invoice.setApprovalNo3Status(isApproved ? "通過" : "拒絕");
		                    invoice.setApprovalNo3Date(new Date());
		                    if (isApproved) {
		                    	invoice.setFinalStatus("最終通過");
		                    	// 最後一個審核通過，通知最終結果
		                        notificationService.createNotificationBasisOnEmpno("請款最終審核結果通知", invoice.getEmployee().getEmpno());
		                    } else {
		                    	invoice.setFinalStatus("拒絕");
		                    	 notificationService.createNotificationBasisOnEmpno("請款最終審核結果通知", invoice.getEmployee().getEmpno());
		                    }
		                }
		                break;

		            default:
		                throw new IllegalArgumentException("無法驗證目前審查階段: " + approvalLevel);
		        }

		        updateFinalStatus(invoice);
		        return invoiceRepository.save(invoice);
		    } else {
		        throw new IllegalArgumentException("請假單編號不存在 " + invoiceID);
		    }
		}
		
		
		//管理者的一鍵審核功能
		public void approveAllPendingInvoicesByAdmin(String invoiceID) {
			Optional<Invoice> byId = invoiceRepository.findById(invoiceID);
			Invoice invoice = invoiceRepository.findById(invoiceID)
		            .orElseThrow(() -> new IllegalArgumentException("請假單編號不存在 " + invoiceID));
			
			if ("待審查".equals(invoice.getApprovalNo1Status())) {
				invoice.setApprovalNo1Status("管理者已完成審核的更改");
	            invoice.setApprovalNo1Date(new Date());
	        }
	        if ("待審查".equals(invoice.getApprovalNo2Status())) {
	        	invoice.setApprovalNo2Status("管理者已完成審核的更改");
	        	invoice.setApprovalNo2Date(new Date());
	        }
	        if ("待審查".equals(invoice.getApprovalNo3Status())) {
	        	invoice.setApprovalNo3Status("管理者已完成審核的更改");
	        	invoice.setApprovalNo3Date(new Date());
	        }
	        invoice.setFinalStatus("管理者已完成審核的更改");
	        invoiceRepository.save(invoice);
	    }

}
