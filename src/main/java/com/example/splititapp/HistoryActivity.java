package com.example.splititapp;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class HistoryActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private BillAdapter adapter;
    private List<Bill> historyList;
    private ImageButton backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        recyclerView = findViewById(R.id.rvHistory);
        backButton = findViewById(R.id.imageButton2); // Assuming this is your back button ID

        // If your back button has a different ID, change it here
        // For example: backButton = findViewById(R.id.btnBack);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        historyList = new ArrayList<>();

        adapter = new BillAdapter(historyList, true);
        recyclerView.setAdapter(adapter);

        // Set up back button click listener
        if (backButton != null) {
            backButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goBack();
                }
            });
        } else {
            // Log error if back button not found
            Log.e("HISTORY_ERROR", "Back button not found in layout");
            Toast.makeText(this, "Back button not available", Toast.LENGTH_SHORT).show();
        }

        fetchHistory();
    }

    /**
     * Handles the back button functionality
     */
    private void goBack() {
        // Simply finish the activity to go back to the previous screen
        finish();

        // Optional: Add a fade animation
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    /**
     * Override the hardware/device back button to use the same functionality
     */
    @Override
    public void onBackPressed() {
        goBack();
    }

    private void fetchHistory() {
        String url = "http://localhost/split_it/get_history.php";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONArray array = new JSONArray(response);
                        historyList.clear();

                        for (int i = 0; i < array.length(); i++) {
                            JSONObject obj = array.getJSONObject(i);

                            historyList.add(new Bill(
                                    obj.getString("id"),
                                    obj.getString("title"),
                                    obj.getString("total_amount"),
                                    obj.getString("payer_name"),
                                    obj.getString("due_date"),
                                    obj.getInt("total_members"),
                                    obj.getInt("paid_members")
                            ));
                        }
                        adapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        Log.e("HISTORY_ERROR", "JSON Parsing error: " + e.getMessage());
                    }
                },
                error -> {
                    Log.e("HISTORY_ERROR", "Volley error: " + error.toString());
                    Toast.makeText(this, "Network Error. Check connection.", Toast.LENGTH_SHORT).show();
                });

        Volley.newRequestQueue(this).add(stringRequest);
    }
}