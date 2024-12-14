package model;


import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

public class LoggedInUser extends User {
    private static ArrayList<ObjectId> addedShelvesIDs; //for dynamic UI
    private static LoggedInUser instance;

    //the only non-static method. for singleton pattern purposes.
    private LoggedInUser(ObjectId ID, String email, String username, int profilePictureOption, String about, ObjectId[] addedShelvesIDs) {
        super(ID, email, username, profilePictureOption, about);
        LoggedInUser.addedShelvesIDs = new ArrayList<ObjectId>(List.of(addedShelvesIDs));
    }

    private static void ensureLogIn() {
        if(!isLoggedIn()) {
            throw new RuntimeException("Not logged in");
        }
    }

    protected static void createInstance(ObjectId ID, String email, String username, int profilePictureOption, String about, ObjectId[] addedShelvesIDs) {
        if(isLoggedIn()) {
            throw new RuntimeException("Already logged in");
        }
        instance = new LoggedInUser(ID, email, username, profilePictureOption, about, addedShelvesIDs);
    }

    protected static ArrayList<ObjectId> getAddedShelvesIDs() {
        ensureLogIn();
        return addedShelvesIDs;
    }

    public static boolean isLoggedIn() {
        return instance != null;
    }

    //should be public because we will maybe get the username of the logged in user from controllers
    public static LoggedInUser getInstance() {
        ensureLogIn();
        return instance;
    }

    public static Shelf[] getAddedShelves() {
        ensureLogIn();
        return ShelfCollection.getShelvesWithIDs(addedShelvesIDs.toArray(new ObjectId[0]));
    }

    @Override
    public void setAbout(String newAbout) {
        super.setAbout(newAbout);
        UserCollection.updateLoggedInUser();
    }

    @Override
    public void setUsername(String newUsername) {
        super.setUsername(newUsername);
        UserCollection.updateLoggedInUser();
    }

    @Override
    public void setProfilePictureOption(int newOption) {
        super.setProfilePictureOption(newOption);
        UserCollection.updateLoggedInUser();
    }

    public static void joinShelf(String invitation) {
        ensureLogIn();
        Shelf shelf = ShelfCollection.getShelfWithInvitation(invitation);
        if(addedShelvesIDs.contains(shelf.getID())) {
            return;
        }
        LoggedInUser.addedShelvesIDs.add(shelf.getID());
        shelf.addUser(instance.getID(), shelf.getAdminInvitation().equals(invitation));
        UserCollection.updateLoggedInUser();
    }
    public static void leaveShelf(Shelf shelf) {
        ensureLogIn();
        if(shelf.getParticipantsIDs().contains(instance.getID())) {
            shelf.kickUser(instance.getID());
            addedShelvesIDs.remove(shelf.getID());
            UserCollection.updateLoggedInUser();
        }
    }

    public static boolean changePassword(String oldPassword, String newPassword) {
        ensureLogIn();
        if(oldPassword.equals(newPassword)) {
            return false;
        }

        if(UserCollection.checkPassword(oldPassword)) {
            UserCollection.updatePassword(newPassword);

            return true;
        }
        return false;
    }

    public static void logOut() {
        ensureLogIn();
        addedShelvesIDs = null;
        instance = null;
    }



}
