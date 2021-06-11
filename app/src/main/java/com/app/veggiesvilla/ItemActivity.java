package com.app.veggiesvilla;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ItemActivity extends AppCompatActivity {

    MaterialToolbar toolbar;
    Button btnAddToCart;
    TextView txtName;
    TextView txtPrice;
    ImageView itemImage;
    TextView txtDescription;
    TextView txtAddReview;

    RecyclerView reviewsRecView;
    ReviewsAdapter reviewsAdapter;

    private Item incomingItem;
    public static final String GROCERY_ITEM_KEY = "incoming_item";

    double overallItemPrice = 0.0;
    double totalPriceFinal = 0.0;

    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    String phoneNumber = currentUser.getPhoneNumber();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item);

        Intent incomingItemIntent = getIntent();
        incomingItem = incomingItemIntent.getParcelableExtra(GROCERY_ITEM_KEY);

        initViews();
        toolbar.setTitle(incomingItem.getName());
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        txtName.setText(incomingItem.getName());
        txtPrice.setText(String.valueOf(incomingItem.getPrice()));
        Glide.with(this).asBitmap().load(incomingItem.getImageUrl()).into(itemImage);
        txtDescription.setText(incomingItem.getDescription());

        btnAddToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double price = incomingItem.getPrice();
                int quantity = incomingItem.getQuantity();
                overallItemPrice = (price)*(quantity);

                final DatabaseReference cartItemsRef = FirebaseDatabase.getInstance().getReference("Users").child(phoneNumber).child("Cart").child("Items");
                Item item = new Item(incomingItem.getId(), incomingItem.getName(), incomingItem.getPrice(), incomingItem.getQuantity(), overallItemPrice);
                cartItemsRef.child(item.getId()).setValue(item);

                onAddItem(incomingItem);

                Intent cartIntent = new Intent(ItemActivity.this, CartActivity.class);
                cartIntent.putExtra("cartItem", incomingItem);
                startActivity(cartIntent);
            }
        });

        reviewsRecView.setLayoutManager(new LinearLayoutManager(this));
        FirebaseRecyclerOptions<Review> options =
                new FirebaseRecyclerOptions.Builder<Review>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("Category").child("Vegitables").child(incomingItem.getId()).child("reviews"), Review.class)
                        .build();

        reviewsAdapter = new ReviewsAdapter(options);
        reviewsRecView.setAdapter(reviewsAdapter);

        txtAddReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddReviewDialog dialog = new AddReviewDialog();
                Bundle bundle = new Bundle();
                bundle.putParcelable(GROCERY_ITEM_KEY, incomingItem);
                dialog.setArguments(bundle);
                dialog.show(getSupportFragmentManager(), "add review");
            }
        });
    }

    public void initViews(){
        toolbar = findViewById(R.id.toolbar);
        btnAddToCart = findViewById(R.id.btnAddToCart);
        txtName = findViewById(R.id.txtName);
        txtPrice = findViewById(R.id.txtPrice);
        itemImage = findViewById(R.id.itemImage);
        txtDescription = findViewById(R.id.txtDescription);
        txtAddReview = findViewById(R.id.txtAddReview);
        reviewsRecView = findViewById(R.id.reviewsRecView);
    }

    public void onAddItem(Item item) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String phoneNumber = currentUser.getPhoneNumber();
        DatabaseReference cartItemRef = FirebaseDatabase.getInstance().getReference().child("Users").child(phoneNumber).child("Cart").child("Items").child(item.getId());
        cartItemRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    final Item cartItem = snapshot.getValue(Item.class);
                    final DatabaseReference cartTotalPriceRef = FirebaseDatabase.getInstance().getReference().child("Users").child(phoneNumber).child("Cart").child("TotalPrice");
                    cartTotalPriceRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.exists()) {
                                totalPriceFinal = snapshot.getValue(Double.class);
                                double overallItemPrice = cartItem.getOverallItemPrice();
                                totalPriceFinal += overallItemPrice;
                                cartTotalPriceRef.setValue(totalPriceFinal);
                            } else {
                                double overallItemPrice = cartItem.getOverallItemPrice();
                                totalPriceFinal += overallItemPrice;
                                cartTotalPriceRef.setValue(totalPriceFinal);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });
                }
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

    @Override
    public void onStart() {
        super.onStart();
        reviewsAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        reviewsAdapter.stopListening();
    }
}