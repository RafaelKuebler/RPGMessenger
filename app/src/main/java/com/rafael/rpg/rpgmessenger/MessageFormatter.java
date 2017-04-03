package com.rafael.rpg.rpgmessenger;

import android.support.v7.app.AppCompatActivity;

import com.rafael.rpg.dbwrappers.Message;

/**
 * Formats the passed message with HTML tags depending on the sender and type
 */
public abstract class MessageFormatter {

    public static Message format(Message message) {
        return message;
    }
}
