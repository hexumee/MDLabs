package me.hexu.road.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Random;

public class CarBitmaps {
    private final int CARS_COUNT = 5;
    private final ArrayList<Bitmap> carsOnLeftSide = new ArrayList<>();
    private final ArrayList<Bitmap> carsOnRightSide = new ArrayList<>();
    private final Random carChooser;

    public CarBitmaps(Context context) {
        carChooser = new Random(context.hashCode());

        try {
            Matrix matrixReversed = new Matrix();
            matrixReversed.postRotate(180);

            for (int i = 1; i < CARS_COUNT+1; i++) {
                InputStream assetFile = context.getAssets().open(String.format(context.getResources().getConfiguration().locale,"car%d.png", i));
                Bitmap scaledAsset = Bitmap.createScaledBitmap(BitmapFactory.decodeStream(assetFile), 160, 256, false);
                carsOnLeftSide.add(Bitmap.createBitmap(scaledAsset, 0, 0, scaledAsset.getWidth(), scaledAsset.getHeight(), matrixReversed, false));
                carsOnRightSide.add(scaledAsset);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Bitmap getRandomLeftCar() {
        return carsOnLeftSide.get(carChooser.nextInt(CARS_COUNT));
    }

    public Bitmap getRandomRightCar() {
        return carsOnRightSide.get(carChooser.nextInt(CARS_COUNT));
    }
}
