package com.example.demo.service;

import java.util.ArrayList;
import java.util.Comparator;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dto.ReserveCountDTO;
import com.example.demo.entity.RoomReserve;
import com.example.demo.repository.RoomReserveRepository;





@Service
public class RoomReserveService {
	
	@Autowired
	private RoomReserveRepository roomReserveRepos;
	
	//ok
	//整批刪除
	public void deleteReservationsByIds(List<Integer> ids) {
		roomReserveRepos.deleteAllById(ids);
	}
	
	//ok
	//後台回傳會議室使用紀錄
	public List<ReserveCountDTO> findRoomReserveCount(){
		List<Object[]> roomReserveCount = roomReserveRepos.findRoomReserveCount();
		//lambda
		return roomReserveCount.stream().map(r ->
			new ReserveCountDTO((String) r[0], (Integer) r[1])).collect(Collectors.toList());
//		List<ReserveCountDTO> count = new ArrayList<>(); 
//		for (Object[] r : roomReserveCount) {
//			ReserveCountDTO reserveCountDTO = new ReserveCountDTO(
//					(String)r[0],
//					(Integer)r[1]);
//			count.add(reserveCountDTO);
//		}
//		return count;
	}
	
	//被datatable取代
	//後台回傳頁數物件
//	public Page<RoomReserve> findRoomReservePage(Integer pageNumber){
//		Pageable pgb = PageRequest.of(pageNumber-1, 5, Sort.Direction.DESC, "reserveno");
//		return roomReserveRepos.findAll(pgb);
//	}
	
	//ok
	//找全部並且順序相反
	public List<RoomReserve> findAll (){
		List<RoomReserve> reserves = roomReserveRepos.findAll();
//		reserves.sort(Comparator.comparing(RoomReserve::getReserveno).reversed());//因為datatable的關係所以順序反轉的效果不會出來
		return reserves;
	}
	///---------------------

	//ok
	public boolean TimeConflict(RoomReserve roomReserve) {
		List<RoomReserve> findReservaion = roomReserveRepos.findReservaion(roomReserve.getRoomName());
		//只要有一個符合回傳true，都沒有則回傳false
		//lambda
		return findReservaion.stream()
				.anyMatch(record -> "放行".equals(record.getStatus()) &&
						record.getEventDay().equals(roomReserve.getEventDay()) &&
						record.getStartTime().equals(roomReserve.getStartTime()) &&
						record.getEndTime().equals(roomReserve.getEndTime())
						);
		/*
		 * == 比较的是两个引用是否指向同一个对象。
			equals() 比较的是两个字符串的值是否相等。
		*/
//		for (RoomReserve record : findReservaion) {
//			if (record.getStatus().equals("放行")) {
//				if (record.getEventDay().equals(roomReserve.getEventDay())) {
//					if (record.getStartTime().equals(roomReserve.getStartTime()) && record.getEndTime().equals(roomReserve.getEndTime()) ) {
//						return true;
//					}
//				}			
//			}
//		}
//		return false;
	}
	
	public RoomReserve insert(RoomReserve roomReserve) {
		RoomReserve insertBean = roomReserveRepos.save(roomReserve);
		return insertBean;
	}
	
	//ok
	@Transactional  //可以確保所有操作都在同一個事務中做處裡，只要有任何一個步驟出錯，全部rollback
	public void deleteById(Integer id) {
		roomReserveRepos.deleteById(id);
	}
	
	public RoomReserve update(RoomReserve updateBean) {
		return roomReserveRepos.save(updateBean);
	}
	
	//ok
	public RoomReserve findById(Integer id) {
		Optional<RoomReserve> findById = roomReserveRepos.findById(id);
		if (findById != null) {
			return findById.get();
		}
		return null;
	}
	
	
	
	//查詢已經放行的預約紀錄
	public List<RoomReserve> findAllReservation(){
		List<RoomReserve> reserves = roomReserveRepos.findAll();
		
		List<RoomReserve> passReserves = new ArrayList<>();
		for (RoomReserve reserve : reserves) {
			if (reserve.getStatus().equals("放行")) {
				passReserves.add(reserve);
			}
		}

		return passReserves;
	}
	
	//ok
	public List<RoomReserve> findReservaion(String roomName) {
		return roomReserveRepos.findReservaion(roomName);
	}
	
	public List<RoomReserve> findByReserveTitle(String reserveTitle) {
		
		List<RoomReserve> byReserveTitle = roomReserveRepos.findByReserveTitle(reserveTitle);
		byReserveTitle.sort(Comparator.comparing(RoomReserve::getReserveno).reversed());
		return byReserveTitle;
	}
	
	//------員工端Service-------
	
	//用員工編號和假刪除判斷
	public List<RoomReserve> findReservaionByEmpnoAndFakeDelete (String fakeDelet, String empno){
		List<RoomReserve> reserves = roomReserveRepos.findReservaionByEmpnoAndFakeDelete(fakeDelet, empno);
//		reserves.sort(Comparator.comparing(RoomReserve::getReserveno).reversed());	
		return reserves;
	}

	//回傳全部-頁數-物件用員工編號、和假刪除判斷
	public List<RoomReserve> findReservaionByEmpnoAndFakeDelete(
			String fakeDelet, String empno,Integer pageNumber){
		List<RoomReserve> roomReserves = roomReserveRepos.findReservaionByEmpnoAndFakeDelete(fakeDelet,empno);
		return roomReserves;
	}
	
	//回傳預約資料
	public List<RoomReserve> findReservaionByEmpno(String empno){
		List<RoomReserve> result = roomReserveRepos.findReservaionByEmpno(empno);
		return result;
	}
	
	//被datatable取代
	//回傳全部-頁數-物件用員工編號、和假刪除判斷
//	public Page<RoomReserve> findReservaionByEmpnoAndFakeDeletePage(
//			String fakeDelet, String empno,Integer pageNumber){
//		Pageable pgb = PageRequest.of(pageNumber-1, 5, Sort.Direction.DESC, "reserveno");
//		Page<RoomReserve> page = roomReserveRepos.findReservaionByEmpnoAndFakeDeletePage(fakeDelet,empno,pgb);
//		return page;
//	}
	
//	///模糊查詢 -回傳頁數-物件用員工編號、和假刪除判斷
//	public Page<RoomReserve> findRoomReserveByEmpnoAndFakeDeleteAndFuzzyPage(
//			String query,String empno,Integer pageNumber){
//		Pageable pgb = PageRequest.of(pageNumber-1, 5, Sort.Direction.DESC, "reserveno");
//		Page<RoomReserve> page = roomReserveRepos.findRoomReserveByEmpnoAndFakeDeleteAndFuzzyPage(empno, query, pgb);	
//		return page;	
//	}
	
//	//模糊查詢
//    public List<RoomReserve> searchRoomReserves(String keyword, java.sql.Date startDate, java.sql.Date endDate) { //這裡用sqldate
//        Specification<RoomReserve> specification = RoomReserveFuzzyQuery.containsTextInAllFields(keyword,startDate,endDate);
//        List<RoomReserve> reserves = roomReserveRepos.findAll(specification);
//        reserves.sort(Comparator.comparing(RoomReserve::getReserveno).reversed());	
//        return reserves;
//    }
//    
//    //新模糊查詢
//    public Page<RoomReserve> fuzzySearch(String search,Integer pageNumber){
//    	Pageable pgb = PageRequest.of(pageNumber-1, 5, Sort.Direction.DESC, "reserveno");
//    	return roomReserveRepos.findRoomReserveByFuzzyPage(search, pgb);
//    }

	
}
