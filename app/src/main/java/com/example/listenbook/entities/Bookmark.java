package com.example.listenbook.entities;

public class Bookmark {
    public int id;
    public int fKeyBookId;
    public String title;
    public String notes;
    public long duration;

    public Bookmark(int id, int fKeyBookId, String title, String notes, long duration) {
        this.id = id;
        this.fKeyBookId = fKeyBookId;
        this.title = title;
        this.notes = notes;
        this.duration = duration;
    }

    public Bookmark(int fKeyBookId, String title, String notes, long duration) {
        this.fKeyBookId = fKeyBookId;
        this.title = title;
        this.notes = notes;
        this.duration = duration;
    }
}
