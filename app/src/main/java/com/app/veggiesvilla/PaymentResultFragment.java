package com.app.veggiesvilla;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.snapshot.IndexedNode;

public class PaymentResultFragment extends Fragment {

    private TextView txtMessage;
    private Button btnHome;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_payment_result, container, false);

        initViews(view);

        Bundle bundle = this.getArguments();
        int resultCode = bundle.getInt("resultCode");

        Bundle bundle1 = getArguments();
        String paymentMode = bundle1.getString("paymentMode");

        if(resultCode == -1 || paymentMode == "COD") {
            txtMessage.setText("Your Order Placed Successfully!");
            btnHome.setVisibility(View.VISIBLE);
        }

        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        return view;
    }

    private void initViews(View view){
        txtMessage = view.findViewById(R.id.txtMessage);
        btnHome = view.findViewById(R.id.btnHome);
    }
}