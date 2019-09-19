package com.example.firstaidapp;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.util.Log;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class PhotoFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    private Button captureImageButton;
    private Button sharebutton;
    private Spinner specialist;
    private ImageView imageView;
    private static final int CAMERA_REQUEST = 1534;
    Uri image_uri;
    private static final int IMAGECAPTURED=1621;
    Intent chooser=null,intent1;
    static Bitmap imageBitmap;
    static String  mailid;

    @Override

    //onCreateview method it will load imageview, takephoto, shareimage button
    //checks the permission requests for the camera. Asks the user to grant access for storage
    //calls the open camera function
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d("upload","capture victim's image");
        View view=inflater.inflate(R.layout.upload_photo,container,false);
        captureImageButton = view.findViewById(R.id.captureImageButton);
        imageView = view.findViewById(R.id.imageView);
        specialist=view.findViewById(R.id.shareImageButton);
        sharebutton=view.findViewById(R.id.sharebutton);
        Resources resources = getResources();



        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_item, resources.getStringArray(R.array.Doctorslist));
        dataAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        specialist.setAdapter(dataAdapter);
        specialist.setSelection(0);
        captureImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
                    if((ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA)== PackageManager.PERMISSION_DENIED) ||
                            (ContextCompat.checkSelfPermission(getContext(),Manifest.permission.WRITE_EXTERNAL_STORAGE)==PackageManager.PERMISSION_DENIED))
                    {

                        ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE},CAMERA_REQUEST);


                    }
                    //permissions are granted
                    else{
                        openCamera();
                    }
                }
                //system version is less than marshmallow
                else{
                    openCamera();
                }

            }
        });

        return view;
    }

//open camera function is used to capture the image.

    private void openCamera(){



        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent,IMAGECAPTURED);



    }

    @Override
    //handling permission result
    public void onRequestPermissionsResult(int requestCode,String[] permissions,int[] grantResults) {


        switch(requestCode){
            case CAMERA_REQUEST:{
                if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    openCamera();
                }
                else{
                    Toast.makeText(getContext(),"permission denied",Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

//  Below code converts image to bitmap format, display the image.

    private File savebitmap(Bitmap bmp) {
        String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
        OutputStream outStream = null;
        File file = new File(extStorageDirectory+"/Captures/",  "screen.png");
        if (!file.exists()) {
            file.mkdirs();

        }
        else {
            file.delete();
            file = new File(extStorageDirectory+"/Captures/",  "screen.png");
        }

        try {
            outStream = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 100, outStream);
            outStream.flush();
            outStream.close();
            MediaStore.Images.Media.insertImage(getActivity().getApplicationContext().getContentResolver(), file.getAbsolutePath(), file.getName(), file.getName());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return file;
    }

//the captured image is set to bitmap
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK) {

            // set image captured to image view
            Bundle extras = data.getExtras();
            imageBitmap = (Bitmap) extras.get("data");
            imageView.setImageBitmap(imageBitmap);
            imageView.setImageURI(image_uri);
            specialist.setOnItemSelectedListener(this);
        }
    }

    //on selection of doctor type and click on the share button will be used to share via email.
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        String item = adapterView.getItemAtPosition(i).toString();
        Resources resources = getResources();
        String[] mail=resources.getStringArray(R.array.emails);
        mailid=mail[i];
        sharebutton.setOnClickListener(new View.OnClickListener() {
                                           @Override
                                           public void onClick(View view) {


                                               File  file = savebitmap(imageBitmap);
                                               if(file!=null){
                                                   Uri apkURI = FileProvider.getUriForFile(
                                                           getContext(),
                                                           "com.example.firstaidapp.provider", file);
                                                   try {

                                                       Log.d("error", String.valueOf(apkURI));
                                                       file.setReadable(true,false);

                                                       intent1 = new Intent(Intent.ACTION_SEND);
                                                       intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                       intent1.setType("image/*");
                                                       intent1.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{mailid});
                                                       intent1.putExtra(android.content.Intent.EXTRA_SUBJECT, "Patients Image");
                                                       intent1.putExtra(Intent.EXTRA_STREAM,apkURI);
                                                       intent1.putExtra(Intent.EXTRA_TEXT,"Hi Doctor, Need first aid");
                                                       chooser=Intent.createChooser(intent1,"Send Image");
                                                       startActivity(chooser);

                                                   }catch(Exception e){
                                                       e.printStackTrace();
                                                   }}
                                               else{
                                                   Toast.makeText(getContext(),"please capture an image and select the doctor", Toast.LENGTH_LONG).show();
                                               }
                                           }
                                       }
        );

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
