package com.caoc.marketplace.database.model;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity
public class Product {

    @PrimaryKey(autoGenerate = true)
    private long id;
    private String name;
    private String description;
    private String image;
    private String price;
    private String key;
    private int count;
    private String user;

    @Ignore
    public Product(String name, String description, String image, String price) {
        this.name = name;
        this.description = description;
        this.image = image;
        this.price = price;
    }

    public Product(String name, String description, String image, String price, String user) {
        this.name = name;
        this.description = description;
        this.image = image;
        this.price = price;
        this.user = user;
    }

    @Ignore
    public Product(){
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", image='" + image + '\'' +
                ", price='" + price + '\'' +
                ", key='" + key + '\'' +
                ", count=" + count +
                '}';
    }
}
