package com.example.demo.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.entity.Employees;

import jakarta.transaction.Transactional;

public interface EmployeesRepository extends JpaRepository<Employees, String> {

	Employees findByEmpno(String empno);

	@Query("from Employees")
	Page<Employees> findLatest(Pageable pgb);

//	@Query("FROM Employees e WHERE e.:col LIKE %:colvalue%")
//    List<Employees> findByColumnLike(@Param("col") String col, @Param("colvalue") String colvalue);

	@Query("from Employees order by empno desc")
	List<Employees> findFirstByOrderByEmpnoDesc();
	
	@Modifying
    @Transactional
    @Query("UPDATE Employees e SET e.lastTimeLogin = :lastTimeLogin WHERE e.empno = :empno")
    void logoutUpdate(@Param("empno") String empno, @Param("lastTimeLogin") Date lastTimeLogin);

	@Query("SELECT empno FROM Employees")
    List<String> findAllEmployeeIds();
	
	//Louis增加
    @Query("SELECT e FROM Employees e WHERE e.empno LIKE %:query% OR e.name LIKE %:query%")
    List<Employees> getEmployeesByEmpnoAndNameFuzzySearch(@Param("query") String query);
     
    @Query("SELECT e FROM Employees e WHERE e.deptno = :deptno AND e.job = 'manager'")
    Employees findFirstByDeptnoAndJob(@Param("deptno") String deptno);
    
    
    @Modifying
    @Transactional
    @Query("UPDATE Employees e SET e.deptno = :deptno, e.job = :job, e.deptTransferID = :deptTransferID, e.mgr = :mgr WHERE e.empno = :empno")
    void deptTransfer(@Param("empno") String empno, @Param("deptno") String deptno, @Param("deptTransferID") String deptTransferID, @Param("job") String job,@Param("mgr") String mgr);

    
    @Query("SELECT e FROM Employees e WHERE e.empno = :empno AND e.phone = :phone AND e.identityID = :identityID") 
    Employees forgotpassword(@Param("empno") String empno,@Param("phone") String phone,@Param("identityID") String identityID);
    
    
    @Modifying
    @Transactional
    @Query("UPDATE Employees e SET e.password = :password WHERE e.empno = :empno")
    void updatepassword(@Param("empno") String empno,@Param("password") String password);
  
    List<Employees> findByEmpnoContainingOrNameContaining(String empno, String name);

    List<Employees> findByDeptno(String deptno);
    
    
}
