package com.example.demo.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entity.NotifyRetrun;
import com.example.demo.repository.NotifyRetrunRepository;

@Service
public class NotifyRetrunService {

	@Autowired
	private NotifyRetrunRepository notifyRetrunRepository;
	
	
	// 依照category查找完整的內容
	public NotifyRetrun findByCategory(String category) {
		Optional<NotifyRetrun> optional = notifyRetrunRepository.findById(category);
		if(optional.isPresent()) {
			return optional.get();
		}
		return null;
	}
	
//	public List<String> findAllPageName(){
//		return notifyRetrunRepository.findAllPageName();
//	}
	
	public List<NotifyRetrun> findAll(){
		return notifyRetrunRepository.findAll();
	}
}
