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
    private final String FRAGMENT_TAG = this.getClass().getSimpleName();
    FragmentActivity mActivity;
    MediaPlayerManager.MediaPlayerHandler mMediaPlayer;
    PlayListItem mPlayListItem;


    private AudioPlayStateListener mAudioPlayStateListener;
    private AudioTokenMediaPlayerPlayListener mAudioTokenMediaPlayerPlayListener;

    protected abstract PlayBaseFragment getPlayFragment();

    protected abstract void startPlayAudio(int index);

    protected abstract void onSoundPlayComplete();


    AudioToken(FragmentActivity activity, MediaPlayerManager.MediaPlayerHandler mediaPlayer, PlayListItem item) {
        mActivity = activity;
        mPlayListItem = item;
        mMediaPlayer = mediaPlayer;
        mAudioTokenMediaPlayerPlayListener = new AudioTokenMediaPlayerPlayListener();
    }


    public void showView() {
        LogUtil.d(TAG, "show: " + FRAGMENT_TAG);
        FragmentTransaction transaction = mActivity.getSupportFragmentManager().beginTransaction();
        PlayBaseFragment fragment = getCurrentFragment();
        mMediaPlayer.addMeidaPlayListener(mAudioTokenMediaPlayerPlayListener);
        LogUtil.d(TAG, "show  isAdded: " + fragment.isAdded());
        LogUtil.d(TAG, "show  isDetached: " + fragment.isDetached());
        LogUtil.d(TAG, "show  isHidden: " + fragment.isHidden());
        if (!getCurrentFragment().isAdded()) {
            transaction.replace(R.id.fragment_container, fragment, FRAGMENT_TAG).show(fragment).commit();
        } else {
            transaction.show(fragment).commit();
        }
    }

    public void hide() {
        FragmentTransaction transaction = mActivity.getSupportFragmentManager().beginTransaction();
        PlayBaseFragment fragment = getCurrentFragment();
        LogUtil.d(TAG, "hide  isAdded: " + fragment);
        transaction.hide(fragment).remove(fragment).commit();
        mMediaPlayer.removeMeidaPlayListener(mAudioTokenMediaPlayerPlayListener);

    }

    private PlayBaseFragment getCurrentFragment() {
        PlayBaseFragment fragment = (PlayBaseFragment) mActivity.getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG);
        LogUtil.d(TAG, "getCurrentFragment  fragment: " + fragment);

        if (fragment == null) {
            fragment = getPlayFragment();
        }
        return fragment;
    }


    public void startPlay(AudioPlayStateListener audioPlayStateListener) {
        LogUtil.d(TAG, "startPlay ");
        mAudioPlayStateListener = audioPlayStateListener;
        startPlayAudio(DEFAULT_PLAY_INDEX);
    }

    protected AudioPlayStateListener getAudioPlayStateListener() {
        return mAudioPlayStateListener;
    }

    private class AudioTokenMediaPlayerPlayListener implements MediaPlayerManager.MediaPlayerPlayListener

    {
        @Override
        public void onPlayStart() {
            LogUtil.d(TAG, "onPlayStart:  ");

        }

        @Override
        public void onPlayProgress(int currPos, int duration) {
            LogUtil.d(TAG, "onPlayProgress:  ");

        }

        @Override
        public void onPlayStop() {
            LogUtil.d(TAG, "onPlayStop:  ");

        }

        @Override
        public void onSoundPlayComplete() {
            LogUtil.d(TAG, "onSoundPlayComplete:  ");
            onSoundPlayComplete();
        }
    }

    public interface AudioPlayStateListener {
        void onComplete();

        void onStart();

    }
}
