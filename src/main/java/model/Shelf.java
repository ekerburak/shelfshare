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
    private double popularity;
    private String standardInvitation;
    private String adminInvitation;

    private ObjectId forumChatID;
    private ArrayList<ObjectId> addedBooksIDs;
    private ArrayList<ObjectId> participantsIDs;
    private ArrayList<ObjectId> adminsIDs;
    private ArrayList<ObjectId> ratedParticipantsIDs;

    public Shelf(
            ObjectId ID,
            String name,
            boolean isPublic,
            boolean allowBookAdd,
            boolean allowBookAnnotate,
            boolean allowDiscussion,
            boolean allowInvitation,
            double popularity,
            String adminInvitation,
            String standardInvitation,
            ObjectId forumChatID,
            ArrayList<ObjectId> addedBooksIDs,
            ArrayList<ObjectId> participantsIDs,
            ArrayList<ObjectId> adminsIDs,
            ArrayList<ObjectId> ratedParticipantsIDs
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
        this.ratedParticipantsIDs = ratedParticipantsIDs;
        assert ratedParticipantsIDs != null;
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
    public ArrayList<User> getParticipants() {
        return UserCollection.getUsersByIDs(participantsIDs);
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
        ShelfCollection.updateShelf(this);
    }
    public void setIsPublic(boolean isPublic) {
        this.isPublic = isPublic;
        ShelfCollection.updateShelf(this);
    }
    public void setAllowBookAdd(boolean allowBookAdd) {
        this.allowBookAdd = allowBookAdd;
        ShelfCollection.updateShelf(this);
    }
    public void setAllowBookAnnotate(boolean allowBookAnnotate) {
        this.allowBookAnnotate = allowBookAnnotate;
        ShelfCollection.updateShelf(this);
    }

    public void setAllowDiscussion(boolean allowDiscussion) {
        this.allowDiscussion = allowDiscussion;
        ShelfCollection.updateShelf(this);
    }

    public void setAllowInvitation(boolean allowInvitation) {
        this.allowInvitation = allowInvitation;
        ShelfCollection.updateShelf(this);
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

    //TODO: Check if logged in user is admin
    public void kickUser(ObjectId userID) {
        if(!participantsIDs.contains(userID)) {
            throw new IllegalArgumentException("User is not a participant of this shelf");
        }
        participantsIDs.remove(userID);
        adminsIDs.remove(userID);
        UserCollection.removeFromAddedShelves(userID, ID);
        ShelfCollection.updateShelf(this);
    }

    private Object lock = new Object();

    public void addBook(String name,int num, boolean isDownloadable, String[] base64Pages) {
        synchronized (lock) {
            ObjectId bookID = BookCollection.addBook(name, num, isDownloadable, base64Pages);
            this.addedBooksIDs.add(bookID);
            ShelfCollection.updateShelf(this);
        }
    }

    public ArrayList<Book> getBooks() {
        synchronized (lock) {
             return BookCollection.getAddedBooksByIDs(addedBooksIDs);
        }
    }

    public void deleteBook(ObjectId bookID) {
        if(!addedBooksIDs.contains(bookID)) {
            throw new IllegalArgumentException("Book not in this shelf");
        }
        addedBooksIDs.remove(bookID);
        ShelfCollection.updateShelf(this);
        BookCollection.deleteBook(bookID);
    }

    public Chat getForumChat() {
        return ChatCollection.getChat(forumChatID);
    }


    public double getPopularity() {
        return popularity;
    }

    public String toString() {
        return "< Shelf id : " + ID + " name: " + name + " >";
    }

    public ArrayList<ObjectId> getRatedParticipantsIDs() {
        return ratedParticipantsIDs;
    }

    //1 <= rating <= 5
    public boolean rateShelf(int rating) {
        if(ratedParticipantsIDs.contains(LoggedInUser.getInstance().getID())) {
            //already rated
            return false;
        }
        if(!(1 <= rating && rating <= 5)) {
            return false;
        }
        popularity *= ratedParticipantsIDs.size();
        popularity += rating;
        ratedParticipantsIDs.add(LoggedInUser.getInstance().getID());
        popularity /= ratedParticipantsIDs.size();
        ShelfCollection.updateShelf(this);
        return true;
    }
}
