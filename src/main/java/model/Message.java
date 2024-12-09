package model;
//
//messages [{
//prev, (reply değilse previ kendisi)
//root, (reply değilse rootu kendisi)
//timestamp,
//sender (username),
//senderID
//content,

public class Message {
    private String timestamp;
    private String sender;
    private String senderID;
    private String content;
    public Message(String timestamp, String sender, String senderID, String content) {
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

    public String getSenderID() {
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
