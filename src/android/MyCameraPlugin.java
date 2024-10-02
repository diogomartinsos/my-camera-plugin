package com.outsystems.experts;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MyCameraPlugin extends CordovaPlugin {
    private static final String TAG = "MyCordovaPlugin";
    private static final int PERMISSION_REQUEST_CODE = 100;

    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
    }

    public boolean execute(String action, JSONArray args, final CallbackContext callbackContext) throws JSONException {
        if (action.equals("openCamera")) {
            openCamera();
        }
        return true;
    }

    private void openCamera() {
        // Permissions to be requested
        String[] permissions = new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
        };

        // Check and request permissions
        if (checkPermissions(permissions)) {
            // Both permissions are granted, open the camera
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (cameraIntent.resolveActivity(cordova.getContext().getPackageManager()) != null) {
                cordova.getActivity().startActivity(cameraIntent);
            } else {
                Toast.makeText(cordova.getContext(), "No Camera app available", Toast.LENGTH_SHORT).show();
            }
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

        // Request permissions if any are not granted
        if (!allPermissionsGranted) {
            ActivityCompat.requestPermissions(cordova.getActivity(), permissions, PERMISSION_REQUEST_CODE);
            Toast.makeText(cordova.getContext(), "Requesting permissions", Toast.LENGTH_SHORT).show();
        }

        return allPermissionsGranted;
    }

    @Override
    public void onRequestPermissionResult(int requestCode, String[] permissions, int[] grantResults) throws JSONException {
        super.onRequestPermissionResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_REQUEST_CODE) {
            boolean cameraGranted = false;
            boolean storageGranted = false;

            // Check which permissions were granted
            for (int i = 0; i < permissions.length; i++) {
                if (permissions[i].equals(Manifest.permission.CAMERA)) {
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        cameraGranted = true;
                    } else {
                        Toast.makeText(cordova.getContext(), "Camera Permission Denied", Toast.LENGTH_SHORT).show();
                    }
                } else if (permissions[i].equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        storageGranted = true;
                    } else {
                        Toast.makeText(cordova.getContext(), "Storage Permission Denied", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            // Open camera if both permissions are granted
            if (cameraGranted && storageGranted) {
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (cameraIntent.resolveActivity(cordova.getContext().getPackageManager()) != null) {
                    cordova.getActivity().startActivity(cameraIntent);
                } else {
                    Toast.makeText(cordova.getContext(), "No Camera app available", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
