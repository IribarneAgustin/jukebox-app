package com.juke.api.service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.juke.api.dto.TrackInfoDTO;
import com.juke.api.model.Track;
import com.juke.api.model.Transaction;
import com.juke.api.repository.ITransactionRepository;
import com.juke.utils.SpotifyUtils;

@Service
public class TransactionService {

	@Autowired
	private ITransactionRepository transactionRepository;
	
	@Autowired
	private TrackService trackService;

	public void saveNewTransaction(String paymentId, String trackURI, Double amount, String albumCover, String artistName, String trackName) throws Exception {
		try {
			Transaction transaction = new Transaction();
			String spotifyId = SpotifyUtils.extractSpotifyId(trackURI);
			Track track = trackService.findBySpotifyId(spotifyId);
			if (track == null) {
				track = new Track();
				track.setAlbumCover(albumCover);
				track.setArtistName(artistName);
				track.setSpotifyId(spotifyId);
				track.setTrackName(trackName);
				trackService.save(track);
			}
			transaction.setActive(Boolean.TRUE);
			transaction.setPaymentId(paymentId);
			transaction.setTrack(track);
			transaction.setCreationTimestamp(new Timestamp(System.currentTimeMillis()));
			transaction.setAmount(new BigDecimal(amount));
			transactionRepository.save(transaction);
			System.out.println("Transaction saved succesfully "+ new Timestamp(System.currentTimeMillis()).toString());
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	
	public List<Transaction> findLastTenByOrderByCreationTimestampDesc() {
		return transactionRepository.findLastTenByOrderByCreationTimestampDesc();
	}
	
	
	public List<TrackInfoDTO> getTrackQueue() {
		List<Transaction> trxList = findLastTenByOrderByCreationTimestampDesc();
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
	

}
