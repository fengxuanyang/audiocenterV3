package com.ragentek.homeset.audiocenter.view.fragment;

import android.app.Activity;

import com.ragentek.homeset.audiocenter.service.MyMediaPlayerControl;

import java.util.List;

/**
 * Created by xuanyang.feng on 2017/2/8.
 */

public abstract class PlayBaseFragment<T> extends BaseFragment {
    T playdata;
    AudioFragmentListener mAudioFragmentListener;


    public PlayBaseFragment() {

    }

    public T getPlaydata() {
        return playdata;
    }

    public void setPlaydata(T data) {

        if (playdata != null && playdata instanceof List) {
            ((List) playdata).addAll((List) data);
        } else {
            this.playdata = data;

        }
        if (isVisible()) {
            onDataChanged(playdata);
        }
    }

    public PlayBaseFragment(T data) {
        playdata = data;
    }

    /**
     * set the  Inner sellected of play fragment list
     *
     * @param index
     */
    public abstract void setInnerSellected(int index);

    abstract void onDataChanged(T playdata);

    public void setAudioFragmentListener(AudioFragmentListener listemer) {
        mAudioFragmentListener = listemer;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    public interface AudioFragmentListener {
        void onPlayItemClick(int position);

        void onLoadMore();
    }

}
