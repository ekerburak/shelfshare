package model;

import java.util.ArrayList;

public class Chat implements ChatListener {
    private final String ID;
    private final ArrayList<Message> messages;
    private final ArrayList<ChatListener> listeners;

    protected Chat(String ID, ArrayList<Message> messages) {
        this.ID = ID;
        listeners = new ArrayList<>();
        this.messages = messages;
        listeners.add(this);
    }

    @Override
    public void onIncomingMessage(Message msg) {
        messages.add(msg);
    }

    public void startListening() {
        ChatCollection.setChangeStream(this);
    }

    protected void notifyListeners(Message msg) {
        for (ChatListener listener : listeners) {
            listener.onIncomingMessage(msg);
        }
    }


    public String getID() {
        return ID;
    }

    public ArrayList<Message> getAllMessages() {
        return messages;
    }

    public void sendMessage(String content) {
        Message msg = new Message(
                Long.valueOf(System.currentTimeMillis()).toString(),
                LoggedInUser.getInstance().getUsername(),
                LoggedInUser.getInstance().getID(),
                content
        );
        ChatCollection.sendMessageUtil(ID, msg);
        messages.add(msg);
    }

    public void addChatListener(ChatListener listener) {
        listeners.add(listener);
    }
}
