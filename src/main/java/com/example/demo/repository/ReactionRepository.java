package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.entity.Reaction;

import jakarta.transaction.Transactional;

public interface ReactionRepository extends JpaRepository<Reaction, Integer> {
	
	@Query("SELECT COUNT(r) FROM Reaction r WHERE r.reactionBelongsPostId = :reactionBelongsPostId AND r.reactionCategory = 'like'")
    Integer countLikesByPostId(@Param("reactionBelongsPostId") String reactionBelongsPostId);
	
	boolean existsByreactionBelongsPostIdAndReactionEmpAndReactionCategory(String reactionBelongsPostId, String reactionEmp, String reactionCategory);
	
	@Modifying
    @Transactional
    void deleteByreactionBelongsPostIdAndReactionEmpAndReactionCategory(String reactionBelongsPostId, String reactionEmp, String reactionCategory);


}
