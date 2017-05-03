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
    private static final int PAGE_COUNT = 20;
    private PlayBaseFragment.IAudioDataChangerListener<List<TrackVO>> mIAudioDataChangerListener;
    private MediaPlayerManager.MediaPlayerHandler mMediaPlayer;
    private PlayListItem mPlayListItem;
    private int currentPlayIndext;
    private boolean waitingForPlay = true;
    private List<TrackVO> wholeTracks;
    private List<PlayItem> wholePlayList = new ArrayList<>();
    private AlbumMediaPlayerPlayListener mAlbumMediaPlayerPlayListener = new AlbumMediaPlayerPlayListener();


    AlbumToken(FragmentActivity activity, MediaPlayerManager.MediaPlayerHandler mediaPlayer, PlayListItem item) {
        super(activity, mediaPlayer, item);
        mMediaPlayer = mediaPlayer;
        mPlayListItem = item;
        //cache the trackvo
        wholeTracks = new ArrayList<TrackVO>();
    }


    @Override
    protected PlayBaseFragment<List<TrackVO>, AlbumAudioControl> getPlayFragment() {
        LogUtil.d(TAG, "getPlayFragment  view: " + this);
        //TODO
        currentPlayIndext = 0;
        currentPage = 1;
//        mMediaPlayer.addMeidaPlayListener(mAlbumMediaPlayerPlayListener);
        Fragment view = mActivity.getSupportFragmentManager().findFragmentByTag(this.getClass().getSimpleName());
        AlbumFragment albumFragment;
        LogUtil.d(TAG, "getPlayFragment  view: " + view + ",tag::" + this.getClass().getSimpleName());

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
        LogUtil.d(TAG, "getPlayListAsync: " + wholeTracks.size() + ",index::" + index);
        if (wholeTracks.size() > index) {
            waitingForPlay = false;
            mMediaPlayer.setPlayList(wholePlayList, index);
        } else {
            waitingForPlay = true;
            getPlayListAsync();
        }

    }


    private void getPlayListAsync() {
        LogUtil.d(TAG, "getPlayListAsync: ");

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
                        if (waitingForPlay) {
                            wholePlayList.addAll(list);
                            mMediaPlayer.setPlayList(list, currentPlayIndext);
                            waitingForPlay = false;
                        } else {
                            mMediaPlayer.addPlayList(list);
                        }
                    }
                    currentPage++;
                }
            }
        };
        AudioCenterHttpManager.getInstance(mActivity).getTracks(getTagSubscriber, mPlayListItem.getId(), currentPage, PAGE_COUNT);
    }

    private void playSellectedTrack(int position) {
        LogUtil.d(TAG, "playSellectedTrack : " + position);
        mMediaPlayer.play(position);
        currentPlayIndext = position;
    }

    public class AlbumAudioControl implements IAudioControl {


        public void playSellected(int position) {
            playSellectedTrack(position);
        }

        public List<TrackVO> getData() {
            return wholeTracks;
        }

        public void registerMeidaPlayListener() {
            LogUtil.d(TAG, "registerMeidaPlayListener : " + mAlbumMediaPlayerPlayListener);

            mMediaPlayer.addMeidaPlayListener(mAlbumMediaPlayerPlayListener);
        }

        public void unregisterMeidaPlayListener() {
            LogUtil.d(TAG, "unregisterMeidaPlayListener : " + mAlbumMediaPlayerPlayListener);
            mMediaPlayer.removeMeidaPlayListener(mAlbumMediaPlayerPlayListener);
        }

        public void getMoreData() {
            getPlayListAsync();
        }

        //TODO
        @Override
        public void setDataChangerListener(AlbumFragment.IAudioDataChangerListener listener) {
            LogUtil.d(TAG, "setDataChangerListener : " + listener);
            mIAudioDataChangerListener = listener;
        }
    }


    private class AlbumMediaPlayerPlayListener implements MediaPlayerManager.MediaPlayerPlayListener

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
            LogUtil.d(TAG, "onSoundPlayComplete:  " + currentPlayIndext);
            playSellectedTrack(currentPlayIndext + 1);
            mIAudioDataChangerListener.onPlayStartData(currentPlayIndext);
        }


    }
}


