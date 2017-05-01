package com.ragentek.homeset.audiocenter;

import android.support.v4.app.FragmentActivity;

import com.ragentek.homeset.audiocenter.model.bean.PlayItem;
import com.ragentek.homeset.audiocenter.model.bean.PlayListItem;
import com.ragentek.homeset.audiocenter.service.MediaPlayerManager;
import com.ragentek.homeset.audiocenter.utils.LogUtil;
import com.ragentek.homeset.audiocenter.view.fragment.MusicFragment;
import com.ragentek.homeset.audiocenter.view.fragment.PlayBaseFragment;
import com.ragentek.protocol.commons.audio.BaseAudioVO;
import com.ragentek.protocol.commons.audio.MusicVO;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xuanyang.feng on 2017/4/21.
 */

public class MusicToken extends AudioToken<BaseAudioVO, MusicToken.MusicAudioControl> {
    private static final String TAG = "MusicToken";
    private MediaPlayerManager.MediaPlayerHandler mMediaPlayer;
    private PlayListItem mPlayListItem;
    private int currentPage = 1;
    private MusicVO currentMusic;

    MusicToken(FragmentActivity activity, MediaPlayerManager.MediaPlayerHandler mediaPlayer, PlayListItem item) {
        super(activity, mediaPlayer, item);
        mMediaPlayer = mediaPlayer;
        mPlayListItem = item;
    }

    @Override
    protected PlayBaseFragment getPlayFragment() {
        MusicFragment fragment = MusicFragment.newInstances();
        fragment.setAudioControl(new MusicAudioControl());
        return fragment;
    }

    @Override
    protected void playAudio(int index) {
        currentMusic = (MusicVO) mPlayListItem.getAudio();
        List<PlayItem> list = new ArrayList<>();
        PlayItem playItem = new PlayItem();
        playItem.setPlayUrl(currentMusic.getPlay_url());
        playItem.setCoverUrl(currentMusic.getCover_url());
        playItem.setTitle(currentMusic.getSong_name());
        list.add(playItem);
        mMediaPlayer.setPlayList(list, currentPage);
    }

    public class MusicAudioControl implements IAudioControl {


        @Override
        public void setDataChangerListener(PlayBaseFragment.IAudioDataChangerListener listener) {
//            listener.
        }

        public MusicVO getPlayData() {
            LogUtil.d(TAG, "getPlayData   currentMusic: " + currentMusic.getSong_name());

            return currentMusic;
        }

    }
}
