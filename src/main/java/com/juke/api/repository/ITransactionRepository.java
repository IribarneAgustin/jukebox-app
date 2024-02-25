package com.juke.api.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.juke.api.model.Transaction;

@Repository
public interface ITransactionRepository extends JpaRepository<Transaction, Long> {

	public List<Transaction> findFirst10ByOrderByCreationTimestampDesc();

	public List<Transaction> findAllByActiveTrueOrderByCreationTimestampDesc();

	@Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t")
	public BigDecimal getTotalAmount();

	@Query(value = "SELECT EXTRACT(MONTH FROM t.creation_Timestamp) AS month, COALESCE(SUM(t.amount), 0) AS total_Amount "
	        + "FROM Transaction t "
	        + "WHERE EXTRACT(YEAR FROM t.creation_Timestamp) = :year "
	        + "GROUP BY EXTRACT(MONTH FROM t.creation_Timestamp)", nativeQuery = true)
	List<Map<Integer, BigDecimal>> getTotalAmountOfLastYearSeparatedByMonth(@Param("year") Integer year);


}
