package com.example.zen.nytimessearch.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;

import com.example.zen.nytimessearch.Article;
import com.example.zen.nytimessearch.ArticleArrayAdapter;
import com.example.zen.nytimessearch.R;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class SearchActivity extends AppCompatActivity {

    EditText etQuery;
    Button btnSearch;
    GridView gvResults;

    ArrayList<Article> articles;
    ArticleArrayAdapter adapter;

    private final int REQUEST_CODE = 20;
    RequestParams params = new RequestParams();
    String date;
    String order;
    String category;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setupViews();

    }

    public void setupViews() {
        etQuery = (EditText) findViewById(R.id.etQuery);
        btnSearch = (Button) findViewById(R.id.btnSearch);
        gvResults = (GridView) findViewById(R.id.gvResults);
        articles = new ArrayList<>();
        adapter = new ArticleArrayAdapter(this,articles);
        gvResults.setAdapter(adapter);

        //hook up Listener for grid click
        gvResults.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id){
                //create an intent to display the article
                Intent i = new Intent(getApplicationContext(),ArticleActivity.class);
                //get the article to display
                Article article = articles.get(position);
                //pass in that article into intent
//                i.putExtra("url", article.getWebUrl());
                i.putExtra("article", article);
                //launch the activity
                startActivity(i);
            }
        });

        gvResults.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public boolean onLoadMore(int page, int totalItemsCount) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to your AdapterView
                loadNextDataFromApi(page);
                // or loadNextDataFromApi(totalItemsCount);
                return true; // ONLY if more data is actually being loaded; false otherwise.
            }
        });
    }

    // Append the next page of data into the adapter
    // This method probably sends out a network request and appends new data items to your adapter.
    public void loadNextDataFromApi(int offset) {
        // Send an API request to retrieve appropriate paginated data

        //  --> Send the request including an offset value (i.e `page`) as a query parameter.

        AsyncHttpClient client = new AsyncHttpClient();
        String url = "https://api.nytimes.com/svc/search/v2/articlesearch.json";

        params.put("page",offset);

        client.get(url, params, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("DEBUG",response.toString());
                JSONArray articleJsonResults = null;

                try {
                    articleJsonResults = response.getJSONObject("response").getJSONArray("docs");
                    adapter.addAll(Article.fromJsonArray(articleJsonResults));
                    adapter.notifyDataSetChanged();
                    Log.d("DEBUG", articles.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }) ;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onArticleSearch(View view) {
        String query = etQuery.getText().toString();

//        Toast.makeText(this,"Searching for "+ query,Toast.LENGTH_LONG).show();
        AsyncHttpClient client = new AsyncHttpClient();
        String url = "https://api.nytimes.com/svc/search/v2/articlesearch.json";

        articles.clear();
        params.put("api-key","29234936851147408e6e5db758bd6d0d");
        params.put("page",0);
        params.put("q",query);

        client.get(url, params, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("DEBUG",response.toString());
                JSONArray articleJsonResults = null;

                try {
                    articleJsonResults = response.getJSONObject("response").getJSONArray("docs");
                    adapter.addAll(Article.fromJsonArray(articleJsonResults));
                    adapter.notifyDataSetChanged();
                    Log.d("DEBUG", articles.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }) ;
    }

    public void onFilterAction(MenuItem mi){
        //create an intent to display the article
        Intent i = new Intent(getApplicationContext(),FilterActivity.class);
        i.putExtra("mode", 2); // pass arbitrary data to launched activity
        startActivityForResult(i, REQUEST_CODE);//launch the activity

    }

    //filter activity return
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // REQUEST_CODE is defined above
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
            // Extract name value from result extras
            date = data.getExtras().getString("date");
            order = data.getExtras().getString("order");
            category = data.getExtras().getString("category");
            int code = data.getExtras().getInt("code", 0);
            // Toast the name to display temporarily on screen
//            Toast.makeText(this, category, Toast.LENGTH_SHORT).show();

            params.put("begin_date",date);
            params.put("sort",order);
            params.put("fq","news_desk:("+category+")");
        }
    }

    public abstract class EndlessScrollListener implements AbsListView.OnScrollListener {
        // The minimum number of items to have below your current scroll position
        // before loading more.
        private int visibleThreshold = 5;
        // The current offset index of data you have loaded
        private int currentPage = 0;
        // The total number of items in the dataset after the last load
        private int previousTotalItemCount = 0;
        // True if we are still waiting for the last set of data to load.
        private boolean loading = true;
        // Sets the starting page index
        private int startingPageIndex = 0;

        public EndlessScrollListener() {
        }

        public EndlessScrollListener(int visibleThreshold) {
            this.visibleThreshold = visibleThreshold;
        }

        public EndlessScrollListener(int visibleThreshold, int startPage) {
            this.visibleThreshold = visibleThreshold;
            this.startingPageIndex = startPage;
            this.currentPage = startPage;
        }


        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount)
        {
            //for control maximum page, NYTimes API's maximum is 1000 articles,developer key has a maximum of 5000 calls per day
            if (this.currentPage < 50){
                // If the total item count is zero and the previous isn't, assume the
                // list is invalidated and should be reset back to initial state
                if (totalItemCount < previousTotalItemCount) {
                    this.currentPage = this.startingPageIndex;
                    this.previousTotalItemCount = totalItemCount;
                    if (totalItemCount == 0) { this.loading = true; }
                }
                // If it's still loading, we check to see if the dataset count has
                // changed, if so we conclude it has finished loading and update the current page
                // number and total item count.
                if (loading && (totalItemCount > previousTotalItemCount)) {
                    loading = false;
                    previousTotalItemCount = totalItemCount;
                    currentPage++;
                }

                // If it isn't currently loading, we check to see if we have breached
                // the visibleThreshold and need to reload more data.
                // If we do need to reload some more data, we execute onLoadMore to fetch the data.
                if (!loading && (firstVisibleItem + visibleItemCount + visibleThreshold) >= totalItemCount ) {
                    loading = onLoadMore(currentPage + 1, totalItemCount);
                }
            }
        }

        // Defines the process for actually loading more data based on page
        // Returns true if more data is being loaded; returns false if there is no more data to load.
        public abstract boolean onLoadMore(int page, int totalItemsCount);

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            // Don't take any action on changed
        }
    }

}

