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
import com.juke.api.model.Transaction;
import com.juke.api.repository.ITransactionRepository;

@Service
public class TransactionService {

	@Autowired
	private ITransactionRepository transactionRepository;

	@Autowired
	private TrackService trackService;

	public void saveNewTransactionAndTrackIfNotExists(String paymentId, String trackURI, Double amount, String albumCover,
			String artistName, String trackName) throws Exception {
		try {
			Transaction transaction = new Transaction();
			//String spotifyId = SpotifyUtils.extractSpotifyId(trackURI);
			Track track = trackService.findBySpotifyURI(trackURI);
			if (track == null) {
				track = new Track();
				track.setAlbumCover(albumCover);
				track.setArtistName(artistName);
				track.setSpotifyURI(trackURI);
				track.setTrackName(trackName);
				trackService.save(track);
			}
			transaction.setActive(Boolean.TRUE);
			transaction.setPaymentId(paymentId);
			transaction.setTrack(track);
			transaction.setCreationTimestamp(new Timestamp(System.currentTimeMillis()));
			transaction.setAmount(new BigDecimal(amount));
			transactionRepository.save(transaction);
			System.out.println("Transaction saved succesfully " + new Timestamp(System.currentTimeMillis()).toString());
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	public List<Transaction> findFirst10ByOrderByCreationTimestampDesc() {
		return transactionRepository.findFirst10ByOrderByCreationTimestampDesc();
	}

	public List<TrackInfoDTO> getTrackQueue() {
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

		return trackInfoList;

	}

	public List<Transaction> findAllByActiveTrueOrderByCreationTimestampDesc() {
		return transactionRepository.findAllByActiveTrueOrderByCreationTimestampDesc();
	}

	public ResponseEntity<Map<String, Object>> getTotalAmount() {
		ResponseEntity<Map<String, Object>> response = null;

		try {
			BigDecimal total = transactionRepository.getTotalAmount();

			Map<String, Object> responseBody = new HashMap<>();
			responseBody.put("totalAmount", total);

			response = ResponseEntity.ok(responseBody);

		} catch (Exception e) {
			e.printStackTrace();
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
            e.printStackTrace(); // Handle the exception appropriately
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
