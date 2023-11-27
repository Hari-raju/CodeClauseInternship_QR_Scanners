package com.example.qrscanner;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentPagerAdapter;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.widget.Toast;

import com.example.qrscanner.adapters.TabAdapter;
import com.example.qrscanner.databinding.ActivityMainBinding;
import com.example.qrscanner.fragments.PickQRFragment;
import com.example.qrscanner.fragments.ScanFragment;
public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding mainBinding;
    private static Context context;
    private static Activity activity;
    private String[] permission1;
    private String[] permission2;
    public static Context getContext() {
        return context;
    }
    public static void setContext(Context context) {
        MainActivity.context = context;
    }
    public static Activity getActivity() {
        return activity;
    }
    public static void setActivity(Activity context) {
        MainActivity.activity = context;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mainBinding.getRoot());
        permission1 = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        permission2 = new String[]{Manifest.permission.CAMERA};
        if (checkPermissions()) {
            setAdapter();
            setContext(this);
            setActivity(this);
        } else {
            reqPermissions();
        }
    }
    private boolean checkPermissions() {
        boolean camera, storage;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            camera = ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
            storage = Environment.isExternalStorageManager();
        } else {
            camera = ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
            storage = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        }
        return camera && storage;
    }
    private void reqPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            ActivityCompat.requestPermissions(this, permission2, 100);
        } else {
            ActivityCompat.requestPermissions(this, permission1, 101);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 100:
                if (grantResults.length > 0) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(this, "Camera Granted", Toast.LENGTH_SHORT).show();
                        reqStorage();
                    } else {
                        Toast.makeText(this, "Camera Denied", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case 101:
                if (grantResults.length > 0) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                        setAdapter();
                        Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
    }
    private void reqStorage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            try {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                Uri uri = Uri.fromParts("package", this.getPackageName(), null);
                intent.setData(uri);
                reqLauncher.launch(intent);
            } catch (Exception e) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                reqLauncher.launch(intent);
            }
        }
    }
    ActivityResultLauncher<Intent> reqLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult o) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (Environment.isExternalStorageManager()) {
                    setAdapter();
                } else {
                    Toast.makeText(MainActivity.this, "Denied", Toast.LENGTH_SHORT).show();
                }
            }
        }
    });
    private void setAdapter() {
        TabAdapter adapter = new TabAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        adapter.addFragments(new ScanFragment(), "Scan");
        adapter.addFragments(new PickQRFragment(), "Gallery");
        mainBinding.tabLayout.setupWithViewPager(mainBinding.frame);
        mainBinding.frame.setAdapter(adapter);
        mainBinding.tabLayout.getTabAt(0).setIcon(R.drawable.baseline_qr_code_scanner_24);
        mainBinding.tabLayout.getTabAt(1).setIcon(R.drawable.baseline_upload_24);
    }
}