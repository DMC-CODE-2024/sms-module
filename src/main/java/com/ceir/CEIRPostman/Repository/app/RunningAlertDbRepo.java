package com.ceir.CEIRPostman.Repository.app;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ceir.CEIRPostman.model.app.RunningAlertDb;
import org.springframework.stereotype.Repository;


@Repository
public interface RunningAlertDbRepo extends JpaRepository<RunningAlertDb, Long>{

	public RunningAlertDb save(RunningAlertDb alertDb);
}
