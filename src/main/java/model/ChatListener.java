package model;

public interface ChatListener {
    //USE Platform.runLater INSIDE FOR JAVAFX OPERATIONS FOR THREAD-SAFETY!
    void onIncomingMessage(Message msg);
}
