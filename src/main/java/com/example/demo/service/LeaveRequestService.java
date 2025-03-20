package com.example.demo.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.example.demo.entity.Approval;
import com.example.demo.entity.Employees;
import com.example.demo.entity.LeaveRequest;
import com.example.demo.entity.LeaveType;
import com.example.demo.repository.ApprovalRepository;
import com.example.demo.repository.EmployeesRepository;
import com.example.demo.repository.LeaveRequestRepository;
import com.example.demo.repository.LeaveTypeRepository;

import jakarta.persistence.criteria.Join;

@Service
public class LeaveRequestService {
	
	@Autowired
	private LeaveRequestRepository leaveRequestRepository;
	
	@Autowired
	private EmployeesRepository employeesRepository;
	
	@Autowired
	private LeaveTypeRepository leaveTypeRepository;
	
	@Autowired
	private ApprovalRepository approvalRepository;
	
	
	@Autowired
	private NotificationService notificationService;
	
	
	
	//查看所有請假資料
	public List<LeaveRequest> getAllLeaveRequest(){
		List<LeaveRequest> leaveRequests = leaveRequestRepository.findAll();
		return leaveRequests;
	}
	
	//管理者查看所有不是最終狀態的資料
	public List<LeaveRequest> getLeaveRequestNotFinalStatus() {
        
        List<LeaveRequest> leaveRequest = leaveRequestRepository.findALLLeaveRequestsByFinalStatus();
        
        //更新每個請求的最終狀態
        leaveRequest.forEach(this::updateFinalStatus);
        
        return leaveRequest; 
    }
	
	//抓取當月份員工請假資料
	public List<LeaveRequest> getLeaveRequestByMonth(){
		List<LeaveRequest> leaveRequest = leaveRequestRepository.findALLLeaveRequestsByMonth();
		return leaveRequest;
	}
	
	
	//查看單一資料
	public LeaveRequest getLeaveRequest(String requestID) {
		Optional<LeaveRequest> leaveRequestOptional = leaveRequestRepository.findById(requestID);
		
		if (leaveRequestOptional.isPresent()) {
			LeaveRequest leaveRequest = leaveRequestOptional.get();
	        // 更新最終狀態
	        //updateFinalStatus(leaveRequest);
			return leaveRequest;
		}
		return null;
	}
	
	//新增請假資料
	public LeaveRequest insertLeaveRequest(LeaveRequest leaveRequest) {
		return leaveRequestRepository.save(leaveRequest);
	}
	
	//修改請假資料
	public LeaveRequest updateLeaveRequest(LeaveRequest leaveRequest) {
		String requestID = leaveRequest.getRequestID();
		
		if(requestID != null) {
			return leaveRequestRepository.save(leaveRequest);
		} return null;
	}
	
	//刪除請假資料
	public void deleteLeaveRequest(String requestID) {
		leaveRequestRepository.deleteById(requestID);
	}
	
	
	
	//模糊查詢
	public Page<LeaveRequest> searchLeaveRequests(String keyword, int page, int size) {
		    Pageable pageable = PageRequest.of(page - 1, size);
		    Page<LeaveRequest> results = leaveRequestRepository.searchAllFields(keyword, pageable);
		    results.forEach(this::updateFinalStatus);
		    return leaveRequestRepository.searchAllFields(keyword, pageable);
		}

	 //--------------------------------------查找外鍵內容------------------------------------------
	
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
	 
	 //利用請假單編號查找requestType
	 public LeaveType getLeaveTypeById(Long requestTypeID) {
		 LeaveType leaveType = leaveTypeRepository.findByRequestTypeID(requestTypeID);
		 if (leaveType != null) {
	            System.out.println("leaveType found: " + leaveType);
	        } else {
	            System.out.println("leaveType not found with requestTypeID: " + leaveType);
	        }
	        return leaveType;
	 }
	 
	 //透過員工編號來查找請假單資料
	 public List<LeaveRequest> getLeaveRequestsByEmpno(String empno) {
		 
		 List<LeaveRequest> leaveRequest = leaveRequestRepository.findByEmployeeEmpno(empno);
		 leaveRequest.forEach(this::updateFinalStatus);
		 
	        return leaveRequest;
	    }	 

	 //透過員工編號來查找請假單資料
	 public Page<LeaveRequest> getLeaveRequestsByEmpno(int page, int size ,String empno) {
		 Pageable pageable = PageRequest.of(page-1, size); // 頁數從0開始，因此需要減1
		 Page<LeaveRequest> leaveRequestPage = leaveRequestRepository.findByEmployeeEmpno(pageable , empno);
		 
		//更新每個請求的最終狀態
	    leaveRequestPage.forEach(this::updateFinalStatus);
	    return leaveRequestPage;
	        		
	  }
	
