package com.example.demo.controller;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.dto.PostDto;
import com.example.demo.dto.PostImageUpdatedDto;
import com.example.demo.dto.PostWallDto;
import com.example.demo.entity.Post;
import com.example.demo.entity.PostImageUpload;
import com.example.demo.service.PostImageUploadService;
import com.example.demo.service.PostService;

import jakarta.servlet.http.HttpSession;

@Controller
public class PostController {
	
	private static final Set<String> ADMIN_ACCOUNTS = Set.of("A0001", "A0002", "A0003", "A0005", "A0006", "A0008", "A0010");
	
	@Autowired
	PostService postService;
	
	@Autowired
	PostImageUploadService postImageUploadService;
	
	
//	---------------------前台----------------------
	
//	顯示新增貼文輸入頁面
	@GetMapping("/insertPost.controller")
	public String insertPost() {
		
		return"post/insertPost.html";
		
	}
	
	
//	navbar導入"新增貼文"頁面
	@RequestMapping(value = "/navbarInsertPost", method = {RequestMethod.GET, RequestMethod.POST})
	public String navbarInsertPost() {
		
		return "post/navbarInsertPost.html";
	}
	
//	執行資料庫新增貼文
	 @PostMapping("/insertPostProcess.controller")
	    public String insertPostProcess(
	            @RequestParam("postTitle") String postTitle,
	            @RequestParam("postCategory") String postCategory,
	            @RequestParam("postContent") String postContent,
	            @RequestParam("postEmp") String postEmp,
	            @RequestParam("upload") List<MultipartFile> images,
	            RedirectAttributes redirectAttributes) {

		    if (postEmp == null || postEmp.isEmpty()) {
		        
		        return "redirect:/"; // 返回登入頁面
		    }
		 
	        LocalDateTime postDate = LocalDateTime.now();
	        LocalDateTime postExpiryDate = postDate.plusDays(7);
	        String postId = postService.generatePostId(postExpiryDate, postEmp);

	        Post post = new Post();

	        post.setPostId(postId);
	        post.setPostDate(postDate);
	        post.setPostExpiryDate(postExpiryDate);
	        post.setPostTitle(postTitle);
	        post.setPostCategory(postCategory);
	        post.setPostContent(postContent);
	        post.setPostEmp(postEmp);

	        String successInsert;
	        try {
	            successInsert = postService.insertNewPost(post, images);

	            if (successInsert != null) {
	                return "redirect:/myPosts";
	            }

	        } catch (IOException e) {
	            e.printStackTrace();
	            redirectAttributes.addFlashAttribute("isExisted", "Failed to upload image or insert post");
	            return "redirect:/navbarInsertPost";
	        }

	        String isExisted = "The post is existed. Please retry.";
	        redirectAttributes.addFlashAttribute("isExisted", isExisted);
	        return "redirect:/navbarInsertPost";

	    }
	
	
//	導入貼文牆
	@RequestMapping(value = "/postWall", method = {RequestMethod.GET, RequestMethod.POST})
	public String postWall(Model model) {
		List<PostWallDto> posts = postService.findAllPostsOnWall();
		model.addAttribute("posts",posts);
		return "post/postWall.html";
	}
	
//	導入輸入搜尋條件的頁面
	@RequestMapping(value = "/searchPostWall", method = {RequestMethod.GET, RequestMethod.POST})
	public String searchPostWall() {

		return "post/searchPostWall.html";
	}
	
//	導入搜尋條件後的貼文牆
	@RequestMapping(value = "/searchPostWallByKeyWords", method = {RequestMethod.GET, RequestMethod.POST})
	public String postWall(
			@RequestParam(required = false) String keyword1,
            @RequestParam(required = false) String keyword2,
			Model model) {
		List<PostWallDto> posts = postService.findAllPostsOnWallByKeywords(keyword1, keyword2);
		model.addAttribute("posts",posts);
		return "post/postWall.html";
	}
	
	
//	導入"我的貼文"頁面
	@RequestMapping(value = "/myPosts", method = {RequestMethod.GET, RequestMethod.POST})
	public String findAllPostsByPostEmp(Model model, HttpSession httpSession) {
		
		String postEmp = (String) httpSession.getAttribute("account");
		List<PostDto> posts = postService.findAllPostsByPostEmp(postEmp);
		model.addAttribute("posts",posts);
		return "post/viewAllPosts.html";
	}
	
//	導入"我的貼文"+"貼文分類"頁面
	@RequestMapping(value = "/myPostsWithCategory", method = {RequestMethod.GET, RequestMethod.POST})
	public String findAllPostsByPostEmpAndCategory(@RequestParam("postCategory") String postCategory ,Model model, HttpSession httpSession) {
		
		String postEmp = (String) httpSession.getAttribute("account");
		List<PostDto> posts = postService.findAllPostsByPostEmpAndPostCategory(postEmp, postCategory);
		model.addAttribute("posts",posts);
		return "post/viewAllPosts.html";
	}
	
//	以postId查詢指定貼文
	@RequestMapping(value = "/findPostById.controller", method = {RequestMethod.GET, RequestMethod.POST})
	public String findPostByIdProcess(@RequestParam("postId") String postId, Model model ) {
		
		PostDto post = postService.findPostById(postId);
				
		if (post != null) {
		
			List<PostImageUpload> postImageUploads = postImageUploadService.findAllActivePostImages(postId);

			List<PostImageUpload> base64EncodedImages = new ArrayList<>();

            for (PostImageUpload image : postImageUploads) {
            	
            	if (image.getPostImageUri() != null && !image.getPostImageUri().isEmpty() ) {
            		// 將base64轉換的圖片檔資訊解碼，重新改為圖片檔字元數組
            		byte[] decodedBytes = Base64.getDecoder().decode(image.getPostImageData());
            		// 將字元數組重新编码为 Base64 字符串，并添加 data URI 前缀
            		String base64ImageData = "data:" + image.getPostImageDataType() + ";base64," + Base64.getEncoder().encodeToString(decodedBytes);
            		image.setBase64ImageData(base64ImageData); // 设置新的 Base64 编码字段
            		base64EncodedImages.add(image);
				}
            }
			
			
			model.addAttribute("postImageUploads",base64EncodedImages);
			model.addAttribute("post",post);
			return "post/viewPost.html";
		}

		return "post/postWall.html";			
	}
	
	
//	以id顯示要修改貼文
	@PostMapping("/showUpdatePostById.controller")
	public String showUpdatePostByIdProcess(@RequestParam("postId") String postId, Model model ) {
		
		PostDto post = postService.findPostById(postId);
		List<PostImageUpload> images = postImageUploadService.findAllActivePostImages(postId);
		
		List<PostImageUpload> base64EncodedImages = new ArrayList<>();

        for (PostImageUpload image : images) {
        	
        	
        	System.out.println("PostImageId: " + image.getPostImageId());
        	
        	
            // 解码 Base64 编码的字符串到字节数组
            byte[] decodedBytes = Base64.getDecoder().decode(image.getPostImageData());
            // 将字节数组重新编码为 Base64 字符串，并添加 data URI 前缀
            String base64ImageData = "data:" + image.getPostImageDataType() + ";base64," + Base64.getEncoder().encodeToString(decodedBytes);
            image.setBase64ImageData(base64ImageData); // 设置新的 Base64 编码字段
            base64EncodedImages.add(image);
            
            System.out.println("PostImageId2: " + image.getPostImageId());
            
        }
        
		
		model.addAttribute("images",base64EncodedImages);
		model.addAttribute("post",post);
		return "post/updatePost.html";
			
	}
	
//	(使用者)以id假刪除特定貼文
	@PostMapping("/softDeletePostById.controller")
	public String softDeletePostByIdProcess(@RequestParam("postId") String postId) {
		
		postService.softDeletePostById(postId);
		
		return "redirect:/myPosts";
	}
	
//	(使用者)以id重啟瀏覽特定貼文
	@PostMapping("/browsePostById.controller")
	public String browsePostByIdProcess(@RequestParam("postId") String postId) {
		
		postService.browsePostById(postId);
		
		return "redirect:/myPosts";
	}
	
	
	
//	---------------------後台----------------------
	
//	無條件瀏覽所有貼文
	@RequestMapping(value = "/findAllPosts.controller", method = {RequestMethod.GET, RequestMethod.POST})
	public String findAllPosts(Model model) {
		List<PostDto> posts = postService.findAllPosts();
		model.addAttribute("posts",posts);
		return "post/back/viewAllPosts.html";
	}
	
//	導入"貼文分類"頁面
	@RequestMapping(value = "/postsWithCategory", method = {RequestMethod.GET, RequestMethod.POST})
	public String findAllPostsByCategory(@RequestParam("postCategory") String postCategory ,Model model, HttpSession httpSession) {
		
		List<PostDto> posts = postService.findAllPostsByPostCategory(postCategory);
		model.addAttribute("posts",posts);
		return "post/back/viewAllPosts.html";
	}
	
//	(管理者)以postId查詢指定貼文
	@RequestMapping(value = "/findPostById.manager", method = {RequestMethod.GET, RequestMethod.POST})
	public String findPostByIdManager(@RequestParam("postId") String postId, Model model ) {
		
		PostDto post = postService.findPostById(postId);
				
		if (post != null) {
		
			List<PostImageUpload> postImageUploads = postImageUploadService.findAllActivePostImages(postId);

			List<PostImageUpload> base64EncodedImages = new ArrayList<>();

            for (PostImageUpload image : postImageUploads) {
                // 解码 Base64 编码的字符串到字节数组
                byte[] decodedBytes = Base64.getDecoder().decode(image.getPostImageData());
                // 将字节数组重新编码为 Base64 字符串，并添加 data URI 前缀
                String base64ImageData = "data:" + image.getPostImageDataType() + ";base64," + Base64.getEncoder().encodeToString(decodedBytes);
                image.setBase64ImageData(base64ImageData); // 设置新的 Base64 编码字段
                base64EncodedImages.add(image);
            }
			
			
			model.addAttribute("postImageUploads",base64EncodedImages);
			model.addAttribute("post",post);
			return "post/back/viewPost.html";
		}

		return "redirect:/findAllPosts.controller";			
	}
	
	
//	(管理者)以id顯示要修改貼文
	@PostMapping("/showUpdatePostById.manager")
	public String showUpdatePostByIdManager(@RequestParam("postId") String postId, Model model ) {
		
		PostDto post = postService.findPostById(postId);
		List<PostImageUpload> images = postImageUploadService.findAllActivePostImages(postId);
		
		List<PostImageUpload> base64EncodedImages = new ArrayList<>();

        for (PostImageUpload image : images) {
        	
        	
        	System.out.println("PostImageId: " + image.getPostImageId());
        	
        	
            // 解码 Base64 编码的字符串到字节数组
            byte[] decodedBytes = Base64.getDecoder().decode(image.getPostImageData());
            // 将字节数组重新编码为 Base64 字符串，并添加 data URI 前缀
            String base64ImageData = "data:" + image.getPostImageDataType() + ";base64," + Base64.getEncoder().encodeToString(decodedBytes);
            image.setBase64ImageData(base64ImageData); // 设置新的 Base64 编码字段
            base64EncodedImages.add(image);
            
            System.out.println("PostImageId2: " + image.getPostImageId());
            
        }
        
		
		model.addAttribute("images",base64EncodedImages);
		model.addAttribute("post",post);
		return "post/back/updatePost.html";
			
	}
	
//	(管理者)以id刪除特定貼文
	@PostMapping("/deletePostById.controller")
	public String deletePostByIdProcess(@RequestParam("postId") String postId) {
		System.out.println("----------------------------------------------------------------------------------------------");
		System.out.println("Received postId: " + postId);
		System.out.println("----------------------------------------------------------------------------------------------");
		postService.deletePostById(postId);
		
		return "redirect:/findAllPosts.controller";
	}
	
//	(管理者)以id下架特定貼文
	@PostMapping("/softDeletePostById.manager.controller")
	public String managerSoftDeletePostByIdProcess(@RequestParam("postId") String postId) {
		
		postService.softDeletePostById(postId);
		
		return "redirect:/findAllPosts.controller";
	}
	
//	(管理者)以id重啟瀏覽特定貼文
	@PostMapping("/browsePostById.manager.controller")
	public String managerBrowsePostByIdProcess(@RequestParam("postId") String postId) {
		
		postService.browsePostById(postId);
		
		return "redirect:/findAllPosts.controller";
	}
	
//	---------------------分隔線----------------------
	
//	以id執行修改貼文
	@PostMapping("/updatePostById.controller")
	public String updatePostByIdProcess(
			@RequestParam("postId") String postId,
			@RequestParam("postDate") String postDateString,
			@RequestParam("postExpiryDate") String postExpiryDateString,
			@RequestParam("postStatus") Boolean postStatus,
			@RequestParam("postTitle") String postTitle,
			@RequestParam("postCategory") String postCategory,
			@RequestParam("postContent") String postContent,
			@RequestParam("postEmp") String postEmp,
			@RequestParam("isImageUpdated") boolean isImageUpdated,
			@RequestParam("updateActions") String updateActions,
			@RequestParam("postImageIds") String postImageIds,
			@RequestPart(name = "imageFiles", required = false) List<MultipartFile> imageFiles,
			HttpSession httpSession
			) throws IOException {
		
		// 定義日期格式
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
		
		// 將String轉換為LocalDateTime
		LocalDateTime postDate = LocalDateTime.parse(postDateString, formatter);
		LocalDateTime postExpiryDate = LocalDateTime.parse(postExpiryDateString, formatter);
		
		
		// 使用逗號分隔的字符串分割為列表
		List<String> updateActionList = Arrays.asList(updateActions.split(","));
		List<String> postImageIdList = Arrays.asList(postImageIds.split(","));
		
		// 構建PostImageUpdatedDto列表
		List<PostImageUpdatedDto> postImageUpdateInfos = new ArrayList<>();
		
		int imageFileIndex = 0;
		for (int i = 0; i < updateActionList.size(); i++) {
			PostImageUpdatedDto dto = new PostImageUpdatedDto();
			dto.setUpdateAction(updateActionList.get(i));
			
			// 確保 postImageIdList 的索引不超出範圍
			if (i < postImageIdList.size()) {
				dto.setPostImageId(postImageIdList.get(i));
			} else {
				dto.setPostImageId(""); // 設置為空字符串或其他默認值
			}
			
			if ("新增".equals(updateActionList.get(i))) {
				// 當 "新增" 操作時，檢查 imageFiles 是否有對應的文件
				if (imageFiles != null && imageFileIndex < imageFiles.size()) {
					dto.setImageFile(imageFiles.get(imageFileIndex));
					imageFileIndex++;
				} else {
					throw new IllegalArgumentException("Image file not found for update action: 新增");
				}
			}
			
			postImageUpdateInfos.add(dto);
		}
		
		// 先更新貼文和圖片，並將舊內容返回
		String contentBeforeUpdate = postService.updatePostAndImages(postId, postDate, postExpiryDate, postStatus, postTitle, postCategory, postContent, postEmp, isImageUpdated, postImageUpdateInfos);
		
		//抓user資訊
		String updateEmp = (String) httpSession.getAttribute("account");
		String updateEmpName = (String) httpSession.getAttribute("name");
		
		
		
		// 再更新圖片記錄
		postService.updatePostImageRecords(postId, postImageUpdateInfos, contentBeforeUpdate, postContent, isImageUpdated, updateEmp, updateEmpName);
	
		
		//--------------------------------------------------------------------------------
		
		
		//判斷目前User是否為"管理者"
		if (ADMIN_ACCOUNTS.contains(updateEmp)) {
			return "redirect:/findPostById.manager?postId=" + postId;
		}
		
		return "redirect:/findPostById.controller?postId=" + postId;
	}
	
	
//	瀏覽所有"上架"貼文
	@RequestMapping(value = "/findAllActivePosts.controller", method = {RequestMethod.GET, RequestMethod.POST})
	public String findAllActivePosts(Model model) {
		List<Post> posts = postService.findAllActivePosts();
		model.addAttribute("posts",posts);
		return "post/viewAllPosts.html";
	}
	
//	顯示查詢指定貼文頁面
	@GetMapping("/findPost.controller")
	public String findPostById() {
		
		return "post/findPost.html";
		
	}
	

	
//	以多個參數查詢符合貼文(帳號可以指定不同發文者)
	@PostMapping("/findPostByMultiParam.controller")
	public String findPostByMultiParam(
			@RequestParam(value="postId", required = false) String postId, 
			@RequestParam(value="postStatusString", required = false) String postStatusString, 
			@RequestParam(value="postCategory", required = false) String postCategory, 
			@RequestParam(value="postTitle", required = false) String postTitle, 
			@RequestParam(value="postEmp", required = false) String postEmp, 
			HttpSession session,
			Model model ) {
		
		Boolean postStatus = null;
		if (postStatusString != null && !postStatusString.isEmpty()) {
			postStatus = Boolean.valueOf(postStatusString);
		}
		
		List<Post> posts = postService.findPostByMultiParam(postId, postEmp, postStatus, postCategory, postTitle);
		
		if (posts != null) {
			model.addAttribute("posts",posts);
			return "post/viewAllPosts.html";
		}
		String notFound = "The post isn't found. Please try another.";
		model.addAttribute("notFound",notFound);
		return "post/findPost.html";			
	}
	

	
	

	

	

//	和軒導入貼文牆

	@RequestMapping(value = "/empsal", method = {RequestMethod.GET, RequestMethod.POST})
	public String SalWall(
			@RequestParam(required = false) String keyword1,
            @RequestParam(required = false) String keyword2,
			Model model) {
		List<PostWallDto> posts = postService.findAllPostsOnWallByKeywords(keyword1, "薪資");
		model.addAttribute("posts",posts);
		return "sal/empSal.html";
	}
	
	

}
