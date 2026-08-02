package com.jobtrack.applications;

import java.time.LocalDate;

public record ApplicationRequest(
        String company,
        String position,
        String source,
        LocalDate applicationDate,
        String stage,
        String notes,
        LocalDate nextFollowUpDate
) {
}
