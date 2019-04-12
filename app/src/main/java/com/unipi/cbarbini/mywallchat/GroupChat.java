package com.unipi.cbarbini.mywallchat;

import java.util.Date;

public class GroupChat {

//Model Class for a message
    private  String user;
    private  String message;
    private  long time;

    public GroupChat(String user, String message) {

        this.user= user;
        this.message=message;
        time = new Date().getTime();
    }
    public GroupChat(){}

    public  String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public   String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public   long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }}
