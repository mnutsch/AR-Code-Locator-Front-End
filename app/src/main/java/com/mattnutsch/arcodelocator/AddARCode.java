package com.mattnutsch.arcodelocator;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AddARCode extends AppCompatActivity {

    public OkHttpClient mOkHttpClient;
    public String loggedInUser = "cBTest";

    EditText nameText;
    EditText urlText;
    EditText longitudeText;
    EditText latitudeText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_ar_code);

        //Get the bundle
        Bundle bundle = getIntent().getExtras();
        //Extract the logged in user ID from the bundle.
        loggedInUser = bundle.getString("loggedInUserID");
        Log.i(null,"The logged in user is: " + loggedInUser);

        nameText = (EditText)findViewById(R.id.editText);
        urlText = (EditText)findViewById(R.id.editText2);
        longitudeText = (EditText)findViewById(R.id.editText3);
        latitudeText = (EditText)findViewById(R.id.editText4);

        //button on click listener for save
        final Button buttonSave = (Button)  findViewById(R.id.buttonSave);
        buttonSave.setOnClickListener(new View.OnClickListener() {
            public void onClick(final View v) {
                // Code here executes on main thread after user presses button

                //add toast message telling the user that the save is starting
                Toast.makeText(getApplicationContext(), "Saving...", Toast.LENGTH_SHORT).show();

                try{
                    Log.i(null,"Button clicked!");

                    //get the values from the inputs
                    Log.i(null,"name == " + nameText.getText().toString());
                    Log.i(null,"urlText == " + urlText.getText().toString());
                    Log.i(null,"longitudeText == " + longitudeText.getText().toString());
                    Log.i(null,"latitudeText == " + latitudeText.getText().toString());
                    Log.i(null,"loggedInUser == " + loggedInUser);

                    //make an HTTP REST call to the API
                    /***START HTTP REST CODE***/
                    mOkHttpClient = new OkHttpClient();
                    HttpUrl reqUrl = HttpUrl.parse("https://arcodelocator.appspot.com/target/");
                    Log.i("debug", String.valueOf(reqUrl));

                    /*
                    RequestBody formBody = new FormBody.Builder()
                            .add("name", nameText.getText().toString())
                            .add("url", urlText.getText().toString())
                            .add("longitude", longitudeText.getText().toString())
                            .add("latitude", latitudeText.getText().toString())
                            .add("createdBy", loggedInUser)
                            .build();
                    */

                    String postBody = "{";
                    postBody = postBody + "\"name\": \"" + nameText.getText().toString() + "\", ";
                    postBody = postBody + "\"url\": \"" + urlText.getText().toString() + "\", ";
                    postBody = postBody + "\"longitude\": \"" + longitudeText.getText().toString() + "\", ";
                    postBody = postBody + "\"latitude\": \"" + latitudeText.getText().toString() + "\", ";
                    postBody = postBody + "\"createdBy\": \"" + loggedInUser + "\"";
                    postBody = postBody + "}";

                    Log.i("debug","postBody == " + postBody);

                    MediaType JSON = MediaType.parse("application/json; charset=utf-8");

                    RequestBody formBody = RequestBody.create(JSON, postBody);
                    Request request = new Request.Builder()
                            .url(reqUrl)
                            .addHeader("Content-Type", "application/json")
                            .post(formBody)
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

                            Boolean responseIsSuccess = response.isSuccessful();
                            Log.i("debug","responseIsSuccess == " + responseIsSuccess);

                            String r = response.body().string();
                            Log.i("debug","JSON from API == " + r);

                            if(responseIsSuccess){
                                Log.i("debug","Response is success");

                                //take the user back to the main menu
                                Intent Intent = new Intent(v.getContext(), com.mattnutsch.arcodelocator.SignedInMenu.class);
                                v.getContext().startActivity(Intent);
                            }
                            else{
                                //add toast message telling the user that there was a problem
                                Toast.makeText(getApplicationContext(), "Error saving.", Toast.LENGTH_SHORT).show();
                                Log.i("debug","Response is failure");
                            }


                        }

                    });

                    /***END HTTP REST CODE***/

                } catch(Exception e){
                    e.printStackTrace();
                }
            }
        });

        //button on click listener for cancel
        final Button buttonCancel = (Button)  findViewById(R.id.buttonCancel);
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code here executes on main thread after user presses button

                //add toast message telling the user that the save is canceling
                Toast.makeText(getApplicationContext(), "Canceling...", Toast.LENGTH_SHORT).show();

                try{
                    Log.i(null,"Button clicked!");

                    Intent Intent = new Intent(v.getContext(), com.mattnutsch.arcodelocator.SignedInMenu.class);
                    v.getContext().startActivity(Intent);

                } catch(Exception e){
                    e.printStackTrace();
                }
            }
        });



    }
}
