package com.example.demo.dto;

import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PostImageUpdatedDto {
	
	private String postImageId;
	private MultipartFile imageFile;
	private String updateAction;
	private boolean isDeleted;
}
