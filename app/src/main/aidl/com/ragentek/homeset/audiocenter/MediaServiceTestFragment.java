package com.ragentek.homeset.audiocenter;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Created by xuanyang.feng on 2017/4/24.
 */

public class MediaServiceTestFragment extends Fragment implements View.OnClickListener {
    private Activity mActivity;
    private static final String TAG = "MediaServiceTestFragmen";
    private static final ComponentName MEDIA_SERVICE_COMPONENT = new ComponentName(
            "com.ragentek.homeset.audiocenter", "com.ragentek.homeset.audiocenter.MediaService");
    Button startButton;

    Button initButton;

    Button pausButton;

    Button resumeButton;
    private IMediaService mMediaService;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = getActivity();

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mediaservice, container, false);

        initView(view);
        return view;
    }

    private void initView(View view) {
        startButton = (Button) view.findViewById(R.id.button_service_start);
        startButton.setOnClickListener(this);
        initButton = (Button) view.findViewById(R.id.button_service_init);
        initButton.setOnClickListener(this);

        pausButton = (Button) view.findViewById(R.id.button_service_pause);
        pausButton.setOnClickListener(this);

        resumeButton = (Button) view.findViewById(R.id.button_service_resume);
        resumeButton.setOnClickListener(this);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mActivity = getActivity();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_service_start:
                startService();
                break;
            case R.id.button_service_init:
                initService();
                break;
            case R.id.button_service_resume:
                resume();
                break;
            case R.id.button_service_pause:
                pause();
                break;
        }

    }

    private void pause() {
    }

    private void resume() {
    }

    private void initService() {
        try {
            mMediaService.init(new IMediaPlayerInitListener() {
                @Override
                public void initComplete() throws RemoteException {
                    Log.d(TAG, "initComplete: ");

                }

                @Override
                public IBinder asBinder() {
                    return null;
                }
            });
 
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void startService() {
        Intent intent = new Intent().setComponent(MEDIA_SERVICE_COMPONENT);
        mActivity.bindService(intent, new MediaServiceConnection(), Context.BIND_AUTO_CREATE);
    }


    class MediaServiceConnection implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            LogUtil.d(TAG, "onServiceConnected");
            mMediaService = IMediaService.Stub.asInterface(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    }

}
