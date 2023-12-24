package com.juke.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.juke.api.model.Administrator;

@Repository
public interface IAdministratorRepository extends JpaRepository<Administrator, Long>{
	
	public Administrator findByUsername(String username);

}
