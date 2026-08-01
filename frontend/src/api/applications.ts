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
