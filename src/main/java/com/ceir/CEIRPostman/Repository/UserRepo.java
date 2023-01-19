package com.ceir.CEIRPostman.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ceir.CEIRPostman.model.User;

public interface UserRepo extends JpaRepository<User, Long>{

	public User findById(long id);
	
}
