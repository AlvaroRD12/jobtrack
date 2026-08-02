package com.jobtrack.applications;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jobtrack.auth.UserEntity;
import com.jobtrack.auth.UserRepository;
import com.jobtrack.common.AccessDeniedException;

@Service
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final UserRepository userRepository;

    public ApplicationService(ApplicationRepository applicationRepository, UserRepository userRepository) {
        this.applicationRepository = applicationRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<ApplicationEntity> listForCurrentUser() {
        return applicationRepository.findByUserId(currentUserId());
    }

    @Transactional(readOnly = true)
    public ApplicationEntity getForCurrentUser(Long id) {
        Long currentUserId = currentUserId();
        return applicationRepository.findByIdAndUserId(id, currentUserId)
                .orElseThrow(() -> new AccessDeniedException("You do not have access to this application"));
    }

    @Transactional
    public ApplicationEntity create(ApplicationRequest request) {
        ApplicationEntity entity = new ApplicationEntity();
        applyUpdates(entity, request);
        entity.setUserId(currentUserId());
        entity.setArchived(false);
        return applicationRepository.save(entity);
    }

    @Transactional
    public ApplicationEntity update(Long id, ApplicationRequest request) {
        ApplicationEntity entity = getForCurrentUser(id);
        applyUpdates(entity, request);
        return applicationRepository.save(entity);
    }

    @Transactional
    public ApplicationEntity archive(Long id, boolean archived) {
        ApplicationEntity entity = getForCurrentUser(id);
        entity.setArchived(archived);
        return applicationRepository.save(entity);
    }

    @Transactional
    public void delete(Long id) {
        ApplicationEntity entity = getForCurrentUser(id);
        applicationRepository.delete(entity);
    }

    private void applyUpdates(ApplicationEntity entity, ApplicationRequest request) {
        entity.setCompany(request.company());
        entity.setPosition(request.position());
        entity.setSource(request.source());
        entity.setApplicationDate(request.applicationDate());
        entity.setStage(request.stage() != null ? request.stage() : "Applied");
        entity.setNotes(request.notes());
        entity.setFollowUpDate(request.nextFollowUpDate());
    }

    private Long currentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("Authentication required");
        }

        String username = authentication.getName();
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return user.getId();
    }
}
