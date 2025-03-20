package com.example.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entity.EmpFamilyMember;
import com.example.demo.repository.EmpFamilyMemberRepository;

@Service
public class EmpFamilyMemberService {

	@Autowired
	private EmpFamilyMemberRepository empFamilyMemberRepository;

	public EmpFamilyMember createEmpFamilyMember(EmpFamilyMember empFamilyMember) {
		String empno = empFamilyMember.getEmpno();
		int count = empFamilyMemberRepository.countByEmpno(empno) + 1;
		String id = String.format("%s_%03d", empno, count);
		empFamilyMember.setId(id);
		return empFamilyMemberRepository.save(empFamilyMember);
	}

	public List<EmpFamilyMember> findByempno(String empno) {
		return empFamilyMemberRepository.findByEmpno(empno);
	}

	public void deleteByEmpno(String empno) {
		empFamilyMemberRepository.deleteByEmpno(empno);
	}

}