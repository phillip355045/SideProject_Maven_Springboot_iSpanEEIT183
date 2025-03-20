package com.example.demo.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Map;

import org.hibernate.dialect.function.array.ArrayRemoveIndexUnnestFunction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.example.demo.entity.Employees;
import com.example.demo.entity.Notification;
import com.example.demo.entity.NotificationEmployees;
import com.example.demo.entity.NotifyRetrun;
import com.example.demo.entity.Post;
import com.example.demo.entity.RoomReserve;
import com.example.demo.entity.Survey;
import com.example.demo.repository.EmployeesRepository;
import com.example.demo.repository.NotificationEmployeesRepository;
import com.example.demo.repository.NotificationRepository;
import com.example.demo.repository.NotifyRetrunRepository;
import com.example.demo.repository.PostRepository;
import com.example.demo.repository.RoomReserveRepository;
import com.example.demo.repository.SurveyRepository;
import com.example.demo.util.TimeTransfer;

import jakarta.servlet.http.HttpSession;

@Service
public class NotificationService {
	@Autowired
	private NotificationRepository notificationRepository;

	@Autowired
	private EmployeesRepository employeesRepository;

	@Autowired
	private NotificationEmployeesRepository notificationEmployeesRepository;

	@Autowired
	private NotifyRetrunRepository notifyRetrunRepository;
	
	@Autowired
	private RoomReserveRepository roomReserveRepo;
	
	@Autowired
	private SurveyRepository surveyRepo;
	
	@Autowired
	private PostRepository postRepo;

	public Notification createNotification(String basis, String category, String message, String returnPage,
			List<String> empnos) {
		Notification notification = new Notification();
		notification.setBasis(basis);
		notification.setCategory(category);
		notification.setMessage(message);
		notification.setRetrunPage(returnPage);
		notification.setAnnounceTime(new Date());
		notification = notificationRepository.save(notification);

		for (String empno : empnos) {
			Optional<Employees> employeeOptional = employeesRepository.findById(empno);
			if (employeeOptional.isPresent()) {
				Employees employee = employeeOptional.get();
				NotificationEmployees notificationEmployee = new NotificationEmployees();
				notificationEmployee.setNotification(notification);
				notificationEmployee.setEmployees(employee);
				notificationEmployee.setIsRead(false);
				notificationEmployeesRepository.save(notificationEmployee);
			}
		}
		return notification;

	}

	public Notification createNotificationBydeptno(String basis, String category, String message, String returnPage,
			List<String> deptnos) {

		Notification notification = new Notification();
		notification.setBasis(basis);
		notification.setCategory(category);
		notification.setMessage(message);
		notification.setRetrunPage(returnPage);
		notification.setAnnounceTime(new Date());
		notification = notificationRepository.save(notification);

		for (String deptno : deptnos) {
			List<Employees> employeesList = employeesRepository.findByDeptno(deptno);
			for (Employees emp : employeesList) {
				NotificationEmployees notificationEmployee = new NotificationEmployees();
				notificationEmployee.setNotification(notification);
				notificationEmployee.setEmployees(emp);
				notificationEmployee.setIsRead(false);
				notificationEmployeesRepository.save(notificationEmployee);
			}
		}
		return notification;

	}

	public Notification createNotificationBasisOnEmpno(String category, List<String> empnos) {

		Notification notification = new Notification();
		notification.setBasis("empno");
		notification.setCategory(category);

		// message跟returnPage透過category代入
		Optional<NotifyRetrun> optional = notifyRetrunRepository.findById(category);
		if (optional.isPresent()) {
			String message = optional.get().getMessage();
			String returnPage = optional.get().getRetrunPage();
			notification.setMessage(message);
			notification.setRetrunPage(returnPage);
		}

		notification.setAnnounceTime(new Date());
		notification = notificationRepository.save(notification);

		for (String empno : empnos) {
			Optional<Employees> employeeOptional = employeesRepository.findById(empno);
			if (employeeOptional.isPresent()) {
				Employees employee = employeeOptional.get();
				NotificationEmployees notificationEmployee = new NotificationEmployees();
				notificationEmployee.setNotification(notification);
				notificationEmployee.setEmployees(employee);
				notificationEmployee.setIsRead(false);
				notificationEmployeesRepository.save(notificationEmployee);
			}
		}
		return notification;

	}

