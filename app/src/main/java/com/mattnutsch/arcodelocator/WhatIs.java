package com.mattnutsch.arcodelocator;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class WhatIs extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.what_is);


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

    }
}
