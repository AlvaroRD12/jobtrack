package com.jobtrack.applications;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.jobtrack.common.ApiResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/applications")
public class ApplicationController {

    private final ApplicationRepository repository;

    public ApplicationController(ApplicationRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public ApiResponse<List<ApplicationEntity>> list() {
        return ApiResponse.ok(repository.findAll());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<ApplicationEntity> create(@Valid @RequestBody ApplicationEntity application) {
        return ApiResponse.ok("Application created", repository.save(application));
    }

    @PutMapping("/{id}")
    public ApiResponse<ApplicationEntity> update(@PathVariable Long id, @Valid @RequestBody ApplicationEntity payload) {
        ApplicationEntity existing = repository.findById(id).orElseThrow(() -> new IllegalArgumentException("Application not found"));
        existing.setCompany(payload.getCompany());
        existing.setPosition(payload.getPosition());
        existing.setSource(payload.getSource());
        existing.setApplicationDate(payload.getApplicationDate());
        existing.setStage(payload.getStage());
        existing.setOutcome(payload.getOutcome());
        existing.setNotes(payload.getNotes());
        existing.setFollowUpDate(payload.getFollowUpDate());
        return ApiResponse.ok("Application updated", repository.save(existing));
    }
}
