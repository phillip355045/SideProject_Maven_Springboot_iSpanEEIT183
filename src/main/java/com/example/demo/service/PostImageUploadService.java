package com.example.demo.service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Base64Utils;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.embedded.PostImageBelongsRecordId;
import com.example.demo.entity.Post;
import com.example.demo.entity.PostImageBelongsRecord;
import com.example.demo.entity.PostImageUpload;
import com.example.demo.repository.PostImageBelongsRecordRepository;
import com.example.demo.repository.PostImageUploadRepository;
import com.example.demo.repository.PostRepository;

import jakarta.persistence.EntityNotFoundException;

//圖片上傳邏輯: 新增、刪除、展示所有、隱藏/顯示貼文圖片

@Service
public class PostImageUploadService {
	
	@Autowired
	PostImageUploadRepository postImageUploadRepository;
	
//	--------------------------------------------------------------------------------
	@Autowired
	PostImageBelongsRecordRepository postImageBelongsRecordRepository;
	
	@Autowired
	PostImageBelongsRecordService postImageBelongsRecordService;
//	--------------------------------------------------------------------------------
	
	@Autowired
	PostRepository postRepository;
	
	private final String storagePath = "src/main/resources/static/images/post/";
//	private final String storagePath = "C:\\SpringBoot\\workspace\\SprinBoot3Demo\\src\\main\\resources\\static\\images\\post\\";
	
	
//	用byte[]64來上傳圖檔
    public void uploadImagesByByte(String postId, List<MultipartFile> images) throws IOException {
        
    	List<PostImageUpload> imageUploads = new ArrayList<>();
    	
    	Post post = null;
    	Optional<Post> optional = postRepository.findById(postId);
    	if (optional.isPresent()) {
			post = optional.get();
		}
    	
    	for (MultipartFile file : images) {
    		
            PostImageUpload postImageUpload = new PostImageUpload();
            
            if (file.getOriginalFilename() != null && !file.getOriginalFilename().isEmpty()) {
				
            	postImageUpload.setPostImageUri(file.getOriginalFilename());
            	postImageUpload.setPostImageDataType(file.getContentType());
            	
            	// 將圖片文件轉換為 Base64 字符串
            	String base64Image = Base64.getEncoder().encodeToString(file.getBytes());
            	postImageUpload.setPostImageData(base64Image.getBytes());
            	
            	postImageUpload.setPostImageUploadTime(LocalDateTime.now());
            	postImageUpload.setPostImageStatus(true);
            	
            	imageUploads.add(postImageUpload);
            	
            	postImageUploadRepository.save(postImageUpload);
            	
            	postImageBelongsRecordService.createImageBelongsPostId(post, postImageUpload);
			}
            
        }

        // 一次保存所有圖片，但因為我每筆上傳圖片都要記錄屬於哪篇貼文，所以暫時無法使用
        //postImageUploadRepository.saveAll(imageUploads);
    }
	
    public void deleteImageByImageId(String postImageId) {
        Optional<PostImageUpload> optionalImage = postImageUploadRepository.findById(postImageId);

        if (optionalImage.isPresent()) {
            PostImageUpload postImageUpload = optionalImage.get();

            // 刪除與此圖片相關的所有PostImageBelongsRecord記錄
            List<PostImageBelongsRecord> records = postImageBelongsRecordRepository.findByPostImageBelongsRecordIdPostImageId(postImageId);
            for (PostImageBelongsRecord record : records) {
                postImageBelongsRecordRepository.delete(record);
            }

            // 刪除圖片記錄
            postImageUploadRepository.delete(postImageUpload);
        } else {
            throw new EntityNotFoundException("Image not found with id: " + postImageId);
        }
    }
	
//	------------------------------------------------------------------------------------------------------------------
	
