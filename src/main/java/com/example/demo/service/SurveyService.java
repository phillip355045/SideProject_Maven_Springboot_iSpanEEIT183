package com.example.demo.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.dto.MeetingRoomInfoDTO;
import com.example.demo.embedded.SurveyId;
import com.example.demo.entity.Employees;
import com.example.demo.entity.RoomReserve;
import com.example.demo.entity.Survey;
import com.example.demo.repository.EmployeesRepository;
import com.example.demo.repository.SurveyRepository;

@Service
public class SurveyService {

    @Autowired
    private SurveyRepository surveyRepos;
    @Autowired
    private EmployeesRepository employeesRepo;
    
	//透過姓名or帳號取得所有員工的資料
	public List<Employees> getEmployeesByEmpnoAndNameFuzzySearch(String query){
		return employeesRepo.getEmployeesByEmpnoAndNameFuzzySearch(query);
	}
	
    //會議室邀請參與人員
    public void createSurvey(RoomReserve roomReserve, Employees employee) {
    	Survey survey = new Survey();
    	SurveyId surveyId = new SurveyId(roomReserve,employee);
    	survey.setId(surveyId);
    	surveyRepos.save(survey);
    }
    
    //是否已經邀請
	public Boolean hasInfo(RoomReserve roomReserve, Employees employee) {
		SurveyId surveyId = new SurveyId(roomReserve,employee);
		Optional<Survey> survey = surveyRepos.findById(surveyId);
		if (survey.isPresent()) {
			return true;
		}
		return false;
	}
    
    //會議人員提交feedback但沒有意見
	public void submitSurveyWithoutOpinion(SurveyId surveyId,
    		Integer agendaDegree,
    		Integer solveProblemDegree,
    		Integer satisfiedDegree) {
    	Survey survey = surveyRepos.findById(surveyId).get();
    	survey.setAgendaDegree(agendaDegree);
    	survey.setSolveProblemDegree(solveProblemDegree);
    	survey.setSatisfiedDegree(satisfiedDegree);
    	survey.setCompleted(true);
    	surveyRepos.save(survey);
    }
    
    //會議人員提交feedback有意見
	public void submitSurveyWithoutOpinion(SurveyId surveyId,
    		Integer agendaDegree,
    		Integer solveProblemDegree,
    		Integer satisfiedDegree,
    		String opinion) {
    	Survey survey = surveyRepos.findById(surveyId).get();
    	survey.setAgendaDegree(agendaDegree);
    	survey.setSolveProblemDegree(solveProblemDegree);
    	survey.setSatisfiedDegree(satisfiedDegree);
    	survey.setOpinion(opinion);
    	survey.setCompleted(true);
    	surveyRepos.save(survey);
    }
    
    //用原編查找
    public List<Survey> findByIdEmployeeId(String empno){
    	return surveyRepos.findByIdEmployeeId(empno);
    }
    //用預約編號查找
    public List<Survey> findByIdRoomReserveId(Integer roomReserveId){
    	return surveyRepos.findByIdRoomReserveId(roomReserveId);
    }
    
    //用員工編號來查詢被預約的資料
	public List<MeetingRoomInfoDTO> findMeetingInfoByEmpno(String empno) {
		List<Object[]> results = surveyRepos.findMeetingInfoByEmpno(empno);
		List<MeetingRoomInfoDTO> dtos = new ArrayList<>();
		for (Object[] result : results) {
			MeetingRoomInfoDTO dto = new MeetingRoomInfoDTO(
					(Integer) result[0],
					(String) result[1],
					(String) result[2],
					(String) result[3],
					(String) result[4],
					(Date) result[5],
					(Date) result[6],
					(Date) result[7],
					(String) result[8]);
			dtos.add(dto);
		}
		return dtos;
	}
}
