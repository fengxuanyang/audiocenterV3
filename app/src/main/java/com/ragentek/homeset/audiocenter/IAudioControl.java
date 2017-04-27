package com.ragentek.homeset.audiocenter;

/**
 * Created by xuanyang.feng on 2017/4/27.
 */

public interface IAudioControl {


    void setDataChangerListener(IAudioDataChangerListener mListener);

    void playSellected(int position);

    void getDataAsync();
}
