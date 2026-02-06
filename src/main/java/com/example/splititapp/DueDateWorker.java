package com.example.splititapp;

import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class DueDateWorker extends Worker {

    public DueDateWorker(@NonNull android.content.Context context,
                         @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        try {
            // This should point to a script that just CHECKS bills, NOT insert
            URL url = new URL("http://localhost/split_it/check_due.php");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(conn.getInputStream())
            );

            StringBuilder result = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }

            JSONObject json = new JSONObject(result.toString());
            boolean mayDue = json.getBoolean("due");

            if (mayDue) {
                String billTitle = json.getString("title");

                Intent intent = new Intent(getApplicationContext(), DueDateReceiver.class);
                intent.putExtra("title", "Payment Due");
                intent.putExtra("message", billTitle + " is due today!");
                getApplicationContext().sendBroadcast(intent);
            }

        } catch (Exception e) {
            e.printStackTrace();
            return Result.failure();
        }

        return Result.success();
    }
}
