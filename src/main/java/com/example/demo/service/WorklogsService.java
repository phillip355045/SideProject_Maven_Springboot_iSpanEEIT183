package com.example.demo.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.demo.dto.WorklogItemDTO;
import com.example.demo.dto.WorklogsDTO;
import com.example.demo.entity.Department;
import com.example.demo.entity.Employees;
import com.example.demo.entity.WorklogItem;
import com.example.demo.entity.Worklogs;
import com.example.demo.repository.DepartmentRepository;
import com.example.demo.repository.EmployeesRepository;
import com.example.demo.repository.WorklogItemRepository;
import com.example.demo.repository.WorklogsRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

@Service
public class WorklogsService {

	@Autowired
	private WorklogsRepository worklogsRepo;
	@Autowired
	private WorklogItemRepository worklogItemRepo;
	@PersistenceContext
	private EntityManager entityManager;

	@Autowired
	private EmployeesRepository employeesRepo;
	
	@Autowired
	private DepartmentRepository departmentRepo;

	public WorklogsService(WorklogsRepository worklogsRepo, EmployeesRepository employeesRepo) {
		this.worklogsRepo = worklogsRepo;
		this.employeesRepo = employeesRepo;
	}

	// 透過姓名帳號取得員工
	public List<Employees> getEmployeesByEmpnoAndName(String query) {
		return employeesRepo.findByEmpnoContainingOrNameContaining(query, query);
	}

	// 抓取員工編號
	public Employees getCurrentEmployee(String empno) {
		return employeesRepo.findByEmpno(empno);
	}
	
	// 抓取員工的部門編號
	public Department getCurrentEmployeeByDeptno(String deptno) {
		return departmentRepo.findByDeptno(deptno);
	}
	

	// 抓取員工編號->分頁
	public Page<Worklogs> getAllWorklogsByEmpno(String empno, int page, int size) {
		Pageable pageable = PageRequest.of(page - 1, size);
		return worklogsRepo.findByEmpnoAndNotFakeDeleted(empno, pageable);
	}

	// 抓取員工編號對應日誌id
	public Worklogs getWorklogByEmpnoAndId(String empno, Long worklogid) {
		return worklogsRepo.findByEmployeesEmpnoAndWorklogid(empno, worklogid).orElse(null); // 或者拋出異常
	}

	// 放在主頁的小框框->讓其顯示最新的前三筆
	public List<Worklogs> getWorklogsByEmpno(String empno) {
		Pageable topThree = PageRequest.of(0, 3);
		return worklogsRepo.findByEmpnoWithItems(empno, topThree);
	}

	// 管理者 查詢全部 不會顯示假刪除的資料
	public List<Worklogs> getAllWorklogs() {
		List<Worklogs> worklogs = worklogsRepo.findByFakeDeleteIsNullOrFakeDeleteFalse();
		return worklogs;
	}

	// 管理者模糊查詢
	public Page<Worklogs> searchWorklogs(String keyword, int page, int size) {
		Pageable pageable = PageRequest.of(page - 1, size);

		return worklogsRepo.searchAllFields(keyword, pageable);
	}

	// 分頁找全部
	public Page<Worklogs> getAllWorklogsPage(int page, int size) {
		Pageable pageable = PageRequest.of(page - 1, size);
		return worklogsRepo.findByFakeDeleteIsNullOrFakeDeleteFalse(pageable);
	}

	// 歷史紀錄分頁找全部 可看到假刪除的資料
	public Page<Worklogs> getAllWorklogsPage1(int page, int size) {
		Pageable pageable = PageRequest.of(page - 1, size);
		return worklogsRepo.findAllWorklogs(pageable);
	}

	// 歷史紀錄模糊查詢
	public Page<Worklogs> searchAllWorklogs(String keyword, int page, int size) {
		Pageable pageable = PageRequest.of(page - 1, size);
		return worklogsRepo.searchAll(keyword, pageable);
	}

	// 審核分頁找全部
	public Page<Worklogs> ReviewAllWorklogsPage(int page, int size) {
		Pageable pageable = PageRequest.of(page - 1, size);
		return worklogsRepo.findAllActiveWorklogs(pageable);
	}

