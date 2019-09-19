package com.example.firstaidapp;

import android.os.AsyncTask;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

public class GetNearbyhospitals extends AsyncTask<Object,String,String > {
    private String placedata,url;
    private GoogleMap mMap;

    @Override
    // googleplacesurl and map with current location are received are received
    protected String doInBackground(Object... objects) {
        mMap=(GoogleMap)objects[0];
        url=(String)objects[1];
        try {
            placedata=ReadUrl(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return placedata;
    }

    @Override
    // the JSON data is sent to data parser to read data and return the hospital details
    protected void onPostExecute(String s) {
        List<HashMap<String,String>> nearbyhospitals=null;
        Dataparser dataparser=new Dataparser();
        try {
            nearbyhospitals=dataparser.parse(s);
            Displayhospitals(nearbyhospitals);
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

// the data that is obtained from googleplaces api is marked on the map
    private void Displayhospitals(List<HashMap<String,String>> nearByplaceList)
    {
        int i=0;
        while (i<nearByplaceList.size()){
            MarkerOptions markerOptions=new MarkerOptions();
            HashMap<String,String>nearbyhospitals=nearByplaceList.get(i);
            String place_name=nearbyhospitals.get("place_name");

            double lat=Double.parseDouble(nearbyhospitals.get("lat"));
            double lng=Double.parseDouble(nearbyhospitals.get("lng"));
            LatLng latLng=new LatLng(lat,lng);
            markerOptions.position(latLng);


            BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.hospital);
            markerOptions.icon(icon);
            mMap.addMarker(markerOptions);

            mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
            i++;

        }
    }

// google places url is parsed here and json data is obtained here
    public String ReadUrl(String placeurl) throws IOException {
        String data="";
        InputStream inputStream=null;
        HttpURLConnection httpURLConnection=null;
        try{
            URL url=new URL(placeurl);
            httpURLConnection=(HttpURLConnection)url.openConnection();
            httpURLConnection.connect();
            inputStream=httpURLConnection.getInputStream();
            BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(inputStream));
            StringBuffer stringBuffer=new StringBuffer();
            String line="";
            while((line=bufferedReader.readLine())!=null)
            {
                stringBuffer.append(line);
            }
            data=  stringBuffer.toString();
            bufferedReader.close();


        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally{
            inputStream.close();
            httpURLConnection.disconnect();
        }
        return data;
    }
}
