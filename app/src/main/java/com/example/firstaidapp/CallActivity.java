package com.example.firstaidapp;

import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.appbar.CollapsingToolbarLayout;
// Material design lab_7 is taken as reference to create layout
public class CallActivity extends AppCompatActivity {
    public static final String EXTRA_POSITION = "position";
    Button callbutton;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.call_activity);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        // Set title of Detail page

        int postion = getIntent().getIntExtra(EXTRA_POSITION, 0);
        Resources resources = getResources();
        String[] doctors=resources.getStringArray(R.array.Doctors);

        // phone number is read from values

        String[] phonenumber = resources.getStringArray(R.array.phone_numbers);

        // title is set based on the doctor type

        getSupportActionBar().setTitle(doctors[postion % doctors.length]);
        callbutton=findViewById(R.id.callbutton);
        final String number=phonenumber[postion % phonenumber.length];
        // call is placed to selected doctor

        callbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                Intent intent = callIntent.setData(Uri.parse("tel:"+number));


                startActivity(callIntent);
            }
        });




    }

}
