package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.example.demo.entity.Files;

import jakarta.transaction.Transactional;

public interface FilesRepository extends JpaRepository<Files, Integer> {
	
	@Query("from Files where reserveno = ?1")
	public List<Files> findByReserveno(int reserveno);
	
	
    @Modifying   //告訴 Spring Data JPA 这是一个修改操作
    @Transactional  //保证删除操作在事务中运行
	@Query("DELETE from Files where reserveno = ?1")
	public void deleteByReserveno(Integer reserveno);
	
}
