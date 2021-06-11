package com.app.veggiesvilla;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FirstCartFragment extends Fragment implements CartAdapter.ItemCallback {

    RecyclerView cartItemsRecView;
    CartAdapter cartAdapter;

    TextView txtCartDescription, txtTotalPrice;
    Button btnNext;
    LinearLayout btnTotalPrice;

    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    String phoneNumber = currentUser.getPhoneNumber();

    Double totalPriceFinal = 0.0;

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_first_cart, container, false);

        cartItemsRecView = view.findViewById(R.id.cartItemsRecView);
        cartItemsRecView.setLayoutManager(new LinearLayoutManager(getContext()));
        txtCartDescription = view.findViewById(R.id.txtCartDescription);
        txtTotalPrice = view.findViewById(R.id.txtTotalPrice);
        btnTotalPrice = view.findViewById(R.id.btnTotalPrice);
        btnNext = view.findViewById(R.id.btnNext);

        DatabaseReference cartItemsRef = FirebaseDatabase.getInstance().getReference().child("Users").child(phoneNumber).child("Cart").child("Items");
        cartItemsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    DatabaseReference cartTotalPriceRef = FirebaseDatabase.getInstance().getReference().child("Users").child(phoneNumber).child("Cart").child("TotalPrice");
                    cartTotalPriceRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            totalPriceFinal = snapshot.getValue(Double.class);
                            txtTotalPrice.setText(totalPriceFinal.toString());
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                } else {
                    txtCartDescription.setText("Empty Cart!");
                    btnTotalPrice.setVisibility(View.INVISIBLE);
                    btnNext.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        FirebaseRecyclerOptions<Item> options =
                new FirebaseRecyclerOptions.Builder<Item>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("Users").child(phoneNumber).child("Cart").child("Items"), Item.class)
                        .build();

        cartAdapter = new CartAdapter(options, this);
        cartItemsRecView.setAdapter(cartAdapter);

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.container, new SecondCartFragment());
                fragmentTransaction.commit();
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        cartAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        cartAdapter.stopListening();
    }

    @Override
    public void updateTextView() {
        txtTotalPrice.setText(cartAdapter.totalPriceFinal.toString());
        if(cartAdapter.totalPriceFinal == 0 || cartAdapter.totalPriceFinal == 0.0) {
            txtCartDescription.setText("Empty Cart!");
            btnTotalPrice.setVisibility(View.INVISIBLE);
            btnNext.setVisibility(View.INVISIBLE);
        }
        DatabaseReference cartTotalPriceRef = FirebaseDatabase.getInstance().getReference().child("Users").child(phoneNumber).child("Cart").child("TotalPrice");
        cartTotalPriceRef.setValue(cartAdapter.totalPriceFinal);
    }
}