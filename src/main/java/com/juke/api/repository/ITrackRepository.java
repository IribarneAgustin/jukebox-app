package com.juke.api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.juke.api.model.Track;

@Repository
public interface ITrackRepository extends JpaRepository<Track,Long>{
	
	public Track findBySpotifyURI(String spotifyURI);
	
	public List<Track> findByArtistNameContainingIgnoreCaseOrTrackNameContainingIgnoreCase(String artistName, String trackName);
	
	//FIX ME: DUPLICATED VALUES
	@Query("SELECT t FROM Track t " +
		       "WHERE LOWER(t.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
		       "LOWER(t.artistName) LIKE LOWER(:searchTerm) OR " +
		       "LOWER(t.trackName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
		       "ORDER BY CASE " +
		       "  WHEN LOWER(t.artistName) = LOWER(:searchTerm) THEN 0 " +
		       "  WHEN LOWER(t.trackName) = LOWER(:searchTerm) THEN 0 " +
		       "  ELSE 1 " +
		       "END, t.artistName " + 
		       "LIMIT 5")
		public List<Track> findByDescriptionOrArtistNameOrTrackNameContainingIgnoreCase(@Param("searchTerm") String searchTerm);






	

}
