package com.example.antek.notes10clouds.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;


public class Note implements Parcelable{
    long id;
    String body;
    Date date;

    public Note(long id, String body, Date date) {
        this.id = id;
        this.body = body;
        this.date = date;
    }

    public String getBody() {
        return body;
    }

    public Date getDate() {
        return date;
    }

    public long getId() {
        return id;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(this.getId());
        parcel.writeString(this.getBody());
        parcel.writeLong(this.getDate().getTime());
    }

    public Note(Parcel in){
        this.id = in.readLong();
        this.body = in.readString();
        this.date = new Date(in.readLong());
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Note createFromParcel(Parcel in) {
            return new Note(in);
        }

        @Override
        public Note[] newArray(int i) {
            return new Note[0];
        }
    };

}
