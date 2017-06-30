package com.codepath.apps.restclienttemplate;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;



public class TimelineActivity extends AppCompatActivity {
    private SwipeRefreshLayout swipeContainer;
    private Tweet mTweet;
    public static ContactsContract.Profile profile; /// ???????????????
    ArrayList<Tweet> tweets;
    private TwitterClient client;
    TweetAdapter tweetAdapter; // ????????/ make complex?
    LinearLayoutManager llm;



    //    private static final int COMPOSE_CODE = 0;
    private final int REQUEST_CODE = 10;
    private final int RESULT_CODE = 20;

    @BindView(R.id.rvTweet) RecyclerView rvTweets;
    ///RecyclerView rvTweets;
    //EditText mEditTextDetailsReplyToText;
    ProgressBar pb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        ButterKnife.bind(this);

        getSupportActionBar().setCustomView(R.layout.action_bar);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_SHOW_HOME);

        client = TwitterApp.getRestClient();
        //find the RecyclerView
        //rvTweets = (RecyclerView) findViewById(R.id.rvTweet);
        //init the arraylist
        tweets = new ArrayList<>();
        //construct adapter from this data
        tweetAdapter = new TweetAdapter(tweets);
//         mEditTextDetailsReplyToText = findViewById(R.id.etDetails_ReplyToText);


        //Recyclerview setup
        llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        llm.scrollToPosition(0);
        //Recyclerview setup
        rvTweets.setLayoutManager(llm);

        //setting the adapter
        rvTweets.setAdapter(tweetAdapter);

        // for showing the progress
        pb = (ProgressBar) findViewById(R.id.pbLoading);

// add decoration
        // rvTweets.addItemDecoration(new DividerItemDecoration(this));
        pb.setVisibility(ProgressBar.VISIBLE);
        populateTimeline();
        pb.setVisibility(ProgressBar.INVISIBLE);


        // Lookup the swipe container view
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                fetchTimelineAsync(0);
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

//        if (!isNetworkAvailable()) {
//            Toast.makeText(getApplicationContext(), "Network connectivity Issue", Toast.LENGTH_LONG).show();
//        } else if (!isOnline()) {
//            Toast.makeText(getApplicationContext(), "Your device is not online, " + "Try again", Toast.LENGTH_LONG).show();
//        } else{ tweets.clear();
//            tweetAdapter.notifyDataSetChanged();
//            populateTimeline();}
//    }if

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // adds items to the action bar if it is present, then setting up an onclick listenenr for compose
        getMenuInflater().inflate(R.menu.menu_main, menu);
        pb.setVisibility(ProgressBar.VISIBLE);

        menu.findItem(R.id.miCompose).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent i = new Intent(TimelineActivity.this, ComposeActivity.class);
                startActivityForResult(i, REQUEST_CODE);
                return true;
            }
        });
        pb.setVisibility(ProgressBar.INVISIBLE);

        return true;

    }

    public void fetchTimelineAsync(int page) {
        // Send the network request to fetch the updated data
        // `client` here is an instance of Android Async HTTP
        // getHomeTimeline is an example endpoint.

        client.getHomeTimeline(0, new JsonHttpResponseHandler() {
            public void onSuccess(JSONArray json) {
                // Remember to CLEAR OUT old items before appending in the new ones
                tweetAdapter.clear();
                // ...the data has come back, add new items to your adapter...
                tweetAdapter.addAll(tweets);
                // Now we call setRefreshing(false) to signal refresh has finished
                swipeContainer.setRefreshing(false);
            }

            public void onFailure(Throwable e) {
                Log.d("DEBUG", "Fetch timeline error: " + e.toString());
            }
        });
    }



    private void populateTimeline() {
        client.getHomeTimeline(0, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("TwitterClient", response.toString());
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Log.d("TwitterClient", response.toString());
                //iteratiing thru JSON array and deserializing JSON object 4 each entry
                for (int i = 0; i < response.length(); i++) {
                    //converting the object to a Twwet model & adding model to data source
                    //notifying adapter that a new item ahs been added
                    try {
                        Tweet tweet = Tweet.fromJSON(response.getJSONObject(i));
                        tweets.add(tweet);
                        tweetAdapter.notifyItemInserted(tweets.size() - 1);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.d("TwitterClient", responseString);
                throwable.printStackTrace();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("TwitterClient", errorResponse.toString());
                throwable.printStackTrace();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                Log.d("TwitterClient", errorResponse.toString());
                throwable.printStackTrace();
            }



        });

    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.menu_main, menu);
