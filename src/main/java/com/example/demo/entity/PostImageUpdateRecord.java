package com.example.demo.entity;

import com.example.demo.embedded.PostImageUpdateRecordId;
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

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "postImageUpdateRecord")
public class PostImageUpdateRecord {
	
	@EmbeddedId
	private PostImageUpdateRecordId postImageUpdateRecordId;
	
	@JsonBackReference
	@ManyToOne
	@MapsId("imageUploadUpdateId")
	@JoinColumn(name = "imageUploadUpdateId", referencedColumnName = "postImageId")
	private PostImageUpload postImageUpload;
	
	@JsonBackReference
	@ManyToOne
	@MapsId("updateRecordForImageId")
	@JoinColumn(name = "updateRecordForImageId", referencedColumnName = "postUpdateRecordId")
	private PostUpdateRecord postUpdateRecord;
	
	

}
