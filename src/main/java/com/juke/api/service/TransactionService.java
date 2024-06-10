package com.juke.api.service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.juke.api.dto.TrackInfoDTO;
import com.juke.api.model.Track;
import com.juke.api.model.TrackOrder;
import com.juke.api.model.Transaction;
import com.juke.api.repository.ITransactionRepository;
import com.juke.api.utils.SystemLogger;

@Service
public class TransactionService {

	@Autowired
	private ITransactionRepository transactionRepository;

	@Autowired
	private TrackService trackService;
	
	public Transaction saveNewTransaction(TrackOrder order) throws Exception {
		try {
			Track track = trackService.findBySpotifyURI(order.getTrack().getSpotifyURI());
			if (track == null) {
				track = order.getTrack();
				trackService.save(track);
			}
			Transaction transaction = new Transaction();
			transaction.setActive(Boolean.TRUE);
			transaction.setTrack(order.getTrack());
			transaction.setCreationTimestamp(new Timestamp(System.currentTimeMillis()));
			transaction.setAmount(order.getAmount());
			transaction.setTrackOrder(order);
			return transactionRepository.save(transaction);
		} catch (Exception e) {
			SystemLogger.error(e.getMessage(), e);
			throw e;
		}
	}

	public List<Transaction> findFirst10ByOrderByCreationTimestampDesc() {
		return transactionRepository.findFirst10ByOrderByCreationTimestampDesc();
	}

	public ResponseEntity<List<TrackInfoDTO>> getTrackQueue() {
	    try {
	        List<Transaction> trxList = findFirst10ByOrderByCreationTimestampDesc();
	        ArrayList<TrackInfoDTO> trackInfoList = new ArrayList<>();

	        if (trxList != null && !trxList.isEmpty()) {
	            for (Transaction trx : trxList) {
	                TrackInfoDTO trackInfoDTO = new TrackInfoDTO();
	                trackInfoDTO.setAddedAt(trx.getCreationTimestamp());
	                trackInfoDTO.setAlbumCover(trx.getTrack().getAlbumCover());
	                trackInfoDTO.setArtistName(trx.getTrack().getArtistName());
	                trackInfoDTO.setTrackName(trx.getTrack().getTrackName());

	                trackInfoList.add(trackInfoDTO);
	            }
	        }

	        return new ResponseEntity<>(trackInfoList, HttpStatus.OK);
	    } catch (Exception e) {
	        SystemLogger.error(e.getMessage(), e);
	        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
	    }
	}

	public List<Transaction> findAllByActiveTrueOrderByCreationTimestampDesc() {
		return transactionRepository.findAllByActiveTrueOrderByCreationTimestampDesc();
	}
	
	public ResponseEntity<List<Transaction>> findActiveTransactions() {
	    ResponseEntity<List<Transaction>> responseEntity = null;
	    try {
	        List<Transaction> transactionList = findAllByActiveTrueOrderByCreationTimestampDesc();
	        HttpStatus status = transactionList != null ? HttpStatus.OK : HttpStatus.NOT_FOUND;
	        responseEntity = new ResponseEntity<>(transactionList, status);
	    } catch (Exception e) {
			SystemLogger.error(e.getMessage(), e);
	        responseEntity = new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
	    }

	    return responseEntity;
	}

	public ResponseEntity<Map<String, Object>> getTotalAmount() {
		ResponseEntity<Map<String, Object>> response = null;

		try {
			BigDecimal total = transactionRepository.getTotalAmount();

			Map<String, Object> responseBody = new HashMap<>();
			responseBody.put("totalAmount", total);

			response = ResponseEntity.ok(responseBody);

		} catch (Exception e) {
			SystemLogger.error(e.getMessage(), e);
			response = new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);

		}
		return response;
	}
	
	public ResponseEntity<Map<String, Object>> getTotalAmountOfLastYearSeparatedByMonth(Integer year) {
        try {
        	List<Map<Integer, BigDecimal>> totalAmountByMonth = transactionRepository.getTotalAmountOfLastYearSeparatedByMonth(year);

            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("totalAmountByMonth", totalAmountByMonth);

            return ResponseEntity.ok(responseBody);
        } catch (Exception e) {
			SystemLogger.error(e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
