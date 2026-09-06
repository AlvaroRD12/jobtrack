<template>
  <section class="statistics-panel space-y-6">
    <h2 class="text-2xl font-bold tracking-tight text-neutral-900">Dashboard Statistics</h2>

    <div v-if="state.loading" class="loading rounded-lg border border-neutral-200 bg-white px-5 py-10 text-center text-sm text-neutral-500 shadow-sm">Loading statistics...</div>
    <div v-else-if="state.error" class="error rounded-md border border-red-200 bg-red-50 px-4 py-3 text-sm font-medium text-overdue-700">{{ state.error }}</div>
    <div v-else class="stats-content grid gap-6">
      <!-- Funnel Counts -->
      <section class="stat-section rounded-lg border border-neutral-200 bg-white p-5 shadow-sm">
        <h3 class="mb-4 text-lg font-semibold text-neutral-900">Applications by Stage</h3>
        <ul class="funnel-list divide-y divide-neutral-100">
          <li v-for="stat in state.funnelCounts" :key="stat.stage" class="funnel-item flex items-center justify-between gap-4 py-3 first:pt-0 last:pb-0">
            <span class="stage-name text-sm font-medium text-neutral-700">{{ stat.stage }}:</span>
            <span class="count min-w-10 rounded-full bg-primary-50 px-3 py-1 text-center text-sm font-semibold text-primary-700">{{ stat.count }}</span>
          </li>
        </ul>
      </section>

      <!-- Activity Over Time -->
      <section class="stat-section rounded-lg border border-neutral-200 bg-white p-5 shadow-sm">
        <h3 class="mb-4 text-lg font-semibold text-neutral-900">Application Activity Over Time</h3>
        <ul v-if="state.activityOverTime.length" class="activity-list divide-y divide-neutral-100">
          <li v-for="day in state.activityOverTime" :key="day.date" class="activity-item flex items-center justify-between gap-4 py-3 first:pt-0 last:pb-0">
            <span class="date text-sm text-neutral-600">{{ day.date }}</span>
            <span class="count text-sm font-semibold text-neutral-900">{{ day.count }} application{{ day.count !== 1 ? 's' : '' }}</span>
          </li>
        </ul>
        <p v-else class="no-data rounded-md bg-neutral-50 px-4 py-6 text-center text-sm italic text-neutral-500">No application activity recorded yet.</p>
      </section>

      <!-- Conversion Rates -->
      <section class="stat-section rounded-lg border border-neutral-200 bg-white p-5 shadow-sm">
        <h3 class="mb-4 text-lg font-semibold text-neutral-900">Conversion Rates Between Stages</h3>
        <ul v-if="state.conversionRates.length" class="conversion-list divide-y divide-neutral-100">
          <li v-for="conv in state.conversionRates" :key="conv.fromStage + '-' + conv.toStage" class="conversion-item flex items-center justify-between gap-4 py-3 first:pt-0 last:pb-0">
            <span class="stage-pair text-sm font-medium text-neutral-700">
              {{ conv.fromStage }} &rarr; {{ conv.toStage }}
            </span>
            <span class="rate rounded-full bg-primary-50 px-3 py-1 text-sm font-semibold text-primary-700">{{ (conv.rate * 100).toFixed(1) }}%</span>
          </li>
        </ul>
        <p v-else class="no-data rounded-md bg-neutral-50 px-4 py-6 text-center text-sm italic text-neutral-500">No conversion data available.</p>
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
