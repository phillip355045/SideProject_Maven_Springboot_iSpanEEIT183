package com.example.demo.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entity.WorklogItem;
import com.example.demo.entity.Worklogs;
import com.example.demo.repository.WorklogItemRepository;
import com.example.demo.repository.WorklogsRepository;

@Service
public class WorklogItemService {

	@Autowired
	private WorklogItemRepository worklogItemRepo;
	
	@Autowired
	private WorklogsRepository worklogsRepo;
	
	
	 public WorklogItem insertWorklogItem(Long worklogId, WorklogItem worklogItem) {
	        Optional<Worklogs> worklogsOptional = worklogsRepo.findById(worklogId);
	        if (worklogsOptional.isPresent()) {
	            Worklogs worklogs = worklogsOptional.get();
	            worklogItem.setWorklogs(worklogs);
	            return worklogItemRepo.save(worklogItem);
	        }
	        return null;
	    }

	    public List<WorklogItem> getSpecificWorklogItems(Long worklogId) {
	        Optional<Worklogs> worklogOptional = worklogsRepo.findById(worklogId);
	        return worklogOptional.map(Worklogs::getWorklogItems).orElse(null);
	    }

	    public WorklogItem updateWorklogItem(Long itemId, WorklogItem updatedItem) {
	        return worklogItemRepo.findById(itemId).map(item -> {
	            item.setDeptname(updatedItem.getDeptname());
	            item.setJobType(updatedItem.getJobType());
	            item.setJobDescription(updatedItem.getJobDescription());
	            item.setRegularHours(updatedItem.getRegularHours());
	            item.setOvertimeHours(updatedItem.getOvertimeHours());
	            return worklogItemRepo.save(item);
	        }).orElse(null);
	    }

	 // 假刪除工作項目
	    public void softDeleteWorklogItem(Long itemId) {
	        worklogItemRepo.findById(itemId).ifPresent(item -> {
	            item.setFakeDelete("1");
	            worklogItemRepo.save(item);
	        });
	    }
	
	
	
}
