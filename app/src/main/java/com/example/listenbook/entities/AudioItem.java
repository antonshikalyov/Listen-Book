package com.example.listenbook.entities;

public class AudioItem {
    public int id;
    public String title;
    public String uri;
    public Long duration;
    public Long positionInBook;

    public AudioItem(int id, String title, String uri, Long duration, Long positionInBook) {
        this.id = id;
        this.title = title;
        this.uri = uri;
        this.duration = duration;
        this.positionInBook = positionInBook;
    }

    public int getId() {
        return id;
    }

}
