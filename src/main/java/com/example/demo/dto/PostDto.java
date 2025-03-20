package com.example.demo.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PostDto {
	private String postId;
	private String postDate;
	private String postExpiryDate;
	private Boolean postStatus;
	private String postTitle;
	private String postCategory;
	private String postContent;
	private String postEmp;
	private String postEmpName;

}
