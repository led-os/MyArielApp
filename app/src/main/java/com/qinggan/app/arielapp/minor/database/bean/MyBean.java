package com.qinggan.app.arielapp.minor.database.bean;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by pateo on 18-11-3.
 */

@DatabaseTable(tableName = "Book")
public class MyBean {

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(columnName = "name")
    public String name;

    @DatabaseField(columnName = "author")
    public String author;

    @DatabaseField(columnName = "price")
    public String price;

    @DatabaseField(columnName = "pages")
    public int pages;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public int getPages() {
        return pages;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}