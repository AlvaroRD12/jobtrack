<template>
  <section class="dashboard-view">
    <div class="dashboard-header">
      <h1>JobTrack Dashboard</h1>
      <p class="dashboard-subtitle">Review your application funnel and progress</p>
    </div>

    <StatisticsPanel />
  </section>
</template>

<script setup lang="ts">
import StatisticsPanel from '../components/statistics/StatisticsPanel.vue';
</script>

<style scoped>
.dashboard-view {
  padding: 2rem;
  max-width: 1200px;
  margin: 0 auto;
}

.dashboard-header {
  text-align: center;
  margin-bottom: 2rem;
}

.dashboard-header h1 {
  margin: 0 0 0.5rem 0;
  color: #1f2937;
  font-size: 2rem;
}

.dashboard-header p {
  margin: 0;
  color: #6b7280;
  font-size: 1.125rem;
}
</style>