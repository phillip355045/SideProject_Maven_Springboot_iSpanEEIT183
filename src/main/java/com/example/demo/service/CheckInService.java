package com.example.demo.service;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.example.demo.entity.CheckIn;
import com.example.demo.entity.CheckInId;
import com.example.demo.repository.CheckInRepository;
import com.example.demo.repository.EmployeesRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;

@Service
public class CheckInService {

	@Autowired
	private CheckInRepository checkInRepo;

	@Autowired
	private EmployeesRepository employeesRepo;

	@PersistenceContext
	private EntityManager entityManager;
//
//    private List<String> getAllEmployeeIds() {
//        return employeesRepo.findAllEmployeeIds();
//    }
//
	@Autowired
	private NotificationService notificationService;
    // 定時，每天自動新增
	@Scheduled(cron = "00 03 10 * * ?")
//	@Scheduled(cron = "00 33 13 * * SUN-THU") 
    public void generateDailyCheckIns() {
        List<String> employeeIds = getAllEmployeeIds();
        Date today = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(today);
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        Date tomorrow = calendar.getTime();
        System.out.println("打卡日期排成啟動!");
        for (String empno : employeeIds) {
            CheckInId checkInId = new CheckInId();
            checkInId.setEmpno(empno);
            checkInId.setDate(tomorrow);
            if (!checkInRepo.existsById(checkInId)) {
                CheckIn checkIn = new CheckIn();
                checkIn.setCheckInId(checkInId);               
                checkInRepo.save(checkIn);
            }
        }
        notificationService.createNotificationBasisOnEmpno("打卡日期排成啟動", "A0001");
    }

