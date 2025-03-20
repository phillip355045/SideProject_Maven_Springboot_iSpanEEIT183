package com.example.demo.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.entity.Files;
import com.example.demo.entity.RoomReserve;
import com.example.demo.service.FileService;
import com.example.demo.service.RoomReserveService;
import com.example.demo.util.FileFunction;

@Controller
public class FilesController {
	
	@Autowired
	private FileService fileService;
	
	@Autowired
	private RoomReserveService roomReserveService;
	
	//首頁下載檔案功能
	@GetMapping("/HomePageGetFile.controller")
	public void homePageGetFile(@RequestParam("reserveno") String reserveno) {   //單獨準備畫面
		int reserveno1 =  Integer.parseInt(reserveno);
		RoomReserve roomReserve = roomReserveService.findById(reserveno1);
		String reserveTitle = roomReserve.getReserveTitle();
		List<Files> findByReserveno = fileService.findByReserveno(reserveno1);
		
		FileFunction.downloadFile(findByReserveno, reserveno1,reserveTitle);		
//		return "redirect:HomePage";
	}
	
	//---前台功能開始-------
	//前台下載功能
	@GetMapping("/GetFile.controller")
	public String GetFile(@RequestParam("reserveno") String reserveno) {   //單獨準備畫面
		int reserveno1 =  Integer.parseInt(reserveno);
		RoomReserve roomReserve = roomReserveService.findById(reserveno1);
		String reserveTitle = roomReserve.getReserveTitle();
		List<Files> findByReserveno = fileService.findByReserveno(reserveno1);
		
		FileFunction.downloadFile(findByReserveno, reserveno1,reserveTitle);		
		return "redirect:FrontGetAllReserve.controller2";
	}
	
	
	@PostMapping("/UploadFile.controller")
	public String insertRoom(@RequestParam("reserveno") String reserveno,
			@RequestParam("document") List<MultipartFile> documents) throws FileNotFoundException, IOException {
		
		Integer reserveno1 =Integer.parseInt(reserveno);
		
	    String uploadPath = "c:/temp/upload/";
	    File uploadDir = new File(uploadPath);
	    if (!uploadDir.exists()) {
	        uploadDir.mkdir();
	    }
	    RoomReserve findById = roomReserveService.findById(reserveno1);
	    List<Files> fileLists = new ArrayList<>();
		for (MultipartFile document : documents) {
			if (!document.getOriginalFilename().isEmpty()) {
				
				String fileName = document.getOriginalFilename();
				String newFileName = UUID.randomUUID().toString() + "." + fileName;
				String contentType = document.getContentType();
				String filePathForSQL = "C:/temp/upload/" + newFileName;
				Files file = new Files(fileName,contentType,filePathForSQL);
				file.setRoomReserve(findById);	
				fileLists.add(file);
				
				//把檔案輸出至C:/temp/upload/
				File saveFilePath = new File(uploadPath,newFileName);
				byte[] b = document.getBytes();
				document.transferTo(saveFilePath);
			}
			
		}
		fileService.saveFile(fileLists);
		
//		//這邊可以做一個判斷式
		return "redirect:FrontGetAllReserve.controller2";
	}
	
	//刪除檔案
	@GetMapping("/DeleteFile.controller")
	public String deleteFile(@RequestParam("reserveno") String reserveno) {   //單獨準備畫面
		Integer reserveno1 =  Integer.parseInt(reserveno);
		
		fileService.deleteByReserveno(reserveno1);
		return "redirect:FrontGetAllReserve.controller2";
	}
	//---前台功能結束-------
	
	
	
}
