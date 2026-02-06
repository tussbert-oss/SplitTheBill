package com.example.splititapp.fragments;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.splititapp.ByOption;
import com.example.splititapp.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;


public class AddPage extends Fragment {
    String splitType = "none";
    private ArrayList<String> savedNames = new ArrayList<>();
    private ArrayList<String> savedAmounts = new ArrayList<>();
    Button button5,equallybtn,customizebtn;
    ImageButton imageButton3;
    EditText titleEditText, amountEditText;
    TextView textView18, dueDate;

    private ActivityResultLauncher<Intent> startForResult;
    private ActivityResultLauncher<Intent> forOptionLauncher;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_page, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        button5 = view.findViewById(R.id.button5);
        imageButton3 = view.findViewById(R.id.imageButton3);
        titleEditText = view.findViewById(R.id.titleEditText);
        amountEditText = view.findViewById(R.id.amountEditText);
        textView18 = view.findViewById(R.id.textView18);
        dueDate = view.findViewById(R.id.dueDate);
        equallybtn = view.findViewById(R.id.equallybtn);
        customizebtn = view.findViewById(R.id.customizebtn);

        startForResult = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result ->{
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null){
                        String selectedName = result.getData().getStringExtra("payer_name");
                        textView18.setText(selectedName);
                    }
                }
        );
        forOptionLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        savedNames = result.getData().getStringArrayListExtra("split_names");
                        savedAmounts = result.getData().getStringArrayListExtra("split_amounts");

                        if (savedNames != null) {
                            Toast.makeText(getContext(), "Split with " + savedNames.size() + " people", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );
        dueDate.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    requireContext(),
                    (dialogView, selectedYear, selectedMonth, selectedDay) -> {
                        String date = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                        dueDate.setText(date);
                    }, year, month, day);
            datePickerDialog.show();
        });

        // --- SAVE BUTTON WITH NUMERIC-ONLY VALIDATION ---
        button5.setOnClickListener(v -> {
            String title = titleEditText.getText().toString().trim();
            String amountStr = amountEditText.getText().toString().trim();
            String date = dueDate.getText().toString().trim();

            if (title.isEmpty() || amountStr.isEmpty() || date.isEmpty()) {
                Toast.makeText(requireContext(), "Please fill all fields!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Only numbers and at most 2 decimals
            if (!amountStr.matches("\\d+(\\.\\d{1,2})?")) {
                Toast.makeText(requireContext(), "Amount can only contain numbers and up to 2 decimal places", Toast.LENGTH_SHORT).show();
                return;
            }

            double amount = Double.parseDouble(amountStr);
            if (amount <= 0) {
                Toast.makeText(requireContext(), "Amount must be greater than 0", Toast.LENGTH_SHORT).show();
                return;
            }

            // Passed all validations
            sendDataToXAMPP();
        });

        imageButton3.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), ByOption.class);
            startForResult.launch(intent);
        });

        equallybtn.setOnClickListener(v -> {
            splitType = "Equally";
            String currentAmount = amountEditText.getText().toString();
            if (currentAmount.isEmpty() || currentAmount.equals("0.00")) {
                Toast.makeText(requireContext(), "Please enter an amount!", Toast.LENGTH_SHORT).show();
            } else {
                Intent intent = new Intent(requireContext(), com.example.splititapp.ForOptionEqually.class);
                intent.putExtra("total_amount", currentAmount);
                forOptionLauncher.launch(intent);
            }
        });

        customizebtn.setOnClickListener(v -> {
            splitType = "Customize";
            String currentAmount = amountEditText.getText().toString();
            if (currentAmount.isEmpty() || currentAmount.equals("0.00")) {
                Toast.makeText(requireContext(), "Please enter an amount!", Toast.LENGTH_SHORT).show();
            } else {
                Intent intent = new Intent(requireContext(), com.example.splititapp.ForOptionCustomize.class);
                intent.putExtra("total_amount", currentAmount);
                forOptionLauncher.launch(intent);
            }
        });
    }

    private void sendDataToXAMPP() {
        if (savedNames == null || savedNames.isEmpty()) {
            Toast.makeText(getContext(), "Error: No people to split with", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = "http://localhost/split_it/save_bill.php";

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> Toast.makeText(getContext(), "Bill Saved!", Toast.LENGTH_SHORT).show(),
                error -> {
                    String message = (error.getMessage() != null) ? error.getMessage() : "Server not found or connection failed";
                    Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("title", titleEditText.getText().toString());
                params.put("total", amountEditText.getText().toString());
                params.put("payer", textView18.getText().toString());
                params.put("date", dueDate.getText().toString());
                params.put("split_type", splitType);

                if (savedNames != null && !savedNames.isEmpty()) {
                    params.put("names", String.join(",", savedNames));
                }
                if (savedAmounts != null && !savedAmounts.isEmpty()) {
                    params.put("amounts", String.join(",", savedAmounts));
                }
                return params;
            }
        };
        Volley.newRequestQueue(requireContext()).add(request);
    }
}
