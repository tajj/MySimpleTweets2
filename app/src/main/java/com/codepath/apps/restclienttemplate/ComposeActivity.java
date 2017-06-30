package com.codepath.apps.restclienttemplate;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

import static com.raizlabs.android.dbflow.config.FlowManager.getContext;


//YOU ARE CLOSE !!!!!!!!!!!!!!!!!!!!!!!!!!
public class ComposeActivity extends AppCompatActivity {

   // /**the update ID for this tweet if it is a reply*/
    private long tweetID = 0;
   // /**the username for the tweet if it is a reply*/
    private String tweetName = "";
    private TwitterClient client;
    private final int RESULT_OK = 20;
    private static final int MAX_CHARS = 140;
    //    private StatusUpdateListener listener;
   // private TextView tvCharsLeft;
//    EditText etTweet;
//    Button btnSend;
//    TextView tvUserName;
    ImageView ivProfileImage;

    @BindView(R.id.btnSend) Button btnSend;
    @BindView(R.id.tvUserName) TextView  tvUserName;
    @BindView(R.id.tvHandle) TextView tvHandle;
    @BindView(R.id.etTweet) EditText etTweet;
    @BindView(R.id.tvCharsLeft) TextView tvCharsLeft;
   // @BindView(R.id.ivProfileImage) TextView ivProfileImage;




    //@Nullable
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);
        ButterKnife.bind(this);
        tvCharsLeft.setTextColor(Color.BLACK);

        getSupportActionBar().setTitle("Compose a Tweet");




        ivProfileImage = (ImageView) findViewById(R.id.ivProfileImage);


        // tvUserScreenName.setText("@" + user.getScreenName());
        User.getCurrentUser(new User.UserCallbackInterface() {
            @Override
            public void onUserAvailable(User user) {

                tvUserName.setText(user.getName());
                //tvHandle.setText(user.getHandle());

                Glide.with(getContext()).load(user.profileImageUrl).into(ivProfileImage);
            }
        });

//        setupCharacterLimit(view);
//        TweetButton(view);
//        return view;
//    }

//    private void setupCharacterLimit(View view) {
        // tvCharsLeft.setText(String.valueOf(MAX_CHARS));
        etTweet.setFilters(new InputFilter[]{new InputFilter.LengthFilter(MAX_CHARS)});
        etTweet.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Do nothing.
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // int charsLeft = MAX_CHARS - s.length();
                // tvCharsLeft.setText(String.valueOf(charsLeft));

                long length = 0;
                if (s.length() > 140) {
                    tvCharsLeft.setTextColor(Color.RED);
                    length = 140 - s.length();
                    btnSend.setEnabled(false);
                } else {
                    tvCharsLeft.setTextColor(Color.BLACK);
                    length = s.length();
                    btnSend.setEnabled(true);
                }

                tvCharsLeft.setText("" + length);
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Do nothing.
            }
        });
//    }

        client = TwitterApp.getRestClient();



//        //trying to account for replies
//        Bundle extras = getIntent().getExtras();
//
//        if(extras !=null)
//        {
//            //get the ID of the tweet we are replying to
//            tweetID = extras.getLong("tweetID");
//            //get the user screen name for the tweet we are replying to
//            tweetName = extras.getString("tweetUser");
//
//            etTweet.setText("@"+tweetName+" ");
//            //set the cursor to the end of the text for entry
//            etTweet.setSelection(etTweet.getText().length());
//
//        } else{etTweet.setText("");}




//    public void TweetButton(View view) {

        btnSend.setOnClickListener(new View.OnClickListener()

        {
        //@OnClick(R.id.btnSend)
            @Override
            public void onClick(View view) {


                client.sendTweet(etTweet.getText().toString(), new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject object) {//JSONObject object) {
                        super.onSuccess(statusCode, headers, object); //may not need

                        Tweet tweet = null;
                        try {
                            tweet = Tweet.fromJSON(object);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        //setting up the intents
                        Intent data = new Intent();
                        data.putExtra("tweet", tweet);
                        setResult(RESULT_OK, data);
                        Toast.makeText(ComposeActivity.this, "Success! Tweet sent", Toast.LENGTH_SHORT).show();

                        finish();
                    }


                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        super.onFailure(statusCode, headers, throwable, errorResponse);
                        Log.d("DEBUG", errorResponse.toString());
                        Toast.makeText(ComposeActivity.this, "Error: tweet did not send", Toast.LENGTH_SHORT).show();
                        //finish(); DO I need this??

                    }
                });
            }

        });


//



    }
}