package com.app.veggiesvilla;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    TextView signUp;
    EditText edtLPhone;
    Button btnLogin;
    FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initializeViews();

        MaterialToolbar materialToolbar = findViewById(R.id.materialToolbar);
        setSupportActionBar(materialToolbar);

        if(currentUser != null) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("Phone", edtLPhone.getText().toString());
            startActivity(intent);
            finish();
        } else {
            btnLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String phoneNo = edtLPhone.getText().toString().trim();
                    if (phoneNo.equals("") || phoneNo.length() < 12) {
                        edtLPhone.setError("Enter a registered phone number!");
                        edtLPhone.requestFocus();
                        return;
                    }
                    Intent loginIntent = new Intent(LoginActivity.this, VerifyPhoneActivity.class);
                    loginIntent.putExtra("phone", phoneNo);
                    startActivity(loginIntent);
                }
            });
        }

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signUpIntent = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(signUpIntent);
            }
        });
    }

    public void initializeViews() {
        signUp = findViewById(R.id.signUp);
        edtLPhone = findViewById(R.id.edtLPhone);
        btnLogin = findViewById(R.id.btnLogin);
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
    }
}
