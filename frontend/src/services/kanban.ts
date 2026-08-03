import { apiClient } from '../lib/api';

export async function updateApplicationStage(id: number, stage: string) {
  const response = await apiClient.put(`/applications/${id}/stage`, { stage });
  return response.data;
}
