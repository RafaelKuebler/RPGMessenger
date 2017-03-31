package com.rafael.rpg.diceroller;

import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DiceRoller {
    private static Pattern diceNotation = Pattern.compile("([1-9]\\d*)?d([1-9]\\d*)");
    private static Pattern digit = Pattern.compile("(\\d+)");

    public String roll(String description) {
        String rolls[] = description.split("[+]");
        String totalString = "";
        int total = 0;

        for(String rollDescription : rolls){
            Matcher diceNotationMatcher = diceNotation.matcher(rollDescription);
            Matcher digitMatcher = digit.matcher(rollDescription);

            if (diceNotationMatcher.matches()) {
                // if in dice notation
                int amount = (diceNotationMatcher.group(1) != null) ? Integer.parseInt(diceNotationMatcher.group(1)) : 1;
                int die = Integer.parseInt(diceNotationMatcher.group(2));

                int rollResult = calculateRoll(amount, die);
                total += rollResult;

                // modify output string
                if(!totalString.equals("")) totalString += "+";
                totalString = totalString + String.valueOf(rollResult);

            } else if(digitMatcher.matches()){
                // if just a digit
                total += Integer.parseInt(digitMatcher.group(1));

                // modify output string
                if(!totalString.equals("")) totalString += "+";
                totalString = totalString + digitMatcher.group(1);
            } else {
                return null;
            }
        }

        return totalString + "=" + String.valueOf(total);
    }

    private int calculateRoll(int amount, int die) {
        int random = 0;
        Random randomGenerator = new Random();

        for (int i = 0; i < amount; i++) {
            random += randomGenerator.nextInt(die) + 1;
        }

        return random;
    }
}
