<template>
  <main class="min-h-screen bg-neutral-50 px-4 py-6 text-neutral-900 sm:px-6 lg:px-8">
    <div class="mx-auto max-w-7xl">
      <header class="border-b border-neutral-200 pb-5">
        <div class="flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between">
          <h1 class="text-2xl font-bold tracking-tight text-neutral-900">JobTrack - Test</h1>
          <nav class="flex items-center gap-2" aria-label="Primary navigation">
            <router-link
              to="/"
              class="rounded-md px-4 py-2 text-sm font-medium text-neutral-700 transition hover:bg-neutral-100 hover:text-neutral-900"
              active-class="bg-primary text-white hover:bg-primary-700 hover:text-white"
            >Applications</router-link>
            <router-link
              to="/dashboard"
              class="rounded-md px-4 py-2 text-sm font-medium text-neutral-700 transition hover:bg-neutral-100 hover:text-neutral-900"
              active-class="bg-primary text-white hover:bg-primary-700 hover:text-white"
            >Dashboard</router-link>
          </nav>
        </div>
      </header>
      <div class="pt-8">
        <router-view />
      </div>
    </div>
  </main>
</template>

<script setup lang="ts">
import ApplicationsView from './views/ApplicationsView.vue';
</script>
