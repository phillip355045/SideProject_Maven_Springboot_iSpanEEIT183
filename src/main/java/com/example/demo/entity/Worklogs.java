package com.example.demo.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.CascadeType;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "worklogs")
@Component
public class Worklogs {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long worklogid;
	
    private String mgr;

    @ManyToOne
    @JoinColumn(name = "deptno",nullable = false)
    private Department department;
	
	@ManyToOne
    @JoinColumn(name = "empno",nullable = false)
    private Employees employees;

	@JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@Temporal(TemporalType.DATE)
	private Date worklogdate;

	// 審核 預設為審核中
	private String reviewstatus = "待審核";

	// 做假刪除 預設為0、當等於1的時候就代表已經被使用者刪除，但資料表中繼續留存
	private String fakeDelete = "0";
	
	

	// 增加與 WorklogItem 的關係
	@OneToMany(mappedBy = "worklogs", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<WorklogItem> worklogItems = new ArrayList<>();

	 // 管理關係
    public void addWorklogItem(WorklogItem worklogItem) {
        worklogItems.add(worklogItem);
        worklogItem.setWorklogs(this);
    }

    public void removeWorklogItem(WorklogItem worklogItem) {
        worklogItems.remove(worklogItem);
        worklogItem.setWorklogs(null);
    }
    
    

    // 修改這裡的方法名稱
    public List<WorklogItem> getWorklogItems() {
        return worklogItems;
    }

    public void setWorklogItems(List<WorklogItem> worklogItems) {
        this.worklogItems = worklogItems;
    }

	
	
}