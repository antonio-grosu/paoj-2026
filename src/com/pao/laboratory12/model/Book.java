package com.pao.laboratory12.model;

public class Book {
    private long id;
    private String title;
    private long authorId;
    private boolean available;

    public Book() {}

    public Book(String title, long authorId) {
        this.title = title;
        this.authorId = authorId;
        this.available = true;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public long getAuthorId() { return authorId; }
    public void setAuthorId(long authorId) { this.authorId = authorId; }
    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }

    @Override
    public String toString() {
        return "Book{id=" + id + ", title='" + title + "', authorId=" + authorId + ", available=" + available + "}";
    }
}
