package nl.bedrijvendagen.bedrijvendagen;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Size;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;
import java.util.List;

import static nl.bedrijvendagen.bedrijvendagen.StudentCredentials.firstName;
import static nl.bedrijvendagen.bedrijvendagen.StudentCredentials.hasEmail;
import static nl.bedrijvendagen.bedrijvendagen.StudentCredentials.lastName;
import static nl.bedrijvendagen.bedrijvendagen.StudentCredentials.study;
import static nl.bedrijvendagen.bedrijvendagen.StudentCredentials.userID;

public class ScannerActivity extends AppCompatActivity {

    private final int MY_PERMISSIONS_REQUEST_CAMERA = 0;

    private Button bManualInput;
    private SurfaceView svCamera;
    private BarcodeDetector barcodeDetector;
    private CameraSource cameraSource;
    private boolean scanned = false;
    private int width, height;
    private Toast toast;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);
        initViews();
        initListeners();

        requestPermission();

        barcodeDetector = new BarcodeDetector.Builder(this)
                .setBarcodeFormats(Barcode.QR_CODE)
                .build();



        cameraSource = new CameraSource.Builder(this, barcodeDetector)
                .setRequestedPreviewSize(720, 1280)
                .setAutoFocusEnabled(true)
                // TODO: setRequestedPreviewSize(w,h) implementeren.
                .build();

        svCamera.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder surfaceHolder) {
                requestPermission();
                try {
                    if (ContextCompat.checkSelfPermission(ScannerActivity.this,
                            Manifest.permission.CAMERA)
                            != PackageManager.PERMISSION_GRANTED) {

                        // Permission is not granted
                        // Should we show an explanation?
                        if (ActivityCompat.shouldShowRequestPermissionRationale(ScannerActivity.this,
                                Manifest.permission.CAMERA)) {
                            // Show an explanation to the user *asynchronously* -- don't block
                            // this thread waiting for the user's response! After the user
                            // sees the explanation, try again to request the permission.
                            makeToast("Please grant camera permission to use the QR scanner!", Toast.LENGTH_LONG);
                        } else {
                            // No explanation needed; request the permission
                            ActivityCompat.requestPermissions(ScannerActivity.this,
                                    new String[]{Manifest.permission.CAMERA},
                                    MY_PERMISSIONS_REQUEST_CAMERA);

                            // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                            // app-defined int constant. The callback method gets the
                            // result of the request.
                        }
                    } else {
                        // Permission has already been granted
                    }
                    cameraSource.start(svCamera.getHolder());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
                cameraSource.stop();
            }
        });

        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {
                scanned = true;
            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> barcodes = detections.getDetectedItems();
                if (barcodes.size() != 0 && !scanned) {
                    makeToast("Code recognised", Toast.LENGTH_LONG);
                    scanned = true;
                    String[] qrResult = barcodes.valueAt(0).rawValue.split(";");
//                    // Format:
//                    // 0:   "bd"
//                    // 1:   account_id
//                    // 2:   boolean email
//                    // 3:   first name
//                    // 4:   last name
                    // 5:   study
                    if (qrResult[0].contentEquals("bd")) {
//                        scanned = true;
                        userID = Integer.parseInt(qrResult[1]);
                        hasEmail = qrResult[2].equals("Y");
                        firstName = qrResult[3];
                        lastName = qrResult[4];
                        if (qrResult.length == 6) {
                            study = qrResult[5];
                        }

                        makeToast(userID + ", " + firstName + ", " + lastName, Toast.LENGTH_LONG);

                        if (!hasEmail) {
                            Intent manualInputIntent = new Intent(ScannerActivity.this, ManualInputActivity.class);
                            startActivity(manualInputIntent);
//                            scanned = false;
                        } else {
                            Intent commentIntent = new Intent(ScannerActivity.this, CommentActivity.class);
                            commentIntent.putExtra("isOverwriting", false);
                            startActivity(commentIntent);
//                            scanned test= false;
                        }
                        finish();
                    } else {
                        makeToast("The QR code is not part of the Bedrijvendagen event.", Toast.LENGTH_LONG);
                        scanned = false;
                    }
                    for (String s : qrResult) {
                        Log.d("QR RESULT:", s);
                    }
                    Log.d("QR RESULT", "End of content.");

                }
            }
        });
    }

    @Override
    public void onResume() {
        StudentCredentials.reset();
//        Refresher.refresh(this);
        super.onResume();
    }

    private void makeToast(final String message, final int duration) {
        Log.d("TOAST", message);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (toast != null) {
                    toast.cancel();
                }
                toast = Toast.makeText(ScannerActivity.this, message, duration);
                toast.show();
            }
        });
    }

    private void requestPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.CAMERA)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA},
                        MY_PERMISSIONS_REQUEST_CAMERA);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CAMERA: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {
                    Toast.makeText(this, "Camera permission is required for the QR scanning.", Toast.LENGTH_LONG).show();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    private void initViews() {
        bManualInput = findViewById(R.id.bManualInput);
        svCamera = findViewById(R.id.svCamera);
    }

    private void initListeners() {
        bManualInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent manualInputIntent = new Intent(ScannerActivity.this, ManualInputActivity.class);
                startActivity(manualInputIntent);
            }
        });
    }
}