package com.ceir.CEIRPostman.Repository.app;

import org.springframework.data.jpa.repository.JpaRepository;
import com.ceir.CEIRPostman.model.app.SystemConfigurationDb;

public interface SystemConfigurationDbRepository extends JpaRepository<SystemConfigurationDb, Long> {

	public SystemConfigurationDb getByTag(String tag);
	public SystemConfigurationDb getById(Long id);
	public SystemConfigurationDb save(SystemConfigurationDb systemConfigurationDb);

}
