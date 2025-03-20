package com.example.demo.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.dto.PostUpdatedRecordDto;
import com.example.demo.entity.Post;
import com.example.demo.entity.PostUpdateRecord;
import com.example.demo.repository.PostUpdateRecordRepository;

@Service
public class PostUpdateRecordService {
	
	@Autowired
	PostUpdateRecordRepository postUpdateRecordRepository;
	
	
	public List<PostUpdatedRecordDto> findAllUpdatedRecords(String postId) {
		
		List<PostUpdatedRecordDto> postUpdatedRecordDtos = new ArrayList<>();
		
		List<PostUpdateRecord> postUpdateRecords = postUpdateRecordRepository.findAllPostUpdateRecordsByPostId(postId);
		
		for (PostUpdateRecord postUpdateRecord : postUpdateRecords) {
			
        	//抓內容，並取前25個字
        	String beforeContent = postUpdateRecord.getContentBeforeUpdate();
        	String afterContent = postUpdateRecord.getContentAfterUpdate();
            
        	String beforeExcerpt;
        	String afterExcerpt;

            if (beforeContent.length() > 35) {
                beforeExcerpt = beforeContent.substring(0, 35) + "...";
            } else {
                beforeExcerpt = beforeContent;
            }
            
            if (afterContent.length() > 35) {
                afterExcerpt = afterContent.substring(0, 35) + "...";
            } else {
                afterExcerpt = afterContent;
            }
            
            //抓時間，並調整顯示樣式
            LocalDateTime postUpdateTime = postUpdateRecord.getPostUpdateTime();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String formattedDateTime = postUpdateTime.format(formatter); 
                    
            PostUpdatedRecordDto postUpdatedRecordDto = new PostUpdatedRecordDto(
            		postUpdateRecord.getPostUpdateRecordId(), 
            		formattedDateTime, 
            		beforeExcerpt, 
            		afterExcerpt, 
            		postUpdateRecord.getUpdateEmp(), 
            		postUpdateRecord.getUpdateEmpName(), 
            		postUpdateRecord.getUpdateRecordBelonsPostId(), 
            		postUpdateRecord.getIsImageUpdated());
            
            postUpdatedRecordDtos.add(postUpdatedRecordDto);
           
        }

		
		
		return postUpdatedRecordDtos;
	}
	

}
