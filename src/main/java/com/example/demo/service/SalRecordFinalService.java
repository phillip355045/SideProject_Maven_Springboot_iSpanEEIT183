package com.example.demo.service;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import com.example.demo.dto.SalTotalDTO;
import com.example.demo.entity.CheckIn;
import com.example.demo.entity.Employees;
import com.example.demo.entity.SalRecordBean;
import com.example.demo.entity.SalRecordFinal;

import com.example.demo.repository.EmployeesRepository;
import com.example.demo.repository.SalRecordFinalRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;

@Service

public class SalRecordFinalService {
	@Autowired
	private SalRecordFinalRepository salRecordRepo;

	@Autowired
	private EmployeesRepository employeesRepo;

	private final JdbcTemplate jdbcTemplate;

	public SalRecordFinalService(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

//	更新status狀態
	@Transactional
	public void updateStatus(int salno, String status) {
		SalRecordFinal salRecordFinal = salRecordRepo.findById(salno)
				.orElseThrow(() -> new IllegalArgumentException("Invalid salno: " + salno));
		salRecordFinal.setStatus(status);
		salRecordRepo.save(salRecordFinal);
	}

//	 預存程序新增
	public void insertSalRecordFinal(String empno, String year, String month) {
		salRecordRepo.insertSalRecordFinal(empno, year, month);
	}

	// 刪除
	public void deleteSalById(Integer salno) {
		salRecordRepo.deleteById(salno);
	}

//新增
	public void insertEmpSal(SalRecordFinal emp) {
		try {
			

			// Handle empty strings by setting default values
			if (emp.getFoodAllowance() == null) {
				emp.setFoodAllowance(new BigDecimal("1000"));
			}
			if (emp.getHolidayAllowance() == null) {
				emp.setHolidayAllowance(BigDecimal.ZERO);
			}
			if (emp.getOvertimePay() == null) {
				emp.setOvertimePay(BigDecimal.ZERO);
			}
			if (emp.getAttendanceBonus() == null) {
				emp.setAttendanceBonus(BigDecimal.ZERO);
			}
			if (emp.getLeavePay() == null) {
				emp.setLeavePay(BigDecimal.ZERO);
			}
			salRecordRepo.save(emp);
		} catch (Exception e) {
			throw new RuntimeException("新增失敗", e);
		}
	}

	// 批次新增
//	@Transactional
//	public void insertEmpSalBatch(String year, String month) {
//		try {
//			List<SalRecordFinal> empList = new ArrayList<>();
//
//			Set<String> processedEmpnos = new HashSet<>();
//			// Get all empno list
//			List<String> empnos = salRecordRepo.findAllEmpnos();
//
//			for (String empno : empnos) {
//				// Skip if already processed this empno
//				if (processedEmpnos.contains(empno)) {
//					continue;
//				}
//
//				SalRecordFinal emp = new SalRecordFinal();
//				emp.setEmpno(empno);
//				emp.setYear(year);
//				emp.setMonth(month);
//
//				if (emp.getFoodAllowance() == null) {
//					emp.setFoodAllowance(new BigDecimal("1000"));
//				}
//				if (emp.getHolidayAllowance() == null) {
//					emp.setHolidayAllowance(BigDecimal.ZERO);
//				}
//				if (emp.getOvertimePay() == null) {
//					emp.setOvertimePay(BigDecimal.ZERO);
//				}
//				if (emp.getAttendanceBonus() == null) {
//					emp.setAttendanceBonus(BigDecimal.ZERO);
//				}
//				if (emp.getLeavePay() == null) {
//					emp.setLeavePay(BigDecimal.ZERO);
//				}
//
//				empList.add(emp);
//				processedEmpnos.add(empno); // Mark empno as processed
//			}
//
//			salRecordRepo.saveAll(empList);
//		} catch (Exception e) {
//			throw new RuntimeException("批次新增失敗", e);
//		}
//	}

	@Transactional
	public void insertEmpSalBatch(String year, String month) {
		try {
			Set<String> processedEmpnos = new HashSet<>();
			// Get all empno list
			List<String> empnos = salRecordRepo.findAllEmpnos();

			for (String empno : empnos) {
				// Skip if already processed this empno
				if (processedEmpnos.contains(empno)) {
					continue;
				}

				insertSalRecordFinal(empno, year, month);
				processedEmpnos.add(empno); // Mark empno as processed
			}
		} catch (Exception e) {
			throw new RuntimeException("批次新增失敗", e);
		}
	}

//	   checkbox批次發送
	public void updateStatusToPublished(List<Integer> salnos) {
		for (Integer salno : salnos) {
			SalRecordFinal salary = salRecordRepo.findBySalno(salno);
			if (salary != null) {
				salary.setStatus("已發送");
				salRecordRepo.save(salary);
			}
		}
	}

//    抓取要修改資料 
	public SalRecordFinal getUpdateData(Integer salno) {
		Optional<SalRecordFinal> optional = salRecordRepo.findById(salno);
		return optional.orElse(null);
	}

	// 修改資料
	public SalRecordFinal updateData(SalRecordFinal emp) {

		SalRecordFinal existingEmp = salRecordRepo.findById(emp.getSalno())
				.orElseThrow(() -> new IllegalArgumentException("Invalid employee ID"));
		// Handle empty strings by setting default values
		if (emp.getFoodAllowance() == null) {
			emp.setFoodAllowance(BigDecimal.ZERO);
		}
		if (emp.getHolidayAllowance() == null) {
			emp.setHolidayAllowance(BigDecimal.ZERO);
		}
		if (emp.getOvertimePay() == null) {
			emp.setOvertimePay(BigDecimal.ZERO);
		}
		if (emp.getAttendanceBonus() == null) {
			emp.setAttendanceBonus(BigDecimal.ZERO);
		}

		// 確保其他字段不為 null，若為 null 則保留原有值
		emp.setSal(existingEmp.getSal() != null ? emp.getSal() : existingEmp.getSal());
		emp.setDeptno(emp.getDeptno() != null ? emp.getDeptno() : existingEmp.getDeptno());
		emp.setJob(emp.getJob() != null ? emp.getJob() : existingEmp.getJob());
		emp.setMgr(emp.getMgr() != null ? emp.getMgr() : existingEmp.getMgr());
		emp.setName(emp.getName() != null ? emp.getName() : existingEmp.getName());

		return salRecordRepo.save(emp);
	}

	@PersistenceContext
	private EntityManager entityManager;

//	查詢單筆資料
	public SalRecordFinal getSal(Integer salno) {
		Optional<SalRecordFinal> optional = salRecordRepo.findById(salno);
		return optional.orElse(null);
	}

	// 查詢全部
	public List<SalRecordFinal> getAllSals() {
		return salRecordRepo.findAll();
	}

	public List<SalRecordFinal> findByStatus(String status) {
		return salRecordRepo.findByStatus(status);
	}

//		模糊查詢
	public List<SalRecordFinal> getSelectSal(String col, String colvalue) {
		String queryStr = String.format("SELECT * FROM SalRecordFinal WHERE %s LIKE :colvalue", col);
		Query query = entityManager.createNativeQuery(queryStr, SalRecordFinal.class);
		query.setParameter("colvalue", "%" + colvalue + "%");
		return query.getResultList();
	}

	// 透過姓名or帳號取得所有員工的資料
	public List<Employees> getEmployeesByEmpnoAndNameFuzzySearch(String query) {
		return employeesRepo.getEmployeesByEmpnoAndNameFuzzySearch(query);
	}

	
	
	
}
