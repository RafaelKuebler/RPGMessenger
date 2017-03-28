package com.rafael.rpg.messengerclasses;

import java.util.ArrayList;
import java.util.List;

public class Group {
    String groupID = "";
    String title = "";
    List<String> memberIDs = new ArrayList<>();

    public Group(){
        // default constructor required for firebase database
    }

    public Group(String groupID, String title){
        this.groupID = groupID;
        this.title = title;
    }

    public String getGroupID(){
        return groupID;
    }

    public String getTitle(){
        return title;
    }

    public List<String> getMemberIDs(){
        return memberIDs;
    }
}
