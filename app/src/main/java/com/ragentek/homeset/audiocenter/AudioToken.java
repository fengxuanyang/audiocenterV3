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

public abstract class AudioToken<E> {
    private static final String TAG = "AudioToken";
    public static final int PLAYLIST_RESULT_ERROR_NET = -1;
    public static final int PLAYLIST_RESULT_NONE = 0;
    public static final int PLAYLIST_RESULT_SUCCESS = 1;
    FragmentActivity mActivity;
    private MediaPlayerManager.MediaPlayerHandler mediaPlayerManager;
    private PlayListItem currentPlayitem;
    private AudioPlayListResultListener mPlayListResultListener;
    PlayBaseFragment fragment = null;
    private int currentPlayIndex = 0;
    private AudioControl mAudioControl;
    private IAudioDataChangerListener<E> mAudioDataChangerListener;

    protected abstract PlayBaseFragment<E> getPlayFragment();

    protected abstract void getPlayListAsync(AudioPlayListResultListener listener, PlayListItem listitem);

    AudioToken(FragmentActivity activity, MediaPlayerManager.MediaPlayerHandler mediaPlayer, PlayListItem item) {
        mActivity = activity;
        mediaPlayerManager = mediaPlayer;
        currentPlayitem = item;
        mAudioControl = new AudioControl();
    }

    void show() {
        LogUtil.d(TAG, "show: " + this.getClass().getSimpleName());
        Fragment view = mActivity.getSupportFragmentManager().findFragmentByTag(this.getClass().getSimpleName());
        FragmentTransaction transaction = mActivity.getSupportFragmentManager().beginTransaction();
        if (view == null) {
            //TODO
            fragment = getPlayFragment();
            fragment.setAudioControl(mAudioControl);
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
        mPlayListResultListener = new AudioPlayListResultListener();
        getPlayListAsync(mPlayListResultListener, currentPlayitem);
    }

    void hide() {
        LogUtil.d(TAG, "hide: " + this.getClass().getSimpleName());
        FragmentTransaction transaction = mActivity.getSupportFragmentManager().beginTransaction();
        Fragment view = mActivity.getSupportFragmentManager().findFragmentByTag(this.getClass().getSimpleName());
        if (view.isAdded()) {
            transaction.remove(view).commit();
        }
    }

    private void playAudioSellected(int index) {
        mediaPlayerManager.play(index);
    }


    class AudioPlayListResultListener {
        void onPlayAudioListGet(int result, List<PlayItem> playlist, E audiouidata) {
            LogUtil.d(TAG, "onPlayListResult: " + currentPlayIndex);
            mediaPlayerManager.addPlayList(playlist);
            //TODO  play sellected
            mAudioDataChangerListener.onGetData(result, audiouidata);
        }
    }

    private class AudioControl implements IAudioControl {


        @Override
        public void setDataChangerListener(IAudioDataChangerListener listener) {
            mAudioDataChangerListener = listener;
        }


        @Override
        public void playSellected(int position) {
            playAudioSellected(position);
        }

        @Override
        public void getDataAsync() {
            getPlayListAsync(mPlayListResultListener, currentPlayitem);
        }
    }

}
