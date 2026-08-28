<template>
  <section class="statistics-panel">
    <h2>Dashboard Statistics</h2>

    <div v-if="state.loading" class="loading">Loading statistics...</div>
    <div v-else-if="state.error" class="error">{{ state.error }}</div>
    <div v-else class="stats-content">
      <!-- Funnel Counts -->
      <section class="stat-section">
        <h3>Applications by Stage</h3>
        <ul class="funnel-list">
          <li v-for="stat in state.funnelCounts" :key="stat.stage" class="funnel-item">
            <span class="stage-name">{{ stat.stage }}:</span>
            <span class="count">{{ stat.count }}</span>
          </li>
        </ul>
      </section>

      <!-- Activity Over Time -->
      <section class="stat-section">
        <h3>Application Activity Over Time</h3>
        <ul v-if="state.activityOverTime.length" class="activity-list">
          <li v-for="day in state.activityOverTime" :key="day.date" class="activity-item">
            <span class="date">{{ day.date }}</span>
            <span class="count">{{ day.count }} application{{ day.count !== 1 ? 's' : '' }}</span>
          </li>
        </ul>
        <p v-else class="no-data">No application activity recorded yet.</p>
      </section>

      <!-- Conversion Rates -->
      <section class="stat-section">
        <h3>Conversion Rates Between Stages</h3>
        <ul v-if="state.conversionRates.length" class="conversion-list">
          <li v-for="conv in state.conversionRates" :key="conv.fromStage + '-' + conv.toStage" class="conversion-item">
            <span class="stage-pair">
              {{ conv.fromStage }} &rarr; {{ conv.toStage }}
            </span>
            <span class="rate">{{ (conv.rate * 100).toFixed(1) }}%</span>
          </li>
        </ul>
        <p v-else class="no-data">No conversion data available.</p>
      </section>
    </div>
  </section>
</template>

<script setup lang="ts">
import { onMounted, reactive } from 'vue';
import { getFunnelCounts, getActivityOverTime, getConversionRates } from '../../api/statistics';

const state = reactive({
  loading: true,
  error: null as string | null,
  funnelCounts: [] as { stage: string; count: number }[],
  activityOverTime: [] as { date: string; count: number }[],
  conversionRates: [] as { fromStage: string; toStage: string; rate: number }[]
});

async function loadStatistics() {
  try {
    state.loading = true;
    state.error = null;

    // Fetch all three statistics in parallel
    const [funnelResp, activityResp, conversionResp] = await Promise.all([
      getFunnelCounts(),
      getActivityOverTime(),
      getConversionRates()
    ]);

    state.funnelCounts = funnelResp;
    state.activityOverTime = activityResp;
    state.conversionRates = conversionResp;
  } catch (err) {
    state.error = 'Failed to load statistics: ' + (err instanceof Error ? err.message : String(err));
  } finally {
    state.loading = false;
  }
}

onMounted(() => {
  loadStatistics();
});
</script>

<style scoped>
.statistics-panel {
  max-width: 800px;
  margin: 0 auto;
  padding: 1rem;
}

.stat-section {
  margin-bottom: 2rem;
  padding: 1.5rem;
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  background-color: #fafafa;
}

.stat-section h3 {
  margin-top: 0;
  margin-bottom: 1rem;
  color: #333;
  font-size: 1.25rem;
}

.funnel-list,
.activity-list,
.conversion-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.funnel-item,
.activity-item,
.conversion-item {
  display: flex;
  justify-content: space-between;
  padding: 0.5rem 0;
  border-bottom: 1px solid #eee;
}

.funnel-item:last-child,
.activity-item:last-child,
.conversion-item:last-child {
  border-bottom: none;
}

.stage-name {
  font-weight: 600;
}

.count {
  font-weight: 500;
}

.date {
  min-width: 100px;
  display: inline-block;
}

.stage-pair {
  font-weight: 500;
}

.rate {
  font-weight: 600;
  color: #2563eb;
}

.loading,
.error {
  text-align: center;
  padding: 2rem;
  color: #666;
}

.error {
  color: #dc2626;
}

.no-data {
  text-align: center;
  color: #888;
  font-style: italic;
  margin: 1rem 0;
}
</style>