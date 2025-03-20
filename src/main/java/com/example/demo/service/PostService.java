package com.example.demo.service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.dto.PostDto;
import com.example.demo.dto.PostImageUpdatedDto;
import com.example.demo.dto.PostWallDto;
import com.example.demo.embedded.PostImageBelongsRecordId;
import com.example.demo.embedded.PostImageUpdateRecordId;
import com.example.demo.entity.Comment;
import com.example.demo.entity.Employees;
import com.example.demo.entity.Post;
import com.example.demo.entity.PostImageBelongsRecord;
import com.example.demo.entity.PostImageUpdateRecord;
import com.example.demo.entity.PostImageUpload;
import com.example.demo.entity.PostUpdateRecord;
import com.example.demo.repository.EmployeesRepository;
import com.example.demo.repository.PostImageBelongsRecordRepository;
import com.example.demo.repository.PostImageUpdateRecordRepository;
import com.example.demo.repository.PostImageUploadRepository;
import com.example.demo.repository.PostRepository;
import com.example.demo.repository.PostUpdateRecordRepository;

import jakarta.transaction.Transactional;

@Service
public class PostService {
	
	@Autowired
	PostRepository postRepository;
	
	@Autowired
	PostUpdateRecordRepository postUpdateRecordRepository;
	
	@Autowired
	PostImageUpdateRecordRepository postImageUpdateRecordRepository;
	
	@Autowired
	PostImageUploadRepository postImageUploadRepository;
	
	@Autowired
	PostImageBelongsRecordRepository postImageBelongsRecordRepository;
	
	@Autowired
	PostImageBelongsRecordService postImageBelongsRecordService;
	
	@Autowired
	PostImageUploadService postImageUploadService;
	
	@Autowired
	ReactionService reactionService;
	
	@Autowired
	CommentService commentService;
	
    @Autowired
    EmployeesRepository employeesRepository;
	
//	---------------------前台----------------------
	
//	展示HomePage公告(限定'活動','考勤','薪資','福利')
	
