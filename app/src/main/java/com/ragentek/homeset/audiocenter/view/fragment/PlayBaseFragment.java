package com.ragentek.homeset.audiocenter.view.fragment;

import android.app.Activity;

import com.ragentek.homeset.audiocenter.IAudioControl;


/**
 * Created by xuanyang.feng on 2017/2/8.
 */

public abstract class PlayBaseFragment<T, E extends IAudioControl> extends BaseFragment {
    E mIAudioControl;

    abstract void onDataChanged(int resultCode, T data);

    public void setAudioControl(E control) {
        mIAudioControl = control;
        mIAudioControl.setDataChangerListener(new IAudioDataChangerListener() {

            @Override
            public void onGetData(int resultCode, Object data) {

            }
        });
    }

    public interface IAudioDataChangerListener<H> {
        void onGetData(int resultCode, H data);
    }
}
