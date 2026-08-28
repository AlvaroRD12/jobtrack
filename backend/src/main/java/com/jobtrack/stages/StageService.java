package com.jobtrack.stages;

import org.springframework.stereotype.Service;

@Service
public class StageService {

    private final StageDefinitionRepository stageDefinitionRepository;

    public StageService(StageDefinitionRepository stageDefinitionRepository) {
        this.stageDefinitionRepository = stageDefinitionRepository;
    }

    public String normalizeStage(String stage) {
        if (stage == null || stage.isBlank()) {
            throw new IllegalArgumentException("Unknown stage: null");
        }

        String normalized = stage.trim();
        if (StageCatalog.isValid(normalized)) {
            return normalized;
        }

        boolean knownDefinition = stageDefinitionRepository.findByName(normalized)
                .isPresent();
        if (knownDefinition) {
            return normalized;
        }

        throw new IllegalArgumentException("Unknown stage: " + stage);
    }

    public boolean isTerminal(String stage) {
        return StageCatalog.TERMINAL_STAGES.contains(stage);
    }
}
