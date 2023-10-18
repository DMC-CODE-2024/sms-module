package com.ceir.CEIRPostman.RepositoryService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ceir.CEIRPostman.Repository.app.SystemConfigurationDbRepository;
import com.ceir.CEIRPostman.model.app.SystemConfigurationDb;

@Service
public class SystemConfigurationDbRepoImpl {

	@Autowired
	SystemConfigurationDbRepository SystemConfigurationDbRepo;
	
	public SystemConfigurationDb getDataByTag(String tag) {
	try {
		SystemConfigurationDb systemConfigDb=new SystemConfigurationDb();
		systemConfigDb=SystemConfigurationDbRepo.getByTag(tag);
		return systemConfigDb;
	}
	catch(Exception e) {
		return null;
	}
	}

	public SystemConfigurationDb saveConfigDb(SystemConfigurationDb systemConfigurationDb) {

		try {
			return SystemConfigurationDbRepo.save(systemConfigurationDb);
		}
		catch(Exception e) {
			return null;
		}
	}
}