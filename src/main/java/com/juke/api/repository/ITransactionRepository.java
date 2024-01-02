package com.juke.api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.juke.api.model.Transaction;


@Repository
public interface ITransactionRepository extends JpaRepository<Transaction, Long>{
	
	public List<Transaction> findLastTenByOrderByCreationTimestampDesc();

	public List<Transaction> findAllByActiveTrueOrderByCreationTimestampDesc();


}
