package com.example.demo.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entity.LeaveType;
import com.example.demo.repository.LeaveTypeRepository;

@Service
public class LeaveTypeService {
	
	@Autowired
	private LeaveTypeRepository leaveTypeRepository;
	
	
	public LeaveType getLeaveType(Long requestTypeID) {
		Optional<LeaveType> leaveType = leaveTypeRepository.findById(requestTypeID);
		
		if (leaveType.isPresent()) {
			return leaveType.get();
		}
		return null;
	}

}