	//審核請假單
	public Page<LeaveRequest> getLeaveRequestsForManager(int page, int size, String empno) {
	        Pageable pageable = PageRequest.of(page - 1, size);
	        List<String> jobs = Arrays.asList("leader", "assistant");
	        return leaveRequestRepository.findByJobAndEmpnoIn(jobs, empno, pageable);
	    }

	public Page<LeaveRequest> getLeaveRequestsForLeader(int page, int size, String empno) {
	        Pageable pageable = PageRequest.of(page - 1, size);
	        return leaveRequestRepository.findByJobAndEmpno("assistant", empno, pageable);
	    }
	
	//利用員工編號抓取審核人員
	public Approval getApprovalByEmpno(String empno){
		return approvalRepository.findByEmployee_Empno(empno);
	}
	
	//抓取歷史資料
	public List<LeaveRequest> getLeaveRequestsByFinalStatus() {
        return leaveRequestRepository.findLeaveRequestsByFinalStatus();
    }
	
	//抓取各層級的歷史資料
	public List<LeaveRequest> getLeaveRequestsByFinalStatusWithEmpno(String empno){
		return leaveRequestRepository.findLeaveRequestsByFinalStatusWithEmpno(empno);
	}
	
	//審查流程
	public LeaveRequest approveLeaveRequest(String requestID, int approvalLevel, boolean isApproved) {
	    Optional<LeaveRequest> leaveRequestOptional = leaveRequestRepository.findById(requestID);

	    if (leaveRequestOptional.isPresent()) {
	        LeaveRequest leaveRequest = leaveRequestOptional.get();
	        //System.out.println("有抓到請假表單 : " + leaveRequest);

	        switch (approvalLevel) {
	            case 1:
	                leaveRequest.setApprovalNo1Status(isApproved ? "通過" : "拒絕");
	                leaveRequest.setApprovalNo1Date(new Date());
	                if (isApproved) {
	                    leaveRequest.setApprovalNo2Status("待審查");
	                    // 發送通知給審核二
	                    notificationService.createNotificationBasisOnEmpno("請假審核通知2", leaveRequest.getApprovalNo2ID());
	                } else {
	                    leaveRequest.setFinalStatus("拒絕");
	                    notificationService.createNotificationBasisOnEmpno("請假最終審核結果通知", leaveRequest.getEmployee().getEmpno());
	                }
	                break;

	            case 2:
	                if ("待審查".equals(leaveRequest.getApprovalNo2Status())) {
	                    leaveRequest.setApprovalNo2Status(isApproved ? "通過" : "拒絕");
	                    leaveRequest.setApprovalNo2Date(new Date());
	                    if (isApproved) {
	                        leaveRequest.setApprovalNo3Status("待審查");
	                        // 發送通知給審核三
	                        notificationService.createNotificationBasisOnEmpno("請假審核通知3", leaveRequest.getApprovalNo3ID());
	                    } else {
	                        leaveRequest.setFinalStatus("拒絕");
	                        notificationService.createNotificationBasisOnEmpno("請假最終審核結果通知", leaveRequest.getEmployee().getEmpno());
	                    }
	                }
	                break;

	            case 3:
	                if ("待審查".equals(leaveRequest.getApprovalNo3Status())) {
	                    leaveRequest.setApprovalNo3Status(isApproved ? "通過" : "拒絕");
	                    leaveRequest.setApprovalNo3Date(new Date());
	                    if (isApproved) {
	                        leaveRequest.setFinalStatus("最終通過");
	                        // 最後一個審核通過，通知最終結果
	                        notificationService.createNotificationBasisOnEmpno("請假最終審核結果通知", leaveRequest.getEmployee().getEmpno());
	                    } else {
	                        leaveRequest.setFinalStatus("拒絕");
	                        notificationService.createNotificationBasisOnEmpno("請假最終審核結果通知", leaveRequest.getEmployee().getEmpno());
	                    }
	                }
	                break;

	            default:
	                throw new IllegalArgumentException("無法驗證目前審查階段: " + approvalLevel);
	        }

	        updateFinalStatus(leaveRequest);
	        return leaveRequestRepository.save(leaveRequest);
	    } else {
	        throw new IllegalArgumentException("請假單編號不存在 " + requestID);
	    }
	}
	
