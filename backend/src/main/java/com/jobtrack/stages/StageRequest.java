package com.jobtrack.stages;

import jakarta.validation.constraints.NotBlank;

public record StageRequest(@NotBlank String stage) {
}
