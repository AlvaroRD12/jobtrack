<template>
  <section class="kanban-board">
    <p v-if="errorMessage" class="kanban-error" role="alert">{{ errorMessage }}</p>
    <div v-for="stage in stages" :key="stage" class="kanban-column" :data-testid="`stage-column-${stage}`" @dragover.prevent @drop="onDrop(stage)">
      <h2>{{ stage }}</h2>
      <div
        v-for="application in applicationsByStage(stage)"
        :key="application.id"
        class="kanban-card"
        draggable="true"
        :data-testid="`application-card-${application.id}`"
        @dragstart="onDragStart(application)"
      >
        <strong>{{ application.company }}</strong>
        <span>{{ application.position }}</span>
      </div>
      <p v-if="!applicationsByStage(stage).length">No applications</p>
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