	public String uploadImages(String belongsPostId, List<MultipartFile> images) throws IOException {
		
		for(MultipartFile image : images) {
			
			// 從 MultipartFile 獲取文件名
			String fileName = image.getOriginalFilename();
			
			System.out.println("Processing file: " + fileName);
			
			// 確保文件名存在
			if (fileName != null && !fileName.isEmpty()) {
				
				//指定我要的儲存路徑
				Path path = Paths.get(storagePath + fileName);
				
				System.out.println(path.toString());
				
				// 從 MultipartFile 獲取輸入流
				InputStream inputStream = image.getInputStream();
				
				// 使用 Java NIO 的 Files.copy 方法將輸入流的內容複製到指定路徑
				Files.copy(inputStream, path, StandardCopyOption.REPLACE_EXISTING);
				
				
				System.out.println("File " + fileName + " saved to " + path.toString());
				
				//----------------------------------------------------------
				LocalDateTime uploadTime = LocalDateTime.now();
				
				PostImageUpload postImageUpload = new PostImageUpload();
				postImageUpload.setPostImageUri(fileName);
				postImageUpload.setPostImageUploadTime(uploadTime);
				postImageUpload.setPostImageDataType(image.getContentType());
				
				postImageUploadRepository.save(postImageUpload);
				//-----------------------------------------------------------
				
				//創建嵌入複合主鍵的class來取得belongsPostId、belongsImageId包起來等等要存進去
				PostImageBelongsRecordId postImageBelongsRecordId = new PostImageBelongsRecordId(belongsPostId, postImageUpload.getPostImageId());
				
				PostImageBelongsRecord postImageBelongsRecord = new PostImageBelongsRecord();
				
				postImageBelongsRecord.setPostImageBelongsRecordId(postImageBelongsRecordId);
				postImageBelongsRecord.setPost(new Post(belongsPostId));
				postImageBelongsRecord.setPostImageUpload(postImageUpload);
				
				postImageBelongsRecordRepository.save(postImageBelongsRecord);
				
				return path.toString();
				
			} else {
                System.out.println("File name is null or empty for one of the uploaded files.");
                
                return "File name is null or empty for one of the uploaded files.";
            }
		}
		return "File name is null or empty for one of the uploaded files.";
		
	}
	
//	刪除上傳的圖片
	public void deletePostImage(String postImageId) {
		
		PostImageUpload postImageUpload = null;
		
		Optional<PostImageUpload> optional = postImageUploadRepository.findById(postImageId);
		
		if (optional.isPresent()) {
			postImageUpload = optional.get();
		}
		
		if (postImageId != null) {
			
			//刪除要考慮兩個部分: 實體檔案、資料庫內資料
			String fileName = postImageUpload.getPostImageUri();
			Path path = Paths.get(storagePath + fileName);
			
			try {
				//調用java.nio的API來刪除路徑中儲存的實體檔案
				Files.deleteIfExists(path);
				
				//刪除資料庫內的檔案詳細資訊
				postImageUploadRepository.deleteById(postImageId);
				
				System.out.println("File " + fileName + " and its record have been deleted successfully.");
				
			} catch (IOException e) {
				
				e.printStackTrace();
				System.out.println("Failed to delete file " + fileName);
				
			}
			
		} else {
			System.out.println("PostImageUpload with ID " + postImageId + " not found.");	
		}
		
	}
	
//	貼文圖片下架(假刪除)
	public void softDeletePostImage(String postImageId) {
		
		postImageUploadRepository.softDeletePostImage(postImageId);
		
	}
	
//	貼文圖片上架
	public void browsePostImage(String postImageId) {
		
		postImageUploadRepository.browsePostImage(postImageId);
		
	}
	
//	---------------------前台----------------------
//	展示"貼文所屬"、"可瀏覽"所有圖片
	public List<PostImageUpload> findAllActivePostImages(String postId) {
		
		List<PostImageUpload> images = postImageUploadRepository.findAllPostImagesByPostIdAndStatus(postId);
		
		return images;
	}
	
//	---------------------後台----------------------
//	無條件展示"貼文所屬"所有圖片
	public List<PostImageUpload> findAllPostImages(String postId) {
		
		List<PostImageUpload> images = postImageUploadRepository.findAllPostImages(postId);
		
		return images;
	}
	
//	---------------------分隔線----------------------
	
	
	
	

}


