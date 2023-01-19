package com.ceir.CEIRPostman.RepositoryService;

import org.apache.log4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ceir.CEIRPostman.Repository.UserRepo;
import com.ceir.CEIRPostman.model.User;
@Service
public class UserRepoService {
	
	@Autowired
	UserRepo userRepo;
	
	private final Logger log = Logger.getLogger(getClass());
	
	public User getById(long id) {
		try {
			log.info("going to fetch User data by Id "+id);
			return userRepo.findById(id);
			
		}
		catch(Exception e) {
			log.info("exception occurs when search data for user table by id"+id);
			return new User();
		}
	}

}



