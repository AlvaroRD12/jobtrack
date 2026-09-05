<template>
  <section class="applications-view">
    <h1>Applications</h1>

    <div class="auth-tabs" role="tablist" aria-label="Account access">
      <button type="button" :class="{ active: authMode === 'login' }" @click="showLogin">Log in</button>
      <button type="button" :class="{ active: authMode === 'register' }" @click="showRegister">Register</button>
    </div>

    <p v-if="authMessage" class="auth-message" role="status">{{ authMessage }}</p>
    <p v-if="authError" class="auth-error" role="alert">{{ authError }}</p>

    <form v-if="authMode === 'login'" class="login-form" data-testid="login-form" @submit.prevent="login">
      <input v-model="auth.username" placeholder="Username" required />
      <input v-model="auth.password" type="password" placeholder="Password" required />
      <button type="submit">Log in</button>
    </form>

    <form v-else class="login-form" data-testid="register-form" @submit.prevent="register">
      <input v-model="auth.username" placeholder="Username" required />
      <input v-model="auth.password" type="password" placeholder="Password" required />
      <button type="submit">Register</button>
    </form>

    <form @submit.prevent="submitForm" class="application-form" data-testid="application-form">
      <div class="field">
        <label for="company">Company</label>
        <input id="company" v-model="form.company" placeholder="Company" required />
      </div>
      <div class="field">
        <label for="position">Position</label>
        <input id="position" v-model="form.position" placeholder="Position" required />
      </div>
      <div class="field">
        <label for="source">Source</label>
        <input id="source" v-model="form.source" placeholder="Source" />
      </div>
      <div class="field">
        <label for="applicationDate">Application Date</label>
        <input id="applicationDate" v-model="form.applicationDate" type="date" required />
      </div>
      <div class="field">
        <label for="stage">Stage</label>
        <input id="stage" v-model="form.stage" placeholder="Stage" />
      </div>
      <div class="field">
        <label for="notes">Notes</label>
        <textarea id="notes" v-model="form.notes" placeholder="Notes"></textarea>
      </div>
      <div class="field follow-up-field">
        <label for="followUpDate">Follow-up Date</label>
        <input id="followUpDate" v-model="form.nextFollowUpDate" type="date" :class="{ overdue: isFollowUpOverdue }">
        <span v-if="isFollowUpOverdue" class="overdue-warning">Overdue!</span>
      </div>
      <button type="submit" class="save-button">Save</button>
    </form>

    <KanbanBoard v-if="applications.length" :applications="applications" />
    <p v-else>No applications yet.</p>
  </section>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref, computed } from 'vue';
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
  overdue?: boolean;
}

const applications = ref<ApplicationRecord[]>([]);
const authMode = ref<'login' | 'register'>('login');
const authMessage = ref('');
const authError = ref('');
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

const isFollowUpOverdue = computed(() => {
  if (!form.nextFollowUpDate) return false;
  return new Date(form.nextFollowUpDate) < new Date();
});

async function loadApplications() {
  const response = await listApplications();
  const apps = response?.data ?? [];
  applications.value = apps;
}

async function login() {
  authMessage.value = '';
  authError.value = '';
  try {
    const response = await apiClient.post('/auth/login', {
      username: auth.username,
      password: auth.password
    });

    const token = response.data?.data;
    setAuthToken(token ?? null);
    await loadApplications();
    authMessage.value = 'Logged in successfully.';
  } catch (error) {
    authError.value = getAuthErrorMessage(error);
  }
}

async function register() {
  authError.value = '';
  authMessage.value = '';
  try {
    await apiClient.post('/auth/register', {
      username: auth.username,
      password: auth.password
    });
    authMode.value = 'login';
    authMessage.value = 'Registration successful. Please log in.';
  } catch (error) {
    authError.value = getAuthErrorMessage(error);
  }
}

function getAuthErrorMessage(error: unknown) {
  const response = (error as { response?: { data?: { message?: string } } }).response;
  return response?.data?.message ?? 'Unable to complete the request.';
}

function showLogin() {
  authMode.value = 'login';
  authError.value = '';
}

function showRegister() {
  authMode.value = 'register';
  authMessage.value = '';
  authError.value = '';
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

<style scoped>
.application-form {
  display: flex;
  flex-wrap: wrap;
  gap: 1rem;
  align-items: stretch;
}
.application-form .field {
  display: flex;
  flex-direction: column;
  flex: 1 1 200px;
}
.application-form .field label {
  margin-bottom: 0.25rem;
  font-weight: 600;
}
.application-form .field input,
.application-form .field textarea {
  padding: 0.5rem;
  border: 1px solid #ccc;
  border-radius: 4px;
  font: inherit;
}
.application-form .field textarea {
  min-height: 80px;
  resize: vertical;
}
.application-form .follow-up-field {
  /* same as .field */
}
.application-form .overdue-warning {
  margin-left: 0.5rem;
  color: #ef4444;
  font-weight: 600;
}
.application-form .save-button {
  width: 100%;
  padding: 0.75rem;
  background-color: #2563eb;
  color: white;
  border: none;
  border-radius: 4px;
  font-size: 1rem;
  cursor: pointer;
  margin-top: 1rem;
}
.application-form .save-button:hover {
  background-color: #1d4ed8;
}

/* Keep existing login form styling */
.login-form {
  display: flex;
  flex-wrap: wrap;
  gap: 0.5rem;
  margin-bottom: 2rem;
}
.auth-tabs {
  display: flex;
  gap: 0.5rem;
  margin-bottom: 0.75rem;
}
.auth-tabs button {
  padding: 0.5rem 1rem;
  border: 1px solid #ccc;
  border-radius: 4px;
  background: white;
  cursor: pointer;
}
.auth-tabs button.active {
  background-color: #2563eb;
  color: white;
  border-color: #2563eb;
}
.auth-message,
.auth-error {
  margin: 0 0 0.75rem;
}
.auth-message {
  color: #166534;
}
.auth-error {
  color: #b91c1c;
}
.login-form input {
  padding: 0.5rem;
  border: 1px solid #ccc;
  border-radius: 4px;
}
.login-form button {
  padding: 0.5rem 1rem;
  background-color: #2563eb;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}
.login-form button:hover {
  background-color: #1d4ed8;
}
</style>