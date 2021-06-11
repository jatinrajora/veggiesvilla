package com.app.veggiesvilla;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SecondCartFragment extends Fragment {

    EditText edtCAddress, edtCPhone;
    Button btnBack, btnNext;

    String databaseAddress;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_second_cart, container, false);

        edtCAddress = view.findViewById(R.id.edtCAddress);
        edtCPhone = view.findViewById(R.id.edtCPhone);
        btnBack = view.findViewById(R.id.btnBack);
        btnNext = view.findViewById(R.id.btnNext);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        final String phoneNumber = currentUser.getPhoneNumber();
        edtCPhone.setText(phoneNumber);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(phoneNumber).child("address");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    databaseAddress = snapshot.getValue(String.class);
                    edtCAddress.setText(databaseAddress);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.container, new FirstCartFragment());
                fragmentTransaction.commit();
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(databaseAddress != edtCAddress.getText().toString() || phoneNumber != edtCPhone.getText().toString()) {
                    DatabaseReference userAddressRef = FirebaseDatabase.getInstance().getReference().child("Users").child(phoneNumber).child("address");
                    userAddressRef.setValue(edtCAddress.getText().toString());
                    DatabaseReference userPhoneRef = FirebaseDatabase.getInstance().getReference().child("Users").child(phoneNumber).child("phone");
                    userPhoneRef.setValue(edtCPhone.getText().toString());
                }
                FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.container, new ThirdCartFragment());
                fragmentTransaction.commit();
            }
        });

        return view;
    }
}