package org.sumon.customcamera;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "mycamera";
    private Camera camera;
    FrameLayout cameraPreviewLayout;
    LinearLayout capturedImageHolder;
    ArrayList<Bitmap> capturedImgBitmap = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        reqPermission();

    }

    private void reqPermission() {
        Dexter.withContext(MainActivity.this)
                .withPermissions(
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                ).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport report) {
                if (report.areAllPermissionsGranted()) {
                    Log.d(TAG, "onPermissionsChecked: all permission granted");
                    initCamera();
                } else {
                    reqPermission();
                }
            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {

            }

        }).check();
    }

    private void initCamera() {
        cameraPreviewLayout = findViewById(R.id.camera_preview);
        capturedImageHolder = findViewById(R.id.captured_image);
        camera = getCameraInstance();
        if (camera != null) {
            CameraPreview cameraPreview = new CameraPreview(MainActivity.this, camera);
            cameraPreviewLayout.addView(cameraPreview);

            FloatingActionButton captureButton = findViewById(R.id.camera);
            captureButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (camera != null) {
                        camera.takePicture(null, null, mPicture);
                    } else {
                        Log.d(TAG, "onClick: camera getting null");
                    }
                }
            });

        } else {
            Log.d(TAG, "initCamera: camera getting null");
        }
    }

    /**
     * A safe way to get an instance of the Camera object.
     */
    public Camera getCameraInstance() {
        Camera c = null;
        try {
            c = Camera.open(); // attempt to get a Camera instance
        } catch (Exception e) {
            Log.d("TAG", "getCameraInstance: " + e.getMessage());
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }

    private Camera.PictureCallback mPicture = new Camera.PictureCallback() {

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            Log.d(TAG, "onPictureTaken");
            camera.startPreview();


            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
            if (bitmap == null) {
                Toast.makeText(MainActivity.this, "Captured image is empty", Toast.LENGTH_LONG).show();
                return;
            }
            capturedImgBitmap.add(bitmap);

            if (capturedImgBitmap.size() > 0) {
                //
                capturedImageHolder.removeAllViews();
                //
                for (Bitmap bit : capturedImgBitmap) {
                    ImageView imageView = new ImageView(MainActivity.this);
                    imageView.setImageBitmap(bit);
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(dpToPx(150), dpToPx(200));
                    lp.setMargins(dpToPx(2), 0, dpToPx(2), 0);
                    imageView.setLayoutParams(lp);
                    imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                    capturedImageHolder.addView(imageView);
                }
            }

            Log.d(TAG, "onPictureTaken: capturedImgBitmap size = " + capturedImgBitmap.size());



            /*File pictureFile = getOutputMediaFile();
            if (pictureFile == null){
                Log.d("TAG", "Error creating media file, check storage permissions");
                return;
            }

            try {
                FileOutputStream fos = new FileOutputStream(pictureFile);
                fos.write(data);
                fos.close();
                Log.d("TAG", "onPictureTaken: done");
            } catch (FileNotFoundException e) {
                Log.d("TAG", "File not found: " + e.getMessage());
            } catch (IOException e) {
                Log.d("TAG", "Error accessing file: " + e.getMessage());
            }*/
        }
    };

    public  int pxToDp(int px) {
        return (int) (px / ((float) getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    public int dpToPx(int dp) {
        return (int) (dp * ((float) getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    /**
     * Create a File for saving an image or video
     */
    private File getOutputMediaFile() {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "MyCameraApp");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("MyCameraApp", "failed to create directory");
                return null;
            }
        }
        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                "IMG_" + timeStamp + ".jpg");

        return mediaFile;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (camera != null) {
            camera.release();        // release the camera for other applications
            camera = null;
        }
    }
}
