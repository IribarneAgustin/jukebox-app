package com.juke.api.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.juke.api.model.Transaction;
import com.juke.api.service.TransactionService;

@RestController
@RequestMapping("/transaction")
public class TransactionController {
	
	@Autowired
	private TransactionService transactionService;
	
	
	@GetMapping("/get")
	public List<Transaction> findAllByActiveTrueOrderByCreationTimestampDesc() {
		return transactionService.findAllByActiveTrueOrderByCreationTimestampDesc();		
	}
	
	@GetMapping("/get/total")
	public ResponseEntity<Map<String, Object>> getTotalAmount () {
		return transactionService.getTotalAmount();
	}
	
	@GetMapping("/get/amountByYear/{year}")
	public ResponseEntity<Map<String, Object>> getTotalAmountOfLastYearSeparatedByMonth(@PathVariable(name = "year") Integer year) {
		return transactionService.getTotalAmountOfLastYearSeparatedByMonth(year);
		
	}
	

}
