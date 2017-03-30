package com.rafael.rpg.dbwrappers;

import java.util.ArrayList;
import java.util.List;

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
