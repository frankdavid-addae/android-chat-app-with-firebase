package com.example.androidchatapp;

public class User {
    private String username;
    private String emailAddress;
    private String profilePicture;

    public User() {}

    public User(String username, String emailAddress, String profilePicture) {
        this.username = username;
        this.emailAddress = emailAddress;
        this.profilePicture = profilePicture;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }
}
