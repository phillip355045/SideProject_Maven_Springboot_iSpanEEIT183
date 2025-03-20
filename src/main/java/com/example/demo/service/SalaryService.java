package com.example.demo.service;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;

import com.example.demo.dto.SalTotalDTO;
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

public class SalaryService {
	@Autowired
	private SalRecordFinalRepository salRecordRepo;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	public Map<String, Object> getSalaryData(String empno, String year, String month) {
		Map<String, Object> resultMap = new HashMap<>();

		// Define the query and set parameters
		String query = "DECLARE @empno NVARCHAR(10) = ?; " + "DECLARE @year NVARCHAR(10) = ?; "
				+ "DECLARE @month NVARCHAR(10) = ?; " +

				" DECLARE @sal DECIMAL;\r\n"
				+ "    DECLARE @deptno NVARCHAR(10);\r\n"
				+ "    DECLARE @job NVARCHAR(10);\r\n"
				+ "    DECLARE @mgr NVARCHAR(10);\r\n"
				+ "    DECLARE @name NVARCHAR(10);\r\n"
				+ "    DECLARE @foodAllowance DECIMAL = 1000;\r\n"
				+ "    DECLARE @trafficAllowance DECIMAL = 0;\r\n"
				+ "    DECLARE @mgrAllowance DECIMAL = 0;\r\n"
				+ "    DECLARE @holidayAllowance DECIMAL = 0;\r\n"
				+ "    DECLARE @totalOvertimeHours DECIMAL = 0;\r\n"
				+ "    DECLARE @overtimePay DECIMAL = 0;\r\n"
				+ "    DECLARE @attendanceBonus DECIMAL = 0;\r\n"
				+ "    DECLARE @halfpaidHours  int = 0;\r\n"
				+ "	 DECLARE @fullpaidHours  int = 0;\r\n"
				+ "	 DECLARE @unpaidHours  int = 0;\r\n"
				+ "\r\n"
				+ "    DECLARE @leavePay DECIMAL = 0;\r\n"
				+ "	DECLARE @invoice  DECIMAL = 0;\r\n"
				+ "\r\n"
				+ "    DECLARE @laborIhealthLevelId INT = 0;\r\n"
				+ "    DECLARE @laborInsurance DECIMAL = 0;\r\n"
				+ "    DECLARE @healthInsurance DECIMAL = 0;\r\n"
				+ "    DECLARE @laborInsuranceCompany DECIMAL = 0;\r\n"
				+ "    DECLARE @healthInsuranceCompany DECIMAL = 0;\r\n"
				+ "\r\n"
				+ "	DECLARE @status NVARCHAR(10)='未發送';\r\n"
				+ "\r\n"
				+ "    -- 從 employees 表中查詢員工基本信息\r\n"
				+ "    SELECT \r\n"
				+ "        @sal = sl.sal,\r\n"
				+ "        @deptno = e.deptno,\r\n"
				+ "        @job = e.job,\r\n"
				+ "        @mgr = e.mgr,\r\n"
				+ "        @name = e.name\r\n"
				+ "    FROM employees e\r\n"
				+ "    JOIN salLevel sl ON e.salGrade = sl.salGrade\r\n"
				+ "    WHERE e.empno = @empno;\r\n"
				+ "\r\n"
				+ "    -- 計算交通津貼\r\n"
				+ "    SELECT @trafficAllowance = COALESCE(tl.trafficAllowance, 0)\r\n"
				+ "    FROM employees e\r\n"
				+ "    LEFT JOIN trafficLevel tl ON e.city = tl.city\r\n"
				+ "    WHERE e.empno = @empno;\r\n"
				+ "\r\n"
				+ "    -- 計算主管津貼\r\n"
				+ "    SET @mgrAllowance = CASE WHEN NULLIF(@mgr, '') IS NULL THEN 5000 ELSE 0 END;\r\n"
				+ "\r\n"
				+ "    -- 計算節日禮金\r\n"
				+ "    SET @holidayAllowance = CASE \r\n"
				+ "        WHEN @month = '01' THEN @sal * 2 \r\n"
				+ "        WHEN @month IN ('06', '09') THEN 3000 \r\n"
				+ "        ELSE 0 \r\n"
				+ "    END;\r\n"
				+ "\r\n"
				+ "	\r\n"
				+ "    -- 計算病假、事假、年假的工時和薪資相關數據\r\n"
				+ "    SELECT \r\n"
				+ "        @halfpaidHours = ISNULL(SUM(CASE WHEN lr.requestTypeID = '1' AND YEAR(lr.startTime) = @year AND MONTH(lr.startTime) = @month THEN lr.totalHours ELSE 0 END), 0),\r\n"
				+ "        @unpaidHours = ISNULL(SUM(CASE WHEN lr.requestTypeID = '2' AND YEAR(lr.startTime) = @year AND MONTH(lr.startTime) = @month THEN lr.totalHours ELSE 0 END), 0),\r\n"
				+ "        @fullpaidHours = ISNULL(SUM(CASE WHEN lr.requestTypeID NOT IN ('1', '2') AND YEAR(lr.startTime) = @year AND MONTH(lr.startTime) = @month THEN lr.totalHours ELSE 0 END), 0),\r\n"
				+ "        @leavePay = @sal / 240 * (0.5 * @halfpaidHours + @unpaidHours)\r\n"
				+ "    FROM leaveRequest lr\r\n"
				+ "    WHERE YEAR(lr.startTime) = @year AND MONTH(lr.startTime) = @month\r\n"
				+ "          AND lr.employeeID = @empno;\r\n"
				+ "\r\n"
				+ "\r\n"
				+ "-- 計算每筆加班費用並加總成 @totalOvertimePay\r\n"
				+ "SELECT \r\n"
				+ "     @totalOvertimeHours = ISNULL(SUM(\r\n"
				+ "            CASE \r\n"
				+ "                WHEN workoff > CAST('18:00:00' AS TIME) THEN \r\n"
				+ "                    DATEDIFF(MINUTE, '18:00:00', workoff) / 60.0\r\n"
				+ "                ELSE 0\r\n"
				+ "            END\r\n"
				+ "        ), 0),\r\n"
				+ "  @attendanceBonus = CASE \r\n"
				+ "        WHEN @halfpaidHours = 0 AND @fullpaidHours = 0 AND @unpaidHours = 0\r\n"
				+ "        THEN 1000 \r\n"
				+ "        ELSE 0 \r\n"
				+ "    END,\r\n"
				+ "  @OvertimePay = ISNULL(SUM(\r\n"
				+ "            CASE \r\n"
				+ "                WHEN workoff IS NULL THEN 0\r\n"
				+ "                WHEN CAST(workoff AS TIME) <= CAST('18:00:00' AS TIME) THEN 0\r\n"
				+ "                WHEN DATEDIFF(MINUTE, '18:00:00', workoff) / 60.0 <= 2 THEN \r\n"
				+ "                    (DATEDIFF(MINUTE, '18:00:00', workoff) / 60.0) * 1.34 * (@sal / 240)\r\n"
				+ "                WHEN DATEDIFF(MINUTE, '18:00:00', workoff) / 60.0 <= 4 THEN \r\n"
				+ "                    2 * 1.34 * (@sal / 240) + (DATEDIFF(MINUTE, '18:00:00', workoff) / 60.0 - 2) * 1.67 * (@sal / 240)\r\n"
				+ "                ELSE \r\n"
				+ "                    (2 * 1.34 + 2 * 1.67 + (DATEDIFF(MINUTE, '18:00:00', workoff) / 60.0 - 4) * 2) * (@sal / 240)\r\n"
				+ "            END\r\n"
				+ "        ), 0)\r\n"
				+ "FROM checkIn\r\n"
				+ "WHERE empno = @empno AND YEAR(date) = @year AND MONTH(date) = @month;\r\n"
				+ "\r\n"
				+ "    -- 查找對應的 laborIhealthLevelId\r\n"
				+ "    SELECT TOP 1 @laborIhealthLevelId = id\r\n"
				+ "    FROM laborIhealthLevel\r\n"
				+ "    WHERE (@sal + @foodAllowance + @trafficAllowance + @mgrAllowance + @holidayAllowance + @overtimePay + @attendanceBonus)\r\n"
				+ "          BETWEEN minSalGetTotal AND maxSalGetTotal;\r\n"
				+ "\r\n"
				+ "    -- 計算勞保和健保\r\n"
				+ "   SELECT \r\n"
				+ "    @laborInsurance = ROUND(\r\n"
				+ "        CASE \r\n"
				+ "            WHEN id = 52 THEN minSalGetTotal * 0.0240 \r\n"
				+ "            ELSE maxSalGetTotal * 0.0240 \r\n"
				+ "        END, 0),\r\n"
				+ "    @healthInsurance = ROUND(\r\n"
				+ "        CASE \r\n"
				+ "            WHEN id = 52 THEN minSalGetTotal * 0.0155 \r\n"
				+ "            ELSE maxSalGetTotal * 0.0155 \r\n"
				+ "        END, 0)\r\n"
				+ "FROM laborIhealthLevel\r\n"
				+ "WHERE id = @laborIhealthLevelId;\r\n"
				+ "\r\n"
				+ "    -- 計算公司負擔的勞保和健保\r\n"
				+ "    SET @laborInsuranceCompany = @laborInsurance * 0.7;\r\n"
				+ "    SET @healthInsuranceCompany = @healthInsurance * 0.7;"

				
				+ "SELECT @empno AS empno, @year AS year, @month AS month, @sal AS sal, @deptno AS deptno, @job AS job, @mgr AS mgr, @name AS name, "
				+ "@foodAllowance AS foodAllowance, @trafficAllowance AS trafficAllowance, @mgrAllowance AS mgrAllowance, @holidayAllowance AS holidayAllowance, "
				+ "@totalOvertimeHours AS totalOvertimeHours, @overtimePay AS overtimePay, @attendanceBonus AS attendanceBonus , @laborIhealthLevelId AS laborIhealthLevelId, @laborInsurance AS laborInsurance, @healthInsurance AS healthInsurance, @laborInsuranceCompany AS laborInsuranceCompany, @healthInsuranceCompany AS healthInsuranceCompany, @halfpaidHours AS halfpaidHours, @fullpaidHours AS fullpaidHours, @unpaidHours AS unpaidHours, @leavePay AS leavePay, @invoice AS invoice, @status AS status;;";
		// Execute the query and fetch results
		SqlRowSet rowSet = jdbcTemplate.queryForRowSet(query, empno, year, month);
		if (rowSet.next()) {
			resultMap.put("sal", rowSet.getBigDecimal("sal"));
			
			 // Check laborInsurance and set it to 1100 if it's greater than 1100
		    BigDecimal laborInsurance = rowSet.getBigDecimal("laborInsurance");
		    if (laborInsurance.compareTo(new BigDecimal("1100")) > 0) {
		        laborInsurance = new BigDecimal("1100");
		    }
		    
		    resultMap.put("laborInsurance", laborInsurance);
			
			resultMap.put("mgrAllowance", rowSet.getBigDecimal("mgrAllowance"));
			resultMap.put("healthInsurance", rowSet.getBigDecimal("healthInsurance"));
			resultMap.put("foodAllowance", rowSet.getBigDecimal("foodAllowance"));
			resultMap.put("leavePay", rowSet.getBigDecimal("leavePay"));
			resultMap.put("trafficAllowance", rowSet.getBigDecimal("trafficAllowance"));
			resultMap.put("holidayAllowance", rowSet.getBigDecimal("holidayAllowance"));
			resultMap.put("overtimePay", rowSet.getBigDecimal("overtimePay"));
			resultMap.put("attendanceBonus", rowSet.getBigDecimal("attendanceBonus"));

			resultMap.put("deptno", rowSet.getString("deptno"));
			resultMap.put("job", rowSet.getString("job"));
			resultMap.put("mgr", rowSet.getString("mgr"));
			resultMap.put("name", rowSet.getString("name"));
//	       status要調整
			resultMap.put("status", rowSet.getString("status"));
			
			resultMap.put("totalOvertimeHours", rowSet.getBigDecimal("totalOvertimeHours"));
			resultMap.put("laborIhealthLevelId", rowSet.getInt("laborIhealthLevelId"));
			resultMap.put("halfpaidHours", rowSet.getInt("halfpaidHours"));
			resultMap.put("fullpaidHours", rowSet.getInt("fullpaidHours"));
			resultMap.put("unpaidHours", rowSet.getInt("unpaidHours"));
			resultMap.put("invoice", rowSet.getBigDecimal("invoice"));
			resultMap.put("laborInsuranceCompany", rowSet.getBigDecimal("laborInsuranceCompany"));
			resultMap.put("healthInsuranceCompany", rowSet.getBigDecimal("healthInsuranceCompany"));

//	            resultMap.put("totalPayment", rowSet.getBigDecimal("totalPayment"));
//	            resultMap.put("totalDeduction", rowSet.getBigDecimal("totalDeduction"));
//	            resultMap.put("netSalary", rowSet.getBigDecimal("netSalary"));
		}

		return resultMap;
	}
}