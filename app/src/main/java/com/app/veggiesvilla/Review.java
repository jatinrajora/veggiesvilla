package com.app.veggiesvilla;

public class Review {

    private String itemId;
    private String username;
    private String text;
    private String date;

    public Review() {
    }

    public Review(String itemId, String username, String text, String date) {
        this.itemId = itemId;
        this.username = username;
        this.text = text;
        this.date = date;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "Review{" +
                ", itemId='" + itemId + '\'' +
                ", username='" + username + '\'' +
                ", text='" + text + '\'' +
                ", date='" + date + '\'' +
                '}';
    }
}
