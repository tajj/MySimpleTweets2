package com.codepath.apps.restclienttemplate.models;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by tjeanjacques on 6/24/17.
 */

public class Tweet implements Parcelable {
    //atributes
    public String body;
    public long uid;
    public User user;
    public Date createdAt;


    //deserializr
    private Tweet(Parcel in) {
        body = in.readString();
        uid= in.readLong();
        user = in.readParcelable(getClass().getClassLoader());
        createdAt = new Date(in.readLong());
    }

    public Tweet() {

    }

    public static Tweet fromJSON(JSONObject jsonObject) throws JSONException {
        Tweet tweet = new Tweet();

        //extract values from JSON
        tweet.body = jsonObject.getString("text");
        tweet.uid = jsonObject.getLong("id");
        try {
            tweet.createdAt = Tweet.getRelativeTimestamp(jsonObject.getString("created_at"));
        } catch (ParseException e) {
            e.printStackTrace();
        }        tweet.user = User.fromJSON(jsonObject.getJSONObject("user"));
        return tweet;
    }

    public static Date getRelativeTimestamp(String date) throws ParseException
    {
        final String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";


        SimpleDateFormat sdf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sdf.setLenient(true);
        return sdf.parse(date);
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public String getTimeAgo() {
        Date system_date = createdAt;
        Date user_date = new Date();
        double diff = Math.floor((user_date.getTime() - system_date.getTime()) / 1000);
        if (diff <= 1) {return "just now";}
        if (diff < 30) {return diff + "s";}
        if (diff < 60) {return "45s";}
        if (diff <= 90) {return "1m";}
        if (diff <= 3540) {return Math.round(diff / 60) + "m";}
        if (diff <= 5400) {return "1h";}
        if (diff <= 86400) {return Math.round(diff / 3600) + "h";}
        if (diff <= 129600) {return "1d";}
        if (diff < 604800) {return Math.round(diff / 86400) + "d";}
        if (diff <= 777600) {return "1w";}
        return Math.round(diff / 604800) + "w";
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(body);
        out.writeLong(uid);
        out.writeParcelable( user, flags); //HELP? added (PArcelable)
        out.writeLong(createdAt.getTime());
    }

    public static final Parcelable.Creator<Tweet> CREATOR
            = new Parcelable.Creator<Tweet>() {

        @Override
        public Tweet createFromParcel(Parcel in) {
            return new Tweet(in);
        }

        @Override
        public Tweet[] newArray(int size) {
            return new Tweet[size];
        }
    };
}
