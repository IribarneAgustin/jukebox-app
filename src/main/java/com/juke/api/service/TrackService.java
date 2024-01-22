package com.juke.api.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

	public Track findBySpotifyId(String spotifyId) {
		return trackRepository.findBySpotifyId(spotifyId);
	}

	public List<Track> searchTracksByUserInput(String userInput) {
		// Replace spaces with underscores in the user input
		String formattedInput = userInput.replace(" ", "_");

		// Use both query methods to cover cases with spaces or underscores
		List<Track> tracksWithSpaces = trackRepository
				.searchTracks(userInput);
		List<Track> tracksWithUnderscores = trackRepository
				.searchTracks(formattedInput);

		// Combine the results
		List<Track> combinedResults = new ArrayList<>(new HashSet<>(
				Stream.concat(tracksWithSpaces.stream(), tracksWithUnderscores.stream()).collect(Collectors.toList())));

		return combinedResults;
	}

	public void storeTracksFromPlaylists() {
		List<String> playlistIds = spotifyWebApiService.getPlaylistIdsForCountry("AR");

		for (String playlistId : playlistIds) {
			List<Track> tracks = spotifyWebApiService.getPlaylistTracks(playlistId);
			storeTrackList(tracks);
		}
	}

	private void storeTrackList(List<Track> tracks) {
		System.out.println(tracks.size());
		for (Track track : tracks) {
			if (track.getSpotifyId() != null && !track.getSpotifyId().isEmpty()
					&& trackRepository.findBySpotifyId(track.getSpotifyId()) == null) {
				trackRepository.save(track);
			}
		}

	}

	public ResponseEntity<String> getTracksByTrackOrArtistNameFromDBAndApiResult(String userInput) {
		ResponseEntity<String> response = null;
        try {
            List<Track> tracksFromApi = getTracksFromApiSearch(userInput);
            tracksFromApi = new ArrayList<>(); 
            List<Track> tracksFromDB = searchTracksByUserInput(userInput);
            List<Track> mergedTracks = new ArrayList<>(tracksFromApi); //merge if db result is empty
            mergedTracks.addAll(tracksFromDB);

            String responseBody = convertTracksToJson(mergedTracks);

            // Return a ResponseEntity with the merged tracks in the response body
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
		String spotifyId = trackNode.path("id").asText();

		return new Track(albumCover, artistName, trackName, spotifyId);
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
