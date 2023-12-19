package com.juke.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.juke.api.model.Track;

@Repository
public interface ITrackRepository extends JpaRepository<Track,Long>{
	
	public Track findBySpotifyId(String spotifyId);

}
