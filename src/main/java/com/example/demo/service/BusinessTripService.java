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
import com.example.demo.entity.LeaveRequest;
import com.example.demo.repository.ApprovalRepository;
import com.example.demo.repository.BusinessTripRepository;
import com.example.demo.repository.EmployeesRepository;

@Service
public class BusinessTripService {
	
	@Autowired
	private BusinessTripRepository businessTripRepository;
	
	@Autowired
	private EmployeesRepository employeesRepository;
	
	@Autowired
	private ApprovalRepository approvalRepository;
	
	@Autowired
	private NotificationService notificationService;
	
	//管理者查看所有資料
	public List<BusinessTrip> getBusinessTrip() {

		List<BusinessTrip> businessTrip = businessTripRepository.findAll();//不用在businessTripRepository宣告該方法
	        
	        //更新每個請求的最終狀態
			businessTrip.forEach(this::updateFinalStatus);
	        
	        return businessTrip; 
	}
	
	//管理者查看所有不是最終狀態的資料
	public List<BusinessTrip> getBusinessTripNotFinalStatus() {
        
        List<BusinessTrip> businessTrip = businessTripRepository.findALLBusinessTripByFinalStatus();
        
        //更新每個請求的最終狀態
        businessTrip.forEach(this::updateFinalStatus);
        
        return businessTrip; 
    }
	
	//一般層級查看所有不是最終狀態的資料
	public List<BusinessTrip> getBusinessTripForApproval(String empno) {
        
        List<BusinessTrip> businessTrip = businessTripRepository.findBusinessTripForApproval(empno);
        
        businessTrip.forEach(this::updateFinalStatus);
        // 調試輸出查詢結果
        //System.out.println("查詢結果: " + leaveRequests.getContent());
        return businessTrip;
    }
	
	//審查流程
	public BusinessTrip approveBusinessTrip(String tripID, int approvalLevel, boolean isApproved) {
	    Optional<BusinessTrip> businessTripOptional = businessTripRepository.findById(tripID);

	    if (businessTripOptional.isPresent()) {
	    	BusinessTrip businessTrip = businessTripOptional.get();
	        //System.out.println("有抓到請假表單 : " + leaveRequest);

	        switch (approvalLevel) {
	            case 1:
	            	businessTrip.setApprovalNo1Status(isApproved ? "通過" : "拒絕");
	            	businessTrip.setApprovalNo1Date(new Date());
	                if (isApproved) {
	                	businessTrip.setApprovalNo2Status("待審查");
	                	// 發送通知給審核二
	                    notificationService.createNotificationBasisOnEmpno("出差審核通知2", businessTrip.getApprovalNo2ID());
	                } else {
	                	businessTrip.setFinalStatus("拒絕");
	                	notificationService.createNotificationBasisOnEmpno("出差最終審核結果通知", businessTrip.getEmployee().getEmpno());
	                }
	                break;

	            case 2:
	                if ("待審查".equals(businessTrip.getApprovalNo2Status())) {
	                	businessTrip.setApprovalNo2Status(isApproved ? "通過" : "拒絕");
	                	businessTrip.setApprovalNo2Date(new Date());
	                    if (isApproved) {
	                    	businessTrip.setApprovalNo3Status("待審查");
	                    	// 發送通知給審核三
		                    notificationService.createNotificationBasisOnEmpno("出差審核通知3", businessTrip.getApprovalNo3ID());
	                    } else {
	                    	businessTrip.setFinalStatus("拒絕");
	                    	notificationService.createNotificationBasisOnEmpno("出差最終審核結果通知", businessTrip.getEmployee().getEmpno());
	                    }
	                }
	                break;

	            case 3:
	                if ("待審查".equals(businessTrip.getApprovalNo3Status())) {
	                	businessTrip.setApprovalNo3Status(isApproved ? "通過" : "拒絕");
	                	businessTrip.setApprovalNo3Date(new Date());
	                    if (isApproved) {
	                    	businessTrip.setFinalStatus("最終通過");
	                    	// 最後一個審核通過，通知最終結果
	                        notificationService.createNotificationBasisOnEmpno("出差最終審核結果通知", businessTrip.getEmployee().getEmpno());
	                    } else {
	                    	businessTrip.setFinalStatus("拒絕");
	                    	notificationService.createNotificationBasisOnEmpno("出差最終審核結果通知", businessTrip.getEmployee().getEmpno());
	                    }
	                }
	                break;

	            default:
	                throw new IllegalArgumentException("無法驗證目前審查階段: " + approvalLevel);
	        }

	        updateFinalStatus(businessTrip);
	        return businessTripRepository.save(businessTrip);
	    } else {
	        throw new IllegalArgumentException("請假單編號不存在 " + tripID);
	    }
	}
	
	
	//管理者的一鍵審核功能
	public void approveAllPendingBusinessTripByAdmin(String tripID) {
		BusinessTrip businessTrip = businessTripRepository.findById(tripID)
	            .orElseThrow(() -> new IllegalArgumentException("出差編號不存在 " + tripID));
		
		if ("待審查".equals(businessTrip.getApprovalNo1Status())) {
			businessTrip.setApprovalNo1Status("管理者已完成審核的更改");
			businessTrip.setApprovalNo1Date(new Date());
        }
        if ("待審查".equals(businessTrip.getApprovalNo2Status())) {
        	businessTrip.setApprovalNo2Status("管理者已完成審核的更改");
        	businessTrip.setApprovalNo2Date(new Date());
        }
        if ("待審查".equals(businessTrip.getApprovalNo3Status())) {
        	businessTrip.setApprovalNo3Status("管理者已完成審核的更改");
        	businessTrip.setApprovalNo3Date(new Date());
        }
        businessTrip.setFinalStatus("管理者已完成審核的更改");
        businessTripRepository.save(businessTrip);
    }
	
