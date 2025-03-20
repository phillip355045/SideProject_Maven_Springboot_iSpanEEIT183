package com.example.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.entity.Worklogs;

public interface WorklogsRepository extends JpaRepository<Worklogs, Long> {

	// 放在主頁的小框框 日期越接近的越前面 並且只顯示前三筆
	@Query("SELECT DISTINCT w FROM Worklogs w WHERE w.employees.empno = :empno ORDER BY w.worklogdate DESC")
	List<Worklogs> findByEmpnoWithItems(@Param("empno") String empno, Pageable pageable);

	// 查詢用
	@Query("SELECT w FROM Worklogs w LEFT JOIN FETCH w.worklogItems WHERE w.worklogid = :worklogid")
	Worklogs findByIdWithItems(@Param("worklogid") Long worklogId);

	// 過濾假刪除
	List<Worklogs> findByFakeDeleteIsNullOrFakeDeleteFalse();

	// 過濾假刪除 分頁
	Page<Worklogs> findByFakeDeleteIsNullOrFakeDeleteFalse(Pageable pageable);

	// 審核總表使用 過濾狀態 分頁
	 @Query("SELECT w FROM Worklogs w WHERE (w.fakeDelete IS NULL OR w.fakeDelete = '0') AND w.reviewstatus NOT IN ('已核准', '填寫中')")
    Page<Worklogs> findAllActiveWorklogs(Pageable pageable);
	
	// 歷史紀錄用 可以看到假刪除資料
	List<Worklogs> findByFakeDelete(String fakeDelete);
	
	// 歷史紀錄用 可以看到假刪除資料 分頁
	@Query("SELECT w FROM Worklogs w")
	Page<Worklogs> findAllWorklogs(Pageable pageable);

	// 抓取員工session 過濾假刪除
	@Query("SELECT w FROM Worklogs w WHERE w.employees.empno = :empno AND (w.fakeDelete IS NULL OR w.fakeDelete = '0')")
	Page<Worklogs> findByEmpnoAndNotFakeDeleted(@Param("empno") String empno, Pageable pageable);

	// 抓取員工編號對應日誌id
	Optional<Worklogs> findByEmployeesEmpnoAndWorklogid(String empno, Long worklogid);

	// User的模糊查詢
	@Query("SELECT w FROM Worklogs w LEFT JOIN w.employees e LEFT JOIN w.department d " + "WHERE e.empno = :empno AND ("
			+ "CAST(w.worklogid AS string) LIKE %:keyword% OR " + "d.deptno LIKE %:keyword% OR "
			+ "CAST(w.worklogdate AS string) LIKE %:keyword% OR " + "w.reviewstatus LIKE %:keyword%)"
			+ "AND (w.fakeDelete IS NULL OR w.fakeDelete = '0')")
	Page<Worklogs> UsersearchAllFields(@Param("empno") String empno, @Param("keyword") String keyword,
			Pageable pageable);

	// 審核者的模糊查詢
	@Query("SELECT w FROM Worklogs w LEFT JOIN w.employees e LEFT JOIN w.department d " + "WHERE (CAST(w.worklogid AS string) LIKE %:keyword% "
			+ "OR d.deptno LIKE %:keyword% " + "OR e.empno LIKE %:keyword% "
			+ "OR CAST(w.worklogdate AS string) LIKE %:keyword% " + "OR w.reviewstatus LIKE %:keyword%) "
			+ "AND (w.fakeDelete IS NULL OR w.fakeDelete = '0') " + "AND w.reviewstatus NOT IN ('已核准', '填寫中')")
	Page<Worklogs> ReviewsearchAllFields(@Param("keyword") String keyword, Pageable pageable);

	// 管理者的模糊查詢
	@Query("SELECT w FROM Worklogs w LEFT JOIN w.employees e LEFT JOIN w.department d " + "WHERE (CAST(w.worklogid AS string) LIKE %:keyword% "
			+ "OR d.deptno LIKE %:keyword% " + "OR e.empno LIKE %:keyword% "
			+ "OR CAST(w.worklogdate AS string) LIKE %:keyword% " + "OR w.reviewstatus LIKE %:keyword%) "
			+ "AND (w.fakeDelete IS NULL OR w.fakeDelete = '0')")
	Page<Worklogs> searchAllFields(@Param("keyword") String keyword, Pageable pageable);

	// 歷史紀錄的模糊查詢
	@Query("SELECT w FROM Worklogs w LEFT JOIN w.employees e LEFT JOIN w.department d " + "WHERE (CAST(w.worklogid AS string) LIKE %:keyword% "
			+ "OR d.deptno LIKE %:keyword% " + "OR e.empno LIKE %:keyword% "
			+ "OR CAST(w.worklogdate AS string) LIKE %:keyword% " + "OR w.reviewstatus LIKE %:keyword%)")
	Page<Worklogs> searchAll(@Param("keyword") String keyword, Pageable pageable);

}
