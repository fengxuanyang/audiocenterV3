package com.ragentek.homeset.audiocenter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

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
    private static final String TAG = PlayListToken.class.getSimpleName();

    public static final String PLAYLIST_FRAGMENT_TAG = "playlist";

    /**
     * load data result code
     */
    public static final int PLAYLISTMANAGER_RESULT_ERROR_NET = -1;
    public static final int PLAYLISTMANAGER_RESULT_ERROR_NONTINIT = -2;
    public static final int PLAYLISTMANAGER_RESULT_SUCCESS = 0;
    public static final int PLAYLISTMANAGER_RESULT_NONE = 1;

    public static final int LOAD_DATA_STATE_IDLE = 0;
    public static final int LOAD_DATA_STATE_LOADING = 1;

    private int loadDataState = LOAD_DATA_STATE_IDLE;

    protected TagDetail mTagDetail;
    protected FragmentActivity mActivity;
    private MediaPlayerManager.MediaPlayerHandler mMediaPlayerManager;

    private PlayListLoadDataListener mPlayListLoadDataListener;
    private List<OnDataChangeListTokenListener> mDataChangeCallBacks;
    private PlayListFragment mPlayListFragment;
    private PlayListControlHandle mPlayListControlHandle;

    protected List<PlayListItem> wholePlayList;
    protected List<AudioToken> audioTokenList;
    private int currentPlayIndex = -1;

    abstract public void updateLocalPlayList(long id);

    abstract public void loadData(IPlayListLoadListener listener);

    public interface OnDataChangeListTokenListener {
        void onDataUpdate(int resultCode, PlayListItem item);

        void onGetData(int resultCode, List<PlayListItem> data);
    }

    public PlayListToken(TagDetail tag, FragmentActivity activity, MediaPlayerManager.MediaPlayerHandler handler) {
        mMediaPlayerManager = handler;
        mTagDetail = tag;
        mActivity = activity;
    }

    public void init() {
        LogUtil.d(TAG, ": init");
        mDataChangeCallBacks = new ArrayList<OnDataChangeListTokenListener>();
        wholePlayList = new ArrayList<>();
        audioTokenList = new ArrayList<>();
        mPlayListControlHandle = new PlayListControlHandle();
        mPlayListLoadDataListener = new PlayListLoadDataListener();
        loadData(mPlayListLoadDataListener);
    }

    public void addDataChangeListener(OnDataChangeListTokenListener callBack) {
        mDataChangeCallBacks.add(callBack);
    }

    public void removeDataChangeListener(OnDataChangeListTokenListener callBack) {
        mDataChangeCallBacks.remove(callBack);
    }


    public List<PlayListItem> getData() {
        return wholePlayList;
    }

    public void playPre() {
        if (currentPlayIndex > 0) {
            currentPlayIndex--;
        }
        LogUtil.d(TAG, ": release" + currentPlayIndex);
        showCurrentAudio();
    }


    public void playNext() {
        if (currentPlayIndex < audioTokenList.size()) {
            currentPlayIndex++;
        }
        LogUtil.d(TAG, ": playNext" + currentPlayIndex);
        showCurrentAudio();
    }

    private void showCurrentAudio() {
        Log.d(TAG, "showCurrentAudio: " + currentPlayIndex);
        Log.d(TAG, "showCurrentAudio: " + audioTokenList.get(currentPlayIndex));
        audioTokenList.get(currentPlayIndex).show();
    }


    public void showPlayList() {
        LogUtil.d(TAG, "showPlayList");
        Fragment fragment = mActivity.getSupportFragmentManager().findFragmentByTag(PLAYLIST_FRAGMENT_TAG);
        FragmentTransaction ft = mActivity.getSupportFragmentManager().beginTransaction();
        if (fragment != null) {
            mPlayListFragment = (PlayListFragment) fragment;
            ft.show(mPlayListFragment).commit();
        } else {
            mPlayListFragment = PlayListFragment.newInstance(currentPlayIndex);
            mPlayListFragment.setPlayControl(mPlayListControlHandle);
            ft.add(mPlayListFragment, PLAYLIST_FRAGMENT_TAG).commit();
        }
    }

    public void hidePlayList() {
        LogUtil.d(TAG, "hidePlayList");
        hideFragment(PLAYLIST_FRAGMENT_TAG);
    }

    private void hideFragment(String tag) {
        Fragment fragment = mActivity.getSupportFragmentManager().findFragmentByTag(tag);
        LogUtil.d(TAG, "showPlayList ::" + fragment);
        FragmentTransaction ft = mActivity.getSupportFragmentManager().beginTransaction();
        if (fragment != null) {
            ft.hide(fragment).commit();
        }
    }

    public void updateFav2Server() {
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
                for (OnDataChangeListTokenListener dataUpdataCall : mDataChangeCallBacks) {
                    dataUpdataCall.onDataUpdate(PLAYLISTMANAGER_RESULT_SUCCESS, item2BeChanged);
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

    //TODO
    public void release() {
        LogUtil.d(TAG, ": release");
    }


    private class PlayListLoadDataListener implements IPlayListLoadListener {

        @Override
        public void onLoadData(int resultCode, List<PlayListItem> resultmessage) {
            wholePlayList.addAll(resultmessage);
            for (PlayListItem item : resultmessage) {
                AudioToken mtoken = AudioTokenFactory.getAudioToken(mActivity, item, mMediaPlayerManager);
                Log.d(TAG, "onLoadData: " + mtoken);
                audioTokenList.add(mtoken);
            }
            if (currentPlayIndex == -1) {
                currentPlayIndex = 1;
                showCurrentAudio();
            }
            for (OnDataChangeListTokenListener dataUpdataCall : mDataChangeCallBacks) {
                dataUpdataCall.onGetData(PLAYLISTMANAGER_RESULT_SUCCESS, resultmessage);
            }
        }

    }

    private class PlayListControlHandle implements PlayListControl {

        @Override
        public void addDataListener(OnDataChangeListTokenListener listener) {
            mDataChangeCallBacks.add(listener);
        }

        @Override
        public void removeDataListener(OnDataChangeListTokenListener listener) {
            mDataChangeCallBacks.remove(listener);
        }

        @Override
        public List<PlayListItem> getData() {
            return wholePlayList;
        }

        @Override
        public void getDataAsync() {
            getDataAsync();
        }

        @Override
        public void playSellected(int position) {

        }

        @Override
        public void closePlaylistFragment() {
            hideFragment(PLAYLIST_FRAGMENT_TAG);
        }

        @Override
        public int getCurrentPlayIndex() {
            return currentPlayIndex;
        }

        @Override
        public int getLoadDatastate() {
            return loadDataState;
        }
    }

}
