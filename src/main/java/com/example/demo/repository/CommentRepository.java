package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.entity.Comment;

import jakarta.transaction.Transactional;


//(JPA會檢測方法的命名中關鍵字部分，來建立Query指令，不一定要自己下JPQL)
public interface CommentRepository extends JpaRepository<Comment, Integer> {
	
//	計算"指定貼文"留言數
	@Query("SELECT COUNT(c) FROM Comment c WHERE c.commentBelongsPostId = :commentBelongsPostId")
    Integer countCommentsByPostId(@Param("commentBelongsPostId") String commentBelongsPostId);
	
//	顯示貼文所有留言(由時間新-->舊排序)
	@Query("SELECT c FROM Comment c WHERE c.commentBelongsPostId = :commentBelongsPostId ORDER BY c.commentDateTime DESC ")
    List<Comment> findAllCommentsByPostId(@Param("commentBelongsPostId") String commentBelongsPostId);

//	顯示貼文所有留言
	List<Comment> findByCommentBelongsPostId(String commentBelongsPostId);
	
//	假刪除留言
	@Transactional
	@Modifying
	@Query("UPDATE Comment c SET c.commentStatus = false WHERE c.commentId = :commentId")
	void softDeleteComment(@Param("commentId") Integer commentId);
}
