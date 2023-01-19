package com.ceir.CEIRPostman.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ceir.CEIRPostman.model.UserTemporarydetails;


public interface UserTemporarydetailsRepo extends JpaRepository<UserTemporarydetails, Long>{
	public UserTemporarydetails save(UserTemporarydetails details );
	public UserTemporarydetails findByUserDetails_id(long id);                 
}
