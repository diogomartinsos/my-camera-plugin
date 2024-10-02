package com.outsystems.experts;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.json.JSONArray;
import org.json.JSONException;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.provider.MediaStore;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MyCameraPlugin extends CordovaPlugin {
      private static final String TAG = "MyCordovaPlugin";
      private static final int PERMISSION_REQUEST_CODE = 100;

    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
    }

    @Override
    public boolean execute(String action, JSONArray args, final CallbackContext callbackContext) throws JSONException {
        if (action.equals("openCamera")) {
            openCamera();
            return true;
        }
        return false;
    }

    private void openCamera() {
        // Permissions to request
        String[] permissions = {
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
        };

        // Check and request permissions
        if (checkPermissions(permissions)) {
            // Permissions are already granted, open the camera
            launchCamera();
        }
    }

    // Function to check and request both permissions.
    private boolean checkPermissions(String[] permissions) {
        boolean allPermissionsGranted = true;

        // Check if all permissions are granted
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(cordova.getContext(), permission) == PackageManager.PERMISSION_DENIED) {
                allPermissionsGranted = false;
            }
        }

        // If permissions are not granted, request them
        if (!allPermissionsGranted) {
            ActivityCompat.requestPermissions(cordova.getActivity(), permissions, PERMISSION_REQUEST_CODE);
        }

        return allPermissionsGranted;
    }

    // This method will be called after user responds to the permission dialog
    @Override
    public void onRequestPermissionResult(int requestCode, String[] permissions, int[] grantResults) throws JSONException {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            boolean cameraGranted = false;
            boolean storageGranted = false;

            // Check if permissions were granted
            for (int i = 0; i < permissions.length; i++) {
                if (permissions[i].equals(Manifest.permission.CAMERA)) {
                    cameraGranted = (grantResults[i] == PackageManager.PERMISSION_GRANTED);
                } else if (permissions[i].equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    storageGranted = (grantResults[i] == PackageManager.PERMISSION_GRANTED);
                }
            }

            // If both permissions are granted, open the camera
            if (cameraGranted && storageGranted) {
                launchCamera();
            } else {
                Toast.makeText(cordova.getContext(), "Camera and Storage permissions are required", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Launch the camera after permissions are granted
    private void launchCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(cordova.getContext().getPackageManager()) != null) {
            cordova.getActivity().startActivity(cameraIntent);
        } else {
            Toast.makeText(cordova.getContext(), "No Camera app available", Toast.LENGTH_SHORT).show();
        }
    }
}