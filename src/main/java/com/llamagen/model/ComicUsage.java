package com.llamagen.model;

public final class ComicUsage {
    private Integer apiUsageCount;
    private Integer apiMaxUsage;
    private Integer credits;
    private Boolean isPaidPlan;

    public Integer getApiUsageCount() {
        return apiUsageCount;
    }

    public Integer getApiMaxUsage() {
        return apiMaxUsage;
    }

    public Integer getCredits() {
        return credits;
    }

    public Boolean getIsPaidPlan() {
        return isPaidPlan;
    }
}
