package com.juke.api.service;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.juke.api.dto.PaymentDTO;
import com.juke.api.dto.TrackInfoDTO;
import com.juke.api.model.AppConfiguration;
import com.juke.api.model.TrackPriceConfiguration;

@Service
public class TrackQueueService {
	
	
	private PaymentContext paymentContext = new PaymentContext();
		
	@Autowired
	private AdminConfigurationService adminConfiguration;
	
	@Value("${MERCADO_PAGO_TOKEN}")
	public String MERCADO_PAGO_TOKEN;
	
	@Value("${MERCADO_PAGO_SUCCESS_URL}")
	public String MERCADO_PAGO_SUCCESS_URL;
	
	@Value("${CLIENT_HOME_URL}")
	private String CLIENT_HOME_URL;
	
	@Autowired
	private SpotifyPlaybackSDK spotifyPlaybackSKDService;

	@Autowired
	private SpotifyAuthService spotifyAuthService;
	
	public ResponseEntity<String> generatePaymentId(TrackInfoDTO trackInfoDTO, String paymentGateway) {
		
		ResponseEntity<String> response = null;
		String paymentId = null;
		try {

			AppConfiguration appConfig = adminConfiguration.findAppConfigurationByActiveTrue();

			if (appConfig != null && appConfig.getIsAvailable()) { //handle it with interceptors?
				TrackPriceConfiguration priceConfig = adminConfiguration.findTrackPriceConfigurationByActiveTrue();
				paymentContext.setPaymentGateway(paymentGateway);
				paymentId = paymentContext.generatePaymentId(createPaymentDTO(priceConfig.getTrackPrice(), trackInfoDTO));
				if (paymentId != null) {
				     response = new ResponseEntity<>(paymentId, HttpStatus.CREATED);
				} else {
					response = new ResponseEntity<String>(HttpStatus.BAD_GATEWAY);
				}

			}
		} catch (Exception e) {
			response = new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR);
			e.printStackTrace();
		}

		return response;
		
	}
	
	public void enqueueTrack(String trackURI) throws Exception {
		try {
			AppConfiguration adminConfig = adminConfiguration.findAppConfigurationByActiveTrue();

			if (adminConfig != null && adminConfig.getSpotifyPlaylistId() != null) {
				spotifyPlaybackSKDService.addTrackToPlaylist(trackURI, spotifyAuthService.getToken(), adminConfig.getSpotifyPlaylistId());
			} else {
				throw new Exception("Cannot get spotify playlist Id");
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	private PaymentDTO createPaymentDTO(BigDecimal trackPrice, TrackInfoDTO trackInfoDTO) {
		PaymentDTO paymentDTO = new PaymentDTO();
		paymentDTO.setPrice(trackPrice.doubleValue());
		paymentDTO.setQuantity(1);
		paymentDTO.setDescription(trackInfoDTO.getArtistName() + " - " + trackInfoDTO.getTrackName());
		paymentDTO.setTrackInfoDTO(trackInfoDTO);
		//paymentDTO.setCurrency(null);TODO get it from config
		
		 //FIXME when be implemented other payments gateways
		paymentDTO.setToken(MERCADO_PAGO_TOKEN); 
		paymentDTO.setSuccessUrl(MERCADO_PAGO_SUCCESS_URL);
		paymentDTO.setFailedUrl(CLIENT_HOME_URL);
		return paymentDTO;
	}

}
