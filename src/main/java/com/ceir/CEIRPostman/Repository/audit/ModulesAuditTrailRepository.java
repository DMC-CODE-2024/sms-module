package com.ceir.CEIRPostman.Repository.audit;

import com.ceir.CEIRPostman.model.audit.ModulesAuditTrail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ModulesAuditTrailRepository extends JpaRepository<ModulesAuditTrail,Long>, JpaSpecificationExecutor<ModulesAuditTrail> {
}
