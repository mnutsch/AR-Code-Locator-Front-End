package com.mattnutsch.arcodelocator;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MyARCodes extends AppCompatActivity {

    public OkHttpClient mOkHttpClient;
    public String loggedInUser = "cBTest";

    int numberOfResultsToDisplay = 30;
    List<String> recordIDs = new ArrayList<String>();
    List<String> recordLongitudes = new ArrayList<String>();
    List<String> recordLatitudes = new ArrayList<String>();
    List<String> recordURLs = new ArrayList<String>();
    List<String> recordNames = new ArrayList<String>();

    public void onClickBtn(View v)
    {
        //Toast.makeText(this, "Clicked on Button", Toast.LENGTH_LONG).show();
        View parentRow = (View) v.getParent();
        ListView listView = (ListView) parentRow.getParent();
        final int position = listView.getPositionForView(parentRow);
        Log.i(null,"position == " + position);

        String thisRecordID = recordIDs.get(position);
        String thisRecordName = recordNames.get(position);
        String thisRecordURL = recordURLs.get(position);
        String thisRecordLongitude = recordLongitudes.get(position);
        String thisRecordLatitude = recordLatitudes.get(position);
        Log.i(null,"thisRecordID == " + thisRecordID);
        Log.i(null,"thisRecordName == " + thisRecordName);
        Log.i(null,"thisRecordURL == " + thisRecordURL);
        Log.i(null,"thisRecordLongitude == " + thisRecordLongitude);
        Log.i(null,"thisRecordLatitude == " + thisRecordLatitude);

        //open the Edit Activity with the record values passed through
        Intent Intent = new Intent(v.getContext(), com.mattnutsch.arcodelocator.EditARCode.class);

        //Create the bundle
        Bundle bundle = new Bundle();
        //pass through info about this AR code
        bundle.putString("thisRecordID", thisRecordID);
        bundle.putString("thisRecordName", thisRecordName);
        bundle.putString("thisRecordURL", thisRecordURL);
        bundle.putString("thisRecordLongitude", thisRecordLongitude);
        bundle.putString("thisRecordLatitude", thisRecordLatitude);
        bundle.putString("loggedInUser", loggedInUser);

        //Add the bundle to the intent
        Intent.putExtras(bundle);

        v.getContext().startActivity(Intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_ar_codes);

        //Get the bundle
        Bundle bundle = getIntent().getExtras();
        //Extract the logged in user ID from the bundle.
        loggedInUser = bundle.getString("loggedInUserID");
        Log.i(null,"The logged in user is: " + loggedInUser);

        //button on click listener
        final Button button = (Button)  findViewById(R.id.buttonBack);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code here executes on main thread after user presses button

                try{
                    Log.i(null,"Button clicked!");

                    Intent Intent = new Intent(v.getContext(), com.mattnutsch.arcodelocator.SignedInMenu.class);
                    v.getContext().startActivity(Intent);

                } catch(Exception e){
                    e.printStackTrace();
                }
            }
        });

        //button on click listener
        final Button buttonGetMyList = (Button) findViewById(R.id.buttonGetMyList);
        buttonGetMyList.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code here executes on main thread after user presses button

                /***START HTTP REST CODE***/
                mOkHttpClient = new OkHttpClient();
                HttpUrl reqUrl = HttpUrl.parse("https://arcodelocator.appspot.com/target/?user_id=" + loggedInUser);
                Log.i("debug", String.valueOf(reqUrl));

                //reqUrl = reqUrl.newBuilder().addQueryParameter("key", "AIzaSyCvMlEt5LYkooZzddvlMn_MIO7hFkPtOuY").build();
                Request request = new Request.Builder()
                        .url(reqUrl)
                        .addHeader("Content-Type", "application/json")
                        .build();
                mOkHttpClient.newCall(request).enqueue(new Callback() {

                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.i("debug","Failed to connect to the API. :(");
                        Log.i("debug", String.valueOf(e));

                        //e.printStackTrace();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException{

                        Log.i("debug","Response from API. :)");

                        String r = response.body().string();

                        Log.i("debug","JSON from API == " + r);


                        try{

                            //START CODE TO POPULATE TABLE

                            Log.i("debug","Putting the results in a hash map.");
                            JSONArray items = new JSONArray(r);

                            Log.i("debug", String.valueOf(items));

                            List<Map<String,String>> posts = new ArrayList<Map<String,String>>();

                            int numberOfItemsToRetrieve = numberOfResultsToDisplay;
                            if(items.length() < numberOfResultsToDisplay){
                                numberOfItemsToRetrieve = items.length();
                            }

                            //parse the JSON
                            for(int i = 0; i < numberOfItemsToRetrieve; i++){

                                //put in a hash map
                                HashMap<String, String> n = new HashMap<String, String>();
                                n.put("name", items.getJSONObject(i).getString("name"));
                                n.put("url",items.getJSONObject(i).getString("url"));
                                n.put("longitude", items.getJSONObject(i).getString("longitude"));
                                n.put("latitude",items.getJSONObject(i).getString("latitude"));
                                posts.add(n);

                                //also put the data into an array for reference when clicking a button
                                String tempString = items.getJSONObject(i).getString("key");
                                recordIDs.add(tempString);
                                tempString = items.getJSONObject(i).getString("name");
                                recordNames.add(tempString);
                                tempString = items.getJSONObject(i).getString("longitude");
                                recordLongitudes.add(tempString);
                                tempString = items.getJSONObject(i).getString("latitude");
                                recordLatitudes.add(tempString);
                                tempString = items.getJSONObject(i).getString("url");
                                recordURLs.add(tempString);
                            }

                            Log.i("debug", "posts == ");
                            Log.i("debug", String.valueOf(posts));

                            Log.i("debug","Displaying the items.");

                            final SimpleAdapter postAdapter = new SimpleAdapter(
                                    MyARCodes.this,
                                    posts,
                                    R.layout.search_results_my_codes,
                                    new String[]{"name","url","longitude","latitude"},
                                    new int[]{R.id.name_text, R.id.url_text, R.id.longitude_text, R.id.latitude_text});
                            runOnUiThread( new Runnable() {
                                @Override
                                public void run() {
                                    Log.i("debug","runOnUiThread run().");
                                    ListView PostListView = (ListView) findViewById(R.id.tagListView);

                                    PostListView.setAdapter(postAdapter);

                                }
                            });

                            //END CODE TO POPULATE TABLE

                        } catch (JSONException el){

                            Log.i("debug","Failed to display results. :(");

                            el.printStackTrace();
                        }

                    }

                });
                /***END HTTP REST CODE***/

            }
        });

    }


}
