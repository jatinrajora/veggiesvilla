package com.app.veggiesvilla;

import android.content.Intent;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

public class OrdersAdapter extends FirebaseRecyclerAdapter<Order, OrdersAdapter.ViewHolder> {

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView txtOrderId, txtOrderDate, txtOrderDetails;

        public ViewHolder(@NonNull final View orderView) {
            super(orderView);
            txtOrderId = orderView.findViewById(R.id.txtOrderId);
            txtOrderDate = orderView.findViewById(R.id.txtOrderDate);
            txtOrderDetails = orderView.findViewById(R.id.txtOrderDetails);
        }
    }

    public OrdersAdapter(@NonNull FirebaseRecyclerOptions<Order> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull OrdersAdapter.ViewHolder viewHolder, int i, @NonNull Order order) {
        viewHolder.txtOrderId.setText(order.getId());
        viewHolder.txtOrderDate.setText(order.getOrderDate());
        viewHolder.txtOrderDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(viewHolder.txtOrderId.getContext(), OrderDetailActivity.class);
                intent.putExtra("orderId", viewHolder.txtOrderId.getText());
                viewHolder.txtOrderId.getContext().startActivity(intent);
            }
        });
    }

    @NonNull
    @Override
    public OrdersAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_layout, parent, false);
        return new OrdersAdapter.ViewHolder(view);
    }
}
