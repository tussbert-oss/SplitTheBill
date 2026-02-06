package com.example.splititapp;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity {

    // UI elements
    private EditText fullname, email, password, confirmpassword, birthdate;
    private Button signupbtn;
    private ImageButton backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize UI elements
        fullname = findViewById(R.id.fullname);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        confirmpassword = findViewById(R.id.confirmpassword);
        birthdate = findViewById(R.id.birthdate);
        signupbtn = findViewById(R.id.signupbtn);
        backButton = findViewById(R.id.imageButton); // Back button

        // Back button click
        backButton.setOnClickListener(v -> finish());

        // Date picker
        birthdate.setOnClickListener(v -> {
            final java.util.Calendar c = java.util.Calendar.getInstance();
            int year = c.get(java.util.Calendar.YEAR);
            int month = c.get(java.util.Calendar.MONTH);
            int day = c.get(java.util.Calendar.DAY_OF_MONTH);

            android.app.DatePickerDialog datePickerDialog = new android.app.DatePickerDialog(
                    Register.this,
                    (view, selectedYear, selectedMonth, selectedDay) -> {
                        String date = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                        birthdate.setText(date);
                    },
                    year, month, day
            );
            datePickerDialog.show();
        });

        // Signup button click
        signupbtn.setOnClickListener(v -> {
            if (validateRegisterInput()) {
                sendDataToXAMPP();
            }
        });
    }

    // Validate user input
    private boolean validateRegisterInput() {
        String nameText = fullname.getText().toString().trim();
        String emailText = email.getText().toString().trim();
        String passwordText = password.getText().toString().trim();
        String confirmText = confirmpassword.getText().toString().trim();
        String birthdateText = birthdate.getText().toString().trim();

        if (nameText.isEmpty()) {
            fullname.setError("Full name is required");
            fullname.requestFocus();
            return false;
        }
        if (emailText.isEmpty()) {
            email.setError("Email is required");
            email.requestFocus();
            return false;
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(emailText).matches()) {
            email.setError("Invalid email");
            email.requestFocus();
            return false;
        }
        if (passwordText.isEmpty()) {
            password.setError("Password is required");
            password.requestFocus();
            return false;
        }
        if (passwordText.length() < 6) {
            password.setError("Password must be at least 6 characters");
            password.requestFocus();
            return false;
        }
        if (!passwordText.equals(confirmText)) {
            confirmpassword.setError("Passwords do not match");
            confirmpassword.requestFocus();
            return false;
        }
        if (birthdateText.isEmpty()) {
            birthdate.setError("Birthdate is required");
            birthdate.requestFocus();
            return false;
        }
        return true;
    }

    // Send data to XAMPP server
    private void sendDataToXAMPP() {
        String url = "http://localhost/split_it/register.php";

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    Toast.makeText(Register.this, response, Toast.LENGTH_SHORT).show();
                    if (response.contains("Successful")) {
                        finish();
                    }
                },
                error -> Toast.makeText(Register.this, "Error connecting to XAMPP", Toast.LENGTH_SHORT).show()
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("fullname", fullname.getText().toString().trim());
                params.put("email", email.getText().toString().trim());
                params.put("password", password.getText().toString().trim());
                params.put("birthdate", birthdate.getText().toString().trim());
                return params;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }
}
