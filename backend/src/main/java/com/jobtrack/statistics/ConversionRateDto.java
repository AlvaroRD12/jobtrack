package com.jobtrack.statistics;

public class ConversionRateDto {
    private String fromStage;
    private String toStage;
    private double rate; // as a fraction (0.0 to 1.0)

    public ConversionRateDto() {}

    public ConversionRateDto(String fromStage, String toStage, double rate) {
        this.fromStage = fromStage;
        this.toStage = toStage;
        this.rate = rate;
    }

    public String getFromStage() {
        return fromStage;
    }

    public void setFromStage(String fromStage) {
        this.fromStage = fromStage;
    }

    public String getToStage() {
        return toStage;
    }

    public void setToStage(String toStage) {
        this.toStage = toStage;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }
}