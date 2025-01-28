package com.devdroid.cardviewexmp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ConatctPage extends AppCompatActivity {
    private EditText text1, text2, text3, text4, text5;
    private MyDBHelper dbhelper;
    Button submitbtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contactpage);
        submitbtn=findViewById(R.id.submitbtn);

        //initialise
        text1 = findViewById(R.id.text1);
        text2 = findViewById(R.id.text2);
        text3 = findViewById(R.id.text3);
        text4 = findViewById(R.id.text4);
        text5 = findViewById(R.id.text5);

        // Initialize database helper
        dbhelper = new MyDBHelper(this);

        // Set up the button click listener
        submitbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String contact1 = text1.getText().toString();
                String contact2 = text2.getText().toString();
                String contact3 = text3.getText().toString();
                String contact4 = text4.getText().toString();
                String contact5 = text5.getText().toString();


                // Check if the first three contact fields are filled
                if (contact1.isEmpty() || contact2.isEmpty() || contact3.isEmpty()) {
                    Toast.makeText(ConatctPage.this, "Please fill in at least the first three contacts", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Insert contacts into the database
                boolean isInserted = dbhelper.insertContacts(contact1, contact2, contact3, contact4, contact5);

                if (isInserted) {
                    Toast.makeText(ConatctPage.this, "Contacts saved successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ConatctPage.this, "Failed to save contacts", Toast.LENGTH_SHORT).show();
                }
            }
        });






}

}