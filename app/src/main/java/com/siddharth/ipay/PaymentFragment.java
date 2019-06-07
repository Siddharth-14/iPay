package com.siddharth.ipay;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

public class PaymentFragment extends Fragment {
    View v;
    private Context context;
    public PaymentFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_payment, container, false);
        this.context = v.getContext();
        Button btCash = v.findViewById(R.id.bt_cash);
        btCash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment destFragment = new MajorFragment();
                FragmentManager fragmentManager = ((Activity) (context)).getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.main_recyclerview, destFragment);
                fragmentTransaction.commit();
            }
        });
        Button btOnline = v.findViewById(R.id.bt_online_payment);
        btOnline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Payment done with cash", Toast.LENGTH_LONG).show();
            }
        });
        return v;
    }
}