	// 審核的模糊查詢
	public Page<Worklogs> ReviewsearchWorklogs(String keyword, int page, int size) {
		Pageable pageable = PageRequest.of(page - 1, size);

		return worklogsRepo.ReviewsearchAllFields(keyword, pageable);
	}

	// 使用者
	// 總表只提供查詢
	public List<Worklogs> UserAllWorklogs() {
		List<Worklogs> worklogs = worklogsRepo.findByFakeDeleteIsNullOrFakeDeleteFalse();
		return worklogs;
	}

	// 使用者的模糊查詢
	public Page<Worklogs> UsersearchWorklogs(String empno, String keyword, int page, int size) {
		Pageable pageable = PageRequest.of(page - 1, size);

		return worklogsRepo.UsersearchAllFields(empno, keyword, pageable);
	}

	// 查詢單筆
	public Worklogs getWorklog(Long worklogid) {
		Optional<Worklogs> optional = worklogsRepo.findById(worklogid);

		if (optional.isPresent()) {
			return optional.get();
		}
		return null;
	}

	public Worklogs getWorklogWithItems(Long worklogid) {
		return worklogsRepo.findByIdWithItems(worklogid);
	}

	// 新增
	public Worklogs insertWorklog(Worklogs worklogs) {
		return worklogsRepo.save(worklogs);
	}

	// 一鍵生成假資料
	public void generateFakeData() {
		Employees employee = employeesRepo.findByEmpno("A0001");
		if (employee == null) {
			employee = new Employees();
			employeesRepo.save(employee);
		}
		
		Department department = departmentRepo.findByDeptno("D01");
	    if (department == null) {
	        department = new Department();
	        departmentRepo.save(department);
	    }
	    
		Worklogs worklogs = new Worklogs();
		worklogs.setDepartment(department);
		worklogs.setEmployees(employee);

		// 使用 SimpleDateFormat 將字串轉換為 Date 類型
		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			Date date = dateFormat.parse("2023-05-20");
			worklogs.setWorklogdate(date);
		} catch (Exception e) {
			e.printStackTrace();
		}

		worklogs.setReviewstatus("待審核");
		worklogs.setFakeDelete("0");

		Worklogs savedWorklog = worklogsRepo.save(worklogs);

		WorklogItem worklogItem = new WorklogItem();
		worklogItem.setDeptname("人力資源部門");
		worklogItem.setJobType("員工調查");
		worklogItem.setJobDescription("進行員工滿意度調查，收集反饋意見。");
		worklogItem.setRegularHours(5.5);
		worklogItem.setOvertimeHours(1.0);
		worklogItem.setFakeDelete("0");
		worklogItem.setWorklogs(savedWorklog);

