package com.musai.musai.dto;

public class ArtworkInfoDTO {

    private String title;
    private String artist;
    private String year;
    private String style;
    private String description;
    private String level;

    // getter & setter
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getArtist() { return artist; }
    public void setArtist(String artist) { this.artist = artist; }

    public String getYear() { return year; }
    public void setYear(String year) { this.year = year; }

    public String getStyle() { return style; }
    public void setStyle(String style) { this.style = style; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getLevel() { return level; }
    public void setLevel(String level) { this.level = level; }
}