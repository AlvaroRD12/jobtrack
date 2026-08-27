<template>
  <main class="app-shell">
    <h1>JobTrack</h1>
    <nav class="navbar">
      <router-link to="/" class="nav-link">Applications</router-link>
      <router-link to="/dashboard" class="nav-link">Dashboard</router-link>
    </nav>
    <router-view />
  </main>
</template>

<style scoped>
.navbar {
  display: flex;
  gap: 1rem;
  margin-bottom: 2rem;
}
.nav-link {
  text-decoration: none;
  padding: 0.5rem 1rem;
  background-color: #f0f0f0;
  border-radius: 4px;
  font-weight: 500;
}
.nav-link:hover {
  background-color: #e0e0e0;
}
.nav-link.router-link-active {
  background-color: #2563eb;
  color: white;
}
</style>

<script setup lang="ts">
import ApplicationsView from './views/ApplicationsView.vue';
</script>
