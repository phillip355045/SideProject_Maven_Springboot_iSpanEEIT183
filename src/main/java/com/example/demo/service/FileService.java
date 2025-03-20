package com.example.demo.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entity.Files;
import com.example.demo.repository.FilesRepository;


@Service
public class FileService {
	
	@Autowired
	private FilesRepository filesRepo;
	
	public Files insert(Files insertBean) {
		return filesRepo.save(insertBean);
	}
    public void saveFile(List<Files> files) {
    	filesRepo.saveAll(files);
    }


	public void deleteById(Integer id) {
		filesRepo.deleteById(id);
	}
	
	
	public void deleteByReserveno(Integer reserveno) {
		filesRepo.deleteByReserveno(reserveno);
	}


	
	public Files update(Files updateBean) {
		return filesRepo.save(updateBean);
	}

	
	public Files findById(Integer id) {
		Optional<Files> findById = filesRepo.findById(id);
		if (findById != null) {
			return findById.get();
		}
		return null;
	}

	
	public List<Files> findAll() {
		return filesRepo.findAll();
	}

	
	public List<Files> findByReserveno(Integer reserveno) {
		return filesRepo.findByReserveno(reserveno);
	}
	
}