	public List<PostDto> findAllPostsOnHomePage() {
		
		Sort sort = Sort.by(Sort.Direction.DESC, "postDate");
		Pageable pageable = PageRequest.of(0, 6, sort); 
		Page<Post> postsPage = postRepository.findAllActivePosts(pageable);
		List<Post> posts = postsPage.getContent();
        List<PostDto> postExcerpts = new ArrayList<>();
        
        for (Post post : posts) {
            
        	String title = post.getPostTitle();
        	
            String category = post.getPostCategory();
            
			LocalDateTime postDateTime = post.getPostDate();
			LocalDateTime postExpiryDateTime = post.getPostExpiryDate();
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
			String formattedDateTime = postDateTime.format(formatter);
			String formattedExpiryDateTime = postExpiryDateTime.format(formatter);
			
			PostDto postExcerpt = new PostDto(post.getPostId(), formattedDateTime, formattedExpiryDateTime, post.getPostStatus(), title, category, post.getPostContent(), post.getPostEmp(),post.getPostEmpName());
            postExcerpts.add(postExcerpt);
        }

        return postExcerpts;
        
	}
	
	
//	展示貼文牆貼文
	public List<PostWallDto> findAllPostsOnWall() {
		
		Sort sort = Sort.by(Sort.Direction.DESC, "postDate");
		List<Post> posts = postRepository.findAllActivePosts(sort);
        List<PostWallDto> postExcerpts = new ArrayList<>();
        
        List<String> empnos = posts.stream()
        		.map(Post::getPostEmp)
        		.distinct()
        		.collect(Collectors.toList());
        
        // 一次性查詢所有工號對應的員工姓名
        List<Employees> employees = employeesRepository.findAllById(empnos);
        Map<String, String> empnoToNameMap = employees.stream()
        		.collect(Collectors.toMap(Employees::getEmpno, Employees::getName));

        for (Post post : posts) {
            
        	String empno = post.getPostEmp();
			String name = empnoToNameMap.get(empno);
			post.setPostEmpName(name);
        	
			String postId = post.getPostId();
			Integer likeCounts = reactionService.getLikeCounts(postId);
			Integer commentCounts = commentService.getCommentCounts(postId);
			
        	
        	//抓內容，並取前25個字
        	String content = post.getPostContent();
            String excerpt;

            if (content.length() > 25) {
                excerpt = content.substring(0, 25) + "...";
            } else {
                excerpt = content;
            }
            
            //抓時間，並調整顯示樣式
            LocalDateTime postDateTime = post.getPostDate();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            String formattedDateTime = postDateTime.format(formatter); 
                    
            
            PostWallDto postExcerpt = new PostWallDto(post.getPostId(), formattedDateTime, post.getPostTitle(), post.getPostCategory(), excerpt, post.getPostEmp(), post.getPostEmpName(),likeCounts,commentCounts);
            postExcerpts.add(postExcerpt);
        }

        return postExcerpts;
    }
	
//	展示貼文牆搜尋條件篩選後貼文
	public List<PostWallDto> findAllPostsOnWallByKeywords(String keyword1, String keyword2) {
		
		List<Post> posts = postRepository.findPostsByKeywords(keyword1, keyword2);
        List<PostWallDto> postExcerpts = new ArrayList<>();
        
        List<String> empnos = posts.stream()
        		.map(Post::getPostEmp)
        		.distinct()
        		.collect(Collectors.toList());
        
        // 一次性查詢所有工號對應的員工姓名
        List<Employees> employees = employeesRepository.findAllById(empnos);
        Map<String, String> empnoToNameMap = employees.stream()
        		.collect(Collectors.toMap(Employees::getEmpno, Employees::getName));
        

        for (Post post : posts) {
        	
        	String empno = post.getPostEmp();
			String name = empnoToNameMap.get(empno);
			post.setPostEmpName(name);
			
			String postId = post.getPostId();
			Integer likeCounts = reactionService.getLikeCounts(postId);
			Integer commentCounts = commentService.getCommentCounts(postId);
            
        	//抓內容，並取前25個字
        	String content = post.getPostContent();
            String excerpt;

            if (content.length() > 25) {
                excerpt = content.substring(0, 25) + "...";
            } else {
                excerpt = content;
            }
            
            //抓時間，並調整顯示樣式
            LocalDateTime postDateTime = post.getPostDate();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            String formattedDateTime = postDateTime.format(formatter); 
            
            PostWallDto postExcerpt = new PostWallDto(post.getPostId(), formattedDateTime, post.getPostTitle(), post.getPostCategory(), excerpt, post.getPostEmp(), post.getPostEmpName(),likeCounts,commentCounts);
            postExcerpts.add(postExcerpt);
        }

        return postExcerpts;
    }
	
//	展示"發文者"+"貼文分類"所有貼文
	public List<PostDto> findAllPostsByPostEmpAndPostCategory(String postEmp, String postCategory) {

		List<Post> posts = postRepository.findAllPostsByPostEmpAndPostCategory(postEmp, postCategory);
		List<PostDto> postsDtos = new ArrayList<>();
		List<String> empnos = posts.stream().map(Post::getPostEmp).distinct().collect(Collectors.toList());

		// 一次性查詢所有工號對應的員工姓名
		List<Employees> employees = employeesRepository.findAllById(empnos);
		Map<String, String> empnoToNameMap = employees.stream()
				.collect(Collectors.toMap(Employees::getEmpno, Employees::getName));

		for (Post post : posts) {

			String empno = post.getPostEmp();
			String name = empnoToNameMap.get(empno);
			post.setPostEmpName(name);
			
			//抓標題，並取前10個字
        	String title = post.getPostTitle();
            String titleExcerpt;

            if (title.length() > 10) {
            	titleExcerpt = title.substring(0, 10) + "...";
            } else {
            	titleExcerpt = title;
            }

			LocalDateTime postDateTime = post.getPostDate();
			LocalDateTime postExpiryDateTime = post.getPostExpiryDate();
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
			String formattedDateTime = postDateTime.format(formatter);
			String formattedExpiryDateTime = postExpiryDateTime.format(formatter);

			PostDto postDto = new PostDto(post.getPostId(), formattedDateTime, formattedExpiryDateTime,
					post.getPostStatus(), titleExcerpt, post.getPostCategory(), post.getPostContent(),
					post.getPostEmp(), post.getPostEmpName());
			postsDtos.add(postDto);

		}
		return postsDtos;
	}
	
//	展示"發文者"所有貼文
	public List<PostDto> findAllPostsByPostEmp(String postEmp) {
		
		List<Post> posts = postRepository.findAllPostsByPostEmp(postEmp);
		List<PostDto> postsDtos = new ArrayList<>();
		
		List<String> empnos = posts.stream()
	        		.map(Post::getPostEmp)
	        		.distinct()
	        		.collect(Collectors.toList());
	        
	        // 一次性查詢所有工號對應的員工姓名
	    List<Employees> employees = employeesRepository.findAllById(empnos);
	    Map<String, String> empnoToNameMap = employees.stream().collect(Collectors.toMap(Employees::getEmpno, Employees::getName));
		
		for (Post post : posts) {
			
			String empno = post.getPostEmp();
			String name = empnoToNameMap.get(empno);
			post.setPostEmpName(name);
			
			//抓標題，並取前10個字
        	String title = post.getPostTitle();
            String titleExcerpt;

            if (title.length() > 10) {
            	titleExcerpt = title.substring(0, 10) + "...";
            } else {
            	titleExcerpt = title;
            }
			
			
			
			LocalDateTime postDateTime = post.getPostDate();
			LocalDateTime postExpiryDateTime = post.getPostExpiryDate();
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
			String formattedDateTime = postDateTime.format(formatter);
			String formattedExpiryDateTime = postExpiryDateTime.format(formatter);
			
			PostDto postDto = new PostDto(post.getPostId(), formattedDateTime, formattedExpiryDateTime, post.getPostStatus(), titleExcerpt, post.getPostCategory(), post.getPostContent(), post.getPostEmp(),post.getPostEmpName());	
			postsDtos.add(postDto);
			
		}
		return postsDtos;
	}
	
//	用id查詢指定貼文
	public PostDto findPostById(String postId) {
		
		Optional<Post> optional = postRepository.findById(postId);
		
		if (optional.isPresent()) {
			
			Post post = optional.get();
			
			String empno = post.getPostEmp();
			Employees employees = null;
			Optional<Employees> optionalEmp = employeesRepository.findById(empno);
			
			if(optionalEmp.isPresent()){
				employees = optionalEmp.get();
			}
			
			String postEmpName = employees.getName();
			post.setPostEmpName(postEmpName);
			
			LocalDateTime postDateTime = post.getPostDate();
			LocalDateTime postExpiryDateTime = post.getPostExpiryDate();
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
			String formattedDateTime = postDateTime.format(formatter);
			String formattedExpiryDateTime = postExpiryDateTime.format(formatter);
			
			PostDto postDto = new PostDto(post.getPostId(), formattedDateTime, formattedExpiryDateTime, post.getPostStatus(), post.getPostTitle(), post.getPostCategory(), post.getPostContent(), post.getPostEmp(),post.getPostEmpName());
			return postDto;
		}
		
		return null;
	}
	
	
//	---------------------後台----------------------
//	無條件展示所有貼文
	public List<PostDto> findAllPosts() {
		
		List<Post> posts = postRepository.findAll();
		
		List<PostDto> postsDtos = new ArrayList<>();
		
		List<String> empnos = posts.stream().map(Post::getPostEmp).distinct().collect(Collectors.toList());

		// 一次性查詢所有工號對應的員工姓名
		List<Employees> employees = employeesRepository.findAllById(empnos);
		Map<String, String> empnoToNameMap = employees.stream()
				.collect(Collectors.toMap(Employees::getEmpno, Employees::getName));

		for (Post post : posts) {

			String empno = post.getPostEmp();
			String name = empnoToNameMap.get(empno);
			post.setPostEmpName(name);
			
			//抓標題，並取前10個字
        	String title = post.getPostTitle();
            String titleExcerpt;

            if (title.length() > 10) {
            	titleExcerpt = title.substring(0, 10) + "...";
            } else {
            	titleExcerpt = title;
            }
			
			LocalDateTime postDateTime = post.getPostDate();
			LocalDateTime postExpiryDateTime = post.getPostExpiryDate();
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
			String formattedDateTime = postDateTime.format(formatter);
			String formattedExpiryDateTime = postExpiryDateTime.format(formatter);
			
			PostDto postDto = new PostDto(post.getPostId(), formattedDateTime, formattedExpiryDateTime, post.getPostStatus(), titleExcerpt, post.getPostCategory(), post.getPostContent(), post.getPostEmp(),post.getPostEmpName());
			postsDtos.add(postDto);
			
		}
		
		return postsDtos;
		
	}
	
//	展示"貼文分類"所有貼文
	public List<PostDto> findAllPostsByPostCategory(String postCategory) {
		
		List<Post> posts = postRepository.findAllPostsByPostCategory(postCategory);
		List<PostDto> postsDtos = new ArrayList<>();
		
		List<String> empnos = posts.stream().map(Post::getPostEmp).distinct().collect(Collectors.toList());

		// 一次性查詢所有工號對應的員工姓名
		List<Employees> employees = employeesRepository.findAllById(empnos);
		Map<String, String> empnoToNameMap = employees.stream()
				.collect(Collectors.toMap(Employees::getEmpno, Employees::getName));

		for (Post post : posts) {

			String empno = post.getPostEmp();
			String name = empnoToNameMap.get(empno);
			post.setPostEmpName(name);
			
			//抓標題，並取前10個字
        	String title = post.getPostTitle();
            String titleExcerpt;

            if (title.length() > 10) {
            	titleExcerpt = title.substring(0, 10) + "...";
            } else {
            	titleExcerpt = title;
            }
			
			LocalDateTime postDateTime = post.getPostDate();
			LocalDateTime postExpiryDateTime = post.getPostExpiryDate();
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
			String formattedDateTime = postDateTime.format(formatter);
			String formattedExpiryDateTime = postExpiryDateTime.format(formatter);
			
			PostDto postDto = new PostDto(post.getPostId(), formattedDateTime, formattedExpiryDateTime, post.getPostStatus(), titleExcerpt, post.getPostCategory(), post.getPostContent(), post.getPostEmp(),post.getPostEmpName());
			postsDtos.add(postDto);
			
		}
		return postsDtos;
	}
//	---------------------分隔線----------------------
	
	
	
	
//	用其他條件查詢指定貼文
	public List<Post> findPostByMultiParam(
			String postId, 
			String postEmp, 
			Boolean postStatus, 
			String postCategory, 
			String postTitle) {
		
		if (postId != null && postId.isEmpty()) {
            postId = null;
        }
        if (postEmp != null && postEmp.isEmpty()) {
            postEmp = null;
        }
        if (postCategory != null && postCategory.isEmpty()) {
            postCategory = null;
        }
        if (postTitle != null && postTitle.isEmpty()) {
            postTitle = null;
        }
		
		List<Post> posts = postRepository.findPostByMultiParam(postId, postEmp, postStatus, postCategory, postTitle);
		
		return posts;
	}
	
	

	
//	展示所有貼文(排除假刪除貼文)
	public List<Post> findAllActivePosts() {
		
		Sort sort = Sort.by(Sort.Direction.DESC, "postDate");
		List<Post> posts = postRepository.findAllActivePosts(sort);
		return posts;
		
	}
	
//	postId產生器
//	"postId"由"postDate"+"postEmp"組成
	public String generatePostId(LocalDateTime postDate, String postEmp) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
		String formattedDate = postDate.format(formatter);
		String postId = formattedDate + "_" + postEmp;
		return postId;
	}

