package com.app.veggiesvilla;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CartAdapter extends FirebaseRecyclerAdapter<Item, CartAdapter.ViewHolder> {

    private Context context;

    Double totalPriceFinal = 0.0;
    Double totalPrice = 0.0;

    interface ItemCallback {
        void updateTextView();
    }

    ItemCallback itemCallback;

    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    String phoneNumber = currentUser.getPhoneNumber();

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView txtName, txtPrice, txtQty, txtDelete;

        public ViewHolder(@NonNull final View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.txtName);
            txtPrice = itemView.findViewById(R.id.txtPrice);
            txtQty = itemView.findViewById(R.id.txtQty);
            txtDelete = itemView.findViewById(R.id.txtDelete);
        }
    }

    public CartAdapter(@NonNull FirebaseRecyclerOptions<Item> options, ItemCallback itemCallback) {
        super(options);
        this.itemCallback = itemCallback;
    }

    @Override
    protected void onBindViewHolder(@NonNull CartAdapter.ViewHolder viewHolder, final int i, @NonNull final Item item) {
        context = viewHolder.txtName.getContext();
        viewHolder.txtName.setText(item.getName());
        viewHolder.txtPrice.setText(String.valueOf(item.getPrice()));
        viewHolder.txtQty.setText(String.valueOf(item.getQuantity()));
        viewHolder.txtDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context)
                        .setTitle("Deleting..")
                        .setMessage("Are you sure you want to delete this item?")
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                try {
                                    DatabaseReference cartItemRef = FirebaseDatabase.getInstance().getReference().child("Users").child(phoneNumber).child("Cart").child("Items").child(item.getId());
                                    cartItemRef.removeValue();
                                    onDeleteItem(item);
                                } catch (ClassCastException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                builder.create().show();
            }
        });
    }

    @NonNull
    @Override
    public CartAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_item_layout, parent, false);
        return new ViewHolder(view);
    }

    public void onDeleteItem(final Item item) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference cartTotalPriceRef = FirebaseDatabase.getInstance().getReference().child("Users").child(phoneNumber).child("Cart").child("TotalPrice");
        cartTotalPriceRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    totalPrice = snapshot.getValue(Double.class);
                    Double itemOverallPrice = item.getOverallItemPrice();
                    totalPriceFinal = (totalPrice) - (itemOverallPrice);
                    itemCallback.updateTextView();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
}
