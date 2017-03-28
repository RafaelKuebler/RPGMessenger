package com.rafael.rpg.messengerclasses;

import java.util.ArrayList;
import java.util.List;

public class User {
    private String name;
    private List<String> groups = new ArrayList<>();

    public User(){
        // default constructor required for firebase database
    }

    public User(String name){
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public List<String> getGroups() {
        return groups;
    }
}
