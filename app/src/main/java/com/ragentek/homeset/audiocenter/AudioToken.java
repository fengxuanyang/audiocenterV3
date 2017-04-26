package com.ragentek.homeset.audiocenter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;

import com.ragentek.homeset.audiocenter.model.bean.PlayItem;
import com.ragentek.homeset.audiocenter.model.bean.PlayListItem;
import com.ragentek.homeset.audiocenter.service.MediaPlayerManager;
import com.ragentek.homeset.audiocenter.utils.LogUtil;
import com.ragentek.homeset.audiocenter.view.fragment.AlbumFragment;
import com.ragentek.homeset.audiocenter.view.fragment.PlayBaseFragment;
import com.ragentek.homeset.core.R;

import java.util.List;

/**
 * Created by xuanyang.feng on 2017/4/20.
 */

public abstract class AudioToken<T> {
    private static final String TAG = "AudioToken";
    MediaPlayerManager.MediaPlayerHandler mediaPlayerManager;
    private String fragmentTag = this.getClass().getSimpleName();
    FragmentActivity mActivity;
    private PlayListItem currentPlayitem;
    private PlayListResultListener mPlayListResultListener = new PlayListResultListener();
    PlayBaseFragment fragment = null;
    int currentPlayIndex = 0;

    AudioToken(FragmentActivity activity, MediaPlayerManager.MediaPlayerHandler mediaPlayer, PlayListItem item) {
        mActivity = activity;
        mediaPlayerManager = mediaPlayer;
        currentPlayitem = item;
    }


    public void show() {
        LogUtil.d(TAG, "show: " + this.getClass().getSimpleName());
        Fragment view = mActivity.getSupportFragmentManager().findFragmentByTag(this.getClass().getSimpleName());
        FragmentTransaction transaction = mActivity.getSupportFragmentManager().beginTransaction();
        if (view == null) {
            fragment = getPlayFragment();
            fragment.setAudioFragmentListener(new PlayBaseFragment.AudioFragmentListener() {
                @Override
                public void onPlayItemClick(int position) {
                    LogUtil.d(TAG, "onPlayItemClick: " + position);

                    mediaPlayerManager.play(position);
                    currentPlayIndex = position;
                }

                @Override
                public void onLoadMore() {
                    LogUtil.d(TAG, "onLoadMore: ");
                    getPlayList(mPlayListResultListener, currentPlayitem);

                }
            });
            transaction.replace(R.id.fragment_container, fragment, this.getClass().getSimpleName()).commit();
        } else {
            fragment = (PlayBaseFragment) view;
            if (!fragment.isAdded()) {
                transaction.replace(R.id.fragment_container, fragment, this.getClass().getSimpleName()).commit();
            } else {
                transaction.show(fragment).commit();
            }
        }
        getPlayList(mPlayListResultListener, currentPlayitem);
    }

    public void hide() {
        LogUtil.d(TAG, "hide: " + this.getClass().getSimpleName());
        FragmentTransaction transaction = mActivity.getSupportFragmentManager().beginTransaction();
        Fragment view = mActivity.getSupportFragmentManager().findFragmentByTag(this.getClass().getSimpleName());
        if (view.isAdded()) {
            transaction.remove(view).commit();
        }
    }

    public void playAudioSellected(int index) {
        mediaPlayerManager.play(index);
    }

    public void pause() {
        mediaPlayerManager.pause();
    }

    public void resume() {
        mediaPlayerManager.resume();
    }

    class PlayListResultListener {
        void onPlayListResult(List<PlayItem> list, T audiouidata) {
            LogUtil.d(TAG, "onPlayListResult: " + currentPlayIndex);
            mediaPlayerManager.setPlayList(list, currentPlayIndex);
            fragment.setPlaydata(audiouidata);
        }
    }

    protected abstract PlayBaseFragment<T> getPlayFragment();

    protected abstract void getPlayList(PlayListResultListener listener, PlayListItem item);

}
