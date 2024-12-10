package model;


public class LoggedInUser extends User {
    private static String[] addedShelvesIDs; //for dynamic UI
    private static LoggedInUser instance;

    //the only non-static method. for singleton pattern purposes.
    private LoggedInUser(String ID, String email, String username, int profilePictureOption, String about, String[] addedShelvesIDs) {
        super(ID, email, username, profilePictureOption, about);
        LoggedInUser.addedShelvesIDs = addedShelvesIDs;
    }

    private static void ensureLogIn() {
        if(!isLoggedIn()) {
            throw new RuntimeException("Not logged in");
        }
    }

    protected static void createInstance(String ID, String email, String username, int profilePictureOption, String about, String[] addedShelvesIDs) {
        if(isLoggedIn()) {
            throw new RuntimeException("Already logged in");
        }
        instance = new LoggedInUser(ID, email, username, profilePictureOption, about, addedShelvesIDs);
    }

    protected static String[] getAddedShelvesIDs() {
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
        return ShelfCollection.getShelvesWithIDs(addedShelvesIDs);
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
        shelf.addUser(instance.getID(), shelf.getAdminInvitation().equals(invitation));
    }
    public static void leaveShelf(Shelf shelf) {
        ensureLogIn();

    }
    public static void changePassword(String oldPassword, String newPassword) {
        ensureLogIn();
    }
    public static void logOut() {
        ensureLogIn();
    }



}
