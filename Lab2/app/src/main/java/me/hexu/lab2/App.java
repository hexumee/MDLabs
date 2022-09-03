package me.hexu.lab2;

import android.app.Application;
import java.util.ArrayList;
import java.util.Locale;

public class App extends Application {
    private static final ArrayList<String> items = new ArrayList<>();
    public static final Locale localeEn = new Locale("en");
    public static final Locale localeRu = new Locale("ru");

    public static ArrayList<String> getItems() {
        return items;
    }

}
