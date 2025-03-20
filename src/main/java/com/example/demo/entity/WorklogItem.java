package com.example.demo.entity;

import org.springframework.stereotype.Component;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "worklogitem")
@Component
public class WorklogItem {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long itemId;

	private String deptname;

	private String jobType;

	private String jobDescription;

	private Double regularHours;

	private Double overtimeHours;

	// 做假刪除 預設為0、當等於1的時候就代表已經被使用者刪除，但資料庫繼續留存
	private String fakeDelete = "0";

	@ManyToOne
	@JoinColumn(name = "worklogid")
	private Worklogs worklogs;
}
