package com.app.veggiesvilla;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;

public class CartActivity extends AppCompatActivity implements ThirdCartFragment.OnDataPass {

    private MaterialToolbar materialToolbar;
    BottomNavigationView bottomNavigationView;

    String oOrderId, oPaymentMode, oPaymentStatus, oAddress, oPhone, oPhoneNumber, oPrice;

    SimpleDateFormat currentDateFormatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    Date currentDate = new Date();
    String orderDate = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        initViews();
        setSupportActionBar(materialToolbar);

        orderDate = currentDateFormatter.format(currentDate);

        materialToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(item.getItemId() == R.id.orders){
                    Intent ordersIntent = new Intent(CartActivity.this, OrdersActivity.class);
                    startActivity(ordersIntent);
                }
                return false;
            }
        });

        bottomNavigationView.setSelectedItemId(R.id.cart);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.home:
                        Intent homeIntent = new Intent(CartActivity.this, MainActivity.class);
                        homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(homeIntent);
                        break;
                    case R.id.search:
                        Intent searchIntent = new Intent(CartActivity.this, SearchActivity.class);
                        startActivity(searchIntent);
                        break;
                    case R.id.cart:
                        break;
                    default:
                        break;
                }
                return false;
            }
        });

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, new FirstCartFragment());
        transaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.orders_navigation_menu, menu);
        return true;
    }

    @Override
    public void onDataPass(String orderId, String paymentMode, String paymentStatus, String cAddress, String cPhone, String cPhoneNumber, String cPrice) {
        oOrderId = orderId;
        oPaymentMode = paymentMode;
        oPaymentStatus = paymentStatus;
        oAddress = cAddress;
        oPhone = cPhone;
        oPhoneNumber = cPhoneNumber;
        oPrice = cPrice;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode,Intent data) {
        String TAG = "CartActivity";
        Log.e(TAG ," result code " + resultCode);
        // -1 means successful  // 0 means failed
        // one error is - nativeSdkForMerchantMessage : networkError
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2 && data != null) {
            Bundle bundle = data.getExtras();
            if (bundle != null) {
                for (String key : bundle.keySet()) {
                    Log.e(TAG, key + " : " + (bundle.get(key) != null ? bundle.get(key) : "NULL"));
                }
            }
            Log.e(TAG, " data " + data.getStringExtra("nativeSdkForMerchantMessage"));
            Log.e(TAG, " data response - " + data.getStringExtra("response"));
            if(resultCode == -1) {
                Order order = new Order(oOrderId, oPaymentMode, oPaymentStatus, oPrice, oAddress, oPhone, "Pending", orderDate);
                DatabaseReference ordersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(oPhoneNumber).child("Orders");
                ordersRef.child(order.getId()).setValue(order);
                final DatabaseReference cartItemsRef = FirebaseDatabase.getInstance().getReference().child("Users").child(oPhoneNumber).child("Cart").child("Items");
                cartItemsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            Item item = dataSnapshot.getValue(Item.class);
                            ordersRef.child(order.getId()).child("Items").child(item.getId()).setValue(item);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });

                DatabaseReference cartRef = FirebaseDatabase.getInstance().getReference().child("Users").child(oPhoneNumber).child("Cart");
                cartRef.removeValue();

                Bundle bundle2 = new Bundle();
                bundle2.putInt("resultCode", resultCode);
                PaymentResultFragment fragment = new PaymentResultFragment();
                fragment.setArguments(bundle2);
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.container, fragment);
                fragmentTransaction.commit();
            }
        }
        else {
            Log.e(TAG, " payment failed");
        }
    }

    private void initViews(){
        materialToolbar = findViewById(R.id.materialToolbar);
        bottomNavigationView = findViewById(R.id.bottomNavView);
    }
}