package com.example.splititapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ByOption extends AppCompatActivity {

    Button savebtn;
    ImageButton backbtn;
    EditText groupPayerEditText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_by_option);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        savebtn = findViewById(R.id.buttonSave);
        backbtn = findViewById(R.id.imageButtonBack);
        groupPayerEditText = findViewById(R.id.groupPayerEditText);

        backbtn.setOnClickListener(v -> finish());

        savebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = groupPayerEditText.getText().toString().trim();

                if (!name.isEmpty()){
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("payer_name",name);
                    setResult(Activity.RESULT_OK,resultIntent);
                    finish();
                }else{
                    Toast.makeText(ByOption.this,"Please enter a name",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}