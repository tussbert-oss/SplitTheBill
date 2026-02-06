package com.example.splititapp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.splititapp.Bill;
import com.example.splititapp.BillAdapter;
import com.example.splititapp.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class HomePage extends Fragment {

    private RecyclerView recyclerView;
    private BillAdapter adapter;
    private List<Bill> billList = new ArrayList<>();

    // Notification fields
    private ImageView notifIcon;
    private TextView notifBadge;
    private ArrayList<String> upcomingNotifications = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_homepage, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewBills);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new BillAdapter(billList, false);
        recyclerView.setAdapter(adapter);

        // Initialize notification icon & badge
        notifIcon = view.findViewById(R.id.notif);
        notifBadge = view.findViewById(R.id.notifBadge);

        notifIcon.setOnClickListener(v -> showUpcomingBills());

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadBillsFromDB();
    }

    private void loadBillsFromDB() {
        String url = "http://localhost/split_it/get_bills.php";

        // Clear previous data
        billList.clear();
        upcomingNotifications.clear();
        adapter.notifyDataSetChanged();

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONArray array = new JSONArray(response);

                        for (int i = 0; i < array.length(); i++) {
                            JSONObject obj = array.getJSONObject(i);

                            String dueDate = obj.getString("due_date");
                            String title = obj.getString("title");

                            billList.add(new Bill(
                                    obj.getString("id"),
                                    title,
                                    obj.getString("total_amount"),
                                    obj.getString("payer_name"),
                                    dueDate,
                                    obj.getInt("total_members"),
                                    obj.getInt("paid_members")
                            ));

                            // Check if the bill is due today
                            if (isBillDueToday(dueDate)) {
                                upcomingNotifications.add(title);
                            }
                        }

                        adapter.notifyDataSetChanged();

                        // Update notification badge
                        updateNotificationBadge();

                    } catch (JSONException e) {
                        android.util.Log.e("JSON_ERROR", "Error: " + e.getMessage());
                    }
                },
                error -> Toast.makeText(getContext(), "Connection Error", Toast.LENGTH_SHORT).show()
        );

        Volley.newRequestQueue(requireContext()).add(stringRequest);
    }

    // ------------------- Helper Methods -------------------

    // Checks if a bill is due today
    private boolean isBillDueToday(String date) {
        try {
            String[] parts = date.split("/");
            int day = Integer.parseInt(parts[0]);
            int month = Integer.parseInt(parts[1]);
            int year = Integer.parseInt(parts[2]);

            Calendar today = Calendar.getInstance();
            return (day == today.get(Calendar.DAY_OF_MONTH) &&
                    month == (today.get(Calendar.MONTH) + 1) &&
                    year == today.get(Calendar.YEAR));
        } catch (Exception e) {
            return false; // If date format is invalid
        }
    }

    // Updates the badge number
    private void updateNotificationBadge() {
        int count = upcomingNotifications.size();
        if (count > 0) {
            notifBadge.setText(String.valueOf(count));
            notifBadge.setVisibility(View.VISIBLE);
        } else {
            notifBadge.setVisibility(View.GONE);
        }
    }

    // Shows a dialog with upcoming bills
    private void showUpcomingBills() {
        if (upcomingNotifications.isEmpty()) {
            Toast.makeText(getContext(), "No upcoming bills", Toast.LENGTH_SHORT).show();
        } else {
            StringBuilder sb = new StringBuilder();
            for (String title : upcomingNotifications) {
                sb.append("- ").append(title).append("\n");
            }
            new AlertDialog.Builder(getContext())
                    .setTitle("Upcoming Bills")
                    .setMessage(sb.toString())
                    .setPositiveButton("OK", null)
                    .show();
        }
    }

}
