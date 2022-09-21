package me.hexu.road.database.remote;

import android.util.Log;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class RemoteDatabaseRequest {
    private static final String SERVER_URL = "http://192.168.132.84:5000";    // IP-адрес сервера

    private static String requestGetAsync(String sortType) throws InterruptedException {
        final AtomicReference<HttpURLConnection> urlConnection = new AtomicReference<>();    // Сетевое поключение
        final AtomicReference<BufferedReader> bufferedReader = new AtomicReference<>();      // Чтение с сетевого потока
        final AtomicReference<String> responseLine = new AtomicReference<>();                // Ответ с сервера
        StringBuilder response = new StringBuilder();
        ExecutorService executor = Executors.newSingleThreadExecutor();                      // Исполнитель потоков (один поток)

        executor.execute(() -> {
            try {
                urlConnection.set((HttpURLConnection) new URL(SERVER_URL.concat("?sort_by=").concat(sortType)).openConnection());
                urlConnection.get().setRequestMethod("GET");
                bufferedReader.set(new BufferedReader(new InputStreamReader(urlConnection.get().getInputStream())));

                responseLine.set(bufferedReader.get().readLine());
                while (responseLine.get() != null) {
                    response.append(responseLine.get());
                    responseLine.set(bufferedReader.get().readLine());
                }

                bufferedReader.get().close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        // Завершаем исполнитель
        executor.shutdown();

        // Если через минуту исполнитель так и не завершился
        if (!executor.awaitTermination(1, TimeUnit.MINUTES)) {
            Log.e("ApiRequest", "Still waiting for response...");
        }

        return response.toString();
    }

    private static String requestPostAsync(String nickname, int score, long time) throws InterruptedException {
        final AtomicReference<HttpURLConnection> urlConnection = new AtomicReference<>();          // Сетевое поключение
        final AtomicReference<BufferedReader> bufferedReader = new AtomicReference<>();            // Чтение с сетевого потока
        final AtomicReference<OutputStreamWriter> outputStreamWriter = new AtomicReference<>();    // Запись в сетевой поток
        final AtomicReference<String> responseLine = new AtomicReference<>();                      // Ответ с сервера
        StringBuilder response = new StringBuilder();
        ExecutorService executor = Executors.newSingleThreadExecutor();                            // Исполнитель потоков (один поток)

        executor.execute(() -> {
            try {
                urlConnection.set((HttpURLConnection) new URL(SERVER_URL).openConnection());
                urlConnection.get().setRequestMethod("POST");
                urlConnection.get().setDoOutput(true);
                urlConnection.get().setChunkedStreamingMode(0);

                outputStreamWriter.set(new OutputStreamWriter(urlConnection.get().getOutputStream()));
                outputStreamWriter.get().write("nickname=".concat(URLEncoder.encode(nickname, "UTF-8")));
                outputStreamWriter.get().write("&score=".concat(URLEncoder.encode(String.valueOf(score), "UTF-8")));
                outputStreamWriter.get().write("&time=".concat(URLEncoder.encode(String.valueOf(time), "UTF-8")));
                outputStreamWriter.get().close();

                bufferedReader.set(new BufferedReader(new InputStreamReader(urlConnection.get().getInputStream())));
                responseLine.set(bufferedReader.get().readLine());
                while (responseLine.get() != null) {
                    response.append(responseLine.get());
                    responseLine.set(bufferedReader.get().readLine());
                }

                outputStreamWriter.get().close();
                bufferedReader.get().close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        // Завершаем исполнитель
        executor.shutdown();

        // Если через минуту исполнитель так и не завершился
        if (!executor.awaitTermination(1, TimeUnit.MINUTES)) {
            Log.e("ApiRequest", "Still waiting for response...");
        }

        return response.toString();
    }

    public static void requestUrlGet(RemoteCallback callback, String sortType) {
        new Thread(() -> {
            String result = "";

            try {
                result = requestGetAsync(sortType);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            callback.onGetResponse(result);
        }).start();
    }

    public static void requestUrlPost(RemoteCallback callback, String nickname, int score, long time) {
        new Thread(() -> {
            String result = "";

            try {
                result = requestPostAsync(nickname, score, time);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            callback.onPostResponse(result);
        }).start();
    }
}
