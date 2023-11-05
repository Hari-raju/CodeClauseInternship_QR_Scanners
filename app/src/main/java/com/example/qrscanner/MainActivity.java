package com.example.qrscanner;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.os.Bundle;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.Toast;

import com.example.qrscanner.databinding.ActivityMainBinding;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding mainBinding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding=ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mainBinding.getRoot());
        checkPermissions();
        Listeners();
    }


    private void Listeners(){
        mainBinding.scan.setOnClickListener(v->{
            if(checkSelfPermission(Manifest.permission.CAMERA)==PackageManager.PERMISSION_GRANTED){
                ScanOptions options = new ScanOptions();
                options.setDesiredBarcodeFormats(ScanOptions.ALL_CODE_TYPES);
                options.setPrompt("Scan a barcode");
                options.setOrientationLocked(true);
                options.setCaptureActivity(Capture.class);
                options.setBeepEnabled(true);
                launcher.launch(options);
            }
            else{
                Toast.makeText(this, "Allow access of Camera to continue", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkPermissions() {
        if(checkSelfPermission(Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.CAMERA},101);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode!=101){
            checkPermissions();
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private ActivityResultLauncher<ScanOptions> launcher = registerForActivityResult(new ScanContract(),
            result->{
                if(result.getContents() == null) {
                    Toast.makeText(MainActivity.this, "Cancelled", Toast.LENGTH_LONG).show();
                } else
                {
                    mainBinding.resultHeader.setVisibility(View.GONE);
                    mainBinding.result.setVisibility(View.VISIBLE);
                    mainBinding.result.setText(String.format("Results from Scanned QR code:\n%s", result.getContents()));
                    if(URLUtil.isValidUrl(result.getContents())){

                       new AlertDialog.Builder(this)
                               .setTitle("URL found would you like us to open it for you?")
                               .setMessage(result.getContents())
                               .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                   @Override
                                   public void onClick(DialogInterface dialogInterface, int i) {
                                       Intent intent = new Intent(Intent.ACTION_VIEW);
                                       intent.setData(Uri.parse(result.getContents()));
                                       startActivity(intent);
                                       dialogInterface.dismiss();
                                   }
                               })
                               .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                   @Override
                                   public void onClick(DialogInterface dialog, int which) {
                                       dialog.dismiss();
                                   }
                               })
                               .show();
                   }
                }
            });
}