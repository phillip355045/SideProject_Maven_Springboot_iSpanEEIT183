package com.example.demo.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entity.Comment;
import com.example.demo.entity.CommentUpdateRecord;
import com.example.demo.entity.Employees;
import com.example.demo.repository.CommentRepository;
import com.example.demo.repository.CommentUpdateRecordRepository;
import com.example.demo.repository.EmployeesRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class CommentService {
	
	@Autowired
	CommentRepository commentRepository;
	
	@Autowired
	CommentUpdateRecordRepository commentUpdateRecordRepository;
	
	@Autowired
	EmployeesRepository employeesRepository;
	
	
//	新增留言
	public String insertNewComment(Comment comment) {
		
		Comment commentReply = commentRepository.save(comment);
		
		return commentReply != null ? "Success" : "Failed";
		
	}
	
	
//	瀏覽所有留言
	public List<Comment> findAllComments(String postId) {
		
		List<Comment> comments = commentRepository.findAllCommentsByPostId(postId);
		
		 // 抓出所有員工編號
        List<String> empnos = comments.stream()
                                      .map(Comment::getCommentEmp)
                                      .distinct()
                                      .collect(Collectors.toList());

        // 一次性查詢所有工號對應的員工姓名
        List<Employees> employees = employeesRepository.findAllById(empnos);
        Map<String, String> empnoToNameMap = employees.stream()
                                                      .collect(Collectors.toMap(Employees::getEmpno, Employees::getName));
        
        // 設定commentEmpName的值
        for (Comment comment : comments) {
            String empno = comment.getCommentEmp();
            String name = empnoToNameMap.get(empno);
            comment.setCommentEmpName(name);
        }
		
		return comments;
	}
	
//	計算"指定貼文"留言數量
	public Integer getCommentCounts(String postId) {
		
		Integer commentCounts = commentRepository.countCommentsByPostId(postId);
		
		return commentCounts;
	}
	
//	修改留言，同時新增留言修改紀錄
    public Comment updateComment(Integer commentId, String newCommentContent, String commentEmp) {
       
    	Comment comment;
    	Optional<Comment> optional = commentRepository.findById(commentId);
    	
    	if (optional.isPresent()) {
    		comment = optional.get();
		}else {
			throw new EntityNotFoundException("Comment not found for id: " + commentId);
		}
    	
    	String oldCommentContent = comment.getCommentContent();
//    	三元運算，若為null，設值為0；若非null，用comment.getCommentModifiedTimes()取目前的值
    	Integer modifiedTimes = comment.getCommentModifiedTimes() == null ? 0 : comment.getCommentModifiedTimes();
    	comment.setCommentModifiedTimes(modifiedTimes + 1);
    	comment.setCommentContent(newCommentContent);
    	
    	commentRepository.save(comment);

//    	以下為新增CommentUpdateRecord的程式碼
    	CommentUpdateRecord updateRecord = new CommentUpdateRecord();
    	updateRecord.setCommentUpdateDateTime(LocalDateTime.now());
    	updateRecord.setCommentPreviousContent(oldCommentContent);
    	updateRecord.setCommentUpdateContent(newCommentContent);
    	updateRecord.setUpdateRecordBelongsCommentId(commentId);

    	commentUpdateRecordRepository.save(updateRecord);
//      結束: 新增CommentUpdateRecord的程式碼
        
        return comment;
    }
	
//	留言下架(假刪除)
	public void softDeleteComment(Integer commentId) {
		
		commentRepository.softDeleteComment(commentId);
		
	}
	
//	刪除留言
	public void deleteComment(Integer commentId) {
		
		commentRepository.deleteById(commentId);
		
	}

}
