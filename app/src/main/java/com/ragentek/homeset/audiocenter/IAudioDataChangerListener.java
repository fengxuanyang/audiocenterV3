package com.ragentek.homeset.audiocenter;

/**
 * Created by xuanyang.feng on 2017/4/27.
 */

public interface IAudioDataChangerListener<T> {
    void onGetData(int resultCode, T data);
}
