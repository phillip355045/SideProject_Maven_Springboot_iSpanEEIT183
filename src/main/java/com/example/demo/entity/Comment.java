package com.example.demo.entity;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "comment")
public class Comment {

	@Id
	@Column(name = "commentId", nullable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer commentId;
	
	@Column(nullable = false)
	@DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")// 告訴程式該如何解析收到的資料(ex. 月份在哪個位置)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")// 告訴程式該如何解析收到的資料(ex. 月份在哪個位置)
	private LocalDateTime commentDateTime;
	
	private Boolean commentStatus = true;
	
	private Integer commentModifiedTimes = 0;
	
	@Column(name = "commentContent", columnDefinition = "TEXT")
	private String commentContent;

	@Column(nullable = false)
	private String commentEmp;
	
	@Transient
	private String commentEmpName;
		
	@Column(nullable = false)
	private String commentBelongsPostId;
	
	@JsonBackReference
	@ManyToOne
	@JoinColumn(name = "commentBelongsPostId", 
	referencedColumnName = "postId", 
	insertable = false, 
	updatable = false)
	private Post post;

	@JsonManagedReference
	@OneToMany(mappedBy = "comment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CommentUpdateRecord> commentUpdateRecords;
	
	
}
