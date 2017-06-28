package com.codepath.apps.restclienttemplate;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class ComposeActivity extends AppCompatActivity {
     EditText etTweet;
    private TwitterClient client;
    private final int RESULT_OK = 20;
     Button btnSend;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);

        getSupportActionBar().setTitle("Compose");

        client = TwitterApp.getRestClient();

        etTweet = (EditText) findViewById(R.id.etTweet);
       btnSend = (Button) findViewById(R.id.btnSend);

        btnSend.setOnClickListener(new View.OnClickListener() {
             @Override
            public void onClick(View view) {
                    client.sendTweet(etTweet.getText().toString(), new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject object) {//JSONObject object) {
                            super.onSuccess(statusCode, headers, object); //may not need

                            Tweet tweet = null;
                            try{
                                tweet = Tweet.fromJSON(object);
                            }catch(JSONException e){ e.printStackTrace ();}
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


}
}