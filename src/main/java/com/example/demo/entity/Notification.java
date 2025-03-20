package com.example.demo.entity;

import java.util.Date;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "notification")
@Component
public class Notification {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	// 依據員工編號還是部門，所以選項只有(empno,deptno)這兩個
	private String basis;

	// 什麼類型的通知
	private String category;

	// 通知內容，例如:本月薪水明細已公布，請查看;有人請假唷麻煩請審核;當日有會議通知麻煩請確認
	private String message;

	// 想要回傳到哪個分頁
	private String retrunPage;

	// 發布時間
	@JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@Temporal(TemporalType.TIMESTAMP)
	@Column(columnDefinition = "datetime2(0)")
	private Date announceTime;

	// 串聯員工表
	@ManyToMany
    @JoinTable(
            name = "notification_employees",
            joinColumns = @JoinColumn(name = "notification_id"),
            inverseJoinColumns = @JoinColumn(name = "empno")
    )
	@JsonIgnore
	private List<Employees> employees;

//	// 假設選擇員工編號，才有輸入這個欄位
//	private String empno;
//
//	// 假設選擇部門，才有輸入這個欄位
//	private String deptno;

//	// 員工是否有已讀
//	private Boolean isRead = false;
}
