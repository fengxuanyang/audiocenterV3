package com.ragentek.homeset.audiocenter;

/**
 * Created by xuanyang.feng on 2017/4/27.
 */

public interface IAudioControl<T> {

    void playSellected();

    T getData();

    void setDataChangerListener(IAudioDataChangerListener mListener);

    void playSellected(int position);

}
