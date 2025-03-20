package com.example.demo.dto;

import java.util.Date;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
public class WorklogsDTO {

	private Long worklogId;
	private String deptno;
    private String empno;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date worklogdate;
    private String reviewstatus = "待審核";
    private List<WorklogItemDTO> worklogItems;
    public void setWorklogItems(List<WorklogItemDTO> worklogItems) {
        this.worklogItems = worklogItems;
    }
}
