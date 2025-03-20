package com.example.demo.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import com.example.demo.dto.ReserveCountDTO;
import com.example.demo.dto.RoomReserveDTO;
import com.example.demo.entity.Files;
import com.example.demo.entity.RoomReserve;
import com.example.demo.service.EmployeesService;
import com.example.demo.service.FileService;
import com.example.demo.service.NotificationService;
import com.example.demo.service.RoomReserveService;
import com.example.demo.service.WebexService;
import com.example.demo.util.TimeTransfer;

import jakarta.servlet.http.HttpSession;

@Controller
public class RoomReserveController {

	@Autowired
	private RoomReserveService roomReserveService;
	
	@Autowired
	private FileService fileService;

	//接收會議室連結
	@Autowired
	private WebexService webexService;
	
	//通知審核人員放行
	@Autowired
	private NotificationService notificationService;
	
	@Autowired
	private EmployeesService employeesService;
	
	//-------後端開始----------------
	
	//ok
	//準備chat.js資料
	@GetMapping("/BackHomePage")
	public String ReserveAnalysis(Model model, HttpSession session){
	    model.addAttribute("employees", employeesService.getAllEmployees());
		return "meetingRoom/back/MeetingRoomAnalysis";
	}
	
	//ok
	//獲得會議室名稱以借用次數
	@ResponseBody
	@GetMapping("/GetReserveCount")
	public List<ReserveCountDTO> getReserveCount(){
		return roomReserveService.findRoomReserveCount();	
	}
	
	//ok
	//後台所有預約資料
	@GetMapping("/GetAllReserve.controller2")
	public String roomReservePage(Model model) {
		List<RoomReserve> reserves = roomReserveService.findAll();
		model.addAttribute("reserves", reserves);
		return "meetingRoom/back/GetAllReserveDataTable2";
	}
	
	//這應該傳給後台會議室預約畫面來判斷預約紀錄的狀況
	//ok
	// 傳送list也能自動轉換成json陣列
	@GetMapping("/GetReservation.controller")
	@ResponseBody
	public List<RoomReserve> getReservation(@RequestParam("roomName") String roomname, Model m) {
		return roomReserveService.findReservaion(roomname);		
	}
	
	//ok
	//後台真刪除
	@GetMapping("/DeleteReserve.controller")
	public ResponseEntity<Map<String, Object>> deleteReserve(@RequestParam("reserveno") String reserveno) { // 單獨準備畫面
		int reserveno1 = Integer.parseInt(reserveno);
		Map<String, Object> response = new HashMap<>();	
		try {
			roomReserveService.deleteById(reserveno1);
			response.put("success", true);
        } catch (Exception e) {
            response.put("success", false);
        }
		return ResponseEntity.ok(response);
	}

	//ok
	//後台放行功能
	@GetMapping("/PassReservation.controller")
	public ResponseEntity<Map<String, Object>> passReservation(@RequestParam("reserveno") String reserveno, Model model) {
		Integer reserveno1 = Integer.parseInt(reserveno);
		RoomReserve byId = roomReserveService.findById(reserveno1);
		boolean timeConflict = roomReserveService.TimeConflict(byId);
		Map<String, Object> response = new HashMap<>();
		if (timeConflict) {
			response.put("error", "會議時間衝突");
		}else {
			String onelineMeetingHref = webexService.createMeeting(
					byId.getReserveTitle(),byId.getEventDay(),
					byId.getStartTime(),byId.getEndTime());		
			try {
				byId.setStatus("放行");		
				byId.setOnlineMeeting(onelineMeetingHref);
				roomReserveService.update(byId);
				response.put("success", true);
			} catch (Exception e) {
				e.getMessage();
				response.put("success", false);
			}
		}
		return ResponseEntity.ok(response);
	}
	
	//ok
	//後端退回預約功能
	@GetMapping("/ReturnReservation.controller")
	public ResponseEntity<Map<String, Object>> returnReservation(@RequestParam("reserveno") String reserveno) {
		Integer reserveno1 = Integer.parseInt(reserveno);
		RoomReserve byId = roomReserveService.findById(reserveno1);
		Map<String, Object> response = new HashMap<>();		
		try {
			byId.setStatus("退回");
			roomReserveService.update(byId);
			
			response.put("success", true);
		} catch (Exception e) {
			response.put("success", false);
			response.put("error", e.getMessage());
		}
		return ResponseEntity.ok(response);				
	}
	//被datatable取代
	//回傳全部資料以及頁數物件
//	@GetMapping("/GetAllReserve.controller2")
//	public String roomReservePage(
//			@RequestParam(value="p",defaultValue = "1") Integer pageNum,
//			Model model) {
//		Page<RoomReserve> page = roomReserveService.findRoomReservePage(pageNum);
//		
//		model.addAttribute("page", page);
//		
//		return "meetingRoom/back/GetAllReserveDataTable";
//	}
	
