package com.mattnutsch.arcodelocator;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class NearbyARCodes extends FragmentActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private GoogleApiClient googleApiClient;

    private static final int LOCATION_REQUEST_CODE = 101;

    double _latitude = 44.5;
    double _longitude = -123.2;
    boolean hasLocationPermission = false;

    int numberOfResultsToDisplay = 30;

    public String loggedInUser = "cBTest";

    public OkHttpClient mOkHttpClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nearby_ar_codes);

        //Get the bundle
        Bundle bundle = getIntent().getExtras();
        //Extract the logged in user ID from the bundle.
        loggedInUser = bundle.getString("loggedInUserID");
        Log.i(null,"The logged in user is: " + loggedInUser);

        //Instantiating the GoogleApiClient for location info
        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        //button on click listener
        final Button button = (Button) findViewById(R.id.buttonBack);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code here executes on main thread after user presses button

                try {
                    Log.i(null, "Button clicked!");

                    Intent Intent = new Intent(v.getContext(), com.mattnutsch.arcodelocator.SignedInMenu.class);
                    v.getContext().startActivity(Intent);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        //button on click listener
        final Button button2 = (Button) findViewById(R.id.buttonGetList);
        button2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code here executes on main thread after user presses button

                /***START DEVICE LOCATION CODE***/
                announceLocation(); //toast the location

                //check if the user granted permission for this
                if (hasLocationPermission == false) {
                    //the user did not, so use default values for latitude and longitude
                    _latitude = 44.5;
                    _longitude = -123.2;

                } else {
                    //Fetching the last known location using the FusedLocationAPI
                    Location location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);

                    if (location != null) {
                        _latitude = location.getLatitude();
                        _longitude = location.getLongitude();
                    }
                    else{
                        Toast.makeText(getApplicationContext(), "Error: Location is null", Toast.LENGTH_SHORT).show();
                        Log.i(null, "Error: Location is null");
                    }
                }
                /***END DEVICE LOCATION CODE***/

                /***START HTTP REST CODE***/
                mOkHttpClient = new OkHttpClient();
                HttpUrl reqUrl = HttpUrl.parse("https://arcodelocator.appspot.com/target/");
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
                            //DEV NOTE: add code here to populate the table

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
                                HashMap<String, String> n = new HashMap<String, String>();
                                n.put("name", items.getJSONObject(i).getString("name"));
                                n.put("url",items.getJSONObject(i).getString("url"));
                                n.put("longitude", items.getJSONObject(i).getString("longitude"));
                                n.put("latitude",items.getJSONObject(i).getString("latitude"));
                                posts.add(n);
                            }

                            Log.i("debug", "posts == ");
                            Log.i("debug", String.valueOf(posts));

                            Log.i("debug","Displaying the items.");

                            final SimpleAdapter postAdapter = new SimpleAdapter(
                                    NearbyARCodes.this,
                                    posts,
                                    R.layout.search_results,
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

    @Override
    public void onStart() {
        super.onStart();
        // Initiating the Google API connection for location info
        googleApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        // Disconnecting the Google API connection
        googleApiClient.disconnect();

    }

    //Callback invoked once the GoogleApiClient is connected successfully
    @Override
    public void onConnected(Bundle bundle) {
        //Fetching the last known location using the FusedLocationProviderApi
        //Toast.makeText(getApplicationContext(), "GoogleApiClient is connected successfully! :)", Toast.LENGTH_SHORT).show();
        //Location location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
    }

    //Callback invoked if the Google API connection is suspended
    @Override
    public void onConnectionSuspended(int i) {
        Toast.makeText(getApplicationContext(), "Error: GoogleApiClient connection suspended! :(", Toast.LENGTH_SHORT).show();
    }

    //Callback invoked if the GoogleApiClient connection fails
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Toast.makeText(getApplicationContext(), "Error: GoogleApiClient connection failed! :(", Toast.LENGTH_SHORT).show();
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case LOCATION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PERMISSION_GRANTED) {
                    //do nothing
                } else
                    Toast.makeText(this, "Error: Location Permission Denied", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    //reads the location and displays it
    private void announceLocation() {
        //Checking if the user has granted the permission
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //Requesting the Location permission
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
            return;
        }

        hasLocationPermission = true;

        //Fetching the last known location using the FusedLocationAPI
        Location location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);

        //tell the user the lat and lon
        if (location != null) {
            //Toast.makeText(getApplicationContext(), "Location = " + Double.toString(location.getLatitude()) + ", " + Double.toString(location.getLongitude()), Toast.LENGTH_SHORT).show();
            Log.i(null, "Location = " + Double.toString(location.getLatitude()) + ", " + Double.toString(location.getLongitude()));
        }
        else{
            //Toast.makeText(getApplicationContext(), "Error: Location is null", Toast.LENGTH_SHORT).show();
            Log.i(null, "Error: Location is null");
        }

    }


}