//        return true; //super.onCreateOptionsMenu(menu);
//    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_CODE && requestCode == REQUEST_CODE) {
            //use data parameter
            Tweet newTweet = (Tweet) data.getParcelableExtra("newTweet");
            tweets.add(0, newTweet);
            tweetAdapter.notifyItemInserted(0);
            rvTweets.getLayoutManager().scrollToPosition(0);

            Toast.makeText(this, "New Tweet has been posted :)", Toast.LENGTH_SHORT).show();
        }
    }

//    @Override
//    public boolean onPrepareOptionsMenu(Menu menu) {
//        // Store instance of the menu item containing progress
//        miActionProgressItem = menu.findItem(R.id.miActionProgress);
//        // Extract the action-view from the menu item
//        ProgressBar v =  (ProgressBar) MenuItemCompat.getActionView(miActionProgressItem);
//        // Return to finish
//        return super.onPrepareOptionsMenu(menu);
//    }
//
//    public void showProgressBar() {
//        // Show progress item
//        miActionProgressItem.setVisible(true);
//    }
//
//    public void hideProgressBar() {
//        // Hide progress item
//        miActionProgressItem.setVisible(false);
//    }

//    private void tweetDetails(final Tweet tweet) {
//        ImageView ivReply = (ImageView) findViewById(R.id.ivReply);
//        ivReply.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                FragmentManager fragmentManager = getSupportFragmentManager();
//                composeTweetFragment = new ComposeTweetFragment();
//                composeTweetFragment.setInReplyToStatusId(tweet.getId());
//                composeTweetFragment.setListener(new ComposeTweetFragment.StatusUpdateListener() {
//                    @Override
//                    public void onStatusUpdated() {
//                        composeTweetFragment.dismiss();
//                    }
//                });
//                composeTweetFragment.show(fragmentManager, "COMPOSE_TWEET");
//            }
//        });
//    }
//PROFILE
//
//    @Override
//    protected void showAuthenticatedUserProfile() {
//        Intent intent = new Intent(TimelineActivity.this, ProfileActivity.class);
//        intent.putExtra(Extras.USER_ID, authenticatedUser.getId());
//        startActivity(intent);
//    }
//
//mEditTextDetailsReplyToText.setOnClickListener(new View.OnClickListener() {
//        @Override
//        public void onClick(View v) {
//            mEditTextDetailsReplyToText.setText("@" + mTweet.getUser().getTwitterHandle() + " ");
//            mEditTextDetailsReplyToText.setSelection(mEditTextDetailsReplyToText.getText().length());
//            mRelativeLayoutSendAction.setVisibility(View.VISIBLE);
//        }
//    });
//
//        mEditTextDetailsReplyToText.addTextChangedListener(new TextWatcher() {
//        @Override
//        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//        }
//
//        @Override
//        public void onTextChanged(CharSequence s, int start, int before, int count) {
//            long length = 0;
//            if (s.length() > 140) {
//                mTextViewDetailsCharacterCount.setTextColor(Color.RED);
//                length = 140 - s.length();
//                mButtonDetailsTweetSend.setEnabled(false);
//            } else {
//                mTextViewDetailsCharacterCount.setTextColor(Color.BLACK);
//                length = s.length();
//                mButtonDetailsTweetSend.setEnabled(true);
//            }
//
//            mTextViewDetailsCharacterCount.setText("" + length);
//        }
//
//        @Override
//        public void afterTextChanged(Editable s) {
//
//        }
//    });
//
//    mTwitterClient = TwitterApplication.getRestClient();
//    mTweet = getIntent().getParcelableExtra("tweet");
//    loadPage();
//}




//private Boolean isNetworkAvailable() {
//    ConnectivityManager connectivityManager
//            = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
//    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
//    return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
//}
//    private boolean isOnline() {
//        Runtime runtime = Runtime.getRuntime();
//        try {
//            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
//            int     exitValue = ipProcess.waitFor();
//            return (exitValue == 0);
//        } catch (InterruptedException | IOException e) { e.printStackTrace(); }
//        return false;
//    }
}
