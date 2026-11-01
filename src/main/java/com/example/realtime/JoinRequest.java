package com.example.realtime;

public class JoinRequest {
    private String room;
    private String name;

    public JoinRequest() { }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
