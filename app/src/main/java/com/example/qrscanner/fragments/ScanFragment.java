package com.example.qrscanner.fragments;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.qrscanner.MainActivity;
import com.example.qrscanner.R;
import com.example.qrscanner.Result_QR;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class ScanFragment extends Fragment {
    public ScanFragment() {}
    private View view;
    private FloatingActionButton camera;
    private MaterialButton scan,rescan;
    private ImageView qr_code;
    private TextView whatTodo;
    private Uri imageUrl = null;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_scan, container, false);
        camera = view.findViewById(R.id.scan_camera);
        whatTodo=view.findViewById(R.id.whatTodo);
        qr_code=view.findViewById(R.id.qr_code);
        scan=view.findViewById(R.id.scan);
        rescan=view.findViewById(R.id.re_scan);
        init();
        Listeners();
        return view;
    }
    private void Listeners(){
        scan.setOnClickListener(v->{
            if(imageUrl==null){
                Toast.makeText(MainActivity.getContext(), "Please capture QR code", Toast.LENGTH_SHORT).show();
            }
            else{
                try{
                    Intent intent = new Intent(MainActivity.getActivity(), Result_QR.class);
                    intent.putExtra("url",imageUrl.toString());
                    startActivity(intent);
                }
                catch (Exception e){
                    Toast.makeText(MainActivity.getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
        camera.setOnClickListener(v->{
            scanImage();
        });
        rescan.setOnClickListener(v->{
            init();
        });
    }
    //In this method we are allowing users to take qr pictures
    private void init(){
        whatTodo.setVisibility(View.VISIBLE);
        camera.setVisibility(View.VISIBLE);
        qr_code.setVisibility(View.GONE);
        scan.setVisibility(View.GONE);
        rescan.setVisibility(View.GONE);
    }

    //In this method we are allowing users to scan the taken Qr picture
    private void Scan(){
        whatTodo.setVisibility(View.GONE);
        camera.setVisibility(View.GONE);
        qr_code.setVisibility(View.VISIBLE);
        scan.setVisibility(View.VISIBLE);
        rescan.setVisibility(View.VISIBLE);
    }
    //Starting Camera so that users can take pictures
    private void scanImage(){
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.TITLE,"QR title");
        contentValues.put(MediaStore.Images.Media.DESCRIPTION,"QR description");
        imageUrl=MainActivity.getContext().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,contentValues);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUrl);
        camLauncher.launch(intent);
    }
    private final ActivityResultLauncher<Intent> camLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult o) {
                    if(o.getResultCode()==Activity.RESULT_OK){
                        Intent data = o.getData();
                        qr_code.setImageURI(imageUrl);
                        Scan();
                    }
                    else {
                        Toast.makeText(MainActivity.getContext(), "Cancelled", Toast.LENGTH_SHORT).show();
                    }
                }
            });
}
