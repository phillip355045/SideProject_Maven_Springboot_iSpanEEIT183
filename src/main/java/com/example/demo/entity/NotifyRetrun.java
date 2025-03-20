package com.example.demo.entity;

import org.springframework.stereotype.Component;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "notifyRetrun")
@Component
public class NotifyRetrun {

	// 通知設定邏輯
	// 在資料庫中建立 如果發送什麼類型的通知要retrun 哪個mapping，直接帶入成th:href
	// 可以透過網頁手動發送通知，也可以在大家的功能加上一個notification的API(basis定義empno)
	// 並且在大家的html設定一個變數名稱為:category
	// 假設員工請假按下送出紐時(在送出紐中的API裡增加notification的API)，就會將審核人1設定為收到通知的人
	// 當審核人1按下通過，就會傳送通知給審核人2
	// 假設新增薪資明細，就是發送給所有empno
	// 假設發公告也是發送給所有empno
	// 假設會議室租借，就是發送給收到通知的人，審核的部分跟請假一樣，可以提醒有申請須審核
	// 假設工作日誌，可以設定於每天下午17:00通知要提交工作日誌，或是可以手動提醒整個部門人員或單一員工提交工作日誌

	// 前端(html)加上的內容
	// <input type="hidden" value="員工資料修改" name="category" id="category">
	// <input type="hidden" th:value="${emp.empno}" name="notifyEmpno" id="notifyEmpno">
	/*
	 * 前端傳的是List<String>
	 * <select multiple class="form-control" id="notifyEmpno"
	 * name="notifyEmpno"> <option th:each="employee : ${employees}"
	 * th:value="${employee.empno}" th:text="${employee.name}"></option> </select>
	 */

	// 後段(Controller)加上的內容
	// @RequestParam String category,@RequestParam String notifyEmpno
	// 假設empno傳的是List<String> @RequestParam List<String> notifyEmpno
	// notificationService.createNotificationBasisOnEmpno(category, notifyEmpno);

	// SQL資料庫 NotifyRetrun 存入
	// insert into notifyRetrun (category,message,retrunPage,pageName)
	// values('員工資料修改','員工資料修改成功!!!!!!','http://localhost:8088/PSNEXUS/getEmpByAccount')

	// 什麼類型的通知
	@Id
	private String category;

	// 想要回傳到哪個分頁
	private String retrunPage;

	// 想要回傳到哪個分頁
	private String message;
	
//	// 回傳頁面名稱
//	private String pageName;
}
