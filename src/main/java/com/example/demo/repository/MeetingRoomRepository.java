package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.demo.entity.MeetingRoom;


public interface MeetingRoomRepository extends JpaRepository<MeetingRoom, Integer> {
	
	@Query("from MeetingRoom where roomName = ?1")
	MeetingRoom findByName(String name);
}
