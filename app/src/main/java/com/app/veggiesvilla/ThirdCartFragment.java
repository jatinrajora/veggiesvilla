package com.app.veggiesvilla;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;
import com.paytm.pgsdk.TransactionManager;

import java.text.SimpleDateFormat;
import java.time.DateTimeException;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ThirdCartFragment extends Fragment {

    TextView txtItems, txtPrice, txtAddress, txtPhoneNumber;
    RadioGroup rgPaymentMethod;
    RadioButton rbPaytm, rbCod;
    Button btnBack, btnCheckout;

    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    String phoneNumber = currentUser.getPhoneNumber();

    String items = "", address, phone;
    Double price;

    private String TAG ="DemoPaytmActivity";
    private ProgressBar progressBar;
    private String midString ="zCfRGF96184554828211", txnAmountString="", orderIdString="", txnTokenString="";
    private Integer ActivityRequestCode = 2;

    String paymentMode, paymentStatus;

    public interface OnDataPass {
        void onDataPass(String orderId, String paymentMode, String paymentStatus,String cAddress, String cPhone, String cPhoneNumber, String cPrice);
    }

    OnDataPass dataPass;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_third_cart, container, false);
        initViews(view);

        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("ddMMyyyy");
        String date = df.format(c.getTime());
        Random rand = new Random();
        int min =1000, max= 9999;
        // nextInt as provided by Random is exclusive of the top value so you need to add 1
        int randomNum = rand.nextInt((max - min) + 1) + min;
        orderIdString =  date+String.valueOf(randomNum);
        SimpleDateFormat currentDateFormatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date currentDate = new Date();
        String orderDate = currentDateFormatter.format(currentDate);

        final DatabaseReference cartItemsRef = FirebaseDatabase.getInstance().getReference().child("Users").child(phoneNumber).child("Cart").child("Items");
        cartItemsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Item item = dataSnapshot.getValue(Item.class);
                    items += "\n\t" + item.getName();
                }
                txtItems.setText(items);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        final DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(phoneNumber);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                price = snapshot.child("Cart").child("TotalPrice").getValue(Double.class);
                address = snapshot.child("address").getValue(String.class);
                phone = snapshot.child("phone").getValue(String.class);
                txtPrice.setText(price.toString());
                txtAddress.setText(address);
                txtPhoneNumber.setText(phone);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.container, new SecondCartFragment());
                fragmentTransaction.commit();
            }
        });

        btnCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (rgPaymentMethod.getCheckedRadioButtonId()) {
                    case R.id.rbPaytm:
                        paymentMode = "Paytm";
                        paymentStatus = "Done";
                        txnAmountString = price.toString();
                        String errors = "";
                        if(orderIdString.equalsIgnoreCase("")) {
                            errors ="Enter valid Order ID here\n";
                        }
                        else if(txnAmountString.equalsIgnoreCase("")) {
                            errors ="Enter valid Amount here\n";
                        }
                        else {
                            getToken();
                        }

                        break;
                    case R.id.rbCod:
                        paymentMode = "COD";
                        paymentStatus = "Pending";
                        Order order = new Order(orderIdString, paymentMode, paymentStatus, price.toString(), address, phone, "Pending", orderDate);
                        DatabaseReference ordersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(phoneNumber).child("Orders");
                        ordersRef.child(order.getId()).setValue(order);
                        final DatabaseReference cartItemsRef = FirebaseDatabase.getInstance().getReference().child("Users").child(phoneNumber).child("Cart").child("Items");
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

                        DatabaseReference cartRef = FirebaseDatabase.getInstance().getReference().child("Users").child(phoneNumber).child("Cart");
                        cartRef.removeValue();

                        Bundle bundle = new Bundle();
                        bundle.putString("paymentMode", paymentMode);
                        PaymentResultFragment fragment = new PaymentResultFragment();
                        fragment.setArguments(bundle);
                        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.container, fragment);
                        fragmentTransaction.commit();

                        break;
                }
            }
        });

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            dataPass = (OnDataPass) context;
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
    }

    private  void getToken() {
        Log.e(TAG, " get token start");
        progressBar.setVisibility(View.VISIBLE);
        ServiceWrapper serviceWrapper = new ServiceWrapper(null);
        Call<Token_Res> call = serviceWrapper.getTokenCall("12345", midString, orderIdString, txnAmountString);
        call.enqueue(new Callback<Token_Res>() {
            @Override
            public void onResponse(Call<Token_Res> call, Response<Token_Res> response) {
                Log.e(TAG, " respo "+ response.isSuccessful());
                progressBar.setVisibility(View.GONE);
                try {
                    if (response.isSuccessful() && response.body() != null) {
                        if (response.body().getBody().getTxnToken() != "") {
                            Log.e(TAG, " transaction token : "+response.body().getBody().getTxnToken());
                            startPaytmPayment(response.body().getBody().getTxnToken());
                        }else {
                            Log.e(TAG, " Token status false");
                        }
                    }
                }catch (Exception e){
                    Log.e(TAG, " error in Token Res "+e.toString());
                }
            }

            @Override
            public void onFailure(Call<Token_Res> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Log.e(TAG, " response error "+t.toString());
            }
        });
    }

    public void startPaytmPayment (String token) {

        txnTokenString = token;
        // for test mode use it
        String host = "https://securegw-stage.paytm.in/";
        // for production mode use it
        // String host = "https://securegw.paytm.in/";
        String orderDetails = "MID: " + midString + ", OrderId: " + orderIdString + ", TxnToken: " + txnTokenString
                + ", Amount: " + txnAmountString;
        Log.e(TAG, "order details "+ orderDetails);

        String callBackUrl = host + "theia/paytmCallback?ORDER_ID="+orderIdString;
        Log.e(TAG, " callback URL " + callBackUrl);
        PaytmOrder paytmOrder = new PaytmOrder(orderIdString, midString, txnTokenString, txnAmountString, callBackUrl);
        TransactionManager transactionManager = new TransactionManager(paytmOrder, new PaytmPaymentTransactionCallback() {
            @Override
            public void onTransactionResponse(Bundle bundle) {
                Log.e(TAG, "Response (onTransactionResponse) : " + bundle.toString());
            }

            @Override
            public void networkNotAvailable() {
                Log.e(TAG, "network not available ");
            }

            @Override
            public void onErrorProceed(String s) {
                Log.e(TAG, " onErrorProcess " + s.toString());
            }

            @Override
            public void clientAuthenticationFailed(String s) {
                Log.e(TAG, "Clientauth " + s);
            }

            @Override
            public void someUIErrorOccurred(String s) {
                Log.e(TAG, " UI error " + s);
            }

            @Override
            public void onErrorLoadingWebPage(int i, String s, String s1) {
                Log.e(TAG, " error loading web " + s + "--" + s1);
            }

            @Override
            public void onBackPressedCancelTransaction() {
                Log.e(TAG, "backPress ");
            }

            @Override
            public void onTransactionCancel(String s, Bundle bundle) {
                Log.e(TAG, " transaction cancel " + s);
            }
        });

        transactionManager.setShowPaymentUrl(host + "theia/api/v1/showPaymentPage");
        transactionManager.startTransaction(getActivity(), ActivityRequestCode);

        dataPass.onDataPass(orderIdString, paymentMode, paymentStatus, address, phone, phoneNumber, price.toString());
        Intent thirdCartFragmentIntent = new Intent(getActivity(), ThirdCartFragment.class);
        getActivity().startActivityForResult(thirdCartFragmentIntent, ActivityRequestCode);
    }

    public void initViews(View view) {
        txtItems = view.findViewById(R.id.txtItems);
        txtPrice = view.findViewById(R.id.txtPrice);
        txtAddress = view.findViewById(R.id.txtAddress);
        txtPhoneNumber = view.findViewById(R.id.txtPhoneNumber);
        rgPaymentMethod = view.findViewById(R.id.rgPaymentMethod);
        rbPaytm = view.findViewById(R.id.rbPaytm);
        rbCod = view.findViewById(R.id.rbCod);
        btnBack = view.findViewById(R.id.btnBack);
        btnCheckout = view.findViewById(R.id.btnCheckout);
        progressBar = view.findViewById(R.id.progressBar);
    }
}