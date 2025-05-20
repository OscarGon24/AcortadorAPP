package com.example.login4;

public class UrlRequest {
    private String url;
    private String email;

    public UrlRequest(String url, String email) {
        this.url = url;
        this.email = email;
    }

    public String getUrl() {
        return url;
    }
    public String getEmail() {
        return email;
    }
}
