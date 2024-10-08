package com.newtry.myapplication.Models;

public class MessageModel {

    String uId,message,messageId;

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    long timestamp;

    public MessageModel(String uId, String message, long timestamp,String messageId) {
        this.uId = uId;
        this.message = message;
        this.timestamp = timestamp;
        this.messageId=messageId;
    }

    public MessageModel(String uId, String message) {
        this.uId = uId;
        this.message = message;
    }

    public MessageModel() {
    }

    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
    @Override
    public String toString(){

        return uId+" "+message;
    }
}
