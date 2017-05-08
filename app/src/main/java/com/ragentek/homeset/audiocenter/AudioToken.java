package com.ragentek.homeset.audiocenter;

import android.support.v4.app.Fragment;
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
    private final String FRAGMENT_TAG = "fxy";// this.getClass().getSimpleName();
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

//    PlayBaseFragment basefragment;

    public void showView() {
        LogUtil.d(TAG, "show: " + FRAGMENT_TAG);
        FragmentTransaction transaction = mActivity.getSupportFragmentManager().beginTransaction();
        Fragment basefragment = getCurrentFragment();
        mMediaPlayer.addMeidaPlayListener(mAudioTokenMediaPlayerPlayListener);
        LogUtil.d(TAG, "show  isAdded: " + basefragment.isAdded());
        LogUtil.d(TAG, "show  isDetached: " + basefragment.isDetached());
        LogUtil.d(TAG, "show  isHidden: " + basefragment.isHidden());
        LogUtil.d(TAG, "show  isHidden: " + basefragment.getTag());

        transaction.add(R.id.fragment_container, basefragment, "fxy").show(basefragment).commit();

    }

    public void hide() {
        FragmentTransaction transaction = mActivity.getSupportFragmentManager().beginTransaction();
        Fragment basefragment = getCurrentFragment();

        LogUtil.d(TAG, "before hide  isAdded: " + basefragment);
        LogUtil.d(TAG, "  hide  isAdded: " + basefragment.isAdded());
        transaction.remove(basefragment).hide(basefragment).commit();
        mMediaPlayer.removeMeidaPlayListener(mAudioTokenMediaPlayerPlayListener);
        LogUtil.d(TAG, "after hide  isAdded: " + basefragment.isAdded());

    }

    private PlayBaseFragment getCurrentFragment() {
        Fragment fragment = mActivity.getSupportFragmentManager().findFragmentByTag("fxy");
        LogUtil.d(TAG, "getCurrentFragment  fragment: " + fragment);
        LogUtil.d(TAG, "getCurrentFragment  FRAGMENT_TAG: " + FRAGMENT_TAG);
        if (fragment == null) {
            fragment = getPlayFragment();
        }
        return (PlayBaseFragment) fragment;
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
