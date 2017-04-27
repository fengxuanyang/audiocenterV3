package com.ragentek.homeset.audiocenter;

import android.support.v4.app.FragmentActivity;

import com.ragentek.homeset.audiocenter.model.bean.PlayItem;
import com.ragentek.homeset.audiocenter.model.bean.PlayListItem;
import com.ragentek.homeset.audiocenter.service.MediaPlayerManager;
import com.ragentek.homeset.audiocenter.view.fragment.MusicFragment;
import com.ragentek.homeset.audiocenter.view.fragment.PlayBaseFragment;
import com.ragentek.protocol.commons.audio.MusicVO;
import com.ragentek.protocol.commons.audio.RadioVO;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xuanyang.feng on 2017/4/21.
 */

public class RadioToken extends AudioToken<RadioVO> {

    RadioToken(FragmentActivity activity, MediaPlayerManager.MediaPlayerHandler mediaPlayer, PlayListItem item) {
        super(activity, mediaPlayer, item);
    }

    @Override
    protected PlayBaseFragment getPlayFragment() {
        return new MusicFragment();
    }

    @Override
    protected void getPlayListAsync(AudioPlayListResultListener listener, PlayListItem listitem) {
        List<PlayItem> list = new ArrayList<>();
        PlayItem item = new PlayItem();
        RadioVO radio = (RadioVO) listitem.getAudio();
        item.setPlayUrl(radio.getPlay_url());
        item.setCoverUrl(radio.getCover_url());
        item.setTitle(radio.getName());
        list.add(item);
        listener.onPlayAudioListGet(PLAYLIST_RESULT_SUCCESS, list, radio);
    }


}
