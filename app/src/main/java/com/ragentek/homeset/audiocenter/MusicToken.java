package com.ragentek.homeset.audiocenter;

import android.support.v4.app.FragmentActivity;

import com.ragentek.homeset.audiocenter.AudioToken;
import com.ragentek.homeset.audiocenter.model.bean.PlayItem;
import com.ragentek.homeset.audiocenter.model.bean.PlayListItem;
import com.ragentek.homeset.audiocenter.service.MediaPlayerManager;
import com.ragentek.homeset.audiocenter.view.fragment.MusicFragment;
import com.ragentek.homeset.audiocenter.view.fragment.PlayBaseFragment;
import com.ragentek.protocol.commons.audio.MusicVO;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xuanyang.feng on 2017/4/21.
 */

class MusicToken extends AudioToken<MusicVO> {

    MusicToken(FragmentActivity activity, MediaPlayerManager.MediaPlayerHandler mediaPlayer, PlayListItem item) {
        super(activity, mediaPlayer, item);
    }

    @Override
    protected PlayBaseFragment getPlayFragment() {
        return MusicFragment.newInstances();
    }

    @Override
    protected void getPlayListAsync(AudioPlayListResultListener listener, PlayListItem item) {
        MusicVO music = (MusicVO) item.getAudio();
        List<PlayItem> list = new ArrayList<>();
        PlayItem playItem = new PlayItem();
        playItem.setPlayUrl(music.getPlay_url());
        playItem.setCoverUrl(music.getCover_url());
        playItem.setTitle(music.getSong_name());
        list.add(playItem);
        listener.onPlayAudioListGet(PLAYLIST_RESULT_SUCCESS, list, music);
    }


}
