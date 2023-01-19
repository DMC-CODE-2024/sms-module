package com.ceir.CEIRPostman.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.ceir.CEIRPostman.model.SystemConfigurationDb;

public interface SystemConfigurationDbRepository extends JpaRepository<SystemConfigurationDb, Long> {

	public SystemConfigurationDb getByTag(String tag);
	public SystemConfigurationDb getById(Long id);

}
