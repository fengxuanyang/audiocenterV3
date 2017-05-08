package com.ragentek.homeset.audiocenter;

import android.os.IBinder;
import android.os.RemoteException;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import com.ragentek.homeset.audiocenter.model.bean.PlayItem;
import com.ragentek.homeset.audiocenter.model.bean.PlayListItem;
import com.ragentek.homeset.audiocenter.net.AudioCenterHttpManager;
import com.ragentek.homeset.audiocenter.service.MediaPlayerManager;
import com.ragentek.homeset.audiocenter.utils.LogUtil;
import com.ragentek.homeset.audiocenter.view.fragment.AlbumFragment;
import com.ragentek.homeset.audiocenter.view.fragment.PlayBaseFragment;
import com.ragentek.homeset.audiocenter.view.fragment.SingleMusicFragment;
import com.ragentek.protocol.commons.audio.AlbumVO;
import com.ragentek.protocol.commons.audio.BaseAudioVO;
import com.ragentek.protocol.commons.audio.TrackVO;
import com.ragentek.protocol.messages.http.audio.TrackResultVO;

import java.util.ArrayList;
import java.util.List;

import rx.Subscriber;


/**
 * Created by xuanyang.feng on 2017/4/20.
 */

public class AlbumToken extends AudioToken<AlbumVO, AlbumToken.AlbumAudioControl> {
    private static final String TAG = "AlbumToken";
    private int currentPage = 1;
    private static final int PAGE_COUNT = 2;
    private PlayBaseFragment.IAudioDataChangerListener<List<TrackVO>> mIAudioDataChangerListener;
    private MediaPlayerManager.MediaPlayerHandler mMediaPlayer;
    private PlayListItem mPlayListItem;
    private int currentPlayIndext;
    private boolean waitingForPlay = true;
    private List<TrackVO> wholeTracks;
    private List<PlayItem> wholePlayList = new ArrayList<>();
    private boolean isLoadingMore = false;


    AlbumToken(FragmentActivity activity, MediaPlayerManager.MediaPlayerHandler mediaPlayer, PlayListItem item) {
        super(activity, mediaPlayer, item);
        mMediaPlayer = mediaPlayer;
        mPlayListItem = item;
        //cache the trackvo
        wholeTracks = new ArrayList<TrackVO>();
    }


    @Override
    protected PlayBaseFragment<List<TrackVO>, AlbumAudioControl> getPlayFragment() {
        Fragment view = mActivity.getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG);
        AlbumFragment albumFragment;
        if (view == null) {
            albumFragment = AlbumFragment.newInstances();
        } else {
            albumFragment = (AlbumFragment) view;
        }
        AlbumAudioControl mAlbumAudioControl = new AlbumAudioControl();
        albumFragment.setAudioControl(mAlbumAudioControl);
        return albumFragment;
    }

    @Override
    protected void startPlayAudio(int index) {
        LogUtil.d(TAG, "startPlayAudio: " + wholeTracks.size() + ",index::" + index);
        LogUtil.d(TAG, "startPlayAudio  wholePlayList: " + wholePlayList.size());
        if (index <= currentPlayIndext && wholeTracks.size() > currentPlayIndext) {
            initPlayListAndStart();
        } else {
            waitingForPlay = true;
            getPlayListAsync();
        }
    }

    @Override
    protected void onSoundPlayComplete() {
        playSellectedTrack(currentPlayIndext + 1);
        mIAudioDataChangerListener.onPlayStartData(currentPlayIndext);
    }

    private void initPlayListAndStart() {
        waitingForPlay = false;
        mMediaPlayer.setPlayList(wholePlayList, currentPlayIndext);
        updateSellected();
    }

    private void updateSellected() {
        mIAudioDataChangerListener.onPlayStartData(currentPlayIndext);
    }

    private void getPlayListAsync() {
        LogUtil.d(TAG, "getPlayListAsync: ");
        Subscriber<TrackResultVO> getTagSubscriber = new Subscriber<TrackResultVO>() {
            @Override
            public void onCompleted() {
                isLoadingMore = false;
                LogUtil.d(TAG, "onCompleted : ");
            }

            @Override
            public void onError(Throwable e) {
                LogUtil.e(TAG, "onError : " + e.toString());
            }

            @Override
            public void onNext(TrackResultVO tagResult) {
                //TODO fail
                LogUtil.d(TAG, "onNext : " + tagResult);
                if (tagResult != null) {
                    List<PlayItem> list = new ArrayList<>();
                    if (tagResult.getTracks() != null) {
                        for (int i = 0; i < tagResult.getTracks().size(); i++) {
                            TrackVO trackvo = tagResult.getTracks().get(i);
                            LogUtil.d(TAG, "onNext : " + trackvo);
                            LogUtil.d(TAG, "onNext getDuration: " + trackvo.getDuration());

                            LogUtil.d(TAG, "onNext getTitle: " + trackvo.getTitle());
                            LogUtil.d(TAG, "onNext getPlayUrl : " + trackvo.getPlayUrl());
                            LogUtil.d(TAG, "onNext getPlayHdurl : " + trackvo.getPlayHdurl());
                            LogUtil.d(TAG, "onNext getAlbumTitle: " + trackvo.getAlbumTitle());

                            PlayItem item = new PlayItem();
                            item.setPlayUrl(trackvo.getPlayUrl());
                            item.setDuration(trackvo.getDuration().intValue());
                            item.setCoverUrl(trackvo.getCoverUrl());
                            item.setTitle(trackvo.getTitle());
                            list.add(item);
                        }
                        LogUtil.d(TAG, "onNext addAll: ");

                        wholeTracks.addAll(tagResult.getTracks());
                        LogUtil.d(TAG, "onNext : " + tagResult.getTracks().size());
                        mIAudioDataChangerListener.onGetData(PLAYLIST_RESULT_SUCCESS, tagResult.getTracks());
                        wholePlayList.addAll(list);
                        if (waitingForPlay) {
                            initPlayListAndStart();
                        } else {
                            mMediaPlayer.addPlayList(list);
                        }
                    }
                    currentPage++;
                }
            }
        };
        AudioCenterHttpManager.getInstance(mActivity).getTracks(getTagSubscriber, mPlayListItem.getId(), currentPage, PAGE_COUNT);
        isLoadingMore = true;
    }

    private void playSellectedTrack(int position) {
        LogUtil.d(TAG, "playSellectedTrack : " + position);
        mMediaPlayer.play(position);
        currentPlayIndext = position;
        updateSellected();
    }

    public class AlbumAudioControl implements IAudioControl {


        public void playSellected(int position) {
            playSellectedTrack(position);
        }

        public List<TrackVO> getData() {
            return wholeTracks;
        }

        public int getCurrentPlayIndex() {
            return currentPlayIndext;
        }


        public void getMoreData() {
            LogUtil.d(TAG, "getMoreData isLoadingMore: " + isLoadingMore);
            if (!isLoadingMore) {
                getPlayListAsync();
            }
        }

        //TODO
        @Override
        public void setDataChangerListener(AlbumFragment.IAudioDataChangerListener listener) {
            LogUtil.d(TAG, "setDataChangerListener : " + listener);
            mIAudioDataChangerListener = listener;
        }
    }

}


