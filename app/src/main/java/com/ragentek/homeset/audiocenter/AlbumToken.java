package com.ragentek.homeset.audiocenter;

import android.support.v4.app.FragmentActivity;

import com.ragentek.homeset.audiocenter.model.bean.PlayItem;
import com.ragentek.homeset.audiocenter.model.bean.PlayListItem;
import com.ragentek.homeset.audiocenter.net.AudioCenterHttpManager;
import com.ragentek.homeset.audiocenter.service.MediaPlayerManager;
import com.ragentek.homeset.audiocenter.utils.LogUtil;
import com.ragentek.homeset.audiocenter.view.fragment.AlbumFragment;
import com.ragentek.homeset.audiocenter.view.fragment.PlayBaseFragment;
import com.ragentek.protocol.commons.audio.TrackVO;
import com.ragentek.protocol.messages.http.audio.TrackResultVO;

import java.util.ArrayList;
import java.util.List;

import rx.Subscriber;


/**
 * Created by xuanyang.feng on 2017/4/20.
 */

public class AlbumToken extends AudioToken {
    private static final String TAG = "AlbumToken";
    private int currentPage = 1;
    private static final int PAGE_COUNT = 20;
    private PlayBaseFragment.IAudioDataChangerListener<List<TrackVO>> mIAudioDataChangerListener;
    private MediaPlayerManager.MediaPlayerHandler mMediaPlayer;
    private PlayListItem mPlayListItem;
    private int currentPlayIndext;
    private boolean waitingForPlay = true;
    private List<TrackVO> wholeTracks;


    AlbumToken(FragmentActivity activity, MediaPlayerManager.MediaPlayerHandler mediaPlayer, PlayListItem item) {
        super(activity, mediaPlayer, item);
        mMediaPlayer = mediaPlayer;
        mPlayListItem = item;
    }


    @Override
    protected PlayBaseFragment<List<TrackVO>, AlbumAudioControl> getPlayFragment() {
        AlbumAudioControl mAlbumAudioControl = new AlbumAudioControl();
        wholeTracks = new ArrayList<>();
        //TODO
        AlbumFragment albumFragment = AlbumFragment.newInstances();
        albumFragment.setAudioControl(mAlbumAudioControl);
        return albumFragment;
    }

    @Override
    protected void playAudio(int index) {
        if (wholeTracks.size() > index) {
            mMediaPlayer.play(index);
        } else {
            getPlayListAsync();
        }
        currentPlayIndext = index;
    }


    private void getPlayListAsync() {
        LogUtil.d(TAG, "getPlayListAsync: ");
        waitingForPlay = true;

        Subscriber<TrackResultVO> getTagSubscriber = new Subscriber<TrackResultVO>() {
            @Override
            public void onCompleted() {
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
                            LogUtil.d(TAG, "onNext : " + trackvo.getTitle());
                            PlayItem item = new PlayItem();
                            item.setPlayUrl(trackvo.getPlay_url());
                            item.setDuration(trackvo.getDuration());
                            item.setCoverUrl(trackvo.getCover_url());
                            item.setTitle(trackvo.getTitle());
                            list.add(item);
                        }
                        wholeTracks.addAll(tagResult.getTracks());
                        mIAudioDataChangerListener.onGetData(PLAYLIST_RESULT_SUCCESS, tagResult.getTracks());
                        mMediaPlayer.addPlayList(list);
                        if (waitingForPlay) {
                            mMediaPlayer.play(currentPlayIndext);
                            waitingForPlay = false;
                        }
                    }
                    currentPage++;
                }
            }
        };

        AudioCenterHttpManager.getInstance(mActivity).getTracks(getTagSubscriber, mPlayListItem.getId(), currentPage, PAGE_COUNT);
    }


    public class AlbumAudioControl implements IAudioControl {


        public void playSellected(int position) {
            mMediaPlayer.play(position);
        }

        public void getMoreData() {
            getPlayListAsync();
        }


        @Override
        public void setDataChangerListener(PlayBaseFragment.IAudioDataChangerListener listener) {
            mIAudioDataChangerListener = listener;
        }
    }


}


