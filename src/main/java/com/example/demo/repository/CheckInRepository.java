package com.example.demo.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.entity.CheckIn;
import com.example.demo.entity.CheckInId;

import jakarta.transaction.Transactional;

public interface CheckInRepository extends JpaRepository<CheckIn, CheckInId> {

	List<CheckIn> findByCheckInIdEmpnoAndCheckInIdDate(String empno, Date date);

	@Query("SELECT c FROM CheckIn c WHERE c.checkInId.empno = :empno AND c.checkInId.date = :date")
	List<CheckIn> findByEmpnoAndDate(@Param("empno") String empno, @Param("date") Date date);

	@Modifying
	@Query("UPDATE CheckIn c SET c.workoff = :workoff WHERE c.checkInId.empno = :empno AND c.checkInId.date = :date")
	void updateWorkOff(@Param("empno") String empno, @Param("workoff") Date workoff, @Param("date") Date date);

	@Query("SELECT c FROM CheckIn c WHERE c.checkInId.empno = :empno")
	List<CheckIn> findByEmpno(@Param("empno") String empno);
	
	@Query("SELECT c FROM CheckIn c WHERE c.checkInId.empno = :empno AND c.checkInId.date <= :date")
	List<CheckIn> findByEmpnoBeforeToday(@Param("empno") String empno,@Param("date") Date date);
	
	
	@Query("SELECT c FROM CheckIn c WHERE c.checkInId.empno = :empno AND c.checkInId.date >= :startDate AND c.checkInId.date < :endDate")
	List<CheckIn> empCheckInSearchByMonth(@Param("empno") String empno,@Param("startDate") Date startDate,@Param("endDate") Date endDate);
	
	@Modifying
	@Transactional
	@Query("DELETE CheckIn where checkInId.empno = :empno AND checkInId.date <= :date")
	void deleteByempno(@Param("empno") String empno,@Param("date") Date date);
}
