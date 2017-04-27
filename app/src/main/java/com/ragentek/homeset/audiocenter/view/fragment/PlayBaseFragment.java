package com.ragentek.homeset.audiocenter.view.fragment;

import android.app.Activity;

import com.ragentek.homeset.audiocenter.IAudioControl;
import com.ragentek.homeset.audiocenter.IAudioDataChangerListener;


/**
 * Created by xuanyang.feng on 2017/2/8.
 */

public abstract class PlayBaseFragment<T> extends BaseFragment {
    IAudioControl mIAudioControl;

    abstract void onDataChanged(int resultCode, T data);

    public void setAudioControl(IAudioControl control) {
        mIAudioControl = control;
        mIAudioControl.setDataChangerListener(new IAudioDataChangerListener<T>() {

            @Override
            public void onGetData(int resultCode, T data) {
                if (isVisible()) {
                    onDataChanged(resultCode, data);
                }
            }
        });

    }


}
