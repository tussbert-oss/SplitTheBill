package com.example.splititapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class PrivacyAndSupport extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_privacy_and_support);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ImageButton btnBack = findViewById(R.id.backbtn);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        Button tvContact = findViewById(R.id.contactbtn);
        if (tvContact != null) {
            tvContact.setOnClickListener(v -> {
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:support@splititapp.com"));
                intent.putExtra(Intent.EXTRA_SUBJECT, "SplitIt Support Request");
                try {
                    startActivity(intent);
                } catch (Exception e) {
                    Toast.makeText(this, "No email app found", Toast.LENGTH_SHORT).show();
                }
            });
        }

        Button btnPrivacy = findViewById(R.id.privacybtn);
        btnPrivacy.setOnClickListener(v -> {
            Toast.makeText(this, "Privacy Policy coming soon!", Toast.LENGTH_SHORT).show();
        });

        Button btnTerms = findViewById(R.id.termsbtn);
        btnTerms.setOnClickListener(v -> {
            Toast.makeText(this, "Terms of Service coming soon!", Toast.LENGTH_SHORT).show();
        });

    }
}