package com.example.demo.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.aspectj.apache.bcel.classfile.Module.Require;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.demo.dto.MeetingInfoRequestDTO;
import com.example.demo.dto.MeetingRoomInfoDTO;
import com.example.demo.dto.SurveyDTO;
import com.example.demo.dto.SurveyFeedbackDTO;
import com.example.demo.embedded.SurveyId;
import com.example.demo.entity.Employees;
import com.example.demo.entity.Files;
import com.example.demo.entity.RoomReserve;
import com.example.demo.entity.Survey;
import com.example.demo.service.EmployeesService;
import com.example.demo.service.FileService;
import com.example.demo.service.RoomReserveService;
import com.example.demo.service.SurveyService;
import com.example.demo.util.TimeTransfer;

import jakarta.servlet.http.HttpSession;

@Controller
public class SurveyController {
	@Autowired
	private RoomReserveService roomReserveService;
	@Autowired
	private EmployeesService employeesService;

	@Autowired
	private SurveyService surveyService;
	
	@Autowired
	private FileService fileService;

	//人員模糊查詢
	@GetMapping("/GetEmployeesByEmpnoAndNameFuzzySearch")
	@ResponseBody
	public List<Employees> getEmployeesByEmpnoAndNameFuzzySearch(HttpSession session, @RequestParam String query){
		String empno = (String)session.getAttribute("account");
		
		System.out.println(empno);
		List<Employees> result = surveyService.getEmployeesByEmpnoAndNameFuzzySearch(query);		
		//移除當前帳號的選項
		for(int i = 0; i<result.size(); i++) {
			Employees employee = result.get(i);
			if (empno.equals(employee.getEmpno())) {
				result.remove(i);
			}
		}	
		return result;
	}
	
	// 準備通知與會人員畫面
	@GetMapping("/MeetingInfo.controller")
	public String MeetingInfo1(@RequestParam String reserveno, Model model) {
		Integer reserveno1 = Integer.parseInt(reserveno);
		List<Survey> result = surveyService.findByIdRoomReserveId(reserveno1);
		String empno = "";
		for (Survey s : result) {
			SurveyId id = s.getId();
			String empno2 = id.getEmployee().getEmpno();
			String eName = id.getEmployee().getName();
			empno += empno2 + eName + "、";
		}
		//移除末尾的分號
	    if (!empno.isEmpty() && empno.endsWith("、")) {
	        empno = empno.substring(0, empno.length() - 1);
	    }
		model.addAttribute("members", empno);
		model.addAttribute("reserveno", reserveno);
		return "meetingRoom/MeetingInfo";
	}
	
	//新增會議人員
	@ResponseBody
	@PostMapping("/MeetingInfoForInsert")
	public ResponseEntity<String> MeetingForInfo(@RequestBody MeetingInfoRequestDTO meetingInfoRequestDTO,Model model) {
		Integer reserveno = Integer.valueOf(meetingInfoRequestDTO.getReserveno());
		RoomReserve reserveRoom = roomReserveService.findById(reserveno);
		System.out.println("------"+reserveRoom.getEmpno());
		LocalDateTime startTime = TimeTransfer.connectDateAndTime(reserveRoom.getEventDay(),reserveRoom.getStartTime());
		LocalDateTime endTime = TimeTransfer.connectDateAndTime(reserveRoom.getEventDay(),reserveRoom.getEndTime());
		LocalDateTime now = LocalDateTime.now();
		if (startTime.isBefore(now) && endTime.isAfter(now)) {
//			model.addAttribute("errorMsg","會議進行中");
//			System.out.println("123456");
//			return "meetingRoom/MeetingInfo";
			String errorMsg = "會議進行中";
			return ResponseEntity.ok(errorMsg);
		}
		if(endTime.isBefore(now)) {
//			model.addAttribute("errorMsg","會議已結束");
//			System.out.println("78910");
//			return "meetingRoom/MeetingInfo";
			String errorMsg = "會議已結束";
			return ResponseEntity.ok(errorMsg);
		}
		
		List<String> empnoList = meetingInfoRequestDTO.getEmpnoList();
		for (String empno : empnoList) {		
			Employees employee = employeesService.findUsersByEmpno(empno);
			Boolean hasInfo = surveyService.hasInfo(reserveRoom, employee);
			if (hasInfo) {
					continue;
				}
				surveyService.createSurvey(reserveRoom, employee);
			}
		
		return ResponseEntity.ok("通知成功");
	}	
	
