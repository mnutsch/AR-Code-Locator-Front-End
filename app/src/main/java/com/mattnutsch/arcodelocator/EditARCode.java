package com.mattnutsch.arcodelocator;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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

public class EditARCode extends AppCompatActivity {

    public OkHttpClient mOkHttpClient;
    public String loggedInUser = "cBTest";
    public String currentName = "";
    public String currentURL = "";
    public String currentLongitude = "";
    public String currentLatitude = "";
    public String recordID = "";

    EditText nameText;
    EditText urlText;
    EditText longitudeText;
    EditText latitudeText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_ar_code);

        nameText = (EditText)findViewById(R.id.editText);
        urlText = (EditText)findViewById(R.id.editText2);
        longitudeText = (EditText)findViewById(R.id.editText3);
        latitudeText = (EditText)findViewById(R.id.editText4);

        //Get the bundle
        Bundle bundle = getIntent().getExtras();
        //Extract the logged in user ID from the bundle.
        loggedInUser = bundle.getString("loggedInUser");
        currentName = bundle.getString("thisRecordName");
        currentURL = bundle.getString("thisRecordURL");
        currentLongitude = bundle.getString("thisRecordLongitude");
        currentLatitude = bundle.getString("thisRecordLatitude");
        recordID = bundle.getString("thisRecordID");
        Log.i(null,"The logged in user is: " + loggedInUser);
        Log.i(null,"currentName == " + currentName);
        Log.i(null,"currentURL == " + currentURL);
        Log.i(null,"currentLongitude == " + currentLongitude);
        Log.i(null,"currentLatitude == " + currentLatitude);

        //Set the values in the text inputs
        EditText nameTextView = (EditText)findViewById(R.id.editText);
        EditText urlTextView = (EditText)findViewById(R.id.editText2);
        EditText longitudeTextView = (EditText)findViewById(R.id.editText4);
        EditText latitudeTextView = (EditText)findViewById(R.id.editText3);
        nameTextView.setText(currentName);
        urlTextView.setText(currentURL);
        longitudeTextView.setText(currentLongitude);
        latitudeTextView.setText(currentLatitude);

        //button on click listener for save
        final Button buttonSave = (Button)  findViewById(R.id.buttonSave);
        buttonSave.setOnClickListener(new View.OnClickListener() {
            public void onClick(final View v) {
                // Code here executes on main thread after user presses button

                //add toast message telling the user that the save is starting
                Toast.makeText(getApplicationContext(), "Saving...", Toast.LENGTH_SHORT).show();


                /*

                Dev note: in Version 2 add code here to patch the record

                */

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

        //button on click listener for delete
        final Button buttonDelete = (Button)  findViewById(R.id.buttonDelete);
        buttonDelete.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code here executes on main thread after user presses button

                //add toast message telling the user that the save is deleting
                Toast.makeText(getApplicationContext(), "Deleting...", Toast.LENGTH_SHORT).show();

                /*

                Dev note: in Version 2 add code here to delete the record

                */
            }
        });



    }
}
