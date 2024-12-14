package model;

/*
    email
    username
    about
    password
    profilePictureOption
    addedShelves
* */

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.Updates;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.util.ArrayList;


public class UserCollection {
    private static MongoCollection<Document> collection;

    public static void setup() {
        if(collection != null) {
            throw new RuntimeException("Collection already set");
        }
        collection = DatabaseConnector.getCollection("user");
    }

    private static User convertMongoUserToUser(Document mongoUser) {
        return new User(
                mongoUser.getObjectId("_id"),
                mongoUser.getString("email"),
                mongoUser.getString("username"),
                mongoUser.getInteger("profilePictureOption"),
                mongoUser.getString("about")
        );
    }
    protected static User getUser(ObjectId userID) {
        Document mongoUser = collection.find(
                Filters.eq("_id", userID)
        ).first();
        if(mongoUser == null) {
            throw new RuntimeException("Getting user by id failed");
        }
        return convertMongoUserToUser(mongoUser);
    }
    protected static void updateLoggedInUser() {
        LoggedInUser user = LoggedInUser.getInstance();
        collection.updateOne(
                new Document().append("_id", user.getID()),
                Updates.combine(
                        Updates.set("username", user.getUsername()),
                        Updates.set("email", user.getEmail()),
                        Updates.set("about", user.getAbout()),
                        Updates.set("profilePictureOption", user.getProfilePictureOption()),
                        Updates.set("addedShelvesIDs", LoggedInUser.getAddedShelvesIDs())
                )
        );
    }

    protected static void removeFromAddedShelves(ObjectId userID, ObjectId shelfID) {
        Bson filter;
        if(userID != null) {
            filter = Filters.eq("_id", userID);
        } else {
            filter = Filters.in("addedShelvesIDs", shelfID);
        }
        collection.updateMany(
                filter,
                Updates.pull("addedShelvesIDs", shelfID)
        );
    }

    //returns false if signup fails (fails when email or username is taken)
    //does NOT automatically log in.
    public static boolean signUp(String email, String username, String password) {
        if(LoggedInUser.isLoggedIn()) {
            throw new RuntimeException("Cannot sign up while logged in");
        }
        long count = collection.countDocuments(
                Filters.or(
                        Filters.eq("email", email),
                        Filters.eq("username", username)
                )
        );
        if(count != 0) {
            return false;
        }
        collection.insertOne(new Document()
                .append("email", email)
                .append("username", username)
                .append("password", password)
                .append("profilePictureOption", 0)
                .append("about", "lorem ipsum")
                .append("addedShelvesIDs", new ArrayList<String>())
        );
        return true;
    }
    public static boolean logIn(String email, String password) {
        Document mongoUser = collection.find(
                Filters.and(
                        Filters.eq("email", email),
                        Filters.eq("password", password)
                )
        ).first();
        if(mongoUser == null) {
            return false;
        }
        LoggedInUser.createInstance(
                mongoUser.getObjectId("_id"),
                mongoUser.getString("email"),
                mongoUser.getString("username"),
                mongoUser.getInteger("profilePictureOption"),
                mongoUser.getString("about"),
                mongoUser.getList("addedShelvesIDs", ObjectId.class).toArray(new ObjectId[0])
        );
        return true;
    }

    protected static boolean checkPassword(String claimedPassword) {
        String realPassword = collection.find(Filters.eq("_id", LoggedInUser.getInstance().getID()))
                .projection(Projections.include("password"))
                .first().getString("password");
        return claimedPassword.equals(realPassword);
    }

    protected static void updatePassword(String newPassword) {
        collection.updateOne(
                Filters.eq("_id", LoggedInUser.getInstance().getID()),
                Updates.set("password", newPassword)
        );
    }

    public static void main(String[] args) {
        setup();
    }
}