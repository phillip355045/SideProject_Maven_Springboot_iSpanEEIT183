package com.example.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entity.RoomPicture;
import com.example.demo.repository.RoomPictureRepository;

@Service
public class RoomPictureService {

	@Autowired
	private RoomPictureRepository roomPictureRepo;
	
	//ok
	public List<RoomPicture> findByRoomno(Integer roomno){
		return roomPictureRepo.findByRoomno(roomno);	
	}
	
	//ok
	public void deleteAll(List<RoomPicture> pictures) {
		roomPictureRepo.deleteAll(pictures);
	}
	
	//ok
	public void insertAll(List<RoomPicture> pictures) {
		roomPictureRepo.saveAll(pictures);
	}

}
