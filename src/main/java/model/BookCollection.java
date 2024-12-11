package model;

import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoIterable;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.Updates;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

/*
isDownloadable
pageCount
discussion (chat)

pages [{
    image: string (base64)
    highlightCoordinates: []
    highlightColors: []
    lineCoordinates: []
    lineColorStrings: []
}]
* */


public class BookCollection {
    private static MongoCollection<Document> collection;

    public static void setup() {
        if(collection != null) {
            throw new RuntimeException("Collection already set");
        }
        collection = DatabaseConnector.getCollection("book");
    }

    /**
     *  private final String ID;
     *     private String name;
     *     private final String uploaderName;
     *     private boolean isDownloadable;
     *     private final int pageCount; //pages are numbered from 0 to pageCount - 1 inclusive
     *     private final String discussionChatID;
     * @param mongoBook
     * @return
     */

    private static Book convertMongoBookToBook(Document mongoBook) {
        return new Book(
                mongoBook.getObjectId("_id"),
                mongoBook.getString("name"),
                mongoBook.getString("uploaderName"),
                mongoBook.getBoolean("isDownloadable"),
                mongoBook.getInteger("pageCount"),
                mongoBook.getObjectId("discussionChatID")
        );
    }

    protected static ArrayList<Book> getAddedBooksByIDs(ArrayList<ObjectId> IDs) {
        ArrayList<ObjectId> objectIDs = new ArrayList<>();
        for(ObjectId ID : IDs) {
            objectIDs.add(ID);
        }
        MongoIterable<Document> mongoBooks = collection.find(
                Filters.in("_id", objectIDs)
        );
        ArrayList<Book> books = new ArrayList<Book>();
        for(Document mongoBook : mongoBooks) {
            books.add(convertMongoBookToBook(mongoBook));
        }
        return books;
    }

    //can't implement a generic updateBook method, because book objects are too large
    protected static void updatePage(Book book, Page page) {
        Document mongoPage = new Document()
                .append("image", page.getImage())
                .append("highlightCoordinates", page.getHighlightCoordinates())
                .append("highlightColorStrings", page.getHighlightColorStrings())
                .append("lineCoordinates", page.getLineCoordinates())
                .append("lineColorStrings", page.getLineColorStrings());
        collection.updateOne(
                new Document().append("_id", book.getID()),
                Updates.set("pages." + page.getPageNumber() + ".content", mongoPage)
        );
    }


    protected static void updateProperties(Book book) {
        collection.updateOne(
                new Document().append("_id", book.getID()),
                Updates.combine(
                        Updates.set("isDownloadable", book.getIsDownloadable())
                        //ADD CHAT ID HERE!!
                )
        );
    }

    //gets all the pages from pageLow to pageHigh (inclusive)
    protected static ArrayList<Page> getPages(ObjectId bookID, int pageLow, int pageHigh) {
        Document mongoBook = collection.aggregate(List.of(
                Filters.eq("_id", bookID),
                Projections.slice("pages", pageLow, pageHigh - pageLow + 1)
        )).first();

        if(mongoBook == null) {
            throw new RuntimeException("Book not found");
        }

        ArrayList<Document> mongoPages = new ArrayList<>(mongoBook.getList("pages", Document.class));

        if(mongoPages.size() != pageHigh - pageLow + 1) {
            throw new IllegalArgumentException("Invalid page range");
        }

        ArrayList<Page> pages = new ArrayList<Page>();

        for(int i = 0; i < mongoPages.size(); i++) {
            pages.add(new Page(mongoPages.get(i), i));
        }

        return pages;
    }

    //returns the mongo id of the added book
    protected static ObjectId addBook(
            boolean isDownloadable,
            String[] pageImages
    ) {
        if(pageImages.length == 0) {
            throw new IllegalArgumentException("Pages cannot be empty");
        }
        ArrayList<Document> mongoPages = new ArrayList<Document>();
        for(int i = 0; i < pageImages.length; i++) {
            Page page = new Page(pageImages[i], i);
            Document mongoPage = new Document()
                    .append("image", page.getImage())
                    .append("highlightCoordinates", page.getHighlightCoordinates())
                    .append("highlightColorStrings", page.getHighlightColorStrings())
                    .append("lineCoordinates", page.getLineCoordinates())
                    .append("lineColorStrings", page.getLineColorStrings());
            mongoPages.add(mongoPage);
        }
        ObjectId discussionChatID = ChatCollection.createChat();
        Document mongoBook = new Document()
                .append("isDownloadable", isDownloadable)
                .append("pages", mongoPages)
                .append("pageCount", pageImages.length)
                .append("discussionChatID", discussionChatID); //MUST CHANGE WITH OBJECTID
        collection.insertOne(mongoBook);
        return mongoBook.getObjectId("_id");
    }

    protected static void deleteBook(ObjectId bookID) {
        collection.deleteOne(new Document("_id", bookID));
    }
}
