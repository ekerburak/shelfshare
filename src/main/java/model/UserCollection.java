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
import com.mongodb.client.model.Updates;
import org.bson.Document;
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
                mongoUser.get("_id").toString(),
                mongoUser.getString("email"),
                mongoUser.getString("username"),
                mongoUser.getInteger("profilePictureOption"),
                mongoUser.getString("about")
        );
    }
    protected static User getUser(String userID) {
        ObjectId documentID = new ObjectId(userID);
        Document mongoUser = collection.find(
                Filters.eq("_id", documentID)
        ).first();
        if(mongoUser == null) {
            throw new RuntimeException("Getting user by id failed");
        }
        return convertMongoUserToUser(mongoUser);
    }
    protected static void updateLoggedInUser() {
        LoggedInUser user = LoggedInUser.getInstance();
        collection.updateOne(
                new Document().append("_id", new ObjectId(user.getID())),
                Updates.combine(
                        Updates.set("username", user.getUsername()),
                        Updates.set("email", user.getEmail()),
                        Updates.set("about", user.getAbout()),
                        Updates.set("profilePictureOption", user.getProfilePictureOption()),
                        Updates.set("addedShelvesIDs", LoggedInUser.getAddedShelvesIDs())
                )
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
                mongoUser.get("_id").toString(),
                mongoUser.getString("email"),
                mongoUser.getString("username"),
                mongoUser.getInteger("profilePictureOption"),
                mongoUser.getString("about"),
                mongoUser.getList("addedShelvesIDs", String.class).toArray(new String[0])
        );
        return true;
    }

    public static void main(String[] args) {
        setup();
        System.out.println(getUser("6752c7e785056b3e0756ae25"));

    }
}