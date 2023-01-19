package com.ceir.CEIRPostman.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ceir.CEIRPostman.Repository.AuthorityNotiRepo;
import com.ceir.CEIRPostman.model.AuthorityNotification;

@Service
public class AuthorityRepoService {

	@Autowired
	AuthorityNotiRepo authorityRepo;
	
	
	public AuthorityNotification saveNotification(AuthorityNotification noti) {
		
		try 
		{
			return authorityRepo.save(noti);
		}
		catch(Exception e)
		{
			return null;
		}
	}
}
