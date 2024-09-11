package com.ceir.CEIRPostman.Repository.app;

import com.ceir.CEIRPostman.model.app.Operator;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OperatorRepository extends JpaRepository<Operator,Long>, JpaSpecificationExecutor<Operator> {
    @EntityGraph(attributePaths = "properties")
    Optional<Operator> findByOperatorName(String operatorName);

    @EntityGraph(attributePaths = "properties")
    List<Operator> findAll();
}
