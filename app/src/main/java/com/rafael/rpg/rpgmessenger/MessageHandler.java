package com.rafael.rpg.rpgmessenger;

import com.rafael.rpg.diceroller.DiceRoller;

/**
 * Handles the message according to its content and escape messages (e.g. \roll xxx to perform a dice roll).
 */
public abstract class MessageHandler {

    public static String handle(String message){
        String splitMessage[] = message.split(" ");
        switch(splitMessage[0]){
            case "\\roll":
                return rollDice(splitMessage[1]);
            default:
                return message;
        }
    }

    private static String rollDice(String message){
        String rollResult = DiceRoller.roll(message);

        if(rollResult != null){
            return (message + " : " + rollResult);
        }
        return message;
    }
}
