import axios from 'axios';

const client = axios.create({
  baseURL: '/api'
});

export async function listApplications() {
  const response = await client.get('/applications');
  return response.data;
}

export async function createApplication(payload: Record<string, unknown>) {
  const response = await client.post('/applications', payload);
  return response.data;
}

export async function updateApplication(id: number, payload: Record<string, unknown>) {
  const response = await client.put(`/applications/${id}`, payload);
  return response.data;
}

export async function archiveApplication(id: number, archived: boolean) {
  const response = await client.patch(`/applications/${id}/archive`, { archived });
  return response.data;
}

export async function deleteApplication(id: number) {
  const response = await client.delete(`/applications/${id}`);
  return response.data;
}
