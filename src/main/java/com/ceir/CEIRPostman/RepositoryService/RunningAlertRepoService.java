package com.ceir.CEIRPostman.RepositoryService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ceir.CEIRPostman.Repository.app.RunningAlertDbRepo;
import com.ceir.CEIRPostman.model.app.RunningAlertDb;



@Service
public class RunningAlertRepoService {

	
	@Autowired
		RunningAlertDbRepo alertRepo;
	
	public RunningAlertDb saveAlertDb(RunningAlertDb runningAlertDb) {
	
		try {
			return alertRepo.save(runningAlertDb);
		}
		catch(Exception e) {
			return null;
		}
	}
	
}