	//被套件取代的功能，不捨得刪除，必須留著這都是血和淚的積累
	// 後端的模糊查詢
//	@PostMapping("/FuzzySearch.controller")
//	public String fuzzySearch(@RequestParam(required = false, name = "freeYouselfToRearch") String freeYouselfToRearch,
//			@RequestParam(required = false, name = "startDate") String startDate,
//			@RequestParam(required = false, name = "endDate") String endDate, Model m) {
//		java.sql.Date sqlStartDate = TimeTransfer.sqlDate(startDate);
//		java.sql.Date sqlEndDate = TimeTransfer.sqlDate(endDate);
//
//		List<RoomReserve> fuzzySearch = roomReserveService.searchRoomReserves(freeYouselfToRearch, sqlStartDate,
//				sqlEndDate);
//
//		m.addAttribute("reserves", fuzzySearch);
//		return "meetingRoom/back/GetAllReserveForFuzzySearch";
//	}
	
	//新模糊查詢
//	@PostMapping("/NewFuzzySearch")
//	public String fuzzySearch(@RequestParam String search,
//			@RequestParam(value="p",defaultValue = "1") Integer pageNum,
//			Model model) {
//		Page<RoomReserve> pageRoomReserve = roomReserveService.fuzzySearch(search, pageNum);
//		
//
//		model.addAttribute("page", pageRoomReserve);
//
//		return "meetingRoom/back/GetAllReserve2";
//	}
	
	//-------後端結束----------------

	
	/*-------員工端controller--------*/

	@GetMapping("/FrontGetAllReserve.controller2")
	public String frontRoomReservePage(HttpSession session,Model model) {
		String empno = (String) session.getAttribute("account");
		String fakeDelete = "0";// 找到員工端上未刪除的部分		
		List<RoomReserve> roomReserves = roomReserveService.findReservaionByEmpnoAndFakeDelete(fakeDelete, empno);
		List<Files> findAll2 = fileService.findAll();
		
		for (RoomReserve reserve : roomReserves) {
//			System.out.println("FrontGetAllReserve.controller" + reserve.getEmpno());
			Boolean hasFile = findAll2.stream().anyMatch(file -> file.getReserveno().equals(reserve.getReserveno()));
			System.out.println("GetAllReserve.controller判斷:" + hasFile);
		
			if (hasFile) {
				reserve.setHasFile(hasFile);
			} else {
				reserve.setHasFile(false);
			}
		}	
		// 每一個預約只抓取多筆檔案的其中一筆，解決前端會顯示多筆資料的問題
		List<Files> pickFirstOne = new ArrayList<>();
		for (RoomReserve reserve : roomReserves) {
			for (Files files : findAll2) {
				if (reserve.getReserveno().equals(files.getReserveno())) {  //這邊之前用==來比較檔案的reserveno是否跟預約的一樣，有時會導致前台文件無法顯示
					System.out.println("------" + files.getReserveno());
					pickFirstOne.add(files);
					break;
				}
			}
		}	
		model.addAttribute("roomReserves", roomReserves);
		model.addAttribute("files", pickFirstOne);
		
		return "meetingRoom/FrontGetAllReserve2";
	}
	
	//前台取消預約
	@GetMapping("/CancelReserve.controller")
	public String frontDeleteReserve(@RequestParam("reserveno") String reserveno) { // 單獨準備畫面

		int reserveno1 = Integer.parseInt(reserveno);
		RoomReserve findById = roomReserveService.findById(reserveno1);
//		findById.setFakeDelete("1");
		findById.setStatus("取消");
		roomReserveService.update(findById);
//		roomReserveService.deleteById(reserveno1);	
		return "redirect:FrontGetAllReserve.controller2";
	}
	
	//前台預約畫面準備
	@GetMapping("/ReserveRoom.controller")
	public String ReserveRoomView(@RequestParam("roomName") String roomName, Model m) { // 單獨準備畫面
		m.addAttribute("roomName", roomName);
		return "meetingRoom/ReserveRoom";
	}

	//前台預約會議室
	@PostMapping("/ReserveRoom")
	public String insertRoom(@ModelAttribute RoomReserve roomReserve,
			@RequestParam("document") List<MultipartFile> documents, Model model,
			@RequestParam String category,@RequestParam String notifyEmpno)
			throws FileNotFoundException, IOException {
		boolean timeConflict = roomReserveService.TimeConflict(roomReserve);
		if (timeConflict) {
			String roomName = roomReserve.getRoomName(); 
			model.addAttribute("errorMsg", "預約時段有衝突，請重新預約");
			model.addAttribute("roomName", roomName);
			return "meetingRoom/ReserveRoom";
		}
		//發出通知
		notificationService.createNotificationBasisOnEmpno(category, notifyEmpno);
		
		List<Files> fileLists = new ArrayList<>();

		String uploadPath = "c:/temp/upload/";
		File uploadDir = new File(uploadPath);
		if (!uploadDir.exists()) {
			uploadDir.mkdir();
		}
		for (MultipartFile document : documents) {
			if (!document.getOriginalFilename().isEmpty()) {
				System.out.println("roomreservecontroller" + document.getOriginalFilename().isEmpty());
				System.out.println("roomreservecontroller" + document.getName());
				String fileName = document.getOriginalFilename();
				String newFileName = UUID.randomUUID().toString() + "." + fileName;
				String contentType = document.getContentType();
				String filePathForSQL = "C:/temp/upload/" + newFileName;
				Files file = new Files(fileName, contentType, filePathForSQL);
				file.setRoomReserve(roomReserve);
				fileLists.add(file);

				// 把檔案輸出至C:/temp/upload/
				File saveFilePath = new File(uploadPath, newFileName);
				// byte[] b = document.getBytes();
				document.transferTo(saveFilePath);
			}
		}
//		String onelineMeetingHref = webexService.createMeeting(roomReserve.getReserveTitle(),roomReserve.getEventDay(),
//															roomReserve.getStartTime(),roomReserve.getEndTime());
		
//		roomReserve.setOnlineMeeting(onelineMeetingHref);
		roomReserve.setReservationFiles(fileLists);
		roomReserveService.insert(roomReserve);

//		//這邊可以做一個判斷式
		return "redirect:FrontGetAllReserve.controller2";
	}
	
