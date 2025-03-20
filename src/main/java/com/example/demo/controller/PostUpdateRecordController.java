package com.example.demo.controller;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.dto.PostUpdatedRecordDto;
import com.example.demo.entity.PostUpdateRecord;
import com.example.demo.service.PostUpdateRecordService;

import jakarta.servlet.http.HttpSession;

@Controller
public class PostUpdateRecordController {
	
	private static final Set<String> ADMIN_ACCOUNTS = Set.of("A0001", "A0002", "A0003", "A0005", "A0006", "A0008", "A0010");
	
	@Autowired
	PostUpdateRecordService postUpdateRecordService;
//	------------------------------------前台------------------------------------
	@PostMapping("/findAllPostUpdateRecords")
	public String findAllPostUpdateRecords(
			@RequestParam String postId,
			Model model,
			HttpSession httpSession) {
		
		List<PostUpdatedRecordDto> postUpdatedRecords = postUpdateRecordService.findAllUpdatedRecords(postId);
		
		model.addAttribute("postUpdatedRecords",postUpdatedRecords);
		model.addAttribute("postId", postId);

		
		return "post/postUpdatedRecords.html";
	}
	
//	------------------------------------後台------------------------------------
	
	@PostMapping("/findAllPostUpdateRecords.manager")
	public String findAllPostUpdateRecordsByManager(
			@RequestParam String postId,
			Model model,
			HttpSession httpSession) {
		
		List<PostUpdatedRecordDto> postUpdatedRecords = postUpdateRecordService.findAllUpdatedRecords(postId);
		
		model.addAttribute("postUpdatedRecords",postUpdatedRecords);
		model.addAttribute("postId", postId);

		
		return "post/back/postUpdatedRecords.html";
	}

	
//	------------------------------------分隔線------------------------------------
}
