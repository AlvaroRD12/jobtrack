package com.jobtrack.stages;

import java.util.List;
import java.util.Set;

public final class StageCatalog {
    public static final List<String> STAGES = List.of(
            "Applied",
            "In progress",
            "Interview",
            "Offer",
            "Rejected",
            "Withdrawn"
    );

    public static final Set<String> TERMINAL_STAGES = Set.of("Offer", "Rejected", "Withdrawn");

    private StageCatalog() {
    }

    public static boolean isValid(String stage) {
        return STAGES.contains(stage);
    }
}