	public Notification createNotificationBasisOnEmpno(String category, String empno) {

		Notification notification = new Notification();
		notification.setBasis("empno");
		notification.setCategory(category);

		// message跟returnPage透過category代入
		Optional<NotifyRetrun> optional = notifyRetrunRepository.findById(category);
		if (optional.isPresent()) {
			String message = optional.get().getMessage();
			String returnPage = optional.get().getRetrunPage();
			notification.setMessage(message);
			notification.setRetrunPage(returnPage); 
		}

		notification.setAnnounceTime(new Date());
		notification = notificationRepository.save(notification);

		Optional<Employees> employeeOptional = employeesRepository.findById(empno);
		if (employeeOptional.isPresent()) {
			Employees employee = employeeOptional.get();
			NotificationEmployees notificationEmployee = new NotificationEmployees();
			notificationEmployee.setNotification(notification);
			notificationEmployee.setEmployees(employee);
			notificationEmployee.setIsRead(false);
			notificationEmployeesRepository.save(notificationEmployee);
		}
		return notification;
	}

	public Notification createNotificationBasisOnEmpno(String category, String empno, String account) {

		Notification notification = new Notification();
		notification.setBasis("empno");
		notification.setCategory(category);

		// message跟returnPage透過category代入
		Optional<NotifyRetrun> optional = notifyRetrunRepository.findById(category);
		if (optional.isPresent()) {
			String message = optional.get().getMessage();
			String returnPage = optional.get().getRetrunPage();
			String name = employeesRepository.findById(account).get().getName();
			notification.setMessage(account + " " + name + " " + message);
			notification.setRetrunPage(returnPage + account);
		}

		notification.setAnnounceTime(new Date());
		notification = notificationRepository.save(notification);

		Optional<Employees> employeeOptional = employeesRepository.findById(empno);
		if (employeeOptional.isPresent()) {
			Employees employee = employeeOptional.get();
			NotificationEmployees notificationEmployee = new NotificationEmployees();
			notificationEmployee.setNotification(notification);
			notificationEmployee.setEmployees(employee);
			notificationEmployee.setIsRead(false);
			notificationEmployeesRepository.save(notificationEmployee);
		}
		return notification;
	}

	// 可以將訊息代為變數，例如我在資料庫存入 訊息為:於X調動部門為Y!!
	public Notification createNotificationBasisOnEmpno(String category, String empno, String varA, String varB) {

		Notification notification = new Notification();
		notification.setBasis("empno");
		notification.setCategory(category);

		// message跟returnPage透過category代入
		Optional<NotifyRetrun> optional = notifyRetrunRepository.findById(category);
		if (optional.isPresent()) {
			String message = optional.get().getMessage().replace("X", varA).replace("Y", varB);
			String returnPage = optional.get().getRetrunPage();
			notification.setMessage(message);
			notification.setRetrunPage(returnPage);
		}

		notification.setAnnounceTime(new Date());
		notification = notificationRepository.save(notification);

		Optional<Employees> employeeOptional = employeesRepository.findById(empno);
		if (employeeOptional.isPresent()) {
			Employees employee = employeeOptional.get();
			NotificationEmployees notificationEmployee = new NotificationEmployees();
			notificationEmployee.setNotification(notification);
			notificationEmployee.setEmployees(employee);
			notificationEmployee.setIsRead(false);
			notificationEmployeesRepository.save(notificationEmployee);
		}
		return notification;
	}

