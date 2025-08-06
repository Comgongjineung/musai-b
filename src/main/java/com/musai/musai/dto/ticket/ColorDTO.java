package com.musai.musai.dto.ticket;

import java.util.List;

public class ColorDTO {
    private List<Integer> dominantRgb;
    private List<List<Integer>> palette;

    public ColorDTO() {}

    public ColorDTO(List<Integer> dominantRgb, List<List<Integer>> palette) {
        this.dominantRgb = dominantRgb;
        this.palette = palette;
    }

    public List<Integer> getDominantRgb() {
        return dominantRgb;
    }

    public void setDominantRgb(List<Integer> dominantRgb) {
        this.dominantRgb = dominantRgb;
    }

    public List<List<Integer>> getPalette() {
        return palette;
    }

    public void setPalette(List<List<Integer>> palette) {
        this.palette = palette;
    }
}