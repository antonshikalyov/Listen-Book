package com.example.listenbook.entities;

public class Book {
    public int id;
    public String title;
    public byte[] image;
    public Long book_duration;
    public String directory_uri;
    public String chapters;

    public Book(int id, String title, byte[] image, Long book_duration, String directory_uri, String chapters) {
        this.id = id;
        this.title = title;
        this.image = image;
        this.book_duration = book_duration;
        this.directory_uri = directory_uri;
        this.chapters = chapters;
    }

    public Book(String title, byte[] image, Long book_duration, String directory_uri, String chapters) {
        this.title = title;
        this.image = image;
        this.book_duration = book_duration;
        this.directory_uri = directory_uri;
        this.chapters = chapters;
    }
}
