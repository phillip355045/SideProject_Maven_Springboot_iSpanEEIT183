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
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "postUpdateRecord")
public class PostUpdateRecord {
	
	@Id
	@Column(name = "postUpdateRecordId")
	private String postUpdateRecordId;
	
	@Column(nullable = false)
	@DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")// 告訴程式該如何解析收到的資料(ex. 月份在哪個位置)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")// for json格式資料，告訴程式該如何解析收到的資料(ex. 月份在哪個位置)
	private LocalDateTime postUpdateTime;
	
	@Column(name="contentBeforeUpdate", columnDefinition = "NVARCHAR(MAX)")
	private String contentBeforeUpdate;
	
	@Column(name="contentAfterUpdate", columnDefinition = "NVARCHAR(MAX)")
	private String contentAfterUpdate;
	
	private Boolean isImageUpdated = false;

	@Column(nullable = false)
	private String updateRecordBelonsPostId;
	
	private String updateEmp;
	
	private String updateEmpName;
	
	
	@JsonBackReference
	@JoinColumn(name = "updateRecordBelonsPostId", 
	referencedColumnName = "postId", 
	insertable = false, 
	updatable = false)
	@ManyToOne
	private Post post;
	
	@JsonManagedReference
	@OneToMany(mappedBy = "postUpdateRecord", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<PostImageUpdateRecord> postImageUpdateRecords;
	
}
