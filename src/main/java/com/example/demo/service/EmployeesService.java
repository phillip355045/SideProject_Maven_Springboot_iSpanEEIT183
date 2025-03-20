package com.example.demo.service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.demo.entity.Employees;
import com.example.demo.repository.EmployeesRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

@Service
public class EmployeesService {

	@Autowired
	private EmployeesRepository employeesRepo;

	@PersistenceContext
	private EntityManager entityManager;
//	@Autowired
//	private PasswordEncoder passwordEncoder;

//	@Autowired
//	private DepartmentRepository departmentRepository;

	public Employees checkLogin(String empno, String password) {
		Employees dbEmps = employeesRepo.findByEmpno(empno);

		if (dbEmps == null) {
			return null;

		} else {
			String dbPassword = dbEmps.getPassword();

			// 直接比對密碼
			if (password.equals(dbPassword)) {
				return dbEmps;
//			String dbPassword = dbEmps.getPassword();
//			boolean result = passwordEncoder.matches(password, dbPassword);
//
//			if (result) {
//				return dbEmps;
			}
			return null;
		}
	}

//	// 按照分頁找全部
//	public Page<Employees> findByPage(Integer pageNumber) {
//		Pageable pgb = PageRequest.of(pageNumber - 1, 5, Sort.Direction.DESC, "added");
//		Page<Employees> page = employeesRepo.findAll(pgb);
//		return page;
//	}

	// 按照分頁找全部
	public Page<Employees> getEmployeesPage(int page, int size) {
		Pageable pageable = PageRequest.of(page - 1, size);
		return employeesRepo.findAll(pageable);
	}

	// 找全部
	public List<Employees> getAllEmployees() {
		List<Employees> emps = employeesRepo.findAll();
		return emps;
	}

	// 獲取單一
	public Employees findUsersByEmpno(String empno) {
		Optional<Employees> optional = employeesRepo.findById(empno);
		if (optional.isPresent()) {
			return optional.get();
		}
		return null;
	}

	// 刪除
	public void deleteMsgById(String empno) {
		employeesRepo.deleteById(empno);
	}

	// 新增、更新
	public Employees saveEmployees(Employees emp) {
		return employeesRepo.save(emp);
	}

	// 依照欄位查詢
	public List<Employees> getEmployeesByCol(String col, String colvalue) {
		String hql = "FROM Employees e WHERE e." + col + " LIKE :colvalue";
		TypedQuery<Employees> query = entityManager.createQuery(hql, Employees.class);
		query.setParameter("colvalue", "%" + colvalue + "%");
		return query.getResultList();
	}

	// 查詢最後一個ID
	public String getLastEmployeeId() {
		List<Employees> employees = employeesRepo.findFirstByOrderByEmpnoDesc();
		return employees != null ? employees.get(0).getEmpno() : null;
	}

	public void logoutAndUpdate(String empno, Date lastTimeLogin) {
		employeesRepo.logoutUpdate(empno, lastTimeLogin);
	}

	// 找主管
	public Employees findManagerByDeptno(String deptno) {
		return employeesRepo.findFirstByDeptnoAndJob(deptno);
	}

	// 找ID
	public List<String> findAllEmployeeIds() {
		return employeesRepo.findAllEmployeeIds();
	}

	// 找全部
	public List<Employees> findAll() {
		return employeesRepo.findAll();
	}

	public Employees forgotpassword(String empno, String phone, String identityID) {
		return employeesRepo.forgotpassword(empno, phone, identityID);
	}

	public void updatepassword(String empno, String password) {
		employeesRepo.updatepassword(empno, password);
	}
}