	//抓取參與會議名單以及資料到前端顯示
	@GetMapping("/GetInfoAndFileJson")
	@ResponseBody
	public List<MeetingRoomInfoDTO> getInfoAndFileJson(HttpSession session) {	
		String empno = (String)session.getAttribute("account");
		List<MeetingRoomInfoDTO> result = surveyService.findMeetingInfoByEmpno(empno);
		
		ArrayList<MeetingRoomInfoDTO> todayMeetingRoomInfoDTO = new ArrayList<MeetingRoomInfoDTO>();
		LocalDate today = LocalDate.now();
		for (MeetingRoomInfoDTO meetingRoomInfoDTO : result) {
			LocalDate eventDay = TimeTransfer.dateToLocalDate(meetingRoomInfoDTO.getEventDay());
			if (today.equals(eventDay)) {
				Employees sponsor = employeesService.findUsersByEmpno(meetingRoomInfoDTO.getSponsor());
				meetingRoomInfoDTO.setSponsorName(sponsor.getName());
				todayMeetingRoomInfoDTO.add(meetingRoomInfoDTO);
			}
		}		
		List<Files> findAll = fileService.findAll();
		for (MeetingRoomInfoDTO dto : todayMeetingRoomInfoDTO) {
			Boolean hasFile = findAll.stream().anyMatch(file -> file.getReserveno().equals(dto.getReserveno()));
			if (hasFile) {
				dto.setHasFile(hasFile);
			} else {
				dto.setHasFile(false);
			}
		}	
		return todayMeetingRoomInfoDTO;
	}
	
	
	//會議評分
	@GetMapping("/FeedbackViewForSponsor")
	public String feedbackViewForSponsor(HttpSession session,Model model) {
		String empno = (String)session.getAttribute("account");
		//該員工所有的預約紀錄
		List<RoomReserve> resultList = roomReserveService.findReservaionByEmpno(empno);
		List<SurveyFeedbackDTO> surveyFeedBackDtoList = new ArrayList<SurveyFeedbackDTO>();
		for (RoomReserve roomReserve : resultList) {
			if(!roomReserve.getSurveys().isEmpty()) {//判斷有邀請會議人員再顯示
				List<Survey> surveyList = roomReserve.getSurveys();
				Integer total = surveyList.size(); 
				Integer feedbackNumber = 0;
				Integer totalScore = 0;
				double averagescore = 0.0;
				String opinionList = "<ul>";
				for (Survey survey : surveyList) {
					if (survey.isCompleted()) {
						feedbackNumber++;
						Integer singleScore = survey.getAgendaDegree()+survey.getSolveProblemDegree()+survey.getSatisfiedDegree();
						totalScore += singleScore;
					}
					if(survey.getOpinion() != null) {
						String opinion = "<li>"+ survey.getOpinion() + "</li>";
						opinionList += opinion;					
					}
				}
				opinionList += "</ul>";
				if(totalScore != 0) {
					averagescore = totalScore / feedbackNumber;
				}
				
				SurveyFeedbackDTO surveyFeedbackDTO = new SurveyFeedbackDTO(
						roomReserve.getReserveno(),
						roomReserve.getReserveTitle(),
						roomReserve.getEventDay(),
						roomReserve.getStartTime(),
						roomReserve.getEndTime(),
						total,feedbackNumber,averagescore,
						opinionList);			
				surveyFeedBackDtoList.add(surveyFeedbackDTO);
			}	
			
		}
		model.addAttribute("feedbackList", surveyFeedBackDtoList);
		return "meetingRoom/MeetingRecord";
	}
	
