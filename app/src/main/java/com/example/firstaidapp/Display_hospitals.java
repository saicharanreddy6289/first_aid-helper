package com.example.firstaidapp;

import android.Manifest;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.CollapsingToolbarLayout;

public class Display_hospitals extends AppCompatActivity implements OnMapReadyCallback  {
    private static final String TAG ="Hospitalsnearby";
    private static final int ERROR_DIALOG_REQUEST= 9001;
    private GoogleMap mMap;
    private Marker mCurrLocationMarker;
    private GoogleApiClient client;
    private LocationRequest locationCall;
    private Location mLastLocation;
    private GoogleApiClient googleApiClient;

    private FusedLocationProviderClient mFusedLocationClient;
    private static final String FINE_LOCATION= "Manifest.permission.ACCESS_FINE_LOCATION";
    private static final String COARSE_LOCATION="Manifest.permission.ACCESS_COARSE_LOCATION";
    private Boolean mLocationPermissionsGranted=false;
    private static final int LOCATION_PERMISSION_REQUEST_CODE=1111;
    private static final float DEFAULT_ZOOM = 18f;
    private EditText mSearchText;
    static double latitude,longitude;

//check the services ok or not it true performs location permission function, then displays user location
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hospitals_nearby);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setTitle("Hospitals Near by");

        if(isServicesOk())
        {
            getLocationPermission();

            FragmentManager fmanager = getSupportFragmentManager();



            SupportMapFragment mapFragment=(SupportMapFragment)fmanager.findFragmentById(R.id.mapfirstaid) ;
            mapFragment.getMapAsync(this);
        }

    }


//check all services are enabled.
    public boolean isServicesOk(){

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this);
        if(ConnectionResult.SUCCESS==available){

            return true;
        }
        else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)){

            Dialog dialog=GoogleApiAvailability.getInstance().getErrorDialog(this,available, ERROR_DIALOG_REQUEST);
            dialog.show();
        }
        else{
            Toast.makeText(this,"We can't make map requests",Toast.LENGTH_SHORT).show();
        }
        return false;
    }
//get location permission requests
    private void getLocationPermission(){
        String[] permissions={Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION};
        if(ActivityCompat.checkSelfPermission(getApplicationContext(),Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionsGranted = true;

                initMap();

            } else {
                ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
            }
        }
        else{
            ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }
//initialises the map by using getMapsync methods, onMapReady method is called when getMapAsync is executed,
    private void initMap(){
        Log.d(TAG,"initMap");
        FragmentManager fmanager = getSupportFragmentManager();



        SupportMapFragment mapFragment=(SupportMapFragment)fmanager.findFragmentById(R.id.mapfirstaid) ;
        mapFragment.getMapAsync(this);
    }


   //moves camera to the zoomed location.

    private void moveCamera(LatLng latLng,float zoom){

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }



//loads user location and sets marker

    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        final Object transfer[]=new Object[2];
        final GetNearbyhospitals getNearbyhospitals=new GetNearbyhospitals();

        mMap = googleMap;
        if(mLocationPermissionsGranted){

            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
            try{
                if(mLocationPermissionsGranted){

                    Task location = mFusedLocationClient.getLastLocation();
                    location.addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete( Task task) {
                            if(task.isSuccessful()){


                                Location currentLocation = (Location) task.getResult();
                                latitude=currentLocation.getLatitude();
                                longitude=currentLocation.getLongitude();
                                Log.d(TAG, String.valueOf(latitude));

                                // current location is marked in the map.

                                MarkerOptions markerOptions=new MarkerOptions();
                                LatLng latLng=new LatLng(latitude,longitude);
                                markerOptions.position(latLng);
                                markerOptions.title("Current Location");
                                moveCamera(new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude()),DEFAULT_ZOOM);
                                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                                mMap.addMarker(markerOptions);
                                mMap.setMyLocationEnabled(true);
                                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                                String url="";

                                // google places url is passed to get the near by hospitals

                                StringBuffer hospitalurl=new StringBuffer("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
                                hospitalurl.append("location="+latitude+","+longitude);
                                hospitalurl.append("&radius=1000");
                                hospitalurl.append("&type=hospital");
                                hospitalurl.append("&key="+"AIzaSyB0Oos0pREK9M5PrTPuS6G19Zsdg_PEo1M");
                                url=hospitalurl.toString();
                                transfer[0]=mMap;
                                transfer[1]=url;
                                Log.d(TAG, url);
                                getNearbyhospitals.execute(transfer);

                            }
                            else{
                                Log.d(TAG,"onComplete: location not found");


                            }
                        }
                    });
                }

            }catch(SecurityException e){
                Log.e(TAG,"Security Exception error is"+e.getMessage());
            }


            if(ActivityCompat.checkSelfPermission(this.getApplicationContext(),Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){


            }



        }


    }

}
