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
import com.ragentek.protocol.commons.audio.BaseAudioVO;

import java.util.List;

public abstract class AudioToken<M extends BaseAudioVO, S extends IAudioControl> {
    private static final String TAG = "AudioToken";
    public static final int PLAYLIST_RESULT_ERROR_NET = -1;
    public static final int PLAYLIST_RESULT_NONE = 0;
    public static final int PLAYLIST_RESULT_SUCCESS = 1;
    FragmentActivity mActivity;
    private static final int DEFAULT_PLAY_INDEX = 0;


    PlayBaseFragment fragment = null;
    private int currentPlayIndex = 0;

    protected abstract PlayBaseFragment getPlayFragment();

    protected abstract void startPlayAudio(int index);


    AudioToken(FragmentActivity activity, MediaPlayerManager.MediaPlayerHandler mediaPlayer, PlayListItem item) {
        mActivity = activity;
    }


    public void showView() {
        String fragmenttag = this.getClass().getSimpleName();
        LogUtil.d(TAG, "show: " + fragmenttag);
        FragmentTransaction transaction = mActivity.getSupportFragmentManager().beginTransaction();
        //TODO
        fragment = getPlayFragment();
        LogUtil.d(TAG, "show  getSimpleName: " + this.getClass().getSimpleName());
        LogUtil.d(TAG, "show  isAdded: " + fragment.isAdded());
        LogUtil.d(TAG, "show  isDetached: " + fragment.isDetached());
        LogUtil.d(TAG, "show  isHidden: " + fragment.isHidden());
        if (!fragment.isAdded()) {
            transaction.replace(R.id.fragment_container, fragment).show(fragment).commit();
        } else {
            transaction.show(fragment).commit();
        }
    }

    public void hide() {
        FragmentTransaction transaction = mActivity.getSupportFragmentManager().beginTransaction();
        LogUtil.d(TAG, "hide  isHidden: " + fragment.isHidden());
        transaction.hide(fragment).remove(fragment).commit();
    }

    public void startPlay() {
        LogUtil.d(TAG, "startPlay ");
        startPlayAudio(DEFAULT_PLAY_INDEX);
    }
}
