package model;

/*
messages [{
    timestamp,
    sender (username),
    content,
}]
* */

import com.mongodb.client.ChangeStreamIterable;
import com.mongodb.client.MongoChangeStreamCursor;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.mongodb.client.model.changestream.ChangeStreamDocument;
import com.mongodb.client.model.changestream.UpdateDescription;
import com.mongodb.client.result.DeleteResult;
import org.bson.BsonDocument;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ChatCollection {
    private static MongoCollection<Document> collection;

    public static void setup() {
        collection = DatabaseConnector.getCollection("chat");
    }

    public static ObjectId createChat() {
        Document mongoChat = new Document()
                .append("messages", new ArrayList<Document>());
        collection.insertOne(mongoChat);
        return mongoChat.getObjectId("_id");
    }

    private static Chat convertMongoChatToChat(Document mongoChat) {
        List<Document> mongoMessageList = mongoChat.getList("messages", Document.class);
        ArrayList<Message> messages = new ArrayList<>();
        for(Document mongoMessage : mongoMessageList) {
            Message message = new Message(
                    mongoMessage.getString("timestamp"),
                    mongoMessage.getString("sender"),
                    mongoMessage.getObjectId("senderID"),
                    mongoMessage.getString("content")
            );
            messages.add(message);
        }
        return new Chat(
                mongoChat.getObjectId("_id"),
                messages
        );
    }

    public static Chat getChat(ObjectId chatID) {
        Document mongoChat = collection.find(
                Filters.eq("_id", chatID )
        ).first();
        if(mongoChat == null) {
            throw new IllegalArgumentException("Invalid chat id");
        }
        return convertMongoChatToChat(mongoChat);
    }

    private static Document convertMessageToMongoMessage(Message message) {
        return new Document()
                .append("timestamp", message.getTimestamp())
                .append("sender", message.getSender())
                .append("senderID", message.getSenderID())
                .append("content", message.getContent());
    }

    protected static void sendMessageUtil(ObjectId chatID, Message msg) {
        collection.updateOne(
                Filters.eq("_id", chatID),
                Updates.push("messages", convertMessageToMongoMessage(msg))
        );
    }

    //invoked ONLY when a shelf or book is deleted. cannot be invoked manually.
    protected static void deleteChat(ObjectId chatID) {
        DeleteResult result = collection.deleteOne(new Document("_id", chatID));
        if(result.getDeletedCount() == 0) {
            throw new IllegalArgumentException("Invalid chat id");
        }
    }

    protected static void setChangeStream(Chat chat) {
       new Thread(() -> {
           List<Bson> pipeline = Arrays.asList(
                   Aggregates.match(
                           Filters.and(
                                   Filters.eq("documentKey._id", chat.getID()),
                                   Filters.eq("operationType", "update")
                           )
                   )
           );

           ChangeStreamIterable<Document> changeStream = collection.watch(pipeline);

           try (MongoChangeStreamCursor<ChangeStreamDocument<Document>> cursor = changeStream.cursor()) {
               while (cursor.hasNext()) {

                   ChangeStreamDocument<Document> change = cursor.next();

                   UpdateDescription updateDescription = change.getUpdateDescription();

                   if(updateDescription == null || updateDescription.getUpdatedFields() == null) {
                       throw new RuntimeException("Unexpected mongo error");
                   }

                   BsonDocument updatedField = updateDescription.getUpdatedFields();

                   BsonDocument mongoMessage = updatedField.getDocument(updatedField.getFirstKey());

                   Message message = new Message(
                           mongoMessage.getString("timestamp").getValue(),
                           mongoMessage.getString("sender").getValue(),
                           mongoMessage.getObjectId("senderID").getValue(),
                           mongoMessage.getString("content").getValue()
                   );

                   chat.notifyListeners(message);


               }
           } catch (Exception e) {
               e.printStackTrace();
           }
       }).start();
    }
}
