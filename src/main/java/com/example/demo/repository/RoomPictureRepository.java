package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.demo.entity.RoomPicture;


public interface RoomPictureRepository extends JpaRepository<RoomPicture, Integer> {
	
	//ok
	@Query(value="SELECT * FROM roomPicture WHERE roomno = ?1",nativeQuery = true)
	List<RoomPicture> findByRoomno(Integer roomno);
}