	//準備會議feedback表單
	@GetMapping("/feedbackView")
	public String getInvitedMeeting(HttpSession session,Model model){
		String empno = (String)session.getAttribute("account");
		//取得會議記錄
		List<Survey> result = surveyService.findByIdEmployeeId(empno);
		List<Files> findAll2 = fileService.findAll();
		
		List<SurveyDTO> surveyDTO = new ArrayList<SurveyDTO>();
		for (Survey survey : result) {
			Employees employee = employeesService.findUsersByEmpno(survey.getId().getRoomReserve().getEmpno());		
			SurveyDTO surveyDTO2 = new SurveyDTO(
					survey.getId().getRoomReserve().getEmpno(),
					survey.getId().getRoomReserve().getReserveno(),
					survey.getId().getRoomReserve().getReserveTitle(),
					survey.getId().getRoomReserve().getEventDay(),
					survey.getId().getRoomReserve().getStartTime(),
					survey.getId().getRoomReserve().getEndTime(),
					survey.isCompleted());			
			surveyDTO2.setOnlineMeeting(survey.getId().getRoomReserve().getOnlineMeeting());
			surveyDTO2.setSponsorName(employee.getName());
			Boolean hasFile = findAll2.stream().anyMatch(file -> file.getReserveno().equals(survey.getId().getRoomReserve().getReserveno()));
			if (hasFile) {
				surveyDTO2.setHasFile(hasFile);
			} 
			surveyDTO.add(surveyDTO2);
		}
		
		// 每一個預約只抓取多筆檔案的其中一筆，解決前端會顯示多筆資料的問題
		List<Files> pickFirstOne = new ArrayList<>();
		for (SurveyDTO s : surveyDTO) {
			for (Files files : findAll2) {
				if (s.getReserveno().equals(files.getReserveno())) {
					pickFirstOne.add(files);
					break;
				}
			}
		}
		
		//該員工所有的預約紀錄
		List<RoomReserve> resultList = roomReserveService.findReservaionByEmpno(empno);
		List<SurveyFeedbackDTO> surveyFeedBackDtoList = new ArrayList<SurveyFeedbackDTO>();
		for (RoomReserve roomReserve : resultList) {
			List<Survey> surveyList = roomReserve.getSurveys();
			Integer total = surveyList.size(); 
			Integer feedbackNumber = 0;
			Integer totalScore = 0;
			Double averagescore = 0.0;
//			List<String> opinionList = new ArrayList<>();
			String opinionList = "<ul>";
			for (Survey survey : surveyList) {
				if (survey.isCompleted()) {
					feedbackNumber++;
					Integer singleScore = survey.getAgendaDegree()+survey.getSolveProblemDegree()+survey.getSatisfiedDegree();
					totalScore += singleScore;
				}
				if(survey.getOpinion() != null) {
					String opinion = "<li>"+ survey.getOpinion() + "</li>";
					opinionList += opinion;
//					opinionList.add(survey.getOpinion());
					
				}
			}
			opinionList += "</ul>";
			if(totalScore != 0) {
				averagescore = (double)totalScore / feedbackNumber;
			}
			SurveyFeedbackDTO surveyFeedbackDTO = new SurveyFeedbackDTO(
					roomReserve.getReserveno(),
					roomReserve.getReserveTitle(),
					roomReserve.getEventDay(),
					roomReserve.getStartTime(),
					roomReserve.getEndTime(),
					total,feedbackNumber,averagescore,
					opinionList);				
			surveyFeedBackDtoList.add(surveyFeedbackDTO);
			
		}		
		model.addAttribute("invitedList", surveyDTO);
		model.addAttribute("files", pickFirstOne);
		model.addAttribute("feedbackList", surveyFeedBackDtoList);
		return "meetingRoom/MeetingFeedback";
	}
	
	//新增會議室意見
    @PostMapping("/submitSurvey")
    public String submitSurvey(
    		HttpSession session,
    		@RequestParam(name = "reserveno", required = true) Integer reserveno,
            @RequestParam(name = "question1", required = true) Integer question1,
            @RequestParam(name = "question2", required = true) Integer question2,
            @RequestParam(name = "question3", required = true) Integer question3,
            @RequestParam(name = "feedback", required = true) String feedback,
            Model model) {

    	String empno = (String)session.getAttribute("account");
    	Employees employee = employeesService.findUsersByEmpno(empno);
    	RoomReserve roomReserve = roomReserveService.findById(reserveno);
    	SurveyId surveyId = new SurveyId(roomReserve,employee);
    	
    	if (feedback == null || feedback.trim().isEmpty()) {
			surveyService.submitSurveyWithoutOpinion(surveyId,question1, question2, question3);
    	}else {
			surveyService.submitSurveyWithoutOpinion(surveyId, question1, question2, question3, feedback);
		}
        return "redirect:feedbackView";
    }
    
    
}
