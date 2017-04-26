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

public class MusicToken extends AudioToken<MusicVO> {
    private int currentPlayIndex = 0;

    MusicToken(FragmentActivity activity, MediaPlayerManager.MediaPlayerHandler mediaPlayer, PlayListItem item) {
        super(activity, mediaPlayer, item);
    }

    @Override
    protected PlayBaseFragment getPlayFragment() {
        return new MusicFragment();
    }

    @Override
    protected void getPlayList(AudioToken.PlayListResultListener listener, PlayListItem listitem) {
        List<PlayItem> list = new ArrayList<>();
        PlayItem item = new PlayItem();
        MusicVO music = (MusicVO) listitem.getAudio();
        item.setPlayUrl(music.getPlay_url());
        item.setCoverUrl(music.getCover_url());
        item.setTitle(music.getSong_name());
        list.add(item);
        listener.onPlayListResult(list, music);
    }
}
