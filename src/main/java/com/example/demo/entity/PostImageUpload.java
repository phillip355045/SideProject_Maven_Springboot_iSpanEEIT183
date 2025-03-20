package com.example.demo.entity;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "postImageUpload")
public class PostImageUpload {
	
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(name = "postImageId")
	private String postImageId;
	
	
	@Column(name="postImageUri", columnDefinition = "NVARCHAR(MAX)")
	private String postImageUri;
	
	@Column(name = "postImageDataType")
	private String postImageDataType;
	
	@Column(name = "postImageStatus")
	private Boolean postImageStatus = true;

	@Lob
	@Column(name = "postImageData", columnDefinition = "VARBINARY(MAX)")
	private byte[] postImageData;
	
	@Transient // This field will not be persisted to the database
    private String base64ImageData; // 新字段用于存储Base64编码的数据
	
	@Column(nullable = false)
	@DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")// 告訴程式該如何解析收到的資料(ex. 月份在哪個位置)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")// for json格式資料，告訴程式該如何解析收到的資料(ex. 月份在哪個位置)
	private LocalDateTime postImageUploadTime;

	@JsonManagedReference
	@OneToMany(mappedBy = "postImageUpload", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostImageBelongsRecord> postImageBelongsRecords;
	
	@JsonManagedReference
	@OneToMany(mappedBy = "postImageUpload", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<PostImageUpdateRecord> postImageUpdateRecords;
	
	
	
}