	// 工作日誌設定提醒 dolly增加 
	// @Scheduled(cron = "0 0 17 * * ?") // 固定下午五點發通知
//	 @Scheduled(cron = "45 * * * * ?") // 測試每分鐘發一次通知
//	@Scheduled(cron = "0 13 10 * * ?")
	@Scheduled(cron = "0 52 14 * * ?") // 固定13:40發通知(正式專題時，使用這個)
	public void dailyWorklogReminder() {
		NotifyRetrun notifyRetrun = notifyRetrunRepository.findByCategory("工作日誌提醒");

		if (notifyRetrun != null) {
			List<Employees> employees = employeesRepository.findAll();
			for (Employees employee : employees) {
				sendNotification(employee.getEmpno(), notifyRetrun);
			}
		} else {
		}
	}

	// 發送給所有員工
	public Notification sendNotification(String empno, NotifyRetrun notifyRetrun) {
		Notification notification = new Notification();
		notification.setBasis("empno");
		notification.setCategory(notifyRetrun.getCategory());
		notification.setMessage(notifyRetrun.getMessage());
		notification.setRetrunPage(notifyRetrun.getRetrunPage());
		notification.setAnnounceTime(new Date());
//		notification.setEmployees(List.of(employeesRepository.findByEmpno(empno)));
		notificationRepository.save(notification);

		
		Optional<Employees> employeeOptional = employeesRepository.findById(empno);
		if (employeeOptional.isPresent()) {
			Employees employee = employeeOptional.get();
			NotificationEmployees notificationEmployee = new NotificationEmployees();
			notificationEmployee.setNotification(notification);
			notificationEmployee.setEmployees(employee);
			notificationEmployee.setIsRead(false);
			notificationEmployeesRepository.save(notificationEmployee);
		}
		return notification;
	}

//		NotificationEmployees notificationEmployees = new NotificationEmployees();
//		notificationEmployees.setNotification(notification);
//		notificationEmployees.setEmployees(employeesRepository.findByEmpno(empno));
//		notificationEmployees.setIsRead(false);
//		notificationEmployeesRepository.save(notificationEmployees);
//	}
	
	
	//會議開始提醒
	@Scheduled(initialDelay = 0, fixedRate = 1800000)//每30分鐘執行一次 
	public Notification reservePass() {
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime nowPlusThirty = now.plusMinutes(480);//抓八個小時內即將進行的會議室通知(為了DEMO) 

		List<RoomReserve> upcomingMeetings = new ArrayList<RoomReserve>();//30分鐘內的會議
		for (RoomReserve roomReserve : roomReserveRepo.findAll()) {
			if (roomReserve.getStatus().equals("放行")) {
				LocalDateTime startTime = TimeTransfer.connectDateAndTime(
						roomReserve.getEventDay(), roomReserve.getStartTime());				
				if(startTime.isAfter(now) && startTime.isBefore(nowPlusThirty)) {
					upcomingMeetings.add(roomReserve);
				}
			}
		}	
		//
		List<List<Survey>> survetListList = new ArrayList<List<Survey>>();
		for (RoomReserve roomReserve : upcomingMeetings) {
			List<Survey> result = surveyRepo.findByIdRoomReserveId(roomReserve.getReserveno());
			survetListList.add(result);
		}
		
		Notification notification = new Notification();
		notification.setBasis("empno");
		notification.setCategory("會議室通知");//會議室通知
		
		for (List<Survey> list : survetListList) {//每一個預約的參與人員只取出第一筆
			if (list != null && !list.isEmpty() && list.get(0) != null) {	
				
				Survey getOne = list.get(0);
				// message跟returnPage透過category代入
				Optional<NotifyRetrun> optional = notifyRetrunRepository.findById("會議室通知");
				//取出通知類型
				if (optional.isPresent()) {
					String message = optional.get().getMessage()
							.replace("X", getOne.getId().getRoomReserve().getStartTime().toString())
							.replace("Y", getOne.getId().getRoomReserve().getEndTime().toString())
							.replace("Z", getOne.getId().getRoomReserve().getRoomName());
					String returnPage = optional.get().getRetrunPage();
					notification.setMessage(message);
					notification.setRetrunPage(returnPage);
					notification.setAnnounceTime(new Date());
					notification = notificationRepository.save(notification);//存入通知
				}
				for (Survey list2 : list) {//通知人員
					//看有誰需要通知
					Optional<Employees> employeeOptional = employeesRepository.findById(list2.getId().getEmployee().getEmpno());
					if (employeeOptional.isPresent()) {
						Employees employee = employeeOptional.get();
						NotificationEmployees notificationEmployee = new NotificationEmployees();
						notificationEmployee.setNotification(notification);
						notificationEmployee.setEmployees(employee);
						notificationEmployee.setIsRead(false);
						notificationEmployeesRepository.save(notificationEmployee);
					}			
				}
			}	
		}		
		return notification;
	}

	
	
