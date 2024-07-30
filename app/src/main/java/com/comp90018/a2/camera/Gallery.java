package com.comp90018.a2.camera;

import android.app.Activity;
import android.content.Intent;
import android.provider.MediaStore;

public class Gallery {
    private static final int REQUEST_IMAGE_PICK = 2;
    private Activity activity;

    public Gallery(Activity activity) {
        this.activity = activity;
    }

    public void dispatchPickPictureIntent() {
        Intent pickPictureIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        activity.startActivityForResult(pickPictureIntent, REQUEST_IMAGE_PICK);
//        if (pickPictureIntent.resolveActivity(activity.getPackageManager()) != null) {
//            activity.startActivityForResult(pickPictureIntent, REQUEST_IMAGE_PICK);
//        }
    }

    public static int getRequestImagePick() {
        return REQUEST_IMAGE_PICK;
    }
}

