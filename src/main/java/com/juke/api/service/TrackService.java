package com.juke.api.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.juke.api.model.Track;
import com.juke.api.repository.ITrackRepository;

@Service
public class TrackService {

	@Autowired
	private ITrackRepository trackRepository;

	@Autowired
	private SpotifyWebApiService spotifyWebApiService;

	public Track save(Track track) {
		return trackRepository.save(track);
	}

	public Track findBySpotifyURI(String spotifyURI) {
		return trackRepository.findBySpotifyURI(spotifyURI);
	}

	public List<Track> searchTracksByUserInput(String userInput) {
		return trackRepository.findByDescriptionOrArtistNameOrTrackNameContainingIgnoreCase(userInput);
	}

	public ResponseEntity<String> updateTracksTask() {
		ResponseEntity<String> response = null;
		try {
			System.out.println("BEGIN TracksUpdateTask " + new Timestamp(System.currentTimeMillis()));
			List<String> playlistIds = spotifyWebApiService.getPlaylistIdsForCountry("AR");
			System.out.println("Processing " + playlistIds.size() + " playlists");
			for (String playlistId : playlistIds) {
				List<Track> tracks = spotifyWebApiService.getPlaylistTracks(playlistId);
				storeTrackList(tracks);
			}
            response = new ResponseEntity<>(HttpStatus.OK);
			System.out.println("END TracksUpdateTask " + new Timestamp(System.currentTimeMillis()));
		} catch (Exception e) {
            response = new ResponseEntity<>("Error occurred: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
			e.printStackTrace();
		}
		return response;
	}

	private void storeTrackList(List<Track> tracks) {
		for (Track track : tracks) {
			if (track.getSpotifyURI() != null && !track.getSpotifyURI().isEmpty()
					&& trackRepository.findBySpotifyURI(track.getSpotifyURI()) == null) {
				trackRepository.save(track);
			}
		}

	}

	public ResponseEntity<String> getTracksByUserInput(String userInput) {
		ResponseEntity<String> response = null;
		try {
			String responseBody = null;
			List<Track> tracksFromDB = searchTracksByUserInput(userInput);
			if (tracksFromDB == null || (tracksFromDB != null && (tracksFromDB.isEmpty() || tracksFromDB.size() <= 2))) {
				List<Track> tracksFromApi = getTracksFromApiSearch(userInput);
				List<Track> mergedTracks = new ArrayList<>(tracksFromApi);
				mergedTracks.addAll(tracksFromDB);
				responseBody = convertTracksToJson(mergedTracks);
			} else {
				responseBody = convertTracksToJson(tracksFromDB);
			}

			response = new ResponseEntity<>(responseBody, HttpStatus.OK);

		} catch (Exception e) {
			e.printStackTrace();
			response = new ResponseEntity<>("Error occurred: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return response;
	}

	private List<Track> getTracksFromApiSearch(String searchQuery) throws Exception {
		List<Track> tracksFromApi = new ArrayList<>();
		ResponseEntity<String> apiResult = spotifyWebApiService.getTrackInformationByName(searchQuery);
		if (HttpStatus.OK.equals(apiResult.getStatusCode())) {
			tracksFromApi = mapTracksFromResponse(apiResult.getBody());

		}
		return tracksFromApi;
	}

	private List<Track> mapTracksFromResponse(String apiResponse) throws Exception {
		List<Track> tracks = new ArrayList<>();
		ObjectMapper objectMapper = new ObjectMapper();

		try {
			JsonNode rootNode = objectMapper.readTree(apiResponse);
			JsonNode tracksNode = rootNode.path("tracks").path("items");

			for (JsonNode trackNode : tracksNode) {
				Track track = createTrackFromNode(trackNode);
				tracks.add(track);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}

		return tracks;
	}

	private Track createTrackFromNode(JsonNode trackNode) {

		String albumCover = trackNode.path("album").path("images").get(0).path("url").asText();
		String artistName = trackNode.path("artists").get(0).path("name").asText();
		String trackName = trackNode.path("name").asText();
		String spotifyURI = trackNode.path("uri").asText();
		String description = trackNode.path("artists").get(0).path("name").asText() + " - " + trackNode.get("name").asText();

		return new Track(albumCover, artistName, trackName, spotifyURI, description);
	}
	
    private String convertTracksToJson(List<Track> tracks) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        String response = "";
        try {
            response = objectMapper.writeValueAsString(tracks);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw e;
        }
        return response;
    }


}
