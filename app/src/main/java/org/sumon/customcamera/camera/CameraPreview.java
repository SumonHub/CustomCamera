package org.sumon.customcamera.camera;

/**
 * Created by SumOn on 20,May,2020
 */

import android.content.Context;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;
import java.util.List;

/**
 * A basic Camera preview class
 */
public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
    private static final String TAG = "mycamera";
    private SurfaceHolder mHolder;
    private Camera mCamera;

    public CameraPreview(Context context, Camera camera) {
        super(context);
        mCamera = camera;
        mHolder = getHolder();
        mHolder.addCallback(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // The Surface has been created, now tell the camera where to draw the preview.
        try {
            mCamera.setPreviewDisplay(holder);
            mCamera.setDisplayOrientation(90);

            Parameters parameters = mCamera.getParameters();
            // sett focus
            Log.d(TAG, "surfaceCreated: " + parameters.getSupportedFocusModes());
            if (parameters.getSupportedFocusModes().contains(Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
                parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
            }
            //
            Camera.Size bestSize;
            List<Camera.Size> sizeList = parameters.getSupportedPreviewSizes();
            bestSize = sizeList.get(0);
            for (int i = 1; i < sizeList.size(); i++) {
                if ((sizeList.get(i).width * sizeList.get(i).height) > (bestSize.width * bestSize.height)) {
                    bestSize = sizeList.get(i);
                }
            }

            List<Integer> supportedPreviewFormats = parameters.getSupportedPreviewFormats();
            for (Integer previewFormat : supportedPreviewFormats) {
                if (previewFormat == ImageFormat.YV12) {
                    parameters.setPreviewFormat(previewFormat);
                }
            }

            parameters.setPreviewSize(bestSize.width, bestSize.height);
            parameters.setPictureSize(bestSize.width, bestSize.height);

            mCamera.setParameters(parameters);
            mCamera.startPreview();
        } catch (IOException e) {
            Log.d(TAG, "Error setting camera preview: " + e.getMessage());
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // empty. Take care of releasing the Camera preview in your activity.
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        // If your preview can change or rotate, take care of those events here.
        // Make sure to stop the preview before resizing or reformatting it.

        if (mHolder.getSurface() == null) {
            // preview surface does not exist
            return;
        }

        // stop preview before making changes
        try {
            mCamera.stopPreview();
        } catch (Exception e) {
            // ignore: tried to stop a non-existent preview
        }

        // set preview size and make any resize, rotate or
        // reformatting changes here

        // start preview with new settings
        try {
            mCamera.setPreviewDisplay(mHolder);
            mCamera.startPreview();

        } catch (Exception e) {
            Log.d(TAG, "Error starting camera preview: " + e.getMessage());
        }
    }
}
