package com.juke.api.service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.juke.api.dto.TrackInfoDTO;
import com.juke.api.model.TrackOrder;
import com.juke.api.model.Track;
import com.juke.api.repository.OrderRepository;
import com.juke.api.utils.OrderState;

@Service
public class OrderService {
	
	@Autowired
	private OrderRepository orderRepository;
	
	@Autowired
	private TrackService trackService;
	
	public TrackOrder save(TrackOrder order) {
		return orderRepository.save(order);		
	}
	
	//without referenceNumber
	public TrackOrder createAndSaveNewOrder(TrackInfoDTO trackInfo, BigDecimal amount) {
		TrackOrder order = new TrackOrder();
		Track track = trackService.findBySpotifyURI(trackInfo.getTrackUri());
		if (track == null) {
			track = new Track();
			track.setActive(Boolean.TRUE);
			track.setAlbumCover(trackInfo.getAlbumCover());
			track.setArtistName(trackInfo.getArtistName());
			track.setDescription(trackInfo.getArtistName() + " " + trackInfo.getTrackName());
			track.setSpotifyURI(trackInfo.getTrackUri());
			track.setTrackName(trackInfo.getTrackName());
			
			trackService.save(track);
		}
		order.setTrack(track);
		order.setState(OrderState.PENDING);
		order.setActive(Boolean.TRUE);
		order.setAmount(amount);
		order.setCreationTimestamp(new Timestamp(System.currentTimeMillis()));
		return save(order);
	}
	
	public TrackOrder findByExternalReference(String externalReference) {
		return orderRepository.findByExternalReference(externalReference);
	}
	
	public TrackOrder findById (Long orderId) {
		Optional<TrackOrder> optionalTrackOrder = orderRepository.findById(orderId);
		TrackOrder order = null;
		if(optionalTrackOrder.isPresent()) {
			order = optionalTrackOrder.get();
		}
		return order;
	}

	public TrackOrder findByIdAndActiveTrueAndState(Long id, OrderState state) {
		return orderRepository.findByIdAndActiveTrueAndState(id, state);
	}
	
	public Boolean verifyOrderStateWithAttempts(Long orderId, OrderState state) {
		Integer attempts = 0;
		Boolean success = false;

		while (!success && attempts < 3) {
			TrackOrder order = findByIdAndActiveTrueAndState(orderId, OrderState.SUCCESS);
			if (order != null) {
				success = true;
			} else {
				attempts++;
				try {
					Thread.sleep(1500); // 1 second delay between attempts
				} catch (InterruptedException ex) {
					Thread.currentThread().interrupt();
				}
			}
		}
		return success;
	}
	

}
