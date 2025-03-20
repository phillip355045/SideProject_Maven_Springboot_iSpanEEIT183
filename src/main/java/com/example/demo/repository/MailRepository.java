package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.Mail;

public interface MailRepository extends JpaRepository<Mail, Integer> {

}
