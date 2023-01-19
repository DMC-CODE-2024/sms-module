package com.ceir.CEIRPostman.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ceir.CEIRPostman.model.AuthorityNotification;

public interface AuthorityNotiRepo extends JpaRepository<AuthorityNotification, Long>{

	public AuthorityNotification save(AuthorityNotification notification);
}
