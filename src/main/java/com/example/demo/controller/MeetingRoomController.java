package com.example.demo.controller;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Base64;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.entity.MeetingRoom;
import com.example.demo.entity.RoomPicture;
import com.example.demo.service.MeetingRoomService;
import com.example.demo.service.RoomPictureService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
public class MeetingRoomController {
	
	@Autowired
	private MeetingRoomService meetingRoomService;
	
	@Autowired
	private RoomPictureService roomPictureService;
	
	//ok
	//後台返還所有頁面
	@GetMapping("/GetAllRoom.controller")
	public String getAllRoomPage(
			@RequestParam(value="p",defaultValue = "1") Integer pageNum,
			Model model) {
		Page<MeetingRoom> page = meetingRoomService.backFindMeetingRoomPage(pageNum);	
		model.addAttribute("page", page);
		return "meetingRoom/back/GetAllRoom2";
	}
	
	//ok
	//後端準備新增會議室頁面
	@GetMapping("/InsertRoom.controller")
	public String insertRoomView(Model model) {   //單獨準備畫面
		List<MeetingRoom> all = meetingRoomService.findAll();
		//lambda改寫
		String newRoomName = all.stream().map(MeetingRoom::getRoomName)
				.max(Comparator.naturalOrder())//找最後一個項目
				.map(lastName ->{
					String prefix = lastName.substring(0,5);
					int number = Integer.parseInt(lastName.substring(5))+1;
					return prefix + String.format("%02d", number);
				})
				.orElse("RoomA01");
		model.addAttribute("roomName",newRoomName);
		
//		if (all.isEmpty()) {
//			model.addAttribute("roomName", "RoomA01");			
//		}else {
//			MeetingRoom meetingRoom = all.get(all.size() -1);
//			String lastName = meetingRoom.getRoomName();
//			String prefix = lastName.substring(0,5);
//			String numberPart = lastName.substring(5);
//			
//			int number = Integer.parseInt(numberPart);
//			number++;
//			String newNumberPart = String.format("%02d", number);
//			model.addAttribute("roomName", prefix + newNumberPart);
//		}
		
		return "meetingRoom/back/InsertRoom2";
	}
	
	//ok
	//後端處理新增會議室資料
	@PostMapping("/InsertRoom")
	public String insertRoom(@ModelAttribute MeetingRoom meetingRoom,
			Model model,
			@RequestParam("picture") List<MultipartFile> roomPictures) throws IOException {
		String roomName = meetingRoom.getRoomName();	
		MeetingRoom byName = meetingRoomService.findByName(roomName);
		//lamda
		if(byName != null) { //提前判斷並且返回資料，就不用執行後面
			model.addAttribute("errorMsg","會議室名稱重複，請重新輸入");
			return "meetingRoom/InsertRoom";
		}
		List<RoomPicture> pictureList = roomPictures.stream()
				.filter(roomPicture -> !roomPicture.getOriginalFilename().isEmpty())
				.map(roomPicture ->{
					try {
						byte[] imageBytes = roomPicture.getBytes();
						String base64Image = Base64.getEncoder().encodeToString(imageBytes);
						RoomPicture roomPicture2 = new RoomPicture(base64Image);
						roomPicture2.setMeetingRoom(meetingRoom);
						return roomPicture2;
					} catch (IOException e) {
						throw new UncheckedIOException(e);
					}
				})
				.collect(Collectors.toList());//將處理過後的roomPicture2都放入 pictureList裡面
	    meetingRoom.setRoomPictures(pictureList);
	    meetingRoomService.insertAndUpdate(meetingRoom);
	    return "redirect:GetAllRoom.controller";
//		List<RoomPicture> pictureList = new ArrayList<>();
//		if (byName == null) {
//			for (MultipartFile roomPicture : roomPictures) {
//				if (!roomPicture.getOriginalFilename().isEmpty()) {
//					byte[] imageBytes = roomPicture.getBytes();
//					String base64Image = Base64.getEncoder().encodeToString(imageBytes);
//					RoomPicture roomPicture2 = new RoomPicture(base64Image);
//					roomPicture2.setMeetingRoom(meetingRoom);
//					pictureList.add(roomPicture2);
//				}			
//			}
//			meetingRoom.setRoomPictures(pictureList);
//			meetingRoomService.insertAndUpdate(meetingRoom);
//			return "redirect:GetAllRoom.controller";
//			
//		}else {
//			model.addAttribute("errorMsg", "會議室名稱重複，請重新輸入");
//			return "meetingRoom/InsertRoom";
//		}	
	}
	
	//ok
	//刪除會議室
	@GetMapping("/DeleteRoom.controller")
	public String deleteRoom(@RequestParam("roomno") Integer roomno) {
		meetingRoomService.deleteById(roomno);
		return "redirect:GetAllRoom.controller";
	}
	
	//ok
	//前端準備預約畫面
	@GetMapping("/UpdateRoom.controller")
	public String updateRoom(@RequestParam("roomno") Integer roomno,Model m) {
		MeetingRoom findById = meetingRoomService.findById(roomno);	
		m.addAttribute("room", findById);
		m.addAttribute("pictures",findById.getRoomPictures());
		return "meetingRoom/back/UpdateRoom2";
	}
	
