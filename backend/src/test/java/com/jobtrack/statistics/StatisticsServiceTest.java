package com.jobtrack.statistics;

import com.jobtrack.applications.ApplicationEntity;
import com.jobtrack.applications.ApplicationService;
import com.jobtrack.stages.StageCatalog;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class StatisticsServiceTest {

    @Autowired
    private StatisticsService statisticsService;

    @MockBean
    private ApplicationService applicationService;

    @BeforeEach
    void setUp() {
    }

    @Test
    void testGetFunnelCounts() {
        // Arrange
        ApplicationEntity app1 = new ApplicationEntity();
        app1.setStage("Applied");
        ApplicationEntity app2 = new ApplicationEntity();
        app2.setStage("Applied");
        ApplicationEntity app3 = new ApplicationEntity();
        app3.setStage("In progress");
        ApplicationEntity app4 = new ApplicationEntity();
        app4.setStage("Offer");

        List<ApplicationEntity> applications = Arrays.asList(app1, app2, app3, app4);
        when(applicationService.listForCurrentUser()).thenReturn(applications);

        // Act
        List<StageCountDto> result = statisticsService.getFunnelCounts();

        // Assert
        assertEquals(StageCatalog.STAGES.size(), result.size(), "Should have an entry for each stage");

        // Check specific counts
        assertEquals(2, result.stream().filter(dto -> dto.getStage().equals("Applied")).findFirst().get().getCount());
        assertEquals(1, result.stream().filter(dto -> dto.getStage().equals("In progress")).findFirst().get().getCount());
        assertEquals(0, result.stream().filter(dto -> dto.getStage().equals("Interview")).findFirst().get().getCount());
        assertEquals(1, result.stream().filter(dto -> dto.getStage().equals("Offer")).findFirst().get().getCount());
        assertEquals(0, result.stream().filter(dto -> dto.getStage().equals("Rejected")).findFirst().get().getCount());
        assertEquals(0, result.stream().filter(dto -> dto.getStage().equals("Withdrawn")).findFirst().get().getCount());
    }

    @Test
    void testGetActivityOverTime() {
        // Arrange
        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);

        ApplicationEntity app1 = new ApplicationEntity();
        app1.setApplicationDate(today);
        ApplicationEntity app2 = new ApplicationEntity();
        app2.setApplicationDate(today);
        ApplicationEntity app3 = new ApplicationEntity();
        app3.setApplicationDate(yesterday);

        List<ApplicationEntity> applications = Arrays.asList(app1, app2, app3);
        when(applicationService.listForCurrentUser()).thenReturn(applications);

        // Act
        List<ActivityDto> result = statisticsService.getActivityOverTime();

        // Assert
        assertEquals(2, result.size(), "Should have two days");

        // Check that the list is sorted by date ascending
        assertEquals(yesterday, result.get(0).getDate());
        assertEquals(today, result.get(1).getDate());

        // Check counts
        assertEquals(1, result.get(0).getCount());
        assertEquals(2, result.get(1).getCount());
    }

    @Test
    void testGetConversionRates() {
        // Arrange
        ApplicationEntity app1 = new ApplicationEntity();
        app1.setStage("Applied");
        ApplicationEntity app2 = new ApplicationEntity();
        app2.setStage("Applied");
        ApplicationEntity app3 = new ApplicationEntity();
        app3.setStage("In progress");
        ApplicationEntity app4 = new ApplicationEntity();
        app4.setStage("In progress");
        ApplicationEntity app5 = new ApplicationEntity();
        app5.setStage("Interview");
        ApplicationEntity app6 = new ApplicationEntity();
        app6.setStage("Offer");

        List<ApplicationEntity> applications = Arrays.asList(app1, app2, app3, app4, app5, app6);
        when(applicationService.listForCurrentUser()).thenReturn(applications);

        // Act
        List<ConversionRateDto> result = statisticsService.getConversionRates();

        // Assert
        assertEquals(StageCatalog.STAGES.size() - 1, result.size(), "Should have one less conversion rate than stages");

        // Applied -> In progress: 2/2 = 1.0
        assertEquals(1.0, result.stream().filter(dto -> dto.getFromStage().equals("Applied") && dto.getToStage().equals("In progress")).findFirst().get().getRate(), 0.001);
        // In progress -> Interview: 1/2 = 0.5
        assertEquals(0.5, result.stream().filter(dto -> dto.getFromStage().equals("In progress") && dto.getToStage().equals("Interview")).findFirst().get().getRate(), 0.001);
        // Interview -> Offer: 1/1 = 1.0
        assertEquals(1.0, result.stream().filter(dto -> dto.getFromStage().equals("Interview") && dto.getToStage().equals("Offer")).findFirst().get().getRate(), 0.001);
        // Offer -> Rejected: 0/1 = 0.0
        assertEquals(0.0, result.stream().filter(dto -> dto.getFromStage().equals("Offer") && dto.getToStage().equals("Rejected")).findFirst().get().getRate(), 0.001);
        // Rejected -> Withdrawn: 0/0 = 0.0 (by our convention)
        assertEquals(0.0, result.stream().filter(dto -> dto.getFromStage().equals("Rejected") && dto.getToStage().equals("Withdrawn")).findFirst().get().getRate(), 0.001);
    }

    @Test
    void testGetConversionRatesWithZeroInFirstStage() {
        // Arrange
        ApplicationEntity app1 = new ApplicationEntity();
        app1.setStage("In progress");
        ApplicationEntity app2 = new ApplicationEntity();
        app2.setStage("Interview");

        List<ApplicationEntity> applications = Arrays.asList(app1, app2);
        when(applicationService.listForCurrentUser()).thenReturn(applications);

        // Act
        List<ConversionRateDto> result = statisticsService.getConversionRates();

        // Assert
        // Applied -> In progress: 0/0 = 0.0 (since no Applied)
        assertEquals(0.0, result.stream().filter(dto -> dto.getFromStage().equals("Applied") && dto.getToStage().equals("In progress")).findFirst().get().getRate(), 0.001);
        // In progress -> Interview: 1/1 = 1.0
        assertEquals(1.0, result.stream().filter(dto -> dto.getFromStage().equals("In progress") && dto.getToStage().equals("Interview")).findFirst().get().getRate(), 0.001);
    }
}