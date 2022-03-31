package de.lacodev.staffbungee.objects;

public class Message {

    Integer id;
    String sender;
    String message;
    String timestamp;

    public Message(Integer id, String sender, String message, String timestamp) {
        super();
        this.id = id;
        this.sender = sender;
        this.message = message;
        this.timestamp = timestamp;
    }

    public Integer getId() {
        return id;
    }

    public String getSender() {
        return sender;
    }

    public String getMessage() {
        return message;
    }

    public String getTimestamp() {
        return timestamp;
    }

}
