package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.embedded.PostImageBelongsRecordId;
import com.example.demo.entity.PostImageBelongsRecord;

public interface PostImageBelongsRecordRepository
		extends JpaRepository<PostImageBelongsRecord, PostImageBelongsRecordId> {
	
//	查詢圖片是否仍有中介表關聯紀錄
	@Query("SELECT p FROM PostImageBelongsRecord p WHERE p.postImageBelongsRecordId.belongsImageId = :belongsImageId")
    List<PostImageBelongsRecord> findByPostImageBelongsRecordIdPostImageId(@Param("belongsImageId") String belongsImageId);

}
