package com.app.veggiesvilla;

import android.os.Parcel;
import android.os.Parcelable;

public class Item implements Parcelable {

    String id;
    String name;
    String description;
    String imageUrl;
    String category;
    double price;
    int quantity = 1;
    double overallItemPrice;

    public Item() {
    }

    public Item(String id, String name, String description, String imageUrl, String category, double price, int quantity, double overallItemPrice) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.imageUrl = imageUrl;
        this.category = category;
        this.price = price;
        this.quantity = quantity;
        this.overallItemPrice = overallItemPrice;
    }

    public Item(String id, String name, double price, int quantity, double overallItemPrice) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.overallItemPrice = overallItemPrice;
    }

    protected Item(Parcel in) {
        id = in.readString();
        name = in.readString();
        description = in.readString();
        imageUrl = in.readString();
        category = in.readString();
        price = in.readDouble();
        quantity = in.readInt();
        overallItemPrice = in.readDouble();
    }

    public static final Creator<Item> CREATOR = new Creator<Item>() {
        @Override
        public Item createFromParcel(Parcel in) {
            return new Item(in);
        }

        @Override
        public Item[] newArray(int size) {
            return new Item[size];
        }
    };

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public  String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getOverallItemPrice() {
        return overallItemPrice;
    }

    public void setOverallItemPrice(double overallItemPrice) {
        this.overallItemPrice = overallItemPrice;
    }

    @Override
    public String toString() {
        return "Item{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", category='" + category + '\'' +
                ", price=" + price +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(description);
        dest.writeString(imageUrl);
        dest.writeString(category);
        dest.writeDouble(price);
        dest.writeInt(quantity);
        dest.writeDouble(overallItemPrice);
    }
}