	// 建立sql資料表
	public void generateCheckInsForAllEmployees(int year, int month) {
		List<String> employeeIds = getAllEmployeeIds();

		for (String empno : employeeIds) {
			LocalDate startDate = LocalDate.of(year, month, 1);
			LocalDate endDate = startDate.plusMonths(1).minusDays(1);

			LocalDate date = startDate;
			while (date.isBefore(endDate) || date.isEqual(endDate)) {
				if (date.getDayOfWeek() != DayOfWeek.SATURDAY && date.getDayOfWeek() != DayOfWeek.SUNDAY) {
					// 處理一到五
					CheckInId checkInId = new CheckInId();
					checkInId.setEmpno(empno);
					checkInId.setDate(Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant()));

					// 檢查資料庫
					if (!checkInRepo.existsById(checkInId)) {
						CheckIn checkIn = new CheckIn();
						checkIn.setCheckInId(checkInId);
						checkInRepo.save(checkIn);
					}
				}
				date = date.plusDays(1);
			}
		}
	}

	private List<String> getAllEmployeeIds() {
		return employeesRepo.findAllEmployeeIds();
	}

	public CheckIn getAccountCheckIn(String empno) throws ParseException {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date today = dateFormat.parse(dateFormat.format(new Date()));
		List<CheckIn> checkIns = checkInRepo.findByCheckInIdEmpnoAndCheckInIdDate(empno, today);
		if (checkIns.isEmpty()) {
			return null;
		} else {
			CheckIn checkIn = checkIns.get(0);
			return checkIn;
		}

	}

	// 打卡
	@Transactional
	public void handleCheckIn(String empno, String checkinTime) throws ParseException {
		SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

		Date parsedCheckinTime = inputFormat.parse(checkinTime);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(parsedCheckinTime);
		calendar.add(Calendar.HOUR_OF_DAY, 8);
		parsedCheckinTime = calendar.getTime();
		Date today = dateFormat.parse(dateFormat.format(new Date()));

		List<CheckIn> checkIns = checkInRepo.findByCheckInIdEmpnoAndCheckInIdDate(empno, today);
		if (checkIns.isEmpty()) {
			CheckIn newCheckIn = new CheckIn();
			CheckInId checkInId = new CheckInId();
			checkInId.setEmpno(empno);
			checkInId.setDate(today);
			newCheckIn.setCheckInId(checkInId);
			newCheckIn.setWorkon(parsedCheckinTime);
			newCheckIn.setWorkoff(null);
			newCheckIn.setNote("");
			newCheckIn.setRecord("");
			checkInRepo.save(newCheckIn);
		} else {
			CheckIn checkIn = checkIns.get(0);
			if (checkIn.getWorkon() == null) {
				checkIn.setWorkon(parsedCheckinTime);
			} else {
				checkIn.setWorkoff(parsedCheckinTime);
			}
			checkInRepo.save(checkIn);
		}
	}

	// 按照分頁
	public Page<CheckIn> getCheckInPage(int page, int size) {
		Pageable pageable = PageRequest.of(page - 1, size);
		return checkInRepo.findAll(pageable);
	}

	// 找全部
	public List<CheckIn> getAllCheckIns() {
		List<CheckIn> checkIn = checkInRepo.findAll();
		return checkIn;
	}

	// 獲取單一
	public CheckIn findCheckinById(CheckInId checkInId) {
		Optional<CheckIn> optional = checkInRepo.findById(checkInId);

		if (optional.isPresent()) {
			return optional.get();
		}
		return null;
	}

	// 新增、更新
	public CheckIn saveCheckIn(CheckIn checkIn) {
		return checkInRepo.save(checkIn);
	}

	// 刪除
	public void deleteById(CheckInId checkInId) {
		checkInRepo.deleteById(checkInId);
	}
	
	public void deleteByempno(String empno,Date date) {
		checkInRepo.deleteByempno(empno,date);
	}

	// 依照欄位查詢
	public List<CheckIn> getCheckInByCol(String col, String colvalue) throws ParseException {
		TypedQuery<CheckIn> query = null;
		String hql = "FROM CheckIn e WHERE e." + col;

		if (colvalue.contains("-")) {
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			Date date = dateFormat.parse(colvalue);
			hql += " = :colvalue";
			query = entityManager.createQuery(hql, CheckIn.class);
			query.setParameter("colvalue", date);
		} else {
			hql += " LIKE :colvalue";
			query = entityManager.createQuery(hql, CheckIn.class);
			query.setParameter("colvalue", "%" + colvalue + "%");
		}

		return query.getResultList();
	}

	public List<CheckIn> getEmpCheckIn(String empno) throws ParseException {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date today = dateFormat.parse(dateFormat.format(new Date()));
		return checkInRepo.findByEmpnoBeforeToday(empno, today);
//		return checkInRepo.findByEmpno(empno);
	}

	// 依照欄位批量刪除打卡資料
	@Transactional
	public void deleteByCol(String col, String colvalue, String operator) throws ParseException {
		String sql = "DELETE FROM CheckIn WHERE ";
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

		if (colvalue.contains("-")) {
			Date date = dateFormat.parse(colvalue);
			sql += "date " + operator + " :colvalue AND empno IS NOT NULL";
			entityManager.createNativeQuery(sql).setParameter("colvalue", date).executeUpdate();
		} else {
			sql += "empno LIKE :colvalue AND date IS NOT NULL";
			entityManager.createNativeQuery(sql).setParameter("colvalue", "%" + colvalue + "%").executeUpdate();
		}

	}

	public String findByCheckInId(String empno) throws ParseException {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date today = dateFormat.parse(dateFormat.format(new Date()));

		List<CheckIn> check = checkInRepo.findByEmpnoAndDate(empno, today);
		
		System.out.println("測試"+check);
		
		String note = null;

		if (!check.isEmpty()) {
			Date workon = check.get(0).getWorkon();
			if (workon != null) {
				LocalTime currentTime = LocalTime.now();
				LocalTime thresholdTime = LocalTime.of(18, 0, 0);

				if (currentTime.isAfter(thresholdTime)) {
					note = "workoff";
				} else {
					note = "cantworkoff";
				}
			} else {
				note = "workon";
			}
		} else {
			note = "workon";
		}

		return note;

//		List<CheckIn> checkin = checkInRepo.findByEmpnoAndDate(empno, today);
//		if (checkin != null) {
//			Date workon = checkin.get(0).getWorkon();
//			if(workon!=null) {
//				workon.plusHours(8);
//			}
//		}
//		return null;
	}

	
	public List<CheckIn> empCheckInSearchByMonth(String empno,Date startDate,Date endDate){
		return checkInRepo.empCheckInSearchByMonth(empno, startDate, endDate);
	}
	
	
}
