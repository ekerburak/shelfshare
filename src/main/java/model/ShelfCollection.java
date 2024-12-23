package model;

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
ratedParticipants
* */

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.DeleteResult;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.*;

public class ShelfCollection {
    private static MongoCollection<Document> collection;

    public static void setup() {
        if(collection != null) {
            throw new RuntimeException("Collection already set");
        }
        collection = DatabaseConnector.getCollection("shelf");
    }

    private static Shelf convertMongoShelfToShelf(Document mongoShelf) {
        return new Shelf(
                mongoShelf.getObjectId("_id"),
                mongoShelf.getString("name"),
                mongoShelf.getBoolean("isPublic"),
                mongoShelf.getBoolean("allowBookAdd"),
                mongoShelf.getBoolean("allowBookAnnotate"),
                mongoShelf.getBoolean("allowDiscussion"),
                mongoShelf.getBoolean("allowInvitation"),
                mongoShelf.getDouble("popularity"),
                mongoShelf.getString("adminInvitation"),
                mongoShelf.getString("standardInvitation"),
                mongoShelf.getObjectId("forumChatID"),
                new ArrayList<ObjectId>(mongoShelf.getList("addedBooksIDs", ObjectId.class)),
                new ArrayList<ObjectId>(mongoShelf.getList("participantsIDs", ObjectId.class)),
                new ArrayList<ObjectId>(mongoShelf.getList("adminsIDs", ObjectId.class)),
                new ArrayList<ObjectId>(mongoShelf.getList("ratedParticipantsIDs", ObjectId.class))
        );
    }

    protected static Shelf[] getShelvesWithIDs(ObjectId[] IDs) {
        ArrayList<ObjectId> objectIDs = new ArrayList<>(IDs.length);
        Collections.addAll(objectIDs, IDs);
        FindIterable<Document> mongoShelves = collection.find(
                Filters.in("_id", objectIDs)
        );
        ArrayList<Shelf> shelves = new ArrayList<Shelf>();
        for(Document mongoShelf: mongoShelves) {
            shelves.add(convertMongoShelfToShelf(mongoShelf));
        }
        if(shelves.size() != IDs.length) {
            System.out.println(shelves + " " + Arrays.toString(IDs));
            throw new RuntimeException("Some IDs do not correspond to concrete documents");
        }
        return shelves.toArray(new Shelf[0]);
    }
    //call from LoggedInUser.joinShelf
    protected static Shelf getShelfWithInvitation(String invitation) {
        Document mongoShelf = collection.find(
                Filters.or(
                        Filters.eq("adminInvitation", invitation),
                        Filters.eq("standardInvitation", invitation)
                )
        ).first();
        if(mongoShelf == null) {
            throw new IllegalStateException("No shelf with invitation " + invitation);
        }
        return convertMongoShelfToShelf(mongoShelf);
    }

    private static final Object lock = new Object();

    protected static void updateShelf(Shelf shelf) {
       synchronized (lock) {
           System.out.println("Updating shelf " + shelf);
           collection.updateOne(
                   new Document().append("_id", shelf.getID()),
                   Updates.combine(
                           Updates.set("name", shelf.getName()),
                           Updates.set("isPublic", shelf.getIsPublic()),
                           Updates.set("allowBookAdd", shelf.getAllowBookAdd()),
                           Updates.set("allowBookAnnotate", shelf.getAllowBookAnnotate()),
                           Updates.set("allowDiscussion", shelf.getAllowDiscussion()),
                           Updates.set("allowInvitation", shelf.getAllowInvitation()),
                           Updates.set("popularity", shelf.getPopularity()),
                           Updates.set("adminInvitation", shelf.getAdminInvitation()),
                           Updates.set("standardInvitation", shelf.getStandardInvitation()),
                           Updates.set("forumChatID", shelf.getForumChatID()),
                           Updates.set("addedBooksIDs", shelf.getAddedBooksIDs()),
                           Updates.set("participantsIDs", shelf.getParticipantsIDs()),
                           Updates.set("adminsIDs", shelf.getAdminsIDs()),
                           Updates.set("ratedParticipantsIDs", shelf.getRatedParticipantsIDs())
                   )
           );
       }
    }

    public static Shelf createShelf(
            String name,
            boolean isPublic,
            boolean allowBookAdd,
            boolean allowBookAnnotate,
            boolean allowDiscussion,
            boolean allowInvitation) {
        ObjectId forumChatID = ChatCollection.createChat();
        Document mongoShelf = new Document()
                .append("name", name)
                .append("isPublic", isPublic)
                .append("allowBookAdd", allowBookAdd)
                .append("allowBookAnnotate", allowBookAnnotate)
                .append("allowDiscussion", allowDiscussion)
                .append("allowInvitation", allowInvitation)
                .append("popularity", 0.0)
                .append("adminInvitation", UUID.randomUUID().toString().replace("-", "").substring(0, 6).toUpperCase())
                .append("standardInvitation", UUID.randomUUID().toString().replace("-", "").substring(0, 6).toUpperCase())
                .append("addedBooksIDs", new ArrayList<ObjectId>())
                .append("participantsIDs", new ArrayList<ObjectId>())
                .append("adminsIDs", new ArrayList<ObjectId>())
                .append("forumChatID", forumChatID)
                .append("ratedParticipantsIDs", new ArrayList<ObjectId>());
        collection.insertOne(mongoShelf);
        LoggedInUser.joinShelf(mongoShelf.get("adminInvitation").toString());
        return convertMongoShelfToShelf(mongoShelf);
    }

    public static void deleteShelf(ObjectId shelfID) {
        Shelf shelfToDelete = getShelvesWithIDs(new ObjectId[]{shelfID})[0];
        LoggedInUser.getAddedShelvesIDs().remove(shelfToDelete.getID());
        UserCollection.removeFromAddedShelves(null, shelfToDelete.getID());
        DeleteResult result = collection.deleteOne(
                new Document().append("_id", shelfID));
        if(result.getDeletedCount() == 0) {
            throw new RuntimeException("Invalid shelf id");
        }
    }


    private static void manualSort(ArrayList<Shelf> shelves) {
        //bubble sort
        for(int i = 0; i < shelves.size(); i++)
            for(int j = 0; j < shelves.size(); j++)
                if(shelves.get(i).getPopularity() < shelves.get(j).getPopularity()) {
                    Shelf temp = shelves.get(i);
                    shelves.set(i, shelves.get(j));
                    shelves.set(j, temp);
                }

    }

    //THE REQUIREMENT OF USING SORT IS SATISFIED HERE
    /**
     *
     * @param limit
     * @return the first {@code limit} most popular public shelves in descending order on
     * the basis of their popularity.
     */
    public static ArrayList<Shelf> getRecommendedShelves(int limit) {
        FindIterable<Document> allMongoShelves;
        if(!LoggedInUser.isLoggedIn()) {
            allMongoShelves = collection.find(
                    Filters.eq("isPublic", true)
            );
        } else {
            allMongoShelves = collection.find(
                    Filters.and(
                            Filters.eq("isPublic", true),
                            Filters.nin("participantsIDs", LoggedInUser.getInstance().getID())
                    )
            );
        }
        ArrayList<Shelf> sortList = new ArrayList<Shelf>();
        for(Document mongoShelf: allMongoShelves) {
            sortList.add(convertMongoShelfToShelf(mongoShelf));
        }
        manualSort(sortList);
        return new ArrayList<Shelf>(sortList.subList(0, Math.min(limit, sortList.size())));
    }

}