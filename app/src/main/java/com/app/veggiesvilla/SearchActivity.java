package com.app.veggiesvilla;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {

    private MaterialToolbar toolbar;
    private EditText searchBox;
    private ImageView btnSearchImage;
    private RecyclerView recyclerView;
    private BottomNavigationView bottomNavigationView;
    CustomSearchRecyclerAdapter adapter;
    ArrayList<Item> allItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        initViews();
        initBottomNavView();

        setSupportActionBar(toolbar);

        DatabaseReference itemsRef = FirebaseDatabase.getInstance().getReference().child("Category").child("Vegitables");
        itemsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Item item = dataSnapshot.getValue(Item.class);
                    allItems.add(item);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        btnSearchImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!searchBox.getText().toString().equals("")) {
                    String name = searchBox.getText().toString();
                    ArrayList<Item> items = searchForItems(name);
                    if (items.size() != 0){
                        recyclerView.setLayoutManager(new LinearLayoutManager(SearchActivity.this));
                        adapter = new CustomSearchRecyclerAdapter(SearchActivity.this, items);
                        recyclerView.setAdapter(adapter);
                    }
                }
            }
        });

        searchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!searchBox.getText().toString().equals("")){
                    String name = searchBox.getText().toString();
                    ArrayList<Item> items = searchForItems(name);
                    if (items.size() != 0){
                        recyclerView.setLayoutManager(new LinearLayoutManager(SearchActivity.this));
                        adapter = new CustomSearchRecyclerAdapter(SearchActivity.this, items);
                        recyclerView.setAdapter(adapter);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    public ArrayList<Item> searchForItems(String text) {

        if (allItems.size() != 0){
            ArrayList<Item> items = new ArrayList<>();
            for (Item item: allItems){
                if (item.getName().equalsIgnoreCase(text)){
                    items.add(item);
                }

                String[] names = item.getName().split(" ");
                for (int i=0; i<names.length; i++){
                    if (text.equalsIgnoreCase(names[i])){
                        boolean doesExist = false;

                        for (Item j: items){
                            if (j.getId() == item.getId()){
                                doesExist = true;
                            }
                        }

                        if (!doesExist){
                            items.add(item);
                        }
                    }
                }
            }

            return items;
        }

        return null;
    }

    private void initBottomNavView(){
        bottomNavigationView.setSelectedItemId(R.id.search);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.home:
                        Intent homeIntent = new Intent(SearchActivity.this, MainActivity.class);
                        homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(homeIntent);
                        break;

                    case R.id.search:
                        break;

                    case R.id.cart:
                        Intent cartIntent = new Intent(SearchActivity.this, CartActivity.class);
                        cartIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(cartIntent);
                        break;

                    default:
                        break;
                }
                return false;
            }
        });
    }

    private void initViews(){
        toolbar = findViewById(R.id.toolbar);
        searchBox = findViewById(R.id.searchBox);
        btnSearchImage = findViewById(R.id.btnSearchImage);
        recyclerView = findViewById(R.id.recyclerViewSearch);
        bottomNavigationView = findViewById(R.id.bottomNavView);
    }

    /*@Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }*/
}