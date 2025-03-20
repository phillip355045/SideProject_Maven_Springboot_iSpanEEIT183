package com.example.demo.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.example.demo.entity.Files;

import jakarta.servlet.http.Part;

public class FileFunction {
	 
	//上傳檔案存到
	public static void uploadFile(MultipartFile part, String newFileName) throws FileNotFoundException, IOException {
	    String uploadPath = "C:/Upload/";
	    File uploadDir = new File(uploadPath);
	    if (!uploadDir.exists()) {
	        uploadDir.mkdir();
	    }
	    String filePath = uploadPath + newFileName;//把這個路徑放到資料庫當中
        try (InputStream inputStream = part.getInputStream();
        FileOutputStream outputStream = new FileOutputStream(filePath)) {
	         byte[] buffer = new byte[4096];
	         int bytesRead;
	         while ((bytesRead = inputStream.read(buffer)) != -1) {
	             outputStream.write(buffer, 0, bytesRead);
	         }
	      } 
		
	}
	
	//下載檔案存到
	public static void downloadFile(List<Files> files, int reservno, String reserveTitle) {
		String downloadPath = "C:/Download/";
        File downloadDir = new File(downloadPath);
        if (!downloadDir.exists()) {
            downloadDir.mkdirs();
        }
        
        for (Files file: files) {
        	String oldFilePath = file.getFilePath();
        	
//            int lastIndex = oldFilePath.lastIndexOf('/');
//            if (lastIndex == -1) {
//                lastIndex = oldFilePath.lastIndexOf('\\'); // 处理Windows的反斜杠
//            }
//            String fileName = oldFilePath.substring(lastIndex + 32);
        	String fileName = reservno + reserveTitle + "-" + file.getName();
            String filePath = downloadPath + fileName;
        	    	
        	try (InputStream inputStream = new FileInputStream(oldFilePath);
        			FileOutputStream outputStream = new FileOutputStream(filePath)) {
        		
        		byte[] buffer = new byte[4096];
        		int bytesRead;
        		while ((bytesRead = inputStream.read(buffer)) != -1) {
        			outputStream.write(buffer, 0, bytesRead);
        		}      		
        		System.out.println("文件已成功複製到: " + downloadPath);
        		
        	} catch (IOException e) {
        		e.printStackTrace();
        	}			
		}
		
	}
	
	//這段程式碼會獲取資料的檔名，在使用者上傳以後，將檔案轉換成Byte回傳
	public static byte[] getFileByteArray(Part part) {
		InputStream inputStream;
		try {
			inputStream = part.getInputStream();
			ByteArrayOutputStream buffer = new ByteArrayOutputStream(); 
			int nRead; //整數變量 nRead，用來存儲每次從 InputStream 讀取的字節數。
			byte[] data = new byte[1024]; //1024 字節的緩衝區
			
			//當inputStream將資料讀入data中達到1024時就會執行回圈內的buffer
			while ( (nRead = inputStream.read(data, 0, data.length)) != -1) { //將 InputStream 轉換成一個 byte[]  (data(目標字節數組), 0(儲存數據的起始位址), data.length(讀取的最大量))
				buffer.write(data, 0, nRead); 
			}
			buffer.flush(); //buffer.flush() 將強制將 ByteArrayOutputStream 緩存中的所有數據寫入其內部緩存區。
			byte[] byteArray = buffer.toByteArray(); //轉換成字節，ByteArrayOutputStream 確實是一個基於字節數組的輸出流，但它本身並不是一個字節數組
			return byteArray;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
		
		
	}
	
	//返回檔名
//	public static String getFileName(Part part) {
//		String header = part.getHeader("Content-Disposition");
//		int slashIdx = header.lastIndexOf("\\");
//		String filename;
//		if (slashIdx != -1)
//			filename = header.substring(slashIdx + 1, header.length()-1);			
//		else {
//			int idx = header.indexOf("filename");
//			filename = header.substring(idx + 10, header.length()-1);			
//		}
//		return filename;
//	}
	
	
	
	
		
}
