package com.example.demo.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.example.demo.entity.RoomReserve;

public interface RoomReserveRepository extends JpaRepository<RoomReserve, Integer>, JpaSpecificationExecutor<RoomReserve>  {
		
	
	//後台的模糊查詢
	@Query("from RoomReserve where reserveTitle like %?1%")
	List<RoomReserve> findByReserveTitle(String reserveTitle);
	
	//ok
	@Query("from RoomReserve where roomName = ?1")
	List<RoomReserve> findReservaion(String name);
	
	@Query(value="SELECT r.roomName, COUNT(*) as 'count' FROM roomReserve r WHERE r.status = '放行' GROUP BY r.roomName", nativeQuery = true)
	List<Object[]> findRoomReserveCount();
		
	//----------前台的查詢----------
	
	//查詢預約的資料for會議回饋
	@Query("from RoomReserve where empno= ?1")
	List<RoomReserve> findReservaionByEmpno(String empno);
	
	//被datatable取代
	//之後要加入員工編號來判斷
//	@Query("from RoomReserve where fakeDelete = ?1 and empno= ?2")
//	Page<RoomReserve> findReservaionByEmpnoAndFakeDeletePage(String fakeDelet, String empno, Pageable pageable);

	@Query("from RoomReserve where fakeDelete = ?1 and empno= ?2")
	List<RoomReserve> findReservaionByEmpnoAndFakeDelete(String fakeDelet, String empno);
	
	@Query("SELECT r FROM RoomReserve r WHERE r.reserveTitle LIKE %:query%"
			+ " OR r.roomName LIKE %:query%"
			+ " OR r.empno LIKE %:query%"
			+ " OR r.status LIKE %:query%")
	  Page<RoomReserve> findRoomReserveByFuzzyPage(
	  @Param("query") String query,
	  Pageable pageable);
	

//	@Query("from RoomReserve where reserveDate between ?1 and ?2")
//	Page<RoomReserve> findReservationByEmpnoAndFakeDeleteAndDateRange(Date startDate, Date endDate, Pageable pageable);
	
	//Louis增加
//    @Query("SELECT e FROM Employees e WHERE e.empno LIKE %:query% OR e.name LIKE %:query%")
//    List<Employees> getEmployeesByEmpnoAndNameFuzzySearch(@Param("query") String query);
}
