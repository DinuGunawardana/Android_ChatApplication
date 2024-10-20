package com.example.android_chat_application;

public class Message {
    private String text;
    private String sender;
    private long timestamp;

    public Message() {
        // Firestore requires a no-argument constructor
    }

    public Message(String text, String sender, long timestamp) {
        this.text = text;
        this.sender = sender;
        this.timestamp = timestamp;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}

