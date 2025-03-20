package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.DepartmentTransfer;

public interface DepartmentTransferRepository extends JpaRepository<DepartmentTransfer, String> {

}
