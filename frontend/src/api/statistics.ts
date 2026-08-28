import { apiClient } from '../lib/api';
import { StageCountDto } from '../types/statistics';
import { ActivityDto } from '../types/statistics';
import { ConversionRateDto } from '../types/statistics';

export async function getFunnelCounts(): Promise<StageCountDto[]> {
  const response = await apiClient.get('/statistics/funnel');
  return response.data.data;
}

export async function getActivityOverTime(): Promise<ActivityDto[]> {
  const response = await apiClient.get('/statistics/activity');
  return response.data.data;
}

export async function getConversionRates(): Promise<ConversionRateDto[]> {
  const response = await apiClient.get('/statistics/conversion');
  return response.data.data;
}