	//抓取歷史資料
	public List<BusinessTrip> getBusinessTripByFinalStatus() {
        return businessTripRepository.findBusinessTripByFinalStatus();
    }
	
	
	//抓取各層級的歷史資料
	public List<BusinessTrip> getBusinessTripByFinalStatusWithEmpno(String empno){
		return businessTripRepository.findBusinessTripByFinalStatusWithEmpno(empno);
	}
	
	
	//設定最終狀態
	private void updateFinalStatus(BusinessTrip businessTrip) {
		businessTrip.setFinalStatus(getFinalStatus(businessTrip));
	}
	
	private String getFinalStatus(BusinessTrip businessTrip) {
	    if ("拒絕".equals(businessTrip.getApprovalNo1Status())) {
	        return "拒絕";
	    }
	    if ("管理者已完成審核的更改".equals(businessTrip.getApprovalNo1Status()) ||
	            "管理者已完成審核的更改".equals(businessTrip.getApprovalNo2Status()) ||
	            "管理者已完成審核的更改".equals(businessTrip.getApprovalNo3Status())) {
	            return "管理者已完成審核的更改";
	       }
	    if ("通過".equals(businessTrip.getApprovalNo1Status()) && "待審查".equals(businessTrip.getApprovalNo2Status())) {
	        return "審查一完成";
	    }
	    if ("拒絕".equals(businessTrip.getApprovalNo2Status())) {
	        return "拒絕";
	    }
	    if ("通過".equals(businessTrip.getApprovalNo2Status()) && "待審查".equals(businessTrip.getApprovalNo3Status())) {
	        return "審查二完成";
	    }
	    if ("拒絕".equals(businessTrip.getApprovalNo3Status())) {
	        return "拒絕";
	    }
	    if ("通過".equals(businessTrip.getApprovalNo3Status())) {
	        return "最終通過";
	    }
	    return "待審查";
	}
	
	//查看單一資料
	public BusinessTrip getBusinessTrip(String tripID) {
		Optional<BusinessTrip> businessTripOptional = businessTripRepository.findById(tripID);
		
		if (businessTripOptional.isPresent()) {
				BusinessTrip businessTrip = businessTripOptional.get();
		        // 更新最終狀態
		        updateFinalStatus(businessTrip);
				return businessTrip;
			}
			return null;
		}
	
	//利用員工編號查找出差表單(一般層級員工)
	public List<BusinessTrip> getBusinessTripByEmpno(String empno) {
		 
		List<BusinessTrip> businessTrip = businessTripRepository.findByEmployeeEmpno(empno);
		 
		//更新每個請求的最終狀態
		businessTrip.forEach(this::updateFinalStatus);
	    return businessTrip;
	        		
	}
	
	//利用員工編號抓取審核人員
	public Approval getApprovalByEmpno(String empno){
		return approvalRepository.findByEmployee_Empno(empno);
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
	

	//新增請假資料
	public BusinessTrip insertBusinessTrip(BusinessTrip businessTrip) {
		return businessTripRepository.save(businessTrip);
	}
	
	//修改請假資料
	public BusinessTrip updateBusinessTrip(BusinessTrip businessTrip) {
		String tripID = businessTrip.getTripID();
		
		if(tripID != null) {
			return businessTripRepository.save(businessTrip);
		} return null;
	}
	
	//刪除請假資料
	public void deleteBusinessTrip(String tripID) {
		businessTripRepository.deleteById(tripID);
	}
	
	 //模糊查詢
	 public Page<BusinessTrip> searchBusinessTrips(String keyword, int page, int size) {
		    Pageable pageable = PageRequest.of(page - 1, size);
		    Page<BusinessTrip> results = businessTripRepository.searchAllFields(keyword, pageable);
		    results.forEach(this::updateFinalStatus);
		    return businessTripRepository.searchAllFields(keyword, pageable);
		}
	
	//審核請假單
	public Page<BusinessTrip> getBusinessTripsForManager(int page, int size, String empno) {
		        Pageable pageable = PageRequest.of(page - 1, size);
		        List<String> jobs = Arrays.asList("leader", "assistant");
		        return businessTripRepository.findByJobAndEmpnoIn(jobs, empno, pageable);
		    }

	public Page<BusinessTrip> getBusinessTripsForLeader(int page, int size, String empno) {
		        Pageable pageable = PageRequest.of(page - 1, size);
		        return businessTripRepository.findByJobAndEmpno("assistant", empno, pageable);
		    }

}
