package model;

import com.mongodb.client.*;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.Updates;
import com.mongodb.client.model.changestream.ChangeStreamDocument;
import com.mongodb.client.model.changestream.UpdateDescription;
import org.bson.BsonDocument;
import org.bson.BsonValue;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/*
isDownloadable
pageCount
discussion (chat)

pages [{
    image: string (base64)
    highlightCoordinates: []
    lineCoordinates: []
}]
* */


public class BookCollection {
    private static MongoCollection<Document> collection;

    public static void setup() {
        if (collection != null) {
            throw new RuntimeException("Collection already set");
        }
        collection = DatabaseConnector.getCollection("book");
    }

    /**
     * private final String ID;
     * private String name;
     * private final String uploaderName;
     * private boolean isDownloadable;
     * private final int pageCount; //pages are numbered from 0 to pageCount - 1 inclusive
     * private final String discussionChatID;
     *
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
        ArrayList<ObjectId> objectIDs = new ArrayList<>(IDs);
        MongoIterable<Document> mongoBooks = collection.find(Filters.in("_id", objectIDs))
                .projection(Projections.exclude("pages"));
        ArrayList<Book> books = new ArrayList<Book>();
        for (Document mongoBook : mongoBooks) {
            books.add(convertMongoBookToBook(mongoBook));
        }
        return books;
    }

    //can't implement a generic updateBook method, because book objects are too large
    protected static void updatePage(Book book, Page page) {
        Document mongoPage = new Document()
                .append("image", page.getImage())
                .append("highlightCoordinates", page.getHighlightCoordinates())
                .append("lineCoordinates", page.getLineCoordinates());
        collection.updateOne(
                new Document().append("_id", book.getID()),
                Updates.set("pages." + page.getPageNumber() + ".content", mongoPage)
        );
    }


    protected static void updateProperties(Book book) {
        collection.updateOne(
                new Document().append("_id", book.getID()),
                Updates.combine(
                        Updates.set("isDownloadable", book.getIsDownloadable()),
                        Updates.set("name", book.getName())
                )
        );
    }

    //gets all the pages from pageLow to pageHigh (inclusive)
    protected static ArrayList<Page> getPages(ObjectId bookID, int pageLow, int pageHigh) {

        Document mongoBook = collection.find(Filters.eq("_id", bookID))
                .projection(Projections.fields(Projections.slice("pages", pageLow, pageHigh - pageLow + 1)))
                .first();

        if (mongoBook == null) {
            throw new RuntimeException("Book not found");
        }

        ArrayList<Document> mongoPages = new ArrayList<>(mongoBook.getList("pages", Document.class));

        if (mongoPages.size() != pageHigh - pageLow + 1) {
            throw new IllegalArgumentException("Invalid page range");
        }

        ArrayList<Page> pages = new ArrayList<Page>();

        for (int i = 0; i < mongoPages.size(); i++) {
            pages.add(new Page(mongoPages.get(i), i));
        }

        return pages;
    }

    //returns the mongo id of the added book
    protected static ObjectId addBook(
            String name,
            boolean isDownloadable,
            String[] pageImages
    ) {
        if (pageImages.length == 0) {
            throw new IllegalArgumentException("Pages cannot be empty");
        }
        ArrayList<Document> mongoPages = new ArrayList<Document>();
        for (int i = 0; i < pageImages.length; i++) {
            Page page = new Page(pageImages[i], i);
            Document mongoPage = new Document()
                    .append("image", page.getImage())
                    .append("highlightCoordinates", page.getHighlightCoordinates())
                    .append("lineCoordinates", page.getLineCoordinates());
            mongoPages.add(mongoPage);
        }
        ObjectId discussionChatID = ChatCollection.createChat();
        Document mongoBook = new Document()
                .append("name", name)
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

    protected static void addHighlightToPage(ObjectId bookID, int pageNumber, ArrayList<Integer> coordinate) {
        collection.updateOne(
                new Document().append("_id", bookID),
                Updates.combine(
                        Updates.push("pages." + pageNumber + ".highlightCoordinates", coordinate)
                )
        );
    }

    protected static void addUnderlineToPage(ObjectId bookID, int pageNumber, ArrayList<Integer> coordinate) {
        collection.updateOne(
                new Document().append("_id", bookID),
                Updates.combine(
                        Updates.push("pages." + pageNumber + ".lineCoordinates", coordinate)
                )
        );
    }

    protected static void addStickyToPage(ObjectId bookID, int pageNumber, StickyNote s) {
        collection.updateOne(
                new Document().append("_id", bookID),
                Updates.combine(
                        Updates.push("pages."+pageNumber+".stickyNotes", new Document()
                                .append("coordinate", s.getCoordinate())
                                .append("content", s.getContent())
                        )
                )
        );
    }

    protected static void removeHighlightFromPage(ObjectId bookID, int pageNumber, ArrayList<Integer> coordinate) {
        collection.updateOne(
                new Document().append("_id", bookID),
                Updates.combine(
                        Updates.pull("pages." + pageNumber + ".highlightCoordinates", coordinate)
                )
        );
    }

    protected static void removeUnderlineFromPage(ObjectId bookID, int pageNumber, ArrayList<Integer> coordinate) {
        collection.updateOne(
                new Document().append("_id", bookID),
                Updates.combine(
                        Updates.pull("pages." + pageNumber + ".lineCoordinates", coordinate)
                )
        );
    }

    private static int parseUpdatedPage(String updatedField) {
        //the page number is between the first and second dot
        int firstDotIndex = updatedField.indexOf(".");
        int secondDotIndex = updatedField.indexOf(".", firstDotIndex + 1);
        return Integer.parseInt(updatedField.substring(firstDotIndex + 1, secondDotIndex));

    }


    public static void setChangeStream(Book book) {

        System.out.println("are you even called?");
        new Thread(() -> {
            List<Bson> pipeline = Arrays.asList(
                    Aggregates.match(
                            Filters.and(
                                    Filters.eq("documentKey._id", book.getID()),
                                    Filters.eq("operationType", "update")
                            )
                    )
            );

            ChangeStreamIterable<Document> changeStream = collection.watch(pipeline);

            String regexHighlightCoordinateAdd = "pages\\.\\d+\\.highlightCoordinates\\.\\d+$";
            String regexHighlightCoordinateRemove = "pages\\.\\d+\\.highlightCoordinates$";

            String regexUnderlineCoordinateAdd = "pages\\.\\d+\\.lineCoordinates\\.\\d+$";
            String regexUnderlineCoordinateRemove = "pages\\.\\d+\\.lineCoordinates$";


            String regexStickyNoteAdd = "pages\\.\\d+\\.stickyNotes\\.\\d+$";
            String regexStickyNoteRemove = "pages\\.\\d+\\.stickyNotes$";

            try (MongoChangeStreamCursor<ChangeStreamDocument<Document>> cursor = changeStream.cursor()) {
                while (cursor.hasNext()) {

                    ChangeStreamDocument<Document> change = cursor.next();

                    UpdateDescription updateDescription = change.getUpdateDescription();

                    if (updateDescription == null || updateDescription.getUpdatedFields() == null) {
                        throw new RuntimeException("Unexpected mongo error");
                    }

                    BsonDocument updatedFields = updateDescription.getUpdatedFields();


                    final int NONE = 0, HIGHLIGHT = 1, UNDERLINE = 2, STICKY = 3;
                    int mode = NONE;
                    boolean isAdd = false;
                    int modifiedPageNumber = -1;
                    ArrayList<Integer> addedCoordinate = null;
                    ArrayList<ArrayList<Integer>> remainingCoordinates = null;
                    ArrayList<StickyNote> remainingStickyNotes = null;
                    String stickyContent = null;

                    for (String updatedKey : updatedFields.keySet()) {

                        System.out.println(updatedKey);

                        //HIGHLIGHT
                        if (updatedKey.matches(regexHighlightCoordinateAdd)) {
                            mode = HIGHLIGHT;
                            isAdd = true;
                            modifiedPageNumber = parseUpdatedPage(updatedKey);
                            addedCoordinate = new ArrayList<Integer>();
                            for (BsonValue coordinate : updatedFields.get(updatedKey).asArray()) {
                                addedCoordinate.add(coordinate.asInt32().getValue());
                            }
                        } else if (updatedKey.matches(regexHighlightCoordinateRemove)) {
                            mode = HIGHLIGHT;
                            isAdd = false;
                            modifiedPageNumber = parseUpdatedPage(updatedKey);
                            remainingCoordinates = new ArrayList<ArrayList<Integer>>();
                            for (BsonValue coordinate : updatedFields.get(updatedKey).asArray()) {
                                ArrayList<Integer> coordinateToAdd = new ArrayList<Integer>();
                                for (BsonValue coordinateData : coordinate.asArray()) {
                                    coordinateToAdd.add(coordinateData.asInt32().getValue());
                                }
                                remainingCoordinates.add(coordinateToAdd);
                            }

                        }

                        //UNDERLINE
                        if (updatedKey.matches(regexUnderlineCoordinateAdd)) {
                            mode = UNDERLINE;
                            isAdd = true;
                            modifiedPageNumber = parseUpdatedPage(updatedKey);
                            addedCoordinate = new ArrayList<Integer>();
                            for (BsonValue coordinate : updatedFields.get(updatedKey).asArray()) {
                                addedCoordinate.add(coordinate.asInt32().getValue());
                            }
                        } else if (updatedKey.matches(regexUnderlineCoordinateRemove)) {
                            mode = UNDERLINE;
                            isAdd = false;
                            modifiedPageNumber = parseUpdatedPage(updatedKey);
                            remainingCoordinates = new ArrayList<ArrayList<Integer>>();
                            for (BsonValue coordinate : updatedFields.get(updatedKey).asArray()) {
                                ArrayList<Integer> coordinateToAdd = new ArrayList<Integer>();
                                for (BsonValue coordinateData : coordinate.asArray()) {
                                    coordinateToAdd.add(coordinateData.asInt32().getValue());
                                }
                                remainingCoordinates.add(coordinateToAdd);
                            }

                        }

                        //STICKY
                        if (updatedKey.matches(regexStickyNoteAdd)) {
                            mode = STICKY;
                            isAdd = true;
                            modifiedPageNumber = parseUpdatedPage(updatedKey);
                            addedCoordinate = new ArrayList<Integer>();
                            for (BsonValue coordinate : updatedFields.get(updatedKey).asDocument().getArray("coordinate").asArray()) {
                                addedCoordinate.add(coordinate.asInt32().getValue());
                            }

                            stickyContent = updatedFields.get(updatedKey).asDocument().getString("content").getValue();
                        } else if (updatedKey.matches(regexStickyNoteRemove)) {
                            mode = STICKY;
                            isAdd = false;
                            modifiedPageNumber = parseUpdatedPage(updatedKey);
                            remainingStickyNotes = new ArrayList<StickyNote>();
                            for (BsonValue mongoSticky : updatedFields.get(updatedKey).asArray()) {
                                ArrayList<Integer> coordinateToAdd = new ArrayList<Integer>();
                                for (BsonValue coordinateData : mongoSticky.asDocument().getArray("coordinate").asArray()) {
                                    coordinateToAdd.add(coordinateData.asInt32().getValue());
                                }
                                String content = mongoSticky.asDocument().getString("content").getValue();
                                remainingStickyNotes.add(new StickyNote(coordinateToAdd, content));
                            }

                        }
                    }

                    if (mode == NONE) {
                        continue;
                    }

                    if (isAdd) {
                        assert addedCoordinate != null && modifiedPageNumber != -1;
                        if (mode == HIGHLIGHT) {
                            book.notifyPageHighlightAdded(modifiedPageNumber, addedCoordinate);
                        } else if (mode == UNDERLINE) {
                            book.notifyPageUnderlineAdded(modifiedPageNumber, addedCoordinate);
                        } else {
                            assert stickyContent != null;
                            book.notifyPageStickyAdded(modifiedPageNumber, new StickyNote(addedCoordinate, stickyContent));
                        }
                    } else {
                        assert remainingCoordinates != null && modifiedPageNumber != -1;
                        if (mode == HIGHLIGHT) {
                            book.notifyPageHighlightRemoved(modifiedPageNumber, remainingCoordinates);
                        } else if(mode == UNDERLINE) {
                            book.notifyPageUnderlineRemoved(modifiedPageNumber, remainingCoordinates);
                        } else {
                            book.notifyPageStickyRemoved(modifiedPageNumber, remainingStickyNotes);
                        }
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }


        }).start();


    }

    public static void main(String[] args) {
        setup();
        Book book = BookCollection.getAddedBooksByIDs(new ArrayList<ObjectId>(List.of(new ObjectId("675dc91bc16e4836de0f531d")))).get(0);
        //System.out.println(book.getName());
        book.startListening();
        //book.addHighlightToCurrentPage(new ArrayList<Integer>(List.of(1, 2, 3)));
        //book.addUnderlineToCurrentPage(new ArrayList<Integer>(List.of(1, 2, 3)));
    }

    public static void removeStickyFromPage(ObjectId id, int currentPageNumber, StickyNote s) {
        collection.updateOne(
                new Document().append("_id", id),
                Updates.combine(
                        Updates.pull("pages."+currentPageNumber+".stickyNotes", new Document()
                                .append("coordinate", s.getCoordinate())
                                .append("content", s.getContent())
                        )
                )
        );
    }
}
