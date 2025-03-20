package com.example.demo.controller;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.entity.Comment;
import com.example.demo.entity.Notification;
import com.example.demo.service.CommentService;
import com.example.demo.service.NotificationService;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpSession;

@RestController
public class CommentController {
	
    private static final Logger logger = LoggerFactory.getLogger(CommentController.class);
	
	@Autowired
	CommentService commentService;
	
	@Autowired
	NotificationService notificationService;
	
    @Autowired
    private ObjectMapper objectMapper;

	 @PostMapping("/insertNewComment")
	 public ResponseEntity<Map<String, String>> insertNewComment(@RequestBody Map<String, String> dtoObject, HttpSession httpSession) {
	        String commentContent = dtoObject.get("commentContent");
	        String commentBelongsPostId = dtoObject.get("postId");

	        LocalDateTime commentDateTime = LocalDateTime.now();
	        String loginUserId = (String) httpSession.getAttribute("account");
	        String loginUserName = (String) httpSession.getAttribute("name");
	        

	        if (loginUserId == null) {
	            Map<String, String> response = new HashMap<>();
	            response.put("status", "error");
	            response.put("message", "User is not logged in");
	            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
	        }

	        Comment comment = new Comment();
	        comment.setCommentDateTime(commentDateTime);
	        comment.setCommentContent(commentContent);
	        comment.setCommentEmp(loginUserId);
	        comment.setCommentBelongsPostId(commentBelongsPostId);

	        String successInsert = commentService.insertNewComment(comment);
	        //新增comment，同時新增通知進去notification
	        Notification notification = notificationService.commentNotification(commentBelongsPostId, loginUserName);

	        Map<String, String> response = new HashMap<>();
	        if ("Success".equals(successInsert)) {
	            response.put("status", "success");
	            response.put("message", "Comment created successfully");
	            return new ResponseEntity<>(response, HttpStatus.OK);
	            
	        } else {
	            response.put("status", "error");
	            response.put("message", "Failed to create comment");
	        }
	        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
	
	}
	 

	 
	 @RequestMapping(value = "/findAllComments", method = {RequestMethod.POST, RequestMethod.GET})
	 public List<Comment> findAllComents(@RequestBody Map<String, String> postIdContainer) {
		
		 String postId = postIdContainer.get("postId");
		 
		 List<Comment> comments = commentService.findAllComments(postId);
		 
		 return comments;
	}
	 
//	 @GetMapping(value = "/findAllComments", produces = "text/plain")
//	 public String findAllComments(@RequestParam String postId) {
//	     List<Comment> comments = commentService.findAllActiveComments(postId);
//
//	     StringBuilder response = new StringBuilder();
//	     comments.forEach(comment -> response.append(comment.getCommentId()).append("|").append(comment.getCommentContent()).append("\n"));
//
//	     return response.toString().trim();
//	 }

	 
//	 @PutMapping("/updateComment")
//	 public ResponseEntity<Map<String, Object>> updateComment(
//	         @RequestBody Map<String, String> updateLoad,
//	         HttpSession httpSession) {
//
//	     Integer commentId = Integer.parseInt(updateLoad.get("commentId"));
//	     String newCommentContent = updateLoad.get("newCommentContent");
//
//	     String commentEmp = (String) httpSession.getAttribute("account");
//
//	     if (commentEmp == null) {
//	         Map<String, Object> response = new HashMap<>();
//	         response.put("status", "error");
//	         response.put("message", "User is not logged in");
//	         return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
//	     }
//
//	     Comment updatedComment = commentService.updateComment(commentId, newCommentContent, commentEmp);
//
//	     Map<String, Object> response = new HashMap<>();
//	     response.put("status", "success");
//	     response.put("message", "Comment updated successfully");
//	     response.put("updatedComment", updatedComment);
//	     return new ResponseEntity<>(response, HttpStatus.OK);
//	 }
	 
	 
	 
//	 因為使用ajax傳遞json格式資料，這裡參數不能@RequestParam，要改用@RequestBody
	 @PostMapping("/updateComment")
	 public Comment updateComment(
			 @RequestBody Map<String,String> updateLoad,
			 HttpSession httpSession) {
		 
//		 因為從前端接收json格式資料，所以拿到@RequestBody後，要用get函數拆解出我要的區域變數
		 Integer commentId = Integer.parseInt(updateLoad.get("commentId"));
	     String newCommentContent = updateLoad.get("newCommentContent");
	     
		 String commentEmp = (String) httpSession.getAttribute("account");
//		 String commentEmp = "A0001";
		 Comment updatedComment = commentService.updateComment(commentId, newCommentContent, commentEmp);
		 
		 return updatedComment;
	 }
	 
	 
	 
	 
	 
//	 下架留言(假刪除)
	 @PostMapping("/softDeleteComment")
	 public ResponseEntity<Void> softDeleteComment(@RequestParam("commentId") Integer commentId) {
		 
		 commentService.softDeleteComment(commentId);
		 return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		 
	 }
	 
//	 刪除留言
	 @PostMapping("/deleteComment")
	 public ResponseEntity<Void> deleteComment(@RequestParam("commentId") Integer commentId) {
		 
		 commentService.deleteComment(commentId);
		 return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		 
	 }
	 
	 
}
