package com.example.demo.entity;

import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "commentUpdateRecord")
@Entity
public class CommentUpdateRecord {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "commentUpdateRecordId", nullable = false)
	private Integer commentUpdateRecordId;
	
	@Column(nullable = false)
	@DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")// 告訴程式該如何解析收到的資料(ex. 月份在哪個位置)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")// 告訴程式該如何解析收到的資料(ex. 月份在哪個位置)
	private LocalDateTime commentUpdateDateTime;
	
	@Column(name = "commentPreviousContent", columnDefinition = "TEXT")
	private String commentPreviousContent;
	
	@Column(name = "commentUpdateContent", columnDefinition = "TEXT")
	private String commentUpdateContent;
	
	@Column(name = "updateRecordBelongsCommentId", nullable = false)
	private Integer updateRecordBelongsCommentId;
	
	@JsonBackReference
	@ManyToOne
	@JoinColumn(name = "updateRecordBelongsCommentId", 
	referencedColumnName = "commentId", 
	insertable = false, 
	updatable = false)
	private Comment comment;

	
	
}
