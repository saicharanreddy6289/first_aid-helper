package com.example.firstaidapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

// Display_hospitals class is called when the button is clicked
public class NearbyHospitalFragment extends Fragment {
    Button callmap;
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view =  inflater.inflate(R.layout.call_map, container, false);
        callmap=view.findViewById(R.id.mapbutton);
        callmap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                Context context = view.getContext();
                Intent intent = new Intent(context, Display_hospitals.class);

                context.startActivity(intent);
            }
        });


        return view;
    }
}
