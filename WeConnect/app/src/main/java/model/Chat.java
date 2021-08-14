package model;

public class Chat {

    String sender;
    String receiver;
    String message;
    String id;

    public Chat() {
    }

    public Chat(String sender, String receiver, String message,String id) {
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
        this.id=id;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}