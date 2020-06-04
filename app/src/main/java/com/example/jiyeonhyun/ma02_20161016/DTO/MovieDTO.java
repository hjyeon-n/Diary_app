package com.example.jiyeonhyun.ma02_20161016.DTO;

import android.text.Html;
import android.text.Spanned;

public class MovieDTO {
    private int _id;
    private String title;
    private String director;
    private String pubDate;
    private String link;
    private String image;

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String getTitle() {
        Spanned spanned = Html.fromHtml(title);
        return spanned.toString();
        //return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public String getPubDate() {
        return pubDate;
    }

    public void setPubDate(String pubDate) {
        this.pubDate = pubDate;
    }

    @Override
    public String toString() {
        return "MovieDto{" +
                "_id=" + _id +
                ", title='" + title + '\'' +
                ", director='" + director + '\'' +
                ", pubDate='" + pubDate + '\'' +
                ", link='" + link + '\'' +
                '}';
    }
}
