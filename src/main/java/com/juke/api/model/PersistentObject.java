package com.juke.api.model;


import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class PersistentObject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private Boolean active;
	
	public PersistentObject() {
		setActive(Boolean.TRUE);
	}

	public PersistentObject(Long id, Boolean active) {
		this.id = id;
		this.active = active;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

}