	//ok
	//處理管理者更新會議室資料
	@ResponseBody
	@PostMapping(value="/UpdateForRoom.controller",consumes = "multipart/form-data")
	public ResponseEntity<Map<String, Object>> updateForRoom(
			@RequestPart("meetingRoom") String meetingRoomJson,//前端把meetingRoom包裝物件然後json序列化傳遞到後端，而後端是以字串接收
            @RequestPart("pictures") String imagesJson){		
		ObjectMapper objectMapper = new ObjectMapper();//能將json轉換成java物件、或者java物件轉換成json	
		Map<String, Object> response = new HashMap<>();
		//lambda語法
		try {
			MeetingRoom meetingRoom = objectMapper.readValue(meetingRoomJson, MeetingRoom.class);//將前端傳入的json物件轉換成MeetingRoom物件
			List<String> images = objectMapper.readValue(imagesJson, new TypeReference<List<String>>() {});
			//刪除現有的圖片
			List<RoomPicture> existingPictures = roomPictureService.findByRoomno(meetingRoom.getRoomno());
			if (!existingPictures.isEmpty()) {
				roomPictureService.deleteAll(existingPictures);
			}
			//新增圖片
			List<RoomPicture> pictures = images.stream().map(image -> {
				RoomPicture roomPicture = new RoomPicture(image);
				roomPicture.setMeetingRoom(meetingRoom);
				return roomPicture;
			})
			.collect(Collectors.toList());
			roomPictureService.insertAll(pictures);
			meetingRoomService.insertAndUpdate(meetingRoom);//更新會議室資訊
			response.put("success", true);
		} catch (Exception e) {
	        response.put("success", false);
	        response.put("error", e.getMessage());
	        e.printStackTrace();
		}
		
//		try {			
//			MeetingRoom meetingRoom = objectMapper.readValue(meetingRoomJson, MeetingRoom.class);//將前端傳入的json物件轉換成MeetingRoom物件
//
//			List<String> images = objectMapper.readValue(imagesJson, new TypeReference<List<String>>(){});
//			List<RoomPicture> findByRoomno = roomPictureService.findByRoomno(meetingRoom.getRoomno());
//			List<RoomPicture> pictures = new ArrayList<>();
//			if (!findByRoomno.isEmpty()) {
//				roomPictureService.deleteAll(findByRoomno);
//				for (String string : images) {
//					RoomPicture roomPicture = new RoomPicture(string);
//					roomPicture.setMeetingRoom(meetingRoom);
//					pictures.add(roomPicture);
//				}
//				roomPictureService.insertAll(pictures);				
//			}else {
//				for (String string : images) {
//					RoomPicture roomPicture = new RoomPicture(string);
//					roomPicture.setMeetingRoom(meetingRoom);
//					pictures.add(roomPicture);
//				}
//				roomPictureService.insertAll(pictures);	
//			}
//
//			meetingRoomService.insertAndUpdate(meetingRoom);
//			response.put("success", true);
//			
//		} catch (Exception e) {
//			response.put("success", false);
//			response.put("error", e.getMessage());
//			e.printStackTrace();
//			
//		}
		return ResponseEntity.ok(response);	
	}

	//傳送list也能自動轉換成json陣列(現在沒在用，這是早期我用來製造會議室的下拉選單)
//	@GetMapping("/PrepareReserve.controller")
//	@ResponseBody
//	public List<String> prepareReserve(){
//		List<MeetingRoom> findAll = meetingRoomService.findAll();
//		List<String> roomNames = new ArrayList<>();
//		for (MeetingRoom meetingRoom : findAll) {
//			String roomName = meetingRoom.getRoomName();
//			roomNames.add(roomName);
//		}		
//		return roomNames;
//	}
	
	//---------前端------------
	
	//回傳前台的會議室物件
	@GetMapping("/FrontGetAllRoom.controller")
	public String frontGetAllRoomPage(
			@RequestParam(value="p",defaultValue = "1") Integer pageNum,
			Model model) {
		//lambda
		Page<MeetingRoom> page = meetingRoomService.findMeetingRoomPage(pageNum);
		Map<Integer, List<RoomPicture>> pictureMap = page.getContent().stream().collect(Collectors.toMap(MeetingRoom::getRoomno, MeetingRoom::getRoomPictures));
//		Page<MeetingRoom> page = meetingRoomService.findMeetingRoomPage(pageNum);	
//		Map<Integer,List<RoomPicture>> pictureMap = new HashMap<>();	
//		for (MeetingRoom meetingRoom : page.getContent()) {
//			List<RoomPicture> roomPictures = meetingRoom.getRoomPictures();
//			pictureMap.put(meetingRoom.getRoomno(), roomPictures);	
//		}	
		model.addAttribute("page", page);
		model.addAttribute("pictureMap",pictureMap);
		
		return "meetingRoom/FrontGetAllRoom4";
	}
}
