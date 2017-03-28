package com.rafael.rpg.messengerclasses;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Group {
    private String title = "";
    private List<String> memberIDs = new ArrayList<>();

    public Group(){
        // default constructor required for firebase database
    }

    public Group(String title){
        this.title = title;
    }

    public String getTitle(){
        return title;
    }

    public List<String> getMemberIDs(){
        return memberIDs;
    }

    public void addMember(String member){
        if(!memberIDs.contains(member)){
            memberIDs.add(member);
        }
    }
}
