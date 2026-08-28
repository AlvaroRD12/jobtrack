package com.jobtrack.stages;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StageDefinitionRepository extends JpaRepository<StageDefinition, Long> {
    List<StageDefinition> findAllByOrderByOrderAsc();
    Optional<StageDefinition> findByName(String name);
}
