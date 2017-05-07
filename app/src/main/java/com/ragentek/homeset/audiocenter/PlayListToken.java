package com.ragentek.homeset.audiocenter;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.widget.Switch;

import com.ragentek.homeset.audiocenter.model.bean.PlayItem;
import com.ragentek.homeset.audiocenter.model.bean.PlayListItem;
import com.ragentek.homeset.audiocenter.model.bean.TagDetail;
import com.ragentek.homeset.audiocenter.net.AudioCenterHttpManager;
import com.ragentek.homeset.audiocenter.service.MediaPlayerManager;
import com.ragentek.homeset.audiocenter.utils.Constants;
import com.ragentek.homeset.audiocenter.utils.LogUtil;
import com.ragentek.homeset.audiocenter.view.fragment.PlayListFragment;
import com.ragentek.homeset.core.task.event.PushAudioFavEvent;
import com.ragentek.protocol.commons.audio.BaseAudioVO;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

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
    private List<PlayDataChangeListTokenListener> mDataChangeCallBacks;

    private PlayListFragment mPlayListFragment;
    private IPlayListControlHandle mPlayListControlHandle;
    private AudioPlayStateListener mAudioPlayStateListener;
    private boolean isLoadingData = false;

    protected List<PlayListItem> wholePlayList;
    protected List<AudioToken> audioTokenList;

    private int currentPlayIndex = -1;
    private int prePlayIndex = -1;
    public static final int DEFAULT_PLAY_INDEX = 0;

    //TODO  add action  param
    abstract public void onLocalPlayListUpdate(int index, PushAudioFavEvent fav);

    abstract public void loadData(IPlayListLoadListener listener);

    public interface PlayDataChangeListTokenListener {
        void onDataUpdate(int resultCode, PlayListItem item);

        void onGetData(int resultCode, List<PlayListItem> data);

        void onPlayStart(PlayListItem data);

        void onPlayIndexChanged(int index);

    }

    public PlayListToken(TagDetail tag, FragmentActivity activity, MediaPlayerManager.MediaPlayerHandler handler) {
        mMediaPlayerManager = handler;
        mTagDetail = tag;
        mActivity = activity;
        mDataChangeCallBacks = new ArrayList<PlayDataChangeListTokenListener>();
        wholePlayList = new ArrayList<>();
        audioTokenList = new ArrayList<>();
        mPlayListControlHandle = new IPlayListControlHandle();
        mPlayListLoadDataListener = new PlayListLoadDataListener();
        mAudioPlayStateListener = new AudioPlayStateListener();
    }

    public void init() {
        LogUtil.d(TAG, ": init");
        startLoadMoreData();
        EventBus.getDefault().register(this);
    }

    protected int getCurrentPlayIndex() {
        return currentPlayIndex;
    }

    private void startLoadMoreData() {
        LogUtil.d(TAG, ": startLoadMoreData");
        isLoadingData = true;
        loadData(mPlayListLoadDataListener);
    }

    protected void addNewPlayListItem(int index, PlayListItem item) {
        Log.d(TAG, "addNewPlayListItem  index: " + index + ",item" + item.toString());
        //TODO
        AudioToken mtoken = AudioTokenFactory.getAudioToken(mActivity, item, mMediaPlayerManager);
        audioTokenList.add(index, mtoken);        //TODO  remove the last token ,but the token not hid
        wholePlayList.add(index, item);
    }

    protected void removePlayListItem(int index) {
        Log.d(TAG, "removePlayListItem  index: ");
        audioTokenList.remove(index);
        wholePlayList.remove(index);
    }

    public void addDataChangeListener(@NonNull PlayDataChangeListTokenListener callBack) {
        mDataChangeCallBacks.add(callBack);
    }

    public void removeDataChangeListener(@NonNull PlayDataChangeListTokenListener callBack) {
        mDataChangeCallBacks.remove(callBack);
    }

    public List<PlayListItem> getData() {
        return wholePlayList;
    }

    public void playPre() {
        if (currentPlayIndex > 0) {
            currentPlayIndex--;
        }
        LogUtil.d(TAG, ": playPre  currentPlayIndex：" + currentPlayIndex);
        switchAudioToken();
    }

    protected void play(int index) {
        currentPlayIndex = index;
        LogUtil.d(TAG, ": play  currentPlayIndex：" + currentPlayIndex);
        switchAudioToken();
    }

    //TODO
    public void playNext() {
        if (currentPlayIndex < audioTokenList.size() - 1) {
            currentPlayIndex++;
        } else {
            currentPlayIndex = 0;
            LogUtil.d(TAG, ": playNext  max" + currentPlayIndex);
        }
        switchAudioToken();
        //TODO  nnext
    }


    private void switchAudioToken() {
        Log.d(TAG, "switchAudioToken: " + currentPlayIndex);
        //TODO show sate fragment
        if (prePlayIndex < 0) {
            audioTokenList.get(currentPlayIndex).showView();
        } else {
            audioTokenList.get(prePlayIndex).hide();
            audioTokenList.get(currentPlayIndex).showView();
        }
        audioTokenList.get(currentPlayIndex).startPlay(mAudioPlayStateListener);
        prePlayIndex = currentPlayIndex;
        for (PlayDataChangeListTokenListener dataUpdataCall : mDataChangeCallBacks) {
            dataUpdataCall.onPlayStart(wholePlayList.get(currentPlayIndex));
        }
    }


    public void showPlayList() {
        LogUtil.d(TAG, "showPlayList");
        FragmentTransaction ft = mActivity.getSupportFragmentManager().beginTransaction();
        if (mPlayListFragment == null) {
            mPlayListFragment = PlayListFragment.newInstance(currentPlayIndex);
            mPlayListFragment.setPlayControl(mPlayListControlHandle);
        }

        ft.add(mPlayListFragment, PLAYLIST_FRAGMENT_TAG).show(mPlayListFragment).commit();

    }


    private void hideFragment(String tag) {
        LogUtil.d(TAG, "hideFragment ::");
        FragmentTransaction ft = mActivity.getSupportFragmentManager().beginTransaction();
        if (mPlayListFragment != null) {
            ft.remove(mPlayListFragment).hide(mPlayListFragment).commit();
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
                //TODO
                LogUtil.d(TAG, "onNext result: " + result + ",item2BeChanged:" + item2BeChanged);
                item2BeChanged.updateFav();
                //TODO update audio token fav
                for (PlayDataChangeListTokenListener dataUpdataCall : mDataChangeCallBacks) {
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

    @Subscribe
    public void onAudioFavEvent(final PushAudioFavEvent fav) {
        LogUtil.d(TAG, ": onAudioFavEvent:" + fav.getFavoriteVO().getAudio_id());

        Observable.just(fav)
                .map(new Func1<PushAudioFavEvent, Integer>() {
                    @Override
                    public Integer call(PushAudioFavEvent pushAudioFavEvent) {
                        int result = isCurrentPlaylistContain(fav.getFavoriteVO().getAudio_id().longValue());
                        LogUtil.d(TAG, "onAudioFavEvent : updateTheFavState result:" + result);
                        return result;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        if (-1 != integer.intValue()) {
                            PlayListItem item = wholePlayList.get(integer.intValue());
                            item.setFav(fav.getAction());
                            wholePlayList.set(integer.intValue(), item);
                            final PlayListItem itemChanged = wholePlayList.get(currentPlayIndex);
                            for (PlayDataChangeListTokenListener dataUpdataCall : mDataChangeCallBacks) {
                                dataUpdataCall.onDataUpdate(PLAYLISTMANAGER_RESULT_SUCCESS, itemChanged);
                            }
                        }
                        onLocalPlayListUpdate(integer.intValue(), fav);
                    }

                });
    }

    /**
     * @param audioId  audioId
     * @param favstate favstate
     * @return -1: current playlist do not  contain the audioId  ,else return the index in the playlist
     */
    private int updateLocalFavItem(long audioId, int favstate) {
        int containIndex = isCurrentPlaylistContain(audioId);
        if (containIndex != -1) {
            PlayListItem item = wholePlayList.get(containIndex);
            item.setFav(favstate);
            wholePlayList.set(containIndex, item);
        }
        return containIndex;
    }

    /**
     * @param audioId audioId
     * @return if contains ,replace the item and return the index,
     * else return -1
     */
    private int isCurrentPlaylistContain(long audioId) {
        for (int i = 0; i < wholePlayList.size(); i++) {
            PlayListItem item = wholePlayList.get(i);
            if (item.getId().longValue() == audioId) {
                return i;
            }
        }
        return -1;
    }

    //TODO
    public void release() {
        LogUtil.d(TAG, ": release");
        EventBus.getDefault().unregister(this);
    }


    private class PlayListLoadDataListener implements IPlayListLoadListener {

        @Override
        public void onLoadData(int resultCode, List<PlayListItem> resultmessage) {
            Log.d(TAG, "onLoadData size: " + resultmessage.size());
            isLoadingData = false;
            wholePlayList.addAll(resultmessage);
            for (PlayListItem item : resultmessage) {
                AudioToken mtoken = AudioTokenFactory.getAudioToken(mActivity, item, mMediaPlayerManager);
                Log.d(TAG, "onLoadData: " + audioTokenList.size());
                audioTokenList.add(mtoken);
            }
            if (currentPlayIndex == -1) {
                currentPlayIndex = DEFAULT_PLAY_INDEX;
                switchAudioToken();
            }
            for (PlayDataChangeListTokenListener dataUpdataCall : mDataChangeCallBacks) {
                dataUpdataCall.onGetData(PLAYLISTMANAGER_RESULT_SUCCESS, resultmessage);
            }
        }

        @Override
        public void playIndexChanger(int index) {

        }


    }

    public PlayListItem getCurrentPlayItem() {
        return wholePlayList.get(currentPlayIndex);

    }


    private class IPlayListControlHandle implements IPlayListControl {

        @Override
        public void addDataListener(PlayDataChangeListTokenListener listener) {
            mDataChangeCallBacks.add(listener);
        }

        @Override
        public void removeDataListener(PlayDataChangeListTokenListener listener) {
            mDataChangeCallBacks.remove(listener);
        }

        @Override
        public List<PlayListItem> getData() {
            return wholePlayList;
        }

        @Override
        public void getDataAsync() {
            LogUtil.d(TAG, ": getDataAsync");
            if (!isLoadingData) {
                startLoadMoreData();
            }
        }

        @Override
        public void playSellected(int position) {
            currentPlayIndex = position;
            switchAudioToken();
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

    private class AudioPlayStateListener implements AudioToken.AudioPlayStateListener {

        @Override
        public void onComplete() {
            playNext();
        }

        @Override
        public void onStart() {

        }
    }


}