//	postUpdteRecordId產生器
//	"postUpdateRecordId"由"postUpdateTime"+"isImageUpdated"+"postId"組成
	public String generatePostUpdateRecordId(LocalDateTime postUpdateTime, Boolean isImageUpdated, String postId) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
		String formattedDate = postUpdateTime.format(formatter);
		
		String isImageChanged = isImageUpdated.toString();
		
		String postUpdateRecordId = formattedDate + "_" + isImageChanged + "_" + postId;
		return postUpdateRecordId;
	}
	
//	新增貼文
    public String insertNewPost(Post post, List<MultipartFile> images) throws IOException {
        postRepository.save(post);

        if (images != null && !images.isEmpty()) {
            postImageUploadService.uploadImagesByByte(post.getPostId(), images);
        }

        String successMessage = "貼文新增成功";
        return successMessage;
    }
	
//  更新貼文(含圖片新增/刪除)
    @Transactional
    public String updatePostAndImages(
            String postId,
            LocalDateTime postDate,
            LocalDateTime postExpiryDate,
            Boolean postStatus,
            String postTitle,
            String postCategory,
            String postContent,
            String postEmp,
            boolean isImageUpdated,
            List<PostImageUpdatedDto> postImageUpdateInfos
    ) throws IOException {

        // 紀錄一下舊貼文內容
        Optional<Post> optionalPost = postRepository.findById(postId);

        if (optionalPost.isPresent()) {
            Post beforeUpdatePost = optionalPost.get();
            String contentBeforeUpdate = beforeUpdatePost.getPostContent();

            // 更新貼文
            Post post = beforeUpdatePost;
            post.setPostId(postId);
            post.setPostDate(postDate);
            post.setPostExpiryDate(postExpiryDate);
            post.setPostStatus(postStatus);
            post.setPostTitle(postTitle);
            post.setPostCategory(postCategory);
            post.setPostContent(postContent);
            post.setPostEmp(postEmp);

            // 保存貼文
            postRepository.save(post);

            // 若有修改圖片，處理圖片更新
            if (isImageUpdated) {
                for (PostImageUpdatedDto dto : postImageUpdateInfos) {
                    PostImageUpload postImageUpload = postImageUploadRepository.findById(dto.getPostImageId()).orElse(null);

                    // 處理圖片新增和刪除操作
                    if ("新增".equals(dto.getUpdateAction())) {
                        if (dto.getImageFile() != null) {
                            postImageUpload = new PostImageUpload();
                            postImageUpload.setPostImageId(UUID.randomUUID().toString());
                            postImageUpload.setPostImageUri(dto.getImageFile().getOriginalFilename());
                            postImageUpload.setPostImageDataType(dto.getImageFile().getContentType());
                            postImageUpload.setPostImageData(dto.getImageFile().getBytes());
                            postImageUpload.setPostImageUploadTime(LocalDateTime.now());
                            postImageUploadRepository.save(postImageUpload);
                            
                            postImageUploadService.uploadImagesByByte(postId, List.of(dto.getImageFile()));
                            
                            
                            
                            
                        } else {
                        	
                            throw new IllegalArgumentException("Image file is null for update action: 新增");
                            
                        }
                    } else if ("刪除".equals(dto.getUpdateAction())) {
                    	
                        if (postImageUpload != null) {
                        	
                            postImageUploadService.deleteImageByImageId(dto.getPostImageId());
                            
                        } else {
                        	
                            // 記錄錯誤或警告日誌
                        	
                            System.err.println("Image not found with id: " + dto.getPostImageId());
                            
                            // 根據需求處理，這裡選擇忽略該操作，避免拋出異常
                        }
                    }
                }
            }

            // 返回舊內容
            return contentBeforeUpdate;
        }

        throw new IllegalArgumentException("Post not found with id: " + postId);
    }

	
    
