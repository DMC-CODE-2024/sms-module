package com.ceir.CEIRPostman.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ceir.CEIRPostman.model.RunningAlertDb;


public interface RunningAlertDbRepo extends JpaRepository<RunningAlertDb, Long>{

	public RunningAlertDb save(RunningAlertDb alertDb);
}
