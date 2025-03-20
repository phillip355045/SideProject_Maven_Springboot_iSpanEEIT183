package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.embedded.PostImageUpdateRecordId;
import com.example.demo.entity.PostImageUpdateRecord;

public interface PostImageUpdateRecordRepository extends JpaRepository<PostImageUpdateRecord, PostImageUpdateRecordId> {

}
