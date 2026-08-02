package com.jobtrack.applications;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ApplicationRepository extends JpaRepository<ApplicationEntity, Long> {
    List<ApplicationEntity> findByUserId(Long userId);

    Optional<ApplicationEntity> findByIdAndUserId(Long id, Long userId);
}
