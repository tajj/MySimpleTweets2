package com.codepath.apps.restclienttemplate;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by tjeanjacques on 6/28/17.
 */

public class ProfileActivity extends AppCompatActivity{
    // /**the update ID for this tweet if it is a reply*/
    private long tweetID = 0;
    // /**the username for the tweet if it is a reply*/
    private String tweetName = "";
    private TwitterClient client;
    private final int RESULT_OK = 20;
    private static final int MAX_CHARS = 140;

    ImageView ivProfileImage;

    @BindView(R.id.tvUserName) TextView tvUserName;
    @BindView(R.id.tvHandle) TextView tvHandle;
    @BindView(R.id.etTweet) EditText etTweet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);

    }
}
