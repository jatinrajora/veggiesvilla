package com.app.veggiesvilla;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
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

public class OrderDetailActivity extends AppCompatActivity {

    MaterialToolbar toolbar;
    TextView items, date, address, deliveryStatus, paymentMode, paymentStatus, totalPrice;

    final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    String phoneNumber = currentUser.getPhoneNumber();

    String orderId = null;
    String orderedItems = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);
        initViews();

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Intent intent = getIntent();
        orderId = intent.getStringExtra("orderId");

        DatabaseReference orderRef = FirebaseDatabase.getInstance().getReference().child("Users").child(phoneNumber).child("Orders").child(orderId);
        orderRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Order order = snapshot.getValue(Order.class);
                date.setText(order.getOrderDate());
                address.setText(order.getAddress());
                deliveryStatus.setText(order.getDeliveryStatus());
                paymentMode.setText(order.getPaymentMode());
                paymentStatus.setText(order.getPaymentStatus());
                totalPrice.setText(order.getTotalPrice());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        DatabaseReference orderedItemsRef = FirebaseDatabase.getInstance().getReference().child("Users").child(phoneNumber).child("Orders").child(orderId).child("Items");
        orderedItemsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Item item = dataSnapshot.getValue(Item.class);
                    orderedItems += "\n\t" + item.getName();
                }
                items.setText(orderedItems);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

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

    public void initViews(){
        toolbar = findViewById(R.id.orderDetailToolbar);
        items = findViewById(R.id.txtOrderDetailsItems);
        date = findViewById(R.id.txtOrderDetailsDate);
        address = findViewById(R.id.txtOrderDetailsAddress);
        deliveryStatus = findViewById(R.id.txtOrderDetailsDelivery);
        paymentMode = findViewById(R.id.txtOrderDetailsPaymentMode);
        paymentStatus = findViewById(R.id.txtOrderDetailsPaymentStatus);
        totalPrice = findViewById(R.id.txtOrderDetailsTotalPrice);
    }
}
