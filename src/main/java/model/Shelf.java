package model;

import com.mongodb.client.MongoIterable;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;

/*
name
isPublic
allowBookAdd
allowBookAnnotate
allowDiscussion
allowInvitation
popularity
standardInvitation
adminInvitation

forum (chat)
addedBooks (book)
participants (user)
admin (user) (multiple people)
* */



public class Shelf {
    private ObjectId ID;
    private String name;
    private boolean isPublic;
    private boolean allowBookAdd;
    private boolean allowBookAnnotate;
    private boolean allowDiscussion;
    private boolean allowInvitation;
    private int popularity;
    private String standardInvitation;
    private String adminInvitation;

    private ObjectId forumChatID;
    private ArrayList<ObjectId> addedBooksIDs;
    private ArrayList<ObjectId> participantsIDs;
    private ArrayList<ObjectId> adminsIDs;

    public Shelf(
            ObjectId ID,
            String name,
            boolean isPublic,
            boolean allowBookAdd,
            boolean allowBookAnnotate,
            boolean allowDiscussion,
            boolean allowInvitation,
            int popularity,
            String adminInvitation,
            String standardInvitation,
            ObjectId forumChatID,
            ArrayList<ObjectId> addedBooksIDs,
            ArrayList<ObjectId> participantsIDs,
            ArrayList<ObjectId> adminsIDs
    ) {
        this.ID = ID;
        this.name = name;
        this.isPublic = isPublic;
        this.allowBookAdd = allowBookAdd;
        this.allowBookAnnotate = allowBookAnnotate;
        this.allowDiscussion = allowDiscussion;
        this.allowInvitation = allowInvitation;
        this.popularity = popularity;
        this.adminInvitation = adminInvitation;
        this.standardInvitation = standardInvitation;
        this.forumChatID = forumChatID;
        this.addedBooksIDs = addedBooksIDs;
        this.participantsIDs = participantsIDs;
        this.adminsIDs = adminsIDs;
    }

    public ObjectId getID() {
        return ID;
    }

    public String getName() {
        return name;
    }

    public boolean getIsPublic() {
        return isPublic;
    }

    public boolean getAllowBookAdd() {
        return allowBookAdd;
    }

    public boolean getAllowBookAnnotate() {
        return allowBookAnnotate;
    }

    public boolean getAllowDiscussion() {
        return allowDiscussion;
    }

    public boolean getAllowInvitation() {
        return allowInvitation;
    }

    public ObjectId getForumChatID() {
        return forumChatID;
    }

    public ArrayList<ObjectId> getAddedBooksIDs() {
        return addedBooksIDs;
    }

    public ArrayList<ObjectId> getParticipantsIDs() {
        return participantsIDs;
    }

    public ArrayList<ObjectId> getAdminsIDs() {
        return adminsIDs;
    }

    public String getAdminInvitation() {
        return adminInvitation;
    }

    public String getStandardInvitation() {
        return standardInvitation;
    }

    public void setName(String name) {
        this.name = name;
    }
    public void setIsPublic(boolean isPublic) {
        this.isPublic = isPublic;
    }
    public void setAllowBookAdd(boolean allowBookAdd) {
        this.allowBookAdd = allowBookAdd;
    }
    public void setAllowBookAnnotate(boolean allowBookAnnotate) {
        this.allowBookAnnotate = allowBookAnnotate;
    }
    public void setAllowDiscussion(boolean allowDiscussion) {
        this.allowDiscussion = allowDiscussion;
    }
    public void setAllowInvitation(boolean allowInvitation) {
        this.allowInvitation = allowInvitation;
    }

    public void addUser(ObjectId userID, boolean asAdmin) {
        if(participantsIDs.contains(userID)) {
            throw new IllegalStateException("User already in shelf");
        }
        participantsIDs.add(userID);
        if(asAdmin) {
            adminsIDs.add(userID);
        }
        ShelfCollection.updateShelf(this);
    }

    public void kickUser(ObjectId userID) {
        if(!participantsIDs.contains(userID)) {
            throw new IllegalArgumentException("User is not a participant of this shelf");
        }
        participantsIDs.remove(userID);
        adminsIDs.remove(userID);
        UserCollection.removeFromAddedShelves(userID, ID);
        ShelfCollection.updateShelf(this);
    }

    public void addBook(boolean isDownloadable, String[] base64Pages) {
        ObjectId bookID = BookCollection.addBook(isDownloadable, base64Pages);
        this.addedBooksIDs.add(bookID);
        ShelfCollection.updateShelf(this);
    }

    public ArrayList<Book> getBooks() {
        return BookCollection.getAddedBooksByIDs(addedBooksIDs);
    }

    //TODO
    public void removeBook(ObjectId bookID) {
        throw new RuntimeException("NOT IMPLEMENTED!!!");
    }

    public Chat getForumChat() {
        throw new RuntimeException("NOT IMPLEMENTED!!");
    }
    public int getPopularity() {
        return popularity;
    }
}
