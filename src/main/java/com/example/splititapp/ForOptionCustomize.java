package com.example.splititapp; // Make sure this matches your project package

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

public class ForOptionCustomize extends AppCompatActivity {

    LinearLayout containerLayout;
    double totalBill = 0.0;
    boolean isDeleteMode = false;

    ImageButton backBtn;
    Button saveBtn, addBtn, deleteBtn;
    TextView tvRemaining;

    private final ArrayList<View> recentlyDeletedRows = new ArrayList<>();
    private final ArrayList<Integer> recentlyDeletedPositions = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_for_option_customize); // Make sure this layout exists

        containerLayout = findViewById(R.id.containerLayout);
        backBtn = findViewById(R.id.imageButton7);
        saveBtn = findViewById(R.id.button3);
        addBtn = findViewById(R.id.button4);
        deleteBtn = findViewById(R.id.button9);
        tvRemaining = findViewById(R.id.tvRemaining);

        String amountStr = getIntent().getStringExtra("total_amount");
        try {
            if (amountStr != null) totalBill = Double.parseDouble(amountStr);
        } catch (NumberFormatException e) {
            totalBill = 0.0;
        }

        backBtn.setOnClickListener(v -> handleBackPressed());
        addBtn.setOnClickListener(v -> addNewRow());
        deleteBtn.setOnClickListener(v -> toggleDeleteMode());
        saveBtn.setOnClickListener(v -> validateAndSave());

        addNewRow(); // start with one row
    }

    @Override
    public void onBackPressed() {
        handleBackPressed();
    }

    private void handleBackPressed() {
        if (isDeleteMode) {
            toggleDeleteMode();
            Toast.makeText(this, "Delete mode cancelled", Toast.LENGTH_SHORT).show();
        } else {
            new AlertDialog.Builder(this)
                    .setTitle("Go Back")
                    .setMessage("Are you sure you want to go back? Unsaved changes will be lost.")
                    .setPositiveButton("Yes", (dialog, which) -> finish())
                    .setNegativeButton("Cancel", null)
                    .show();
        }
    }

    private void addNewRow() {
        View rowView = getLayoutInflater().inflate(R.layout.row_person, containerLayout, false);
        EditText amountInput = rowView.findViewById(R.id.splitAmount);
        CheckBox deleteCheckBox = rowView.findViewById(R.id.checkSelect);

        deleteCheckBox.setVisibility(View.GONE);

        amountInput.addTextChangedListener(new android.text.TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(android.text.Editable s) {
                updateRemainingText();
            }
        });

        containerLayout.addView(rowView);
        updateRemainingText();
    }

    private void toggleDeleteMode() {
        isDeleteMode = !isDeleteMode;

        if (isDeleteMode) {
            deleteBtn.setText("Delete Selected");
            saveBtn.setVisibility(View.GONE);

            for (int i = 0; i < containerLayout.getChildCount(); i++) {
                View row = containerLayout.getChildAt(i);
                CheckBox checkBox = row.findViewById(R.id.checkSelect);
                checkBox.setVisibility(View.VISIBLE);
                checkBox.setChecked(false);
            }

            deleteBtn.setOnClickListener(v -> confirmDelete());
        } else {
            deleteBtn.setText("Delete");
            saveBtn.setVisibility(View.VISIBLE);

            for (int i = 0; i < containerLayout.getChildCount(); i++) {
                View row = containerLayout.getChildAt(i);
                CheckBox checkBox = row.findViewById(R.id.checkSelect);
                checkBox.setVisibility(View.GONE);
            }

            deleteBtn.setOnClickListener(v -> toggleDeleteMode());
        }
    }

    private void confirmDelete() {
        int count = containerLayout.getChildCount();
        int selectedCount = 0;
        ArrayList<String> selectedNames = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            View row = containerLayout.getChildAt(i);
            CheckBox checkBox = row.findViewById(R.id.checkSelect);
            if (checkBox.isChecked()) {
                selectedCount++;
                EditText nameInput = row.findViewById(R.id.personName);
                String name = nameInput.getText().toString().trim();
                selectedNames.add(name.isEmpty() ? "Person " + (i + 1) : name);
            }
        }

        if (selectedCount == 0) {
            Toast.makeText(this, "Select at least one row to delete", Toast.LENGTH_SHORT).show();
            return;
        }

        StringBuilder message = new StringBuilder();
        message.append("Are you sure you want to delete ");
        if (selectedCount == 1) {
            message.append("this person?\n\n").append(selectedNames.get(0));
        } else {
            message.append("these ").append(selectedCount).append(" people?\n\n");
            for (int i = 0; i < Math.min(selectedNames.size(), 3); i++) {
                message.append("• ").append(selectedNames.get(i)).append("\n");
            }
            if (selectedCount > 3) {
                message.append("• and ").append(selectedCount - 3).append(" more...");
            }
        }

        new AlertDialog.Builder(this)
                .setTitle("Confirm Delete")
                .setMessage(message.toString())
                .setPositiveButton("Delete", (dialog, which) -> performDelete())
                .setNegativeButton("Cancel", null)
                .setCancelable(true)
                .show();
    }

    private void performDelete() {
        int count = containerLayout.getChildCount();
        recentlyDeletedRows.clear();
        recentlyDeletedPositions.clear();
        int deletedCount = 0;

        for (int i = count - 1; i >= 0; i--) {
            View row = containerLayout.getChildAt(i);
            CheckBox checkBox = row.findViewById(R.id.checkSelect);

            if (checkBox.isChecked()) {
                if (containerLayout.getChildCount() <= 1) {
                    Toast.makeText(this, "At least one person is required", Toast.LENGTH_SHORT).show();
                    return;
                }
                recentlyDeletedRows.add(0, row);
                recentlyDeletedPositions.add(0, i);
                containerLayout.removeView(row);
                deletedCount++;
            }
        }

        if (deletedCount > 0) {
            String message = deletedCount == 1 ? "1 person deleted" : deletedCount + " people deleted";
            Snackbar snackbar = Snackbar.make(containerLayout, message, Snackbar.LENGTH_LONG);
            snackbar.setAction("UNDO", v -> undoDelete());
            snackbar.show();

            toggleDeleteMode();
        }

        updateRemainingText();
    }

    private void undoDelete() {
        for (int i = 0; i < recentlyDeletedRows.size(); i++) {
            View row = recentlyDeletedRows.get(i);
            int position = recentlyDeletedPositions.get(i);
            containerLayout.addView(row, position);
        }
        recentlyDeletedRows.clear();
        recentlyDeletedPositions.clear();
        updateRemainingText();
    }

    private void updateRemainingText() {
        double currentSum = 0;

        for (int i = 0; i < containerLayout.getChildCount(); i++) {
            View row = containerLayout.getChildAt(i);
            EditText amountInput = row.findViewById(R.id.splitAmount);
            String val = amountInput.getText().toString();

            if (!val.isEmpty()) {
                try {
                    currentSum += Double.parseDouble(val);
                } catch (NumberFormatException ignored) {}
            }
        }

        double remaining = totalBill - currentSum;
        if (tvRemaining != null) {
            tvRemaining.setText(String.format("Remaining: ₱%.2f", remaining));
            tvRemaining.setTextColor(Math.abs(remaining) < 0.01 ? android.graphics.Color.GREEN : android.graphics.Color.RED);
        }
    }

    private void validateAndSave() {
        double currentSum = 0;
        ArrayList<String> names = new ArrayList<>();
        ArrayList<String> amounts = new ArrayList<>();

        for (int i = 0; i < containerLayout.getChildCount(); i++) {
            View row = containerLayout.getChildAt(i);
            EditText nameInput = row.findViewById(R.id.personName);
            EditText amountInput = row.findViewById(R.id.splitAmount);

            String name = nameInput.getText().toString().trim();
            String amtStr = amountInput.getText().toString().trim();

            if (!amtStr.isEmpty()) {
                try {
                    double val = Double.parseDouble(amtStr);
                    currentSum += val;
                    names.add(name.isEmpty() ? "Person " + (i + 1) : name);
                    amounts.add(amtStr);
                } catch (NumberFormatException e) {
                    Toast.makeText(this, "Invalid amount in a row", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        }

        if (Math.abs(currentSum - totalBill) > 0.01) {
            Toast.makeText(this,
                    "The total must be ₱" + String.format("%.2f", totalBill),
                    Toast.LENGTH_LONG).show();
        } else {
            Intent resultIntent = new Intent();
            resultIntent.putStringArrayListExtra("split_names", names);
            resultIntent.putStringArrayListExtra("split_amounts", amounts);
            setResult(Activity.RESULT_OK, resultIntent);
            finish();
        }
    }
}
