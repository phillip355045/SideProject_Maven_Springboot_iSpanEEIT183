package com.example.demo.entity;

import com.example.demo.embedded.SurveyId;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;

@Entity
@Table(name="survey")
public class Survey {
	
	@EmbeddedId
	private SurveyId id;
	
	@MapsId("roomReserve")
	@ManyToOne
	@JoinColumn(name="roomreserveId")
	private RoomReserve roomReserve;
	
	@MapsId("employee")
	@ManyToOne
	@JoinColumn(name="employeeId")
	private Employees employee;
	
	private Integer agendaDegree;
	
	private Integer solveProblemDegree;
	
	private Integer satisfiedDegree;
	
	private String opinion;
	
	
	private boolean completed;
	
	
	public Survey() {
		this.completed = false;//初始化
	}
	

	public SurveyId getId() {
		return id;
	}


	public void setId(SurveyId id) {
		this.id = id;
	}


	public Integer getAgendaDegree() {
		return agendaDegree;
	}


	public void setAgendaDegree(Integer agendaDegree) {
		this.agendaDegree = agendaDegree;
	}


	public Integer getSolveProblemDegree() {
		return solveProblemDegree;
	}


	public void setSolveProblemDegree(Integer solveProblemDegree) {
		this.solveProblemDegree = solveProblemDegree;
	}


	public Integer getSatisfiedDegree() {
		return satisfiedDegree;
	}


	public void setSatisfiedDegree(Integer satisfiedDegree) {
		this.satisfiedDegree = satisfiedDegree;
	}


	public String getOpinion() {
		return opinion;
	}


	public void setOpinion(String opinion) {
		this.opinion = opinion;
	}

	public boolean isCompleted() {
		return completed;
	}


	public void setCompleted(boolean completed) {
		this.completed = completed;
	}
	
	
	
	
	
	
	

}
