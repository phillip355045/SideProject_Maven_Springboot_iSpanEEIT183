package com.example.demo.controller;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.demo.entity.Notification;
import com.example.demo.entity.Reaction;
import com.example.demo.service.NotificationService;
import com.example.demo.service.ReactionService;
import com.example.demo.dto.ReactionResponseDto;
import com.example.demo.dto.ReactionStatusDto;

import jakarta.servlet.http.HttpSession;

@Controller
public class ReactionController {
	
	@Autowired
	ReactionService reactionService;
	
	@Autowired
	NotificationService notificationService;
	
	
	
	@ResponseBody
	@PostMapping("/insertReaction")
	public ReactionResponseDto insertReaction(
			@RequestParam("reactionCategory") String reactionCategory,
			@RequestParam("postId") String reactionBelongsPostId,
			HttpSession httpSession) {
		
		LocalDateTime reactionTime = LocalDateTime.now();
		String reactionEmp = (String) httpSession.getAttribute("account");
		String reactionEmpName = (String) httpSession.getAttribute("name");
		
		// 檢查用戶是否已經按過讚
        boolean alreadyLiked = reactionService.hasUserReacted(reactionBelongsPostId, reactionEmp, reactionCategory);
        if (alreadyLiked) {
            // 如果已經按過讚，進行取消讚操作
            reactionService.removeReaction(reactionBelongsPostId, reactionEmp, reactionCategory);
            int likeCounts = reactionService.getLikeCounts(reactionBelongsPostId);
            
            ReactionResponseDto reactionResponseDto = new ReactionResponseDto(null, likeCounts);
            return reactionResponseDto;
         
        }
		
		
		Reaction reaction = new Reaction();
		
		reaction.setReactionBelongsPostId(reactionBelongsPostId);
		reaction.setReactionCategory(reactionCategory);
		reaction.setReactionEmp(reactionEmp);
		reaction.setReactionTime(reactionTime);
		
		String successMsg = reactionService.insertReaction(reaction);
        //新增reaction，同時新增通知進去notification
        Notification notification = notificationService.reactionNotification(reactionBelongsPostId, reactionEmpName);
		
		if (successMsg.equals("Success")) {
			 int likeCount = reactionService.getLikeCounts(reactionBelongsPostId);
	            ReactionResponseDto reactionResponseDto = new ReactionResponseDto(reaction, likeCount);
	            return reactionResponseDto;
		}
		
		return null;
	}
	
	
	@ResponseBody
	@PostMapping(value = "/deleteReaction")
    public ReactionResponseDto deleteReaction(
            @RequestParam("reactionCategory") String reactionCategory,
            @RequestParam("postId") String reactionBelongsPostId,
            HttpSession httpSession) {

        String reactionEmp = (String) httpSession.getAttribute("account");

        // 執行刪除操作
        reactionService.removeReaction(reactionBelongsPostId, reactionEmp, reactionCategory);
        int likeCounts = reactionService.getLikeCounts(reactionBelongsPostId);

        ReactionResponseDto reactionResponseDto = new ReactionResponseDto(null, likeCounts);
        return reactionResponseDto;
    }
	
	
	@ResponseBody
	@PostMapping("/getReactionStatus")
	public ReactionStatusDto getReactionStatus(@RequestParam("postId") String postId, HttpSession httpSession) {
	    String reactionEmp = (String) httpSession.getAttribute("account");
	    boolean userReacted = reactionService.hasUserReacted(postId, reactionEmp, "like");
	    int likeCounts = reactionService.getLikeCounts(postId);
	    
	    ReactionStatusDto reactionStatusDto = new ReactionStatusDto(userReacted, likeCounts);

	    return reactionStatusDto;
	}

}
