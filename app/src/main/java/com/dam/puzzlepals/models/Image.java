package com.dam.puzzlepals.models;

public class Image {

    private Integer id;
    private Integer width;
    private Integer height;
    private String url;
    private String photographer;
    private String photographer_url;
    private Integer photographer_id;
    private String avg_color;
    private SrcImage src;
    private boolean liked;
    private String alt;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPhotographer() {
        return photographer;
    }

    public void setPhotographer(String photographer) {
        this.photographer = photographer;
    }

    public String getPhotographer_url() {
        return photographer_url;
    }

    public void setPhotographer_url(String photographer_url) {
        this.photographer_url = photographer_url;
    }

    public Integer getPhotographer_id() {
        return photographer_id;
    }

    public void setPhotographer_id(Integer photographer_id) {
        this.photographer_id = photographer_id;
    }

    public String getAvg_color() {
        return avg_color;
    }

    public void setAvg_color(String avg_color) {
        this.avg_color = avg_color;
    }

    public SrcImage getSrc() {
        return src;
    }

    public void setSrc(SrcImage src) {
        this.src = src;
    }

    public boolean isLiked() {
        return liked;
    }

    public void setLiked(boolean liked) {
        this.liked = liked;
    }

    public String getAlt() {
        return alt;
    }

    public void setAlt(String alt) {
        this.alt = alt;
    }
}
