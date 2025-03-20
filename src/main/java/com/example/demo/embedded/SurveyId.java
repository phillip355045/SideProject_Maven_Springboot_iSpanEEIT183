package com.example.demo.embedded;

import java.io.Serializable;

import com.example.demo.entity.Employees;
import com.example.demo.entity.RoomReserve;

import jakarta.persistence.Embeddable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Embeddable
public class SurveyId implements Serializable {
// 是用来辅助序列化过程的一个版本控制标识符。它被用于验证序列化的对象和对应类定义是否版本匹配。这是非常重要的，特别是在进行对象序列化和反序列化时，需要确保类的版本兼容性。
	private static final long serialVersionUID = 1L;
	@ManyToOne
    @JoinColumn(name = "roomreserveId")
    private RoomReserve roomReserve;

    @ManyToOne
    @JoinColumn(name = "employeeId")
    private Employees employee;

	public SurveyId() {
		
	}
	

	public SurveyId(RoomReserve roomReserve, Employees employee) {
		super();
		this.roomReserve = roomReserve;
		this.employee = employee;
	}


	public RoomReserve getRoomReserve() {
		return roomReserve;
	}

	public void setRoomReserve(RoomReserve roomReserve) {
		this.roomReserve = roomReserve;
	}

	public Employees getEmployee() {
		return employee;
	}

	public void setEmployee(Employees employee) {
		this.employee = employee;
	}
	
	

}
