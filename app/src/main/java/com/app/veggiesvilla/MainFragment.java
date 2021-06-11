package com.app.veggiesvilla;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.FirebaseDatabase;

public class MainFragment extends Fragment {

    RecyclerView itemsRecyclerView;
    BottomNavigationView bottomNavigationView;
    ItemAdapter itemAdapter;

    public MainFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        itemsRecyclerView = view.findViewById(R.id.itemsRecView);
        itemsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        bottomNavigationView = view.findViewById(R.id.bottomNavView);

        FirebaseRecyclerOptions<Item> options =
                new FirebaseRecyclerOptions.Builder<Item>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("Category").child("Vegitables"), Item.class)
                        .build();

        itemAdapter = new ItemAdapter(options);
        itemsRecyclerView.setAdapter(itemAdapter);

        bottomNavigationView.setSelectedItemId(R.id.home);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.home:
                        break;
                    case R.id.search:
                        Intent searchIntent = new Intent(getContext(), SearchActivity.class);
                        startActivity(searchIntent);
                        break;
                    case R.id.cart:
                        Intent cartIntent = new Intent(getContext(), CartActivity.class);
                        startActivity(cartIntent);
                        break;
                    default:
                        break;
                }
                return false;
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        itemAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        itemAdapter.stopListening();
    }
}
