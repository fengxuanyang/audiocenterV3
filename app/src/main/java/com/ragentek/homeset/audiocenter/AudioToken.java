package com.ragentek.homeset.audiocenter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;

import com.ragentek.homeset.audiocenter.model.bean.PlayItem;
import com.ragentek.homeset.audiocenter.model.bean.PlayListItem;
import com.ragentek.homeset.audiocenter.service.MediaPlayerManager;
import com.ragentek.homeset.audiocenter.utils.LogUtil;
import com.ragentek.homeset.audiocenter.view.fragment.PlayBaseFragment;
import com.ragentek.homeset.core.R;

import java.util.List;

public abstract class AudioToken {
    private static final String TAG = "AudioToken";
    public static final int PLAYLIST_RESULT_ERROR_NET = -1;
    public static final int PLAYLIST_RESULT_NONE = 0;
    public static final int PLAYLIST_RESULT_SUCCESS = 1;
    FragmentActivity mActivity;


    PlayBaseFragment fragment = null;
    private int currentPlayIndex = 0;

    protected abstract PlayBaseFragment getPlayFragment();

    protected abstract void playAudio(int index);


    AudioToken(FragmentActivity activity, MediaPlayerManager.MediaPlayerHandler mediaPlayer, PlayListItem item) {
        mActivity = activity;
    }

    public void showView() {
        LogUtil.d(TAG, "show: " + this.getClass().getSimpleName());
        Fragment view = mActivity.getSupportFragmentManager().findFragmentByTag(this.getClass().getSimpleName());
        FragmentTransaction transaction = mActivity.getSupportFragmentManager().beginTransaction();
        if (view == null) {
            //TODO
            fragment = getPlayFragment();
            transaction.replace(R.id.fragment_container, fragment, this.getClass().getSimpleName()).commit();
        } else {
            fragment = (PlayBaseFragment) view;
            if (!fragment.isAdded()) {
                transaction.replace(R.id.fragment_container, fragment, this.getClass().getSimpleName()).commit();
            } else {
                transaction.show(fragment).commit();
            }
        }
        //TODO
    }

    public void startPlay() {
        playAudio(0);
    }

    public void startPlay(int indext) {
        playAudio(indext);
    }

    public void hide() {
        LogUtil.d(TAG, "hide: " + this.getClass().getSimpleName());
        FragmentTransaction transaction = mActivity.getSupportFragmentManager().beginTransaction();
        Fragment view = mActivity.getSupportFragmentManager().findFragmentByTag(this.getClass().getSimpleName());
        if (view.isAdded()) {
            transaction.remove(view).commit();
        }
    }


}
