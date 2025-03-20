package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.NotifyRetrun;

@Repository
public interface NotifyRetrunRepository extends JpaRepository<NotifyRetrun, String> {

	// dolly增加 抓取notifyRetrun的category
    NotifyRetrun findByCategory(String category);
    
//    @Query("SELECT pageName FROM NotifyRetrun")
//    List<String> findAllPageName();

}