	//管理者的一鍵審核功能
	public void approveAllPendingRequestsByAdmin(String requestID) {
		LeaveRequest leaveRequest = leaveRequestRepository.findById(requestID)
	            .orElseThrow(() -> new IllegalArgumentException("請假單編號不存在 " + requestID));
		
		if ("待審查".equals(leaveRequest.getApprovalNo1Status())) {
            leaveRequest.setApprovalNo1Status("管理者已完成審核的更改");
            leaveRequest.setApprovalNo1Date(new Date());
        }
        if ("待審查".equals(leaveRequest.getApprovalNo2Status())) {
            leaveRequest.setApprovalNo2Status("管理者已完成審核的更改");
            leaveRequest.setApprovalNo2Date(new Date());
        }
        if ("待審查".equals(leaveRequest.getApprovalNo3Status())) {
            leaveRequest.setApprovalNo3Status("管理者已完成審核的更改");
            leaveRequest.setApprovalNo3Date(new Date());
        }
        leaveRequest.setFinalStatus("管理者已完成審核的更改");
        leaveRequestRepository.save(leaveRequest);
    }
	
	

	
	//使用page的方法，利用empno將
	public List<LeaveRequest> getLeaveRequestsForApproval(String empno) {
        
        List<LeaveRequest> leaveRequestPage = leaveRequestRepository.findLeaveRequestsForApproval(empno);
        
        leaveRequestPage.forEach(this::updateFinalStatus);
        // 調試輸出查詢結果
        //System.out.println("查詢結果: " + leaveRequests.getContent());
        return leaveRequestPage;
    }
	
	
	//------------------------------------小功能------------------------------------(目前用不到的)
	
	//設定最終狀態
	private void updateFinalStatus(LeaveRequest leaveRequest) {
	    leaveRequest.setFinalStatus(getFinalStatus(leaveRequest));
	}

	private String getFinalStatus(LeaveRequest leaveRequest) {
	    if ("拒絕".equals(leaveRequest.getApprovalNo1Status())) {
	        return "拒絕";
	    }
	    if ("管理者已完成審核的更改".equals(leaveRequest.getApprovalNo1Status()) ||
	            "管理者已完成審核的更改".equals(leaveRequest.getApprovalNo2Status()) ||
	            "管理者已完成審核的更改".equals(leaveRequest.getApprovalNo3Status())) {
	            return "管理者已完成審核的更改";
	       }
	    if ("通過".equals(leaveRequest.getApprovalNo1Status()) && "待審查".equals(leaveRequest.getApprovalNo2Status())) {
	        return "審查一完成";
	    }
	    if ("拒絕".equals(leaveRequest.getApprovalNo2Status())) {
	        return "拒絕";
	    }
	    if ("通過".equals(leaveRequest.getApprovalNo2Status()) && "待審查".equals(leaveRequest.getApprovalNo3Status())) {
	        return "審查二完成";
	    }
	    if ("拒絕".equals(leaveRequest.getApprovalNo3Status())) {
	        return "拒絕";
	    }
	    if ("通過".equals(leaveRequest.getApprovalNo3Status())) {
	        return "最終通過";
	    }
	    return "待審查";
	}
	
	//計算請假時數
	public long calculateTotalLeaveHours(Date startTime, Date endTime) {
			
			//將Date轉換為LocalDateTime，目的是用來計算時間
	        LocalDateTime start = LocalDateTime.ofInstant(startTime.toInstant(), ZoneId.systemDefault());
	        LocalDateTime end = LocalDateTime.ofInstant(endTime.toInstant(), ZoneId.systemDefault());
	        
	        //設定起始時間與結束時間
	        LocalTime workStart = LocalTime.of(9, 0);
	        LocalTime workEnd = LocalTime.of(18, 0);

	        long totalHours = 0;

	        while (!start.toLocalDate().isAfter(end.toLocalDate())) {
	            LocalDateTime currentStart = start.toLocalDate().atTime(workStart);
	            LocalDateTime currentEnd = start.toLocalDate().atTime(workEnd);

	            if (start.isAfter(currentStart)) {
	                currentStart = start;
	            }

	            if (end.toLocalDate().equals(start.toLocalDate()) && end.isBefore(currentEnd)) {
	                currentEnd = end;
	            }

	            if (!currentStart.isAfter(currentEnd)) {
	                totalHours += Duration.between(currentStart, currentEnd).toHours();
	            }

	            start = start.plusDays(1).toLocalDate().atTime(workStart);
	        }

	        return totalHours;
	    }
		
	//查詢特定請假資料
	public List<LeaveRequest> getSpecificLeaveRequest(String field, String value) {
			 	//創建Specification，用來生成查詢條件(使用 lambda 表達式)
			 	//root代表LeaveRequest實體、query代表 JPA 查詢對象、criteriaBuilder代表生成查詢條件
		        Specification<LeaveRequest> specification = (root, query, criteriaBuilder) -> {
		            if ("requestTypeName".equals(field)) {
		            	//因為是需要用到關聯兩張表(需要用到leaveType這張表)
		                Join<Object, Object> leaveType = root.join("leaveType"); //將兩張表關聯
		                //再利用leaveType這張表去查詢requestTypeName中的內容
		                return criteriaBuilder.equal(leaveType.get("requestTypeName"), value);
		            } else {
		                return criteriaBuilder.equal(root.get(field), value);
		            }
		        };

		        return leaveRequestRepository.findAll(specification);
		  }
	

}
