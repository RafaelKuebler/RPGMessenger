package com.rafael.rpg.diceroller;

import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DiceRoller {
    private static Pattern diceNotation = Pattern.compile("([1-9]\\d*)?d([1-9]\\d*)([+]\\d+)?");

    public int roll(String description) {
        int amount;
        int die;
        int add;

        Matcher matcher = diceNotation.matcher(description);
        if (matcher.matches()) {
            amount = (matcher.group(1) != null) ? Integer.parseInt(matcher.group(1)) : 1;
            die = Integer.parseInt(matcher.group(2));
            add = (matcher.group(3) != null) ? Integer.parseInt(matcher.group(3).substring(1)) : 0;

            return calculateRoll(amount, die, add);
        } else {
            return -1;
        }
    }

    private int calculateRoll(int amount, int die, int add) {
        int random = 0;
        Random randomGenerator = new Random();

        for(int i = 0; i < amount; i++){
            random += randomGenerator.nextInt(die) + add + 1;
        }

        return random;
    }
}
