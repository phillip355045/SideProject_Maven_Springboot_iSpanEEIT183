package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.LeaveType;

public interface LeaveTypeRepository extends JpaRepository<LeaveType,Long>{

	LeaveType findByRequestTypeID(Long requestTypeID);
}
