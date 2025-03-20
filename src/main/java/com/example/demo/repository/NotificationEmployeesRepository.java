package com.example.demo.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.NotificationEmployees;

import jakarta.transaction.Transactional;

@Repository
public interface NotificationEmployeesRepository extends JpaRepository<NotificationEmployees, Integer> {

	List<NotificationEmployees> findByEmployeesEmpnoAndIsReadFalse(String empno);


	List<NotificationEmployees> findByEmployeesEmpno(String empno);
	
	@Modifying
    @Transactional
    @Query("UPDATE NotificationEmployees n SET n.isRead =true,n.readTime=:readTime WHERE n.id = :id")
	void updateRead(@Param("id") Integer id,@Param("readTime") Date readTime);

	@Modifying
	@Transactional
	@Query("UPDATE NotificationEmployees n SET n.isRead =true WHERE n.id = :id")
	void updateRead(@Param("id") Integer id);

	@Query("SELECT ne FROM NotificationEmployees ne WHERE ne.employees.empno = :empno AND ne.isRead = false")
	List<NotificationEmployees> findUnreadNotificationsByEmployee(@Param("empno") String empno);

	@Modifying
	@Transactional
	@Query("DELETE FROM NotificationEmployees ne WHERE ne.employees.empno = :empno")
	void deleteByempno(@Param("empno") String empno);
	
	@Modifying
    @Transactional
    @Query("UPDATE NotificationEmployees n SET n.isRead =true,n.readTime=:readTime WHERE n.employees.empno = :empno And n.readTime IS NULL")
	void updateRead(@Param("empno") String empno,@Param("readTime") Date readTime);
	
}
