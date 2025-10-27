package com.example.hardwaremanagement.dto;

public class UserResponse {
    private String username;
    private String email;
    private String role;
    private String fullName;
    private String profileImage;

    public UserResponse() {}

    public UserResponse(String username, String email, String role, String fullName, String profileImage) {
        this.username = username;
        this.email = email;
        this.role = role;
        this.fullName = fullName;
        this.profileImage = profileImage;
    }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getProfileImage() { return profileImage; }
    public void setProfileImage(String profileImage) { this.profileImage = profileImage; }
}
