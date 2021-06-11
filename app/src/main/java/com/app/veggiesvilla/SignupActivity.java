package com.app.veggiesvilla;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignupActivity extends AppCompatActivity {

    EditText name, phone, email, address;
    Button btnSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        name = findViewById(R.id.edtSName);
        phone = findViewById(R.id.edtSPhone);
        email = findViewById(R.id.edtSEmail);
        address = findViewById(R.id.edtSAddress);
        btnSignUp = findViewById(R.id.btnSignUp);

        MaterialToolbar materialToolbar = findViewById(R.id.materialToolbar);
        setSupportActionBar(materialToolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sname = name.getText().toString();
                String sphone = phone.getText().toString();
                String semail = email.getText().toString();
                String saddress = address.getText().toString();

                if(sname.equals("") && sphone.equals("") && saddress.equals("")) {
                    Toast.makeText(getApplicationContext(),"Fields cannot be empty.", Toast.LENGTH_LONG).show();
                }
                else {
                    Intent verifyPhoneIntent = new Intent(SignupActivity.this, VerifyPhoneActivity.class);
                    verifyPhoneIntent.putExtra("phone", sphone);
                    startActivity(verifyPhoneIntent);
                    DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("Users");
                    User user = new User(sname, sphone, semail, saddress);
                    usersRef.child(sphone).setValue(user);
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}