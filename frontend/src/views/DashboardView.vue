<template>
  <section class="dashboard-view space-y-8">
    <div class="dashboard-header">
      <h1 class="text-3xl font-bold tracking-tight text-neutral-900">JobTrack Dashboard</h1>
      <p class="dashboard-subtitle mt-2 text-sm text-neutral-500">Review your application funnel and progress</p>
    </div>

    <StatisticsPanel />
  </section>
</template>

<script setup lang="ts">
import StatisticsPanel from '../components/statistics/StatisticsPanel.vue';
</script>
