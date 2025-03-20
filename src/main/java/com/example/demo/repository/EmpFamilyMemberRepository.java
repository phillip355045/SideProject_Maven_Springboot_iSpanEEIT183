package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.EmpFamilyMember;

import jakarta.transaction.Transactional;

@Repository
public interface EmpFamilyMemberRepository extends JpaRepository<EmpFamilyMember, String> {
    int countByEmpno(String empno);
    
    @Query("SELECT e FROM EmpFamilyMember e WHERE e.empno = :empno")
    List<EmpFamilyMember> findByEmpno(String empno);
    
    @Modifying
    @Transactional
    @Query("DELETE FROM EmpFamilyMember e WHERE e.empno = :empno")
    void deleteByEmpno(@Param("empno") String empno);
}
