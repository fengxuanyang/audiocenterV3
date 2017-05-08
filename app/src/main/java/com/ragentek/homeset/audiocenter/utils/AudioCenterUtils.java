package com.ragentek.homeset.audiocenter.utils;

import android.os.Environment;

import com.ragentek.homeset.audiocenter.model.bean.PlayListItem;
import com.ragentek.protocol.commons.audio.AlbumVO;
import com.ragentek.protocol.commons.audio.FavoriteVO;
import com.ragentek.protocol.commons.audio.MusicVO;
import com.ragentek.protocol.commons.audio.RadioVO;
import com.ragentek.protocol.constants.Category;
import com.ragentek.protocol.constants.CategoryEnum;

/**
 * Created by xuanyang.feng on 2017/3/7.
 */

public class AudioCenterUtils {

    private static final String TAG = "AudioCenterUtils";

    private static final int ONE_MIN = 1 * 60 * 1000;

    private static final int ONE_SECOND = 1 * 1000;

    public static String getInnerSDCardPath() {
        return Environment.getExternalStorageDirectory().getPath();
    }


    public static String formatTime(long ms) {
        StringBuilder sb = new StringBuilder();
        int min = (int) (ms / ONE_MIN);
        int sec = (int) (ms % ONE_MIN) / ONE_SECOND;
        if (min == 0) {
            sb.append("00:");
        } else if (min < 10) {
            sb.append("0").append(min).append(":");
        } else {
            sb.append(min).append(":");
        }
        if (sec == 0) {
            sb.append("00");
        } else if (sec < 10) {
            sb.append("0").append(sec);
        } else {
            sb.append(sec);
        }
        return sb.toString();
    }


    public static PlayListItem decoratorFavoriteVO(FavoriteVO fav) {
        PlayListItem playlistitem = null;
        LogUtil.d(TAG, "decoratorFavoriteVO  getId: " + fav.getId() + ",getAudio_id:" + fav.getAudioId());
        switch (fav.getGroup()) {
            case Category.GROUP.MUSIC_GROUP:
                playlistitem = new PlayListItem(Constants.AUDIO_TYPE_SINGLE_MUSIC, getCategoryIdFromName(fav.getCategoryName()), fav.getAudioId());
                playlistitem.setFav(Constants.FAV);
                playlistitem.setGroup(fav.getGroup());
                MusicVO music = new MusicVO();
                music.setPlayUrl(fav.getPlayUrl());
                music.setId(fav.getAudioId());
                music.setSingerName(fav.getAnnouncer());
                music.setSongName(fav.getTitle());
                music.setAlbumName(fav.getAnnouncer());
                music.setCoverUrl(fav.getCoverUrl());
                music.setCategoryId(getCategoryIdFromName(fav.getCategoryName()));
                playlistitem.setAudio(music);
                break;
            case Category.GROUP.OTHER_GOUP:
                playlistitem = new PlayListItem(Constants.AUDIO_TYPE_ALBUM, getCategoryIdFromName(fav.getCategoryName()), fav.getAudioId());
                playlistitem.setFav(Constants.FAV);
                playlistitem.setGroup(fav.getGroup());
                AlbumVO album = new AlbumVO();
                album.setId(fav.getAudioId());
                album.setTitle(fav.getTitle());
                album.setCoverUrl(fav.getCoverUrl());
                album.setCategoryId(getCategoryIdFromName(fav.getCategoryName()));
                playlistitem.setAudio(album);
                break;
            case Category.GROUP.RADIO_GROUP:
                playlistitem = new PlayListItem(Constants.AUDIO_TYPE_RADIO, getCategoryIdFromName(fav.getCategoryName()), fav.getAudioId());
                playlistitem.setFav(Constants.FAV);
                playlistitem.setGroup(fav.getGroup());
                RadioVO radio = new RadioVO();
                radio.setPlayUrl(fav.getPlayUrl());
                radio.setId(fav.getAudioId());
                radio.setName(fav.getTitle());
                radio.setDesc(fav.getAnnouncer());
                radio.setCoverUrl(fav.getCoverUrl());
                radio.setCategoryId(getCategoryIdFromName(fav.getCategoryName()));
                playlistitem.setAudio(radio);
                break;
            default:
                LogUtil.e(TAG, " error  group type not supported" + fav.getGroup());

        }
        return playlistitem;
    }

    public static int getCategoryIdFromName(String name) {
        LogUtil.d(TAG, "name:" + name);
        int id = CategoryEnum.MUSIC.getId();
        for (CategoryEnum em : CategoryEnum.values()) {
            if (em.getName().equals(name)) {
                id = em.getId();
            }
        }
        LogUtil.d(TAG, "id:" + id);

        return id;
    }

}
