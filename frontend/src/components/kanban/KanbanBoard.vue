<template>
  <section class="kanban-board grid gap-5 xl:grid-cols-6">
    <p v-if="errorMessage" class="kanban-error rounded-md border border-red-200 bg-red-50 px-4 py-3 text-sm font-medium text-overdue-700 xl:col-span-6" role="alert">{{ errorMessage }}</p>
    <div v-for="stage in stages" :key="stage" class="kanban-column min-h-56 rounded-lg border border-neutral-200 bg-neutral-50 p-4 shadow-sm" :data-testid="`stage-column-${stage}`" @dragover.prevent @drop="onDrop(stage)">
      <div class="mb-4 flex items-center justify-between gap-3 border-b border-neutral-200 pb-3">
        <h2 class="text-sm font-semibold text-neutral-900">{{ stage }}</h2>
        <span class="rounded-full bg-white px-2.5 py-1 text-xs font-semibold text-neutral-600 shadow-sm">{{ applicationsByStage(stage).length }}</span>
      </div>
      <div
        v-for="application in applicationsByStage(stage)"
        :key="application.id"
        class="kanban-card mb-3 rounded-md border border-neutral-200 bg-white p-4 shadow-sm transition hover:-translate-y-0.5 hover:shadow-md"
        draggable="true"
        :data-testid="`application-card-${application.id}`"
        :class="{ 'overdue border-l-4 border-overdue-500 bg-red-50': application.overdue }"
        @dragstart="onDragStart(application)"
      >
        <div class="flex flex-wrap items-baseline gap-x-1 gap-y-1">
          <strong class="text-sm font-semibold text-neutral-900">{{ application.company }}</strong>
          <span class="text-sm text-neutral-500">·</span>
          <span class="text-sm text-neutral-700">{{ application.position }}</span>
        </div>
        <template v-if="application.overdue">
          <span class="overdue-label mt-3 inline-flex rounded-full bg-red-100 px-2.5 py-1 text-xs font-semibold uppercase tracking-wide text-overdue-700">OVERDUE</span>
        </template>
      </div>
      <p v-if="!applicationsByStage(stage).length" class="py-6 text-center text-sm text-neutral-500">No applications</p>
    </div>
  </section>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { updateApplicationStage } from '../../services/kanban';

interface ApplicationRecord {
  id: number;
  company: string;
  position: string;
  stage: string;
}

const props = defineProps<{
  applications: ApplicationRecord[];
}>();

const stages = ['Applied', 'In progress', 'Interview', 'Offer', 'Rejected', 'Withdrawn'];
const draggedApplication = ref<ApplicationRecord | null>(null);
const errorMessage = ref('');

function applicationsByStage(stage: string) {
  return props.applications.filter((application) => application.stage === stage);
}

function onDragStart(application: ApplicationRecord) {
  draggedApplication.value = application;
}

async function onDrop(stage: string) {
  if (!draggedApplication.value) {
    return;
  }

  const movedApplication = draggedApplication.value;

  try {
    await updateApplicationStage(movedApplication.id, stage);
    movedApplication.stage = stage;
    errorMessage.value = '';
  } catch (error) {
    const status = (error as { response?: { status?: number } })?.response?.status;
    const backendMessage = (error as { response?: { data?: { message?: string; data?: string } } })?.response?.data?.message
      ?? (error as { response?: { data?: { data?: string } } })?.response?.data?.data
      ?? 'Unable to move application stage.';

    errorMessage.value = status ? `Stage update failed (${status}): ${backendMessage}` : `Stage update failed: ${backendMessage}`;
  } finally {
    draggedApplication.value = null;
  }
}
</script>
