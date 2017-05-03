package com.ragentek.homeset.audiocenter;


import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

/**
 * Created by fxy on 2017/4/22.
 */

public class MediaServiceTestActivity extends AppCompatActivity {
    MediaServiceTestFragment testFragment;
    MediaServiceTestFragment testFragment2;

    public static final String fragTag = "MediaServiceTestFragment";
    private Button mSwitchBtn;
    private static final String TAG = "MediaServiceTest";
    private int index = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        testFragment = new MediaServiceTestFragment();
        testFragment2 = new MediaServiceTestFragment();

        mSwitchBtn = (Button) this.findViewById(R.id.switchfrag_button);
        mSwitchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchFragment();
            }
        });
        replaceTestFragment();
    }

    private void replaceTestFragment() {
        Log.d(TAG, "replaceTestFragment: ");
        android.support.v4.app.FragmentTransaction transaction = this.getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.fragment_container, testFragment, fragTag).show(testFragment).commit();

//        index++;
//        if (index % 2 == 1) {
//            transaction.replace(R.id.fragment_container, testFragment, fragTag).commit();
////            transaction.commitAllowingStateLoss();
//
//        } else {
//            transaction.replace(R.id.fragment_container, testFragment2, fragTag).commit();

//    }

    }

    private void switchFragment() {
        android.support.v4.app.Fragment frag = this.getSupportFragmentManager().findFragmentByTag(fragTag);
        index++;
        Log.d(TAG, "switchFragment: ");
        if (frag == null) {
            addFragment(testFragment);
        } else {
            removeFragment(frag);

        }
    }

    private void removeFragment(android.support.v4.app.Fragment frag) {
        Log.d(TAG, "removeFragment: " + frag.isAdded());
        android.support.v4.app.FragmentTransaction transaction = this.getSupportFragmentManager().beginTransaction();
        if (frag.isAdded()) {
            transaction.hide(frag).remove(frag).commit();
        }
    }

    private void addFragment(android.support.v4.app.Fragment frag) {
        Log.d(TAG, "showFragment: " + frag.isAdded());
        android.support.v4.app.FragmentTransaction transaction = this.getSupportFragmentManager().beginTransaction();
        if (!frag.isAdded()) {
//            transaction.replace(R.id.fragment_container, testFragment, fragTag).commit();

            transaction.add(R.id.fragment_container, testFragment, fragTag)
                    .show(frag)
                    .commit();
        } else {

        }
    }
}
