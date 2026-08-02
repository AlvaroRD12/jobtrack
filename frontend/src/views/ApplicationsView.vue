<template>
  <section class="applications-view">
    <h1>Applications</h1>
    <form @submit.prevent="submitForm" class="application-form">
      <input v-model="form.company" placeholder="Company" required />
      <input v-model="form.position" placeholder="Position" required />
      <input v-model="form.source" placeholder="Source" />
      <input v-model="form.applicationDate" type="date" required />
      <input v-model="form.stage" placeholder="Stage" />
      <textarea v-model="form.notes" placeholder="Notes"></textarea>
      <input v-model="form.nextFollowUpDate" type="date" />
      <button type="submit">Save</button>
    </form>

    <ul v-if="applications.length" class="application-list">
      <li v-for="application in applications" :key="application.id">
        <strong>{{ application.company }}</strong> — {{ application.position }}
        <span v-if="application.archived">(archived)</span>
      </li>
    </ul>
    <p v-else>No applications yet.</p>
  </section>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue';
import { archiveApplication, createApplication, deleteApplication, listApplications, updateApplication } from '../api/applications';

interface ApplicationRecord {
  id?: number;
  company: string;
  position: string;
  source?: string;
  applicationDate: string;
  stage?: string;
  notes?: string;
  nextFollowUpDate?: string;
  archived?: boolean;
}

const applications = ref<ApplicationRecord[]>([]);
const form = reactive<ApplicationRecord>({
  company: '',
  position: '',
  source: '',
  applicationDate: '',
  stage: 'Applied',
  notes: '',
  nextFollowUpDate: ''
});

async function loadApplications() {
  const response = await listApplications();
  applications.value = response.data ?? [];
}

async function submitForm() {
  const payload = {
    company: form.company,
    position: form.position,
    source: form.source || null,
    applicationDate: form.applicationDate,
    stage: form.stage || 'Applied',
    notes: form.notes || '',
    nextFollowUpDate: form.nextFollowUpDate || null
  };

  const response = await createApplication(payload);
  applications.value.push(response.data);
  resetForm();
}

async function archiveCurrent(id: number) {
  const response = await archiveApplication(id, true);
  const target = applications.value.find((item) => item.id === id);
  if (target) {
    target.archived = response.data?.archived ?? true;
  }
}

async function removeCurrent(id: number) {
  await deleteApplication(id);
  applications.value = applications.value.filter((item) => item.id !== id);
}

function resetForm() {
  form.company = '';
  form.position = '';
  form.source = '';
  form.applicationDate = '';
  form.stage = 'Applied';
  form.notes = '';
  form.nextFollowUpDate = '';
}

onMounted(loadApplications);
</script>
