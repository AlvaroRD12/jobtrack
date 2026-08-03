import { apiClient } from '../lib/api';

export async function listApplications() {
  const response = await apiClient.get('/applications');
  return response.data;
}

export async function createApplication(payload: Record<string, unknown>) {
  const response = await apiClient.post('/applications', payload);
  return response.data;
}

export async function updateApplication(id: number, payload: Record<string, unknown>) {
  const response = await apiClient.put(`/applications/${id}`, payload);
  return response.data;
}

export async function archiveApplication(id: number, archived: boolean) {
  const response = await apiClient.patch(`/applications/${id}/archive`, { archived });
  return response.data;
}

export async function deleteApplication(id: number) {
  const response = await apiClient.delete(`/applications/${id}`);
  return response.data;
}