	//貼文留言提醒發文者: 留言者放X、postId放Y
	public Notification commentNotification(String commentBelongsPostId, String commentEmpName) {
		
		
		Post post = null;
		Optional<Post> optionalPost = postRepo.findById(commentBelongsPostId);
		if (optionalPost.isPresent()) {
			
			post = optionalPost.get();
			
		}
		//
		String empno = post.getPostEmp();
		
		Notification notification = new Notification();
		
		notification.setBasis(empno);
		notification.setCategory("留言提醒");
		
		Optional<NotifyRetrun> optionNotice = notifyRetrunRepository.findById("留言提醒");
		
		if (optionNotice.isPresent()) {
			NotifyRetrun noteInfos = optionNotice.get();
			
			String message = noteInfos.getMessage()
			.replace("X", commentEmpName)
			.replace("Y", commentBelongsPostId);
			
			//"http://localhost:8088/PSNEXUS/findPostById.controller?postId=" + "postId"
			String returnPage = noteInfos.getRetrunPage() + commentBelongsPostId;
			System.out.println(returnPage);
			
			notification.setMessage(message);
			notification.setRetrunPage(returnPage);
			notification.setAnnounceTime(new Date());
			
			notification = notificationRepository.save(notification);
			
		}
		
		Optional<Employees> optionEmp = employeesRepository.findById(empno);
		if (optionEmp.isPresent()) {
			
			Employees employees = optionEmp.get();
			
			NotificationEmployees notificationEmployees = new NotificationEmployees();
			
			notificationEmployees.setNotification(notification);
			notificationEmployees.setEmployees(employees);
			notificationEmployees.setIsRead(false);
			
			notificationEmployeesRepository.save(notificationEmployees);
			
		}

		return notification;

	}
	
	
	//貼文按讚提醒發文者: 按讚者放X、postId放Y
	public Notification reactionNotification(String reactionBelongsPostId, String reactionEmpName) {
		
		
		Post post = null;
		Optional<Post> optionalPost = postRepo.findById(reactionBelongsPostId);
		if (optionalPost.isPresent()) {
			
			post = optionalPost.get();
			
		}
		//
		String empno = post.getPostEmp();
		
		Notification notification = new Notification();
		
		notification.setBasis(empno);
		notification.setCategory("按讚提醒");
		
		Optional<NotifyRetrun> optionNotice = notifyRetrunRepository.findById("按讚提醒");
		
		if (optionNotice.isPresent()) {
			NotifyRetrun noteInfos = optionNotice.get();
			
			String message = noteInfos.getMessage()
			.replace("X", reactionEmpName)
			.replace("Y", reactionBelongsPostId);
			
			//"http://localhost:8088/PSNEXUS/findPostById.controller?postId=" + "postId"
			String returnPage = noteInfos.getRetrunPage() + reactionBelongsPostId;
			System.out.println(returnPage);
			
			notification.setMessage(message);
			notification.setRetrunPage(returnPage);
			notification.setAnnounceTime(new Date());
			
			notification = notificationRepository.save(notification);
			
		}
		
		Optional<Employees> optionEmp = employeesRepository.findById(empno);
		if (optionEmp.isPresent()) {
			
			Employees employees = optionEmp.get();
			
			NotificationEmployees notificationEmployees = new NotificationEmployees();
			
			notificationEmployees.setNotification(notification);
			notificationEmployees.setEmployees(employees);
			notificationEmployees.setIsRead(false);
			
			notificationEmployeesRepository.save(notificationEmployees);
			
		}

		return notification;

	}
	

}
