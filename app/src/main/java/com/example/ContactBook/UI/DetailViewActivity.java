package com.example.ContactBook.UI;

import android.content.Intent;
import android.os.Bundle;

import com.example.ContactBook.Model.Contactitem;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.ContactBook.R;

public class DetailViewActivity extends AppCompatActivity {
    TextView nameTextView, usernameTextView, phoneTextView, emailTextView, addressTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_view);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setElevation(0);
        getSupportActionBar().setTitle("Detail View");

        Contactitem item = (Contactitem) getIntent().getSerializableExtra("serialzable");
        Log.v("user", item.getEmail());

        nameTextView = (TextView) findViewById(R.id.name_tv);
        usernameTextView = (TextView) findViewById(R.id.username_tv);
        phoneTextView = (TextView) findViewById(R.id.phone_tv);
        emailTextView = (TextView) findViewById(R.id.email_tv);
        addressTextView = (TextView) findViewById(R.id.address);


        nameTextView.setText(item.getName());
        usernameTextView.setText(item.getUsername());
        phoneTextView.setText(item.getPhone());
        emailTextView.setText(item.getEmail());
//        addressTextView.setText(item.get());


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }
}