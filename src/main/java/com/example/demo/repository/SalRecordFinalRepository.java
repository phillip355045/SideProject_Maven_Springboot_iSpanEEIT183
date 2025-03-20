package com.example.demo.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.dto.SalTotalDTO;
import com.example.demo.entity.LaborIhealthLevel;
import com.example.demo.entity.SalRecordBean;
import com.example.demo.entity.SalRecordFinal;

@Repository
public interface SalRecordFinalRepository extends JpaRepository<SalRecordFinal, Integer> {

	SalRecordFinal findBySalno(Integer salno);

//	@Modifying
//	@Query("UPDATE SalRecordFinal s SET s.status = :status WHERE s.salno = :salno")
//	int updateStatus(@Param("salno") Integer salno, @Param("status") String status);

	@Query("from SalRecordFinal")
	Page<SalRecordFinal> findAllWithPagination(Pageable pageable);

	@Query(value = "SELECT * FROM SalRecordFinal WHERE :col LIKE :colvalue", nativeQuery = true)
	List<SalRecordFinal> findByColumn(@Param("col") String col, @Param("colvalue") String colvalue);

	// 自動帶入增加
	@Query("SELECT e FROM SalRecordFinal e WHERE e.empno LIKE %:query% OR e.name LIKE %:query%")
	List<SalRecordFinal> getEmployeesByEmpnoAndNameFuzzySearch(@Param("query") String query);

	@Query("SELECT emp.empno FROM SalRecordFinal emp")
	List<String> findAllEmpnos();

	@Procedure(name = "insertSalRecordFinal")
	void insertSalRecordFinal(@Param("empno") String empno, @Param("year") String year, @Param("month") String month);

	List<SalRecordFinal> findByStatus(String status);

//	勞健保試算
	   @Query("SELECT l FROM LaborIhealthLevel l WHERE :salary BETWEEN l.minSalGetTotal AND l.maxSalGetTotal")
	    Optional<LaborIhealthLevel> findBySalary(@Param("salary") BigDecimal salary);
}
