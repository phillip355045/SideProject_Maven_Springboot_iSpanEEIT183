package com.example.demo.entity;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name="post")
public class Post {
	
//	屬性，對應DB的Table欄位名
//	我的主鍵型別為String，@GeneratedValue通常用於數字型別主鍵，目前直接用給的，暫不設定
	@Id
	@Column(name="postId")
	private String postId;
	
	@DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")// 告訴程式該如何解析收到的資料(ex. 月份在哪個位置)
	@Column(name="postDate")
	private LocalDateTime postDate;
	
	@DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")// 告訴程式該如何解析收到的資料(ex. 月份在哪個位置)
	@Column(name="postExpiryDate")
	private LocalDateTime postExpiryDate;
	
//	true = can find
	@Column(name = "postStatus")
	private Boolean postStatus = true;
	
	@Column(name = "postModifiedTimes")
	private Integer postModifiedTimes = 0;
	
	@Column(name="postTitle")
	private String postTitle;
	
	@Column(name="postCategory")
	private String postCategory;
	
	@Column(name="postContent", columnDefinition = "NVARCHAR(MAX)")
	private String postContent;
	
	@Column(name="postEmp")
	private String postEmp;
	
	@Transient
	private String postEmpName;

	@JsonManagedReference
	@OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments;
	
	@JsonManagedReference
	@OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Reaction> reactions;
	
	@JsonManagedReference
	@OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<PostUpdateRecord> postUpdateRecords;
	
	@JsonManagedReference
	@OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostImageBelongsRecord> postImageBelongsRecords;
	
	
//	NoArgsConstrutor，若後續導入lombok套件，無參數Constructor可以用@Annotation方式取代
	public Post() {

	}

//  中介表需要利用從前端收到postId的值建立實體
	public Post(String postId) {
	
	this.postId = postId;
	}
	


//	AllArgsConstrutor，導入lombok套件，所有參數Constructor可以用@Annotation方式取代
	
//	getter & setter，導入lombok套件，可以用@Annotation方式取代

	


}
