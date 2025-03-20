package com.example.demo.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
public class WorklogItemDTO {
	
	private String deptname;
    private String jobType;
    private String jobDescription;
    private double  regularHours;
    private double  overtimeHours;
	
}
