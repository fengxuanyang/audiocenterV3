package com.ragentek.homeset.audiocenter;

import android.support.v4.app.FragmentActivity;

import com.ragentek.homeset.audiocenter.model.bean.PlayListItem;
import com.ragentek.homeset.audiocenter.service.MediaPlayerManager;
import com.ragentek.homeset.audiocenter.utils.Constants;

/**
 * Created by xuanyang.feng on 2017/4/21.
 */

public class AudioTokenFactory {
    public static AudioToken getAudioToken(FragmentActivity activity, PlayListItem item) {
        MediaPlayerManager.MediaPlayerHandler mediaPlayer = MediaPlayerManager.getInstance(activity).geMediaPlayerHandler();
        AudioToken audiotoken = null;
        switch (item.getAudioType()) {
            case Constants.AUDIO_TYPE_ALBUM:

                audiotoken = new AlbumAudioToken(activity, mediaPlayer, item);
                break;
            case Constants.AUDIO_TYPE_MUSIC:
                audiotoken = new MusicToken(activity, mediaPlayer, item);

                break;
            case Constants.AUDIO_TYPE_RADIO:
                audiotoken = new RadioToken(activity, mediaPlayer, item);

                break;
            case Constants.AUDIO_TYPE_SINGLE_MUSIC:
                audiotoken = new SingleMusicToken(activity, mediaPlayer, item);

                break;
        }
        return audiotoken;
    }

}
