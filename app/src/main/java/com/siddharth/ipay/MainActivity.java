package com.siddharth.ipay;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private FirebaseUser mAuth;
    Button mHistory;
    Button mRequest;
    Button mLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance().getCurrentUser();
        mHistory = (Button) findViewById(R.id.bt_history);
        mRequest = (Button) findViewById(R.id.bt_request);
        mLogout = (Button) findViewById(R.id.bt_logout);
        mHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), History.class));
            }
        });
        mRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Request.class));
            }
        });
        mLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getApplicationContext(), SignInActivity.class));
                finish();
            }
        });
        if (mAuth == null) {
            Intent intent = new Intent(this,SignUpActivity.class);
            startActivity(intent);
        }
        Fragment androidFragment = new MajorFragment();
        this.setDefaultFragment(androidFragment);
    }

        private void setDefaultFragment(Fragment defaultFragment)
        {
            this.replaceFragment(defaultFragment);
        }

    public void replaceFragment(Fragment destFragment)
    {
        FragmentManager fragmentManager = this.getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.main_recyclerview, destFragment);
        fragmentTransaction.commit();
    }
}
