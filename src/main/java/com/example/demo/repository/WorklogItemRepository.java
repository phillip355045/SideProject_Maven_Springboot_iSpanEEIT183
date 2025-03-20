package com.example.demo.repository;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.entity.WorklogItem;
import com.example.demo.entity.Worklogs;

public interface WorklogItemRepository extends JpaRepository<WorklogItem, Long>{
    List<WorklogItem> findByFakeDelete(String fakeDelete);
//    List<WorklogItem> findByWorklogId(Long worklogId);
    
 // 取得各單位做預算人力的日期條件
//    List<WorklogItem> findAllByWorklogs_DeptnoAndWorklogs_WorklogdateBetween(String deptno, LocalDate startDate, LocalDate endDate);

    List<WorklogItem> findByWorklogsIn(List<Worklogs> worklogs);
    
//    @Query("SELECT w FROM WorklogItem w WHERE w.worklogs.deptno = :deptno AND w.worklogs.worklogdate BETWEEN :startDate AND :endDate")
//    List<WorklogItem> findItemsByDeptnoAndDateRange(@Param("deptno") String deptno, @Param("startDate") Date startDate, @Param("endDate") Date endDate);

//    @Query("SELECT wi FROM WorklogItem wi JOIN wi.worklogs w JOIN w.department d " + "WHERE d.deptno = :deptno AND w.worklogdate BETWEEN :startDate AND :endDate")
//     List<WorklogItem> findItemsByDeptnoAndDateRange(@Param("deptno") String deptno,
//                                                            @Param("startDate") Date startDate,
//                                                            @Param("endDate") Date endDate);
    
}
