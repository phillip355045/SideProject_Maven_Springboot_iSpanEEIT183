package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.entity.ChatMessage;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

	@Query(value = "SELECT TOP 10 * FROM chatmessage WHERE (empno = :sender AND receiver = :receiver) OR (empno = :receiver AND receiver = :sender) ORDER BY chattime DESC", nativeQuery = true)
	List<ChatMessage> findChatHistory(String sender, String receiver);
	
    List<ChatMessage> findByEmpno(String empno);

    


}
