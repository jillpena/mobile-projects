package jillpena.c323finalproj.com.finalproject;

import java.io.Serializable;

public class Video  {

    private String url;
    private int views;
    private boolean favorite;

    public Video(String urlString, int viewsInt){
        this.url=urlString;
        this.views=viewsInt;
        favorite = false;
    }

    public String getUrl() {
        return url;
    }

    public int getViews() {
        return views;
    }

    public void setViews(int fav){
        views=fav;
    }

    public boolean getFavorite() {
        return favorite;
    }

    public void setFavorite(Boolean fav){
        favorite = fav;
    }
}
