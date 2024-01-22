package com.juke.api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.juke.api.model.Track;

@Repository
public interface ITrackRepository extends JpaRepository<Track,Long>{
	
	public Track findBySpotifyId(String spotifyId);
	
	List<Track> findByArtistNameContainingIgnoreCaseOrTrackNameContainingIgnoreCase(String artistName, String trackName);
	
	@Query(value = "SELECT * FROM Track " +
            "WHERE LOWER(artist_name) LIKE LOWER(CONCAT('%', :term, '%')) OR " +
            "LOWER(track_name) LIKE LOWER(CONCAT('%', :term, '%')) OR " +
            "LOWER(CONCAT(artist_name, ' ', track_name)) LIKE LOWER(CONCAT('%', :term, '%')) OR " +
            "LOWER(CONCAT(track_name, ' ', artist_name)) LIKE LOWER(CONCAT('%', :term, '%'))",
            nativeQuery = true)
    List<Track> searchTracks(@Param("term") String term);

}
