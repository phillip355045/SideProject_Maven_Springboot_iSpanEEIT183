package com.example.demo.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.dto.PostImageIdDto;
import com.example.demo.entity.PostImageUpload;
import com.example.demo.service.PostImageUploadService;

@RestController
public class PostImageUploadController {
	
	@Autowired
	PostImageUploadService postImageUploadService;
	
//	貼文圖片上傳
	@PostMapping("/uploadPostImages")
	public String uploadPostImages(
			@RequestParam("postId") String belongsPostId,
			@RequestParam("upload") List<MultipartFile> images
			) {
		
		try {
			
			String url = postImageUploadService.uploadImages(belongsPostId, images);
			return "{\"url\":\"" + url + "\"}";
			
		} catch (IOException e) {
			
			e.printStackTrace();
			return "{\"error\":\"Failed to upload image\"}";	
			
		}		
		
	}
	
	
//	貼文圖片下架
	@PostMapping("/softDeletePostImages")
	public String softDeletePostImages(
			@RequestBody PostImageIdDto postImageIdDto) {
		
		String postImageId = postImageIdDto.getPostImageId();
		
		postImageUploadService.softDeletePostImage(postImageId);
		
		return "Images deleted successfully";
	}
	
//	貼文圖片上架
	@PostMapping("/browsePostImages")
	public String browsePostImages(
			@RequestBody PostImageIdDto postImageIdDto) {
		
		String postImageId = postImageIdDto.getPostImageId();
		
		postImageUploadService.browsePostImage(postImageId);
		
		return "Images deleted successfully";
	}
	
	
//	展示"貼文所屬"、"可瀏覽"所有圖片
	@RequestMapping(value = "/findAllActivePictures",method = {RequestMethod.POST,RequestMethod.GET})
	public List<PostImageUpload> findAllActivePostImages(@RequestParam("postId") String postId){
		
		List<PostImageUpload> images = postImageUploadService.findAllActivePostImages(postId);
		
		return images;
		
	}
	
//	貼文圖片移除
	@PostMapping("/deletePostImages")
	public String deletePostImages(
			@RequestBody PostImageIdDto postImageIdDto) {
		
		String postImageId = postImageIdDto.getPostImageId();
		
		postImageUploadService.deleteImageByImageId(postImageId);
		
		return "Images deleted successfully";
	}
	
}
