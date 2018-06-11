package com.mattnutsch.arcodelocator;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import net.openid.appauth.AuthState;
import net.openid.appauth.AuthorizationException;
import net.openid.appauth.AuthorizationRequest;
import net.openid.appauth.AuthorizationService;
import net.openid.appauth.AuthorizationServiceConfiguration;
import net.openid.appauth.ResponseTypeValues;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

public class SignedInMenu extends AppCompatActivity {

    public AuthorizationService mAuthorizationService;
    public AuthState mAuthState;
    public OkHttpClient mOkHttpClient;

    public String loggedInUserID = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        SharedPreferences authPreference = getSharedPreferences("auth", MODE_PRIVATE);
        setContentView(R.layout.signed_in_menu);

        mAuthorizationService = new AuthorizationService(this);

        //button on click listener
        Button buttonAdd = (Button) findViewById(R.id.buttonAddNearbyCode);
        buttonAdd.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code here executes on main thread after user presses button

                try{
                Log.i(null,"Button clicked!");

                Intent Intent = new Intent(v.getContext(), com.mattnutsch.arcodelocator.AddARCode.class);

                //Create the bundle
                Bundle bundle = new Bundle();

                //Add the logged in user
                bundle.putString("loggedInUserID", loggedInUserID);

                //Add the bundle to the intent
                Intent.putExtras(bundle);

                v.getContext().startActivity(Intent);

                } catch(Exception e){
                    e.printStackTrace();
                }
            }
        });

        //button on click listener
        final Button buttonMy = (Button)  findViewById(R.id.buttonMyCodes);
        buttonMy.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code here executes on main thread after user presses button

                try{
                    Log.i(null,"Button clicked!");

                    Intent Intent = new Intent(v.getContext(), com.mattnutsch.arcodelocator.MyARCodes.class);

                    //Create the bundle
                    Bundle bundle = new Bundle();

                    //Add the logged in user
                    bundle.putString("loggedInUserID", loggedInUserID);

                    //Add the bundle to the intent
                    Intent.putExtras(bundle);

                    v.getContext().startActivity(Intent);

                } catch(Exception e){
                    e.printStackTrace();
                }
            }
        });

        //button on click listener
        final Button buttonNearby = (Button)  findViewById(R.id.buttonFindNearby);
        buttonNearby.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code here executes on main thread after user presses button

                try{
                    Log.i(null,"Button clicked!");

                    Intent Intent = new Intent(v.getContext(), com.mattnutsch.arcodelocator.NearbyARCodes.class);

                    //Create the bundle
                    Bundle bundle = new Bundle();

                    //Add the logged in user
                    bundle.putString("loggedInUserID", loggedInUserID);

                    //Add the bundle to the intent
                    Intent.putExtras(bundle);

                    v.getContext().startActivity(Intent);

                } catch(Exception e){
                    e.printStackTrace();
                }
            }
        });

        //button on click listener
        final Button buttonWhatIs = (Button)  findViewById(R.id.buttonWhatIs);
        buttonWhatIs.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code here executes on main thread after user presses button

                try{
                    Log.i(null,"Button clicked!");

                    Intent Intent = new Intent(v.getContext(), com.mattnutsch.arcodelocator.WhatIs.class);
                    v.getContext().startActivity(Intent);

                } catch(Exception e){
                    e.printStackTrace();
                }
            }
        });

    }

    @Override
    protected void onStart(){

        Log.i("debug","onStart ran. Getting getting or creating auth state");

        mAuthState = getOrCreateAuthState();
        super.onStart();

        try{
            Log.i(null,"Signed in! Getting Google Plus profile ID.");

            mAuthState.performActionWithFreshTokens(mAuthorizationService, new AuthState.AuthStateAction() {
                @Override public void execute(
                        String accessToken,
                        String idToken,
                        AuthorizationException e) {
                    if (e == null) {
                        mOkHttpClient = new OkHttpClient();
                        //HttpUrl reqUrl = HttpUrl.parse("https://www.googleapis.com/plusDomains/v1/people/me/activities/user");
                        HttpUrl reqUrl = HttpUrl.parse("https://www.googleapis.com/plusDomains/v1/people/me/?alt=json");
                        Log.i("debug", String.valueOf(reqUrl));
                        reqUrl = reqUrl.newBuilder().addQueryParameter("key", "INSERT_KEY_HERE").build();
                        Request request = new Request.Builder()
                                .url(reqUrl)
                                .addHeader("Authorization", "Bearer " + accessToken)
                                .build();
                        mOkHttpClient.newCall(request).enqueue(new Callback() {

                            @Override
                            public void onFailure(Call call, IOException e) {
                                Log.i("debug","Authorization failure. :(");
                                Log.i("debug", String.valueOf(e));

                                //e.printStackTrace();
                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException{

                                Log.i("debug","Authorization response. :)");

                                String r = response.body().string();

                                Log.i("debug","r == " + r);

                                JSONObject j = null;
                                try {
                                    j = new JSONObject(r);
                                    Log.i("debug","j == " + j);
                                    Log.i("debug","j id == " + j.getString("id"));

                                    //update the value of loggedInUserID, so that it can be passed to other activities
                                    loggedInUserID = j.getString("id");

                                } catch (JSONException e1) {
                                    e1.printStackTrace();
                                }




                            }

                        });
                    }
                }
            });

        } catch(Exception e){
            e.printStackTrace();
        }
    }

    AuthState getOrCreateAuthState(){

        Log.i("debug","getOrCreateAuthState ran.");

        AuthState auth = null;
        SharedPreferences authPreference = getSharedPreferences("auth", MODE_PRIVATE);
        String stateJson = authPreference.getString("stateJson", null);
        if(stateJson != null){
            try{
                auth = AuthState.jsonDeserialize(stateJson);

                Log.i("debug","auth == " + auth);

            } catch (JSONException e){
                e.printStackTrace();
                return null;
            }
        }
        if(auth != null && auth.getAccessToken() != null){
            return auth;
        } else {
            updateAuthState();
            return null;
        }
    }

    void updateAuthState(){

        Log.i("debug","updateAuthState ran.");

        Uri authEndpoint = new Uri.Builder().scheme("https").authority("accounts.google.com").path("/o/oauth2/v2/auth").build();
        Uri tokenEndpoint = new Uri.Builder().scheme("https").authority("www.googleapis.com").path("/oauth2/v4/token").build();
        Uri redirect = new Uri.Builder().scheme("com.mattnutsch.arcodelocator").path("oauth2redirect").build();

        Log.i("debug","debug point.");

        AuthorizationServiceConfiguration config = new AuthorizationServiceConfiguration(authEndpoint, tokenEndpoint, null);
        AuthorizationRequest req = new AuthorizationRequest.Builder(config, "963585173987-h2gphk6cm5f0lm59dmkirhp19f4qlpvm.apps.googleusercontent.com", ResponseTypeValues.CODE, redirect)
                .setScopes("https://www.googleapis.com/auth/plus.me", "https://www.googleapis.com/auth/plus.stream.write", "https://www.googleapis.com/auth/plus.stream.read")
                .build();

        Intent authComplete = new Intent(this, AuthCompleteActivity.class);
        mAuthorizationService.performAuthorizationRequest(req, PendingIntent.getActivity(this, req.hashCode(), authComplete, 0));

    }

}
