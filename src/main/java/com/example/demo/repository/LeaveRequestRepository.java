package com.example.demo.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.entity.LeaveRequest;

//使用 Specification 來實現動態查詢
public interface LeaveRequestRepository extends JpaRepository<LeaveRequest,String> , JpaSpecificationExecutor<LeaveRequest>{
	
	 
//    // 通過 requestTypeName 查詢
//    @Query("from LeaveRequest lr where lr.leaveType.requestTypeName = :value")
//    List<LeaveRequest> findByLeaveTypeRequestTypeName(@Param("value") String value);
//    
//    // 通過其他欄位查詢
//    @Query("from LeaveRequest lr where lr.:field = :value")
//    List<LeaveRequest> findByField(@Param("field") String field, @Param("value") String value);
	
	@Query("SELECT lr FROM LeaveRequest lr " +
		       "WHERE lr.employee.empno LIKE %:keyword% " +
		       "OR lr.reason LIKE %:keyword% " +
		       "OR lr.delegateID LIKE %:keyword% " +
		       "OR lr.approvalNo1ID LIKE %:keyword% " +
		       "OR lr.approvalNo2ID LIKE %:keyword% " +
		       "OR lr.approvalNo3ID LIKE %:keyword% " +
		       "OR lr.documents LIKE %:keyword% " +
		       "OR lr.leaveType.requestTypeName LIKE %:keyword% " +
		       "OR lr.finalStatus LIKE %:keyword%")
		Page<LeaveRequest> searchAllFields(@Param("keyword") String keyword, Pageable pageable);
	
	Page<LeaveRequest> findByEmployeeEmpno(Pageable pageable , String empno);
	List<LeaveRequest> findByEmployeeEmpno(String empno);
	
	//審核篩選
	@Query("SELECT lr FROM LeaveRequest lr WHERE lr.employee.job IN :jobs AND lr.employee.empno = :empno")
	Page<LeaveRequest> findByJobAndEmpnoIn(@Param("jobs") List<String> jobs, @Param("empno") String empno, Pageable pageable);

	@Query("SELECT lr FROM LeaveRequest lr WHERE lr.employee.job = :job AND lr.employee.empno = :empno")
	Page<LeaveRequest> findByJobAndEmpno(@Param("job") String job, @Param("empno") String empno, Pageable pageable);
	
	//在請假表單中的請假者必須與審查表單中的empno相同(才能查出那個人有哪些審查人員)
	@Query("SELECT lr FROM LeaveRequest lr WHERE " +
	           "(lr.approvalNo1ID = :empno AND lr.approvalNo1Status = '待審查') " +
	           "OR (lr.approvalNo2ID = :empno AND lr.approvalNo1Status = '通過' AND lr.approvalNo2Status = '待審查') " +
	           "OR (lr.approvalNo3ID = :empno AND lr.approvalNo2Status = '通過' AND lr.approvalNo3Status = '待審查')" +
				"AND lr.approvalNo1Status <> '管理者已完成審核的更改' " +
			    "AND lr.approvalNo2Status <> '管理者已完成審核的更改' " +
			    "AND lr.approvalNo3Status <> '管理者已完成審核的更改'")
	List<LeaveRequest> findLeaveRequestsForApproval(@Param("empno") String empno);
	
	//管理者的查詢所有審查表單(不包括狀態為最終通過和管理者已完成審核的更改)
	@Query("SELECT lr FROM LeaveRequest lr WHERE lr.finalStatus <> '最終通過' AND lr.finalStatus <> '管理者已完成審核的更改'")
	List<LeaveRequest> findALLLeaveRequestsByFinalStatus();
	
	//管理者使用一鍵審核
	@Query("SELECT lr FROM LeaveRequest lr WHERE lr.finalStatus <> '最終通過' AND " +
	           "(lr.approvalNo1Status = '待審查' OR lr.approvalNo2Status = '待審查' OR lr.approvalNo3Status = '待審查')")
	    List<LeaveRequest> findPendingRequestsForAdmin();
	    
	//抓取歷史資料(管理者)
	@Query("SELECT lr FROM LeaveRequest lr WHERE lr.finalStatus = '最終通過' OR lr.finalStatus = '管理者已完成審核的更改'")
	List<LeaveRequest> findLeaveRequestsByFinalStatus();
	
	//抓取歷史資料(各層級)
	@Query("SELECT lr FROM LeaveRequest lr " +
	           "WHERE lr.finalStatus = '最終通過' " +
	           "OR lr.finalStatus = '管理者已完成審核的更改' " +
	           "AND (lr.approvalNo1ID = :empno OR lr.approvalNo2ID = :empno OR lr.approvalNo3ID = :empno)")
	List<LeaveRequest> findLeaveRequestsByFinalStatusWithEmpno(String empno);
	
	//以月分抓取員工資料
	@Query("SELECT lr FROM LeaveRequest lr " +
	           "WHERE FUNCTION('MONTH', lr.startTime) = FUNCTION('MONTH', CURRENT_DATE) " +
	           "AND FUNCTION('YEAR', lr.startTime) = FUNCTION('YEAR', CURRENT_DATE)")
	List<LeaveRequest> findALLLeaveRequestsByMonth();

}
