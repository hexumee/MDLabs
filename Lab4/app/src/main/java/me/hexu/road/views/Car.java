package me.hexu.road.views;

import android.graphics.Bitmap;

public class Car {
    private int carOffset;
    private final Bitmap carBitmap;

    public Car(int offset, Bitmap bitmap) {
        this.carOffset = offset;
        this.carBitmap = bitmap;
    }

    public int getCarOffset() {
        return carOffset;
    }

    public Bitmap getCarBitmap() {
        return carBitmap;
    }

    public void setCarOffset(int offset) {
        this.carOffset = offset;
    }
}
