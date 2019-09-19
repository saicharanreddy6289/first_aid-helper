package com.example.firstaidapp;

import android.content.res.Resources;
import android.os.Bundle;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.widget.TextView;


// below code is taken from material design lab lecture lab 7
public class DetailActivity extends AppCompatActivity {

    public static final String EXTRA_POSITION = "position";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // treatment discription is displayed by using below code

        int postion = getIntent().getIntExtra(EXTRA_POSITION, 0);
        Resources resources = getResources();
        String[] injuries = resources.getStringArray(R.array.injuries);
        getSupportActionBar().setTitle(injuries[postion % injuries.length]);

        String[] treatment_methods = resources.getStringArray(R.array.treatment_methods);
        TextView method = (TextView) findViewById(R.id.place_detail);
        method.setText(treatment_methods[postion % treatment_methods.length]);








    }
}