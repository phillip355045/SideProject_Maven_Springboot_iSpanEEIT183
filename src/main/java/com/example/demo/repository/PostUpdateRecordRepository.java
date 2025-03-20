package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.entity.PostUpdateRecord;

public interface PostUpdateRecordRepository extends JpaRepository<PostUpdateRecord, String> {
	
//	瀏覽"特定發文者"的所有文章
	@Query("SELECT pu FROM PostUpdateRecord pu WHERE pu.updateRecordBelonsPostId = :updateRecordBelonsPostId ORDER BY pu.postUpdateTime DESC")
	List<PostUpdateRecord> findAllPostUpdateRecordsByPostId(@Param("updateRecordBelonsPostId") String updateRecordBelonsPostId);

}
