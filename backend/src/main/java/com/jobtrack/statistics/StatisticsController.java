package com.jobtrack.statistics;

import com.jobtrack.common.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/statistics")
public class StatisticsController {

    private final StatisticsService statisticsService;

    public StatisticsController(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    @GetMapping("/funnel")
    public ApiResponse<List<StageCountDto>> getFunnel() {
        return ApiResponse.ok(statisticsService.getFunnelCounts());
    }

    @GetMapping("/activity")
    public ApiResponse<List<ActivityDto>> getActivity() {
        return ApiResponse.ok(statisticsService.getActivityOverTime());
    }

    @GetMapping("/conversion")
    public ApiResponse<List<ConversionRateDto>> getConversion() {
        return ApiResponse.ok(statisticsService.getConversionRates());
    }
}