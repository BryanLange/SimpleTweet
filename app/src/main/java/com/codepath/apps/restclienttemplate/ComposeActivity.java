package com.codepath.apps.restclienttemplate;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import cz.msebera.android.httpclient.Header;

public class ComposeActivity extends AppCompatActivity {

    public static final int MAX_TWEET_LENGTH = 140;

    private EditText etCompose;
    private Button btnTweet;
    private TwitterClient client;
    private TextView tvCount;
    private EditText etValue;
    private ActionBar actionBar;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // inflate the menu; this add items to the action bar if it present
        getMenuInflater().inflate(R.menu.menu_compose, menu);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);

        client = TwitterApp.getRestClient(this);
        etCompose = findViewById(R.id.etCompose);
        btnTweet = findViewById(R.id.btnTweet);
        tvCount = findViewById(R.id.tvCount);
        etValue = (EditText) findViewById(R.id.etCompose);
        tvCount.setText(0 + "/" + MAX_TWEET_LENGTH);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Compose Tweet");
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#1DA1F2")));


        // set click listener on button
        btnTweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String tweetContent = etCompose.getText().toString();

                // TODO: error-handling
                if(tweetContent.isEmpty()) {
                    Toast.makeText(ComposeActivity.this, "Your tweet is empty!", Toast.LENGTH_LONG).show();
                    return;
                }
                if(tweetContent.length() > MAX_TWEET_LENGTH) {
                    Toast.makeText(ComposeActivity.this, "Your tweet is too long!", Toast.LENGTH_LONG).show();
                    return;
                }
                Toast.makeText(ComposeActivity.this, tweetContent, Toast.LENGTH_LONG).show();
                // make API call to Twitter to publish the content in edit text
                client.composeTweet(tweetContent, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        Log.d("TwitterClient", "Successfully posted tweet!" + response.toString());
                        try {
                            Tweet tweet = Tweet.fromJson(response);
                            Intent data = new Intent();

                            data.putExtra("tweet", Parcels.wrap(tweet));
                            setResult(RESULT_OK, data);

                            // close the activity, pass data to parent
                            finish();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        Log.e("TwitterClient", "Failed to post tweet: " + responseString);
                    }
                });
            }
        }); // end btnTweet.setOnClickListener()


        etValue.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable editable) {
                int length = editable.length();
                tvCount.setText(length + "/" + MAX_TWEET_LENGTH);

                if(etCompose.length() < 1) {
                    btnTweet.setEnabled(false);
                } else if (etCompose.length() > 140 ) {
                    btnTweet.setEnabled(false);
                    tvCount.setTextColor(Color.RED);
                } else {
                    btnTweet.setEnabled(true);
                    tvCount.setTextColor(Color.BLACK);
                }
            }
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
        });

        // disable btnTweet at activity start
        btnTweet.setEnabled(false);
    } // end onCreate()

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    } // end onOptionsItemSelected()
}
