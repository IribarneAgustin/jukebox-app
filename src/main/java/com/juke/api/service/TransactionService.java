package com.juke.api.service;

import java.math.BigDecimal;
import java.sql.Timestamp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.juke.api.model.Transaction;
import com.juke.api.repository.ITransactionRepository;

@Service
public class TransactionService {

	@Autowired
	private ITransactionRepository transactionRepository;

	public void saveNewTransaction(String paymentId, String trackURI, Double amount) throws Exception{
		Transaction transaction = new Transaction();
		try {
			transaction.setActive(Boolean.TRUE);
			transaction.setPaymentId(paymentId);
			transaction.setTrackUri(trackURI);
			transaction.setCreationTimestamp(new Timestamp(System.currentTimeMillis()));
			transaction.setAmount(new BigDecimal(amount));
			transactionRepository.save(transaction);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

}
