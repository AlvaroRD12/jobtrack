package com.jobtrack.applications;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
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

    private final ApplicationService applicationService;

    public ApplicationController(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @GetMapping
    public ApiResponse<List<ApplicationEntity>> list() {
        return ApiResponse.ok(applicationService.listForCurrentUser());
    }

    @GetMapping("/{id}")
    public ApiResponse<ApplicationEntity> get(@PathVariable Long id) {
        return ApiResponse.ok(applicationService.getForCurrentUser(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<ApplicationEntity> create(@Valid @RequestBody ApplicationRequest request) {
        return ApiResponse.ok("Application created", applicationService.create(request));
    }

    @PutMapping("/{id}")
    public ApiResponse<ApplicationEntity> update(@PathVariable Long id, @Valid @RequestBody ApplicationRequest request) {
        return ApiResponse.ok("Application updated", applicationService.update(id, request));
    }

    @PatchMapping("/{id}/archive")
    public ApiResponse<ApplicationEntity> archive(@PathVariable Long id, @RequestBody ArchiveRequest request) {
        return ApiResponse.ok("Application archived", applicationService.archive(id, request.archived()));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        applicationService.delete(id);
        return ApiResponse.ok("Application deleted", null);
    }
}
