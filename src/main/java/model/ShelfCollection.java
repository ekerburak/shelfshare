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
                mongoShelf.get("_id").toString(),
                mongoShelf.getString("name"),
                mongoShelf.getBoolean("isPublic"),
                mongoShelf.getBoolean("allowBookAdd"),
                mongoShelf.getBoolean("allowBookAnnotate"),
                mongoShelf.getBoolean("allowDiscussion"),
                mongoShelf.getBoolean("allowInvitation"),
                mongoShelf.getInteger("popularity"),
                mongoShelf.getString("adminInvitation"),
                mongoShelf.getString("standardInvitation"),
                mongoShelf.getString("forumChatID"),
                new ArrayList<String>(mongoShelf.getList("addedBooksIDs", String.class)),
                new ArrayList<String>(mongoShelf.getList("participantsIDs", String.class)),
                new ArrayList<String>(mongoShelf.getList("adminsIDs", String.class))
        );
    }

    protected static Shelf[] getShelvesWithIDs(String[] IDs) {
        ArrayList<ObjectId> objectIDs = new ArrayList<>(IDs.length);
        for(String ID : IDs) {
            objectIDs.add(new ObjectId(ID));
        }
        FindIterable<Document> mongoShelves = collection.find(
                Filters.in("_id", objectIDs)
        );
        ArrayList<Shelf> shelves = new ArrayList<Shelf>();
        for(Document mongoShelf: mongoShelves) {
            shelves.add(convertMongoShelfToShelf(mongoShelf));
        }
        if(shelves.size() != IDs.length) {
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

    protected static void updateShelf(Shelf shelf) {
        collection.updateOne(
                new Document().append("_id", new ObjectId(shelf.getID())),
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
                        Updates.set("adminsIDs", shelf.getAdminsIDs())
                )
        );
    }

    public static Shelf createShelf(
            String name,
            boolean isPublic,
            boolean allowBookAdd,
            boolean allowBookAnnotate,
            boolean allowDiscussion,
            boolean allowInvitation) {
        String forumChatID = ChatCollection.createChat();
        Document mongoShelf = new Document()
                .append("name", name)
                .append("isPublic", isPublic)
                .append("allowBookAdd", allowBookAdd)
                .append("allowBookAnnotate", allowBookAnnotate)
                .append("allowDiscussion", allowDiscussion)
                .append("allowInvitation", allowInvitation)
                .append("popularity", 0)
                .append("adminInvitation", UUID.randomUUID().toString().replace("-", "").substring(0, 6).toUpperCase())
                .append("standardInvitation", UUID.randomUUID().toString().replace("-", "").substring(0, 6).toUpperCase())
                .append("addedBooksIDs", new ArrayList<String>())
                .append("participantsIDs", new ArrayList<String>())
                .append("adminsIDs", new ArrayList<String>())
                .append("forumChatID", forumChatID);
        collection.insertOne(mongoShelf);
        LoggedInUser.joinShelf(mongoShelf.get("adminInvitation").toString());
        return convertMongoShelfToShelf(mongoShelf);
    }

    public static void deleteShelf(String shelfID) {
        DeleteResult result = collection.deleteOne(
                new Document().append("_id", new ObjectId(shelfID)));
        if(result.getDeletedCount() == 0) {
            throw new RuntimeException("Invalid shelf id");
        }
    }

    private static class PopularityComparator implements Comparator<Shelf> {
        @Override
        public int compare(Shelf s1, Shelf s2) {
            return s2.getPopularity() - s1.getPopularity();
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
        FindIterable<Document> allMongoShelves = collection.find(
                Filters.eq("isPublic", true)
        );
        ArrayList<Shelf> sortList = new ArrayList<Shelf>();
        for(Document mongoShelf: allMongoShelves) {
            sortList.add(convertMongoShelfToShelf(mongoShelf));
        }
        sortList.sort(new PopularityComparator());
        return new ArrayList<Shelf>(sortList.subList(0, Math.min(limit, sortList.size())));
    }
}