package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.entity.Post;
import com.example.demo.entity.PostImageUpload;

import jakarta.transaction.Transactional;

public interface PostImageUploadRepository extends JpaRepository<PostImageUpload, String> {
	
//	---------------------前台----------------------
//	展示"貼文所屬"且"上架"的所有貼文圖片
	@Query("SELECT p FROM PostImageUpload p JOIN PostImageBelongsRecord r ON p.postImageId = r.postImageBelongsRecordId.belongsImageId WHERE r.postImageBelongsRecordId.belongsPostId = :postId AND r.isImageEnabled = true")
	List<PostImageUpload> findAllPostImagesByPostIdAndStatus(@Param("postId") String postId);
	
	
	
//	---------------------後台----------------------
	
//	無條件瀏覽貼文所屬所有圖片(過濾下架的貼文圖片)
	@Query("SELECT p FROM PostImageUpload p JOIN PostImageBelongsRecord r ON p.postImageId = r.postImageBelongsRecordId.belongsImageId WHERE r.postImageBelongsRecordId.belongsPostId = :postId")
	List<PostImageUpload> findAllPostImages(@Param("postId") String postId);
	
	
//	---------------------分隔線----------------------
//	貼文圖片下架(假刪除)
	@Transactional
	@Modifying
	@Query("UPDATE PostImageUpload iu SET iu.postImageStatus = false WHERE iu.postImageId = :postImageId")
	void softDeletePostImage(@Param("postImageId") String postImageId);
	
//	貼文圖片上架
	@Transactional
	@Modifying
	@Query("UPDATE PostImageUpload iu SET iu.postImageStatus = true WHERE iu.postImageId = :postImageId")
	void browsePostImage(@Param("postImageId") String postImageId);

	
	
	

}
