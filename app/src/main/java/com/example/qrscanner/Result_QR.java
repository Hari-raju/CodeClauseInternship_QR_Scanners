package com.example.qrscanner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Point;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.qrscanner.databinding.ActivityResultBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScannerOptions;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.common.InputImage;

import java.util.List;

public class Result_QR extends AppCompatActivity {
    private ActivityResultBinding resultBinding;
    Uri imageUrl = null;
    private BarcodeScannerOptions scannerOptions;
    private BarcodeScanner scanner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        resultBinding = ActivityResultBinding.inflate(getLayoutInflater());
        setContentView(resultBinding.getRoot());
        resultBinding.get.setVisibility(View.VISIBLE);
        //Initializing ml kits scanneroption
        scannerOptions = new BarcodeScannerOptions.Builder()
                .setBarcodeFormats(Barcode.FORMAT_ALL_FORMATS)
                .build();
        scanner = BarcodeScanning.getClient(scannerOptions);
        imageUrl = Uri.parse(getIntent().getStringExtra("url"));
        resultBinding.qrImage.setImageURI(imageUrl);
        Toast.makeText(this, "Press button to get Data", Toast.LENGTH_SHORT).show();
        resultBinding.get.setOnClickListener(v -> {
            getQR_data();
        });
    }

    private void getQR_data() {
        resultBinding.progress.setVisibility(View.VISIBLE);
        try {
            //Processing the barcodes present in the image
            InputImage image = InputImage.fromFilePath(this, imageUrl);
            Task<List<Barcode>> result = scanner.process(image)
                    .addOnSuccessListener(new OnSuccessListener<List<Barcode>>() {
                        @Override
                        public void onSuccess(List<Barcode> barcodes) {
                            if (barcodes.isEmpty()) {
                                resultBinding.progress.setVisibility(View.GONE);
                                Toast.makeText(Result_QR.this, "Empty", Toast.LENGTH_SHORT).show();
                            }
                            //If barcode is not empty then we proceed to decoding
                            else
                                decodeData(barcodes);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(Result_QR.this, "Issue :" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void decodeData(List<Barcode> barcodes) {
        Toast.makeText(this, "Decoding", Toast.LENGTH_SHORT).show();
        for (Barcode barcode : barcodes) {
            Rect bounds = barcode.getBoundingBox();
            Point[] corners = barcode.getCornerPoints();
            String rawValue = barcode.getRawValue();

            int valueType = barcode.getValueType();
            switch (valueType) {
                //for wifi
                case Barcode.TYPE_WIFI:
                    Barcode.WiFi typeWifi = barcode.getWifi();
                    resultBinding.resultHead.setText("Wifi Details");
                    String ssid = typeWifi.getSsid();
                    String password = typeWifi.getPassword();
                    String encryption = String.valueOf(typeWifi.getEncryptionType());
                    resultBinding.results.setText(String.format("Results\n\nRaw Values : %s\nSSID : %s\nPassword : %s\nEncryption : %s", rawValue, ssid, password, encryption));
                    break;
                    //for mail
                case Barcode.TYPE_EMAIL:
                    Barcode.Email typeEmail = barcode.getEmail();
                    resultBinding.resultHead.setText("Email Details");
                    String Address = typeEmail.getAddress();
                    String Subject = typeEmail.getSubject();
                    String body = typeEmail.getBody();
                    resultBinding.results.setText(String.format("Results\n\nRaw Values : %s\nAddress : %s\nSubject : %s\nBody : %s", rawValue, Address, Subject, body));
                    break;
                    //for url
                case Barcode.TYPE_URL:
                    Barcode.UrlBookmark typeUrl = barcode.getUrl();
                    resultBinding.resultHead.setText("Url Details");
                    String title = typeUrl.getTitle();
                    String url = typeUrl.getUrl();
                    if (title != null) {
                        resultBinding.results.setText(String.format("Results\n\nRaw Values : %s\nUrl Title : %s\nUrl : %s", rawValue, title, url));
                    } else {
                        resultBinding.results.setText(String.format("Results\n\nRaw Values : %s\nUrl : %s", rawValue, url));
                    }
                    break;
                    //for phone number
                case Barcode.TYPE_PHONE:
                    Barcode.Phone typePhone = barcode.getPhone();
                    resultBinding.resultHead.setText("Phone Details");
                    String type = String.valueOf(typePhone.getType());
                    String number = typePhone.getNumber();
                    resultBinding.results.setText(String.format("Results\n\nRaw Values : %s\nType : %s\nNumber : %s", rawValue, type, number));
                    break;
                    //for sms
                case Barcode.TYPE_SMS:
                    Barcode.Sms typeSms = barcode.getSms();
                    resultBinding.resultHead.setText("SMS Details");
                    String message = typeSms.getMessage();
                    String number1 = typeSms.getPhoneNumber();
                    resultBinding.results.setText(String.format("Results\n\nRaw Values : %s\nMessage : %s\nNumber : %s", rawValue, message, number1));
                    break;
                    //for contact sharing qr
                case Barcode.TYPE_CONTACT_INFO:
                    Barcode.ContactInfo typeContact = barcode.getContactInfo();
                    resultBinding.resultHead.setText("Contact Details");
                    String name = String.valueOf(typeContact.getName());
                    String title1 = typeContact.getTitle();
                    String org = typeContact.getOrganization();
                    String phone = String.valueOf(typeContact.getPhones().get(0));
                    String url1 = typeContact.getUrls().get(0);
                    String email = String.valueOf(typeContact.getEmails().get(0));
                    resultBinding.results.setText(String.format("Results\n\nRaw Values : %s\nName : %s\nTitle : %s\nOrganization : %s\nPhone : %s\nUrl : %s\nEmail : %s", rawValue, name, title1, org, phone, url1, email));
                    break;
                    //for location
                case Barcode.TYPE_GEO:
                    Barcode.GeoPoint typeGeo = barcode.getGeoPoint();
                    resultBinding.resultHead.setText("Geo Details");
                    String latitude = String.valueOf(typeGeo.getLat());
                    String longitude = String.valueOf(typeGeo.getLng());
                    resultBinding.results.setText(String.format("Results\n\nRaw Values : %s\nLatitude : %s\nLongitude : %s", rawValue, latitude, longitude));
                    break;
                default:
                    resultBinding.resultHead.setText("QR Details");
                    resultBinding.results.setText(rawValue);
            }
        }
        resultBinding.get.setVisibility(View.GONE);
        resultBinding.progress.setVisibility(View.GONE);
    }
}