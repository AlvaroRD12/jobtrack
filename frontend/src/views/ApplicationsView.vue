<template>
  <section class="applications-view">
    <h1>Applications</h1>

    <form class="login-form" @submit.prevent="login">
      <input v-model="auth.username" placeholder="Username" required />
      <input v-model="auth.password" type="password" placeholder="Password" required />
      <button type="submit">Log in</button>
    </form>

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

    <KanbanBoard v-if="applications.length" :applications="applications" />
    <p v-else>No applications yet.</p>
  </section>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue';
import { archiveApplication, createApplication, deleteApplication, listApplications } from '../api/applications';
import { apiClient, getStoredAuthToken, setAuthToken } from '../lib/api';
import KanbanBoard from '../components/kanban/KanbanBoard.vue';

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
const auth = reactive({
  username: '',
  password: ''
});
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
  applications.value = response?.data ?? [];
}

async function login() {
  const response = await apiClient.post('/auth/login', {
    username: auth.username,
    password: auth.password
  });

  const token = response.data?.data;
  setAuthToken(token ?? null);
  await loadApplications();
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
  const createdApplication = response?.data?.data ?? response?.data ?? response;
  applications.value.push(createdApplication);
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

onMounted(async () => {
  if (getStoredAuthToken()) {
    await loadApplications();
  }
});
</script>
