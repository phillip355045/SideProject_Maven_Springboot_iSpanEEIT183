package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class PostUpdatedRecordDto {
	
	private String postUpdateRecordId;
	private String postUpdateTime;
	private String contentBeforeUpdate;
	private String contentAfterUpdate;
	private String updateEmp;
	private String updateEmpName;
	private String updateRecordBelongsPostId;
	private Boolean isImageUpdated;
	
	

}
