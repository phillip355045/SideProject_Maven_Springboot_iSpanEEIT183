package com.example.demo.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PostWallDto {
	private String postId;
	private String postDateTime;
	private String postTitle;
	private String postCategory;
	private String postContent;
	private String postEmp;
	private String postEmpName;
	private Integer likeCounts;
	private Integer commentCounts;
	
	

}
