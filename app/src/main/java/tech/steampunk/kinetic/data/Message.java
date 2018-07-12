package tech.steampunk.kinetic.data;

import android.net.Uri;

/**
 * Created by Vamshi on 9/9/2017.
 */

public class Message {

    private String Message;
    private String Time;
    private String Type;
    private String number;
    private String url;
    private String mood;

    public String getMood() {
        return mood;
    }

    public void setMood(String mood) {
        this.mood = mood;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public Message(String message, String time, String number, String type, Boolean c) {
        this.Message = message;
        this.Time = time;
        this.number = number;
        this.Type = type;
    }

    public Message(String message, String time, String number, String type, String mood) {
        this.Message = message;
        this.Time = time;
        this.number = number;
        this.Type = type;
        this.mood = mood;
    }

    public Message(String url, String time, String number, String type) {
        this.url = url;
        this.Time = time;
        this.number = number;
        this.Type = type;
    }
    public Message(){

    }
}
