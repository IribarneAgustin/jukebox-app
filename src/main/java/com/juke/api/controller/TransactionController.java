package com.juke.api.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
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
	

}
