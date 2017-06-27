package it.unitn.disi.anybook.activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import it.unitn.disi.anybook.R;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;

/**
 * Questa classe rappresenta l'activity per creare il collegamento con la fotocamera, mostrarla a schermo
 * e rilevare il codice a barre
 */

public class ScanActivity extends AppCompatActivity {

    SurfaceView cameraPreview;

    /**
     * imposta la fotocamera
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
        cameraPreview = (SurfaceView) findViewById(R.id.camera_preview);
        //richiamo il metodo per cominciare la ripresa della camera
        createCameraSource();
    }


    /**
     * Questo metodo crea il collegamento alla camera, la dispone sullo schermo e le connette il barcodeDetector
     */
    private void createCameraSource() {
        //creo il barcode detector grazie a google api
        BarcodeDetector barcodeDetector = new BarcodeDetector.Builder(this).build();

        //creo la comerasource con dimensione ecc
        final CameraSource cameraSource = new CameraSource.Builder(this, barcodeDetector)
                .setAutoFocusEnabled(true)
                .setRequestedPreviewSize(1600, 1024)
                .build();

        //setto l'holder, non so bene come funziona ma in pratica fa vedere
        //quello che vede la camera sullo schermo
        cameraPreview.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                if (ActivityCompat.checkSelfPermission(ScanActivity.this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                       return;
                }
                try {
                    cameraSource.start(cameraPreview.getHolder());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                cameraSource.stop();
            }
        });


        //richiama  le api in qualche modo e fa il detatce del barcode e lo inserisce nel result e lo passa in alto
        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {
            }
            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> barcodeSparseArray = detections.getDetectedItems();
                if(barcodeSparseArray.size() > 0){
                    Intent intent = new Intent();
                    intent.putExtra("barcode", barcodeSparseArray.valueAt(0));
                    //get lòastest from array
                    setResult(CommonStatusCodes.SUCCESS, intent);
                    finish();
                }
            }
        });

    }
}
