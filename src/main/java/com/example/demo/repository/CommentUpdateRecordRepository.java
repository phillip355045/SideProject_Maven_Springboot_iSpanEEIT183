package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.CommentUpdateRecord;

public interface CommentUpdateRecordRepository extends JpaRepository<CommentUpdateRecord, Integer> {

	
	
}