//  更新貼文圖片修改紀錄
    @Transactional
    public void updatePostImageRecords(
            String postId,
            List<PostImageUpdatedDto> postImageUpdateInfos,
            String contentBeforeUpdate,
            String contentAfterUpdate,
            boolean isImageUpdated,
            String updateEmp,
            String updateEmpName
    ) {

        LocalDateTime postUpdateTime = LocalDateTime.now();

        PostUpdateRecord updateRecord = new PostUpdateRecord();
        
        updateRecord.setPostUpdateRecordId(generatePostUpdateRecordId(postUpdateTime, isImageUpdated, postId));
        updateRecord.setPostUpdateTime(postUpdateTime);
        updateRecord.setContentBeforeUpdate(contentBeforeUpdate);
        updateRecord.setContentAfterUpdate(contentAfterUpdate);
        updateRecord.setIsImageUpdated(isImageUpdated);
        updateRecord.setUpdateRecordBelonsPostId(postId);
        updateRecord.setUpdateEmp(updateEmp);
        updateRecord.setUpdateEmpName(updateEmpName);

//        
        if (updateRecord.getPostImageUpdateRecords() == null) {
            updateRecord.setPostImageUpdateRecords(new ArrayList<>());
        } else {
            updateRecord.getPostImageUpdateRecords().clear();
        }

        for (PostImageUpdatedDto dto : postImageUpdateInfos) {
            PostImageUpload postImageUpload = postImageUploadRepository.findById(dto.getPostImageId()).orElse(null);
            if (postImageUpload == null) {
                // 記錄錯誤或警告日誌
                System.err.println("Image not found with id: " + dto.getPostImageId());
                // 根據需求處理，這裡選擇忽略該操作，避免拋出異常
                continue;
            }

            // 添加新記錄到 postImageUpdateRecords
            PostImageUpdateRecord imageUpdateRecord = new PostImageUpdateRecord();
            PostImageUpdateRecordId imageUpdateRecordId = new PostImageUpdateRecordId();
            imageUpdateRecordId.setImageUploadUpdateId(dto.getPostImageId());
            imageUpdateRecordId.setUpdateRecordForImageId(updateRecord.getPostUpdateRecordId());
            imageUpdateRecord.setPostImageUpdateRecordId(imageUpdateRecordId);
            imageUpdateRecord.setPostImageUpload(postImageUpload);
            imageUpdateRecord.setPostUpdateRecord(updateRecord);
            
            //這個不確定能不能存入!!!
            postImageUpdateRecordRepository.save(imageUpdateRecord);

            updateRecord.getPostImageUpdateRecords().add(imageUpdateRecord);
        }
        
//
        
        postUpdateRecordRepository.save(updateRecord);
    }

	
	
	
	
