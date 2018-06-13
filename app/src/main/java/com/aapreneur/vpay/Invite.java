package com.aapreneur.vpay;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import org.hashids.Hashids;


public class Invite extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite);
        findViewById(R.id.invite_button);
        TextView textID = (TextView) findViewById(R.id.unique);
        Hashids hashids = new Hashids("saltySaltofNamak", 5);
        String hash = hashids.encodeHex("Anmol");
        textID.setText(hash);
    }
}