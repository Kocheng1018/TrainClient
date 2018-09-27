package com.example.user.demo;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;

public class gps extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gps);
        Uri uri = Uri.parse("https://www.google.com/maps/dir/?api=1&origin=&destination=%E6%9C%80%E8%BF%91%E7%9A%84%20train%20station&travelmode=walking");
        Intent it = new Intent(Intent.ACTION_VIEW,uri);
        startActivity(it);
        finish();
    }
}