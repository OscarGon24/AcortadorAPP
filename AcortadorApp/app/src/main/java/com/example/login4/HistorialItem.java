package com.example.login4;

public class HistorialItem {
    String slug;
    private String url;

    public HistorialItem(String slug, String url) {
        this.slug = slug;
        this.url = url;
    }

    public String getSlug() {
        return slug;
    }

    public String getUrl() {
        return url;
    }
}

