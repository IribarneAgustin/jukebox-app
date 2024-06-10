package com.juke.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.juke.api.model.TrackOrder;
import com.juke.api.utils.OrderState;

@Repository
public interface OrderRepository extends JpaRepository<TrackOrder, Long>{
	
	public TrackOrder findByExternalReference(String externalReference);

	public TrackOrder findByIdAndActiveTrueAndState(Long id, OrderState state);

}
