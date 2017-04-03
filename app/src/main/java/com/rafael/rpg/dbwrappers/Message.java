package com.rafael.rpg.dbwrappers;

public class Message {
    private String text;
    private String sender;
    private int style;

    public Message() {
    }

    public Message(String text, String author, int style) {
        this.text = text;
        this.sender = author;
        this.style = style;
    }

    public String getText() {
        return text;
    }

    public String getSender() {
        return sender;
    }

    public int getStyle() {
        return style;
    }
}
