package com.ragentek.homeset.audiocenter;


import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;

/**
 * Created by fxy on 2017/4/22.
 */

public class MediaServiceTestActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startTestFragment();
    }

    private void startTestFragment() {
        MediaServiceTestFragment testFragment = new MediaServiceTestFragment();
        android.support.v4.app.FragmentTransaction transaction = this.getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, testFragment).commit();
    }


}
