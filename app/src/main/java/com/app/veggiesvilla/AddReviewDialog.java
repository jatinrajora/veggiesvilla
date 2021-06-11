package com.app.veggiesvilla;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import static com.app.veggiesvilla.ItemActivity.GROCERY_ITEM_KEY;

public class AddReviewDialog extends DialogFragment {

    private TextView txtItemName, txtWarning;
    private TextView txtUsername;
    private EditText edtReview;
    private Button btnAddReview;
    String userId;
    String userName;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = getActivity().getLayoutInflater().inflate(R.layout.activity_add_review_dialog, null);
        initViews(view);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity()).setView(view);

        Bundle bundle = getArguments();
        if (bundle != null){
            final Item item = bundle.getParcelable(GROCERY_ITEM_KEY);
            if (item != null){
                txtItemName.setText(item.getName());
                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                userId = currentUser.getPhoneNumber();

                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(String.valueOf(currentUser.getPhoneNumber()));
                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User user = snapshot.getValue(User.class);
                        if(user != null)
                            userName = user.getName();
                            txtUsername.setText(userName);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                btnAddReview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (edtReview.getText().toString().equals("")){
                            txtWarning.setText("Please write a review!");
                            txtWarning.setVisibility(View.VISIBLE);
                        } else{
                            txtWarning.setVisibility(View.GONE);
                            Review review1 = new Review(item.getId(), userName, edtReview.getText().toString(), getCurrentDate());
                            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Category").child("Vegitables").child(item.getId()).child("reviews");
                            databaseReference.child(String.valueOf(userId)).setValue(review1);
                            dismiss();
                        }
                    }
                });
            }
        }
        return builder.create();
    }

    private String getCurrentDate(){
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-YYYY");
        return sdf.format(calendar.getTime());
    }

    public void initViews(View view) {
        txtItemName = view.findViewById(R.id.txtItemName);
        txtWarning = view.findViewById(R.id.txtWarning);
        txtUsername = view.findViewById(R.id.txtUsername);
        edtReview = view.findViewById(R.id.edtReview);
        btnAddReview = view.findViewById(R.id.btnAddReview);
    }
}