		worklogItemRepo.save(worklogItem);
	}

	// 修改
	public Worklogs getWorklogById(Long worklogId) {
		return worklogsRepo.findByIdWithItems(worklogId);
	}

	@Transactional
	public Worklogs updateWorklog(Long worklogId, Worklogs updatedWorklog) {
		return worklogsRepo.findById(worklogId).map(worklogs -> {
	        Department department = departmentRepo.findByDeptno(updatedWorklog.getDepartment().getDeptno());
	        if (department != null) {
	            worklogs.setDepartment(department);
	        } else {
	            throw new EntityNotFoundException("Department not found: " + updatedWorklog.getDepartment().getDeptno());
	        }
			// 找到相應的員工
			Employees employee = employeesRepo.findByEmpno(updatedWorklog.getEmployees().getEmpno());
			if (employee != null) {
				worklogs.setEmployees(employee);
			} else {
				throw new EntityNotFoundException("Employee not found: " + updatedWorklog.getEmployees().getEmpno());
			}

			worklogs.setWorklogdate(updatedWorklog.getWorklogdate());
			worklogs.setReviewstatus("待審核");

			// 工作項目
			List<WorklogItem> updatedItems = updatedWorklog.getWorklogItems();
			if (updatedItems != null) {
				// 清除所有關聯
				worklogs.getWorklogItems().clear();

				for (WorklogItem updatedItem : updatedItems) {
					Long updatedItemId = updatedItem.getItemId();
					if (updatedItemId != null) {
						Optional<WorklogItem> existingItemOptional = worklogItemRepo.findById(updatedItemId);
						if (existingItemOptional.isPresent()) {
							WorklogItem existingItem = existingItemOptional.get();
							existingItem.setDeptname(updatedItem.getDeptname());
							existingItem.setJobType(updatedItem.getJobType());
							existingItem.setJobDescription(updatedItem.getJobDescription());
							existingItem.setRegularHours(updatedItem.getRegularHours());
							existingItem.setOvertimeHours(updatedItem.getOvertimeHours());
							existingItem.setWorklogs(worklogs);

							worklogs.getWorklogItems().add(existingItem);
						} else {
							throw new EntityNotFoundException("工作項目不存在: " + updatedItemId);
						}
					} else {
						// 如果WorklogItem ID null (但基本上不會
						WorklogItem newItem = new WorklogItem();
						newItem.setDeptname(updatedItem.getDeptname());
						newItem.setJobType(updatedItem.getJobType());
						newItem.setJobDescription(updatedItem.getJobDescription());
						newItem.setRegularHours(updatedItem.getRegularHours());
						newItem.setOvertimeHours(updatedItem.getOvertimeHours());
						newItem.setWorklogs(worklogs);

						worklogs.getWorklogItems().add(newItem);
					}
				}
			}

			return worklogsRepo.save(worklogs);
		}).orElse(null);
	}

	// 假刪除工作日誌
	public void softDeleteWorklog(Long worklogId) {
		Worklogs worklogs = worklogsRepo.findById(worklogId)
				.orElseThrow(() -> new IllegalArgumentException("Invalid worklog ID"));
		worklogs.setFakeDelete("1");
		for (WorklogItem item : worklogs.getWorklogItems()) {
			item.setFakeDelete("1");
		}
		worklogsRepo.save(worklogs);
	}


	// 查詢所有未假刪除的工作日誌
	public List<Worklogs> findAllActiveWorklogs() {
		return worklogsRepo.findByFakeDelete("0");
	}

	// 找到工作項目
	public List<WorklogItem> getAllWorklogItems(Long worklogId) {
		Optional<Worklogs> worklogOptional = worklogsRepo.findById(worklogId);

		// 是否找到了對應的工作日誌
		if (worklogOptional.isPresent()) {
			Worklogs worklog = worklogOptional.get();
			return worklog.getWorklogItems(); // 顯示工作日誌的所有工作項目
		} else {
			return null;
		}
	}

	// 審核需用到的
	public Worklogs findById(Long id) {
		Optional<Worklogs> worklogOptional = worklogsRepo.findById(id);
		return worklogOptional.orElse(null);
	}

	public void save(Worklogs worklogs) {
		worklogsRepo.save(worklogs);
	}

	public Worklogs getWorklogWithItems1(Long worklogId) {
		// 根據工作日誌ID查詢工作日誌、項目
		return worklogsRepo.findById(worklogId).orElse(null);
	}

	public void updateReviewStatus(Long worklogid, Worklogs worklogs, String reviewstatus) {
		Worklogs worklog = worklogsRepo.findById(worklogid).orElse(null);
		if (worklog != null) {
			Department department = departmentRepo.findByDeptno(worklogs.getDepartment().getDeptno());
		    if (department != null) {
		        worklog.setDepartment(department);
		    }
		    Employees employee = employeesRepo.findByEmpno(worklogs.getEmployees().getEmpno());
		    if (employee != null) {
		        worklog.setEmployees(employee);
		    }

			worklog.setWorklogdate(worklogs.getWorklogdate());
			worklog.setReviewstatus(reviewstatus);

			// 清除原有的工作項目 不然會存不進去 因為關聯式的關係
			worklog.getWorklogItems().clear();

//			List<WorklogItem> worklogItems = new ArrayList<>();
			for (WorklogItem item : worklogs.getWorklogItems()) {
		        WorklogItem newItem = new WorklogItem();
		        newItem.setDeptname(item.getDeptname());
		        newItem.setJobType(item.getJobType());
		        newItem.setJobDescription(item.getJobDescription());
		        newItem.setRegularHours(item.getRegularHours());
		        newItem.setOvertimeHours(item.getOvertimeHours());
		        newItem.setWorklogs(worklog);
		        worklog.getWorklogItems().add(newItem);
		    }

			worklogsRepo.save(worklog);
		}
	}

}
