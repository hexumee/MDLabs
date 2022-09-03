package me.hexu.lab3;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    ArrayList<Double> dots = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        int lbound = genrand(1000, -1000);
        int rbound = genrand(1000, -1000);

        System.out.println(lbound);
        System.out.println(rbound);

        for (int x = Math.min(lbound, rbound); x < Math.max(lbound, rbound); x++) {
            dots.add(x*Math.cos(x/2f));
        }

        System.out.println("min: " + Collections.min(dots));
        System.out.println("max: " + Collections.max(dots));
    }

    public static int genrand(int max , int min) {
        return (new Random().nextInt((max - min) + 1) + min);
    }
}