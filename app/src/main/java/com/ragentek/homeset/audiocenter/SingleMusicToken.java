package com.ragentek.homeset.audiocenter;

import android.support.v4.app.FragmentActivity;

import com.ragentek.homeset.audiocenter.model.bean.PlayItem;
import com.ragentek.homeset.audiocenter.model.bean.PlayListItem;
import com.ragentek.homeset.audiocenter.service.MediaPlayerManager;
import com.ragentek.homeset.audiocenter.view.fragment.PlayBaseFragment;
import com.ragentek.homeset.audiocenter.view.fragment.SingleMusicFragment;
import com.ragentek.protocol.commons.audio.MusicVO;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xuanyang.feng on 2017/4/21.
 */

public class SingleMusicToken extends AudioToken<MusicVO, SingleMusicToken.SingleMusicAudioControl> {

    private MusicVO mMusicVO;

    private MediaPlayerManager.MediaPlayerHandler mMediaPlayer;
    private int currentPlayIndext = 1;

    SingleMusicToken(FragmentActivity activity, MediaPlayerManager.MediaPlayerHandler mediaPlayer, PlayListItem item) {
        super(activity, mediaPlayer, item);
        mMediaPlayer = mediaPlayer;
        mMusicVO = (MusicVO) item.getAudio();
    }

    @Override
    protected PlayBaseFragment getPlayFragment() {
        SingleMusicFragment sing = SingleMusicFragment.newInstence();
        sing.setAudioControl(new SingleMusicAudioControl());
        return sing;
    }

    @Override
    protected void playAudio(int index) {
        List<PlayItem> list = new ArrayList<>();
        PlayItem item = new PlayItem();
        item.setPlayUrl(mMusicVO.getPlay_url());
        item.setCoverUrl(mMusicVO.getCover_url());
        item.setTitle(mMusicVO.getSong_name());
        mMediaPlayer.setPlayList(list, currentPlayIndext);
        mMediaPlayer.setPlayList(list, currentPlayIndext);
    }


    public class SingleMusicAudioControl implements IAudioControl {

        @Override
        public void setDataChangerListener(PlayBaseFragment.IAudioDataChangerListener mListener) {

        }

        public MusicVO getData() {
            return mMusicVO;

        }
    }
}