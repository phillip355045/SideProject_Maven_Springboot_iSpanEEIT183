package com.example.demo.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.embedded.PostImageBelongsRecordId;
import com.example.demo.entity.Post;
import com.example.demo.entity.PostImageBelongsRecord;
import com.example.demo.entity.PostImageUpload;
import com.example.demo.repository.PostImageBelongsRecordRepository;

@Service
public class PostImageBelongsRecordService {

	@Autowired
	PostImageBelongsRecordRepository postImageBelongsRecordRepository;
	
//	@Autowired
//	PostImageUploadService postImageUploadService;
	
//	貼文圖片上傳，同時寫入貼文圖片關聯紀錄
	public void createImageBelongsPostId(Post post, PostImageUpload postImageUpload) {
		
		//利用AllArgsConstructor建立複合主鍵
		PostImageBelongsRecordId postImageBelongsRecordId = new PostImageBelongsRecordId(post.getPostId(), postImageUpload.getPostImageId());
		
		//圖片上傳，同時收集資料，寫入貼文圖片關聯紀錄
		PostImageBelongsRecord postImageBelongsRecord = new PostImageBelongsRecord();
		
		postImageBelongsRecord.setPostImageBelongsRecordId(postImageBelongsRecordId);
		postImageBelongsRecord.setIsImageEnabled(true);
		postImageBelongsRecord.setPost(post);
		postImageBelongsRecord.setPostImageUpload(postImageUpload);
		
		postImageBelongsRecordRepository.save(postImageBelongsRecord);
		
	}
	
	
	
	
	public void addPostImageBelongsRecord(PostImageBelongsRecord postImageBelongsRecord) {
        postImageBelongsRecordRepository.save(postImageBelongsRecord);
    }
	
//	上架單一貼文指定圖片
	public void enablePostImageBelongs(String belongsPostId, String postImageId) {
		
		PostImageBelongsRecordId postImageBelongsRecordId = new PostImageBelongsRecordId(belongsPostId, postImageId);
		
        Optional<PostImageBelongsRecord> optional = postImageBelongsRecordRepository.findById(postImageBelongsRecordId);
        if (optional.isPresent()) {
            PostImageBelongsRecord postImageBelongsRecord = optional.get();
            postImageBelongsRecord.setIsImageEnabled(true);
            postImageBelongsRecordRepository.save(postImageBelongsRecord);
        }
    }
	
	
	
//	 下架單一貼文指定圖片
	 public void disablePostImageBelongs(String belongsPostId, String postImageId) {
		 
		 PostImageBelongsRecordId postImageBelongsRecordId = new PostImageBelongsRecordId(belongsPostId, postImageId);
		 
		 Optional<PostImageBelongsRecord> optional = postImageBelongsRecordRepository.findById(postImageBelongsRecordId);
		 if (optional.isPresent()) {
			 PostImageBelongsRecord postImageBelongsRecord = optional.get();
			 postImageBelongsRecord.setIsImageEnabled(false);
			 postImageBelongsRecordRepository.save(postImageBelongsRecord);
	        }
	    }
	 
	 
//	 刪除單一貼文指定圖片
	 public void deletePostImageBelongsRecord(String belongsPostId, String postImageId) {
		 
		 PostImageBelongsRecordId postImageBelongsRecordId = new PostImageBelongsRecordId(belongsPostId, postImageId);
		 
	     Optional<PostImageBelongsRecord> optional = postImageBelongsRecordRepository.findById(postImageBelongsRecordId);
	        
	     if (optional.isPresent()) {
	         PostImageBelongsRecord postImageBelongsRecord = optional.get();
	         postImageBelongsRecordRepository.delete(postImageBelongsRecord);
	         
//	         檢查該圖片於中介表是否還有其他關聯，若無則調用真刪除圖片方法
	         List<PostImageBelongsRecord> remainingRecords = postImageBelongsRecordRepository.findByPostImageBelongsRecordIdPostImageId(postImageId);
	         
//	         if (remainingRecords.isEmpty()) {
//				postImageUploadService.deletePostImage(postImageId);
//			}
	     }
	    }
}
