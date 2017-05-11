package com.ragentek.homeset.audiocenter;

import com.ragentek.homeset.audiocenter.view.fragment.PlayBaseFragment;

/**
 * Created by xuanyang.feng on 2017/4/27.
 */

public interface IAudioControl<T> {
    void setDataChangerListener(PlayBaseFragment.IAudioDataChangerListener mListener);

    T getPlayData();
}
