package com.example.demo.repository;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.example.demo.entity.RoomReserve;

import jakarta.persistence.criteria.Predicate;

public class RoomReserveFuzzyQuery { //Specification用于定义 JPA 的查询规约
	public static Specification<RoomReserve> containsTextInAllFields(String text, Date startDate, Date endDate) {
		return (root, query, builder) -> {
			List<Predicate> predicates = new ArrayList<>();//用来存放所有的查询条件的結果。



            if (text != null && !text.isEmpty()) {
	            // 一一比用模糊查詢比對以下欄位
	            predicates.add(builder.like(root.get("reserveTitle"), "%" + text + "%"));
	            predicates.add(builder.like(root.get("roomName"), "%" + text + "%"));
	            predicates.add(builder.like(root.get("empno"), "%" + text + "%"));
	            predicates.add(builder.like(root.get("status"), "%" + text + "%"));
            }
            if (startDate != null && endDate != null) {
            	predicates.add(builder.between(root.get("eventDay"), startDate, endDate));
            }
			// 時間因為不是字串不能使用，但有解決辦法，之後可以想
//            predicates.add(builder.like(root.get("eventDay"), "%" + text + "%"));
//            predicates.add(builder.like(root.get("startTime"), "%" + text + "%"));
//            predicates.add(builder.like(root.get("endTime"), "%" + text + "%"));
			// fakeDelete是0&1，不適合模糊查詢
//            predicates.add(builder.like(root.get("fakeDelete"), "%" + text + "%"));

//            predicates.add(builder.like(root.get("hasFile"), "%" + text + "%"));
			// 如果有更多字段需要模糊查询，可以在这里添加

			return builder.or(predicates.toArray(new Predicate[0]));
		};
	}
}
