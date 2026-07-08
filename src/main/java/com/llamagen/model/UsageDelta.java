package com.llamagen.model;

public final class UsageDelta {
    private Integer amount;
    private String unit;
    private Integer totalComics;
    private Integer total_comics;
    private Integer redrawPanels;
    private Integer redraw_panels;

    public Integer getAmount() {
        return amount;
    }

    public String getUnit() {
        return unit;
    }

    public Integer getTotalComics() {
        return totalComics != null ? totalComics : total_comics;
    }

    public Integer getRedrawPanels() {
        return redrawPanels != null ? redrawPanels : redraw_panels;
    }
}
