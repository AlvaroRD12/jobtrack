export interface StageCountDto {
  stage: string;
  count: number;
}

export interface ActivityDto {
  date: string; // ISO date string
  count: number;
}

export interface ConversionRateDto {
  fromStage: string;
  toStage: string;
  rate: number; // fraction (0 to 1)
}