package com.rafael.rpg.dbwrappers;

public class Message {
    private String text;
    private String author;
    private int style;

    public Message() {
    }

    public Message(String text, String author, int style) {
        this.text = text;
        this.author = author;
        this.style = style;
    }

    public String getText() {
        return text;
    }

    public String getAuthor() {
        return author;
    }

    public int getStyle() {
        return style;
    }
}
