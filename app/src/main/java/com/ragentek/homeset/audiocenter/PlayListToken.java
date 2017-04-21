package com.ragentek.homeset.audiocenter;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;

import com.iflytek.cloud.thirdparty.A;
import com.ragentek.homeset.audiocenter.model.bean.PlayItem;
import com.ragentek.homeset.audiocenter.model.bean.PlayListItem;
import com.ragentek.homeset.audiocenter.model.bean.TagDetail;
import com.ragentek.homeset.audiocenter.net.AudioCenterHttpManager;
import com.ragentek.homeset.audiocenter.service.MediaPlayerManager;
import com.ragentek.homeset.audiocenter.utils.Constants;
import com.ragentek.homeset.audiocenter.utils.LogUtil;
import com.ragentek.homeset.audiocenter.view.fragment.PlayListFragment;

import java.util.ArrayList;
import java.util.List;

import rx.Subscriber;

/**
 * Created by xuanyang.feng on 2017/4/17.
 */

public abstract class PlayListToken {
    private static final String TAG = "PlayListToken";
    public static final int PLAYLISTMANAGER_RESULT_ERROR_NET = -1;
    public static final int PLAYLISTMANAGER_RESULT_ERROR_NONTINIT = -2;
    public static final String PLAYLIST = "playlist";

    public static final int PLAYLISTMANAGER_RESULT_SUCCESS = 0;
    public static final int PLAYLISTMANAGER_RESULT_NONE = 1;
    private PlayListLoadDataListener mPlayListListener = new PlayListLoadDataListener();
    private PlayListUIListener mPlayListUIListener = new PlayListUIListener();

    TagDetail mTagDetail;
    FragmentActivity mActivity;
    List<PlayListItem> wholePlayList = new ArrayList<>();
    private PlayListFragment mPlayListFragment;
    List<AudioToken> audioTokenList;

    boolean isInitted = false;
    int currentPlayIndex = 0;

    public PlayListToken(TagDetail tag, FragmentActivity activity) {
        mTagDetail = tag;
        mActivity = activity;
    }

    public void init() {
        LogUtil.d(TAG, ": init");
        loadData(mPlayListListener);
    }

    //TODO
    public void release() {
        LogUtil.d(TAG, ": release");

    }

    public void playPre() {
        if (currentPlayIndex > 0) {
            currentPlayIndex--;
        }
        LogUtil.d(TAG, ": release" + currentPlayIndex);

        showCurrentAudio();
    }

    public void playNext() {
        if (currentPlayIndex < 0) {
            currentPlayIndex--;
        }
        LogUtil.d(TAG, ": playNext" + currentPlayIndex);

        showCurrentAudio();
    }

    public void showPlayList() {
        LogUtil.d(TAG, "showPlayList");
        Fragment fragment = mActivity.getSupportFragmentManager().findFragmentByTag(PLAYLIST);
        FragmentTransaction ft = mActivity.getSupportFragmentManager().beginTransaction();
        if (fragment != null) {
            mPlayListFragment = (PlayListFragment) fragment;
            ft.attach(mPlayListFragment).commit();
        } else {
            mPlayListFragment = new PlayListFragment();
            mPlayListFragment.setPlayListUIListener(mPlayListUIListener);
            Bundle b = new Bundle();
            b.putInt(PlayListFragment.TAG_PLAYINDEX, currentPlayIndex);
            mPlayListFragment.setArguments(b);
            ft.add(mPlayListFragment, PLAYLIST).commit();
        }
        mPlayListFragment.addData(wholePlayList);
        mPlayListFragment.setCurrentPlayIndext(currentPlayIndex);
    }

    private void showCurrentAudio() {
        audioTokenList.get(currentPlayIndex).show();

    }

    public void updateFav2Server(final PlayListManagerListener managerListener) {
        LogUtil.d(TAG, ": updateFav2Server" + currentPlayIndex);

        final PlayListItem item2BeChanged = wholePlayList.get(currentPlayIndex);
        Subscriber<String> mSetFavSubscriber = new Subscriber<String>() {
            @Override
            public void onCompleted() {
                LogUtil.d(TAG, "onNext onCompleted: ");
            }

            @Override
            public void onError(Throwable e) {
                LogUtil.e(TAG, "onNext result: " + e.getMessage());
            }

            @Override
            public void onNext(String result) {
                LogUtil.d(TAG, "onNext result: " + result + ",item2BeChanged:" + item2BeChanged);
                item2BeChanged.updateFav();
                if (item2BeChanged.getId() == wholePlayList.get(currentPlayIndex).getId().longValue()) {
                    managerListener.onUpdate2ServerComplete(PLAYLISTMANAGER_RESULT_SUCCESS, item2BeChanged.getFav());
                }
            }
        };
        LogUtil.d(TAG, "setFav  : " + item2BeChanged.getId());
        if (item2BeChanged.getFav() == Constants.UNFAV) {
            AudioCenterHttpManager.getInstance(mActivity).addFavorite(mSetFavSubscriber, item2BeChanged.getId(), item2BeChanged.getCategoryType(), item2BeChanged.getGroup());
        } else {
            AudioCenterHttpManager.getInstance(mActivity).removeFavorite(mSetFavSubscriber, item2BeChanged.getId(), item2BeChanged.getCategoryType(), item2BeChanged.getGroup());
        }
    }


    private int isCurrentPlaylistContain(long audioId) {
        for (int i = 0; i < wholePlayList.size(); i++) {
            PlayListItem item = wholePlayList.get(i);
            if (item.getId().longValue() == audioId) {
                return i;
            }
        }
        return -1;
    }

    abstract protected void loadData(IPlayListLoadListener listener);


    //TODO
    abstract public void updateLocalPlayList(long id);


    private class PlayListLoadDataListener implements IPlayListLoadListener {


        @Override
        public void onLoadData(int resultCode, List<PlayListItem> resultmessage) {
            wholePlayList.addAll(resultmessage);
            if (mPlayListFragment != null) {
                mPlayListFragment.addData(resultmessage);
            }
            if (audioTokenList == null) {
                audioTokenList = new ArrayList<>();
                for (PlayListItem item : resultmessage) {
                    AudioToken mtoken = AudioTokenFactory.getAudioToken(mActivity, item);
                    audioTokenList.add(mtoken);
                }
                showCurrentAudio();
            } else {
                for (PlayListItem item : resultmessage) {
                    AudioToken mtoken = AudioTokenFactory.getAudioToken(mActivity, item);
                    audioTokenList.add(mtoken);
                }
            }
        }
    }


    private class PlayListUIListener implements PlayListFragment.PlayListListener {

        @Override
        public void onItemClick(int position) {

        }

        @Override
        public void onCloseClick() {
            detachFragment(PLAYLIST);
        }

        @Override
        public void onFavClick(int position) {

        }

        @Override
        public void onLoadMore() {

        }
    }

    private void detachFragment(String tag) {
        Fragment fragment = mActivity.getSupportFragmentManager().findFragmentByTag(tag);
        LogUtil.d(TAG, "showPlayList ::" + fragment);
        FragmentTransaction ft = mActivity.getSupportFragmentManager().beginTransaction();
        if (fragment != null) {
            ft.detach(fragment).commit();
        }
    }
}
