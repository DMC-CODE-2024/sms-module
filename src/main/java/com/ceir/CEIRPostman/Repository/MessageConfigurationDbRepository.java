package com.ceir.CEIRPostman.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.ceir.CEIRPostman.model.MessageConfigurationDb;


public interface MessageConfigurationDbRepository extends JpaRepository<MessageConfigurationDb, Long> {

	public MessageConfigurationDb getByTag(String tagValue);

	public MessageConfigurationDb getById(Long id);

}

