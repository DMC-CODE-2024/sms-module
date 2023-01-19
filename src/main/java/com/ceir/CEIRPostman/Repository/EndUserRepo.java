package com.ceir.CEIRPostman.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ceir.CEIRPostman.model.EndUserDB;

public interface EndUserRepo extends JpaRepository<EndUserDB, Long>{

	public EndUserDB findById(long id);
	
}