//	當貼文圖片有回傳修改資訊時，需進行圖片修改的種類判斷與修改
	
	//可上下架不同貼文指定圖片(暫停使用)
//	private void postImageUpdateHandlingForAllPosts(
//			List<PostImageUpdatedDto> postImageUpdateInfos, 
//			PostUpdateRecord updateRecord) {
//		
//		if (postImageUpdateInfos != null) {
//			
//			for (PostImageUpdatedDto postImageUpdatedInfo : postImageUpdateInfos) {
//				
//				String postImageId = postImageUpdatedInfo.getPostImageId();
//				
//				PostImageUpdateRecordId postImageUpdateRecordId = new PostImageUpdateRecordId(postImageId, updateRecord.getPostUpdateRecordId());
//				PostImageUpdateRecord postImageUpdateRecord = new PostImageUpdateRecord();
//				
//				postImageUpdateRecord.setPostImageUpdateRecordId(postImageUpdateRecordId);
//				postImageUpdateRecord.setPostUpdateRecord(updateRecord);
//				
//				Optional<PostImageUpload> optionalImageUpload = postImageUploadRepository.findById(postImageId);
//				
//				if (optionalImageUpload.isPresent()) {
//					
//					PostImageUpload postImageUpload = optionalImageUpload.get();
//					postImageUploadRepository.save(postImageUpload);
//					
//					//處理圖片不同的操作(新增、刪除、上架、下架)
//					switch (postImageUpdatedInfo.getUpdateAction()) {
//                    case "add":
//                        // 處理新增圖片的邏輯
//                        break;
//                    case "enable":
//                        // 處理上架圖片的邏輯
//                    	postImageUpload.setPostImageStatus(true);
//                        postImageUploadRepository.save(postImageUpload);
//                        break;
//                    case "disable":
//                        // 處理下架圖片的邏輯
//                    	postImageUpload.setPostImageStatus(false);
//                        postImageUploadRepository.save(postImageUpload);
//                        break;
//                }
//            } else if ("remove".equals(postImageUpdatedInfo.getUpdateAction())) {
//                // 如果圖片不存在，且操作是刪除，直接記錄刪除操作
//                postImageUpdateRecord.setPostImageUpload(null); // 設置為空，因為圖片已經不存在
//            }
//
//            postImageUpdateRecordRepository.save(postImageUpdateRecord);
//						
//			}
//		}
//			
//	}
	
	//可上下架單一貼文指定圖片
	private void postImageUpdateHandlingForOnePost(
	        List<PostImageUpdatedDto> postImageUpdateInfos, 
	        PostUpdateRecord updateRecord) {

	    if (postImageUpdateInfos != null) {
	        
	        for (PostImageUpdatedDto postImageUpdatedInfo : postImageUpdateInfos) {
	            
	            String postImageId = postImageUpdatedInfo.getPostImageId();
	            
	            PostImageUpdateRecordId postImageUpdateRecordId = new PostImageUpdateRecordId(postImageId, updateRecord.getPostUpdateRecordId());
	            PostImageUpdateRecord postImageUpdateRecord = new PostImageUpdateRecord();
	            
	            postImageUpdateRecord.setPostImageUpdateRecordId(postImageUpdateRecordId);
	            postImageUpdateRecord.setPostUpdateRecord(updateRecord);
	            
	            Optional<PostImageUpload> optionalImageUpload = postImageUploadRepository.findById(postImageId);
	            
	            if (optionalImageUpload.isPresent()) {
	                
	                PostImageUpload postImageUpload = optionalImageUpload.get();
	               
	                PostImageBelongsRecordId postImageBelongsRecordId = new PostImageBelongsRecordId(updateRecord.getUpdateRecordBelonsPostId(), postImageId);
	                
	                Optional<PostImageBelongsRecord> optionalBelongsRecord = postImageBelongsRecordRepository.findById(postImageBelongsRecordId);
	                
	                if (optionalBelongsRecord.isPresent()) {
	                    PostImageBelongsRecord belongsRecord = optionalBelongsRecord.get();
	                    
	                    // 處理圖片不同的操作(新增、刪除、上架、下架)
	                    switch (postImageUpdatedInfo.getUpdateAction()) {
	                        case "add":
	                            // 處理新增圖片的邏輯
	                            break;
	                        case "enable":
	                            // 處理上架圖片的邏輯
	                        	postImageBelongsRecordService.enablePostImageBelongs(updateRecord.getUpdateRecordBelonsPostId(), postImageId);
	                            break;
	                        case "disable":
	                            // 處理下架圖片的邏輯
	                        	postImageBelongsRecordService.disablePostImageBelongs(updateRecord.getUpdateRecordBelonsPostId(), postImageId);
	                            break;
	                    }
	                    
	                    postImageUpdateRecord.setPostImageUpload(postImageUpload);
	                    
	                } 
	                
	                if (!optionalImageUpload.isPresent() || "remove".equals(postImageUpdatedInfo.getUpdateAction())) {
	                    // 如果圖片不存在，且操作是刪除，直接記錄刪除操作
	                    postImageUpdateRecord.setPostImageUpload(null); // 設置為空，因為圖片已經不存在
	                    
	                    if (optionalImageUpload.isPresent() && "remove".equals(postImageUpdatedInfo.getUpdateAction())) {
	                        postImageBelongsRecordService.deletePostImageBelongsRecord(updateRecord.getUpdateRecordBelonsPostId(), postImageId);
	                    }
	                }
	                
	                postImageUpdateRecordRepository.save(postImageUpdateRecord);
	            }
	        }
	    }
	}

	
	
	
	
	
//	由id刪除指定貼文
	public void deletePostById(String postId) {
		postRepository.deleteById(postId);
	}
	
//	由id假刪除(下架)指定貼文
	public void softDeletePostById(String postId) {
		postRepository.softDeletePost(postId);
		
	}
	
//	由id重啟瀏覽(上架)指定貼文
	public void browsePostById(String postId) {
		postRepository.browsePost(postId);
		
	}

}
