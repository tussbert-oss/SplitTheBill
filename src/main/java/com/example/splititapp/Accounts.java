package com.example.splititapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

public class Accounts extends AppCompatActivity {

    private EditText etName, etEmail, etBirthdate, etPassword;
    private TextView tvHeaderName, tvHeaderEmail;
    private ImageButton btnEditToggle, btnBack;
    private Button btnSave;
    private boolean isEditMode = false;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accounts);

        SharedPreferences pref = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        userId = pref.getString("user_id", null);

        if (userId == null) {
            Toast.makeText(this, "Please login again", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etBirthdate = findViewById(R.id.etBirthdate);
        etPassword = findViewById(R.id.etPassword);
        tvHeaderName = findViewById(R.id.tvHeaderName);
        tvHeaderEmail = findViewById(R.id.tvHeaderEmail);
        btnEditToggle = findViewById(R.id.btnEditToggle);
        btnSave = findViewById(R.id.btnSave);
        btnBack = findViewById(R.id.btnBack);
        Button btnLogout = findViewById(R.id.logoutbtn);

        toggleFields(false);

        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        btnLogout.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Logout")
                    .setMessage("Are you sure you want to logout?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        SharedPreferences.Editor editor = pref.edit();
                        editor.clear();
                        editor.apply();
                        Intent intent = new Intent(Accounts.this, Login.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    })
                    .setNegativeButton("No", null)
                    .show();
        });

        fetchUserData();

        btnEditToggle.setOnClickListener(v -> {
            isEditMode = !isEditMode;
            toggleFields(isEditMode);
        });

        btnSave.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Save Changes")
                    .setMessage("Are you sure you want to update your profile?")
                    .setPositiveButton("Yes", (dialog, which) -> updateUserData())
                    .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                    .show();
        });
    }

    private void toggleFields(boolean enable) {
        etName.setEnabled(enable);
        etEmail.setEnabled(false);
        etBirthdate.setEnabled(enable);
        etPassword.setEnabled(enable);
        btnSave.setVisibility(enable ? View.VISIBLE : View.GONE);
        btnEditToggle.setImageResource(enable ? android.R.drawable.ic_menu_close_clear_cancel : android.R.drawable.ic_menu_edit);
    }

    private void fetchUserData() {
        String url = "http://10.0.2.2/split_it/get_profile.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    Log.d("API_RESPONSE", response);
                    try {
                        JSONObject obj = new JSONObject(response);
                        if (obj.has("fullname") || obj.has("name")) {
                            String name = obj.optString("fullname", obj.optString("name", "N/A"));
                            String email = obj.optString("email", "N/A");

                            etName.setText(name);
                            etEmail.setText(email);
                            etBirthdate.setText(obj.optString("birthdate", ""));
                            etPassword.setText(obj.optString("password", ""));

                            tvHeaderName.setText(name);
                            tvHeaderEmail.setText(email);
                        }
                    } catch (JSONException e) {
                        Log.e("JSON_ERROR", "Response was: " + response);
                    }
                }, error -> Log.e("API_ERROR", error.toString())) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", userId);
                return params;
            }
        };
        Volley.newRequestQueue(this).add(stringRequest);
    }

    private void updateUserData() {
        String url = "http://localhost/split_it/update_profile.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        JSONObject obj = new JSONObject(response);
                        Toast.makeText(this, obj.optString("message", "Updated!"), Toast.LENGTH_SHORT).show();

                        tvHeaderName.setText(etName.getText().toString());
                        tvHeaderEmail.setText(etEmail.getText().toString());

                        isEditMode = false;
                        toggleFields(false);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> Toast.makeText(this, "Update failed!", Toast.LENGTH_SHORT).show()) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", userId);
                params.put("fullname", etName.getText().toString());
                params.put("email", etEmail.getText().toString());
                params.put("birthdate", etBirthdate.getText().toString());
                params.put("password", etPassword.getText().toString());
                return params;
            }
        };
        Volley.newRequestQueue(this).add(stringRequest);
    }
}