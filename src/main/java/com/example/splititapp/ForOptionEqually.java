package com.example.splititapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class ForOptionEqually extends AppCompatActivity {

    LinearLayout containerLayout;
    double totalBill = 0.0;
    boolean isDeleteMode = false;
    private ImageButton backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_for_option_equally);

        containerLayout = findViewById(R.id.containerLayout);
        Button addButton = findViewById(R.id.button4);
        Button saveButton = findViewById(R.id.button3);
        Button deleteButton = findViewById(R.id.button8);
        backButton = findViewById(R.id.imageButton7);

        // Get total amount
        String amount = getIntent().getStringExtra("total_amount");
        try {
            if (amount != null) totalBill = Double.parseDouble(amount);
        } catch (NumberFormatException e) {
            totalBill = 0.0;
        }

        // Add, Save, Delete, Back listeners
        addButton.setOnClickListener(v -> addNewRow());
        saveButton.setOnClickListener(v -> saveAndReturn());
        deleteButton.setOnClickListener(v -> toggleDeleteMode());

        backButton.setOnClickListener(v -> {
            if (isDeleteMode) {
                exitDeleteMode();
                Toast.makeText(this, "Delete mode cancelled", Toast.LENGTH_SHORT).show();
            } else {
                goBackToBreakdownPage();
            }
        });

        addNewRow();
    }

    // Hardware back button
    @Override
    public void onBackPressed() {
        if (isDeleteMode) {
            exitDeleteMode();
            Toast.makeText(this, "Delete mode cancelled", Toast.LENGTH_SHORT).show();
        } else {
            goBackToBreakdownPage();
        }
    }

    private void goBackToBreakdownPage() {
        new AlertDialog.Builder(this)
                .setTitle("Go Back")
                .setMessage("Are you sure you want to go back? Unsaved changes will be lost.")
                .setPositiveButton("Yes", (dialog, which) -> {
                    setResult(RESULT_CANCELED);
                    finish();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void addNewRow() {
        View rowView = getLayoutInflater().inflate(R.layout.row_person, null);

        EditText amountInput = rowView.findViewById(R.id.splitAmount);
        CheckBox deleteCheckBox = rowView.findViewById(R.id.checkSelect);

        // Amount is disabled in equal split mode
        amountInput.setFocusable(false);
        amountInput.setEnabled(false);
        amountInput.setCursorVisible(false);

        deleteCheckBox.setVisibility(View.GONE);

        containerLayout.addView(rowView);
        calculateEqually();
    }

    private void toggleDeleteMode() {
        if (!isDeleteMode) enterDeleteMode();
        else exitDeleteMode();
    }

    private void enterDeleteMode() {
        isDeleteMode = true;
        Button deleteButton = findViewById(R.id.button8);
        Button addButton = findViewById(R.id.button4);
        Button saveButton = findViewById(R.id.button3);

        deleteButton.setText("Delete Selected");
        addButton.setEnabled(false);
        saveButton.setEnabled(false);

        // Show checkboxes
        for (int i = 0; i < containerLayout.getChildCount(); i++) {
            View row = containerLayout.getChildAt(i);
            CheckBox checkBox = row.findViewById(R.id.checkSelect);
            checkBox.setVisibility(View.VISIBLE);
            checkBox.setChecked(false);
        }

        deleteButton.setOnClickListener(v -> confirmDelete());
    }

    private void exitDeleteMode() {
        isDeleteMode = false;
        Button deleteButton = findViewById(R.id.button8);
        Button addButton = findViewById(R.id.button4);
        Button saveButton = findViewById(R.id.button3);

        deleteButton.setText("Delete");
        deleteButton.setOnClickListener(v -> toggleDeleteMode());

        addButton.setEnabled(true);
        saveButton.setEnabled(true);

        for (int i = 0; i < containerLayout.getChildCount(); i++) {
            View row = containerLayout.getChildAt(i);
            CheckBox checkBox = row.findViewById(R.id.checkSelect);
            checkBox.setVisibility(View.GONE);
            checkBox.setChecked(false);
        }
    }

    private void confirmDelete() {
        int count = containerLayout.getChildCount();
        ArrayList<String> selectedNames = new ArrayList<>();
        int selectedCount = 0;

        for (int i = 0; i < count; i++) {
            View row = containerLayout.getChildAt(i);
            CheckBox cb = row.findViewById(R.id.checkSelect);
            if (cb.isChecked()) {
                selectedCount++;
                EditText nameInput = row.findViewById(R.id.personName);
                String name = nameInput.getText().toString().trim();
                selectedNames.add(name.isEmpty() ? "Person " + (i + 1) : name);
            }
        }

        if (selectedCount == 0) {
            Toast.makeText(this, "Select at least one person to delete", Toast.LENGTH_SHORT).show();
            return;
        }
        if (selectedCount == count) {
            Toast.makeText(this, "At least one person must remain", Toast.LENGTH_SHORT).show();
            return;
        }

        StringBuilder message = new StringBuilder();
        message.append("Are you sure you want to delete ");
        if (selectedCount == 1) message.append("this person?\n\n").append(selectedNames.get(0));
        else {
            message.append("these ").append(selectedCount).append(" people?\n\n");
            for (int i = 0; i < Math.min(selectedNames.size(), 3); i++) message.append("• ").append(selectedNames.get(i)).append("\n");
            if (selectedCount > 3) message.append("• and ").append(selectedCount - 3).append(" more...");
        }

        new AlertDialog.Builder(this)
                .setTitle("Confirm Delete")
                .setMessage(message.toString())
                .setPositiveButton("Delete", (dialog, which) -> performDelete())
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void performDelete() {
        int count = containerLayout.getChildCount();
        int deletedCount = 0;

        for (int i = count - 1; i >= 0; i--) {
            View row = containerLayout.getChildAt(i);
            CheckBox cb = row.findViewById(R.id.checkSelect);
            if (cb.isChecked()) {
                if (containerLayout.getChildCount() <= 1) {
                    Toast.makeText(this, "At least one person is required", Toast.LENGTH_SHORT).show();
                    exitDeleteMode();
                    return;
                }
                containerLayout.removeView(row);
                deletedCount++;
            }
        }

        if (deletedCount > 0) {
            Toast.makeText(this,
                    deletedCount == 1 ? "1 person deleted successfully" : deletedCount + " people deleted successfully",
                    Toast.LENGTH_SHORT).show();
            calculateEqually();
        }

        exitDeleteMode();
    }

    private void calculateEqually() {
        int personCount = containerLayout.getChildCount();
        if (personCount == 0) return;

        double share = totalBill / personCount;
        for (int i = 0; i < personCount; i++) {
            View row = containerLayout.getChildAt(i);
            EditText amount = row.findViewById(R.id.splitAmount);
            if (amount != null) amount.setText(String.format("%.2f", share));
        }
    }

    private void saveAndReturn() {
        ArrayList<String> names = new ArrayList<>();
        ArrayList<String> amounts = new ArrayList<>();

        int personCount = containerLayout.getChildCount();
        if (personCount == 0) return;

        double share = totalBill / personCount;
        String shareString = String.format("%.2f", share);

        for (int i = 0; i < personCount; i++) {
            View row = containerLayout.getChildAt(i);
            EditText nameInput = row.findViewById(R.id.personName);
            String name = nameInput.getText().toString().trim();
            names.add(name.isEmpty() ? "Person " + (i + 1) : name);
            amounts.add(shareString);
        }

        Intent resultIntent = new Intent();
        resultIntent.putStringArrayListExtra("split_names", names);
        resultIntent.putStringArrayListExtra("split_amounts", amounts);
        setResult(RESULT_OK, resultIntent);
        finish();
    }
}
