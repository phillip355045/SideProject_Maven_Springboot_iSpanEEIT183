package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.demo.embedded.SurveyId;
import com.example.demo.entity.Survey;

public interface SurveyRepository extends JpaRepository<Survey, SurveyId> {
	
	@Query(value = "SELECT * FROM Survey s WHERE s.employeeId = ?1",nativeQuery = true)
    List<Survey> findByIdEmployeeId(String employeeId);
	@Query(value = "SELECT * FROM Survey s WHERE s.roomReserveId = ?1",nativeQuery = true)
    List<Survey> findByIdRoomReserveId(Integer roomReserveId);
	
	//找到被預約的人員
    @Query(value = "SELECT s.roomReserveId, r.empno, s.employeeId, r.roomName, r.reserveTitle, r.eventDay, r.startTime, r.endTime, r.onlineMeeting " +
            "FROM Survey s JOIN RoomReserve r ON s.roomReserveId = r.reserveno " +
            "WHERE s.employeeId = ?1", nativeQuery = true)
    List<Object[]> findMeetingInfoByEmpno(String empno);
}
