package com.app.veggiesvilla;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

public class ReviewsAdapter extends FirebaseRecyclerAdapter<Review, ReviewsAdapter.ViewHolder> {

    public class ViewHolder extends RecyclerView.ViewHolder{

        private TextView txtUserName, txtReview, txtDate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtUserName = itemView.findViewById(R.id.txtUserName);
            txtReview = itemView.findViewById(R.id.txtReview);
            txtDate = itemView.findViewById(R.id.txtDate);
        }
    }

    public ReviewsAdapter(@NonNull FirebaseRecyclerOptions<Review> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull ReviewsAdapter.ViewHolder viewHolder, int i, @NonNull Review review) {
        viewHolder.txtUserName.setText(review.getUsername());
        viewHolder.txtReview.setText(review.getText());
        viewHolder.txtDate.setText(review.getDate());
    }

    @NonNull
    @Override
    public ReviewsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.review_layout, parent, false);
        return new ViewHolder(view);
    }

}
