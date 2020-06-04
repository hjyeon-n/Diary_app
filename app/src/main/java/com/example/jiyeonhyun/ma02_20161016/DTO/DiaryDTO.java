package com.example.jiyeonhyun.ma02_20161016.DTO;

public class DiaryDTO {
    private long id;
    private String date;
    private String whether;
    private int feeling;
    private String contents;
    private String location;

    public DiaryDTO(long id, String date, String whether, int feeling, String contents, String location) {
        this.id = id;
        this.date = date;
        this.whether = whether;
        this.feeling = feeling;
        this.contents = contents;
        this.location = location;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getWhether() {
        return whether;
    }

    public void setWhether(String whether) {
        this.whether = whether;
    }

    public int getFeeling() {
        return feeling;
    }

    public void setFeeling(int feeling) {
        this.feeling = feeling;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
