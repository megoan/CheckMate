package com.example.user.check_mate.model.entities;

import android.graphics.Bitmap;

/**
 * Created by User on 13/12/2017.
 */

public class MyBitmap {
    Bitmap bitmap;

    public MyBitmap() {
    }

    public MyBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }
}
