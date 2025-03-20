package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;

import com.example.demo.entity.Reaction;
import com.example.demo.repository.ReactionRepository;

import jakarta.transaction.Transactional;

@Service
public class ReactionService {
	
	@Autowired
	ReactionRepository reactionRepository;
	
	public String insertReaction(Reaction reaction) {
		
		Reaction reactionReply = reactionRepository.save(reaction);
		
		if (reactionReply != null) {
			return "Success";
		}
		
		return "Failure";
	}
	
	public Integer getLikeCounts(String postId) {
		
		Integer likeCounts = reactionRepository.countLikesByPostId(postId);
		
		return likeCounts;
	}
	
	public Boolean hasUserReacted(String reactionBelongsPostId, String reactionEmp, String reactionCategory) {
		
		Boolean checkExisted = reactionRepository.existsByreactionBelongsPostIdAndReactionEmpAndReactionCategory(reactionBelongsPostId, reactionEmp, reactionCategory);
		
		return checkExisted;
		
	}
	
	
	public void removeReaction(String reactionBelongsPostId, String reactionEmp, String reactionCategory) {
        
		reactionRepository.deleteByreactionBelongsPostIdAndReactionEmpAndReactionCategory(reactionBelongsPostId, reactionEmp, reactionCategory);
    
	}
	

}
