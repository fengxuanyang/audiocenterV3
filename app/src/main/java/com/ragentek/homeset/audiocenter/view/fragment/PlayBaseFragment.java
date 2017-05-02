package com.ragentek.homeset.audiocenter.view.fragment;

import android.app.Activity;

import com.ragentek.homeset.audiocenter.IAudioControl;


/**
 * Created by xuanyang.feng on 2017/2/8.
 */

public abstract class PlayBaseFragment<T, E extends IAudioControl> extends BaseFragment {
    abstract IAudioDataChangerListener<T> getIAudioDataChangerListener();

    E mIAudioControl;

    public void setAudioControl(E control) {
        mIAudioControl = control;
        //TODO
//        mIAudioControl.setDataChangerListener(getIAudioDataChangerListener());
    }

    public interface IAudioDataChangerListener<H> {
        void onGetData(int resultCode, H data);
    }
}
