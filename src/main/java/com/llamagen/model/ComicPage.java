package com.llamagen.model;

import java.util.List;

public final class ComicPage {
    private Integer page;
    private List<ComicPanel> panels;

    public Integer getPage() {
        return page;
    }

    public List<ComicPanel> getPanels() {
        return panels;
    }
}
