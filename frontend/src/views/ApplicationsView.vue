<template>
  <section class="applications-view space-y-8">
    <h1 class="text-3xl font-bold tracking-tight text-neutral-900">Applications</h1>

    <div class="auth-tabs flex flex-wrap gap-2" role="tablist" aria-label="Account access">
      <button
        type="button"
        class="rounded-md border border-neutral-200 px-4 py-2 text-sm font-medium transition focus:outline-none focus:ring-2 focus:ring-primary-500 focus:ring-offset-2"
        :class="authMode === 'login'
          ? 'bg-primary text-white hover:bg-primary-700'
          : 'bg-white text-neutral-700 hover:bg-neutral-100'"
        @click="showLogin"
      >Log in</button>
      <button
        type="button"
        class="rounded-md border border-neutral-200 px-4 py-2 text-sm font-medium transition focus:outline-none focus:ring-2 focus:ring-primary-500 focus:ring-offset-2"
        :class="authMode === 'register'
          ? 'bg-primary text-white hover:bg-primary-700'
          : 'bg-white text-neutral-700 hover:bg-neutral-100'"
        @click="showRegister"
      >Register</button>
    </div>

    <p v-if="authMessage" class="auth-message rounded-md border border-green-200 bg-green-50 px-4 py-3 text-sm font-medium text-green-800" role="status">{{ authMessage }}</p>
    <p v-if="authError" class="auth-error rounded-md border border-red-200 bg-red-50 px-4 py-3 text-sm font-medium text-overdue-700" role="alert">{{ authError }}</p>

    <form v-if="authMode === 'login'" class="login-form grid gap-4 rounded-lg border border-neutral-200 bg-white p-5 shadow-sm sm:grid-cols-[1fr_1fr_auto] sm:items-end" data-testid="login-form" @submit.prevent="login">
      <label class="grid gap-1.5 text-sm font-medium text-neutral-700">
        Username
        <input v-model="auth.username" class="rounded-md border border-neutral-300 px-3 py-2 text-neutral-900 shadow-sm outline-none transition placeholder:text-neutral-500 focus:border-primary focus:ring-2 focus:ring-primary-100" placeholder="Username" required />
      </label>
      <label class="grid gap-1.5 text-sm font-medium text-neutral-700">
        Password
        <input v-model="auth.password" class="rounded-md border border-neutral-300 px-3 py-2 text-neutral-900 shadow-sm outline-none transition placeholder:text-neutral-500 focus:border-primary focus:ring-2 focus:ring-primary-100" type="password" placeholder="Password" required />
      </label>
      <button type="submit" class="rounded-md bg-primary px-4 py-2 font-medium text-white shadow-sm transition hover:bg-primary-700 focus:outline-none focus:ring-2 focus:ring-primary-500 focus:ring-offset-2">Log in</button>
    </form>

    <form v-else class="login-form grid gap-4 rounded-lg border border-neutral-200 bg-white p-5 shadow-sm sm:grid-cols-[1fr_1fr_auto] sm:items-end" data-testid="register-form" @submit.prevent="register">
      <label class="grid gap-1.5 text-sm font-medium text-neutral-700">
        Username
        <input v-model="auth.username" class="rounded-md border border-neutral-300 px-3 py-2 text-neutral-900 shadow-sm outline-none transition placeholder:text-neutral-500 focus:border-primary focus:ring-2 focus:ring-primary-100" placeholder="Username" required />
      </label>
      <label class="grid gap-1.5 text-sm font-medium text-neutral-700">
        Password
        <input v-model="auth.password" class="rounded-md border border-neutral-300 px-3 py-2 text-neutral-900 shadow-sm outline-none transition placeholder:text-neutral-500 focus:border-primary focus:ring-2 focus:ring-primary-100" type="password" placeholder="Password" required />
      </label>
      <button type="submit" class="rounded-md bg-primary px-4 py-2 font-medium text-white shadow-sm transition hover:bg-primary-700 focus:outline-none focus:ring-2 focus:ring-primary-500 focus:ring-offset-2">Register</button>
    </form>

    <form @submit.prevent="submitForm" class="application-form grid gap-5 rounded-lg border border-neutral-200 bg-white p-5 shadow-sm sm:grid-cols-2" data-testid="application-form">
      <div class="field grid gap-1.5">
        <label for="company" class="text-sm font-medium text-neutral-700">Company</label>
        <input id="company" v-model="form.company" class="rounded-md border border-neutral-300 px-3 py-2 text-neutral-900 shadow-sm outline-none transition placeholder:text-neutral-500 focus:border-primary focus:ring-2 focus:ring-primary-100" placeholder="Company" required />
      </div>
      <div class="field grid gap-1.5">
        <label for="position" class="text-sm font-medium text-neutral-700">Position</label>
        <input id="position" v-model="form.position" class="rounded-md border border-neutral-300 px-3 py-2 text-neutral-900 shadow-sm outline-none transition placeholder:text-neutral-500 focus:border-primary focus:ring-2 focus:ring-primary-100" placeholder="Position" required />
      </div>
      <div class="field grid gap-1.5">
        <label for="source" class="text-sm font-medium text-neutral-700">Source</label>
        <input id="source" v-model="form.source" class="rounded-md border border-neutral-300 px-3 py-2 text-neutral-900 shadow-sm outline-none transition placeholder:text-neutral-500 focus:border-primary focus:ring-2 focus:ring-primary-100" placeholder="Source" />
      </div>
      <div class="field grid gap-1.5">
        <label for="applicationDate" class="text-sm font-medium text-neutral-700">Application Date</label>
        <input id="applicationDate" v-model="form.applicationDate" class="rounded-md border border-neutral-300 px-3 py-2 text-neutral-900 shadow-sm outline-none transition focus:border-primary focus:ring-2 focus:ring-primary-100" type="date" required />
      </div>
      <div class="field grid gap-1.5">
        <label for="stage" class="text-sm font-medium text-neutral-700">Stage</label>
        <input id="stage" v-model="form.stage" class="rounded-md border border-neutral-300 px-3 py-2 text-neutral-900 shadow-sm outline-none transition placeholder:text-neutral-500 focus:border-primary focus:ring-2 focus:ring-primary-100" placeholder="Stage" />
      </div>
      <div class="field grid gap-1.5">
        <label for="notes" class="text-sm font-medium text-neutral-700">Notes</label>
        <textarea id="notes" v-model="form.notes" class="min-h-24 resize-y rounded-md border border-neutral-300 px-3 py-2 text-neutral-900 shadow-sm outline-none transition placeholder:text-neutral-500 focus:border-primary focus:ring-2 focus:ring-primary-100" placeholder="Notes"></textarea>
      </div>
      <div class="field follow-up-field grid gap-1.5">
        <label for="followUpDate" class="text-sm font-medium text-neutral-700">Follow-up Date</label>
        <input id="followUpDate" v-model="form.nextFollowUpDate" class="rounded-md border border-neutral-300 px-3 py-2 text-neutral-900 shadow-sm outline-none transition focus:border-primary focus:ring-2 focus:ring-primary-100" :class="{ overdue: isFollowUpOverdue, 'border-overdue-600 bg-red-50 text-overdue-700 ring-1 ring-overdue-500': isFollowUpOverdue }" type="date">
        <span v-if="isFollowUpOverdue" class="overdue-warning inline-flex w-fit rounded-full bg-red-100 px-2.5 py-1 text-xs font-semibold text-overdue-700">Overdue!</span>
      </div>
      <button type="submit" class="save-button sm:col-span-2 rounded-md bg-primary px-4 py-3 font-medium text-white shadow-sm transition hover:bg-primary-700 focus:outline-none focus:ring-2 focus:ring-primary-500 focus:ring-offset-2">Save</button>
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
