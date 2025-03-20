package com.example.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entity.Connection;
import com.example.demo.repository.ConnectionRepository;

@Service
public class ConnectionService {

	@Autowired
	private ConnectionRepository connectionRepository;
	
	public Connection saveConnection(Connection connection) {
		return connectionRepository.save(connection);
	}
	
	public List<Connection> allconnection() {
		return connectionRepository.findAll();
	}
}
