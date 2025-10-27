package com.example.hardwaremanagement.dto;

public class GoogleAuthRequest {
    private String idToken;
    private GoogleProfile profile;
    private String type; // "signin" or "signup"

    public String getIdToken() {
        return idToken;
    }

    public void setIdToken(String idToken) {
        this.idToken = idToken;
    }

    public GoogleProfile getProfile() {
        return profile;
    }

    public void setProfile(GoogleProfile profile) {
        this.profile = profile;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
