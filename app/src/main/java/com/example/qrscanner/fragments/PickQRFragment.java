package com.example.qrscanner.fragments;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

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


public class PickQRFragment extends Fragment {
    private View view;
    private FloatingActionButton upload;
    private MaterialButton scan, rescan;
    private ImageView qr_code;
    private TextView whatTodo;
    private Uri imageUrl = null;

    public PickQRFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_pick_q_r, container, false);
        upload = view.findViewById(R.id.scan_gallery);
        qr_code=view.findViewById(R.id.qr_code_upload);
        scan=view.findViewById(R.id.scan_upload);
        rescan=view.findViewById(R.id.re_scan_upload);
        whatTodo=view.findViewById(R.id.whatTodo_upload);
        init();
        Listeners();
        return view;
    }

    private void Listeners() {
        upload.setOnClickListener(v->{
            uploadImage();
        });

        scan.setOnClickListener(v->{
            if(imageUrl!=null){
                Intent intent = new Intent(MainActivity.getActivity(), Result_QR.class);
                intent.putExtra("url",imageUrl.toString());
                startActivity(intent);
            }
        });

        rescan.setOnClickListener(v->{
            init();
        });

    }

    private void init(){
        whatTodo.setVisibility(View.VISIBLE);
        upload.setVisibility(View.VISIBLE);
        qr_code.setVisibility(View.GONE);
        scan.setVisibility(View.GONE);
        rescan.setVisibility(View.GONE);
    }

    private void Scan(){
        whatTodo.setVisibility(View.GONE);
        upload.setVisibility(View.GONE);
        qr_code.setVisibility(View.VISIBLE);
        scan.setVisibility(View.VISIBLE);
        rescan.setVisibility(View.VISIBLE);
    }

    private void uploadImage(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        uploadLauncher.launch(intent);
    }

    private final ActivityResultLauncher<Intent> uploadLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult o) {
                    if(o.getResultCode()== Activity.RESULT_OK){
                        Intent data = o.getData();
                        imageUrl=data.getData();
                        qr_code.setImageURI(imageUrl);
                        Scan();
                    }
                    else {
                        Toast.makeText(MainActivity.getContext(), "Cancelled", Toast.LENGTH_SHORT).show();
                    }
                }
            });
}