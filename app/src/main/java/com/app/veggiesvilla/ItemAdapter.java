package com.app.veggiesvilla;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static com.app.veggiesvilla.ItemActivity.GROCERY_ITEM_KEY;

public class ItemAdapter extends FirebaseRecyclerAdapter<Item, ItemAdapter.ViewHolder> {

    double overallItemPrice = 0.0;
    double totalPriceFinal = 0.0;

    final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    String phoneNumber = currentUser.getPhoneNumber();

    private ArrayList<Item> items = new ArrayList<>();

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView txtItemName, txtMrp, txtQty;
        ImageView imgItem, imgSub, imgAdd;
        Button btnAdd;
        CardView parent;

        public ViewHolder(@NonNull final View itemView) {
            super(itemView);
            txtItemName = itemView.findViewById(R.id.txtItemName);
            txtMrp = itemView.findViewById(R.id.txtMrp);
            txtQty = itemView.findViewById(R.id.txtQty);
            imgItem = itemView.findViewById(R.id.imgItem);
            imgSub = itemView.findViewById(R.id.imgSub);
            imgAdd = itemView.findViewById(R.id.imgAdd);
            btnAdd = itemView.findViewById(R.id.btnAdd);
            parent = itemView.findViewById(R.id.parent);
        }
    }

    public ItemAdapter(@NonNull FirebaseRecyclerOptions<Item> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int i, @NonNull final Item item) {
        viewHolder.txtItemName.setText(item.getName());
        viewHolder.txtMrp.setText(String.valueOf(item.getPrice()));
        Glide.with(viewHolder.imgItem.getContext()).asBitmap().load(item.getImageUrl()).into(viewHolder.imgItem);

        viewHolder.imgSub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(item.getQuantity() == 1){
                    item.setQuantity(1);
                }else if(item.getQuantity() > 1){
                    item.setQuantity(item.getQuantity()-1);
                }
                viewHolder.txtQty.setText(String.valueOf(item.getQuantity()));
            }
        });

        viewHolder.imgAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                item.setQuantity(item.getQuantity()+1);
                viewHolder.txtQty.setText(String.valueOf(item.getQuantity()));
            }
        });

        viewHolder.btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double price = item.getPrice();
                int quantity = item.getQuantity();
                overallItemPrice = (price)*(quantity);

                final DatabaseReference cartItemsRef = FirebaseDatabase.getInstance().getReference("Users").child(phoneNumber).child("Cart").child("Items");
                Item item1 = new Item(item.getId(), item.getName(), item.getPrice(), Integer.parseInt((String) viewHolder.txtQty.getText()), overallItemPrice);
                cartItemsRef.child(item.getId()).setValue(item1);

                onAddItem(item);
            }
        });

        viewHolder.parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(viewHolder.imgItem.getContext(), ItemActivity.class);
                intent.putExtra(GROCERY_ITEM_KEY, item);
                viewHolder.imgItem.getContext().startActivity(intent);
            }
        });
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout, parent, false);
        return new ViewHolder(view);
    }

    public void onAddItem(final Item item) {
        DatabaseReference cartItemRef = FirebaseDatabase.getInstance().getReference().child("Users").child(phoneNumber).child("Cart").child("Items").child(item.getId());
        cartItemRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    final Item item1 = snapshot.getValue(Item.class);
                    final DatabaseReference cartTotalPriceRef = FirebaseDatabase.getInstance().getReference().child("Users").child(phoneNumber).child("Cart").child("TotalPrice");
                    cartTotalPriceRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.exists()) {
                                totalPriceFinal = snapshot.getValue(Double.class);
                                double overallItemPrice = item1.getOverallItemPrice();
                                totalPriceFinal += overallItemPrice;
                                cartTotalPriceRef.setValue(totalPriceFinal);
                            } else {
                                double overallItemPrice = item1.getOverallItemPrice();
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

    public void setItems(ArrayList<Item> items) {
        this.items = items;
        notifyDataSetChanged();
    }
}