	//前台整批假刪除刪除功能	
	@ResponseBody
	@PostMapping("/BatchDeleteReservations.controller")
	public ResponseEntity<?> batchDeleteReservations(@RequestBody ArrayList<Integer> selectedIds){
//		roomReserveService.deleteReservationsByIds(selectedIds);
		for (Integer integer : selectedIds) {
			RoomReserve findById = roomReserveService.findById(integer);
			findById.setFakeDelete("1");
			roomReserveService.update(findById);
		}
		return ResponseEntity.ok().build();
	}
	
	//前台準備變更預約紀錄畫面
	@GetMapping("/UpdateReserve.controller")
	public String updateReserve(@RequestParam("reserveno") String reserveno, Model m) {
		int reserveno1 = Integer.parseInt(reserveno);
		RoomReserve findById = roomReserveService.findById(reserveno1);
		m.addAttribute("reserve", findById);
		return "meetingRoom/UpdateReserve";
	}
	
	//前台變更預約紀錄處理
	@PostMapping("/UpdateForReserve.controller")
	public String insertRoom(@ModelAttribute RoomReserveDTO roomReserveDTO)
			throws FileNotFoundException, IOException, ParseException {

		Date utilDate = TimeTransfer.utilDate(roomReserveDTO.getEventDay());
		Date utilStartTime = TimeTransfer.utilTime(roomReserveDTO.getStartTime());
		Date utilEndTime = TimeTransfer.utilTime(roomReserveDTO.getEndTime());

		RoomReserve roomReserve = new RoomReserve(roomReserveDTO.getReserveno(), roomReserveDTO.getReserveTitle(),
				roomReserveDTO.getRoomName(), roomReserveDTO.getEmpno(), utilDate, utilStartTime, utilEndTime,
				roomReserveDTO.getStatus(), roomReserveDTO.getFakeDelete());

		roomReserveService.update(roomReserve);
		return "redirect:FrontGetAllReserve.controller2";
	}
	
	// 傳送list也能自動轉換成json陣列
	@GetMapping("/FrontGetAllReservation.controller")
	@ResponseBody
	public List<RoomReserve> getAllReservation() {
		List<RoomReserve> findAll = roomReserveService.findAllReservation();

		return findAll;
	}
	
	//被datatable取代
	//回傳全部資料以及頁數物件
//	@GetMapping("/FrontGetAllReserve.controller2")
//	public String frontRoomReservePage(HttpSession session,
//			@RequestParam(value="p",defaultValue = "1") Integer pageNum,
//			Model model) {
//		String empno = (String) session.getAttribute("account");
//		String fakeDelete = "0";// 找到員工端上未刪除的部分		
//		Page<RoomReserve> pageRoomReserve = roomReserveService.findReservaionByEmpnoAndFakeDeletePage(fakeDelete, empno, pageNum);
//		List<Files> findAll2 = fileService.findAll();
//		
//		for (RoomReserve reserve : pageRoomReserve.getContent()) {
////			System.out.println("FrontGetAllReserve.controller" + reserve.getEmpno());
//			Boolean hasFile = findAll2.stream().anyMatch(file -> file.getReserveno().equals(reserve.getReserveno()));
////			System.out.println("GetAllReserve.controller判斷:" + hasFile);
//			if (hasFile) {
//				reserve.setHasFile(hasFile);
//			} else {
//				reserve.setHasFile(false);
//			}
//		}
//
//		// 每一個預約只抓取多筆檔案的其中一筆，解決前端會顯示多筆資料的問題
//		List<Files> pickFirstOne = new ArrayList<>();
//		for (RoomReserve reserve : pageRoomReserve.getContent()) {
//			for (Files files : findAll2) {
//				if (reserve.getReserveno() == files.getReserveno()) {
//					pickFirstOne.add(files);
//					break;
//				}
//			}
//		}	
//		model.addAttribute("page", pageRoomReserve);
//		model.addAttribute("files", pickFirstOne);
//	
//		return "meetingRoom/FrontGetAllReserve2";
//	}


}
