package com.ragentek.homeset.audiocenter;

import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;

import com.ragentek.homeset.audiocenter.model.bean.PlayListItem;
import com.ragentek.homeset.audiocenter.service.MediaPlayerManager;
import com.ragentek.homeset.audiocenter.utils.LogUtil;
import com.ragentek.homeset.audiocenter.view.fragment.PlayBaseFragment;
import com.ragentek.homeset.core.R;
import com.ragentek.protocol.commons.audio.BaseAudioVO;

import static android.R.attr.fragment;


public abstract class AudioToken<M extends BaseAudioVO, S extends IAudioControl> {
    private static final String TAG = "AudioToken";
    public static final int PLAYLIST_RESULT_ERROR_NET = -1;
    public static final int PLAYLIST_RESULT_NONE = 0;
    public static final int PLAYLIST_RESULT_SUCCESS = 1;
    private static final int DEFAULT_PLAY_INDEX = 0;

    FragmentActivity mActivity;


    protected abstract PlayBaseFragment getPlayFragment();

    protected abstract void startPlayAudio(int index);


    AudioToken(FragmentActivity activity, MediaPlayerManager.MediaPlayerHandler mediaPlayer, PlayListItem item) {
        mActivity = activity;
    }

    String fragmentTag = this.getClass().getSimpleName();


    public void showView() {
        LogUtil.d(TAG, "show: " + fragmentTag);
        FragmentTransaction transaction = mActivity.getSupportFragmentManager().beginTransaction();
        PlayBaseFragment fragment = (PlayBaseFragment) mActivity.getSupportFragmentManager().findFragmentByTag(fragmentTag);

        //TODO
        LogUtil.d(TAG, "show  getSimpleName: " + this.getClass().getSimpleName());
        LogUtil.d(TAG, "show  isAdded: " + fragment.isAdded());
        LogUtil.d(TAG, "show  isDetached: " + fragment.isDetached());
        LogUtil.d(TAG, "show  isHidden: " + fragment.isHidden());
        if (!getCurrentFragment().isAdded()) {
            transaction.replace(R.id.fragment_container, fragment, fragmentTag).show(fragment).commit();
        } else {
            transaction.show(fragment).commit();
        }
    }

    public void hide() {


        FragmentTransaction transaction = mActivity.getSupportFragmentManager().beginTransaction();
        PlayBaseFragment fragment = getCurrentFragment();
        LogUtil.d(TAG, "show  isAdded: " + fragment);

        transaction.hide(fragment).remove(fragment).commit();
    }

    private PlayBaseFragment getCurrentFragment() {
        PlayBaseFragment fragment = (PlayBaseFragment) mActivity.getSupportFragmentManager().findFragmentByTag(fragmentTag);
        if (fragment == null) {
            fragment = getPlayFragment();
        }
        return fragment;
    }

    public void startPlay() {
        LogUtil.d(TAG, "startPlay ");
        startPlayAudio(DEFAULT_PLAY_INDEX);
    }
}
