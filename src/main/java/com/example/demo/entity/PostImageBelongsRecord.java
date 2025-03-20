package com.example.demo.entity;

import com.example.demo.embedded.PostImageBelongsRecordId;
import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "postImageBelongsRecord")
@Entity
public class PostImageBelongsRecord {
	
	
	
	@EmbeddedId
	private PostImageBelongsRecordId postImageBelongsRecordId;
	
	@JsonBackReference
	@ManyToOne
	@MapsId("belongsPostId")
	@JoinColumn(name = "belongsPostId", referencedColumnName = "postId")
	private Post post;
	
	@JsonBackReference
	@ManyToOne
	@MapsId("belongsImageId")
	@JoinColumn(name = "belongsImageId", referencedColumnName = "postImageId")
	private PostImageUpload postImageUpload;
	
	private Boolean isImageEnabled = true; // 新增字段，管理圖片在特定貼文中的上下架狀態
	
}
