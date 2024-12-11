package model;
//
//messages [{
//prev, (reply değilse previ kendisi)
//root, (reply değilse rootu kendisi)
//timestamp,
//sender (username),
//senderID
//content,

import org.bson.types.ObjectId;

public class Message {
    private final String timestamp;
    private final String sender;
    private final ObjectId senderID;
    private final String content;
    public Message(String timestamp, String sender, ObjectId senderID, String content) {
        this.timestamp = timestamp;
        this.sender = sender;
        this.senderID = senderID;
        this.content = content;
    }

    public String toString() {
        return "Message: <("
                + timestamp + " "
                + sender + " "
                + senderID + ") => "
                + content + ">";
    }

    public ObjectId getSenderID() {
        return senderID;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getSender() {
        return sender;
    }

    public String getContent() {
        return content;
    }
}
