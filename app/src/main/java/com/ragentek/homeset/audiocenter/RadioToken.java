package com.ragentek.homeset.audiocenter;

import android.support.v4.app.FragmentActivity;

import com.ragentek.homeset.audiocenter.model.bean.PlayItem;
import com.ragentek.homeset.audiocenter.model.bean.PlayListItem;
import com.ragentek.homeset.audiocenter.service.MediaPlayerManager;
import com.ragentek.homeset.audiocenter.view.fragment.MusicFragment;
import com.ragentek.homeset.audiocenter.view.fragment.PlayBaseFragment;
import com.ragentek.protocol.commons.audio.BaseAudioVO;
import com.ragentek.protocol.commons.audio.RadioVO;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xuanyang.feng on 2017/4/21.
 */

public class RadioToken extends AudioToken<BaseAudioVO, RadioToken.RadioAudioControl> {
    private MediaPlayerManager.MediaPlayerHandler mMediaPlayer;
    private PlayListItem mPlayListItem;
    private int currentPlayIndext = 1;
    private RadioVO radio;

    RadioToken(FragmentActivity activity, MediaPlayerManager.MediaPlayerHandler mediaPlayer, PlayListItem item) {
        super(activity, mediaPlayer, item);
        mMediaPlayer = mediaPlayer;
        radio = (RadioVO) item.getAudio();

    }

    @Override
    protected PlayBaseFragment getPlayFragment() {
        return new MusicFragment();
    }

    @Override
    protected void playAudio(int index) {
        List<PlayItem> list = new ArrayList<>();
        PlayItem item = new PlayItem();
        item.setPlayUrl(radio.getPlay_url());
        item.setCoverUrl(radio.getCover_url());
        item.setTitle(radio.getName());
        list.add(item);
        mMediaPlayer.setPlayList(list, currentPlayIndext);
    }

    public class RadioAudioControl implements IAudioControl {

        @Override
        public void setDataChangerListener(PlayBaseFragment.IAudioDataChangerListener mListener) {

        }

        public RadioVO getData() {
            return radio;

        }
    }
}
