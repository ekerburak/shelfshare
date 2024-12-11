package model;

import org.bson.types.ObjectId;

public class User {
    private ObjectId ID;
    private String
            email,
            username,
            about;
    private int profilePictureOption;

    protected User(ObjectId ID, String email, String username, int profilePictureOption, String about) {
        this.ID = ID;
        this.email = email;
        this.username = username;
        this.profilePictureOption = profilePictureOption;
        this.about = about;
    }

    protected void setUsername(String username) {
        this.username = username;
    }

    protected void setProfilePictureOption(int profilePictureOption) {
        this.profilePictureOption = profilePictureOption;
    }

    protected void setAbout(String about) {
        this.about = about;
    }

    public String toString() {
        return "User: <"
                + ID + " "
                + username + " "
                + email + " "
                + about + " "
                + profilePictureOption + ">";
    }

    public ObjectId getID() {
        return ID;
    }

    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }

    public int getProfilePictureOption() {
        return profilePictureOption;
    }

    public String getAbout() {
        return about;
    }
}
