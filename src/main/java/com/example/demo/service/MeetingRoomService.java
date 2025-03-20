package com.example.demo.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.entity.MeetingRoom;
import com.example.demo.entity.RoomReserve;
import com.example.demo.repository.MeetingRoomRepository;


@Service
public class MeetingRoomService {
	
	@Autowired
	private MeetingRoomRepository meetingRoomRepo;
	
	//ok
	public MeetingRoom findById(Integer roomno) {
		Optional<MeetingRoom> optional = meetingRoomRepo.findById(roomno);
		if (optional.isPresent()) {
			return optional.get();
		}
		return null;
	}
	
	public MeetingRoom findByName(String roomName) {
		return meetingRoomRepo.findByName(roomName);
	
	}
	
	//ok
	//後端會議室畫面回傳page物件
	public Page<MeetingRoom> backFindMeetingRoomPage(Integer pageNumber){
		Pageable pgb = PageRequest.of(pageNumber-1, 5, Sort.Direction.ASC, "roomno");
		Page<MeetingRoom> page = meetingRoomRepo.findAll(pgb);
		return page;
	}
	
	//ok
	//前端會議室畫面回傳page物件
	public Page<MeetingRoom> findMeetingRoomPage(Integer pageNumber){
		Pageable pgb = PageRequest.of(pageNumber-1, 2, Sort.Direction.ASC, "roomno");
		return meetingRoomRepo.findAll(pgb);		
	}
	
	public List<MeetingRoom> findAll() {
		return meetingRoomRepo.findAll();
	}
	
	
	//ok
	//新增、修改
	@Transactional  //雖然不是絕對畢業，但能夠確保操作只能全部成功，不然就是全部rollback
	public MeetingRoom insertAndUpdate(MeetingRoom insertBean) {
		return meetingRoomRepo.save(insertBean);
	}
	
	//ok
	public void deleteById(Integer roomno) {		
		meetingRoomRepo.deleteById(roomno);
	}
}
