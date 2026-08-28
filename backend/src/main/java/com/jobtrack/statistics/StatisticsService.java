package com.jobtrack.statistics;

import com.jobtrack.applications.ApplicationService;
import com.jobtrack.applications.ApplicationEntity;
import com.jobtrack.stages.StageCatalog;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class StatisticsService {

    private final ApplicationService applicationService;

    public StatisticsService(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    /**
     * Get funnel counts: number of applications in each stage.
     * @return list of StageCountDto for each stage in the canonical order
     */
    public List<StageCountDto> getFunnelCounts() {
        // Get all applications for the current user
        List<com.jobtrack.applications.ApplicationEntity> applications = applicationService.listForCurrentUser();

        // Count by stage
        Map<String, Long> counts = new HashMap<>();
        for (ApplicationEntity app : applications) {
            String stage = app.getStage();
            counts.put(stage, counts.getOrDefault(stage, 0L) + 1);
        }

        // Build result in the order of StageCatalog.STAGES, ensuring zero counts are included
        List<StageCountDto> result = new ArrayList<>();
        for (String stage : StageCatalog.STAGES) {
            long count = counts.getOrDefault(stage, 0L);
            result.add(new StageCountDto(stage, count));
        }
        return result;
    }

    /**
     * Get application activity over time: number of applications submitted each day.
     * @return list of ActivityDto sorted by date ascending
     */
    public List<ActivityDto> getActivityOverTime() {
        List<com.jobtrack.applications.ApplicationEntity> applications = applicationService.listForCurrentUser();

        // Group by applicationDate
        Map<LocalDate, Long> counts = new HashMap<>();
        for (ApplicationEntity app : applications) {
            LocalDate date = app.getApplicationDate();
            counts.put(date, counts.getOrDefault(date, 0L) + 1);
        }

        // Sort by date and convert to DTOs
        List<ActivityDto> result = counts.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> new ActivityDto(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
        return result;
    }

    /**
     * Get conversion rates between consecutive stages.
     * Conversion rate from stage A to stage B = (count in B) / (count in A)
     * @return list of ConversionRateDto for each consecutive pair in StageCatalog.STAGES
     */
    public List<ConversionRateDto> getConversionRates() {
        List<StageCountDto> funnel = getFunnelCounts();
        // Create a map for quick lookup
        Map<String, Long> countMap = funnel.stream()
                .collect(Collectors.toMap(StageCountDto::getStage, StageCountDto::getCount));

        List<ConversionRateDto> result = new ArrayList<>();
        List<String> stages = StageCatalog.STAGES;

        // Calculate conversion rate for each consecutive pair
        for (int i = 0; i < stages.size() - 1; i++) {
            String fromStage = stages.get(i);
            String toStage = stages.get(i + 1);
            Long fromCount = countMap.get(fromStage);
            Long toCount = countMap.get(toStage);

            double rate = (fromCount == 0) ? 0.0 : (double) toCount / fromCount;
            result.add(new ConversionRateDto(fromStage, toStage, rate));
        }
        return result;
    }
}