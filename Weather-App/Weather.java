package com.c323proj10.jillpena;

public class Weather {

    private String temp;
    private String date;
    private String details;

    public Weather(String temp, String date, String details){
        this.temp=temp;
        this.date=date;
        this.details=details;
    }

    public String getTemp() {
        return temp;
    }

    public String getDate() {
        return date;
    }

    public String getDetails() {
        return details;
    }
}
