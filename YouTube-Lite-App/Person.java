package jillpena.c323finalproj.com.finalproject;

import java.util.ArrayList;

public class Person {

    private String name;
    private int image;
    ArrayList<String> videosWatched;

    public Person(String nameString, int image){

        this.name=nameString;
        this.image = image;
        videosWatched = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public int getImage() {
        return image;
    }

    public void addVideoToList(String video){
        videosWatched.add(video);
    }


}
